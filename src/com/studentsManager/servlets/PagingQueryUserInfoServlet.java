package com.studentsManager.servlets;

import com.google.gson.Gson;
import com.studentsManager.dao.Db;
import com.studentsManager.entity.User;
import com.studentsManager.util.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class PagingQueryUserInfoServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //获取db
        Db db = (Db) req.getAttribute("db");
        //获取Gson
        Gson gson = new Gson();
        //获取页码
        Map pageNumber = gson.fromJson(req.getReader(),Map.class);
        double number = (double) pageNumber.get("pageNumber");
        //设置UserInfo的所在数据表
        db.setDataTable("users");

        //调整合适的页码
        int num = (int)number - 1;
        //得到所有用户
        Set<User> users = UserUtil.getPageingUsers(db,num,10);

        //将list转为json
        String usersJson = gson.toJson(users, Set.class);
        //将json发送到客户端
        resp.getWriter().println(usersJson);
    }
}
