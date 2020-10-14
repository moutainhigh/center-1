package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;


/** 
* @ClassName: InvoiceService 
* @Description: 权威标识管理
* @author 张海生
* @date 2015-1-20 下午4:24:40 
*  
*/
public class InvoiceService extends BaseClass {

	/** 
	* @Description:权威标识查询
	* @param appCode 应用编号
	* @author 张海生
	* @date 2015-1-20 下午4:24:54
	* @return List<MDataMap> 
	* @throws 
	*/
	public List<String> getInvoiceList(String appCode) {
		List<String> result = new ArrayList<String>();
		List<MDataMap> vList = DbUp.upTable("oc_invoice").queryAll("invoice_content",
				"invoice_order desc", null,
				new MDataMap("manage_code", appCode));
		for (int i = 0; i < vList.size(); i++) {
			result.add(vList.get(i).get("invoice_content"));
		}
		return result;
	}
	/** 
	* @Description:权威标识查询(走缓存)
	* @param appCode 应用编号
	* @author 张海生
	* @date 2015-1-20 下午4:24:54
	* @return List<MDataMap> 
	* @throws 
	*/
	public List<String> getInvoiceListRedis(String appCode) {
		List<String> result = new ArrayList<String>();
		List<MDataMap> vList = WebTemp.upTempDataList("oc_invoice", "invoice_content", "invoice_order desc", "", "manage_code",appCode);
		for (int i = 0; i < vList.size(); i++) {
			result.add(vList.get(i).get("invoice_content"));
		}
		return result;
	}
}
