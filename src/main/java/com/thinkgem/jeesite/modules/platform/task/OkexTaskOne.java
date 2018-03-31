package com.thinkgem.jeesite.modules.platform.task;

import java.net.URI;
import java.util.Timer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.socket.okexone.OkexMoniterTask;
import com.thinkgem.jeesite.modules.platform.socket.okexone.OkexWebsocketClientEndpoint;

@Service("okexTaskOne")
public class OkexTaskOne {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String URL = "wss://real.okex.com:10440/websocket/okexapi";
	
	public static final String BTC_QUARTER = "ok_sub_futureusd_btc_ticker_quarter";
	public static final String BTC_INDEX = "ok_sub_futureusd_btc_index";
	public static final String BTC_DEPTH20 = "ok_sub_futureusd_btc_depth_quarter_20";
	
	// 次周
	public static final String BTC_NEXT_WEEK = "ok_sub_futureusd_btc_ticker_next_week";
	public static final String BTC_NEXT_WEEK_DEPTH20 = "ok_sub_futureusd_btc_depth_next_week_20";
	
	public static final String LTC_QUARTER = "ok_sub_futureusd_ltc_ticker_quarter";
	public static final String LTC_INDEX = "ok_sub_futureusd_ltc_index";
	public static final String LTC_DEPTH20 = "ok_sub_futureusd_ltc_depth_quarter_20";
	
	private OkexWebsocketClientEndpoint cep = null;
	private Timer timerTask = null;
	private OkexMoniterTask okexMoniterTask = null;
	
	@Async
	public void okexWebsocketTask(){
		log.info(">>>>>>>>>>>>>>>>   mexWebsocketTask start! <<<<<<<<<<<<<<<<  ");
		
		try {
			okexMoniterTask = new OkexMoniterTask(this);
			timerTask = new Timer();
			timerTask.schedule(okexMoniterTask, 1000, 4800); // bitmex API 设置一个 5 秒钟的ping定时器 
			connect();
			// add listener
			listenerHandler(cep);
						
		} catch (Exception e) {
			log.error(">> okexMoniterTask　exception : ",e);
		}
	}
	
	public void listenerHandler(OkexWebsocketClientEndpoint cep){
		if(null != cep){
			cep.addMessageHandler(new OkexWebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					//log.info(">>> lll --- message="+message);
					try {
						if(StringUtils.isNotBlank(message)){
							//log.info(">>  ok  rrrr - - -11"+message);
							// 响应  更新监控时间
							okexMoniterTask.updateTime();
							EhCacheUtils.put(Constants.PRICE_CACHE,Constants.SYMBOL_OKEX_TIME, System.currentTimeMillis());
							if(message.contains("pong")){
								// pong 响应 不处理
							}else{
								// 获取实时价格 instrument 放入缓存
								JSONArray jArr = JSONArray.parseArray(message);
								if(null != jArr && !jArr.isEmpty()){
									JSONObject json = jArr.getJSONObject(0);
									String channel = json.getString("channel");
									String cacheKey = "";
									if(BTC_QUARTER.equals(channel)){
										// btc
										cacheKey = Constants.CACHE_BTCOKEX_PRICE_KEY;
									}else if(LTC_QUARTER.equals(channel)){
										// ltc
										cacheKey = Constants.CACHE_LTCOKEX_PRICE_KEY;
									}
									// 实时价格
									if(json.containsKey("data")){
										JSONObject data = json.getJSONObject("data");
										if(data.containsKey("last")){
											double price = data.getDoubleValue("last");
											// 实时价格写入缓存
											EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, price);
										}
										
									}
								}
							}
						}
					} catch (Exception e) {
						log.error(">> okexMoniterTask listenerHandler　exception : ",e);
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
		String msg = "[{'event':'addChannel','channel':'ok_sub_futureusd_btc_ticker_quarter'}," +
				"{'event':'addChannel','channel':'ok_sub_futureusd_ltc_ticker_quarter'}," +
				"{'event':'addChannel','channel':'ok_sub_futureusd_btc_depth_quarter_20'}," +
				"{'event':'addChannel','channel':'ok_sub_futureusd_ltc_depth_quarter_20'}," +
				"{'event':'addChannel','channel':'ok_sub_futureusd_btc_ticker_next_week'}," +
				"{'event':'addChannel','channel':'ok_sub_futureusd_btc_depth_next_week_20'}]";
		// open websocket
		if(null == cep){
			cep = new OkexWebsocketClientEndpoint(new URI(URL));
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
		}
	}
}
