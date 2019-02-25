package com.studentsManager.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SelectionUserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取session
        HttpSession session = req.getSession(false);
        Integer userId = null;
        //获取数据
        if(session != null){
            userId = (Integer) session.getAttribute("userId");
            if(null != userId){
                req.setAttribute("userId",userId);
                System.out.println(req.getAttribute("userId")+"这是刚刚存入的userId");
            }
        }

        //转发请求
        req.getRequestDispatcher("/userinfo").forward(req,resp);
    }
}
