package com.gym.ai_hearthealth.config;


import cn.hutool.core.text.AntPathMatcher;
import com.gym.ai_hearthealth.util.JwtAuthticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/error",
            "/api/test",
            "/api/user/login",
            "/api/user/add"
    };

    public static Boolean isPublicPATH(String requestUri){
        for (String publicPath : PUBLIC_PATHS) {
            if (antPathMatcher.match(publicPath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    @Bean
    public JwtAuthticationFilter JwtAuthticationFilter(){
        return new JwtAuthticationFilter();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthticationFilter> jwtFilterRegistration(JwtAuthticationFilter filter) {
        FilterRegistrationBean<JwtAuthticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //禁用CSRF保护（API服务通常不需要）
                .csrf(AbstractHttpConfigurer::disable)
                //配置会话管理为无状态(JWT需要)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //配置请求的授权规则
                .authorizeHttpRequests(auth -> auth
                        //公开的路径，无需登录即可访问
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // 其他请求都需要认证
                        .anyRequest().authenticated()
                )
                //添加JWT认证过滤器
                .addFilterBefore(JwtAuthticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
