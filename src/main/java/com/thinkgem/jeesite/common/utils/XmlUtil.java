package com.thinkgem.jeesite.common.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML工具类
 * 
 * @author WernGin jhuanga@isoftstone.com
 * @time 2013-12-24 16:42:47
 * 
 */
public class XmlUtil {

	private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	/**
	 * 把request请求流转换为字符串
	 * 
	 * @param in
	 *            request.getInputStream()返回的值
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static final String inputStream2String(InputStream in)
			throws UnsupportedEncodingException, IOException {
		if (in == null)
			return "";

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n, "UTF-8"));
		}
		return out.toString();
	}

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param in
	 *            request.getInputStream()返回的值
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(String xmlStr) {
		logger.debug("parseXml^^^^^^^^^WW");
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		Document document = null;
		InputStream inputStream = null;
		try {
			// 读取输入流
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-8");
			inputStream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
			try{
				document = reader.read(inputStream);
			}catch(DocumentException de){
				throw new Exception(de.getMessage());
			}
			logger.debug("parseXml^^^^^^^^^reader.read(in)");
			// 得到xml根元素
			Element root = document.getRootElement();
			// 得到根元素的所有子节点
			List<Element> elementList = root.elements();
			// 遍历所有子节点
			for (Element e : elementList) {
				logger.debug("parseXml... node[" + e.getName() + "],text["
						+ e.getText() + "]");
				map.put(e.getName(), e.getTextTrim());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
				inputStream=null;
			}
		}
		logger.debug(" End parseXml!!!!!!!!");
		return map;
	}
	
	/** 
	  * 替换一个字符串中的某些指定字符 
	  * @param strData String 原始字符串 
	  * @param regex String 要替换的字符串 
	  * @param replacement String 替代字符串 
	  * @return String 替换后的字符串 
	  */  
	 private static String replaceString(String strData, String regex,  
	         String replacement)  
	 {  
	     if (strData == null)  
	     {  
	         return null;  
	     }  
	     int index;  
	     index = strData.indexOf(regex);  
	     String strNew = "";  
	     if (index >= 0)  
	     {  
	         while (index >= 0)  
	         {  
	             strNew += strData.substring(0, index) + replacement;  
	             strData = strData.substring(index + regex.length());  
	             index = strData.indexOf(regex);  
	         }  
	         strNew += strData;  
	         return strNew;  
	     }  
	     return strData;  
	 }  
	  
	 /** 
	  * 
	  * 替换XML字符串中特殊字符 .
	  * @author WernGin jhhuanga@isoftstone.com
	  * @time 2014-1-29
	  */
	public static String encodeXMLString(String strData)  
	 {  
	     if (strData == null)  
	     {  
	         return "";  
	     }  
	     strData = replaceString(strData, "&", "&amp;");  
	     strData = replaceString(strData, "<", "&lt;");  
	     strData = replaceString(strData, ">", "&gt;");  
	     strData = replaceString(strData, "&apos;", "&apos;");  
	     strData = replaceString(strData, "\"", "&quot;");  
	     return strData;  
	 }  
	  
	 /**
	  *  
	  * 还原字符串中特殊字符 .
	  * @author WernGin jhhuanga@isoftstone.com
	  * @time 2014-1-29
	  */
	public static String decodeXMLString(String strData)  
	 {  
	     strData = replaceString(strData, "&lt;", "<");  
	     strData = replaceString(strData, "&gt;", ">");  
	     strData = replaceString(strData, "&apos;", "&apos;");  
	     strData = replaceString(strData, "&quot;", "\"");  
	     strData = replaceString(strData, "&amp;", "&");  
	     return strData;  
	 }
	
}