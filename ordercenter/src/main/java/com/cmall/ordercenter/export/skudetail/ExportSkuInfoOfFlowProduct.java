package com.cmall.ordercenter.export.skudetail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import com.cmall.productcenter.service.ProductLogoService;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

@SuppressWarnings("deprecation")
public class ExportSkuInfoOfFlowProduct extends RootExport {

	public void getProductInfo(HttpServletResponse response, List<List<String>> pd) {
		String product_name = "";
		String cost_price = "";
		String tax_rate ="";
		String dlr_id = "";
		String dlr_nm = "";
		String oa_site_no = "";
		String sku_code = "";
		String sell_price = "";
		String sku_name = "";
		String product_code="";
		String property_key="";
		String property_value="";
		String concatinfo="";
		String settle_name="";
		HSSFWorkbook wb = null;
		
		InputStream in = ExportSkuInfoOfFlowProduct.class.getResourceAsStream("/skuinfoFlow.xls");
		try {
			wb = new HSSFWorkbook(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row = sheet.getRow(0);
		
		/**
		 * 导出超时问题优化 2016-08-25
		 */
		//所有的商品编号,组成一个数组，方便拼接成字符串去数据库查询
		List<String> productCodeArr = new ArrayList<String>();
		for (List<String> p : pd) {
			productCodeArr.add(p.get(3).toString());
		}
		
		//商品基本信息
		Map<String,Map<String,Object>> productinfoMap = this.getProductinfo(productCodeArr);

		//商品sku规格信息
		Map<String,List<Map<String,Object>>> productSkuPropertyMap = this.getProductSkuPropertyMap(productCodeArr);

		//商品sku信息
		Map<String,List<Map<String,Object>>> productSkuinfoMap = this.getProductSkuMap(productCodeArr);
		
		//结算方式
		Map<String,String> defineMap = new ProductLogoService().getScDefineByparentCode("449747160011");
		
		for (List<String> p : pd) {
//			MDataMap mapinfo = new MDataMap();
			product_code = p.get(3).toString();
			
			Map<String, Object> productinfoM = productinfoMap.get(product_code) ;				//商品信息
			List<Map<String,Object>> productSkuPropertyM = productSkuPropertyMap.get(product_code);	//sku规格属性
			List<Map<String, Object>> skuinfoM = productSkuinfoMap.get(product_code);			//sku信息
			if (null == productinfoM) {
				continue;
			}
			product_name = productinfoM.get("product_name").toString();
			cost_price = productinfoM.get("cost_price") == null ? "0.00" : productinfoM.get("cost_price").toString();	// 成本价
			tax_rate = String.valueOf(new Double((Double.valueOf(productinfoM.get("tax_rate") == null ? "0.00" : productinfoM.get("tax_rate").toString())*100)).intValue())+"%";// 税率
			
			settle_name = defineMap.get(productinfoM.get("settlement_type").toString())==null?"":defineMap.get(productinfoM.get("settlement_type").toString());
			oa_site_no = productinfoM.get("oa_site_no").toString();	// 仓库编码
			dlr_id = productinfoM.get("dlr_id").toString();
			dlr_nm = productinfoM.get("dlr_nm").toString();
			//获取组合信息
			StringBuffer sb=new StringBuffer();
			if(null != productSkuPropertyM && productSkuPropertyM.size()>0){
				for (Map<String, Object> productSkuProperty : productSkuPropertyM) {
					property_key=productSkuProperty.get("property_key").toString();
					property_value=productSkuProperty.get("property_value").toString();
					sb.append(property_key+"="+property_value+"&");
				}
				concatinfo=sb.toString().substring(0, sb.toString().length()-1);
			}
			int skusize = 0;
			if (null == skuinfoM) {
				skuinfoM = new ArrayList<Map<String, Object>>();
				skusize = 1;
			}else{
				skusize = skuinfoM.size();
			}
			int lastRowIndex = sheet.getLastRowNum();
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
			}
			// 四个参数分别是：起始行，起始列，结束行，结束列
			sheet.addMergedRegion(new Region(startrow + 1, (short) 0, startrow+ skusize, (short) 0));
			sheet.getRow(startrow + 1).getCell(0).setCellValue(product_code);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 1, startrow+ skusize, (short) 1));
			sheet.getRow(startrow + 1).getCell(1).setCellValue(product_name);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 6, startrow+ skusize, (short) 6));
			sheet.getRow(startrow + 1).getCell(6).setCellValue(concatinfo);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 7, startrow+ skusize, (short) 7));
			sheet.getRow(startrow + 1).getCell(7).setCellValue(tax_rate);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 8, startrow+ skusize, (short) 8));
			sheet.getRow(startrow + 1).getCell(8).setCellValue(dlr_id);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 9, startrow+ skusize, (short) 9));
			sheet.getRow(startrow + 1).getCell(9).setCellValue(dlr_nm);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 10, startrow+ skusize, (short) 10));
			sheet.getRow(startrow + 1).getCell(10).setCellValue(settle_name);
			sheet.addMergedRegion(new Region(startrow + 1, (short) 11, startrow+ skusize, (short) 11));
			sheet.getRow(startrow + 1).getCell(11).setCellValue(oa_site_no);
			int count = 0;
			for (Map<String, Object> skuinfo : skuinfoM) {
				sku_code = skuinfo.get("sku_code").toString();// 商户编号
				sku_name = skuinfo.get("sku_name").toString();// 商户名称
				sell_price=skuinfo.get("sell_price").toString();// 销售价
				cost_price=skuinfo.get("cost_price").toString();// 成本价
				sheet.getRow(startrow + 1 + count).getCell(2).setCellValue(sku_code);
				sheet.getRow(startrow + 1 + count).getCell(3).setCellValue(sku_name);
				sheet.getRow(startrow + 1 + count).getCell(4).setCellValue(sell_price);
				sheet.getRow(startrow + 1 + count).getCell(5).setCellValue(cost_price);
				count++;
			}
		}
		try {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename="
					+ java.net.URLEncoder.encode("商户商品导出", "UTF-8") + ".xls");
			OutputStream ouputStream = response.getOutputStream();
			ouputStream.flush();
			wb.write(ouputStream);
			ouputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		exportExcel(sOperateId, request, response);
		MPageData pageData = getPageData();
		List<List<String>> pd = pageData.getPageData();
		this.getProductInfo(response, pd);
	}
	
	
	/**
	 * 获取商品基本信息
	 * @param productCodeArr
	 * @return
	 */
	private Map<String,Map<String,Object>> getProductinfo(List<String> productCodeArr){
		Map<String,Map<String,Object>> productinfoMap = new HashMap<String,Map<String,Object>>();
		if (null == productCodeArr || productCodeArr.size() == 0) {
			return productinfoMap;
		}
		String sql = "select pi.product_code,pi.product_name,pi.cost_price,pi.tax_rate,pie.dlr_id,pie.dlr_nm,pie.settlement_type,pie.oa_site_no "
				+ " from productcenter.pc_productinfo pi,productcenter.pc_productinfo_ext pie "
				+ " where pi.product_code=pie.product_code and pi.product_code in ('"+StringUtils.join(productCodeArr,"','")+"')";
		List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList(sql, null);
		
		for (Map<String, Object> productMap : list) {
			productinfoMap.put(productMap.get("product_code").toString(), productMap);
		}
		
		return productinfoMap;
	}
	
	/**
	 * 获取sku规格属性信息
	 * @param productCodeArr
	 * @return
	 */
	private Map<String,List<Map<String,Object>>> getProductSkuPropertyMap(List<String> productCodeArr){
		Map<String,List<Map<String,Object>>> productinfoMap = new HashMap<String,List<Map<String,Object>>>();
		if (null == productCodeArr || productCodeArr.size() == 0) {
			return productinfoMap;
		}
		String sql = "select product_code,property_key,property_value from pc_productproperty where property_type='449736200004' and product_code in ('"+StringUtils.join(productCodeArr,"','")+"')";
		List<Map<String, Object>> list = DbUp.upTable("pc_productproperty").dataSqlList(sql, null);
		for (Map<String, Object> productMap : list) {
			List<Map<String,Object>> productMapList = new ArrayList<Map<String,Object>>();
			if (productinfoMap.containsKey(productMap.get("product_code").toString())) {
				productMapList = productinfoMap.get(productMap.get("product_code").toString());
			}
			productMapList.add(productMap);
			productinfoMap.put(productMap.get("product_code").toString(), productMapList);
		}
		
		return productinfoMap;
	}
	/**
	 * 获取sku信息
	 * @param productCodeArr
	 * @return
	 */
	private Map<String,List<Map<String,Object>>> getProductSkuMap(List<String> productCodeArr){
		Map<String,List<Map<String,Object>>> productSkuMap = new HashMap<String,List<Map<String,Object>>>();
		if (null == productCodeArr || productCodeArr.size() == 0) {
			return productSkuMap;
		}
		String sql = "select product_code,sku_code,sku_name,sell_price,cost_price from pc_skuinfo where product_code in ('"+StringUtils.join(productCodeArr,"','")+"')";
		List<Map<String, Object>> list = DbUp.upTable("pc_skuinfo").dataSqlList(sql, null);
		for (Map<String, Object> skuMap : list) {
			List<Map<String,Object>> productMapList = new ArrayList<Map<String,Object>>();
			if (productSkuMap.containsKey(skuMap.get("product_code").toString())) {
				productMapList = productSkuMap.get(skuMap.get("product_code").toString());
			}
			productMapList.add(skuMap);
			productSkuMap.put(skuMap.get("product_code").toString(), productMapList);
		}
		
		return productSkuMap;
	}
}
