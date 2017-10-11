package com.thinkgem.jeesite.modules.platform.socket.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.socket.WebSocketService;

/**
 * 订阅信息处理类需要实现WebSocketService接口
 *
 */
public class WebSocketServiceImpl implements WebSocketService{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	/** okex wewsocket url  */
	public static final String SOCKET_URL = "wss://real.okex.com:10440/websocket/okexapi";
	
	public static final String BTC_QUARTER = "ok_sub_futureusd_btc_ticker_quarter";
	public static final String BTC_INDEX = "ok_sub_futureusd_btc_index";
	public static final String BTC_DEPTH20 = "ok_sub_futureusd_btc_depth_quarter_20";
	
	public static final String LTC_QUARTER = "ok_sub_futureusd_ltc_ticker_quarter";
	public static final String LTC_INDEX = "ok_sub_futureusd_ltc_index";
	public static final String LTC_DEPTH20 = "ok_sub_futureusd_ltc_depth_quarter_20";
	
	@Override
	public void onReceive(String msg){
		if(StringUtils.isNotBlank(msg)){
			//log.info(">>  ok  rrrr - - -");
			EhCacheUtils.put(Constants.PRICE_CACHE,Constants.SYMBOL_OKEX_TIME, System.currentTimeMillis());
			if(msg.contains("\"event\":\"pong\"")){
				// pong 响应 不处理
			}else{
				// 获取实时价格 instrument 放入缓存
				JSONArray jArr = JSONArray.parseArray(msg);
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
	}

 }
