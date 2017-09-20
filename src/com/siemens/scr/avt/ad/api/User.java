package com.siemens.scr.avt.ad.api;


public class User {
	
	private int userId;
	private String userName;
	private int roleInt;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getRoleInt() {
		return roleInt;
	}
	public void setRoleInt(int roleInt) {
		this.roleInt = roleInt;
	}	
}
