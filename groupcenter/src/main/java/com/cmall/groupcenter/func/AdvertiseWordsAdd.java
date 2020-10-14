package com.cmall.groupcenter.func;

import com.cmall.groupcenter.accountmarketing.util.DateFormatUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 广告语添加
 * @author gaozx
 * date 20150421
 * @version 1.0
 **/
public class AdvertiseWordsAdd extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		
		if (mResult.upFlagTrue()) {
			/*系统当前时间*/
			String create_time = DateFormatUtil.getNowTime();
			/*获取当前登录人*/
			String create_user = UserFactory.INSTANCE.create().getLoginName();
			
			//栏目名称
			String column_code = mAddMaps.get("column_code");
			//广告文字
			String ad_prompt = mAddMaps.get("ad_prompt");
			//广告图片
			String adImg = mAddMaps.get("adImg");
			//广告链接
			String adImg_url = mAddMaps.get("adImg_url");
			//开始显示时间
			String start_time = mAddMaps.get("start_time");
			//结束显示时间
			String end_time = mAddMaps.get("end_time");
			//app名称
			String app_code = mAddMaps.get("app_code");
			app_code = null == app_code || "".equals(app_code) ? "SI2011" : app_code;
			
			if(null != ad_prompt && !"".equals(ad_prompt) 
					&& (ad_prompt.length() < 3 || ad_prompt.length() > 10)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("广告语字数只能是3-10字！");
				return mResult;	
			}
			if(null == adImg || "".equals(adImg)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("广告图片不能为空！");
				return mResult;
			}
			if(null == adImg_url || "".equals(adImg_url)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("广告链接不能为空！");
				return mResult;
			}
			if(null == start_time || "".equals(start_time)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("开始显示时间不能为空！");
				return mResult;
			}
			if(null == end_time || "".equals(end_time)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("结束显示时间不能为空！");
				return mResult;
			}
			if(DateFormatUtil.getTimefag(start_time, end_time)) {
				mResult.setResultCode(-1);
				mResult.setResultMessage("开始显示时间不能晚于结束显示时间！");
				return mResult;
			}
			map.put("column_code", column_code);
			map.put("ad_prompt", ad_prompt);
			map.put("adImg", adImg);
			map.put("adImg_url", adImg_url);
			map.put("create_user", create_user);
			map.put("create_time", create_time);
			map.put("start_time", start_time);
			map.put("end_time", end_time);
			map.put("app_code", app_code);
			
			/**插入nc_advertise表中*/
			DbUp.upTable("nc_advertise").dataInsert(map);
		}
		return mResult;
	}

}
