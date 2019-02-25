package com.studentsManager.filters;

import com.studentsManager.dao.DbPool;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GeneralityFilter implements Filter {


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 获取请求和相应
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 获取访问者的IP地址
		String remoteAddr = req.getRemoteAddr();

		// 获取要访问的页面
		String toUrl = req.getRequestURL().toString();

		// 获取当前访问时间
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String time = format.format(date);

		// 控制台打印
		System.out.println("IP地址为“" + remoteAddr + "”在 " + time + " 时访问了 " + toUrl);
	
	
		//解决中文问题
		
		req.setCharacterEncoding("UTF-8");
		//req.setCharacterEncoding("UTF-8");

		//获取ServletContext
		ServletContext sc = request.getServletContext();
		Map<String,String> mimeMap = (Map<String, String>) sc.getAttribute("mimeMap");
		String URI = req.getServletPath();
		int isSuffix = URI.indexOf(".");
		if(isSuffix != -1){
			String suffix = URI.substring(URI.indexOf(".")+1,URI.length());
			//通过映射表找到Mime类型，然后进行操作
			String mimeType = mimeMap.get(suffix);
			System.out.println(mimeType+";charset=UTF-8");
			resp.setHeader("Content-Typ",mimeType+";charset=UTF-8");
		}else{
			System.out.println("该请求是AJAX异步请求设置编码格式是“application/json;charset=UTF-8”");
			resp.setContentType("application/json;charset=UTF-8");
		}

		//分配数据库连接
        if(isSuffix == -1){
            // 获取数据库连接池
            DbPool dbs = (DbPool) sc.getAttribute("DbConns");
            // 获取数据库的连接类
            request.setAttribute("db",dbs.getDb());
            System.out.println("借到db对象，连接池中还有"+dbs.length()+"条连接");
        }

		//过滤器放行
		chain.doFilter(req, resp);
	}

}
