package com.polar.bear.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.polar.bear.api.mappers.QnaInfoMapper;
import com.polar.bear.api.models.QnaInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QnaInfoService {

	@Autowired
	private QnaInfoMapper qnaInfoMapper;

	@Autowired
	PasswordEncoder passwordEncoder;

	public List<QnaInfoDto> selectQnaInfoList(Integer startNum, Integer pageCnt) throws Exception {

		Map<String, Object> params = new HashMap<>();

		if (startNum == null) {
			startNum = 0;
		}

		params.put("startNum", startNum);

		if (pageCnt == null) {
			pageCnt = 20;
		}

		params.put("pageCnt", pageCnt);

		List<QnaInfoDto> QnaInfoList = qnaInfoMapper.selectQnaInfoList(params);

		return QnaInfoList;

	}

	public Map<String, Object> selectAdminQnaInfoList(String searchType, String keyword,
			java.time.LocalDateTime registeredFrom, java.time.LocalDateTime registeredTo,
			List<String> answerYns, int page, int size) throws Exception {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 100);
		Map<String, Object> params = new HashMap<>();
		params.put("searchType", searchType);
		params.put("keyword", StringUtils.trimToNull(keyword));
		params.put("registeredFrom", registeredFrom);
		params.put("registeredTo", registeredTo);
		params.put("answerYns", answerYns);
		params.put("offset", (safePage - 1) * safeSize);
		params.put("limit", safeSize);

		Map<String, Object> result = new HashMap<>();
		result.put("items", qnaInfoMapper.selectAdminQnaInfoList(params));
		result.put("totalCount", qnaInfoMapper.selectAdminQnaInfoListCnt(params));
		result.put("page", safePage);
		result.put("size", safeSize);
		return result;
	}

	public QnaInfoDto selectAdminQnaInfoDetail(Integer qnaNo) throws Exception {
		return qnaInfoMapper.selectAdminQnaInfoDetail(qnaNo);
	}
	
	public Integer selectQnaInfoListCnt() throws Exception{
		return qnaInfoMapper.selectQnaInfoListCnt();
	};

	public QnaInfoDto selectQnaInfoDetail(Integer qnaNo, String qnaUserId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("qnaNo", qnaNo);
		params.put("qnaUserId", qnaUserId);

		QnaInfoDto qnaInfoDto = qnaInfoMapper.selectQnaInfoDetail(params);

		if (qnaInfoDto != null) {
			qnaInfoDto.decryptDto();
		}

		return qnaInfoDto;
	};

	public int insertQnaInfo(QnaInfoDto qnaInfoDto) throws Exception {

		if (qnaInfoDto != null && StringUtils.isNotBlank(qnaInfoDto.getQnaUserPwd())) {
			qnaInfoDto.setQnaUserPwd(passwordEncoder.encode(qnaInfoDto.getQnaUserPwd()));
		}

		int result = qnaInfoMapper.insertQnaInfo(qnaInfoDto);

		return result;
	};

	public boolean checkQnaUserPwd(QnaInfoDto qnaInfoDto) throws Exception {
		String originPwd = qnaInfoMapper.selectQnaUserPwd(qnaInfoDto);

		if (StringUtils.isNotBlank(qnaInfoDto.getQnaUserPwd()) && 
				(StringUtils.isNotBlank(originPwd)	&& passwordEncoder.matches(qnaInfoDto.getQnaUserPwd(), originPwd))) {
			return true;
		}

		return false;
	};

	public int updateQnaInfo(QnaInfoDto qnaInfoDto) throws Exception {

		int result = qnaInfoMapper.updateQnaInfo(qnaInfoDto);

		return result;
	};

	@Transactional
	public int updateQnaCsrNo(List<String> qnaNoList, List<String> qnaUserIdList, Integer csrNo) throws Exception {
		int result = 0;
		for(int i = 0; i < qnaNoList.size(); i++) {
			QnaInfoDto qnaInfoDto = new QnaInfoDto();
			qnaInfoDto.setQnaNo(Integer.valueOf(qnaNoList.get(i)));
			qnaInfoDto.setQnaUserId(qnaUserIdList.get(i));
			qnaInfoDto.setCsrNo(csrNo);
			
			result += qnaInfoMapper.updateQnaCsrNo(qnaInfoDto);
		}

		return result;
	};
	
	@Transactional
	public int insertQnaReplyInfo(QnaInfoDto qnaInfoDto) throws Exception{
		int result = qnaInfoMapper.insertQnaReplyInfo(qnaInfoDto);
		
		if(result > 0) {
			qnaInfoDto.setAnswerYn("Y");
			result = qnaInfoMapper.updateQnaAnswerYn(qnaInfoDto);			
		}
		
		return result;
	}
	
	@Transactional
	public int updateQnaReplyInfo(QnaInfoDto qnaInfoDto) throws Exception{
		int result = qnaInfoMapper.updateQnaReplyInfo(qnaInfoDto);
		
		if(result > 0) {
			qnaInfoDto.setAnswerYn("Y");
			result = qnaInfoMapper.updateQnaAnswerYn(qnaInfoDto);			
		}
		
		return result;
	}
}
