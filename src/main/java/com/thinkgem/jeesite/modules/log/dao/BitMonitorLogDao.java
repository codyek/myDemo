package com.thinkgem.jeesite.modules.log.dao;

import java.util.List;

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
	
	public List<BitMonitorLog> findAllSendList();
}