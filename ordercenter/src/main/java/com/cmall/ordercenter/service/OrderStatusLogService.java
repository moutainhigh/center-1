package com.cmall.ordercenter.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.model.OrderStatusLog;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**   
*    
* 项目名称：ordercenter   
* 类名称：OrderStatusLogService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 下午6:48:49   
* 修改人：yanzj
* 修改时间：2013-9-2 下午6:48:49   
* 修改备注：   
* @version    
*    
*/
public class OrderStatusLogService extends BaseClass {
	
	/**
	 * @param statusLog
	 */
	public void AddOrderStatusLogService(OrderStatusLog statusLog)
	{
		try
		{
			UUID uuid = UUID.randomUUID();
			
			MDataMap insertDatamap = new MDataMap();
			
			insertDatamap.put("uid", uuid.toString().replace("-", ""));
			insertDatamap.put("code", statusLog.getCode());
			insertDatamap.put("info", statusLog.getInfo());
			insertDatamap.put("create_time", statusLog.getCreateTime());
			insertDatamap.put("create_user", statusLog.getCreateUser());
			insertDatamap.put("old_status", statusLog.getOldStatus());
			insertDatamap.put("now_status", statusLog.getNowStatus());
			insertDatamap.put("info", "OrderStatusLogService");
			DbUp.upTable("lc_orderstatus").dataInsert(insertDatamap);
		}
		catch(Exception ex)
		{
			bLogError(939301006, statusLog.getCode());
		}
	}
	
	/**
	 * 获取操作人登录名
	 * @param cucode
	 * @return
	 */
	public String converCreateUser(String cucode){
		
		//用户
		if(StringUtils.startsWith(cucode, "MI")){
			MDataMap dataMap = DbUp.upTable("mc_login_info").oneWhere("login_name", "", "member_code=:member_code", "member_code",cucode);
			if(dataMap!=null&&!dataMap.isEmpty()){
				return "[用户]"+dataMap.get("login_name");
			}
			return "";
		}
		
		//后台
		if(StringUtils.startsWith(cucode, "UI")){
			MDataMap dataMap = DbUp.upTable("za_userinfo").oneWhere("user_name", "", "user_code=:user_code", "user_code",cucode);
			if(dataMap!=null&&!dataMap.isEmpty()){
				return "[后台]"+dataMap.get("user_name");
			}
			return "";
		}
		
		return "[系统]"+cucode;
	}
	
}
