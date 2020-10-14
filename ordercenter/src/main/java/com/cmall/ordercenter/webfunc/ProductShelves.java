/**
 * Project Name:ordercenter
 * File Name:ProductShelves.java
 * Package Name:com.cmall.ordercenter.webfunc
 * Date:2013年11月6日下午1:37:03
 *
*/

package com.cmall.ordercenter.webfunc;

import java.util.List;

import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName:ProductShelves <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月6日 下午1:37:03 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ProductShelves extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String sellerCode = mDataMap.get("zw_f_seller_code");
		/*if(!isAdded(sellerCode))
		{
			mResult.setResultCode(939301078);
			mResult.setResultMessage(bInfo(939301078));
			return mResult;
		}*/
		MDataMap mp = new MDataMap();
		mp.put("seller_code", sellerCode);
		// 449746230003    下架状态
	/*	if(validateData(sellerCode))
		{
			mResult.setResultCode(939301077);
			mResult.setResultMessage(bInfo(939301077));
			return mResult;
		}*/
		mp.put("product_status", "4497153900060003");
		try {
			DbUp.upTable("pc_productinfo").dataUpdate(mp, "product_status", "seller_code");
			
			
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
	
	private boolean validateData(String seller_code)
	{
		MDataMap mp  =   DbUp.upTable("pc_productinfo").one("seller_code",seller_code,"product_status","4497153900060003");
		if(null == mp)
			return false;
		else
			return true;
	}
	
	/**
	 * @param 判断当前商家是否有上架的商品
	 * @return
	 */
	private boolean isAdded(String seller_code)
	{
		MDataMap mp  =   DbUp.upTable("pc_productinfo").one("seller_code",seller_code);
		if(null == mp)
			return false;
		else
			return true;
	}

}

