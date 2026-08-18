package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.polar.bear.api.mappers.LoginHistoryMapper;
import com.polar.bear.api.models.LoginHistoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LoginHistoryServiceTest {

	@Mock
	private LoginHistoryMapper loginHistoryMapper;

	@Test
	void savesUserLoginHistoryWithForwardedIpAndBrowserInfo() throws Exception {
		LoginHistoryService loginHistoryService = new LoginHistoryService(loginHistoryMapper);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.199");
		request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/140.0.0.0 Safari/537.36");

		loginHistoryService.saveUserLoginHistory(7, request);

		ArgumentCaptor<LoginHistoryDto> captor = ArgumentCaptor.forClass(LoginHistoryDto.class);
		verify(loginHistoryMapper).insertUserLoginHistory(captor.capture());
		LoginHistoryDto loginHistoryDto = captor.getValue();
		assertThat(loginHistoryDto.getUserNo()).isEqualTo(7);
		assertThat(loginHistoryDto.getIpAddress()).isEqualTo("10.0.0.199");
		assertThat(loginHistoryDto.getOsName()).isEqualTo("macOS");
		assertThat(loginHistoryDto.getAppName()).isEqualTo("Chrome");
		assertThat(loginHistoryDto.getAppVersion()).isEqualTo("140.0.0.0");
	}

	@Test
	void savesAdminLoginHistoryWithCustomAppVersion() throws Exception {
		LoginHistoryService loginHistoryService = new LoginHistoryService(loginHistoryMapper);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRemoteAddr("198.51.100.20");
		request.addHeader("User-Agent", "PolarAdminApp/2.4.1 (Linux)");

		loginHistoryService.saveAdminLoginHistory(3, request);

		ArgumentCaptor<LoginHistoryDto> captor = ArgumentCaptor.forClass(LoginHistoryDto.class);
		verify(loginHistoryMapper).insertAdminLoginHistory(captor.capture());
		LoginHistoryDto loginHistoryDto = captor.getValue();
		assertThat(loginHistoryDto.getAdminNo()).isEqualTo(3);
		assertThat(loginHistoryDto.getIpAddress()).isEqualTo("198.51.100.20");
		assertThat(loginHistoryDto.getOsName()).isEqualTo("Linux");
		assertThat(loginHistoryDto.getAppName()).isEqualTo("PolarAdminApp");
		assertThat(loginHistoryDto.getAppVersion()).isEqualTo("2.4.1");
	}
}
