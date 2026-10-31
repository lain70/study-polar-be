package com.polar.bear.api.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.models.UserPasswordConfirmRequestVo;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

class UserProfileControllerTest {
	private final UserInfoService users = mock(UserInfoService.class);
	private final JwtUtil jwt = mock(JwtUtil.class);
	private final UserInfoController controller = new UserInfoController(users, jwt);

	private UserInfoDto authenticatedUser() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-key");
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(jwt.validateAccessToken("test-token")).thenReturn(login);
		when(login.getUserId()).thenReturn("member");
		UserInfoDto user = new UserInfoDto();
		user.setUserId("member");
		user.setUserName("Member");
		user.setUserPwd("not-for-response");
		when(users.selectUserInfoById("member")).thenReturn(user);
		return user;
	}

	@Test
	void ownProfileReturnsOnlyDisplayFieldsAndCannotBeCached() throws Exception {
		authenticatedUser();
		ResponseEntity<?> response = controller.getUserInfo("test-token", "test-key");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
		assertThat((Map<?, ?>) response.getBody()).hasSize(3);
		assertThat(((Map<?, ?>) response.getBody()).containsKey("userPwd")).isFalse();
		assertThat(((Map<?, ?>) response.getBody()).containsKey("userNo")).isFalse();
		assertThat(((Map<?, ?>) response.getBody()).get("userId")).isEqualTo("member");
	}

	@Test
	void badServiceKeyDoesNotResolveIdentity() {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-key");
		assertThat(controller.getUserInfo("test-token", "invalid").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		verifyNoInteractions(jwt, users);
	}

	@Test
	void deletedMemberCannotGetAProfile() throws Exception {
		authenticatedUser();
		when(users.selectUserInfoById("member")).thenReturn(null);
		assertThat(controller.getUserInfo("test-token", "test-key").getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void incorrectConfirmationPasswordDoesNotReturnEditableProfile() throws Exception {
		UserInfoDto user = authenticatedUser();
		when(users.matchesPassword("incorrect", user)).thenReturn(false);
		UserPasswordConfirmRequestVo request = new UserPasswordConfirmRequestVo();
		request.setUserPwd("incorrect");
		assertThat(controller.confirmPassword("test-token", "test-key", request).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
}
