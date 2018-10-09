package com.qcloud.weapp.demo.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.authorization.LoginService;
import com.qcloud.weapp.authorization.LoginServiceException;
import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.demo.ChatTunnelHandler;
import com.qcloud.weapp.demo.QdTunnelHandler;
import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelService;

@WebServlet("/qdTunnel")
public class QdTunnelServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 912419269573764954L;

	/**
	 * 把所有的请求交给 SDK 处理，提供 TunnelHandler 处理信道事件
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 创建信道服务处理信道相关请求
		TunnelService tunnelService = new TunnelService(request, response);
		/*
		LoginService loginService = new LoginService(request, response);
		UserInfo userInfo = null;
		try {
			userInfo = loginService.check();
		} catch (LoginServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (userInfo == null) {
			JSONObject result = new JSONObject();
			try {
				result.put("code", 109032);
				result.put("message", "userinfo is null");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 进入房间的验证码不正确
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result.toString());
			return;
		}
		*/
		String roomId = request.getParameter("roomId");
//		String memoName = request.getParameter("memoName");
//		if (userInfo.getOpenId().equals(roomId)) {
//			memoName = userInfo.getNickName() + "(裁判)";
//		} else {
//			memoName = userInfo.getNickName() + "(选手)";
//		}
//		int validCode = qdRoom.getValidNo();
//		if (validCode != -1) {
//			String playerValidCodeStr = request.getParameter("validCode");
//			int playerValidCode = Integer.valueOf(playerValidCodeStr);
//			if (playerValidCode != validCode && !qdRoom.getRefereeOpenId().equals(userInfo.getOpenId())) {
//				JSONObject result = new JSONObject();
//				try {
//					result.put("code", 109031);
//					result.put("message", "validCode error");
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} // 进入房间的验证码不正确
//				response.setContentType("application/json");
//				response.setCharacterEncoding("utf-8");
//				response.getWriter().write(result.toString());
//				return;
//			}
//		}
		try {
			// 配置是可选的，配置 CheckLogin 为 true 的话，会在隧道建立之前获取用户信息，以便业务将隧道和用户关联起来
			TunnelHandleOptions options = new TunnelHandleOptions();
			options.setCheckLogin(true);

			// 需要实现信道处理器，ChatTunnelHandler 是一个实现的范例
			tunnelService.handle(new QdTunnelHandler(roomId, ""), options);
//			tunnelService.handle(new ChatTunnelHandler(), options);
		} catch (ConfigurationException e) {
			// logger.error("create tunnel error", e);
			e.printStackTrace();
		}
	}
}
