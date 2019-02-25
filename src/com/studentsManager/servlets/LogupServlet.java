package com.studentsManager.servlets;

/**
 * 用户注册服务
 */

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.studentsManager.Listener.InitListener;
import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;
import com.studentsManager.util.UserUtil;

public class LogupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 设置返回类型
		resp.setContentType("text/html;charset=UTF-8");

		// 获取用户名和密码
		String name = req.getParameter("name");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String power = req.getParameter("userType");

		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");
		// 设置UserInfo的所在数据表
		db.setDataTable("users");

		// 创建user
		User user = new User(name, username, password, power);

		// 将user ORM到数据库
		boolean up = UserUtil.ORMObject(db, "username", user);
		if (up) {
			resp.getWriter().print("<h1>注册成功   " + name + "</h1>");
		} else {
			// resp.sendRedirect("");
			resp.getWriter().print("<h1>注册失败，该用户已被注册</h1>");
		}
	}
}
