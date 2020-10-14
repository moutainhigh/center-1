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
 * 功能:精选特惠的修改（主要有一个最多发布数量的限制）
 * @author lipengfei
 * @date 2015-5-19
 * email:lipf@ichsy.com
 *
 */
public class FuncSpecialDealsEdit extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();

		String uid = mDataMap.get("zw_f_uid");
		
		boolean pubAvailable = CheckPubCounts.checkPubAvaliable();
		
		
		if(!pubAvailable){
			
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("最多只能发布10条数据");
			return mWebResult;
			
		}else{
			
			MDataMap whereDataMap  = new MDataMap();
			whereDataMap.put("title", mDataMap.get("zw_f_title"));
			whereDataMap.put("url", mDataMap.get("zw_f_url"));
			whereDataMap.put("deal_describe", mDataMap.get("zw_f_deal_describe"));
			whereDataMap.put("pic_url", mDataMap.get("zw_f_pic_url"));
			whereDataMap.put("if_pub", mDataMap.get("zw_f_if_pub"));
			whereDataMap.put("uid",uid);
			
			
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			String datestr = sdf.format( new Date()); 
			whereDataMap.put("update_date",datestr);
			DbUp.upTable("gc_webchat_special_deals").dataUpdate(whereDataMap,null, "uid");
		}
		
		return mWebResult;
	}

}
