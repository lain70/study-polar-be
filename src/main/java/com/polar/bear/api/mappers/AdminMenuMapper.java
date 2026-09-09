package com.polar.bear.api.mappers;

import java.util.List;

import com.polar.bear.api.models.AdminMenuDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMenuMapper {
	List<AdminMenuDto> selectActiveMenus() throws Exception;
}
