package com.cmall.ordercenter.webfunc;

import java.util.List;

import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.OcActivityProductRel;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ActivityUpdate extends RootFunc {

	public ActivityUpdate() {
		// TODO Auto-generated constructor stub
	}

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		
		
		String uid = mDataMap.get("zw_f_uid");
		
		MDataMap mdm = DbUp.upTable("oc_activity").one("uid",uid);
		
		if(mdm == null){
			
			mResult.setResultCode(939301028);
			mResult.setResultMessage(bInfo(939301028));
		}else{
			

			if(mdm.get("flag").equals("0"))
			{
				mResult.setResultCode(939301029);
				mResult.setResultMessage(bInfo(939301029));
			}
			else{
				MUserInfo userInfo = UserFactory.INSTANCE.create();
				
				String operator = userInfo.getUserCode();
				String datetime = DateUtil.getSysDateTimeString();
				
				MDataMap updateParam = new MDataMap();
				updateParam.put("uid", uid);
				updateParam.put("flag", "0");
				updateParam.put("updator", operator);
				updateParam.put("update_time", datetime);
				DbUp.upTable("oc_activity").dataUpdate(updateParam,"flag,updator,update_time","uid");
				
				
				try {
					
					if(mdm.get("activity_type").equals(OrderConst.XSXLACTIVITY)){
						MDataMap productMapParam = new MDataMap();
						productMapParam.put("activity_code", mdm.get("activity_code"));
						List<MDataMap> productListMap = DbUp.upTable("oc_activity_product_rel")
								.query("", "", "activity_code=:activity_code", productMapParam, -1, -1);
						
						ProductJmsSupport pjs = new ProductJmsSupport();
					
						if(productListMap!=null)
						{
							int psize = productListMap.size();
							for(int k=0;k<psize;k++)
							{
								pjs.onChangeForSkuChangePrice(productListMap.get(k).get("sku_code"));
							}
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
		
		return mResult;
	}

}
