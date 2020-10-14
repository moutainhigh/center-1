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
 * 类: ExportRetentionMoneyOperateLog <br>
 * 描述: 导出质保金管理操作日志 <br>
 * 作者: zhy<br>
 * 时间: 2017年6月9日 上午11:30:36
 */
public class ExportRetentionMoneyOperateLog extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String small_seller_code = request.getParameter("small_seller_code");
		String type = request.getParameter("type");
		String sql = "select retention_money,operate_date,(select real_name from zapdata.za_userinfo where user_code=creator )creator,create_time,remark from logcenter.lc_retention_money where small_seller_code='" + small_seller_code
				+ "' and operate_type=" + type;
		try {
			operateLogToExcel(sql, Integer.valueOf(type), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void operateLogToExcel(String sql, Integer type, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			if (type == 0) {
				in = ExportApplyPayment.class.getResourceAsStream("/retention_money_receive.xls");
			} else {
				in = ExportApplyPayment.class.getResourceAsStream("/retention_money_adjust.xls");
			}

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
			List<Map<String, Object>> logs = DbUp.upTable("lc_retention_money").dataSqlList(sql, null);
			if (null != logs && logs.size() > 0) {
				for (Map<String, Object> log : logs) {
					row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
					double retention_money = Double.valueOf(
							isEmpty(log.get("retention_money")) ? "0.00" : log.get("retention_money").toString());
					if (type == 0) {
						row.createCell(0).setCellValue(retention_money); // 预收质保金
						row.createCell(1).setCellValue(log.get("operate_date").toString()); // 预收质保金时间
						row.createCell(2).setCellValue(log.get("creator").toString());// 维护人
						row.createCell(3).setCellValue(log.get("create_time").toString());// 维护时间
						row.createCell(4).setCellValue(log.get("remark").toString());// 备注
					} else {
						row.createCell(0).setCellValue(retention_money); // 调整质保金
						row.createCell(1).setCellValue(log.get("operate_date").toString()); // 调整质保金时间
						row.createCell(2).setCellValue(log.get("remark").toString());// 调整质保金原因
						row.createCell(3).setCellValue(log.get("creator").toString());// 维护人
						row.createCell(4).setCellValue(log.get("create_time").toString());// 维护时间
					}
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
