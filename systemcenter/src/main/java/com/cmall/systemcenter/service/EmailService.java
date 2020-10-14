package com.cmall.systemcenter.service;

import com.cmall.systemcenter.model.EmailModel;
import com.srnpr.zapcom.baseclass.BaseClass;

/**   
*    
* 项目名称：systemcenter   
* 类名称：EmailService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 上午11:32:05   
* 修改人：yanzj
* 修改时间：2013-9-2 上午11:32:05   
* 修改备注：   
* @version    
*    
*/
public class EmailService extends BaseClass {
	
	/**
	 * 发送邮件,插入到表中，定时器发送,如果失败，记录日志，不予处理，不能影响主程序
	 * @param title 标题
	 * @param content 内容
	 * @param mailTo 邮寄到
	 */
	public void SendEmail(String title,String content,String mailTo)
	{
		try {
			
		} catch (Exception e) {
			bLogError(949701001, mailTo);
		}
	}
	
	/**
	 * 发送邮件,插入到表中，定时器发送,如果失败，记录日志，不予处理，不能影响主程序
	 * @param em
	 */
	public void SendEmail(EmailModel em)
	{
		try {
			
		} catch (Exception e) {
			bLogError(949701001, em.getEmailAddress());
		}
	}

}
