package com.polar.bear.api.controllers;

import java.util.List;

import com.polar.bear.api.models.CustomerGoodsDto;
import com.polar.bear.api.service.GoodsInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@Slf4j
public class GoodsController {
	private static final int FEATURED_GOODS_LIMIT = 5;

	@Value("${service-key}")
	private String originServiceKey;
	private final GoodsInfoService goodsInfoService;

	@GetMapping(value = "/featured", produces = "application/json")
	public ResponseEntity<?> getFeaturedGoods(@RequestHeader("x-auth-user-service-key") String serviceKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {
			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			List<CustomerGoodsDto> goods = goodsInfoService.selectFeaturedGoods(FEATURED_GOODS_LIMIT);
			return new ResponseEntity<List<CustomerGoodsDto>>(goods, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/goods/featured) [GET]", e);
			return ResponseUtil.getResponseEntity("상품 정보를 불러오지 못했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
