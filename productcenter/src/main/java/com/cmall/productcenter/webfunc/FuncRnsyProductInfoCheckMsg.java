package com.cmall.productcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 惠家有沙皮狗商品互联互通的一些必要检查信息
 * 
 * @author ligj
 * 
 */
public class FuncRnsyProductInfoCheckMsg extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		String sharpeiSellerCode = "SI3003";
		String familyhasSellerCode = "SI2003";
		String productCodePrefix = "6";
		String rnsyProductCode = mDataMap.get("zw_f_product_code"); // 需要同步上商品编号
		String targetSystem = mDataMap.get("targetSystem");			//目标系统
		// 商品编号为空，提示信息：商品编号错误，查不到商品信息！
		if (StringUtils.isBlank(rnsyProductCode)) {
			mResult.inErrorMessage(941901119);
			return mResult;
		}
		if (mResult.upFlagTrue()) {
			String sWhereFilter = " product_code='" + rnsyProductCode
					+ "' or product_code='" + productCodePrefix
					+ rnsyProductCode + "'";
			List<MDataMap> productMapList = DbUp
					.upTable("pc_productinfo")
					.queryAll(
							"product_code,seller_code,small_seller_code,product_status,product_code_copy",
							"", sWhereFilter, null);
			int addFlag = 1;  //为1时进行复制新增，0进行更新操作,2目标商品为上架状态
			int copyFlag = 1; // 是否为有来源商品编号的商品，1是，0否,为是时不允许同步
			// 查不到商品信息时返回错误，提示信息：商品编号错误，查不到商品信息，此处应该输入被复制的商品编号！
			if (productMapList == null || productMapList.isEmpty()) {
				mResult.inErrorMessage(941901119);
				return mResult;
			}
			String targetProductCode = "";
			for (MDataMap productMap : productMapList) {
				String sellerCode = productMap.get("seller_code");
				 String productStatus = productMap.get("product_status");
				String productCodeCopy = productMap.get("product_code_copy");
				
				if ("SI3003".equals(targetSystem)) {
					// 判断是否有来源商品编号的商品
					if (StringUtils.isEmpty(productCodeCopy)
							&& sellerCode.equals(familyhasSellerCode)) {
						copyFlag = 0;
					}
					if (sellerCode.equals(sharpeiSellerCode)) {
						targetProductCode =  productMap.get("product_code");
						if ("4497153900060002".equals(productStatus)) {
							addFlag = 2;
						}else{
							addFlag = 0;
						}
					}
				}else if ("SI2003".equals(targetSystem)) {
					// 判断是否有来源商品编号的商品
					if (StringUtils.isEmpty(productCodeCopy)
							&& sellerCode.equals(sharpeiSellerCode)) {
						copyFlag = 0;
					}
					if (sellerCode.equals(familyhasSellerCode)) {
						targetProductCode =  productMap.get("product_code");
						if ("4497153900060002".equals(productStatus)) {
							addFlag = 2;
						}else{
							addFlag = 0;
						}
					}
				}
			}
			// 二期为有来源商品编号的商品不能同步，提示信息：此商品不允许被同步，请查看该商品的来源。
			if (copyFlag == 1) {
				mResult.inErrorMessage(941901118);
				return mResult;
			}
			if (addFlag == 0) {
				mResult.setResultMessage(bInfo(941901121, targetProductCode));
			}else if (addFlag == 1) {
				mResult.setResultMessage(bInfo(941901122, targetProductCode));
			}else if (addFlag == 2) {
				mResult.setResultMessage(bInfo(941901120, targetProductCode));
			}
		}
		return mResult;
	}
}