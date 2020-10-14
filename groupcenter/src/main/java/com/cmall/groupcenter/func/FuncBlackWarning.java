package com.cmall.groupcenter.func;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


/**
 * 
 * 模块:黑名单警告
 * @author panwei
 *
 */
public class FuncBlackWarning extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		
		String reporter = mDataMap.get("zw_f_be_report_user");
		
		MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",reporter,"manage_code","SI2011","flag_enable","1");
		String sAccountCode=memberMap.get("account_code");
		AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
		addSinglePushCommentInput.setAccountCode(sAccountCode);
		addSinglePushCommentInput.setAppCode("SI2011");
		addSinglePushCommentInput.setType("44974720000400010002");
		
		addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
		addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
		addSinglePushCommentInput.setTitle("您被举报啦");
		addSinglePushCommentInput.setUserCode(reporter);
		
	    String content="您可能存在违规行为，以后需要多多注意哦！具体违规行为请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#";
	    addSinglePushCommentInput.setContent(content);
		SinglePushComment.addPushComment(addSinglePushCommentInput);
			
		return mWebResult;
	}

}
