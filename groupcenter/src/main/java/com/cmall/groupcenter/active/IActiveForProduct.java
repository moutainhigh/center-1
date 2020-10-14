package com.cmall.groupcenter.active;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品类活动接口
 * @author jlin
 *
 */
public interface IActiveForProduct extends IActiveDo {

	/**
	 * 活动处理业务,商品不参与该活动，返回 null 即可
	 * @param activeType 活动类型信息
	 * @param pcSkuInfo sku信息
	 * @param skuNum sku数量
	 * @param buyerCode 买家编号
	 * @param appCode 活动所属app
	 * @param rootResult 存放错误信息
	 * @return 若不参与活动，返回null或负数
	 */
	public BaseActive doProcess (ActiveType activeType,ProductSkuInfo pcSkuInfo,int skuNum,String appCode,MDataMap paramsExt,RootResultWeb rootResult);
	
}
