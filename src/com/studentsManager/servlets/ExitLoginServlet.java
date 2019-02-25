package com.studentsManager.servlets;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExitLoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取session
        HttpSession session = req.getSession(false);

        //获取gson
        Gson gson = new Gson();

        //获取json数据
        Map jsonData = gson.fromJson(req.getReader(), Map.class);
        double cookieExpires = (double) jsonData.get("cookieExpires");

        //清除cookie
        Cookie cookie = new Cookie("username",null);
        cookie.setMaxAge((int)cookieExpires);
        resp.addCookie(cookie);

        //清除session
        session.removeAttribute("userCheck");

        Map<String,String> respData = new HashMap<String, String>();
        respData.put("jumpAddress","index.html");
        //返回要跳转的页面
        resp.getWriter().println(gson.toJson(respData));
    }
}
