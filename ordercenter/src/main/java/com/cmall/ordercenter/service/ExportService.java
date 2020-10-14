package com.cmall.ordercenter.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;

public class ExportService  extends BaseClass{
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 导出订单数据到excel
	 */
	public void export(){
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT a.order_code,a.create_time,a.order_status,a.product_name,b.product_code,b.sku_price,b.sku_num ,c.seller_name");
		sb.append("FROM `ordercenter`.`oc_orderinfo` a ,`ordercenter`.`oc_orderdetail` b,`usercenter`.`uc_sellerinfo` c");
		sb.append("where a.order_code = b.order_code and a.seller_code = c.seller_code  LIMIT 0,1000");
		
		DbTemplate jdbcTemplate = DbUp.upTable("oc_orderinfo").upTemplate();
		Map<String,Object> dbMap = jdbcTemplate.queryForMap(sb.toString(), paramMap);
	}
//	/**
//	 * 状态更新测试
//	 * 
//	 * @param mDataMap
//	 * @return
//	 */
//	public boolean add(MDataMap mDataMap) {
//		log.info("add 添加测试......");
//		try {
//			MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
//			
//			System.out.println(mAddMaps.get("uid"));
//			System.out.println(mAddMaps.get("name"));
//			System.out.println(mAddMaps.get("addr"));
//			System.out.println(mAddMaps.get("phone"));
//			System.out.println(mAddMaps.get("money"));
//			System.out.println(mAddMaps.get("pay_type"));
//			
//			//DbUp.upTable("za_test_xxx").update(mAddMaps);
//			
//			DbUp.upTable("za_test_xxx").dataInsert(mAddMaps);
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//	public boolean update(MDataMap mDataMap) {
//		log.info("update 修改 测试......");
//		try {
//			MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
//			
//			System.out.println(mAddMaps.get("uid"));
//			System.out.println(mAddMaps.get("name"));
//			System.out.println(mAddMaps.get("addr"));
//			System.out.println(mAddMaps.get("phone"));
//			System.out.println(mAddMaps.get("money"));
//			System.out.println(mAddMaps.get("pay_type"));
//			
//			DbUp.upTable("za_test_xxx").update(mAddMaps);
//			
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//	public boolean insert(MDataMap mDataMap) {
//		System.out.println("insert ......");
//		boolean flag = true;
//		try {
//			MDataMap map = new MDataMap();
////			map.put("name", mDataMap.get("name"));
////			map.put("addr", mDataMap.get("addr"));
////			map.put("phone", mDataMap.get("phone"));
////			map.put("money", mDataMap.get("money"));
////			map.put("pay_type", mDataMap.get("pay_type"));
//			
//			map.put("name", "kkk");
//			map.put("addr", "ccc");
//			map.put("phone","123");
//			map.put("money", "1");
//			map.put("pay_type", "11111");
//			DbUp.upTable("za_test_xxx").dataInsert(map);
//		} catch (Exception e) {
//			flag = false;
//			// 异常处理待定
//		}
//		return flag;
//	}

}
