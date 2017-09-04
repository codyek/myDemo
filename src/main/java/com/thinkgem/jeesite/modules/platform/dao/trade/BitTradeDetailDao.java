/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.dao.trade;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTradeDetail;

/**
 * 交易明细信息DAO接口
 * @author hzf
 * @version 2017-08-27
 */
@MyBatisDao
public interface BitTradeDetailDao extends CrudDao<BitTradeDetail> {
	
	public BitTradeDetail getByCode(String code);
}