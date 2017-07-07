package com.thinkgem.jeesite.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private static final String UTF8 = "UTF-8";

	private final static DefaultHttpClient getHttpsClient() {
		// 创建默认的httpClient实例
		DefaultHttpClient httpClient = new DefaultHttpClient();

		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {

			public boolean verify(String arg0, SSLSession arg1) {
				return false;
			}

			public void verify(String arg0, SSLSocket arg1) throws IOException {
			}

			public void verify(String arg0, X509Certificate arg1)
					throws SSLException {
			}

			public void verify(String arg0, String[] arg1, String[] arg2)
					throws SSLException {
			}

		};

		SSLContext ctx;
		try {

			// ctx = SSLContext.getInstance("SSL", "SunJSSE");
			ctx = SSLContext.getInstance("TLS");

			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm },
					new java.security.SecureRandom());

			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,
					hostnameVerifier);

			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", 443, socketFactory));
		} catch (Exception e) {
			logger.error(">> SSLSocketFactory init error:",e);
		}

		return httpClient;
	}

	/**
	 * 
	 * @param url  路径
	 * @param params 参数
	 * @return
	 */
	public final static String httpsGet(String url, Map<String, String> params) {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=" + UTF8);
		return get(url, convertMap2PostParams(params), headerMap);
	}

	/**
	 * 
	 * @param url  
	 * @param paramList  路径参数
	 * @param postDataMap  post参数
	 * @return
	 */
	public final static String httpsPost(String url,
			List<NameValuePair> paramList, Map<String, String> postDataMap) {
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=" + UTF8);

		List<BasicNameValuePair> postData = parseMap2BasicForm(postDataMap);

		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postData, UTF8);
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to encode the form entity.", e);
			throw new RuntimeException(e);
		}
		return post(url, paramList, headerMap, entity);
	}

	/**
	 * 
	 * @param url
	 * @param paramList  路径参数
	 * @param postDataJson  post参数
	 * @return
	 */
	public final static String httpsPostJson(String url,
			List<NameValuePair> paramList, String postDataJson) {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/json; charset=" + UTF8);
		// 组装参数
		StringEntity entity = null;
		try {
			entity = new StringEntity(postDataJson, UTF8);
		} catch (Exception e) {
			logger.error("Failed to encode the JSON entity.", e);
			throw new RuntimeException(e);
		}
		return post(url, paramList, headerMap, entity);
	}

	/**
	 * HTTPSPOST JSON 改为根据URL的前面几个字母（协议）
	 * 
	 * @param url
	 * @param paramJson
	 * @param encoding
	 * @return
	 */
	public final static String httpsPostJson(String url, String postDataJson) {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/json; charset=" + UTF8);
		// 组装参数
		StringEntity entity = null;
		try {
			entity = new StringEntity(postDataJson, UTF8);
		} catch (Exception e) {
			logger.error("Failed to encode the JSON entity.", e);
			throw new RuntimeException(e);
		}
		return post(url, new ArrayList<NameValuePair>(), headerMap, entity);
	}

	private final static String get(String url, List<NameValuePair> paramList,
			Map<String, String> headerMap) {

		String result = "";

		URI uri = getUri(url, paramList);

		// 创建一个httpGet请求
		HttpGet httpGet = new HttpGet(uri);

		// 配置请求的超时设置
		RequestConfig requestConfig = getRequestConfig();

		httpGet.setConfig(requestConfig);

		// 组装header参数
		Set<Entry<String, String>> headerEntries = headerMap.entrySet();
		for (Entry<String, String> entry : headerEntries) {
			httpGet.setHeader(String.valueOf(entry.getKey()),
					String.valueOf(entry.getValue()));
		}

		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = getHttpsClient();
			httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 得到客户段响应的实体内容
				result = EntityUtils.toString(httpResponse.getEntity(),
						UTF8);
			} else {
				logger.error("URL:" + url + "\tStatusCode:" + statusCode);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		} finally {
			releaseConnection(httpResponse, httpClient);
		}

		return result;
	}

	private final static String post(String url, List<NameValuePair> paramList,
			Map<String, String> headerMap, HttpEntity requestEntity) {
		String result = "";

		URI uri = getUri(url, paramList);

		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = null;
		try {
			// 创建一个httpGet请求
			HttpPost httpPost = new HttpPost(uri);

			// 配置请求的超时设置
			RequestConfig requestConfig = getRequestConfig();

			httpPost.setConfig(requestConfig);

			// 组装header参数
			Set<Entry<String, String>> headerEntries = headerMap.entrySet();
			for (Entry<String, String> entry : headerEntries) {
				httpPost.setHeader(String.valueOf(entry.getKey()),
						String.valueOf(entry.getValue()));
			}

			// 设置参数
			httpPost.setEntity(requestEntity);

			logger.info("Post Data to [" + url + "] ");

			httpClient = getHttpsClient();

			httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity(),
						UTF8);
			}
			httpPost.releaseConnection();
		} catch (Exception e) {
			String errorInfo = String.format("httpclient post failed:\nUrl=%s",
					url);
			logger.error(errorInfo, e);
		} finally {
			releaseConnection(httpResponse, httpClient);
		}

		return result;
	}

	/**
	 * 通过httpPost方式上传文件
	 * 
	 * @param filePath
	 * @param url
	 */
	public static String httpUpload(String url, String filePath,
			List<NameValuePair> paramList) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String result = "";

		URI uri = getUri(url, paramList);
		try {
			// 把一个普通参数和文件上传给下面这个地址 是一个servlet
			HttpPost httpPost = new HttpPost(uri);

			// 配置请求的超时设置
			RequestConfig requestConfig = getRequestConfig();

			httpPost.setConfig(requestConfig);

			File file = new File(filePath);
			if (!file.exists()) {
				logger.error(String.format("the file %s is not exists", file));
				return result;
			}
			// 把文件转换成流对象FileBody
			FileBody fileBody = new FileBody(file);

			HttpEntity reqEntity = MultipartEntityBuilder.create()
			// 相当于<input type="file" name="file"/>
					.addPart("file", fileBody).build();

			httpPost.setEntity(reqEntity);

			httpClient = HttpClients.createDefault();
			// 发起请求 并返回请求的响应
			response = httpClient.execute(httpPost);
			// 获取响应对象
			HttpEntity resEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				result = EntityUtils.toString(resEntity, Charset.forName(UTF8));
			} else {
				logger.error("URL:" + url + "\tStatusCode:" + statusCode);
			}
			// 销毁
			EntityUtils.consume(resEntity);
			httpPost.releaseConnection();
		} catch (Exception e) {
			String errorInfo = String.format(
					"httpclient post upload failed:\nUrl=%s", url);
			logger.error(errorInfo, e);
		} finally {
			releaseConnection(response, httpClient);
		}

		return result;
	}

	/**
	 * 释放资源
	 * 
	 * @param response
	 * @param httpClient
	 */
	public static void releaseConnection(CloseableHttpResponse response,
			CloseableHttpClient httpClient) {

		try {
			if (response != null)
				response.close();
		} catch (IOException e) {
			logger.error("close CloseableHttpResponse failed!", e);
		}

		try {
			if (httpClient != null)
				httpClient.close();
		} catch (IOException e) {
			logger.error("close CloseableHttpClient failed!", e);
		}
	}

	/**
	 * 获取超时配置 10秒
	 * 
	 * @return
	 */
	public static RequestConfig getRequestConfig() {

		return RequestConfig.custom()
				.setConnectionRequestTimeout(10000)
				.setConnectTimeout(10000)
				.setSocketTimeout(10000)
				.build();
	}

	/**
	 * 根据参数拼装uri
	 * 
	 * @param url
	 * @param paramList
	 * @return
	 */
	public static URI getUri(String url, List<NameValuePair> paramList) {

		URI uri = null;
		try {
			uri = new URIBuilder(url).addParameters(paramList).build();
		} catch (URISyntaxException e) {
			logger.error("Failed to create a legal URI by given url:" + url, e);
		}

		return uri;
	}

	/**
	 * 封装MAP格式的参数到BasicNameValuePair中
	 * 
	 * @param paramsMap
	 * @return
	 */
	private static final List<BasicNameValuePair> parseMap2BasicForm(
			Map<String, String> paramsMap) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		;
		if (paramsMap != null && paramsMap.size() > 0) {
			Iterator<String> it = paramsMap.keySet().iterator();

			String keyTmp = null;
			while (it.hasNext()) {
				keyTmp = it.next();
				params.add(new BasicNameValuePair(keyTmp, paramsMap.get(keyTmp)));
			}
		}
		return params;
	}
	
	/**
	 * 封装MAP格式的参数到NameValuePair中
	 * 
	 * @param paramsMap
	 * @return List<NameValuePair>
	 */
	private static final List<NameValuePair> convertMap2PostParams(Map<String,String> params){
		List<String> keys = new ArrayList<String>(params.keySet());
		if(keys.isEmpty()){
			return null;
		}
		int keySize = keys.size();
		List<NameValuePair>  data = new LinkedList<NameValuePair>() ;
		for(int i=0;i<keySize;i++){
			String key = keys.get(i);
			String value = params.get(key);
			data.add(new BasicNameValuePair(key,value));
		}
		return data;
	}

	/**
	private static class BitX509TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class BitVerifyHostname implements HostnameVerifier {

		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			return false;
		}
	}
	**/
}
