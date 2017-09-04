package com.thinkgem.jeesite.modules.platform.socket.bitmex;

import java.net.URISyntaxException;
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
		log.debug("MexMoniterTask TimerTask is starting.... ");
		this.client = client;
	}
	
	public void updateTime() {
		log.debug("startTime is update");
		startTime = System.currentTimeMillis();
	}

	
	@Override
	public void run() {
		if (System.currentTimeMillis() - startTime > checkTime) {
			log.debug("Moniter reconnect....... ");
			try {
				client.connect();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		client.sentPing();
		log.debug("Moniter ping data sent.... ");
	}

}
