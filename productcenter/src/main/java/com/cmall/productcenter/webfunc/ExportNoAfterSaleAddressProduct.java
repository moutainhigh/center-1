package com.cmall.productcenter.webfunc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmethod.WebMethod;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出无售后地址商品
 * @author lgx
 */
public class ExportNoAfterSaleAddressProduct extends RootExport {

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		try {
			setExportName(java.net.URLEncoder.encode("无售后地址商品", "UTF-8"));
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


		// 重写数据
		List<List<String>> pd = pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();

		// 获取商户id
		WebMethod webMethod = new WebMethod();
		MUserInfo upUserInfo = webMethod.upUserInfo();
		String manageCode = upUserInfo.getManageCode();
		
		pd.clear();
		List<MDataMap> product_list = DbUp.upTable("pc_productinfo").queryAll("product_code,product_name", "","small_seller_code=:small_seller_code and after_sale_address_uid=''",new MDataMap("small_seller_code", manageCode));
		if(product_list != null && product_list.size() > 0) {
			for (MDataMap mDataMap : product_list) {
				// 整理数据
				List<String> dataf = new ArrayList<String>(head_list.size());
				dataf.add(mDataMap.get("product_code"));
				dataf.add(mDataMap.get("product_name"));
				data.add(dataf);
			}
		}
		pageData.setPageData(data);

		doExport();
	}

}
