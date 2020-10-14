package com.cmall.ordercenter.tallyorder;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出发票信息
 */

public class ExportFaPiao extends RootExport {

	@Override
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
        //添加导出排序条件
		String zw_p_sort = "update_time desc,zid desc";
		//MPageData pageData = getPageData();
		MPageData pageData =exportExcelWithSort(sOperateId, request, response,zw_p_sort);
		List<List<String>> pd = pageData.getPageData();
		String exFlag = judgeExportType(pd);
		this.exprotMethodRun(response, pd,exFlag);
	}

	private String judgeExportType(List<List<String>> pd) {
		if(pd!=null&&pd.size()>0) {
			String docuCode = pd.get(0).get(1);
			if(docuCode.contains("FWF")) {
				return "FWF";
			}
			else if(docuCode.contains("FJKF")) {
				return "FJKF";
			}
		}
		
		return "";
	}

	private void exprotMethodRun(HttpServletResponse response, List<List<String>> sscL,String exFlag) {

		HSSFWorkbook wb = null;
		InputStream is = null;
        if("".equals(exFlag)||"FWF".equals(exFlag)) {
        	is = ExportFaPiao.class.getResourceAsStream("/fapiaoInfos.xls");
        }
        else {
        	is = ExportFaPiao.class.getResourceAsStream("/fapiaoInfos_fjkf.xls");
        }

		OutputStream outputStream = null;
		try {
			wb = new HSSFWorkbook(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row = sheet.getRow(1);
		//做样式修改
		HSSFFont font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);// 设置字体大小
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		
		int startrow = 1;

		List<String> uidList = new ArrayList<String>();
		for (List<String> s : sscL) {
			uidList.add(s.get(0).toString());
		}
		
		//获取封装数据
		List<Map<String,Object>> fpMap=this.fezhuangData(uidList);
		if("".equals(exFlag)||"FWF".equals(exFlag)) {
			for (Map<String, Object> map : fpMap) {
				row = sheet.createRow((short) startrow + 1); 	 
				row.createCell(0).setCellValue(map.get("document_code").toString());		
				row.createCell(1).setCellValue(map.get("account_amount").toString()); 	 
				row.createCell(2).setCellValue(map.get("small_seller_code").toString());		 
				row.createCell(3).setCellValue(map.get("small_seller_name").toString());   
				row.createCell(4).setCellValue(map.get("uc_seller_type").toString());  
				row.createCell(5).setCellValue(map.get("account_clear_type").toString());		   
				row.createCell(6).setCellValue(map.get("taxpayer_certificate_select").toString());          
				row.createCell(7).setCellValue(map.get("tax_identification_number").toString());	   
				row.createCell(8).setCellValue(Double.valueOf(StringUtils.isBlank(map.get("service_fee").toString()) ? "0.00": map.get("service_fee").toString()));	      
				row.createCell(9).setCellValue(map.get("is_issue").toString());			   
				//row.createCell(10).setCellValue(map.get("document_type").toString());	
				row.createCell(10).setCellValue(map.get("submit_flag").toString());
				row.createCell(11).setCellValue(map.get("document_state").toString());
				row.createCell(12).setCellValue(map.get("bill_time").toString());
				row.createCell(13).setCellValue(map.get("waybill_num").toString());
				row.createCell(14).setCellValue(map.get("document_nature").toString());
				row.createCell(15).setCellValue(map.get("document_type").toString());
				row.createCell(16).setCellValue(map.get("taxpayer_certificate_input").toString());
				row.createCell(17).setCellValue(map.get("bank_account").toString());
				row.createCell(18).setCellValue(map.get("address").toString());
				row.createCell(19).setCellValue(map.get("phone").toString());
				row.createCell(20).setCellValue(map.get("receiver_address").toString());
				row.createCell(21).setCellValue(map.get("receiver_name").toString());
				row.createCell(22).setCellValue(map.get("mail").toString());
				row.createCell(23).setCellValue(map.get("telphone_num").toString());
				startrow++;
			}
	
		}
		else {
			for (Map<String, Object> map : fpMap) {
				row = sheet.createRow((short) startrow + 1); 	 
				row.createCell(0).setCellValue(map.get("document_code").toString());		
				row.createCell(1).setCellValue(map.get("account_amount").toString()); 	 
				row.createCell(2).setCellValue(map.get("small_seller_code").toString());		 
				row.createCell(3).setCellValue(map.get("small_seller_name").toString());   
				row.createCell(4).setCellValue(map.get("uc_seller_type").toString());  
				row.createCell(5).setCellValue(map.get("account_clear_type").toString());		   
				row.createCell(6).setCellValue(map.get("taxpayer_certificate_select").toString());          
				row.createCell(7).setCellValue(map.get("tax_identification_number").toString());	   
				row.createCell(8).setCellValue(Double.valueOf(StringUtils.isBlank(map.get("add_fee").toString()) ? "0.00": map.get("add_fee").toString()));	       
				row.createCell(9).setCellValue(map.get("is_issue").toString());			   
				//row.createCell(10).setCellValue(map.get("document_type").toString());	
				row.createCell(10).setCellValue(map.get("submit_flag").toString());
				row.createCell(11).setCellValue(map.get("document_state").toString());
				row.createCell(12).setCellValue(map.get("bill_time").toString());
				row.createCell(13).setCellValue(map.get("waybill_num").toString());
				//row.createCell(14).setCellValue(map.get("document_nature").toString());
				row.createCell(14).setCellValue(map.get("document_type").toString());
				row.createCell(15).setCellValue(map.get("taxpayer_certificate_input").toString());
				row.createCell(16).setCellValue(map.get("bank_account").toString());
				row.createCell(17).setCellValue(map.get("address").toString());
				row.createCell(18).setCellValue(map.get("phone").toString());
				row.createCell(19).setCellValue(map.get("receiver_address").toString());
				row.createCell(20).setCellValue(map.get("receiver_name").toString());
				row.createCell(21).setCellValue(map.get("mail").toString());
				row.createCell(22).setCellValue(map.get("telphone_num").toString());
				startrow++;
				}
		}

		
		response.setContentType("application/vnd.ms-excel");
		try {
			if("FWF".equals(exFlag)) {
				response.setHeader("Content-disposition", "attachment;filename=" +  java.net.URLEncoder.encode("服务费发票", "UTF-8") + ".xls");
			}
			else if("FJKF".equals(exFlag)){
				response.setHeader("Content-disposition", "attachment;filename=" +  java.net.URLEncoder.encode("附加扣费发票", "UTF-8") + ".xls");
			}
			else if("".equals(exFlag)){	
				response.setHeader("Content-disposition", "attachment;filename=" +  java.net.URLEncoder.encode("发票信息", "UTF-8") + ".xls");
			}

			outputStream = response.getOutputStream();
			wb.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private List<Map<String,Object>> fezhuangData(List<String> uidList) {
		List<Map<String,Object>> resutList = new ArrayList<Map<String,Object>>();
		String sql = "select a.document_code as document_code,a.account_amount as account_amount,a.small_seller_code as small_seller_code, a.small_seller_name as small_seller_name,a.uc_seller_type as uc_seller_type,a.account_clear_type as account_clear_type,a.taxpayer_certificate_select as taxpayer_certificate_select,a.service_fee as service_fee,a.add_fee as add_fee,a.is_issue as is_issue,a.document_nature as document_nature,a.document_type as document_type,a.submit_flag as submit_flag,a.document_state as document_state,a.bill_time as bill_time,a.waybill_num as waybill_num,a.fee_type as fee_type,a.bill_time_flag as bill_time_flag,b.tax_identification_number as tax_identification_number,b.taxpayer_certificate_input as taxpayer_certificate_input,b.bank_account as bank_account,b.address as address,b.phone as phone,b.receiver_address as receiver_address,b.receiver_name as receiver_name,b.mail as mail,b.telphone_num as telphone_num from   ordercenter.oc_documents_info a left join  usercenter.uc_seller_invoice_info b on a.small_seller_code=b.small_seller_code where a.uid in ('"+StringUtils.join(uidList,"','")+"') order by a.update_time desc,a.zid desc";
		List<Map<String, Object>> resultList = DbUp.upTable("uc_seller_invoice_info").dataSqlList(sql, null);
		for (Map<String, Object> subMap : resultList) {
			Set<String> keySet = subMap.keySet();
			for (String key : keySet) {
				if(subMap.get(key)==null) {
					subMap.put(key, "");
				}
				else {
					if("uc_seller_type".equals(key)) {
						if("4497478100050001".equals(subMap.get(key).toString())) {
							subMap.put(key, "普通商户");
						}
						else {
							subMap.put(key, "平台入驻");
						}
					}
					else if("account_clear_type".equals(key)){
						if("4497478100030003".equals(subMap.get(key).toString())) {
							subMap.put(key, "整月结算");
						}else if("4497478100030006".equals(subMap.get(key).toString())) {
							subMap.put(key, "自定义");
						}
						else {
							subMap.put(key, "半月结算");
						}
					}
					else if("taxpayer_certificate_select".equals(key)){
						if("0".equals(subMap.get(key).toString())) {
							subMap.put(key, "否");
						}
						else if("1".equals(subMap.get(key).toString())){
							subMap.put(key, "是");
						}
					}
					else if("is_issue".equals(key)){
						if("0".equals(subMap.get(key).toString())) {
							subMap.put(key, "放弃");
						}
						else if("1".equals(subMap.get(key).toString())){
							subMap.put(key, "开具");
						}
					}
					else if("document_nature".equals(key)){
						if("zp".equals(subMap.get(key).toString())) {
							subMap.put(key, "专票");
						}
						else {
							subMap.put(key, "普票");
						}
					}
					else if("document_type".equals(key)){
						if("dz".equals(subMap.get(key).toString())) {
							subMap.put(key, "电子发票");
						}
						else {
							subMap.put(key, "纸质发票");
						}
					}
					else if("submit_flag".equals(key)){
						if("0".equals(subMap.get(key).toString())) {
							subMap.put(key, "未提交");
						}
						else if("1".equals(subMap.get(key).toString())){
							subMap.put(key, "已提交");
						}
					}
					else if("document_state".equals(key)){
						if("0".equals(subMap.get(key).toString())) {
							subMap.put(key, "未开");
						}
						else if("1".equals(subMap.get(key).toString())){
							subMap.put(key, "已开");
						}
					}
					else if("document_state".equals(key)){
						if("0".equals(subMap.get(key).toString())) {
							subMap.put(key, "未开");
						}
						else if("1".equals(subMap.get(key).toString())){
							subMap.put(key, "已开");
						}
					}
					
				}
			}
			resutList.add(subMap);
			
		}
		return resutList;
	}

	
}
