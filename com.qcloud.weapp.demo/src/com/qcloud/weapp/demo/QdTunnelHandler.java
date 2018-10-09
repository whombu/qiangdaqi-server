package com.qcloud.weapp.demo;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.demo.bean.PlayerInfo;
import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;
import com.qcloud.weapp.demo.service.TunnelServiceUtil;
import com.qcloud.weapp.tunnel.EmitError;
import com.qcloud.weapp.tunnel.EmitResult;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelInvalidInfo;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelRoom;
import com.sun.javafx.collections.MappingChange.Map;

public class QdTunnelHandler implements TunnelHandler {

	private String qdRoomId;

	private String memoName;

	public QdTunnelHandler(String qdRoomId, String memoName) {
		super();
		this.qdRoomId = qdRoomId;
		this.memoName = memoName;
	}

	/**
	 * 当客户端请求信道服务时调用此接口 请求连接,</br>
	 * 包含裁判和普通参赛队员 当有用户请求信道服务时,</br>
	 * 将该用户和信道信息关联起来 </br>
	 * 当房间在线人数达到限制时
	 */
	@Override
	public void onTunnelRequest(Tunnel tunnel, UserInfo userInfo) {
		TunnelServiceUtil.onTunnelRequest(qdRoomId, tunnel, userInfo);
	}

	/**
	 * 当客户端与信道服务连接成功后调用此接口</br>
	 */
	@Override
	public void onTunnelConnect(Tunnel tunnel) {
		TunnelServiceUtil.onTunnelConnect(tunnel);
	}

	@Override
	public void onTunnelMessage(Tunnel tunnel, TunnelMessage message) {
		TunnelServiceUtil.onTunnelMessage(tunnel, message);
	}

	@Override
	public void onTunnelClose(Tunnel tunnel) {
		TunnelServiceUtil.onTunnelClose(tunnel);
	}
}
