package com.cmall.newscenter.webfunc;


import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加一级分销商
 * 
 * @author shiyz
 * 
 */
public class AgencyManagerAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String parent_ode = UserFactory.INSTANCE.create().getUserCode();
		
		// 将用户信息插入数据库
		MDataMap mdDataMap = new MDataMap();

		String level_code = WebHelper.upCode("UI");

		String sReplace = SecrurityHelper.MD5Customer(bConfig("newscenter.pass_word"));

		mdDataMap.put("user_name", level_code);

		mdDataMap.put("user_password", sReplace);

		mdDataMap.put("flag_enable", "0");

		mdDataMap.put("manage_code", UserFactory.INSTANCE.create()
				.getManageCode());

		mdDataMap.put("create_time", FormatHelper.upDateTime());

		mdDataMap.put("real_name", mAddMaps.get("agent_name"));

		mdDataMap.put("user_type_did", bConfig("newscenter.user_typeId"));

		mdDataMap.put("user_code", level_code);

		mdDataMap.put("email_address", "");

		mAddMaps.put("parent_id", parent_ode);

		mAddMaps.put("level_number", level_code);

		mAddMaps.put("examine_agent", parent_ode);

		mAddMaps.put("manage_code", UserFactory.INSTANCE.create()
				.getManageCode());

		mAddMaps.put("agent_stauts", "4497172100030001");
		
		/*一级代理商*/
		mAddMaps.put("agent_level", "4497464900050002");

		if (mResult.upFlagTrue()) {

			try {

				DbUp.upTable("za_userinfo").dataInsert(mdDataMap);
				
				MDataMap insDataMap = new MDataMap();
				
				insDataMap.put("user_code", level_code);
				
				insDataMap.put("role_code", bConfig("newscenter.role_agenCode"));
				
				DbUp.upTable("za_userrole").dataInsert(insDataMap);
				
				DbUp.upTable("nc_agency").dataInsert(mAddMaps);

			} catch (Exception e) {

				e.printStackTrace();
				
			}

		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
		
}
