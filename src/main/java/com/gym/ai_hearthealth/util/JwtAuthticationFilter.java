package com.gym.ai_hearthealth.util;

import com.gym.ai_hearthealth.common.ResultCode;
import com.gym.ai_hearthealth.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthticationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String requestUri = request.getRequestURI();
        //检查是否为公共路径
        return SecurityConfig.isPublicPATH(requestUri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //获取请求的URL和方法
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("Request URI: " + requestUri + ", Method: " + method);

        //获取请求头中的JWT token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if (StringUtils.hasText(token)){

        }else {
            //清理上下文
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        //继续过滤器链
        chain.doFilter(request, response);

    }

    //清理Spring Security上下文
    private void clearSecurityContext() {
        // 实现清理Spring Security上下文的逻辑
        SecurityContextHolder.clearContext();

    }
}
