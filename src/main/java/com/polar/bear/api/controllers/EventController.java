package com.polar.bear.api.controllers;

import java.util.List;

import com.polar.bear.api.models.EventInfoDto;
import com.polar.bear.api.service.EventInfoService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
	@Value("${service-key}")
	private String originServiceKey;
	private final EventInfoService eventInfoService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getEvents(@RequestHeader("x-auth-user-service-key") String serviceKey) {
		HttpHeaders headers = jsonHeaders();
		try {
			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<List<EventInfoDto>>(eventInfoService.selectCustomerEvents(), headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("API(/api/events) [GET]", e);
			return ResponseUtil.getResponseEntity("이벤트 정보를 불러오지 못했습니다.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return headers;
	}
}
