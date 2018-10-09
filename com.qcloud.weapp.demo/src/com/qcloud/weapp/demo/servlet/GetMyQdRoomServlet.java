package com.qcloud.weapp.demo.servlet;

import java.io.IOException;

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
import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;

@WebServlet("/getMyQdRoom")
public class GetMyQdRoomServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1538373329603301646L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LoginService service = new LoginService(request, response);
		try {
			// 调用检查登录接口，成功后可以获得用户信息，进行正常的业务请求
			UserInfo userInfo = service.check();
			QdRoom qdRoom = DbMemory.qdRooms.get(userInfo.getOpenId());
			JSONObject result = new JSONObject();
			if (qdRoom != null) {
				// 获取会话成功，输出获得的用户信息
				JSONObject data = new JSONObject();
				result.put("code", 0);
				result.put("message", "OK");
				data.put("roomName", qdRoom.getRoomName());
				data.put("roomId", qdRoom.getRoomId());
				data.put("playerNum", qdRoom.getPlayNum());
				result.put("data",data);
			}else{
				result.put("code", -1);
				result.put("message", "获取抢答房间信息失败...");
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result.toString());

		} catch (LoginServiceException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
