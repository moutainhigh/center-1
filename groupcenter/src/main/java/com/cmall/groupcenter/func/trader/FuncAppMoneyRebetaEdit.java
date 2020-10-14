package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.systemcenter.message.SendMessageBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.MessageSupport;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * 修改返利信息
 *
 *  @author fengl
 * @date 2016-3-9
 * 
 *
 */
public class FuncAppMoneyRebetaEdit  extends RootFunc{

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
		
		String appName=mAddMaps.get("app_name");

		mAddMaps.put("create_time", createDate);
		mAddMaps.put("create_user", createUserCode);		
		mAddMaps.put("apply_time", createDate);
		mAddMaps.put("money_rebate_grade", money_rebate_grade);
		mAddMaps.put("status", "1");
		
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
		MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("trader_code",mAddMaps.get("trader_code"));
		MMessage message=new MMessage();
		
		String content="您申请的"+appName+"应用已审核通过，请及时登录商户平台设置应用返利起止时间，设置成功后"+appName+"应用下的商品将按照新的返利规则返利，  ";
		String title="应用等级变更审核成功";
		
		SendMessageBase base=new SendMessageBase();
		message.setSendSource(base.upSendSourceByManageCode("SI2011"));
		message.setMessageContent(content);
		
		String phone=traderInfo.get("telephone");
		if(phone!=null&&phone.length()>0){
			message.setMessageReceive(phone);
			MessageSupport.INSTANCE.sendMessage(message);
		}
		String email=traderInfo.get("trader_email");
		if(email!=null&&email.length()>0){
			MailSupport.INSTANCE.sendMail(email, title, content);
		}


		return mResult;
	}

}
