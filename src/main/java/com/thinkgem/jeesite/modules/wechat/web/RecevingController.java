package com.thinkgem.jeesite.modules.wechat.web;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.utils.DigestUtil;
import com.thinkgem.jeesite.common.utils.XmlUtil;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

@Controller
@RequestMapping(value = "${frontPath}/wechat")
public class RecevingController extends BaseController {

	
	@RequestMapping(value = "/receving", method = RequestMethod.POST, produces = { "application/xml;charset=UTF-8" })
	@ResponseBody
	public String replyMessage(HttpServletRequest request, HttpServletResponse response) {
		String back = null;
		// 仅处理微信服务端发的请求
		if (checkWeixinSignature(request)) {
			// 设置微信输入的XML编码格式为 UTF-8
			try {
				request.setCharacterEncoding("UTF-8");
				// 获取输入流
				ServletInputStream in = request.getInputStream();

				// 把输入流转换为 字符串,此处应该为json字符串
				String inputString = XmlUtil.inputStream2String(in);
				logger.info("****request from Wechat:\n" + inputString);
				// 释放资源
				in.close();

				in = null;
				// DOTO 需要再实现 处理输入信息，并做出响应 
				//back = recevingService.dealResponseWeixin(inputString, localPath);
			} catch (Exception e) {
				logger.error("处理微信消息失败"+e.getMessage(),e);
			}
			//back = "系统接入中...";
			logger.info("**>>back:\n" + back);
		} else {
			back = "error";
		}
		
		return back;
	}
	/**
	 * 微信公众平台验证url是否有效使用的接口
	 * 	微信会根据在公众平台中设置的URL中，通过GET请求，来验证接收微信消息的是否是否正常！
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/receving", method = RequestMethod.GET)
	@ResponseBody
	public String initWeixinURL(HttpServletRequest request) {
		logger.info(">> initWeixinURL 微信公众号平台请求接入");
		String echostr = request.getParameter("echostr");
		if (checkWeixinSignature(request) && echostr != null) {
			return echostr;
		} else {
			return "error";
		}
	}
	
	/**
	 * 根据token计算signature验证是否为weixin服务端发送的消息
	 * @param request
	 * @return
	 */
	private final boolean checkWeixinSignature(HttpServletRequest request) {

		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String sid=request.getParameter("sid");
		
		String token = DictUtils.getDictValue("","","");// 从字典获取token
		logger.info("in_sid:"+sid+" ,localtoken:"+token);
		if (signature != null && timestamp != null && nonce != null) {
			String[] strSet = new String[] {token, timestamp, nonce };
			java.util.Arrays.sort(strSet);
			String key = "";
			for (String string : strSet) {
				key = key + string;
			}
			String pwd = DigestUtil.sha1(key);
			return pwd.equals(signature);
		} else {
			return false;
		}
	}
}
