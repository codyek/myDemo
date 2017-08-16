package com.thinkgem.jeesite.modules.platform.web;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.platform.constants.Constants;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexAccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.bitmex.MexOrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.AccountInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.InfoInterfaceService;
import com.thinkgem.jeesite.modules.platform.service.okex.OrderInterfaceService;
import com.thinkgem.jeesite.modules.platform.socket.bitmex.WebsocketClientEndpoint;

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
	
	@Autowired
	private MexOrderInterfaceService mexOrderService;
	
	@Autowired
	private MexAccountInterfaceService mexAccountService;
	

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
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_XBTU17MEX_PRICE_KEY);
		}else if("ltcu17".equals(name)){
			obj = EhCacheUtils.get(Constants.PRICE_CACHE,Constants.CACHE_LTCU17MEX_PRICE_KEY);
			
		}
		return obj.toString();
	}
			
}