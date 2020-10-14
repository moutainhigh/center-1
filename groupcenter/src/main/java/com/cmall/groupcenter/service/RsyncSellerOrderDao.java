package com.cmall.groupcenter.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 与微公社同步订单返利数据访问层
 * 
 * @author pangjh
 *
 */
@Repository
public class RsyncSellerOrderDao {
	
	/**
	 * 查询需同步的订单信息（状态：下单成功 未发货、未付款）
	 * @param startDate
	 * 		统计开始日期
	 * @param endDate
	 * 		统计结束日期
	 * @return 
	 * 	List<MDataMap>订单信息集合
	 */
	public List<MDataMap> queryRsyncSellerOrderList(MDataMap statusMap,String startDate,String endDate){
		
		List<MDataMap> list = null;
		
		StringBuffer whereSql = new StringBuffer();
		
		MDataMap mWhereMap = new MDataMap();
		
		//whereSql.append("seller_code =:seller_code and small_seller_code like :small_seller_code and order_code like :order_code");
		whereSql.append("seller_code =:seller_code  and order_code like :order_code");
		
		whereSql.append(" and small_seller_code<>'SF03100094' ");
		
		whereSql.append(" and order_source<>'449715190007' ");
		
		whereSql.append(" and buyer_code != 'MI160913100122' ");
		
		if(StringUtils.isNotBlank(startDate)){
			
			whereSql.append(" and update_time >=:startDate ");
			
			mWhereMap.put("startDate", startDate);
		}
		
		if(StringUtils.isNotBlank(endDate)){
			
			whereSql.append(" and update_time <:endDate ");
			
			mWhereMap.put("endDate", endDate);
			
		}
		
		mWhereMap.put("seller_code", "SI2003");
		
		//mWhereMap.put("small_seller_code", "SF03%");
		
		mWhereMap.put("order_code", "DD%");
		
		whereSql.append(" and order_status in (");
		
		Iterator<String> keys = statusMap.keySet().iterator();
		
		StringBuffer whereStatus = new StringBuffer();
		
		while (keys.hasNext()) {
			
			String key = (String) keys.next();
			
			whereStatus.append(":").append(key).append(",");
			
			mWhereMap.put(key, statusMap.get(key));
			
		}
		
		whereSql.append(whereStatus.substring(0, whereStatus.length()-1)).append(")");
		
		list = DbUp.upTable("oc_orderinfo").queryAll("order_code,update_time,order_status", "", whereSql.toString(), mWhereMap);
		
		return list;
		
	}
	
	/**
	 * 同步沙皮狗订单信息
	 * @return 
	 * 	List<MDataMap>订单信息集合
	 */
	public List<MDataMap> queryRsyncSellerOrderListForSPG(MDataMap statusMap){
		
		List<MDataMap> list = null;
		
		StringBuffer whereSql = new StringBuffer();
		
		MDataMap mWhereMap = new MDataMap();
		
		whereSql.append("seller_code like :seller_code and order_code like :order_code");
		
		mWhereMap.put("seller_code", "SI3003%");
		
		mWhereMap.put("order_code", "DD%");
		
		whereSql.append(" and order_status in (");
		
		Iterator<String> keys = statusMap.keySet().iterator();
		
		StringBuffer whereStatus = new StringBuffer();
		
		while (keys.hasNext()) {
			
			String key = (String) keys.next();
			
			whereStatus.append(":").append(key).append(",");
			
			mWhereMap.put(key, statusMap.get(key));
			
		}
		
		whereSql.append(whereStatus.substring(0, whereStatus.length()-1)).append(")");
		
		list = DbUp.upTable("oc_orderinfo").queryAll("order_code,update_time,order_status", "", whereSql.toString(), mWhereMap);
		
		return list;
		
	}
	

}
