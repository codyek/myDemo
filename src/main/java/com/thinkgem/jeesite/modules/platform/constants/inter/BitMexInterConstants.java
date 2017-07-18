package com.thinkgem.jeesite.modules.platform.constants.inter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;
import com.thinkgem.jeesite.modules.platform.service.inter.InterfacesService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * bitmex接口信息常量类
* @ClassName: OkexInterConstants 
* @author EK huangone 
* @date 2017-7-6 下午9:34:12 
*
 */
public class BitMexInterConstants {
	protected static Logger logger = LoggerFactory.getLogger(BitMexInterConstants.class);
	
	/** 交易平台  */
	private final static String PLATFORM_CODE = "bitmex";
	
	/** 交易平台域名地址字典  默认 https://www.bitmex.com  */
	private final static String URL_PREX_TYPE = "bit_url_prex";
	private final static String URL_PREX_LABEL = "bitmex_url_prex";
	/** bitmex交易平台域名地址  */
	public static String BITMEX_PREX_URL;
	
	
	private static String GET_ORDER_CODE = "get_order";
	/**  获取你的订单  */
	public static String GET_ORDER_URL;
	
	private static String PUT_ORDER_CODE = "put_order";
	/**  修改公开订单的数量或价格  */
	public static String PUT_ORDER_URL;
	
	private static String POST_ORDER_CODE = "post_order";
	/**  创建新订单  */
	public static String POST_ORDER_URL;
	
	private static String DEL_ORDER_CODE = "delete_order";
	/**  取消订单, 发送多个订单ID批量取消  */
	public static String DEL_ORDER_URL;
	
	private static String DEL_ORDER_ALL_CODE = "delete_order_all";
	/**  取消您的所有订单  */
	public static String DEL_ORDER_ALL_URL;
	
	private static String PUT_ORDER_BULK_CODE = "put_order_bulk";
	/**  修改相同符号的多个订单  */
	public static String PUT_ORDER_BULK_URL;
	
	private static String POST_ORDER_BULK_CODE = "post_order_bulk";
	/**  为同一符号创建多个新订单  */
	public static String POST_ORDER_BULK_URL;
	
	private static String POST_ORDER_CANCELALLAFTER_CODE = "post_order_cancelAllAfter";
	/**  在指定的超时后自动取消所有订单  */
	public static String POST_ORDER_CANCELALLAFTER_URL;
	
	private static String POST_ORDER_CLOSEPOSITION_CODE = "post_order_closePosition";
	/**  关闭一个位置  */
	public static String POST_ORDER_CLOSEPOSITION_URL;
	
	private static String GET_ORDERBOOK_L2_CODE = "get_orderBook_L2";
	/**  以垂直格式获取当前订单  */
	public static String GET_ORDERBOOK_L2_URL;
	
	private static String GET_USER_MARGIN_CODE = "get_user_margin";
	/**  获取您帐户的保证金状态  */
	public static String GET_USER_MARGIN_URL;
	
	private static String _CODE = "aaa";
	/**  aaaa  */
	public static String _URL;


	static{
		refresh();
	}
	
	public static void refresh(){
		logger.info(">> refresh interface url ...........");
		// 交易平台访问地址域名  默认 https://www.bitmex.com
		BITMEX_PREX_URL = DictUtils.getDictValue(URL_PREX_LABEL, URL_PREX_TYPE, "https://www.bitmex.com");
		
		InterfacesService interService = SpringContextHolder.getBean(InterfacesService.class);
		Interfaces inter = new Interfaces();
		inter.setPlatform(PLATFORM_CODE);
		inter.setDelFlag(inter.DEL_FLAG_NORMAL);
		List<Interfaces> list = interService.findList(inter);
		if(null != list && !list.isEmpty()){
			for (Interfaces ifs : list) {
				String code = ifs.getCode();
				
				if(GET_ORDER_CODE.equals(code)){
					GET_ORDER_URL = ifs.getUrl();
				}
				if(PUT_ORDER_CODE.equals(code)){
					PUT_ORDER_URL = ifs.getUrl();
				}
				if(POST_ORDER_CODE.equals(code)){
					POST_ORDER_URL = ifs.getUrl();
				}
				if(DEL_ORDER_CODE.equals(code)){
					DEL_ORDER_URL = ifs.getUrl();
				}
				if(DEL_ORDER_ALL_CODE.equals(code)){
					DEL_ORDER_ALL_URL = ifs.getUrl();
				}
				if(PUT_ORDER_BULK_CODE.equals(code)){
					PUT_ORDER_BULK_URL = ifs.getUrl();
				}
				if(POST_ORDER_BULK_CODE.equals(code)){
					POST_ORDER_BULK_URL = ifs.getUrl();
				}
				if(POST_ORDER_CANCELALLAFTER_CODE.equals(code)){
					POST_ORDER_CANCELALLAFTER_URL = ifs.getUrl();
				}
				if(POST_ORDER_CLOSEPOSITION_CODE.equals(code)){
					POST_ORDER_CLOSEPOSITION_URL = ifs.getUrl();
				}
				if(GET_ORDERBOOK_L2_CODE.equals(code)){
					GET_ORDERBOOK_L2_URL = ifs.getUrl();
				}
				if(GET_USER_MARGIN_CODE.equals(code)){
					GET_USER_MARGIN_URL = ifs.getUrl();
				}
			
			}
			logger.info(">> refresh interface url list size = "+list.size());
		}else { 
			logger.error(">> refresh interface url list is null !!!!!");
		}
	}
}
