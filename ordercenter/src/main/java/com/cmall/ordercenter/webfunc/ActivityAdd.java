package com.cmall.ordercenter.webfunc;

import java.util.UUID;

import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.OcActivityProductRel;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncAdd;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ActivityAdd extends RootFunc {

	public ActivityAdd() {
		// TODO Auto-generated constructor stub
	}

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		
		String activityCode = com.srnpr.zapweb.helper.WebHelper.upCode(OrderConst.ActivityHead);
		
		if (mResult.upFlagTrue()) {
			
			String activityType = mDataMap.get("zw_f_activity_type");
			
			if(activityType.equals(OrderConst.XSXLACTIVITY)){//限时抢购
				String productItems = mDataMap.get("zw_f_productitems");
				if(!productItems.trim().equals(""))
				{
					String[] ary = productItems.split(";");
					
					if(ary.length >0)
					{
						for(int i=0;i<ary.length;i++)
						{
							if(!ary[i].trim().equals(""))
							{
								String[] items = ary[i].split("##");
								//skuCode + "##" + price+ "##" + stock + ";";
								if(items.length!=3)
								{
									mResult.setResultCode(939301027);
									mResult.setResultMessage(bInfo(939301027));
								}
								else
								{
									MDataMap mInsertMap = new MDataMap();
									UUID uuid = UUID.randomUUID();
									mInsertMap.put("uid", uuid.toString().replace("-", ""));
									mInsertMap.put("activity_code", activityCode);
									mInsertMap.put("sku_code", items[0]);
									mInsertMap.put("sell_price", items[1]);
									mInsertMap.put("sell_stock", items[2]);
									
									DbUp.upTable("oc_activity_product_rel").dataInsert(mInsertMap);
								}
							}
						}
					}
				}
			}
			else if(activityType.equals(OrderConst.MJACTIVITY)){//满减
				
				String categoryItems = mDataMap.get("zw_f_categoryitems");
				if(!categoryItems.trim().equals(""))
				{
					String[] categoryAry = categoryItems.split(",");
					
					if(categoryAry.length > 0)
					{
						for(int i=0;i<categoryAry.length;i++)
						{
							if(!categoryAry[i].trim().equals(""))
							{
								MDataMap mInsertMap = new MDataMap();
								UUID uuid = UUID.randomUUID();
								mInsertMap.put("uid", uuid.toString().replace("-", ""));
								mInsertMap.put("activity_code", activityCode);
								mInsertMap.put("category_code", categoryAry[i]);
								DbUp.upTable("oc_activity_sellercategory_rel").dataInsert(mInsertMap);
							}
						}
					}
				}
				
				
				
			}else if(activityType.equals(OrderConst.MYFACTIVITY)){//免运费
				
				
				
			}
		}
		
		if (mResult.upFlagTrue()){
			
			FuncAdd fa = new FuncAdd();
			
			
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			
			String sellerCode = userInfo.getManageCode();
			String operator = userInfo.getUserCode();
			String datetime = DateUtil.getSysDateTimeString();
			
			mDataMap.put("zw_f_creator", operator);
			mDataMap.put("zw_f_updator", operator);
			mDataMap.put("zw_f_create_time", datetime);
			mDataMap.put("zw_f_update_time", datetime);
			mDataMap.put("zw_f_activity_code", activityCode);
			mDataMap.put("zw_f_seller_code", sellerCode);
			
			
			mResult = fa.funcDo(sOperateUid, mDataMap);
		}
		
		return mResult;
	}

}
