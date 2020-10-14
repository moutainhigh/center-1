package com.cmall.ordercenter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.Hash;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 类: FaPiaoInfoBookService <br>
 * 描述: 发票信息查看 <br>
 * 作者: zhangbo<br>
 * 时间: 2019年6月20日 上午10:07:52
 */
public class FaPiaoInfoBookService extends BaseClass {


/*	public  List<Map<String,Object>> getFaPiaoInfo(String uid) {
		List<Map<String,Object>> list = DbUp.upTable("oc_documents_info").dataSqlList("select * from oc_documents_info where uid=:uid", new MDataMap("uid",uid));
		return list;
	}*/

	public  List<Map<String,Object>> getFaPiaoInfoWeiHu(String uid) {
		List<Map<String,Object>> list = DbUp.upTable("uc_seller_invoice_info").dataSqlList("select a.* from usercenter.uc_seller_invoice_info a, ordercenter.oc_documents_info b where  a.small_seller_code=b.small_seller_code and b.uid=:uid", new MDataMap("uid",uid));
		return list;
	}
	
	public  Map<String,Object> getFaPiaoInfoWeiHu2(String uid,String submit_flow) {
		if(!"44975003004".equals(submit_flow)) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("taxpayer_certificate_input", "");
			map.put("bank_account", "");
			map.put("address", "");
			map.put("phone", "");
			map.put("receiver_address", "");
			map.put("receiver_name", "");
			map.put("mail", "");
			map.put("telphone_num", "");
			Map<String,Object>  relMap = DbUp.upTable("uc_seller_invoice_info").dataSqlOne("select a.* from usercenter.uc_seller_invoice_info a, ordercenter.oc_documents_info b where  a.small_seller_code=b.small_seller_code and b.uid=:uid", new MDataMap("uid",uid));
			return relMap==null?map:relMap;
		}
		else {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("taxpayer_certificate_input", "");
			map.put("bank_account", "");
			map.put("address", "");
			map.put("phone", "");
			map.put("receiver_address", "");
			map.put("receiver_name", "");
			map.put("mail", "");
			map.put("telphone_num", "");
			Map<String,Object>  relMap = DbUp.upTable("uc_seller_invoice_info_over").dataSqlOne("select * from usercenter.uc_seller_invoice_info_over  where uid=:uid", new MDataMap("uid",uid));
			return relMap==null?map:relMap;
		}
	}

	public List<Map<String, Object>> getOperateLogs(String uid) {
		List<Map<String, Object>> list = DbUp.upTable("lc_fapiao_log").dataSqlList("select a.* from logcenter.lc_fapiao_log a,ordercenter.oc_documents_info b where a.document_code=b.document_code and b.uid=:uid order by a.operating_time desc",  new MDataMap("uid",uid));
		return list;
	}

	public List<Map<String, Object>> getFaPiaoOperateLog(String small_seller_code) {
		List<Map<String, Object>> list = DbUp.upTable("lc_fapiao_seller_infos_log").dataSqlList("select * from logcenter.lc_fapiao_seller_infos_log  where small_seller_code=:small_seller_code order by zid desc",  new MDataMap("small_seller_code",small_seller_code));
		return list;
	}
/*	public String getFaPiaoType(String uid) {
		Map<String,Object> map = DbUp.upTable("oc_documents_info").dataSqlOne("select * from oc_documents_info where uid=:uid", new MDataMap("uid",uid));;
		return map.get("uc_seller_type").toString();
	}*/
	
	public Map<String,Object> getFaPiaoInfos(String uid) {
		Map<String,Object> map = DbUp.upTable("oc_documents_info").dataSqlOne("select * from oc_documents_info where uid=:uid", new MDataMap("uid",uid));;
		return map;
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
