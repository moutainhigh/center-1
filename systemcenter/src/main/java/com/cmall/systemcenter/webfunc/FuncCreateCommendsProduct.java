package com.cmall.systemcenter.webfunc;

import java.util.UUID;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加新的推荐商品
 * @author GaoYang
 *
 */
public class FuncCreateCommendsProduct extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//获取页面选择的行业编码
				String tradeCodeList=mSubMap.get("trade_code");
				String[] tradeCodeArry = tradeCodeList.split(",");
				//获取页面选择的商品编码(SKU)
				String skuCodeList=mSubMap.get("sku_code");
				String[] skuCodeArray = skuCodeList.split(",");
				
				//插入数据，一个商品可以添加到多个行业
				if(tradeCodeArry.length > 0 && skuCodeArray.length > 0){
					for(int i  = 0;i < tradeCodeArry.length;i++){
						String tradeCode = tradeCodeArry[i];
						for(int j = 0;j < skuCodeArray.length;j++){
							String skuCode = skuCodeArray[j];
							//校验此行业下是否已存在此商品,如果存在就过滤掉,否则登陆
							if(checkSku(tradeCode,skuCode)){
								insertCommendsProduct(tradeCode,skuCode);
							}
						}
					}
				}else{
					mResult.setResultCode(949701040);
					mResult.setResultMessage(bInfo(949701040));
					return mResult;
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(949701040);
				mResult.setResultMessage(bInfo(949701040));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 插入新的推荐商品
	 * @param tradeCode
	 * @param skuCode
	 */
	private void insertCommendsProduct(String tradeCode, String skuCode) {
		MDataMap insMap = new MDataMap();
		UUID uuid = UUID.randomUUID();
		insMap.put("uid", uuid.toString().replace("-", ""));
		insMap.put("trade_code", tradeCode);
		insMap.put("sku_code", skuCode);
		DbUp.upTable("agent_commends_product").dataInsert(insMap);
	}

	/**
	 * 校验此行业下是否已存在此商品
	 * @param tradeCode
	 * @param skuCode
	 * @return
	 */
	private boolean checkSku(String tradeCode, String skuCode) {
		String sWhere = "trade_code='" + tradeCode + "' and sku_code ='" + skuCode + "'";
		int atCount = DbUp.upTable("agent_commends_product").dataCount(sWhere, new MDataMap());
		if(atCount <= 0){
			return true;
		}
		return false;
	}
	
}
