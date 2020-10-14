package com.cmall.groupcenter.flow;

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
 * 客服确认换货
 * 
 * @author jlin
 *
 */
public class FlowForSureChangeGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		MDataMap exchangeGoodsInfo = DbUp.upTable("oc_exchange_goods").one("uid", flowCode);
		
		RootResult result = new RootResult();
		
		// 确认换货时如果是京东订单则创建京东售后单
		if(Constants.SMALL_SELLER_CODE_JD.equals(exchangeGoodsInfo.get("small_seller_code"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", exchangeGoodsInfo.get("exchange_no"));
			// 京东售后单不存在时创建
			if(orderAfterSale == null || StringUtils.isBlank(orderAfterSale.get("afs_service_id"))) {
				result = new JdAfterSaleSupport().execAfsApplyCreate(exchangeGoodsInfo.get("exchange_no"));
			}
		}
		
		// 确认换货时如果是多货主订单则创建多货主售后单
		if("4497471600430002".equals(exchangeGoodsInfo.get("delivery_store_type"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_duohz_after").one("asale_code", exchangeGoodsInfo.get("exchange_no"));
			// 多货主售后单不存在时创建
			if(orderAfterSale == null || StringUtils.isBlank(orderAfterSale.get("dhz_asale_code"))) {
				result = new DuohzAfterSaleSupport().execApplyAfterSale(exchangeGoodsInfo.get("exchange_no"));
			}
		}
		
		return result;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		MDataMap exchangeGoodsInfo = DbUp.upTable("oc_exchange_goods").one("uid", flowCode);
		String exchange_no=exchangeGoodsInfo.get("exchange_no");
		String order_code=exchangeGoodsInfo.get("order_code");
		String flag_return_goods=exchangeGoodsInfo.get("flag_return_goods");
		String now=DateUtil.getSysDateTimeString();
		
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040001","update_time",now,"available","0","asale_code",exchange_no), "oac_status,update_time,available", "asale_code");
		
		String asaleStatus = "";
		if("4497477800090001".equals(flag_return_goods)){
			asaleStatus = "4497477800050010";  // 待完善物流
		} else {
			asaleStatus = "4497477800050013";  // 换货中
		}
		
		// 京东订单需要根据寄回方式确认是否需要完善物流信息
		if(Constants.SMALL_SELLER_CODE_JD.equals(exchangeGoodsInfo.get("small_seller_code"))) {
			MDataMap orderAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", exchange_no);
			if(orderAfterSale != null && "4".equals(orderAfterSale.get("pickware_type"))) { // 上门取件默认是换货中
				asaleStatus = "4497477800050013";  // 换货中
			}
		}
		
		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",exchange_no,"asale_status",asaleStatus,"update_time",now,"flow_end","0"), "asale_status,update_time,flow_end", "asale_code");
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}
		
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", exchange_no);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", asaleStatus);
		loasMap.put("remark", "[客服确认换货"+("4497477800090002".equals(flag_return_goods)?"不退货":"")+"]"+mSubMap.get("remark"));
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
//		if(DbUp.upTable("lc_order_after_sale").count("asale_code", exchange_no,"asale_status", "4497477800050010")>1){
//			return result;
//		}
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", exchange_no);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", "4497477800070001");
		lsasMap.put("create_time", now);
		
		MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100014");
		lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),exchangeGoodsInfo.get("after_sale_address"),exchangeGoodsInfo.get("after_sale_person"),exchangeGoodsInfo.get("after_sale_mobile"),exchangeGoodsInfo.get("after_sale_postcode")));
		lsasMap.put("serial_title", templateMap.get("template_title"));
		lsasMap.put("template_code", templateMap.get("template_code"));
		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		
		// 待完善物流
		if("4497477800050010".equals(asaleStatus)){
			MDataMap templateMap1=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100018");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap1.get("template_context")));
			lsasMap.put("serial_title", templateMap1.get("template_title"));
			lsasMap.put("template_code", templateMap1.get("template_code"));
			DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		}
		
		
		DbUp.upTable("lc_exchangegoods").dataInsert(new MDataMap("exchange_no",exchange_no,"info",loasMap.get("remark"),"create_user",create_user,"now_status",toStatus,"create_time",now));
		
		// 非京东订单下发短信通知
		if(!Constants.SMALL_SELLER_CODE_JD.equals(exchangeGoodsInfo.get("small_seller_code"))) {
			if("4497477800090001".equals(exchangeGoodsInfo.get("flag_return_goods"))){
				MMessage messages = new MMessage();
				messages.setMessageContent(FormatHelper.formatString(bConfig("groupcenter.FlowForSureReturnGoods_msm1"), exchangeGoodsInfo.get("after_sale_address"),exchangeGoodsInfo.get("after_sale_person"),exchangeGoodsInfo.get("after_sale_mobile"),exchangeGoodsInfo.get("after_sale_postcode"),StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
				messages.setMessageReceive(exchangeGoodsInfo.get("buyer_mobile"));
				messages.setSendSource("4497467200020006");
				MessageSupport.INSTANCE.sendMessage(messages);
			}
		}
		
		
		return result;
	}
}
