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

/**
 * 商户结算3.0-导出交接明细
 * @author zht
 *
 */
public class ExportHandoverDetail extends RootExport {
	
	@Override
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String flag = request.getParameter("flag");
		//用不用只查询is_pay=1,此时wait_pay_amount由待付款金额变成实际付款金额
		String where = "";
		String sql = "select merchant_code, merchant_name, actual_pay_amount, "
				+ "period_money, wait_pay_amount, "
				+ "branch_name, joint_number, branch_address, bank_account "
				+ "from ordercenter.oc_bill_apply_payment a inner join usercenter.uc_seller_info_extend b "
				+ "where a.merchant_code =b.small_seller_code "; 
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
			where += "and a.flag ='" + flag + "' ";
		}
		//where += "group by a.merchant_code ";
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
			in = ExportApplyPayment.class.getResourceAsStream("/handoverDetail.xls");
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
				double sumActualPayAmount = 0.0;
				double sumPeriodMoney = 0.0;
				double sumWaitPayAmount = 0.0;
				for (Map<String, Object> applyPay : applyPayList) {
					//商户编号
					String sellerCode = applyPay.get("merchant_code").toString();
					//商户名称
					String sellerName = applyPay.get("merchant_name").toString();
					//开户银行支行名称
					String branchName = isEmpty(applyPay.get("branch_name")) ? "" : applyPay.get("branch_name").toString();
					//联行号
					String jointNumber = isEmpty(applyPay.get("joint_number")) ? "" : applyPay.get("joint_number").toString();
					//开户行支行所在地
					String branchAddress = isEmpty(applyPay.get("branch_address")) ? "" : applyPay.get("branch_address").toString();
					//银行账号
					String bankAccount = isEmpty(applyPay.get("bank_account")) ? "" : applyPay.get("bank_account").toString();
					
					//应付金额
					double actualPayAmount = Double.valueOf(isEmpty(applyPay.get("actual_pay_amount")) ? "0.00"
							: applyPay.get("actual_pay_amount").toString());
					//质保金
					double periodMoney = Double.valueOf(isEmpty(applyPay.get("period_money")) ? "0.00"
							: applyPay.get("period_money").toString());
					//实付金额
					double waitPayAmount = Double.valueOf(isEmpty(applyPay.get("wait_pay_amount")) ? "0.00"
							: applyPay.get("wait_pay_amount").toString());

					sumActualPayAmount += actualPayAmount;
					sumPeriodMoney += periodMoney;
					sumWaitPayAmount += waitPayAmount;
					
					row = sheet.createRow((short) ++startrow); 	 
					row.createCell(0).setCellValue(startrow); 	 		//序号
					row.createCell(1).setCellValue(sellerCode); 	 	//商户编号
					row.createCell(2).setCellValue(sellerName); 	 	//商户名称
					row.createCell(3).setCellValue(branchName); 	 	//开户银行支行名称
					row.createCell(4).setCellValue(bankAccount);		//银行账号
					row.createCell(5).setCellValue(actualPayAmount);   	//应付金额
					row.createCell(6).setCellValue(periodMoney);   		//质保金
					row.createCell(7).setCellValue(waitPayAmount);		//实付金额
					row.createCell(8).setCellValue(branchAddress);      //开户行支行所在地
					row.createCell(9).setCellValue("联行号:" + jointNumber);   //联行号
				}
				row = sheet.createRow((short) ++startrow); 	 	 
				row.createCell(0).setCellValue("合计");
				row.createCell(5).setCellValue(sumActualPayAmount);	 //累加应付金额
				row.createCell(6).setCellValue(sumPeriodMoney);   	 //累加质保金
				row.createCell(7).setCellValue(sumWaitPayAmount);    //累加实付金额
				row = sheet.createRow((short) ++startrow); 
				row.createCell(2).setCellValue("制单人：");
				row.createCell(4).setCellValue("审核人：");
				row.createCell(6).setCellValue("出纳：");
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" +
					java.net.URLEncoder.encode("交接明细", "UTF-8").replace("+", "") + ".xls");
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
