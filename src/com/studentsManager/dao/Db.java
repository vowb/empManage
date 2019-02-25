package com.studentsManager.dao;

/**
 * 数据库操作类
 */

import java.io.*;
import java.sql.Blob;

/*
 * 数据库操作类
 */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

public class Db {
	private String address;
	private String dataBaseName;
	private String name;
	private String password;
	private String dataTable;
	private Connection conn;
	private Statement se;
	// 单例
//	private static Db db;

	// getter和setter方法
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public String getDataTable() {
		return dataTable;
	}

	//设置数据库中的表名
	public void setDataTable(String dataTable) {
		this.dataTable = dataTable;
	}

	// 初始化数据库
	static {

	}

	// 默认无参构造方法
//	public Db() throws SQLException {
//		this("jdbc:mysql://127.0.0.1:3306?characterEncoding=UTF-8", "root", "root", "studentsmanager");
//	}

	public Db() {}
	// 有参构造方法
	public Db(String address, String name, String password, String dataBase) throws SQLException {
		StringBuffer sb = new StringBuffer(address);
		address = sb.insert(sb.indexOf("?"), "/" + dataBase).toString();
		this.address = address;
		this.dataBaseName = dataBase;
		this.name = name;
		this.password = password;
		conn = DriverManager.getConnection(address, name, password);
		se = conn.createStatement();
	}

//	//单例无参Db
//	public static Db getDb() throws SQLException {
//		if(db == null) {
//			return db = new Db();
//		}else {
//			return db;
//		}
//	}
//	
//	//单例有参Db
//		public static Db getDb(String address, String name, String password,String dataBase) throws SQLException {
//			if(db == null) {
//				return db = new Db(address,name,password,dataBase);
//			}else {
//				return db;
//			}
//		}

	// 创建数据表
	public int createDataTable(String sql) throws SQLException {
		StringBuffer sb = new StringBuffer(sql);
		String[] str = sb.substring(0, sb.indexOf("(")).split(" ");
		String tableName = str[str.length - 1];
		setDataTable(tableName);
		return se.executeUpdate(sql);
	}

	public int createDataTables(String... args) throws SQLException {
		conn.setAutoCommit(false);
		for (String sql : args)
			se.addBatch(sql);
		int[] sums = se.executeBatch();
		int sum = 0;
		for (int el : sums)
			sum += el;
		conn.commit();
		return sum;
	}

	// 获取数据库中有多少数据表
	public String[] getDataTables() {
		ResultSet dbTables = null;
		List<String> tables = new LinkedList<String>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			dbTables = dbmd.getTables(null, null, null, null);

			// 将数据表添加到List
			while (dbTables.next()) {
				tables.add(dbTables.getString(3));
			}
		} catch (SQLException e) {
			System.out.println("获取元数据失败");
			e.printStackTrace();
		}

		return (String[]) tables.toArray();

	}

	// 增加数据
	public int insert(Object... args) throws SQLException {
		String element = "";
		for (Object el : args) {
			if (null == el)
				el = "undefined";
			if (el.getClass() == String.class)
				el = "\"" + el + "\"";
			element = element.concat("," + el);
		}
		String sql = "insert into " + getDataTable() + " values(null" + element + ")";
		return se.executeUpdate(sql);
	}

	public int preparedInsert(Object... args) throws SQLException {
		// 生成“?”字符
		String element = "";
		for (int i = 0; i < args.length; i++) {
			element += ",?";
		}
		element = element.substring(1, element.length());
		// 拼接问好sql语句
		String sql = "replace into " + getDataTable() + " values(" + element + ")";
		// 执行sql语句
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < args.length; i++) {
			int index = i+1;
			if(null == args[i]) {
				ps.setNull(index,Types.NULL);
			} else if (args[i].getClass() == String.class) {
				ps.setString(index, (String) args[i]);
			} else if (args[i] instanceof Blob) {
				ps.setBlob(index, (Blob) args[i]);
			} else if (args[i].getClass() == InputStream.class) {
				ps.setBinaryStream(index, (InputStream) args[i]);
			} else if (args[i].getClass() == Integer.class) {
				ps.setInt(index, (int) args[i]);
			}
		}
		return ps.executeUpdate();
	}

	// 查询数据
	public ResultSet query(String row, String key, Object value) throws SQLException {
		if (value.getClass() == String.class)
			value = "\"" + value + "\"";
		String sql = "select " + row + " from " + getDataTable() + " where " + key + "=" + value;
		ResultSet rs = se.executeQuery(sql);
		return rs;
	}

	// 查询数据
	public ResultSet query(String row) throws SQLException {
		String sql = "select " + row + " from " + getDataTable();
		ResultSet rs = se.executeQuery(sql);
		return rs;
	}

	//分页查询数据

	/**
	 * 如果isAsc为true则asc排序，否则desc排序
	 * @param row
	 * @param orderRow
	 * @param isAsc
	 * @param start
	 * @param over
	 * @return
	 * @throws SQLException
	 */
	public ResultSet pageingQuery(String row,String orderRow,boolean isAsc,int start,int over) throws SQLException {
		String order = isAsc ? "asc" : "desc";
		String sql = "select " + row + " from " + getDataTable() + " order by " + orderRow + " " + order + " limit "+start+","+over;
		ResultSet rs = se.executeQuery(sql);
		return rs;
	}

	// 修改数据
	public int update(String upKey, String upVal, String key, Object val) throws SQLException {
		if (upVal.getClass() == String.class)
			upVal = "\"" + upVal + "\"";
		if (val.getClass() == String.class)
			val = "\"" + val + "\"";
		String sql = "update " + getDataTable() + " set " + upKey + "=" + upVal + " where " + key + "=" + val;
		return se.executeUpdate(sql);
	}

	// 删除数据
	public int delete(String key, Object val) throws SQLException {
		if (val.getClass() == String.class)
			val = "\"" + val + "\"";
		String sql = "delete from " + getDataTable() + " where " + key + "=" + val;
		return se.executeUpdate(sql);
	}

	// 获取表中的结构
	public List<String> getAttr() {
		String sql = "desc " + getDataTable();
		List<String> attr = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = se.executeQuery(sql);
			while (rs.next()) {
				attr.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attr;
	}

	//执行sql语句
	public int runSql(String sql){
		int su = 0;
		try {
			su = se.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public ResultSet runSqlQuery(String sql){
		ResultSet rs = null;
		try {
			rs = se.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	//	执行sql文件CUD
	public int[] runSql(File file){
		InputStream is = null;
		BufferedReader br = null;
		String sql = "";
		int[] arr = null;
		if(file.exists()) {
			try {
				is = new FileInputStream(file);
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	
				//	读取内容
				String str = null;
				while (null != (str = br.readLine())) {
					sql += str;
					//	如果sql字符串的最后一个值是分号时将sql语句加入到批处理
					if (sql.substring(sql.length() - 1).equals(";")) {
						//清除注释
						sql.replaceAll("^/\\*.*\\*/$", "");
						//添加一个sql语句到批处理
						se.addBatch(sql);
					}
				}
				arr = se.executeBatch();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return arr;
	}

	//执行sql文件CURD
	//返回的结果中有可能有resultSet对象也可能有更新数
	public List<Object> runSqlQuery(File file){
		InputStream is = null;
		BufferedReader br = null;
		String sql = "";
		ResultSet rs = null;
		List<Object> rss = new LinkedList<Object>();
		if(file.exists()){
			try {
				is = new FileInputStream(file);
				br = new BufferedReader(new InputStreamReader(is,"UTF-8"));

				//	读取内容
				String str = null;
				while(null != (str = br.readLine()) ){
					sql += str;
					//	如果sql字符串的最后一个值是分号时将执行sql语句
					if(sql.substring(sql.length()-1).equals(";")){
						//匹配并清除注释
						sql.replaceAll("^/\\*.*\\*/$", "");
						if(se.execute(sql)){
							//	如果是查询语句将结果集添加到rss
							rss.add(se.getResultSet());
						}else{
							rss.add(se.getUpdateCount());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rss;
	}


	// 关闭数据库
	public boolean close() {
		try {
			se.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
}
