package com.thinkgem.jeesite.modules.platform.service.thread;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.HedgeUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.constants.inter.BitMexInterConstants;
import com.thinkgem.jeesite.modules.platform.constants.inter.OkexInterConstants;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;
import com.thinkgem.jeesite.modules.platform.entity.account.BitOkAccount;
import com.thinkgem.jeesite.modules.platform.entity.order.OkexOrder;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.account.BitMexAccountService;
import com.thinkgem.jeesite.modules.platform.service.account.BitOkAccountService;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexOrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitMonitorService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeDetailService;
import com.thinkgem.jeesite.modules.platform.service.trade.BitTradeService;
import com.thinkgem.jeesite.modules.sys.entity.User;

public class HedgeAutoMainThread implements Runnable{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final BigDecimal ZERO = new BigDecimal(0);
	// 状态： 可开仓， 操作中， 可平仓  ,默认可开仓
	private String status = Constants.CAN_OPEN;
	// 交易主表code
	private String tradeCode = null;
	// 当前用户
	private User user;
	// 缓存key
	private String cacheKey;
	
	private TradeTaskReq req;
	
	private long checkTime = 20000; // 每二十秒检查一次

	public HedgeAutoMainThread(TradeTaskReq req, User user, String cacheKey) {
		this.req = req;
		this.user = user;
		this.cacheKey = cacheKey;
	}
	
	// 两位小数格式
	private DecimalFormat pDF = new DecimalFormat("#.00");
	
	// 设置Mex 杠杆倍数
	private boolean setMexLeverage = false;
	
	// T+n 高延伸价
	private BigDecimal extendMax = new BigDecimal(0);
	// T+n 低延伸价
	private BigDecimal extendMin = new BigDecimal(0);
	
	// 是否已初始化
	private boolean initFlag = false;
	/**  okex 可用保证金  */
	private BigDecimal okexMargin;
	/**  bitmex 可用保证金  */
	private BigDecimal mexMargin;
	
	private Timer timerTask = null;
	private HedgeAutoTask hedgeAutoTask = null;

	@Override
	public void run() {
		try {
			log.info(">> start runing ....");
			long startTime = System.currentTimeMillis();
			hedgeAutoTask = new HedgeAutoTask(user);
			timerTask = new Timer();
			timerTask.schedule(hedgeAutoTask, 1000, 10*1000); // 设置一个 10 秒钟的ping定时器 
			
			while (true) {
				try {
					long currTime = System.currentTimeMillis();
					if(currTime - startTime > checkTime){
						AutoTask();
						startTime = currTime;
					}
					// 关闭自动任务：关闭命令+//可开仓状态
					if ("stop".equals(req.getStopJob())) {
						//if(Constants.CAN_OPEN.equals(status)){
							break;
						//}
					}
					// 初始化数据
					if(!initFlag){
						//init();
					}
					// 是否交易
					String flag = isOrder();
					if(StringUtils.isNotBlank(flag)){
						if(Constants.OPEN.equals(flag)){
							extendMax = new BigDecimal(0);// 重置
							openTrade();
						}else if(Constants.CLOSE.equals(flag)){
							extendMin = new BigDecimal(0);// 重置
							closeTrade();
						}
					}
					/**  待平仓状态下 判断是否爆仓   **/
					if(Constants.CAN_CLOSE.equals(status)){
						checkBurst();
					}
					/**  检查健康状态  */
					checkHealth();
				} catch (Exception e) {
					log.error(">>>> auto thread error : ",e);
					break;
				}
			}
			log.info(">>>  The end auto thread .");
		} catch (Exception ee) {
			log.error(">>>> Thread error : ",ee);
		}
		// 结束监控更新监控表数据
		setStopJob();
	}
	// 初始数据
	private void init(){
		// 初始可用保证金
		okexMargin = HedgeUtils.getOkexMarginByHttp();
		mexMargin = HedgeUtils.getMexMarginByHttp();
		if(null == mexMargin){
			mexMargin = HedgeUtils.getMexMarginBySocket();
		}
		if(null != okexMargin && null != mexMargin){
			initFlag = true;
		}
		HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, "初始可用保证金成功！", "1");
	}
	
	
	/** 
	 * 是否下单
	* @Title: isOrder
	* @return String
	 * @throws Exception 
	 */
	private String isOrder() throws Exception{
		String flag = "";
		BigDecimal max = req.getMaxAgio();
		BigDecimal min = req.getMinAgio();
		Integer drawRate = req.getWithdrawRate(); // 回测率 %
		// 获取差价
		BigDecimal agio = getAgio(req.getSymbolA(), req.getSymbolB());
		// 比较差价与最大值、最小值
		if(agio.compareTo(max) > 0){
			// 差价 > 最大值   操作：宽开 
			if(Constants.CAN_OPEN.equals(status)){
				// 宽开 
				/** T+n 差价延伸策略  */
				// 回撤价格 = max * (drawRate/100)
				BigDecimal drawPrice  = max.multiply(new BigDecimal(drawRate).divide(new BigDecimal(100)));
				if(drawRate.intValue()==0){
					flag = Constants.OPEN;
					String msg = "开仓监控：当前差价："+pDF.format(agio)+"，定义差价："+req.getMaxAgio()+"，是否开仓："+flag;
					HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, msg, "1");
				}else if(extendPloy(true,drawPrice,agio)){
					flag = Constants.OPEN;
				}
			}
		}
		if(agio.compareTo(min) < 0){
			// 差价  < 最小值   操作： 窄平
			if(Constants.CAN_CLOSE.equals(status)){
				// 窄平 
				/** T+n 差价延伸策略  */
				// 回撤价格 = min * (drawRate/100)
				BigDecimal drawPrice  = min.multiply(new BigDecimal(drawRate).divide(new BigDecimal(100)));
				if(drawRate.intValue()==0){
					flag = Constants.CLOSE;
					String msg = "平仓监控：当前差价："+pDF.format(agio)+",定义差价："+req.getMinAgio()+"，是否平仓："+flag;
					HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, msg, "1");
				}else if(extendPloy(false,drawPrice,agio)){
					flag = Constants.CLOSE;
				}
				/** 盈利率策略
				if(req.getCloseByProfit()){
					// TODO
				}  */
			}
		}
		return flag;
	}
	
	private long extendPloyTime = System.currentTimeMillis();
	/** 
	 * T+n 差价延伸策略 
	 * del true 开仓，false 平仓
	 * drawPrice 高回撤价格
	 * agio  当前差价
	 */
	private boolean extendPloy(boolean del,BigDecimal drawPrice,BigDecimal agio){
		boolean flag = false;
		BigDecimal flagPice = new BigDecimal(0);
		String msg = "";
		if (del) {
			// 开仓
			if(extendMax.compareTo(new BigDecimal(0))==0){
				extendMax = agio;
			}else {
				if(agio.compareTo(extendMax)>=0){
					//当前差价>高延伸价,更新高延伸价
					extendMax = agio;
				}else{
					//当前差价<高延伸价
					// 标记价格>=max, 且当前差价<标记价格,则可开仓
					BigDecimal max = req.getMaxAgio();
					flagPice = extendMax.subtract(drawPrice);
					if(flagPice.compareTo(max) >= 0 && agio.compareTo(flagPice)<=0){
						flag = true;
					}
				}
			}
			msg = "开仓监控：当前差价："+pDF.format(agio)+"，定义差价："+req.getMaxAgio()
					+"，标记差价："+pDF.format(flagPice)+"，最高差价："+pDF.format(extendMax)+"，是否开仓："+flag;
			
		} else {
			// 平仓
			/** 差价小于10 平仓  */
			if(agio.compareTo(new BigDecimal(10))<=0){
				flag = true;
			}else{
				if(extendMin.compareTo(new BigDecimal(0))==0){
					extendMin = agio;
				}else {
					if(agio.compareTo(extendMin)<0){
						//当前差价<低延伸价,更新低延伸价
						extendMin = agio;
					}else{
						//当前差价>低延伸价
						// 标记价格<min，且当前差价>标记价格,则可平仓
						BigDecimal min = req.getMinAgio();
						flagPice = extendMin.add(drawPrice);
						if(flagPice.compareTo(min) <= 0 && agio.compareTo(flagPice)>=0){
							flag = true;
						}
					}
				}
			}
			msg = "平仓监控：当前差价："+pDF.format(agio)+",最小差价："+req.getMinAgio()+"标记差价："
					+flagPice+"，最小差价："+pDF.format(extendMin)+"，是否平仓："+flag;
		}
		long currTimeAccount = System.currentTimeMillis();
		
		if(flag){
			HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, msg, "1");
		}else if(currTimeAccount - extendPloyTime > 10000){// 10秒查一次
			HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, msg, "1");
			extendPloyTime = currTimeAccount;
		}
		return flag;
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
		String msg = "爆仓平单，币种：";
		if(isBurstA){
			msg = msg+req.getSymbolA();
		}
		if(isBurstB){
			msg = msg+req.getSymbolB();
		}
		HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_MONITOR, msg, "1");
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
				Thread.sleep(5000); // 休眠5秒
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
				Thread.sleep(5000); // 休眠5秒
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
	 */
	private void openTrade() throws Exception{
		log.info("----> openTrade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
		String detailType = "";
		detailType = Constants.DETAIL_TYPE_KK; // 宽开
		BitTrade entity = getTradeEty(detailType,true); // 交易主表信息
		this.setTradeCode();
		entity.setCode(tradeCode);
		
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
			// 宽开   A买空，B买多
			openDirA = Constants.DIRECTION_BUY_DOWN;
			openDirB = Constants.DIRECTION_BUY_UP;
			// 计算爆仓价格
			BurstPiceA = priceA.add(areaA);
			BurstPiceB = priceB.subtract(areaB);
		}else {
			// A  <  B  
			// 宽开 B买空，A买多
			openDirA = Constants.DIRECTION_BUY_UP;
			openDirB = Constants.DIRECTION_BUY_DOWN;
			// 计算爆仓价格
			BurstPiceA = priceA.subtract(areaA);
			BurstPiceB = priceB.add(areaB);
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
			log.info("----> open A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
			isSuccess = doTradeDetailOrder(detailEtyA,true,true);
			if(isSuccess){
				log.info("----> open B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
				isSuccess = doTradeDetailOrder(detailEtyB,true,false);
				if(!isSuccess){
					// 失败，前一币种回撤(平仓)
					if(Constants.DIRECTION_BUY_UP.equals(detailEtyA.getDirection())){
						detailEtyA.setDirection(Constants.DIRECTION_SELL_UP);
					}else{
						detailEtyA.setDirection(Constants.DIRECTION_SELL_DOWN);
					}
					log.info("----> open ret A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
					doTradeDetailOrder(detailEtyA,false,true);
				}
			}
		}else{
			log.info("----> open B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
			isSuccess = doTradeDetailOrder(detailEtyB,true,false);
			if(isSuccess){
				log.info("----> open A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
				isSuccess = doTradeDetailOrder(detailEtyA,true,true);
				if(!isSuccess){
					// 失败，前一币种回撤(平仓)
					if(Constants.DIRECTION_BUY_UP.equals(detailEtyB.getDirection())){
						detailEtyB.setDirection(Constants.DIRECTION_SELL_UP);
					}else{
						detailEtyB.setDirection(Constants.DIRECTION_SELL_DOWN);
					}
					log.info("----> open ret B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
					doTradeDetailOrder(detailEtyB,false,false);
				}
			}
		}
		log.info("----> openTrade over, time="+DateUtils.getDate("hh:mm:ss:SSS"));
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
	 */
	private void closeTrade() throws Exception{
		log.info("----> closeTrade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
		//againTime = 0;
		String detailType = "";
		detailType = Constants.DETAIL_TYPE_ZP;  //窄平
		BitTrade entity = getTradeEty(detailType,false); // 交易主表信息
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
			log.info("----> close  A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
			isSuccess = doTradeDetailOrder(detailEtyA,false,true);
			if(isSuccess){
				log.info("----> close  B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
				isSuccess = doTradeDetailOrder(detailEtyB,false,false);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyA.getDirection())){
						detailEtyA.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyA.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					log.info("----> close ret A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
					doTradeDetailOrder(detailEtyA,true,true);
				}
			}
				
		}else{
			log.info("----> close  B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
			isSuccess = doTradeDetailOrder(detailEtyB,false,false);
			if(isSuccess){
				log.info("----> close  A Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
				isSuccess = doTradeDetailOrder(detailEtyA,false,true);
				if(!isSuccess){
					// 失败，前一币种回撤(开仓)
					if(Constants.DIRECTION_SELL_UP.equals(detailEtyB.getDirection())){
						detailEtyB.setDirection(Constants.DIRECTION_BUY_UP);
					}else{
						detailEtyB.setDirection(Constants.DIRECTION_BUY_DOWN);
					}
					log.info("----> close ret B Trade, time="+DateUtils.getDate("hh:mm:ss:SSS"));
					doTradeDetailOrder(detailEtyB,true,false);
				}
			}
		}
		log.info("----> closeTrade over, time="+DateUtils.getDate("hh:mm:ss:SSS"));
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
	 *  获取币种 A与B 实时差价 或 一币种实时价格  (绝对值)
	* @return BigDecimal
	 */
	private BigDecimal getAgio(String symbolA, String symbolB) throws Exception{
		BigDecimal agio = Constants.BIGDECIMAL_ZERO;
		BigDecimal priceA = getCachePrice(symbolA);
		BigDecimal priceB = getCachePrice(symbolB);
		agio = priceA.subtract(priceB);
		return agio.abs();
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
		//detailEty.setRemarks(queryTime+"");
		// 头寸 = 交易价格 * 委托数量
		BigDecimal price = getCachePrice(symbol); // 实时价格
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
			flage = postMex(detailEty,false);
			log.info(">>>>>>>>　　　Post Mex flage ="+flage);
			if(!flage){
				// 设置健康状态
				mexOrderHealth = false;
			}
		}else if(OkexInterConstants.PLATFORM_CODE.equals(platform)){
			// OKEX
			flage = postOkex(detailEty,false);
			log.info(">>>>>>>>　　　Post Okex flage ="+flage);
			if(!flage){
				// 设置健康状态
				okOrderHealth = false;
				Thread.sleep(HedgeUtils.sleepTime_30);
			}
		}
		if(!flage){
			Thread.sleep(HedgeUtils.sleepTime_15); // 休眠15秒防止频繁调用超过次数
		}
		return flage;
	}
	
	/**
	 * 下Mex 订单并保存
	* @param @param detailEty
	* @param @param isAgain  是否重试
	* @param @throws Exception
	* @return Boolean
	 */
	private Boolean postMex(BitTradeDetail detailEty,boolean isAgain) throws Exception{
		Boolean flage = false;
		StringBuilder logMsg = new StringBuilder("BitMex下单：");
		if(isAgain){
			logMsg.append("重试下单,");
		}
		MexOrderInterfaceService service = SpringContextHolder.getBean(MexOrderInterfaceService.class);
		long startTime = System.currentTimeMillis();
		log.info(">> mex post Amount =  "+detailEty.getAmount());
		try {
			if(!setMexLeverage){
				// 设置杠杆倍数
				service.post_leverage(detailEty.getSymbol(),10D);
				setMexLeverage = true;
			}
			String direction = detailEty.getDirection();
			String side = "";
			if(Constants.DIRECTION_BUY_UP.equals(direction) || Constants.DIRECTION_SELL_DOWN.equals(direction)){
				side = "Buy"; // 1开多,4平空
			}else if(Constants.DIRECTION_BUY_DOWN.equals(direction) || Constants.DIRECTION_SELL_UP.equals(direction)){
				side = "Sell"; // 2开空，3平多
			}
			logMsg.append("币种：").append(detailEty.getSymbol()).append("，方向："+side).append("，数量：").append(detailEty.getAmount());
			// doPost
			String json = service.post_order(detailEty.getSymbol(),"Market",0D,detailEty.getAmount().doubleValue(), side, detailEty.getTradeCode());
			if(StringUtils.isNotBlank(json)){
				JSONObject jobJ = JSONObject.parseObject(json);
				if(jobJ.containsKey("orderID")){
					String orderId = jobJ.getString("orderID");
					log.info(">> Bitmex Post success oId = "+orderId);
					detailEty.setRemarks(orderId);
					flage = true;
				}
			}
			// 检查下单后保证金变化,保存下单后保证金
			flage = checkOrder(BitMexInterConstants.PLATFORM_CODE,flage);
			
			// TODO 
			//flage = true;
		} catch (ServiceException e) {
			// 参数失败
			flage = false;
			logMsg.append(",异常信息：").append(e.getMessage());
			log.error(">> postMex Biz_error :",e.getMessage());
		} catch (Exception e) {
			log.error(">> postMex error :",e);
			logMsg.append(",重试前异常信息：").append(e.getMessage());
			// 记录保证金变化
			checkOrder(BitMexInterConstants.PLATFORM_CODE,flage);
			// 网络异常，重试一次
			if(!isAgain){
				Thread.sleep(HedgeUtils.sleepTime_15); // 休眠15秒防止频繁调用超过次数
				flage = postMex(detailEty,true);
			}else{
				flage = false;
			}
		}
		long endTime = System.currentTimeMillis();
		log.info(">> postMex used time ="+(endTime-startTime));
		if(flage){
			//save DB
			BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
			bean.save(detailEty);
		}
		logMsg.append(",交易结果：").append(flage);
		HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_TRADE, logMsg.toString(), "1");
		return flage;
	}
	
	// okex 20016\20018 重试6次
	//private int againTime = 0;
	/**
	 * 下Okex 订单并保存
	* @param @param detailEty
	* @param @param isAgain  是否重试
	* @param @throws Exception
	* @return Boolean
	 */
	private Boolean postOkex(BitTradeDetail detailEty,boolean isAgain) throws Exception{
		Boolean flage = false;
		StringBuilder logMsg = new StringBuilder("Okex下单：");
		if(isAgain){
			logMsg.append("重试下单,");
		}
		OrderInterfaceService service = SpringContextHolder.getBean(OrderInterfaceService.class);
		long startTime = System.currentTimeMillis();
		log.info(">> mex Okex Amount =  "+detailEty.getAmount());
		try {
			String direction = detailEty.getDirection();
			Double curPrice = (Double)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_BTCOKEX_PRICE_KEY);
			String side = OkexOrder.getTypeStr(direction);
			logMsg.append("币种：").append(detailEty.getSymbol()).append("，方向："+side).append("，数量：").append(detailEty.getAmount());
			// doPost
			String json = service.future_trade(detailEty.getSymbol(), "quarter", curPrice.toString(),
					detailEty.getAmount().toString(), direction, "1", "10");
			if(StringUtils.isNotBlank(json)){
				JSONObject jobJ = JSONObject.parseObject(json);
				if(jobJ.containsKey("result") && jobJ.containsKey("order_id") && jobJ.getBooleanValue("result")){
					Long orderId = jobJ.getLong("order_id");
					log.info(">> okex Post success oId = "+orderId);
					detailEty.setRemarks(orderId.toString());
					flage = true;
					//againTime = 0;
				}
				if(jobJ.containsKey("result") && jobJ.containsKey("error_code")){
					 int code = jobJ.getInteger("error_code");
					 if(20004==code || 20003== code){
						 // 爆仓账户冻结
						 flage = true;
					 }
					 else if(20016==code || 20018==code){
						 // 循环10次
						 for (int i = 0; i < 10; i++) {
							 log.info(">> okex Post 1618 error! i="+i);
							 String jsStr = service.future_trade(detailEty.getSymbol(), "quarter", curPrice.toString(),
									 detailEty.getAmount().toString(), direction, "1", "10");
							 if(StringUtils.isNotBlank(jsStr)){
								JSONObject jObj = JSONObject.parseObject(jsStr);
								if(jObj.containsKey("result") && jObj.containsKey("order_id") && jObj.getBooleanValue("result")){
									Long orderId = jObj.getLong("order_id");
									log.info(">> okex Post 1618 success oId = "+orderId);
									detailEty.setRemarks(orderId.toString());
									flage = true;
									break;
								}
							 }
						 }
					 }
				}
			}
			
			// 检查下单后保证金变化,保存下单后保证金
			flage = checkOrder(OkexInterConstants.PLATFORM_CODE,flage);
		
			// TODO
			//flage = true;
		} catch (Exception e) {
			log.info(">> !!okex Post isAgain = "+isAgain);
			logMsg.append(",异常信息：").append(e.getMessage());
			// 重试一次
			if(!isAgain){
				Thread.sleep(HedgeUtils.sleepTime_15); // 休眠15秒防止频繁调用超过次数
				flage = postOkex(detailEty,true);
			}else{
				flage = false;
				log.error(">> postOkex error :",e);
			}
		}
		long endTime = System.currentTimeMillis();
		log.info(">> postOkex used time ="+(endTime-startTime));
		if(flage){
			//save DB
			BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
			bean.save(detailEty);
		}
		logMsg.append(",交易结果：").append(flage);
		HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_TRADE, logMsg.toString(), "1");
		return flage;
	}

	// 保存交易主表信息
	private void saveTradeMainEty(BitTrade entity) throws Exception{
		BitTradeService bean = SpringContextHolder.getBean(BitTradeService.class);
		bean.save(entity);
	}
	
	/**
	 * 计算 完整一次交易订单的收入，费用，盈利
	* @param @param entity
	 */
	private void calculateOrder(BitTrade entity) throws Exception{
		BitTradeDetailService bean = SpringContextHolder.getBean(BitTradeDetailService.class);
		
		BitTradeDetail detailEty = new BitTradeDetail();
		detailEty.setTradeCode(tradeCode);
		List<BitTradeDetail> list = bean.findList(detailEty);
		//boolean upIsA = false; // A币种是否为做多
		if(null != list && !list.isEmpty()){
			// 以币种计算  [0]开， [1]平
			BitTradeDetail[] symbolUp = new BitTradeDetail[2];
			BitTradeDetail[] symbolDown = new BitTradeDetail[2];
			for (BitTradeDetail dal : list) {
				// `direction` 交易方向：1:开多, 2:开空,  3:平多,  4:平空
				String direction = dal.getDirection();
				
				if(Constants.DIRECTION_BUY_UP.equals(direction)){
					/*if(req.getSymbolA().equals(dal.getSymbol())){
						upIsA = true;
					}*/
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
			// -- down 收入
			BigDecimal revenue_down = totalKai_down.subtract(totalPin_down);
			// 总收入
			BigDecimal totalRevenue = revenue_up.add(revenue_down);
			
			/** 保存账户保证金  */
			BigDecimal feeRateA = req.getFeeRateA();
			BigDecimal feeRateB = req.getFeeRateB();
			
			// 费用 
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
	 * 获取币种的当前价格
	* @param @param symbol
	* @return double
	 */
	private BigDecimal getCachePrice(String symbol) throws Exception{
		String cacheKey = "";
		if(Constants.SYMBOL_BTC_USD.equals(symbol)){
			cacheKey = Constants.CACHE_BTCOKEX_PRICE_KEY;
		}else if(Constants.SYMBOL_LTC_USD.equals(symbol)){
			cacheKey = Constants.CACHE_LTCOKEX_PRICE_KEY;
		}else if(Constants.SYMBOL_XBTUSD.equals(symbol)){
			cacheKey = Constants.CACHE_XBTUSDMEX_PRICE_KEY;
		}else if(Constants.SYMBOL_XBTQAE.equals(symbol)){
			cacheKey = Constants.CACHE_XBTQAEMEX_PRICE_KEY;
		}else if(Constants.SYMBOL_LTCQAE.equals(symbol)){
			cacheKey = Constants.CACHE_LTCQAEMEX_PRICE_KEY;
		}
		Double curPrice = (Double)EhCacheUtils.get(Constants.PRICE_CACHE,cacheKey);
		return new BigDecimal(curPrice);
	}
	
	private BigDecimal getAccountBalance(String symbol,String platform) throws Exception{
		BigDecimal balance = this.ZERO;
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
		det.setUseId(user.getId());
		det.setMonitorPice(price);
		det.setTradeCode(tradeCode);
		det.setDetailType(detailType);
		det.setIfBurstBarn(Constants.STATUS_CLOSE);
		det.setTradePrice(ZERO);// 默认0
		return det;
	}
	
	/**
	 * 检查是否下单成功,并保存下单后保证金
	* @return boolean
	 */
	private boolean checkOrder(String platform, boolean flag){
		log.error(">> checkOrder checkOrder="+platform+",flag="+flag);
		if(!flag){
			try {
				// 休眠2秒 等待 平台保证金变化
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error(">> checkOrder sleep:",e);
			}
		}
		BigDecimal curMargin = new BigDecimal(0);
		BigDecimal oldMargin = new BigDecimal(0);
		if(OkexInterConstants.PLATFORM_CODE.equals(platform)){
			// okex
			// http 获取账户可用保证金
			curMargin = HedgeUtils.getOkexMarginByHttp();
			oldMargin = okexMargin;
			if(!flag && curMargin != null && curMargin.compareTo(okexMargin)!=0){
				// 当前保证金与上次保证金不相等，下单成功
				flag = true;
			}
			if(flag && curMargin != null){
				okexMargin = curMargin;
			}
		}else if(BitMexInterConstants.PLATFORM_CODE.equals(platform)){
			// mex 
			// 1. http 获取账户可用保证金
			curMargin = HedgeUtils.getMexMarginByHttp();
			oldMargin = mexMargin;
			if(null == curMargin){
				// 2. socket 获取账户可用保证金
				curMargin = HedgeUtils.getMexMarginBySocket();
			}
			if(!flag && curMargin != null && curMargin.compareTo(mexMargin)!=0){
				// 当前保证金与上次保证金不相等，下单成功
				flag = true;
			}
			if(flag && curMargin != null){
				mexMargin = curMargin;
			}
		}
		// 记录日志
		String msg = "验证下单-平台："+platform+"，最新保证金："+curMargin+"，原保证金："+oldMargin+"，下单状态："+flag;
		HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_TRADE, msg, "1");
		return flag;
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
	
	private long startTimeAccount = System.currentTimeMillis();
	private long startTimeLog = System.currentTimeMillis();
	/** 每二十秒打印一次日志，更新 TradeTaskReq
	 * @throws
	 */
	private void AutoTask() throws Exception{
		BigDecimal agio = getAgio(req.getSymbolA(), req.getSymbolB());
		BigDecimal priceA = getCachePrice(req.getSymbolA());
		BigDecimal priceB = getCachePrice(req.getSymbolB());
		log.info(">> AutoTask AGIO ="+pDF.format(agio) +",priceA="+pDF.format(priceA)
				+",priceB="+pDF.format(priceB));
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
		// 查询账户余额信息
		long currTimeAccount = System.currentTimeMillis();
		if(currTimeAccount - startTimeAccount > 300000){// 300秒查一次
			HedgeUtils.updateAccount();
			startTimeAccount = currTimeAccount;
			// 检查获取价格是否健康
			Long threeMinute = currTimeAccount - 3*60*1000; // 3分钟前
			// ok time
			Long okTime = (Long)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.SYMBOL_OKEX_TIME);
			if(null == okTime || okTime < threeMinute){
				okPriceHealth = false;
			}
			// Mex time
			Long mexTime = (Long)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.SYMBOL_MEX_TIME);
			if(null == mexTime || mexTime < threeMinute){
				mexPriceHealth = false;
			}
		}
		if(currTimeAccount - startTimeLog > 300000){// 300\60秒查一次
			startTimeLog = currTimeAccount;
			// 监控记录
			StringBuilder logMsg = new StringBuilder("运行：健康，当前差价：");
			logMsg.append(pDF.format(agio));
			HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_TRADE, logMsg.toString(), "1");
		}
	}
	
	// 短信发送时间
	private long smsSendTime = 0L;
	// okex获取价格是否健康
	boolean okPriceHealth = true;
	// mex获取价格是否健康
	boolean mexPriceHealth = true;
	// okex下单是否健康
	boolean okOrderHealth = true;
	// mex下单是否健康
	boolean mexOrderHealth = true;
	//监控健康检查，发送短信提醒
	private void checkHealth(){
		String msg = null;
		if (!okPriceHealth) {
			msg = "平台：okex,无法获取实时价格。";
		} else if (!mexPriceHealth) {
			msg = "平台：mex,无法获取实时价格。";
		} else if (!okOrderHealth) {
			msg = "平台：okex，未操作项：test，数量：10";
		} else if (!mexOrderHealth) {
			msg = "平台：mex，未操作项：test，数量：10";
		}
		if(null != msg){
			long currTimeAccount = System.currentTimeMillis();
			if(smsSendTime == 0L){
				smsSendTime = currTimeAccount;
			}
			if(currTimeAccount - smsSendTime > 180000){// 180秒查一次
				//msg = "平台：test，未操作项：test，数量：10";
				HedgeUtils.saveLog(user.getId(), Constants.LOG_TYPE_TRADE, msg, "1");
				HedgeUtils.sendSMS(user.getId(), user.getMobile(), msg);
				smsSendTime = currTimeAccount;
				okPriceHealth = true;
				mexPriceHealth = true;
				okOrderHealth = true;
				mexOrderHealth = true;
			}
		}
	}
}
