package com.cmall.newscenter.webfunc;

import java.util.List;
import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *  对于帖子评论状态进行修改
 * @author houwen
 *
 */
public class postCommentUnStatus extends RootFunc {

	
	private static String TABLE_TPL="nc_posts_comment"; //帖子评论表
	
	
	/**
	 * 
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		
		MDataMap mLogDataMap = new MDataMap();
		MWebResult mResult = new MWebResult();
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
	
		// TODO Auto-generated method stub
		
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		
		mLogDataMap.put("operater_code", userCode);
		mLogDataMap.put("app_code", appCode);
		
		//mLogDataMap.put("operater_nick_name", mDataMap2.get(0).get("nickname"));
		MDataMap mWhereDataMap2 = new MDataMap();
		mWhereDataMap2.put("uid", tplUid);
		
		//获取操作的某条评论内容的相关信息
		List<MDataMap> mDataMap3 = DbUp.upTable("nc_posts_comment").queryAll("", "", "", mWhereDataMap2);
		if(mDataMap3.size()!=0){
			//mLogDataMap.put("comment_content", mDataMap3.get(0).get("comment_content"));
			mLogDataMap.put("comment_title", mDataMap3.get(0).get("post_title"));
			mLogDataMap.put("operate_id", mDataMap3.get(0).get("comment_code"));
			mLogDataMap.put("publisher_code", mDataMap3.get(0).get("publisher_code"));
		}
		mLogDataMap.put("operate_time",DateUtil.getSysDateTimeString());
		//审核通过： 449746800001 ；审核拒绝：449746800002； 待审核：449746800003
		String isDisable = mDataMap.get("zw_f_isDisable");
		if("449746800003".equals(isDisable)){  //禁用的话 记录时间
			
				dataMap.put("status", "449746800002");
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
				mLogDataMap.put("operate_status","449747020002"); //审核通过：449747020001； 审核拒绝：449747020002；敏感词过滤： 449747020001
				mLogDataMap.put("operate_type", "449747010003");  //操作类型：主帖：449747010001；追帖：449747010002；评论：449747010003
				DbUp.upTable("nc_comment_log").dataInsert(mLogDataMap);
		
		}else if("449746800001".equals(isDisable)){
			dataMap.put("status", "449746800002");
		//	dataMap.put("disableDate", FormatHelper.upDateTime());
			DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
			mLogDataMap.put("operate_status","449747020002");
			mLogDataMap.put("operate_type", "449747010003");  //操作类型：主帖：449747010001；追帖：449747010002；评论：449747010003
			DbUp.upTable("nc_comment_log").dataInsert(mLogDataMap);
		}else{
			mResult.setResultCode(934205104);
			mResult.setResultMessage("已是审核拒绝状态！");
		}
		
		return mResult;
	}
	
}
