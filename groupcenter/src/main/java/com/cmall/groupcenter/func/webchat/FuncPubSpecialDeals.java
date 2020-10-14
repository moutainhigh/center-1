package com.cmall.groupcenter.func.webchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


/**
 * 
 * 模块:微信公众号--精选特惠
 * 功能:精选特惠的发布与不发布
 * @author lipengfei
 * @date 2015-5-19
 * email:lipf@ichsy.com
 *
 */
public class FuncPubSpecialDeals extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		String status = mDataMap.get("zw_f_if_pub");
		String uid = mDataMap.get("zw_f_uid");
		MDataMap whereDataMap  = new MDataMap();
		
		
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String datestr = sdf.format( new Date()); 
		whereDataMap.put("update_date",datestr);
		
		if("4497472000030001".equals(status)) {//当前为发布状态，修改为未发布
				whereDataMap.put("if_pub", "4497472000030002");
				whereDataMap.put("uid", uid);
				DbUp.upTable("gc_webchat_special_deals").dataUpdate(whereDataMap, null, "uid");
		
		} else if("4497472000030002".equals(status)) {
			
			boolean pubAvailable = CheckPubCounts.checkPubAvaliable();
			
			//超过十条了， 不允许发布
			if(!pubAvailable){
				
				mWebResult.setResultCode(-1);
				mWebResult.setResultMessage("最多只能发布10条");
				return mWebResult;
				
			}else{
				whereDataMap.put("if_pub", "4497472000030001");
				whereDataMap.put("uid", uid);
				
				
				whereDataMap.put("update_date",datestr);
				
				DbUp.upTable("gc_webchat_special_deals").dataUpdate(whereDataMap,null, "uid");
			}
		}
		return mWebResult;
	}

}
