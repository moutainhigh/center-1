package com.cmall.newscenter.api;


import com.cmall.newscenter.model.PcFeedbackInput;
import com.cmall.newscenter.model.PcFeedbackResult;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * pc意见反馈-保存Api
 * 
 * @author wangzx 
 * @version1.0
 */
public class PcFeedbackSaveApi extends
		RootApiForToken<PcFeedbackResult, PcFeedbackInput> {


public PcFeedbackResult Process(PcFeedbackInput inputParam, MDataMap mRequestMap) {
	PcFeedbackResult result = new PcFeedbackResult();
		/*DbUp.upTable("nc_chat_message").insert("uid",WebHelper.upUuid(),
			        "sender_id",inputParam.getSenderId(),
			        "receiver_id",inputParam.getReceiverId(),
			        "member_code",this.getUserCode(),
			        "chat_content",inputParam.getChatContent()
		        );*/

		/*String sAccountCode = DbUp.upTable("mc_member_info")
			.oneWhere("account_code", "", "", "member_code", getUserCode())
			.get("account_code");*/
		
		DbUp.upTable("lc_vpc_feedback").dataInsert(
				new MDataMap("account_code",this.getUserCode(),
						     "login_code",this.getOauthInfo().getLoginName(),
						     "description",inputParam.getDescription(),
						     "img_url",inputParam.getImgUrl(),
						     "create_time",DateHelper.upNow()
						     )
				);
		
		return result;
	}


	

}
