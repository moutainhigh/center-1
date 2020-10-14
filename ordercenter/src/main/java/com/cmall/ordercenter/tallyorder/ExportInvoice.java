package com.cmall.ordercenter.tallyorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * 结算3.0-导出发票信息
 * @author zht
 *
 */
public class ExportInvoice extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Override
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String flag = request.getParameter("flag");

		String where = "";
		String sql = "select pay_code, merchant_code, merchant_name, invoice_codes, invoice_amount, "
				+ "actual_invoice_amount, period_money, add_deduction, actual_pay_amount, wait_pay_amount, create_time, pay_time "
				+ "from oc_bill_apply_payment a where 1=1 ";
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
		try {
			invoiceToExcel(sql + where, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void invoiceToExcel(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			in = ExportApplyPayment.class.getResourceAsStream("/invoiceinfo.xls");
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
			List<Map<String, Object>> applyPayList = DbUp.upTable("oc_bill_apply_payment").dataSqlList(sql, null);
			if(null != applyPayList && applyPayList.size() > 0) {
				double sumAmount = 0.0;
				double sumTaxAmount = 0.0;
				double sumTotalMoney = 0.0;
				for (Map<String, Object> applyPay : applyPayList) {
					String payCode = applyPay.get("pay_code").toString();
					String sellerCode = applyPay.get("merchant_code").toString();
					String sellerName = applyPay.get("merchant_name").toString();
					String invoiceCodes = applyPay.get("invoice_codes").toString();
					if(!StringUtils.isEmpty(invoiceCodes)) {
						if(invoiceCodes.endsWith(","))
							invoiceCodes = invoiceCodes.substring(0, invoiceCodes.length() - 1);
						invoiceCodes = invoiceCodes.replaceAll(",", "','");
					}
					sql = "select invoice_code, amount, tax_amount, total_money "
							+ "from oc_bill_invoice "
							+ "where invoice_code in ('" + invoiceCodes + "')";
					List<Map<String, Object>> invoiceList = DbUp.upTable("oc_bill_invoice").dataSqlList(sql, null);
					if(null != invoiceList && invoiceList.size() > 0) {
						for(Map<String, Object> invoice : invoiceList) {
							String invoiceCode = invoice.get("invoice_code").toString();
							double amount = Double.valueOf(isEmpty(invoice.get("amount")) ? "0.00"
									: invoice.get("amount").toString());
							double taxAmount = Double.valueOf(isEmpty(invoice.get("tax_amount")) ? "0.00"
									: invoice.get("tax_amount").toString());
							double totalMoney = Double.valueOf(isEmpty(invoice.get("total_money")) ? "0.00"
									: invoice.get("total_money").toString());
							sumAmount += amount;
							sumTaxAmount += taxAmount;
							sumTotalMoney += totalMoney;
							row = sheet.createRow((short) startrow + 1); 	 //在现有行号后追加数据
							row.createCell(0).setCellValue(sellerCode); 	 //商户编号
							row.createCell(1).setCellValue(sellerName);		 //商户名称
							row.createCell(2).setCellValue(invoiceCode);   	 //发票号
							row.createCell(3).setCellValue(amount);   		 //金额
							row.createCell(4).setCellValue(taxAmount);		 //税额
							row.createCell(5).setCellValue(totalMoney);      //税价合计
							startrow++;
						}
					}
				}
				row = sheet.createRow((short) startrow + 1); 	 	  //在现有行号后追加数据
				row.createCell(0).setCellValue("合计");
				row.createCell(3).setCellValue(sumAmount);		 	 //累加金额
				row.createCell(4).setCellValue(sumTaxAmount);   	 //累加税额
				row.createCell(5).setCellValue(sumTotalMoney);   	 //累加税价合计
				
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" +
					java.net.URLEncoder.encode("发票清单", "UTF-8").replace("+", "") + ".xls");
			outputStream = response.getOutputStream();
			wb.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != in) in.close();
			if(null != outputStream) outputStream.close();
		}
	}
	
	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
