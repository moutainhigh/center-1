package com.cmall.groupcenter.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 与微公社同步退货审核成功访问层
 * 
 * @author zmm
 *
 */
@Repository
public class RsyncReturnOrderDao {
	
	/**
	 * 查询需同步的订单信息（状态：退货通过审核）
	 * @param startDate
	 * 		统计开始日期
	 * @param endDate
	 * 		统计结束日期
	 * @return 
	 * 	List<MDataMap>订单信息集合
	 */
	public List<MDataMap> queryRsyncReturnOrderList(MDataMap statusMap){
		
		List<MDataMap> list = null;
		
		StringBuffer whereSql = new StringBuffer();
		
		MDataMap mWhereMap = new MDataMap();
		
		whereSql.append("order_code like :order_code");
		
//		if(StringUtils.isNotBlank(startDate)){
//			
//			whereSql.append(" and create_time >=:startDate ");
//			
//			mWhereMap.put("startDate", startDate);
//		}
//		
//		if(StringUtils.isNotBlank(endDate)){
//			
//			whereSql.append(" and create_time <:endDate ");
//			
//			mWhereMap.put("endDate", endDate);
//			
//		}
		
		mWhereMap.put("order_code", "DD%");
		
		whereSql.append(" and status in (");
		
		Iterator<String> keys = statusMap.keySet().iterator();
		
		StringBuffer whereStatus = new StringBuffer();
		
		while (keys.hasNext()) {
			
			String key = (String) keys.next();
			
			whereStatus.append(":").append(key).append(",");
			
			mWhereMap.put(key, statusMap.get(key));
			
		}
		
		whereSql.append(whereStatus.substring(0, whereStatus.length()-1)).append(")");
		
		list = DbUp.upTable("oc_return_goods").queryAll("order_code,create_time,status", "", whereSql.toString(), mWhereMap);
		
		return list;
		
	}
	
}
