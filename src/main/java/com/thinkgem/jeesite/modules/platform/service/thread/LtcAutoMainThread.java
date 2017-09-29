package com.thinkgem.jeesite.modules.platform.service.thread;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.mongodb.model.LtcTradeData;
import com.thinkgem.jeesite.modules.mongodb.service.LtcTradeDataInterface;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexOrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeDetailService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeService;
import com.thinkgem.jeesite.modules.sys.entity.User;

public class LtcAutoMainThread extends Thread{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	// 状态： 可开仓， 操作中， 可平仓  ,默认可开仓
	private String status = Constants.CAN_OPEN;
	// 交易主表code
	private String tradeCode = null;
	// 当前用户
	private User user;
	// 
	private String cacheKey;
	
	private TradeTaskReq req;

	public LtcAutoMainThread(TradeTaskReq req, User user, String cacheKey) {
		this.req = req;
		this.user = user;
		this.cacheKey = cacheKey;
	}

	@Override
	public void run() {
			log.info(">> start runing ....");
			BigDecimal max = req.getMaxAgio();
			BigDecimal min = req.getMinAgio();
			String symbolA = req.getSymbolA(); 
			String symbolB = req.getSymbolB();
			DecimalFormat df = new DecimalFormat("#.00");
			while (true) {
				try {
					// 获取差价
					BigDecimal agioOld = getAgio(symbolA, symbolB);
					BigDecimal agio =  agioOld.abs();
					log.info(">> montiro agio ="+df.format(agio)+" ,agioOld = "+df.format(agioOld) );
					// 比较差价与最大值、最小值
					if(agio.compareTo(max) > 0){
						// 差价 > 最大值   操作：宽开 或 宽平
						// 获取type 宽开窄平、窄开宽平或 两者
						String type = getType(cacheKey);
						if(Constants.CAN_OPEN.equals(status)){
							// 可开仓
							if(Constants.TRADE_TYPE_WIDEOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 宽开  
								BitTrade entity = getTradeNewEty(type); // 交易主表信息
								this.setTradeCode();
								entity.setCode(tradeCode);
								
								// 获取 A B 监控时价格
								BigDecimal priceA = getMonitorPice(symbolA);
								BigDecimal priceB = getMonitorPice(symbolB);
								
								Integer leverA = req.getLeverA();
								Integer leverB = req.getLeverB();
								
								BitTradeDetail detailEtyA = getDetailEty(symbolA,priceA,Constants.DETAIL_TYPE_KK,leverA,req.getDepositA());
								BitTradeDetail detailEtyB = getDetailEty(symbolB,priceB,Constants.DETAIL_TYPE_KK,leverB,req.getDepositB());
								
								if(priceA.compareTo(priceB) > 0){
									// A  >  B  A买空，B买多
									req.setOpenDirA(Constants.DIRECTION_BUY_DOWN);
									req.setOpenDirB(Constants.DIRECTION_BUY_UP);
									detailEtyA.setDirection(Constants.DIRECTION_BUY_DOWN);
									detailEtyB.setDirection(Constants.DIRECTION_BUY_UP);
									// 计算爆仓价格
									BigDecimal areaA = priceA.multiply(new BigDecimal((0.01 * leverA)));
									req.setBurstPiceA(priceA.add(areaA));
									BigDecimal areaB = priceB.multiply(new BigDecimal((0.01 * leverB)));
									req.setBurstPiceB(priceB.subtract(areaB));
									detailEtyA.setBurstPice(priceA.add(areaA));
									detailEtyB.setBurstPice(priceB.subtract(areaB));
								}else {
									// A  <  B  B买空，A买多
									req.setOpenDirA(Constants.DIRECTION_BUY_UP);
									req.setOpenDirB(Constants.DIRECTION_BUY_DOWN);
									detailEtyB.setDirection(Constants.DIRECTION_BUY_DOWN);
									detailEtyA.setDirection(Constants.DIRECTION_BUY_UP);
									// 计算爆仓价格
									BigDecimal areaA = priceA.multiply(new BigDecimal((0.01 * leverA)));
									req.setBurstPiceA(priceA.subtract(areaA));
									BigDecimal areaB = priceB.multiply(new BigDecimal((0.01 * leverB)));
									req.setBurstPiceB(priceB.add(areaB));
									detailEtyA.setBurstPice(priceA.subtract(areaA));
									detailEtyB.setBurstPice(priceB.add(areaB));
								}
								// A,B 交易
								doTradeDetailOrder(detailEtyA,true);
								doTradeDetailOrder(detailEtyB,true);
								
								// 保存交易主表信息
								entity.setOpenBarnTime(new Date());
								entity.setIfClose(Constants.STATUS_CLOSE);
								saveTradeMainEty(entity);
								// 更改status 状态   可平仓
								status = Constants.CAN_CLOSE;
							}
							
						}else if(Constants.CAN_CLOSE.equals(status)){
							// 可平仓
							if(Constants.TRADE_TYPE_NARROWOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 宽平
								BitTrade entity = getTradeEtyByCode(tradeCode); // 交易主表信息
								
								// 获取 A B 监控时价格
								BigDecimal priceA = getMonitorPice(symbolA);
								BigDecimal priceB = getMonitorPice(symbolB);
								
								BitTradeDetail detailEtyA = getDetailEty(symbolA,priceA,Constants.DETAIL_TYPE_KP,req.getLeverA(),req.getDepositA());
								BitTradeDetail detailEtyB = getDetailEty(symbolB,priceB,Constants.DETAIL_TYPE_KP,req.getLeverB(),req.getDepositB());
								
								
								/**  判断A开仓方向，原开多的币种现平多；原开空的币种现平空  **/
								if(Constants.DIRECTION_BUY_UP.equals(req.getOpenDirA())){
									// A为开多，则A平多
									detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
									detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
								}else {
									detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
									detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
								}
								// A,B 交易
								doTradeDetailOrder(detailEtyA,false);
								doTradeDetailOrder(detailEtyB,false);
								
								// 保存交易主表信息
								// 计算费用、利润
								calculateOrder(entity);
								entity.setIfClose(Constants.STATUS_RUN);
								saveTradeMainEty(entity);
								// 更改status 状态   可开仓
								status = Constants.CAN_OPEN;
							}
						}
					}
					if(agio.compareTo(min) < 0){
						// 差价  < 最大值   操作：窄开 或 窄平
						// 获取type 宽开窄平、窄开宽平 或 两者
						String type = getType(cacheKey);
						if(Constants.CAN_OPEN.equals(status)){
							// 可开仓
							if(Constants.TRADE_TYPE_NARROWOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 窄开
								BitTrade entity = getTradeNewEty(type); // 交易主表信息
								this.setTradeCode();
								entity.setCode(tradeCode);
								
								// 获取 A B 监控时价格
								BigDecimal priceA = getMonitorPice(symbolA);
								BigDecimal priceB = getMonitorPice(symbolB);
								
								Integer leverA = req.getLeverA();
								Integer leverB = req.getLeverB();
								
								BitTradeDetail detailEtyA = getDetailEty(symbolA,priceA,Constants.DETAIL_TYPE_ZK,leverA,req.getDepositA());
								BitTradeDetail detailEtyB = getDetailEty(symbolB,priceB,Constants.DETAIL_TYPE_ZK,leverB,req.getDepositB());
								
								if(priceA.compareTo(priceB) > 0){
									// A  >  B  A买多，B买空
									req.setOpenDirA(Constants.DIRECTION_BUY_UP);
									req.setOpenDirB(Constants.DIRECTION_BUY_DOWN);
									detailEtyA.setDirection(Constants.DIRECTION_BUY_UP);
									detailEtyB.setDirection(Constants.DIRECTION_BUY_DOWN);
									// 计算爆仓价格
									BigDecimal areaA = priceA.multiply(new BigDecimal((0.01 * leverA)));
									req.setBurstPiceA(priceA.subtract(areaA));
									BigDecimal areaB = priceB.multiply(new BigDecimal((0.01 * leverB)));
									req.setBurstPiceB(priceB.add(areaB));
									detailEtyA.setBurstPice(priceA.subtract(areaA));
									detailEtyB.setBurstPice(priceB.add(areaB));
								}else {
									// A  <  B  B买多，A买空
									req.setOpenDirA(Constants.DIRECTION_BUY_DOWN);
									req.setOpenDirB(Constants.DIRECTION_BUY_UP);
									detailEtyB.setDirection(Constants.DIRECTION_BUY_UP);
									detailEtyA.setDirection(Constants.DIRECTION_BUY_DOWN);
									// 计算爆仓价格
									BigDecimal areaA = priceA.multiply(new BigDecimal((0.01 * leverA)));
									req.setBurstPiceA(priceA.add(areaA));
									BigDecimal areaB = priceB.multiply(new BigDecimal((0.01 * leverB)));
									req.setBurstPiceB(priceB.subtract(areaB));
									detailEtyA.setBurstPice(priceA.add(areaA));
									detailEtyB.setBurstPice(priceB.subtract(areaB));
								}
								// A,B 交易
								doTradeDetailOrder(detailEtyA,true);
								doTradeDetailOrder(detailEtyB,true);
								
								// 保存交易主表信息
								entity.setOpenBarnTime(new Date());
								entity.setIfClose(Constants.STATUS_CLOSE);
								saveTradeMainEty(entity);
								// 更改status 状态   可平仓
								status = Constants.CAN_CLOSE;
							}
							
						}else if(Constants.CAN_CLOSE.equals(status)){
							// 可平仓
							if(Constants.TRADE_TYPE_WIDEOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 窄平
								BitTrade entity = getTradeEtyByCode(tradeCode); // 交易主表信息
								
								// 获取 A B 监控时价格
								BigDecimal priceA = getMonitorPice(symbolA);
								BigDecimal priceB = getMonitorPice(symbolB);
								
								BitTradeDetail detailEtyA = getDetailEty(symbolA,priceA,Constants.DETAIL_TYPE_ZP,req.getLeverA(),req.getDepositA());
								BitTradeDetail detailEtyB = getDetailEty(symbolB,priceB,Constants.DETAIL_TYPE_ZP,req.getLeverB(),req.getDepositB());
								
								/**  判断A开仓方向，原开多的币种现平多；原开空的币种现平空  **/
								if(Constants.DIRECTION_BUY_UP.equals(req.getOpenDirA())){
									// A为开多，则A平多
									detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
									detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
								}else {
									detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
									detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
								}
								// A,B 交易
								doTradeDetailOrder(detailEtyA,false);
								doTradeDetailOrder(detailEtyB,false);
								
								// 保存交易主表信息
								// 计算费用、利润
								calculateOrder(entity);
								entity.setIfClose(Constants.STATUS_RUN);
								saveTradeMainEty(entity);
								// 更改status 状态   可开仓
								status = Constants.CAN_OPEN;
							}
						}
					}
					
					/**  待平仓状态下 判断是否爆仓   **/
					if(Constants.CAN_CLOSE.equals(status)){
						checkBurst();
					}
					
					int it = 9-6;
					if (it < 1) {
						if(Constants.CAN_OPEN.equals(status)){
							// 关闭自动任务：关闭命令+可开仓状态
							break;
						}
					}
				} catch (Exception e) {
					log.error("auto thread error: ",e);
					break;
				}
			}
			
		log.info(">>>  The end auto thread .");
	}
	
	private void checkBurst(){
		boolean isBurstA = false;
		boolean isBurstB = false;
		String symbolA = req.getSymbolA();
		String symbolB = req.getSymbolB();
		String openDirA = req.getOpenDirA();
		BigDecimal burstA = req.getBurstPiceA();
		BigDecimal burstB = req.getBurstPiceB();
		BigDecimal okPrice = new BigDecimal(ltcDate.getOkLTCPrice());
		BigDecimal mexPrice = new BigDecimal(ltcDate.getMexLTCPrice());
		if(Constants.DIRECTION_BUY_UP.equals(openDirA)){
			// A 开多， 爆仓：实时价格 <= 爆仓价
			if("ltc_usd".equals(symbolA)){
				isBurstA = okPrice.compareTo(burstA) <=0;
			}else{
				isBurstA = mexPrice.compareTo(burstA) <=0;
			}
			// B 开空， 爆仓：实时价格 >= 爆仓价
			if("ltc_usd".equals(symbolB)){
				isBurstB = okPrice.compareTo(burstB) >=0;
			}else{
				isBurstB = mexPrice.compareTo(burstB) >=0;
			}
		}else{
			// A 开空
			if("ltc_usd".equals(symbolA)){
				isBurstA = okPrice.compareTo(burstA) >=0;
			}else{
				isBurstA = mexPrice.compareTo(burstA) >=0;
			}
			// B 开多
			if("ltc_usd".equals(symbolB)){
				isBurstB = okPrice.compareTo(burstB) <=0;
			}else{
				isBurstB = mexPrice.compareTo(burstB) <=0;
			}
		}
		if(isBurstA || isBurstB){
			log.info(">>>>>>   爆仓！！！！！ time = "+ltcDate.getTime());
			/** 爆仓处理  **/
			doBurst(isBurstA, isBurstB);
		}
	}
	
	/**
	 * 爆仓平单
	* @Title: doBurst
	* @param @param symbolA
	* @param @param symbolB
	 */
	private void doBurst(Boolean isBurstA, Boolean isBurstB){
		String symbolA = req.getSymbolA();
		String symbolB = req.getSymbolB();
		// 窄平
		BitTrade entity = getTradeEtyByCode(tradeCode); // 交易主表信息
		
		// 获取 A B 监控时价格
		BigDecimal priceA = getMonitorPice(symbolA);
		BigDecimal priceB = getMonitorPice(symbolB);
		
		BitTradeDetail detailEtyA = getDetailEty(symbolA,priceA,Constants.DETAIL_TYPE_ZP,req.getLeverA(),req.getDepositA());
		BitTradeDetail detailEtyB = getDetailEty(symbolB,priceB,Constants.DETAIL_TYPE_ZP,req.getLeverB(),req.getDepositB());
		
		/**  判断A开仓方向，原开多的币种现平多；原开空的币种现平空  **/
		if(Constants.DIRECTION_BUY_UP.equals(req.getOpenDirA())){
			// A为开多，则A平多
			detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
		}else {
			detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
		}
		if(isBurstA){
			detailEtyA.setIfBurstBarn(Constants.STATUS_RUN);
		}
		if(isBurstB){
			detailEtyB.setIfBurstBarn(Constants.STATUS_RUN);
		}
		// A,B 交易
		doTradeDetailOrder(detailEtyA,false);
		doTradeDetailOrder(detailEtyB,false);
		
		// 保存交易主表信息
		// 计算费用、利润
		calculateOrder(entity);
		entity.setIfClose(Constants.STATUS_RUN);
		entity.setIfBurstBarn(Constants.STATUS_RUN);
		saveTradeMainEty(entity);
		// 更改status 状态   可开仓
		status = Constants.CAN_OPEN;
	}
	
	/** 
	 * 设置交易主表code为当前时间戳值
	 */
	private void setTradeCode(){
		this.tradeCode = DateUtils.getSysTimeMillisCode();
	}
	
	/**
	 * 获取币种实时价格
	* @param @param symbol
	* @return BigDecimal
	 */
	private BigDecimal getPriceBySymbol(String symbol){
		return getAgio(symbol, null);
	}
	
	private LtcTradeDataInterface ltcTradeData;
	private Long queryTime = 0L;
	private LtcTradeData ltcDate;
	/**
	 *  获取币种 A与B 实时差价 或 一币种实时价格
	* @return BigDecimal
	 */
	private BigDecimal getAgio(String symbolA, String symbolB){
		BigDecimal agio = Constants.BIGDECIMAL_ZERO;
		// TODO 重要
		if(null == ltcTradeData){
			ltcTradeData = SpringContextHolder.getBean(LtcTradeDataInterface.class);
		}
		if(null == queryTime || queryTime == 0L){
			queryTime = 20170720121212L;
		}
		
		Direction direction = Direction.ASC;
		PageRequest pageable = new PageRequest(0,1,new Sort(direction,"time"));
		List<LtcTradeData> list = ltcTradeData.findByTimePageable(queryTime, pageable);
		JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
		ltcDate = arr.getObject(0, LtcTradeData.class);
		
		queryTime = ltcDate.getTime();
		  
		if(null == symbolB){
			if("ltc_usd".equals(symbolA)){
				agio = new BigDecimal(ltcDate.getOkLTCPrice());
			}else {
				agio = new BigDecimal(ltcDate.getMexLTCPrice());
			}
		}else{
			agio = new BigDecimal(ltcDate.getOkToMexAgio());
		}
		return agio;
	}
	
	/**
	 * 获取币种监控时价格
	* @param @param symbol
	* @return BigDecimal
	 */
	private BigDecimal getMonitorPice(String symbol){
		BigDecimal price = new BigDecimal(0);
		if("ltc_usd".equals(symbol)){
			price = new BigDecimal(ltcDate.getOkLTCPrice());
		}else {
			price = new BigDecimal(ltcDate.getMexLTCPrice());
		}
		return price;
	}
	
	/**
	 * 获取type 宽开窄平、窄开宽平或两者
	* @return String
	 */
	private String getType(String key){
		String type = req.getType(); 
		// TODO 重要 应从缓存取
		//String value = (String)EhCacheUtils.get(Constants.PRICE_CACHE,cacheKey);
		//TradeTaskReq cacheReq = JSONObject.parseObject(value, TradeTaskReq.class);
		return type;
	}
	
	
	/**
	 * 获取新交易主表对象
	 */
	private BitTrade getTradeNewEty(String type){
		BitTrade entity = new BitTrade();
		entity.setSymbolA(req.getSymbolA());
		entity.setSymbolB(req.getSymbolB());
		entity.setMaxAgio(req.getMaxAgio());
		entity.setMinAgio(req.getMinAgio());
		entity.setTypeFlag(type);
		entity.setUser(user);
		entity.setMonitorCode(req.getMonitorCode());
		entity.setOpenBarnTime(new Date());
		entity.setIfBurstBarn(Constants.STATUS_CLOSE);
		
		return entity;
	}
	
	// 获取交易主表对象Bycode
	private BitTrade getTradeEtyByCode(String code){
		BitTradeService bean = SpringContextHolder.getBean(BitTradeService.class);
		BitTrade entity = bean.getByCode(code);
		return entity;
	}
	
	/**
	 * 获取交易明细对象
	 */
	private BitTradeDetail getDetailEty(String symbol,BigDecimal price, 
			String detailType, Integer lever, BigDecimal deposit){
		BitTradeDetail det = new BitTradeDetail();
		det.setSymbol(symbol);
		det.setTradeCode(tradeCode);
		det.setMonitorPice(price);
		det.setDetailType(detailType);
		det.setLever(lever);
		det.setDeposit(deposit);
		det.setIfBurstBarn(Constants.STATUS_CLOSE);
		return det;
	}
	
	private BigDecimal PriceAmountOkex;
	private BigDecimal PriceAmountMex;
	
	// 处理明细交易
	private void doTradeDetailOrder(BitTradeDetail detailEty, boolean isOpen){
		String symbol = detailEty.getSymbol();
		Integer lever =detailEty.getLever(); 
		BigDecimal deposit = detailEty.getDeposit();
		// 使用杠杆后可用额（头寸） = 杠杆 * 保证金
		BigDecimal allQuota = deposit.multiply(new BigDecimal(lever));
		// 字典
		String platform = "";
		// TODO  重要 从字典取
		// 委托数量
		Integer amount = 0;
		
		if("ltc_usd".equals(symbol)){
			platform = OkexInterConstants.PLATFORM_CODE;
			// 根据保证金、杠杆、 计算委托数量   okex btc 1张= 100USD
			amount = allQuota.intValue() / 100;
		}else{
			platform = BitMexInterConstants.PLATFORM_CODE;
			// 根据保证金、杠杆、 计算委托数量   Mex btc 1张 = 1USD
			amount = allQuota.intValue() / 1;
		}
		detailEty.setPlatform(platform);
		detailEty.setCode(DateUtils.getSysTimeMillisCode());
		detailEty.setStatusFlag(Constants.STATUS_CLOSE);
		detailEty.setRemarks(queryTime+"");
		// 头寸 = 交易价格 * 委托数量
		BigDecimal price = getPriceBySymbol(symbol); // 实时价格
		detailEty.setPrice(price);
		detailEty.setAmount(amount);
		detailEty.setPosition(allQuota);
		//PriceAmountOkex 
		// 价格对应的数量 = 头寸/价格 
		BigDecimal pAmount = allQuota.divide(price, 6, BigDecimal.ROUND_HALF_DOWN);
		if(isOpen){
			if("ltc_usd".equals(symbol)){
				PriceAmountOkex = pAmount;
			}else{
				PriceAmountMex = pAmount;
			}
			detailEty.setPriceAmount(pAmount); 
		}else {
			if("ltc_usd".equals(symbol)){
				detailEty.setPriceAmount(PriceAmountOkex); 
			}else{
				detailEty.setPriceAmount(PriceAmountMex); 
			}
		}
		if(BitMexInterConstants.PLATFORM_CODE.equals(platform)){
			// MEX
			postMex(detailEty);
		}else if(OkexInterConstants.PLATFORM_CODE.equals(platform)){
			// OKEX
			postOkex(detailEty);
		}
	}
	
	// 下Mex 订单并保存
	private void postMex(BitTradeDetail detailEty){
		//MexOrderInterfaceService service = new MexOrderInterfaceService();
		//String direction = detailEty.getDirection();
		// doPost
		//service.post_order(detailEty.getSymbol(), direction, price, orderQty, side, text);
		//saveDB();
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		bean.save(detailEty);
	}
	
	// 下Okex 订单并保存
	private void postOkex(BitTradeDetail detailEty){
		//OrderInterfaceService service = new OrderInterfaceService();
		//String direction = detailEty.getDirection();
		// doPost
		//service.future_trade(symbol, contractType, price, amount, type, match_price, lever_rate);
		//saveDB();
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		bean.save(detailEty);
	}

	// 保存交易主表信息
	private void saveTradeMainEty(BitTrade entity){
		BitTradeService bean = SpringContextHolder.getBean(BitTradeService.class);
		bean.save(entity);
	}
	
	
	/**
	 * 计算 完整一次交易订单的收入，费用，盈利
	* @param @param entity
	 */
	private void calculateOrder(BitTrade entity){
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		
		BitTradeDetail detailEty = new BitTradeDetail();
		detailEty.setTradeCode(tradeCode);
		List<BitTradeDetail> list = bean.findList(detailEty);
		
		if(null != list && !list.isEmpty()){
			// 以币种计算  [0]开， [1]平
			BitTradeDetail[] symbolUp = new BitTradeDetail[2];
			BitTradeDetail[] symbolDown = new BitTradeDetail[2];
			for (BitTradeDetail dal : list) {
				// `direction` 交易方向：1:开多, 2:开空,  3:平多,  4:平空
				String direction = dal.getDirection();
				
				if(Constants.DIRECTION_BUY_UP.equals(direction)){
					// 开多
					symbolUp[0] = dal;
				}else if(Constants.DIRECTION_SELL_UP.equals(direction)){
					// 平多
					symbolUp[1] = dal;
				}else if(Constants.DIRECTION_BUY_DOWN.equals(direction)){
					// 开空
					symbolDown[0] = dal;
				}else if(Constants.DIRECTION_SELL_DOWN.equals(direction)){
					// 平空
					symbolDown[1] = dal;
				}
			}
			
			// 收入
			// -- up   收入=平 - 开 
			BitTradeDetail kai_up = symbolUp[0];
			BigDecimal totalKai_up = kai_up.getPrice().multiply(kai_up.getPriceAmount());
			BitTradeDetail pin_up = symbolUp[1];
			BigDecimal totalPin_up = pin_up.getPrice().multiply(pin_up.getPriceAmount());
			// -- up 收入
			BigDecimal revenue_up = totalPin_up.subtract(totalKai_up);
			// -- down 收入=开 - 平 
			BitTradeDetail kai_down = symbolDown[0];
			BigDecimal totalKai_down = kai_down.getPrice().multiply(kai_down.getPriceAmount());
			BitTradeDetail pin_down = symbolDown[1];
			BigDecimal totalPin_down = pin_down.getPrice().multiply(pin_down.getPriceAmount());
			// -- up 收入
			BigDecimal revenue_down = totalKai_down.subtract(totalPin_down);
			// 总收入
			BigDecimal totalRevenue = revenue_up.add(revenue_down);
			
			// 费用 
			// TODO 预计费用
			BigDecimal totalPin = totalPin_up.add(totalPin_down);
			BigDecimal feeRate = new BigDecimal(0.001); // 0.1%
			BigDecimal fee = feeRate.multiply(totalPin);
			// 利润
			BigDecimal profit = totalRevenue.subtract(fee);
			
			entity.setFee(fee);
			entity.setProfit(profit);
			entity.setRevenue(totalRevenue);
			entity.setCloseBarnTime(new Date());
		}
	}
}
