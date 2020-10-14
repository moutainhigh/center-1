package com.cmall.newscenter.webfunc;

import java.util.List;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 对于帖子状态进行修改
 * @author houwen
 *
 */
public class postStatusModify extends RootFunc {

	
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
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		// 审核通过 ：449746730001、审核拒绝：449746730002
		String isDisable = mDataMap.get("zw_f_isDisable");
		if("449746730002".equals(isDisable)){  //审核通过
			
				dataMap.put("status", "449746730001");
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
				//更新操作日志
				this.updateOperateLog(tplUid, "449747020001",userCode,appCode); //审核通过：449747020001； 审核拒绝：449747020002；敏感词过滤： 449747020001
		
		}else if("449746730001".equals(isDisable)){
			
			mResult.setResultCode(934205104);
			mResult.setResultMessage("状态已为审核通过！");
		}

		return mResult;
	}
	
	public void updateOperateLog(String tplUid,String status,String userCode,String appCode){
	
		MDataMap mWhereDataMap2 = new MDataMap();
		MDataMap mLogDataMap = new MDataMap();
		mWhereDataMap2.put("uid", tplUid);
		
		//获取操作的某条评论内容的相关信息
		List<MDataMap> mDataMap3 = DbUp.upTable("nc_posts").queryAll("", "", "", mWhereDataMap2);
		if(mDataMap3.size()!=0){
		if(mDataMap3.get(0).get("post_type").equals("449746780001")){
			mLogDataMap.put("operate_type", "449747010001"); //内容类型：主帖：449747010001；追帖：449747010002；评论：449747010003

		}else if(mDataMap3.get(0).get("post_type").equals("449746780002")) {
			mLogDataMap.put("operate_type", "449747010002"); //内容类型：主帖：449747010001；追帖：449747010002；评论：449747010003

		}
		mLogDataMap.put("comment_title", mDataMap3.get(0).get("post_title"));
		mLogDataMap.put("operate_id", mDataMap3.get(0).get("post_code"));
		mLogDataMap.put("publisher_code", mDataMap3.get(0).get("publisher_code"));
		}
		mLogDataMap.put("operater_code", userCode);
		mLogDataMap.put("operate_time",DateUtil.getSysDateTimeString());
		mLogDataMap.put("operate_status", status); 
		mLogDataMap.put("app_code", appCode);
		DbUp.upTable("nc_comment_log").dataInsert(mLogDataMap);
		
	}

}
