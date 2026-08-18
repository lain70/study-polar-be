package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.Map;

import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.models.UserInfoUpdateRequestVo;
import com.polar.bear.api.models.UserPasswordConfirmRequestVo;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.UserInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {

	private static final int USER_ID_MAX_LENGTH = 30;

	@Value("${service-key}")
	private String originServiceKey;

	private final UserInfoService userInfoService;
	private final JwtUtil jwtUtil;

	@RequestMapping(value = "/join", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> join(
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody UserInfoDto userInfoDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

		try {
			if (StringUtils.isBlank(serviceKey) || !StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (userInfoDto == null || StringUtils.isBlank(userInfoDto.getUserId())
					|| StringUtils.isBlank(userInfoDto.getUserPwd()) || StringUtils.isBlank(userInfoDto.getUserName())) {
				return ResponseUtil.getResponseEntity("필수 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (userInfoDto.getUserId().length() > USER_ID_MAX_LENGTH) {
				return ResponseUtil.getResponseEntity("아이디는 30자 이하로 입력해 주세요.", headers, HttpStatus.BAD_REQUEST);
			}

			if (userInfoService.selectUserInfoById(userInfoDto.getUserId()) != null) {
				return ResponseUtil.getResponseEntity("이미 사용 중인 아이디입니다.", headers, HttpStatus.CONFLICT);
			}

			int result = userInfoService.insertUserInfo(userInfoDto);
			if (result > 0 && userInfoDto.getUserNo() != null) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("userNo", userInfoDto.getUserNo());
				return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.CREATED);
			}

			return ResponseUtil.getResponseEntity("회원가입에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("API(/api/user/join) [POST]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/password-confirm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> confirmPassword(
			@RequestHeader(value = "Authorization") String token,
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody UserPasswordConfirmRequestVo requestVo) {
		HttpHeaders headers = createJsonHeaders();

		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			if (requestVo == null || StringUtils.isBlank(requestVo.getUserPwd())) {
				return ResponseUtil.getResponseEntity("비밀번호를 입력해 주세요.", headers, HttpStatus.BAD_REQUEST);
			}

			UserInfoDto userInfoDto = getAuthenticatedUser(token);
			if (!userInfoService.matchesPassword(requestVo.getUserPwd(), userInfoDto)) {
				return ResponseUtil.getResponseEntity("비밀번호가 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);
			}

			return new ResponseEntity<UserInfoDto>(toProfile(userInfoDto), headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/user/password-confirm) [POST]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/info", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateUserInfo(
			@RequestHeader(value = "Authorization") String token,
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody UserInfoUpdateRequestVo requestVo) {
		HttpHeaders headers = createJsonHeaders();

		try {
			if (!isValidServiceKey(serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			if (requestVo == null || StringUtils.isBlank(requestVo.getCurrentUserPwd())
					|| StringUtils.isBlank(requestVo.getUserName())) {
				return ResponseUtil.getResponseEntity("필수 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			UserInfoDto currentUser = getAuthenticatedUser(token);
			if (!userInfoService.matchesPassword(requestVo.getCurrentUserPwd(), currentUser)) {
				return ResponseUtil.getResponseEntity("비밀번호 재확인이 필요합니다.", headers, HttpStatus.UNAUTHORIZED);
			}

			UserInfoDto updateUser = new UserInfoDto();
			updateUser.setUserNo(currentUser.getUserNo());
			updateUser.setUserId(currentUser.getUserId());
			updateUser.setUserName(requestVo.getUserName());
			updateUser.setUserPhone(requestVo.getUserPhone());
			updateUser.setUserPwd(StringUtils.isBlank(requestVo.getNewUserPwd()) ? null : requestVo.getNewUserPwd());

			if (userInfoService.updateUserInfo(updateUser) < 1) {
				return ResponseUtil.getResponseEntity("회원정보 수정에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return ResponseUtil.getResponseEntity("회원정보가 수정되었습니다.", headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/user/info) [PUT]", e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private HttpHeaders createJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return headers;
	}

	private boolean isValidServiceKey(String serviceKey) {
		return StringUtils.isNotBlank(serviceKey) && StringUtils.equals(originServiceKey, serviceKey);
	}

	private UserInfoDto getAuthenticatedUser(String token) throws Exception {
		LoginRedisVo loginRedisVo = jwtUtil.validateAccessToken(token);
		return userInfoService.selectUserInfoById(loginRedisVo.getUserId());
	}

	private UserInfoDto toProfile(UserInfoDto userInfoDto) {
		UserInfoDto profile = new UserInfoDto();
		profile.setUserId(userInfoDto.getUserId());
		profile.setUserName(userInfoDto.getUserName());
		profile.setUserPhone(userInfoDto.getUserPhone());
		return profile;
	}
}
