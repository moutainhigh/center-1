package com.cmall.ordercenter.webfunc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;

import com.cmall.ordercenter.util.MoneyUtil;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 质保金收据批量导出为zip压缩包 
 * @author renhongbin
 */
public class RetentionMoneyReceiptExportRar extends RootExport {
	

	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		MDataMap mReqMap = convertRequest(request);
		String receiptCodes = mReqMap.get("receipt_code_list");
		
		List<String> list = new ArrayList<String>(); 
		if("null".equals(receiptCodes)){//导出全部数据
			
			String manageCode = UserFactory.INSTANCE.create().getManageCode();
			
			MDataMap param = new MDataMap();
			param.put("small_seller_code", manageCode);
			param.put("receipt_retention_money_type","449748110001");
			
			List<MDataMap> queryAll = DbUp.upTable("oc_seller_retention_money_receipt")
					.queryAll("receipt_retention_money_code,small_seller_code,retention_money,ajust_money,receipt_retention_money_time", "zid", "small_seller_code=:small_seller_code and receipt_retention_money_type=:receipt_retention_money_type", param);
		
			if(null != queryAll && queryAll.size() > 0){
				
				MDataMap resultMap = DbUp.upTable("oc_seller_retention_money")
						.one("small_seller_code",queryAll.get(0).get("small_seller_code"));
				
				for (MDataMap retentionResult : queryAll) {
					
					if(null != resultMap && resultMap.containsKey("small_seller_name")){
						retentionResult.put("small_seller_name", resultMap.get("small_seller_name"));
					}
					
					list.add(this.initFile(request, response, retentionResult));
				}
			}
		}else{//导出本页数据
			String [] arr = receiptCodes.split(",");  //  order code 数组
			
			for(int i = 0 ; i < arr.length ; i ++){
				String receipt_retention_money_code = arr[i];
				
				MDataMap one = DbUp.upTable("oc_seller_retention_money_receipt").one("receipt_retention_money_code",receipt_retention_money_code);
				if(null != one && "449748110001".equals(one.get("receipt_retention_money_type"))) {
					list.add(this.initFile(request, response, receipt_retention_money_code));
				}
			}
		}
		
		this.exportZip(response , list); 
	}
	
	/**
	 * @description: 准备生成zip压缩包，并返回给前端，同时删除服务器目录下的pdf文档。
	 * @author renhongbin
	 */
	private String initFile(HttpServletRequest request, HttpServletResponse response, MDataMap retentionResult) {
	
		ByteArrayOutputStream bos = null;
		FileOutputStream fos = null;
	    PdfStamper ps = null;
	    PdfReader reader = null;
	    InputStream inp = null;
		String url = "";
		
		try {
			String exportName = retentionResult.get("receipt_retention_money_code");
			String path = "resources" + File.separator + "zipPdf";
			url = request.getSession().getServletContext().getRealPath("/") + /*File.separator +*/ path; 
			File pathFile = new File(url);
			if(!pathFile.exists()){
				pathFile.mkdirs();
			}
			
			url += File.separator + exportName + ".pdf";
			
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
          
	        data.put("money_number", money_number );
	
	        for (String key : data.keySet()) {
	                form.setField(key,data.get(key).toString());
	        }
	        
	        ps.setFormFlattening(true);
	        ps.close();
	        
	        fos = new FileOutputStream(new File(url));  
	        fos.write(bos.toByteArray());  
		}catch(Exception e){
			e.printStackTrace();
			bLogError(0, e.getMessage());
		}finally{
			close(reader);
			close(bos);
			close(fos);
			close(inp);
		}
		
		return url;  
	}

	/**
	 * @description: 实例化批量中的每一个pdf文档    
	 * @author renhongbin
	 */
	private String initFile(HttpServletRequest request , HttpServletResponse response , String receipt_retention_money_code){
		
		MDataMap retentionResult = DbUp.upTable("oc_seller_retention_money_receipt")
					.one("receipt_retention_money_code",receipt_retention_money_code);
		
		MDataMap resultMap = DbUp.upTable("oc_seller_retention_money")
					.one("small_seller_code",retentionResult.get("small_seller_code"));
		
		if(null != resultMap && resultMap.size() > 0){
			retentionResult.put("small_seller_name", resultMap.get("small_seller_name"));
		}
		
		ByteArrayOutputStream bos = null;
		FileOutputStream fos = null;
        PdfStamper ps = null;
        PdfReader reader = null;
        InputStream inp = null;
		String url = "";
		try {
			String exportName = retentionResult.get("receipt_retention_money_code");
			String path = "resources" + File.separator + "zipPdf";
			url = request.getSession().getServletContext().getRealPath("/") + /*File.separator +*/ path; 
			File pathFile = new File(url);
			if(!pathFile.exists()){
				pathFile.mkdirs();
			}
			
			url += File.separator + exportName + ".pdf";
			
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
           
            data.put("money_number",  money_number);

            for (String key : data.keySet()) {
                    form.setField(key,data.get(key).toString());
            }
            
            ps.setFormFlattening(true);
            ps.close();
            
            fos = new FileOutputStream(new File(url));  
            fos.write(bos.toByteArray());  
		}catch(Exception e){
			e.printStackTrace();
			bLogError(0, e.getMessage());
		}finally{
			close(reader);
			close(bos);
			close(fos);
			close(inp);
		}
		
		return url;  
	}
	
	/**
	 * @description: 准备生成zip压缩包，并返回给前端，同时删除服务器目录下的pdf文档。
	 * @author renhongbin
	 */
	private void exportZip(HttpServletResponse res ,  List<String> list){
		ZipOutputStream out = null;
		FileInputStream fis = null;
		try {
			String exportZipName = FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
			res.setContentType("application/binary;charset=UTF-8");
			res.setHeader("Content-disposition", "attachment; filename=" + exportZipName + ".zip");// 组装附件名称和格式
			out = new ZipOutputStream(res.getOutputStream());  // 创建zip输出流
			byte[] buffer = new byte[1024];
			for(String s : list)	{
				File file = new File(s);
				if(file.exists()){
					fis = new FileInputStream(file); 
					s = s.split("zipPdf")[1];
					s = s.substring(1, s.length());
					out.putNextEntry(new ZipEntry(s));
					int len = 0 ;
					// 读取文件的内容,打包到zip文件    
					while ((len = fis.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
				}
	            out.flush();
	            out.closeEntry();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(fis != null){
					fis.close();
				}
				if(out != null){
					out.close();
				}
				// 删除服务器目录下的pdf文档
				for(String s : list)	{
					File file = new File(s);
					if(file.exists()){
						System.out.println(file.getName() );
						boolean flag = file.delete(); 
						int count = 1;
						while(!flag){
							flag = file.delete();
							count ++; 
							if(count > 100){   // 上线删除次数，解决文件无法删除问题
								System.out.println(file.getName() + " 删除失败");  
								break;
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); 
			}
			 
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
	
	public void close(FileOutputStream fos){
		
		if(fos != null){
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				bLogError(0, e.getMessage());
			}
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
