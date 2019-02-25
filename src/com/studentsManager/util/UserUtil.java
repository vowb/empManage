package com.studentsManager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.studentsManager.dao.Db;
import com.studentsManager.entity.Power;
import com.studentsManager.entity.User;
import com.studentsManager.entity.UserInfo;
import com.studentsManager.util.ORMFieldAnnotations.Removing;
import com.studentsManager.util.ORMFieldAnnotations.Type;

public class UserUtil {

	//获取对象中的属性
	public static Map<String,Object> getObjFields(Object obj) {

		//将属性值遍历到Map
		Map<String, Object> atts = new LinkedHashMap<String, Object>() {
		};

		try {
			//获取Class对象
			Class<?> objClass = obj.getClass();

			//获取属性值
			String checkAttr = null;
			for (Field el : objClass.getDeclaredFields()) {
				//设置私有属性可见
				el.setAccessible(true);
				//获取属性值
				Object attribute = el.get(obj);
				//检查该属性中存在那种注释
				if (el.isAnnotationPresent(Removing.class)) {
					continue;
				} else if (el.isAnnotationPresent(Type.class)) {
					//获取注释
					Type fieldType = el.getDeclaredAnnotation(Type.class);
					//获取属性类型
					String type = fieldType.type().toString();
					if ("STRING".equals(type)) {

					} else if ("DATE".equals(type)) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						attribute = format.format(attribute);
					} else if ("POWER".equals(type)) {
						attribute = ((Power) attribute).getPowerVal();
					} else if ("INT".equals(type)){

					}
				}
				//将该对象中的属性名和属性值put到map中
				atts.put(el.getName(), attribute);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		//返回这个map
		return atts;
	}

		// ORM User到数据库
	public static boolean ORMObject(Db db, String confirmAttr,Object obj) {
		Map<String,Object> fields = getObjFields(obj);
		//获取要验证的值
		String checkAttr = (String) fields.get(confirmAttr);
		//获取对象中所有的属性值
		Collection<Object> atts = fields.values();
		try{
			//获取SQL返回对象
			//查询是否有该要插入的用户，如果有返回false,否则返回true
			ResultSet rs = db.query(confirmAttr,confirmAttr,checkAttr);
			//遍历返回数据
			for(int i = 0;rs.next();i++) {
				String r = rs.getString(1);
				if(r.equals(checkAttr) ){
					return false;
				}
			}
			return db.insert(atts.toArray()) > 0;
		} catch (Exception e) {
			System.out.println("数据库中没有数据");
			e.printStackTrace();
			//返回是否成功
			return false;
		}
	}

	//更新对象
	public static boolean replaceObject(Db db, String confirmAttr,Object obj) {
		Map<String,Object> fields = getObjFields(obj);
		//获取对象中所有的属性值
		Collection<Object> atts = fields.values();
		try{
			//返回成功与否
			return db.preparedInsert(atts.toArray()) > 0;
		} catch (Exception e) {
			System.out.println("替换失败");
			e.printStackTrace();
			//返回是否成功
			return false;
		}
	}
	
	//ORM User来自数据库
	public static Object ORMUserFromDb(Db db,String key,Object val){
		User user = null;
		try{
			ResultSet rs = db.query("*", key, val);

			Set<User> users = getUsers(rs);
			Iterator it = users.iterator();
			if(it.hasNext()) {
				user = (User) it.next();
			}
		}catch(NullPointerException | SQLException e){
			System.out.println("数据库转发对象失败");
			e.printStackTrace();
		}

		return user;
	}

	//将resultSet中的所有的结果转化为user对象
	private static Set<User> getUsers(ResultSet rs){
		Set<User> users =new LinkedHashSet<User>();
		try{
			while(rs.next()) {
				User user = new User(rs.getString("name"),rs.getString("username"),rs.getString("password"),rs.getInt("power"));
				user.setUserGroup(rs.getInt("usergroup"));
				user.setInitTime(rs.getTimestamp("inittime"));
				user.setInDepartment(rs.getString("indepartment"));
				user.setId(rs.getInt("id"));
				//最后修改属性一定要最后写
				user.setCtime(rs.getTimestamp("ctime"));
				users.add(user);
			}
		}catch(SQLException e){
			users = null;
			e.printStackTrace();
		}
		return users;
	}

	public static boolean updateCTime(Db db,User user){
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(date);
		boolean flag = false;
		try {
			//设置要更改的数据表
			db.setDataTable("users");
			db.update("ctime",dateStr,"username",user.getUsername());
			user.setCtime(date);
			flag = true;
		} catch (SQLException e) {
			System.out.println("刷新最后修改时间出错");
			e.printStackTrace();
		}
		return flag;
	}
	
	//读取html文件
	public static String getHtml(File file) throws FileNotFoundException {
		String html = "";
		FileChannel fc = null;
		ByteBuffer buffer = null;
		//获取文件通道
		fc = new FileInputStream(file).getChannel();
		//获取缓存区
		buffer = ByteBuffer.allocate(5 * 1024);
		
		//缓存区读取通道信息
		try {
			while(fc.read(buffer) != -1) {
				//将缓存区改为写入状态
				buffer.flip();
				//设置字符编码为UTF-8
				CharBuffer cb = Charset.forName("UTF-8").decode(buffer);
				html = html.concat(cb.toString());
				buffer.rewind();
			}
			System.out.println("通道读取到缓存区成功...");
		} catch (IOException e) {
			System.out.println("通道读取到缓存区失败！！！");
			e.printStackTrace();
		}
		//返回html,如果没读取到就会返回""字符
		return html;
		
	}
	
	//获取所有用户
	public static Set<User> getAllUsers(Db db) {
		ResultSet rs = null;
		User user = null;
		Set<User> users = null;
		try {
			//获取数据库查询结果
			rs = db.query("*");
			users = getUsers(rs);

		} catch (SQLException e) {
			System.out.println("获取所查询用户失败");
			e.printStackTrace();
		}

		return users;
	}

	public static Set<User> getPageingUsers(Db db,int page,int length){
		ResultSet rs = null;
		User user = null;
		Set<User> users = null;
		int start = page * length;
		try {
			//获取数据库查询结果
			rs = db.pageingQuery("*","id",true,start,length);
			users = getUsers(rs);

		} catch (SQLException e) {
			System.out.println("获取所查询用户失败");
			e.printStackTrace();
		}

		return users;
	}
	
}
