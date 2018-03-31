package com.thinkgem.jeesite.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class DigestUtil {

	private static final String SHA = "SHA-1";
	/**
	 * sha1加密算法
	 * @param key需要加密的字符串
	 * @return 加密后的结果
	 */
	public static String sha1(String key) {
		String signature = null;
		try {
			MessageDigest crypt = MessageDigest.getInstance(SHA);
			crypt.reset();
			crypt.update(key.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return signature;
	}
	
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	
//	public static void main(String[] args) {
//		String string1 = "jsapi_ticket=bxLdikRXVbTPdHSM05e5uxOlYYJIKN-8Zprn2k-Arf5FzRLyj6hNCnDUNiODfzHR6lk830xz44Oe320QFPvsbQ&noncestr=hh&timestamp=1464320127&url=https://wechatorderingqa1.amwaynet.com.cn/wechatordering/Shopping/Product/Onlineshopping.html?ada=344703&token=8763bf47-c495-43ad-8ce7-f15929e80889&adaName=%E5%BC%A0%E4%B8%89&itemClass=GB&time=20160527105341&openid=oTNvgjkuZJZ9wE0uHRpaFVoUxzR0";
//		System.out.println(sha1(string1));
//	}
	
}
