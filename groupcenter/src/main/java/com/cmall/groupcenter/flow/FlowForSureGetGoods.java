package com.cmall.groupcenter.flow;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.jd.JdAfterSaleSupport;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.helper.OrderHelper;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.model.api.ApiOrderStatusChangeNoticInput;
import com.cmall.ordercenter.service.ReturnMoneyService;
import com.cmall.ordercenter.service.api.ApiOrderStatusChangeNotic;
import com.cmall.ordercenter.service.goods.ReturnGoodsApi;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmasorder.service.TeslaCrdtService;
import com.srnpr.xmasorder.service.TeslaPpcService;
import com.srnpr.xmassystem.Constants;
import com.srnpr.xmassystem.enumer.HjyBeanExecType;
import com.srnpr.xmassystem.service.HjybeanService;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.websupport.ApiCallSupport;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 商户确认收到货
 * 
 * @author jlin
 *
 */
public class FlowForSureGetGoods extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		return null;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {

		RootResult result = new RootResult();

		ReturnGoods goods = new ReturnGoodsApi().getReturnGoodsCodeByUid(flowCode);
		String return_code=goods.getReturn_code();
		String flag_return_goods=goods.getFlag_return_goods();
		
		String now=DateUtil.getSysDateTimeString();
		
		
		DbUp.upTable("oc_order_achange").dataUpdate(new MDataMap("oac_status","4497477800040002","update_time",now,"available","0","asale_code",return_code), "oac_status,update_time,available", "asale_code");
		
//		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_code,"asale_status","4497477800050006","update_time",now,"flow_end","1"), "asale_status,update_time,flow_end", "asale_code"); 
		DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_code,"asale_status",(BigDecimal.ZERO.compareTo(goods.getExpected_return_money())<0)?"4497477800050001":"4497477800050002","update_time",now,"flow_end","1"), "asale_status,update_time,flow_end", "asale_code"); 
		
		
		String create_user="";
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		if(userInfo!=null){
			create_user=userInfo.getUserCode();
		}
		
		if(StringUtils.isBlank(create_user) && StringUtils.isNotBlank(mSubMap.get("userCodex"))) {
			create_user = mSubMap.get("userCodex");
		}
		
		
		MDataMap loasMap=new MDataMap();
		loasMap.put("asale_code", return_code);
		loasMap.put("create_user", create_user);
		loasMap.put("create_time", now);
		loasMap.put("asale_status", "4497477800050006");
		loasMap.put("remark", "[商户确认"+("4497477800090002".equals(flag_return_goods)?"不":"")+"退货]"+mSubMap.get("remark"));
		loasMap.put("lac_code", WebHelper.upCode("LAC"));
		DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
		
		
		MDataMap lsasMap=new MDataMap();
		lsasMap.put("asale_code", return_code);
		lsasMap.put("lac_code", loasMap.get("lac_code"));
		lsasMap.put("create_source", "4497477800070001");
		lsasMap.put("create_time", now);
		String title = "";
		
		if("4497477800090002".equals(flag_return_goods)){
			
			MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100021");
			title = templateMap.get("template_title");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
			lsasMap.put("serial_title", templateMap.get("template_title"));
			lsasMap.put("template_code", templateMap.get("template_code"));
			DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);

		}else{
			
			MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100010");
			title = templateMap.get("template_title");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),StringUtils.isBlank(mSubMap.get("remark"))?"无":mSubMap.get("remark")));
			lsasMap.put("serial_title", templateMap.get("template_title"));
			lsasMap.put("template_code", templateMap.get("template_code"));
			DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
		}
		
		
		DbUp.upTable("lc_return_goods_status").dataInsert(new MDataMap("return_no",return_code,"info",loasMap.get("remark"),"create_user",create_user,"status",toStatus,"create_time",now));
		
		//退款
		if(Constants.SMALL_SELLER_CODE_JD.equals(goods.getSmall_seller_code())) {
			// 京东退款走异步确认
			new JdAfterSaleSupport().createOrderRefundTask(goods.getOrder_code(),goods.getReturn_code());
		} else {
			creatReturnMoney(goods);
		}
		
		//新增逻辑
		String order_code=goods.getOrder_code();
		Integer allNum=0;
		Integer retNum=0;
		Map<String, Object> detailMap = DbUp.upTable("oc_orderdetail").dataSqlOne("select sum(sku_num) as sku_num from oc_orderdetail where order_code=:order_code", new MDataMap("order_code",order_code));
		if(detailMap!=null&&!detailMap.isEmpty()){
			allNum=((BigDecimal)detailMap.get("sku_num")).intValue();
		}
		
		Map<String, Object> retMap = DbUp.upTable("oc_return_goods").dataSqlOne("SELECT SUM(d.count) as sku_num from oc_return_goods g RIGHT JOIN  oc_return_goods_detail d on g.return_code=d.return_code where g.`status` in ('4497153900050001','4497153900050003') and g.order_code=:order_code", new MDataMap("order_code",order_code));
		if(retMap!=null&&!retMap.isEmpty()){
			retNum=((BigDecimal)retMap.get("sku_num")).intValue();
		}
		
		if(retNum>=allNum){//全部退货
			String order_status="4497153900010005";
//			if(StringUtils.equals(goods_receipt, "4497476900040002")){//注掉原因：全部退货不影响订单主状态
//				order_status="4497153900010006";
//			}
			
			MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140004"), "order_status_ext", "order_code");
			if(!StringUtils.equals(order_status, orderInfo.get("order_status"))){
				DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",orderInfo.get("order_status"),"now_status",order_status,"info","FlowForSureGetGoods"));
				//订单状态变更 调用多彩宝订单状态变更通知接口 -- rhb
				MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", order_code);
				if(dm.containsKey("order_source") && "449715190014".equals(dm.get("order_source"))){
					ApiOrderStatusChangeNoticInput inputParam = new ApiOrderStatusChangeNoticInput();
					inputParam.setJyOrderCode(order_code);
					inputParam.setStatus(OrderHelper.convertStatusCode(toStatus));
					inputParam.setStatusCode(order_status);
					inputParam.setUpdateTime(DateUtil.getSysDateTimeString());
					//添加物流信息
					MDataMap oneWhere = DbUp.upTable("oc_order_shipments").oneWhere("logisticse_name,logisticse_code,waybill", "", "", "order_code", inputParam.getJyOrderCode());
					if(null != oneWhere){
						inputParam.setLogisticseName(oneWhere.get("logisticse_name"));
						inputParam.setLogisticseCode(oneWhere.get("logisticse_code"));
						inputParam.setWaybill(oneWhere.get("waybill"));
					}
					new ApiOrderStatusChangeNotic().Process(inputParam, new MDataMap());
				}
			}
		}else{
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140003"), "order_status_ext", "order_code");
		}
		
		// 订单退货需要退还使用的积分
		try {
			new PlusServiceAccm().addExecInfoForReturn(order_code, return_code, "订单退货");
		} catch (Exception e) {
			e.printStackTrace();
			WebHelper.errorMessage(return_code, "FlowForSureGetGoods", 1,"returnForAccmAmt on FlowForSureGetGoods", "", e);
		}
		// 订单退货需要退还使用的惠币
		try {
			new HjycoinService().addExecInfoForReturn(order_code, goods.getReturn_code(), "订单退货");
		} catch (Exception e) {
			e.printStackTrace();
			WebHelper.errorMessage(goods.getReturn_code(), "ChangeReturnGoodsStatus", 1,"returnForHjycoin on ChangeReturnGoodsStatus", "", e);
		}
		// 订单退货需要退还使用的储值金
		try {
			new TeslaPpcService().addExecInfoForReturn(order_code, return_code, "订单退货");
		} catch (Exception e) {
			e.printStackTrace();
			WebHelper.errorMessage(return_code, "FlowForSureGetGoods", 1,"returnForPpcAmt on FlowForSureGetGoods", "", e);
		}
		// 订单退货需要退还使用的暂存款
		try {
			new TeslaCrdtService().addExecInfoForReturn(order_code, return_code, "订单退货");
		} catch (Exception e) {
			e.printStackTrace();
			WebHelper.errorMessage(return_code, "FlowForSureGetGoods", 1,"returnForCrdtAmt on FlowForSureGetGoods", "", e);
		}
		
		if("4497153900050001".equals(toStatus)) {
			// 如果是退货,扣减惠惠农场用户水滴
			try {
				// 查询该订单是否赠送雨滴
				MDataMap farmOrder = DbUp.upTable("sc_huodong_farm_order").one("order_code",order_code,"is_cancel","0","is_give_water","1");
				if(farmOrder != null) {
					// 如果赠送过雨滴,则添加扣减雨滴和进度定时
					JobExecHelper.createExecInfo("449746990046", order_code, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(order_code, "cancelOrderReduceFarmWater", 9,"cancelOrderReduceFarmWater on FlowForSureGetGoods", "", e);
			}
		}
		
		//消息推送表数据插入NG++ 20190620
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String message = "您的申请已通过，我们正在为您办理退款，预计1-5个工作日，退还到您的支付账号。";
		MDataMap saleMap = DbUp.upTable("oc_order_after_sale").one("asale_code",return_code);
		DbUp.upTable("nc_aftersale_push_news").dataInsert(new MDataMap("uid",uuid,"member_code",saleMap.get("buyer_code"),"title",title,"message",message,"create_time",DateUtil.getSysDateTimeString(),"push_times","0","checker",create_user,"after_sale_code",return_code,"after_sale_status","4497477800050006","to_page","13","if_read","0"));
		return result;
	}
	
	
	public ReturnMoneyResult creatReturnMoney(ReturnGoods goods) {
		if(DbUp.upTable("oc_return_money").count("return_goods_code", goods.getReturn_code(),"order_code",goods.getOrder_code()) > 0) {
			return new ReturnMoneyResult();
		}
		
		ReturnMoneyResult result = new ReturnMoneyResult();
		// 积分退款金额，退款金额中减去积分的金额才是真正给用户退款的钱
		BigDecimal expected_return_accm_money=goods.getExpected_return_accm_money();
		BigDecimal expected_return_group_money=goods.getExpected_return_group_money();
		BigDecimal expected_return_money=goods.getExpected_return_money();
		BigDecimal expected_return_ppc_money = goods.getExpected_return_ppc_money();
		BigDecimal expected_return_crdt_money = goods.getExpected_return_crdt_money();
		BigDecimal expected_return_hjycoin_money = goods.getExpected_return_hjycoin_money();
		
		// 解决退还积分金额超过商品金额时退款单金额是负数的问题
		//添加扣减储值金、暂存款  、惠币  20180531  -rhb
		BigDecimal realReturnMoney=expected_return_money.subtract(expected_return_accm_money).subtract(expected_return_ppc_money).subtract(expected_return_crdt_money).subtract(expected_return_hjycoin_money);
		if(realReturnMoney.compareTo(BigDecimal.ZERO) < 0){
			realReturnMoney = BigDecimal.ZERO;
		}
		String money_no = WebHelper.upCode("RTM");
		if(expected_return_money.compareTo(BigDecimal.ZERO)>0){
			MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_type", "zid desc","order_code=:order_code", "order_code", goods.getOrder_code());
			
			//生成退款单
			MDataMap map = new MDataMap();
			map.put("return_money_code", money_no);
			map.put("return_goods_code", goods.getReturn_code());
			map.put("buyer_code", goods.getBuyer_code());
			map.put("seller_code", goods.getSeller_code());
			map.put("small_seller_code", goods.getSmall_seller_code());
			map.put("contacts", "");//联系人
			map.put("status", "4497153900040003");
			map.put("return_money","449746280016".equals(payMap.get("pay_type")) ? "0" : realReturnMoney.toString());
			map.put("mobile", goods.getBuyer_mobile());
			map.put("create_time", DateUtil.getNowTime());
			map.put("poundage", "0");
			map.put("order_code", goods.getOrder_code());
			map.put("pay_method", "449716200001");
			map.put("online_money", "449746280016".equals(payMap.get("pay_type")) ? "0" : realReturnMoney.toString());
			map.put("returned_money", String.valueOf(new ReturnMoneyService().returnMoney(goods.getOrder_code())));//已退款金额
			map.put("return_accm_money", goods.getExpected_return_accm_money().toString());//积分退款金额
			map.put("return_hjycoin_money", goods.getExpected_return_hjycoin_money().toString());//惠币退款金额
			map.put("return_ppc_money", goods.getExpected_return_ppc_money().toString());//储值金退款金额
			map.put("return_crdt_money", goods.getExpected_return_crdt_money().toString());//暂存款退款金额
			String orderCode = goods.getOrder_code();
			String outOrderCode = "";
			try{
				MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				outOrderCode = orderInfo.get("out_order_code");
			}catch(Exception e){
				e.getStackTrace();
			}
			map.put("out_order_code", outOrderCode);
			DbUp.upTable("oc_return_money").dataInsert(map);
			
			// 创建流水日志
			MDataMap logMap = new MDataMap();
			logMap.put("return_money_no", money_no);
			logMap.put("info", "用户退货，创建退款单");
			logMap.put("create_time", DateUtil.getNowTime());
			String create_user="";
			try {
				create_user=UserFactory.INSTANCE.create().getLoginName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logMap.put("create_user", create_user);
			logMap.put("status", map.get("status"));
			DbUp.upTable("lc_return_money_status").dataInsert(logMap);
			
			
			MMessage messages = new MMessage();
			messages.setMessageContent(bConfig("groupcenter.flow_sureGetGoods_msm1"));
			messages.setMessageReceive(goods.getBuyer_mobile());
			messages.setSendSource("4497467200020006");
			// 退货确认时不再发送短信，只在退款确认时发送一次
			//MessageSupport.INSTANCE.sendMessage(messages);
			
			result.setReturnMoneyCode(money_no);
		}else{
			// 0元单退货时退还使用的惠豆
			HjybeanService.addHjyBeanTimer(HjyBeanExecType.RETURN_GOODS, goods.getReturn_code(),goods.getOrder_code());
		}
		
		//自动退还微公社余额
		if(expected_return_group_money.compareTo(BigDecimal.ZERO)>0){
			
			//退返微公社部分
			GroupRefundInput groupRefundInput = new GroupRefundInput();
//			groupRefundInput.setTradeCode(money_no);
			groupRefundInput.setTradeCode(DbUp.upTable("oc_order_pay").one("order_code",goods.getOrder_code(),"pay_type","449746280009").get("pay_sequenceid"));
			groupRefundInput.setMemberCode(goods.getBuyer_code());
			groupRefundInput.setRefundMoney(expected_return_group_money.toString());
			groupRefundInput.setOrderCode(goods.getOrder_code());
			groupRefundInput.setRefundTime(DateUtil.getSysDateTimeString());
			groupRefundInput.setRemark("退货自动退还微公社余额");
			groupRefundInput.setBusinessTradeCode(money_no);//一个流水值退一次
//			new GroupPayService().groupRefundSome(groupRefundInput, goods.getSeller_code());
			
			ApiCallSupport<GroupRefundInput, GroupRefundResult> apiCallSupport=new ApiCallSupport<GroupRefundInput, GroupRefundResult>();
//			GroupRefundResult refundResult = null;
			try {
				apiCallSupport.doCallApi(
						bConfig("xmassystem.group_pay_url"),
						bConfig("xmassystem.group_pay_refund_face"),
						bConfig("xmassystem.group_pay_key"),
						bConfig("xmassystem.group_pay_pass"), groupRefundInput,
						new GroupRefundResult());
				
				MMessage messages = new MMessage();
				messages.setMessageContent(bConfig("groupcenter.flow_sureGetGoods_msm"));
				messages.setMessageReceive(goods.getBuyer_mobile());
				messages.setSendSource("4497467200020006");
				MessageSupport.INSTANCE.sendMessage(messages);
				
			} catch (Exception e) {
				//此处暂时流程，退款失败，不影响总流程
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
}
