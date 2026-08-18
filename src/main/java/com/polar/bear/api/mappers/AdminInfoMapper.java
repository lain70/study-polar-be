package com.polar.bear.api.mappers;

import com.polar.bear.api.models.AdminInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminInfoMapper {

	AdminInfoDto selectAdminInfoById(@Param("adminId") String adminId) throws Exception;
}
