package com.thinkgem.jeesite.modules.platform.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.task.MexTask;
import com.thinkgem.jeesite.modules.platform.task.OkexTask;
import com.thinkgem.jeesite.modules.platform.task.OkexTaskOne;
import com.thinkgem.jeesite.modules.platform.task.RestartMonitor;
import com.thinkgem.jeesite.modules.wechat.common.AccessTokenUtil;
import com.thinkgem.jeesite.modules.wechat.service.SendMessageService;

@Component
public class BitPriceInitListener implements ApplicationListener{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
    @Qualifier("okexTask") 
    private OkexTask okexTask;
	
	/*@Autowired
	@Qualifier("okexTaskOne") 
	private OkexTaskOne okexTaskOne;*/
	
	@Autowired
    @Qualifier("mexTask") 
    private MexTask mexTask;
	
	@Autowired
	@Qualifier("restartMonitor") 
	private RestartMonitor restartMonitor;
	
	@Autowired
    @Qualifier("sendMessageService") 
    private SendMessageService sendMessageService;
	
	private static boolean isStart = false;

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		if (!isStart) {// 这个可以解决项目启动加载两次的问题
			isStart = true;
			okexTask.okexWebsocketTask();
			//okexTaskOne.okexWebsocketTask();
			
			mexTask.mexWebsocketTask();
			
			//检查是否有未关闭的监控，重新启动原未关闭的监控
			//restartMonitor.doRestartMonitor();
		}
	}
	
	// 定时Task 检查价格是否实施更新
	@Scheduled(cron = "0 0/2 * * * ? ") // 2分钟
	public void currencyPriceTask(){
		log.info(">>>>> currencyPriceTask    <<< ");
		
		Long curTime = System.currentTimeMillis() - 2*60*1000; // 2分钟前
		// ok time
		Long okTime = (Long)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.SYMBOL_OKEX_TIME);
		if(null == okTime || okTime < curTime){
			log.error(">> ok price no run! rerunning。");
			okexTask.okexWebsocketTask();
			//okexTaskOne.okexWebsocketTask();
		}
		// Mex time
		Long mexTime = (Long)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.SYMBOL_MEX_TIME);
		if(null == mexTime || mexTime < curTime){
			log.error(">> mex price no run! rerunning。");
			mexTask.mexWebsocketTask();
		}
		
	}

	// 定时Task 发送微信消息
	@Scheduled(cron = "0/30 * * * * ?") //30秒
	//@Scheduled(fixedRate=10000) // 上一次开始执行时间点后30秒再次执行
	public void sendMessage(){
		sendMessageService.taskSendMessages();
	}
	
	// 定时Task 更新accessToken
	@Scheduled(cron = "0 0 * * * ?") // 1小时
	public void updateToken(){
		AccessTokenUtil.getToken();
	}
}
