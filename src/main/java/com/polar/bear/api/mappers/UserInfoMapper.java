package com.polar.bear.api.mappers;

import com.polar.bear.api.models.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserInfoMapper {

	UserInfoDto selectUserInfoById(@Param("userId") String userId) throws Exception;

	int insertUserInfo(UserInfoDto userInfoDto) throws Exception;

	int updateUserInfo(UserInfoDto userInfoDto) throws Exception;
}
