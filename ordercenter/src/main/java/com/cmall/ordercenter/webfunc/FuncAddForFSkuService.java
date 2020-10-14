package com.cmall.ordercenter.webfunc;

import java.math.BigDecimal;
import java.util.List;

import com.cmall.productcenter.service.ProductStoreService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加闪购商品
 * @author jl
 *
 */
public class FuncAddForFSkuService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		recheckMapField(mResult, mPage, mAddMaps);

		String loginname=UserFactory.INSTANCE.create().getLoginName();
		
		if (mResult.upFlagTrue()) {

			//判断登陆用户是否为空
			if(loginname==null||"".equals(loginname)){
				mResult.inErrorMessage(941901073);
				return mResult;
			}
			
			/*销售价*/
		    BigDecimal sellPrice = 	BigDecimal.valueOf(Double.valueOf(mAddMaps.get("sell_price")));
			/*优惠价*/
		    BigDecimal vipPrice =   BigDecimal.valueOf(Double.valueOf(mAddMaps.get("vip_price")));
		    
		    if(sellPrice.compareTo(vipPrice)<0){
		    	mResult.inErrorMessage(941901105);
				return mResult;
		    }
			/*库存数*/
			int stock_num = Integer.valueOf(mAddMaps.get("stock_num"));
			/*促销库存*/
			int sales_num = Integer.valueOf(mAddMaps.get("sales_num"));
			
//			if(sales_num>stock_num){
//				
//				mResult.inErrorMessage(941901106);
//				return mResult;
//			}
			
		}
		
		String activity_code=mAddMaps.get("activity_code");
		String product_code=mAddMaps.get("product_code");
		List<MDataMap> skuList=DbUp.upTable("pc_skuinfo").queryAll("sku_code,sell_price,sku_name,sell_price", "", "flag_enable=:flag_enable and product_code=:product_code", new MDataMap("flag_enable","1","product_code",product_code));
		
		if(skuList!=null&&skuList.size()>0){
			
			String lock_id=WebHelper.addLock(1000, activity_code);
			StringBuffer sb=new StringBuffer();
			
			ProductStoreService productStoreService = new ProductStoreService();
			for (MDataMap skuMap : skuList) {
				
				String sku_code=skuMap.get("sku_code");
				
				//查看是否有时间重合的闪购，若有，则添加应该失败
				//查询不重合的情况
				int count=DbUp.upTable(mPage.getPageTable()).count("activity_code",activity_code,"sku_code",sku_code,"status","449746810001");
				if(count>0){
					sb.append("[").append(sku_code).append("][").append(skuMap.get("sku_name")).append("],");
					continue;
				}
				
				MDataMap mInsertMap = new MDataMap();
				mInsertMap.put("activity_code", activity_code);
				mInsertMap.put("sku_code", sku_code);
				mInsertMap.put("sku_name", skuMap.get("sku_name"));
				mInsertMap.put("stock_num", String.valueOf(productStoreService.getStockNumBySku(sku_code)));
				mInsertMap.put("sales_num", mAddMaps.get("sales_num"));
				mInsertMap.put("sell_price", skuMap.get("sell_price"));
				mInsertMap.put("vip_price", mAddMaps.get("vip_price"));
				mInsertMap.put("purchase_limit_vip_num", mAddMaps.get("purchase_limit_vip_num"));
				mInsertMap.put("purchase_limit_order_num", mAddMaps.get("purchase_limit_order_num"));
				mInsertMap.put("purchase_limit_day_num", mAddMaps.get("purchase_limit_day_num"));
				mInsertMap.put("location", mAddMaps.get("location"));
				mInsertMap.put("update_time", DateUtil.getSysDateTimeString());
				mInsertMap.put("update_user", loginname);   // 更新的用户
				mInsertMap.put("status", "449746810001");
				mInsertMap.put("product_name", mAddMaps.get("product_name"));
				mInsertMap.put("product_code", product_code);
				DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			}
			if(sb.length()>0){
				sb=sb.deleteCharAt(sb.length()-1);
				mResult.inErrorMessage(939301313, sb);
			}
			WebHelper.unLock(lock_id);
		}
		
		
		return mResult;

	}
	
}
