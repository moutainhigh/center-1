package com.cmall.ordercenter.service;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;

/**
 * 
 * 类: RetentionAdjustMoneyService <br>
 * 描述: 质保金额调整 <br>
 * 作者: zhangbo<br>
 * 时间: 2018年9月11日 上午11:08:52
 */
public class RetentionAdjustMoneyService extends BaseClass {


	/**
	 * 
	 * 方法: logs <br>
	 * 描述: 获取质保金调整的操作日志 <br>
     * 作者: zhangbo<br>
     * 时间: 2018年9月11日 上午11:08:52
	 * 
	 * @param small_seller_code
	 * @param type
	 * @return
	 */
	public List<Map<String, Object>> getOperateLogs(String small_seller_code,String receipt_retention_money_code) {	
		String sql = "select adjust_money,adjust_time,adjust_reason,(select real_name from zapdata.za_userinfo where user_code=operator )operator from lc_retention_adjust_money where small_seller_code='"+small_seller_code+"' and receipt_retention_money_code= '"+receipt_retention_money_code+"' order by adjust_time desc";
		List<Map<String, Object>> list = DbUp.upTable("lc_retention_adjust_money").dataSqlList(sql, null);
		return list;
	}

}
