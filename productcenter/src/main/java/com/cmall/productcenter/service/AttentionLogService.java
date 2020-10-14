package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.util.PageService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 查询关注日志表
 * @author wz
 *
 */
public class AttentionLogService extends BaseClass{
	/**
	 * 统计用户关注总数
	 * @param userName
	 * @return
	 */
	public static int countAttentionLog(String buyerCode,String status){
		int count = 0;
		
		MDataMap map = new MDataMap();
		map.put("buyer_code", buyerCode);
		map.put("status", status);
		
		if(!"".equals(buyerCode) && buyerCode!=null && !"".equals(status) && status!=null){
			count = DbUp.upTable("hp_attention_log").dataCount("buyer_code=:buyer_code and status=:status", map);
		}
		return count;
	}
	
	/**
	 * 按条件查询关注相关信息
	 * @param userName
	 * @param productCode
	 * @param status
	 * @return
	 */
	public  List<Map<String,Object>> selectAttentionLogAll(String buyerCode,String productCode,String status,int  nextPage, int num){
		String sql="";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		PageService orderPageService = new PageService();
		Map<String, Integer> pageMap = orderPageService.pageNumber(nextPage,num);
		
		MDataMap map = new MDataMap();
		map.put("buyer_code", buyerCode);
		map.put("product_code", productCode);
		map.put("status", status);
		map.put("startNumber", pageMap.get("startNumber").toString());
		map.put("endNumber", pageMap.get("endNumber").toString());
		
		if(!"".equals(buyerCode)&&buyerCode!=null && ("".equals(productCode)||productCode==null) && !"".equals(status)&&status!=null ){
			sql="select * from hp_attention_log where buyer_code=:buyer_code and status=:status order by create_time desc LIMIT "
									+ map.get("startNumber")
									+ ","
									+ map.get("endNumber") + "";
			list = DbUp.upTable("hp_attention_log").dataSqlList(sql, map);
		}else if(!"".equals(buyerCode)&&buyerCode!=null && !"".equals(productCode) && productCode!=null && !"".equals(status)&&status!=null){
			sql = "select * from hp_attention_log where buyer_code=:buyer_code and status=:status and product_code=:product_code order by create_time desc LIMIT "
									+ map.get("startNumber")
									+ ","
									+ map.get("endNumber") + "";
			list = DbUp.upTable("hp_attention_log").dataSqlList(sql, map);
		}
		return list;
	}
	/**
	 * 关注 更新 插入操作
	 * @param userName
	 * @param productCode
	 * @param states
	 */
	public static void deleteUpdateAttention(String buyerCode,String productCode,String states){ 
		
		
		MDataMap map = new MDataMap();
		map.put("buyer_code", buyerCode);
		map.put("product_code", productCode);
		map.put("create_time", DateUtil.getSysDateTimeString());
		map.put("update_time", DateUtil.getSysDateTimeString());
		map.put("status", states);
		
		if("0".equals(states)){   //加入关注
			DbUp.upTable("hp_attention_log").dataInsert(map);
		}else if("1".equals(states)){ //取消关注
			DbUp.upTable("hp_attention_log").dataUpdate(map, "status,update_time", "buyer_code,product_code");
		}
	}
}
