package com.cmall.groupcenter.job;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 邮件给客服发送LD售后订单
 */
public class JobForAfterSaleLdNotify extends RootJob {
	
	public void doExecute(JobExecutionContext context) {
		String sNowDay = FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1),"yyyy-MM-dd");
		// 查询昨天的售后单
		List<MDataMap> itemList = DbUp.upTable("oc_after_sale_ld").queryAll("", "", "create_time like '"+sNowDay+"%' AND after_sale_status not in('04')", new MDataMap());
	
		List<MDataMap> dataList = new ArrayList<MDataMap>();
		MDataMap dataMap;
		String outOrderCode,productCode;
		for(MDataMap item : itemList) {
			outOrderCode = item.get("order_code");
			productCode = item.get("product_code");
			
			dataMap = new MDataMap();
			dataMap.put("afterSaleType", item.get("after_sale_type"));
			dataMap.put("reason", item.get("reason"));
			dataMap.put("remark", item.get("remark"));
			dataMap.put("outOrderCode", outOrderCode);
			dataMap.put("hjyOrderCode", StringUtils.trimToEmpty((String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "seller_code = 'SI2003' AND small_seller_code = 'SI2003' AND out_order_code = :outOrderCode", 
					new MDataMap("outOrderCode", outOrderCode))));
			
			dataMap.put("productCode", productCode);
			dataMap.put("productName", StringUtils.trimToEmpty((String)DbUp.upTable("pc_productinfo").dataGet("product_name", "seller_code = 'SI2003' AND small_seller_code = 'SI2003' AND product_code = :productCode", 
					new MDataMap("productCode", productCode))));
			
			dataMap.put("createTime", item.get("create_time"));
			dataList.add(dataMap);
		}
		
		File excelFile = null;
		String content = "申请售后数量： "+dataList.size();
		if(!dataList.isEmpty()) {
			excelFile = saveExcelFile(dataList);
		}
		
		String today = FormatHelper.upDateTime("yyyy-MM-dd");
		sendMail(today + "LD订单申请售后数据", content, excelFile);
		
		if(excelFile !=null && excelFile.exists()) {
			excelFile.delete();
		}
	}
	
	private File saveExcelFile(List<MDataMap> dataList) {
		HSSFWorkbook wkbook = new HSSFWorkbook();
		HSSFSheet sheet = wkbook.createSheet();
		// 创建标题
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("惠家有订单号");
		row.createCell(1).setCellValue("LD订单号");
		row.createCell(2).setCellValue("售后类型");
		row.createCell(3).setCellValue("原因");
		row.createCell(4).setCellValue("备注");
		row.createCell(5).setCellValue("商品编号");
		row.createCell(6).setCellValue("申请时间");
		row.createCell(7).setCellValue("商品名称");
		
		int rowNum = 1;
		for(MDataMap m : dataList) {
			row = sheet.createRow(rowNum);
			row.createCell(0).setCellValue(m.get("hjyOrderCode"));
			row.createCell(1).setCellValue(m.get("outOrderCode"));
			row.createCell(2).setCellValue(afterSaleTypeName(m.get("afterSaleType")));
			row.createCell(3).setCellValue(m.get("reason"));
			row.createCell(4).setCellValue(m.get("remark"));
			row.createCell(5).setCellValue(m.get("productCode"));
			row.createCell(6).setCellValue(m.get("createTime"));
			row.createCell(7).setCellValue(m.get("productName"));
			
			rowNum++;
		}
		
		String today = FormatHelper.upDateTime("yyyy-MM-dd");
		File saveFile = new File(TopConst.CONST_TOP_DIR_SERVLET + "/tmp/"+today+"-订单明细.xls");
		if(saveFile.exists()) saveFile.delete();
		if(!saveFile.getParentFile().exists()) {
			saveFile.getParentFile().mkdir();
		}
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			wkbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(out);
		}
		
		return saveFile;
	}
	
	private String afterSaleTypeName(String code) {
		if("1".equals(code)) {
			return "退货";
		} else if("2".equals(code)) {
			return "换货";
		} else {
			return "";
		}
	}
	
	private void sendMail(String title, String content, File excelFile) {
		String[] mails = bConfig("groupcenter.after_sale_ld_mail").split(",");
		for(String v : mails) {
			if(StringUtils.isNotBlank(v)) {
				MailSupport.INSTANCE.sendMail(StringUtils.trimToEmpty(v), title, content, excelFile == null ? null : new File[]{excelFile});
			}
		}
	}
	
}