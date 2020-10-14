package com.cmall.groupcenter.flow;

import java.util.UUID;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.service.goods.ReturnGoodsApi;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 商户拒绝退货
 * 
 * @author jlin
 *
 */
public class FlowForRefuseGetGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		return null;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		ReturnGoods goods = new ReturnGoodsApi().getReturnGoodsCodeByUid(flowCode);
		String return_code=goods.getReturn_code();
		
		String flag_return_goods=goods.getFlag_return_goods();
		
		String now=DateUtil.getSysDateTimeString();
		
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040001","update_time",now,"available","0","asale_code",return_code), "oac_status,update_time,available", "asale_code");
		if("4497477800090001".equals(flag_return_goods)){
			DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_code,"asale_status","4497477800050005","update_time",now,"flow_end","0"), "asale_status,update_time,flow_end", "asale_code"); 
		}
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}
		
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", return_code);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", "4497477800050005");
		loasMap.put("remark", "[商户拒绝退货]"+mSubMap.get("remark"));
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", return_code);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", "4497477800070001");
		lsasMap.put("create_time", now);
		
		MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100024");
		lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),mSubMap.get("remark").toString()));
		lsasMap.put("serial_title", templateMap.get("template_title"));
		lsasMap.put("template_code", templateMap.get("template_code"));
		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		
		DbUp.upTable("lc_return_goods_status").dataInsert(new MDataMap("return_no",return_code,"info",loasMap.get("remark"),"create_user",create_user,"status",toStatus,"create_time",now));
		//消息推送表数据插入NG++ 20190620
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String message = "商户驳回了您的售后申请，正在等待客服审核";
		MDataMap saleMap = DbUp.upTable("oc_order_after_sale").one("asale_code",return_code);
		DbUp.upTable("nc_aftersale_push_news").dataInsert(new MDataMap("uid",uuid,"member_code",saleMap.get("buyer_code"),"title",templateMap.get("template_title"),"message",message,"create_time",DateUtil.getSysDateTimeString(),"push_times","0","checker",create_user,"after_sale_code",return_code,"after_sale_status","4497477800050005","to_page","13","if_read","0"));
		return result;
	}
}
