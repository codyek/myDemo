package com.thinkgem.jeesite.modules.platform.socket.okexone;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.modules.platform.task.OkexTaskOne;

public class OkexMoniterTask extends TimerTask {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private long startTime = System.currentTimeMillis();
	private int checkTime = 15000;
	private OkexTaskOne client = null;

	public OkexMoniterTask(OkexTaskOne client) {
		log.info("OkexMoniterTask TimerTask is starting...");
		this.client = client;
	}
	
	public void updateTime() {
		log.debug("okex startTime is update");
		startTime = System.currentTimeMillis();
	}

	
	@Override
	public void run() {
		log.debug(">>>  okex MexMoniterTask running....... ");
		if (System.currentTimeMillis() - startTime > checkTime) {
			log.info("okex Moniter reconnect...");
			client.reconnect();
		}
		client.sentPing();
		log.debug("okex Moniter ping data sent...");
	}

}
