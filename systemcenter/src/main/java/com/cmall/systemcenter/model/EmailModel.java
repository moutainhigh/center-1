package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**   
*    
* 项目名称：systemcenter   
* 类名称：EmailModel   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 上午11:27:57   
* 修改人：yanzj
* 修改时间：2013-9-2 上午11:27:57   
* 修改备注：   
* @version    
*    
*/
public class EmailModel extends BaseClass {
	
	private String emailTitle = "";
	private String emailContent="";
	private String emailAddress = "";
	
	
	
	public String getEmailTitle() {
		return emailTitle;
	}
	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	public String getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
	
}
