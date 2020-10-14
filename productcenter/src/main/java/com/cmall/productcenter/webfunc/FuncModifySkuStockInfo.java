package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     FuncModifySkuStockInfo 
 * 类描述：     商品总览中修改SKU库存信息
 * 创建人：     gaoy
 * 创建时间：2013年10月15日下午3:51:34
 * 修改人：     gaoy
 * 修改时间：2013年10月15日下午3:51:34
 * 修改备注：  
 */
public class FuncModifySkuStockInfo extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		try{
			//获取页面数据
			MDataMap mWindowMap = mDataMap.upSubMap("window_modify_stock_");
			
			String sData = mWindowMap.toString().replace("{", "").replace("}", "");
			String[] strList = sData.split(",");
			//判断是否是重复数据，避免同一样条数据被重复提交
			String tempUid="";
			for(int i = 0;i<strList.length;i++){
				MDataMap updMap = new MDataMap();
				String skuCode = "";
				String price = "";
				String num = "";
				String skuAdv = "";
				String sellProductcode = "";
				String uid1 = strList[i].substring(strList[i].indexOf("_")+1,strList[i].indexOf("="));
				for(int j = i+1;j<strList.length;j++){
					String uid2 = strList[j].substring(strList[j].indexOf("_")+1,strList[j].indexOf("="));
					//判断UID是否相等，相等则说明是同一条数据
					if(!uid1.equals(tempUid) && uid1.equals(uid2)){
						tempUid = uid2;
						skuCode = mWindowMap.get("skuCode_"+uid1);
						price = mWindowMap.get("sellPrice_"+uid1);
						num = mWindowMap.get("stcokNum_"+uid1);
						sellProductcode = mWindowMap.get("sellProductcode_"+uid1);
						skuAdv = mWindowMap.get("skuAdv_"+uid1);
						break;
					}
				}
				
				//同一条数据进行更新操作
				if(StringUtils.isNotBlank(uid1) && StringUtils.isNotBlank(price) && StringUtils.isNotBlank(num)){
					updMap.put("uid", uid1);
					updMap.put("sell_price", price);
					updMap.put("stock_num", num);
					updMap.put("sku_adv", skuAdv);
					updMap.put("sell_productcode", sellProductcode);
					String updColumn = "sell_price,stock_num,sku_adv,sell_productcode";
					DbUp.upTable("pc_skuinfo").dataUpdate(updMap,updColumn,"uid");
					
					//调用缓存处理
					ProductJmsSupport pjs = new ProductJmsSupport();
					pjs.onChangeForSkuChangeAll(skuCode);
				}
			}
			return mResult;
		}catch (Exception ex) {
			mResult.setResultCode(941901035);
			mResult.setResultMessage(bInfo(941901035));
			return mResult;
		}
	}
}