package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.polar.bear.api.utils.Aes256Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;

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
@Alias("csrInfoDto")
public class CsrInfoDto implements Serializable {
	
	private static final long serialVersionUID = 8657340687945699764L;
	
	private Integer csrNo;
	private String csrId;
	private String csrPwd;
	private String csrName;
	private String csrPhone;
	private String csrStatus;
	private Integer loginFailCnt;
	private String lastLoginDate;
	private LocalDateTime regDate;
	private String regId;
	private LocalDateTime updtDate;
	private String updtId;
	
	public CsrInfoDto decryptDto() {
		if(StringUtils.isNotBlank(csrName)) {
			this.csrName = Aes256Util.getDecrypt(this.csrName);
		}
		return this;
	}
}
