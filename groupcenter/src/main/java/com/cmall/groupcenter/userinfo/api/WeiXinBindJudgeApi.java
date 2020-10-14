package com.cmall.groupcenter.userinfo.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.userinfo.model.WeiXinBindJudgeInput;
import com.cmall.groupcenter.userinfo.model.WeiXinBindJudgeResult;
import com.cmall.membercenter.model.HXUserLoginInfo;
import com.cmall.membercenter.model.HXUserLoginInfoExtendInfo;
import com.cmall.membercenter.model.HXUserLoginService;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StartPageService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class WeiXinBindJudgeApi extends RootApiForManage<WeiXinBindJudgeResult, WeiXinBindJudgeInput>{

	public WeiXinBindJudgeResult Process(WeiXinBindJudgeInput inputParam,
			MDataMap mRequestMap) {
		WeiXinBindJudgeResult weiXinBindJudgeResult=new WeiXinBindJudgeResult();
	    String openId=inputParam.getOpenId();
	    MDataMap bindMap=DbUp.upTable("mc_weixin_binding").one("open_id",openId,"manage_code",getManageCode(),"flag_enable","1");
	    //未绑定,跳绑定页面
	    if(bindMap==null){
	    	//先存下信息
	    	MDataMap newBindMap=new MDataMap();
	    	newBindMap.inAllValues("open_id",inputParam.getOpenId(),"union_id",inputParam.getUnionId(),"gender",inputParam.getGender(),
	    			"location",inputParam.getLocation(),"headerImageUrl",inputParam.getHeaderImageUrl(),"nickName",inputParam.getNickName(),
	    			"create_time",FormatHelper.upDateTime(),"flag_enable","1","manage_code",getManageCode());
	    	DbUp.upTable("mc_weixin_binding").dataInsert(newBindMap);
	    	if(getManageCode().equals(AppConst.MANAGE_CODE_CDOG)){
	    		weiXinBindJudgeResult.setPageUrl("/cgroup/web/grouppageSecond/weixinbindspg.html?openId="+inputParam.getOpenId()+"&web_api_serial="+inputParam.getSerialNumber());
	    	}else {
	    		weiXinBindJudgeResult.setPageUrl("/cgroup/web/grouppageSecond/weixinbind.html?openId="+inputParam.getOpenId()+"&web_api_serial="+inputParam.getSerialNumber());
			}
	    	
	    }
	    //判断membercode
	    else{
	    	if(StringUtils.isNotBlank(bindMap.get("member_code"))){
	    		//不为空，已绑定，跳首页
		    	weiXinBindJudgeResult.setIsPhoneBind(1);//wangzx 增加
	    		MDataMap memberMap=DbUp.upTable("mc_login_info").one("member_code",bindMap.get("member_code"),"flag_enable","1");
	    		//绑定用户流水号
	    		if (weiXinBindJudgeResult.upFlagTrue()) {
	    			new StartPageService().updateLsh(inputParam.getSerialNumber(), bindMap.get("member_code"));
	    		}
	    		
	    		// 开始返回用户的登录信息
	    		if (weiXinBindJudgeResult.upFlagTrue()) {
	    			String sAuthCode = new MemberLoginSupport().memberLogin(bindMap.get("member_code"), getManageCode(), memberMap.get("login_name"));
	    			if (StringUtils.isNotEmpty(sAuthCode)) {
	    				weiXinBindJudgeResult.setMemberCode(bindMap.get("member_code"));
	    				weiXinBindJudgeResult.setUserMobile(memberMap.get("login_name"));
	    				weiXinBindJudgeResult.setUserToken(sAuthCode);
	    				if(getManageCode().equals(AppConst.MANAGE_CODE_CDOG)){
	    					//客服：沙皮狗
	    					String memberCode = weiXinBindJudgeResult.getMemberCode();
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
	    						weiXinBindJudgeResult.setHxUserLoginInfo(hxUserLoginInfo);
	    						weiXinBindJudgeResult.setPageUrl("weixinspg");
	    					}
	    		    	}else {
	    		    		
	    		    		weiXinBindJudgeResult.setPageUrl("/cgroup/web/grouppageSecond/index.html?web_api_token="+sAuthCode+"&user_phone="+memberMap.get("login_name")+"&web_api_serial="+inputParam.getSerialNumber());
	    				}
	    				
	    			} else {
	    				weiXinBindJudgeResult.inErrorMessage(934105102);
	    			}
	    		}
	    	}
	    	else{
	    		if(getManageCode().equals(AppConst.MANAGE_CODE_CDOG)){
		    		weiXinBindJudgeResult.setPageUrl("/cgroup/web/grouppageSecond/weixinbindspg.html?openId="+inputParam.getOpenId()+"&web_api_serial="+inputParam.getSerialNumber());
		    	}else {
	    		weiXinBindJudgeResult.setPageUrl("/cgroup/web/grouppageSecond/weixinbind.html?openId="+inputParam.getOpenId()+"&web_api_serial="+inputParam.getSerialNumber());
	    	}
	    	}
	    }
		return weiXinBindJudgeResult;
	}

	
}
