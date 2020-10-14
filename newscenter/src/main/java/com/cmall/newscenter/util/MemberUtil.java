package com.cmall.newscenter.util;

import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class MemberUtil {

	public String getLoginName(String memberCode) {

		String mobile_phone = "";

		MDataMap map = DbUp.upTable("mc_extend_info_star").one("member_code",
				memberCode);

		if (map != null) {

			mobile_phone = map.get("mobile_phone");

		}

		return mobile_phone;
	}

	public String getNikeName(String memberCode) {

		String nikeName = "";

		MDataMap map = DbUp.upTable("mc_extend_info_star").one("member_code",
				memberCode);

		if (map != null) {

			nikeName = map.get("nickname");

		}

		return nikeName;
	}

	/* 訂單號查詢收貨人電話 */
	public String getOrderPerson(String orderCode) {

		String order_person = "";

		MDataMap map = DbUp.upTable("oc_orderadress").one("order_code",
				orderCode);

		if (map != null) {

			order_person = map.get("receive_person");

		}

		return order_person;

	}

	/* 訂單號查詢收貨人電話 */
	public String getOrderMoblie(String orderCode) {

		String order_moblie = "";

		MDataMap map = DbUp.upTable("oc_orderadress").one("order_code",
				orderCode);

		if (map != null) {

			order_moblie = map.get("mobilephone");

		}

		return order_moblie;

	}

	public String getLongUrl(String shortUrl) {

		String longUrl = "";

		MDataMap map = DbUp.upTable("nc_short_url").one("short_url", shortUrl);

		if (map != null) {

			longUrl = map.get("long_url");

		}

		return longUrl;

	}

	/**
	 * 详细地址
	 * 
	 * @param address_id
	 * @return
	 */
	public String addressName(String address_id) {

		MDataMap mDataMap = DbUp.upTable("nc_address").one("address_id",
				address_id);

		String addressName = "";

		if (mDataMap != null && !mDataMap.isEmpty()) {

			addressName = mDataMap.get("address_street");
		}

		return addressName;
	}

	/**
	 * 代理商等级
	 * 
	 * @param agent_code
	 * @return
	 */
	public MDataMap Agent_name(String agent_code) {

		MDataMap mDataMap = DbUp.upTable("nc_agency").one("level_number",
				agent_code);

		MDataMap mDataMap2 = new MDataMap();

		if (mDataMap != null && !mDataMap.isEmpty()) {

			mDataMap2.put("agent_mobilephone", mDataMap.get("agent_mobilephone")); 
			mDataMap2.put("agent_wechat",mDataMap.get("agent_wechat"));
			mDataMap2.put("agent_name", mDataMap.get("agent_name"));
			mDataMap2.put("agent_level", mDataMap.get("agent_level"));
		}

		return mDataMap2;
	}
	
	/**
	 * 代理商名称
	 * 
	 * @param agent_code
	 * @return
	 */
	public String Agent_grade(String agent_code) {

		MDataMap mDataMap = DbUp.upTable("nc_agency_level").one("agency_level",
				agent_code);

		String agentName = "";

		if (mDataMap != null && !mDataMap.isEmpty()) {

			agentName = mDataMap.get("level_name");
		}

		return agentName;
	}
	
	
	/**
	 * 代理商上级
	 * 
	 * @param agent_code
	 * @return
	 */
	public String Agent_parent(String agent_code) {

		MDataMap mDataMap = DbUp.upTable("nc_agency").one("level_number",
				agent_code);

		String parent = "";

		if (mDataMap != null && !mDataMap.isEmpty()) {

			parent = mDataMap.get("parent_id");
		}

		return parent;
	}
	
	/**
	 * 商品名称
	 * 
	 * @param agent_code
	 * @return
	 */
	public String product_name(String delivery_no) {

		List<MDataMap> ListMap = DbUp.upTable("nc_delivery_details").queryByWhere("delivery_no",
				delivery_no);

		String productName = "";

		if (ListMap.size()!=0) {

			MDataMap nameMap = new MDataMap();
			
			nameMap.put("securityx_code", ListMap.get(0).get("dimensional_code"));
			
			nameMap.put("securityh_code", ListMap.get(0).get("dimensional_code"));
			
			List<MDataMap> listName = DbUp.upTable("nc_agent_details").queryAll("", "", " securityx_code=:securityx_code or securityh_code=:securityh_code", nameMap);
			if(listName.size()!=0){
				
				productName = listName.get(0).get("security_productname");
			}
			
		}

		return productName;
	}
}
