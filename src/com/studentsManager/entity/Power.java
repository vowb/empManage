package com.studentsManager.entity;

public enum  Power {
	ADMIN(1),PLAIN(0),EXTRANEOUS(-1);
	private int powerVal;
	
	private Power(int val) {
		this.powerVal = val;
	}
	
	public int getPowerVal() {
		return powerVal;
	}
	
	//toString方法
	public String toString() {
		if(powerVal > 0) {
			return "admin";
		}else if(powerVal < 0) {
			return "extraneous";
		}else {
			return "plain";
		}
	}
}
