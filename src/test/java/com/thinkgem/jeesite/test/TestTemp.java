package com.thinkgem.jeesite.test;

import java.math.BigDecimal;

//import com.alibaba.fastjson.JSONObject;

public class TestTemp {

	public static void main(String[] args) {

		/*String jsonStr = "{\"JACKIE_ZHANG\":\"张学友\",\"ANDY_LAU\":\"刘德华\",\"LIMING\":\"黎明\",\"Aaron_Kwok\":\"郭富城\"}";

		// 做5次测试
		for (int i = 0, j = 5; i < j; i++) {
			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			for (java.util.Map.Entry<String, Object> entry : jsonObject
					.entrySet()) {
				System.out
						.print(entry.getKey() + "-" + entry.getValue() + "\t");
			}
			System.out.println();// 用来换行
		}*/
		BigDecimal deposit = new BigDecimal(669);
		deposit = new BigDecimal((deposit.intValue()/10)*10);
		Integer all = 392;
		System.out.println("aa = "+ deposit);
	}

}
