package com.polar.bear.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polar.bear.api.mappers.GoodsInfoMapper;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.models.GoodsInfoHistoryDto;
import com.polar.bear.api.models.GoodsPriceHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class GoodsHistoryService {
	private final GoodsInfoMapper mapper;
	private final ObjectMapper objectMapper;

	// The caller holds the goods row lock and transaction; both histories must commit with the goods update.
	@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
	public Long record(GoodsInfoDto before, GoodsInfoDto after, String changeType) throws Exception {
		Map<String, Object> oldValues = before == null ? null : snapshot(before);
		Map<String, Object> newValues = snapshot(after);
		List<String> changed = new ArrayList<>();
		for (String field : newValues.keySet()) {
			if (oldValues == null || !same(oldValues.get(field), newValues.get(field))) {
				changed.add(field);
			}
		}
		GoodsInfoHistoryDto history = new GoodsInfoHistoryDto();
		history.setGoodsNo(after.getGoodsNo());
		history.setChangeType(changeType);
		history.setBeforeValues(oldValues == null ? null : objectMapper.writeValueAsString(oldValues));
		history.setAfterValues(objectMapper.writeValueAsString(newValues));
		history.setChangedFields(objectMapper.writeValueAsString(changed));
		history.setRegId(after.getUpdtId());
		if (mapper.insertGoodsInfoHistory(history) != 1 || history.getGoodsInfoHistoryNo() == null) {
			throw new DataIntegrityViolationException("상품 정보 이력을 저장하지 못했습니다.");
		}
		if (before == null || !same(before.getCostPrice(), after.getCostPrice())
				|| !same(before.getSalePrice(), after.getSalePrice())
				|| !same(before.getDiscountPrice(), after.getDiscountPrice())) {
			GoodsPriceHistoryDto price = new GoodsPriceHistoryDto();
			price.setGoodsInfoHistoryNo(history.getGoodsInfoHistoryNo());
			price.setGoodsNo(after.getGoodsNo());
			price.setChangeType(changeType);
			price.setBeforeCostPrice(before == null ? null : before.getCostPrice());
			price.setAfterCostPrice(after.getCostPrice());
			price.setBeforeSalePrice(before == null ? null : before.getSalePrice());
			price.setAfterSalePrice(after.getSalePrice());
			price.setBeforeDiscountPrice(before == null ? null : before.getDiscountPrice());
			price.setAfterDiscountPrice(after.getDiscountPrice());
			price.setRegId(after.getUpdtId());
			if (mapper.insertGoodsPriceHistory(price) != 1) {
				throw new DataIntegrityViolationException("상품 가격 이력을 저장하지 못했습니다.");
			}
		}
		return history.getGoodsInfoHistoryNo();
	}

	private boolean same(Object first, Object second) {
		if (first instanceof BigDecimal && second instanceof BigDecimal) {
			return ((BigDecimal) first).compareTo((BigDecimal) second) == 0;
		}
		return Objects.equals(first, second);
	}

	private Map<String, Object> snapshot(GoodsInfoDto goods) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("goodsCode", goods.getGoodsCode());
		values.put("goodsNameKo", goods.getGoodsNameKo());
		values.put("goodsNameEn", goods.getGoodsNameEn());
		values.put("brandNo", goods.getBrandNo());
		values.put("categoryNo", goods.getCategoryNo());
		values.put("sellerName", goods.getSellerName());
		values.put("manufacturer", goods.getManufacturer());
		values.put("originCountry", goods.getOriginCountry());
		values.put("stockQuantity", goods.getStockQuantity());
		values.put("safetyStockQuantity", goods.getSafetyStockQuantity());
		values.put("taxType", goods.getTaxType());
		values.put("shippingType", goods.getShippingType());
		values.put("shippingFee", goods.getShippingFee());
		values.put("goodsStatus", goods.getGoodsStatus());
		values.put("useYn", goods.getUseYn());
		values.put("displayYn", goods.getDisplayYn());
		values.put("displayStartDate", goods.getDisplayStartDate());
		values.put("displayEndDate", goods.getDisplayEndDate());
		values.put("shortDescription", goods.getShortDescription());
		values.put("detailDescription", goods.getDetailDescription());
		values.put("searchKeywords", goods.getSearchKeywords());
		return values;
	}
}
