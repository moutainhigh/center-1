package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.TraderPreWithdrawInfoInput;
import com.cmall.groupcenter.account.model.TraderPreWithdrawInfoResult;
import com.cmall.groupcenter.report.model.ReportBlackInput;
import com.cmall.groupcenter.report.model.ReportBlackResult;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *通过traderCode获取预存款管理的数据
 * @author lipengfei
 * @date 2015-10-09
 * @time 15:20
 * @email:lipengfei217@163.com
 */
public class ApiGetTraderPreWithdrawInfo extends RootApiForManage<TraderPreWithdrawInfoResult,TraderPreWithdrawInfoInput> {

    public TraderPreWithdrawInfoResult Process(TraderPreWithdrawInfoInput inputParam, MDataMap mRequestMap) {

        TraderPreWithdrawInfoResult result = new TraderPreWithdrawInfoResult();

        //获取traderCode的值
        String traderCode = inputParam.getTraderCode();
        MDataMap dataMap = DbUp.upTable("gc_pre_withdraw_notify").one("trader_code", traderCode);

        if (dataMap!=null){

            result.setFirstNotify(StringHelper.getStringFromMap(dataMap, "first_notify"));
            result.setFirstNotifyFlag(StringHelper.getStringFromMap(dataMap, "first_notify_flag"));
            result.setFirstNotifyOpen(StringHelper.getStringFromMap(dataMap, "first_notify_open"));
            result.setSecondNotify(StringHelper.getStringFromMap(dataMap, "second_notify"));
            result.setSecondNotifyFlag(StringHelper.getStringFromMap(dataMap, "second_notify_flag"));
            result.setSecondNotifyOpen(StringHelper.getStringFromMap(dataMap, "second_notify_open"));
            result.setStopRebateNotify(StringHelper.getStringFromMap(dataMap, "stop_rebate_notify"));
            result.setStopRebateNotifyFlag(StringHelper.getStringFromMap(dataMap, "stop_rebate_notify_flag"));
            result.setStopRebateNotifyOpen(StringHelper.getStringFromMap(dataMap, "stop_rebate_notify_open"));
            result.setSendMailFlag(StringHelper.getStringFromMap(dataMap, "send_mail_flag"));
            result.setSendMessageFlag(StringHelper.getStringFromMap(dataMap, "send_message_flag"));

            result.setFirstUnenoughBalNotify(StringHelper.getStringFromMap(dataMap, "first_unenough_bal_notify"));
            result.setFirstUnenoughBalFlag(StringHelper.getStringFromMap(dataMap, "first_unenough_bal_flag"));
            result.setFirstUnenoughBalNotifyOpen(StringHelper.getStringFromMap(dataMap, "first_unenough_bal_notify_open"));

            result.setSecUnenoughBalNotify(StringHelper.getStringFromMap(dataMap, "sec_unenough_bal_notify"));
            result.setSecUnenoughBalFlag(StringHelper.getStringFromMap(dataMap, "sec_unenough_bal_notify_flag"));
            result.setSecUnenoughBalNotifyOpen(StringHelper.getStringFromMap(dataMap, "sec_unenough_bal_notify_open"));

        }else {
//            result.setResultCode(0);
//            result.setResultMessage("木有数据惹");
        }

        return result;
    }
}
