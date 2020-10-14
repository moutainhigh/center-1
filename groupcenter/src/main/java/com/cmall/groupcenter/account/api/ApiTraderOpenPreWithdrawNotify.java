package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.TraderOpenPreWithdrawNotifyInput;
import com.cmall.groupcenter.account.model.TraderOpenPreWithdrawNotifyResult;
import com.cmall.groupcenter.account.model.TraderPreWithdrawInfoInput;
import com.cmall.groupcenter.account.model.TraderPreWithdrawInfoResult;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

/**
 *  开启预存款消息提醒服务的接口
 * @author lipengfei
 * @date 2015-10-09
 * @time 15:20
 * @email:lipengfei217@163.com
 */
public class ApiTraderOpenPreWithdrawNotify extends RootApiForManage<TraderOpenPreWithdrawNotifyResult,TraderOpenPreWithdrawNotifyInput> {

    public TraderOpenPreWithdrawNotifyResult Process(TraderOpenPreWithdrawNotifyInput traderOpenPreWithdrawNotifyInput, MDataMap mDataMap) {

        TraderOpenPreWithdrawNotifyResult result = new TraderOpenPreWithdrawNotifyResult();

        String traderCode = traderOpenPreWithdrawNotifyInput.getTraderCode();

        String openType = traderOpenPreWithdrawNotifyInput.getOpenType();

        String openValue = traderOpenPreWithdrawNotifyInput.getOpenValue();

        MDataMap dataMap = DbUp.upTable("gc_pre_withdraw_notify").one("trader_code", traderCode);

        if(dataMap!=null){
            if (StringUtils.isNotEmpty(traderCode)){

                //需要修改的字段
                String updateFiled=null;

//            第一次提醒开启与否
                if ("first".equals(openType)){

                    updateFiled="first_notify_open";
//                第二次提醒开启与否
                }else if ("second".equals(openType)){

                    updateFiled="second_notify_open";
//                第三次提醒开启与否
                }else if ("stoprebate".equals(openType)){
                    updateFiled="stop_rebate_notify_open";
                }else if ("unenoughBalNotify".equals(openType)){
                    updateFiled="first_unenough_bal_notify_open";
                }else if("secUnenoughBalNotify".equals(openType)){
                    updateFiled="sec_unenough_bal_notify_open";
                }

                if (updateFiled!=null && StringUtils.isNotEmpty(openValue)){
                    //修改相应字段的状态值
                    MDataMap theDataMap = new MDataMap();
                    theDataMap.put(updateFiled,openValue);
                    theDataMap.put("trader_code",traderCode);
//                MDataMap mDataMap, String sUpdateFields, String sWhereFields
                    DbUp.upTable("gc_pre_withdraw_notify").dataUpdate(theDataMap,updateFiled,"trader_code");
                    result.setResultMessage("修改成功");
                    result.setResultCode(1);
                    result.setOpenType(openType);
                    result.setOpenValue(openValue);
                }else {
                    result.setResultCode(0);
                    result.setResultMessage("参数openType或openValue有误");
                }
            }else{
                result.setResultCode(0);
                result.setResultMessage("traderCode不能为空");
            }
        }else{
            result.setResultCode(0);
            result.setResultMessage("请先保存预存款提醒服务设置");
        }

        return result;
    }
}
