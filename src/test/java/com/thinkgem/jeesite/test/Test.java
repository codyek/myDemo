package com.thinkgem.jeesite.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.MexHMACSHA256;

public class Test {

	//String verb = "GET";
	//String path = "/api/v1/instrument?filter=%7B%22symbol%22%3A+%22XBTM15%22%7D";
	//String nonce = "1429631577690";
	//String data = "";
	
	static String verb = "POST";
	static String path = "/api/v1/order";
	static String nonce = "1429631577995";  //13位
	static String data = "{\"symbol\":\"XBTM15\",\"price\":219.0,\"clOrdID\":\"mm_bitmex_1a/oemUeQ4CAJZgP3fjHsA\",\"orderQty\":98}";
	
	static String apiKey = "LAqUlngMIQkIUjXMUreyu3qn";
	static String apiSecret = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";
			
	public static void main(String[] args) {
		System.out.println("count = "+123);
		
		RestTemplate rest = new RestTemplate();
		String keys = test();
		HttpHeaders headers = new HttpHeaders();
        headers.set("api-nonce", apiSecret);
        headers.set("api-key", apiKey);
        headers.set("api-signature", keys);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        JSONObject parm = JSONObject.parseObject(data);
        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(parm, headers);
        
        HttpEntity<String> response = rest.exchange("https://www.bitmex.com/api/v1/order", HttpMethod.POST, entity, String.class);
        //这里放JSONObject, String 都可以。因为JSONObject返回的时候其实也就是string
        
        String msg = response.getBody();
        System.out.println("return = " + msg);
        
	}
	
	public static String test() {
		String msg = verb+path+nonce+data;
		
		System.out.println("time ="+ System.currentTimeMillis());
		String signature = "";
		signature = MexHMACSHA256.HMACSHA256(msg.getBytes(),apiSecret.getBytes());
		
		System.out.println(">> signature = "+signature);
		
		return signature;
	}
}
