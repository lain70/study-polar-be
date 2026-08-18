package com.polar.bear.api.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.polar.bear.api.mappers.LoginHistoryMapper;
import com.polar.bear.api.models.LoginHistoryDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

	private static final Pattern EDGE_PATTERN = Pattern.compile("Edg/([0-9.]+)");
	private static final Pattern CHROME_PATTERN = Pattern.compile("(?:Chrome|CriOS)/([0-9.]+)");
	private static final Pattern FIREFOX_PATTERN = Pattern.compile("(?:Firefox|FxiOS)/([0-9.]+)");
	private static final Pattern SAFARI_PATTERN = Pattern.compile("Version/([0-9.]+).+Safari/");
	private static final Pattern APP_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9._-]*)/([0-9][A-Za-z0-9._-]*)");

	private final LoginHistoryMapper loginHistoryMapper;

	public void saveUserLoginHistory(Integer userNo, HttpServletRequest request) throws Exception {
		LoginHistoryDto loginHistoryDto = createLoginHistory(request);
		loginHistoryDto.setUserNo(userNo);
		loginHistoryMapper.insertUserLoginHistory(loginHistoryDto);
	}

	public void saveAdminLoginHistory(Integer adminNo, HttpServletRequest request) throws Exception {
		LoginHistoryDto loginHistoryDto = createLoginHistory(request);
		loginHistoryDto.setAdminNo(adminNo);
		loginHistoryMapper.insertAdminLoginHistory(loginHistoryDto);
	}

	private LoginHistoryDto createLoginHistory(HttpServletRequest request) {
		String userAgent = StringUtils.defaultString(request.getHeader("User-Agent"));
		LoginHistoryDto loginHistoryDto = new LoginHistoryDto();
		loginHistoryDto.setIpAddress(getClientIp(request));
		loginHistoryDto.setAccessInfo(StringUtils.left(userAgent, 1000));
		loginHistoryDto.setOsName(StringUtils.left(resolveOs(userAgent), 100));

		String[] appInfo = resolveApp(userAgent);
		loginHistoryDto.setAppName(StringUtils.left(appInfo[0], 100));
		loginHistoryDto.setAppVersion(StringUtils.left(appInfo[1], 50));
		return loginHistoryDto;
	}

	private String getClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotBlank(forwardedFor)) {
			String[] forwardedAddresses = forwardedFor.split(",");
			return StringUtils.left(StringUtils.trim(forwardedAddresses[forwardedAddresses.length - 1]), 45);
		}
		return StringUtils.left(request.getRemoteAddr(), 45);
	}

	private String resolveOs(String userAgent) {
		if (StringUtils.containsIgnoreCase(userAgent, "Windows NT 10.0")) {
			return "Windows 10/11";
		}
		if (StringUtils.containsIgnoreCase(userAgent, "Android")) {
			return "Android";
		}
		if (StringUtils.containsIgnoreCase(userAgent, "iPhone") || StringUtils.containsIgnoreCase(userAgent, "iPad")) {
			return "iOS/iPadOS";
		}
		if (StringUtils.containsIgnoreCase(userAgent, "Mac OS X")) {
			return "macOS";
		}
		if (StringUtils.containsIgnoreCase(userAgent, "Linux")) {
			return "Linux";
		}
		return "UNKNOWN";
	}

	private String[] resolveApp(String userAgent) {
		String[] appInfo = matchApp(userAgent, EDGE_PATTERN, "Edge");
		if (appInfo != null) {
			return appInfo;
		}

		appInfo = matchApp(userAgent, CHROME_PATTERN, "Chrome");
		if (appInfo != null) {
			return appInfo;
		}

		appInfo = matchApp(userAgent, FIREFOX_PATTERN, "Firefox");
		if (appInfo != null) {
			return appInfo;
		}

		appInfo = matchApp(userAgent, SAFARI_PATTERN, "Safari");
		if (appInfo != null) {
			return appInfo;
		}

		Matcher matcher = APP_PATTERN.matcher(userAgent);
		if (matcher.find()) {
			return new String[] { matcher.group(1), matcher.group(2) };
		}
		return new String[] { "UNKNOWN", "" };
	}

	private String[] matchApp(String userAgent, Pattern pattern, String appName) {
		Matcher matcher = pattern.matcher(userAgent);
		if (matcher.find()) {
			return new String[] { appName, matcher.group(1) };
		}
		return null;
	}
}
