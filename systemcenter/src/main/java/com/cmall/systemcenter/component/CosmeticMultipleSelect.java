package com.cmall.systemcenter.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MapSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebHtml;

/**
 * 化妆品 多选控件
 * @author GaoYang
 *
 */
public class CosmeticMultipleSelect extends ComponentWindowSingle {
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=nc_cosmetic_bag|cosmetic_code|cosmetic_name&zw_s_source_page=page_chart_vsel_nc_cosmetic_bag");
		
		return upShowCostemicText(mWebField, mDataMap, iType);
	}

	/**
	 * 获取展示文本
	 * 
	 * @param mWebField 字段信息  需要设置setSourceParam  请参考数据库编号469903190005的描述信息
	 * @param mDataMap
	 * @param iType
	 * @return
	 */
	private String upShowCostemicText(MWebField mWebField, MDataMap mDataMap,
			int iType) {

		String sReturnString = "";

		MDataMap mSetMap = upSetMap(mWebField.getSourceParam());

		String sFieldName = mWebField.getPageFieldName();
		String sValue = mWebField.getPageFieldValue();
		if (mDataMap.containsKey(mWebField.getFieldName())) {
			sValue = mDataMap.get(mWebField.getFieldName());
		}

		String sText = "";

		String[] sSources = StringUtils.split(mSetMap.get("source_tableinfo"),
				WebConst.CONST_SPLIT_LINE);

		String sRelTable = "";
		if (mSetMap.containsKey("relevance_tableinfo")) {
			sRelTable = mSetMap.get("relevance_tableinfo");
		}

		int iMax = 0;
		if (mSetMap.containsKey("max_select")) {
			iMax = Integer.valueOf(mSetMap.get("max_select"));
		}

		// 判断是否存在关联表
		if (StringUtils.isNotBlank(sRelTable)) {

		} else if (StringUtils.isNotEmpty(sValue)) {

			List<String> lTextList = new ArrayList<String>();

			for (MDataMap map : DbUp.upTable(sSources[0]).queryIn(sSources[2],
					"", "", new MDataMap(), -1, 0, sSources[1], sValue)) {
				lTextList.add(map.get(sSources[2]).replace(",",
						WebConst.CONST_SPLIT_DOWN));
			}

			sText = StringUtils.join(lTextList, ",");

		}

		if (iType == 3) {
			sReturnString = sText;
		} else {

			MWebHtml mDivHtml = new MWebHtml("div");
			MWebHtml mInputHtml = mDivHtml.addChild("hidden", "id", sFieldName,
					"name", sFieldName, "value", sValue);
			if (iType == 1 || iType == 5) {
				mInputHtml.inAttributes(upRegexString(mWebField));
			}

			mDivHtml.addChild("hidden", "id", sFieldName + "_show_text",
					"value", sText);

			MDataMap mClient = new MDataMap();
			mClient.inAllValues("id", sFieldName, "text", sText, "value",
					sValue, "max", String.valueOf(iMax), "source",
					mSetMap.get("source_page"));

			mDivHtml.addChild("script",
					"zapjs.f.require(['zapadmin/js/cbeautyCosmetic_Multi'],function(a){a.init("
							+ MapSupport.INSTANCE.toJson(mClient) + ");});");

			sReturnString = mDivHtml.upString();
		}
		return sReturnString;

	
	}

}
