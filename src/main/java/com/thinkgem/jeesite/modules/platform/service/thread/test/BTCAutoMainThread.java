package com.thinkgem.jeesite.modules.platform.service.thread.test;

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
import com.thinkgem.jeesite.modules.mongodb.model.BtcToXbtTradeData;
import com.thinkgem.jeesite.modules.mongodb.service.BtcTradeDataInterface;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.account.BitMexAccountService;
import com.thinkgem.jeesite.modules.platform.service.account.BitOkAccountService;
import com.thinkgem.jeesite.modules.platform.service.thread.HedgeTotalControlService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeDetailService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 *  V 1.0
* @ClassName: AutoMainThread 
* @Description: TODO
* @author EK huangone 
* @date 2017-10-28 下午5:36:30 
*
 */
public class BTCAutoMainThread extends Thread{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final BigDecimal ZERO = new BigDecimal(0);
	// 状态： 可开仓， 操作中， 可平仓  ,默认可开仓
	private String status = Constants.CAN_OPEN;
	// 交易主表code
	private String tradeCode = null;
	// 当前用户
	private User user;
	// 
	private String cacheKey;
	
	private TradeTaskReq req;

	private long checkTime = 20000; // 每二十秒检查一次
	
	private Long queryStartTime = 0L; // 开始时间
	private Long queryEndTime = 0L;   // 结束时间
	
	public BTCAutoMainThread(TradeTaskReq req, User user, String cacheKey) {
		this.req = req;
		this.user = user;
		this.cacheKey = cacheKey;
		this.queryStartTime = req.getQueryStartTime();
		this.queryEndTime = req.getQueryEndTime();
	}

	@Override
	public void run() {
		try {
			log.info(">> start Test runing ....");
			long startTime = System.currentTimeMillis();
			while (true) {
				try {
					long currTime = System.currentTimeMillis();
					if(currTime - startTime > checkTime){
						AutoTask();
						startTime = currTime;
					}
					BigDecimal max = req.getMaxAgio();
					BigDecimal min = req.getMinAgio();
					// 关闭自动任务：关闭命令+可开仓状态
					if ("stop".equals(req.getStopJob())) {
						//if(Constants.CAN_OPEN.equals(status)){
							break;
						//}
					}
					// 获取差价
					BigDecimal agioOld = getAgio(req.getSymbolA(), req.getSymbolB());
					BigDecimal agio =  agioOld.abs();
					//log.info(">> new montiro agio ="+df.format(agio)+" ,agioOld = "+df.format(agioOld) );
					// 比较差价与最大值、最小值
					if(agio.compareTo(max) > 0){
						// 差价 > 最大值   操作：宽开 或 宽平
						// 获取type 宽开窄平、窄开宽平或 两者
						String type = req.getType();
						if(Constants.CAN_OPEN.equals(status)){
							// 可开仓
							if(Constants.TRADE_TYPE_WIDEOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 宽开  
								openTrade(true,type);
							}
						}else if(Constants.CAN_CLOSE.equals(status)){
							// 可平仓
							if(Constants.TRADE_TYPE_NARROWOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 宽平
								closeTrade(true,type);
							}
						}
					}
					if(agio.compareTo(min) < 0){
						// 差价  < 最大值   操作：窄开 或 窄平
						// 获取type 宽开窄平、窄开宽平 或 两者
						String type = req.getType();
						if(Constants.CAN_OPEN.equals(status)){
							// 可开仓
							if(Constants.TRADE_TYPE_NARROWOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 窄开
								openTrade(false,type);
							}
						}else if(Constants.CAN_CLOSE.equals(status)){
							// 可平仓
							if(Constants.TRADE_TYPE_WIDEOPEN.equals(type)
									|| Constants.TRADE_TYPE_BOTH.equals(type)){
								// 窄平
								closeTrade(false,type);
							}
						}
					}
					
					/**  待平仓状态下 判断是否爆仓   **/
					if(Constants.CAN_CLOSE.equals(status)){
						checkBurst();
					}
					
				} catch (Exception e) {
					//e.printStackTrace();
					log.error(">>>> auto thread error : ",e);
					break;
				}
			}
			log.info(">>>  The end auto thread .");
		} catch (Exception ee) {
			//ee.printStackTrace();
			log.error(">>>> Thread error : ",ee);
		}
		// 结束监控更新监控表数据
		setStopJob();
	}
	
	/**
	 * 检查是否爆仓
	 */
	private void checkBurst() throws Exception{
		log.debug(" >> checkBurst 1");
		boolean isBurstA = false;
		boolean isBurstB = false;
		String symbolA = req.getSymbolA();
		String symbolB = req.getSymbolB();
		String openDirA = req.getOpenDirA();
		BigDecimal burstA = req.getBurstPiceA();
		BigDecimal burstB = req.getBurstPiceB();
		BigDecimal priceA = getCachePrice(symbolA);
		BigDecimal priceB = getCachePrice(symbolB);
		if(Constants.DIRECTION_BUY_UP.equals(openDirA)){
			// A 开多， 爆仓：实时价格 <= 爆仓价
			isBurstA = priceA.compareTo(burstA) <=0;
			// B 开空， 爆仓：实时价格 >= 爆仓价
			isBurstB = priceB.compareTo(burstB) >=0;
		}else{
			// A 开空
			isBurstA = priceA.compareTo(burstA) >=0;
			// B 开多
			isBurstB = priceB.compareTo(burstB) <=0;
		}
		log.debug(" >> checkBurst 2");
		if(isBurstA || isBurstB){
			log.info(">>>>>>   爆仓！！！！！ time = ");
			/** 爆仓处理  **/
			doBurst(isBurstA, isBurstB);
		}
		log.debug(" >> checkBurst 3");
	}
	
	/**
	 * 爆仓平单
	* @param isBurstA
	* @param isBurstB
	 */
	private void doBurst(Boolean isBurstA, Boolean isBurstB) throws Exception{
		log.info(">>> doBurst isBurstA="+isBurstA +", isBurstB="+isBurstB);
		// 窄平
		BitTrade entity = getTradeEty(null,false); // 交易主表信息
		
		// 获取 A B 监控时价格
		BigDecimal priceA = getCachePrice(req.getSymbolA());
		BigDecimal priceB = getCachePrice(req.getSymbolB());
		
		BitTradeDetail detailEtyA = getDetailEty(true,Constants.DETAIL_TYPE_ZP,priceA);
		BitTradeDetail detailEtyB = getDetailEty(false,Constants.DETAIL_TYPE_ZP,priceB);
		
		/**  判断A开仓方向，原开多的币种现平多；原开空的币种现平空  **/
		if(Constants.DIRECTION_BUY_UP.equals(req.getOpenDirA())){
			// A为开多，则A平多
			detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
		}else {
			detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
		}
		// 爆仓的先交易
		// TODO 判断亏损方先交易
		boolean isSuccess = false;
		if(isBurstA){
			detailEtyA.setIfBurstBarn(Constants.STATUS_RUN);
			isSuccess = doTradeDetailOrder(detailEtyA,false,true);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyB,false,false);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyA.getDirection())){
						detailEtyA.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyA.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					doTradeDetailOrder(detailEtyA,true,true);
				}
			}
		}
		if(isBurstB){
			detailEtyB.setIfBurstBarn(Constants.STATUS_RUN);
			isSuccess = doTradeDetailOrder(detailEtyB,false,false);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyA,false,true);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyB.getDirection())){
						detailEtyB.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyB.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					doTradeDetailOrder(detailEtyB,true,false);
				}
			}
		}
		if(isSuccess){
			// 计算费用、利润
			calculateOrder(entity);
			entity.setCloseBarnTime(new Date());
			entity.setIfClose(Constants.STATUS_RUN);
			entity.setIfBurstBarn(Constants.STATUS_RUN);
			saveTradeMainEty(entity); // 保存交易主表信息
			// 更改status 状态   可开仓
			status = Constants.CAN_OPEN;
		}
	}
	
	/** 开仓
	 * isWide TRUE 宽开， FALSE 窄开
	 * type 监听类型
	 */
	private void openTrade(Boolean isWide,String type) throws Exception{
		log.info(">> openTrade isWide ="+isWide+" , type="+type);
		
		BitTrade entity = getTradeEty(type,true); // 交易主表信息
		this.setTradeCode();
		entity.setCode(tradeCode);
		
		String detailType = "";
		if(isWide){
			detailType = Constants.DETAIL_TYPE_KK;
		}else {
			detailType = Constants.DETAIL_TYPE_ZK;
		}
		BigDecimal priceA = getCachePrice(req.getSymbolA());
		BigDecimal priceB = getCachePrice(req.getSymbolB());
		
		BitTradeDetail detailEtyA = getDetailEty(true,detailType,priceA);
		BitTradeDetail detailEtyB = getDetailEty(false,detailType,priceB);
		
		String openDirA = "";
		String openDirB = "";
		BigDecimal BurstPiceA = ZERO;
		BigDecimal BurstPiceB = ZERO;
		BigDecimal areaA = priceA.multiply(new BigDecimal((0.01 * req.getLeverA())));
		BigDecimal areaB = priceB.multiply(new BigDecimal((0.01 * req.getLeverB())));
		if(priceA.compareTo(priceB) > 0){
			// A  >  B  
			if(isWide){
				// 宽开   A买空，B买多
				openDirA = Constants.DIRECTION_BUY_DOWN;
				openDirB = Constants.DIRECTION_BUY_UP;
				// 计算爆仓价格
				BurstPiceA = priceA.add(areaA);
				BurstPiceB = priceB.subtract(areaB);
			}else{
				// 窄开  A买多，B买空
				openDirA = Constants.DIRECTION_BUY_UP;
				openDirB = Constants.DIRECTION_BUY_DOWN;
				// 计算爆仓价格
				BurstPiceA = priceA.subtract(areaA);
				BurstPiceB = priceB.add(areaB);
			}
		}else {
			// A  <  B  
			if(isWide){
				// 宽开 B买空，A买多
				openDirA = Constants.DIRECTION_BUY_UP;
				openDirB = Constants.DIRECTION_BUY_DOWN;
				// 计算爆仓价格
				BurstPiceA = priceA.subtract(areaA);
				BurstPiceB = priceB.add(areaB);
			}else {
				// 窄开  B买多，A买空
				// A  <  B  
				openDirA = Constants.DIRECTION_BUY_DOWN;
				openDirB = Constants.DIRECTION_BUY_UP;
				// 计算爆仓价格
				BurstPiceA = priceA.add(areaA);
				BurstPiceB = priceB.subtract(areaB);
			}
		}
		req.setOpenDirA(openDirA);
		req.setOpenDirB(openDirB);
		detailEtyA.setDirection(openDirA);
		detailEtyB.setDirection(openDirB);
		req.setBurstPiceA(BurstPiceA);
		req.setBurstPiceB(BurstPiceB);
		detailEtyA.setBurstPice(BurstPiceA);
		detailEtyB.setBurstPice(BurstPiceB);
		// A,B 交易  bitme 先交易  TODO 
		boolean isSuccess = false;
		if(req.getPlatformA().equals(BitMexInterConstants.PLATFORM_CODE)){
			isSuccess = doTradeDetailOrder(detailEtyA,true,true);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyB,true,false);
				if(!isSuccess){
					// 失败，前一币种回撤(平仓)
					if(Constants.DIRECTION_BUY_UP.equals(detailEtyA.getDirection())){
						detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
					}else{
						detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
					}
					doTradeDetailOrder(detailEtyA,false,true);
				}
			}
		}else{
			isSuccess = doTradeDetailOrder(detailEtyB,true,false);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyA,true,true);
				if(!isSuccess){
					// 失败，前一币种回撤(平仓)
					if(Constants.DIRECTION_BUY_UP.equals(detailEtyB.getDirection())){
						detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
					}else{
						detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
					}
					doTradeDetailOrder(detailEtyB,false,false);
				}
			}
		}
		if(isSuccess){
			// 保存交易主表信息
			entity.setOpenBarnTime(new Date());
			entity.setIfClose(Constants.STATUS_CLOSE);
			saveTradeMainEty(entity);
			// 更改status 状态   可平仓
			status = Constants.CAN_CLOSE;
		}
		//log.info(">> openTrade 22 isWide ="+isWide);
	}
	
	/** 平仓
	 * isWide TRUE 宽平， FALSE 窄平
	 * type 监听类型
	 */
	private void closeTrade(Boolean isWide,String type) throws Exception{
		log.info(">> CCcloseTrade  isWide ="+isWide +" ,type="+type);
		BitTrade entity = getTradeEty(type,false); // 交易主表信息
		String detailType = "";
		if(isWide){
			detailType = Constants.DETAIL_TYPE_KP;
		}else {
			detailType = Constants.DETAIL_TYPE_ZP;
		}
		// 获取 A B 监控时价格
		BigDecimal priceA = getCachePrice(req.getSymbolA());
		BigDecimal priceB = getCachePrice(req.getSymbolB());
		
		BitTradeDetail detailEtyA = getDetailEty(true,detailType,priceA);
		BitTradeDetail detailEtyB = getDetailEty(false,detailType,priceB);
		
		/**  判断A开仓方向，原开多的币种现平多；原开空的币种现平空  **/
		if(Constants.DIRECTION_BUY_UP.equals(req.getOpenDirA())){
			// A为开多，则A平多
			detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
		}else {
			detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
			detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
		}
		// A,B 交易  bitme 先交易
		// TODO 判断亏损方先交易  + 短信提醒
		boolean isSuccess = false;
		if(req.getPlatformA().equals(BitMexInterConstants.PLATFORM_CODE)){
			isSuccess = doTradeDetailOrder(detailEtyA,false,true);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyB,false,false);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyA.getDirection())){
						detailEtyA.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyA.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					doTradeDetailOrder(detailEtyA,true,true);
				}
			}
				
		}else{
			isSuccess = doTradeDetailOrder(detailEtyB,false,false);
			if(isSuccess){
				isSuccess = doTradeDetailOrder(detailEtyA,false,true);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyB.getDirection())){
						detailEtyB.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyB.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					doTradeDetailOrder(detailEtyB,true,false);
				}
			}
		}
		if(isSuccess){
			// 计算费用、利润
			calculateOrder(entity);
			entity.setCloseBarnTime(new Date());
			entity.setIfClose(Constants.STATUS_RUN);
			saveTradeMainEty(entity);// 保存交易主表信息
			// 更改status 状态   可开仓
			status = Constants.CAN_OPEN;
		}
		//log.info(">> CCcloseTrade 22 isWide ="+isWide);
	}
	
	/**
	 * 获取新/旧交易主表对象
	 * isNew  true 新  false 旧
	 */
	private BitTrade getTradeEty(String type,boolean isNew) throws Exception{
		BitTrade entity = null;
		if(isNew){
			entity = new BitTrade();
			entity.setSymbolA(req.getSymbolA());
			entity.setSymbolB(req.getSymbolB());
			entity.setMaxAgio(req.getMaxAgio());
			entity.setMinAgio(req.getMinAgio());
			entity.setTypeFlag(type);
			entity.setUser(user);
			entity.setMonitorCode(req.getMonitorCode());
			entity.setOpenBarnTime(new Date());
			entity.setIfBurstBarn(Constants.STATUS_CLOSE);
		}else{
			BitTradeService bean = SpringContextHolder.getBean(BitTradeService.class);
			entity = bean.getByCode(tradeCode);
		}
		return entity;
	}
	
	/**
	 * 获取交易明细对象
	 * isA true A币种明细对象， false B币种
	 */
	private BitTradeDetail getDetailEty(Boolean isA, String detailType,BigDecimal price) throws Exception{
		BitTradeDetail det = new BitTradeDetail();
		if(isA){
			det.setSymbol(req.getSymbolA());
			det.setLever(req.getLeverA());
			det.setPlatform(req.getPlatformA());
		}else{
			det.setSymbol(req.getSymbolB());
			det.setLever(req.getLeverB());
			det.setPlatform(req.getPlatformB());
		}
		det.setMonitorPice(price);
		det.setTradeCode(tradeCode);
		det.setDetailType(detailType);
		det.setIfBurstBarn(Constants.STATUS_CLOSE);
		return det;
	}
	
	/**
	 * 获取币种实时价格
	* @param @param symbol
	* @return BigDecimal
	 */
	private BigDecimal getPriceBySymbol(String symbol) throws Exception{
		return getAgio(symbol, null);
	}
	
	private BtcTradeDataInterface btcTradeData;
	
	private BtcToXbtTradeData btcDate;
	/**
	 *  获取币种 A与B 实时差价 或 一币种实时价格
	* @return BigDecimal
	 * @throws Exception 
	 */
	private BigDecimal getAgio(String symbolA, String symbolB) throws Exception{
		BigDecimal agio = Constants.BIGDECIMAL_ZERO;
		// TODO 重要
		if(null == btcTradeData){
			btcTradeData = SpringContextHolder.getBean(BtcTradeDataInterface.class);
		}
		if(null == queryStartTime || queryStartTime == 0L){
			queryStartTime = 20171012121212L;
		}
		
		Direction direction = Direction.ASC;
		PageRequest pageable = new PageRequest(0,1,new Sort(direction,"time"));
		List<BtcToXbtTradeData> list = btcTradeData.findByTimePageable(queryStartTime, pageable);
		JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
		btcDate = arr.getObject(0, BtcToXbtTradeData.class);
		
		queryStartTime = btcDate.getTime();
		/**  待开仓状态下  大于监控时间结束本次 监听  **/
		if(Constants.CAN_OPEN.equals(status) && queryEndTime < btcDate.getTime()){
			log.info(">> do over time = "+queryStartTime+"  ,queryEndTime="+ queryEndTime);
			throw new Exception(">>>>>> do over <<<<<");
		}
		  
		if(null == symbolB){
			if("btc_usd".equals(symbolA)){
				agio = new BigDecimal(btcDate.getOkPrice());
			}else {
				agio = new BigDecimal(btcDate.getMexUSDPrice());
			}
		}else{
			agio = new BigDecimal(btcDate.getOkToUSDAgio());
		}
		return agio;
	}
	
	/**
	 * 获取币种监控时价格
	* @param @param symbol
	* @return BigDecimal
	 */
	private BigDecimal getCachePrice(String symbol){
		BigDecimal price = new BigDecimal(0);
		if("btc_usd".equals(symbol)){
			price = new BigDecimal(btcDate.getOkPrice());
		}else {
			price = new BigDecimal(btcDate.getMexUSDPrice());
		}
		return price;
	}
	
	private BigDecimal getAccountBalance(String symbol,String platform) throws Exception{
		BigDecimal balance = this.ZERO;
		User user = UserUtils.getUser();
		BigDecimal price = getCachePrice(symbol);
		if(OkexInterConstants.PLATFORM_CODE.equals(platform)){
			// okex 账户
			BitOkAccountService OkBean = SpringContextHolder.getBean(BitOkAccountService.class);
			BitOkAccount bitOkAccount = new BitOkAccount();
			bitOkAccount.setUseId(user.getId());
			bitOkAccount.setSymbol(symbol);
			List<BitOkAccount> ret = OkBean.findList(bitOkAccount);
			BitOkAccount okAccount = ret.get(0);
			balance = okAccount.getAccountBalance();
			
		}else{
			// bitmex 账户
			BitMexAccountService MexBean = SpringContextHolder.getBean(BitMexAccountService.class);
			BitMexAccount bitMexAccount = new BitMexAccount();
			bitMexAccount.setUseId(user.getId());
			bitMexAccount.setSymbol("XBTUSD");
			List<BitMexAccount> ret = MexBean.findList(bitMexAccount);
			BitMexAccount mexAccount = ret.get(0);
			balance = mexAccount.getAccountBalance();
		}
		// BTC 转 USD
		balance = balance.multiply(price);
		return balance;
	}
	
	
	/**
	 * 处理明细交易
	* @param @param detailEty
	* @param @param isOpen  true 开仓， false 平仓
	* @param @param isA  true A币种， false B币种
	* @return 是否成功
	 */
	private Boolean doTradeDetailOrder(BitTradeDetail detailEty, boolean isOpen, boolean isA) throws Exception{
		log.info(">>> doTradeDetailOrder isOpen="+isOpen +", isA="+isA);
		Boolean flage = false;
		String symbol = detailEty.getSymbol();
		String platform = detailEty.getPlatform();
		Integer lever =detailEty.getLever(); 
		BigDecimal depositRate = this.ZERO; // 保证金使用比率
		// 获取账户保证金
		BigDecimal balance = getAccountBalance(symbol,platform);
		BigDecimal balanceOther = this.ZERO;  
		BigDecimal depositRateOther = this.ZERO;  
		BigDecimal depositOther = this.ZERO;  
		if(isA){
			depositRate = req.getDepositA().multiply(new BigDecimal(0.01));
			// 获取另一账户用保证金
			balanceOther = getAccountBalance(req.getSymbolB(),req.getPlatformB());
			depositRateOther = req.getDepositB().multiply(new BigDecimal(0.01));
		}else{
			depositRate = req.getDepositB().multiply(new BigDecimal(0.01));
			// 获取另一账户用保证金
			balanceOther = getAccountBalance(req.getSymbolA(),req.getPlatformA());
			depositRateOther = req.getDepositA().multiply(new BigDecimal(0.01));
		}
		depositOther = balanceOther.multiply(depositRateOther); 
		/** 使用最小的可用保证金   */
		// 使用保证金 = 账户保证金 * 使用比率
		BigDecimal deposit = balance.multiply(depositRate); 
		
		if(deposit.compareTo(depositOther) > 0){
			deposit = depositOther;
		}
		// TODO 先计算okex BTC与 mex XBTUSD  取10USD的倍数 保证金= n*10;
		deposit = new BigDecimal((deposit.intValue()/10)*10);
		// 使用杠杆后可用额（头寸） = 使用保证金 * 杠杆 
		BigDecimal allQuota = deposit.multiply(new BigDecimal(lever));
		// 委托数量
		Integer amount = 0;
		Integer parValue = 0;  // 合约单张面值
		if(isA){
			parValue = req.getParValueA();
		}else{
			parValue = req.getParValueB();
		}
		// 根据保证金、杠杆、 计算委托数量  如： okex btc 1张= 100USD， mex 1张=1USD
		amount = allQuota.intValue() / parValue;
		
		detailEty.setAmount(amount);
		detailEty.setPosition(allQuota);
		detailEty.setDeposit(deposit);
		detailEty.setPlatform(platform);
		detailEty.setCode(DateUtils.getSysTimeMillisCode());
		detailEty.setStatusFlag(Constants.STATUS_CLOSE);
		detailEty.setRemarks(queryStartTime+"");
		// 头寸 = 交易价格 * 委托数量
		BigDecimal price = getPriceBySymbol(symbol); // 实时价格
		detailEty.setPrice(price);
		// 价格对应的BTC数量 = 头寸/价格 
		BigDecimal pAmount = allQuota.divide(price, 10, BigDecimal.ROUND_HALF_DOWN);
		if(isOpen){
			if(isA){
				req.setAmountA(amount);
				req.setPriceAmountA(pAmount);
			}else{
				req.setAmountB(amount);
				req.setPriceAmountB(pAmount);
			}
			detailEty.setPriceAmount(pAmount); 
		}else {
			if(isA){
				detailEty.setAmount(req.getAmountA());
				detailEty.setPriceAmount(req.getPriceAmountA()); 
			}else{
				detailEty.setAmount(req.getAmountB());
				detailEty.setPriceAmount(req.getPriceAmountB()); 
			}
		}
		if(BitMexInterConstants.PLATFORM_CODE.equals(platform)){
			// MEX
			flage = postMex(detailEty);
			log.info(">>>>>>>>　　　Post Mex flage ="+flage);
		}else if(OkexInterConstants.PLATFORM_CODE.equals(platform)){
			// OKEX
			flage = postOkex(detailEty);
			log.info(">>>>>>>>　　　Post Okex flage ="+flage);
		}
		return flage;
	}
	
	// 下Mex 订单并保存
	private Boolean postMex(BitTradeDetail detailEty){
		//saveDB();
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		bean.save(detailEty);
		return true;
	}
	
	// 下Okex 订单并保存
	private Boolean postOkex(BitTradeDetail detailEty){
		//saveDB();
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		bean.save(detailEty);
		return true;
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
			
			/** 保存账户保证金  */
			BigDecimal feeRateA = req.getFeeRateA();
			BigDecimal feeRateB = req.getFeeRateB();
			//  预计费用
			BigDecimal totalPin = totalPin_up.add(totalPin_down);
			BigDecimal feeRate = feeRateA.add(feeRateB);
			feeRate = feeRate.multiply(new BigDecimal(0.01));
			BigDecimal fee = feeRate.multiply(totalPin);
			// 利润
			BigDecimal profit = totalRevenue.subtract(fee);
			
			entity.setFee(fee);
			entity.setProfit(profit);
			entity.setRevenue(totalRevenue);
			entity.setCloseBarnTime(new Date());
		}
	}
	
	/** 
	 * 设置交易主表code为当前时间戳值
	 */
	private void setTradeCode() throws Exception{
		this.tradeCode = DateUtils.getSysTimeMillisCode();
	}
	
	// 结束监控更新监控表数据
	private void setStopJob(){
		String monitorCode = req.getMonitorCode();
		BitMonitorService monitorService = SpringContextHolder.getBean(BitMonitorService.class);
		BitMonitor entity = monitorService.getByCode(monitorCode);
		if(null != entity){
			entity.setStatusFlag(Constants.STATUS_CLOSE); // 结束
			monitorService.save(entity);
		}else{
			log.error(">> StopJob error not data! code = "+monitorCode);
		}
		
	}
	
	private void AutoTask() throws Exception{
		DecimalFormat df = new DecimalFormat("#.00");
		BigDecimal agioOld = getAgio(req.getSymbolA(), req.getSymbolB());
		BigDecimal agio =  agioOld.abs();
		BigDecimal priceA = getCachePrice(req.getSymbolA());
		BigDecimal priceB = getCachePrice(req.getSymbolB());
		log.info(">> Test AutoTask time="+queryStartTime+" ,AGIO ="+df.format(agio)+" ,agioOld = "+df.format(agioOld)
				+",priceA="+df.format(priceA)+",priceB="+df.format(priceB));
		TradeTaskReq cacheReq = HedgeTotalControlService.getMonitorCache(cacheKey);
		if(null != cacheReq){
			req.setDepositA(cacheReq.getDepositA());
			req.setDepositB(cacheReq.getDepositB());
			req.setMaxAgio(cacheReq.getMaxAgio());
			req.setMinAgio(cacheReq.getMinAgio());
			req.setStopJob(cacheReq.getStopJob());
		}else{
			log.error(">> cacheReq is null !");
		}
		
	}
}
