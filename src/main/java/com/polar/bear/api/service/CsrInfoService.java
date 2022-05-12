package com.polar.bear.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polar.bear.api.mappers.CsrInfoMapper;
import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.models.QnaInfoDto;
import com.polar.bear.api.utils.Aes256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CsrInfoService {

	@Autowired
	private CsrInfoMapper csrInfoMapper;
	
	@Autowired 
	PasswordEncoder passwordEncoder;
	
	public CsrInfoDto selectCsrInfoDetailById(String csrId) throws Exception{
		Map<String, Object> params = new HashMap<>();
		
		params.put("csrId", csrId);
		
		CsrInfoDto csrInfoDto = csrInfoMapper.selectCsrInfoDetailById(params);
		
		return csrInfoDto;
	};
	
	public CsrInfoDto selectCsrInfoDetailByNo(String csrNo) throws Exception{
		Map<String, Object> params = new HashMap<>();
		
		params.put("csrNo", csrNo);
		
		CsrInfoDto csrInfoDto = csrInfoMapper.selectCsrInfoDetailById(params);
		
		return csrInfoDto;
	};
	
    public int updateCsrLoginFailCnt(String csrId, int loginFailCnt) throws Exception {
		Map<String, Object> params = new HashMap<>();
		
		params.put("csrId", csrId);
		params.put("loginFailCnt", loginFailCnt);
		
		int result = csrInfoMapper.updateCsrLoginFailCnt(params);
		
		return result;
		
    }
    
    public int updateCsrLastLoginDate(String csrId) throws Exception{
    	Map<String, Object> params = new HashMap<>();
		
		params.put("csrId", csrId);
		
		int result = csrInfoMapper.updateCsrLastLoginDate(params);
		
		return result;
    }
    
    public List<QnaInfoDto> selectCsrQnaInfoList(Integer startNum, Integer pageCnt) throws Exception {

		Map<String, Object> params = new HashMap<>();

		if (startNum == null) {
			startNum = 0;
		}

		params.put("startNum", startNum);

		if (pageCnt == null) {
			pageCnt = 20;
		}

		params.put("pageCnt", pageCnt);
		
		List<QnaInfoDto> QnaInfoList = csrInfoMapper.selectCsrQnaInfoList(params);

		return QnaInfoList;	
    }
    
    public int selectQnaCsrNo(Integer qnaNo, String qnaUserId, Integer csrNo) throws Exception{
    	QnaInfoDto qnaInfoDto = new QnaInfoDto();
    	qnaInfoDto.setQnaNo(qnaNo);
    	qnaInfoDto.setQnaUserId(qnaUserId);
    	qnaInfoDto.setCsrNo(csrNo);
    	
    	int result = csrInfoMapper.selectQnaCsrNo(qnaInfoDto);
    	
    	return result;
    }
    
    public int insertCsrInfo(CsrInfoDto csrInfoDto) throws Exception{
    	csrInfoDto.setCsrName(Aes256Util.getEncrypt(csrInfoDto.getCsrName()));
    	csrInfoDto.setCsrPwd(passwordEncoder.encode(csrInfoDto.getCsrPwd()));
    	
    	int result = csrInfoMapper.insertCsrInfo(csrInfoDto);
    	
    	return result;    	
    };
}
