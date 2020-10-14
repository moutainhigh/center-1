package com.cmall.groupcenter.payorder.export;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapweb.webexport.ExportChart;
import com.srnpr.zapweb.webmodel.MPageData;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with cgroup-new
 *
 * @author lipengfei
 * @date 2015-12-04
 * @time 11:42
 */
public class BigDataExportChart extends ExportChart{


    /**
     * 覆盖父类的方法，父类使用的是小数据导出（最多支持65535条数据）
     * 本方法可以用于导出更多的数据
     * @param mPageData
     * @param hResponse
     */
    @Override
    public void exportExcelFile(MPageData mPageData,
                                HttpServletResponse hResponse) {
        String exportName = getExportName();

        if (StringUtils.isEmpty(exportName)) {
            exportName = "export-"
                    + FormatHelper
                    .upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
        }
		/*
		 * hResponse.setContentType("application/binary;charset=ISO8859_1"); try
		 * { exportName = new String(exportName.getBytes(), "ISO8859_1"); }
		 * catch (UnsupportedEncodingException e1) { // TODO Auto-generated
		 * catch block e1.printStackTrace(); }
		 */
        hResponse.setContentType("application/binary;charset=UTF-8");

        hResponse.setHeader("Content-disposition", "attachment; filename="
                + exportName + ".xlsx");// 组装附件名称和格式

        ServletOutputStream outputStream = null;
        try {
            outputStream = hResponse.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

//		Workbook[] wbs = new Workbook[] { new HSSFWorkbook(), new XSSFWorkbook() };

        XSSFWorkbook wb = new XSSFWorkbook();// 建立新HSSFWorkbook对象

        Sheet sheet = wb.createSheet("excel");

        int iNowRow = 0;

        Row headRow = sheet.createRow(iNowRow);


        //定义表头样式
        CellStyle hHeaderStyle=wb.createCellStyle();
        Font font = wb.createFont();
        //加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗

        hHeaderStyle.setFont(font);

        for (int i = 0, j = mPageData.getPageHead().size(); i < j; i++) {
            Cell hCell = headRow.createCell(i);
            hCell.setCellValue(mPageData.getPageHead().get(i));
            hCell.setCellStyle(hHeaderStyle);

        }

        for (List<String> lRow : mPageData.getPageData()) {
            iNowRow++;
            Row hRow = sheet.createRow(iNowRow);
            for (int i = 0, j = lRow.size(); i < j; i++) {
                Cell hCell = hRow.createCell(i);
                hCell.setCellValue(lRow.get(i));
            }

        }

        try {
            wb.write(outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
