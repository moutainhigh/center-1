package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 消息模块-消息列表接口
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageListInput extends RootInput {


    @ZapcomApi(value = "消息列表类型",remark = "0:将消息列表分门别类，即分为好消息、坏消息、系统消息来输出.1:将所有消息融合为系统消息")
	private String messageListType="0";

    public String getMessageListType() {
        return messageListType;
    }

    public void setMessageListType(String messageListType) {
        this.messageListType = messageListType;
    }
}
