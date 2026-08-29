package com.polar.bear.api.service;

import java.util.List;

import com.polar.bear.api.mappers.AdminMenuMapper;
import com.polar.bear.api.models.AdminMenuDto;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMenuService {
	private final AdminMenuMapper adminMenuMapper;

	public List<AdminMenuDto> selectActiveMenus() throws Exception {
		return adminMenuMapper.selectActiveMenus();
	}
}
