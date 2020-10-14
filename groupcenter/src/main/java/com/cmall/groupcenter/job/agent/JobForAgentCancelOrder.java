package com.cmall.groupcenter.job.agent;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 分销取消订单
 * type:449746990029
 * @author 周恩至
 *
 */
public class JobForAgentCancelOrder extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String orderCode) {
		
		MWebResult mWebResult = new MWebResult();
		
		MDataMap agentOrderInfo = DbUp.upTable("fh_agent_order_detail").one("order_code",orderCode);
		if(agentOrderInfo == null || agentOrderInfo.isEmpty()) {
			mWebResult.setResultCode(0);
			return mWebResult;
		}
		String agent_code = agentOrderInfo.get("agent_code");
		String agent_parent_code = agentOrderInfo.get("agent_parent_code");
		//计算分销收益
		BigDecimal profit_money_dec = new BigDecimal(agentOrderInfo.get("sku_num")).multiply(new BigDecimal(agentOrderInfo.get("profit_money")).multiply(new BigDecimal(agentOrderInfo.get("agent_profit_rate")))).setScale(1, BigDecimal.ROUND_HALF_UP);
		//计算分销人父级收益
		BigDecimal agent_parent_rate_dec = new BigDecimal(agentOrderInfo.get("sku_num")).multiply(new BigDecimal(agentOrderInfo.get("profit_money")).multiply(new BigDecimal(agentOrderInfo.get("agent_parent_rate")))).setScale(1, BigDecimal.ROUND_HALF_UP);
		this.caculate(agent_code, profit_money_dec,orderCode,1);
		this.caculate(agent_parent_code, agent_parent_rate_dec,orderCode,2);
		return mWebResult;
	}
	
	/**
	 * 变更预估收益
	 * @param memberCode
	 * @param rate
	 * @param type
	 *            1: 分销收益，2：粉丝收益
	 */
	private void caculate(String memberCode,BigDecimal rate,String order_code,int type) {
		if(StringUtils.isEmpty("memberCode")) {
			return;
		}
		MDataMap agentMember = DbUp.upTable("fh_agent_member_info").one("member_code",memberCode);
		if(agentMember == null || agentMember.isEmpty()) {
			return;
		}
		String sql = "UPDATE familyhas.fh_agent_member_info SET predict_money = predict_money-"+rate+" WHERE member_code = '"+memberCode+"'";
		DbUp.upTable("fh_agent_member_info").dataExec(sql, null);
		//预估收益减少
		MDataMap profitDetial = new MDataMap();
		profitDetial.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
		profitDetial.put("member_code", memberCode);
		profitDetial.put("order_code", order_code);
		profitDetial.put("profit_type", "4497484600030001");// 预估收益
		if (type == 1) {
			profitDetial.put("profit_source", "4497484600040001");
			profitDetial.put("remark", "分销下单收益");
		} else if (type == 2) {
			profitDetial.put("profit_source", "4497484600040002");
			profitDetial.put("remark", "粉丝分销下单收益");
		}
		profitDetial.put("profit", rate.toString());
		profitDetial.put("money_type", "4497484600070002");// 预估收益减少
		profitDetial.put("profit", "-"+rate.toString());
		profitDetial.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("fh_agent_profit_detail").dataInsert(profitDetial);
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990029");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
