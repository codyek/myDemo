package com.thinkgem.jeesite.modules.platform.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.BitMD5Util;
import com.thinkgem.jeesite.common.utils.HttpUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.callback.OkexReturnCode;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.service.apiauthor.APIAuthorizeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 
* @ClassName: OkexBaseService 
* @author EK huangzf 
*
 */
@Transactional(readOnly = true)
public abstract class OkexBaseService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	// okex平台
	private static final String PLATFORM = "okex";
	
	@Autowired
	private APIAuthorizeService apiService;
	
	protected String doPost(String url, Map<String,String> params) throws Exception{
		if(null == params){
			params = new HashMap<String, String>();
		}
		// 当前用户
		User user = UserUtils.getUser();
		APIAuthorize autor = new APIAuthorize();
		autor.setPlatform(PLATFORM);
		autor.setUser(user);
		APIAuthorize api = apiService.getByUserId(autor);
		String appkey = api.getAppkey();
		String secretkey = api.getSecretkey();
		params.put("api_key", appkey);
		
		String sign = BitMD5Util.buildOkexsignV1(params, secretkey);
		
		params.put("sign", sign);
		logger.info(">> post url = " + url);
		logger.info(">> post params = " + params);
		String result = HttpUtils.httpsPost(url, new LinkedList<NameValuePair>(), params);
		if(StringUtils.isEmpty(result)){
			logger.error(">> post result is null !!!, url="+url);
			return null;
		}
		logger.info(">> post result = " + result);
		JSONObject json = JSONObject.parseObject(result);
		Object rlt = json.get("result");
		if(null != rlt){
			String str_rlt = String.valueOf(rlt);
			if("false".equals(str_rlt)){
				OkexReturnCode.HOLDER.getErrorMsgByCode(1);
			}
		}
		return result;
	}
}
