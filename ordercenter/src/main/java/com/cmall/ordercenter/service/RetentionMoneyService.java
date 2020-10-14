package com.cmall.ordercenter.service;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 类: RetentionMoneyService <br>
 * 描述: 质保金管理相关 <br>
 * 作者: zhy<br>
 * 时间: 2017年6月8日 上午11:08:52
 */
public class RetentionMoneyService extends BaseClass {

	/**
	 * 
	 * 方法: getSellerRetentionMoney <br>
	 * 描述: 根据商户编码查询质保金相关信息 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年6月8日 上午11:26:37
	 * 
	 * @param small_seller_code
	 * @return
	 */
	public MDataMap getSellerRetentionMoney(String small_seller_code) {
		MDataMap data = DbUp.upTable("oc_seller_retention_money").oneWhere("max_retention_money,deduct_retention_money",
				"", "", "small_seller_code", small_seller_code);
		return data;
	}

	/**
	 * 
	 * 方法: logs <br>
	 * 描述: 获取质保金的操作日志 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年6月8日 下午3:28:29
	 * 
	 * @param small_seller_code
	 * @param type
	 * @return
	 */
	public List<Map<String, Object>> getOperateLogs(String small_seller_code, Integer type) {
		String sql = "select retention_money,operate_date,(select real_name from zapdata.za_userinfo where user_code=creator )creator,create_time,remark from logcenter.lc_retention_money where small_seller_code='"
				+ small_seller_code + "' and operate_type=" + type +" order by create_time desc";
		List<Map<String, Object>> list = DbUp.upTable("lc_retention_money").dataSqlList(sql, null);
		return list;
	}

	/**
	 * 
	 * 方法: getSellerType <br>
	 * 描述: 获取商户类型 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年6月9日 上午11:07:53
	 * 
	 * @param small_seller_code
	 * @return
	 */
	public String getSellerType(String small_seller_code) {
		return WebHelper.getSellerType(small_seller_code);
	}
}
