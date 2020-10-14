package com.cmall.ordercenter.tallyorder.settle;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.tallyorder.settle.strategy.CrossBorderDirectMailStrategy;
import com.cmall.ordercenter.tallyorder.settle.strategy.CrossBorderSellerStrategy;
import com.cmall.ordercenter.tallyorder.settle.strategy.PlatformSettleStrategy;
import com.cmall.ordercenter.tallyorder.settle.strategy.SettleStrategy;
import com.cmall.ordercenter.tallyorder.settle.strategy.StdSettleStrategy;
import com.cmall.ordercenter.tallyorder.settleperiod.SettlePeriodService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 结算3.0(生成月结和半月结结算单)
 * 4497477900030001 月结
 * 4497477900030002 半月结
 * @author zht
 *
 */
public class TaskGenFinancialStatement extends RootJob {
	
	@Override
	public void doExecute(JobExecutionContext context) {
		// 获取结算时间
		SettlePeriodService ops = new SettlePeriodService();
		List<Map<String, Object>> periodList = ops.getSettlePeriod();
		if (periodList != null && !periodList.isEmpty()) {
			for(Map<String, Object> settlePeriod : periodList) {
				Thread ts = new Thread(genSettleStrategy(settlePeriod));
				ts.start();
			}
		}
	}
	
	/**
	 * 根据settleType生成不同的结算类型
	 * 4497477900040001  常规结算
	 * 4497477900040002  跨境保税
	 * 4497477900040003  跨境直邮
	 * 4497477900040004  平台入驻
	 */
	private SettleStrategy genSettleStrategy(Map<String, Object> settlePeriod) {
		SettleStrategy strategy = null;
		String settleType = settlePeriod.get("settle_type").toString();

		switch(settleType) {
			case "4497477900040001":
				strategy = new StdSettleStrategy(settlePeriod);
				break;
			case "4497477900040002":
				strategy = new CrossBorderSellerStrategy(settlePeriod);
				break;
			case "4497477900040003":
				strategy = new CrossBorderDirectMailStrategy(settlePeriod);
				break;
			case "4497477900040004":
				strategy = new PlatformSettleStrategy(settlePeriod);
				break;
		}
		return strategy;
	}
	
	
	public static void main(String[] args) {
		TaskGenFinancialStatement tfs = new TaskGenFinancialStatement();
		tfs.doExecute(null);
	}
}
