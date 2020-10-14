package com.cmall.ordercenter.tallyorder.exceldata;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class TestExcel {
public static void main(String[] args) {
	InputStream in = TestExcel.class.getResourceAsStream("/test2.xls");
	Workbook wb = null;
	try {
		wb = new HSSFWorkbook(in);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Sheet sheet = wb.getSheetAt(1);// 第一个工作表
	int firstRowIndex = sheet.getFirstRowNum();
	int lastRowIndex = sheet.getLastRowNum();
	//System.out.println("firstRowIndex---"+firstRowIndex);
	//System.out.println("lastRowIndex----"+lastRowIndex);
}
}
