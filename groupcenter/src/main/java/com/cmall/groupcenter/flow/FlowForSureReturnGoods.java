package com.cmall.groupcenter.flow;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.duohuozhu.support.DuohzAfterSaleSupport;
import com.cmall.groupcenter.jd.JdAfterSaleSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmassystem.Constants;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 客服确认退货
 * 
 * @author jlin
 *
 */
public class FlowForSureReturnGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		MDataMap returnGoodsInfo = DbUp.upTable("oc_return_goods").one("uid", flowCode);
		
		RootResult result = new RootResult();
		
		// 确认退货时如果是京东订单则创建京东售后单
		if(Constants.SMALL_SELLER_CODE_JD.equals(returnGoodsInfo.get("small_seller_code"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", returnGoodsInfo.get("return_code"));
			// 京东售后单不存在时创建
			if(orderAfterSale == null || StringUtils.isBlank(orderAfterSale.get("afs_service_id"))) {
				result = new JdAfterSaleSupport().execAfsApplyCreate(returnGoodsInfo.get("return_code"));
			}
		}
		
		// 确认退货时如果是多货主订单则创建多货主售后单
		if("4497471600430002".equals(returnGoodsInfo.get("delivery_store_type"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_duohz_after").one("asale_code", returnGoodsInfo.get("return_code"));
			// 多货主售后单不存在时创建
			if(orderAfterSale == null || StringUtils.isBlank(orderAfterSale.get("dhz_asale_code"))) {
				result = new DuohzAfterSaleSupport().execApplyAfterSale(returnGoodsInfo.get("return_code"));
			}
		}
		
		return result;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		MDataMap returnGoodsInfo = DbUp.upTable("oc_return_goods").one("uid", flowCode);
		String return_code=returnGoodsInfo.get("return_code");
		String flag_return_goods=returnGoodsInfo.get("flag_return_goods");
		
		String now=DateUtil.getSysDateTimeString();
		
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040001","update_time",now,"available","0","asale_code",return_code), "oac_status,update_time,available", "asale_code");
		
		String asaleStatus = "";
		if("4497477800090001".equals(flag_return_goods)){
			asaleStatus = "4497477800050010";  // 待完善物流
		} else {
			asaleStatus = "4497477800050005";  // 退货中
		}
		
		// 京东订单需要根据寄回方式确认是否需要完善物流信息
		if(Constants.SMALL_SELLER_CODE_JD.equals(returnGoodsInfo.get("small_seller_code"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", returnGoodsInfo.get("return_code"));
			if(orderAfterSale != null && "4".equals(orderAfterSale.get("pickware_type"))) { // 上门取件默认是退货中
				asaleStatus = "4497477800050005";  // 退货中
			}
		}
		
		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_code,"asale_status",asaleStatus,"update_time",now,"flow_end","0"), "asale_status,update_time,flow_end", "asale_code"); 
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}
		
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", return_code);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", asaleStatus);
		loasMap.put("remark", "[客服确认"+("4497477800090002".equals(flag_return_goods)?"不":"")+"退货]"+mSubMap.get("remark"));
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		DbUp.upTable("lc_return_goods_status").dataInsert(new MDataMap("return_no",return_code,"info",loasMap.get("remark"),"create_user",create_user,"status",toStatus,"create_time",now));
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", return_code);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", "4497477800070001");
		lsasMap.put("create_time", now);
		
		MDataMap templateMap=new MDataMap();
		if("4497477800050010".equals(asaleStatus)) {
			templateMap = DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100008");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),returnGoodsInfo.get("address"),returnGoodsInfo.get("contacts"),returnGoodsInfo.get("mobile"),returnGoodsInfo.get("receiver_area_code")));
		}else {
			templateMap = DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100025");
			lsasMap.put("serial_msg",FormatHelper.formatString(templateMap.get("template_context"),mSubMap.get("remark")));
		}
		lsasMap.put("serial_title", templateMap.get("template_title"));
		lsasMap.put("template_code", templateMap.get("template_code"));
		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		String title = "客服审批通过，商家待审核";
		// 待完善物流
		if("4497477800050010".equals(asaleStatus)){
			MDataMap templateMap1=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100018");
			title = templateMap1.get("template_title");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap1.get("template_context")));
			lsasMap.put("serial_title", templateMap1.get("template_title"));
			lsasMap.put("template_code", templateMap1.get("template_code"));
			DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		}
		
		// 非京东订单下发短信通知
		if(!Constants.SMALL_SELLER_CODE_JD.equals(returnGoodsInfo.get("small_seller_code"))) {
			if("4497477800090001".equals(returnGoodsInfo.get("flag_return_goods"))){
				MMessage messages = new MMessage();
				messages.setMessageContent(FormatHelper.formatString(bConfig("groupcenter.FlowForSureReturnGoods_msm"), returnGoodsInfo.get("address"),returnGoodsInfo.get("contacts"),returnGoodsInfo.get("mobile"),returnGoodsInfo.get("receiver_area_code"),StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
				messages.setMessageReceive(returnGoodsInfo.get("buyer_mobile"));
				messages.setSendSource("4497467200020006");
				MessageSupport.INSTANCE.sendMessage(messages);
			}
		}
		//消息推送表数据插入NG++ 20190620
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String message = "";
		if("4497477800050010".equals(asaleStatus)) {//待完善物流
			message = "您的申请已通过，请您按照客服提供的地址回寄，保留快递存单，并在7日内填写物流信息";
		}else if("4497477800050005".equals(asaleStatus)) {//不用晚上物流
			message = "您的申请已通过客服审核，正在等待商家确认";
		}
		MDataMap saleMap = DbUp.upTable("oc_order_after_sale").one("asale_code",return_code);
		DbUp.upTable("nc_aftersale_push_news").dataInsert(new MDataMap("uid",uuid,"member_code",saleMap.get("buyer_code"),"title",title,"message",message,"create_time",DateUtil.getSysDateTimeString(),"push_times","0","checker",create_user,"after_sale_code",return_code,"after_sale_status",asaleStatus,"to_page","13","if_read","0"));
		return result;
	}
}
