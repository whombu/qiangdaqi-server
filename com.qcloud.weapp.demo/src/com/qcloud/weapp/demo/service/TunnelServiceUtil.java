package com.qcloud.weapp.demo.service;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.demo.bean.PlayerInfo;
import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;
import com.qcloud.weapp.tunnel.EmitError;
import com.qcloud.weapp.tunnel.EmitResult;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelInvalidInfo;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelRoom;

public class TunnelServiceUtil {
	public static synchronized void onTunnelRequest(String qdRoomId, Tunnel tunnel, UserInfo userInfo) {
		System.out.println(tunnel.getTunnelId() + " server request");
		if (userInfo != null) {
			// 抢答比赛还未开始
			QdRoom qdRoom = DbMemory.qdRooms.get(qdRoomId);
			PlayerInfo playerInfo = new PlayerInfo();
			playerInfo.setMemoName("");
			playerInfo.setUserInfo(userInfo);
			if (qdRoom.getRefereeOpenId().equals(userInfo.getOpenId())) {
				playerInfo.setTheReferee(true);
			} else {
				playerInfo.setTheReferee(false);
			}
			// 比赛已经开始,不允许新用户进入,只允许断线用户重新进入
			qdRoom.addPlayer(tunnel.getTunnelId(), playerInfo);
			DbMemory.qdRooms.put(qdRoomId, qdRoom);
			DbMemory.tunnelIdAndQdRoomId.put(tunnel.getTunnelId(), qdRoomId);

		}
		System.out.println(tunnel.getTunnelId() + " server request end");
	}

	/**
	 * 当客户端与信道服务连接成功后调用此接口</br>
	 */
	public static synchronized void onTunnelConnect(Tunnel tunnel) {
		String qdRoomId = DbMemory.tunnelIdAndQdRoomId.get(tunnel.getTunnelId());
		QdRoom qdRoom = DbMemory.qdRooms.get(qdRoomId);
		System.out.println("get qdRoom end,qdRoomId:" + qdRoomId + "--roomName:" + qdRoom.getRoomName());
		ConcurrentHashMap<String, PlayerInfo> userMap = qdRoom.getUserMap();
		if (userMap.containsKey(tunnel.getTunnelId())) {
			System.out.println(userMap.get(tunnel.getTunnelId()).getUserInfo().getNickName() + " server connect");
			qdRoom.getTunnelRoom().addTunnel(tunnel);
			DbMemory.qdRooms.put(qdRoomId, qdRoom);
			System.out.println("update qdRoom end");
			PlayerInfo playerInfo = userMap.get(tunnel.getTunnelId());
			String refereeOpenId = qdRoom.getRefereeOpenId();
			String userOpenId = playerInfo.getUserInfo().getOpenId();
			TunnelRoom room = qdRoom.getTunnelRoom();
			if (qdRoom.isStart()) {
				// 抢答比赛已经开始,只允许抢答比赛开始时的选手断开后重连;</br>
				// 不允许其他选手进入</br>
			} else {
				// 允许所有选手随意进出
			}
			JSONObject peopleMessage = new JSONObject();
			try {
				int total = room.getTunnelCount();
				System.out.println("getRefereeOpenId:" + qdRoom.getRefereeOpenId());
				if (userMap.containsKey(qdRoom.getRefereeOpenId())) {
					total = total - 1;
				}
				System.out.println("total:" + total);
				peopleMessage.put("total", total);
				peopleMessage.put("enter", new JSONObject(playerInfo.getUserInfo()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			broadcast(qdRoom, "people", peopleMessage);// 广播是谁进来了,是裁判还是参赛队员
			System.out.println(userMap.get(tunnel.getTunnelId()).getUserInfo().getNickName() + " server connect end");
		} else {
			System.out.println("usermap contains error");
			closeTunnel(tunnel);
		}
	}

	public static synchronized void onTunnelMessage(Tunnel tunnel, TunnelMessage message) {
		System.out.println("message:" + message.getType());
		String qdRoomId = DbMemory.tunnelIdAndQdRoomId.get(tunnel.getTunnelId());
		QdRoom qdRoom = DbMemory.qdRooms.get(qdRoomId);
		System.out.println("get qdRoom end,qdRoomId:" + qdRoomId + "--roomName:" + qdRoom.getRoomName());
		ConcurrentHashMap<String, PlayerInfo> userMap = qdRoom.getUserMap();
		if (userMap.containsKey(tunnel.getTunnelId())) {
			System.out.println(userMap.get(tunnel.getTunnelId()).getUserInfo().getNickName() + " server Message");
			// 消息类型为【开始】、【结束】、【裁判发命令开始抢】、【裁判发命令停止抢】、【选手抢】
			// 这里存在并发的问题，需要加锁处理。同一个房间一个锁
			PlayerInfo playerInfo = userMap.get(tunnel.getTunnelId());
			String refereeOpenId = qdRoom.getRefereeOpenId();
			String userOpenId = playerInfo.getUserInfo().getOpenId();
			// 当消息是裁判发的
			JSONObject speakMessage = new JSONObject();
			if (refereeOpenId.equals(userOpenId)) {
				// 当消息为开始比赛时,不允许有新的参赛者进入
				// 比赛开始之前,裁判和参赛者都可以随意进出;比赛开始后,裁判不能断开连接,否则强制终止比赛;
				// 比赛开始后,参赛者可以随意进出,会一直保留位置
				try {
					if (message.getType().equals("startGame") && !qdRoom.isAnswered()) {
						qdRoom.setStart(true);
						speakMessage.put("word", "startGame");
						DbMemory.qdRooms.put(qdRoomId, qdRoom);
					} else if (message.getType().equals("endGame")) {
						// 提示后,关闭所有信道
						qdRoom.setStart(false);
						speakMessage.put("word", "endGame");
					} else if (message.getType().equals("begin")) {
						// 开始抢答,参赛者可以点击抢答按钮
						qdRoom.setAnswered(false);
						speakMessage.put("word", "begin");
					} else if (message.getType().equals("pause")) {
						// 暂停抢答,所有参赛者不能点击抢答按钮
						speakMessage.put("word", "pause");
					} else {
						// 错误消息
					}
					speakMessage.put("who", new JSONObject(playerInfo.getUserInfo()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				broadcast(qdRoom, "speak", speakMessage);
			} else if (qdRoom.isStart() && !qdRoom.isAnswered()) {// 普通参赛者的抢答消息
				if (message.getType().equals("qiang")) {
					// 发送抢答指令,服务器认为收到的第一个抢答指令的参赛者为获胜者
					try {
						// JSONObject messageContent = (JSONObject)
						// message.getContent();
						speakMessage.put("word", "qiang");
						speakMessage.put("who", new JSONObject(playerInfo.getUserInfo()));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					broadcast(qdRoom, "speak", speakMessage);
					qdRoom.setAnswered(true);
				} else {
					// 错误信息
				}
			} else {
				// 普通参赛者在比赛未开始前,不能发送任何消息
			}
			System.out.println(userMap.get(tunnel.getTunnelId()).getUserInfo().getNickName() + " server Message end");
		} else {
			System.out.println("usermap contains error");
			closeTunnel(tunnel);
		}
	}

	public static synchronized void onTunnelClose(Tunnel tunnel) {
		// TODO Auto-generated method stub
		PlayerInfo leaveUser = null;
		String qdRoomId = DbMemory.tunnelIdAndQdRoomId.get(tunnel.getTunnelId());
		QdRoom qdRoom = DbMemory.qdRooms.get(qdRoomId);
		ConcurrentHashMap<String, PlayerInfo> userMap = qdRoom.getUserMap();
		if (userMap.containsKey(tunnel.getTunnelId())) {
			leaveUser = userMap.get(tunnel.getTunnelId());
			System.out.println(leaveUser.getUserInfo().getNickName() + " server close");
			qdRoom.removePlayer(tunnel.getTunnelId());
		}
		qdRoom.getTunnelRoom().removeTunnel(tunnel);
		DbMemory.tunnelIdAndQdRoomId.remove(tunnel.getTunnelId());
		DbMemory.qdRooms.put(qdRoomId, qdRoom);
		JSONObject peopleMessage = new JSONObject();
		try {
			int total = qdRoom.getTunnelRoom().getTunnelCount();
			if (userMap.containsKey(qdRoom.getRefereeOpenId())) {
				total = total - 1;
			}
			peopleMessage.put("total", total);
			peopleMessage.put("leave", new JSONObject(leaveUser.getUserInfo()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		broadcast(qdRoom, "people", peopleMessage);
		System.out.println(leaveUser.getUserInfo().getNickName() + " server close end");
	}

	/**
	 * 关闭指定的信道
	 */
	private static synchronized void closeTunnel(Tunnel tunnel) {
		try {
			tunnel.close();
		} catch (EmitError e) {
			e.printStackTrace();
		}
	}

	/**
	 * 广播消息到房间里所有的信道
	 */
	private static synchronized void broadcast(QdRoom qdRoom, String messageType, JSONObject messageContent) {
		try {
			System.out.println("tunnel size:" + qdRoom.getTunnelRoom().getTunnelCount());
			EmitResult result = qdRoom.getTunnelRoom().broadcast(messageType, messageContent);
			// 广播后发现的无效信道进行清理
			System.out.println("invalid:" + result.getTunnelInvalidInfos().size());
			for (TunnelInvalidInfo invalidInfo : result.getTunnelInvalidInfos()) {
				onTunnelClose(Tunnel.getById(invalidInfo.getTunnelId()));
			}
		} catch (EmitError e) {
			// 如果消息发送发生异常，这里可以进行错误处理或者重试的逻辑
			e.printStackTrace();
		}
	}
}
