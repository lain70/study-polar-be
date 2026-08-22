package com.polar.bear.api.service;

import com.polar.bear.api.mappers.UserInfoMapper;
import com.polar.bear.api.models.UserInfoDto;
import java.util.List;
import java.util.Map;
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

	public List<Map<String, Object>> selectAdminUserList(String searchType, String keyword, String registeredFrom,
			String registeredTo, List<String> grades, List<String> statuses) throws Exception {
		return userInfoMapper.selectAdminUserList(searchType, keyword, registeredFrom, registeredTo, grades, statuses);
	}

	public Map<String, Object> selectAdminUserDetail(Integer userNo) throws Exception {
		return userInfoMapper.selectAdminUserDetail(userNo);
	}

	public int updateAdminUser(Integer userNo, Map<String, Object> values, String updtId) throws Exception {
		return userInfoMapper.updateAdminUser(userNo, (String) values.get("userPhone"),
				(String) values.get("userGrade"), (String) values.get("userStatus"), updtId);
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
