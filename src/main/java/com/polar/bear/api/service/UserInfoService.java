package com.polar.bear.api.service;

import com.polar.bear.api.mappers.UserInfoMapper;
import com.polar.bear.api.models.UserInfoDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfoService {

	private final UserInfoMapper userInfoMapper;
	private final PasswordEncoder passwordEncoder;

	public UserInfoDto selectUserInfoById(String userId) throws Exception {
		return userInfoMapper.selectUserInfoById(userId);
	}

	public int insertUserInfo(UserInfoDto userInfoDto) throws Exception {
		userInfoDto.setUserPwd(passwordEncoder.encode(userInfoDto.getUserPwd()));
		return userInfoMapper.insertUserInfo(userInfoDto);
	}

	public boolean matchesPassword(String rawPassword, UserInfoDto userInfoDto) {
		return userInfoDto != null && passwordEncoder.matches(rawPassword, userInfoDto.getUserPwd());
	}

	public int updateUserInfo(UserInfoDto userInfoDto) throws Exception {
		if (userInfoDto.getUserPwd() != null) {
			userInfoDto.setUserPwd(passwordEncoder.encode(userInfoDto.getUserPwd()));
		}
		return userInfoMapper.updateUserInfo(userInfoDto);
	}
}
