package com.studentsManager.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JudgePowerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;

        req.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Typ","text/html;charset=UTF-8");

        filterChain.doFilter(req,resp);
    }
}
