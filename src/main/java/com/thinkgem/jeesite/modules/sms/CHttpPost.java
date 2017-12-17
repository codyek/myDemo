package com.thinkgem.jeesite.modules.sms;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @功能概要：发送管理类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class CHttpPost {

	// http请求失败
	public static int ERROR_310099 = -310099;

	// 请求超时时间(毫秒) 5秒
	public static int HTTP_REQUEST_TIMEOUT = 5 * 1000;

	// 响应超时时间(毫秒) 60秒
	public static int HTTP_RESPONSE_TIMEOUT = 60 * 1000;

	/**
	 * 
	 * @description 单条发送接口
	 * @param message
	 *            短信参数对象
	 * @param msgId
	 *            返回值为0，则msgId有值。返回值非0，则msgId为空的字符串。字符串为"手机号码,custId,网关流水号"
	 * @return 0:成功 非0:返回错误代码
	 */
	public int singleSend(Message message, StringBuffer msgId) {
		// 定义返回值，默认单条发送失败
		int returnInt = ERROR_310099;

		try {
			// 去掉前后空格,对短信内容进行编码 urlencode（GBK明文）
			message.setContent(URLEncoder.encode(message.getContent(), "GBK"));

			String Message = null;
			StringBuffer messageBuffer = new StringBuffer("");

			// 单条发送
			returnInt = sendSmsByNotKeepAlive("single_send", message,
					messageBuffer);

			// returnInt为0,代表提交成功;returnInt不为0，代表提交失败
			if (returnInt == 0) {
				// 提交成功
				Message = messageBuffer.toString();
				// 请求成功后的处理
				Long rMsgid = null;
				String rCustid = "";
				// 处理返回结果
				if (Message != null && !"".equals(Message.trim())) {
					// 解析JSON
					JSONObject parseObject = (JSONObject)JSONObject.parse(Message);
					// 获取是否成功标识
					returnInt = Integer.parseInt(String.valueOf(parseObject
							.get("result")));
					// returnInt为0，则代表提交成功
					if (returnInt == 0) {
						// 平台返回流水号
						rMsgid = (Long) parseObject.get("msgid");
						// 平台返回的custid
						rCustid = (String) parseObject.get("custid");
						msgId.append(message.getMobile() + "," + rCustid + ","
								+ rMsgid);
					}
				}
			} else {
				// 提交失败，返回错误码
				return returnInt;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// returnInt不为0，则代表提交失败。否则，提交成功。
			if (returnInt != 0) {
				returnInt = ERROR_310099;
			}
		}
		return returnInt;
	}

	/**
	 * 
	 * 
	 * @description 相同内容群发
	 * @param message 参数对象
	 * @param msgId 返回值为0，则msgId有值。返回值非0，则msgId为空的字符串。字符串为"手机号码,custId,网关流水号"
	 * @return 0:成功 非0:返回错误代码
	 */
	public int batchSend(Message message, StringBuffer msgId) {
		// 定义返回值，默认相同内容发送失败
		int returnInt = ERROR_310099;

		try {
			// 对短信内容进行编码 urlencode（GBK明文）
			message.setContent(URLEncoder.encode(message.getContent(), "GBK"));

			String Message = null;

			StringBuffer messageBuffer = new StringBuffer("");

			// 短连接相同内容群发
			returnInt = sendSmsByNotKeepAlive("batch_send", message,
					messageBuffer);

			// returnInt为0,代表提交成功;returnInt不为0，代表提交失败
			if (returnInt == 0) {
				// 提交成功
				Message = messageBuffer.toString();
				Long rMsgid = null;
				String rCustid = "";
				// 处理返回结果
				if (Message != null && !"".equals(Message.trim())) {
					// 解析JSON
					JSONObject parseObject = (JSONObject)JSONObject.parse(Message);
					// 获取是否成功标识
					returnInt = Integer.parseInt(String.valueOf(parseObject
							.get("result")));
					// returnInt为0，则代表提交成功
					if (returnInt == 0) {
						// 平台返回流水号
						rMsgid = (Long) parseObject.get("msgid");
						// 平台返回的custid
						rCustid = (String) parseObject.get("custid");
						msgId.append(message.getMobile().split(",")[0] + ","
								+ rCustid + "," + rMsgid);
					}
				}
			} else {
				// 提交失败，返回错误码
				return returnInt;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// returnInt不为0，则代表提交失败。否则，提交成功。
			if (returnInt != 0) {
				returnInt = ERROR_310099;
			}
		}
		return returnInt;
	}


	/**
	 * @description 短连接发送的方法
	 * @param methodName
	 *            方法名
	 * @param message
	 *            短信请求实体类
	 * @param messageBuffer
	 *            请求网关的返回值
	 * @return 0:提交网关成功; 非0：提交网关失败,值为错误码
	 */
	private int sendSmsByNotKeepAlive(String methodName, Message message,
			StringBuffer messageBuffer) {
		int failCode = ERROR_310099;
		try {
			// 未设置IP
			if (!ConfigManager.ipIsSet) {
				return ERROR_310099;
			}

			// 通过账号密码获取可用的IP
			String availableAddress = new CheckAddress().getAddressByUserID(
					message.getUserid(), message.getPwd(),
					message.getTimestamp());

			// 未获取到IP
			if (availableAddress == null || "".equals(availableAddress.trim())) {
				return failCode;
			}

			String requestHost = "http://" + availableAddress;
			String Message = null;
			Message = executeNotKeepAliveNotUrlEncodePost(message, requestHost
					+ ConfigManager.REQUEST_PATH + methodName);
			// Message为-310099,则请求失败，否则请求成功。短连接请求失败后，不重新请求。
			if (String.valueOf(ERROR_310099).equals(Message)) {
				// 当前可用的IP和主IP相同,则说明主IP发送失败
				if (ConfigManager.masterIpAndPort.equals(availableAddress)) {
					// 主IP最近检测时间
					ConfigManager.LAST_CHECK_TIME = Calendar.getInstance()
							.getTimeInMillis();
					// 主IP状态更新为异常
					ConfigManager.masterIPState = 1;
				}
				// 可用IP设置为空
				ConfigManager.availableIpAndPort = null;

				// 返回错误码 单条发送失败
				return failCode;
			} else {
				// 请求成功
				messageBuffer.append(Message);
				// 请求成功，返回0
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误码 单条发送失败
			return failCode;
		}

	}

	/**
	 * @description 短连接获取上行、状态报告、余额方法
	 * @param methodName
	 *            方法名
	 * @param message
	 *            请求实体类
	 * @param messageBuffer
	 *            请求网关的返回值
	 * @return 0:提交网关成功; 非0：提交网关失败,值为错误码
	 */
	private int getMoRptFeeByNotKeepAlive(String methodName, Message message,
			StringBuffer messageBuffer) {
		int failCode = ERROR_310099;
		try {
			// 未设置IP
			if (!ConfigManager.ipIsSet) {
				return ERROR_310099;
			}

			// 通过账号密码获取可用的IP
			String availableAddress = new CheckAddress().getAddressByUserID(
					message.getUserid(), message.getPwd(),
					message.getTimestamp());

			// 未获取到IP
			if (availableAddress == null || "".equals(availableAddress.trim())) {
				return failCode;
			}

			// 短连接获取余额
			String requestHost = "http://" + availableAddress;
			String Message = null;
			Message = executeNotKeepAliveNotUrlEncodePost(message, requestHost
					+ ConfigManager.REQUEST_PATH + methodName);
			// Message为-310099,则请求查询余额接口失败，否则请求查询余额接口成功
			if (String.valueOf(ERROR_310099).equals(Message)) {
				// 当前可用的IP和主IP相同,则说明主IP发送失败
				if (ConfigManager.masterIpAndPort.equals(availableAddress)) {
					// 主IP最近检测时间
					ConfigManager.LAST_CHECK_TIME = Calendar.getInstance()
							.getTimeInMillis();
					// 主IP状态更新为异常
					ConfigManager.masterIPState = 1;
				}
				// 可用IP设置为空
				ConfigManager.availableIpAndPort = null;

				// 返回错误码 查询余额失败
				return failCode;
			} else {
				// 请求成功
				messageBuffer.append(Message);
				// 请求成功，返回0
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return failCode;
		}
	}

	/**
	 * 
	 * 
	 * @description 对密码进行加密
	 * @param userid
	 *            用户账号
	 * @param pwd
	 *            用户原始密码
	 * @param timestamp
	 *            时间戳
	 * @return 加密后的密码
	 */
	public String encryptPwd(String userid, String pwd, String timestamp) {
		// 加密后的字符串
		String encryptPwd = null;
		try {
			String passwordStr = userid.toUpperCase() + "00000000" + pwd
					+ timestamp;
			// 对密码进行加密
			encryptPwd = getMD5Str(passwordStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回加密字符串
		return encryptPwd;
	}

	/**
	 * 
	 * 
	 * @description MD5加密方法
	 * @param str
	 *            需要加密的字符串
	 * @return 返回加密后的字符串
	 */
	private static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		// 加密前的准备
		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// 初始化加密类失败，返回null。
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 初始化加密类失败，返回null。
			return null;
		}

		byte[] byteArray = messageDigest.digest();

		// 加密后的字符串
		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.toString();
	}

	/**
	 * 
	 * 
	 * @description 短连接发送(不进行URLENCODE)
	 * @param obj
	 *            请求对象
	 * @param httpUrl
	 *            请求地址
	 * @return 请求网关返回的值
	 * @throws Exception
	 */
	private String executeNotKeepAliveNotUrlEncodePost(Object obj,
			String httpUrl) throws Exception {
		String result = String.valueOf(ERROR_310099);
		HttpClient httpclient = null;
		try {
			// 将实体对象，生成JSON字符串
			String entityValue = JSONObject.toJSONString(obj);
			// 定义请求头
			HttpPost httppost = new HttpPost(httpUrl);
			httppost.setHeader("Content-Type", "text/json");

			StringEntity stringEntity = new StringEntity(entityValue,
					HTTP.UTF_8);

			// 设置参数的编码UTF-8
			httppost.setEntity(stringEntity);

			// 创建连接
			httpclient = new DefaultHttpClient();

			// 设置请求超时时间 设置为5秒
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT,
					HTTP_REQUEST_TIMEOUT);
			// 设置响应超时时间 设置为60秒
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, HTTP_RESPONSE_TIMEOUT);

			HttpEntity entity = null;
			HttpResponse httpResponse = null;

			try {
				// 向网关请求
				httpResponse = httpclient.execute(httppost);
				// 若状态码为200，则代表请求成功
				if (httpResponse != null
						&& httpResponse.getStatusLine().getStatusCode() == 200) {
					// 获取响应的实体
					entity = httpResponse.getEntity();
					// 响应的内容不为空，并且响应的内容长度大于0,则获取响应的内容
					if (entity != null && entity.getContentLength() > 0) {
						try {
							// 请求成功，能获取到响应内容
							result = EntityUtils.toString(entity);
						} catch (Exception e) {
							e.printStackTrace();
							// 获取内容失败，返回空字符串
							result = "";
						}
					} else {
						// 请求成功，但是获取不到响应内容
						result = "";
					}
				} else {
					// 设置错误码
					result = String.valueOf(ERROR_310099);
					System.out.println("请求失败："
							+ httpResponse.getStatusLine().toString());
				}

			} catch (Exception e) {
				result = String.valueOf(ERROR_310099);
				e.printStackTrace();
			}
		} catch (Exception e) {
			result = String.valueOf(ERROR_310099);
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (httpclient != null) {
				try {
					httpclient.getConnectionManager().shutdown();
				} catch (Exception e2) {
					// 关闭连接失败
					e2.printStackTrace();
				}

			}
		}
		return result;

	}


}
