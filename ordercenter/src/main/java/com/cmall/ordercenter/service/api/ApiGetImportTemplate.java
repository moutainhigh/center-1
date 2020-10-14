package com.cmall.ordercenter.service.api;

import java.io.File;

import com.cmall.ordercenter.model.api.ApiGetImportTemplateInput;
import com.cmall.ordercenter.webfunc.importdefine.CreateImportTemplate;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 下载导入模板
 * 如果路径上有模板文件，则直接返回
 * 如果没有，就重新生成一个
 * @author cc
 *
 */
public class ApiGetImportTemplate extends RootApi<RootResultWeb, ApiGetImportTemplateInput> {

	@Override
	public RootResultWeb Process(ApiGetImportTemplateInput input, MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		try {
			String orderSource = input.getOrdersource();
			String path = new TopDir().upServerletPath("resources/cfamily/order/");
			File file = new File(path + orderSource + ".xls");
			if(!file.exists()) {
				result = CreateImportTemplate.getInstance().createTemplate(orderSource);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
