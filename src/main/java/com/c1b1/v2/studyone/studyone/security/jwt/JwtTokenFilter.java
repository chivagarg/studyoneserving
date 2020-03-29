package com.c1b1.v2.studyone.studyone.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        // Fetch token from the inbound request
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = (token != null) ? jwtTokenProvider.getAuthentication(token) : null;
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Note that the ExceptionHandler will only work if the request is handled by the DispatcherServlet
        // However this exception occurs before that as it is thrown by a Filter. So we will never be able to
        // handle this exception with an (@)ExceptionHandler
        catch (InvalidJwtAuthenticationException e) {
            HttpServletResponse response=(HttpServletResponse) res;
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(req, res);
    }
}
