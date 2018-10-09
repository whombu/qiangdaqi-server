package com.qcloud.weapp.demo.demodb;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.qcloud.weapp.demo.bean.QdRoom;

public class DbMemory {
	/**
	 * key:创建者ID,即裁判的OPENID.故,一个人只能创建一个抢答房间
	 */
	public static ConcurrentHashMap<String, QdRoom> qdRooms = new ConcurrentHashMap<>(10);
	/**
	 * 信道ID和抢答房间ID的关联关系
	 */
	public static ConcurrentHashMap<String, String> tunnelIdAndQdRoomId = new ConcurrentHashMap<>(20);
	
	public static ConcurrentHashMap<String, HashSet<String>> qdRoomIdAndOpenIds = new ConcurrentHashMap<>(100);
}
