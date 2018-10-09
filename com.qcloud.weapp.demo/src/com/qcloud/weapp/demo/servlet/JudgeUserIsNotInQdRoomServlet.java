package com.qcloud.weapp.demo.servlet;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.demo.demodb.DbMemory;

@WebServlet("/judgeUserIsNotInQdRoom")
public class JudgeUserIsNotInQdRoomServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1499032172002442192L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 调用检查登录接口，成功后可以获得用户信息，进行正常的业务请求
			String roomId = request.getParameter("roomId");
			String openId = request.getParameter("openId");
			HashSet<String> openIds = DbMemory.qdRoomIdAndOpenIds.get(roomId);
			boolean ret = true;
			if (openIds == null || !openIds.contains(openId)) {
				ret = false;
			}
			JSONObject result = new JSONObject();
			// 获取会话成功，输出获得的用户信息
			JSONObject data = new JSONObject();
			result.put("code", 0);
			result.put("message", "OK");
			data.put("result", ret);
			result.put("data", data);
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result.toString());

		} catch (

		JSONException e)

		{
			e.printStackTrace();
		}
	}
}
