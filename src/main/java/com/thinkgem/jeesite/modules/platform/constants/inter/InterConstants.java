package com.thinkgem.jeesite.modules.platform.constants.inter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;
import com.thinkgem.jeesite.modules.platform.service.inter.InterfacesService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 接口信息常量类
* @ClassName: InterConstants 
* @Description: TODO
* @author EK huangone 
* @date 2017-7-6 下午9:34:12 
*
 */
public class InterConstants {
	protected static Logger logger = LoggerFactory.getLogger(InterConstants.class);
	
	/** 交易平台  */
	private final static String PLATFORM_CODE = "okex";
	
	/** 交易平台域名地址字典  默认 https://www.okex.com  */
	private final static String URL_PREX_TYPE = "bit_url_prex";
	private final static String URL_PREX_LABEL = "okex_url_prex";
	
	private static String FUTURE_TICKER_CODE = "future_ticker";
	/**  获取OKEX合约行情  */
	public static String GFUTURE_TICKER_URL;
	
	private static String FUTURE_DEPTH_CODE = "future_depth";
	/**  获取OKEX合约深度信息  */
	public static String GFUTURE_DEPTH_URL;

	private static String FUTURE_TRADES_CODE = "future_trades";
	/**  获取OKEX合约交易记录信息  */
	public static String GFUTURE_TRADES_URL;
	
	private static String FUTURE_INDEX_CODE = "future_index";
	/**  获取OKEX合约指数信息  */
	public static String GFUTURE_INDEX_URL;
	
	private static String EXCHANGE_RATE_CODE = "exchange_rate";
	/**  获取美元人民币汇率  */
	public static String GEXCHANGE_RATE_URL;
	
	private static String FUTURE_ESTIMATED_PRICE_CODE = "future_estimated_price";
	/**  获取交割预估价  */
	public static String GFUTURE_ESTIMATED_PRICE_URL;
	
	private static String FUTURE_KLINE_CODE = "future_kline";
	/**  获取虚拟合约的K线数据  */
	public static String GFUTURE_KLINE_URL;
	
	private static String FUTURE_HOLD_AMOUNT_CODE = "future_hold_amount";
	/**  获取当前可用合约总持仓量  */
	public static String GFUTURE_HOLD_AMOUNT_URL;
	
	private static String FUTURE_PRICE_LIMIT_CODE = "future_price_limit";
	/**  获取合约最高买价和最低卖价  */
	public static String GFUTURE_PRICE_LIMIT_URL;
	
	//////////////////////    post     ///////////////////////////
	private static String FUTURE_USERINFO_CODE = "future_userinfo";
	/**  获取OKEX合约账户信息（全仓）  */
	public static String PFUTURE_USERINFO_URL;
	
	private static String FUTURE_POSITION_CODE = "future_position";
	/**  获取用户持仓获取OKEX合约账户信息（全仓）  */
	public static String PFUTURE_POSITION_URL;
	
	private static String FUTURE_TRADE_CODE = "future_trade";
	/**  合约下单  */
	public static String PFUTURE_TRADE_URL;
	
	private static String FUTURE_TRADES_HISTORY_CODE = "future_trades_history";
	/**  获取OKEX合约交易历史（非个人）  */
	public static String PFUTURE_TRADES_HISTORY_URL;
	
	private static String FUTURE_BATCH_TRADE_CODE = "future_batch_trade";
	/**  批量下单  */
	public static String PFUTURE_BATCH_TRADE_URL;
	
	private static String FUTURE_CANCEL_CODE = "future_cancel";
	/**  取消合约订单  */
	public static String PFUTURE_CANCEL_URL;
	
	private static String FUTURE_ORDER_INFO_CODE = "future_order_info";
	/**  获取合约订单信息  */
	public static String PFUTURE_ORDER_INFO_URL;
	
	private static String FUTURE_ORDERS_INFO_CODE = "future_orders_info";
	/**  批量获取合约订单信息  */
	public static String PFUTURE_ORDERS_INFO_URL;
	
	private static String FUTURE_USERINFO_4FIX_CODE = "future_userinfo_4fix";
	/**  获取逐仓合约账户信息  */
	public static String PFUTURE_USERINFO_4FIX_URL;
	
	private static String FUTURE_POSITION_4FIX_CODE = "future_position_4fix";
	/**  逐仓用户持仓查询  */
	public static String PFUTURE_POSITION_4FIX_URL;
	
	private static String FUTURE_EXPLOSIVE_CODE = "future_explosive";
	/**  获取合约爆仓单  */
	public static String PFUTURE_EXPLOSIVE_URL;

	static{
		refresh();
	}
	
	public static void refresh(){
		logger.info(">> refresh interface url ...........");
		// 交易平台访问地址域名  默认 https://www.okex.com
		String url_prex = DictUtils.getDictValue(URL_PREX_LABEL, URL_PREX_TYPE, "https://www.okex.com");
		
		InterfacesService interService = SpringContextHolder.getBean(InterfacesService.class);
		Interfaces inter = new Interfaces();
		inter.setPlatform(PLATFORM_CODE);
		inter.setDelFlag(inter.DEL_FLAG_NORMAL);
		List<Interfaces> list = interService.findList(inter);
		if(null != list && !list.isEmpty()){
			for (Interfaces ifs : list) {
				String code = ifs.getCode();
				
				if(FUTURE_TICKER_CODE.equals(code)){
					GFUTURE_TICKER_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_DEPTH_CODE.equals(code)){
					GFUTURE_DEPTH_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_TRADES_CODE.equals(code)){
					GFUTURE_TRADES_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_INDEX_CODE.equals(code)){
					GFUTURE_INDEX_URL = url_prex + ifs.getUrl();
				}
				if(EXCHANGE_RATE_CODE.equals(code)){
					GEXCHANGE_RATE_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_ESTIMATED_PRICE_CODE.equals(code)){
					GFUTURE_ESTIMATED_PRICE_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_KLINE_CODE.equals(code)){
					GFUTURE_KLINE_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_HOLD_AMOUNT_CODE.equals(code)){
					GFUTURE_HOLD_AMOUNT_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_PRICE_LIMIT_CODE.equals(code)){
					GFUTURE_PRICE_LIMIT_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_USERINFO_CODE.equals(code)){
					PFUTURE_USERINFO_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_POSITION_CODE.equals(code)){
					PFUTURE_POSITION_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_TRADE_CODE.equals(code)){
					PFUTURE_TRADE_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_TRADES_HISTORY_CODE.equals(code)){
					PFUTURE_TRADES_HISTORY_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_BATCH_TRADE_CODE.equals(code)){
					PFUTURE_BATCH_TRADE_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_CANCEL_CODE.equals(code)){
					PFUTURE_CANCEL_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_ORDER_INFO_CODE.equals(code)){
					PFUTURE_ORDER_INFO_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_ORDERS_INFO_CODE.equals(code)){
					PFUTURE_ORDERS_INFO_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_USERINFO_4FIX_CODE.equals(code)){
					PFUTURE_USERINFO_4FIX_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_POSITION_4FIX_CODE.equals(code)){
					PFUTURE_POSITION_4FIX_URL = url_prex + ifs.getUrl();
				}
				if(FUTURE_EXPLOSIVE_CODE.equals(code)){
					PFUTURE_EXPLOSIVE_URL = url_prex + ifs.getUrl();
				}
			}
			logger.info(">> refresh interface url list size = "+list.size());
		}else { 
			logger.error(">> refresh interface url list is null !!!!!");
		}
	}
}
