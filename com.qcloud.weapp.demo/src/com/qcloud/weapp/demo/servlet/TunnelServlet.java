package com.qcloud.weapp.demo.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelService;
import com.qcloud.weapp.demo.ChatTunnelHandler;

/**
 * 使用 SDK 提供信道服务
 */
@WebServlet("/tunnel")
public class TunnelServlet extends HttpServlet {
	private static final long serialVersionUID = -6490955903032763981L;
	//private static final Logger logger = Logger.getLogger(TunnelServlet.class);
	/**
	 * 把所有的请求交给 SDK 处理，提供 TunnelHandler 处理信道事件
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        // 创建信道服务处理信道相关请求
		TunnelService tunnelService = new TunnelService(request, response);
		System.out.println("request method:" + request.getMethod());
		try {
			// 配置是可选的，配置 CheckLogin 为 true 的话，会在隧道建立之前获取用户信息，以便业务将隧道和用户关联起来
			TunnelHandleOptions options = new TunnelHandleOptions();
			options.setCheckLogin(true);
			
            // 需要实现信道处理器，ChatTunnelHandler 是一个实现的范例
			tunnelService.handle(new ChatTunnelHandler(), options);
		} catch (ConfigurationException e) {
			//logger.error("create tunnel error", e);
			e.printStackTrace();
		}
	}
}
