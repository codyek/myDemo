package com.thinkgem.jeesite.modules.platform.socket.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.modules.platform.socket.WebSocketService;

/**
 * 订阅信息处理类需要实现WebSocketService接口
 *
 */
public class WebSocketServiceImpl implements WebSocketService{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void onReceive(String msg){
		
		log.info("WebSocket Client received message: " + msg);
		
	}
}
