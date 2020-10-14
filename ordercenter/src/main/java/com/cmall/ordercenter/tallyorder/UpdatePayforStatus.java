package com.cmall.ordercenter.tallyorder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 结算2.5期 添加付款标记
 * @author zmm
 *
 */
public class UpdatePayforStatus extends RootFunc{
	
/**
 * 4497476900040008未结算
 * 4497476900040009已结算
 * 默认是未结算
 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mSubMap.get("uid");
		Map<String, String> skumap = DbUp.upTable("oc_bill_merchant_new").oneWhere("uid,settle_code,merchant_code", "","uid=:uid", "uid", uid);
		String settle_code = skumap.get("settle_code").toString();
		String merchant_code = skumap.get("merchant_code").toString();
		String username=UserFactory.INSTANCE.create().getLoginName();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String create_time=df.format(calendar.getTime()).toString();
		if(username!=null&&!"".equals(username)){
			DbUp.upTable("oc_bill_merchant_new").dataUpdate(new MDataMap("uid",uid,"flag","4497476900040009"),"flag", "uid");
			DbUp.upTable("oc_bill_payfor_loginfo").insert("user_name",username,"settle_code",settle_code,"small_seller_code",merchant_code,"create_time",create_time);
			mResult.setResultMessage("操作成功!");
		}else{
			mResult.setResultMessage("您的操作身份异常，请重新登录系统!");
		}
		return mResult;
	}

}
