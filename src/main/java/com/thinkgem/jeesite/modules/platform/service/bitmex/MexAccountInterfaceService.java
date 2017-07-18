package com.thinkgem.jeesite.modules.platform.service.bitmex;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.service.MexBaseService;


/**
 * 
* @Description: 账户接口服务
* @author EK huangone 
* @date 2017-7-9 上午12:38:50 
*
 */
@Service
@Transactional(readOnly = true)
public class MexAccountInterfaceService extends MexBaseService{
	
	
	/**
	* 获取您帐户的保证金状态
	* @param currency 货币：XBt， XBT
	* @return String
	* @throws Exception
	 */
	public String get_user_margin(String currency) throws Exception {
		String url = BitMexInterConstants.GET_USER_MARGIN_URL;
		JSONObject param = new JSONObject();
		param.put("currency", currency);
		return exchange(url, HttpMethod.GET, true, param);
	}
	
	
	/**
	 * 修改公开订单的数量或价格
	 * TODO if use 
	 */
	

}
