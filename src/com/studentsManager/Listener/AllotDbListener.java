package com.studentsManager.Listener;

import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class AllotDbListener implements ServletRequestListener {
//    @Override
//    public void requestInitialized(ServletRequestEvent sre) {
//        //获取ServletContext
//        ServletContext sc = sre.getServletContext();
//        // 获取数据库连接池
//        DbPool dbs = (DbPool) sc.getAttribute("DbConns");
//        // 获取数据库的连接类
//        Db db = dbs.getDb();
//        sre.getServletRequest().setAttribute("db",db);
//        System.out.println("借到db对象，连接池中还有"+dbs.length()+"条连接");
//    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        //获取ServletContext
        ServletContext sc = sre.getServletContext();
        // 获取数据库连接池
        DbPool dbs = (DbPool) sc.getAttribute("DbConns");
        //获取该请求的db对象
        Db db = (Db) sre.getServletRequest().getAttribute("db");
        //归还db对象
        dbs.returnDb(db);
        System.out.println("归还db对象，连接池中还有"+dbs.length()+"条连接");

        //分割每个请求
        System.out.println("\n==========================================\n");
    }
}
