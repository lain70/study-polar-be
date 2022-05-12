package com.polar.bear.api.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polar.bear.api.exception.ExpiredTokenException;
import com.polar.bear.api.exception.NotAuthenticationException;
import com.polar.bear.api.exception.WrongTokenException;
import com.polar.bear.api.redis.LoginRedisRepository;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private JwtUtil jwtUtil;

	private LoginRedisRepository loginRedisRepository;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			LoginRedisRepository loginRedisRepository) {
		super(authenticationManager);
		this.jwtUtil = jwtUtil;
		this.loginRedisRepository = loginRedisRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = request.getHeader("Authorization");

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null || !auth.isAuthenticated()) {

				if (token != null) {
					LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(token);
					UsernamePasswordAuthenticationToken authToken = null;

					if (loginRedisVo != null) {
						List<GrantedAuthority> roles = new ArrayList<>();
						roles.add(new SimpleGrantedAuthority("ROLE_" + loginRedisVo.getType()));
						authToken = new UsernamePasswordAuthenticationToken(loginRedisVo.getCsrKey(), token, roles);
						authToken.setDetails(Optional.of(loginRedisVo));
					}
					auth = authToken;
					
					if(auth == null) {
						throw new NotAuthenticationException();
					}
					
					SecurityContextHolder.getContext().setAuthentication(auth);
				}else {
					throw new NotAuthenticationException();
				}
			}
			
			chain.doFilter(request, response);
		} catch (ExpiredJwtException | ExpiredTokenException e) {
            ResponseUtil.responseMessage(response, HttpStatus.UNAUTHORIZED, "EXPIRED", "토큰이 만료되었습니다.");
        } catch (WrongTokenException e) {
            ResponseUtil.responseMessage(response, HttpStatus.NOT_ACCEPTABLE, "NOT_ACCESS_TOKEN", "ACCESS TOKEN 유효하지 않습니다.");
        } catch (SignatureException e) {
        	ResponseUtil.responseMessage(response, HttpStatus.NOT_ACCEPTABLE, "NOT_VALID", "토큰이 유효하지 않습니다.");            
        } catch (NotAuthenticationException e) {
        	ResponseUtil.responseMessage(response, HttpStatus.UNAUTHORIZED, "DENY", "인증된 사용자가 아닙니다.");
        } catch (Exception e){
            e.printStackTrace();
            ResponseUtil.responseMessage(response, HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR", "시스템 오류가 발생하였습니다.");
        }
	}

}
