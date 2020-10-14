package com.cmall.usercenter.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.usercenter.model.api.UserOrganizationInput;
import com.cmall.usercenter.model.api.UserOrganizationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 部门下的人员
 * @author   jack
 * @version  1.0
 */
public class UserOrganizationApi extends RootApi<UserOrganizationResult,UserOrganizationInput> {

	public UserOrganizationResult Process(UserOrganizationInput inputParam,
			MDataMap mRequestMap) {
		UserOrganizationResult result = new UserOrganizationResult();
		String code = inputParam.getCode();//部门编号
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("code",code);
		List<MDataMap> listP = DbUp.upTable("za_userorganization").queryAll("uid,user_code", "", "", mWhereMap);
		List<MDataMap> re =  new ArrayList<MDataMap>();
		if(!listP.isEmpty()){//获取对应的人员
			String userCodes = "";
			String sql = "";
			Map<String, String> pu = new HashMap<String, String>();//key:user_code value:uid
			for(MDataMap m:listP){
				String userCode = m.get("user_code");
				if("".equals(userCodes)){
					userCodes+=" user_code in ('" +userCode+"'";
				}else{
					userCodes+=",'"+userCode+"'";
				}
				pu.put(userCode, m.get("uid"));
			}
			if(!"".equals(userCodes)){
				sql = sql + userCodes+")";
			}
			mWhereMap.clear();
			List<MDataMap> ps = DbUp.upTable("za_userinfo").queryAll("", "", sql, mWhereMap);
			for(int i=0;i<ps.size();i++){
				MDataMap dataMap = ps.get(i);
				dataMap.put("uid", pu.get(dataMap.get("user_code")));
				re.add(i, dataMap);
			}
		}
		result.setList(re);
		return result;
	}


}

