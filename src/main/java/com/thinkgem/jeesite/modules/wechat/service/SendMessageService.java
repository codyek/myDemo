package com.thinkgem.jeesite.modules.wechat.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.HttpUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;
import com.thinkgem.jeesite.modules.log.service.BitMonitorLogService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.wechat.common.AccessTokenUtil;
import com.thinkgem.jeesite.modules.wechat.common.WechatApiContants;
import com.thinkgem.jeesite.modules.wechat.common.WechatApiResponseBean;
import com.thinkgem.jeesite.modules.wechat.entity.ResponseTextMessage;

@Service("sendMessageService")
public class SendMessageService {
	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
    private BitMonitorLogService bitMonitorLogService;
	
	
	/**
	* @Title: taskSendMessages 异步查询发送任务记录信息到微信端
	 */
	@Async
	public void taskSendMessages(){
		List<BitMonitorLog> logs = bitMonitorLogService.findAllSendList();
		if(null != logs && !logs.isEmpty()){
			log.info(">> taskSendMessages start ...");
			//获取接收用户的微信OPENID
			String opens = DictUtils.getDictValue(WechatApiContants.WECHAT_CONFIG,WechatApiContants.OPENID,"otQ2LuLL7a4VqKVkc7-6YNWL1zpo");// 从字典获取token
			if(StringUtils.isNotBlank(opens)){
				String[] arrOpenID = opens.split(",");
				for (BitMonitorLog log : logs) {
					boolean flag = false;
					String content = log.getLogContent();
					for (int i = 0; i < arrOpenID.length; i++) {
						String openID = arrOpenID[i];
						ResponseTextMessage msg = new ResponseTextMessage(openID,content);
						flag = sendMessage(msg.toCustJsonString());
						//flag = sendTemplateMessage(msg.toTemplateJsonString());
					}
					if(flag){
						log.setStatusFlag("9");
						bitMonitorLogService.save(log);
					}
				}
			}
		}
		
	}
	
	/**
	* @Title: sendMessage 发送模板消息
	* @param @param postJson
	* @return boolean
	 */
	private boolean sendTemplateMessage(String postJson){
		
		boolean ret = false;
		log.info(">> sendTemplateMessage wechat JSON:"+postJson);
		if(null == AccessTokenUtil.ACTOKEN){
			AccessTokenUtil.getToken();
		}
		String url = WechatApiContants.TEMPLATE_SEND_URL+AccessTokenUtil.ACTOKEN;
		
		String retStr = HttpUtils.httpsPostJson(url, postJson);
		if(StringUtils.isNoneBlank(retStr)){
			Map<String, Object> retMap = JSONObject.parseObject(retStr, Map.class);
			WechatApiResponseBean bean = handleWechatResponse(retMap);
			Long code = bean.getCode();
			if(0 == code.intValue()){
				ret = true;
			}
			//else{
			//	log.error("Wechat error :"+retStr);
			//}
		}
		log.info("Wechat return:"+retStr);
		
		return ret;
	}
	
	/**
	* @Title: sendMessage 发送客服消息
	* @param @param postJson
	* @return boolean
	 */
	private boolean sendMessage(String postJson){
		boolean ret = false;
		log.debug(">> sendMessage wechat JSON:"+postJson);
		if(null == AccessTokenUtil.ACTOKEN){
			AccessTokenUtil.getToken();
		}
		String url = WechatApiContants.CUSTOM_SEND_URL+AccessTokenUtil.ACTOKEN;
		
		String retStr = HttpUtils.httpsPostJson(url, postJson);
		if(StringUtils.isNoneBlank(retStr)){
			Map<String, Object> retMap = JSONObject.parseObject(retStr, Map.class);
			WechatApiResponseBean bean = handleWechatResponse(retMap);
			Long code = bean.getCode();
			if(0 == code.intValue()){
				ret = true;
			}
			else{
				log.error("Wechat error :"+retStr);
			}
		}
		log.debug("Wechat return:"+retStr);
		
		return ret;
	}
	
	/**
	 * 	处理微信返回的消息
	 * @param retMap
	 * @param modelName
	 * @param statusDesc
	 * @param status
	 * @return
	 * @throws RequestWechatException
	 */
	protected WechatApiResponseBean handleWechatResponse(final Map<String, Object> retMap){
		WechatApiResponseBean retBean = new WechatApiResponseBean();
		if (retMap != null) {
			String statusDesc = null;
			try {
				retBean.setReturnBody(retMap);//增加JSON体返回
				if(retMap.get("errcode")!=null){
					String errcode = String.valueOf(retMap.get("errcode"));
					if ("0".equals(errcode)) {
						retBean.setSuccess();
					} else {
						statusDesc = " return error:" + retMap.get("errcode") + "-" + retMap.get("errmsg");
						log.error(statusDesc);
						try{
							retBean.setCode(Long.parseLong(errcode));
						}catch(Exception e){}
						retBean.setErrorMessage(statusDesc);
					}
				}else{//没有错误消息返回时，设置返回消息为成功！
					retBean.setSuccess();
				}
			} catch (Exception e) {
				statusDesc = e.getMessage();
				log.error(e.getMessage(), e);
				retBean.setErrorMessage(statusDesc);
			}
		}
		return retBean;
	}
	
	/*public static void main(String[] args) {
		ResponseTextMessage msg = new ResponseTextMessage("otQ2LuLL7a4VqKVkc7-6YNWL1zpo","12测试消息Abc123");
		SendMessageService ss = new SendMessageService();
		ss.sendMessage(msg.toCustJsonString());
	}*/
}
