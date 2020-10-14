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

public class ExportInCheckSkuInfo extends RootExport {

	public static void getProductInfo(HttpServletResponse response, List<List<String>> pd) {
		String product_name = "";
		String cost_price = "";
		String tax_rate ="";
		String dlr_id = "";
		String dlr_nm = "";
		String settlement_type = "";
		String oa_site_no = "";
		String sku_code = "";
		String sell_productcode = "";
		String sell_price = "";
		String sku_name = "";
		String product_code="";
		String property_key="";
		String property_value="";
		String concatinfo="";
		String settle_name="";
		HSSFWorkbook wb = null;
		FileInputStream fs;
		FileOutputStream out = null;
		
		InputStream in = ExportInCheckSkuInfo.class.getResourceAsStream("/skuinfo.xls");
		//File file = new File("e:/yy1.xls");
		try {
			//fs = new FileInputStream(file);
			wb = new HSSFWorkbook(in);
			out = new FileOutputStream("/skuinfo.xls");
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
			// 查询商品详细表
			String sql = "select product_code,product_name,cost_price,tax_rate from pc_productinfo where product_code=:product_code";
			List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList(sql, map);
			for (Map<String, Object> map2 : list) {
				product_name = map2.get("product_name").toString();
				cost_price = map2.get("cost_price") == null ? "0.00" : map2.get("cost_price").toString();// 成本价
				tax_rate = String.valueOf(new Double((Double.valueOf(map2.get("tax_rate") == null ? "0.00" : map2.get("tax_rate").toString())*100)).intValue())+"%";// 税率
				mapinfo.put("product_name", product_name);
				mapinfo.put("cost_price", cost_price);
				mapinfo.put("tax_rate", tax_rate);
			}
			// 查询商品详细扩展表
			String productextsql = "select dlr_id,dlr_nm,settlement_type,oa_site_no from pc_productinfo_ext where product_code=:product_code";
			List<Map<String, Object>> listext = DbUp.upTable("pc_productinfo_ext").dataSqlList(productextsql, map);
			for (Map<String, Object> map3 : listext) {
				dlr_id = map3.get("dlr_id").toString();// 商户编号
				dlr_nm = map3.get("dlr_nm").toString();// 商户名称
				settlement_type = map3.get("settlement_type").toString();// 结算方式
				//取结算方式所对应的名称
				String sqlname="select define_name from sc_define where define_code= "+settlement_type;
				List<Map<String, Object>> listname = DbUp.upTable("sc_define").dataSqlList(sqlname, null);
				for (Map<String, Object> mapname : listname) {
					settle_name=mapname.get("define_name")==null?"":mapname.get("define_name").toString();
				}
				oa_site_no = map3.get("oa_site_no").toString();// 仓库编码
				mapinfo.put("dlr_id", dlr_id);
				mapinfo.put("dlr_nm", dlr_nm);
				mapinfo.put("settlement_type", settlement_type);
				mapinfo.put("oa_site_no", oa_site_no);
			}
			//获取组合信息
			map.put("property_type", "449736200004");
			
			String concatsql = "select property_key,property_value from pc_productproperty where product_code=:product_code and property_type=:property_type ";
			List<Map<String, Object>> concatlist = DbUp.upTable("pc_productproperty").dataSqlList(concatsql, map);
			StringBuffer sb=new StringBuffer();
			if(concatlist.size()>0){
				for (Map<String, Object> map3 : concatlist) {
					property_key=map3.get("property_key").toString();
					property_value=map3.get("property_value").toString();
					sb.append(property_key+"="+property_value+"&");
				}
				concatinfo=sb.toString().substring(0, sb.toString().length()-1);
			}else{
				concatinfo="";
			}
			// 取sku信息
			String skusql = "select sku_code,sell_productcode,cost_price,sku_name,sell_price from pc_skuinfo where product_code=:product_code";
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
				row.createCell(6);
				row.createCell(7);
				row.createCell(8);
				row.createCell(9);
				row.createCell(10);
				row.createCell(11);
				row.createCell(12);
			}
			// 四个参数分别是：起始行，起始列，结束行，结束列
			sheet.addMergedRegion(new Region(startrow + 1, (short) 0, startrow+ skusize, (short) 0));
			sheet.getRow(startrow + 1).getCell(0).setCellValue(product_code);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 1, startrow+ skusize, (short) 1));
			sheet.getRow(startrow + 1).getCell(1).setCellValue(product_name);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 5, startrow+ skusize, (short) 5));
			sheet.getRow(startrow + 1).getCell(5).setCellValue(concatinfo);
			sheet.getRow(startrow + 1).getCell(7).setCellValue(sell_price);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 8, startrow+ skusize, (short) 8));
			sheet.getRow(startrow + 1).getCell(8).setCellValue(tax_rate);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 9, startrow+ skusize, (short) 9));
			sheet.getRow(startrow + 1).getCell(9).setCellValue(dlr_id);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 10, startrow+ skusize, (short) 10));
			sheet.getRow(startrow + 1).getCell(10).setCellValue(dlr_nm);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 11, startrow+ skusize, (short) 11));
			sheet.getRow(startrow + 1).getCell(11).setCellValue(settle_name);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 12, startrow+ skusize, (short) 12));
			sheet.getRow(startrow + 1).getCell(12).setCellValue(oa_site_no);
			int columnNum1 = sheet.getRow(0).getPhysicalNumberOfCells();
			int lastRowIndex1 = sheet.getLastRowNum();
			//System.out.println("总行数--s---" + lastRowIndex1);
			//System.out.println("总列数--s-----" + columnNum1);
			int count = 0;
			for (Map<String, Object> map3 : skulist) 
			{
				sku_code = map3.get("sku_code").toString();// sku编号
				sku_name = map3.get("sku_name").toString();// sku名称
				sell_productcode = map3.get("sell_productcode").toString();// sku货号
				cost_price = map3.get("cost_price").toString();// 成本价
				sell_price = map3.get("sell_price").toString();// 售价
				sheet.getRow(startrow + 1 + count).getCell(2).setCellValue(sell_productcode);
				sheet.getRow(startrow + 1 + count).getCell(3).setCellValue(sku_code);
				sheet.getRow(startrow + 1 + count).getCell(4).setCellValue(sku_name);
				sheet.getRow(startrow + 1 + count).getCell(6).setCellValue(cost_price);
				sheet.getRow(startrow + 1 + count).getCell(7).setCellValue(sell_price);
				count++;
			}
		}
		try {
			//wb.write(out);
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			String excelName = "商户商品导出.xls";
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
		ExportInCheckSkuInfo.getProductInfo(response, pd);
		System.out.println("ok");
	}
}
