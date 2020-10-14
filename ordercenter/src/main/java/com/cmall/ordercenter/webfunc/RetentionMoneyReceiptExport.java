package com.cmall.ordercenter.webfunc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmall.ordercenter.util.MoneyUtil;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * 质保金收据导出
 * @author renhongbin
 */
public class RetentionMoneyReceiptExport extends RootExport {
	
	private MDataMap retentionResult = null;

	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		
		MDataMap mReqMap = convertRequest(request);
		
		retentionResult = DbUp.upTable("oc_seller_retention_money_receipt")
					.one("receipt_retention_money_code",mReqMap.get("receipt_retention_money_code"));
		
		MDataMap resultMap = DbUp.upTable("oc_seller_retention_money")
					.one("small_seller_code",retentionResult.get("small_seller_code"));
		
		if(null != resultMap && resultMap.containsKey("small_seller_name")){
			retentionResult.put("small_seller_name", resultMap.get("small_seller_name"));
		}
		
		exportPdfFile(response);

	}
	
	public void exportPdfFile(HttpServletResponse response) {
		
		ByteArrayOutputStream bos = null;
        PdfStamper ps = null;
        PdfReader reader = null;
        InputStream inp = null;
        
		try {		

			String exportName = retentionResult.get("receipt_retention_money_code") + ".pdf";

			response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "attachment;fileName="
	                + URLEncoder.encode(exportName, "UTF-8"));
	        
	        inp = getClass().getResourceAsStream("/retentionMoneyReceipt.pdf");
            reader = new PdfReader(inp);
            bos = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, bos);

            AcroFields form = ps.getAcroFields();
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bf);

            Map<String, Object> data = new HashMap<String, Object>();
        	if(retentionResult.get("receipt_retention_money_code").indexOf(".") != -1){
        		retentionResult.put(
        				"receipt_retention_money_code", 
        				retentionResult.get("receipt_retention_money_code")
        					.substring(retentionResult.get("receipt_retention_money_code").indexOf(".")+1));
        	}
            data.put("receipt_retention_money_code", retentionResult.get("receipt_retention_money_code"));
            String[] dayArray = retentionResult.get("receipt_retention_money_time").split("-");
            data.put("year", dayArray[0]);
            data.put("month", dayArray[1]);
            data.put("day", dayArray[2]);
            data.put("seller", retentionResult.get("small_seller_name"));
            data.put("retention", "质保金");
            String money_number = new java.text.DecimalFormat("#.00").format(Double.valueOf(retentionResult.get("retention_money")) +Double.valueOf(retentionResult.get("ajust_money")));
            String chinese = MoneyUtil.toChinese(money_number);
            chinese = chinese.replaceAll("元", "圆");
            if(!chinese.endsWith("分")) {
            	chinese += "整";
    	    }
            data.put("money_upper", "人民币" + chinese);
            data.put("money_number", money_number);

            for (String key : data.keySet()) {
                    form.setField(key,data.get(key).toString());
            }
            ps.setFormFlattening(true);
            ps.close();
            
            response.getOutputStream().write(bos.toByteArray());
		} catch (Exception e) {
			bLogError(0, e.getMessage());
		}finally{		
			close(reader);
			close(bos);
			close(inp);
		}
	}
	
	private void close(InputStream inp) {
		
		if(inp != null){
			try {
				inp.close();
			} catch (IOException e) {
				bLogError(0, e.getMessage());
			}
		}
	}

	public void close(PdfReader reader){
		
		if(reader != null){
			reader.close();
		}
	}
	
	public void close(ByteArrayOutputStream outputStream){
		
		if(outputStream != null){
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				bLogError(0, e.getMessage());
			}
		}
	}
	
}
