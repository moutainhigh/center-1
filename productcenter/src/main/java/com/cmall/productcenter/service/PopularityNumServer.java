package com.cmall.productcenter.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 人气方法
 * @author zhouguohui
 * @version 1.0
 */
public class PopularityNumServer extends BaseClass{

	
	/** 暂时只支持惠家有  全部商品
	 * 人气公式 购物车*60% + 收藏%*40%
	 * @param sellerCode  SI2003为惠家有  SI2009为家有汇
	 * @return 返回数量
	 */
	public Map<String,Double> popularitySumNum(String sellerCode){
		String cartNum=null;
		String OperateTypeNum=null;
		/**
		 * 查询所有的收藏商品的数量
		 */
		String queryOperateTypeNum ="select count(*) as operate_type,product_code from familyhas.fh_product_collection  where operate_type='4497472000020001' group by product_code" ;
		List<Map<String, Object>> listOperateTypeNum = DbUp.upTable("pc_skuinfo").dataSqlList(queryOperateTypeNum, new MDataMap());
		
		 /**
         * 查询购物车下商品的数量
         */
		String queryCartNum = "select a.product_code product_code,sum(b.sumSkuNum) skunum"+
				       " from (select s.sku_code ,s.product_code from productcenter.pc_skuinfo s inner join productcenter.pc_productinfo p on s.product_code= p.product_code where p.seller_code='"+sellerCode+"' and p.product_status='4497153900060002') a left join "+  
		               " (select SUM(sku_num) sumSkuNum,sku_code from ordercenter.oc_shopCart  GROUP BY sku_code) b "+
	                   " on a.sku_code = b.sku_code where b.sku_code is not null group by product_code" ;
		List<Map<String, Object>> listCartNum = DbUp.upTable("pc_skuinfo").dataSqlList(queryCartNum, new MDataMap());
		
		/**
		 * 查询全部的product_code
		 */
		String querySku="select product_code from  productcenter.pc_productinfo  where seller_code='"+sellerCode+"' and product_status='4497153900060002'";
		List<Map<String, Object>> listProductCode = DbUp.upTable("pc_productinfo").dataSqlList(querySku, new MDataMap());
		
		Map<String,Double> mapCartNum = new HashMap<String, Double>();
		Map<String,Double> mapOperateTypeNum = new HashMap<String, Double>();
		Map<String,Double> mapPopularityNum = new HashMap<String, Double>();
		for(Map<String,Object> map : listCartNum){
			if(null==map.get("product_code")||"".equals(map.get("product_code"))){
				continue;
			}else{
				
				mapCartNum.put((String) map.get("product_code"),
						(Double) (Double.parseDouble((String) (StringUtils.isEmpty(map.get("skunum")+"")?0:map.get("skunum").toString()))));
			}
			
		}
		
		for(Map<String,Object> map : listOperateTypeNum){
			if(null==map.get("product_code")||"".equals(map.get("product_code"))){
				continue;
			}else{
				
				mapOperateTypeNum.put((String) map.get("product_code"),
						(Double) (Double.parseDouble((String) (StringUtils.isEmpty(map.get("operate_type")+"")?0:map.get("operate_type").toString()))));
			}
		}
		
		
		for(Map<String, Object> map : listProductCode){
			if(null==map.get("product_code")||"".equals(map.get("product_code"))){
				continue;
			}else{
				
				/**如果购物车商品数量为空默认为0**/
				if(null!=mapCartNum.get(map.get("product_code").toString().trim())){
					cartNum = mapCartNum.get(map.get("product_code").toString().trim()).toString();
				}else{
					cartNum="0";
				}
				
				/**如果收藏商品数量为空默认为0**/
				if(null!=mapOperateTypeNum.get(map.get("product_code").toString().trim())){
					OperateTypeNum = mapOperateTypeNum.get(map.get("product_code").toString().trim()).toString();
				}else{
					OperateTypeNum="0";
				}
				DecimalFormat df = new DecimalFormat("0.000");
				mapPopularityNum.put((String) map.get("product_code"),
						Double.parseDouble(df.format((Double.parseDouble(cartNum)*0.6)+(Double.parseDouble(OperateTypeNum)*0.4))));
			}
		}
		return mapPopularityNum;
	}
	
	
	
	
	/** 暂时只支持惠家有    传递商品编号  不支持全部商品
	 * 人气公式 购物车*60% + 收藏%*40%
	 * @param sellerCode  SI2003为惠家有  SI2009为家有汇
	 * @param productCode  可以为单个或多个，productCode中间用,号间隔
	 * @return 返回数量
	 */
	public Map<String,Double> popularityNumOne(String sellerCode,String productCode){
		Map<String,Double> mapPopularityNum = new HashMap<String, Double>();
		
		if (StringUtils.isEmpty(productCode)) {
			return mapPopularityNum;
		}
		
		String[] splitProductCode= productCode.split(",");
		for (int i = 0; i < splitProductCode.length; i++) {
			 /**
	         * 查询购物车下商品的数量 和 收藏商品的数量
	         */
			String queryNum =" select (select count(operate_type) from familyhas.fh_product_collection b where b.operate_type='4497472000020001' and b.product_code='"+splitProductCode[i]+"' ) as operate_type, "+ 
					         " (select SUM(sku_num) sumSkuNum from ordercenter.oc_shopCart where sku_code in (select sku_code  from productcenter.pc_skuinfo  where product_code='"+splitProductCode[i]+"' and seller_code='"+sellerCode+"' ) ) as sumSkuNum "+
					         " from  productcenter.pc_productinfo where product_code='"+splitProductCode[i]+"'";
			List<Map<String, Object>> listCartNum = DbUp.upTable("pc_productinfo").dataSqlList(queryNum, new MDataMap());
			for(Map<String,Object> map : listCartNum){
				
				String sumSkuNum = null;
				if(map.get("sumSkuNum")==null || map.get("sumSkuNum").equals("")){
					sumSkuNum="0";
				}else{
					sumSkuNum=map.get("sumSkuNum").toString();
				}
				String operateType = null;
				if(map.get("operate_type")==null || map.get("operate_type").equals("")){
					operateType="0";
				}else{
					operateType=map.get("operate_type").toString();
				}
				mapPopularityNum.put(splitProductCode[i], 
						(Double) (Double.parseDouble(sumSkuNum)*0.6)+
						(Double) (Double.parseDouble(operateType)*0.4));
				
			}
		}
		
		return mapPopularityNum;
	}
	
	
}
