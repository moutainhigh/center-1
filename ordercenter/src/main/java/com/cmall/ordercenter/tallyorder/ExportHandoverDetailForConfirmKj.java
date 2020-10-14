package com.cmall.ordercenter.tallyorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class ExportHandoverDetailForConfirmKj extends RootExport {

	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String payTimeFrom = request.getParameter("payTimeFrom");
		String payTimeTo = request.getParameter("payTimeTo");

		String where = "";
		String sql = "select pay_code,merchant_code,merchant_name,branch_name,bank_account,branch_address,pay_time,actual_pay_amount,joint_number from ordercenter.oc_bill_apply_payment_kj a where a.flag='4497477900060006' and a.is_pay='4497477900020002' ";
		if (!StringUtils.isEmpty(payCode)) {
			where = "and a.pay_code like '%" + payCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerCode)) {
			where += "and a.merchant_code like '%" + sellerCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerName)) {
			where += "and a.merchant_name like '%" + sellerName + "%' ";
		}
		if (!StringUtils.isEmpty(payTimeFrom) && !StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' and a.pay_time <= '" + payTimeTo + "' ";
		} else if (!StringUtils.isEmpty(payTimeFrom)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time <= '" + payTimeTo + "' ";
		}
		// where += "group by a.merchant_code ";
		try {
			handOverToExcel(sql + where, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handOverToExcel(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			in = ExportApplyPayment.class.getResourceAsStream("/handoverDetail_kj.xls");
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
			List<Map<String, Object>> applyPayList = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlList(sql, null);
			if (null != applyPayList && applyPayList.size() > 0) {
				double sumActualPayAmount = 0.0;
				for (Map<String, Object> applyPay : applyPayList) {
					// 商户编号
					String sellerCode = applyPay.get("merchant_code").toString();
					// 商户名称
					String sellerName = applyPay.get("merchant_name").toString();
					// 开户银行支行名称
					String branchName = isEmpty(applyPay.get("branch_name")) ? ""
							: applyPay.get("branch_name").toString();
					// 联行号
					String jointNumber = isEmpty(applyPay.get("joint_number")) ? ""
							: applyPay.get("joint_number").toString();
					// 开户行支行所在地
					String branchAddress = isEmpty(applyPay.get("branch_address")) ? ""
							: applyPay.get("branch_address").toString();
					// 银行账号
					String bankAccount = isEmpty(applyPay.get("bank_account")) ? ""
							: applyPay.get("bank_account").toString();
					// 实付金额
					double actualPayAmount = Double.valueOf(isEmpty(applyPay.get("actual_pay_amount")) ? "0.00"
							: applyPay.get("actual_pay_amount").toString());
					sumActualPayAmount = sumActualPayAmount + actualPayAmount;

					row = sheet.createRow((short) ++startrow);
					row.createCell(0).setCellValue(sellerCode); // 商户编号
					row.createCell(1).setCellValue(sellerName); // 商户名称
					row.createCell(2).setCellValue(branchName); // 开户银行支行名称
					row.createCell(3).setCellValue(bankAccount); // 银行账号
					row.createCell(4).setCellValue(branchAddress); // 开户行支行所在地
					row.createCell(5).setCellValue(actualPayAmount); // 实付金额
					row.createCell(6).setCellValue("联行号:" + jointNumber); // 联行号
				}
				row = sheet.createRow((short) ++startrow);
				row.createCell(0).setCellValue("合计");
				row.createCell(5).setCellValue(sumActualPayAmount); // 实付金额
				row = sheet.createRow((short) ++startrow);
				row.createCell(2).setCellValue("制单人：");
				row.createCell(4).setCellValue("审核人：");
				row.createCell(6).setCellValue("出纳：");
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition",
					"attachment;filename=" + java.net.URLEncoder.encode("付款申请单", "UTF-8").replace("+", "") + ".xls");
			outputStream = response.getOutputStream();
			wb.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in)
				in.close();
			if (null != outputStream)
				outputStream.close();
		}
	}

	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
