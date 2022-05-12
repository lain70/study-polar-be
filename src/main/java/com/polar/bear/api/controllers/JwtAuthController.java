package com.polar.bear.api.controllers;

import java.util.HashMap;
import java.util.Map;

import com.polar.bear.api.exception.ExpiredTokenException;
import com.polar.bear.api.exception.NotAuthenticationException;
import com.polar.bear.api.exception.WrongTokenException;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.jwt.LoginRequestVo;
import com.polar.bear.api.jwt.LoginResponseVo;
import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.redis.LoginRedisRepository;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.CsrInfoService;
import com.polar.bear.api.utils.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@RequestMapping(
			value="/login",
			method = RequestMethod.POST,
            consumes="application/json",
            produces="application/json"
	)
	public ResponseEntity<?> createAuthToken(
			@RequestHeader(value = "x-auth-user-service-key") String serviceKey,
			@RequestBody LoginRequestVo loginRequestVo
			) throws Exception{
		String csrKey = null;
		Integer csrNo = null;
		String csrId = null;
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
        CsrInfoDto csrInfoDto = csrInfoService.selectCsrInfoDetailById(loginRequestVo.getCsrId());
        
        if(csrInfoDto == null) {
        	return ResponseUtil.getResponseEntity("ID/비번이 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);
			
        }
        
        csrKey = String.valueOf(csrInfoDto.getCsrNo());
		csrNo = csrInfoDto.getCsrNo();
    	csrId = csrInfoDto.getCsrId();
        
        if(!passwordEncoder.matches(loginRequestVo.getCsrPwd(), csrInfoDto.getCsrPwd())){        	
        	if(csrInfoDto.getLoginFailCnt() >= 4) {
            	return ResponseUtil.getResponseEntity("비밀번호가 5회 이상 틀렸습니다. 관리", headers, HttpStatus.UNAUTHORIZED);
            }else {
            	csrInfoService.updateCsrLoginFailCnt(csrId, csrInfoDto.getLoginFailCnt() + 1);
            	return ResponseUtil.getResponseEntity("ID/비번이 올바르지 않습니다.", headers, HttpStatus.UNAUTHORIZED);	
            }
        }
        
        loginRedisVo = this.loginRedisRepository.findById(csrKey);
        if(loginRedisVo != null) {
        	String preAccessToken = loginRedisVo.getAccessToken();
        	
        	if(this.jwtUtil.isTokenExpired(preAccessToken)) {
        		loginRedisVo.refreshInfo(csrInfoDto);
        		accessToken = this.jwtUtil.generateToken(csrKey, csrId, "ACCESS");
        	} else {
                accessToken = preAccessToken;
            }

            refreshToken = this.jwtUtil.generateToken(csrKey, csrId, "REFRESH");
        }else {
        	accessToken = this.jwtUtil.generateToken(csrKey, csrId, "ACCESS");
        	refreshToken = this.jwtUtil.generateToken(csrKey, csrId, "REFRESH");
        }
        
        if(accessToken == null || refreshToken == null) {
        	return ResponseUtil.getResponseEntity("인증된 사용자가 아닙니다.", headers, HttpStatus.UNAUTHORIZED);
        }else {
        	long accessTokenTime = this.jwtUtil.getAccessTokenTime(accessToken);
        	loginRedisVo = new LoginRedisVo(csrKey, csrId, "CSR", csrInfoDto, accessToken, refreshToken, accessTokenTime);
        	this.loginRedisRepository.save(loginRedisVo);
        }
        
        csrInfoService.updateCsrLastLoginDate(csrId);
        
        return ResponseEntity.ok(new LoginResponseVo(csrNo, csrId, accessToken, refreshToken));
        
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
    	CsrInfoDto csrInfoDto = null;
		Integer csrNo = null;
		String csrId = null;
        String accessToken = null;
        
        LoginRedisVo loginRedisVo = this.jwtUtil.validationRefreshToken(refreshToken);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try {


            if (loginRedisVo != null) {
            	csrInfoDto = this.csrInfoService.selectCsrInfoDetailById(loginRedisVo.getCsrId());

                String preAccessToken = loginRedisVo.getAccessToken();
                if(this.jwtUtil.isTokenExpired(preAccessToken)) {
                    accessToken = this.jwtUtil.generateToken(loginRedisVo.getCsrKey(), loginRedisVo.getCsrId(), "ACCESS");
                } else {
                    accessToken = preAccessToken;
                }
                refreshToken = this.jwtUtil.generateToken(loginRedisVo.getCsrKey(), loginRedisVo.getCsrId(), "REFRESH");

                long accessTokenTime = this.jwtUtil.getAccessTokenTime(accessToken);
                loginRedisVo = new LoginRedisVo(loginRedisVo.getCsrKey(), "CSR", loginRedisVo.getCsrId(), csrInfoDto, accessToken, refreshToken, accessTokenTime);                
                this.loginRedisRepository.save(loginRedisVo);

                return ResponseEntity.ok(new LoginResponseVo(csrNo, csrId, accessToken, refreshToken));
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
            consumes="application/json",
            produces="application/json"
    )
    public ResponseEntity<?> csrLogout(
            @RequestHeader(value = "Authorization") String token,
            @RequestHeader(value = "x-auth-user-service-key") String serviceKey
    ) throws Exception {
    	
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Type", "application/json; charset=UTF-8");
        try {
			if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(token)) {
				return ResponseUtil.getResponseEntity("필수 헤더 값이 없습니다.", headers, HttpStatus.BAD_REQUEST);
			}
			
            LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);

            if (loginRedisVo != null) {
                if(loginRedisVo.getCsrKey() != null) {
                    this.loginRedisRepository.delete(loginRedisVo.getCsrKey());
                }
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
