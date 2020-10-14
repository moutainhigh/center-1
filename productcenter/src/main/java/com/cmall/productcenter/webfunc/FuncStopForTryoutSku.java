package com.cmall.productcenter.webfunc;

import java.util.List;
import java.util.Map;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.txservice.TxProductService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 终止试用商品
 * @author 李国杰
 *
 */
public class FuncStopForTryoutSku extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");
		if (mResult.upFlagTrue()){
				//试用商品的uid，end_time
			String uid=mSubMap.get("uid");
			String endTime=mSubMap.get("end_time");
			String skuCode = mSubMap.get("sku_code");
			String isFreeShipping = mSubMap.get("is_freeShipping");
				MDataMap updataData = new MDataMap();
				
				//当前时间大于结束时间则不进行修改操作，同时提示操作成功！
				if (DateUtil.getSysDateTimeString().compareTo(endTime == null ? "" : endTime) > 0) {
					return mResult;
				}
				//先判断登录是否有效
				if(UserFactory.INSTANCE.create() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
					mResult.inErrorMessage(941901073);
					return mResult;
				}else{
					updataData.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
				}
				updataData.put("end_time",DateUtil.getSysDateTimeString());
				updataData.put("update_time",DateUtil.getSysDateTimeString());
				updataData.put("uid",uid);
				
				//查询条件
				updataData.put("sku_code", skuCode);
				updataData.put("isFreeShipping", isFreeShipping);
				updataData.put("endTimeBefore", endTime);			//修改前的endTime,查询用
				
				try {
					txs.updateTryoutInfo(updataData);//事务操作
				} catch (Exception e) {
					mResult.setResultCode(941901078);
					mResult.setResultMessage(bInfo(941901078, "终止商品失败！"));
				}
				
				
		}
		return mResult;
	}
}