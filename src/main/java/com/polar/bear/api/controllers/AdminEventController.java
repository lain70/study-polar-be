package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.EventInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.EventInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
	@Value("${service-key}")
	private String originServiceKey;
	private final JwtUtil jwtUtil;
	private final EventInfoService eventInfoService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getEvents(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(required = false) Long eventNo, @RequestParam(required = false) String eventStartDate,
			@RequestParam(required = false) String eventEndDate, @RequestParam(required = false) String statuses,
			@RequestParam(required = false) String useYns, @RequestParam(required = false) String displayYns) {
		HttpHeaders headers = jsonHeaders();
		try {
			validateAdmin(token, serviceKey);
			return new ResponseEntity<>(eventInfoService.selectAdminEvents(eventNo, eventStartDate, eventEndDate,
					csv(statuses), csv(useYns), csv(displayYns)), headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/events) [GET]", e);
			return ResponseUtil.getResponseEntity("이벤트 목록을 불러오지 못했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<String> csv(String value) {
		return StringUtils.isBlank(value) ? Collections.emptyList() : Arrays.asList(value.split(","));
	}

	@GetMapping(value = "/{eventNo}", produces = "application/json")
	public ResponseEntity<?> getEvent(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Long eventNo) {
		HttpHeaders headers = jsonHeaders();
		try {
			validateAdmin(token, serviceKey);
			EventInfoDto event = eventInfoService.selectEvent(eventNo);
			return event == null
					? ResponseUtil.getResponseEntity("이벤트를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND)
					: new ResponseEntity<EventInfoDto>(event, headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/events/{eventNo}) [GET]", e);
			return ResponseUtil.getResponseEntity("이벤트를 불러오지 못했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createEvent(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @RequestBody EventInfoDto event) {
		HttpHeaders headers = jsonHeaders();
		try {
			LoginRedisVo login = validateAdmin(token, serviceKey);
			validateEvent(event);
			event.setRegId(login.getUserId());
			event.setUpdtId(login.getUserId());
			Map<String, Object> response = new HashMap<>();
			response.put("eventNo", eventInfoService.insertEvent(event));
			return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/events) [POST]", e);
			return ResponseUtil.getResponseEntity("이벤트 등록에 실패했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/{eventNo}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateEvent(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Long eventNo,
			@RequestBody EventInfoDto event) {
		HttpHeaders headers = jsonHeaders();
		try {
			LoginRedisVo login = validateAdmin(token, serviceKey);
			validateEvent(event);
			event.setEventNo(eventNo);
			event.setUpdtId(login.getUserId());
			if (eventInfoService.updateEvent(event) <= 0) {
				return ResponseUtil.getResponseEntity("이벤트를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(event, headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/events/{eventNo}) [PUT]", e);
			return ResponseUtil.getResponseEntity("이벤트 수정에 실패했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateEvent(EventInfoDto event) {
		if (event == null || StringUtils.isAnyBlank(event.getEventTitle(), event.getUseYn(), event.getDisplayYn())
				|| event.getEventStartDate() == null || event.getEventEndDate() == null) {
			throw new IllegalArgumentException("필수 이벤트 정보를 입력해 주세요.");
		}
		if (event.getEventEndDate().isBefore(event.getEventStartDate())) {
			throw new IllegalArgumentException("이벤트 종료일은 시작일 이후로 입력해 주세요.");
		}
	}

	private LoginRedisVo validateAdmin(String token, String serviceKey) throws Exception {
		if (!StringUtils.equals(originServiceKey, serviceKey)) {
			throw new IllegalArgumentException("서비스키가 올바르지 않습니다.");
		}
		LoginRedisVo login = jwtUtil.validateAccessToken(token);
		if (login == null || !StringUtils.equals("ADMIN", login.getType())) {
			throw new IllegalArgumentException("관리자 권한이 없습니다.");
		}
		return login;
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return headers;
	}
}
