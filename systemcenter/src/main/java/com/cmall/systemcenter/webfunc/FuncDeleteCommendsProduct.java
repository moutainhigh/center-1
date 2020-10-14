package com.cmall.systemcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除推荐商品
 * @author GaoYang
 *
 */
public class FuncDeleteCommendsProduct extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//推荐商品的uid
				String uid=mSubMap.get("uid");
				//根据uid校验在推荐商品表中的此数据是否存在
				if(checkSku(uid)){
					deleteCommendsProduct(uid);
				}else{
					//返回提示信息
					mResult.setResultCode(949701043);
					mResult.setResultMessage(bInfo(949701043));
					return mResult;
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(949701042);
				mResult.setResultMessage(bInfo(949701042));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 根据uid删除推荐商品
	 * @param uid
	 */
	private void deleteCommendsProduct(String uid) {
		DbUp.upTable("agent_commends_product").delete("uid", uid);
	}
	
	/**
	 * 校验商品是否还存在
	 * @param uid
	 * @return
	 */
	private boolean checkSku(String uid) {
		int atCount = DbUp.upTable("agent_commends_product").count("uid", uid);
		if(atCount >= 1){
			return true;
		}
		return false;
	}

}
