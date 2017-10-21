package com.thinkgem.jeesite.modules.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Mex_XbtUsd_Depth_Data")
public class MexXbtUsdDepthData extends BaseDepthData{

	//id属性是给mongodb用的，用@Id注解修饰
    @Id
    private String id;
	
}
