package com.zl.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zl.entity.Token;
import com.zl.entity.User;
import com.zl.repository.TokenRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@RestController
public class AdminInterceptor implements  HandlerInterceptor {

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * 这个函数，如果返回false阻止，否则通行
     */

    @Override // 非登录注册 都需要header 里面有token
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        System.out.println(request.getRequestURL().toString());
//        if(request.getRequestURL().toString().contains("/register")||
//                request.getRequestURL().toString().contains("/login")||
//        request.getRequestURL().toString().contains("/test")||
//        request.getRequestURL().toString().contains("/HeadImage"))
//            return true;
//        TokenRepository tokenRepository = getMapper(TokenRepository.class,request);
//        String tokenStr = request.getHeader("token");
//        try {
//           Token token = tokenRepository.findTokensByTokenStr(tokenStr).get(0);
//        }catch(Exception e)
//        {
//            System.out.println("被拦截");
//            response.setStatus(400);
//            return false;
//        }
//        System.out.println("放行");
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws ServletException, IOException {

         System.out.println("执行了TestInterceptor的postHandle方法");
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws ServletException, IOException {
       System.out.println("执行了TestInterceptor的afterCompletion方法");
    }

    private <T> T getMapper(Class<T> clazz, HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(clazz);
    }




}