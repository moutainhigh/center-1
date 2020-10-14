package com.cmall.ordercenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 废弃闪购商品
 * @author jl
 *
 */
public class FuncEditForDiscardFSkuService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			//判断登陆用户是否为空
			String loginname=UserFactory.INSTANCE.create().getLoginName();
			if(loginname==null||"".equals(loginname)){
				mResult.inErrorMessage(941901073);
				return mResult;
			}
			
			String uid=mAddMaps.get("uid");
			
			int cou=DbUp.upTable(mPage.getPageTable()).dataCount("uid=:uid and status='449746810002' ", mAddMaps);
			if(cou>0){// 已经废弃，提示不能废弃
				mResult.setResultCode(939301106);
				mResult.setResultMessage(bInfo(939301106));
				return mResult;
			}
			
			//创建时间为系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			String now=df.format(new Date());
			
			mInsertMap.put("status", "449746810002");//状态置为不可用
			mInsertMap.put("update_time", now);   // 更新时间
			mInsertMap.put("update_user", loginname);   // 更新的用户
			mInsertMap.put("uid", uid);
			
			DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
			mResult.setResultMessage(bInfo(969909001));
			
		}
		
		return mResult;
	}
}
