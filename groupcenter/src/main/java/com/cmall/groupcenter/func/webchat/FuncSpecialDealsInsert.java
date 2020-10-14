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
 * 新增，有最多发布数量的限制
 * @author lipengfei
 * @date 2015-5-19
 * email:lipf@ichsy.com
 *
 */
public class FuncSpecialDealsInsert extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		String if_pub = mDataMap.get("zw_f_if_pub");
		
		if("4497472000030001".equals(if_pub)){
			boolean pubAvailable = CheckPubCounts.checkPubAvaliable();
			
			//如果超过已有一定数量的已发布的作品，不允许
			if(!pubAvailable){
				
				mWebResult.setResultCode(-1);
				mWebResult.setResultMessage("最多只能发布10条数据");
				return mWebResult;
			}
		}
		
			MDataMap whereDataMap  = new MDataMap();
			whereDataMap.put("title", mDataMap.get("zw_f_title"));
			whereDataMap.put("url", mDataMap.get("zw_f_url"));
			whereDataMap.put("deal_describe", mDataMap.get("zw_f_deal_describe"));
			whereDataMap.put("pic_url", mDataMap.get("zw_f_pic_url"));
			whereDataMap.put("if_pub", mDataMap.get("zw_f_if_pub"));
			
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datestr = sdf.format( new Date()); 
			whereDataMap.put("create_date",datestr);
			whereDataMap.put("update_date",datestr);
			DbUp.upTable("gc_webchat_special_deals").dataInsert(whereDataMap);
		
		return mWebResult;
	}

}
