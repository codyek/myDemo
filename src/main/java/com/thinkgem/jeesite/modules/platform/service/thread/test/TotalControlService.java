package com.thinkgem.jeesite.modules.platform.service.thread.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitSymbolConfig;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.thread.ThreadManager;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitSymbolConfigService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 自动对冲总控制线程 V 1.0
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
	
	@Autowired
	private BitSymbolConfigService configService;
	
	public String control(TradeTaskReq req){
		String msg = "success";
		BitSymbolConfig bitSymbolConfig = new BitSymbolConfig();
		bitSymbolConfig.setCode("symbolConfig");
		List<BitSymbolConfig> list = configService.findList(bitSymbolConfig);
		for (BitSymbolConfig config : list) {
			if(req.getSymbolA().equals(config.getSymbol())){
				req.setFeeRateA(new BigDecimal(config.getFeeRate()));
				req.setPlatformA(config.getPlatform());
				req.setParValueA(config.getParValue());
			}
			if(req.getSymbolB().equals(config.getSymbol())){
				req.setFeeRateB(new BigDecimal(config.getFeeRate()));
				req.setPlatformB(config.getPlatform());
				req.setParValueB(config.getParValue());
			}
		}
		User user = UserUtils.getUser();
		log.info(">>Test control startJob user="+user.getName());
		String code = DateUtils.getSysTimeMillisCode();
		String cacheKey = getMonitorCacheKey(user.getId(),req.getSymbolA(),req.getSymbolB());
		req.setMonitorCode(code);
		// new 监控thread 
		Thread thread = new Thread(new BTCAutoMainThread(req,user,cacheKey));
		Long threadId = thread.getId();
		// 保存缓存
		String value = JSONObject.toJSONString(req);
		EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, value);
		// 启动线程
		ThreadManager.getInstance().execute(thread);
		
		// 保存数据
		BitMonitor entity = new BitMonitor();
		entity.setCode(code);
		entity.setUser(user);
		entity.setOpenTime(new Date());
		entity.setThreadId(String.valueOf(threadId));
		entity.setStatusFlag(Constants.STATUS_RUN);
		bitMonitorService.save(entity);
		
		return msg;
	}
	
	public String updateData(TradeTaskReq req){
		String msg = "success";
		// 保存缓存
		User user = UserUtils.getUser();
		log.info(">>Test updateData user="+user.getName());
		String cacheKey = getMonitorCacheKey(user.getId(),req.getSymbolA(),req.getSymbolB());
		String value = JSONObject.toJSONString(req);
		EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, value);
		return msg;
	}
	
	public String stopJob(String symbolA,String symbolB){
		String msg = "success";
		User user = UserUtils.getUser();
		log.info(">>Test stopJob user="+user.getName());
		String cacheKey = getMonitorCacheKey(user.getId(),symbolA,symbolB);
		TradeTaskReq req = getMonitorCache(cacheKey);
		if(null != req){
			req.setStopJob("stop");
			// 保存缓存
			String value = JSONObject.toJSONString(req);
			EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, value);
		}else{
			msg = "noData";
		}
		return msg;
	}
	
	public static String getMonitorCacheKey(String uid,String symbolA,String symbolB){
		return "monitorControl:"+uid+":"+symbolA+symbolB;
	}
	
	public static TradeTaskReq getMonitorCache(String key){
		TradeTaskReq req = null;
		String json = (String)EhCacheUtils.get(Constants.PRICE_CACHE,key);
		if(!StringUtils.isBlank(json)){
			req = JSONObject.parseObject(json, TradeTaskReq.class);
		}
		return req;
	}
}



