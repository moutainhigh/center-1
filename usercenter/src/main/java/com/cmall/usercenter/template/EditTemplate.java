package com.cmall.usercenter.template;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;

public class EditTemplate extends BaseClass {

	public String upExtendJs() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("<script>zs.r([ \"zs/template/zs_template\" ], function(a) {");

		MUserInfo mUserInfo = UserFactory.INSTANCE.create();

		
		sBuilder.append("zs_template.temp.customlink.code_seller=\""
				+ mUserInfo.getManageCode() + "\";");
		sBuilder.append("zs_template.temp.customlink.code_sku=\""
				+  bConfig("productcenter.shop_sku")+ "\";");
		
		sBuilder.append("zs_template.temp.customlink.link_favorite=\""
				+  bConfig("usercenter.shop_url")+ "\";");
		

		sBuilder.append("});</script>");
		return sBuilder.toString();

	}
	
	
	
	
	/**
	 * 获取可操作类型
	 * @return
	 */
	public String upDoType()
	{
		String sReturnString="";
		
		WebSessionHelper webSessionHelper=WebSessionHelper.create();
		
		String sTemplateUid=webSessionHelper.upRequest(WebConst.CONST_WEB_FIELD_NAME+ "uid");
		
		if(StringUtils.isNotEmpty(sTemplateUid))
		{
			
			MDataMap mTemplate=DbUp.upTable("uc_shop_template").one("uid",sTemplateUid);
			String sUserCode=mTemplate.get("seller_code");
			
			MDataMap mUserMap=DbUp.upTable("uc_sellerinfo").one("seller_code",sUserCode);
			
			//判断用户是否有权限修改模板
			if(!mUserMap.get("seller_type").equals("449746390001"))
			{
				sReturnString="no_access";
			}
			else {
				
				String sTempTypeDid=mTemplate.get("template_edit_did");
				
				if(sTempTypeDid.equals("449746360002"))
				{
					sReturnString="readonly";
				}
				else
				{
					sReturnString="edit";
				}
				
				
				
			}
			
			
			
			
			
			
		}
		
		
		
		
		
		
		return sReturnString;
		
		
	}
	
	
	
	

}
