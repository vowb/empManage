package com.studentsManager.servlets;

import java.io.BufferedReader;

/**
 * 服务类
 */

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;
import com.studentsManager.util.Check;
import com.studentsManager.util.UserUtil;
public class QueryDataServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");
		//获取gson对象
		Gson gson = new Gson();
		//设置UserInfo的所在数据表
		db.setDataTable("users");

		//得到所有用户
		Set<User> users = UserUtil.getAllUsers(db);
		System.out.println("得到所有用户");

		//将list转为json
		String usersJson = gson.toJson(users, Set.class);
		System.out.println(usersJson+"user转换成json的格式");
		//将json发送到客户端
		resp.getWriter().println(usersJson);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		//获取gson对象
		Gson gson = new Gson();
		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");

		//获取浏览器端的Json
		Map jsonMap = gson.fromJson(req.getReader(), Map.class);
		System.out.println(jsonMap + "这是获取的数据");

		//获取用户id
		double Id = (double) jsonMap.get("objID");
		int userId = (int) Id;
		System.out.println(userId);

		//设置UserInfo的所在数据表
		db.setDataTable("users");
		User user = (User) UserUtil.ORMUserFromDb(db, "id", userId);
		String active = (String) jsonMap.get("active");
		//要返回的json字符
		Map<String, Object> respJson = new HashMap<String, Object>();
		if (active.equals("del")) {
			System.out.println("del");
			//数据库删除user对象
			boolean isDel = false;
			try {
				//设置User的所在数据表
				db.setDataTable("users");
				int su = db.delete("id", user.getId());
				//设置UserInfo的所在数据表
				db.setDataTable("userinfo");
				su += db.delete("usercode", user.hashCode());
				//判断是否成功
				isDel = su > 0;

			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("删除数据  " + user.getUsername() + isDel);
			respJson.put("goToURL", "");

			resp.getWriter().print(gson.toJson(respJson));
		} else if (active.equals("getInfo")) {

			//将数据存入域属性
			HttpSession session = req.getSession();
			session.setAttribute("userId", userId);
			respJson.put("goToURL","selectionUserInfo.html");
			System.out.println(session.getAttribute("userId") + "已经得到");


			//转发到userInfo.html
			//System.out.println(req.getContextPath()+"/userInfo.html?userId=" + userId);
			//resp.sendRedirect(req.getContextPath() + "/userInfo.html");
			//resp.sendRedirect(req.getContextPath()+"/userInfo.html?userId=" + userId);
		}

		resp.getWriter().print(gson.toJson(respJson));
	}
}
