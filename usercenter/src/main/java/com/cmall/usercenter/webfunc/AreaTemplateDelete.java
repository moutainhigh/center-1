package com.cmall.usercenter.webfunc;

import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AreaTemplateDelete extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String manageCode = UserFactory.INSTANCE.create().getManageCode();
		MDataMap mDataMap2 = new MDataMap();
		String uid = mAddMaps.get("uid");
		Map<String, Object> dataSqlOne = DbUp.upTable("sc_area_template").dataSqlOne("select * from sc_area_template where uid = '"+uid+"'", mDataMap2);
		if(dataSqlOne==null) {
			mResult.setResultMessage("模板不存在");
			mResult.setResultCode(0);
			return mResult;
		}
		//判断模板下 是否存在商品count
		int dataCount = 0;
		try {
			dataCount= DbUp.upTable("productcenter.pc_productinfo").count("if_delete =","0","product_status","4497153900060002", "template_code",dataSqlOne.get("merchants_code").toString());

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//int dataCount = DbUp.upTable("productcenter.pc_productinfo").dataCount("if_delete = '0' and product_status = '4497153900060002' and merchants_code ='"+dataSqlOne.get("template_code")+"'", mDataMap2);
		if(dataCount>0) {
			mResult.setResultMessage("当前模板下存在已上架商品！无法删除");
			mResult.setResultCode(0);
			return mResult;
		}
		MDataMap updateMDataMap = new MDataMap();
		updateMDataMap.put("uid", uid);
		updateMDataMap.put("is_delete", "1");
		updateMDataMap.put("merchants_code", manageCode);
		updateMDataMap.put("zid", dataSqlOne.get("zid").toString());
		DbUp.upTable("sc_area_template").update(updateMDataMap);
		
		return mResult;
	}

}
