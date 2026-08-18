package com.polar.bear.api.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polar.bear.api.jwt.AdminLoginResponseVo;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.jwt.LoginRequestVo;
import com.polar.bear.api.models.AdminInfoDto;
import com.polar.bear.api.redis.LoginRedisRepository;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.AdminInfoService;
import com.polar.bear.api.service.LoginHistoryService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

	private static final String ADMIN_KEY_PREFIX = "ADMIN_";
	private static final String ACTIVE_ADMIN_STATUS = "AD_ST_001";
	private static final String USE_Y = "Y";

	@Value("${service-key}")
	private String originServiceKey;

	private final AdminInfoService adminInfoService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final LoginRedisRepository loginRedisRepository;
	private final LoginHistoryService loginHistoryService;

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> login(
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody LoginRequestVo loginRequestVo,
			HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

		try {
			if (StringUtils.isBlank(serviceKey) || !StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (loginRequestVo == null || StringUtils.isBlank(loginRequestVo.getUserId())
					|| StringUtils.isBlank(loginRequestVo.getUserPwd())) {
				return ResponseUtil.getResponseEntity("아이디와 비밀번호를 입력해 주세요.", headers, HttpStatus.BAD_REQUEST);
			}

			AdminInfoDto adminInfoDto = adminInfoService.selectAdminInfoById(loginRequestVo.getUserId());
			if (adminInfoDto == null || !passwordEncoder.matches(loginRequestVo.getUserPwd(), adminInfoDto.getAdminPwd())) {
				return ResponseUtil.getResponseEntity("ID/비번이 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);
			}

			if (!StringUtils.equals(ACTIVE_ADMIN_STATUS, adminInfoDto.getAdminStatus())
					|| !StringUtils.equals(USE_Y, adminInfoDto.getUseYn())) {
				return ResponseUtil.getResponseEntity("사용할 수 없는 관리자 계정입니다.", headers, HttpStatus.FORBIDDEN);
			}

			String adminKey = ADMIN_KEY_PREFIX + adminInfoDto.getAdminNo();
			String accessToken = jwtUtil.generateToken(adminKey, adminInfoDto.getAdminId(), "ACCESS");
			String refreshToken = jwtUtil.generateToken(adminKey, adminInfoDto.getAdminId(), "REFRESH");
			long accessTokenTime = jwtUtil.getAccessTokenTime(accessToken);
			LoginRedisVo loginRedisVo = new LoginRedisVo(adminKey, adminInfoDto.getAdminId(), "ADMIN",
					adminInfoDto, accessToken, refreshToken, accessTokenTime);
			loginHistoryService.saveAdminLoginHistory(adminInfoDto.getAdminNo(), request);
			loginRedisRepository.save(loginRedisVo);

			return ResponseEntity.ok(new AdminLoginResponseVo(
					adminInfoDto.getAdminNo(),
					adminInfoDto.getAdminId(),
					adminInfoDto.getAdminName(),
					adminInfoDto.getAdminDepartment(),
					adminInfoDto.getAdminPosition(),
					accessToken,
					refreshToken));
		} catch (Exception e) {
			log.error("API(/api/admin/auth/login) [POST]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> logout(
			@RequestHeader(value = "Authorization") String token,
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

		try {
			if (StringUtils.isBlank(serviceKey) || !StringUtils.equals(originServiceKey, serviceKey)
					|| StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			LoginRedisVo loginRedisVo = jwtUtil.validateAccessToken(token);
			if (!StringUtils.equals("ADMIN", loginRedisVo.getType())) {
				return ResponseUtil.getResponseEntity("관리자 권한이 없습니다.", headers, HttpStatus.FORBIDDEN);
			}

			loginRedisRepository.delete(loginRedisVo.getUserKey());
			new SecurityContextLogoutHandler().logout(request, response, authentication);
			return ResponseUtil.getResponseEntity("로그 아웃 되었습니다.", headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/admin/auth/logout) [POST]", e);
			return ResponseUtil.getResponseEntity("로그 아웃에 실패했습니다.", headers, HttpStatus.UNAUTHORIZED);
		}
	}
}
