package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "userPwd")
@NoArgsConstructor
@Alias("userInfoDto")
public class UserInfoDto implements Serializable {

	private static final long serialVersionUID = 3914852408290810417L;

	private Integer userNo;
	private String userName;
	private String userId;
	private String userPwd;
	private String userPhone;
	private String userGrade;
	private String userStatus;
	private LocalDateTime updtDate;
	private String updtId;
	private LocalDateTime regDate;
	private String regId;
}
