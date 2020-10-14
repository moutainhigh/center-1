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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * 
 * 类: ExportRetentionAdjustMoneyOperateLog <br>
 * 描述: 导出质保金管理操作日志 <br>
 */
public class ExportRetentionAdjustMoneyOperateLog extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String small_seller_code = request.getParameter("small_seller_code");
		
		String sql = "select adjust_money,adjust_time,adjust_reason,(select real_name from zapdata.za_userinfo where user_code=operator )operator from lc_retention_adjust_money where small_seller_code='"+small_seller_code+"' order by adjust_time desc";
		try {
			operateLogToExcel(sql, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void operateLogToExcel(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
		
			in = ExportApplyPayment.class.getResourceAsStream("/retention_money_adjust2.xls");
			

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
			List<Map<String, Object>> logs = DbUp.upTable("lc_retention_adjust_money").dataSqlList(sql, null);
			if (null != logs && logs.size() > 0) {
				for (Map<String, Object> log : logs) {
					row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
					double adjust_money = Double.valueOf(
						isEmpty(log.get("adjust_money")) ? "0.00" : log.get("adjust_money").toString());
					
						row.createCell(0).setCellValue(adjust_money); // 调整金额
						row.createCell(1).setCellValue(log.get("adjust_time").toString()); // 时间
						row.createCell(2).setCellValue(log.get("adjust_reason").toString());// 原因
						row.createCell(3).setCellValue(log.get("operator").toString());// 操作人员
				
				
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
