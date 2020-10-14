package com.cmall.newscenter.webfunc;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 修改积分设置类
 * @author shiyz
 * @version 1.0
 * date 2014-07-30
 */
public class IntegralSetEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		
		MDataMap maddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);	
		
		/*根据uid查询积分信息*/
		MDataMap meditMaps = DbUp.upTable("nc_integral").one("uid",maddMaps.get("uid"));
		
		/*获取当前登录人*/
		String user = UserFactory.INSTANCE.create().getLoginName();
		
		/*获取当前时间*/
		String update_time = DateUtil.getNowTime();
		
		/*将操作编号放入map*/
		maddMaps.put("operation_code", meditMaps.get("operation_code"));
		
		maddMaps.put("operation_explain", meditMaps.get("operation_explain"));
		
		maddMaps.put("genus_app", meditMaps.get("genus_app"));
		
		maddMaps.put("zid", meditMaps.get("zid"));
		
		maddMaps.put("operation_modifier", user);
		
		maddMaps.put("operation_time", update_time);
		
		if(mWebResult.upFlagTrue()){
			
			/*更新积分管理表*/
            DbUp.upTable("nc_integral").dataUpdate(maddMaps, "", "uid");
			
		}
		
		return mWebResult;
	}

}
