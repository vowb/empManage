package com.studentsManager.dao;

/**
 * 连接池，可以实现自动超时消减Db连接
 */


import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Db对象的使用池
 */

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class DbPool{

	private static DbPool self = null;
	private Set<Db> dbs;
	private Timer timer;
	private double timerMin;
	private TimerTask tt;
	private String dbAddress;
	private String dbName;
	private String dbPassword;
	private String dbDataBase;
	private int max;
	private int addNumber;
	public int size;

	//getter和setter
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		if(max > 500)max = 500;
		if(max < 0)max = -1;
		this.max = max;
	}

	public int getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(int addNumber) {
		if(addNumber > 500)addNumber = 500;
		if(addNumber < 0)addNumber = 0;
		this.addNumber = addNumber;
	}

	public Set<Db> getDbs() {
		return dbs;
	}

	public void setDbs(Set<Db> dbs) {
		this.dbs = dbs;
	}

	//设置最大连接数和每次增加连接数
	public void setMaxAndAddNum(int max,int addNumber){
		setMax(max);
		setAddNumber(addNumber);
	}

	//单例DbPool
	public static DbPool getDbPool(int su,double min,String address, String name, String password, String dataBase){
		if(self == null){
			//设置线程安全
			synchronized(DbPool.class){
				if(self == null){
					self = new DbPool(su,min,address,name,password,dataBase);
				}
			}
		}
		return self;
	}


	//获取DbPool中的唯一对象，如果存在则返回，反之返回null
	public static DbPool getDbPool(){
		return self;
	}

	//构造函数
	private DbPool(int su, double min, String address, String name, String password, String dataBase) {
		this.dbs = new HashSet<Db>();
		this.dbAddress = address;
		this.dbName = name;
		this.dbPassword = password;
		this.dbDataBase = dataBase;
		this.max = -1;
		this.addNumber = -1;
		this.size = 0;
		init(su);
		timerMin = min;
		getTimer();
		self = this;
	}

	private void getTimer() {
		timer = new Timer();
	}


	private void startTimer(double min) {
		//将分钟数转成秒数
		long sec = (long) (min * 1000 * 60);
		System.out.println("距离下次清除剩余时间："+sec / 1000+"秒");
		//检测是否有任务,如果有，取消并清除
		if(tt != null) {
			tt.cancel();
			timer.purge();
		}

		tt = new TimerTask() {
			public void run() {
				//指定时间间隔清除一个db
				Iterator<Db> it = dbs.iterator();
				if(it.hasNext()) {
					//移除set中闲置的Db对象
					Db db = it.next();
					db.close();
					dbs.remove(db);
					System.out.println("已经移除 "+db+",该连接池中还有"+dbs.size()+"个Db对象");
				}else {
					this.cancel();
					timer.purge();
					System.out.println("DbPool中已经没有任何Db");
				}
			}
		};
		timer.schedule(tt,sec,sec);
	}

	//初始化dbs

	private boolean init(int su) {
		//循环创建Db对象
		Db db = null;
		boolean flag = true;
		//获取db配置

		for(int i = 0;i<su;i++) {
			try {
				db = new Db(dbAddress,dbName,dbPassword,dbDataBase);
			} catch (SQLException e) {
				flag = false;
				System.out.println("Db对象创建失败创建失败");
				e.printStackTrace();
			}
			//加入dbs中
			size++;
			dbs.add(db);
		}
		return flag;
	}

	//添加db到set中
	public synchronized boolean addDb(Db db) {
		//重新启动定时器
		startTimer(timerMin);
		return this.dbs.add(db);
	}

	//得到Db对象
	public synchronized Db getDb() {
		raiseSize();
		//重新启动定时器
		startTimer(timerMin);
		//多线程中的notify()执行后会继续沿着wait()方向继续执行
		System.out.println("连接池中共有连接"+dbs.size()+"条");
		while(dbs.isEmpty()) {
			try {
				System.out.println(Thread.currentThread()+"在等待");
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Iterator<Db> it = dbs.iterator();
		Db db = null;
		if(it.hasNext()) {
			db = it.next();
			dbs.remove(db);
			System.out.println(Thread.currentThread()+"借了一个，连接还有"+dbs.size());
		}
		return db;
	}

	//增加达到指定条件时增加连接
	public boolean raiseSize(){
		if(max == 0 || addNumber == 0){
			return true;
		}
		int maxNum = max == -1 ? 50 : max;
		int addNum = addNumber == -1 ? 10 : addNumber;
		int someNum = 0;
		//如果该最大值不是每次提升数的倍数的话，将多余的数赋值给提升数
		if((someNum = (maxNum-size)%addNum) != 0 )addNum = someNum;
		if(size < maxNum){
			if(length() <= 1) {
				System.out.println("提升连接池连接数量中...");
				return init(addNum);
			}
		}
		return false;
	}

	public synchronized boolean returnDb(Db db) {
		if(null != db && db instanceof Db){
			boolean bl = dbs.add(db);
			this.notifyAll();
			System.out.println(Thread.currentThread()+"归还了一个，连接还有"+dbs.size()+"条");
			return bl;
		}else{
			System.out.println("归还失败，该db对象是空或者db对象的父类不是Db");
			return false;
		}

	}

	//length()方法
	public int length() {
		return this.dbs.size();
	}

}
