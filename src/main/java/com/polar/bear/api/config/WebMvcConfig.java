package com.polar.bear.api.config;

import com.polar.bear.api.interceptor.HttpLoggingInterceptor;
import com.polar.bear.api.interceptor.HttpRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	
    private final HttpRequestInterceptor httpRequestInterceptor;
    private final HttpLoggingInterceptor httpLoggingInterceptor;
    
    

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpRequestInterceptor)
        .order(0)
        .addPathPatterns("/**");
		registry.addInterceptor(httpLoggingInterceptor)
		.order(1)
		.excludePathPatterns("/api/auth/**")		
		.addPathPatterns("/**");		
	}



	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addViewController("/").setViewName("forward:/index.html");
	}

}
