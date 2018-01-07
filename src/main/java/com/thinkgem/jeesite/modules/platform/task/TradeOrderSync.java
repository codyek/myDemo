package com.thinkgem.jeesite.modules.platform.task;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;
import com.thinkgem.jeesite.modules.log.service.BitMonitorLogService;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.order.MexOrder;
import com.thinkgem.jeesite.modules.platform.entity.order.OkexOrder;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexOrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeDetailService;

@Service("tradeOrderSync")
public class TradeOrderSync {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private BitTradeDetailService bitTradeDetailService;
	
	@Autowired
	private BitMonitorLogService bitMonitorLogService;
	
	@Autowired
	private OrderInterfaceService orderService;
	
	@Autowired
	private MexOrderInterfaceService mexOrderService;
	/**
	* 同步交易后的订单价格
	* @throws
	 */
	@Async
	public void tradeOrderSync(){
		log.info(">>>>> syncOrderTradePrice    <<< ");
		// TODO 同步交易价格：10分钟内  交易价格为空的订单
		// 查询所有下单价格是0的订单明细
		BitTradeDetail detail = new BitTradeDetail();
		detail.setTradePrice(new BigDecimal(0));
		List<BitTradeDetail> details = bitTradeDetailService.findList(detail);
		if(null != details && !details.isEmpty()){
			boolean okexPlat = false;
			boolean mexPlat = false;
			for (BitTradeDetail bitTradeDetail : details) {
				String plat = bitTradeDetail.getPlatform();
				if(OkexInterConstants.PLATFORM_CODE.equals(plat)){
					okexPlat = true;
				}
				if(BitMexInterConstants.PLATFORM_CODE.equals(plat)){
					mexPlat = true;
				}
			}
			if(okexPlat){
				List<OkexOrder> list = null;
				try {
					list = orderService.getOrderInfo(Constants.SYMBOL_BTC_USD, "quarter", "-1", "2", "0", "20");
					if(null != list && !list.isEmpty()){
						foop:for (OkexOrder okexOrder : list) {
							String int_type = okexOrder.getType();
							int int_amount = okexOrder.getDealAmount();
							long int_time = okexOrder.getCreateDateLong();
							BigDecimal priceAvg = okexOrder.getPriceAvg();
							for (BitTradeDetail bitTradeDetail : details) {
								String type = bitTradeDetail.getDirection();
								int amount = bitTradeDetail.getAmount();
								long time = bitTradeDetail.getCreateDate().getTime();
								if(type.equals(int_type) && amount == int_amount &&
									(time-8000 <= int_time || int_time <= time+8000)){ //存单时间在下单前后8秒内
									// 保存成交价格、爆仓价格
									bitTradeDetail.setTradePrice(priceAvg);
									if(Constants.DIRECTION_BUY_UP.equals(type)){
										// 开多  爆仓价格 = 成交价格 - 9.5%
										BigDecimal area = priceAvg.multiply(new BigDecimal(0.095));
										BigDecimal burstPice = priceAvg.subtract(area);
										bitTradeDetail.setBurstPice(burstPice);
									}else if(Constants.DIRECTION_BUY_DOWN.equals(type)){
										// 开空  爆仓价格 = 成交价格 + 9.5%
										BigDecimal area = priceAvg.multiply(new BigDecimal(0.095));
										BigDecimal burstPice = priceAvg.add(area);
										bitTradeDetail.setBurstPice(burstPice);
									}
									bitTradeDetailService.save(bitTradeDetail);
									continue foop;
								}
							}
						}
						// 保存操作记录
						String msg = "同步okex成交价格，同步数量："+list.size();
						BitMonitorLog log = new BitMonitorLog();
						log.setUseId("all");
						log.setLogContent(msg);
						log.setTypeFlag(Constants.LOG_TYPE_SYNCORDER);
						log.setStatusFlag("1");
						bitMonitorLogService.save(log);
					}
				} catch (Exception e) {
					log.error(">> syncOkexOrders error:",e);
				}
			}
			if(mexPlat){
				List<MexOrder> list = null;
				try {
					list = mexOrderService.getOrderInfo(Constants.SYMBOL_XBTQAE, 20D);
					if(null != list && !list.isEmpty()){
						foop:for (MexOrder mexOrder : list) {
							String int_type = mexOrder.getSide();
							int int_amount = mexOrder.getCumeQty();
							long int_time = mexOrder.getTransactTimeLong();
							BigDecimal priceAvg = mexOrder.getAvgPx();
							for (BitTradeDetail bitTradeDetail : details) {
								String type = bitTradeDetail.getDirection();
								int amount = bitTradeDetail.getAmount();
								long time = bitTradeDetail.getCreateDate().getTime();
								if(type.equals(int_type) && amount == int_amount &&
									(time-8000 <= int_time || int_time <= time+8000)){ //存单时间在下单前后8秒内
									// 保存成交价格、爆仓价格
									bitTradeDetail.setTradePrice(priceAvg);
									//if(Constants.DIRECTION_BUY_UP.equals(type) || Constants.DIRECTION_SELL_DOWN.equals(type)){
									if(Constants.DIRECTION_BUY_UP.equals(type)){
										// 1开多,4平空  爆仓价格 = 成交价格 - 9.3%
										BigDecimal area = priceAvg.multiply(new BigDecimal(0.093));
										BigDecimal burstPice = priceAvg.subtract(area);
										bitTradeDetail.setBurstPice(burstPice);
									//}else if(Constants.DIRECTION_BUY_DOWN.equals(type) || Constants.DIRECTION_SELL_UP.equals(type)){
									}else if(Constants.DIRECTION_BUY_DOWN.equals(type)){
										// 2开空，3平多  爆仓价格 = 成交价格 + 9.3%
										BigDecimal area = priceAvg.multiply(new BigDecimal(0.093));
										BigDecimal burstPice = priceAvg.add(area);
										bitTradeDetail.setBurstPice(burstPice);
									}
									bitTradeDetailService.save(bitTradeDetail);
									continue foop;
								}
							}
						}
						// 保存操作记录
						String msg = "同步bitmex成交价格，同步数量："+list.size();
						BitMonitorLog log = new BitMonitorLog();
						log.setUseId("all");
						log.setLogContent(msg);
						log.setTypeFlag(Constants.LOG_TYPE_SYNCORDER);
						log.setStatusFlag("1");
						bitMonitorLogService.save(log);
					}
				} catch (Exception e) {
					log.error(">> syncMexOrders error:",e);
				}
			}
		}
	}
}
