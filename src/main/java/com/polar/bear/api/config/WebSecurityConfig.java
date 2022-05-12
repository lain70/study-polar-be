package com.polar.bear.api.config;

import com.polar.bear.api.jwt.CustomAccessDeniedHandler;
import com.polar.bear.api.jwt.JwtAuthenticationFilter;
import com.polar.bear.api.jwt.JwtProperty;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.redis.LoginRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(JwtProperty.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtProperty jwtProperty;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private LoginRedisRepository loginRedisRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable()
		.cors()
		.and()
		.csrf().disable()
		.formLogin().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
			.antMatchers("/api/qna/**").permitAll()
			.antMatchers("/api/auth/login").permitAll()
			.antMatchers("/api/csr/**").hasAnyRole("CSR")
		.and()
			.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
		.and()
			.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtUtil, loginRedisRepository), BasicAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
		.antMatchers(HttpMethod.POST,"/api/auth/**")
		.antMatchers(HttpMethod.GET,"/api/qna/**")
		.antMatchers(HttpMethod.POST,"/api/qna/**")				
		.antMatchers("/view/**")
        .antMatchers("/resources/**")
        .antMatchers("/published/**");
		
		web.httpFirewall(defaultHttpFirewall());
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
	
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
}
