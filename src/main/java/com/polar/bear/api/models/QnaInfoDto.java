package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.polar.bear.api.utils.Aes256Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.factory.annotation.Autowired;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Alias("qnaInfoDto")
public class QnaInfoDto implements Serializable {
	
	private static final long serialVersionUID = 2964634358285879655L;
	
	//qna 정보
	private Integer qnaNo;
	private Integer csrNo;
	private String qnaTitle;
	private String qnaContents;
	private String answerYn;
	private LocalDateTime regDate;
	private LocalDateTime updtDate;
	
	//qna 유저 정보
	private String qnaUserId;
	private String qnaUserPwd;
	
	//qna 답변 정보
	private Integer qnaReplyNo;
	private String qnaReplyContents;
	private LocalDateTime replyRegDate;
	private Integer regCrsNo;
	private LocalDateTime replyUpdtDate;
	private Integer updtCrsNo;
	
	private String csrName;
	
	public QnaInfoDto decryptDto() {
		if(this.csrName != null && StringUtils.isNotBlank(this.csrName)) {
			this.csrName = Aes256Util.getDecrypt(this.csrName);
		}
		
		return this;
	}

}
