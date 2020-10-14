package com.cmall.groupcenter.payorder.export;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webpage.PageExec;

/**
 * 微公社后台-预存款导出
 * @author panwei
 * @audit  lipengfei
 */
public class TraderPrefoundExport extends BigDataExportChart{

	private String exportName = "";
	public void export(String sOperateId,
			HttpServletRequest request, HttpServletResponse response) {
		
		Date stDate = new Date();
		
		MWebPage mPage = WebUp.upPage(sOperateId);

		MDataMap mReqMap = convertRequest(request);

		PageExec pExec = new PageExec();

		MDataMap mOptionMap = new MDataMap("optionExport", "1");

	    exportName = "预存款对账明细" + "-"
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
		headList.add("商户名称");
		headList.add("时间");
		headList.add("对账类型");
		headList.add("收款金额(元)");
		headList.add("付款金额(元)");
		headList.add("预存款金额(元)");
		headList.add("订单编号");

		pageData.setPageHead(headList);
		
		List<List<String>> dataList=pageData.getPageData();
		List<List<String>> newDataList=new ArrayList<List<String>>();
		for(List<String> list:dataList){
			List<String> oneList=new ArrayList<String>();
			//商户名称
			oneList.add(list.get(0));
			//时间
			oneList.add(list.get(1));
			//对账类型
			oneList.add(list.get(2));
			//判断金额
			BigDecimal money=new BigDecimal(list.get(3));
			if(money.compareTo(BigDecimal.ZERO)!=-1){
				//收款金额(元)
				oneList.add(list.get(3));
				oneList.add("-");
			}else{
				oneList.add("-");
				//付款金额(元)
				oneList.add(list.get(3));
			}
			//预存款金额(元)
			oneList.add(list.get(4));
			//订单编号
			oneList.add(list.get(5));
			newDataList.add(oneList);
		}
		pageData.setPageData(newDataList);
		
		
		exportExcelFile(pageData,response);

	}
	

}
