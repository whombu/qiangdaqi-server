package com.qcloud.weapp.demo.bean;

import java.util.concurrent.ConcurrentHashMap;

import com.qcloud.weapp.tunnel.TunnelRoom;

public class QdRoom {
	private TunnelRoom tunnelRoom = new TunnelRoom();

	private String roomId;

	private String roomName = "抢答比赛";

	private String refereeOpenId;// 裁判的ID

	private int playNum = -1;// 参加强大的最大人数,负数为无限制,可能带来问题
	
	private int validNo = -1;//验证码,-1为不验证

	private boolean isStart = false;// 是否已经开始
	
	private boolean isAnswered = false;//每一道题被抢答后，将其设置为true;重新开始下一题时设置为false

	private ConcurrentHashMap<String, PlayerInfo> userMap = new ConcurrentHashMap<>(10);

	public void addPlayer(String tunnelId, PlayerInfo playerInfo) {
		userMap.put(tunnelId, playerInfo);
	}

	public void removePlayer(String tunnelId) {
		userMap.remove(tunnelId);
	}

	public void setPlayerStatus(String tunnelId, boolean isOnline) {
		PlayerInfo playerInfo = userMap.get(tunnelId);
		if (playerInfo != null) {
			playerInfo.setOnline(isOnline);
		}
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRefereeOpenId() {
		return refereeOpenId;
	}

	public void setRefereeOpenId(String refereeOpenId) {
		this.refereeOpenId = refereeOpenId;
	}

	public int getPlayNum() {
		return playNum;
	}

	public void setPlayNum(int playNum) {
		this.playNum = playNum;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public int getValidNo() {
		return validNo;
	}

	public void setValidNo(int validNo) {
		this.validNo = validNo;
	}

	public ConcurrentHashMap<String, PlayerInfo> getUserMap() {
		return userMap;
	}

	public TunnelRoom getTunnelRoom() {
		return tunnelRoom;
	}

	public boolean isAnswered() {
		return isAnswered;
	}

	public void setAnswered(boolean isAnswered) {
		this.isAnswered = isAnswered;
	}
	
	
}
