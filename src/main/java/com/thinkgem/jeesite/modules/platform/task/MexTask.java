package com.thinkgem.jeesite.modules.platform.task;

import java.net.URI;
import java.net.URISyntaxException;
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
		log.info(">>>>>>>>>>>>>>>>   mexWebsocketTask start! <<<<<<<<<<<<<<<<  ");
		
		try {
			mexMoniterTask = new MexMoniterTask(this);
			connect();
			timerTask = new Timer();
			timerTask.schedule(mexMoniterTask, 1000, 9000);
			// add listener
			cep.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					if(StringUtils.isNotBlank(message)){
						if(message.contains("pong")){
							// pong 响应  更新监控时间
							mexMoniterTask.updateTime();
						}else{
							JSONObject json = JSONObject.parseObject(message);
							if(null != json && !json.isEmpty() && json.containsKey("data")){
								JSONArray jArr = json.getJSONArray("data");
								JSONObject data = jArr.getJSONObject(0);
								String symbol = data.getString("symbol");
								if(data.containsKey("lastPrice")){
									
									double price = data.getDoubleValue("lastPrice");
									
									if("XBTUSD".equals(symbol)){
										// 写入缓存
										EhCacheUtils.put(Constants.PRICE_CACHE,Constants.CACHE_XBTUSDMEX_PRICE_KEY, data);
									}else if("XBTU17".equals(symbol)){
										// 写入缓存
										EhCacheUtils.put(Constants.PRICE_CACHE,Constants.CACHE_XBTU17MEX_PRICE_KEY, data);
									}else if("LTCU17".equals(symbol)){
										log.debug(" LTCU17 price = " + price);
										// 写入缓存
										EhCacheUtils.put(Constants.PRICE_CACHE,Constants.CACHE_LTCU17MEX_PRICE_KEY, data);
									}
								}
							}
						}
					}
				}
			});
						
		} catch (Exception e) {
			log.error(">>　exception : ",e);
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
		String dataMsg = "ping";
		cep.sendMessage(dataMsg);
	}
	
	/**
	 *  链接
	* @Title: connect
	* @param @throws URISyntaxException
	* @return void
	* @throws
	 */
	public void connect() throws URISyntaxException{
		// 订阅 XBTUSD,XBTU17，LTCU17 实时价格
		String msg = "{\"op\":\"subscribe\",\"args\":[\"instrument:XBTUSD\"," +
				"\"instrument:XBTU17\"," +
				"\"instrument:LTCU17\"]}";
		// open websocket
		cep = new WebsocketClientEndpoint(new URI(URL));
		
		cep.sendMessage(msg);
	}
}
