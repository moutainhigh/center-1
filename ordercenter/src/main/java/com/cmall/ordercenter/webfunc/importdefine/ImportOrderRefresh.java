package com.cmall.ordercenter.webfunc.importdefine;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: ImportOrderRefresh <br>
 * 描述: 刷新模板 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月25日 下午5:47:14
 */
public class ImportOrderRefresh extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap map = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (map != null && StringUtils.isNotBlank(map.get("code"))) {
			String source = map.get("code");
			result = CreateImportTemplate.getInstance().createTemplate(source);
		} else {
			result.setResultCode(-1);
			result.setResultMessage("刷新失败，配置编码为空");
		}
		return result;
	}

}
