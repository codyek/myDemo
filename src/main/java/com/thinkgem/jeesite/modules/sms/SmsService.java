package com.thinkgem.jeesite.modules.sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	// 日期格式定义
	private SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
	// 用户账号
	private final String userid = "E101MG";
	// 用户密码
	private final String pwd = "c4g899";
	// 主IP信息 必填
	private String masterIpAddress = "api01.monyun.cn:7901";
	// 备IP1 选填
	private String ip1 = "api02.monyun.cn:7901";
	// 备IP2 选填
	private String ip2 = null;
	// 备IP3 选填
	private String ip3 = null;
	
	private void init(){
		// 设置IP
		ConfigManager.setIpInfo(masterIpAddress, ip1, ip2, ip3);
		// 密码是否加密 true：密码加密;false：密码不加密
		ConfigManager.IS_ENCRYPT_PWD = true;
	}
	
	/** 单条发送
	 * @param mobile  发送手机号
	 * @param content 内容
	 */
	public boolean singleSend(String mobile, String content) {
		log.info("  >>>> singleSend start mobile="+mobile);
		boolean retFlag = false;
		try {
			// 初始化
			init();
			// 参数类
			Message message = new Message();
			// 实例化短信处理对象
			CHttpPost cHttpPost = new CHttpPost();
			// 设置账号 将 userid转成大写,以防大小写不一致
			message.setUserid(userid.toUpperCase());
			// 密码加密，则对密码进行加密
			if (ConfigManager.IS_ENCRYPT_PWD) {
				// 设置时间戳
				String timestamp = sdf.format(Calendar.getInstance().getTime());
				message.setTimestamp(timestamp);
				// 对密码进行加密
				String encryptPwd = cHttpPost.encryptPwd(message.getUserid(),
						pwd, message.getTimestamp());
				// 设置加密后的密码
				message.setPwd(encryptPwd);
			} else {
				// 设置密码
				message.setPwd(pwd);
			}

			// 设置手机号码 此处只能设置一个手机号码
			//message.setMobile("18620932500");
			message.setMobile(mobile);
			// 设置内容
			//message.setContent("平台：okex，未操作项：sell，数量：20");
			message.setContent(content);
			// 设置扩展号
			//message.setExno("11");
			// 业务类型
			message.setSvrtype("63062");

			// 返回的平台流水编号等信息
			StringBuffer msgId = new StringBuffer();
			// 返回值
			int result = -310099;
			// 发送短信
			result = cHttpPost.singleSend(message, msgId);
			// result为0:成功;非0:失败
			if (result == 0) {
				retFlag = true;
				log.info("单条发送提交成功！"+msgId.toString());
			} else {
				log.info("单条发送提交失败,错误码：" + result);
			}
		} catch (Exception e) {
			// 异常处理
			log.error(">> 单条发送异常：",e);
		}
		return retFlag;
	}
	
	/**  相同内容群发
	 * @param mobiles  发送手机号(多个号码逗号间隔159XXXXXXXX,139XXXXXXXX)
	 * @param content 内容
	 */
	public boolean batchSend(String mobiles, String content) {
		boolean retFlag = false;
		log.info("  >>>> batchSend start mobile="+mobiles);
		try {
			// 参数类
			Message message = new Message();
			// 实例化短信处理对象
			CHttpPost cHttpPost = new CHttpPost();
			// 设置账号 将 userid转成大写,以防大小写不一致
			message.setUserid(userid.toUpperCase());
			// 密码加密，则对密码进行加密
			if (ConfigManager.IS_ENCRYPT_PWD) {
				// 设置时间戳
				String timestamp = sdf.format(Calendar.getInstance().getTime());
				message.setTimestamp(timestamp);
				// 对密码进行加密
				String encryptPwd = cHttpPost.encryptPwd(message.getUserid(),
						pwd, message.getTimestamp());
				// 设置加密后的密码
				message.setPwd(encryptPwd);
			} else {
				// 设置密码
				message.setPwd(pwd);
			}
			// 设置手机号码
			message.setMobile(mobiles);
			// 设置内容
			message.setContent(content);
			// 业务类型
			message.setSvrtype("63062");
			// 返回的平台流水编号等信息
			StringBuffer msgId = new StringBuffer();
			// 返回值
			int result = -310099;
			// 发送短信
			result = cHttpPost.batchSend(message, msgId);
			// result为0:成功;非0:失败
			if (result == 0) {
				retFlag = true;
				log.info("相同内容发送提交成功！"+msgId.toString());
			} else {
				log.info("相同内容发送提交失败,错误码：" + result);
			}
		} catch (Exception e) {
			// 异常处理
			log.error(">> 相同内容发送异常：",e);
		}
		return retFlag;
	}
}
