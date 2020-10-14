package com.cmall.ordercenter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.ordercenter.model.Logistics;

public class ReadExcelUtil<T> {
	/**
	 * 对外提供读取excel 的方法
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 * @throws SecurityException
	 * */
	@SuppressWarnings("unchecked")
	public List<T> readExcel(boolean isFile,File file,InputStream is, String[] fields,Class[] fieldClass, Class<T> c,String extension)
			throws IOException, InstantiationException, IllegalAccessException,
			SecurityException, Exception {
		
		if(isFile){
			String fileName = file.getName();
			extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		if ("xls".equals(extension)) {
			return read2003Excel(isFile,file,is,fields,fieldClass, c);
		} else if ("xlsx".equals(extension)) {
			 return read2007Excel(isFile,file,is,fields,fieldClass, c);
		} else {
			throw new IOException("不支持的文件类型");
		}
	}
	/**
	 * 读取 office 2003 excel
	 * 
	 * @throws IOException
	 * @throws Exception 
	 * @throws SecurityException 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private  List<T> read2003Excel(boolean isFile,File file,InputStream is,String[] fields,Class[] fieldClass,Class<T> c)
			throws IOException, SecurityException, Exception {
		
		List<T> list = new LinkedList<T>();
		HSSFWorkbook hwb  = null;
		if(isFile){
			hwb = new HSSFWorkbook(new FileInputStream(file));
		}else {
			hwb = new HSSFWorkbook(is);
		}
		HSSFSheet sheet = hwb.getSheetAt(0);
		
		String value = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		
		for (int i = sheet.getFirstRowNum()+1; i <= sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			
			
			T t = c.newInstance();
			
			int blank_cell_num = 0;
			int cell_num = row.getLastCellNum() - row.getFirstCellNum();
			
			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					//System.out.println(i + "行" + j + " 列 is String type");
					value = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					//System.out.println(i + "行" + j + " 列 is Number type ; DateFormt:" + cell.getCellStyle().getDataFormatString());
					
					if ("@".equals(cell.getCellStyle().getDataFormatString())) {
						value = DateFormatUtil.df.format(cell.getNumericCellValue());
					} else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
						value = DateFormatUtil.nf.format(cell.getNumericCellValue());
					} else {
						if(HSSFDateUtil.isCellDateFormatted(cell)){
							value = DateFormatUtil.sdf.format(cell.getDateCellValue());
						}else{
							cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							value = cell.getStringCellValue();
						}
					}
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					//System.out.println(i + "行" + j + " 列 is Boolean type");
					value = String.valueOf(cell.getBooleanCellValue());
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					//System.out.println(i + "行" + j + " 列 is Blank type");
					value = "";
					blank_cell_num++;
					break;
				default:
					//System.out.println(i + "行" + j + " 列 is default type");
					value = cell.toString();
				}
				if (StringUtils.isBlank(value)) {
					continue;
				}
				
				// 支持float和double类型属性
				Object v = null;
				if(fieldClass[j].equals(Float.class)){
					v = Float.valueOf(value.toString()).floatValue();
				}else if(fieldClass[j].equals(Double.class)){
					v = Double.valueOf(value.toString()).doubleValue();
				}else{
					v = value;
				}
				
				Method m = c.getMethod(getFieldSetMethod(fields[j]), fieldClass[j]);
				m.invoke(t, v);
			}	
			
			
			if(blank_cell_num != cell_num){
				list.add(t);
			}
			
		}
		return list;
	}

	/**
	 * 读取Office 2007 excel
	 * 
	 * @return
	 * @throws Exception 
	 * @throws SecurityException 
	 * */
	@SuppressWarnings("unchecked")
	private List<T> read2007Excel(boolean isFile,File file,InputStream is, String[] fields,Class[] fieldClass, Class<T> c)
			throws IOException, SecurityException, Exception {

		List<T> list = new LinkedList<T>();
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = null;
		if(isFile){
			xwb = new XSSFWorkbook(new FileInputStream(file));
		}else{
			xwb = new XSSFWorkbook(is);
		}
		// 读取第一章表格内容
		XSSFSheet sheet = xwb.getSheetAt(0);
		String value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		for (int i = sheet.getFirstRowNum()+1; i <= sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			T t = c.newInstance();
			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					//System.out.println(i + "行" + j + " 列 is String type");
					value = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					//System.out.println(i + "行" + j+ " 列 is Number type ; DateFormt:"+ cell.getCellStyle().getDataFormatString());
					
					if ("@".equals(cell.getCellStyle().getDataFormatString())) {
						value = DateFormatUtil.df.format(cell.getNumericCellValue());
					} else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
						value = DateFormatUtil.nf.format(cell.getNumericCellValue());
					} else {
						if(HSSFDateUtil.isCellDateFormatted(cell)){
							value = DateFormatUtil.sdf.format(cell.getDateCellValue());
						}else{
							cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							value = cell.getStringCellValue();
						}
					}
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					//System.out.println(i + "行" + j + " 列 is Boolean type");
					value = String.valueOf(cell.getBooleanCellValue());
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					//System.out.println(i + "行" + j + " 列 is Blank type");
					value = "";
					break;
				default:
					//System.out.println(i + "行" + j + " 列 is default type");
					value = cell.toString();
				}
				if (StringUtils.isBlank(value)) {
					continue;
				}
				
				// 支持float和double类型属性
				Object v = null;
				if(fieldClass[j].equals(Float.class)){
					v = Float.valueOf(value.toString()).floatValue();
				}else if(fieldClass[j].equals(Double.class)){
					v = Double.valueOf(value.toString()).doubleValue();
				}else{
					v = value;
				}
				
				Method m = c.getMethod(getFieldSetMethod(fields[j]), fieldClass[j]);
				m.invoke(t, v);
			}
			list.add(t);
		}
		return list;
	}
	private String getFieldSetMethod(String field){
		return "set" + field.substring(0,1).toUpperCase() + field.substring(1);
	}
}