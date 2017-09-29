package com.thinkgem.jeesite.modules.platform.socket.bitmex;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.modules.platform.task.MexTask;

public class MexMoniterTask extends TimerTask {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private long startTime = System.currentTimeMillis();
	private int checkTime = 15000;
	private MexTask client = null;

	public MexMoniterTask(MexTask client) {
		log.info("MexMoniterTask TimerTask is starting...");
		this.client = client;
	}
	
	public void updateTime() {
		log.debug("mex startTime is update");
		startTime = System.currentTimeMillis();
	}

	
	@Override
	public void run() {
		log.debug(">>>  mex MexMoniterTask running....... ");
		if (System.currentTimeMillis() - startTime > checkTime) {
			log.info("mex Moniter reconnect...");
			client.reconnect();
		}
		client.sentPing();
		log.debug("mex Moniter ping data sent...");
	}

}
