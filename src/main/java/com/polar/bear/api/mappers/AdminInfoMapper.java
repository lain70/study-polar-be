package com.polar.bear.api.mappers;

import com.polar.bear.api.models.AdminInfoDto;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminInfoMapper {

	AdminInfoDto selectAdminInfoById(@Param("adminId") String adminId) throws Exception;

	List<Map<String, Object>> selectAdminList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") String registeredFrom, @Param("registeredTo") String registeredTo,
			@Param("departments") List<String> departments, @Param("positions") List<String> positions,
			@Param("statuses") List<String> statuses, @Param("useYns") List<String> useYns) throws Exception;

	Map<String, Object> selectAdminDetail(@Param("adminNo") Integer adminNo) throws Exception;
	int updateAdmin(@Param("adminNo") Integer adminNo, @Param("adminPhone") String adminPhone,
			@Param("adminDepartment") String adminDepartment, @Param("adminPosition") String adminPosition,
			@Param("adminStatus") String adminStatus, @Param("useYn") String useYn,
			@Param("updtId") String updtId) throws Exception;
}
