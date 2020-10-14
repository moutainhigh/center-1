package com.cmall.productcenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapzero.root.RootJmsListenser;

/**
 * 处理搜索关键词存入数据库方法
 * @author zhouguohui
 *
 */
public class SearchKeyWordService  extends RootJmsListenser{

	public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
		if(mPropMap!=null){
			MDataMap mDataMap = new MDataMap();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(mPropMap.get("sellerCode")!=null && !mPropMap.get("sellerCode").equals("")){
				mDataMap.put("seller_code", mPropMap.get("sellerCode").toString());
			}
			
			if(mPropMap.get("keyWord")!=null && !mPropMap.get("keyWord").equals("")){
				mDataMap.put("key_word", mPropMap.get("keyWord").toString());
			}
			
			if(mPropMap.get("userName")!=null && !mPropMap.get("userName").equals("")){
				mDataMap.put("user_name", mPropMap.get("userName").toString());
			}
			
			if(mPropMap.get("source")!=null && !mPropMap.get("source").equals("")){
				mDataMap.put("source", mPropMap.get("source").toString());
			}
			mDataMap.put("crate_time", sdf.format(new Date()));
			DbUp.upTable("pc_keyword_search").dataInsert(mDataMap);
		}
		
		return true;
	}

}
