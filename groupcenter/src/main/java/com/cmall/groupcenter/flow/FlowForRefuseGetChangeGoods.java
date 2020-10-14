package com.cmall.groupcenter.flow;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 商户拒绝换货
 * 
 * @author jlin
 *
 */
public class FlowForRefuseGetChangeGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		return null;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		MDataMap exchangeGoodsInfo = DbUp.upTable("oc_exchange_goods").one("uid", flowCode);
		String exchange_no=exchangeGoodsInfo.get("exchange_no");
		
		String flag_return_goods=exchangeGoodsInfo.get("flag_return_goods");
		
		String now=DateUtil.getSysDateTimeString();
		
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040001","update_time",now,"available","0","asale_code",exchange_no), "oac_status,update_time,available", "asale_code");
		if("4497477800090001".equals(flag_return_goods)){
			DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",exchange_no,"asale_status","4497477800050013","update_time",now,"flow_end","0"), "asale_status,update_time,flow_end", "asale_code"); 
		}
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}
		
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", exchange_no);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", "4497477800050013");
		loasMap.put("remark", "[商户拒绝换货]"+mSubMap.get("remark"));
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", exchange_no);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", "4497477800070001");
		lsasMap.put("create_time", now);
		
//		MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100004");
//		lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context")));
//		lsasMap.put("serial_title", templateMap.get("template_title"));
//		lsasMap.put("template_code", templateMap.get("template_code"));
//		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		
		DbUp.upTable("lc_exchangegoods").dataInsert(new MDataMap("exchange_no",exchange_no,"info",loasMap.get("remark"),"create_user",create_user,"now_status",toStatus,"create_time",now));
		
		return result;
	}
}
