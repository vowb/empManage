package com.studentsManager.entity;

import com.studentsManager.Listener.InitListener;
import com.studentsManager.dao.Db;
import com.studentsManager.dao.DbPool;
import com.studentsManager.util.ORMFieldAnnotations.Removing;
import com.studentsManager.util.ORMFieldAnnotations.Type;

import java.lang.annotation.Retention;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * 用户实体类
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
	@Type(type = Type.fieldType.INT)
	private int id;
	@Type
	private String name;
	@Type
	private String username;
	@Type
	private String password;
	@Type(type = Type.fieldType.POWER)
	private Power power;
	private int userGroup;
	@Type(type = Type.fieldType.DATE)
	private Date ctime;
	@Type(type = Type.fieldType.DATE)
	private Date initTime;
	private String inDepartment;
	@Removing
	private UserInfo userInfo;
	
	
	//返回date格式化字符串
	public String getCtimeFormat(String formatText) {
		return getString(formatText, ctime);
	}

	private String getString(String formatText, Date ctime) {
		SimpleDateFormat format = null;
		if(formatText == null) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else {
			format = new SimpleDateFormat(formatText);
		}
		return format.format(ctime);
	}

	public Date getCtime() {
		return ctime;
	}
	
	public void setCtime(Date date) {
		this.ctime = date;
	}
	
	public void setCtime(String date,String formatText) {
		SimpleDateFormat format = formatText == null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : new SimpleDateFormat(formatText);
		
		//转为date
		Date ctime = null;
		try {
			ctime = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.ctime = ctime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInDepartment() {
		return inDepartment;
	}

	public void setInDepartment(String inDepartment) {
		this.inDepartment = inDepartment;
	}

	public int getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(int userGroup) {
		//设置userGroup范围
		userGroup = userGroup>0 ? (userGroup>100 ? 100 : userGroup) : 0;
		this.userGroup = userGroup;
		changeDate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		changeDate();
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		changeDate();
		this.password = password;
	}

	public Power getPower() {
		return power;
	}

	public void setPower(Power power) {
		changeDate();
		this.power = power;
	}
	
	public Date getInitTime() {
		return this.initTime;
	}
	
	public String getInitTimeFormat(String formatText) {
		return getString(formatText, initTime);
	}
	
	public void setInitTime(Date initDate) {
		this.initTime = initDate;
	}
	
	//更改最后修改时间
	private Date changeDate() {
		return this.ctime = new Date();
	}

	// 构造方法
	public User() {

	}

	public User(String name,String username, String password, String type) {
		this(name,username, password, getTypeInt(type));
	}

	public User(String name,String username, String password, int power) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.initTime = changeDate();
		
		//设置默认的用户组为0
		this.setUserGroup(0);
		if (power > 0) {
			this.power = Power.ADMIN;
		} else if (power < 0) {
			this.power = Power.EXTRANEOUS;
		} else {
			this.power = Power.PLAIN;
		}
	}

	// type转换成int
	private static Integer getTypeInt(String str) {
		if ("admin".equalsIgnoreCase(str)) {
			return 1;
		} else if ("plain".equalsIgnoreCase(str)) {
			return 0;
		} else if ("extraneous".equalsIgnoreCase(str)) {
			return -1;
		}
		return null;
	}

	// toString方法
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", name=" + name + ", power=" + power
				+ ", userGroup=" + userGroup + "]";
	}

	// hashCode()方法
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + username.hashCode();
		return result;
	}

	// equals方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (power != other.power)
			return false;
		if (userGroup != other.userGroup)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
