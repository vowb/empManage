package com.studentsManager.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.studentsManager.dao.Db;
import com.studentsManager.entity.User;
import com.studentsManager.util.Check;
import com.studentsManager.util.UserUtil;

public class ChangeUserInfoServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {

		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");
		// 设置User的所在数据表
		db.setDataTable("users");

		// 获取gson对象
		Gson gson = new Gson();
		//获取json且转为map
		Map<String,Object> jsonMap = null;
		//返回
		Map<String,Object> respMap = new HashMap<String,Object>();
		Boolean flag = false;
		
		try {
			jsonMap = gson.fromJson(req.getReader(),Map.class);
			//获取客户端传入的user
			User user = (User) gson.fromJson(gson.toJson(jsonMap.get("user")), User.class);
			//获取客户端传入的info
			String userInfoJSON = gson.toJson(jsonMap.get("info"));
			//获取要更改的状态
            //String status = jsonMap.get();
			//获取user操作类
			Check check = new Check(db,user);

			// 设置UserInfo的所在数据表
			db.setDataTable("userinfo");
			check.changeUserInfo(db,null,userInfoJSON);

			// 设置User的所在数据表
            db.setDataTable("users");
			//将user对象持久化到数据库
            UserUtil.replaceObject(db,"username",user);

			flag = true;
		} catch (JsonSyntaxException e) {
			System.out.println("Gson语法错误");
			e.printStackTrace();
		} catch (JsonIOException e) {
			System.out.println("Gson IO错误");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO错误");
			e.printStackTrace();
		}
		
		respMap.put("status",flag);
		String respJson = gson.toJson(respMap);
		System.out.println(respJson);
		
		try {
			resp.getWriter().println(respJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
