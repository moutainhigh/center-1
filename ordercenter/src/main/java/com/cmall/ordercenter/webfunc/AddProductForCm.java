package com.cmall.ordercenter.webfunc;

import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProductForCm extends RootFunc{
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String  productId=  getProductidByUid(mDataMap.get("zw_f_uid"));          //商品uuid
		String serviceId = getBoutidByUid(mDataMap.get("uuid"));              //售后服务的uuid
		
		if(validateProduct(productId, serviceId) >= 1)
		{
			mResult.inErrorMessage(939301050);
			return mResult;
		}
		mResult = insertRelation(productId, serviceId);
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
	 * @param uid  售后服务UID
	 * @return
	 */
	public String  getBoutidByUid(String uid)
	{
		MDataMap prodcutData = DbUp.upTable("pc_after_service").one("uid", uid);
		return prodcutData.get("service_code");
	}
	/**
	 * @param sellerId 商家UID
	 * @param serviceId 售后服务UID
	 * @return
	 */
	public  MWebResult insertRelation(String productId,String serviceId)
	{
		MWebResult mResult = new MWebResult();
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("product_code", productId);
		insertDatamap.put("service_code", serviceId);
		try {
			DbUp.upTable("pc_service_product").dataInsert(insertDatamap);
		} catch (Exception e) {
			mResult.inErrorMessage(939301049);
			e.printStackTrace();
		}
		return mResult;
	}
	/**
	 * validateSeller:(这里用一句话描述这个方法的作用). <br/>
	 * @author yangrong
	 * @param sellerId
	 * @param serviceId
	 * @return
	 * @since JDK 1.6
	 */
	private int validateProduct(String productId,String serviceId)
	{
		MDataMap mp = new MDataMap();
		mp.put("product_code", productId);
		mp.put("service_code", serviceId);
		List<MDataMap> queryAll  =   DbUp.upTable("pc_service_product").queryAll("", "", "", mp);
		return queryAll.size();
	}
}

