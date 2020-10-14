package com.cmall.groupcenter.flow;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.duohuozhu.support.OrderForDuohuozhuSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 客服拒绝审批
 * 
 * @author jlin
 *
 */
public class FlowForCancelReturnGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		MDataMap returnGoodsInfo = DbUp.upTable("oc_return_goods").one("uid", flowCode);
		// 多货主订单同步调用取消接口
		if("4497471600430002".equals(returnGoodsInfo.get("delivery_store_type"))) {
			MWebResult cancelRes = new OrderForDuohuozhuSupport().cancelReturnGoods(returnGoodsInfo.get("return_code"));
			if(!cancelRes.upFlagTrue()) {
				return cancelRes;
			}
		}
		return null;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		MDataMap returnGoodsInfo = DbUp.upTable("oc_return_goods").one("uid", flowCode);
		String return_code=returnGoodsInfo.get("return_code");
		String order_code=returnGoodsInfo.get("order_code");
		
		String now=DateUtil.getSysDateTimeString();
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040003","update_time",now,"available","1","asale_code",return_code), "oac_status,update_time,available", "asale_code");
		
		// 售后单状态根据用户取消还是客服取消做不同变更
		String afterSaleSatus = "4497477800050004";
		String createSource = "4497477800070001";
		if("4497153900050007".equals(toStatus)) { // 用户取消
			afterSaleSatus = "4497477800050011";
			createSource = "4497477800070002";
		}
		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_code,"asale_status",afterSaleSatus,"update_time",now,"flow_end","1"), "asale_status,update_time,flow_end", "asale_code"); 
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}else{
			create_user="system";
		}
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", return_code);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", afterSaleSatus);
		if("4497153900050007".equals(toStatus)) { // 用户取消
			if("system".equals(create_user)) {//定时取消的订单。
				loasMap.put("remark", "[物流维护超时自动取消]"+mSubMap.get("remark"));
				createSource = "4497477800070001";
			}else {
				loasMap.put("remark", "[用户取消退货]"+mSubMap.get("remark"));
			}
		} else {
			loasMap.put("remark", "[客服取消退货]"+mSubMap.get("remark"));
		}
		
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", return_code);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", createSource);
		lsasMap.put("create_time", now);
		
		MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100009");
		if("4497153900050007".equals(toStatus)) {
			if("4497477800070002".equals(createSource)) {// 用户取消申请
				templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100023");
			}else {// 系统自动取消
				templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100027");
			}
		}
		lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"), StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
		lsasMap.put("serial_title", templateMap.get("template_title"));
		lsasMap.put("template_code", templateMap.get("template_code"));
		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		
		DbUp.upTable("lc_return_goods_status").dataInsert(new MDataMap("return_no",return_code,"info",loasMap.get("remark"),"create_user",create_user,"status",toStatus,"create_time",now));
		
		
		MDataMap saleMap = DbUp.upTable("oc_order_after_sale").one("asale_code", return_code);
//		if ("4497477800060002".equals(saleMap.get("asale_source"))) {
//		}
		for (MDataMap MDataMap : DbUp.upTable("oc_order_achange").queryByWhere("asale_code", return_code)) {
			
			String sku_code = MDataMap.get("sku_code");
			
			MDataMap detail = DbUp.upTable("oc_orderdetail").one("order_code", order_code, "sku_code", sku_code);
			
			
			int count=0;
			Map<String, Object> map1=DbUp.upTable("oc_order_achange").dataSqlOne("select sum(oac_num) count from oc_order_achange where order_code=:order_code and sku_code=:sku_code and available=:available and oac_type=:oac_type", new MDataMap("order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001"));
			if(map1!=null&&!map1.isEmpty()){
				Object obj=map1.get("count");
				if(obj!=null){
					count=((BigDecimal) obj).intValue();
				}
			}
			
//				int count = DbUp.upTable("oc_order_achange").count("order_code", order_code, "sku_code", sku_code, "available", "0", "oac_type","4497477800030001");
			int all_count = Integer.valueOf(detail.get("sku_num"));
			if (all_count > count) {// 还有可以售后的商品
				DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code", order_code, "flag_asale", "0", "sku_code", sku_code, "asale_code", ""),"flag_asale,asale_code", "order_code,sku_code");
			}
		}
		
		MDataMap changeMap = DbUp.upTable("mc_member_integral_change").one("member_code",returnGoodsInfo.get("buyer_code"),"change_type","449748080005","remark", return_code);
		if(changeMap != null){
			new PlusServiceAccm().addExecInfoForCancelReturnGiveAccmAmt(return_code);
		}
		//消息推送表数据插入NG++ 20190620
		if("4497477800050004".equals(afterSaleSatus)) {
			String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
			String message = "客服关闭了您的售后申请，如有疑问请联系客服";
			DbUp.upTable("nc_aftersale_push_news").dataInsert(new MDataMap("uid",uuid,"member_code",saleMap.get("buyer_code"),"title",templateMap.get("template_title"),"message",message,"create_time",DateUtil.getSysDateTimeString(),"push_times","0","checker",create_user,"after_sale_code",return_code,"after_sale_status",afterSaleSatus,"to_page","13","if_read","0"));
		}
		return result;
	}
}
