package com.cmall.ordercenter.tallyorder.exceldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 扣费模板导入 并更新商品信息表
 * @author zmm
 *
 */
public class DeductMoneyImport {

	/**
	 * 读取Excel数据
	 * 
	 * @param file
	 */
	public static void readExcel(File file, MDataMap map) {
		try {
			if (!file.exists()) {
				//System.out.println("文件不存在");
				return;
			}
			InputStream inputStream = new FileInputStream(file);
			String fileName = file.getName();
			Workbook wb = null;
			if (fileName.endsWith("xls")) {
				wb = new HSSFWorkbook(inputStream);// 解析xls格式
			}
			// else if(fileName.endsWith("xlsx")){
			// wb = new XSSFWorkbook(inputStream);//解析xlsx格式
			// }
			Sheet sheet = wb.getSheetAt(1);// 第一个工作表
			int firstRowIndex = sheet.getFirstRowNum();
			int lastRowIndex = sheet.getLastRowNum();
			for (int rIndex = firstRowIndex+1; rIndex <= lastRowIndex; rIndex++) {
				Row row = sheet.getRow(rIndex);
				//int firstCellIndex = row.getFirstCellNum();
				//int lastCellIndex = row.getLastCellNum();
				String product_code = row.getCell(0).toString();
				BigDecimal actual_amount =null ;
				if(map.containsKey(product_code)){
					Map<String, Object> map2 =DbUp.upTable("oc_bill_product_detail_new").dataSqlOne("select actual_pay_amount from oc_bill_product_detail_new where product_code='"+product_code+"' ", null);
					if (map2 != null) {
						Iterator<String> it = map2.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							actual_amount = new BigDecimal(Double.valueOf((map2.get(key).toString()== null ? 0.00 :map2.get(key)).toString()));
							//System.out.println(actual_amount);
						}
					}
					BigDecimal sale_money = new BigDecimal(Double.valueOf(row.getCell(6).toString()));
					BigDecimal postage = new BigDecimal(Double.valueOf(row.getCell(7).toString()));
					BigDecimal manage_money = new BigDecimal(Double.valueOf(row.getCell(8).toString()));
					BigDecimal others = new BigDecimal(Double.valueOf(row.getCell(9).toString()));
					BigDecimal add_amount = new BigDecimal(Double.valueOf(row.getCell(10).toString()));
					String other_reason = row.getCell(11).toString();
					//实际付款金额=发票应开金额-质保金费用-促销费用-邮费-平台管理费-其它
					BigDecimal actual_pay_amount=actual_amount.subtract((sale_money.add(postage).add(manage_money).add(others)));
					//System.out.println(actual_pay_amount);
					DbUp.upTable("oc_bill_product_detail_new").dataUpdate(new MDataMap("product_code",product_code.toString(),"sale_money",sale_money.toString(),
							"postage",postage.toString(),"manage_money",manage_money.toString(),"others",others.toString(),"add_amount",add_amount.toString(),
							"other_reason",other_reason,"actual_pay_amount",actual_pay_amount.toString()),
							"sale_money,postage,manage_money,others,add_amount,other_reason,actual_pay_amount", "product_code");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
