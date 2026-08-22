package com.polar.bear.api.mappers;

import com.polar.bear.api.models.UserInfoDto;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserInfoMapper {

	UserInfoDto selectUserInfoById(@Param("userId") String userId) throws Exception;

	List<Map<String, Object>> selectAdminUserList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") String registeredFrom, @Param("registeredTo") String registeredTo,
			@Param("grades") List<String> grades, @Param("statuses") List<String> statuses) throws Exception;

	Map<String, Object> selectAdminUserDetail(@Param("userNo") Integer userNo) throws Exception;
	int updateAdminUser(@Param("userNo") Integer userNo, @Param("userPhone") String userPhone,
			@Param("userGrade") String userGrade, @Param("userStatus") String userStatus,
			@Param("updtId") String updtId) throws Exception;

	int insertUserInfo(UserInfoDto userInfoDto) throws Exception;

	int updateUserInfo(UserInfoDto userInfoDto) throws Exception;
}
