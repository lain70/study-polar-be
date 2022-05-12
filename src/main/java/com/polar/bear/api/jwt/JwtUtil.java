package com.polar.bear.api.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import com.polar.bear.api.exception.ExpiredTokenException;
import com.polar.bear.api.exception.NotAuthenticationException;
import com.polar.bear.api.exception.WrongTokenException;
import com.polar.bear.api.redis.LoginRedisRepository;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.utils.Aes256Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil implements Serializable {

	private static final long serialVersionUID = -987339296810551655L;
	
	@Autowired
	private LoginRedisRepository loginRedisRepository;
	
	private static final String secretKey = "polar_bear_shop_secretKey_auth";
	
    public static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60; // 1 hours
    public static final long ACCESS_TOKEN_VALIDITY = 1 * 60 * 60; // 1 hours
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60; // 1weeks

    public <T> T getClaimFromJwtToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromJwtToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the jwt.getsecretKey() key
    private Claims getAllClaimsFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromJwtToken(String token) {
        return getClaimFromJwtToken(token, Claims::getExpiration);
    }

    //check if the token has expired
    private Boolean isJwtTokenExpired(String token) {
        final Date expiration = getExpirationDateFromJwtToken(token);
        return expiration.before(new Date());
    }
    
    //token 생성    
    public String generateJWTToken(String csrKey, int csrNo, String accessToken, String refreshToken) {
        Date expiration = new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000);

        Claims claims = Jwts.claims().setSubject(csrKey);
        claims.put("no", csrNo);
        claims.put("csrKey", csrKey);        
        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);
        claims.put("accessTime", System.currentTimeMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(csrKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private TokenVo getTokenVoFromToken(String token) {
        try{

            String base64Dec = new String(Base64.decodeBase64(token.getBytes("UTF-8")), "UTF-8");
            String aesDecBase64Dec = Aes256Util.getDecrypt(base64Dec);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.reader().forType(TokenVo.class).readValue(aesDecBase64Dec);

        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    //retrieve expiration date from token
    public Date getExpirationDateFromToken(String token) {
        TokenVo tokenVo = this.getTokenVoFromToken(token);
        if(tokenVo != null){
            return new Date(tokenVo.getExpiration());
        } else {
            return null;
        }
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //check if the access token
    private Boolean isAccessToken(String token) {
        TokenVo tokenVo = this.getTokenVoFromToken(token);
        if(tokenVo != null){
            return "ACCESS".equals(tokenVo.getType());
        } else {
            return null;
        }
    }

    //validate access token
    public LoginRedisVo validateAccessToken(String token) throws ExpiredTokenException, NotAuthenticationException, WrongTokenException {
        TokenVo tokenVo = this.getTokenVoFromToken(token);

        if(tokenVo == null) {
            throw new WrongTokenException();
        }

        if(this.isTokenExpired(token)) {
            throw new ExpiredTokenException();
        }

        if(!this.isAccessToken(token)) {
            throw new WrongTokenException();
        }

        LoginRedisVo loginRedisVo = this.loginRedisRepository.findById(tokenVo.getCsrKey());

        if(loginRedisVo != null && tokenVo.getCreation() < loginRedisVo.getLastUpdateTime()) {
            throw new ExpiredTokenException();
        }

        if(loginRedisVo != null
                && !StringUtils.isEmpty(loginRedisVo.getCsrId())
                && !StringUtils.isEmpty(tokenVo.getCsrId())
                && loginRedisVo.getCsrId().equals(tokenVo.getCsrId())
        ) {
            return loginRedisVo;
        } else {
            this.loginRedisRepository.delete(tokenVo.getCsrKey());
            throw new NotAuthenticationException();
        }
    }

    //validate refresh token
    public LoginRedisVo validationRefreshToken(String token) throws ExpiredTokenException, NotAuthenticationException, WrongTokenException {
        TokenVo tokenVo = this.getTokenVoFromToken(token);

        if(tokenVo == null) {
            throw new WrongTokenException();
        }

        if(this.isTokenExpired(token)) {
            this.loginRedisRepository.delete(tokenVo.getCsrKey());
            throw new ExpiredTokenException();
        }

        LoginRedisVo loginRedisVo = this.loginRedisRepository.findById(tokenVo.getCsrKey());

        if(loginRedisVo != null
                && !StringUtils.isEmpty(loginRedisVo.getCsrId())
                && !StringUtils.isEmpty(tokenVo.getCsrId())
                && loginRedisVo.getCsrId().equals(tokenVo.getCsrId())
        ) {
            return loginRedisVo;
        } else {
            this.loginRedisRepository.delete(tokenVo.getCsrKey());
            throw new NotAuthenticationException();
        }
    }

    //generate Access token
    public String generateToken(String csrKey, String csrId, String type) throws Exception {

        Date creation = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY * 1000);
        System.out.println(expiration);

        if (type.equals("REFRESH")) {
        	expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY * 1000);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String tokenAsString = objectMapper.writeValueAsString(new TokenVo(type, creation.getTime(), expiration.getTime(), csrKey, csrId)); // ��ūvo �ð� ����
        String aesTokenAsString = Aes256Util.getEncrypt(tokenAsString);

        return new String(Base64.encodeBase64(aesTokenAsString.getBytes("UTF-8")));
    }

    // get Access token Time
    public long getAccessTokenTime(String token) {
        TokenVo tokenVo = this.getTokenVoFromToken(token);
        return tokenVo.getCreation();
    }

}
