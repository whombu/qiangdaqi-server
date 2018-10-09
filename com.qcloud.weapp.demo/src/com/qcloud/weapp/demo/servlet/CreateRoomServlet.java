/**
 * 
 */
package com.qcloud.weapp.demo.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

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
import com.qcloud.weapp.demo.bean.PlayerInfo;
import com.qcloud.weapp.demo.bean.QdRoom;
import com.qcloud.weapp.demo.demodb.DbMemory;

/**
 * @author 2930
 *
 */
@WebServlet("/createRoom")
public class CreateRoomServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -900902061878249469L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String roomName = URLDecoder.decode(request.getParameter("roomName"),"UTF-8");
//		String playerNum = request.getParameter("playerNum");
//		String validNo = request.getParameter("validNo");
		LoginService service = new LoginService(request, response);		
		try {
			// 调用检查登录接口，成功后可以获得用户信息，进行正常的业务请求
			UserInfo userInfo = service.check();
			QdRoom qdRoom = new QdRoom();
			//qdRoom.setPlayNum(Integer.valueOf(playerNum));
			qdRoom.setRoomName(roomName);
			//qdRoom.setValidNo(Integer.valueOf(validNo));
//			qdRoom.setRoomId(UUID.randomUUID().toString());
			qdRoom.setRoomId(userInfo.getOpenId());
			qdRoom.setRefereeOpenId(userInfo.getOpenId());
			DbMemory.qdRooms.put(userInfo.getOpenId(), qdRoom);
			// 获取会话成功，输出获得的用户信息			
			JSONObject result = new JSONObject();
			result.put("code", 0);
			result.put("message", "OK");
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result.toString());
			
		} catch (LoginServiceException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ConfigurationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
