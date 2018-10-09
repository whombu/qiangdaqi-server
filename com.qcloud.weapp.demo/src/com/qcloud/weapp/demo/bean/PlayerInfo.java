package com.qcloud.weapp.demo.bean;

import com.qcloud.weapp.authorization.UserInfo;

public class PlayerInfo {
	private UserInfo userInfo;
	
	private boolean isTheReferee;//是裁判吗？
	
	private String memoName;
	
	private boolean isOnline;//是否在线。当比赛开始时，判断其是否在线
	
	public boolean isTheReferee() {
		return isTheReferee;
	}

	public void setTheReferee(boolean isTheReferee) {
		this.isTheReferee = isTheReferee;
	}

	public PlayerInfo() {
		super();
		isTheReferee = false;
	}

	public String getMemoName() {
		return memoName;
	}

	public void setMemoName(String memoName) {
		this.memoName = memoName;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
}
