package com.cmall.groupcenter.payorder.export;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccount;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccountExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfoExample;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webpage.PageExec;

public class ReckonOrderExport extends RootExport{

	private String exportName = "";
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		MWebPage mPage = WebUp.upPage(sOperateId);

		MDataMap mReqMap = convertRequest(request);

		PageExec pExec = new PageExec();

		MDataMap mOptionMap = new MDataMap("optionExport", "1");

	    exportName = "清分订单列表" + "-"
				+ FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");

		String sAgent = request.getHeader("USER-AGENT");

		if (StringUtils.isNotEmpty(sAgent)) {
			boolean bFlagIE = request.getHeader("USER-AGENT")
					.toLowerCase().indexOf("msie") > 0 ? true : false;

			if (bFlagIE) {
				try {
					exportName = URLEncoder.encode(exportName, "UTF8");
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
			} else {

				try {
					exportName = new String(exportName.getBytes("UTF-8"),
							"ISO8859-1");
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}

			}
		}

		MPageData pageData = pExec.upChartData(mPage, mReqMap, mOptionMap);
		
		//MPageData newPageData=new MPageData();
		//拼凑表头
		List<String> headList=new ArrayList<String>();
		headList.add("账户编号");
		headList.add("级别");
		headList.add("证件姓名");
		headList.add("手机号码");
		headList.add("证件类型");
		headList.add("证件号码");
		headList.add("总清分金额");
		headList.add("清分账户金额");
		headList.add("总计可提现金额");
		headList.add("已提现金额");
		headList.add("可提现账户金额");
		headList.add("订单来源");
		headList.add("订单号");
		headList.add("转入可提现账户时间");
		headList.add("是否本人订单");
		headList.add("销售金额");
		headList.add("清分比列");
		headList.add("清分金额");

		pageData.setPageHead(headList);
		
		List<List<String>> dataList=pageData.getPageData();
		StringBuilder accountBuilder=new StringBuilder("");
		StringBuilder logBuilder=new StringBuilder("");
		for(int k=0;k<dataList.size();k++){
			if(k==dataList.size()-1){
				accountBuilder.append("\"").append(dataList.get(k).get(1)).append("\"");
				logBuilder.append("\"").append(dataList.get(k).get(0)).append("\"");
			}
			else{
				accountBuilder.append("\"").append(dataList.get(k).get(1)).append("\",");
				logBuilder.append("\"").append(dataList.get(k).get(0)).append("\",");
			}
			
		}
		String accountString=accountBuilder.toString();
		String accountSql="select mmi.account_code,mli.login_name from mc_member_info mmi left join mc_login_info mli on mmi.member_code=mli.member_code where mmi.account_code in ("+accountString+") group by mmi.account_code";
		List<Map<String, Object>> accountList=DbUp.upTable("mc_member_info").dataSqlList(accountSql, new MDataMap());
		String logCodeString=logBuilder.toString();
		String logSql="select grl.log_code,gga.account_level,gga.total_reckon_money,gga.account_reckon_money,gga.total_withdraw_money,gga.account_withdraw_money,groi.manage_code,"
				+ "groi.reckon_money from gc_reckon_log grl left join gc_group_account gga on grl.account_code=gga.account_code left join gc_reckon_order_info groi "
				+ "on grl.order_code=groi.order_code where grl.log_code in ("+logCodeString+") order by grl.log_code desc";
		List<Map<String, Object>> logList=DbUp.upTable("gc_reckon_log").dataSqlList(logSql, new MDataMap());
		
		List<List<String>> newDataList=new ArrayList<List<String>>();
		//System.out.println("reckon export start:"+new Date());
		for(int i=0;i<dataList.size();i++){
            List<String> reckonOrder=new ArrayList<String>();
            //账户编号
            reckonOrder.add(dataList.get(i).get(1));
            //级别
            if(logList.get(i)!=null&&logList.get(i).get("account_level")!=null){
            	 reckonOrder.add(WebTemp.upTempDataOne("gc_group_level", "level_name", "level_code",logList.get(i).get("account_level").toString()));
            }
            else{
            	reckonOrder.add("");
            }
           
            //证件姓名
            reckonOrder.add("");
            //手机号码
            String mobile="";
            for(Map<String, Object> map:accountList){
            	if(map.get("account_code").equals(dataList.get(i).get(1))&&map.get("login_name")!=null){
            		mobile=map.get("login_name").toString();
            		break;
            	}
            }
            reckonOrder.add(mobile);
            //证件类型
            reckonOrder.add("");
            //证件号码
            reckonOrder.add("");
            //总清分金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("total_reckon_money")==null?"":logList.get(i).get("total_reckon_money").toString());
            //清分账户金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("account_reckon_money")==null?"":logList.get(i).get("account_reckon_money").toString());
            //总计可提现金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("total_withdraw_money")==null?"":logList.get(i).get("total_withdraw_money").toString());
            //已提现金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("total_withdraw_money")==null||logList.get(i).get("account_withdraw_money")==null?"":new BigDecimal(logList.get(i).get("total_withdraw_money").toString()).subtract(new BigDecimal(logList.get(i).get("account_withdraw_money").toString())).toString());
            //可提现账户金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("account_withdraw_money")==null?"":logList.get(i).get("account_withdraw_money").toString());
            //订单来源
            if(logList.get(i)!=null&&logList.get(i).get("manage_code")!=null){
            	 reckonOrder.add(WebTemp.upTempDataOne("uc_appinfo", "app_name", "app_code",logList.get(i).get("manage_code").toString()));
            }
            else{
            	reckonOrder.add("");
            }
           
            //订单号
            reckonOrder.add(dataList.get(i).get(2));
            //转入可提现账户时间
            reckonOrder.add(dataList.get(i).get(10));
            //是否本人订单
            reckonOrder.add(dataList.get(i).get(7).equals("0")?"是":"否");
            //销售金额
            reckonOrder.add(logList.get(i)==null||logList.get(i).get("reckon_money")==null?"":logList.get(i).get("reckon_money").toString());
            //清分比例
            reckonOrder.add(dataList.get(i).get(5));
            //清分金额
            reckonOrder.add(dataList.get(i).get(4));
            newDataList.add(reckonOrder);
            
		}
		//System.out.println("reckon export end:"+new Date());
		pageData.setPageData(newDataList);
		exportExcelFile(pageData,response);
		
	}
	
	public void exportExcelFile(MPageData mPageData,
			HttpServletResponse hResponse) {

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
				+ exportName + ".xls");// 组装附件名称和格式

		ServletOutputStream outputStream = null;
		try {
			outputStream = hResponse.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象

		HSSFSheet sheet = wb.createSheet("excel");

		int iNowRow = 0;

		HSSFRow headRow = sheet.createRow(iNowRow);
		
		
		//定义表头样式
		HSSFCellStyle hHeaderStyle=wb.createCellStyle();
		HSSFFont font = wb.createFont();
		//加粗
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗
		
		hHeaderStyle.setFont(font);

		for (int i = 0, j = mPageData.getPageHead().size(); i < j; i++) {
			HSSFCell hCell = headRow.createCell(i);
			hCell.setCellValue(mPageData.getPageHead().get(i));
			hCell.setCellStyle(hHeaderStyle);
			
		}

		//HSSFCell cell = null;
		HSSFCellStyle numCellStyle = wb.createCellStyle();
        numCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		for (List<String> lRow : mPageData.getPageData()) {
			iNowRow++;
			HSSFRow hRow = sheet.createRow(iNowRow);
			for (int i = 0, j = lRow.size(); i < j; i++) {
				HSSFCell hCell = hRow.createCell(i);
				if((i>=6&&i<=10)||(i>=15&&i<=17)){
					if(!lRow.get(i).isEmpty()){
						hCell.setCellStyle(numCellStyle);
						hCell.setCellValue(Double.valueOf(lRow.get(i)));
					}
					else{
						hCell.setCellValue(lRow.get(i));
					}
					
				}
				else{
					hCell.setCellValue(lRow.get(i));
				}
				
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
