package com.polar.bear.api.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginHistoryDto implements Serializable {

	private static final long serialVersionUID = 4419681250962736126L;

	private Integer userNo;
	private Integer adminNo;
	private String ipAddress;
	private String accessInfo;
	private String osName;
	private String appName;
	private String appVersion;
}
