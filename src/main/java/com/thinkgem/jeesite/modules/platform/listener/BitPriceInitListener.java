package com.thinkgem.jeesite.modules.platform.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thinkgem.jeesite.modules.platform.task.MexTask;
import com.thinkgem.jeesite.modules.platform.task.OkexTask;

@Component
public class BitPriceInitListener implements ApplicationListener{

	@Autowired
    @Qualifier("okexTask") 
    private OkexTask okexTask;
	
	@Autowired
    @Qualifier("mexTask") 
    private MexTask mexTask;
	
	private static boolean isStart = false;

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		if (!isStart) {// 这个可以解决项目启动加载两次的问题
			isStart = true;
			//okexTask.okexWebsocketTask();
			
			//mexTask.mexWebsocketTask();
		}
	}

}
