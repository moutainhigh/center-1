package com.cmall.ordercenter.tallyorder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

public class ExportProductDetailInfo extends RootExport{
	
	/**
	 * 导出结算单
	 */
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String uid = request.getParameter("zw_f_uid");
		//导出类型(1.有税额字段,HJY会计专用,商户看不到此字段)
		String type = request.getParameter("zw_f_type");
		
		Map<String, String> skumap = DbUp.upTable("oc_bill_merchant_new").oneWhere("uid,settle_type,settle_code,start_time,end_time,merchant_code,merchant_name", "","uid=:uid", "uid", uid);
		if(skumap != null) {
			//常规结算
			String start_time = skumap.get("start_time").toString();
			String end_time = skumap.get("end_time").toString();
			String small_seller_code = skumap.get("merchant_code").toString();
			String settle_code = skumap.get("settle_code").toString();
			String merchant_name = skumap.get("merchant_name").toString();
			String settle_type = skumap.get("settle_type").toString();
			String excelName=merchant_name.concat(settle_code);
			try {
				getProductInfo(settle_code, settle_type, type, start_time, end_time, small_seller_code, merchant_name, excelName, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//特殊结算
			skumap = DbUp.upTable("oc_bill_merchant_new_spec").oneWhere("uid,settle_type,settle_code,start_time,end_time,merchant_code,merchant_name", "","uid=:uid", "uid", uid);
			String start_time = skumap.get("start_time").toString();
			String end_time = skumap.get("end_time").toString();
			String small_seller_code = skumap.get("merchant_code").toString();
			String settle_code = skumap.get("settle_code").toString();
			String merchant_name = skumap.get("merchant_name").toString();
			String settle_type = skumap.get("settle_type").toString();
			String excelName=merchant_name.concat(settle_code);
			try {
				getProductInfo(settle_code, settle_type, type, start_time, end_time, small_seller_code, merchant_name, excelName, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void getProductInfo(String settle_code, String settle_type, String type, String start_time, String end_time,
			String small_seller_code, String merchant_name, String excelName, HttpServletResponse response) throws Exception{

		
		switch(settle_type) {
			case "4497477900040001":  //常规结算
				processStdExportProductDetail(settle_code, settle_type, type, start_time, end_time, small_seller_code, excelName,  response);
				break;
			case "4497477900040002":  //跨境保税
				processCrossBorderSellerExportProductDetail(settle_code, settle_type, start_time, end_time, small_seller_code, merchant_name, excelName, response);
				break;
			case "4497477900040003":  //跨境直邮
				processCrossBorderDirectMailExportProductDetail(settle_code, settle_type, start_time, end_time, small_seller_code, merchant_name, excelName, response);
				break;
			case "4497477900040004":  //平台入驻
				processPlatformExportProductDetail(settle_code, settle_type, start_time, end_time, small_seller_code, merchant_name, excelName, response);
				break;
		}
	}
	
	/**
	 * 导出常规结算某商户SKU级别明细数据
	 */
	public static void processStdExportProductDetail(String settle_code, String settle_type, String type, String start_time, String end_time,
			String small_seller_code,String excelName, HttpServletResponse response) {
		Map<String,String> userinfo=DbUp.upTable("uc_seller_info_extend").oneWhere("small_seller_code,account_line,"
				+ "bank_account,branch_name", "","small_seller_code=:small_seller_code", "small_seller_code",small_seller_code);
		String account_line="";
		String branch_name="";
		String bank_account="";
		if(userinfo!=null) {
			 account_line=userinfo.get("account_line").toString();//全称
			 branch_name=userinfo.get("branch_name").toString();//开户行
			 bank_account=userinfo.get("bank_account").toString();//账号
		}
		
		MDataMap map = new MDataMap();
//		map.put("start_time", start_time);
//		map.put("end_time", end_time); 
		map.put("settle_code", settle_code);
		map.put("small_seller_code", small_seller_code);
		
		double settle_count_sum = 0;
		double invoice_amount_sum=0;
		double period_money_sum=0;
		double sale_money_sum=0;
		double postage_sum=0;
		double manage_money_sum=0;
		double actual_pay_amount_sum=0;
		double other_sum=0;
		double tax_amount_sum=0;
		int no=1;
		
		InputStream in = null;
		if(!StringUtils.isEmpty(type) && type.equals("1")) {
			in = ExportProductDetailInfo.class.getResourceAsStream("/productInfoForAccounter.xls");
		} else {
			in = ExportProductDetailInfo.class.getResourceAsStream("/productInfo.xls");
		}
		
		HSSFWorkbook wb = null;
		 FileOutputStream out;
		try {
			wb = new HSSFWorkbook(in);
			out = new FileOutputStream("productInfo.xls");
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);//设置字体大小
		HSSFCellStyle cellStyle = wb.createCellStyle(); 
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFSheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(0);
		Row row1 ;
		int startrow=0;
		String sql = "select seller_name,small_seller_code,product_code,product_name,cost_price,sum(settle_count) as settle_count,sum(invoice_amount) as invoice_amount,"
				+ "sum(period_money) as period_money,sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
				+ "sum(others) as others,sum(actual_pay_amount) as actual_pay_amount from oc_bill_product_detail_new where "
				+ "settle_code=:settle_code and small_seller_code=:small_seller_code group by product_code,cost_price";
//				+ "start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by product_code,cost_price";
		if(!StringUtils.isEmpty(type) && type.equals("1")) {
			//公司会计专用
			sql = "select seller_name,small_seller_code,product_code,product_name,cost_price,sum(tax_amount) as tax_amount, sum(settle_count) as settle_count,sum(invoice_amount) as invoice_amount,"
					+ "sum(period_money) as period_money,sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
					+ "sum(others) as others,sum(actual_pay_amount) as actual_pay_amount from oc_bill_product_detail_new where "
					+ "settle_code=:settle_code and small_seller_code=:small_seller_code group by product_code,cost_price";
//					+ "start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by product_code,cost_price";
		}
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new")
				.dataSqlList(sql, map);

		String product_code="";
		for (Map<String, Object> productinfo : list) {
			product_code=productinfo.get("product_code").toString();
//			Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere("product_code_copy","", "product_code=:product_code", "product_code",product_code);
//			if(productMap!=null&&!productMap.isEmpty()&&!productMap.equals("")){
//				product_code=productMap.get("product_code_copy").toString();
//				if(product_code==null||product_code.equals("")){
//					product_code=productinfo.get("product_code").toString();
//				}
//			}
			String product_name=productinfo.get("product_name").toString();
//			String sku_code=productinfo.get("sku_code").toString();
//			String sku_name=productinfo.get("sku_name").toString();
			
			String seller_name=productinfo.get("seller_name").toString();
			String seller_code=productinfo.get("small_seller_code").toString();
			double cost_price=Double.valueOf(productinfo.get("cost_price")==null ? "0.00" : productinfo.get("cost_price").toString());
			double settle_count=Double.valueOf(productinfo.get("settle_count")==null ? "0.00" : productinfo.get("settle_count").toString());
			double tax_amount=Double.valueOf(productinfo.get("tax_amount")==null ? "0.00" : productinfo.get("tax_amount").toString());  //公司会计独看(税额)
			double invoice_amount=Double.valueOf(productinfo.get("invoice_amount")==null ? "0.00" : productinfo.get("invoice_amount").toString());
			double period_money=Double.valueOf(productinfo.get("period_money")==null ? "0.00" : productinfo.get("period_money").toString());
			double sale_money=Double.valueOf(productinfo.get("sale_money")==null ? "0.00" : productinfo.get("sale_money").toString());
			double postage=Double.valueOf(productinfo.get("postage")==null ? "0.00" : productinfo.get("postage").toString());
			double manage_money=Double.valueOf(productinfo.get("manage_money")==null ? "0.00" : productinfo.get("manage_money").toString());
			double others=Double.valueOf(productinfo.get("others")==null ? "0.00" : productinfo.get("others").toString());
			double actual_pay_amount=Double.valueOf(productinfo.get("actual_pay_amount")==null ? "0.00" : productinfo.get("actual_pay_amount").toString());
			settle_count_sum+=settle_count;
			invoice_amount_sum+=invoice_amount;
			period_money_sum+=period_money;
			sale_money_sum+=sale_money;
			postage_sum+=postage;
			manage_money_sum+=manage_money;
			actual_pay_amount_sum+=actual_pay_amount;
			other_sum+=others;
			tax_amount_sum+=tax_amount;
			
			sheet.getRow(2).getCell(0).setCellValue("结算时间("+start_time.substring(0, 10)+"至"+end_time.substring(0, 10)+") 常规商户结算帐单");
			sheet.getRow(3).getCell(1).setCellValue(seller_name);
			sheet.getRow(3).getCell(4).setCellValue(seller_code);
			 int columnNum=sheet.getRow(0).getPhysicalNumberOfCells();
		     int lastRowIndex = sheet.getLastRowNum();
		     // System.out.println("总行数-----"+lastRowIndex);
		      //System.out.println("总列数-------"+columnNum);
		        startrow=lastRowIndex;
		        row=sheet.createRow((short)startrow+1); //在现有行号后追加数据  
		        row.createCell(0).setCellValue(no);
		        no++;
		        row.createCell(1).setCellValue(product_code); //设置第二个（从0开始）单元格的数据   
		        row.createCell(2).setCellValue(product_name);
		        row.createCell(3).setCellValue(cost_price);
		        row.createCell(4).setCellValue(settle_count);
		        row.createCell(5).setCellValue(invoice_amount);
		        if(!StringUtils.isEmpty(type) && type.equals("1")) {
		        	//公司会计专用(税额)
			        row.createCell(6).setCellValue(tax_amount);
			        row.createCell(7).setCellValue(period_money);
			        row.createCell(8).setCellValue(sale_money);
			        row.createCell(9).setCellValue(postage);
			        row.createCell(10).setCellValue(manage_money);//商品合同签署
			        row.createCell(11).setCellValue(others);//商品调编
			        row.createCell(12).setCellValue(actual_pay_amount);		        	
		        } else {
			        row.createCell(6).setCellValue(period_money);
			        row.createCell(7).setCellValue(sale_money);
			        row.createCell(8).setCellValue(postage);
			        row.createCell(9).setCellValue(manage_money);//商品合同签署
			        row.createCell(10).setCellValue(others);//商品调编
			        row.createCell(11).setCellValue(actual_pay_amount);			        	
		        }
		}
		row=sheet.createRow((short)startrow+2);
		row.createCell(0).setCellValue("合计");
		row.getCell(0).setCellStyle(cellStyle);
		row.createCell(4).setCellValue(settle_count_sum);
        row.createCell(5).setCellValue(invoice_amount_sum);
        if(!StringUtils.isEmpty(type) && type.equals("1")) {
        	row.createCell(6).setCellValue(tax_amount_sum);
            row.createCell(7).setCellValue(period_money_sum);
            row.createCell(8).setCellValue(sale_money_sum);
            row.createCell(9).setCellValue(postage_sum);
            row.createCell(10).setCellValue(manage_money_sum);//商品合同签署
            row.createCell(11).setCellValue(other_sum);//商品调编
            row.createCell(12).setCellValue(actual_pay_amount_sum);        	
        	
        } else {
            row.createCell(6).setCellValue(period_money_sum);
            row.createCell(7).setCellValue(sale_money_sum);
            row.createCell(8).setCellValue(postage_sum);
            row.createCell(9).setCellValue(manage_money_sum);//商品合同签署
            row.createCell(10).setCellValue(other_sum);//商品调编
            row.createCell(11).setCellValue(actual_pay_amount_sum);
        }
        

        row=sheet.createRow((short)startrow+3);
        sheet.addMergedRegion(new Region(startrow+3, (short) 0, startrow+3, (short) 12));
        row.createCell(0).setCellValue("开票信息：");
        row.getCell(0).setCellStyle(cellStyle);
        row1=sheet.createRow((short)startrow+4);
        sheet.addMergedRegion(new Region(startrow+4, (short) 0, startrow+4, (short) 5));
        row1.createCell(0).setCellValue("全   称："+TopConfig.Instance.bConfig("ordercenter.bill_export_company_name"));
        row1.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+5);
        sheet.addMergedRegion(new Region(startrow+5, (short) 0, startrow+5, (short) 5));
        row.createCell(0).setCellValue("开户行："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_address"));
        row.getCell(0).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+4, (short) 6, startrow+5, (short) 12));
        row1.createCell(6).setCellValue("供应商汇款信息确认：(以下账号信息如有变更请及时反馈,未反馈视为信息无误)");
        row1.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+6);
        sheet.addMergedRegion(new Region(startrow+6, (short) 0, startrow+6, (short) 5));
        row.createCell(0).setCellValue("账   号："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_account"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+7);
        sheet.addMergedRegion(new Region(startrow+7, (short) 0, startrow+7, (short) 5));
        row.createCell(0).setCellValue("注册地址："+TopConfig.Instance.bConfig("ordercenter.bill_export_register_address"));
        row.createCell(6).setCellValue("全   称：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+7, (short) 7, startrow+7, (short) 12));
        row.createCell(7).setCellValue(account_line);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+8);
        sheet.addMergedRegion(new Region(startrow+8, (short) 0, startrow+8, (short) 5));
        row.createCell(0).setCellValue("税务登记证号："+TopConfig.Instance.bConfig("ordercenter.bill_export_tax_registration_number"));
        row.createCell(6).setCellValue("开户行：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+8, (short) 7, startrow+8, (short) 12));
        row.createCell(7).setCellValue(branch_name);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+9);
        sheet.addMergedRegion(new Region(startrow+9, (short) 0, startrow+9, (short) 5));
        row.createCell(0).setCellValue("电   话："+TopConfig.Instance.bConfig("ordercenter.bill_export_telphone"));
        row.createCell(6).setCellValue("账   号：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+9, (short) 7, startrow+9, (short) 12));
        row.createCell(7).setCellValue(bank_account);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+10);
        sheet.addMergedRegion(new Region(startrow+10, (short) 0, startrow+10, (short) 12));
        row.createCell(0).setCellValue("请确认金额及汇款信息,并将发票与结算表于当月15日前邮寄到:"+TopConfig.Instance.bConfig("ordercenter.bill_export_company_address"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+11);
        sheet.addMergedRegion(new Region(startrow+11, (short) 0, startrow+11, (short) 5));
        row.createCell(0).setCellValue("商品部：");
        row.createCell(6).setCellValue("商品行政：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+12);
        sheet.addMergedRegion(new Region(startrow+12, (short) 0, startrow+12, (short) 12));
        row.createCell(0).setCellValue("以下由惠家有提交用款申请时填报");
        row.getCell(0).setCellStyle(cellStyle);
        
        row=sheet.createRow((short)startrow+13);
        sheet.addMergedRegion(new Region(startrow+13, (short) 0, startrow+13, (short) 1));
        sheet.addMergedRegion(new Region(startrow+13, (short) 4, startrow+13, (short) 9));
        sheet.addMergedRegion(new Region(startrow+13, (short) 11, startrow+13, (short) 12));
        row.createCell(0).setCellValue("提交付款日期");
        row.createCell(3).setCellValue("发票号码");
        row.createCell(10).setCellValue("质保金");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+14);
        sheet.addMergedRegion(new Region(startrow+14, (short) 0, startrow+14, (short) 1));
        sheet.addMergedRegion(new Region(startrow+14, (short) 4, startrow+14, (short) 6));
        sheet.addMergedRegion(new Region(startrow+14, (short) 8, startrow+14, (short) 9));
        row.createCell(0).setCellValue("商品部");
        row.createCell(3).setCellValue("财务中心");
        row.createCell(7).setCellValue("财务总监");
        row.createCell(10).setCellValue("实付金额");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(7).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+15);
        sheet.addMergedRegion(new Region(startrow+15, (short) 0, startrow+15, (short) 1));
        sheet.addMergedRegion(new Region(startrow+15, (short) 2, startrow+15, (short) 12));
        row.createCell(0).setCellValue("特殊事项说明");
        row.getCell(0).setCellStyle(cellStyle);
        
		try {
			wb.write(out);
			out.close();
			response.setContentType("application/vnd.ms-excel");    
	        //response.setHeader("Content-disposition", "attachment;filename="+df.format(new Date()).toString()+".xls");  
	        response.setHeader("Content-disposition", "attachment;filename="+java.net.URLEncoder.encode(excelName, "UTF-8").replace("+", "")+".xls"); 
	        OutputStream ouputStream = response.getOutputStream();  
	        ouputStream.flush();   
	        wb.write(ouputStream);    
	        ouputStream.close();
	        removeRow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void removeRow() throws Exception {  
//		    File file = new File("d:/aa.xls");
//	        FileInputStream fs=new FileInputStream(file);  //获取d://test.xls 
			InputStream in = ExportProductDetailInfo.class.getResourceAsStream("/productInfo.xls");
	        HSSFWorkbook wb=new HSSFWorkbook(in); 
	        Sheet sheet1 = wb.getSheetAt(0);
	        int lastRowIndex = sheet1.getLastRowNum(); 
	        for (int i = 5; i <= lastRowIndex; i++) {
				HSSFRow removingRow = (HSSFRow) sheet1.getRow(i);
				sheet1.removeRow(removingRow);
			}
	        FileOutputStream os = new FileOutputStream("/productInfo.xls");
			wb.write(os);
			os.close();
	}
	
	/**
	 * 导出跨境保税结算某商户SKU级别明细数据
	 */
	public static void processCrossBorderSellerExportProductDetail(String settle_code, String settle_type, String start_time, String end_time,
			String small_seller_code, String merchant_name, String excelName, HttpServletResponse response) {
		Map<String,String> userinfo=DbUp.upTable("uc_seller_info_extend").oneWhere("small_seller_code,account_line,"
				+ "bank_account,branch_name", "","small_seller_code=:small_seller_code", "small_seller_code",small_seller_code);
		String account_line="";
		String branch_name="";
		String bank_account="";
		if(userinfo!=null) {
			 account_line=userinfo.get("account_line").toString();//全称
			 branch_name=userinfo.get("branch_name").toString();//开户行
			 bank_account=userinfo.get("bank_account").toString();//账号
		}
		
		MDataMap map = new MDataMap();
		map.put("settle_code", settle_code);
		map.put("small_seller_code", small_seller_code);

		InputStream in = ExportProductDetailInfo.class.getResourceAsStream("/productInfoCrossBorder.xls");
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);//设置字体大小
		HSSFCellStyle cellStyle = wb.createCellStyle(); 
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFSheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(0);
		sheet.getRow(2).getCell(0).setCellValue("结算时间("+start_time.substring(0, 10)+"至"+end_time.substring(0, 10)+") 跨境商户对帐单");
		sheet.getRow(3).getCell(1).setCellValue(merchant_name);
		sheet.getRow(3).getCell(6).setCellValue(small_seller_code);
		
		Row row1 ;
		int startrow=0;
		String sql = "select seller_name,small_seller_code,product_code,product_name,sku_code,sku_name,sell_price,cost_price,"
				+ "sum(settle_count) as settle_count,sum(invoice_amount) as invoice_amount,"
				+ "sum(service_fee) as service_fee,sum(payable_collect_amount) as payable_collect_amount,sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
				+ "sum(others) as others,sum(add_amount) as add_amount,"
				+ "sum(settle_collect_amount) as settle_collect_amount," //结算代收货款
				+ "sum(period_money) as period_money,"
				+ "sum(actual_pay_amount) as actual_pay_amount " //实付代收货款
				+ "from oc_bill_product_detail_new where "
				+ "settle_code=:settle_code and small_seller_code=:small_seller_code group by product_code,sku_code,sell_price,cost_price";

//				+ "start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by product_code,sell_price";

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new").dataSqlList(sql, map);
		
		double settle_count_sum = 0.0;
		double invoice_amount_sum = 0.0;
		double service_fee_sum = 0.0;
		double payable_collect_amount_sum = 0.0;  //应付代收货款		
		double sale_money_sum = 0.0;
		double postage_sum = 0.0;
		double manage_money_sum = 0.0;
		double other_sum = 0.0;
		double add_amount_sum = 0.0;
		double settle_collect_amount_sum=0;  //结算代收货款	
		double period_money_sum=0; 
		double actual_pay_amount_sum=0;
		
		int no=1;
		for (Map<String, Object> productinfo : list) {
			String product_code = productinfo.get("product_code").toString();
			String product_name = productinfo.get("product_name").toString();
			String sku_code = productinfo.get("sku_code").toString();
			String sku_name = productinfo.get("sku_name").toString();			
//			String seller_name = productinfo.get("seller_name").toString();
//			String seller_code = productinfo.get("small_seller_code").toString();
			double sell_price = Double.valueOf(productinfo.get("sell_price")==null ? "0.00" : productinfo.get("sell_price").toString());
			double cost_price = Double.valueOf(productinfo.get("cost_price")==null ? "0.00" : productinfo.get("cost_price").toString());
			double settle_count = Double.valueOf(productinfo.get("settle_count")==null ? "0.00" : productinfo.get("settle_count").toString());
			settle_count_sum += settle_count;
			
			double invoice_amount = Double.valueOf(productinfo.get("invoice_amount")==null ? "0.00" : productinfo.get("invoice_amount").toString());
			invoice_amount_sum += invoice_amount;
			
			double service_fee = Double.valueOf(productinfo.get("service_fee")==null ? "0.00" : productinfo.get("service_fee").toString());
			service_fee_sum += service_fee;
			
			//应付代收货款
			double payable_collect_amount = Double.valueOf(productinfo.get("payable_collect_amount")==null ? "0.00" : productinfo.get("payable_collect_amount").toString());
			payable_collect_amount_sum += payable_collect_amount;			
			
			
			double sale_money = Double.valueOf(productinfo.get("sale_money")==null ? "0.00" : productinfo.get("sale_money").toString());
			sale_money_sum += sale_money;
			
			double postage=Double.valueOf(productinfo.get("postage")==null ? "0.00" : productinfo.get("postage").toString());
			postage_sum += postage;
			
			double manage_money = Double.valueOf(productinfo.get("manage_money")==null ? "0.00" : productinfo.get("manage_money").toString());
			manage_money_sum += manage_money;
			
			double others=Double.valueOf(productinfo.get("others")==null ? "0.00" : productinfo.get("others").toString());
			other_sum += others;
			
			double add_amount=Double.valueOf(productinfo.get("add_amount")==null ? "0.00" : productinfo.get("add_amount").toString());
			add_amount_sum += add_amount;
			
			//结算代收货款
			double settle_collect_amount = Double.valueOf(productinfo.get("settle_collect_amount")==null ? "0.00" : productinfo.get("settle_collect_amount").toString());
			settle_collect_amount_sum += settle_collect_amount;
			
			double period_money = Double.valueOf(productinfo.get("period_money")==null ? "0.00" : productinfo.get("period_money").toString());
			period_money_sum += period_money;
			
			//实付代收货款
			double actual_pay_amount = Double.valueOf(productinfo.get("actual_pay_amount")==null ? "0.00" : productinfo.get("actual_pay_amount").toString());
			actual_pay_amount_sum += actual_pay_amount;
			
		    int lastRowIndex = sheet.getLastRowNum();

		    row = sheet.createRow((short)lastRowIndex + 1); //在现有行号后追加数据  
	        row.createCell(0).setCellValue(no);no++;		//序号
	        row.createCell(1).setCellValue(product_code); 	   
	        row.createCell(2).setCellValue(product_name);
	        row.createCell(3).setCellValue(sku_code); 	   
	        row.createCell(4).setCellValue(sku_name);
	        row.createCell(5).setCellValue(sell_price);
	        row.createCell(6).setCellValue(cost_price);
	        
	        row.createCell(7).setCellValue(settle_count);
	        row.createCell(8).setCellValue(invoice_amount);          //本期代收货款合计
	        row.createCell(9).setCellValue(service_fee);	 		 //服务费
	        row.createCell(10).setCellValue(payable_collect_amount); //应付代收货款
	        row.createCell(11).setCellValue(sale_money);			 //促销费
	        row.createCell(12).setCellValue(postage);				//邮费
	        row.createCell(13).setCellValue(manage_money);			//平台管理费
	        row.createCell(14).setCellValue(others);				//其他
	        row.createCell(15).setCellValue(add_amount);			//附加扣费合计
	        row.createCell(16).setCellValue(settle_collect_amount);	//结算代收货款
	        row.createCell(17).setCellValue(period_money);			//本期质保金
	        row.createCell(18).setCellValue(actual_pay_amount);		//实付代收货款 						
		}
		
		//合计行
		int lastRowIndex = sheet.getLastRowNum();
		row=sheet.createRow((short) lastRowIndex + 1);
		row.createCell(0).setCellValue("合计");
        row.createCell(7).setCellValue(settle_count_sum);
        row.createCell(8).setCellValue(invoice_amount_sum);
        row.createCell(9).setCellValue(service_fee_sum);				//服务费合计
        row.createCell(10).setCellValue(payable_collect_amount_sum); 	//应付代收合计
        row.createCell(11).setCellValue(sale_money_sum);				//促销费合计
        row.createCell(12).setCellValue(postage_sum);					//邮费合计
        row.createCell(13).setCellValue(manage_money_sum);				//平台管理费合计
        row.createCell(14).setCellValue(other_sum);						//其他合计
        row.createCell(15).setCellValue(add_amount_sum);				//附加扣费合计
        row.createCell(16).setCellValue(settle_collect_amount_sum);		//附加扣费合计
        row.createCell(17).setCellValue(period_money_sum);				//附加扣费合计
        row.createCell(18).setCellValue(actual_pay_amount_sum);   		//实际付款金额

        startrow = lastRowIndex + 1;
        
        row=sheet.createRow((short) startrow + 3);
        sheet.addMergedRegion(new Region(startrow+3, (short) 0, startrow+3, (short) 12));
        row.createCell(0).setCellValue("开票信息：");
        row.getCell(0).setCellStyle(cellStyle);
        row1=sheet.createRow((short)startrow+4);
        sheet.addMergedRegion(new Region(startrow+4, (short) 0, startrow+4, (short) 5));
        row1.createCell(0).setCellValue("全   称："+TopConfig.Instance.bConfig("ordercenter.bill_export_company_name"));
        row1.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+5);
        sheet.addMergedRegion(new Region(startrow+5, (short) 0, startrow+5, (short) 5));
        row.createCell(0).setCellValue("开户行："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_address"));
        row.getCell(0).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+4, (short) 6, startrow+5, (short) 12));
        row1.createCell(6).setCellValue("供应商汇款信息确认：(以下账号信息如有变更请及时反馈,未反馈视为信息无误)");
        row1.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+6);
        sheet.addMergedRegion(new Region(startrow+6, (short) 0, startrow+6, (short) 5));
        row.createCell(0).setCellValue("账   号："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_account"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+7);
        sheet.addMergedRegion(new Region(startrow+7, (short) 0, startrow+7, (short) 5));
        row.createCell(0).setCellValue("注册地址："+TopConfig.Instance.bConfig("ordercenter.bill_export_register_address"));
        row.createCell(6).setCellValue("全   称：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+7, (short) 7, startrow+7, (short) 12));
        row.createCell(7).setCellValue(account_line);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+8);
        sheet.addMergedRegion(new Region(startrow+8, (short) 0, startrow+8, (short) 5));
        row.createCell(0).setCellValue("税务登记证号："+TopConfig.Instance.bConfig("ordercenter.bill_export_tax_registration_number"));
        row.createCell(6).setCellValue("开户行：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+8, (short) 7, startrow+8, (short) 12));
        row.createCell(7).setCellValue(branch_name);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+9);
        sheet.addMergedRegion(new Region(startrow+9, (short) 0, startrow+9, (short) 5));
        row.createCell(0).setCellValue("电   话："+TopConfig.Instance.bConfig("ordercenter.bill_export_telphone"));
        row.createCell(6).setCellValue("账   号：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+9, (short) 7, startrow+9, (short) 12));
        row.createCell(7).setCellValue(bank_account);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+10);
        sheet.addMergedRegion(new Region(startrow+10, (short) 0, startrow+10, (short) 12));
        row.createCell(0).setCellValue("请确认金额及汇款信息,并将发票与结算表于当月15日前邮寄到:"+TopConfig.Instance.bConfig("ordercenter.bill_export_company_address"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+11);
        sheet.addMergedRegion(new Region(startrow+11, (short) 0, startrow+11, (short) 5));
        row.createCell(0).setCellValue("商品部：");
        row.createCell(6).setCellValue("商品行政：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+12);
        sheet.addMergedRegion(new Region(startrow+12, (short) 0, startrow+12, (short) 12));
        row.createCell(0).setCellValue("以下由惠家有提交用款申请时填报");
        row.getCell(0).setCellStyle(cellStyle);
        
        row=sheet.createRow((short)startrow+13);
        sheet.addMergedRegion(new Region(startrow+13, (short) 0, startrow+13, (short) 1));
        sheet.addMergedRegion(new Region(startrow+13, (short) 4, startrow+13, (short) 9));
        sheet.addMergedRegion(new Region(startrow+13, (short) 11, startrow+13, (short) 12));
        row.createCell(0).setCellValue("提交付款日期");
        row.createCell(3).setCellValue("发票号码");
        row.createCell(10).setCellValue("质保金");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+14);
        sheet.addMergedRegion(new Region(startrow+14, (short) 0, startrow+14, (short) 1));
        sheet.addMergedRegion(new Region(startrow+14, (short) 4, startrow+14, (short) 6));
        sheet.addMergedRegion(new Region(startrow+14, (short) 8, startrow+14, (short) 9));
        row.createCell(0).setCellValue("商品部");
        row.createCell(3).setCellValue("财务中心");
        row.createCell(7).setCellValue("财务总监");
        row.createCell(10).setCellValue("实付金额");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(7).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+15);
        sheet.addMergedRegion(new Region(startrow+15, (short) 0, startrow+15, (short) 1));
        sheet.addMergedRegion(new Region(startrow+15, (short) 2, startrow+15, (short) 12));
        row.createCell(0).setCellValue("特殊事项说明");
        row.getCell(0).setCellStyle(cellStyle);
        
        OutputStream ouputStream = null;
		try {
			response.setContentType("application/vnd.ms-excel");    
	        response.setHeader("Content-disposition", "attachment;filename="+java.net.URLEncoder.encode(excelName, "UTF-8").replace("+", "")+".xls"); 
	        ouputStream = response.getOutputStream();  
	        ouputStream.flush();   
	        wb.write(ouputStream);    
	       // removeRow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(null != ouputStream)	try { ouputStream.close(); } catch (IOException e) {}
			if(null != in) try { in.close(); } catch (IOException e) {}
		}
	}
	
	/**
	 * 导出跨境直邮结算某商户SKU级别明细数据
	 */
	public static void processCrossBorderDirectMailExportProductDetail(String settle_code, String settle_type, String start_time, String end_time,
			String small_seller_code, String merchant_name, String excelName, HttpServletResponse response) {
		Map<String,String> userinfo=DbUp.upTable("uc_seller_info_extend").oneWhere("small_seller_code,account_line,"
				+ "bank_account,branch_name", "","small_seller_code=:small_seller_code", "small_seller_code",small_seller_code);
		String account_line="";
		String branch_name="";
		String bank_account="";
		if(userinfo!=null) {
			 account_line=userinfo.get("account_line").toString();//全称
			 branch_name=userinfo.get("branch_name").toString();//开户行
			 bank_account=userinfo.get("bank_account").toString();//账号
		}
		
		MDataMap map = new MDataMap();
		map.put("settle_code", settle_code);
		map.put("small_seller_code", small_seller_code);

		InputStream in = ExportProductDetailInfo.class.getResourceAsStream("/productInfoCrossBorderDirectMail.xls");
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);//设置字体大小
		HSSFCellStyle cellStyle = wb.createCellStyle(); 
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFSheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(0);
		sheet.getRow(2).getCell(0).setCellValue("结算时间("+start_time.substring(0, 10)+"至"+end_time.substring(0, 10)+") 跨境直邮对帐单");
		sheet.getRow(3).getCell(1).setCellValue(merchant_name);
		sheet.getRow(3).getCell(5).setCellValue(small_seller_code);
		
		Row row1 ;
		int startrow=0;
		String sql = "select seller_name,small_seller_code,product_code,product_name,sku_code,sku_name,sell_price,cost_price,"
				+ "sum(settle_count) as settle_count,sum(invoice_amount) as invoice_amount,"
				+ "sum(service_fee) as service_fee,sum(payable_collect_amount) as payable_collect_amount,sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
				+ "sum(others) as others,sum(add_amount) as add_amount,"
				+ "sum(settle_collect_amount) as settle_collect_amount," //结算代收货款
				+ "sum(period_money) as period_money,"
				+ "sum(actual_pay_amount) as actual_pay_amount " //实付代收货款
				+ "from oc_bill_product_detail_new where "
				+ "settle_code=:settle_code and small_seller_code=:small_seller_code group by product_code,sku_code,sell_price,cost_price";
//				+ "start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by product_code,sell_price";

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new").dataSqlList(sql, map);
		
		double settle_count_sum = 0.0;
		double invoice_amount_sum = 0.0;
		double service_fee_sum = 0.0;
		double payable_collect_amount_sum = 0.0;  //应付代收货款
		double sale_money_sum = 0.0;
		double postage_sum = 0.0;
		double manage_money_sum = 0.0;
		double other_sum = 0.0;
		double add_amount_sum = 0.0;
		double settle_collect_amount_sum=0;  //结算代收货款
		double period_money_sum=0; 
		double actual_pay_amount_sum=0;  //实付代收货款
		
		int no=1;
		for (Map<String, Object> productinfo : list) {
			String product_code = productinfo.get("product_code").toString();
			String product_name = productinfo.get("product_name").toString();
			String sku_code = productinfo.get("sku_code").toString();
			String sku_name = productinfo.get("sku_name").toString();
//			String seller_name = productinfo.get("seller_name").toString();
//			String seller_code = productinfo.get("small_seller_code").toString();
			double sell_price = Double.valueOf(productinfo.get("sell_price")==null ? "0.00" : productinfo.get("sell_price").toString());
			double cost_price = Double.valueOf(productinfo.get("cost_price")==null ? "0.00" : productinfo.get("cost_price").toString());
			double settle_count = Double.valueOf(productinfo.get("settle_count")==null ? "0.00" : productinfo.get("settle_count").toString());
			settle_count_sum += settle_count;
			
			double invoice_amount = Double.valueOf(productinfo.get("invoice_amount")==null ? "0.00" : productinfo.get("invoice_amount").toString());
			invoice_amount_sum += invoice_amount;
			
			double service_fee = Double.valueOf(productinfo.get("service_fee")==null ? "0.00" : productinfo.get("service_fee").toString());
			service_fee_sum += service_fee;
			
			//应付代收货款
			double payable_collect_amount = Double.valueOf(productinfo.get("payable_collect_amount")==null ? "0.00" : productinfo.get("payable_collect_amount").toString());
			payable_collect_amount_sum += payable_collect_amount;			
			
			
			double sale_money = Double.valueOf(productinfo.get("sale_money")==null ? "0.00" : productinfo.get("sale_money").toString());
			sale_money_sum += sale_money;
			
			double postage=Double.valueOf(productinfo.get("postage")==null ? "0.00" : productinfo.get("postage").toString());
			postage_sum += postage;
			
			double manage_money = Double.valueOf(productinfo.get("manage_money")==null ? "0.00" : productinfo.get("manage_money").toString());
			manage_money_sum += manage_money;
			
			double others=Double.valueOf(productinfo.get("others")==null ? "0.00" : productinfo.get("others").toString());
			other_sum += others;
			
			double add_amount=Double.valueOf(productinfo.get("add_amount")==null ? "0.00" : productinfo.get("add_amount").toString());
			add_amount_sum += add_amount;
			
			//结算代收货款
			double settle_collect_amount = Double.valueOf(productinfo.get("settle_collect_amount")==null ? "0.00" : productinfo.get("settle_collect_amount").toString());
			settle_collect_amount_sum += settle_collect_amount;
			
			double period_money = Double.valueOf(productinfo.get("period_money")==null ? "0.00" : productinfo.get("period_money").toString());
			period_money_sum += period_money;
			
			//实付代收货款
			double actual_pay_amount = Double.valueOf(productinfo.get("actual_pay_amount")==null ? "0.00" : productinfo.get("actual_pay_amount").toString());
			actual_pay_amount_sum += actual_pay_amount;
			
		    int lastRowIndex = sheet.getLastRowNum();

		    row = sheet.createRow((short)lastRowIndex + 1); //在现有行号后追加数据  
		    row.createCell(0).setCellValue(no);no++;		//序号
	        row.createCell(1).setCellValue(product_code); 	   
	        row.createCell(2).setCellValue(product_name);
	        row.createCell(3).setCellValue(sku_code); 	   
	        row.createCell(4).setCellValue(sku_name);
	        row.createCell(5).setCellValue(sell_price);
	        row.createCell(6).setCellValue(cost_price);
	        
	        row.createCell(7).setCellValue(settle_count);
	        row.createCell(8).setCellValue(invoice_amount);          //本期代收货款合计
	        row.createCell(9).setCellValue(service_fee);	 		 //服务费
	        row.createCell(10).setCellValue(payable_collect_amount); //应付代收货款
	        row.createCell(11).setCellValue(sale_money);			 //促销费
	        row.createCell(12).setCellValue(postage);				//邮费
	        row.createCell(13).setCellValue(manage_money);			//平台管理费
	        row.createCell(14).setCellValue(others);				//其他
	        row.createCell(15).setCellValue(add_amount);			//附加扣费合计
	        row.createCell(16).setCellValue(settle_collect_amount);	//结算代收货款
	        row.createCell(17).setCellValue(period_money);			//本期质保金
	        row.createCell(18).setCellValue(actual_pay_amount);		//实付代收货款					
		}
		
		//合计行
		int lastRowIndex = sheet.getLastRowNum();
		row=sheet.createRow((short) lastRowIndex + 1);
		row.createCell(0).setCellValue("合计");
        row.createCell(7).setCellValue(settle_count_sum);
        row.createCell(8).setCellValue(invoice_amount_sum);
        row.createCell(9).setCellValue(service_fee_sum);				//服务费合计
        row.createCell(10).setCellValue(payable_collect_amount_sum); 	//应付代收合计
        row.createCell(11).setCellValue(sale_money_sum);				//促销费合计
        row.createCell(12).setCellValue(postage_sum);					//邮费合计
        row.createCell(13).setCellValue(manage_money_sum);				//平台管理费合计
        row.createCell(14).setCellValue(other_sum);						//其他合计
        row.createCell(15).setCellValue(add_amount_sum);				//附加扣费合计
        row.createCell(16).setCellValue(settle_collect_amount_sum);		//附加扣费合计
        row.createCell(17).setCellValue(period_money_sum);				//附加扣费合计
        row.createCell(18).setCellValue(actual_pay_amount_sum);   		//实际付款金额

        startrow = lastRowIndex + 1;
        
        row=sheet.createRow((short) startrow + 3);
        sheet.addMergedRegion(new Region(startrow+3, (short) 0, startrow+3, (short) 12));
        row.createCell(0).setCellValue("开票信息：");
        row.getCell(0).setCellStyle(cellStyle);
        row1=sheet.createRow((short)startrow+4);
        sheet.addMergedRegion(new Region(startrow+4, (short) 0, startrow+4, (short) 5));
        row1.createCell(0).setCellValue("全   称："+TopConfig.Instance.bConfig("ordercenter.bill_export_company_name"));
        row1.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+5);
        sheet.addMergedRegion(new Region(startrow+5, (short) 0, startrow+5, (short) 5));
        row.createCell(0).setCellValue("开户行："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_address"));
        row.getCell(0).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+4, (short) 6, startrow+5, (short) 12));
        row1.createCell(6).setCellValue("供应商汇款信息确认：(以下账号信息如有变更请及时反馈,未反馈视为信息无误)");
        row1.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+6);
        sheet.addMergedRegion(new Region(startrow+6, (short) 0, startrow+6, (short) 5));
        row.createCell(0).setCellValue("账   号："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_account"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+7);
        sheet.addMergedRegion(new Region(startrow+7, (short) 0, startrow+7, (short) 5));
        row.createCell(0).setCellValue("注册地址："+TopConfig.Instance.bConfig("ordercenter.bill_export_register_address"));
        row.createCell(6).setCellValue("全   称：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+7, (short) 7, startrow+7, (short) 12));
        row.createCell(7).setCellValue(account_line);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+8);
        sheet.addMergedRegion(new Region(startrow+8, (short) 0, startrow+8, (short) 5));
        row.createCell(0).setCellValue("税务登记证号："+TopConfig.Instance.bConfig("ordercenter.bill_export_tax_registration_number"));
        row.createCell(6).setCellValue("开户行：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+8, (short) 7, startrow+8, (short) 12));
        row.createCell(7).setCellValue(branch_name);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+9);
        sheet.addMergedRegion(new Region(startrow+9, (short) 0, startrow+9, (short) 5));
        row.createCell(0).setCellValue("电   话："+TopConfig.Instance.bConfig("ordercenter.bill_export_telphone"));
        row.createCell(6).setCellValue("账   号：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+9, (short) 7, startrow+9, (short) 12));
        row.createCell(7).setCellValue(bank_account);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+10);
        sheet.addMergedRegion(new Region(startrow+10, (short) 0, startrow+10, (short) 12));
        row.createCell(0).setCellValue("请确认金额及汇款信息,并将发票与结算表于当月15日前邮寄到:"+TopConfig.Instance.bConfig("ordercenter.bill_export_company_address"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+11);
        sheet.addMergedRegion(new Region(startrow+11, (short) 0, startrow+11, (short) 5));
        row.createCell(0).setCellValue("商品部：");
        row.createCell(6).setCellValue("商品行政：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+12);
        sheet.addMergedRegion(new Region(startrow+12, (short) 0, startrow+12, (short) 12));
        row.createCell(0).setCellValue("以下由惠家有提交用款申请时填报");
        row.getCell(0).setCellStyle(cellStyle);
        
        row=sheet.createRow((short)startrow+13);
        sheet.addMergedRegion(new Region(startrow+13, (short) 0, startrow+13, (short) 1));
        sheet.addMergedRegion(new Region(startrow+13, (short) 4, startrow+13, (short) 9));
        sheet.addMergedRegion(new Region(startrow+13, (short) 11, startrow+13, (short) 12));
        row.createCell(0).setCellValue("提交付款日期");
        row.createCell(3).setCellValue("发票号码");
        row.createCell(10).setCellValue("质保金");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+14);
        sheet.addMergedRegion(new Region(startrow+14, (short) 0, startrow+14, (short) 1));
        sheet.addMergedRegion(new Region(startrow+14, (short) 4, startrow+14, (short) 6));
        sheet.addMergedRegion(new Region(startrow+14, (short) 8, startrow+14, (short) 9));
        row.createCell(0).setCellValue("商品部");
        row.createCell(3).setCellValue("财务中心");
        row.createCell(7).setCellValue("财务总监");
        row.createCell(10).setCellValue("实付金额");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(7).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+15);
        sheet.addMergedRegion(new Region(startrow+15, (short) 0, startrow+15, (short) 1));
        sheet.addMergedRegion(new Region(startrow+15, (short) 2, startrow+15, (short) 12));
        row.createCell(0).setCellValue("特殊事项说明");
        row.getCell(0).setCellStyle(cellStyle);
        
        OutputStream ouputStream = null;
		try {
			response.setContentType("application/vnd.ms-excel");    
	        response.setHeader("Content-disposition", "attachment;filename="+java.net.URLEncoder.encode(excelName, "UTF-8").replace("+", "")+".xls"); 
	        ouputStream = response.getOutputStream();  
	        ouputStream.flush();   
	        wb.write(ouputStream);    
	       // removeRow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(null != ouputStream)	try { ouputStream.close(); } catch (IOException e) {}
			if(null != in) try { in.close(); } catch (IOException e) {}
		}
	}
	
	/**
	 * 导出平台入驻结算某商户SKU级别明细数据
	 */
	public static void processPlatformExportProductDetail(String settle_code, String settle_type, String start_time, String end_time,
			String small_seller_code, String merchant_name, String excelName, HttpServletResponse response) {
		Map<String,String> userinfo=DbUp.upTable("uc_seller_info_extend").oneWhere("small_seller_code,account_line,"
				+ "bank_account,branch_name", "","small_seller_code=:small_seller_code", "small_seller_code",small_seller_code);
		String account_line="";
		String branch_name="";
		String bank_account="";
		if(userinfo!=null) {
			 account_line=userinfo.get("account_line").toString();//全称
			 branch_name=userinfo.get("branch_name").toString();//开户行
			 bank_account=userinfo.get("bank_account").toString();//账号
		}
		
		MDataMap map = new MDataMap();
		map.put("settle_code", settle_code);
		map.put("small_seller_code", small_seller_code);

		InputStream in = ExportProductDetailInfo.class.getResourceAsStream("/productInfoPlatform.xls");
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);//设置字体大小
		HSSFCellStyle cellStyle = wb.createCellStyle(); 
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFSheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(0);
		sheet.getRow(2).getCell(0).setCellValue("结算时间("+start_time.substring(0, 10)+"至"+end_time.substring(0, 10)+") 入驻商户对帐单");
		sheet.getRow(3).getCell(1).setCellValue(merchant_name);
		sheet.getRow(3).getCell(6).setCellValue(small_seller_code);
		
		Row row1 ;
		int startrow=0;
		String sql = "select seller_name,small_seller_code,product_code,product_name,sku_code,sku_name,sell_price,cost_price,"
				+ "sum(settle_count) as settle_count,sum(invoice_amount) as invoice_amount,"
				+ "sum(service_fee) as service_fee,sum(payable_collect_amount) as payable_collect_amount,sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
				+ "sum(others) as others,sum(add_amount) as add_amount,"
				+ "sum(settle_collect_amount) as settle_collect_amount," //结算代收货款
				+ "sum(period_money) as period_money,"
				+ "sum(actual_pay_amount) as actual_pay_amount " //实付代收货款
				+ "from oc_bill_product_detail_new where "
				+ "settle_code=:settle_code and small_seller_code=:small_seller_code group by product_code,sku_code,sell_price,cost_price";
//				+ "start_time=:start_time and end_time=:end_time and small_seller_code=:small_seller_code group by product_code,sell_price";

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new").dataSqlList(sql, map);
		
		double settle_count_sum = 0.0;
		double invoice_amount_sum = 0.0;
		double service_fee_sum = 0.0;
		double payable_collect_amount_sum = 0.0;  //应付代收货款
		double sale_money_sum = 0.0;
		double postage_sum = 0.0;
		double manage_money_sum = 0.0;
		double other_sum = 0.0;
		double add_amount_sum = 0.0;
		double settle_collect_amount_sum=0;  //结算代收货款
		double period_money_sum=0; 
		double actual_pay_amount_sum=0;  //实付代收货款
		
		int no=1;
		for (Map<String, Object> productinfo : list) {
			String product_code = productinfo.get("product_code").toString();
			String product_name = productinfo.get("product_name").toString();
			String sku_code = productinfo.get("sku_code").toString();
			String sku_name = productinfo.get("sku_name").toString();
//			String seller_name = productinfo.get("seller_name").toString();
//			String seller_code = productinfo.get("small_seller_code").toString();
			double sell_price = Double.valueOf(productinfo.get("sell_price")==null ? "0.00" : productinfo.get("sell_price").toString());
			double cost_price = Double.valueOf(productinfo.get("cost_price")==null ? "0.00" : productinfo.get("cost_price").toString());
			double settle_count = Double.valueOf(productinfo.get("settle_count")==null ? "0.00" : productinfo.get("settle_count").toString());
			settle_count_sum += settle_count;
			
			double invoice_amount = Double.valueOf(productinfo.get("invoice_amount")==null ? "0.00" : productinfo.get("invoice_amount").toString());
			invoice_amount_sum += invoice_amount;
			
			double service_fee = Double.valueOf(productinfo.get("service_fee")==null ? "0.00" : productinfo.get("service_fee").toString());
			service_fee_sum += service_fee;
			
			//应付代收货款
			double payable_collect_amount = Double.valueOf(productinfo.get("payable_collect_amount")==null ? "0.00" : productinfo.get("payable_collect_amount").toString());
			payable_collect_amount_sum += payable_collect_amount;			
			
			
			double sale_money = Double.valueOf(productinfo.get("sale_money")==null ? "0.00" : productinfo.get("sale_money").toString());
			sale_money_sum += sale_money;
			
			double postage=Double.valueOf(productinfo.get("postage")==null ? "0.00" : productinfo.get("postage").toString());
			postage_sum += postage;
			
			double manage_money = Double.valueOf(productinfo.get("manage_money")==null ? "0.00" : productinfo.get("manage_money").toString());
			manage_money_sum += manage_money;
			
			double others=Double.valueOf(productinfo.get("others")==null ? "0.00" : productinfo.get("others").toString());
			other_sum += others;
			
			double add_amount=Double.valueOf(productinfo.get("add_amount")==null ? "0.00" : productinfo.get("add_amount").toString());
			add_amount_sum += add_amount;
			
			//结算代收货款
			double settle_collect_amount = Double.valueOf(productinfo.get("settle_collect_amount")==null ? "0.00" : productinfo.get("settle_collect_amount").toString());
			settle_collect_amount_sum += settle_collect_amount;
			
			double period_money = Double.valueOf(productinfo.get("period_money")==null ? "0.00" : productinfo.get("period_money").toString());
			period_money_sum += period_money;
			
			//实付代收货款
			double actual_pay_amount = Double.valueOf(productinfo.get("actual_pay_amount")==null ? "0.00" : productinfo.get("actual_pay_amount").toString());
			actual_pay_amount_sum += actual_pay_amount;
			
		    int lastRowIndex = sheet.getLastRowNum();

		    row = sheet.createRow((short)lastRowIndex + 1); //在现有行号后追加数据  
	        row.createCell(0).setCellValue(no);no++;		//序号
	        row.createCell(1).setCellValue(product_code); 	   
	        row.createCell(2).setCellValue(product_name);
	        row.createCell(3).setCellValue(sku_code); 	   
	        row.createCell(4).setCellValue(sku_name);
	        row.createCell(5).setCellValue(sell_price);
	        row.createCell(6).setCellValue(cost_price);
	        
	        row.createCell(7).setCellValue(settle_count);
	        row.createCell(8).setCellValue(invoice_amount);          //本期代收货款合计
	        row.createCell(9).setCellValue(service_fee);	 		 //服务费
	        row.createCell(10).setCellValue(payable_collect_amount); //应付代收货款
	        row.createCell(11).setCellValue(sale_money);			 //促销费
	        row.createCell(12).setCellValue(postage);				//邮费
	        row.createCell(13).setCellValue(manage_money);			//平台管理费
	        row.createCell(14).setCellValue(others);				//其他
	        row.createCell(15).setCellValue(add_amount);			//附加扣费合计
	        row.createCell(16).setCellValue(settle_collect_amount);	//结算代收货款
	        row.createCell(17).setCellValue(period_money);			//本期质保金
	        row.createCell(18).setCellValue(actual_pay_amount);		//实付代收货款				
		}
		
		//合计行
		int lastRowIndex = sheet.getLastRowNum();
		row=sheet.createRow((short) lastRowIndex + 1);
		row.createCell(0).setCellValue("合计");
        row.createCell(7).setCellValue(settle_count_sum);
        row.createCell(8).setCellValue(invoice_amount_sum);
        row.createCell(9).setCellValue(service_fee_sum);				//服务费合计
        row.createCell(10).setCellValue(payable_collect_amount_sum); 	//应付代收合计
        row.createCell(11).setCellValue(sale_money_sum);				//促销费合计
        row.createCell(12).setCellValue(postage_sum);					//邮费合计
        row.createCell(13).setCellValue(manage_money_sum);				//平台管理费合计
        row.createCell(14).setCellValue(other_sum);						//其他合计
        row.createCell(15).setCellValue(add_amount_sum);				//附加扣费合计
        row.createCell(16).setCellValue(settle_collect_amount_sum);		//附加扣费合计
        row.createCell(17).setCellValue(period_money_sum);				//附加扣费合计
        row.createCell(18).setCellValue(actual_pay_amount_sum);   		//实际付款金额

        startrow = lastRowIndex + 1;
        
        row=sheet.createRow((short) startrow + 3);
        sheet.addMergedRegion(new Region(startrow+3, (short) 0, startrow+3, (short) 12));
        row.createCell(0).setCellValue("开票信息：");
        row.getCell(0).setCellStyle(cellStyle);
        row1=sheet.createRow((short)startrow+4);
        sheet.addMergedRegion(new Region(startrow+4, (short) 0, startrow+4, (short) 5));
        row1.createCell(0).setCellValue("全   称："+TopConfig.Instance.bConfig("ordercenter.bill_export_company_name"));
        row1.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+5);
        sheet.addMergedRegion(new Region(startrow+5, (short) 0, startrow+5, (short) 5));
        row.createCell(0).setCellValue("开户行："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_address"));
        row.getCell(0).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+4, (short) 6, startrow+5, (short) 12));
        row1.createCell(6).setCellValue("供应商汇款信息确认：(以下账号信息如有变更请及时反馈,未反馈视为信息无误)");
        row1.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+6);
        sheet.addMergedRegion(new Region(startrow+6, (short) 0, startrow+6, (short) 5));
        row.createCell(0).setCellValue("账   号："+TopConfig.Instance.bConfig("ordercenter.bill_export_bank_account"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+7);
        sheet.addMergedRegion(new Region(startrow+7, (short) 0, startrow+7, (short) 5));
        row.createCell(0).setCellValue("注册地址："+TopConfig.Instance.bConfig("ordercenter.bill_export_register_address"));
        row.createCell(6).setCellValue("全   称：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+7, (short) 7, startrow+7, (short) 12));
        row.createCell(7).setCellValue(account_line);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+8);
        sheet.addMergedRegion(new Region(startrow+8, (short) 0, startrow+8, (short) 5));
        row.createCell(0).setCellValue("税务登记证号："+TopConfig.Instance.bConfig("ordercenter.bill_export_tax_registration_number"));
        row.createCell(6).setCellValue("开户行：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+8, (short) 7, startrow+8, (short) 12));
        row.createCell(7).setCellValue(branch_name);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+9);
        sheet.addMergedRegion(new Region(startrow+9, (short) 0, startrow+9, (short) 5));
        row.createCell(0).setCellValue("电   话："+TopConfig.Instance.bConfig("ordercenter.bill_export_telphone"));
        row.createCell(6).setCellValue("账   号：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        sheet.addMergedRegion(new Region(startrow+9, (short) 7, startrow+9, (short) 12));
        row.createCell(7).setCellValue(bank_account);
        row.getCell(7).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+10);
        sheet.addMergedRegion(new Region(startrow+10, (short) 0, startrow+10, (short) 12));
        row.createCell(0).setCellValue("请确认金额及汇款信息,并将发票与结算表于当月15日前邮寄到:"+TopConfig.Instance.bConfig("ordercenter.bill_export_company_address"));
        row.getCell(0).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+11);
        sheet.addMergedRegion(new Region(startrow+11, (short) 0, startrow+11, (short) 5));
        row.createCell(0).setCellValue("商品部：");
        row.createCell(6).setCellValue("商品行政：");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(6).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+12);
        sheet.addMergedRegion(new Region(startrow+12, (short) 0, startrow+12, (short) 12));
        row.createCell(0).setCellValue("以下由惠家有提交用款申请时填报");
        row.getCell(0).setCellStyle(cellStyle);
        
        row=sheet.createRow((short)startrow+13);
        sheet.addMergedRegion(new Region(startrow+13, (short) 0, startrow+13, (short) 1));
        sheet.addMergedRegion(new Region(startrow+13, (short) 4, startrow+13, (short) 9));
        sheet.addMergedRegion(new Region(startrow+13, (short) 11, startrow+13, (short) 12));
        row.createCell(0).setCellValue("提交付款日期");
        row.createCell(3).setCellValue("发票号码");
        row.createCell(10).setCellValue("质保金");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+14);
        sheet.addMergedRegion(new Region(startrow+14, (short) 0, startrow+14, (short) 1));
        sheet.addMergedRegion(new Region(startrow+14, (short) 4, startrow+14, (short) 6));
        sheet.addMergedRegion(new Region(startrow+14, (short) 8, startrow+14, (short) 9));
        row.createCell(0).setCellValue("商品部");
        row.createCell(3).setCellValue("财务中心");
        row.createCell(7).setCellValue("财务总监");
        row.createCell(10).setCellValue("实付金额");
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(3).setCellStyle(cellStyle);
        row.getCell(7).setCellStyle(cellStyle);
        row.getCell(10).setCellStyle(cellStyle);
        row=sheet.createRow((short)startrow+15);
        sheet.addMergedRegion(new Region(startrow+15, (short) 0, startrow+15, (short) 1));
        sheet.addMergedRegion(new Region(startrow+15, (short) 2, startrow+15, (short) 12));
        row.createCell(0).setCellValue("特殊事项说明");
        row.getCell(0).setCellStyle(cellStyle);
        
        OutputStream ouputStream = null;
		try {
			response.setContentType("application/vnd.ms-excel");    
	        response.setHeader("Content-disposition", "attachment;filename="+java.net.URLEncoder.encode(excelName, "UTF-8").replace("+", "")+".xls"); 
	        ouputStream = response.getOutputStream();  
	        ouputStream.flush();   
	        wb.write(ouputStream);    
	       // removeRow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(null != ouputStream)	try { ouputStream.close(); } catch (IOException e) {}
			if(null != in) try { in.close(); } catch (IOException e) {}
		}
	}
	
	public static void main(String[] args) throws Exception {
		ExportProductDetailInfo aa=new ExportProductDetailInfo();
		//aa.getProductInfo("2014-02-11 00:00:00","2014-02-23 17:40:31");
	}
}
