package com.cmall.ordercenter.tallyorder.platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;


/**
 * 结算5.0-平台入驻导出商品结算明细
 * 
 * @author zht
 * 
 */
public class ExportSkuSettleDetailPlatform extends RootExport {
	@Override
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String flag = request.getParameter("flag");
		String settleCodeParam = request.getParameter("settleCodes");

		String where = "";
		String sql = "select merchant_code, settle_codes from oc_bill_apply_payment_pt a where 1=1 ";
		if (!StringUtils.isEmpty(payCode)) {
			where = "and a.pay_code like '%" + payCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerCode)) {
			where += "and a.merchant_code like '%" + sellerCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerName)) {
			where += "and a.merchant_name like '%" + sellerName + "%' ";
		}
		if (!StringUtils.isEmpty(createTimeFrom)
				&& !StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time >= '" + createTimeFrom
					+ "' and a.create_time <= '" + createTimeTo + "' ";
		} else if (!StringUtils.isEmpty(createTimeFrom)) {
			where += "and a.create_time >= '" + createTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time <= '" + createTimeTo + "' ";
		}
		if (!StringUtils.isEmpty(flag)) {
			where += "and a.flag ='" + flag + "'";
		}
		if (!StringUtils.isEmpty(settleCodeParam)) {
			where += "and a.settle_codes ='" + settleCodeParam + "'";
		}
		
		List<Map<String, String>> condi = new ArrayList<Map<String, String>>();
		List<Map<String, Object>> applyList = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlList(sql + where, null);
		if (null != applyList && applyList.size() > 0) {
			for (Map<String, Object> map : applyList) {
				String settleCode = isEmpty(map.get("settle_codes")) ? "" : map.get("settle_codes").toString();
				String merchant_code = isEmpty(map.get("merchant_code")) ? "" : map.get("merchant_code").toString();
				if (StringUtils.isEmpty(settleCode) || StringUtils.isEmpty(merchant_code))
					continue;
				
				Map<String, String> conMap = new HashMap<String, String>();
				settleCode = settleCode.replace(",", "','");
				conMap.put(merchant_code, settleCode);
				condi.add(conMap);
			}
		}
		
		StringBuffer sb = new StringBuffer();
		for(Map<String, String> conMap : condi) {
			Set<Entry<String, String>> entrys = conMap.entrySet();
			Iterator<Entry<String, String>> it = entrys.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				String smallSellerCode = entry.getKey();
				String settleCodes = entry.getValue();
				if(sb.length() ==0)
					sb.append("(mc.settle_code IN ('" + settleCodes + "') AND mc.merchant_code='" + smallSellerCode + "'");
				else
					sb.append(" OR (mc.settle_code IN ('" + settleCodes + "') AND mc.merchant_code='" + smallSellerCode + "'");
			}
			if(sb.length() > 0)
				sb.append(")");
			
		}
		settleToExcel(sb.toString(), response);
	}

	public void settleToExcel(String settleCodes, HttpServletResponse response) {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			in = ExportSkuSettleDetailPlatform.class
					.getResourceAsStream("/skuDetailPlatform.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);

			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 9);// 设置字体大小
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			HSSFSheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			int startrow = 0;
			String exportSql = "SELECT "
					+ "mc.settle_code as settle_code, oe.product_code as product_code,oe.product_name as product_name,"
					+ "oe.sku_code as sku_code,oe.sku_name as sku_name,"
					+ "oe.small_seller_code as small_seller_code,oe.small_seller_name as small_seller_name, oe.settle_num as settle_num,"
					+ "oe.settle_amount as settle_amount, oe.service_fee as service_fee, "
					+ "(oe.settle_amount - oe.service_fee) AS payable_collect_amount,"
					+ "oe.sale_money as sale_money, oe.postage as postage, oe.manage_money as manage_money, oe.others as others, "
					+ "(oe.settle_amount - oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others) AS settle_collect_amount, "
					+ "oe.period_retention_money as period_money,"
					+ "(oe.settle_amount - oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others - oe.period_retention_money) AS actual_pay_amount "
					+ "FROM oc_bill_final_export oe, oc_bill_merchant_new_spec mc "
					+ "WHERE oe.small_seller_code = mc.merchant_code AND oe.start_time = mc.start_time AND oe.end_time = mc.end_time "
					+ "AND (" +  settleCodes   + ")";
			
			
			List<Map<String, Object>> skuSettleList = DbUp.upTable("oc_bill_final_export")
					.dataSqlList(exportSql, new MDataMap());
			if (null != skuSettleList && skuSettleList.size() > 0) {
				for (Map<String, Object> map : skuSettleList) { 
					String settleCode = isEmpty(map.get("settle_code")) ? "" : map.get("settle_code").toString();
					String productCode = isEmpty(map.get("product_code")) ? "" : map.get("product_code").toString();
					String productName = isEmpty(map.get("product_name")) ? "" : map.get("product_name").toString();
					String skuCode = isEmpty(map.get("sku_code")) ? "" : map.get("sku_code").toString();
					String skuName = isEmpty(map.get("sku_name")) ? "" : map.get("sku_name").toString();
					String smallSellerCode = isEmpty(map.get("small_seller_code")) ? "" : map.get("small_seller_code").toString();
					String smallSellerName = isEmpty(map.get("small_seller_name")) ? "" : map.get("small_seller_name").toString();
					int settle_num = Integer.valueOf(isEmpty(map.get("settle_num")) ? "0.0" : map.get("settle_num").toString());
//					double invoice_amount = Double.valueOf(isEmpty(map.get("invoice_amount")) ? "0.0" : map.get("invoice_amount").toString());
					//本期代收货款合计
					double settle_amount = Double.valueOf(isEmpty(map.get("settle_amount")) ? "0.0" : map.get("settle_amount").toString());
					double service_fee = Double.valueOf(isEmpty(map.get("service_fee")) ? "0.0" : map.get("service_fee").toString());
					//应付代收货款
					double payable_collect_amount = Double.valueOf(isEmpty(map.get("payable_collect_amount")) ? "0.0" : map.get("payable_collect_amount").toString());
					double sale_money = Double.valueOf(isEmpty(map.get("sale_money")) ? "0.0" : map.get("sale_money").toString());
					double postage = Double.valueOf(isEmpty(map.get("postage")) ? "0.0" : map.get("postage").toString());
					double manageMoney = Double.valueOf(isEmpty(map.get("manage_money")) ? "0.0" : map.get("manage_money").toString());
					double others = Double.valueOf(isEmpty(map.get("others")) ? "0.0" : map.get("others").toString());
					//结算代收货款
					double settle_collect_amount = Double.valueOf(isEmpty(map.get("settle_collect_amount")) ? "0.0" : map.get("settle_collect_amount").toString());
					double period_money = Double.valueOf(isEmpty(map.get("period_money")) ? "0.0" : map.get("period_money").toString());
					//实付代收货款
					double actualPayAmount = Double.valueOf(isEmpty(map.get("actual_pay_amount")) ? "0.0" : map.get("actual_pay_amount").toString());
					String bank_account = "", branch_name = "";
					if(!StringUtils.isEmpty(smallSellerCode)) {
						String sql = "select "
								+ "branch_name, joint_number, branch_address, bank_account "
								+ "from usercenter.uc_seller_info_extend b "
								+ "where b.small_seller_code='" + smallSellerCode  + "'";
						List<Map<String, Object>> sellerInfoList = DbUp.upTable("oc_bill_final_export")
								.dataSqlList(sql, new MDataMap());
						if(null != sellerInfoList && sellerInfoList.size() > 0) {
							Map<String, Object> sellerMap = sellerInfoList.get(0);
							bank_account = isEmpty(sellerMap.get("bank_account")) ? "" : sellerMap.get("bank_account").toString();
							branch_name = isEmpty(sellerMap.get("branch_name")) ? "" : sellerMap.get("branch_name").toString();
						}
					}
					row = sheet.createRow((short) startrow + 1); 	 //在现有行号后追加数据
					row.createCell(0).setCellValue(settleCode); 	 //结算单编号
					row.createCell(1).setCellValue(productCode);	 //商品编号
					row.createCell(2).setCellValue(productName);   	 //商品名称
					row.createCell(3).setCellValue(skuCode);	 	 //SKU编号
					row.createCell(4).setCellValue(skuName);   	 	 //SKU名称
					row.createCell(5).setCellValue(smallSellerCode); //商户编号
					row.createCell(6).setCellValue(smallSellerName); //商户名称
					row.createCell(7).setCellValue(branch_name);     //开户行名称
					row.createCell(8).setCellValue(bank_account);    //开户行帐户
					row.createCell(9).setCellValue(settle_num);      //本期代收数量合计
//					row.createCell(8).setCellValue(invoice_amount);  //本期代收货款合计
					row.createCell(10).setCellValue(settle_amount);   //本期代收货款合计
					row.createCell(11).setCellValue(service_fee);  	 //服务费
					row.createCell(12).setCellValue(payable_collect_amount);  	 //应付代收货款
					
					row.createCell(13).setCellValue(sale_money);     			//促销费用
					row.createCell(14).setCellValue(postage);      	 			//邮费
					row.createCell(15).setCellValue(manageMoney);    			//平台管理费
					row.createCell(16).setCellValue(others);      	 			//其他
					row.createCell(17).setCellValue(settle_collect_amount);  	//结算代收货款
					row.createCell(18).setCellValue(period_money); 				//本期质保金
					row.createCell(19).setCellValue(actualPayAmount); 			//实付代收货款
					startrow++;
				}
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename="
					+ java.net.URLEncoder.encode("平台入驻-导出商品结算明细", "UTF-8").replace("+", "") + ".xls");
			outputStream = response.getOutputStream();
			wb.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in)	try { in.close(); } catch (IOException e) {}
			if (null != outputStream) try {	outputStream.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}

	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
