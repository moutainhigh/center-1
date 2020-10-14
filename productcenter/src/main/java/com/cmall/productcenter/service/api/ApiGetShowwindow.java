package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.api.ApiGetShowwindowInput;
import com.cmall.productcenter.model.api.ApiGetSkusResult;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ApiGetShowwindow 
 * 类描述：     获取卖家橱窗产品信息
 * 创建人：     GaoYang
 * 创建时间：2013年10月30日上午10:20:11
 * 修改人：     GaoYang
 * 修改时间：2013年10月30日上午10:20:11
 * 修改备注：
 * 
 */
public class ApiGetShowwindow extends RootApi<ApiGetSkusResult,ApiGetShowwindowInput>{
	
	public ApiGetSkusResult Process(ApiGetShowwindowInput inputParam, MDataMap mRequestMap) {
		
		ApiGetSkusResult result = new ApiGetSkusResult();
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService ps = new ProductService();
			//获取传入的卖家编号
			String sellerCode = inputParam.getSelleCode();
			//传入的卖家编号为空时，返回提示信息
			if(StringUtils.isBlank(sellerCode)){
				result.setResultMessage(bInfo(941901029,"商家编号"));
				result.setResultCode(941901029);
			}else{
				//根据传入的卖家编号查询橱窗商品管理表，获取该商家的橱窗产品(SKU)
				List<String> skuTempList = new ArrayList<String>();
				for(MDataMap mMap : DbUp.upTable("pc_showwindow").queryByWhere("seller_code", sellerCode)){
					skuTempList.add(mMap.get("sku_code"));
				}
				//该商家的橱窗产品存在时，返回橱窗产品的SKU信息
				if(skuTempList != null && skuTempList.size() > 0){
					//获取卖家的所有橱窗商品(SKU)
					String skuStrs = StringUtils.join(skuTempList, ",");
					//根据获取的橱窗产品来获取相应的SKU详细信息
					List<ProductSkuInfo> skuInfoList = ps.getSkuListForI(skuStrs);
					//SKU详细信息存在时，返回SKU信息
					if(skuInfoList != null && skuInfoList.size() > 0){
						result.setSkuList(skuInfoList);
					}
				}
			}
		}
		return result;
	}
}