package com.thinkgem.jeesite.modules.platform.dao.trade;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitMonitor;

/**
 * 监控管理DAO接口
 * @author hzf
 * @version 2017-08-27
 */
@MyBatisDao
public interface BitMonitorDao extends CrudDao<BitMonitor> {
	public BitMonitor getByCode(String code);
}