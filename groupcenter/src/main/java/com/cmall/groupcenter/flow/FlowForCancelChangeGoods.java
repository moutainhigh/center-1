package com.cmall.groupcenter.flow;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.duohuozhu.support.OrderForDuohuozhuSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.systemface.IFlowFunc;
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
public class FlowForCancelChangeGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		MDataMap exchangeGoodsInfo = DbUp.upTable("oc_exchange_goods").one("uid", flowCode);
		// 多货主订单同步调用取消接口
		if("4497471600430002".equals(exchangeGoodsInfo.get("delivery_store_type"))) {
			MWebResult cancelRes = new OrderForDuohuozhuSupport().cancelExchangeGoods(exchangeGoodsInfo.get("exchange_no"));
			if(!cancelRes.upFlagTrue()) {
				return cancelRes;
			}
		}
		return null;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		MDataMap exchangeGoodsInfo = DbUp.upTable("oc_exchange_goods").one("uid", flowCode);
		String exchange_no=exchangeGoodsInfo.get("exchange_no");
		String order_code=exchangeGoodsInfo.get("order_code");
		
		String now=DateUtil.getSysDateTimeString();
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040003","update_time",now,"available","1","asale_code",exchange_no), "oac_status,update_time,available", "asale_code");
		
		// 售后单状态根据用户取消还是客服取消做不同变更
		String afterSaleSatus = "4497477800050004";
		String createSource = "4497477800070001";
		if("4497153900020007".equals(toStatus)) { // 用户取消
			afterSaleSatus = "4497477800050011";
			createSource = "4497477800070002";
		}
		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",exchange_no,"asale_status",afterSaleSatus,"update_time",now,"flow_end","1"), "asale_status,update_time,flow_end", "asale_code");
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}else{
			create_user="system";
		}
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", exchange_no);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", afterSaleSatus);
		if("4497153900020007".equals(toStatus)) { // 用户取消
			loasMap.put("remark", "[用户取消换货]"+mSubMap.get("remark"));
		} else {
			loasMap.put("remark", "[客服取消换货]"+mSubMap.get("remark"));
		}
		
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", exchange_no);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", createSource);
		lsasMap.put("create_time", now);
		
		MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100015");
		lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"), StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
		lsasMap.put("serial_title", templateMap.get("template_title"));
		lsasMap.put("template_code", templateMap.get("template_code"));
		
		if("4497153900020007".equals(toStatus)) { // 用户取消
			lsasMap.put("serial_msg", "买家取消申请售后");
			lsasMap.put("serial_title", "取消申请换货成功");
			//lsasMap.put("template_code", templateMap.get("template_code"));
		}
		
		DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		
		DbUp.upTable("lc_exchangegoods").dataInsert(new MDataMap("exchange_no",exchange_no,"info",loasMap.get("remark"),"create_user",create_user,"now_status",toStatus,"create_time",now));
		
		
//		for (MDataMap MDataMap : DbUp.upTable("oc_order_achange").queryByWhere("asale_code",exchange_no)) {
//			
//			String sku_code = MDataMap.get("sku_code");
//			
//			MDataMap detail=DbUp.upTable("oc_orderdetail").one("order_code",order_code,"sku_code",sku_code);
//			
//			int count=DbUp.upTable("oc_order_achange").count("order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001");
//			int all_count=Integer.valueOf(detail.get("sku_num"));
//			if(all_count>count){//还有可以售后的商品
//				if(DbUp.upTable("oc_order_achange").count("order_code",order_code,"sku_code",sku_code,"available","0","asale_source","4497477800060001")<1){
//					DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code", order_code,"flag_asale","0","sku_code",sku_code,"asale_code",""), "flag_asale,asale_code", "order_code,sku_code");
//				}
//			}
//			
//		}
		
		//后台发起的，判断一下前端是否可用发起申请
		MDataMap saleMap=DbUp.upTable("oc_order_after_sale").one("asale_code",exchange_no);
		if("4497477800060002".equals(saleMap.get("asale_source"))){
			
			for (MDataMap MDataMap : DbUp.upTable("oc_order_achange").queryByWhere("asale_code",exchange_no)) {
				
				String sku_code = MDataMap.get("sku_code");
				
				MDataMap detail=DbUp.upTable("oc_orderdetail").one("order_code",order_code,"sku_code",sku_code);
				
				
				int count=0;
				Map<String, Object> map1=DbUp.upTable("oc_order_achange").dataSqlOne("select sum(oac_num) count from oc_order_achange where order_code=:order_code and sku_code=:sku_code and available=:available and oac_type=:oac_type", new MDataMap("order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001"));
				if(map1!=null&&!map1.isEmpty()){
					Object obj=map1.get("count");
					if(obj!=null){
						count=((BigDecimal) obj).intValue();
					}
				}
//				int count=DbUp.upTable("oc_order_achange").count("order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001");
				int all_count=Integer.valueOf(detail.get("sku_num"));
				if(all_count>count){//还有可以售后的商品
					DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code", order_code,"flag_asale","0","sku_code",sku_code,"asale_code",""), "flag_asale,asale_code", "order_code,sku_code");
				}
			}
			
		}
		
		return result;
	}
}
