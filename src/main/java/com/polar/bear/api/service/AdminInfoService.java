package com.polar.bear.api.service;

import com.polar.bear.api.mappers.AdminInfoMapper;
import com.polar.bear.api.models.AdminInfoDto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInfoService {

	private final AdminInfoMapper adminInfoMapper;

	public AdminInfoDto selectAdminInfoById(String adminId) throws Exception {
		return adminInfoMapper.selectAdminInfoById(adminId);
	}

	public List<Map<String, Object>> selectAdminList(String searchType, String keyword, String registeredFrom,
			String registeredTo, List<String> departments, List<String> positions, List<String> statuses,
			List<String> useYns) throws Exception {
		return adminInfoMapper.selectAdminList(searchType, keyword, registeredFrom, registeredTo, departments, positions,
				statuses, useYns);
	}

	public Map<String, Object> selectAdminDetail(Integer adminNo) throws Exception {
		return adminInfoMapper.selectAdminDetail(adminNo);
	}

	public int updateAdmin(Integer adminNo, Map<String, Object> values, String updtId) throws Exception {
		return adminInfoMapper.updateAdmin(adminNo, (String) values.get("adminPhone"),
				(String) values.get("adminDepartment"), (String) values.get("adminPosition"),
				(String) values.get("adminStatus"), (String) values.get("useYn"), updtId);
	}
}
