package com.polar.bear.api.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.QnaInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.QnaInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
@Slf4j
public class AdminQnaController {
	@Value("${service-key}")
	private String originServiceKey;

	private final JwtUtil jwtUtil;
	private final QnaInfoService qnaInfoService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getQnaList(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey,
			@RequestParam(defaultValue = "QNA_TITLE") String searchType,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredTo,
			@RequestParam(required = false) List<String> answerYns,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
		HttpHeaders headers = jsonHeaders();
		try {
			validateAdmin(token, serviceKey);
			return new ResponseEntity<>(qnaInfoService.selectAdminQnaInfoList(searchType, keyword,
					registeredFrom, registeredTo, answerYns, page, size), headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/qna) [GET]", e);
			return ResponseUtil.getResponseEntity("문의 목록을 불러오지 못했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{qnaNo}", produces = "application/json")
	public ResponseEntity<?> getQnaDetail(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Integer qnaNo) {
		HttpHeaders headers = jsonHeaders();
		try {
			validateAdmin(token, serviceKey);
			QnaInfoDto qna = qnaInfoService.selectAdminQnaInfoDetail(qnaNo);
			if (qna == null) {
				return ResponseUtil.getResponseEntity("문의를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(qna, headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/qna/{qnaNo}) [GET]", e);
			return ResponseUtil.getResponseEntity("문의 내용을 불러오지 못했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/{qnaNo}/reply", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> saveReply(@RequestHeader("Authorization") String token,
			@RequestHeader("x-auth-user-service-key") String serviceKey, @PathVariable Integer qnaNo,
			@RequestBody QnaInfoDto replyRequest) {
		HttpHeaders headers = jsonHeaders();
		try {
			LoginRedisVo login = validateAdmin(token, serviceKey);
			if (replyRequest == null || StringUtils.isBlank(replyRequest.getQnaReplyContents())) {
				return ResponseUtil.getResponseEntity("답변 내용을 입력해 주세요.", headers, HttpStatus.BAD_REQUEST);
			}
			QnaInfoDto qna = qnaInfoService.selectAdminQnaInfoDetail(qnaNo);
			if (qna == null) {
				return ResponseUtil.getResponseEntity("문의를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
			replyRequest.setQnaNo(qnaNo);
			replyRequest.setQnaUserId(qna.getQnaUserId());
			replyRequest.setQnaReplyNo(qna.getQnaReplyNo());
			replyRequest.setRegCrsNo(qna.getCsrNo());
			replyRequest.setUpdtCrsNo(qna.getCsrNo());
			int result = qna.getQnaReplyNo() == null
					? qnaInfoService.insertQnaReplyInfo(replyRequest)
					: qnaInfoService.updateQnaReplyInfo(replyRequest);
			if (result <= 0) {
				return ResponseUtil.getResponseEntity("답변 저장에 실패했습니다.", headers, HttpStatus.NOT_FOUND);
			}
			Map<String, Object> response = new HashMap<>();
			response.put("qnaReplyNo", replyRequest.getQnaReplyNo());
			response.put("adminId", login.getUserId());
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.getResponseEntity(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("API(/api/admin/qna/{qnaNo}/reply) [POST]", e);
			return ResponseUtil.getResponseEntity("답변 저장에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private LoginRedisVo validateAdmin(String token, String serviceKey) throws Exception {
		if (StringUtils.isBlank(serviceKey) || !StringUtils.equals(originServiceKey, serviceKey)) {
			throw new IllegalArgumentException("서비스키가 올바르지 않습니다.");
		}
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("관리자 인증이 필요합니다.");
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
