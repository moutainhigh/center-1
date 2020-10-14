package com.cmall.groupcenter.userinfo.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.userinfo.model.WeiXinBindInput;
import com.cmall.membercenter.group.model.GroupLoginResult;
import com.cmall.membercenter.model.HXUserLoginInfo;
import com.cmall.membercenter.model.HXUserLoginInfoExtendInfo;
import com.cmall.membercenter.model.HXUserLoginService;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.service.StartPageService;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 微信绑定
 * @author chenbin
 *
 */
public class WeiXinBindApi extends RootApiForManage<GroupLoginResult, WeiXinBindInput>{

	public GroupLoginResult Process(WeiXinBindInput inputParam,
			MDataMap mRequestMap) {
		GroupLoginResult groupLoginResult=new GroupLoginResult();
		// 判断验证码对不对
		if (groupLoginResult.upFlagTrue()) {
			VerifySupport verifySupport = new VerifySupport();
			groupLoginResult.inOtherResult(verifySupport.checkVerifyCodeByType(
							EVerifyCodeTypeEnumer.WeiXinBind, inputParam.getLoginName(),
							inputParam.getVerifyCode()));
		}
		//未注册时，注册用户
		if(groupLoginResult.upFlagTrue()){
			if(DbUp.upTable("mc_login_info").count("login_name",inputParam.getLoginName(),"manage_code",getManageCode())<1){
				MemberLoginSupport memberLoginSupport=new MemberLoginSupport();
				groupLoginResult.inOtherResult(memberLoginSupport.checkOrCreateUserByWGS(inputParam.getLoginName(), getManageCode()));
			}
		}
		
		MDataMap loginMap=DbUp.upTable("mc_login_info").one("login_name",inputParam.getLoginName(),"manage_code",getManageCode());
		//未绑定时，绑定
		if(groupLoginResult.upFlagTrue()){
			if(loginMap!=null){
				MDataMap bindMap=DbUp.upTable("mc_weixin_binding").one("manage_code",getManageCode(),"open_id",inputParam.getOpenId());
                if(bindMap!=null){
                	//进行绑定
                	if(StringUtils.isBlank(bindMap.get("member_code"))){
                		bindMap.put("member_code", loginMap.get("member_code"));
                		DbUp.upTable("mc_weixin_binding").dataUpdate(bindMap, "member_code", "zid");
                	}
                }
                else{
                	//未有预存信息，对openid进行绑定
                	DbUp.upTable("mc_weixin_binding").insert("member_code",loginMap.get("member_code"),"open_id",inputParam.getOpenId(),"create_time",FormatHelper.upDateTime(),"manage_code",getManageCode());
                }
				
				
			}
		}
		
		//完成登录
		if(groupLoginResult.upFlagTrue()){
			if(loginMap!=null){
				//绑定用户流水号
	    		if (groupLoginResult.upFlagTrue()) {
	    			new StartPageService().updateLsh(inputParam.getSerialNumber(), loginMap.get("member_code"));
	    		}
	    		
	    		// 开始返回用户的登录信息
	    		if (groupLoginResult.upFlagTrue()) {
	    			String sAuthCode = new MemberLoginSupport().memberLogin(loginMap.get("member_code"), getManageCode(), inputParam.getLoginName());
	    			if (StringUtils.isNotEmpty(sAuthCode)) {
	    				if(getManageCode().equals(AppConst.MANAGE_CODE_CDOG)){
	    					//客服：沙皮狗
	    					String memberCode = loginMap.get("member_code");
	    					if(!"".equals(memberCode)) {
	    						HXUserLoginService service = new HXUserLoginService();
	    						MDataMap mDataMap = service.loginInfo(memberCode);
	    						HXUserLoginInfo hxUserLoginInfo = new HXUserLoginInfo();
	    						hxUserLoginInfo.setHxUserName(mDataMap.get("hx_user_name"));
	    						hxUserLoginInfo.setHxPassWord(mDataMap.get("hx_pass_word"));
	    						hxUserLoginInfo.setHxWorkerId(mDataMap.get("hx_worker_id"));
	    						hxUserLoginInfo.setHxStatus(mDataMap.get("hx_status"));
	    						HXUserLoginInfoExtendInfo extendInfo =new MemberLoginSupport().getExtendInfo(memberCode, getManageCode());
	    						hxUserLoginInfo.setExtendInfo(extendInfo);
	    						groupLoginResult.setHxUserLoginInfo(hxUserLoginInfo);
	    						groupLoginResult.setUserToken(sAuthCode);
	    	    				groupLoginResult.setMemberCode(memberCode);
	    					}
	    		    	}else {
	    				groupLoginResult.setUserToken(sAuthCode);
	    				groupLoginResult.setMemberCode(loginMap.get("member_code"));
	    		    	}
	    			} else {
	    				groupLoginResult.inErrorMessage(934105102);
	    			}
	    		}
			}
		}
		
		return groupLoginResult;
	}

}
