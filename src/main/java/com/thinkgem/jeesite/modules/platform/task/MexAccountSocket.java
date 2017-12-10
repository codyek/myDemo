package com.thinkgem.jeesite.modules.platform.task;

import java.net.URI;
import java.util.Timer;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.HedgeUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.service.apiauthor.APIAuthorizeService;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.MexAccountMoniter;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.MexMoniterTask;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.WebsocketClientEndpoint;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

@Service("mexAccountSocket")
public class MexAccountSocket {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String URL = "wss://www.bitmex.com/realtime";
	//private static final String URL = "wss://testnet.bitmex.com/realtime";
	
	private WebsocketClientEndpoint cep = null;
	private Timer timerTask = null;
	private MexAccountMoniter accountMoniter = null;
	
	@Autowired
	private APIAuthorizeService apiService;
	
	
	public void mexAccountsocket(){
		try {
			Thread.sleep(2000);  // 休眠2秒
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info(">>>>>>>>>>>>>>>>   mexAccountsocket start! <<<<<<<<<<<<<<<<  ");
		
		try {
			accountMoniter = new MexAccountMoniter(this);
			timerTask = new Timer();
			timerTask.schedule(accountMoniter, 1000, 4800); // bitmex API 设置一个 5 秒钟的ping定时器 
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
					log.info(">>> MexAccount --- message="+message);
					if(StringUtils.isNotBlank(message)){
						// 响应  更新监控时间
						accountMoniter.updateTime();
						//EhCacheUtils.put(Constants.PRICE_CACHE,Constants.SYMBOL_MEX_TIME, System.currentTimeMillis());
						if(message.contains("pong")){
						}else{
							JSONObject json = JSONObject.parseObject(message);
							if(null != json && !json.isEmpty() && json.containsKey("table") && json.containsKey("data")){
								String table = json.getString("table");
								if("margin".equals(table)){
									// 保证金信息
									JSONArray jArr = json.getJSONArray("data");
									JSONObject data = jArr.getJSONObject(0);
									if(data.containsKey("availableMargin")){
										// 可用余额
										String key = Constants.CACHE_MEX_MARGIN_KEY;
										double price = data.getDoubleValue("availableMargin");
										// 实时价格写入缓存
										EhCacheUtils.put(Constants.PRICE_CACHE,key, price);
									}
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
		// 当前用户
		User user = UserUtils.getUser();
		APIAuthorize autor = new APIAuthorize();
		autor.setPlatform("bitmex");
		autor.setUser(user);
		APIAuthorize api = apiService.getByUserId(autor);
		String appkey = api.getAppkey();
		String secretkey = api.getSecretkey();
		Long nonce = System.currentTimeMillis();
		String signature = HedgeUtils.getMexSocketSign(String.valueOf(nonce), secretkey);
		// 授权订阅  
		// {"op": "authKey", "args": ["<APIKey>", <nonce>, "<signature>"]}
		String authMsg = "{\"op\":\"authKey\",\"args\":[\""+appkey+"\"," +
				nonce + ",\""+signature+"\"]}";
		System.out.println("authMsg = "+authMsg);
		// open websocket
		if(null == cep){
			cep = new WebsocketClientEndpoint(new URI(URL));
		}
		if(null != cep){
			cep.sendMessage(authMsg);
			// {"op": "subscribe", "args": "margin"} 你账户的余额和保证金要求的更新,    "position" 你仓位的更新
			//String msg = "{\"op\":\"subscribe\",\"args\":[\"margin\",\"position\"]}";
			String msg = "{\"op\":\"subscribe\",\"args\":\"margin\"}";
			System.out.println("msg = "+msg);
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
	
	public void onClose(){
		if(null != timerTask){
			System.out.println(" >>>>>>　stooo 11");
			timerTask.cancel();
		}
		if(null != cep){
			String msg = "{\"op\":\"unsubscribe\",\"args\":\"margin\"}";;
			cep.sendMessage(msg);
			System.out.println(" >>>>>>　stooo 1222");
			Session userSession = cep.userSession;
			CloseReason reason = new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY,"close socket");
			cep.onClose(userSession, reason);
			cep = null;
		}
	}
}
