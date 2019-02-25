package com.studentsManager.Listener;

/**
 * 初始化整个web应用程序
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/*
 * 应用初始化类
 */

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.util.GetConfig;

public class InitListener implements ServletContextListener {

	private Map<String,String> connConfig;
	// 覆写contextInitialied
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 初始化数据库
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("数据库加载驱动成功");
		} catch (ClassNotFoundException e) {
			System.out.println("数据库加载驱动失败");
			e.printStackTrace();
		}

		//	获取数据库地址
		//	获取配置文件路径
		String path = this.getClass().getClassLoader().getResource("./resource/dbConnectConfig.properties").getPath();
		connConfig = GetConfig.readProperty(path);
		String dbAddress = connConfig.get("dbAddress");
		String dbUsername = connConfig.get("dbUsername");
		String dbPassword = connConfig.get("dbPassword");
		String dbName = connConfig.get("dbName");

		// 获取ServletContext对象
		ServletContext sc = sce.getServletContext();


		//初始化数据库
		Connection conn = null;
		Statement se = null;
		String sql = "CREATE DATABASE IF NOT EXISTS "+connConfig.get("dbName");

		try {
			try {
				conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
				se = conn.createStatement();
				se.execute(sql);
			} finally {
				conn.close();
				se.close();
			}
			System.out.println("数据库初始化成功");
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("数据库初始化失败");
		}




		// 创建一个数据库连接池,db数为0,访问时在添加
		DbPool DbConns = DbPool.getDbPool(5, 5,dbAddress,dbUsername,dbPassword,dbName);
		//设置最大连接数和每次提升的连接数，如果不写该方法，会使用默认的最大连接数为50，每次提升为10
		DbConns.setMaxAndAddNum(20,5);
		// 将数据库连接池存入Context的域属性中
		sc.setAttribute("DbConns", DbConns);


		//获取mime对照表
		path = this.getClass().getClassLoader().getResource("./resource/MimeTypeMap.properties").getPath();
		Map<String,String> mimeMap = GetConfig.readProperty(path);
		sc.setAttribute("mimeMap", mimeMap);
	}

	// 覆写contextDestroyed
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		Set<Db> DbConns = (Set<Db>) sc.getAttribute("DbConns");
		for (Db el : DbConns) {
			// 关闭所有的数据库连接
			el.close();
		}
		System.out.println("已将所有数据库连接关闭");
	}
}
