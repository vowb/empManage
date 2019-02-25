package com.studentsManager.filters;

import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;
import com.studentsManager.util.Check;
import com.studentsManager.util.UserUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CheckCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //获取req和resp
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        //获取数据库连接类
        DbPool dbPool = DbPool.getDbPool();

        //获取session
        HttpSession session = req.getSession(false);

        //获取cookie
        Cookie[] cookie = req.getCookies();

        //要重定向的地址
        String path = req.getContextPath() + "/userInfo.html";

        //判读session是否存在
        if(session != null){
            Check check = (Check) session.getAttribute("userCheck");
            if(check != null){
                resp.sendRedirect(path);
                return;
            }
        }

        //获取cookie中的用户名
        if(cookie != null){ for(Cookie el:cookie){
                if("username".equals(el.getName())){
                    Db db = dbPool.getDb();
                    System.out.println("这是检查cookie时借走的db");
                    String username = el.getValue();
                    //设置数据所在数据表
                    db.setDataTable("users");
                    User user = (User) UserUtil.ORMUserFromDb(db, "username", username);
                    Check ce = new Check(db,user);
                    //将得到的数据存入session
                    //新建session
                    session = req.getSession();
                    session.setAttribute("userCheck", ce);
                    dbPool.returnDb(db);
                    resp.sendRedirect(path);
                    return;
                }
            }
        }
        //过滤器放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
