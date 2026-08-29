package com.polar.bear.api.service;

import java.util.Collections;
import java.util.List;

import com.polar.bear.api.mappers.EventInfoMapper;
import com.polar.bear.api.models.EventInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventInfoService {
	private final EventInfoMapper eventInfoMapper;

	public List<EventInfoDto> selectCustomerEvents() throws Exception {
		return eventInfoMapper.selectCustomerEvents();
	}

	public List<EventInfoDto> selectAdminEvents() throws Exception {
		return eventInfoMapper.selectAdminEvents();
	}

	public EventInfoDto selectEvent(Long eventNo) throws Exception {
		EventInfoDto event = eventInfoMapper.selectEvent(eventNo);
		if (event != null) {
			event.setGoodsNos(eventInfoMapper.selectEventGoodsNos(eventNo));
		}
		return event;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long insertEvent(EventInfoDto event) throws Exception {
		eventInfoMapper.insertEvent(event);
		replaceRelations(event);
		return event.getEventNo();
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateEvent(EventInfoDto event) throws Exception {
		int updated = eventInfoMapper.updateEvent(event);
		if (updated > 0) {
			replaceRelations(event);
		}
		return updated;
	}

	private void replaceRelations(EventInfoDto event) throws Exception {
		eventInfoMapper.deleteEventGoods(event.getEventNo());
		for (Long goodsNo : event.getGoodsNos() == null ? Collections.<Long>emptyList() : event.getGoodsNos()) {
			eventInfoMapper.insertEventGoods(event.getEventNo(), goodsNo, event.getUpdtId());
		}
		eventInfoMapper.deleteEventImages(event.getEventNo());
		if (event.getMainImageUrl() != null && !event.getMainImageUrl().trim().isEmpty()) {
			eventInfoMapper.insertEventImage(event.getEventNo(), event.getMainImageUrl().trim(), event.getUpdtId());
		}
	}
}
