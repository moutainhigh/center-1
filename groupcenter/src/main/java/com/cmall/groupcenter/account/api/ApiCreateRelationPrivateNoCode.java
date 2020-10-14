package com.cmall.groupcenter.account.api;

import java.util.Map;

import com.cmall.groupcenter.account.model.CreateRelationInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.cmall.membercenter.txservice.TxMemberBase;
import com.cmall.membercenter.txservice.TxMemberForStar;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 无验证码创建上下级关系
 * 
 * @author chenbin
 *
 */
public class ApiCreateRelationPrivateNoCode extends
		RootApiForManage<RootResultWeb, CreateRelationInput> {

	public RootResultWeb Process(CreateRelationInput inputParam,
			MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		RootResultWeb rootResultWeb = new RootResultWeb();
		String loginName = inputParam.getLoginName();
		String parentLoginName = inputParam.getParentLoginName();

		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("login_name",loginName);
		String memberCode = "";
		if (mUserMap != null) {
			memberCode = mUserMap.get("member_code");
		} else {
			TxMemberBase memberService = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");
			MLoginInput input = new MLoginInput();

			input.setLoginName(loginName);

			input.setManageCode(getManageCode());

			input.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			MReginsterResult mReginsterResult = memberService
					.doUserReginster(input);

			if (!mReginsterResult.upFlagTrue()) {
				rootResultWeb.inOtherResult(mReginsterResult);
			}

			memberCode = mReginsterResult.getMemberInfo().getMemberCode();
		}

		if (rootResultWeb.upFlagTrue()) {

			if (null == DbUp.upTable("mc_login_info").one("login_name",
					parentLoginName)) {
				rootResultWeb.inErrorMessage(918505170);
			}
			
			//判断密码是否为空,全为空时才能进行绑定
			if(rootResultWeb.upFlagTrue()){
				if(DbUp.upTable("mc_login_info").dataCount(" login_name=:login_name and login_pass!=\"\" ", new MDataMap("login_name",loginName))>0){
					rootResultWeb.inErrorMessage(918505175,loginName);
				}
			}
			
			if (rootResultWeb.upFlagTrue()) {
				String parentMemberCode = DbUp.upTable("mc_login_info")
						.one("login_name", parentLoginName).get("member_code");
				String accountCode = DbUp.upTable("mc_member_info")
						.one("member_code", memberCode).get("account_code");
				String parentAccountCode = DbUp.upTable("mc_member_info")
						.one("member_code", parentMemberCode)
						.get("account_code");

				GroupAccountSupport groupAccountSupport = new GroupAccountSupport();
				rootResultWeb.inOtherResult(groupAccountSupport.createRelation(
						accountCode, parentAccountCode, "",
						inputParam.getCreateTime()));
			}
		}
		return rootResultWeb;
	}

}
