/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.dao.trade;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.trade.BitSymbolConfig;

/**
 * 币种参数配置DAO接口
 * @author hzf
 * @version 2017-09-06
 */
@MyBatisDao
public interface BitSymbolConfigDao extends CrudDao<BitSymbolConfig> {
	
}