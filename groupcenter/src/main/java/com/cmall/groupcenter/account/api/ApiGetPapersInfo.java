package com.cmall.groupcenter.account.api;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.GroupConstant.PapersEnum;
import com.cmall.groupcenter.account.model.AddPapersInput;
import com.cmall.groupcenter.account.model.GetPapersInfoResult;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 获取证件信息
 * 
 * @author chenxk
 * 
 */
public class ApiGetPapersInfo extends RootApiForToken<GetPapersInfoResult, RootInput> {

	public GetPapersInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {

		GetPapersInfoResult getPapersInfoResult = new GetPapersInfoResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		Map<String, Object>  mDate = DbUp.upTable("gc_member_papers_info").dataSqlOne("select * from gc_member_papers_info where account_code='"+sAccountCode+"'", new MDataMap());
		
		if(mDate != null){
			getPapersInfoResult.setPapersCode((String)mDate.get("papers_code"));
			getPapersInfoResult.setUserName((String)mDate.get("user_name"));
			getPapersInfoResult.setPapersType(PapersEnum.getCnoByCardType((String)mDate.get("papers_type")));
			getPapersInfoResult.setPapersName(PapersEnum.getCardAliasByCardType((String)mDate.get("papers_type")));
			
			//判断用户是否可修改证件信息
			List<MDataMap> list = DbUp.upTable("gc_pay_order_info").query("uid", "", "account_code=:account_code and order_status != '4497153900120003' and pay_status !='4497465200070003' and create_time >:create_time", new MDataMap("create_time","2015-04-13 23:59:59","account_code",sAccountCode), -1, 0);
			if(list.size() >0){
				getPapersInfoResult.setIsModifyFlag("0");//不可修改
			}else{
				getPapersInfoResult.setIsModifyFlag("1");//可修改
			}
		}else{
			getPapersInfoResult.setResultCode(-1);
			getPapersInfoResult.setResultMessage("没有该用户记录");
		}
		return getPapersInfoResult;
	}
}
