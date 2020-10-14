/**
 * Project Name:ordercenter
 * File Name:AddSeller.java
 * Package Name:com.cmall.ordercenter.webfunc
 * Date:2013年11月4日上午11:15:08
 *
*/

package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName:AddSeller <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月4日 上午11:15:08 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class AddSeller extends RootFunc {
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String  sellerId=  getSelleridByUid(mDataMap.get("zw_f_uid"));
		String boutiqueId = getBoutidByUid(mDataMap.get("uuid"));
		
		if(validateSeller(sellerId, boutiqueId) >= 1)
		{
			mResult.inErrorMessage(939301046);
			return mResult;
		}
		mResult = insertRelation(sellerId, boutiqueId);
		return mResult;
	}
	/**
	 * @param uid  商家UID
	 * @return
	 */
	public String  getSelleridByUid(String uid)
	{
		MDataMap prodcutData = DbUp.upTable("uc_sellerinfo").one("uid", uid);
		return prodcutData.get("seller_code");
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
	public  MWebResult insertRelation(String sellerId,String boutiqueId)
	{
		MWebResult mResult = new MWebResult();
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("seller_code", sellerId);
		insertDatamap.put("boutique_code", boutiqueId);
		try {
			DbUp.upTable("oc_boutique_seller_rela").dataInsert(insertDatamap);
		} catch (Exception e) {
			mResult.inErrorMessage(939301045);
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
	public int validateSeller(String sellerId,String boutiqueId)
	{
		MDataMap mp = new MDataMap();
		mp.put("seller_code", sellerId);
		mp.put("boutique_code", boutiqueId);
		return DbUp.upTable("oc_boutique_seller_rela").queryAll("", "", "", mp).size();
	}
}

