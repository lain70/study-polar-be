package com.polar.bear.api.controllers;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.AdminMenuService;
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
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
@Slf4j
public class AdminMenuController {
	@Value("${service-key}")
	private String originServiceKey;
	private final JwtUtil jwtUtil;
	private final AdminMenuService adminMenuService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getMenus(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {
			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				throw new IllegalArgumentException("서비스키가 올바르지 않습니다.");
			}
			LoginRedisVo login = jwtUtil.validateAccessToken(token);
			if (login == null || !StringUtils.equals("ADMIN", login.getType())) {
				throw new IllegalArgumentException("관리자 권한이 없습니다.");
			}
			return new ResponseEntity<>(adminMenuService.selectActiveMenus(), headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/menus) [GET]", e);
			return ResponseUtil.getResponseEntity("관리자 메뉴를 불러오지 못했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
