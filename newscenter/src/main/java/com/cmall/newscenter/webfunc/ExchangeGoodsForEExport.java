package com.cmall.newscenter.webfunc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出退货信息
 * 
 * @author shiyz
 * 
 */
public class ExchangeGoodsForEExport extends RootExport {

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		setExportName("惠美丽退货详情"
				+ FormatHelper.upDateTime(new Date(), "yyMMddHHmmss"));// 修改文件名

		// 修改数据
		MPageData pageData = getPageData();

		List<String> head_list = pageData.getPageHead();
		// 重新写入头
		head_list.clear();
		head_list.add("订单号");
		head_list.add("出库时间");
		head_list.add("通路");
		head_list.add("产品条码(商品)");
		head_list.add("商品名称");
		head_list.add("产品规格");
		head_list.add("商品单价");
		head_list.add("商品数量");
		head_list.add("商品总价");
		head_list.add("购买优惠信息");
		head_list.add("客户付邮");
		head_list.add("总金额");
		head_list.add("商品成本单价");
		head_list.add("商品成本总价");
		head_list.add("退货单号");
		head_list.add("买家编号");
		head_list.add("退货入库时间");
		head_list.add("退款金额");
		head_list.add("退货原因");
		head_list.add("退货类型");

		// 重写数据
		List<List<String>> pd = pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();

		for (List<String> ppd : pd) {

			String return_code = ppd.get(0);// 退货单号

			// 查询退货详情
			MDataMap return_list = DbUp.upTable("oc_return_goods").one(
					"return_code", return_code);

			if (return_list == null || return_list.size() < 1) {
				continue;
			} else {

				String buyer_code = return_list.get("buyer_code");// 买家编号

				String return_time = return_list.get("create_time");
				;// 入库时间

				String return_reason = return_list.get("return_reason");
				;// 退货原因

				String order_code = return_list.get("order_code");
				;// 订单号

				String create_time = "";// 订单出库时间

				// 查询订单详情
				MDataMap return_detail_list = DbUp.upTable(
						"oc_return_goods_detail").one("return_code",
						return_code);

				if (return_detail_list != null) {

					MDataMap orderMap = DbUp.upTable("oc_order_shipments").one(
							"order_code", order_code);

					if (orderMap != null) {

						create_time = orderMap.get("create_time");

					}

					String sell_productcode = return_detail_list.get("sku_code");

					String sku_num = return_detail_list.get("count");
					
					String sku_code = "";

					// 查询商品信息
					MDataMap product_data = null;
					// 查询订单信息
					MDataMap order_data = null;

					MDataMap order_tail = null;

					String product_code = "";
					
					String sku_price = "";
					
					String count ="";

					StringBuffer sb = new StringBuffer("");

					try {
						
							product_code = (String) DbUp.upTable("pc_skuinfo")
									.dataGet("product_code", "sku_code=:sku_code",
											new MDataMap("sku_code", sell_productcode));// 查询商品SKUid	

							product_data = DbUp.upTable("pc_productinfo")
									.queryByWhere("product_code", product_code)
									.get(0);
							
						order_data = DbUp.upTable("oc_orderinfo")
								.queryByWhere("order_code", order_code).get(0);

						order_tail = DbUp
								.upTable("oc_orderdetail")
								.queryByWhere("order_code", order_code,
										"sku_code", sell_productcode).get(0);
						
						sku_price = order_tail.get("sku_price");
						
						count = order_tail.get("sku_num");

					} catch (Exception e) {
						continue;// 防止出现数据不完整的情况
					}

					
					

					// 7 为 产品规格
					if (StringUtils.isNotBlank(product_code)) {
						List<MDataMap> list = DbUp
								.upTable("pc_productproperty").queryAll(
										"property_key,property_value",
										"",
										"product_code=:product_code",
										new MDataMap("product_code",
												product_code));
						if (list != null && list.size() > 0) {

							for (MDataMap mm : list) {
								String property_key = mm.get("property_key");
								String property_value = mm
										.get("property_value");
								sb.append(",").append(property_key).append("=")
										.append(property_value);
							}

							if (sb.length() > 0) {
								sb = sb.deleteCharAt(0);
							}
							ppd.set(7, sb.toString());
						}
					}

					String current_price = "";

					if (return_detail_list != null) {

						current_price = return_detail_list.get("current_price");
					}

					List<String> dd = new ArrayList<String>(34);

					dd.add(order_code);// 订单编号
					dd.add(create_time);// 出库时间
					dd.add("");// 通路
					dd.add(product_data.get("sell_productcode"));// 产品条码(商品)
					dd.add(product_data.get("product_name"));// 商品名称
					dd.add(sb.toString());// 产品规格
					dd.add(sku_price);// 商品单价
					dd.add(sku_num);// 商品数量
					dd.add(String.valueOf(Double.valueOf(sku_price)
							* Double.valueOf(sku_num)));// 商品总价
					dd.add("");// 购买优惠信息
					dd.add(order_data.get("transport_money"));// 客户付邮
					dd.add(String.valueOf(Double.valueOf(sku_price)
							* Double.valueOf(sku_num)
							+ Double.valueOf(order_data.get("transport_money"))));// 总金额
					dd.add(String.valueOf(Double.valueOf(product_data
							.get("cost_price"))));// 商品成本单价
					dd.add(String.valueOf(Double.valueOf(product_data
							.get("cost_price"))
							* Double.valueOf(product_data.get("cost_price"))));// 商品成本总价
					dd.add(return_code);// 退货单号
					dd.add(buyer_code);// 买家编号
					dd.add(return_time);// 入库时间
					dd.add(String.valueOf(Double.valueOf(current_price)
							* Double.valueOf(sku_num)));// 金额
					dd.add(return_reason);// 原因
					dd.add("退货");// 退货类型
					data.add(dd);
				}

			}

		}
		pageData.setPageData(data);

		doExport();
	}
}
