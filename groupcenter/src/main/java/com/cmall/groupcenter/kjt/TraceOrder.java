package com.cmall.groupcenter.kjt;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigTraceOrder;
import com.cmall.groupcenter.kjt.request.RsyncRequestTraceOrder;
import com.cmall.groupcenter.kjt.response.RsyncResponseTraceOrder;
import com.cmall.groupcenter.kjt.response.RsyncResponseTraceOrder.SoOrder;
import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.service.OrderForKJT;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmasorder.model.TeslaModelGroupPayResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.websupport.ApiCallSupport;

/**
 * 同步订单状态
 * @author jlin
 *
 */
public class TraceOrder extends RsyncKjt<RsyncConfigTraceOrder, RsyncRequestTraceOrder, RsyncResponseTraceOrder>{

	private final static RsyncConfigTraceOrder RSYNC_CONFIG_TRACE_ORDER = new RsyncConfigTraceOrder();
	private RsyncRequestTraceOrder rsyncRequestTraceOrder =new   RsyncRequestTraceOrder();
	
	
	@Override
	public RsyncConfigTraceOrder upConfig() {
		return RSYNC_CONFIG_TRACE_ORDER;
	}

	@Override
	public RsyncRequestTraceOrder upRsyncRequest() {
		return rsyncRequestTraceOrder;
	}
	
	@Override
	public RsyncResult doProcess(RsyncRequestTraceOrder tRequest,RsyncResponseTraceOrder tResponse) {
		
		RsyncResult rsyncResult = new RsyncResult();
		
		if(!"0".equals(tResponse.getCode())) {
			rsyncResult.setResultCode(918519135);
			rsyncResult.setResultMessage(tResponse.getDesc());
			return rsyncResult;
		}
		
		List<SoOrder> list = tResponse.getData().getTraceOrderList();
		
		if(list!=null&&list.size()>0){
			for (SoOrder soOrder : list) {
				int soid=soOrder.getSOID();
				int sostatus=soOrder.getSOStatus();
				
				MDataMap dataMap=DbUp.upTable("oc_order_kjt_list").oneWhere("order_code_seq, order_code, order_code_out,sostatus", "", "order_code_out=:order_code_out", "order_code_out",String.valueOf(soid));
				String now=DateUtil.getSysDateTimeString();
				String order_code=dataMap.get("order_code");
				String order_code_seq=dataMap.get("order_code_seq");
				
				if(StringUtils.isBlank(dataMap.get("sostatus"))||Integer.valueOf(dataMap.get("sostatus"))!=sostatus){
					DbUp.upTable("oc_order_kjt_list").dataUpdate(new MDataMap("sostatus",String.valueOf(sostatus),"local_status",OrderForKJT.orderStatusMapper(sostatus),"order_code_out",String.valueOf(soid),"update_time",now), "sostatus,update_time,local_status", "order_code_out");
					
					if(sostatus==65||sostatus==42){
						
						MDataMap addressInfo=DbUp.upTable("oc_orderadress").oneWhere("auth_idcard_number", "", "order_code=:order_code", "order_code",order_code);
						
						String auth_idcard_number=addressInfo.get("auth_idcard_number");
						
						if(StringUtils.isNotBlank(auth_idcard_number)){
							String buyer_code=DbUp.upTable("oc_orderinfo").oneWhere("buyer_code", "", "order_code=:order_code", "order_code",order_code).get("buyer_code");
							DbUp.upTable("mc_authenticationInfo").dataUpdate(new MDataMap("member_code",buyer_code,"idcard_number",auth_idcard_number,"customs_status",sostatus==65?"1":"0"), "customs_status", "member_code,idcard_number");
							
						}
					} 
				}
				
				//更新本地订单状态
				
//				全部对应交易成功	交易成功
//				部分对应已发货	已发货
//				全部对应下单成功-未发货	下单成功-未发货
//				全部对应交易失败	交易失败
				
//				4497153900010002	下单成功-未发货
//				4497153900010003	已发货
//				4497153900010005	交易成功
//				4497153900010006	交易失败
				
				List<Map<String, Object>> klist=DbUp.upTable("oc_order_kjt_list").dataSqlList("SELECT local_status from oc_order_kjt_list where order_code=:order_code GROUP BY local_status", new MDataMap("order_code",dataMap.get("order_code")));
				String status="";
				if(klist.size()==1){
					status=(String)klist.get(0).get("local_status");
				}else{
					//此处映射状态不全，暂时不考虑其他
					status="4497153900010003";
				}
				
				MDataMap orderMap= DbUp.upTable("oc_orderinfo").oneWhere("order_code,order_status", "", "order_code=:order_code", "order_code",dataMap.get("order_code"));
				if(StringUtils.isNotBlank(status)&&!status.equals(orderMap.get("order_status"))&&!"4497153900010005".equals(orderMap.get("order_status"))){
					DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",status,"update_time",DateUtil.getSysDateTimeString(),"order_code",dataMap.get("order_code")), "order_status,update_time", "order_code");
					DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",dataMap.get("order_code"),"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",orderMap.get("order_status"),"now_status",status,"info","TraceOrder"));
				}
				
				
				if(sostatus==-4||sostatus==-1||sostatus==6||sostatus==65){
					creatReturnMoney(order_code_seq, order_code);
				}
				
				//判断发个邮件通知一下
				if(sostatus==-4||sostatus==-1||sostatus==6||sostatus==65||sostatus==7){
					sendMail(dataMap.get("order_code_seq"), dataMap.get("order_code"),dataMap.get("order_code_out"), sostatus);
				}
			}
		}
		
		return rsyncResult;
	}
	
	@Override
	public RsyncResponseTraceOrder upResponseObject() {
		return new RsyncResponseTraceOrder();
	}
	
	
	private void sendMail(String order_code_seq,String order_code,String order_code_out,int sostatus){
		
		String receives[]= bConfig("groupcenter.kjt_orderstata_sendMail_receives").split(",");
		String title= bConfig("groupcenter.kjt_orderstata_sendMail_title");
		String content= bConfig("groupcenter.kjt_orderstata_sendMail_content");
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, FormatHelper.formatString(title,order_code,order_code_seq,getStatusDesc(sostatus),order_code_out), FormatHelper.formatString(content,order_code,order_code_seq,getStatusDesc(sostatus),order_code_out));
			}
		}
	}
	
	private static Map<Integer, String> statusMap = null;
	
	private synchronized static String getStatusDesc(int sostatus){
		if(statusMap==null){
			statusMap=new HashMap<Integer, String>();
			statusMap.put(-4, "系统作废");
			statusMap.put(-1, "作废");
			statusMap.put(0, "待审核");
			statusMap.put(1, "待出库");
			statusMap.put(4, "已出库待申报");
			statusMap.put(41, "已申报待通关");
			statusMap.put(45, "已通过发往顾客");
			statusMap.put(5, "订单完成");
			statusMap.put(6, "申报失败订单作废");
			statusMap.put(65, "通关失败订单作废");
			statusMap.put(7, "订单拒收	");
		}
		return statusMap.get(sostatus);
	}
	
	//此处不牵扯运费的问题
	private void creatReturnMoney(String order_code_seq,String order_code) {
		
		BigDecimal expected_return_group_money=BigDecimal.ZERO;
		BigDecimal expected_return_money=BigDecimal.ZERO;
		
		List<MDataMap> failDetails=DbUp.upTable("oc_order_kjt_detail").queryAll("", "", "order_code_seq=:order_code_seq", new MDataMap("order_code_seq",order_code_seq));
		for (MDataMap mDataMap : failDetails) {
			String sku_code=mDataMap.get("sku_code");
			BigDecimal sku_num=new BigDecimal(mDataMap.get("sku_num"));
			MDataMap detailInfo=DbUp.upTable("oc_orderdetail").one("order_code",order_code,"sku_code",sku_code);
			BigDecimal sku_price = new BigDecimal(detailInfo.get("sku_price"));
			BigDecimal group_price = new BigDecimal(detailInfo.get("group_price"));
			
			expected_return_money=expected_return_money.add(sku_num.multiply(sku_price));
			expected_return_group_money=expected_return_group_money.add(sku_num.multiply(group_price));
		}
		
		MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
		
		
		String money_no = WebHelper.upCode("RTM");
		if(expected_return_money.compareTo(BigDecimal.ZERO)>0){
			//生成退款单
			MDataMap map = new MDataMap();
			map.put("return_money_code", money_no);
			map.put("return_goods_code", "");
			map.put("buyer_code", orderInfo.get("buyer_code"));
			map.put("seller_code", orderInfo.get("seller_code"));
			map.put("small_seller_code", orderInfo.get("small_seller_code"));
			map.put("contacts", "");//联系人
			map.put("status", "4497153900040003");
			map.put("return_money",expected_return_money.toString());
			map.put("mobile", (String)DbUp.upTable("mc_login_info").dataGet("login_name", "member_code=:member_code", new MDataMap("member_code",orderInfo.get("buyer_code"))));
			map.put("create_time", DateUtil.getSysDateTimeString());
			map.put("poundage", "0");
			map.put("order_code", order_code);
			map.put("pay_method", "449716200001");
			map.put("online_money", expected_return_money.toString());
			map.put("out_order_code", orderInfo.get("out_order_code")!=null?orderInfo.get("out_order_code"):"");
			DbUp.upTable("oc_return_money").dataInsert(map);
			
			// 创建流水日志
			MDataMap logMap = new MDataMap();
			logMap.put("return_money_no", money_no);
			logMap.put("info", "跨境通订单失败，直接生成退款单");
			logMap.put("create_time", DateUtil.getSysDateTimeString());
			String create_user="";
			try {
				create_user=UserFactory.INSTANCE.create().getLoginName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logMap.put("create_user", create_user);
			logMap.put("status", map.get("status"));
			DbUp.upTable("lc_return_money_status").dataInsert(logMap);
		}
		
		//自动退还微公社余额
		if(expected_return_group_money.compareTo(BigDecimal.ZERO)>0){
			
			//退返微公社部分
			GroupRefundInput groupRefundInput = new GroupRefundInput();
//			groupRefundInput.setTradeCode(money_no);
			groupRefundInput.setTradeCode(DbUp.upTable("oc_order_pay").one("order_code",order_code,"pay_type","449746280009").get("pay_sequenceid"));
			groupRefundInput.setMemberCode(orderInfo.get("buyer_code"));
			groupRefundInput.setRefundMoney(expected_return_group_money.toString());
			groupRefundInput.setOrderCode(order_code);
			groupRefundInput.setRefundTime(DateUtil.getSysDateTimeString());
			groupRefundInput.setRemark("跨境通自动退还微公社余额");
			groupRefundInput.setBusinessTradeCode(money_no);//一个流水值退一次
//			new GroupPayService().groupRefundSome(groupRefundInput, orderInfo.get("seller_code"));
			
			ApiCallSupport<GroupRefundInput, GroupRefundResult> apiCallSupport=new ApiCallSupport<GroupRefundInput, GroupRefundResult>();
			GroupRefundResult refundResult = null;
			try {
				refundResult=apiCallSupport.doCallApi(
						bConfig("xmassystem.group_pay_url"),
						bConfig("xmassystem.group_pay_refund_face"),
						bConfig("xmassystem.group_pay_key"),
						bConfig("xmassystem.group_pay_pass"), groupRefundInput,
						new GroupRefundResult());
			} catch (Exception e) {
				//此处暂时流程，退款失败，不影响总流程
				e.printStackTrace();
			}
			
			
		}
		
	}
}
