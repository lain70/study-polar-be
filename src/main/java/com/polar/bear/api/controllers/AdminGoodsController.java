package com.polar.bear.api.controllers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.GoodsInfoService;
import com.polar.bear.api.service.GoodsUpdateService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/goods")
@RequiredArgsConstructor
@Slf4j
public class AdminGoodsController {
	private static final BigDecimal MAX_GOODS_AMOUNT = new BigDecimal("9999999999999.99");
	private static final int MAX_DETAIL_DESCRIPTION_BYTES = 65535;
	@Value("${service-key}")
	private String originServiceKey;
	private final GoodsInfoService goodsInfoService;
	private final GoodsUpdateService goodsUpdateService;
	private final JwtUtil jwtUtil;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getGoodsList(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(defaultValue = "GOODS_NAME") String searchType,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredTo,
			@RequestParam(required = false) List<String> statuses,
			@RequestParam(required = false) List<Long> brandNos,
			@RequestParam(required = false) List<Long> categoryNos,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<Map<String, Object>>(goodsInfoService.selectGoodsList(searchType, keyword,
					registeredFrom, registeredTo, statuses, brandNos, categoryNos, page, size), headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/goods) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{goodsNo}", produces = "application/json")
	public ResponseEntity<?> getGoods(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Long goodsNo) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			GoodsInfoDto goods = goodsInfoService.selectGoodsInfo(goodsNo);
			if (goods == null) {
				return ResponseUtil.getResponseEntity("상품을 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<GoodsInfoDto>(goods, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/goods/{goodsNo}) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(consumes = "multipart/form-data", produces = "application/json")
	public ResponseEntity<?> createGoods(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestPart("goods") GoodsInfoDto goods,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			String validationMessage = validateGoods(goods);
			if (validationMessage != null) {
				return ResponseUtil.getResponseEntity(validationMessage, headers, HttpStatus.BAD_REQUEST);
			}
			LoginRedisVo login = jwtUtil.validateAccessToken(token);
			goods.setRegId(login.getUserId());
			Long goodsNo = goodsInfoService.insertGoodsInfo(goods, images);
			Map<String, Object> result = new HashMap<>();
			result.put("goodsNo", goodsNo);
			return new ResponseEntity<Map<String, Object>>(result, headers, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/goods) [POST]", e);
			return ResponseUtil.getResponseEntity("상품 등록에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{goodsNo}/history", produces = "application/json")
	public ResponseEntity<?> getGoodsHistory(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Long goodsNo, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			if (goodsInfoService.selectGoodsInfo(goodsNo) == null) {
				return ResponseUtil.getResponseEntity("상품을 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Map<String, Object>>(goodsInfoService.selectGoodsHistory(goodsNo, page, size), headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/goods/{goodsNo}/history) [GET]", e);
			return ResponseUtil.getResponseEntity("상품 이력을 조회하지 못했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/{goodsNo}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateGoods(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Long goodsNo, @RequestBody GoodsInfoDto goods) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			String validationMessage = validateGoodsUpdate(goods);
			if (validationMessage != null) {
				return ResponseUtil.getResponseEntity(validationMessage, headers, HttpStatus.BAD_REQUEST);
			}
			LoginRedisVo login = jwtUtil.validateAccessToken(token);
			GoodsInfoDto current = goodsInfoService.selectGoodsInfo(goodsNo);
			if (current == null) {
				return ResponseUtil.getResponseEntity("상품을 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			if (!goods.getGoodsInfoHistoryNo().equals(current.getGoodsInfoHistoryNo())) {
				throw new OptimisticLockingFailureException("상품이 변경되었습니다. 다시 조회한 후 수정해 주세요.");
			}
			goods.setGoodsNo(goodsNo);
			goods.setUpdtId(login.getUserId());
			goods.setImages(null);
			boolean queued = goodsUpdateService.update(goods);
			Map<String, Object> result = new HashMap<>();
			result.put("goodsNo", goodsNo);
			result.put("status", queued ? "ACCEPTED" : "COMPLETED");
			if (!queued) {
				result.put("goodsInfoHistoryNo", goods.getGoodsInfoHistoryNo());
			}
			return new ResponseEntity<Map<String, Object>>(result, headers,
					queued ? HttpStatus.ACCEPTED : HttpStatus.OK);
		} catch (OptimisticLockingFailureException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.CONFLICT);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/goods/{goodsNo}) [PUT]", e);
			return ResponseUtil.getResponseEntity("상품 수정에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String validateGoodsUpdate(GoodsInfoDto goods) {
		String validationMessage = validateGoods(goods);
		if (validationMessage != null) {
			return validationMessage;
		}
		if (goods.getGoodsInfoHistoryNo() == null || goods.getGoodsInfoHistoryNo() <= 0) {
			return "조회한 상품 정보 이력 번호를 입력해 주세요.";
		}
		if (StringUtils.isAnyBlank(goods.getTaxType(), goods.getShippingType())) {
			return "과세 유형과 배송 유형을 입력해 주세요.";
		}
		if (goods.getShippingFee() == null || goods.getShippingFee().compareTo(BigDecimal.ZERO) < 0) {
			return "배송비는 0 이상으로 입력해 주세요.";
		}
		return null;
	}

	private String validateGoods(GoodsInfoDto goods) {
		if (goods == null || goods.getCategoryNo() == null
				|| StringUtils.isAnyBlank(goods.getGoodsCode(), goods.getGoodsNameKo(), goods.getSellerName(), goods.getGoodsStatus(),
				goods.getUseYn(), goods.getDisplayYn())) {
			return "필수 상품 정보를 입력해 주세요.";
		}
		String storageLimitMessage = validateGoodsStorageLimits(goods);
		if (storageLimitMessage != null) {
			return storageLimitMessage;
		}
		if (goods.getCostPrice() == null || goods.getSalePrice() == null
				|| goods.getCostPrice().compareTo(BigDecimal.ZERO) < 0
				|| goods.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
			return "가격은 0 이상으로 입력해 주세요.";
		}
		if (goods.getDiscountPrice() != null && (goods.getDiscountPrice().compareTo(BigDecimal.ZERO) < 0
				|| goods.getDiscountPrice().compareTo(goods.getSalePrice()) > 0)) {
			return "할인가는 0 이상, 판매가 이하로 입력해 주세요.";
		}
		if (goods.getStockQuantity() == null || goods.getSafetyStockQuantity() == null
				|| goods.getStockQuantity() < 0 || goods.getSafetyStockQuantity() < 0) {
			return "재고 수량은 0 이상으로 입력해 주세요.";
		}
		if (goods.getDisplayStartDate() != null && goods.getDisplayEndDate() != null
				&& goods.getDisplayEndDate().isBefore(goods.getDisplayStartDate())) {
			return "전시 종료일은 시작일 이후로 입력해 주세요.";
		}
		return null;
	}

	private String validateGoodsStorageLimits(GoodsInfoDto goods) {
		String[] lengthErrors = {
				validateLength("상품 코드", goods.getGoodsCode(), 50),
				validateLength("한글 상품명", goods.getGoodsNameKo(), 200),
				validateLength("영문 상품명", goods.getGoodsNameEn(), 200),
				validateLength("판매자", goods.getSellerName(), 100),
				validateLength("제조사", goods.getManufacturer(), 100),
				validateLength("원산지", goods.getOriginCountry(), 100),
				validateLength("과세 유형", goods.getTaxType(), 20),
				validateLength("배송 유형", goods.getShippingType(), 20),
				validateLength("상품 상태", goods.getGoodsStatus(), 30),
				validateLength("사용 여부", goods.getUseYn(), 1),
				validateLength("전시 여부", goods.getDisplayYn(), 1),
				validateLength("상품 요약", goods.getShortDescription(), 500),
				validateLength("검색 키워드", goods.getSearchKeywords(), 500)
		};
		for (String error : lengthErrors) {
			if (error != null) {
				return error;
			}
		}
		if (goods.getDetailDescription() != null
				&& goods.getDetailDescription().getBytes(StandardCharsets.UTF_8).length > MAX_DETAIL_DESCRIPTION_BYTES) {
			return "상품 상세 설명은 UTF-8 기준 65535바이트 이하로 입력해 주세요.";
		}
		BigDecimal[] amounts = {goods.getCostPrice(), goods.getSalePrice(), goods.getDiscountPrice(), goods.getShippingFee()};
		for (BigDecimal amount : amounts) {
			if (amount != null && amount.compareTo(MAX_GOODS_AMOUNT) > 0) {
				return "금액은 9999999999999.99 이하로 입력해 주세요.";
			}
		}
		return null;
	}

	private String validateLength(String label, String value, int maximumLength) {
		if (value != null && value.codePointCount(0, value.length()) > maximumLength) {
			return label + "은(는) " + maximumLength + "자 이하로 입력해 주세요.";
		}
		return null;
	}

	private boolean isValidServiceKey(String serviceKey) {
		return StringUtils.equals(originServiceKey, serviceKey);
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return headers;
	}
}
