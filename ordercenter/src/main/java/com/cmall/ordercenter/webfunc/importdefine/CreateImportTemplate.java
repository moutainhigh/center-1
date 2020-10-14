package com.cmall.ordercenter.webfunc.importdefine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: CreateImportTemplate <br>
 * 描述: 根据渠道创建新的模板 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月25日 下午3:05:26
 */
public class CreateImportTemplate {

	private static CreateImportTemplate self;

	public static CreateImportTemplate getInstance() {
		if (self == null) {
			self = new CreateImportTemplate();
		}
		return self;
	}
	
	public MWebResult createTemplate(String source){
		MWebResult result = new MWebResult();
		String path = new TopDir().upServerletPath("resources/cfamily/order/");
		File f = new File(path + "template.xls");
		File newF = new File(path + source + ".xls");
		HSSFWorkbook wb = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(f);
			wb = new HSSFWorkbook(fis);
			if(wb.getNumberOfSheets() == 2){
				Sheet sheet = wb.getSheetAt(1);
				if(sheet != null){
					Cell cell = sheet.getRow(1999).getCell(199);
					cell.setCellValue(source);
					fos = new FileOutputStream(newF);
					wb.write(fos);					
				}else{
					result.setResultCode(-1);
					result.setResultMessage("模板错误，工作区间不存在");					
				}
			}else{
				result.setResultCode(-1);
				result.setResultMessage("模板错误，模板应为两个工作区间，即两个sheet");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
}
