package com.thinkgem.jeesite.modules.platform.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.platform.socket.WebSocketService;
import com.thinkgem.jeesite.modules.platform.socket.WebSoketClient;
import com.thinkgem.jeesite.modules.platform.socket.impl.WebSocketServiceImpl;

@Service("okexTask")
public class OkexTask {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	* @Title: okexWebsocketTask 获取比特币,ltc实时价格
	* @param @return
	* @return String
	* @throws
	 */
	@Async
	public void okexWebsocketTask(){
		log.info(">>>>>>>>>>   okexWebsocketTask start!!  <<<<<<<<<<<<<<<<<<<<<");
		// 订阅消息处理类,用于处理WebSocket服务返回的消息
		WebSocketService service = new WebSocketServiceImpl();
		// WebSocket客户端
		WebSoketClient client = new WebSoketClient(WebSocketServiceImpl.SOCKET_URL, service);
		// 启动客户端
		client.start();
		// 订阅实时价格和指数 渠道
		String channel = "[{'event':'addChannel','channel':'"+WebSocketServiceImpl.BTC_QUARTER+"'},"
				        + "{'event':'addChannel','channel':'"+WebSocketServiceImpl.LTC_QUARTER+"'}]";
		// 添加订阅
		client.addChannel(channel);
	}
	
	public static void main(String[] args) {
		OkexTask task = new OkexTask();
		System.out.println("111111111");
		task.okexWebsocketTask();
	}
}
