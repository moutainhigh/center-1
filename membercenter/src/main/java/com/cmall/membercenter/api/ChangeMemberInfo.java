package com.cmall.membercenter.api;

import com.cmall.dborm.txmodel.membercenter.McExtendInfoStar;
import com.cmall.membercenter.memberdo.ScoredEnumer;
import com.cmall.membercenter.model.MemberChangeInput;
import com.cmall.membercenter.model.MemberChangeResult;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.membercenter.txservice.TxMemberForStar;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 修改用户信息
 * 
 * @author srnpr
 * 
 */
public class ChangeMemberInfo extends
		RootApiForToken<MemberChangeResult, MemberChangeInput> {

	public MemberChangeResult Process(MemberChangeInput inputParam,
			MDataMap mRequestMap) {

		MemberInfoSupport memberInfoSupport=new MemberInfoSupport();
		
		
		
		MemberChangeResult memberChangeResult = new MemberChangeResult();

		McExtendInfoStar mcExtendInfoStar = new McExtendInfoStar();
		
		mcExtendInfoStar.setNickname(inputParam.getNickname());
		
		mcExtendInfoStar.setMemberSex(inputParam.getGender());
		
		
		TxMemberForStar memberService = BeansHelper
				.upBean("bean_com_cmall_membercenter_txservice_TxMemberForStar");
		memberService.updateMemberInfo(mcExtendInfoStar, getUserCode());
		
		
		memberChangeResult.setUser(memberInfoSupport.upMemberInfo(getUserCode()));
		
		memberChangeResult.setScored(new ScoredSupport().ChangeScored(getUserCode(), ScoredEnumer.MemberChangeInfo));
		
		
		return memberChangeResult;

	}

}
