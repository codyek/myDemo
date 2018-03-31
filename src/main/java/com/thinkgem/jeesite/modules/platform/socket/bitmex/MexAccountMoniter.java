package com.thinkgem.jeesite.modules.platform.socket.bitmex;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.modules.platform.task.MexAccountSocket;


public class MexAccountMoniter extends TimerTask {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private long startTime = System.currentTimeMillis();
	private int checkTime = 15000;
	private MexAccountSocket client = null;

	public MexAccountMoniter(MexAccountSocket client) {
		log.info("MexMoniterAccount TimerTask is starting...");
		this.client = client;
	}
	
	public void updateTime() {
		log.debug("mexAccount startTime is update");
		startTime = System.currentTimeMillis();
	}

	
	@Override
	public void run() {
		log.debug(">>>  mex MexMoniterAccount running....... ");
		if (System.currentTimeMillis() - startTime > checkTime) {
			log.info("mex AccountMoniter reconnect...");
			client.reconnect();
		}
		client.sentPing();
		log.debug("mex MoniterAccount ping data sent...");
	}

}
