package com.cmall.systemcenter.component;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.RootSimpleComponent;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 妆品选择列表页 替换提醒时间编码为汉字
 * @author GaoYang
 *
 */
public class CosmeticWarntimeReplace extends RootSimpleComponent {

	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		String sReturnString = "";

		MDataMap mSetMap = upSetMap(mWebField.getSourceParam());

		String sFieldName = mWebField.getPageFieldName();
		String sValue = mWebField.getPageFieldValue();
		if (mDataMap.containsKey(mWebField.getFieldName())) {
			sValue = mDataMap.get(mWebField.getFieldName());
		}
		
		//替换提醒时间编码为汉字
		if("zw_f_warn_time".equals(sFieldName) && !StringUtils.isEmpty(sValue)){
			sReturnString = sValue.replaceAll("449747140001","一个月").replaceAll("449747140002","三个月").replaceAll("449747140003","半年").replaceAll("449747140004","一年");
			
		}

		return sReturnString;
	}

	@Override
	public MWebResult inDo(MWebField mWebField, MDataMap mDataMap, int iType) {
		return null;
	}

}
