package com.polar.bear.api.controllers;

import java.util.Optional;
import javax.validation.Valid;
import com.polar.bear.api.models.CartRequestVo;
import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.CartService;
import com.polar.bear.api.service.UserInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/cart")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	private final CartService cartService;
	private final UserInfoService userInfoService;
	@Value("${service-key}")
	private String originServiceKey;

	@GetMapping
	public ResponseEntity<?> getItems(Authentication authentication,
			@RequestHeader("x-auth-user-service-key") String serviceKey) {
		return execute(authentication, serviceKey, userNo -> cartService.getItems(userNo));
	}

	@PostMapping("/{goodsNo}")
	public ResponseEntity<?> addItem(Authentication authentication,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Long goodsNo,
			@Valid @RequestBody CartRequestVo request) {
		return execute(authentication, serviceKey, userNo -> {
			cartService.addItem(userNo, goodsNo, request.getQuantity());
			return null;
		});
	}

	@PutMapping("/{goodsNo}")
	public ResponseEntity<?> updateQuantity(Authentication authentication,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Long goodsNo,
			@Valid @RequestBody CartRequestVo request) {
		return execute(authentication, serviceKey, userNo -> {
			cartService.updateQuantity(userNo, goodsNo, request.getQuantity());
			return null;
		});
	}

	@DeleteMapping("/{goodsNo}")
	public ResponseEntity<?> deleteItem(Authentication authentication,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Long goodsNo) {
		return execute(authentication, serviceKey, userNo -> {
			cartService.deleteItem(userNo, goodsNo);
			return null;
		});
	}

	private ResponseEntity<?> execute(Authentication authentication, String serviceKey, CartAction action) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
		try {
			if (StringUtils.isBlank(serviceKey) || !StringUtils.equals(originServiceKey, serviceKey)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "서비스키가 올바르지 않습니다.");
			}
			Object details = authentication == null ? null : authentication.getDetails();
			Object login = details instanceof Optional ? ((Optional<?>) details).orElse(null) : null;
			if (!(login instanceof LoginRedisVo) || !"USER".equals(((LoginRedisVo) login).getType())) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
			}
			UserInfoDto user = userInfoService.selectUserInfoById(((LoginRedisVo) login).getUserId());
			if (user == null || user.getUserNo() == null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "회원 정보를 확인할 수 없습니다.");
			}
			Object result = action.run(user.getUserNo());
			return new ResponseEntity<>(result, headers, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return ResponseUtil.getResponseEntity(e.getReason(), headers, e.getStatus());
		} catch (Exception e) {
			log.error("장바구니 처리 실패", e);
			return ResponseUtil.getResponseEntity("장바구니 처리에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@FunctionalInterface
	private interface CartAction {
		Object run(Integer userNo);
	}
}
