package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.UUID;
import javax.sql.DataSource;
import com.polar.bear.api.models.GoodsInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("loc")
@EnabledIfEnvironmentVariable(named = "RUN_RABBITMQ_INTEGRATION", matches = "true")
class GoodsRabbitIntegrationTest {
	@Autowired
	private GoodsInfoService goodsInfoService;
	@Autowired
	private GoodsUpdateService goodsUpdateService;
	@Autowired
	private JdbcTemplate jdbc;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	@Autowired
	private org.springframework.transaction.PlatformTransactionManager transactionManager;

	@Test
	void publishesAndPersistsGoodsUpdateThroughRealRabbit() throws Exception {
		try (java.sql.Connection connection = dataSource.getConnection()) {
			assertThat(connection.getMetaData().getURL()).contains("127.0.0.1:3306/");
		}
		Long sampleNo = jdbc.queryForObject("SELECT MIN(GOODS_NO) FROM goods_info", Long.class);
		assertThat(sampleNo).as("기존 로컬 샘플 상품이 필요합니다.").isNotNull();
		GoodsInfoDto goods = goodsInfoService.selectGoodsInfo(sampleNo);
		String testCode = "MQTEST-" + UUID.randomUUID().toString().substring(0, 12);
		goods.setGoodsCode(testCode);
		goods.setGoodsNameKo("RabbitMQ 연동 검증 상품");
		goods.setImages(null);
		Long goodsNo = goodsInfoService.insertGoodsInfo(goods, null);
		try {
			goods = goodsInfoService.selectGoodsInfo(goodsNo);
			goods.setGoodsNameKo("RabbitMQ 비동기 수정 검증 완료");
			goods.setUpdtId(goods.getRegId());
			assertThat(goodsUpdateService).isInstanceOf(QueuedGoodsUpdateService.class);
			assertThat(goodsUpdateService.update(goods)).isTrue();
			long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(15);
			while (System.nanoTime() < deadline) {
				if (goods.getGoodsNameKo().equals(goodsInfoService.selectGoodsInfo(goodsNo).getGoodsNameKo())) {
					return;
				}
				Thread.sleep(100);
			}
			assertThat(goodsInfoService.selectGoodsInfo(goodsNo).getGoodsNameKo()).isEqualTo(goods.getGoodsNameKo());
		} finally {
			deleteTestGoods(goods);
		}
	}
	@Test
	void oldRequestCannotOverwriteNewerHistory() throws Exception {
		GoodsInfoDto first = insertTestGoods();
		try {
			first.setGoodsNameKo("first-update");
			goodsInfoService.updateGoodsInfo(first);
			GoodsInfoDto second = goodsInfoService.selectGoodsInfo(first.getGoodsNo());
			assertThat(second.getGoodsInfoHistoryNo()).isGreaterThan(first.getGoodsInfoHistoryNo());
			second.setGoodsNameKo("latest-update");
			goodsInfoService.updateGoodsInfo(second);
			assertThatThrownBy(() -> goodsInfoService.updateGoodsInfo(first))
					.isInstanceOf(OptimisticLockingFailureException.class);
			GoodsInfoDto latest = goodsInfoService.selectGoodsInfo(first.getGoodsNo());
			assertThat(latest.getGoodsNameKo()).isEqualTo("latest-update");
			assertThat(latest.getGoodsInfoHistoryNo()).isGreaterThan(second.getGoodsInfoHistoryNo());
		} finally {
			deleteTestGoods(first);
		}
	}

	@Test
	void concurrentUpdatesWithSameHistoryAllowOnlyOneCommit() throws Exception {
		GoodsInfoDto first = insertTestGoods();
		java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);
		try {
			GoodsInfoDto second = goodsInfoService.selectGoodsInfo(first.getGoodsNo());
			first.setGoodsNameKo("concurrent-first");
			second.setGoodsNameKo("concurrent-second");
			java.util.concurrent.CountDownLatch ready = new java.util.concurrent.CountDownLatch(2);
			java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
			java.util.List<java.util.concurrent.Future<Boolean>> results = new java.util.ArrayList<>();
			for (GoodsInfoDto goods : java.util.Arrays.asList(first, second)) {
				results.add(executor.submit(() -> {
					ready.countDown();
					if (!start.await(5, java.util.concurrent.TimeUnit.SECONDS)) {
						throw new IllegalStateException("Concurrent test start timed out");
					}
					try {
						goodsInfoService.updateGoodsInfo(goods);
						return true;
					} catch (OptimisticLockingFailureException expected) {
						return false;
					}
				}));
			}
			assertThat(ready.await(5, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
			start.countDown();
			int successful = 0;
			for (java.util.concurrent.Future<Boolean> result : results) {
				if (result.get(10, java.util.concurrent.TimeUnit.SECONDS)) {
					successful++;
				}
			}
			assertThat(successful).isEqualTo(1);
			GoodsInfoDto latest = goodsInfoService.selectGoodsInfo(first.getGoodsNo());
			assertThat(latest.getGoodsInfoHistoryNo()).isGreaterThan(first.getGoodsInfoHistoryNo());
			assertThat(latest.getGoodsNameKo()).isIn("concurrent-first", "concurrent-second");
		} finally {
			executor.shutdownNow();
			executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
			deleteTestGoods(first);
		}
	}

	@Test
	void nonPriceUpdateRecordsChangedFieldsWithoutPriceHistory() throws Exception {
		GoodsInfoDto goods = insertTestGoods();
		try {
			String previousName = goods.getGoodsNameKo();
			goods.setGoodsNameKo("changed-name");
			Long historyNo = goodsInfoService.updateGoodsInfo(goods);
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM goods_info_history WHERE GOODS_NO = ?",
					Integer.class, goods.getGoodsNo())).isEqualTo(2);
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM goods_price_history WHERE GOODS_NO = ?",
					Integer.class, goods.getGoodsNo())).isEqualTo(1);
			assertThat(jdbc.queryForObject("SELECT JSON_UNQUOTE(JSON_EXTRACT(BEFORE_VALUES, '$.goodsNameKo')) "
					+ "FROM goods_info_history WHERE GOODS_INFO_HISTORY_NO = ?", String.class, historyNo)).isEqualTo(previousName);
			assertThat(jdbc.queryForObject("SELECT JSON_UNQUOTE(JSON_EXTRACT(AFTER_VALUES, '$.goodsNameKo')) "
					+ "FROM goods_info_history WHERE GOODS_INFO_HISTORY_NO = ?", String.class, historyNo)).isEqualTo("changed-name");
			assertThat(jdbc.queryForObject("SELECT CHANGED_FIELDS FROM goods_info_history WHERE GOODS_INFO_HISTORY_NO = ?",
					String.class, historyNo)).contains("goodsNameKo").doesNotContain("salePrice");
			com.fasterxml.jackson.databind.JsonNode response = objectMapper.readTree(objectMapper.writeValueAsString(
					goodsInfoService.selectGoodsHistory(goods.getGoodsNo(), 1, 20)));
			assertThat(response.get("goodsInfoHistory").get(0).get("beforeValues").isObject()).isTrue();
			assertThat(response.get("goodsInfoHistory").get(0).get("changedFields").isArray()).isTrue();
			assertThat(response.get("goodsPriceHistoryTotalCount").asInt()).isEqualTo(1);
		} finally {
			deleteTestGoods(goods);
		}
	}

	@Test
	void priceUpdateIsLinkedToInformationHistoryAndRecordsExactAmounts() throws Exception {
		GoodsInfoDto goods = insertTestGoods();
		try {
			java.math.BigDecimal previous = goods.getSalePrice();
			goods.setSalePrice(previous.add(java.math.BigDecimal.TEN));
			Long historyNo = goodsInfoService.updateGoodsInfo(goods);
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM goods_price_history WHERE GOODS_NO = ?",
					Integer.class, goods.getGoodsNo())).isEqualTo(2);
			assertThat(jdbc.queryForObject("SELECT BEFORE_SALE_PRICE FROM goods_price_history WHERE GOODS_INFO_HISTORY_NO = ?",
					java.math.BigDecimal.class, historyNo)).isEqualByComparingTo(previous);
			assertThat(jdbc.queryForObject("SELECT AFTER_SALE_PRICE FROM goods_price_history WHERE GOODS_INFO_HISTORY_NO = ?",
					java.math.BigDecimal.class, historyNo)).isEqualByComparingTo(goods.getSalePrice());
			assertThat(jdbc.queryForObject("SELECT AFTER_VALUES FROM goods_info_history WHERE GOODS_INFO_HISTORY_NO = ?",
					String.class, historyNo)).doesNotContain("salePrice");
		} finally {
			deleteTestGoods(goods);
		}
	}

	@Test
	void transactionRollbackRestoresProductAndBothHistories() throws Exception {
		GoodsInfoDto goods = insertTestGoods();
		try {
			String previousName = goods.getGoodsNameKo();
			goods.setGoodsNameKo("must-rollback");
			goods.setSalePrice(goods.getSalePrice().add(java.math.BigDecimal.TEN));
			org.springframework.transaction.support.TransactionTemplate transaction =
					new org.springframework.transaction.support.TransactionTemplate(transactionManager);
			assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
				try {
					goodsInfoService.updateGoodsInfo(goods);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				throw new IllegalStateException("simulate failure after both histories were recorded");
			})).isInstanceOf(IllegalStateException.class);
			assertThat(goodsInfoService.selectGoodsInfo(goods.getGoodsNo()).getGoodsNameKo()).isEqualTo(previousName);
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM goods_info_history WHERE GOODS_NO = ?",
					Integer.class, goods.getGoodsNo())).isEqualTo(1);
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM goods_price_history WHERE GOODS_NO = ?",
					Integer.class, goods.getGoodsNo())).isEqualTo(1);
		} finally {
			deleteTestGoods(goods);
		}
	}

	private GoodsInfoDto insertTestGoods() throws Exception {
		try (java.sql.Connection connection = dataSource.getConnection()) {
			assertThat(connection.getMetaData().getURL()).contains("127.0.0.1:3306/");
		}
		Long sampleNo = jdbc.queryForObject("SELECT MIN(GOODS_NO) FROM goods_info", Long.class);
		GoodsInfoDto goods = goodsInfoService.selectGoodsInfo(sampleNo);
		goods.setGoodsCode("MQTEST-" + UUID.randomUUID().toString().substring(0, 12));
		goods.setGoodsNameKo("history-test");
		Long goodsNo = goodsInfoService.insertGoodsInfo(goods, null);
		goods = goodsInfoService.selectGoodsInfo(goodsNo);
		goods.setUpdtId(goods.getRegId());
		return goods;
	}

	private void deleteTestGoods(GoodsInfoDto goods) {
		jdbc.update("DELETE FROM goods_price_history WHERE GOODS_NO = ?", goods.getGoodsNo());
		jdbc.update("DELETE FROM goods_info_history WHERE GOODS_NO = ?", goods.getGoodsNo());
		jdbc.update("DELETE FROM goods_info WHERE GOODS_NO = ? AND GOODS_CODE = ?",
				goods.getGoodsNo(), goods.getGoodsCode());
	}

}
