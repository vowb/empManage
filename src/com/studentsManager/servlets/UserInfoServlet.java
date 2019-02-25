package com.studentsManager.servlets;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;
import com.studentsManager.util.Check;
import com.studentsManager.util.UserUtil;

public class UserInfoServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 获取数据库的连接类
		Db db = (Db) req.getAttribute("db");

		// 获取gson对象
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		//jsonMap
		Map<String,Object> jsonMap = new HashMap<String,Object>();

		//获取session
		HttpSession session = req.getSession(false);
		Check userCe = null;
		String headPath = null;
		boolean isHead = false;
		String dbJson = "";
		Map<String,Object> dbMap = null;

		//获取转发过来的id
		Integer userId = (Integer) req.getAttribute("userId");

		if(session != null) {
			userCe = (Check) session.getAttribute("userCheck");
			//session中是否有该属性,如果有的话，userCe会优先于该操作
			if(null != userId){
				System.out.println(userId);
				//获取user对象
				db.setDataTable("users");
				User user = (User) UserUtil.ORMUserFromDb(db,"id",userId);
				userCe = new Check(db,user);
			}
		}

		//用户头像地址
		headPath = req.getContextPath()+"/static/image/heads/"+userCe.getUser().getUsername()+".jpg";
		//将图片从数据库拷贝到本地
		if(!new File(headPath).exists()) {
			// 设置UserInfo的所在数据表
			db.setDataTable("userInfo");
			isHead = userCe.getUserHeadPortrait(db, headPath, "userhead");
		}
		dbJson = userCe.getUserInfoJSON(db, "userinfo");
		if(dbJson != null) {
			dbJson = dbJson.replaceAll("\"","");
			dbMap = gson.fromJson(dbJson, Map.class);
		}

		//将需要的信息插入map
		if(isHead)jsonMap.put("headPortarits", headPath);
		if(dbJson != null)jsonMap.put("data", dbMap);
		jsonMap.put("user", userCe.getUser());


		// 返回
		//System.out.println(gson.toJson(jsonMap)+"-------------------------------------------------------");
		resp.getWriter().println(gson.toJson(jsonMap));


	}
}
