package com.cmall.ordercenter.service;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 内购地址
 * @author wz
 *
 */
public class NcStaffAddress extends BaseClass{
	
	/**
	 * 分页查询内购地址
	 * @param page
	 * @param num
	 * @return
	 */
	public List<Map<String, Object>> queryNcStaffAddressAll(int page,int num){
		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> mapParam = orderPageService.pageNumber(page, num);
		
		List<Map<String, Object>> list = DbUp.upTable("nc_staff_address").dataSqlList("select * from nc_staff_address ORDER BY update_time DESC, zid desc LIMIT "+ mapParam.get("startNumber")
				+ ","
				+ mapParam.get("endNumber") + "", new MDataMap());
		
		return list;
	}
	
	/**
	 * 查询所有内购地址
	 * @param page
	 * @param num
	 * @return
	 */
	public List<Map<String, Object>> queryNcStaffAddressAll(){
		
		List<Map<String, Object>> list = DbUp.upTable("nc_staff_address").dataSqlList("select * from nc_staff_address ORDER BY update_time DESC, zid desc", new MDataMap());
		
		return list;
	}
}
