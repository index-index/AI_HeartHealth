package com.gym.ai_hearthealth.util;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gym.ai_hearthealth.DTO.response.UserLoginResponseDTO;
import com.gym.ai_hearthealth.common.ResultCode;
import com.gym.ai_hearthealth.config.SecurityConfig;
import com.gym.ai_hearthealth.entity.User;
import com.gym.ai_hearthealth.enumClass.UserStatus;
import com.gym.ai_hearthealth.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthticationFilter extends OncePerRequestFilter {
    @Resource
    private UserService userService;

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
            try {
            //2.验证token并获取用户信息
            JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
            if(validationResult != null && validationResult.isValid()){
                //3.查询用户信息验证用户状态
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(validationResult.getUserId());
                System.out.println(JSONUtil.parseObj(user));
                if (user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())){
                    //4. 创建Spring Security认证对象
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                    );

                    //创建UsernamePasswordAuthenticationToken对象
                    UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(),
                            null,
                            authorities
                    );

                    //设置认证信息到Spring Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authcation);

                    //将token存储到请求属性中
                    request.setAttribute("jwtToken", token);
                }else {
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }
            }else{
                //清理上下文
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }
            } catch (JWTVerificationException e) {
                //捕获token验证异常（过期、签名错误等），返回错误响应而非让异常传播
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }
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
