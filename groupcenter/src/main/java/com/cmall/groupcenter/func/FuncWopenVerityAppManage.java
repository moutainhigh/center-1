package com.cmall.groupcenter.func;


import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncWopenVerityAppManage extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		
		String uid = mDataMap.get("uid");
		String verifystatus="4497473700030003";
		if(result.upFlagTrue()){

			MDataMap map = DbUp.upTable("gc_wopen_appmanage").one("uid",uid);
			if(!map.get("verify_status").equals("4497473700030002")){
				
				result.setResultMessage("审核操作已经完成,不能再进行审核操作！");
				return result;
			}
			map.put("verify_status", verifystatus);
			map.put("online_status", "4497473700020002");
			map.put("verity_time", DateUtil.getSysDateTimeString());
			map.put("auditor_name", UserFactory.INSTANCE.create().getLoginName());
			
			
			String apikey=map.get("test_apikey");
			String apipassword=map.get("test_apipassword");
			String apiname=map.get("app_name");
			String apicode=map.get("app_code");
			
			
			DbUp.upTable("gc_wopen_appmanage").dataUpdate(map, "", "uid");
			
			MDataMap mDataMapNew =new MDataMap();
			mDataMapNew.put("api_key", apikey);
			mDataMapNew.put("api_pass", apipassword);
			
			mDataMapNew.put("api_able", "com");
			mDataMapNew.put("remark", apiname);
			mDataMapNew.put("api_roles", "469923200004,469923200005");
			mDataMapNew.put("manage_code", apicode);
			
			
			DbUp.upTable("za_apiauthorize").dataInsert(mDataMapNew);
			
			result.setResultCode(1);
			result.setResultMessage("审核通过提交成功");
		}else{
			result.setResultMessage("审核通过提交失败");
		

		}
		return result;
	
	}
	

}
