package com.thinkgem.jeesite.modules.platform.callback;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 用于定义Okex全局状态码.如果未来状态码有变动，则需参考官方文档，进行调整。
 *
 */
public enum OkexReturnCode {

	HOLDER;
	
	private Map<Long,String> holder = new HashMap<Long,String>();
	
	OkexReturnCode(){
		holder.put(Long.valueOf(-1), "系统繁忙");
		holder.put(Long.valueOf(0), "请求成功");
		holder.put(Long.valueOf(503), "用户请求过于频繁");
		holder.put(Long.valueOf(20001), "用户不存在");
		holder.put(Long.valueOf(20002), "用户被冻结");
		holder.put(Long.valueOf(20003), "用户被爆仓冻结");
		holder.put(Long.valueOf(20004), "合约账户被冻结");
		holder.put(Long.valueOf(20005), "用户合约账户不存在");
		holder.put(Long.valueOf(20006), "必填参数为空");
		holder.put(Long.valueOf(20007), "参数错误");
		holder.put(Long.valueOf(20008), "合约账户余额为空");
		holder.put(Long.valueOf(20009), "虚拟合约状态错误");
		holder.put(Long.valueOf(20010), "合约风险率信息不存在");
		holder.put(Long.valueOf(20011), "开仓前保证金率低于90");
		holder.put(Long.valueOf(20012), "开仓后保证金率低于90");
		holder.put(Long.valueOf(20013), "暂无对手价");
		holder.put(Long.valueOf(20014), "系统错误");
		holder.put(Long.valueOf(20015), "订单信息不存在");
		holder.put(Long.valueOf(20016), "平仓数量是否大于同方向可用持仓数量");
		holder.put(Long.valueOf(20017), "非本人操作");
		holder.put(Long.valueOf(20018), "下单价格高于前一分钟的103%或低于97%");
		holder.put(Long.valueOf(20019), "该IP限制不能请求该资源");
		holder.put(Long.valueOf(20020), "密钥不存在");
		holder.put(Long.valueOf(20021), "指数信息不存在");
		holder.put(Long.valueOf(20022), "接口调用错误（全仓模式调用全仓接口，逐仓模式调用逐仓接口）");
		holder.put(Long.valueOf(20023), "逐仓用户");
		holder.put(Long.valueOf(20024), "sign签名不匹配");
		holder.put(Long.valueOf(20025), "杠杆比率错误");
		holder.put(Long.valueOf(20026), "API鉴权错误");
		holder.put(Long.valueOf(20027), "无交易记录");
		holder.put(Long.valueOf(20028), "合约不存在");
		holder.put(Long.valueOf(20029), "转出金额大于可转金额");
		holder.put(Long.valueOf(20030), "账户存在借款");
		holder.put(Long.valueOf(20038), "根据相关法律，您所在的国家或地区不能使用该功能。");
		holder.put(Long.valueOf(20049), "用户请求接口过于频繁");
	}
	
	public String getErrorMsgByCode(long code){
		String ret = holder.get(code);
		if(ret!=null){
			return ret;
		}else{
			return "未定义的状态码";
		}
	}
}
