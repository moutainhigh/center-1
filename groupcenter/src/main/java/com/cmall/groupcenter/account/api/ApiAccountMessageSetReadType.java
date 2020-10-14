package com.cmall.groupcenter.account.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountMessageSetReadTypeInput;
import com.cmall.groupcenter.util.WebUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微公社消息模块-设置消息为已读接口
 *
 * @author lipengfei
 * @date 2015-6-1
 * email:lipf@ichsy.com
 *
 */
public class ApiAccountMessageSetReadType extends RootApiForToken<RootResultWeb,AccountMessageSetReadTypeInput>{

	public RootResultWeb Process(AccountMessageSetReadTypeInput arg0,
			MDataMap arg1) {
		
		RootResultWeb result = new RootResultWeb();
		
		String userCode  = getUserCode();
		
		String messageType = arg0.getMessageType();
		//新好友加入不属于系统消息fengl
		if(StringUtils.isNotEmpty(messageType)){
//			if("3".equals(messageType)){
//				result.inErrorMessage(918570008);
//				return result;
//			}
			
			String sysMessageCode = WebUtil.getSystemCodeByMessageCode(messageType);
			//新好友加入不属于系统消息fengl
			if(StringUtils.isNotEmpty(sysMessageCode)){
				if("4".equals(sysMessageCode)){//修改该用户下44974720000400010001，44974720000400010002，44974720000400010003

					MDataMap mDataMap = new MDataMap();
					mDataMap.put("user_code", userCode);
					mDataMap.put("type", sysMessageCode);
					mDataMap.put("is_read", "4497465200180002");
					DbUp.upTable("sc_comment_push_single").dataExec("update sc_comment_push_single set is_read='4497465200180002' where user_code=:user_code and type in('44974720000400010001','44974720000400010002','44974720000400010003')",mDataMap);
				}else{
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("user_code", userCode);
					mDataMap.put("type", sysMessageCode);
					mDataMap.put("is_read", "4497465200180002");

					DbUp.upTable("sc_comment_push_single").dataUpdate(mDataMap, "is_read", "user_code,type");
				}
			}else {
				result.setResultCode(0);
				result.setResultMessage("消息类型传参有误,请查看API!");
			}
		}else {
			result.setResultCode(0);
			result.setResultMessage("消息类型不能为空!");
		}
		
		return result;
	}
	

}
