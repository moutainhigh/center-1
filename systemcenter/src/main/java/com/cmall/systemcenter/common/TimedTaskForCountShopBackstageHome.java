package com.cmall.systemcenter.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * 定时统计商家后台首页数据
 * @author GaoYang
 *
 */
public class TimedTaskForCountShopBackstageHome extends RootJob{

	public void doExecute(JobExecutionContext context) {
			
		//获取商家状态为“终审通过”的商家编码
		List<MDataMap> sellerCodeList = new ArrayList<MDataMap>();
		sellerCodeList = DbUp.upTable("uc_sellerinfo").queryAll("seller_code", "", "seller_status = '4497172300040004'", new MDataMap());
		for(int i = 0;i <sellerCodeList.size();i++){
			String sellerCode = sellerCodeList.get(i).get("seller_code");
			if(StringUtils.isNotBlank(sellerCode)){
				//统计商家后台首页数据
				this.countShopBackstageHomeData(sellerCode);
			}
		}
	}

	/**
	 * 统计商家后台首页数据写入到商家后台首页数据表中
	 * @param sellerCode 商家编码
	 */
	private void countShopBackstageHomeData(String sellerCode) {
		
		//下单成功-未付款件数
		int nonPaymentNumber = 0;
		//下单成功-未发货件数
		int accountPaidNumber = 0;
		//在售商品件数
		int onsellProductNumber = 0;
		//商品即将售完件数
		int sellOutNumber = 0;
		//商品已经售完件数
		int besellOutNumber = 0;
		//退货件数
		int returnGoodsNumber = 0;
		//换货件数
		int exchangeGoodsNumber = 0;
		
		try{
			//1.获取订单相关信息
			MDataMap orderInfoMap = DbUp.upTable("oc_orderinfo").oneWhere("sum(case order_status when 4497153900010001 then 1 else 0 end  ) as nonPayment,sum(case order_status when 4497153900010002 then 1 else 0 end  ) as accountPaid", "", "", "seller_code", sellerCode);
			if(orderInfoMap != null){
				if(StringUtils.isNotBlank(orderInfoMap.get("nonPayment"))){
					nonPaymentNumber = Integer.parseInt(orderInfoMap.get("nonPayment"));
				}
				if(StringUtils.isNotBlank(orderInfoMap.get("accountPaid"))){
					accountPaidNumber = Integer.parseInt(orderInfoMap.get("accountPaid"));	
				}
			}
			
			//2.获取商品提示信息
			List<MDataMap> productCodeList = new ArrayList<MDataMap>();
			String sWhere = "seller_code = '"+sellerCode+"' AND product_status = '4497153900060002'";
			productCodeList = DbUp.upTable("pc_productinfo").queryAll("product_code", "", sWhere, new MDataMap());
			if(productCodeList != null && productCodeList.size() >0){
				//获取在售商品件数
				onsellProductNumber = productCodeList.size();
				for(int i = 0; i < productCodeList.size(); i++){
					//获取商品编码
					String productCode = productCodeList.get(i).get("product_code");
					//统计商品库存信息
					MDataMap skuOneMap = DbUp.upTable("pc_skuinfo").oneWhere("sum(stock_num) as sNum","","","product_code",productCode);
					//统计库存临时变量
					int stockTempCnt = 0;
					if(skuOneMap != null){
						//获取商品库存
						if(StringUtils.isNotBlank(skuOneMap.get("sNum"))){
							stockTempCnt = Integer.parseInt(skuOneMap.get("sNum"));
						}
						//计算商品即将售完数量(SKU库存数量小于(含)10件)
						if(stockTempCnt < 11 && stockTempCnt >0){
							sellOutNumber = sellOutNumber+1;
						} else if (stockTempCnt == 0){
							//计算商品已经售完数量
							besellOutNumber = besellOutNumber+1;
						}
					}
				}
			}
		
			//3.获取交易提示信息
			//3.1获取商品退货信息
			List<MDataMap> returnCodeList = new ArrayList<MDataMap>();
			String sRetGoodsWhere = "seller_code = '"+sellerCode+"' AND status = '4497153900050003'";
			returnCodeList = DbUp.upTable("oc_return_goods").queryAll("return_code", "", sRetGoodsWhere, new MDataMap());
			if(returnCodeList != null && returnCodeList.size() >0){
				returnGoodsNumber = returnCodeList.size();
			}
			//3.2获取商品换货信息
			List<MDataMap> exchangeCodeList = new ArrayList<MDataMap>();
			String sExgGoodsWhere = "seller_code = '"+sellerCode+"' AND status = '4497153900020001'";
			exchangeCodeList = DbUp.upTable("oc_exchange_goods").queryAll("exchange_no", "", sExgGoodsWhere, new MDataMap());
			if(exchangeCodeList != null && exchangeCodeList.size() >0){
				exchangeGoodsNumber = exchangeCodeList.size();
			}
		
			//4.将以上信息写入到商家后台首页数据表中
			int homeDataCnt = DbUp.upTable("sc_shopbackstage_home_data").count("seller_code", sellerCode);
			MDataMap insMap = new MDataMap();
			insMap.put("seller_code", sellerCode);
			insMap.put("non_payment_number", String.valueOf(nonPaymentNumber));
			insMap.put("account_paid_number", String.valueOf(accountPaidNumber));
			insMap.put("onsell_product_number", String.valueOf(onsellProductNumber));
			insMap.put("sell_out_number", String.valueOf(sellOutNumber));
			insMap.put("besell_out_number", String.valueOf(besellOutNumber));
			insMap.put("return_goods_number", String.valueOf(returnGoodsNumber));
			insMap.put("exchange_goods_number", String.valueOf(exchangeGoodsNumber));
			insMap.put("last_count_time", DateUtil.getSysDateTimeString());
			if(homeDataCnt > 0){
				//更新字段
				String updColumn = "non_payment_number,account_paid_number,onsell_product_number,sell_out_number,besell_out_number,return_goods_number,exchange_goods_number,last_count_time";
				//以"商家编码"为单位更新
				DbUp.upTable("sc_shopbackstage_home_data").dataUpdate(insMap, updColumn, "seller_code");
			}else{
				//写入操作
				DbUp.upTable("sc_shopbackstage_home_data").dataInsert(insMap);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
