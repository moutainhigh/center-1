package com.cmall.groupcenter.job.agent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.service.LdAfterSaleInfo;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 分销订单取消退货单
 * type:449746990026
 * @author 周恩至
 *
 */
public class JobForAgentCancelReturnOrder extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String returnCode) {
		MWebResult mWebResult = new MWebResult();
		MDataMap params = new MDataMap("return_code",returnCode);
		String sql = "SELECT * FROM ordercenter.oc_return_goods_detail WHERE return_code = :return_code";
		List<Map<String,Object>> returnDetailList = DbUp.upTable("oc_return_goods").dataSqlList(sql, params);
		if(returnDetailList != null && returnDetailList.size()>0) {//惠家有通路下的非TV品售后单
			mWebResult = this.operateUnTV(returnDetailList,returnCode);
		}else {//TV品下的售后单。
			LdAfterSaleInfo ldService = new LdAfterSaleInfo();
			Map<String,Object> returnInfoTV = ldService.getAsaleOrderInfo(returnCode);
			if(returnInfoTV == null) {
				mWebResult.setResultCode(0);
				mWebResult.setResultMessage("执行异常！！！");
			}else {
				mWebResult = this.operateTV(returnInfoTV,returnCode);
			}
		}
		return mWebResult;
		
	}
	
	/**
	 * TV品售后逻辑
	 * @param returnInfoListTV
	 * @return
	 */
	private MWebResult operateTV(Map<String, Object> returnInfoTV,String returnCode) {
		MWebResult result = new MWebResult();
		if(returnInfoTV == null) {
			result.setResultCode(0);
			result.setResultMessage("惠家有订单系统查询异常，根据售后单号查询售后单为空。");
			return result;
		}
		MDataMap mapInfo = new MDataMap(returnInfoTV);
		String ld_order_code = mapInfo.get("order_code");
		String product_code = mapInfo.get("product_code");
		String sku_code = mapInfo.get("sku_code");
		String num = mapInfo.get("good_cnt");//申请售后数量
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("out_order_code",ld_order_code);
		
		// 换货单用原单查询
		if(orderInfo != null && StringUtils.isNotBlank(orderInfo.get("org_ord_id"))) {
			orderInfo = DbUp.upTable("oc_orderinfo").one("out_order_code",orderInfo.get("org_ord_id"), "small_seller_code", "SI2003");
		}
		
		if(orderInfo == null || orderInfo.isEmpty()) {
			result.setResultCode(0);
			result.setResultMessage("售后单："+returnCode+"惠家有订单系统查询异常，找不到此单对应惠家有订单号。");
			return result;
		}
		String order_code = orderInfo.get("order_code");
		MDataMap agentOrder = DbUp.upTable("fh_agent_order_detail").one("order_code",order_code,"product_code",product_code,"sku_code",sku_code);
		if(agentOrder == null || agentOrder.isEmpty()) {
			result.setResultCode(0);
			result.setResultMessage("售后单："+returnCode+"惠家有订单系统查询异常，售后单对应的分销订单为空!!");
			return result;
		}
		BigDecimal predict_money_cut = new BigDecimal(agentOrder.get("agent_profit_rate")).multiply(new BigDecimal(agentOrder.get("profit_money"))).multiply(new BigDecimal(num));
		BigDecimal predict_money_cut_fans =new BigDecimal(agentOrder.get("agent_parent_rate")).multiply(new BigDecimal(agentOrder.get("profit_money"))).multiply(new BigDecimal(num));
		String member_code = agentOrder.get("agent_code");
		String member_code_parent = agentOrder.get("agent_parent_code");
		this.caculate(member_code, predict_money_cut, 1, order_code);
		this.caculate(member_code_parent, predict_money_cut_fans, 2, order_code);
		result.setResultCode(1);
		return result;
	}

	/**
	 * 非TV品售后逻辑
	 * @param returnInfoList
	 * @return
	 */
	private MWebResult operateUnTV(List<Map<String, Object>> returnDetailList,String returnCode) {
		MWebResult result = new MWebResult();
		for(Map<String,Object> map : returnDetailList) {
			if(map == null) {
				result.setResultCode(0);
				result.setResultMessage("售后单:"+returnCode+",查询为空，请确认定时写入售后单号正确");
				return result;
			}
			MDataMap returnDetail = new MDataMap(map);
			MDataMap returnInfo = DbUp.upTable("oc_return_goods").one("return_code",returnDetail.get("return_code"));
			String order_code = returnInfo.get("order_code");
			String num = returnDetail.get("count");
			String sku_code = returnDetail.get("sku_code");
			MDataMap agentOrder = DbUp.upTable("fh_agent_order_detail").one("order_code",order_code,"sku_code",sku_code);
			if(agentOrder == null || agentOrder.isEmpty()) {
				result.setResultCode(0);
				result.setResultMessage("售后单："+returnCode+"惠家有订单系统查询异常，售后单对应的分销订单为空!!");
				return result;
			}
			BigDecimal predict_money_cut = new BigDecimal(agentOrder.get("agent_profit_rate")).multiply(new BigDecimal(agentOrder.get("profit_money"))).multiply(new BigDecimal(num));
			BigDecimal predict_money_cut_fans =new BigDecimal(agentOrder.get("agent_parent_rate")).multiply(new BigDecimal(agentOrder.get("profit_money"))).multiply(new BigDecimal(num));
			String member_code = agentOrder.get("agent_code");
			String member_code_parent = agentOrder.get("agent_parent_code");
			this.caculate(member_code, predict_money_cut, 1, order_code);
			this.caculate(member_code_parent, predict_money_cut_fans, 2, order_code);
		}
		result.setResultCode(1);
		return result;
	}

	/**
	 * 变更预估收益
	 * @param memberCode
	 * @param rate
	 * @param type 1: 分销收益，2：粉丝收益
	 */
	private void caculate(String memberCode,BigDecimal rate,int type,String orderCode) {
		if(StringUtils.isEmpty(memberCode)) {
			return;
		}
		MDataMap agentMember = DbUp.upTable("fh_agent_member_info").one("member_code", memberCode);
		if (agentMember == null || agentMember.isEmpty()) {
			return;
		}
		String sql = "UPDATE familyhas.fh_agent_member_info SET freeze_money = freeze_money -" + rate
				+ ",real_money = real_money+" + rate + " WHERE member_code = '" + memberCode + "'";
		DbUp.upTable("fh_agent_member_info").dataExec(sql, null);
		MDataMap profitDetial = new MDataMap();
		profitDetial.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
		profitDetial.put("member_code", memberCode);
		profitDetial.put("order_code", orderCode);
		profitDetial.put("profit_type", "4497484600030003");// 冻结收益
		if (type == 1) {
			profitDetial.put("profit_source", "4497484600040001");
			profitDetial.put("remark", "分销下单收益");
		} else if (type == 2) {
			profitDetial.put("profit_source", "4497484600040002");
			profitDetial.put("remark", "粉丝分销下单收益");
		}
		profitDetial.put("profit", "-"+rate.toString());
		profitDetial.put("money_type", "4497484600070002");// 冻结收益减少
		profitDetial.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("fh_agent_profit_detail").dataInsert(profitDetial);
		profitDetial.put("profit_type", "4497484600030002");// 可提现收益
		profitDetial.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
		profitDetial.put("money_type", "4497484600070001");// 提现收益增加
		profitDetial.put("profit", rate.toString());
		profitDetial.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("fh_agent_profit_detail").dataInsert(profitDetial);
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990026");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
