package com.polar.bear.api.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = { "currentUserPwd", "newUserPwd" })
@NoArgsConstructor
public class UserInfoUpdateRequestVo {

	private String currentUserPwd;
	private String newUserPwd;
	private String userName;
	private String userPhone;
}
