package com.thinkgem.jeesite.modules.sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @功能概要：调用DEMO
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class Test {
	// 日期格式定义
	private static SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");

	public static void main(String[] args) {
		// 用户账号
		String userid = "E101MG";
		// 用户密码
		String pwd = "c4g899";

		// 主IP信息 必填
		String masterIpAddress = "api01.monyun.cn:7901";
		// 备IP1 选填
		String ip1 = "api02.monyun.cn:7901";
		// 备IP2 选填
		String ip2 = null;
		// 备IP3 选填
		String ip3 = null;
		// 设置IP
		ConfigManager.setIpInfo(masterIpAddress, ip1, ip2, ip3);

		// 密码是否加密 true：密码加密;false：密码不加密
		ConfigManager.IS_ENCRYPT_PWD = true;
		boolean isEncryptPwd = ConfigManager.IS_ENCRYPT_PWD;

		// 单条发送
		singleSend(userid, pwd, isEncryptPwd);

		// 相同内容群发
		//batchSend(userid, pwd, isEncryptPwd);

		// 清除所有IP (此处为清除IP示例代码，如果需要修改IP，请先清除IP，再设置IP)
		ConfigManager.removeAllIpInfo();

	}

	/**
	 * 
	 * @description 单条发送
	 * @param userid  用户账号
	 * @param pwd 用户密码
	 * @param isEncryptPwd 密码是否加密 true：密码加密;false：密码不加密
	 */
	public static void singleSend(String userid, String pwd,
			boolean isEncryptPwd) {
		try {
			System.out.println("  >>>>  singleSend start ");
			// 参数类
			Message message = new Message();
			// 实例化短信处理对象
			CHttpPost cHttpPost = new CHttpPost();
			// 设置账号 将 userid转成大写,以防大小写不一致
			message.setUserid(userid.toUpperCase());

			// 判断密码是否加密。
			// 密码加密，则对密码进行加密
			if (isEncryptPwd) {
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
			message.setMobile("18620932500");
			// 设置内容
			//message.setContent("平台：okex，未操作项：sell，数量：20");
			message.setContent("您的验证码是1688，在5分钟内输入有效。如非本人操作请忽略此短信。");
			// 设置扩展号
			//message.setExno("11");
			// 业务类型
			message.setSvrtype("SMS001");

			// 返回的平台流水编号等信息
			StringBuffer msgId = new StringBuffer();
			// 返回值
			int result = -310099;
			// 发送短信
			result = cHttpPost.singleSend(message, msgId);
			// result为0:成功;非0:失败
			if (result == 0) {
				System.out.println("单条发送提交成功！");

				System.out.println(msgId.toString());

			} else {
				System.out.println("单条发送提交失败,错误码：" + result);
			}
		} catch (Exception e) {
			// 异常处理
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @description 相同内容群发
	 * @param userid
	 *            用户账号
	 * @param pwd
	 *            用户密码
	 * @param isEncryptPwd
	 *            密码是否加密 true：密码加密;false：密码不加密
	 */
	public static void batchSend(String userid, String pwd, boolean isEncryptPwd) {
		try {
			// 参数类
			Message message = new Message();

			// 实例化短信处理对象
			CHttpPost cHttpPost = new CHttpPost();

			// 设置账号 将 userid转成大写,以防大小写不一致
			message.setUserid(userid.toUpperCase());

			// 判断密码是否加密。
			// 密码加密，则对密码进行加密
			if (isEncryptPwd) {
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
			message.setMobile("159XXXXXXXX,139XXXXXXXX");
			// 设置内容
			message.setContent("测试短信");
			// 设置扩展号
			message.setExno("11");
			// 用户自定义流水编号
			message.setCustid("20160929194950100001");
			// 自定义扩展数据
			message.setExdata("abcdef");
			// 业务类型
			message.setSvrtype("SMS001");

			// 返回的平台流水编号等信息
			StringBuffer msgId = new StringBuffer();
			// 返回值
			int result = -310099;
			// 发送短信
			result = cHttpPost.batchSend(message, msgId);
			// result为0:成功;非0:失败
			if (result == 0) {
				System.out.println("相同内容发送提交成功！");

				System.out.println(msgId.toString());
			} else {
				System.out.println("相同内容发送提交失败,错误码：" + result);
			}
		} catch (Exception e) {
			// 异常处理
			e.printStackTrace();
		}
	}



}
