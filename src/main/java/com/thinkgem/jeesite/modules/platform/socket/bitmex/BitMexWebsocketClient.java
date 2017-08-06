package com.thinkgem.jeesite.modules.platform.socket.bitmex;

import java.net.URI;

public class BitMexWebsocketClient {

	private static final String URL = "wss://www.bitmex.com/realtime";
	
	public String sendWebsocket(String msg){
		
		try {
			// open websocket
			WebsocketClientEndpoint cep = new WebsocketClientEndpoint(new URI(URL));
			
			// add listener
			cep.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					System.out.println("return =" + message);
					// TODO 
				}
			});
			
			// send message to websocket
			cep.sendMessage(msg);
						
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
