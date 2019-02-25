package com.studentsManager.filters;

import com.studentsManager.util.Check;
import com.studentsManager.util.GetConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class IsLoginFilter implements Filter {

	//复写filter方法
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
//		//获取请求和相应
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		//获取session
		HttpSession session = req.getSession(false);
		//获取context
		ServletContext context = req.getServletContext();

		//重定向地址
		String path = req.getContextPath() + "/login.html";

		String awayWithReq = req.getContextPath() + "/logup.html";

		if(session != null){
			Check check = (Check) session.getAttribute("userCheck");
			if(check != null){
				//过滤器放行
				chain.doFilter(req,resp);
			}
		}

		//排除请求
		List<String> excludedReqs = (List<String>) context.getAttribute("excludedReqs");
		//当前请求
		String currentReq = req.getRequestURI();
		currentReq = currentReq.substring(currentReq.lastIndexOf("/"));
		for(String el:excludedReqs){
			if(currentReq.equals(el)){
				chain.doFilter(req,resp);
			}
		}


		//重定向到该路径
		resp.sendRedirect(path);
	}
	
}
