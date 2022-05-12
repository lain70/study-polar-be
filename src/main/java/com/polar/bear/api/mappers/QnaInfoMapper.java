package com.polar.bear.api.mappers;

import java.util.List;
import java.util.Map;

import com.polar.bear.api.models.QnaInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QnaInfoMapper {  
	
	/**
	 * 1:1문의 리스트 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<QnaInfoDto> selectQnaInfoList(Map<String, Object> params) throws Exception;
	
	/**
	 * 1:1문의 리스트 갯수
	 * @return
	 * @throws Exception
	 */
	public Integer selectQnaInfoListCnt() throws Exception;
	
	/**
	 * 1:1 문의 상세 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public QnaInfoDto selectQnaInfoDetail(Map<String, Object> params) throws Exception;
	
	/**
	 * 1:1문의 정보 저장
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public int insertQnaInfo(QnaInfoDto qnaInfoDto) throws Exception ;
	
	/**
	 * 1:1문의 등록 비밀번호 조회
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public String selectQnaUserPwd(QnaInfoDto qnaInfoDto) throws Exception;
	
	/**
	 * 1:1문의 정보 수정
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public int updateQnaInfo(QnaInfoDto qnaInfoDto) throws Exception ;
	
	/**
	 * 1:1문의 담당자 지정
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public int updateQnaCsrNo(QnaInfoDto qnaInfoDto) throws Exception ;
	
	/**
	 * 1:1문의 답변 완료 처리
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public int updateQnaAnswerYn(QnaInfoDto qnaInfoDto) throws Exception;
	
	/**
	 * 1:1문의 답변 저장
	 * @param qnaInfoDto
	 * @return
	 * @throws Exception
	 */
	public int insertQnaReplyInfo(QnaInfoDto qnaInfoDto) throws Exception;
	
	/**
	 * 1:1문의 답변 수정
	 * @return
	 * @throws Exception
	 */
	public int updateQnaReplyInfo(QnaInfoDto qnaInfoDto) throws Exception;
}
