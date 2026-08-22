package com.polar.bear.api.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polar.bear.api.utils.LogTool;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HttpLoggingInterceptorTest {
	private final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new ObjectMapper());

	@Test
	void afterCompletionIgnoresNonCachingRequestWrapper() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("log", new LogTool());

		assertDoesNotThrow(() -> interceptor.afterCompletion(request, new MockHttpServletResponse(), null, null));
	}
}
