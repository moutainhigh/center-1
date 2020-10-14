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

public class JobForChannelBillDetail extends RootJob {

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

		String receiveAmtAndNum = "SELECT SUM(c.sku_num) receive_num,a.channel_seller_code channel_seller_code,c.sku_code sku_code,c.tax_rate fee ,c.sku_price sku_price FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_orderinfo b ON a.order_code = b.order_code "
				+ "LEFT JOIN ordercenter.oc_orderdetail c ON b.order_code = c.order_code WHERE b.order_status = '4497153900010005' AND c.gift_flag = 1 AND b.update_time >= '"
				+ firstDay + "' AND b.update_time <= '" + lastDay + "' group by a.channel_seller_code,c.sku_code,c.sku_price,c.tax_rate ";
		
		String returnAmtAndNum = "SELECT SUM(d.count) return_num,a.channel_seller_code,e.sku_code sku_code,e.sku_price sku_price,e.tax_rate fee FROM ordercenter.oc_order_channel a LEFT JOIN ordercenter.oc_order_after_sale b ON a.order_code = b.order_code "
				+ "LEFT JOIN ordercenter.oc_return_goods c ON b.asale_code = c.return_code LEFT JOIN ordercenter.oc_return_goods_detail d ON d.return_code = c.return_code "
				+ "LEFT JOIN ordercenter.oc_orderdetail e ON d.sku_code = e.sku_code and e.order_code = b.order_code WHERE c.status='4497153900050001' AND b.update_time >= '"
				+ firstDay + "' AND b.update_time <= '" + lastDay + "' group by a.channel_seller_code, e.sku_code,e.sku_price,e.tax_rate ";
		List<Map<String,Object>> channelReceiveAmtAndNum = DbUp.upTable("oc_order_channel").dataSqlList(receiveAmtAndNum, null);
		List<Map<String,Object>> channelReturnAmtAndNum = DbUp.upTable("oc_order_channel").dataSqlList(returnAmtAndNum, null);
		for(Map<String,Object> receive : channelReceiveAmtAndNum) {
			String channel_seller_code =  receive.get("channel_seller_code") != null?receive.get("channel_seller_code").toString():"";
			String sku_code = receive.get("sku_code")!=null?receive.get("sku_code").toString():"";
			String fee = receive.get("fee")!=null?receive.get("fee").toString():"";
			String receive_num = receive.get("receive_num")!=null?receive.get("receive_num").toString():"";
			String sku_price = receive.get("sku_price")!=null?receive.get("sku_price").toString():"";
			MDataMap channelInfo = DbUp.upTable("uc_channel_sellerinfo").one("channel_seller_code",channel_seller_code);
			MDataMap skuinfo = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
			String productCode = skuinfo.get("product_code");
			MDataMap productInfo = DbUp.upTable("pc_productinfo").one("product_code",productCode);
			String small_seller_code = productInfo.get("small_seller_code");
			MDataMap sellerinfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code",small_seller_code);
			String bill_code = (channel_seller_code + bill_month).replace("-", "");
			MDataMap insert =  DbUp.upTable("oc_bill_channel_seller_period_detail").one("bill_code",bill_code,"sku_code",sku_code,"sku_price",sku_price,"fee",fee);
			boolean flag = true;
			if(insert == null || insert.isEmpty()) {
				flag = false;
				insert = new MDataMap();
				insert.put("uid", UUID.randomUUID().toString().replace("-", ""));
			}
			insert.put("bill_code", bill_code);
			insert.put("bill_month", bill_month);
			insert.put("channel_seller_code", channel_seller_code);
			insert.put("channel_seller_name", channelInfo != null?channelInfo.get("channel_seller_name"):"");
			BigDecimal totalAmt = new BigDecimal(sku_price).multiply(new BigDecimal(receive_num));
			insert.put("receive_num", receive_num);
			insert.put("receive_amt", totalAmt.toString());
			insert.put("total_num", receive_num);
			insert.put("total_amt", totalAmt.toString());
			insert.put("fee", fee);
			insert.put("sku_statement_type", "代销结算");
			insert.put("sku_code", sku_code);
			insert.put("product_code", skuinfo != null?skuinfo.get("product_code"):"");
			insert.put("product_name", productInfo != null?productInfo.get("product_name"):"");
			insert.put("sku_price", sku_price);
			insert.put("small_seller_code", small_seller_code);
			insert.put("small_seller_name", sellerinfo !=null?sellerinfo.get("seller_name"):"");
			insert.put("fee_class_code", productInfo.get("tax_code"));
			if(flag) {//存在，执行update
				insert.put("update_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_bill_channel_seller_period_detail").dataUpdate(insert, "receive_num,receive_amt,update_time", "uid");
			}else {
				insert.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_bill_channel_seller_period_detail").dataInsert(insert);
			}
		}
		
		for(Map<String,Object> returnMap : channelReturnAmtAndNum) {
			String channel_seller_code =  returnMap.get("channel_seller_code") != null?returnMap.get("channel_seller_code").toString():"";
			String sku_code = returnMap.get("sku_code")!=null?returnMap.get("sku_code").toString():"";
			String fee = returnMap.get("fee")!=null?returnMap.get("fee").toString():"";
			String return_num = returnMap.get("return_num")!=null?returnMap.get("return_num").toString():"";
			String sku_price = returnMap.get("sku_price")!=null?returnMap.get("sku_price").toString():"";
			MDataMap channelInfo = DbUp.upTable("uc_channel_sellerinfo").one("channel_seller_code",channel_seller_code);
			MDataMap skuinfo = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
			String productCode = skuinfo.get("product_code");
			MDataMap productInfo = DbUp.upTable("pc_productinfo").one("product_code",productCode);
			String small_seller_code = productInfo.get("small_seller_code");
			MDataMap sellerinfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code",small_seller_code);
			BigDecimal returnTotalAmt = new BigDecimal(sku_price).multiply(new BigDecimal(return_num));
			String bill_code = (channel_seller_code + bill_month).replace("-", "");
			MDataMap insert =  DbUp.upTable("oc_bill_channel_seller_period_detail").one("bill_code",bill_code,"sku_code",sku_code,"sku_price",sku_price,"fee",fee);
			boolean flag = true;
			Integer receive_num = 0;
			BigDecimal receive_amt = new BigDecimal("0.00");
			if(insert == null || insert.isEmpty()) {
				flag = false;
				insert = new MDataMap();
				insert.put("uid", UUID.randomUUID().toString().replace("-", ""));
			}else {
				receive_num = Integer.parseInt(insert.get("receive_num"));
				receive_amt = new BigDecimal(insert.get("receive_amt"));
			}
			Integer total_num = receive_num - Integer.parseInt(return_num);
			BigDecimal total_amt = receive_amt.subtract(returnTotalAmt);
			insert.put("bill_code", bill_code);
			insert.put("bill_month", bill_month);
			insert.put("channel_seller_code", channel_seller_code);
			insert.put("channel_seller_name", channelInfo != null?channelInfo.get("channel_seller_name"):"");
			insert.put("total_amt", total_amt.toString());
			insert.put("total_num", total_num.toString());
			insert.put("return_amt", returnTotalAmt.toString());
			insert.put("return_num", return_num);
			insert.put("fee", fee);
			insert.put("sku_statement_type", "代销结算");
			insert.put("sku_code", sku_code);
			insert.put("product_code", skuinfo != null?skuinfo.get("product_code"):"");
			insert.put("product_name", productInfo != null?productInfo.get("product_name"):"");
			insert.put("sku_price", sku_price);
			insert.put("small_seller_code", small_seller_code);
			insert.put("small_seller_name", sellerinfo !=null?sellerinfo.get("seller_name"):"");
			insert.put("fee_class_code", productInfo.get("tax_code"));
			if(flag) {//存在，执行update
				insert.put("update_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_bill_channel_seller_period_detail").dataUpdate(insert, "return_amt,return_num,total_amt,total_num,update_time", "uid");
			}else {
				insert.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_bill_channel_seller_period_detail").dataInsert(insert);
			}
		}

	}

}
