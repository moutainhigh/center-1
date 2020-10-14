package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.txservice.TxTraderInfoCreateService;
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
 * 添加或修改某商户的预存款提醒服务设置
 * @author lipengfei
 * @date 2015-10-10
 * @time 11:00
 * @email:lipengfei217@163.com
 */
public class FuncTraderPreWithdrawAdd extends RootFunc{

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		


		String traderCode = mAddMaps.get("trader_code");

		if(StringUtils.isNotEmpty(traderCode)){
//			mWhereMap.put("trader_code",mAddMaps.get("trader_code"));


            MDataMap withdrawNotifyInfo = DbUp.upTable("gc_pre_withdraw_notify").one("trader_code",traderCode);

//            如果为空，则需要添加
            if (withdrawNotifyInfo==null){


                //开启状态默认为1
                mAddMaps.put("first_notify_open","1");
                mAddMaps.put("second_notify_open","1");
                mAddMaps.put("stop_rebate_notify_open","1");

                String now = CalendarHelper.Date2String(new Date(),"yyyy-MM-dd HH:mm:ss");
                mAddMaps.put("create_time",now);
                DbUp.upTable("gc_pre_withdraw_notify").dataInsert(mAddMaps);

            }else{//否则修改


//                如果没有选择短信 邮箱的发送方式，则设置默认值
                if(mAddMaps.get("send_message_flag")==null || mAddMaps.get("send_message_flag").length()==0){
                    mAddMaps.put("send_message_flag","0");
                }

                if(mAddMaps.get("send_mail_flag")==null || mAddMaps.get("send_mail_flag").length()==0){
                    mAddMaps.put("send_mail_flag","0");
                }

                DbUp.upTable("gc_pre_withdraw_notify").dataUpdate(mAddMaps,"first_notify,second_notify,stop_rebate_notify,send_message_flag,send_mail_flag,first_unenough_bal_notify,sec_unenough_bal_notify","trader_code");

            }

		}
//
//		mResult.setResultCode(0);
//		mResult.setResultMessage("颤抖吧人类！");

		return mResult;

	}
	

}
