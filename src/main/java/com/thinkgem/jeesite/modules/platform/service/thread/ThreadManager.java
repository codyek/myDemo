package com.thinkgem.jeesite.modules.platform.service.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

	private static final Integer SIZE = 2;
	private static ExecutorService instance = Executors.newFixedThreadPool(SIZE);
	
	public static synchronized ExecutorService getInstance(){
		if(null == instance){
			instance = Executors.newFixedThreadPool(SIZE);
		}
		return instance;
	}
}
