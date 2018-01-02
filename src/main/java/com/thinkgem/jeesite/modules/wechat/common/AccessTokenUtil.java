package com.thinkgem.jeesite.modules.wechat.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.HttpUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;

public class AccessTokenUtil {
	protected static Logger log = LoggerFactory.getLogger(AccessTokenUtil.class);
	public static String ACTOKEN = null;
	
	/**
	 * 获取公众号token
	 */
	public static void getToken(){
		log.info(">> getToken start...");
		String url = WechatApiContants.GETTOKEN_URL;
		url = url.replace("{APPID}", WechatApiContants.APPID).replace("{APPSECRET}", WechatApiContants.APPSECRET);
		Map<String, String> map = new HashMap<String, String>();
		String ret = HttpUtils.httpsGet(url, map);
		if(StringUtils.isNoneBlank(ret)){
			JSONObject json = JSONObject.parseObject(ret);
			if(null != json && json.containsKey("access_token")){
				ACTOKEN = json.getString("access_token");
				log.info(">> getToken ok!");
			}else{
				log.info(">> getToken is null!");
			}
		}else{
			log.info(">> getToken not return !");
		}
		//System.out.println(">> ret = "+ret);
	}
	
	public static void main(String[] args) {
		System.out.println(">> ret =11 ");
		getToken();
	}
}
