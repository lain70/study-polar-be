package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polar.bear.api.exception.ExpiredTokenException;
import com.polar.bear.api.exception.NotAuthenticationException;
import com.polar.bear.api.exception.WrongTokenException;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.jwt.LoginRequestVo;
import com.polar.bear.api.jwt.LoginResponseVo;
import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.redis.LoginRedisRepository;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.CsrInfoService;
import com.polar.bear.api.service.LoginHistoryService;
import com.polar.bear.api.service.UserInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class JwtAuthController {
	private static final String USER_KEY_PREFIX = "USER:";
	
	@Value("${service-key}")
    private String originServiceKey;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private LoginRedisRepository loginRedisRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CsrInfoService csrInfoService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private LoginHistoryService loginHistoryService;
	
	@RequestMapping(
			value="/login",
			method = RequestMethod.POST,
            consumes="application/json",
            produces="application/json"
	)
	public ResponseEntity<?> createAuthToken(
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody LoginRequestVo loginRequestVo,
			HttpServletRequest request
			) throws Exception{
		String userKey = null;
		Integer userNo = null;
		String userId = null;
		String accessToken = null;
        String refreshToken = null;
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        
        if(StringUtils.isBlank(serviceKey)) {
        	return ResponseUtil.getResponseEntity("필수 헤더 정보가 없습니다.", headers, HttpStatus.BAD_REQUEST);
        }
        
        if(!originServiceKey.equals(serviceKey)) {
        	return ResponseUtil.getResponseEntity("서비스키가 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
        }
        
        LoginRedisVo loginRedisVo = null;
        if (loginRequestVo == null || StringUtils.isBlank(loginRequestVo.getUserId())
                || StringUtils.isBlank(loginRequestVo.getUserPwd())) {
            return ResponseUtil.getResponseEntity("아이디와 비밀번호를 입력해 주세요.", headers, HttpStatus.BAD_REQUEST);
        }

        UserInfoDto userInfoDto = userInfoService.selectUserInfoById(loginRequestVo.getUserId());
        
        if(userInfoDto == null) {
        	return ResponseUtil.getResponseEntity("ID/비번이 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);
			
        }
        
        userKey = USER_KEY_PREFIX + userInfoDto.getUserNo();
		userNo = userInfoDto.getUserNo();
		userId = userInfoDto.getUserId();
        
        if(!passwordEncoder.matches(loginRequestVo.getUserPwd(), userInfoDto.getUserPwd())){
			return ResponseUtil.getResponseEntity("ID/비번이 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);
        }
        
        loginRedisVo = this.loginRedisRepository.findById(userKey);
        if(loginRedisVo != null) {
        	String preAccessToken = loginRedisVo.getAccessToken();
        	
        	if(this.jwtUtil.isTokenExpired(preAccessToken)) {
				loginRedisVo.refreshInfo(userInfoDto);
				accessToken = this.jwtUtil.generateToken(userKey, userId, "ACCESS");
        	} else {
                accessToken = preAccessToken;
            }

            refreshToken = this.jwtUtil.generateToken(userKey, userId, "REFRESH");
        }else {
			accessToken = this.jwtUtil.generateToken(userKey, userId, "ACCESS");
			refreshToken = this.jwtUtil.generateToken(userKey, userId, "REFRESH");
        }
        
        if(accessToken == null || refreshToken == null) {
        	return ResponseUtil.getResponseEntity("인증된 사용자가 아닙니다.", headers, HttpStatus.UNAUTHORIZED);
        }else {
        	long accessTokenTime = this.jwtUtil.getAccessTokenTime(accessToken);
			loginRedisVo = new LoginRedisVo(userKey, userId, "USER", userInfoDto, accessToken, refreshToken, accessTokenTime);
			loginHistoryService.saveUserLoginHistory(userNo, request);
        	this.loginRedisRepository.save(loginRedisVo);
        }
        
        return ResponseEntity.ok(new LoginResponseVo(userNo, userId, accessToken, refreshToken));
        
	}
		
    @RequestMapping(
            value = "/refresh",
            method = RequestMethod.GET,
            consumes="application/json",
            produces="application/json"
    )
    public ResponseEntity<?> refreshAccessToken(
            @RequestParam String refreshToken
    ) throws Exception {
		UserInfoDto userInfoDto = null;
		Integer userNo = null;
		String userId = null;
        String accessToken = null;
        
        LoginRedisVo loginRedisVo = this.jwtUtil.validationRefreshToken(refreshToken);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try {


            if (loginRedisVo != null) {
				userInfoDto = this.userInfoService.selectUserInfoById(loginRedisVo.getUserId());
				userNo = userInfoDto.getUserNo();
				userId = userInfoDto.getUserId();

                String preAccessToken = loginRedisVo.getAccessToken();
                if(this.jwtUtil.isTokenExpired(preAccessToken)) {
                    accessToken = this.jwtUtil.generateToken(loginRedisVo.getUserKey(), loginRedisVo.getUserId(), "ACCESS");
                } else {
                    accessToken = preAccessToken;
                }
                refreshToken = this.jwtUtil.generateToken(loginRedisVo.getUserKey(), loginRedisVo.getUserId(), "REFRESH");

                long accessTokenTime = this.jwtUtil.getAccessTokenTime(accessToken);
                loginRedisVo = new LoginRedisVo(loginRedisVo.getUserKey(), loginRedisVo.getUserId(), "USER", userInfoDto, accessToken, refreshToken, accessTokenTime);
                this.loginRedisRepository.save(loginRedisVo);

                return ResponseEntity.ok(new LoginResponseVo(userNo, userId, accessToken, refreshToken));
            } else {
            	return ResponseUtil.getResponseEntity("인증된 사용자가 아닙니다.", headers, HttpStatus.UNAUTHORIZED);
            }
            
        } catch (ExpiredJwtException e) {
        	return ResponseUtil.getResponseEntity("인증이 만료되었습니다.", headers, HttpStatus.FORBIDDEN);
        } catch (ExpiredTokenException e) {
        	return ResponseUtil.getResponseEntity("인증이 만료되었습니다.", headers, HttpStatus.FORBIDDEN);
        } catch (WrongTokenException e) {
        	return ResponseUtil.getResponseEntity("인증된 사용자가 아닙니다.", headers, HttpStatus.UNAUTHORIZED);
        } catch (NotAuthenticationException e) {
        	return ResponseUtil.getResponseEntity("인증된 사용자가 아닙니다.", headers, HttpStatus.UNAUTHORIZED);
        }
    }
	
    @RequestMapping(    		
            value = "/logout",
            method = RequestMethod.POST,
            produces="application/json"
    )
    public ResponseEntity<?> csrLogout(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "x-auth-user-service-key") String serviceKey,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws Exception {
    	
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Type", "application/json; charset=UTF-8");
        try {
			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
            LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);

            if (loginRedisVo != null) {
                if(loginRedisVo.getUserKey() != null) {
                    this.loginRedisRepository.delete(loginRedisVo.getUserKey());
                }
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                return ResponseUtil.getResponseEntity("로그 아웃 되었습니다.", headers, HttpStatus.OK);
            } else {
                return ResponseUtil.getResponseEntity("로그 아웃에 실패 하였습니다.", headers, HttpStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception e) {
        	return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);            
        }
    }
    
    // 테스트 용도
    @RequestMapping(value = "/save/csrinfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> saveQnaInfo(
			@RequestHeader(value = "x-auth-user-service-key", required = true) String serviceKey,
			@RequestBody CsrInfoDto csrInfoDto

	) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		try {

			if (StringUtils.isBlank(serviceKey)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.equals(originServiceKey, serviceKey)) {
				return ResponseUtil.getResponseEntity("헤더 값이 올바르지 않습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			if(csrInfoDto == null || StringUtils.isBlank(csrInfoDto.getCsrId())
					 || StringUtils.isBlank(csrInfoDto.getCsrPwd()) || StringUtils.isBlank(csrInfoDto.getCsrName())) {
				return ResponseUtil.getResponseEntity("필수 값이 업습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
			int result = csrInfoService.insertCsrInfo(csrInfoDto);

			if (result > 0 && csrInfoDto != null) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("csrNo", csrInfoDto.getCsrNo());
				headers.add("Status-Code", String.valueOf(HttpStatus.OK));
				return new ResponseEntity<Map>(resultMap, headers, HttpStatus.OK);
			} else {
				return ResponseUtil.getResponseEntity("저장된 상담사 정보가 없습니다.", headers, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("API(/qna/list) [GET] : " + e);
			return ResponseUtil.getResponseEntity("시스템 오류가 발생하였습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
