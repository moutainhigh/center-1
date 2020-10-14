package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * 修改返利信息
 *
 * @author fengl
 * @date 2016-3-9
 * 
 *
 */
public class FuncAppMoneyRebetaSave  extends RootFunc{


	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mAddMaps.get("uid");
		String money_rebate_grade_temp=mAddMaps.get("money_rebate_grade");
		String[] gradeArray= money_rebate_grade_temp.split(",");
		String money_rebate_grade="";
		for(int i=0;i<gradeArray.length;i++){
			if(i==gradeArray.length-1){
				money_rebate_grade+=gradeArray[i];
			}else if((i%2)==0){
				money_rebate_grade+=gradeArray[i]+"-"+gradeArray[i+1]+",";
			}
		}

		//商户创建人
		String createUserCode = UserFactory.INSTANCE.create().getUserCode();

		String createDate = CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss");

		mAddMaps.put("create_time", createDate);
		mAddMaps.put("create_user", createUserCode);		
		mAddMaps.put("apply_time", createDate);
		mAddMaps.put("money_rebate_grade", money_rebate_grade);
		mAddMaps.put("status", "0");
		
		if(uid!=null&StringUtils.isNotBlank(uid)){
			MDataMap mMaps=DbUp.upTable("gc_app_rebate_set").oneWhere("create_time", "", "", "uid",uid);
			String cTime=mMaps.get("create_time");
			if(StringUtils.isNotBlank(cTime)){
				mAddMaps.put("update_time", createDate);
				mAddMaps.put("update_user", createUserCode);
				DbUp.upTable("gc_app_rebate_set").dataUpdate(mAddMaps,"update_time,update_user,apply_time,money_rebate_grade,money_rebate_scale,money_rebate_range,status","uid");	
			};

		}else{
			mAddMaps.put("uid", WebHelper.upUuid());
			mAddMaps.remove("app_name");
			DbUp.upTable("gc_app_rebate_set").dataInsert(mAddMaps);
		}


		return mResult;
	}

}
