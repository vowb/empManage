package com.studentsManager.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import com.google.gson.Gson;
import com.studentsManager.dao.Db;
import com.studentsManager.entity.User;
import com.studentsManager.entity.UserInfo;

/**
 * 用户操作类
 * 
 * @author 23602
 *
 */
public class Check {
	private Db db;
	private User user;
	private int userCode;
	private UserInfo userInfo;

	public Db getDb() {
		return db;
	}

	public void setDb(Db db) {
		this.db = db;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public boolean setUserInfo(UserInfo userInfo) {
		boolean flag = false;
		if(userInfo.getUserCode() == userCode){
			this.userInfo = userInfo;
			flag = true;
		}
		return flag;
	}

	// 构造函数
	public Check() {

	}

	public Check(Db db, User user) {
		this.db = db;
		this.user = user;
		userCode = this.user.hashCode();
		this.userInfo = new UserInfo(user,null);
	}

	// 登录
	public boolean login() {
		try {
			ResultSet rs = this.getDb().query("password", "username", this.getUser().getUsername());
			if (rs.next()) {
				return this.getUser().getPassword().equals(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	// 注册
	public boolean logup() {
		try {
			// 获取SQL返回对象
			ResultSet rs = this.getDb().query("username", "username", this.getUser().getUsername());
			System.out.println(null == rs);
			// 遍历返回数据
			while (rs.next()) {
				String r = rs.getString(1);
				System.out.println(r);
				if (r.equals(this.getUser().getUsername())) {
					return false;
				}
			}
			return this.getDb().insert(this.getUser().getUsername(), this.getUser().getPassword(),
					this.getUser().getPower().getPowerVal()) > 0;
		} catch (SQLException e) {
			System.out.println("数据库中没有数据");
			// e.printStackTrace();
			// 返回是否成功
			return false;
//			try {
//				return this.getDb().insert(this.getUser().getUsername(),this.getUser().getPassword(),this.getUser().getPower().getPowerVal()) > 0;
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//				return false;
//			}

		}
	}

	// 清除用户
	public boolean delete() {
		try {
			return this.getDb().delete("username", this.getUser().getUsername()) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 更改用户
	public boolean update(Map<String, Object> updates) {
		boolean flag = false;
		Set<String> keys = updates.keySet();
		Set<Object> vals = (Set<Object>) updates.values();
		Iterator<String> keyIt = keys.iterator();
		Iterator<Object> valIt = vals.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			Object val = valIt.next();
			if (val != null) {
				try {
					flag = this.getDb().update("username", this.getUser().getUsername(), key, val) > 0;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	// 得到该user对象的UserInfo
	public UserInfo getUserInfo(Db db, String blobName) {
		Gson gson = new Gson();
		String userInfoJson = getUserInfoJSON(db, blobName);
		// 获取userInfo中的map
		Map<String, Object> userMap = gson.fromJson(userInfoJson, Map.class);
		// 组成userInfo;
		UserInfo userInfo = new UserInfo(user, userMap);

		return userInfo;
	}

	// 得到该user对象的UserInfo的JSON字符
	public String getUserInfoJSON(Db db, String blobName) {
		String userInfoJson = null;
		Gson gson = new Gson();
		ResultSet rs = null;
		try {
			try {
				rs = db.query(blobName, "userCode", userCode);
				rs.next();
				Blob bo = rs.getBlob(blobName);
				// 将序列化数组生成字符串
				userInfoJson = new String(bo.getBytes(1, (int) bo.length()), "UTF-8");
			}finally {
				rs.close();
			}
		} catch (SQLException | UnsupportedEncodingException e) {
			System.out.println("获取userInfo对象失败");
			e.printStackTrace();
		}
		return userInfoJson;
	}
	
	//得到该用户的头像地址
	public boolean getUserHeadPortrait(Db db,String path,String blobName) {
		DataOutputStream dos = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			rs = db.query(blobName, "usercode", userCode);
			rs.next();
			Blob bo = rs.getBlob(blobName);
			if(bo != null) {
				//图片byte数组
				InputStream bis = bo.getBinaryStream();
				
				//创建文件位置
				File file = new File(path);
				dos = new DataOutputStream(new FileOutputStream(file));
				byte[] datas = new byte[1024*2];
				while(bis.read(datas) != -1) {
					dos.write(datas);
				}
				System.out.println("获取头像成功");
				flag = true;
			}
		} catch (SQLException | IOException e) {
			System.out.println("获取头像失败");
			e.printStackTrace();
		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("数据输出流已经关闭");
		}
		return flag;
	}

	// 更改userInfo
	public boolean changeUserInfo(Db db, InputStream headIs, String attr) {
		boolean flag = false;
		this.userInfo.setUserInfo(attr);

		// 更改操作
        Blob bb = null;
        try {
        	//将json字符串以UTF-8的编码格式打散成数组
            bb = new SerialBlob(attr.getBytes("UTF-8"));
            db.preparedInsert(userCode, headIs, bb);
		} catch (SQLException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	System.out.println("将json字符串以utf-8的方式转为二进制数组时失败");
			e.printStackTrace();
		}
		//更新最后修改时间
		flag = UserUtil.updateCTime(db, getUser());
        return flag;
	}
}
