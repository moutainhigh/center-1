package com.cmall.groupcenter.func;


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

public class FuncWopenRefuseAppManage extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		
		String uid = mDataMap.get("uid");
		String verifystatus="4497473700030004"; //审核不通过
		if(result.upFlagTrue()){
			MDataMap map = DbUp.upTable("gc_wopen_appmanage").one("uid",uid);
			if(!map.get("verify_status").equals("4497473700030002")){
				
				result.setResultMessage("审核操作已经完成,不能再进行审核操作！");
				return result;
			}
			map.put("verify_status", verifystatus);
			map.put("verity_time", DateUtil.getSysDateTimeString());
			map.put("auditor_name", UserFactory.INSTANCE.create().getLoginName());
			DbUp.upTable("gc_wopen_appmanage").dataUpdate(map, "", "uid");

			result.setResultCode(1);
			result.setResultMessage("审核不通过提交成功");
		}else{
			result.setResultMessage("审核不通过提交失败");
		

		}
		return result;
	
	}
	

}
