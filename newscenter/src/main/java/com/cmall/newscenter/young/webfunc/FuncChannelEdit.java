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
 * 修改频道名称
 * 
 * @author shiyz
 * 
 */
public class FuncChannelEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String chaName = mAddMaps.get("channel_name");
		String uid = mAddMaps.get("uid");
		
		if (checkIsExistChannel(chaName,uid)) {
			// 返回提示信息"存在此频道名称，请重新填写！"
			mResult.setResultCode(941901107);
			mResult.setResultMessage(bInfo(941901107));
			
			return mResult;
		}
		
		//判断权值是否为空
		if (StringUtils.isBlank(mAddMaps.get("channel_page").toString())) {
			mAddMaps.put("channel_page", "0");
		} else if (!StringUtils.isNumeric(mAddMaps.get("channel_page").toString())) {
			mResult.setResultCode(934205107);
			mResult.setResultMessage(bInfo(934205107));
			
			return mResult;
			
		}
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
		String create_time = FormatHelper.upDateTime();
		
		mAddMaps.put("channel_updatetime",create_time);
		
		mAddMaps.put("channel_updateman",create_user);
		
		DbUp.upTable("nc_video_channel").dataUpdate(mAddMaps, "channel_updatetime,channel_updateman,channel_name,channel_page", "zid");
		
		return mResult;
}

	/**
	 * 检验是否有对应的频道
	 * @param chaName 频道名称
	 * @param uid
	 * @return 如果存在：true 否则：false
	 */
	private boolean checkIsExistChannel(String chaName, String uid) {
		// TODO Auto-generated method stub
		// 判断数据库中是否存在相同记录
		String sWhere = "channel_name = '" + chaName + "'" + " and uid != '" + uid +"'";
		int count = DbUp.upTable("nc_video_channel").dataCount(sWhere, new MDataMap());
		if (count > 0) {
			return true;
		}
		return false;
	}
}