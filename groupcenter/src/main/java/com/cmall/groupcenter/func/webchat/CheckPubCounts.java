package com.cmall.groupcenter.func.webchat;

import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


/**
 * 一个工具类， 主要是用来检测当前已发布的
 * 
 *
 * @author lipengfei
 * @date 2015-5-20
 * email:lipf@ichsy.com
 *
 */
public class CheckPubCounts {
	
			/**
			 * 检查是否可以发布
			 * <p>如果目前已经有counts个处于已发布状态，则返回false
			 * @author lipengfei
			 * @date 2015-5-20
			 * @return
			 */
			public static boolean checkPubAvaliable(int counts){
				
				boolean isValid = true;
				
				//当前为未发布，修改为发布状态时，需要检查已发布的作品是否超过了10条数据，如果超过了，则不允许发布。
				List<MDataMap>  listResults=DbUp.upTable("gc_webchat_special_deals").queryByWhere("if_pub","4497472000030001");
				
				if(listResults!=null && listResults.size()>=counts){
					isValid = false;
				}
				
				return isValid;
			}
			
			
			/**
			 * 检查是否可以发布
			 * <p>如果目前已经有10个处于已发布状态，则返回false
			 * @author lipengfei
			 * @date 2015-5-20
			 * @return
			 */
			public static boolean checkPubAvaliable(){
				return checkPubAvaliable(10);
			}
			
}
