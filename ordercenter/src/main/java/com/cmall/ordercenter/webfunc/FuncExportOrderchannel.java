package com.cmall.ordercenter.webfunc;

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
 * 查询订单渠道导出
 * 
 * @author lzf
 * 
 */
public class FuncExportOrderchannel extends RootExport {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		 String starttime = request.getParameter("starttime");
		 String endtime = request.getParameter("endtime");

		String where = "";
		String sql = "Select daytime,order_ld,app_in_broadcast,wechat_in_broadcast,smg_in_broadcast,website_in_broadcast,"
				+ "tel_in_broadcast,not_in_broadcast,change_delay,smg_app_delay,"
				+ "app_app_delay,wechat_wechat_delay,app_wechat_addflow,smg_app_imm From lc_change_channel_stat where 1=1";
		if (!StringUtils.isEmpty(starttime)) {
			where = " and daytime>='" + starttime + "'";
		}
		if (!StringUtils.isEmpty(endtime)) {
			where += " and daytime <='" + endtime + "'";
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
		// FileOutputStream out = null;
		try {
			in = FuncExportOrderchannel.class.getResourceAsStream("/orderchannel.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);
			// out = new FileOutputStream(sdf.format(new Date()));

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
			int order_ld_count = 0;
			int app_in_broadcast_count = 0;
			int wechat_in_broadcast_count = 0;
			int smg_in_broadcast_count = 0;
			int website_in_broadcast_count = 0;
			int tel_in_broadcast_count = 0;
			int not_in_broadcast_count = 0;
			int change_delay_count = 0;
			int smg_app_delay_count = 0;
			int app_app_delay_count = 0;
			int wechat_wechat_delay_count = 0;
			int app_wechat_addflow_count = 0;
			int smg_app_imm_count = 0;
			List<Map<String, Object>> applyPayList = DbUp.upTable("lc_change_channel_stat").dataSqlList(sql, null);
			if (null != applyPayList && applyPayList.size() > 0) {
				for (Map<String, Object> applyPay : applyPayList) {
					String daytime = applyPay.get("daytime").toString();
					int order_ld = Integer.valueOf(applyPay.get("order_ld").toString());
					int app_in_broadcast = Integer.valueOf(applyPay.get("app_in_broadcast").toString());
					int wechat_in_broadcast = Integer.valueOf(applyPay.get("wechat_in_broadcast").toString());
					int smg_in_broadcast = Integer.valueOf(applyPay.get("smg_in_broadcast").toString());
					int website_in_broadcast = Integer.valueOf(applyPay.get("website_in_broadcast").toString());
					int tel_in_broadcast = Integer.valueOf(applyPay.get("tel_in_broadcast").toString());
					int not_in_broadcast = Integer.valueOf(applyPay.get("not_in_broadcast").toString());
					int change_delay = Integer.valueOf(applyPay.get("change_delay").toString());
					int smg_app_delay = Integer.valueOf(applyPay.get("smg_app_delay").toString());
					int app_app_delay = Integer.valueOf(applyPay.get("app_app_delay").toString());
					int wechat_wechat_delay = Integer.valueOf(applyPay.get("wechat_wechat_delay").toString());
					int app_wechat_addflow = Integer.valueOf(applyPay.get("app_wechat_addflow").toString());
					int smg_app_imm = Integer.valueOf(applyPay.get("smg_app_imm").toString());

					

					row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
					row.createCell(0).setCellValue(daytime);//时间
					row.createCell(1).setCellValue(order_ld); // 每日LD品下单总数
					row.createCell(2).setCellValue(app_in_broadcast); // app通路直播时段内下单数
					row.createCell(3).setCellValue(wechat_in_broadcast); // 微信通路直播时段内下单数
					row.createCell(4).setCellValue(smg_in_broadcast); // 扫码购通路直播时段内下单数
					row.createCell(5).setCellValue(website_in_broadcast); // 网站通路直播时段内下单数
					row.createCell(6).setCellValue(tel_in_broadcast); // 电话通路直播时段内下单数
					row.createCell(7).setCellValue(not_in_broadcast); // 非直播时段内订单
					row.createCell(8).setCellValue(change_delay); // 改变的订单数(包括改变通路和延迟同步两种情况)
					row.createCell(9).setCellValue(smg_app_delay); // smg转app并延迟同步单数
					row.createCell(10).setCellValue(app_app_delay); // app通路直播时段内延迟同步单数
					row.createCell(11).setCellValue(wechat_wechat_delay);//微信通路直播时段内延迟同步单数
					row.createCell(12).setCellValue(app_wechat_addflow);//app通路转微信通路补流量单数
					row.createCell(13).setCellValue(smg_app_imm);//smg转app并即时同步单数
					
					order_ld_count += order_ld;
					app_in_broadcast_count += app_in_broadcast;
					wechat_in_broadcast_count += wechat_in_broadcast;
					smg_in_broadcast_count += smg_in_broadcast;
					website_in_broadcast_count += website_in_broadcast;
					tel_in_broadcast_count +=  tel_in_broadcast;
					not_in_broadcast_count += not_in_broadcast;
					change_delay_count += change_delay;
					smg_app_delay_count += smg_app_delay;
					app_app_delay_count += app_app_delay;
					wechat_wechat_delay_count += wechat_wechat_delay;
					app_wechat_addflow_count +=  app_wechat_addflow;
					smg_app_imm_count += smg_app_imm;
					
					
					if (applyPayList.size()-1 == startrow){
						row = sheet.createRow((short) startrow + 2);
						row.createCell(0).setCellValue("合计"); // 合计
						row.createCell(1).setCellValue(order_ld_count);
						row.createCell(2).setCellValue(app_in_broadcast_count);
						row.createCell(3).setCellValue(wechat_in_broadcast_count);
						row.createCell(4).setCellValue(smg_in_broadcast_count);
						row.createCell(5).setCellValue(website_in_broadcast_count);
						row.createCell(6).setCellValue(tel_in_broadcast_count);
						row.createCell(7).setCellValue(not_in_broadcast_count);
						row.createCell(8).setCellValue(change_delay_count);
						row.createCell(9).setCellValue(smg_app_delay_count);
						row.createCell(10).setCellValue(app_app_delay_count);
						row.createCell(11).setCellValue(wechat_wechat_delay_count);
						row.createCell(12).setCellValue(app_wechat_addflow_count);
						row.createCell(13).setCellValue(smg_app_imm_count);
					}
					startrow++;

				}
			}
			
			// wb.write(out);
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
