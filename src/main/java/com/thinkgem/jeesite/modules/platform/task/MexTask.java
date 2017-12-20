package com.thinkgem.jeesite.modules.platform.task;

import java.net.URI;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.MexMoniterTask;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.WebsocketClientEndpoint;

@Service("mexTask")
public class MexTask {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String URL = "wss://www.bitmex.com/realtime";
	
	private WebsocketClientEndpoint cep = null;
	private Timer timerTask = null;
	private MexMoniterTask mexMoniterTask = null;
	
	@Async
	public void mexWebsocketTask(){
		try {
			Thread.sleep(2000);  // 休眠2秒
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info(">>>>>>>>>>>>>>>>   mexWebsocketTask start! <<<<<<<<<<<<<<<<  ");
		
		try {
			mexMoniterTask = new MexMoniterTask(this);
			timerTask = new Timer();
			timerTask.schedule(mexMoniterTask, 1000, 4800); // bitmex API 设置一个 5 秒钟的ping定时器 
			connect();
			// add listener
			listenerHandler(cep);
						
		} catch (Exception e) {
			log.error(">> mexMoniterTask　exception : ",e);
		}
	}
	
	public void listenerHandler(WebsocketClientEndpoint cep){
		if(null != cep){
			cep.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					//log.info(">>> lll --- message=");
					if(StringUtils.isNotBlank(message)){
						// 响应  更新监控时间
						mexMoniterTask.updateTime();
						EhCacheUtils.put(Constants.PRICE_CACHE,Constants.SYMBOL_MEX_TIME, System.currentTimeMillis());
						if(message.contains("pong")){
						}else{
							JSONObject json = JSONObject.parseObject(message);
							if(null != json && !json.isEmpty() && json.containsKey("data")){
								JSONArray jArr = json.getJSONArray("data");
								JSONObject data = jArr.getJSONObject(0);
								String symbol = data.getString("symbol");
								if(data.containsKey("lastPrice")){
									double price = data.getDoubleValue("lastPrice");
									String key = "";
									if(Constants.SYMBOL_XBTUSD.equals(symbol)){
										key = Constants.CACHE_XBTUSDMEX_PRICE_KEY;
									}else if(Constants.SYMBOL_XBTQAE.equals(symbol)){
										key = Constants.CACHE_XBTQAEMEX_PRICE_KEY;
									}else if(Constants.SYMBOL_LTCQAE.equals(symbol)){
										key = Constants.CACHE_LTCQAEMEX_PRICE_KEY;
									}
									// 实时价格写入缓存
									EhCacheUtils.put(Constants.PRICE_CACHE,key, price);
								}
							}
						}
					}
				}
			});
		}
	}
	
	/**
	 * 发送心跳检查
	* @Title: sentPing
	* @param 
	* @return void
	* @throws
	 */
	public void sentPing() {
		if(null != cep){
			String dataMsg = "ping";
			cep.sendMessage(dataMsg);
		}
	}
	
	/**
	 *  链接
	* @Title: connect
	* @param @throws URISyntaxException
	* @return void
	* @throws
	 */
	public void connect() throws Exception{
		// 订阅 XBTUSD,XBTU17，LTCU17 实时价格
		String msg = "{\"op\":\"subscribe\",\"args\":[\"instrument:XBTUSD\"," +
				"\"instrument:XBTH18\"," +
				"\"instrument:LTCH18\"]}";
		// open websocket
		if(null == cep){
			cep = new WebsocketClientEndpoint(new URI(URL));
		}
		if(null != cep){
			cep.sendMessage(msg);
		}
	}
	
	public void reconnect(){
		cep = null;
		try {
			connect();
			listenerHandler(cep);
		} catch (Exception e) {
			log.error(">> reconnect error=",e);
			e.printStackTrace();
		}
	}
}
