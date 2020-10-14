package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *
 * @author lipengfei
 * @date 2015-10-09
 * @time 17:25
 * @email:lipengfei217@163.com
 */
public class TraderOpenPreWithdrawNotifyResult extends RootResultWeb {

    @ZapcomApi(value = "开启类型", remark = "first:第一次提醒，second:第二次提醒,stoprebate:停止返利提醒", demo = "stoprebate")
    private String openType;

    @ZapcomApi(value = "开启或关闭", remark = "0:关闭，1：开启", demo = "0")
    private String openValue;

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
}
