package com.studentsManager.entity;

import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;

public class UserInfo {
	private User user;
	private int userCode;
	private Map<String,Object> userInfo ;
	
	//构造方法
	public UserInfo() {
		
	}
	
	public UserInfo(User user,Map<String,Object> attr) {
		this.user = user;
		userCode = user.hashCode();
		userInfo = attr;
	}
	
	//getter和setter方法
	public int getUserCode() {
		return userCode;
	}
	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}
	public Map<String, Object> getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(Map<String, Object> userInfo) {
		//更改用户修改的最后时间
		user.setCtime(new Date());
		this.userInfo = userInfo;
	}

	//字符串更改map
	public void setUserInfo(String userInfo){
		Gson gson = new Gson();
		userInfo = userInfo.replaceAll("\"","");
		System.out.println(userInfo+"这是str");
		Map<String,Object> InfoMap = gson.fromJson(userInfo,Map.class);
		setUserInfo(InfoMap);
	}
	
	//toString方法
	@Override
	public String toString() {
		return "UserInfo [userCode=" + userCode + ", userInfo=" + userInfo + "]";
	}


	//hashCode方法
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + userCode;
		result = prime * result + ((userInfo == null) ? 0 : userInfo.hashCode());
		return result;
	}
	
	//equals方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (userCode != other.userCode)
			return false;
		if (userInfo == null) {
			if (other.userInfo != null)
				return false;
		} else if (!userInfo.equals(other.userInfo))
			return false;
		return true;
	}
	
	
	
	
	
}
