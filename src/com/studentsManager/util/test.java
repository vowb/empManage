package com.studentsManager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.entity.User;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class test {
	private static int s = 0;

	public static void main(String[] args){



		// 连接池测试

		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("数据库加载驱动成功");
		} catch (ClassNotFoundException e) {
			System.out.println("数据库加载驱动失败");
			e.printStackTrace();
		}

		String path = test.class.getClassLoader().getResource("./resource/dbConnectConfig.properties").getPath();
		Map<String,String> connConfig = GetConfig.readProperty(path);
		String dbAddress = connConfig.get("dbAddress");
		String dbUsername = connConfig.get("dbUsername");
		String dbPassword = connConfig.get("dbPassword");
		String dbName = connConfig.get("dbName");

		DbPool dbs = DbPool.getDbPool(5, 0.2,dbAddress,dbUsername,dbPassword,dbName);
		dbs.setMaxAndAddNum(20,3);
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 50 ; i++) {
			Thread td = new Thread() {
				public void run() {
					System.out.println(s);
					Db db = dbs.getDb();
					//System.out.println(Thread.currentThread()+"的db是 "+db);
					try {
						db.createDataTable("create table td"+getS()+"(id int primary key)");
					} catch (SQLException e) {
						System.out.println("戳了");
						e.printStackTrace();
					}finally {
						dbs.returnDb(db);
					}
				}

				private synchronized int getS() {
					return ++s;
				}
			};
			td.start();
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
	}



//		String str = "{\"id\":9.0,\"name\":\"linux\",\"username\":\"141415\",\"password\":\"121314\",\"power\":\"PLAIN\",\"userGroup\":0.0,\"ctime\":\"Dec 3, 2018 11:11:17 AM\",\"initTime\":\"十一月 20, 2018\",\"inDepartment\":\"undefined\"}";
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//		User user = gson.fromJson(str,User.class);
//		System.out.println(user);





//	    String str = "sadasdasdasdsadsads;";
//
//	    System.out.println(str.substring(str.length()-1));

//		Byte a = 11;
//		System.out.println(a.getClass().getName().equals("java.lang.Byte"));

//		String path = "";
//		path = test.class.getClassLoader().getResource("./resource/dbConnectConfig.properties").getPath();
//		path = URLDecoder.decode(path,"UTF-8");
//		path = URLDecoder.decode(path,"UTF-8");
//		System.out.println(path);
//		Map<String,String> connConfig = GetConfig.readProperty(path);
//		System.out.println(connConfig);


//		File file = new File("C:\\Users\\23602\\Desktop\\userData\\sx.properties");
//		Map<String,String> pro = new HashMap<>();
//		pro.put(new String("���Լ�1".getBytes("ISO-8859-1"),"GBK"),new String("ֵ1".getBytes("ISO-8859-1"),"GBK"));
//		pro.put("���Լ�2", "ֵ2");
//		pro.put("���Լ�3", "ֵ3");
//		pro.put("���Լ�4", "ֵ4");
//		System.out.println(file.exists());
//		GetConfig.writeProperty(file, pro, new String("���Բ���".getBytes("ISO-8859-1"),"GBK"));

	}
}