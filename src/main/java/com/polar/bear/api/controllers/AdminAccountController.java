package com.polar.bear.api.controllers;

import java.util.List;
import java.util.Map;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.AdminInfoService;
import com.polar.bear.api.service.UserInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAccountController {
	@Value("${service-key}")
	private String originServiceKey;
	private final UserInfoService userInfoService;
	private final AdminInfoService adminInfoService;
	private final JwtUtil jwtUtil;

	@GetMapping(value = "/users", produces = "application/json")
	public ResponseEntity<?> getUserList(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(defaultValue = "USER_NAME") String searchType, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String registeredFrom, @RequestParam(required = false) String registeredTo,
			@RequestParam(required = false) List<String> grades, @RequestParam(required = false) List<String> statuses) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			List<Map<String, Object>> users = userInfoService.selectAdminUserList(searchType, keyword,
					registeredFrom, registeredTo, grades, statuses);
			return new ResponseEntity<List<Map<String, Object>>>(users, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/users) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/users/{userNo}", produces = "application/json")
	public ResponseEntity<?> getUserDetail(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Integer userNo) {
		return getDetail(serviceKey, userNo, true);
	}

	@GetMapping(value = "/admins", produces = "application/json")
	public ResponseEntity<?> getAdminList(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(defaultValue = "ADMIN_NAME") String searchType, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String registeredFrom, @RequestParam(required = false) String registeredTo,
			@RequestParam(required = false) List<String> departments, @RequestParam(required = false) List<String> positions,
			@RequestParam(required = false) List<String> statuses, @RequestParam(required = false) List<String> useYns) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			List<Map<String, Object>> admins = adminInfoService.selectAdminList(searchType, keyword,
					registeredFrom, registeredTo, departments, positions, statuses, useYns);
			return new ResponseEntity<List<Map<String, Object>>>(admins, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/admins) [GET]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/admins/{adminNo}", produces = "application/json")
	public ResponseEntity<?> getAdminDetail(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@PathVariable Integer adminNo) {
		return getDetail(serviceKey, adminNo, false);
	}

	@PutMapping(value = "/users/{userNo}", produces = "application/json")
	public ResponseEntity<?> updateUser(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestHeader("Authorization") String token, @PathVariable Integer userNo,
			@RequestBody Map<String, Object> values) {
		return updateAccount(serviceKey, token, userNo, values, true);
	}

	@PutMapping(value = "/admins/{adminNo}", produces = "application/json")
	public ResponseEntity<?> updateAdmin(@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestHeader("Authorization") String token, @PathVariable Integer adminNo,
			@RequestBody Map<String, Object> values) {
		return updateAccount(serviceKey, token, adminNo, values, false);
	}

	private ResponseEntity<?> updateAccount(String serviceKey, String token, Integer accountNo,
			Map<String, Object> values, boolean user) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers,
						HttpStatus.BAD_REQUEST);
			}
			LoginRedisVo login = jwtUtil.validateAccessToken(token);
			int updated = user ? userInfoService.updateAdminUser(accountNo, values, login.getUserId())
					: adminInfoService.updateAdmin(accountNo, values, login.getUserId());
			if (updated == 0) {
				return ResponseUtil.getResponseEntity("대상을 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			Map<String, Object> account = user
					? userInfoService.selectAdminUserDetail(accountNo)
					: adminInfoService.selectAdminDetail(accountNo);
			return new ResponseEntity<Map<String, Object>>(account, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/account) [PUT]", e);
			return ResponseUtil.getResponseEntity("정보 수정에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<?> getDetail(String serviceKey, Integer accountNo, boolean user) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			Map<String, Object> account = user
					? userInfoService.selectAdminUserDetail(accountNo)
					: adminInfoService.selectAdminDetail(accountNo);
			if (account == null) {
				return ResponseUtil.getResponseEntity(user ? "회원을 찾을 수 없습니다." : "관리자를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Map<String, Object>>(account, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/{}) [GET]", user ? "users" : "admins", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
