/**
 * Project Name:ordercenter
 * File Name:AddProduct.java
 * Package Name:com.cmall.ordercenter.webfunc
 * Date:2013年11月6日上午9:51:10
 *
*/

package com.cmall.ordercenter.webfunc;

import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName:AddProduct <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月6日 上午9:51:10 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class AddProduct extends RootFunc{
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String  productId=  getProductidByUid(mDataMap.get("zw_f_uid"));
		String boutiqueId = getBoutidByUid(mDataMap.get("uuid"));
		
		if(validateProduct(productId, boutiqueId) >= 1)
		{
			mResult.inErrorMessage(939301050);
			return mResult;
		}
		mResult = insertRelation(productId, boutiqueId);
		return mResult;
	}
	/**
	 * @param uid  商家UID
	 * @return
	 */
	public String  getProductidByUid(String uid)
	{
		MDataMap prodcutData = DbUp.upTable("pc_productinfo").one("uid", uid);
		return prodcutData.get("product_code");
	}
	
	/**
	 * @param uid  精品汇UID
	 * @return
	 */
	public String  getBoutidByUid(String uid)
	{
		MDataMap prodcutData = DbUp.upTable("oc_boutique_market").one("uid", uid);
		return prodcutData.get("boutique_code");
	}
	/**
	 * @param sellerId 商家UID
	 * @param boutiqueId 精品汇UID
	 * @return
	 */
	public  MWebResult insertRelation(String productId,String boutiqueId)
	{
		MWebResult mResult = new MWebResult();
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("product_code", productId);
		insertDatamap.put("boutique_code", boutiqueId);
		try {
			DbUp.upTable("oc_boutique_product_rela").dataInsert(insertDatamap);
		} catch (Exception e) {
			mResult.inErrorMessage(939301049);
			e.printStackTrace();
		}
		return mResult;
	}
	/**
	 * validateSeller:(这里用一句话描述这个方法的作用). <br/>
	 * @author hxd
	 * @param sellerId
	 * @param boutiqueId
	 * @return
	 * @since JDK 1.6
	 */
	private int validateProduct(String productId,String boutiqueId)
	{
		MDataMap mp = new MDataMap();
		mp.put("product_code", productId);
		mp.put("boutique_code", boutiqueId);
		List<MDataMap> queryAll  =   DbUp.upTable("oc_boutique_product_rela").queryAll("", "", "", mp);
		return queryAll.size();
	}
}

