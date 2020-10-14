package com.cmall.ordercenter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class ExportDistrict {
	
	@Test
	public void export() throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
//		FileOutputStream out = null;
		try {
			in = new FileInputStream("e:/1.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);
	
			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 9);// 设置字体大小
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			HSSFSheet sheet = wb.getSheetAt(0);
			int startrow = 0;
			
			String sql = "select * from systemcenter.sc_gov_district order by code asc";
	    	List<Map<String, Object>> list = DbUp.upTable("lc_apply_for_payment").dataSqlList(sql, new MDataMap());
	    	Map<String, String> topLevel = new HashMap<String, String>();
	    	for(Map<String, Object> map : list) {
	    		String code = null == map.get("code") ? "" : map.get("code").toString();
	    		String name = null == map.get("name") ? "" : map.get("name").toString();
	    		if(StringUtils.isNotEmpty(code)) {
	    			Row row = sheet.createRow((short) startrow + 1); 	
	    			if(code.endsWith("0000")) {
	    				//省,直辖市
	    				topLevel.put(code, name);
	    				row.createCell(0).setCellValue(startrow + 1);		
	    				row.createCell(1).setCellValue(name); 	
	    				row.createCell(2).setCellValue(code);		
	    				row.createCell(3).setCellValue("");   
	    				row.createCell(4).setCellValue("");   
	    			} else if(code.endsWith("00")) {
	    				//市,区县
	    				topLevel.put(code, name);
	    				row.createCell(0).setCellValue(startrow + 1);		
	    				String provinceCode = code.substring(0, 3) + "000";
	    				String provinceName = topLevel.get(provinceCode);
	    				if(StringUtils.isEmpty(provinceName)) {
	    					provinceCode = code.substring(0, 2) + "0000";
		    				provinceName = topLevel.get(provinceCode);
		    				if(StringUtils.isEmpty(provinceName)) {
		    					throw new Exception("aaa");
		    				}
	    				}
	    				row.createCell(1).setCellValue(provinceName + name); 
	    				row.createCell(2).setCellValue(provinceCode);		
	    				row.createCell(3).setCellValue(code);  
	    				row.createCell(4).setCellValue("");   
	    			} else {
	    				//区
	    				row.createCell(0).setCellValue(startrow + 1);
	    				String provinceCode = code.substring(0, 3) + "000";
	    				String provinceName = topLevel.get(provinceCode);
	    				if(StringUtils.isEmpty(provinceName)) {
	    					provinceCode = code.substring(0, 2) + "0000";
		    				provinceName = topLevel.get(provinceCode);
		    				if(StringUtils.isEmpty(provinceName)) {
		    					throw new Exception("aaa");
		    				}
	    				}
	    				
	    				String cityCode = code.substring(0, code.length() - 2) + "00";
	    				String cityName = topLevel.get(cityCode);
	    				if(StringUtils.isEmpty(cityName)) {
	    					throw new Exception("aaa");
	    				}
	    				if(cityName.equals("市辖区")) {
	    					row.createCell(1).setCellValue(provinceName + name);
	    				} else {
	    					row.createCell(1).setCellValue(provinceName + cityName + name);
	    				}
	    				row.createCell(2).setCellValue(provinceCode);		 
	    				row.createCell(3).setCellValue(cityCode);   
	    				row.createCell(4).setCellValue(code);   
	    			}
	    			startrow++;
	    		}
	    	}

			outputStream = new FileOutputStream("e:/test.xls");
			wb.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != in) in.close();
			if(null != outputStream) outputStream.close();
		}
		

	}
}
