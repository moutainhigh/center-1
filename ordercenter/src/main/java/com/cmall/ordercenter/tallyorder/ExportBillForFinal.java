package com.cmall.ordercenter.tallyorder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * 财务结算列表中导出某期财务结算数据
 * 
 * @author zht
 *
 */
public class ExportBillForFinal extends RootExport {
	/**
	 * 导出结算单
	 */
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String uid = request.getParameter("zw_f_uid");
		Map<String, String> skumap = DbUp.upTable("oc_bill_finance_amount").oneWhere(
				"uid,start_time,end_time,tuistart,tuiend,settle_code,settle_type", "", "uid=:uid", "uid", uid);
		String saleStartTime = skumap.get("start_time").toString();
		String saleEndTime = skumap.get("end_time").toString();
		// 结算类型
		String settle_type = skumap.get("settle_type").toString();
		// 结算单编号
		String settle_code = skumap.get("settle_code").toString();
		// 上个月最后一天
		try {
			getExportInfo(saleStartTime, saleEndTime, settle_code, settle_type, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getExportInfo(String saleStartTime, String saleEndTime, String settleCode, String settleType,
			HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream ouputStream = null;
		try {
			String fileName = this.getTemplateFileName(settleType);
			if (StringUtils.isEmpty(fileName))
				throw new Exception("Can't find Template file!");

			in = ExportBillForFinal.class.getResourceAsStream("/" + fileName);
			HSSFWorkbook wb = new HSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			// String settleType = getSettleType(settleCode);
			switch (settleType) {
			case "4497477900040001":
				processStdFinanceBill(sheet, settleCode, saleStartTime, saleEndTime);
				break;
			case "4497477900040002":
				processCrossBorderBSFinanceBill(sheet, settleCode, saleStartTime, saleEndTime);
				break;
			case "4497477900040003":
				processCrossBorderDirectMailFinanceBill(sheet, settleCode, saleStartTime, saleEndTime);
				break;
			case "4497477900040004":
				processPlatformFinanceBill(sheet, settleCode, saleStartTime, saleEndTime);
				break;
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition",
					"attachment;filename=" + java.net.URLEncoder.encode("财务结算单", "UTF-8") + settleCode + ".xls");
			ouputStream = response.getOutputStream();
			ouputStream.flush();
			wb.write(ouputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != ouputStream)
				ouputStream.close();
			if (null != in)
				in.close();
		}
	}

	/**
	 * 导出常规结算财务明细数据
	 * 
	 * @param sheet
	 * @param settleCode
	 * @param saleStartTime
	 * @param saleEndTime
	 */
	private void processStdFinanceBill(Sheet sheet, String settleCode, String saleStartTime, String saleEndTime) {
		int no = 1;
		// 增加了导出商户通路字段
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append("success_num, success_amount,return_num,return_amount,settle_num,");
		sb.append("settle_amount,max_retention_money,deduct_retention_money,period_retention_money,");
		sb.append("sale_money,postage,manage_money,others,add_amount,rate,input_tax_subtotal, total,other_pay_reason");
		/**
		 * 添加新字段money_collection_way(质保金收取方式)<br>
		 * 2016-10-31 zhy
		 */
		sb.append(",money_collection_way");
		// sb.append(" from oc_bill_final_export where start_time=
		// '"+saleStartTime+"' and end_time='"+saleEndTime+"'");
		sb.append(" from oc_bill_final_export where settle_code='" + settleCode + "'");
		String sql = sb.toString();
		String product_code = "";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, null);
		for (Map<String, Object> map : list) {
			product_code = map.get("product_code").toString();
			String passage = map.get("passage").toString();
			String product_name = map.get("product_name").toString();
			String sku_code = map.get("sku_code").toString();
			String sku_name = map.get("sku_name").toString();
			String cost_price = map.get("cost_price").toString();
			String sell_price = map.get("sell_price").toString();
			String product_contract_sign = map.get("product_contract_sign").toString();// 商品合同签署
			String product_alter = map.get("product_alter").toString();// 商品调编
			String small_seller_code = map.get("small_seller_code").toString();
			String small_seller_name = map.get("small_seller_name").toString();
			String branch_name = map.get("branch_name").toString();
			String branch_account = map.get("branch_account").toString();
			String supplier_level = map.get("supplier_level").toString();
			String supplier_rate = map.get("supplier_rate").toString();
			String success_num = map.get("success_num").toString();
			String success_amount = map.get("success_amount").toString();
			String return_num = map.get("return_num").toString();
			String return_amount = map.get("return_amount").toString();
			String settle_num = map.get("settle_num").toString();
			String settle_amount = map.get("settle_amount").toString();
			String max_retention_money = map.get("max_retention_money").toString();
			String deduct_retention_money = map.get("deduct_retention_money").toString();
			String period_retention_money = map.get("period_retention_money").toString();
			String sale_money = map.get("sale_money").toString();
			String postage = map.get("postage").toString();
			String manage_money = map.get("manage_money").toString();
			String others = map.get("others").toString();
			double add_amount = Double.valueOf(map.get("add_amount").toString());
			String rate = map.get("rate").toString();
			String input_tax_subtotal = map.get("input_tax_subtotal").toString();
			double total = Double.valueOf(map.get("total").toString());
			Double amount = total - add_amount;
			String other_pay_reason = map.get("other_pay_reason").toString();
			/**
			 * 添加新字段money_collection_way(质保金收取方式)<br>
			 * 2016-10-31 zhy
			 */
			String money_collection_way=map.get("money_collection_way") == null ? "" : map.get("money_collection_way").toString();
			// 总列数
			int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
			// 总行数
			int lastRowIndex = sheet.getLastRowNum();
			int startrow = lastRowIndex;
			Row row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
			row.createCell(0).setCellValue(no);
			no++;
			row.createCell(1).setCellValue("常规结算"); // 设置第二个（从0开始）单元格的数据
			row.createCell(2).setCellValue(passage);// 通路
			row.createCell(3).setCellValue(product_code);
			row.createCell(4).setCellValue(product_name);
			row.createCell(5).setCellValue(sku_code);
			row.createCell(6).setCellValue(sku_name);
			row.createCell(7).setCellValue(cost_price);
			row.createCell(8).setCellValue(sell_price);
			row.createCell(9).setCellValue("");// 商品合同签署
			row.createCell(10).setCellValue("");// 商品调编
			row.createCell(11).setCellValue(small_seller_code);
			row.createCell(12).setCellValue(small_seller_name);
			row.createCell(13).setCellValue(branch_name);
			row.createCell(14).setCellValue(branch_account);
			row.createCell(15).setCellValue("");// 供应商级别
			row.createCell(16).setCellValue("");// 级别比率
			row.createCell(17).setCellValue("");
			row.createCell(17).setCellValue(success_num);// 妥投数量
			row.createCell(18).setCellValue(success_amount);// 妥投金额
			row.createCell(19).setCellValue(return_num);// 销退数量
			row.createCell(20).setCellValue(return_amount);// 消退金额
			row.createCell(21).setCellValue(settle_num);// 本单结算总数量
			row.createCell(22).setCellValue(settle_amount);// 总金额
			/**
			 * 添加新字段money_collection_way(质保金收取方式)<br>
			 * 2016-10-31 zhy
			 */
			row.createCell(23).setCellValue(money_collection_way);// 质保金收取方式
			row.createCell(24).setCellValue(max_retention_money);// 最大质保
			row.createCell(25).setCellValue(deduct_retention_money);// 已扣质保
			row.createCell(26).setCellValue(period_retention_money);// 本期质保
			row.createCell(27).setCellValue(sale_money);// 促销费用
			row.createCell(28).setCellValue(postage);// 邮费
			row.createCell(29).setCellValue(manage_money);// 平台
			row.createCell(30).setCellValue(others);// 其他
			row.createCell(31).setCellValue(add_amount);// 附加
			row.createCell(32).setCellValue(rate);// 税率
			row.createCell(33).setCellValue(input_tax_subtotal);// j进项税金小计
			row.createCell(34).setCellValue(amount);// 合计
			row.createCell(35).setCellValue(other_pay_reason);// 其他扣费原因
		}
		sheet.getRow(0).getCell(0)
				.setCellValue(saleStartTime.substring(0, 10) + "至" + saleEndTime.substring(0, 10) + "常规商户商品结算报表");
	}

	/**
	 * 导出跨境保税结算财务明细数据
	 * 
	 * @param sheet
	 * @param settleCode
	 * @param saleStartTime
	 * @param saleEndTime
	 */
	private void processCrossBorderBSFinanceBill(Sheet sheet, String settleCode, String saleStartTime,
			String saleEndTime) {
		int no = 1;
		// 增加了导出商户通路字段
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append("success_num, success_amount,return_num,return_amount,settle_num,");
		sb.append("settle_amount,service_fee,");
		sb.append("sale_money,postage,manage_money,others,add_amount,total,other_pay_reason");
		//添加质保金收取方式，最大质保金，本期质保金 2016-10-31 zhy
		sb.append(",max_retention_money,period_retention_money,money_collection_way");
		sb.append(" from oc_bill_final_export where settle_code='" + settleCode + "'");
		String sql = sb.toString();
		String product_code = "";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, null);
		for (Map<String, Object> map : list) {
			product_code = map.get("product_code").toString();
			String passage = map.get("passage").toString();
			String product_name = map.get("product_name").toString();
			String sku_code = map.get("sku_code").toString();
			String sku_name = map.get("sku_name").toString();
			String cost_price = map.get("cost_price").toString();
			String sell_price = map.get("sell_price").toString();
			String product_contract_sign = map.get("product_contract_sign").toString(); // 商品合同签署
			String product_alter = map.get("product_alter").toString(); // 商品调编
			String small_seller_code = map.get("small_seller_code").toString();
			String small_seller_name = map.get("small_seller_name").toString();
			String branch_name = map.get("branch_name").toString();
			String branch_account = map.get("branch_account").toString();
			String supplier_level = map.get("supplier_level").toString();
			String supplier_rate = map.get("supplier_rate").toString();
			String success_num = map.get("success_num").toString();
			String success_amount = map.get("success_amount").toString();
			String return_num = map.get("return_num").toString();
			String return_amount = map.get("return_amount").toString();
			String settle_num = map.get("settle_num").toString();
			String settle_amount = map.get("settle_amount").toString();
			String service_fee = map.get("service_fee").toString();
			String sale_money = map.get("sale_money").toString();
			String postage = map.get("postage").toString();
			String manage_money = map.get("manage_money").toString();
			String others = map.get("others").toString();
			double add_amount = Double.valueOf(map.get("add_amount").toString());
			double total = Double.valueOf(map.get("total").toString());
			String other_pay_reason = map.get("other_pay_reason").toString();
			//添加质保金收取方式，最大质保金，本期质保金 2016-10-31 zhy
			double max_retention_money = Double.valueOf(map.get("max_retention_money").toString());
			double period_retention_money = Double.valueOf(map.get("period_retention_money").toString());
			String money_collection_way=map.get("money_collection_way") == null ? "" : map.get("money_collection_way").toString();
			// 总行数
			int lastRowIndex = sheet.getLastRowNum();
			int startrow = lastRowIndex;
			Row row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
			row.createCell(0).setCellValue(no);
			no++;
			row.createCell(1).setCellValue("跨境保税结算"); // 设置第二个（从0开始）单元格的数据
			row.createCell(2).setCellValue(passage);// 通路
			row.createCell(3).setCellValue(product_code);
			row.createCell(4).setCellValue(product_name);
			row.createCell(5).setCellValue(sku_code);
			row.createCell(6).setCellValue(sku_name);
			row.createCell(7).setCellValue(cost_price);
			row.createCell(8).setCellValue(sell_price);
			row.createCell(9).setCellValue(small_seller_code);
			row.createCell(10).setCellValue(small_seller_name);
			row.createCell(11).setCellValue(branch_name);
			row.createCell(12).setCellValue(branch_account);
			row.createCell(13).setCellValue(success_num); // 成功数量
			row.createCell(14).setCellValue(success_amount); // 成功金额
			row.createCell(15).setCellValue(return_num); // 销退数量
			row.createCell(16).setCellValue(return_amount); // 消退金额
			row.createCell(17).setCellValue(settle_num); // 代收数量合计=成功数量-销退数量
			row.createCell(18).setCellValue(settle_amount); // 代收货款合计
			row.createCell(19).setCellValue(service_fee); // 服务费
			double amountToPay = Double.parseDouble(settle_amount) - Double.parseDouble(service_fee);
			row.createCell(20).setCellValue(amountToPay); // 应付代收金额
			row.createCell(21).setCellValue(sale_money); // 促销费用
			row.createCell(22).setCellValue(postage); // 邮费
			row.createCell(23).setCellValue(manage_money); // 平台
			row.createCell(24).setCellValue(others); // 其他
			row.createCell(25).setCellValue(add_amount); // 附加扣费合计
			double amountActualPay = amountToPay - add_amount;
			row.createCell(26).setCellValue(amountActualPay); // 实付代收货款=应付代收货款-附加扣费合计
			row.createCell(27).setCellValue(money_collection_way); // 质保金收取方式
			row.createCell(28).setCellValue(max_retention_money); // 最大质保金
			row.createCell(29).setCellValue(period_retention_money); // 本期质保金
			row.createCell(30).setCellValue(other_pay_reason); // 其他扣费原因
		}
		sheet.getRow(0).getCell(0)
				.setCellValue(saleStartTime.substring(0, 10) + "至" + saleEndTime.substring(0, 10) + "跨境保税商户商品结算报表");
	}

	/**
	 * 导出跨境直邮结算财务明细数据
	 * 
	 * @param sheet
	 * @param settleCode
	 * @param saleStartTime
	 * @param saleEndTime
	 */
	private void processCrossBorderDirectMailFinanceBill(Sheet sheet, String settleCode, String saleStartTime,
			String saleEndTime) {
		int no = 1;
		// 增加了导出商户通路字段
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append("success_num, success_amount,return_num,return_amount,settle_num,");
		sb.append("settle_amount,service_fee,");
		//新添加本期质保金，最大质保金，质保金收取方式 2016-10-28 zhy
		sb.append("	max_retention_money,period_retention_money,money_collection_way,");
		sb.append("sale_money,postage,manage_money,others,add_amount,total,other_pay_reason");
		sb.append(" from oc_bill_final_export where settle_code='" + settleCode + "'");
		String sql = sb.toString();
		String product_code = "";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, null);
		for (Map<String, Object> map : list) {
			product_code = map.get("product_code").toString();
			String passage = map.get("passage").toString();
			String product_name = map.get("product_name").toString();
			String sku_code = map.get("sku_code").toString();
			String sku_name = map.get("sku_name").toString();
			String cost_price = map.get("cost_price").toString();
			String sell_price = map.get("sell_price").toString();
			String product_contract_sign = map.get("product_contract_sign").toString(); // 商品合同签署
			String product_alter = map.get("product_alter").toString(); // 商品调编
			String small_seller_code = map.get("small_seller_code").toString();
			String small_seller_name = map.get("small_seller_name").toString();
			String branch_name = map.get("branch_name").toString();
			String branch_account = map.get("branch_account").toString();
			String supplier_level = map.get("supplier_level").toString();
			String supplier_rate = map.get("supplier_rate").toString();
			String success_num = map.get("success_num").toString();
			String success_amount = map.get("success_amount").toString();
			String return_num = map.get("return_num").toString();
			String return_amount = map.get("return_amount").toString();
			String settle_num = map.get("settle_num").toString();
			String settle_amount = map.get("settle_amount").toString();
			String service_fee = map.get("service_fee").toString();
			String sale_money = map.get("sale_money").toString();
			String postage = map.get("postage").toString();
			String manage_money = map.get("manage_money").toString();
			String others = map.get("others").toString();
			double add_amount = Double.valueOf(map.get("add_amount").toString());
			double total = Double.valueOf(map.get("total").toString());
			String other_pay_reason = map.get("other_pay_reason").toString();
			//新添加本期质保金，最大质保金，质保金收取方式 2016-10-28 zhy
			String max_retention_money = map.get("max_retention_money").toString();
			String period_retention_money = map.get("period_retention_money").toString();
			String money_collection_way=map.get("money_collection_way") == null ? "" : map.get("money_collection_way").toString();
			// 总行数
			int lastRowIndex = sheet.getLastRowNum();
			int startrow = lastRowIndex;
			Row row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
			row.createCell(0).setCellValue(no);
			no++;
			row.createCell(1).setCellValue("跨境直邮结算"); // 设置第二个（从0开始）单元格的数据
			row.createCell(2).setCellValue(passage);// 通路
			row.createCell(3).setCellValue(product_code);
			row.createCell(4).setCellValue(product_name);
			row.createCell(5).setCellValue(sku_code);
			row.createCell(6).setCellValue(sku_name);
			row.createCell(7).setCellValue(cost_price);
			row.createCell(8).setCellValue(sell_price);
			row.createCell(9).setCellValue(small_seller_code);
			row.createCell(10).setCellValue(small_seller_name);
			row.createCell(11).setCellValue(branch_name);
			row.createCell(12).setCellValue(branch_account);
			row.createCell(13).setCellValue(success_num); // 成功数量
			row.createCell(14).setCellValue(success_amount); // 成功金额
			row.createCell(15).setCellValue(return_num); // 销退数量
			row.createCell(16).setCellValue(return_amount); // 消退金额
			row.createCell(17).setCellValue(settle_num); // 代收数量合计=成功数量-销退数量
			row.createCell(18).setCellValue(settle_amount); // 代收货款合计
			row.createCell(19).setCellValue(service_fee); // 服务费
			double amountToPay = Double.parseDouble(settle_amount) - Double.parseDouble(service_fee);
			row.createCell(20).setCellValue(amountToPay); // 应付代收金额
			row.createCell(21).setCellValue(sale_money); // 促销费用
			row.createCell(22).setCellValue(postage); // 邮费
			row.createCell(23).setCellValue(manage_money); // 平台
			row.createCell(24).setCellValue(others); // 其他
			row.createCell(25).setCellValue(add_amount); // 附加扣费合计
			double amountActualPay = amountToPay - add_amount;
			row.createCell(26).setCellValue(amountActualPay); // 实付代收货款=应付代收货款-附加扣费合计
			row.createCell(27).setCellValue(money_collection_way); // 质保金收取方式
			row.createCell(28).setCellValue(max_retention_money); // 最大质保金
			row.createCell(29).setCellValue(period_retention_money); // 本期质保金
			row.createCell(30).setCellValue(other_pay_reason); // 其他扣费原因
		}
		sheet.getRow(0).getCell(0)
				.setCellValue(saleStartTime.substring(0, 10) + "至" + saleEndTime.substring(0, 10) + "跨境直邮商户商品结算报表");
	}

	/**
	 * 导出平台入驻结算财务明细数据
	 * 
	 * @param sheet
	 * @param settleCode
	 * @param saleStartTime
	 * @param saleEndTime
	 */
	private void processPlatformFinanceBill(Sheet sheet, String settleCode, String saleStartTime, String saleEndTime) {
		int no = 1;
		// 增加了导出商户通路字段
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append("success_num, success_amount,return_num,return_amount,settle_num,");
		sb.append("settle_amount,service_fee,");
		//新添加本期质保金，最大质保金，质保金收取方式 2016-10-28 zhy
		//新添加已扣质保金字段 2017-06-30 zhy
		sb.append("	max_retention_money,deduct_retention_money,period_retention_money,money_collection_way,");
		sb.append("sale_money,postage,manage_money,others,add_amount,total,other_pay_reason");
		sb.append(" from oc_bill_final_export where settle_code='" + settleCode + "'");
		String sql = sb.toString();
		String product_code = "";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, null);
		for (Map<String, Object> map : list) {
			product_code = map.get("product_code").toString();
			String passage = map.get("passage").toString();
			String product_name = map.get("product_name").toString();
			String sku_code = map.get("sku_code").toString();
			String sku_name = map.get("sku_name").toString();
			String cost_price = map.get("cost_price").toString();
			String sell_price = map.get("sell_price").toString();
			String product_contract_sign = map.get("product_contract_sign").toString(); // 商品合同签署
			String product_alter = map.get("product_alter").toString(); // 商品调编
			String small_seller_code = map.get("small_seller_code").toString();
			String small_seller_name = map.get("small_seller_name").toString();
			String branch_name = map.get("branch_name").toString();
			String branch_account = map.get("branch_account").toString();
			String supplier_level = map.get("supplier_level").toString();
			String supplier_rate = map.get("supplier_rate").toString();
			String success_num = map.get("success_num").toString();
			String success_amount = map.get("success_amount").toString();
			String return_num = map.get("return_num").toString();
			String return_amount = map.get("return_amount").toString();
			String settle_num = map.get("settle_num").toString();
			String settle_amount = map.get("settle_amount").toString();
			String service_fee = map.get("service_fee").toString();
			String sale_money = map.get("sale_money").toString();
			String postage = map.get("postage").toString();
			String manage_money = map.get("manage_money").toString();
			String others = map.get("others").toString();
			double add_amount = Double.valueOf(map.get("add_amount").toString());
			double total = Double.valueOf(map.get("total").toString());
			String other_pay_reason = map.get("other_pay_reason").toString();
			//新添加本期质保金，最大质保金，质保金收取方式 2016-10-28 zhy
			String max_retention_money = map.get("max_retention_money").toString();
			String deduct_retention_money =map.get("deduct_retention_money").toString();
			String period_retention_money = map.get("period_retention_money").toString();
			String money_collection_way=map.get("money_collection_way") == null ? "" : map.get("money_collection_way").toString();
			// 总行数
			int lastRowIndex = sheet.getLastRowNum();
			int startrow = lastRowIndex;
			Row row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
			row.createCell(0).setCellValue(no);
			no++;
			row.createCell(1).setCellValue("平台入驻结算"); // 设置第二个（从0开始）单元格的数据
			row.createCell(2).setCellValue(passage);// 通路
			row.createCell(3).setCellValue(product_code);
			row.createCell(4).setCellValue(product_name);
			row.createCell(5).setCellValue(sku_code);
			row.createCell(6).setCellValue(sku_name);
			row.createCell(7).setCellValue(cost_price);
			row.createCell(8).setCellValue(sell_price);
			row.createCell(9).setCellValue(small_seller_code);
			row.createCell(10).setCellValue(small_seller_name);
			row.createCell(11).setCellValue(branch_name);
			row.createCell(12).setCellValue(branch_account);
			row.createCell(13).setCellValue(success_num); // 成功数量
			row.createCell(14).setCellValue(success_amount); // 成功金额
			row.createCell(15).setCellValue(return_num); // 销退数量
			row.createCell(16).setCellValue(return_amount); // 消退金额
			row.createCell(17).setCellValue(settle_num); // 代收数量合计=成功数量-销退数量
			row.createCell(18).setCellValue(settle_amount); // 代收货款合计
			row.createCell(19).setCellValue(service_fee); // 服务费
			double amountToPay = Double.parseDouble(settle_amount) - Double.parseDouble(service_fee);
			row.createCell(20).setCellValue(amountToPay); // 应付代收金额
			row.createCell(21).setCellValue(sale_money); // 促销费用
			row.createCell(22).setCellValue(postage); // 邮费
			row.createCell(23).setCellValue(manage_money); // 平台
			row.createCell(24).setCellValue(others); // 其他
			row.createCell(25).setCellValue(add_amount); // 附加扣费合计
			double amountActualPay = amountToPay - add_amount;
			row.createCell(26).setCellValue(amountActualPay); // 实付代收货款=应付代收货款-附加扣费合计
			row.createCell(27).setCellValue(money_collection_way); // 质保金收取方式
			row.createCell(28).setCellValue(max_retention_money); // 最大质保金
			row.createCell(29).setCellValue(deduct_retention_money); // 已扣质保金
			row.createCell(30).setCellValue(period_retention_money); // 本期质保金
			row.createCell(31).setCellValue(other_pay_reason); // 其他扣费原因
		}
		sheet.getRow(0).getCell(0)
				.setCellValue(saleStartTime.substring(0, 10) + "至" + saleEndTime.substring(0, 10) + "平台入驻商户商品结算报表");
	}

	/**
	 * 根据不同的结算类型返回该结算类型所对应的财务结算导出模板 4497477900040001 常规结算 4497477900040002 跨境保税
	 * 4497477900040003 跨境直邮 4497477900040004 平台入驻
	 * 
	 * @param settleCode
	 * @return 不同结算类型对应的财务结算导出模板文件名
	 */
	private String getTemplateFileName(String settleType) {
		String fileName = "";
		// String settleType = getSettleType(settleCode);
		if (StringUtils.isNotEmpty(settleType)) {
			switch (settleType) {
			case "4497477900040001":
				fileName = "StdFinanceBill.xls";
				break;
			case "4497477900040002":
				fileName = "CrossBorderBSFinanceBill.xls";
				break;
			case "4497477900040003":
				fileName = "CrossBorderDirectMailFinanceBill.xls";
				break;
			case "4497477900040004":
				fileName = "PlatformFinanceBill.xls";
				break;
			}
		}
		return fileName;
	}

	/**
	 * 根据结算编号返回该编号所对应的结算类型
	 * 
	 * @param settleCode
	 * @return
	 */
	// private String getSettleType(String settleCode) {
	// String settleType = "";
	// if(StringUtils.isNotEmpty(settleCode) && settleCode.length() > 8) {
	// String periodCode = settleCode.substring(8);
	// MDataMap map =
	// DbUp.upTable("oc_bill_account_period").oneWhere("settle_type", "",
	// "code=:code and state=:state",
	// "code", periodCode, "state","1");
	// if(map != null && !map.isEmpty()) {
	// settleType = map.get("settle_type");
	// }
	// }
	// return settleType;
	// }

	public static void main(String[] args) throws Exception {
		ExportBillForFinal aa = new ExportBillForFinal();
		// aa.getExportInfo("2014-02-11 00:00:00","2014-02-23 17:40:31");
	}
}
