package com.thinkgem.jeesite.modules.platform.service.thread;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 自动对冲总控制线程
* @ClassName: TotalControlService 
* @author EK huangone 
* @date 2017-8-21 下午11:15:59 
*
 */
@Service
public class TotalControlService {
	protected Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private BitMonitorService bitMonitorService;
	
	public String control(TradeTaskReq req){
		String msg = "success";
		User user = UserUtils.getUser();
		String code = DateUtils.getSysTimeMillisCode();
		String cacheKey = getMonitorCacheKey(user.getId());
		req.setMonitorCode(code);
		// new 监控thread 
		Thread thread = new AutoMainThread(req,user,cacheKey);
		Long threadId = thread.getId();
		// 启动线程
		thread.start();
		
		// 保存数据
		BitMonitor entity = new BitMonitor();
		entity.setCode(code);
		entity.setUser(user);
		entity.setOpenTime(new Date());
		entity.setThreadId(String.valueOf(threadId));
		entity.setStatusFlag(Constants.STATUS_RUN);
		bitMonitorService.save(entity);
		
		// 保存缓存
		String value = JSONObject.toJSONString(req);
		EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, value);
		
		return msg;
	}
	
	public static String getMonitorCacheKey(String uid){
		return "monitorControl:"+uid;
	}
}



