package com.thinkgem.jeesite.test;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.MexHMACSHA256;
import com.thinkgem.jeesite.test.utils.RestClient;
import com.thinkgem.jeesite.test.utils.RestClientException;

public class Test {

	// String verb = "GET";
	// String path =
	// "/api/v1/instrument?filter=%7B%22symbol%22%3A+%22XBTM15%22%7D";
	// String nonce = "1429631577690";
	// String data = "";

	static String verb = "POST";
	//static String path = "/api/v1/order";
	//static String path = "/api/v1/position/leverage";
	static String path = "/api/v1/order";
	static String nonce = String.valueOf(System.currentTimeMillis()); // 13位
	//static String data = "{\"symbol\":\"XBTUSD\",\"price\":219.0,\"clOrdID\":\"mm_bitmex_1a/oemUeQ4CAJZgP3fjHsA\",\"orderQty\":98}";
	//static String data = "{\"symbol\":\"XBTUSD\",\"leverage\":\"10\"}";
	
	//static String url = "https://testnet.bitmex.com/api/v1/position/leverage";
	static String url = "https://www.bitmex.com"+path;

	//static String apiKey = "4sjjN6l23OgsAAT4CYraRP67";
	static String apiKey = "NOZeY70bJsNSQQr5wTgjBWYJ";
	//static String apiSecret = "8zL1KDsuj7VDiYseltP9yFC63_IbofoSU9AlJfRjg-kgStqi";
	static String apiSecret = "oztpvZCuZTUueYBv-NfUAJBYBn9huClzsYEJZXIaSDeZtj50";

	public static void main(String[] args) {
		
		System.out.println("count 21= " + 11123);
		
		JSONObject param = new JSONObject();
		param.put("symbol", "XBTUSD");
		//param.put("leverage", "10");
		
		
		param.put("ordType", "Limit");
		param.put("price", 2019.8);
		param.put("orderQty", 1);
		param.put("side", "Sell");
		
		
		String msg = null;
		CloseableHttpClient httpClient = null;
		//try {
			//httpClient = HttpClientUtils.acceptsUntrustedCertsHttpClient();
			//HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
			//		httpClient);
			//RestTemplate rest = new RestTemplate(clientHttpRequestFactory);
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        	requestFactory.setConnectTimeout(1000);
        	requestFactory.setReadTimeout(1000);

        	RestTemplate rest = RestClient.getClient();
//        	RestTemplate rest = new RestTemplate(requestFactory);
//			RestTemplate rest = new RestTemplate();
			String keys = test(param.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.set("api-nonce", nonce);
			headers.set("api-key", apiKey);
			headers.set("api-signature", keys);
			headers.setContentType(MediaType.APPLICATION_JSON);
			//headers.setContentLength(21);

			//param = JSONObject.parseObject(data);
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(param, headers);

			try {
				//msg = RestClientUtil.exchange(url, HttpMethod.POST, headers, parm, String.class, new HashMap());
				HttpEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);
				//ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);
				System.out.println("success body = "+response.getBody());
				
			} catch (HttpClientErrorException ee){
				String code = ee.getStatusCode().toString();
				String text = ee.getStatusText();
				String body = ee.getResponseBodyAsString();
				System.out.println("hht  c= "+code+", t="+text+", b="+body);
			} catch (RestClientException e) {
				String code = e.getStatusCode().toString();
				String text = e.getStatusText();
				String body = e.getResponseBody();
				System.out.println("boddy c= "+code+", t="+text+", b="+body);
			}
			// 这里放JSONObject, String 都可以。因为JSONObject返回的时候其实也就是string

			//msg = response.getBody();
			System.out.println("return = " + msg);
		//} catch (KeyManagementException e) {
			//e.printStackTrace();
		//} catch (KeyStoreException e) {
			//e.printStackTrace();
		//} catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
		//}

	}

	public static String test(String param) {
		System.out.println("param = "+param);
		String msg = verb + path + nonce + param;

		String signature = "";
		signature = MexHMACSHA256.HMACSHA256(msg.getBytes(), apiSecret.getBytes());

		System.out.println(">> signature = " + signature);

		return signature;
	}
}
