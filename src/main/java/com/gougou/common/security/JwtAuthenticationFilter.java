package com.gougou.common.security;

import cn.hutool.core.util.StrUtil;
import com.gougou.common.result.CheckResult;
import com.gougou.common.result.JwtConstant;
import com.gougou.entity.User;
import com.gougou.service.IUserService;
import com.gougou.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/12 9:10
 * <p>
 * 类说明
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    @Autowired
    private IUserService userService;
    @Autowired
    private MyUserDetialServiceImpl myUserDetialService;

    private static final String URL_WHITELIST[] ={
            "/login",
            "/logout",
            "/captcha",
            "/password",
            "/image/**"
    } ;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        String token=request.getHeader("token");
        System.out.println("请求url:"+request.getRequestURI());
// 如果token是空或者url在白名单里 则放行 让后面的springsecurity认证过滤器去认证
        if(StrUtil.isEmpty(token)|| new ArrayList<String>
                (Arrays.asList(URL_WHITELIST)).contains(request.getRequestURI())){
            chain.doFilter(request,response);
            return;
        }
        CheckResult checkResult = JwtUtils.validateJWT(token);
        if(!checkResult.isSuccess()){
            switch (checkResult.getErrCode()){
                case JwtConstant.JWT_ERRCODE_NULL: throw new JwtException("Token不存在");
                case JwtConstant.JWT_ERRCODE_FAIL: throw new JwtException("Token验证不通过");
                case JwtConstant.JWT_ERRCODE_EXPIRE: throw new JwtException("Token过期");
            }
        }
        Claims claims=JwtUtils.parseJWT(token);
        String username=claims.getSubject();
        User user=userService.getUserByName(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                =new UsernamePasswordAuthenticationToken(username,null,myUserDetialService.getUserAuthority(user.getUserId()));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request,response);
    }
}
