package com.thinkgem.jeesite.common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MexHMACSHA256 {

	public static String HMACSHA256(byte[] data, byte[] key) 
	{
	      try  {
	         SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
	         Mac mac = Mac.getInstance("HmacSHA256");
	         mac.init(signingKey);
	         return byte2hex(mac.doFinal(data));
	      } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	      } catch (InvalidKeyException e) {
	        e.printStackTrace();
	      }
	      return null;
	} 
	
	public static String byte2hex(byte[] b) 
	{
	    StringBuilder hs = new StringBuilder();
	    String stmp;
	    for (int n = 0; b!=null && n < b.length; n++) {
	        stmp = Integer.toHexString(b[n] & 0XFF);
	        if (stmp.length() == 1)
	            hs.append('0');
	        hs.append(stmp);
	    }
	    return hs.toString();
	}
	
	public static void main(String[] args) {
		String apiKey = "LAqUlngMIQkIUjXMUreyu3qn";
		String apiSecret = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";
		
		//String verb = "GET";
		//String path = "/api/v1/instrument?filter=%7B%22symbol%22%3A+%22XBTM15%22%7D";
		//String nonce = "1429631577690";
		//String data = "";
		
		String verb = "POST";
		String path = "/api/v1/order";
		String nonce = "1429631577995";  //13位
		String data = "{\"symbol\":\"XBTM15\",\"price\":219.0,\"clOrdID\":\"mm_bitmex_1a/oemUeQ4CAJZgP3fjHsA\",\"orderQty\":98}";
		
		String msg = verb+path+nonce+data;
		
		System.out.println("time ="+ System.currentTimeMillis());
		String signature = "";
		signature = MexHMACSHA256.HMACSHA256(msg.getBytes(),apiSecret.getBytes());
		
		System.out.println(">> signature = "+signature);
	}
}
