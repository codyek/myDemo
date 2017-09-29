/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.platform.dao.account;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.platform.entity.account.BitMexAccount;

/**
 * mex 账户信息表DAO接口
 * @author hzf
 * @version 2017-09-13
 */
@MyBatisDao
public interface BitMexAccountDao extends CrudDao<BitMexAccount> {
	
}