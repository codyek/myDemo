package com.thinkgem.jeesite.modules.wechat.common;

/**
 * 	微信接口静态变量类
 */
public final class WechatApiContants {
	// 微信配置字典信息
	public static String WECHAT_CONFIG="wechat_config";
	public static String OPENID="openid";
	// 公众号信息
	public static String APPID="wx09fa6f15b66d1994";
	public static String APPSECRET="3ec158cbf69417e7dabdf8c5f40b0b9c";
	public static String APPTOKEN="amwayhzf";
	public static String WECHATID="gh_ddd96f4afdac";
	
	public static String RESP_STATUS_OK="ok";
	public static String RESP_STATUS_ERROR="error";
	
	// 获取token接口地址
	public static String GETTOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}";
	
	// 客服消息接口地址
	public static String CUSTOM_SEND_URL="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
	// 客服消息接口地址
	public static String TEMPLATE_SEND_URL="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	
	public static Long RESP_CODE_OK=0L;
	
	
}
