/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sms.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sms.entity.BitSms;

/**
 * 短信发送DAO接口
 * @author hzf
 * @version 2017-12-17
 */
@MyBatisDao
public interface BitSmsDao extends CrudDao<BitSms> {
	
}