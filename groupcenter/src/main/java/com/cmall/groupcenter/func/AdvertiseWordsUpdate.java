package com.cmall.groupcenter.func;

import com.cmall.groupcenter.accountmarketing.util.DateFormatUtil;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 广告语修改
 * @author gaozx
 * date 20150421
 * @version 1.0
 **/
public class AdvertiseWordsUpdate extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			//uid
			String uid = mAddMaps.get("uid");
			/*系统当前时间*/
			String create_time = DateUtil.getNowTime();
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
			MDataMap dataMap = DbUp.upTable("nc_advertise").one("uid",uid);
			dataMap.put("column_code", column_code);
			dataMap.put("ad_prompt", ad_prompt);
			dataMap.put("adImg", adImg);
			dataMap.put("adImg_url", adImg_url);
			dataMap.put("start_time", start_time);
			dataMap.put("end_time", end_time);
			dataMap.put("app_code", app_code);
			//系统添加
			dataMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());
			dataMap.put("update_time ", DateUtil.getNowTime());
			
			/**更新nc_advertise*/
			DbUp.upTable("nc_advertise").dataUpdate(dataMap, "", "uid");
		}
		return mResult;
	}

}
