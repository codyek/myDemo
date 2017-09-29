package com.thinkgem.jeesite.modules.platform.socket;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoniterTask extends TimerTask {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private long startTime = System.currentTimeMillis();
	private int checkTime = 15000;
	private WebSocketBase client = null;

	public MoniterTask(WebSocketBase client) {
		log.info("ok TimerTask is starting.... ");
		this.client = client;
	}
	
	public void updateTime() {
		log.debug("ok startTime is update");
		startTime = System.currentTimeMillis();
	}

	
	@Override
	public void run() {
		if (System.currentTimeMillis() - startTime > checkTime) {
			client.setStatus(false);
			log.info(">>okMoniter reconnect....... ");
			client.reConnect();
		}
		client.sentPing();
		log.debug("okMoniter ping data sent.... ");
	}

}
