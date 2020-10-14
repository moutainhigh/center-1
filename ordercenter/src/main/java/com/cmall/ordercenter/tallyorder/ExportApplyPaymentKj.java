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
 * 
 * 类: ExportApplyPaymentKj <br>
 * 描述: 跨境相关-导出付款申请单 <br>
 * 作者: zhy<br>
 * 时间: 2017年5月2日 下午3:40:34
 */
public class ExportApplyPaymentKj extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String flag = request.getParameter("flag");
		String settleCodes = request.getParameter("settleCodes");
		String settleType = request.getParameter("settleType");
		String payTimeFrom = request.getParameter("payTimeFrom");
		String payTimeTo = request.getParameter("payTimeTo");

		String where = "";
		String sql = "SELECT pay_code,merchant_code,merchant_name,period_collect_amount_total,service_fee,";
		sql += " payable_collect_amount,add_deduction,settle_collect_amount,period_money,actual_pay_amount,";
		sql += " settle_collect_amount,period_money,actual_pay_amount,create_time,pay_time";
		sql += " FROM oc_bill_apply_payment_kj as a where 1=1 ";
		if (!StringUtils.isEmpty(payCode)) {
			where = "and a.pay_code like '%" + payCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerCode)) {
			where += "and a.merchant_code like '%" + sellerCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerName)) {
			where += "and a.merchant_name like '%" + sellerName + "%' ";
		}
		if (!StringUtils.isEmpty(createTimeFrom) && !StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time >= '" + createTimeFrom + "' and a.create_time <= '" + createTimeTo + "' ";
		} else if (!StringUtils.isEmpty(createTimeFrom)) {
			where += "and a.create_time >= '" + createTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time <= '" + createTimeTo + "' ";
		}
		if (!StringUtils.isEmpty(flag)) {
			where += "and a.flag ='" + flag + "'";
		}
		if (!StringUtils.isEmpty(settleCodes)) {
			where += "and a.settle_codes='" + settleCodes + "'";
		}
		if (!StringUtils.isEmpty(settleType)) {
			where += "and a.settle_type='" + settleType + "'";
		}
		if (!StringUtils.isEmpty(payTimeFrom) && !StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' and a.pay_time <= '" + payTimeTo + "' ";
		} else if (!StringUtils.isEmpty(payTimeFrom)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time <= '" + payTimeTo + "' ";
		}
		try {
			applyPaymentToExcel(sql + where, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void applyPaymentToExcel(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			in = ExportApplyPaymentKj.class.getResourceAsStream("/applyPayment_kj.xls");
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
				for (Map<String, Object> applyPay : applyPayList) {
					String payCode = applyPay.get("pay_code").toString();
					String sellerCode = applyPay.get("merchant_code").toString();
					String sellerName = applyPay.get("merchant_name").toString();
					// 本期代收货款合计
					double periodCollectAmountTotal = Double
							.valueOf(isEmpty(applyPay.get("period_collect_amount_total")) ? "0.00"
									: applyPay.get("period_collect_amount_total").toString());
					// 平台服务费
					double serviceFee = Double.valueOf(
							isEmpty(applyPay.get("service_fee")) ? "0.00" : applyPay.get("service_fee").toString());
					// 应付代收货款
					double payableCollectAmount = Double.valueOf(isEmpty(applyPay.get("payable_collect_amount"))
							? "0.00" : applyPay.get("payable_collect_amount").toString());
					// 附加扣费合计
					double addDeduction = Double.valueOf(
							isEmpty(applyPay.get("add_deduction")) ? "0.00" : applyPay.get("add_deduction").toString());
					// 结算代收金额
					double settleCollectAmount = Double.valueOf(isEmpty(applyPay.get("settle_collect_amount")) ? "0.00"
							: applyPay.get("settle_collect_amount").toString());
					// 本期质保金
					double periodMoney = Double.valueOf(
							isEmpty(applyPay.get("period_money")) ? "0.00" : applyPay.get("period_money").toString());
					// 实付代收货款
					double actualPayAmount = Double.valueOf(isEmpty(applyPay.get("actual_pay_amount")) ? "0.00"
							: applyPay.get("actual_pay_amount").toString());
					String createTime = isEmpty(applyPay.get("create_time")) ? ""
							: applyPay.get("create_time").toString();
					String payTime = isEmpty(applyPay.get("pay_time")) ? "" : applyPay.get("pay_time").toString();

					row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
					row.createCell(0).setCellValue(payCode); // 付款申请单编号
					row.createCell(1).setCellValue(sellerCode); // 商户编号
					row.createCell(2).setCellValue(sellerName); // 商户名称
					row.createCell(3).setCellValue(periodCollectAmountTotal); // 本期代收货款合计
					row.createCell(4).setCellValue(serviceFee); // 平台服务费
					row.createCell(5).setCellValue(payableCollectAmount); // 应付代收货款
					row.createCell(6).setCellValue(addDeduction); // 附加扣费合计
					row.createCell(7).setCellValue(settleCollectAmount); // 结算代收金额
					row.createCell(8).setCellValue(periodMoney); // 本期质保金
					row.createCell(9).setCellValue(actualPayAmount);// 实付代收货款
					row.createCell(10).setCellValue(createTime); // 创建时间
					row.createCell(11).setCellValue(payTime); // 付款日期
					startrow++;
				}
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + sdf.format(new Date()) + ".xls");
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
