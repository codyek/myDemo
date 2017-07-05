/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.dao.inter;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.inter.Interfaces;

/**
 * 各平台接口信息DAO接口
 * @author hzf
 * @version 2017-07-06
 */
@MyBatisDao
public interface InterfacesDao extends CrudDao<Interfaces> {
	
}