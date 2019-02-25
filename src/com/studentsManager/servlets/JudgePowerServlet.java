package com.studentsManager.servlets;

import com.google.gson.Gson;
import com.studentsManager.entity.Power;
import com.studentsManager.util.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JudgePowerServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //得到gson
        Gson gson = new Gson();
        //获取json数据
        Map jsonData = gson.fromJson(req.getReader(), Map.class);
        if(Power.ADMIN.toString().equalsIgnoreCase((String )jsonData.get("userPower"))){
            //返回页面数据
            File file = new File("query.html");
            String html = UserUtil.getHtml(file);
            System.out.println(html);
            resp.getWriter().println(html);
        }
    }
}
