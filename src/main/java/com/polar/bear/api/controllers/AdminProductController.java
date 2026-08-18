package com.polar.bear.api.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.ProductInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.ProductInfoService;
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
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {
	@Value("${service-key}")
	private String originServiceKey;
	private final ProductInfoService productInfoService;
	private final JwtUtil jwtUtil;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getProducts(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(defaultValue = "PRODUCT_NAME") String searchType,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredTo,
			@RequestParam(required = false) List<String> statuses,
			@RequestParam(required = false) List<String> brands,
			@RequestParam(required = false) List<String> categories,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<Map<String, Object>>(productInfoService.selectProductList(searchType, keyword,
					registeredFrom, registeredTo, statuses, brands, categories, page, size), headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/products) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{productNo}", produces = "application/json")
	public ResponseEntity<?> getProduct(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Long productNo) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			ProductInfoDto product = productInfoService.selectProductInfo(productNo);
			if (product == null) {
				return ResponseUtil.getResponseEntity("상품을 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<ProductInfoDto>(product, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/products/{productNo}) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(consumes = "multipart/form-data", produces = "application/json")
	public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestPart("product") ProductInfoDto product,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			String validationMessage = validateProduct(product);
			if (validationMessage != null) {
				return ResponseUtil.getResponseEntity(validationMessage, headers, HttpStatus.BAD_REQUEST);
			}
			LoginRedisVo login = jwtUtil.validateAccessToken(token);
			product.setRegId(login.getUserId());
			Long productNo = productInfoService.insertProductInfo(product, images);
			Map<String, Object> result = new HashMap<>();
			result.put("productNo", productNo);
			return new ResponseEntity<Map<String, Object>>(result, headers, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/products) [POST]", e);
			return ResponseUtil.getResponseEntity("상품 등록에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String validateProduct(ProductInfoDto product) {
		if (product == null || StringUtils.isAnyBlank(product.getProductCode(), product.getProductNameKo(),
				product.getCategoryName(), product.getSellerName(), product.getProductStatus(),
				product.getUseYn(), product.getDisplayYn())) {
			return "필수 상품 정보를 입력해 주세요.";
		}
		if (product.getCostPrice() == null || product.getSalePrice() == null
				|| product.getCostPrice().compareTo(BigDecimal.ZERO) < 0
				|| product.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
			return "가격은 0 이상으로 입력해 주세요.";
		}
		if (product.getDiscountPrice() != null && (product.getDiscountPrice().compareTo(BigDecimal.ZERO) < 0
				|| product.getDiscountPrice().compareTo(product.getSalePrice()) > 0)) {
			return "할인가는 0 이상, 판매가 이하로 입력해 주세요.";
		}
		if (product.getStockQuantity() == null || product.getSafetyStockQuantity() == null
				|| product.getStockQuantity() < 0 || product.getSafetyStockQuantity() < 0) {
			return "재고 수량은 0 이상으로 입력해 주세요.";
		}
		if (product.getDisplayStartDate() != null && product.getDisplayEndDate() != null
				&& product.getDisplayEndDate().isBefore(product.getDisplayStartDate())) {
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
