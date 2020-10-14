package com.cmall.groupcenter.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 邮件通用功能
 * @author panwei
 *
 */
public class WgsMailSupport extends BaseClass implements IBaseInstance{
	
	public final static WgsMailSupport INSTANCE = new WgsMailSupport();

	/**
	 * 发送邮件
	 * @param configName 业务配置名称
	 * @param title 邮件标题
	 * @param content 邮件内容
	 */
	public void sendMail(String configName,String title,String content){
		MDataMap configMap=DbUp.upTable("gc_business_name_config").one("business_name",configName);
			if(configMap!=null){
				MDataMap mWhereMap=new MDataMap();
				mWhereMap.put("business_code", configMap.get("business_code"));
				List<MDataMap> emailList=DbUp.upTable("gc_business_email_config").queryAll(
						"", "", "business_code=:business_code", mWhereMap);
				
				if(emailList!=null){
					for(MDataMap email:emailList){
						String receive=email.get("email");
						if(StringUtils.isNotBlank(receive)){
		  					MailSupport.INSTANCE.sendMail(receive, title,content);
		  				}
					}
				}
				
			}
	}
}
