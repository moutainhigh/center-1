package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除商品品牌
 * @author lgx
 *
 */
public class FuncDeleteBrand extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				// 品牌的uid
				String uid=mSubMap.get("uid");
				MDataMap brandData = DbUp.upTable("pc_brandinfo").one("uid", uid);
				if(null != brandData && brandData.size() > 0) {					
					// 检验该品牌下是否有上架商品
					if(checkBrandInfo(brandData)){
						// 返回提示信息
						mResult.setResultCode(-1);
						mResult.setResultMessage("该品牌有上架商品，请下架商品后再删除！");
						return mResult;
					}else{
						// 没有上架商品则删除品牌(将品牌表要删除的数据存入pc_brandinfo_delete表中,然后再删除品牌表数据)
						deleteBrand(brandData);
						if (mResult.upFlagTrue()) {
							mResult.setResultMessage(bInfo(969909001));
						}
					}
				}else {
					mResult.setResultCode(-1);
					mResult.setResultMessage("未查询到品牌信息！");
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(-1);
				mResult.setResultMessage("删除品牌发生错误！");
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 根据uid删除品牌(更改品牌的is_delete(是否删除)状态为 1,同时将flag_enable(是否可用)改为 0)
	 * @param brandData
	 */
	private void deleteBrand(MDataMap brandData) {
		/*(将品牌表要删除的数据存入pc_brandinfo_delete表中,然后再删除品牌表数据)
		brandData.remove("zid");
		DbUp.upTable("pc_brandinfo_delete").dataInsert(brandData);
		DbUp.upTable("pc_brandinfo").delete("uid", brandData.get("uid"));*/
		brandData.put("is_delete", "1");
		brandData.put("flag_enable", "0");
		DbUp.upTable("pc_brandinfo").dataUpdate(brandData, "is_delete,flag_enable", "uid");
	}

	/**
	 * 检验品牌下是否有上架商品
	 * @param brandData
	 * @return
	 */
	private boolean checkBrandInfo(MDataMap brandData) {
		// 品牌下没有上架的商品时可以删除。
		int bradbCount = DbUp.upTable("pc_productinfo").count("brand_code", brandData.get("brand_code"),"product_status","4497153900060002");
		if (bradbCount > 0 )  return true;
		
		return false;
	}
}
