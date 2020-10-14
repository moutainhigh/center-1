package com.cmall.newscenter.young.webfunc;


import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改视频信息
 * 
 * @author shiyz
 * 
 */
public class FuncRecreationEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
		String create_time = FormatHelper.upDateTime();
		
		mAddMaps.put("recreation_updatetime",create_time);
		
		mAddMaps.put("recreation_man",create_user);
		
		String channel_code=mAddMaps.get("recreation_code");
		String js_code=mAddMaps.get("js_code");
		String recreation_url=mAddMaps.get("recreation_url");
		String playing_time=mAddMaps.get("playing_time");
        String url=bConfig("cyoung.shareLink")+"/cyoung/web/video/video.ftl?videoId="+channel_code;

		if(!(StringUtils.isBlank(js_code))){
			recreation_url=url;
		}
		
		//如果播放时长输入中文字符"：",怎将中文字符"："改为英文字符":".
		if (playing_time.indexOf("：")!=-1) {
	         String new_time=playing_time.replaceAll("：", ":");
			  mAddMaps.put("playing_time", new_time);
		}
		
       if("".equals(mAddMaps.get("recreation_updatesum"))){
			
			mAddMaps.put("recreation_updatesum", String.valueOf(0));
			
		}
       mAddMaps.put("recreation_url",recreation_url);
     //判断权值是否为空
     if (StringUtils.isBlank(mAddMaps.get("recreation_weight").toString())) {
     	mAddMaps.put("recreation_weight", "0");
     } else if (!StringUtils.isNumeric(mAddMaps.get("recreation_weight").toString())) {
     	mResult.setResultCode(934205107);
     	mResult.setResultMessage(bInfo(934205107));
     	return mResult;
     	}
     
		DbUp.upTable("nc_recreation").update(mAddMaps);
		
		
		return mResult;

}
}