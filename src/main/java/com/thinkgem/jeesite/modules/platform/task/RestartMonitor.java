package com.thinkgem.jeesite.modules.platform.task;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitSymbolConfig;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.thread.HedgeTotalControlService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitSymbolConfigService;

/**
 * 
* @Description: 因应用停止导致原监控关闭，需重新启动原监控。
* @author EK huangone 
*
 */
@Service("restartMonitor")
public class RestartMonitor {
	
	@Autowired
	private BitMonitorService bitMonitorService;
	
	@Autowired
	private BitSymbolConfigService configService;
	
	/**
	 * 
	* @Description: 应用启动后查询监控主表原监控未关闭的需重新启动监控
	 */
	public void doRestartMonitor(){
		BitMonitor bitMonitor = new BitMonitor();
		bitMonitor.setStatusFlag("1");
		// 检查未关闭的监控记录
		List<BitMonitor> bitMonitors = bitMonitorService.findList(bitMonitor);
		if(null != bitMonitors && !bitMonitors.isEmpty()){
			for (BitMonitor monitor : bitMonitors) {
				String uid = monitor.getUser().getId();
				String cacheKey = null;//HedgeTotalControlService.getMonitorCacheKey(uid);
				TradeTaskReq req = new TradeTaskReq();
				
				// TODO 
				// 获取交易主表和明细表信息
				
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
				
				// 保存缓存
				String value = JSONObject.toJSONString(req);
				EhCacheUtils.put(Constants.PRICE_CACHE,cacheKey, value);
			}
		}
		
	}

}
