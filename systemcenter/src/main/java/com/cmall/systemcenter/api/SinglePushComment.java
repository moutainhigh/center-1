package com.cmall.systemcenter.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * @author dyc
 * 单一用户推送信息
 * */
public class SinglePushComment {

	/**
	 * 新增推送信息
	 * @param 
	 * */
	public static void addPushComment(AddSinglePushCommentInput inputParam) {		
		String preSendTime = DateUtil.getSysDateTimeString();
		if(StringUtils.isNotBlank(inputParam.getPreSendTime())){
			preSendTime = inputParam.getPreSendTime();
		}
		MDataMap param = new MDataMap();
		param.put("title", inputParam.getTitle());
		param.put("content", inputParam.getContent());
		param.put("user_code", inputParam.getUserCode());
		param.put("properties", inputParam.getProperties());
		param.put("pre_send_time", preSendTime);
		param.put("create_time", DateUtil.getSysDateTimeString());
		param.put("app_code", inputParam.getAppCode());
		param.put("type", inputParam.getType());
		param.put("account_code", inputParam.getAccountCode());
		if(StringUtils.isNotBlank(inputParam.getSendStatus())){
			param.put("send_status", inputParam.getSendStatus());
		}
		else{
			param.put("send_status", "4497465000070001");//默认未完成
		}
		param.put("relation_code", inputParam.getRelationCode());//关联编号

		DbUp.upTable("sc_comment_push_single").dataInsert(param);
		
	}

}
