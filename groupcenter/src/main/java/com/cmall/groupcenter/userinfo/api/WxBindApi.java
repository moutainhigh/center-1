package com.cmall.groupcenter.userinfo.api;

import com.cmall.groupcenter.userinfo.model.WxBindInput;
import com.cmall.groupcenter.userinfo.model.WxBindResult;
import com.cmall.groupcenter.weixin.WebchatConstants;
import com.cmall.groupcenter.weixin.WeiXinUtil;
import com.cmall.membercenter.group.model.GroupLoginInput;
import com.cmall.membercenter.group.model.GroupLoginResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class WxBindApi extends RootApiForManage<WxBindResult, WxBindInput>{

	public WxBindResult Process(WxBindInput inputParam, MDataMap mRequestMap) {
		GroupLoginInput loginInput=new GroupLoginInput();
		loginInput.setLoginName(inputParam.getLoginName());
		loginInput.setLoginPass(inputParam.getLoginPass());
		loginInput.setSerialNumber("");
		GroupLoginResult loginResult =new MemberLoginSupport().doGroupLogin(loginInput,
				getManageCode());
		if(loginResult.getResultCode()==1){
			MDataMap mDataMap =DbUp.upTable("mc_weixin_binding").one("open_id",inputParam.getOpenId(),"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
			if(mDataMap!=null&&mDataMap.size()>0){
				mDataMap.put("member_code", loginResult.getMemberCode());
				DbUp.upTable("mc_weixin_binding").update(mDataMap);
				WeiXinUtil wxUtil=new WeiXinUtil();
				//向微信发送消息
				if(inputParam.getTargetId().equals("2")){
					String respContent="恭喜您绑定成功！\n您现在可以免费享受返利、提现、热门活动等通知提醒。";
					
					wxUtil.sendCustomMessage(WeiXinUtil.makeTextCustomMessage(inputParam.getOpenId(), respContent));
					
				}else{
					String mobile=inputParam.getLoginName().substring(7);
					String respContent="您尾号"+mobile+"的微公社账号已经绑定成功，无需登录可享受以下贴心服务："
							+ "\n1、免费通知提醒"
							+ "\n2、可提现余额查询"
							+ "\n3、账户明细查询"
							+ "\n4、财产明细查询"
							+ "\n5、返利记录查询"
							+ "\n6、提现记录查询"
							+ "\n7、邀请好友";
					wxUtil.sendCustomMessage(WeiXinUtil.makeTextCustomMessage(inputParam.getOpenId(), respContent));
				}
			}else{
				loginResult.setResultCode(333);
				loginResult.setResultMessage("您还未关注微公社公众号，请关注后绑定！");
			}
			
		}
		WxBindResult bind=new WxBindResult();
		bind.setMemberCode(loginResult.getMemberCode());
		bind.setResultCode(loginResult.getResultCode());
		bind.setResultMessage(loginResult.getResultMessage());
		bind.setUserToken(loginResult.getUserToken());
		
		
		return bind;
	}
 
	
}
