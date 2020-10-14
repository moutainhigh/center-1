package com.cmall.newscenter.young.webfunc;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 新增频道名称
 * 
 * @author shiyz
 * 
 */
public class FuncChannelAdd extends RootFunc {


	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String create_user = UserFactory.INSTANCE.create().getLoginName();

		String create_time = FormatHelper.upDateTime();
		
		//App
		String appCode = UserFactory.INSTANCE.create().getManageCode();

		if (checkIsExistChannel(mAddMaps.get("channel_name").toString())) {
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
		
		String channel_code = WebHelper.upCode("CHN");
		
		mAddMaps.put("channel_creattime", create_time);

		mAddMaps.put("channel_creatman", create_user);

		mAddMaps.put("channel_code", channel_code);
		
		mAddMaps.put("app_code", appCode);

		DbUp.upTable("nc_video_channel").dataInsert(mAddMaps);

		return mResult;

	}

	/**
	 * 检验是否有对应的频道
	 * 
	 * @param
	 * @return
	 */
	private boolean checkIsExistChannel(String channel_name) {

		// 判断数据库中是否存在相同记录
		int count = DbUp.upTable("nc_video_channel").count("channel_name",
				channel_name);
		if (count > 0) {
			return true;
		}
		return false;
	}

}