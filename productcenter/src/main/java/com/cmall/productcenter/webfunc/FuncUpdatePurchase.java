package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncUpdatePurchase extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		
		if (mResult.upFlagTrue()) {
			if(StringUtils.isEmpty(mAddMaps.get("product_name"))){
				mResult.setResultMessage("你的网络被高智慧生物屏蔽了，稍后再试试");
			}else{
				String[] pro = mAddMaps.get("product_name").split(",");
				for(int j=0;j<pro.length;j++){
					
					try {
						if(XmasKv.upFactory(EKvSchema.UserMemberCode).exists(pro[j])){
							XmasKv.upFactory(EKvSchema.UserMemberCode).del(pro[j]);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						mResult.setResultMessage("你的网络被高智慧生物屏蔽了，稍后再试试");
					}
				}
				
			}
		}
		
		

		return mResult;
	}

}
