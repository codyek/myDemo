package com.thinkgem.jeesite.modules.platform.socket;

/**
 * 通过继承WebSocketBase创建WebSocket客户端
 *
 */
public class WebSoketClient extends WebSocketBase{
	
	public WebSoketClient(String url,WebSocketService service){
		super(url,service);
	}
}
