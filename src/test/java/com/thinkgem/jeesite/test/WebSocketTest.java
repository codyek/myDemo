package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.modules.platform.socket.WebSocketService;
import com.thinkgem.jeesite.modules.platform.socket.WebSoketClient;
import com.thinkgem.jeesite.modules.platform.socket.impl.WebSocketServiceImpl;

public class WebSocketTest {
	
	public static void main(String[] args) {
		//String url = "wss://real.okcoin.com:10440/websocket/okcoinapi";
		String url = "wss://real.okex.com:10440/websocket/okexapi";
		//String url = "wss://www.bitmex.com/realtime";

		// 订阅消息处理类,用于处理WebSocket服务返回的消息
		WebSocketService service = new WebSocketServiceImpl();
		// WebSocket客户端
		WebSoketClient client = new WebSoketClient(url, service);
		// 启动客户端
		client.start();

		//String channel = "{'op': 'help'}";
		String channel = "[{'event':'addChannel','channel':'ok_sub_futureusd_btc_ticker_quarter'},"
				+ "{'event':'addChannel','channel':'ok_sub_futureusd_btc_index'},"
				+ "{'event':'addChannel','channel':'ok_sub_futureusd_btc_depth_quarter_20'}]";
		//String channel = "{'event':'addChannel','channel':'ok_sub_futureusd_btc_depth_this_week_20'}";
		//String channel = "ok_sub_futureusd_btc_ticker_quarter";
		
		// 添加订阅
		client.addChannel(channel);
		//channel = "ok_sub_futureusd_btc_index";
		//client.addChannel(channel);
		//channel = "ok_sub_futureusd_btc_depth_quarter_20";
		//client.addChannel(channel);
	}
}

