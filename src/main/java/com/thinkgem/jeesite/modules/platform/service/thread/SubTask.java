package com.thinkgem.jeesite.modules.platform.service.thread;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubTask extends TimerTask{

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private NewAutoMainThread thread = null;
	
	public SubTask(NewAutoMainThread thread){
		this.thread = thread;
	}
	
	@Override
	public void run() {
		if(null != thread){
			try {
				//thread.AutoTask();
			} catch (Exception e) {
				log.error(">> SubTask error:",e);
			}
		}
	}

}
