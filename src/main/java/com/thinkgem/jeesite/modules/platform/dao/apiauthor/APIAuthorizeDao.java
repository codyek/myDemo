package com.thinkgem.jeesite.modules.platform.dao.apiauthor;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.apiauthor.APIAuthorize;

/**
 * API授权DAO接口
 * @author hzf
 * @version 2017-07-08
 */
@MyBatisDao
public interface APIAuthorizeDao extends CrudDao<APIAuthorize> {
	
	public APIAuthorize getByUserId(APIAuthorize aPIAuthorize);
}