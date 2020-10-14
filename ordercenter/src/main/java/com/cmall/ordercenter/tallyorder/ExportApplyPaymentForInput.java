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
 * 导出已在用友NC系统付款后申请单,用于确认,确认后在ERP系统可"确认付款"
 * @author zht
 * 待审核   4497477900010001
 * 审核通过 4497477900010002
 * 已确认   4497477900010003
 * 已付款   4497477900010004
 * 拒绝     4497477900010005
 * 已收款 4497477900010006
 */
public class ExportApplyPaymentForInput extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String isPay = request.getParameter("isPay");
		
		//只查询已确认的付款申请单
		String where = "";
		String sql = "select pay_code, merchant_code, merchant_name, invoice_amount, "
				+ "actual_invoice_amount, period_money, add_deduction, actual_pay_amount, wait_pay_amount, create_time, pay_time "
				+ "from oc_bill_apply_payment a where a.flag= '4497477900010003' ";
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
		if (!StringUtils.isEmpty(isPay)) {
			where += "and a.is_pay ='" + isPay + "'";
		}
		try {
			applyPaymentToExcel(sql + where, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applyPaymentToExcel(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
//		FileOutputStream out = null;
		try {
			in = ExportApplyPayment.class
					.getResourceAsStream("/applyPaymentForInput.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);
//			out = new FileOutputStream(sdf.format(new Date())); 
	
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
				for (Map<String, Object> applyPay : applyPayList) {
					String payCode = applyPay.get("pay_code").toString();
					String sellerCode = applyPay.get("merchant_code").toString();
					String sellerName = applyPay.get("merchant_name").toString();
//					double invoiceAmount = Double.valueOf(isEmpty(applyPay.get("invoice_amount")) ? "0.00"
//							: applyPay.get("invoice_amount").toString());
//					double actualInvoiceAmount = Double.valueOf(isEmpty(applyPay.get("actual_invoice_amount")) ? "0.00"
//							: applyPay.get("actual_invoice_amount").toString());
//					double periodMoney = Double.valueOf(isEmpty(applyPay.get("period_money")) ? "0.00"
//							: applyPay.get("period_money").toString());
//					double addDeduction = Double.valueOf(isEmpty(applyPay.get("add_deduction")) ? "0.00"
//							: applyPay.get("add_deduction").toString());
//					double actualPayAmount = Double.valueOf(isEmpty(applyPay.get("actual_pay_amount")) ? "0.00"
//							: applyPay.get("actual_pay_amount").toString());
					double waitPayAmount = Double.valueOf(isEmpty(applyPay.get("wait_pay_amount")) ? "0.00"
							: applyPay.get("wait_pay_amount").toString());
					sumAmount += waitPayAmount;
//					String createTime = isEmpty(applyPay.get("create_time")) ? "" : applyPay.get("create_time").toString();
					String payTime = isEmpty(applyPay.get("pay_time")) ? "" : applyPay.get("pay_time").toString();
		
		//			int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
		//			int lastRowIndex = sheet.getLastRowNum();
		//			startrow = lastRowIndex;
					row = sheet.createRow((short) startrow + 1); 	 //在现有行号后追加数据
					row.createCell(0).setCellValue(payCode);		 //付款申请单编号
					row.createCell(1).setCellValue(sellerCode); 	 //商户编号
					row.createCell(2).setCellValue(sellerName);		 //商户名称
					row.createCell(3).setCellValue(waitPayAmount);   //等待付款的实付金额
					row.createCell(4).setCellValue(payTime);   		 //付款日期
					startrow++;
				}
				//合计行
				row = sheet.createRow((short) startrow + 1); 	 
				row.createCell(2).setCellValue("合计");		
				row.createCell(3).setCellValue(sumAmount);  
			}
//			wb.write(out);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + sdf.format(new Date()) + ".xls");
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
