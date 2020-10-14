package com.cmall.newscenter.beauty.api;

import com.cmall.groupcenter.recommend.RecommendUtil;
import com.cmall.newscenter.beauty.model.SmsRecommendationInput;
import com.cmall.newscenter.beauty.model.SmsRecommendationResult;
import com.cmall.systemcenter.message.SendMessageBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 短信推荐联系人接口
 * @author fq
 *
 */
public class SmsRecommendationApi extends RootApiForManage<SmsRecommendationResult,SmsRecommendationInput>{

	public SmsRecommendationResult Process(SmsRecommendationInput inputParam,
			MDataMap mRequestMap) {
		SmsRecommendationResult result = new SmsRecommendationResult();
		String mobile = inputParam.getMobile();
		String recommendMobile = inputParam.getRecommendMobile();
		int pcount = DbUp.upTable("mc_login_info").count("login_name",mobile);
		int count = DbUp.upTable("mc_login_info").count("login_name",recommendMobile);
		
		if(pcount > 0) {
			
			//判断被推荐人是否已经成为会员
			if(count > 0) {
				//发送短信
				SendMessageBase messageBase = new SendMessageBase();
				String content = "该用户已经加入惠美丽微公社，无法接受您的邀请。";
				messageBase.sendMessage(mobile, content,"");
			} else {
				RecommendUtil util = new RecommendUtil();
				util.sendLink(mobile, recommendMobile, getManageCode());
			}
			
		} else {
			result.setResultCode(934205130);
			result.setResultMessage(bInfo(934205130, "【"+mobile+"】"));
		}
		return result;
	}
	
}
