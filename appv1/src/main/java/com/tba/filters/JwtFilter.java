/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.filters;

import com.tba.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }
        List<String> noAuthRequiredPaths = Arrays.asList(
                "/",
                "/api/login",
                "/api/register",
                "/login",
                "/logout",
                "/access-denied"
        );

        if (requestURI.equals(contextPath + "/api/login") || requestURI.equals(contextPath + "/api/register")
                || !requestURI.startsWith(contextPath + "/api/secure/") 
                ) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }


        if (requestURI.startsWith(contextPath + "/api/")) {
            String header = httpRequest.getHeader("Authorization");


            if (header == null || !header.startsWith("Bearer ")) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing or invalid Authorization header.");
                return;
            } else {

                String token = header.substring(7);
                try {
                    String[] claims = JwtUtils.validateTokenAndGetUsername(token);
                    if (claims != null) {
                        String username = claims[0];
                        String role = claims[1];
                        System.out.println("Claims: username=" + username + ", role=" + role);
                        httpRequest.setAttribute("username", username);
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        chain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Token không hợp lệ hoặc hết hạn");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
