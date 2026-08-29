package com.polar.bear.api.mappers;

import java.util.List;

import com.polar.bear.api.models.EventInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventInfoMapper {
	List<EventInfoDto> selectCustomerEvents() throws Exception;
	List<EventInfoDto> selectAdminEvents() throws Exception;
	EventInfoDto selectEvent(@Param("eventNo") Long eventNo) throws Exception;
	List<Long> selectEventGoodsNos(@Param("eventNo") Long eventNo) throws Exception;
	int insertEvent(EventInfoDto event) throws Exception;
	int updateEvent(EventInfoDto event) throws Exception;
	int deleteEventGoods(@Param("eventNo") Long eventNo) throws Exception;
	int insertEventGoods(@Param("eventNo") Long eventNo, @Param("goodsNo") Long goodsNo,
			@Param("regId") String regId) throws Exception;
	int deleteEventImages(@Param("eventNo") Long eventNo) throws Exception;
	int insertEventImage(@Param("eventNo") Long eventNo, @Param("imageUrl") String imageUrl,
			@Param("regId") String regId) throws Exception;
}
