package com.qcloud.weapp.demo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;

@WebServlet("/getQdRoomByRoomId")
public class GetQdRoomByRoomIdServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7638537949508122336L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 调用检查登录接口，成功后可以获得用户信息，进行正常的业务请求
			String roomId = request.getParameter("roomId");
			QdRoom qdRoom = DbMemory.qdRooms.get(roomId);
			JSONObject result = new JSONObject();
			if (qdRoom != null) {
				// 获取会话成功，输出获得的用户信息
				JSONObject data = new JSONObject();
				result.put("code", 0);
				result.put("message", "OK");
				data.put("roomName", qdRoom.getRoomName());
				data.put("roomId", qdRoom.getRoomId());
				data.put("playerNum", qdRoom.getPlayNum());
				data.put("isStart", qdRoom.isStart());
				data.put("refereeOpenId", qdRoom.getRefereeOpenId());
				result.put("data",data);
				System.out.println("RefereeOpenId:" + qdRoom.getRefereeOpenId());
			}else{
				result.put("code", -1);
				result.put("message", "获取抢答房间信息失败...");
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
