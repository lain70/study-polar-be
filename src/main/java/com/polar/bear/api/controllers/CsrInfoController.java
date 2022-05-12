package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.models.QnaInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.CsrInfoService;
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
@RequestMapping("api/csr")
@Slf4j
public class CsrInfoController {
	@Value("${service-key}")
	private String originServiceKey;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	private CsrInfoService csrInfoService;

	@Autowired
	private QnaInfoService qnaInfoService;
	
	@RequestMapping(value = "/qna/list", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getCsrQnaInfoList(@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestHeader(value = "Authorization") String token,
			@RequestParam Integer startNum, @RequestParam Integer pageCnt
			) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");		
		try {

			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			List<QnaInfoDto> qnaInfoList = csrInfoService.selectCsrQnaInfoList(startNum, pageCnt);

			if (qnaInfoList != null && qnaInfoList.size() > 0) {
				int listCnt = qnaInfoService.selectQnaInfoListCnt();
				headers.add("X-Total-Count", String.valueOf(listCnt));
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<List<QnaInfoDto>>(qnaInfoList, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("조회된 1:1문의 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(api/csr/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/qna/checkqnacsr", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> checkQnaCsrNo(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestHeader(value = "Authorization") String token,
			@RequestParam Integer qnaNo, 
			@RequestParam String qnaUserId

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {
			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if (qnaNo == null || StringUtils.isBlank(qnaUserId)) {
				return ResponseUtil.getResponseEntity("필수 값이 업습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);
			CsrInfoDto csrInfoDto = loginRedisVo.getCsrInfoDto();
			
			int csrNo = csrInfoDto.getCsrNo();
			
			int result = csrInfoService.selectQnaCsrNo(qnaNo, qnaUserId, csrNo);

			if (result > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("resultCnt", result);
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("해당 문의에 담당자가 아닙니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(api/csr/qna/checkqnacsr) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/qna/savecsrinfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateCsrQnaInfo(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestHeader(value = "Authorization") String token,
			@RequestParam(value = "qnaNoList", required = true) List<String> qnaNoList,
			@RequestParam(value = "qnaUserIdList", required =true) List<String> qnaUserIdList

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if((qnaNoList.size() < 1 || qnaUserIdList.size() < 1) || (qnaNoList.size() != qnaUserIdList.size())) {
				return ResponseUtil.getResponseEntity("필수 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);
			CsrInfoDto csrInfoDto = loginRedisVo.getCsrInfoDto();
			
			int csrNo = csrInfoDto.getCsrNo();
			
			int result = qnaInfoService.updateQnaCsrNo(qnaNoList, qnaUserIdList, csrNo);			

			if (result > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("resultCnt", result);
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("요청하신 내용이 정상처리 되지 않았습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(api/csr/qna/savecsrinfo) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/qna/detail", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getCsrQnaInfoDetail(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestHeader(value = "Authorization") String token,
			@RequestParam Integer qnaNo, @RequestParam String qnaUserId

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
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
			log.error("API(api/csr/qna/detail) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/qna/savereplay", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> saveQnaReplyInfo(
			@RequestHeader(value = "Authorization") String token,
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestBody QnaInfoDto qnaInfoDto

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if(qnaInfoDto == null || qnaInfoDto.getQnaNo() == null
					|| StringUtils.isBlank(qnaInfoDto.getQnaUserId()) || StringUtils.isBlank(qnaInfoDto.getQnaReplyContents())) {
				return ResponseUtil.getResponseEntity("필수 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);
			CsrInfoDto csrInfoDto = loginRedisVo.getCsrInfoDto();
			
			int csrNo = csrInfoDto.getCsrNo();
			
			qnaInfoDto.setRegCrsNo(csrNo);
			qnaInfoDto.setUpdtCrsNo(csrNo);
			
			int result = qnaInfoService.insertQnaReplyInfo(qnaInfoDto);

			if (result > 0 && qnaInfoDto.getQnaReplyNo() > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("qnaReplyNo", qnaInfoDto.getQnaReplyNo());
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("저장된 답변 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(api/csr/qna/savereply) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/qna/updatereplay", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateQnaReplyInfo(
			@RequestHeader(value = "Authorization") String token,
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestBody QnaInfoDto qnaInfoDto

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if(qnaInfoDto == null || qnaInfoDto.getQnaNo() == null || StringUtils.isBlank(qnaInfoDto.getQnaUserId())
					|| qnaInfoDto.getQnaReplyNo() == null || StringUtils.isBlank(qnaInfoDto.getQnaReplyContents())) {
				return ResponseUtil.getResponseEntity("필수 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);
			CsrInfoDto csrInfoDto = loginRedisVo.getCsrInfoDto();
			
			int csrNo = csrInfoDto.getCsrNo();
			
			qnaInfoDto.setUpdtCrsNo(csrNo);
			
			int result = qnaInfoService.updateQnaReplyInfo(qnaInfoDto);

			if (result > 0 && qnaInfoDto.getQnaReplyNo() > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("qnaReplyNo", qnaInfoDto.getQnaReplyNo());
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("수정된 답변 내용이 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(api/csr/qna/updatereplay) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
