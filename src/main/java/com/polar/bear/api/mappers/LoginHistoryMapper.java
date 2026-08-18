package com.polar.bear.api.mappers;

import com.polar.bear.api.models.LoginHistoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginHistoryMapper {

	int insertUserLoginHistory(LoginHistoryDto loginHistoryDto) throws Exception;

	int insertAdminLoginHistory(LoginHistoryDto loginHistoryDto) throws Exception;
}
