package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *
 * @author lipengfei
 * @date 2015-10-09
 * @time 17:25
 * @email:lipengfei217@163.com
 */
public class TraderOpenPreWithdrawNotifyInput extends RootInput {


        @ZapcomApi(value = "开启类型", remark = "first:第一次提醒，second:第二次提醒,stoprebate:停止返利提醒", demo = "stoprebate")
    private String openType;

    @ZapcomApi(value = "开启或关闭", remark = "0:关闭，1：开启", demo = "0")
    private String openValue;

    @ZapcomApi(value = "商户编码", remark = "商户编码", demo = "SG11150909100001")
    private String traderCode;


    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getOpenValue() {
        return openValue;
    }

    public void setOpenValue(String openValue) {
        this.openValue = openValue;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }


}
