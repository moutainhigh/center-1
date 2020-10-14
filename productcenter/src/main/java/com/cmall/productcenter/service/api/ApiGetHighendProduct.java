package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.HighendProductInfo;
import com.cmall.productcenter.model.api.ApiGetHighendProductInput;
import com.cmall.productcenter.model.api.ApiGetHighendProductResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ApiGetShowwindow 
 * 类描述：     获取高端商品信息
 * 创建人：     GaoYang
 * 创建时间：2013年11月16日下午4:33:11
 * 修改人：     GaoYang
 * 修改时间：2013年11月16日下午4:33:11
 * 修改备注：
 *
 */
public class ApiGetHighendProduct extends RootApi<ApiGetHighendProductResult,ApiGetHighendProductInput>{

	public ApiGetHighendProductResult Process(
			ApiGetHighendProductInput inputParam, MDataMap mRequestMap) {
		
		ApiGetHighendProductResult result = new ApiGetHighendProductResult();
		
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			//获取传入的高端商品名称
			String highendProductName = inputParam.getHighendProductName();
			//获取传入的高端商品分类
			String highendType = inputParam.getHighendType();
			
			//查询项目
			String sFields = "product_code,highend_product_name,min_sell_price,mainpic_url";
			//排序项目(1:ZID ASC;2:ZID DESC;3:PRICE ASC;4:PRICE DESE)
			String getSortType = inputParam.getSortType();
			String sOrders = "zid";
			if(StringUtils.isNotBlank(getSortType)){
				if("1".equals(getSortType)){
					sOrders = "zid";
				}else if("2".equals(getSortType)){
					sOrders = "-zid";
				}else if("3".equals(getSortType)){
					sOrders = "min_sell_price";
				}else if("4".equals(getSortType)){
					sOrders = "-min_sell_price";
				}else{
					sOrders = "zid";
				}
			}
			
			//当前页
			int iStart = inputParam.getPageIndex();
			if(iStart <= 0){
				iStart = 1;
			}
			//每页数量
			int iNumber = inputParam.getPageSize();
			if(iNumber <= 0){
				iNumber = 10;
			}

			//前台页面输入商品名称进行搜索查询
			if(StringUtils.isNotBlank(highendProductName)){
				String sWhere = "highend_product_name like '%" + highendProductName + "%' and flag_usable = '1'";
				searchHighendProduct(sFields,sOrders,sWhere,iStart,iNumber,result);
			} else {
				//前台页面进行精品汇或是好物产分类查询
				if(StringUtils.isBlank(highendType)){
					result.setResultMessage(bInfo(941901029,"高端商品分类"));
					result.setResultCode(941901029);
				}else if(!highendType.startsWith("44974634")){
					result.setResultMessage(bInfo(941901032));
					result.setResultCode(941901032);
				}else{
					//以高端商品分类为条件进行模糊查询
					String sWhere = "highend_type like '" + highendType + "%' and flag_usable = '1'";
					searchHighendProduct(sFields,sOrders,sWhere,iStart,iNumber,result);
				}
			}
		}
		return result;
	}

	/**
	 * 获取高端商品信息
	 * @param sFields
	 * @param sOrders
	 * @param sWhere
	 * @param iStart
	 * @param iNumber
	 * @param result 
	 */
	private ApiGetHighendProductResult searchHighendProduct(String sFields, String sOrders,
			String sWhere, int iStart, int iNumber, ApiGetHighendProductResult result) {
		
		//根据传入的高端商品名称或是分类查询高端商品管理表，获取高端商品信息
		List<HighendProductInfo> productList = new ArrayList<HighendProductInfo>();
		List<MDataMap> hightendData = new ArrayList<MDataMap>();
		HighendProductInfo highInfo = new HighendProductInfo();
		
//		//过滤商品信息表中不可售的商品
//		List<MDataMap> saleProductList = new ArrayList<MDataMap>();
//		StringBuffer strBuff = new StringBuffer();
		
		//高端商品总数量
		int highendCount = 0;
		//分类分页排序查询
		hightendData = DbUp.upTable("pc_highend_productinfo").query(sFields, sOrders, sWhere, null, (iStart-1)*iNumber, iNumber);
		
//		//过滤掉商品信息表中不可售的商品
//		if(hightendData.size() > 0){
//			strBuff.append("flag_sale = 0 and product_code in (");
//			for(int i = 0;i<hightendData.size();i++){
//				String tempCode = hightendData.get(i).get("product_code");
//				if(i>0){
//					strBuff.append(",");
//				}
//				strBuff.append(tempCode);
//			}
//			strBuff.append(") ");
//			//获取不可售商品
//			saleProductList = DbUp.upTable("pc_productinfo").queryAll("product_code", "", strBuff.toString(), new MDataMap());
//			//过滤不可售商品
//			for(int len = 0; len < saleProductList.size();len++){
//				String saleProductCode = saleProductList.get(len).get("product_code");
//				for(int j=hightendData.size() - 1; j >= 0; j--){
//					String tempCode = hightendData.get(j).get("product_code");
//					if(tempCode.equals(saleProductCode)){
//						hightendData.remove(j);
//					}
//				}
//			}
//		}
		
		//获取过滤后的高端商品
		for(int i = 0;i<hightendData.size();i++){
			highInfo = new SerializeSupport<HighendProductInfo>().serialize(hightendData.get(i),new HighendProductInfo());
			productList.add(highInfo);
		}
		
		//统计总数量
//		highendCount = DbUp.upTable("pc_highend_productinfo").dataCount(sWhere,null);
		highendCount = hightendData.size();
		//高端商品存在时，返回商品信息列表
		if(productList != null && productList.size() >0){
			result.setProductList(productList);
			result.setHighendCount(highendCount);
		}else{
			//高端商品不存在时，返回提示信息
			result.setResultMessage(bInfo(941901031));
			result.setResultCode(941901031);
		}
		return result;
	}
}