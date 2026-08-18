package com.polar.bear.api.service;

import com.polar.bear.api.mappers.AdminInfoMapper;
import com.polar.bear.api.models.AdminInfoDto;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInfoService {

	private final AdminInfoMapper adminInfoMapper;

	public AdminInfoDto selectAdminInfoById(String adminId) throws Exception {
		return adminInfoMapper.selectAdminInfoById(adminId);
	}
}
