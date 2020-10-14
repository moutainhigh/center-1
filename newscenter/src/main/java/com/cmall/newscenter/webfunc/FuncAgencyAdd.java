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
 * 添加分销商
 * 
 * @author shiyz
 * 
 */
public class FuncAgencyAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String parent_ode = UserFactory.INSTANCE.create().getUserCode();
		
//		String sSql = "select parent_id from nc_agency  where level_number = " +
//				"(select parent_id from nc_agency  where level_number =" +
//				"(select parent_id from nc_agency where level_number = " +
//				"(select parent_id from nc_agency nc " +
//				"where nc.level_number = '"+parent_ode+"' )))";
//
//		List<Map<String, Object>> list = DbUp.upTable("nc_agency").dataSqlList(sSql, new MDataMap());
		
		MDataMap map = DbUp.upTable("nc_agency").one("level_number",parent_ode);
		
		if(!map.get("agent_level").equals("4497464900050005")){
		
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
		
		if(map.get("agent_level").equals("4497464900050002")){
			
			mAddMaps.put("agent_level", "4497464900050003");
			
		}else if(map.get("agent_level").equals("4497464900050003")){
			
			mAddMaps.put("agent_level", "4497464900050004");
			
		}else if(map.get("agent_level").equals("4497464900050004")) {
			
			mAddMaps.put("agent_level", "4497464900050005");
			 
		}

		if (mResult.upFlagTrue()) {

			try {

				
				DbUp.upTable("za_userinfo").dataInsert(mdDataMap);
				
				MDataMap insDataMap = new MDataMap();
				
				insDataMap.put("user_code", level_code);
				
				insDataMap.put("role_code", bConfig("newscenter.role_agenCode"));
				if(!mAddMaps.get("agent_level").equals("4497464900050005")){
				DbUp.upTable("za_userrole").dataInsert(insDataMap);
				}
				DbUp.upTable("nc_agency").dataInsert(mAddMaps);

				
			} catch (Exception e) {

				e.printStackTrace();
				
			}

		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}else{
		
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969912011));
		}
		return mResult;
		
	}
		
}
}