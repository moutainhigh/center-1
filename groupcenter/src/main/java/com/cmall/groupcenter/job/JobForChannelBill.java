package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobExecutionContext;

import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobForChannelBill extends RootJob {

	/**
	 * order_status : 4497153900010005（交易成功）
	 * 
	 */
	@Override
	public void doExecute(JobExecutionContext context) {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Timestamp lastTime = DateUtil.addMonths(time, -1);
		Timestamp first = DateUtil.getFirstDayOfMonth(lastTime);
		Timestamp last = DateUtil.getLastDayOfMonth(lastTime);
		String firstDay = first.toString().substring(0, 19);
		String lastDay = last.toString().substring(0, 11) + "23:59:59";
		String bill_month = last.toString().substring(0, 7);
		// 获取上一月起始结束时间

		String sqlAmt = "SELECT SUM(b.order_money) receive_amt,a.channel_seller_code channel_seller_code FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_orderinfo b ON a.order_code = b.order_code WHERE b.order_status = '4497153900010005' AND b.update_time >= '"
				+ firstDay + "' AND b.update_time <= '" + lastDay + "' group by a.channel_seller_code ";
		
		String sqlReceiveNum = "SELECT SUM(b.sku_num) receive_num,a.channel_seller_code channel_seller_code FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_orderdetail b ON a.order_code = b.order_code"
				+ " LEFT JOIN ordercenter.oc_orderinfo c ON b.order_code = c.order_code WHERE c.order_status = '4497153900010005' AND c.update_time >= '"
				+ firstDay + "' AND c.update_time <= '" + lastDay + "' group by a.channel_seller_code ";
		
		String sqlReturnAmt = "SELECT SUM(c.expected_return_money) return_amt , a.channel_seller_code channel_seller_code FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_order_after_sale b ON a.order_code = b.order_code "
				+ "LEFT JOIN ordercenter.oc_return_goods c ON b.asale_code = c.return_code WHERE c.status='4497153900050001' AND b.update_time >= '"
				+ firstDay + "' AND b.update_time <= '" + lastDay + "' group by a.channel_seller_code ";
		
		String sqlReturnNum = "SELECT SUM(d.count) sku_num,a.channel_seller_code FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_order_after_sale b ON a.order_code = b.order_code "
				+ "LEFT JOIN ordercenter.oc_return_goods c ON b.asale_code = c.return_code LEFT JOIN ordercenter.oc_return_goods_detail d ON d.return_code = c.return_code WHERE c.status='4497153900050001' AND b.update_time >= '"
				+ firstDay + "' AND b.update_time <= '" + lastDay + "' group by a.channel_seller_code ";
		List<Map<String,Object>> channelAmt = DbUp.upTable("oc_order_channel").dataSqlList(sqlAmt, null);
		List<Map<String,Object>> channelReceiveNum = DbUp.upTable("oc_order_channel").dataSqlList(sqlReceiveNum, null);
		List<Map<String,Object>> channelReturnAmt = DbUp.upTable("oc_order_channel").dataSqlList(sqlReturnAmt, null);
		List<Map<String,Object>> channelReturnNum = DbUp.upTable("oc_order_channel").dataSqlList(sqlReturnNum, null);
		String channelSql = "SELECT * FROM usercenter.uc_channel_sellerinfo";
		List<Map<String,Object>> channelSellers = DbUp.upTable("uc_channel_sellerinfo").dataSqlList(channelSql, null);
		for(Map<String,Object> map : channelSellers) {
			String channel_seller_code = map.get("channel_seller_code").toString();
			String channel_seller_name = map.get("channel_seller_name").toString();
			String sku_receive_num = "0";
			String sku_return_num = "0";
			String sku_receive_amt = "0";
			String sku_return_amt = "0";
			String bill_code = (channel_seller_code + bill_month).replace("-", "");
			MDataMap insert = new MDataMap();
			insert.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
			insert.put("bill_code", bill_code);
			insert.put("bill_month", bill_month);
			insert.put("channel_seller_code", channel_seller_code);
			insert.put("channel_seller_name", channel_seller_name);
			for(Map<String,Object> renmap : channelReceiveNum) {
				String re_channel_seller_code = renmap.get("channel_seller_code").toString();
				if(re_channel_seller_code.equals(channel_seller_code)) {
					sku_receive_num = renmap.get("receive_num") != null ?renmap.get("receive_num").toString():"0";
					insert.put("sku_receive_num", sku_receive_num);
				}
			}
			for(Map<String,Object> retnmap : channelReturnNum) {
				String ret_channel_seller_code = retnmap.get("channel_seller_code").toString();
				if(ret_channel_seller_code.equals(channel_seller_code)) {
					sku_return_num = retnmap.get("sku_num")!=null?retnmap.get("sku_num").toString():"0";
					insert.put("sku_return_num", sku_return_num);
				}
			}
			for(Map<String,Object> amtmap : channelAmt) {
				String amt_channel_seller_code = amtmap.get("channel_seller_code").toString();
				if(amt_channel_seller_code.equals(channel_seller_code)) {
					sku_receive_amt = amtmap.get("receive_amt")!=null?amtmap.get("receive_amt").toString():"0";
					insert.put("sku_receive_amt", sku_receive_amt);
				}
			}
			for(Map<String,Object> reamtmap : channelReturnAmt) {
				String reamt_channel_seller_code = reamtmap.get("channel_seller_code").toString();
				if(reamt_channel_seller_code.equals(channel_seller_code)) {
					sku_return_amt = reamtmap.get("return_amt") != null?reamtmap.get("return_amt").toString():"0";
					insert.put("sku_return_amt", sku_return_amt);
				}
			}
			Integer sku_total_num = Integer.parseInt(sku_receive_num) - Integer.parseInt(sku_return_num);
			BigDecimal bill_amt = new BigDecimal(sku_receive_amt).subtract(new BigDecimal(sku_return_amt));
			insert.put("sku_total_num", sku_total_num.toString());
			insert.put("bill_amt", bill_amt.toString());
			insert.put("bill_period_code", "gjht");
			insert.put("bill_period_desc", "根据合同");
			Integer count = DbUp.upTable("oc_bill_channel_seller_period").count("bill_code",bill_code);
			if(count > 0) {
				insert.put("update_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_bill_channel_seller_period").dataUpdate(insert, "sku_receive_num,sku_return_num,sku_receive_amt,sku_return_amt,update_time", "bill_code=:bill_code");
			}else {
				if(!"0".equals(sku_receive_num)||!"0".equals(sku_return_num)) {
					insert.put("create_time", DateUtil.getSysDateTimeString());
					DbUp.upTable("oc_bill_channel_seller_period").dataInsert(insert);
				}
			}
		}

	}

}
