package com.cmall.newscenter.api;

import java.text.SimpleDateFormat;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.newscenter.model.ActivityRegisterInput;
import com.cmall.newscenter.model.ActivityRegisterResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动-报名Api
 * 
 * @author yangrong 
 * @version1.0
 */
public class ActivityRegisterApi extends
		RootApiForToken<ActivityRegisterResult, ActivityRegisterInput> {

	public ActivityRegisterResult Process(ActivityRegisterInput inputParam,MDataMap mRequestMap) {

		ActivityRegisterResult result = new ActivityRegisterResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();

			mDataMap = DbUp.upTable("nc_registration").one("info_code",inputParam.getActivity(), "info_member", getUserCode());

			/* 查询用户资讯活动报名信息 */
			MDataMap mDataMap2 = DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());

			/* 查询用户活动信息 */
			MDataMap mDataMap3 = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode());

			if (mDataMap == null) {

				// 将报名信息插入报名表中
				mDataMap.put("info_code",inputParam.getActivity());
				mDataMap.put("info_member", getUserCode());
				mDataMap.put("manage_code", getManageCode());
				mDataMap.put("registration_time",FormatHelper.upDateTime());
				mDataMap.put("is_enable", "1");
				if (mDataMap2 != null) {

					if (!mDataMap2.get("info_title").equals("")) {

						mDataMap.put("info_nickname",mDataMap2.get("info_title"));
					}
				}

				if (mDataMap3 != null) {

					if (!mDataMap3.get("mobile_phone").equals("")) {

						mDataMap.put("telephone", mDataMap3.get("mobile_phone"));
					}

					if (!mDataMap3.get("member_level").equals("")) {

						mDataMap.put("info_class",mDataMap3.get("member_level"));
					}

				}
				DbUp.upTable("nc_registration").dataInsert(mDataMap);
				
			} else {
				if (mDataMap.get("is_enable").equals("0")) {

					mDataMap.put("is_enable", "1");
					DbUp.upTable("nc_registration").dataUpdate(mDataMap,"is_enable", "zid");

				}
			}

			ScoredChange scored = new ScoredChange();

			scored = new ScoredSupport().activitiesScored(getUserCode(),
					inputParam.getActivity());

			// 插入消息表
			MDataMap minfoMap = new MDataMap();

			minfoMap = DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式

			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("info_code", inputParam.getActivity());

			MPageData mPageData = DataPaging.upPageData("nc_registration", "","", mWhereMap, new PageOption());

			// 参数 ————活动id 活动标题 报名人数 报名时间 发起人编号 报名人编号
			MessageRule.MessageApplyRule(inputParam.getActivity(),minfoMap.get("info_title"), mPageData.getListData().size(),minfoMap.get("create_time"), minfoMap.get("create_member"),getUserCode(), getManageCode());

			result.setScored(scored);
		}
		return result;
	}

}
