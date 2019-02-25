package com.studentsManager.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;
import com.studentsManager.util.Check;
import com.studentsManager.util.UserUtil;

public class LoginServlet extends HttpServlet {

	/**
	 * 用户登录服务
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// 获取session
		HttpSession session = req.getSession();

		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");
		// 设置所在数据表
		db.setDataTable("users");

		// 获取用户名和密码
		String username = req.getParameter("username");
		String password = req.getParameter("password");

        //创建Cookie
        Cookie cookie = new Cookie("username",username);
        //设置cookie有效期为半小时
        cookie.setMaxAge(30 * 60);

        //数据库ORM user对象
		User user = (User) UserUtil.ORMUserFromDb(db, "username", username);


		// 返回
		try {
			if (user != null) {
				if (user.getPassword().equals(password)) {
					Check ce = new Check(db,user);
					//登录成功
                    //将得到的数据存入session
					session.setAttribute("userCheck", ce);
				    //添加Cookie
					resp.addCookie(cookie);
					resp.sendRedirect(req.getContextPath() + "/userInfo.html");
					// resp.getWriter().print("<h1>欢迎你 "+username+"</h1>");
				} else {
					resp.getWriter().print("<h1>密码错误，登录失败</h1>");
				}
			} else {
				resp.getWriter().print("<h1>没有账户，请注册</h1>");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
