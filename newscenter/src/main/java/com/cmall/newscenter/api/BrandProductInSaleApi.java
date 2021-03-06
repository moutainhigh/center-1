package com.cmall.newscenter.api;

import java.util.List;

import com.cmall.newscenter.model.BrandProductInSaleInput;
import com.cmall.newscenter.model.BrandProductInSaleResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 品牌_在售商品列表Api
 * @author lqiang
 * date: 2014-07-10
 * @version1.0
 */
public class BrandProductInSaleApi extends RootApiForMember<BrandProductInSaleResult, BrandProductInSaleInput> {
	/**
	 * @author yangrong
	 */

	public BrandProductInSaleResult Process(BrandProductInSaleInput inputParam,
			MDataMap mRequestMap) {
		
		BrandProductInSaleResult result = new BrandProductInSaleResult();
		String appCode = bConfig("newscenter.app_code");
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			ProductService productService = new ProductService();
			List<PcProductinfo> productSkuInfoList = productService.getSellProducts(appCode, inputParam.getCategory(),inputParam.getProduct());
			
			//商品总数
			int totalNum = productSkuInfoList.size();
			int offset = inputParam.getPaging().getOffset();//起始页
			int limit = inputParam.getPaging().getLimit();//每页条数
			int startNum = limit*offset;//开始条数
			int endNum = startNum+limit;//结束条数
			int more = 1;//有更多数据
			if(endNum>totalNum){
				endNum = totalNum;
				more = 0;
			}
			//如果起始条件大于总数则返回0条数据
			if(startNum>totalNum){
				startNum = 0;
				endNum = 0;
				more = 0;
			}
			//分页信息
			PageResults pageResults = new PageResults();
			pageResults.setTotal(totalNum);
			pageResults.setCount(endNum-startNum);
			pageResults.setMore(more);
			result.setPaged(pageResults);
			//返回界面商品列表
			List<PcProductinfo> subList = productSkuInfoList.subList(startNum, endNum);
			//关联商品其它信息
			FuncQueryProductInfo queryProduct = new FuncQueryProductInfo();
			String userCode = "";
			if(getFlagLogin()){
				userCode = getOauthInfo().getUserCode();
			}
			result.setProducts(queryProduct.queryProductInSale(subList, userCode,appCode));
			
		}
		return result;
	}
}

