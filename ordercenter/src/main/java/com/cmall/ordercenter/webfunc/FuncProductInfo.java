package com.cmall.ordercenter.webfunc;

import java.util.List;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncProductInfo extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
			MWebResult mResult = new MWebResult();
			String sellerCode = mDataMap.get("zw_f_idx");
			String message = mDataMap.get("zw_f_to_status");
			
			//lc_soldout_log
			MDataMap mp1 = new MDataMap();
			mp1.put("seller_code", sellerCode);
			mp1.put("message", message);
			mp1.put("create_time", DateUtil.getNowTime());
			mp1.put("operator", UserFactory.INSTANCE.create().getLoginName());
			
			DbUp.upTable("lc_soldout_log").dataInsert(mp1);
			
			MDataMap mp = new MDataMap();
			mp.put("seller_code", sellerCode);
			mp.put("product_status", "4497153900060004"); //4497153900060003
			
			MDataMap mp2 = new MDataMap();
			mp2.put("seller_code", sellerCode);
			mp2.put("flag_sale", "0");
			try {
				DbUp.upTable("pc_productinfo").dataUpdate(mp, "product_status", "seller_code");
				DbUp.upTable("pc_productinfo").dataUpdate(mp2, "flag_sale", "seller_code");
				
				
		
				
				updateSellerStatusAndSellerModel(sellerCode);
				
				
				//begin   yanzj 修改  调用jms 生成前台静态页面
				List<MDataMap> list = DbUp.upTable("pc_productinfo")
						.query("product_code", "", "seller_code=:seller_code", mp, -1, -1);
				ProductService ps = new ProductService();
				if(list!=null || list.size()>0){
					for(MDataMap m : list){
						ps.genarateJmsStaticPageForProductCode(m.get("product_code"));
					}
				}
				
				//end
			} catch (Exception e) {
				mResult.inErrorMessage(939301052);
				e.printStackTrace();
				return mResult;
			}
			return mResult;
		}
		
		
		/**
		 * 商品下架下 商家状态 和 模板状态更新
		 * @param sellerCode
		 */
		private void updateSellerStatusAndSellerModel(String sellerCode)
		{
			 MDataMap m1 = new MDataMap();
			 m1.put("seller_code", sellerCode);
			 m1.put("flag_enable", "0");
			 
			 MDataMap m2 = new MDataMap();
			 m2.put("seller_code", sellerCode);
			 m2.put("seller_status", "4497172300040001");
			 
			 MDataMap m3 = new MDataMap();
			 m3.put("manage_code", sellerCode);
			 m3.put("flag_enable", "0");
			 
			 DbUp.upTable("uc_shop_template").dataUpdate(m1, "flag_enable", "seller_code");
			 DbUp.upTable("uc_sellerinfo").dataUpdate(m2, "seller_status", "seller_code");
			 DbUp.upTable("za_userinfo").dataUpdate(m3, "flag_enable", "manage_code");
			 
		}
	}

