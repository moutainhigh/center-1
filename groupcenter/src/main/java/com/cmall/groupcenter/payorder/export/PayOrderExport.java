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

import net.minidev.json.JSONUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmall.dborm.txmapper.groupcenter.GcPayOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfoExample;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webpage.PageExec;


public class PayOrderExport extends RootExport{

	private String exportName = "";
	public void export(String sOperateId,
			HttpServletRequest request, HttpServletResponse response) {
		
		Date stDate = new Date();
		
		MWebPage mPage = WebUp.upPage(sOperateId);

		MDataMap mReqMap = convertRequest(request);

		PageExec pExec = new PageExec();

		MDataMap mOptionMap = new MDataMap("optionExport", "1");

	    exportName = "微公社提款单" + "-"
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
		headList.add("提款单号");
		headList.add("账户编号");
		headList.add("提现日期");
		headList.add("证件姓名");
		headList.add("手机号码");
		headList.add("证件类型");
		headList.add("证件号码");
		headList.add("卡号");
		headList.add("订单号");
		headList.add("订单来源");
		headList.add("通路");
		headList.add("是否本人订单");
		headList.add("销售金额");
		headList.add("清分比例");
		headList.add("提现影响金额");
		headList.add("账户余额");
		headList.add("提现金额");
		headList.add("税额");
		headList.add("完税金额");
		headList.add("小于100的扣手续费");
		headList.add("实付金额");

		pageData.setPageHead(headList);
		
		List<List<String>> dataList=pageData.getPageData();
		List<List<String>> newDataList=new ArrayList<List<String>>();
		//GcReckonOrderInfoMapper gcReckonOrderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderInfoMapper");
		int i=0;
		for(List<String> list:dataList){
			i++;
	    	if(i==4){
	    		//break;
	    	}
			//提款单号
			String payOrderCode=list.get(0);
			//取会员编号
			//提现日期
			
			 String  gpSql= "select gp.*,mc.login_name  from gc_pay_order_info gp LEFT JOIN membercenter.mc_login_info mc on gp.member_code = mc.member_code " +
			 		"where pay_order_code ='"+payOrderCode+"' limit 1 ";
			Map<String, Object> GcPayOrderInfoMap = DbUp.upTable("gc_pay_order_info").dataSqlOne(gpSql,null);
			GcPayOrderInfo gcPayOrderInfo = getPayOrderInfoBean(GcPayOrderInfoMap);
			 
		    
		    MDataMap bankDataMap=null;
		    
		    String  gpDetailSql= "select gp.*, oo.order_channel  from (select gp.* from gc_pay_order_detail gp  where gp.pay_order_code =:payOrderCode ) gp LEFT JOIN  ordercenter.oc_orderinfo oo  ON gp.order_code = oo.out_order_code " ;
		    MDataMap params= new MDataMap();
		    params.put("payOrderCode",payOrderCode);
			List<Map<String, Object>> detailList = DbUp.upTable("gc_pay_order_detail").dataSqlList(gpDetailSql,params);
			
		    if(detailList!=null&&detailList.size()>0){
			    for(Map<String, Object> dataMap:detailList){
			    	GcPayOrderDetail gcPayOrderDetail =getPayOrderDetailBean(dataMap);
			    	
			    	String  reckongSql= "select scale_reckon  from gc_reckon_log gp  " +
					 		"where gp.order_code ='"+gcPayOrderDetail.getOrderCode()+"' and  gp.reckon_change_type='4497465200030001'  limit 1 ";
			    	 Map<String, Object> reckongMap = DbUp.upTable("gc_reckon_log").dataSqlOne(reckongSql,null);
					
				    	String  reckonOrderInfoSql= "select  gro.manage_code,gro.reckon_money  from gc_reckon_order_info gro where "+
						 		" gro.order_code ='"+gcPayOrderDetail.getOrderCode()+"'    limit 1 ";
				    	 Map<String, Object> reckonOrderInfoMap = DbUp.upTable("gc_reckon_order_info").dataSqlOne(reckonOrderInfoSql,null);
						
			    	List<String> oneList=new ArrayList<String>();
			    	//提款单号
			    	oneList.add(gcPayOrderInfo.getPayOrderCode());
			    	//账户编号
			    	oneList.add(gcPayOrderInfo.getAccountCode()==null?"":gcPayOrderInfo.getAccountCode());
			    	
			    	//提现日期
			    	oneList.add(gcPayOrderInfo.getCreateTime()==null?"":gcPayOrderInfo.getCreateTime());
		
			    	
			    	//证件姓名
			    	oneList.add(gcPayOrderInfo.getMemberName()==null?"":gcPayOrderInfo.getMemberName());
			    	//手机号码
			    	if(GcPayOrderInfoMap!=null){
			    		oneList.add(String.valueOf(GcPayOrderInfoMap.get("login_name")));
			    	}
			    	else{
			    		if(bankDataMap!=null){
			    			oneList.add(bankDataMap.get("bank_phone"));
			    		}
			    		else{
			    			oneList.add("");
			    		}
			    		
			    	}
			    	//证件类型
			    	if(gcPayOrderInfo.getCertificateType()!=null){
			    		oneList.add(list.get(4)==null?"":list.get(4));
			    	}
			    	else{
			    		oneList.add("");
			    	}
			    	
			    	
			    	//证件号码
			    	oneList.add(gcPayOrderInfo.getCertificateNo()==null?"":gcPayOrderInfo.getCertificateNo());
			    	//卡号
			    	oneList.add(gcPayOrderInfo.getCardCode()==null?"":gcPayOrderInfo.getCardCode());
			    	//订单号
			    	oneList.add(gcPayOrderDetail==null||gcPayOrderDetail.getOrderCode()==null?"":gcPayOrderDetail.getOrderCode());
			    	//订单来源
//			    	oneList.add(gcReckonOrderInfo==null||gcReckonOrderInfo.getManageCode()==null?"":DbUp.upTable("uc_appinfo").one("app_code",gcReckonOrderInfo.getManageCode()).get("app_name"));
			    	String orderFrom = "";
			    	if(reckonOrderInfoMap==null||reckonOrderInfoMap.get("manage_code")==null){
			    		orderFrom = "";
			    	}else{
			    		String appName=WebTemp.upTempDataOne("uc_appinfo", "app_name", "app_code",String.valueOf(reckonOrderInfoMap.get("manage_code")));
//			    		MDataMap orderFromMap = DbUp.upTable("uc_appinfo").one("app_code",String.valueOf(reckonOrderInfoMap.get("manage_code")));
			    		if(appName != null){
			    			orderFrom =appName;
			    		}
			    	}
			    	oneList.add(orderFrom);
			    	
			    	//订单通路
			    	String orderChannel = "";
			    	//String orderCode = gcPayOrderDetail==null||gcPayOrderDetail.getOrderCode()==null?"":gcPayOrderDetail.getOrderCode();
			    	if(dataMap.get("order_channel")!=null && dataMap.get("order_channel").toString().length()>0 ){
			    			String channelCode = dataMap.get("order_channel").toString();
			    			if("2".equals(channelCode)){
			    				orderChannel = "网站";
			    			}else if("34".equals(channelCode)){
			    				orderChannel = "APP";
			    			}else if("35".equals(channelCode)){
			    				orderChannel = "网站WAP";
			    			}else if("39".equals(channelCode)){
			    				orderChannel = "扫码购";
			    			}else if("42".equals(channelCode)){
			    				orderChannel = "微信商城";
			    			}else if("449747430001".equals(channelCode)){
			    				orderChannel = "APP";
			    			}else if("449747430002".equals(channelCode)){
			    				orderChannel = "网站WAP";
			    			}else if("449747430003".equals(channelCode)){
			    				orderChannel = "微信商城";
			    			}
			    	}
			    	oneList.add(orderChannel);
			    	//是否本人订单
			    	if(gcPayOrderDetail!=null){
			    	if(gcPayOrderDetail.getIsOwn().equals("")){
			    		oneList.add("");
			    	}
			    	else if(gcPayOrderDetail.getIsOwn().equals("1")){
			    	    oneList.add("是");
			    	}
			    	else if(gcPayOrderDetail.getIsOwn().equals("0")){
			    		oneList.add("否");
			    	}
			    	}
			    	else{
			    		oneList.add("");
			    	}
			    	//销售金额
			    	oneList.add(reckonOrderInfoMap.get("reckon_money")==null||reckonOrderInfoMap.get("reckon_money")==null?"":reckonOrderInfoMap.get("reckon_money").toString());
			    	//清分比例
			    	oneList.add(reckongMap==null||reckongMap.get("scale_reckon")==null?"":reckongMap.get("scale_reckon").toString());
			    	//实际清分金额
			    	oneList.add(gcPayOrderDetail==null||gcPayOrderDetail.getReckonMoney()==null?"":gcPayOrderDetail.getReckonMoney().toString());
			    	//账户余额
			    	oneList.add(gcPayOrderInfo.getBeforeWithdrawMoney()==null?"":gcPayOrderInfo.getBeforeWithdrawMoney().toString());
			    	//提现金额
			    	oneList.add(gcPayOrderInfo.getWithdrawMoney()==null?"":gcPayOrderInfo.getWithdrawMoney().toString());
			    	//税额
			    	oneList.add(gcPayOrderInfo.getTaxMoney()==null?"":gcPayOrderInfo.getTaxMoney().toString());
			    	//完税金额
			    	if(gcPayOrderInfo.getAfterTaxMoney()!=null){
			    		oneList.add(gcPayOrderInfo.getAfterTaxMoney().toString());
			    	}
			    	else{
			    		oneList.add("");
			    	}
			    	
			    	//小于100的扣手续费
			    	oneList.add(gcPayOrderInfo.getFeeMoney()==null?"":gcPayOrderInfo.getFeeMoney().toString());
			    	//实付金额
			    	oneList.add(gcPayOrderInfo.getPayMoney()==null?"":gcPayOrderInfo.getPayMoney().toString());
			        newDataList.add(oneList);
			    }
		    }
		    else{
		    	List<String> payList=new ArrayList<String>();
		    	//提款单号
		    	payList.add(gcPayOrderInfo.getPayOrderCode());
		    	//账户编号
		    	payList.add(gcPayOrderInfo.getAccountCode()==null?"":gcPayOrderInfo.getAccountCode());
		    	
		    	//提现日期
		    	payList.add(gcPayOrderInfo.getCreateTime()==null?"":gcPayOrderInfo.getCreateTime());
	
		    	
		    	//证件姓名
		    	payList.add(gcPayOrderInfo.getMemberName()==null?"":gcPayOrderInfo.getMemberName());
		    	//手机号码
		    	if(GcPayOrderInfoMap!=null){
		    		payList.add(String.valueOf(GcPayOrderInfoMap.get("login_name")));
		    	}
		    	else{
		    		if(bankDataMap!=null){
		    			payList.add(bankDataMap.get("bank_phone"));
		    		}
		    		else{
		    			payList.add("");
		    		}
		    		
		    	}
		    	//证件类型
		    	if(gcPayOrderInfo.getCertificateType()!=null){
		    		payList.add(list.get(4)==null?"":list.get(4));
		    	}
		    	else{
		    		payList.add("");
		    	}
		    	
		    	
		    	//证件号码
		    	payList.add(gcPayOrderInfo.getCertificateNo()==null?"":gcPayOrderInfo.getCertificateNo());
		    	//卡号
		    	payList.add(gcPayOrderInfo.getCardCode()==null?"":gcPayOrderInfo.getCardCode());
		    	//订单号
		    	payList.add("");
		    	//订单来源
		    	payList.add("");
		    	//订单通路
		    	payList.add("");
		    	//是否本人订单
		    	payList.add("");
		 
		    	//销售金额
		    	payList.add("0");
		    	//清分比例
		    	payList.add("0");
		    	//实际清分金额
		    	payList.add("0");
		    	//账户余额
		    	payList.add(gcPayOrderInfo.getBeforeWithdrawMoney()==null?"":gcPayOrderInfo.getBeforeWithdrawMoney().toString());
		    	//提现金额
		    	payList.add(gcPayOrderInfo.getWithdrawMoney()==null?"":gcPayOrderInfo.getWithdrawMoney().toString());
		    	//税额
		    	payList.add(gcPayOrderInfo.getTaxMoney()==null?"":gcPayOrderInfo.getTaxMoney().toString());
		    	//完税金额
		    	if(gcPayOrderInfo.getAfterTaxMoney()!=null){
		    		payList.add(gcPayOrderInfo.getAfterTaxMoney().toString());
		    	}
		    	else{
		    		payList.add("");
		    	}
		    	
		    	//小于100的扣手续费
		    	payList.add(gcPayOrderInfo.getFeeMoney()==null?"":gcPayOrderInfo.getFeeMoney().toString());
		    	//实付金额
		    	payList.add(gcPayOrderInfo.getPayMoney()==null?"":gcPayOrderInfo.getPayMoney().toString());
		        newDataList.add(payList);
		    	
		    }
		    //gcPayOrderInfoExample.clear();
		    //gcPayOrderDetailExample.clear();
		}
		pageData.setPageData(newDataList);
		
		//Date endDate = new Date();
		//System.out.println("数据库查询 ============:"+((endDate.getTime()-stDate.getTime())/1000)+"秒");
		
		
		exportExcelFile(pageData,response);
//		return null;

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
				if(i>=12&&i<=20){
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
	/**
	 * 获取支付订单bean
	 * @param map
	 * @return
	 */
	public GcPayOrderInfo  getPayOrderInfoBean(Map<String, Object> map){
		GcPayOrderInfo b = new GcPayOrderInfo();
		b.setAccountCode((String)map.get("account_code"));
		b.setAfterTaxMoney((BigDecimal)map.get("after_tax_money"));
		b.setAfterWithdrawMoney((BigDecimal)map.get("after_withdraw_money"));
		b.setAuditTime((String)map.get("audit_time"));
		b.setBankCode((String)map.get("bank_code"));
		b.setBankName((String)map.get("bank_name"));
		b.setBeforeWithdrawMoney((BigDecimal)map.get("before_withdraw_money"));
		b.setCardCode((String)map.get("card_code"));
		b.setCertificateNo((String)map.get("certificate_no"));
		b.setCertificateType((String)map.get("certificate_type"));
		b.setCreateTime((String)map.get("create_time"));
		b.setFeeMoney((BigDecimal)map.get("fee_money"));
		b.setLdCode((String)map.get("ld_code"));
		b.setLdName((String)map.get("ld_name"));
		b.setMemberCode((String)map.get("member_code"));
		b.setMemberName((String)map.get("member_name"));
		b.setOrderStatus((String)map.get("order_status"));
		b.setPayMoney((BigDecimal)map.get("pay_money"));
		b.setPayOrderCode((String)map.get("pay_order_code"));
		b.setPayStatus((String)map.get("pay_status"));
		b.setRemark((String)map.get("remark"));
		b.setTaxMoney((BigDecimal)map.get("tax_money"));
		b.setUid((String)map.get("uid"));
		b.setWithdrawMoney((BigDecimal)map.get("withdraw_money"));
		b.setZid((Integer)map.get("zid"));
		return b;
	}
	
	/**
	 * 获取支付订单详情bean
	 * @param map
	 * @return
	 */
	public GcPayOrderDetail  getPayOrderDetailBean(Map<String, Object> map){
		GcPayOrderDetail b = new GcPayOrderDetail();
		b.setZid((Integer)map.get("zid"));
		b.setUid((String)map.get("uid"));
		b.setRefundMoney((BigDecimal)map.get("refund_money"));
		b.setReferZid((String)map.get("refer_zid"));
		b.setReckonMoney((BigDecimal)map.get("reckon_money"));
		b.setPayOrderCode((String)map.get("pay_order_code"));
		b.setOrderCode((String)map.get("order_code"));
		b.setIsOwn((String)map.get("is_own"));
		b.setCreateTime((String)map.get("create_time"));
		return b;
	}
	/**
	 * 获取清分bean
	 * @param map
	 * @return
	 */
	public GcReckonLog getGcReckonLogBean(Map<String, Object> map){
		GcReckonLog b = new GcReckonLog();
		return b;
	}

	
}
