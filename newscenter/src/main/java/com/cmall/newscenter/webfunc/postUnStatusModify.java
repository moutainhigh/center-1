package com.cmall.newscenter.webfunc;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 对于帖子状态进行修改  审核拒绝 向操作日志中添加数据并向发布人发送消息
 * @author houwen
 *
 */
public class postUnStatusModify extends RootFunc {

	
	private static String TABLE_TPL="nc_posts"; //姐妹圈帖子表
	
	/**
	 * 
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		//获取登录人信息  登录人
		String userCode = "";
		String loginName = "";
		String appCode = "";
		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			userCode = UserFactory.INSTANCE.create().getUserCode();  //获取usercode
			loginName = UserFactory.INSTANCE.create().getLoginName();  //获取当前登录名
			appCode = UserFactory.INSTANCE.create().getManageCode();
		}
		String tplUid = mDataMap.get("zw_f_uid");
		String message = mDataMap.get("zw_f_message");
		//String postCode = mDataMap.get("zw_f_post");
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		// 审核通过 ：449746730001、审核拒绝：449746730002
		String isDisable = mDataMap.get("zw_f_status");
		String publisher = mDataMap.get("zw_f_publisher");
		
		if("449746730001".equals(isDisable)){  //审核通过
			
			dataMap.put("status", "449746730002");
			DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
			//更新操作日志
			postStatusModify postStatusModify = new postStatusModify();
			postStatusModify.updateOperateLog(tplUid, "449747020002",userCode,appCode); //审核通过：449747020001； 审核拒绝：449747020002；敏感词过滤： 449747020001
			//审核拒绝时向发布人发送消息 ,如果输入内容为空，则不向发送人发送消息
			if(message!=null && !message.equals("")){
			
			String messageCode = WebHelper.upCode("XX");
			String dateTime = DateUtil.getSysDateTimeString();
			DbUp.upTable("nc_message_info").insert("message_code", messageCode,"message_info",message,"message_type","449746910002","member_send",publisher,"send_time",dateTime,"manage_code",UserFactory.INSTANCE.create().getManageCode());
			DbUp.upTable("nc_system_message").insert("message_code", messageCode,"message_info",message,"message_type","449746910002","member_send",publisher,"send_time",dateTime,"manage_code",UserFactory.INSTANCE.create().getManageCode());
			}
		
		}else if("449746730002".equals(isDisable)){
			
			mResult.setResultCode(934205104);
			mResult.setResultMessage("状态已为审核拒绝！");
		}

		return mResult;
	}

}
