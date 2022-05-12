package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polar.bear.api.models.QnaInfoDto;
import com.polar.bear.api.service.QnaInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/qna")
@Slf4j
public class QnaInfoController {

	@Value("${service-key}")
	private String originServiceKey;

	@Autowired
	private QnaInfoService qnaInfoService;

	@RequestMapping(value = "/list", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getQnaInfoList(@RequestParam Integer startNum, @RequestParam Integer pageCnt,
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");		
		try {

			if (StringUtils.isBlank(serviceKey)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			List<QnaInfoDto> qnaInfoList = qnaInfoService.selectQnaInfoList(startNum, pageCnt);

			if (qnaInfoList != null && qnaInfoList.size() > 0) {
				int listCnt = qnaInfoService.selectQnaInfoListCnt();
				headers.add("X-Total-Count", String.valueOf(listCnt));
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<List<QnaInfoDto>>(qnaInfoList, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("조회된 1:1문의 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getQnaInfoDetail(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestParam Integer qnaNo, @RequestParam String qnaUserId

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (qnaNo == null || StringUtils.isBlank(qnaUserId)) {
				return ResponseUtil.getResponseEntity("필수 값이 업습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			QnaInfoDto qnaInfoDto = qnaInfoService.selectQnaInfoDetail(qnaNo, qnaUserId);

			if (qnaInfoDto != null) {
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<QnaInfoDto>(qnaInfoDto, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("조회된 1:1문의 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> saveQnaInfo(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestBody QnaInfoDto qnaInfoDto

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if(qnaInfoDto == null || StringUtils.isBlank(qnaInfoDto.getQnaUserId())
					 || StringUtils.isBlank(qnaInfoDto.getQnaUserPwd()) || StringUtils.isBlank(qnaInfoDto.getQnaTitle())
					 || StringUtils.isBlank(qnaInfoDto.getQnaContents())) {
				return ResponseUtil.getResponseEntity("필수 값이 업습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			int result = qnaInfoService.insertQnaInfo(qnaInfoDto);

			if (result > 0 && qnaInfoDto.getQnaNo() > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("qnaNo", qnaInfoDto.getQnaNo());
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("저장된 1:1문의 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateQnaInfo(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestBody QnaInfoDto qnaInfoDto

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if(qnaInfoDto == null || qnaInfoDto.getQnaNo() == null
					|| StringUtils.isBlank(qnaInfoDto.getQnaUserId()) || StringUtils.isBlank(qnaInfoDto.getQnaUserPwd())) {
				return ResponseUtil.getResponseEntity("필수 값이 업습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			boolean resultBool = qnaInfoService.checkQnaUserPwd(qnaInfoDto);
			
			if (!resultBool) {
				return ResponseUtil.getResponseEntity("1:1문의 등록 시 설정한 비밀번호가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			int result = qnaInfoService.updateQnaInfo(qnaInfoDto);

			if (result > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("qnaNo", qnaInfoDto.getQnaNo());
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("수정된 1:1문의 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
