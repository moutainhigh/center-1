package com.cmall.ordercenter.export.skudetail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.hssf.usermodel.HSSFRow;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

public class ExportInCheckSkuInfoCost extends RootExport {

	public static void getProductInfo(HttpServletResponse response, List<List<String>> pd) {
		String sku_code = "";
		String sku_name = "";
		String product_code="";
		String cost = "";
		String sell_productcode = "";
		HSSFWorkbook wb = null;
		FileInputStream fs;
		FileOutputStream out = null;
		
		InputStream in = ExportInCheckSkuInfoCost.class.getResourceAsStream("/skuinfocost.xls");
		//File file = new File("e:/yy1.xls");
		try {
			//fs = new FileInputStream(file);
			wb = new HSSFWorkbook(in);
			out = new FileOutputStream("/skuinfocost.xls");
		} catch (Exception e) {
			System.out.println("读取excel路劲有问题");
			e.printStackTrace();
		}
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row = sheet.getRow(0);
		for (List<String> p : pd) {
			MDataMap map = new MDataMap();
			MDataMap mapinfo = new MDataMap();
			product_code = p.get(0).toString();
			map.put("product_code", product_code);
			// 取sku信息
			String skusql = "select sku_code,cost_price,sku_name,sell_price,sell_productcode,stock_num from pc_skuinfo where product_code=:product_code";
			List<Map<String, Object>> skulist = DbUp.upTable("pc_skuinfo").dataSqlList(skusql, map);
			int skusize = skulist.size();
			int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
			//System.out.println("columnNum-------------------" + columnNum);
			int lastRowIndex = sheet.getLastRowNum();
			//System.out.println("lastRowIndex----------" + lastRowIndex);
			int startrow = lastRowIndex;
			for (int i = 1; i <= skusize; i++) {
				row = sheet.createRow((int) startrow + i); // 在现有行号后追加数据
				row.createCell(0);
				row.createCell(1);
				row.createCell(2);
				row.createCell(3);
				row.createCell(4);
				row.createCell(5);
			}
			int columnNum1 = sheet.getRow(0).getPhysicalNumberOfCells();
			int lastRowIndex1 = sheet.getLastRowNum();
			//System.out.println("总行数--s---" + lastRowIndex1);
			//System.out.println("总列数--s-----" + columnNum1);
			int count = 0;
			for (Map<String, Object> map3 : skulist) {
				sku_code = map3.get("sku_code").toString();// 商户编号
				sku_name = map3.get("sku_name").toString();// 商户名称
				sell_productcode = map3.get("sell_productcode").toString();// 货号
				cost = map3.get("cost_price").toString();//库存
				sheet.getRow(startrow + 1 + count).getCell(0).setCellValue(sell_productcode);
				sheet.getRow(startrow + 1 + count).getCell(1).setCellValue(product_code);
				sheet.getRow(startrow + 1 + count).getCell(2).setCellValue(sku_code);
				sheet.getRow(startrow + 1 + count).getCell(3).setCellValue(sku_name);
				sheet.getRow(startrow + 1 + count).getCell(4).setCellValue(cost);
				count++;
			}
		}
		try {
			//wb.write(out);
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			String excelName = "商户商品成本价导出.xls";
			response.setHeader("Content-disposition", "attachment;filename="+new String(excelName.getBytes("gb2312"), "ISO8859-1"));
			OutputStream ouputStream = response.getOutputStream();
			ouputStream.flush();
			wb.write(ouputStream);
			ouputStream.close();
			//out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// ExportSkuInfoOfProduct.getProductInfo("9132925");
		// System.out.println("ok");
		// ExportSkuInfoOfProduct.getProductInfo("9132924");
		// System.out.println("0k");
	}

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		exportExcel(sOperateId, request, response);
		MPageData pageData = getPageData();
		List<List<String>> pd = pageData.getPageData();
		//String product_code = "";
//		for (List<String> p : pd) {
//			product_code = p.get(0).toString();// 订单号
//			System.out.println(product_code);
//		}
		ExportInCheckSkuInfoCost.getProductInfo(response, pd);
		System.out.println("ok");
	}
}
