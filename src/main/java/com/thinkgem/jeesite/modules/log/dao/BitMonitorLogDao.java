/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.log.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.log.entity.BitMonitorLog;

/**
 * 监控记录表DAO接口
 * @author hzf
 * @version 2017-12-23
 */
@MyBatisDao
public interface BitMonitorLogDao extends CrudDao<BitMonitorLog> {
	
}