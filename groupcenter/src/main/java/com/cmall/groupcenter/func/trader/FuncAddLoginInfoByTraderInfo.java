package com.cmall.groupcenter.func.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加商户信息页面新增用户
 * 
 * @author srnpr
 * 
 */
public class FuncAddLoginInfoByTraderInfo extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MDataMap addDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		MemberLoginSupport loginSupport = new MemberLoginSupport();
		
		//System.out.println(addDataMap);
//		MWebResult result = new MWebResult();
//		result.setResultCode(1);
		MWebResult result = loginSupport.checkOrCreateUserByMobile(addDataMap.get("login_name"), addDataMap.get("login_pass"), "SI2011");
		
		if(result.getResultCode()!=0){//添加成功时，返回相应字段信息
//			
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("login_name", addDataMap.get("login_name"));
			Map<String, Object> map = DbUp.upTable("v_trader_select_user").dataSqlOne("select * from v_trader_select_user WHERE login_name =:login_name ", mWhereMap);

			if(map==null){
				
			}
//			map.get("account_code");
			//创建微公社账号
			GroupAccountSupport support = new GroupAccountSupport();
			support.checkAndCreateGroupAccount(map.get("account_code").toString());
			
			
			List<Object> list = new ArrayList<Object>();
			list.add(map);
			result.setResultList(list);
		}
		
		
		
		return result;
	}
	
}
