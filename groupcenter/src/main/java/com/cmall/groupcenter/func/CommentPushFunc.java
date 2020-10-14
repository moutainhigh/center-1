package com.cmall.groupcenter.func;

import com.cmall.groupcenter.baidupush.AndroidPushMessageToSingleUser;
import com.cmall.groupcenter.baidupush.IosPushMessageToSingleUser;
import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * @author dyc
 * 百度推送公共调用函数
 * */
public class CommentPushFunc {

	private final static AndroidPushMessageToSingleUser androidPushSingle = new AndroidPushMessageToSingleUser();
	private final static IosPushMessageToSingleUser iosPushSingle = new IosPushMessageToSingleUser();
	/**
	 * 推送消息给指定用户(无论推送是否成功都只推送一次)
	 * */
	public static MWebResult pushToSingleUser(MDataMap info){
		StringBuffer pushRtnMsg = new StringBuffer();//推送消息返回信息
		MWebResult mResult = new MWebResult();
		//校验推送信息参数是否正确
		String tips = checkParams(info);
		if(tips.length()>0){//参数不正确
			mResult.setResultCode(123456);
			mResult.setResultMessage(tips);	
			pushRtnMsg.append(tips);
		}else{
			if(StringUtility.isNotNull(info.get("os"))&&info.get("os").equals("android")){
				//发送通知给安卓用户
				mResult = androidPushSingle.sendNotifyMsg(info);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(mResult.getResultMessage());
				}
			}else{
				//发送通知给IOS用户
				mResult = iosPushSingle.sendNotifyMsg(info);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(";").append(mResult.getResultMessage());
				}
			}
		}
		//消息无论推送是否成功都更新消息状态
		info.put("send_status", "4497465000070002");
		info.put("send_time", DateUtil.getNowTime());
		DbUp.upTable("sc_comment_push_single").dataUpdate(info, "send_status,send_time", "uid");
		
		//记录日志信息
		MDataMap log = new MDataMap();
		log.inAllValues("send_info",info.toString(),"return_info",pushRtnMsg.toString(),"time",DateUtil.getNowTime());
		DbUp.upTable("lc_pushcomment_log").dataInsert(log);
		return mResult;
	}
	
	/**
	 * {title:xxx(必填),content:xxx(必填), params:xxx(必填),apiKey:xxx(必填),secretKey:xxx(必填),deployStatus:xxx(1: Developer 2: Production必填),userId:xxx(必填),channelId:xxx(可填)}
	 * */
	private static String checkParams(MDataMap info){
		if(info!=null){
			
			if(StringUtility.isNull(info.get("content"))){
				return "content is null";
			}
			if(StringUtility.isNull(info.get("params"))){
				return "params is null";
			}
			if(StringUtility.isNull(info.get("apiKey"))){
				return "apiKey is null";
			}
			if(StringUtility.isNull(info.get("secretKey"))){
				return "secretKey is null";
			}
			if(StringUtility.isNull(info.get("deployStatus"))){
				return "deployStatus is null";
			}
			if(StringUtility.isNull(info.get("userId"))){
				return "userId is null";
			}
		}else{
			return "param is null";
		}
		return "";
	}
}
