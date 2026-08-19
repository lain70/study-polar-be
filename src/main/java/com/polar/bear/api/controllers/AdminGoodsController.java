package com.polar.bear.api.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.GoodsInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
	@Value("${service-key}")
	private String originServiceKey;
	private final GoodsInfoService goodsInfoService;
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

	private String validateGoods(GoodsInfoDto goods) {
		if (goods == null || goods.getCategoryNo() == null
				|| StringUtils.isAnyBlank(goods.getGoodsCode(), goods.getGoodsNameKo(), goods.getSellerName(), goods.getGoodsStatus(),
				goods.getUseYn(), goods.getDisplayYn())) {
			return "필수 상품 정보를 입력해 주세요.";
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

	private boolean isValidServiceKey(String serviceKey) {
		return StringUtils.equals(originServiceKey, serviceKey);
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return headers;
	}
}
