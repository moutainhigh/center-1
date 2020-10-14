package com.cmall.groupcenter.account.api;


import com.cmall.groupcenter.account.model.AccountInfoResult;
import com.cmall.groupcenter.account.model.CheckAppActivateStatusInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 检查应用是否激活
 * @author wangzx
 */
public class ApiCheckAppActivateStatus extends
	RootApiForToken<RootResultWeb, CheckAppActivateStatusInput> {

	public RootResultWeb Process(CheckAppActivateStatusInput inputParam, MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		String managecode = inputParam.getManagecode();
		if(!"SI2011".equals(managecode) && !"SI2003".equals(managecode))
		{
			result.setResultCode(918546001);//
			result.setResultMessage(bInfo(918546001));
			return result;
		}
		MDataMap nMap = DbUp.upTable("mc_login_info").one("login_name",this.getOauthInfo().getLoginName(),"manage_code",inputParam.getManagecode());
		if(nMap==null){//未激活
			result.setResultCode(918546003);
			result.setResultMessage(bInfo(918546003));
		} else{//已激活
			result.setResultCode(918546002);
			result.setResultMessage(bInfo(918546002));
		}
		return result;
	}
	

}
