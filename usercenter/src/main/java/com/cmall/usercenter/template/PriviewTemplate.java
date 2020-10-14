package com.cmall.usercenter.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.TagHtml;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.ObjectCache;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;

public class PriviewTemplate extends BaseClass {

	public String upHtml() {

		String sTemplateString = upPriview(upTemplateAuto(),"../resources/");

		String sSourceTemp = "";

		String sKey = WebConst.CONST_OBJECT_CACHE_NAME
				+ "com.cmall.usercenter.template.PriviewTemplate.upHtml";

		if (!ObjectCache.getInstance().containsKey(sKey)) {
			try {
				sSourceTemp = WebClientSupport.upPost(
						bConfig("usercenter.template_priview"), new MDataMap());
				
				sSourceTemp=TagHtml.AddWarnHtml(sSourceTemp,bInfo(949705201));
				
			} catch (Exception e) {

				e.printStackTrace();
			}
			ObjectCache.getInstance().inElement(sKey, sSourceTemp);
		} else {
			{
				sSourceTemp = ObjectCache.getInstance().upValue(sKey)
						.toString();
			}
		}

		sTemplateString = sSourceTemp.replace("<!--body-->", sTemplateString);

		return sTemplateString;

	}

	public String upPriview(String sContent,String sResourcesLink) {

		
		sContent= RegexHelper.upScanHtmlSimple(sContent);
		
		
		List<String> list = new ArrayList<String>();

		list.add("<link type=\"text/css\" href=\""+sResourcesLink+"ctheme/shop/red.css\"  rel=\"stylesheet\" />");
		
		
		
		//如果有轮播广告  则插入广告
		if(sContent.indexOf("ctheme_shop_centeradv")>-1)
		{
		list.add("<script type=\"text/javascript\" src=\""+sResourcesLink+"zs/zs.js\"></script>");
		
		list.add("<script type=\"text/javascript\" src=\""+sResourcesLink+"zs/focus/zs_focus_carousel.js\"></script>");
		}
		
		
		list.add("<div class=\"ctheme_shop_preview\">");
		
		
		
		list.add(sContent);
		list.add("</div>");

		return StringUtils.join(list, "");

	}


	private String upTemplateAuto() {
		

		String sReturmString = "";

		String sUid = WebSessionHelper.create().upRequest("uid");
		//MUserInfo mUserInfo = UserFactory.INSTANCE.create();

		MDataMap mDataMap = DbUp.upTable("uc_shop_template").one( "uid", sUid);

		sReturmString = mDataMap.get("template_preview");

		return sReturmString;
	}

}
