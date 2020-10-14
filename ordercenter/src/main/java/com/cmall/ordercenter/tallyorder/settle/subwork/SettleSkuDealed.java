package com.cmall.ordercenter.tallyorder.settle.subwork;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.tallyorder.settle.strategy.SettleStrategy;
import com.srnpr.zapcom.basehelper.DateHelper;

/**
 * 生成某种结算类型下一批商户的交易成功SKU的临时结算数据
 * @author zht
 *
 */
public class SettleSkuDealed implements Runnable {
	/**
	 * 在某结算类型对应的结算周期内有订单成功交易的商户分拆列表
	 */
	private List<String> sellerList;
	
	/**
	 * 结算周期
	 */
	protected Map<String, Object> period;
	
	/**
	 * 结算类型对应的结算策略.目前供有四种
	 * 1.常规商户结算
	 * 2.跨境商户结算
	 * 3.跨境直邮商户结算
	 * 4.平台入驻商户结算
	 */
	protected SettleStrategy strategy;
	
	public SettleSkuDealed(List<String> sellerList, Map<String, Object> period, SettleStrategy strategy) {
		this.sellerList = sellerList;
		this.period = period;
		this.strategy = strategy;
	}
	
	@Override
	public void run() {
		try {
			for(String smallSellerCode : sellerList) {
				strategy.grabSkuDealed(smallSellerCode);
				Thread.sleep(10L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
