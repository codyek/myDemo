package com.thinkgem.jeesite.modules.platform.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.MexHMACSHA256;
import com.thinkgem.jeesite.common.utils.RestClient;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;
import com.thinkgem.jeesite.modules.platform.service.apiauthor.APIAuthorizeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 
* @ClassName: MexBaseService 
* @author EK huangzf 
*
 */
@Transactional(readOnly = true)
public abstract class MexBaseService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	// bitmex平台
	private static final String PLATFORM = "bitmex";
	
	/** Parameter Error 参数错误  */
	protected static final String CODE_400 = "400";
	/** Unauthorized 授权失败  */
	protected static final String CODE_401 = "401";
	/** Not Found */
	protected static final String CODE_404 = "404";
	/** Service Unavailable */
	protected static final String CODE_503 = "503";
	
	
	@Autowired
	private APIAuthorizeService apiService;
	
	/**
	 * 执行请求
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param isAuth
	 *            是否需要appkey授权
	 * @param param
	 *            要提交的数据
	 * @return 结果对象
	 * @throws Exception
	 */
	protected String exchange(String url, HttpMethod method,
			boolean isAuth, JSONObject param) throws Exception {
		String result = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			if(isAuth){
				String nonce = String.valueOf(System.currentTimeMillis());
				// 当前用户
				User user = UserUtils.getUser();
				APIAuthorize autor = new APIAuthorize();
				autor.setPlatform(PLATFORM);
				autor.setUser(user);
				APIAuthorize api = apiService.getByUserId(autor);
				String appkey = api.getAppkey();
				String secretkey = api.getSecretkey();
				String sign = null;
				if("GET".equals(method.name())){
					// GET 方法再URL后补充参数
					String pUrl = convertJson2GetParamsUrlEncode(param);
					url += "?"+pUrl;
					param.clear();
					sign = getSign(method.name(),url,nonce,"",secretkey);
				}else{
					String pUrl = convertJson2GetParamsUrlEncode(param);
					url += "?"+pUrl;
					sign = getSign(method.name(),url,nonce,param.toString(),secretkey);
				}
				
				headers.set("api-nonce", nonce);
				headers.set("api-key", appkey);
				headers.set("api-signature", sign);
			}
			
			HttpEntity<Object> entity = new HttpEntity<Object>(param, headers);
			RestTemplate rest = RestClient.getClient();
			// 完整路径
			String allUrl = BitMexInterConstants.BITMEX_PREX_URL + url;
			
			logger.info(">> allUrl="+allUrl+", method="+method.name()+", param="+param);
			HttpEntity<String> response = rest.exchange(allUrl, method, entity, String.class);
			result = response.getBody();
		} catch (HttpClientErrorException e) {
			String status = e.getStatusCode()+"";
			logger.error(">> post response status = "+status);
			if(CODE_400.equals(status)){
				// Parameter Error 参数错误
				throw new ServiceException("400 Parameter Error 参数错误!");
			} else if(CODE_401.equals(status)){
				// Unauthorized 授权失败
				throw new ServiceException("401 Unauthorized 授权失败!");
			} else if(CODE_404.equals(status)){
				// Not Found 
				throw new Exception(e);
			} else if(CODE_503.equals(status)){
				// Service Unavailable
				throw new ServiceException("503 Service Unavailable");
			}else{
				
			}
			result = e.getResponseBodyAsString();
		} catch (Exception e) {
			throw new Exception(e);
		}
		logger.info(">> post response result = "+result);
		return result;
	}
	
	/**
	* @Title: 获取签名
	* @param @param verb
	* @param @param path
	* @param @param nonce
	* @param @param param
	* @param @param apiSecret
	* @return String
	* @throws
	 */
	private String getSign(String verb, String path, String nonce, String param, String apiSecret){
		String content = verb + path + nonce + param;
		logger.debug(">> sign content = "+content);
		String signature = MexHMACSHA256.HMACSHA256(content.getBytes(), apiSecret.getBytes());
		logger.debug(">> signature = "+signature);
		return signature;
	}
	
	private String convertJson2GetParams(JSONObject params){
		StringBuffer urlParams = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		if(keys.isEmpty()){
			return null;
		}
		
		int keySize = keys.size();
		
		for(int i=0;i<keySize;i++){
			if(i > 0){
				urlParams.append("&");
			}
			String key = keys.get(i);
			Object value = params.get(key);
			urlParams.append(key).append("=").append(value);
		}
		return urlParams.toString();
	}
	
	private String convertJson2GetParamsUrlEncode(JSONObject params) throws UnsupportedEncodingException{
		StringBuffer urlParams = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		if(keys.isEmpty()){
			return null;
		}
		
		int keySize = keys.size();
		
		for(int i=0;i<keySize;i++){
			if(i > 0){
				urlParams.append("&");
			}
			String key = keys.get(i);
			String value = params.get(key)+"";
			urlParams.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
		}
		return urlParams.toString();
	}
}
