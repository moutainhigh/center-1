package com.cmall.productcenter.webfunc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出售后地址商品
 * @author lgx
 */
public class ExportAfterSaleAddressProduct extends RootExport {

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		try {
			setExportName(java.net.URLEncoder.encode("商户商品", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}// 修改文件名
		
		// 修改数据
		MPageData pageData = getPageData();
		
		List<String> head_list = pageData.getPageHead();
		// 重新写入头
		head_list.clear();
		head_list.add("商品编码");
		head_list.add("商品名称");
		head_list.add("品牌");

		// 重写数据
		List<List<String>> pd = pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();

		for (List<String> p : pd) {
			
			// 整理数据
			List<String> dataf = new ArrayList<String>(head_list.size());
			dataf.add(p.get(0));
			dataf.add(p.get(1));
			dataf.add(p.get(2));
			
			data.add(dataf);
			
		}
		pageData.setPageData(data);

		doExport();
	}

}
