package com.polar.bear.api.mappers;

import java.util.List;
import java.util.Map;

import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.models.QnaInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
@Mapper
@Repository
public interface CsrInfoMapper {

  
	/**
	 * 상담사 정보 조회 with CsrId
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public CsrInfoDto selectCsrInfoDetailById(Map<String, Object> params) throws Exception;
    
    /**
     * 상담사 정보 조회 with CsrNo
     * @param params
     * @return
     * @throws Exception
     */
    public CsrInfoDto selectCsrInfoDetailByNo(Map<String, Object> params) throws Exception;
    
    /**
     * 로그인 실패 횟수 저장
     * @param params
     * @return
     * @throws Exception
     */
    public int updateCsrLoginFailCnt(Map<String, Object> params) throws Exception;    
    
    /**
     * 마지막 로그인 일시 업데이트
     * @return
     */
    public int updateCsrLastLoginDate(Map<String, Object> params) throws Exception;
    
    /**
     * 1:1문의 정보 조회
     * @param params
     * @return
     * @throws Exception
     */
    public List<QnaInfoDto> selectCsrQnaInfoList(Map<String, Object> params) throws Exception;
    
    /**
     * 1:1문의 담당자 확인
     * @return
     * @throws Exception
     */
    public int selectQnaCsrNo(QnaInfoDto qnaInfoDto) throws Exception;
    
    /**
     * 상담사 정보 저장(추가)
     * @param csrInfoDto
     * @return
     * @throws Exception
     */
    public int insertCsrInfo(CsrInfoDto csrInfoDto) throws Exception;

}
