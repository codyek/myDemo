package com.thinkgem.jeesite.modules.platform.dao.trade;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitTrade;

/**
 * 交易主表DAO接口
 * @author hzf
 * @version 2017-08-27
 */
@MyBatisDao
public interface BitTradeDao extends CrudDao<BitTrade> {
	
	public BitTrade getByCode(String code);
}