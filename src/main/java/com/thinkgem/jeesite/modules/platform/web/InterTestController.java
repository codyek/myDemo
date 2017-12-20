package com.thinkgem.jeesite.modules.platform.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.mongodb.model.LtcTradeData;
import com.thinkgem.jeesite.modules.mongodb.service.LtcTradeDataInterface;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.entity.trade.TradeTaskReq;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexAccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexOrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.InfoInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.WebsocketClientEndpoint;
import com.thinkgem.jeesite.modules.platform.task.MexAccountSocket;
import com.thinkgem.jeesite.modules.platform.task.MexTask;

/**
 * inter测试Controller
 * @author hzf
 * @version 2017-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/inter/test")
public class InterTestController extends BaseController {

	@Autowired
	private InfoInterfaceService interService;
	
	@Autowired
	private AccountInterfaceService accountService;
	
	@Autowired
	private OrderInterfaceService orderService;
	
	@Autowired
	private MexOrderInterfaceService mexOrderService;
	
	@Autowired
	private MexAccountInterfaceService mexAccountService;
	
	@RequestMapping(value = {"gets", ""})
	@ResponseBody
	public String gest(HttpServletRequest request, HttpServletResponse response, Model model) {
		String symbol = "btc_usd";
		String contractType = "this_week";
		String type = "5min";
		Integer size = 20;
		Integer merge = 0;
		
		Long since = 0L;
		
		String ret = "";
		try {
			System.out.println("11. start ------------- future_depth");
			ret = interService.future_depth(symbol, contractType, size, merge);
			
			System.out.println("22. start ------------- exchange_rate");
			ret = interService.exchange_rate();
			
			System.out.println("33. start ------------- future_estimated_price");
			ret = interService.future_estimated_price(symbol);
			
			
			
			System.out.println("55. start ------------- future_index");
			ret = interService.future_index(symbol);
			
			System.out.println("66. start ------------- future_kline");
			ret = interService.future_kline(symbol, contractType, type, size, since);
			
			System.out.println("77. start ------------- future_price_limit");
			ret = interService.future_price_limit(symbol, contractType);
			
			System.out.println("88. start ------------- future_ticker");
			ret = interService.future_ticker(symbol, contractType);
			
			System.out.println("99. start ------------- future_trades");
			ret = interService.future_trades(symbol, contractType);
			
			System.out.println("44. start ------------- future_hold_amount");
			ret = interService.future_hold_amount(symbol, contractType);
			
			System.out.println("----- ret = "+ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "get okkkkk!";
	}

	@RequestMapping(value = {"posts", ""})
	@ResponseBody
	public String dopost(HttpServletRequest request, HttpServletResponse response, Model model) {
		String symbol = "btc_usd";
		String contractType = "this_week";
		String type = "1";
		Integer size = 20;
		Integer merge = 0;
		
		Long since = 0L;
		
		String ret = "";
		try {
			//System.out.println("11. start ------------- future_depth");
			//ret = accountService.future_userinfo();
			
			//System.out.println("22. start ------------- future_position");
			//ret = accountService.future_position(symbol, contractType);
			
			//System.out.println("33. start ------------- future_position");
			//ret = accountService.future_position_4fix(symbol, contractType, type);
			
			//System.out.println("44. start ------------- future_position");
			//ret = accountService.future_userinfo_4fix();
			 
			//// ---------------------------- order 
			
			System.out.println("55. start ------------- future_position");
			ret = orderService.future_trade(symbol, contractType, "10.134", "1", "1", "0", "10");
			
			System.out.println("66. start ------------- future_position");
			ret = orderService.future_order_info(symbol, contractType, "123321", "1", "1", "2");
			
			System.out.println("77. start ------------- future_position");
			ret = orderService.future_cancel(symbol, contractType, "123321");
			
			
			System.out.println("-----post ret = "+ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "post okkkkk!";
	}
	
	// --------------------------------------------------------------------
	

	@RequestMapping(value = {"mex", ""})
	@ResponseBody
	public String testMex(HttpServletRequest request, HttpServletResponse response, Model model) {
		String symbol = "XBTUSD";
		
		String ret = "";
		try {
			 
			//// ---------------------------- order 
			
			System.out.println("11. start ------------- get_order");
			ret = mexOrderService.get_order(symbol, 50D);
			
			System.out.println("22. start ------------- post_order");
			ret = mexOrderService.post_order(symbol, "Limit", 2020D, 1D, "Sell", "my_order");
			
			System.out.println("33. start ------------- get_orderBookL2");
			ret = mexOrderService.get_orderBookL2(symbol, 0D);
			
			System.out.println("44. start ------------- get_user_margin");
			ret = mexAccountService.get_user_margin("XBt");
			
			
			System.out.println("-----mex ret = "+ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "mex okkkkk!";
	}
	
	@RequestMapping("websk")
	@ResponseBody
    public String websk(String name){
		System.out.println("websk ====" + name);
		
		//String url = "wss://real.okcoin.cn:10440/websocket/okcoinapi";
		//String url = "wss://www.bitmex.com/realtime";
		
		//String msg = "{'event':'addChannel','channel':'ok_btccny_ticker'}";
		//String msg = "{\"op\":\"help\"}";
		//String msg = "{\"op\":\"subscribe\",\"args\":[\"orderBookL2:XBTUSD\"]}";
		/*String msg = "{\"op\":\"subscribe\",\"args\":[\"instrument:XBTUSD\"," +
				"\"instrument:XBTU17\"," +
				"\"instrument:LTCU17\"]}";*/
		/*try {
			// open websocket
			final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(
					new URI(url));

			// add listener
			clientEndPoint
					.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
						public void handleMessage(String message) {
							System.out.println("return =" + message);
						}
					});

			// send message to websocket
			clientEndPoint.sendMessage(msg);

			// wait 5 seconds for messages from websocket
			while (true) {
				Thread.sleep(5000);
				clientEndPoint.sendMessage("ping");
			}

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: "
					+ ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: "
					+ ex.getMessage());
		}*/
		Object obj = new Object();
		if("btc".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_BTCOKEX_PRICE_KEY);
		}else if("ltc".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_LTCOKEX_PRICE_KEY);
		}else if("xbt".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_XBTUSDMEX_PRICE_KEY);
		}else if("xbtu17".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_XBTQAEMEX_PRICE_KEY);
		}else if("ltcu17".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_LTCQAEMEX_PRICE_KEY);
			
		}
		return obj.toString();
	}
	
	@Autowired
	private LtcTradeDataInterface ltcTradeData;
	
	@RequestMapping(value = "postPriceLtc")
	@ResponseBody
	public JSONArray taskPostLtc(String startDt, String endDt) throws Exception {
		logger.info(">>>-- postPrice LTC  input="+ startDt + " , " +endDt);
		if(null != startDt && null != endDt){
			long start = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(startDt), "yyyyMMddHHmmss"));
			long end = Long.parseLong(
					DateUtils.formatDate(
							DateUtils.parseDate(endDt), "yyyyMMddHHmmss"));
			logger.info(">>>-- postPrice LTC  start="+ start + " , end=" +end);
			Direction direction = Direction.ASC;
			List<LtcTradeData> list = ltcTradeData.findByTimeBetween(start, end,new Sort(direction,"time"));
			JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
	        return arr;
		}else{
			return null;
		}
	}
	
	@RequestMapping(value = "postPriceLtcOne")
	@ResponseBody
	public JSONArray taskPostLtcOne(String date) throws Exception {
		logger.info(">>>-- postPrice LTC  input="+ date);
		if(null != date){
			long start = Long.parseLong(date);
			logger.info(">>>-- postPrice LTC  start="+ start);
			Direction direction = Direction.ASC;
			//List<LtcTradeData> list = ltcTradeData.findByTimeBetween(start, end,new Sort(direction,"time"));
			PageRequest pageable = new PageRequest(0,1,new Sort(direction,"time"));
			List<LtcTradeData> list = ltcTradeData.findByTimePageable(start, pageable);
			JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
	        return arr;
		}else{
			return null;
		}
	}
	
	@RequestMapping(value = {"testGetOrder", ""})
	public String testGetOrderPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		//model.addAttribute("page", page);
		return "modules/platform/trade/TestGetOrder";
	}
	
	@RequestMapping(value = {"testInterForm", ""})
	public String testInterFormPage(TradeTaskReq req,HttpServletRequest request, HttpServletResponse response, Model model) {
		//model.addAttribute("page", page);
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/trade/TestInterForm";
	}
	
	@RequestMapping("saveOrders")
	@ResponseBody
    public String saveOrders(TradeTaskReq req){
		String msg = "";
		try {
			String symbol = req.getSymbolA();
			String dir = req.getOpenDirA();
			if("btc_usd".equals(symbol)){
				Double curPrice = (Double)EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_BTCOKEX_PRICE_KEY);
				// okex
				msg = orderService.future_trade(symbol, "quarter", curPrice.toString(), req.getAmountA().toString(), dir, "1", "10");
			}else if("XBTUSD".equals(symbol) || "XBTH18".equals(symbol)){
				// mex
				// 设置杠杆倍数
				//msg = mexOrderService.post_leverage(symbol,10D);
				mexOrderService.post_leverage(symbol,10D);
				//logger.info(">>>　　post_leverage＝　"+msg);
				String side = "";
				if(Constants.DIRECTION_BUY_UP.equals(dir) || Constants.DIRECTION_SELL_DOWN.equals(dir)){
					side = "Buy"; // 1开多,4平空
				}else if(Constants.DIRECTION_BUY_DOWN.equals(dir) || Constants.DIRECTION_SELL_UP.equals(dir)){
					side = "Sell"; // 2开空，3平多
				}
				msg = mexOrderService.post_order(symbol, "Market",0D, req.getAmountA().doubleValue(), side, req.getType());
			}
		} catch (Exception e) {
			msg = e.getMessage();
			logger.error(">> getOkexOrders error:",e);
		}
		
		return msg;
	}
	
	@RequestMapping("cancelMexOrder")
	@ResponseBody
    public String cancelMexOrder(String orderID,String text){
		String msg = "";
		try {
			msg = mexOrderService.delete_order(orderID, text);
		} catch (Exception e) {
			msg = e.getMessage();
			logger.error(">> cancelMexOrder error:",e);
		}
		return msg;
	}
	
	@RequestMapping("getOkexOrders")
	@ResponseBody
    public String getOkexOrders(String symbol,String status){
		String msg = "";
		try {
			msg = orderService.future_order_info(symbol, "quarter", "-1", status, "0", "20");
		} catch (Exception e) {
			logger.error(">> getOkexOrders error:",e);
		}
		
		return msg;
	}
		
	@RequestMapping("getMexOrders")
	@ResponseBody
    public String getMexOrders(String symbol){
		String msg = "";
		try {
			msg = mexOrderService.get_order(symbol, 20D);
		} catch (Exception e) {
			logger.error(">> getMexOrders error:",e);
		}
		
		return msg;
	}
	
	@RequestMapping(value = {"testGetAccount", ""})
	public String testGetAccount(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/platform/trade/TestGetAccount";
	}
	
	@RequestMapping("getMexAccount")
	@ResponseBody
    public String getMexAccount(){
		String msg = "";
		try {
			msg = mexAccountService.get_user_margin("XBt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	@RequestMapping("getOkexAccount")
	@ResponseBody
    public String getOkexAccount(){
		String msg = "";
		try {
			msg = accountService.future_userinfo_4fix();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 *  计算收益页面
	 */
	@RequestMapping(value = {"showTool", ""})
	public String showTool(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/platform/tool/countIncome";
	}
	
	/**
	 *  数据回测页面
	 */
	@RequestMapping(value = {"backTest", ""})
	public String backTest(TradeTaskReq req,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("TradeTaskReq", req);
		return "modules/platform/backTest/backTest";
	}
	
	@Autowired
    @Qualifier("mexAccountSocket") 
    private MexAccountSocket mexAccountSocket;
	
	
	@RequestMapping("mexAccountSocket")
	@ResponseBody
    public String mexAccountSocket(){
		String msg = "success";
		try {
			mexAccountSocket.mexAccountsocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	@RequestMapping("mexAccountSocketClose")
	@ResponseBody
    public String mexAccountSocketClose(){
		String msg = "success";
		try {
			mexAccountSocket.onClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	@RequestMapping("getMexMargin")
	@ResponseBody
    public String getMexMargin(){
		String msg = "success";
		try {
			Object oo = EhCacheUtils.get(Constants.PRICE_CACHE, Constants.CACHE_MEX_MARGIN_KEY);
			msg+=" = "+oo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	@RequestMapping("mexAccountSocketOne")
	@ResponseBody
    public String mexAccountSocketOne(){
		String msg = "success";
		try {
			mexAccountSocket.mexAccountsocket();
			System.out.println("  >>  sss  ");
			Thread.sleep(12000);  // 
			mexAccountSocket.onClose();
			System.out.println("  >>  sssaaa  ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
}