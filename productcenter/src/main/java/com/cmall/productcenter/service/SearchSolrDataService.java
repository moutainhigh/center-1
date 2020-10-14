package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.Product;
import com.cmall.productcenter.util.SolrDataUtil;
import com.cmall.productcenter.util.SolrQueryUtil;
import com.cmall.productcenter.util.SolrjUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 惠家有  家有汇  系统
 * 全量更新索引库service层统一写在peoductcenter里面
 * @author zhouguohui
 * @version 1.0
 */
public class SearchSolrDataService  extends BaseClass{

	/**
	 * 实现方法
	 * @param sellerCode   系统编码（ SI2003代表惠家有系统   SI2009代表家有汇系统  SI3003代表沙皮狗系统）
	 * @param cluster     是否开始集群（yes代表开启集群      no代表关闭集群）
	 * @param mapMinSellPrice  家有汇最低商品价格 （惠家有此字段为NULL）
	 * @param mapMinSellPriceHjy  惠家有最低商品价格 （家有汇此字段为NULL）
	 * @return
	 */
	public int insertSolrData(String sellerCode,String cluster,Map<String,Double> mapMinSellPrice, Map<String,BigDecimal> mapMinSellPriceHjy ){
		int num=0;
		List<String> twoId = null;
		List<String> twoName = null;
		List<String> oneId = null;
		List<String> oneName = null;
		List<String> propertyValue=null;
		Product product = null;
		int number=0;
		List<Product> listProductSet = new ArrayList<Product>();
		new SolrDataUtil().getFacet(sellerCode);
		try{
			
			/***
			 * 统计销量 service类
			 */
			ProductService ps = new  ProductService();
			MDataMap productSalesMapHjy = null;
			MDataMap productSalesMapSpg = null;
			Map<String,Map<String,String>> productSalesMapJyh = null;
			if("SI2009".equals(sellerCode)){
				productSalesMapJyh = ps.getProductSales(sellerCode, "");
			}else if("SI2003".equals(sellerCode)){
			   productSalesMapHjy = ps.getProductFictitiousSales(sellerCode,null,30);
			}else if("SI3003".equals(sellerCode)){
				productSalesMapSpg = ps.getProductFictitiousSales(sellerCode,null,30);
			}
			
			/**
			 * 统计人气service类
			 */
			PopularityNumServer popularityNum =  new PopularityNumServer();
			Map<String,Double> mapPopularityNum =null;
			if("SI2003".equals(sellerCode)){
				mapPopularityNum = popularityNum.popularitySumNum(sellerCode);
			}else if("SI3003".equals(sellerCode)){
				mapPopularityNum = popularityNum.popularitySumNum(sellerCode);
			}
			
			/**
			 * 统计是否有货     3.8.0的库存加版空
			 */
			ProductStoreService  pss = new ProductStoreService();
			Map<String,Integer> mapStockNum = null;
			if(VersionHelper.checkServerVersion("3.5.72.55")){
				mapStockNum = pss.getStockNumAll(sellerCode, null,1);
			}else{
				mapStockNum = pss.getStockNumAll(sellerCode, null);
			}
			
            /**
             * 查询惠家有 家有汇系统下的所有商品信息
             */
			String query = "select p.product_code,p.product_name,p.mainpic_url,p.market_price,p.min_sell_price,p.update_time,p.brand_code,t.description_info,t.keyword,"+
                           "(select f.brand_name from pc_brandinfo f where p.brand_code=f.brand_code) as brand_name "+
                           "  from pc_productinfo p  join pc_productdescription t  on t.product_code = p.product_code    where p.seller_code=:seller_code and p.product_status='4497153900060002'";
			List<Map<String, Object>> listProduct = DbUp.upTable("pc_productinfo").dataSqlList(query, new MDataMap("seller_code",sellerCode));
			
			
			/**
			 * 查询所有商品所对应的标签
			 */
			String queryPropertyValue = "select GROUP_CONCAT(property_value SEPARATOR ',') as property_value,product_code from  pc_productproperty where property_type='449736200003' GROUP BY product_code";
			List<Map<String, Object>> listPropertyValue = DbUp.upTable("pc_productproperty").dataSqlList(queryPropertyValue, new MDataMap());
			Map<String,String> mapValue =new HashMap<String,String>();
			if(null!=listPropertyValue || !"".equals(listPropertyValue)){
				for (Map<String,Object> map : listPropertyValue) {
					mapValue.put( (null==map.get("product_code") || "".equals(map.get("product_code")))  ? "0" : map.get("product_code").toString() , (null==map.get("property_value") || "".equals(map.get("property_value"))) ? "" :map.get("property_value").toString());
				}
			}
			
				/**
				 * 开始循环遍历封装List<Product>
				 */
				for(Map<String,Object> map : listProduct){
					twoId = new ArrayList<String>();
					twoName = new ArrayList<String>();
					oneId = new ArrayList<String>();
					oneName = new ArrayList<String>();
					propertyValue=new ArrayList<String>();
					product = new Product();
					if(null==map.get("product_code")||"".equals(map.get("product_code"))){
						product.setProductCode("");
					}else{
						product.setProductCode(map.get("product_code").toString().trim());
					}
					
					
					if(null==mapStockNum.get(map.get("product_code").toString().trim()) || "".equals(mapStockNum.get(map.get("product_code").toString().trim()))){
						product.setStockNum(0);
					}else{
						product.setStockNum(Integer.parseInt(mapStockNum.get(map.get("product_code").toString().trim()).toString()));
					}
					
					
					/**添加品牌字段信息**/
					if(null==map.get("brand_code")||"".equals(map.get("brand_code"))){
						product.setBrandCode("");
					}else{
						product.setBrandCode(map.get("brand_code").toString().trim());
					}
					
					if(null==map.get("brand_name")||"".equals(map.get("brand_name"))){
						product.setBrandCodeName("");
					}else{
						product.setBrandCodeName(map.get("brand_name").toString().trim());
					}
					/**添加品牌字段信息结束**/
					
					if(null==map.get("product_name")||"".equals(map.get("product_name"))){
						product.setProductName("");
					}else{
						product.setProductName(map.get("product_name").toString().trim());
					}
					
					
					if(null==map.get("description_info")||"".equals(map.get("description_info"))){
						product.setProductDetails("");
					}else{
						product.setProductDetails(map.get("description_info").toString());
					}
					if(null==map.get("keyword")||"".equals(map.get("keyword"))){
						product.setTagList("");
					}else{
						product.setTagList(map.get("keyword").toString());
					}
					if(null==map.get("mainpic_url")||"".equals(map.get("mainpic_url"))){
						product.setMainpicUrl("");
					}else{
						product.setMainpicUrl(map.get("mainpic_url").toString().trim());
					}
					if(null==map.get("market_price") || "".equals(map.get("market_price"))){
						product.setOriginalPrice(0.0);
					}else{
						product.setOriginalPrice(Double.parseDouble(map.get("market_price").toString()));
					}
					
					
					
					/**
					 * 封装价格  和 销量 两个系统的算法不一样
					 */
					if("SI2009".equals(sellerCode)){
						/**最后价格**/
						product.setCurrentPrice((null==mapMinSellPrice.get(map.get("product_code").toString()) || "".equals(mapMinSellPrice.get(map.get("product_code").toString())))?0.0 : mapMinSellPrice.get(map.get("product_code").toString()));
						/**最后的销量***/
						Map<String,String> mapNumber = productSalesMapJyh.get(map.get("product_code").toString().trim());
						product.setProductNumber((mapNumber==null || "".equals(mapNumber)) ? 0 : Integer.parseInt(mapNumber.get("thirty_day").toString().trim()));
						
					}else if("SI2003".equals(sellerCode)){
						
						/**最后价格**/
						product.setCurrentPrice((null==mapMinSellPriceHjy.get(map.get("product_code").toString()) || "".equals(mapMinSellPriceHjy.get(map.get("product_code").toString())))?0.0 : Double.parseDouble(mapMinSellPriceHjy.get(map.get("product_code").toString()).toString()));
						/**最后的销量***/
						String mapNumber =productSalesMapHjy.get(map.get("product_code").toString().trim());
						if(StringUtils.isNotEmpty(mapNumber)){
							number=Integer.parseInt(mapNumber);
						}else{
							number=0;
						}
						product.setProductNumber(number);
					}else if("SI3003".equals(sellerCode)){
						/**最后价格**/
						product.setCurrentPrice((null==mapMinSellPriceHjy.get(map.get("product_code").toString()) || "".equals(mapMinSellPriceHjy.get(map.get("product_code").toString())))?0.0 : Double.parseDouble(mapMinSellPriceHjy.get(map.get("product_code").toString()).toString()));
						
						
						/***方法需要修改*********************************************************/
						
						/**最后的销量***/
						String mapNumber =productSalesMapSpg.get(map.get("product_code").toString().trim());
						if(StringUtils.isNotEmpty(mapNumber)){
							number=Integer.parseInt(mapNumber);
						}else{
							number=0;
						}
						product.setProductNumber(number);
						product.setProductNumber(0);
					}
					
					
					if(null==map.get("update_time") || "".equals(map.get("update_time"))){
					}else{
						product.setUpdateTime(SolrjUtil.getYear(map.get("update_time").toString()));
					}
					//查询当前商品所在的分类下
					String querySql = "SELECT GROUP_CONCAT(category_code SEPARATOR ',') AS category_code FROM uc_sellercategory_product_relation WHERE seller_code=:seller_code and product_code=:product_code GROUP BY product_code";
					Map<String, Object> mapCategoryCode = DbUp.upTable("uc_sellercategory_product_relation").dataSqlOne(querySql, new MDataMap("seller_code",sellerCode,"product_code",map.get("product_code").toString().trim()));
					
					//查询当前商品所在的一级分类和二级分类
					if(null!=mapCategoryCode && !"".equals(mapCategoryCode)){
						String[] splitValue = mapCategoryCode.get("category_code").toString().split(",");
						if(null == splitValue || "".equals(splitValue) || splitValue.length<=0){
							oneId.add(null);
							oneName.add(null);
							twoId.add(null);
							twoName.add(null);
						}else{
							for (int i = 0; i < splitValue.length; i++) {
								String queryCategory = "select n.category_name as category_name_one,(select parent_code from uc_sellercategory  where category_code=:category_code and seller_code=:seller_code) as parent_code_one ,"
										+"(select category_name from uc_sellercategory  where category_code=:category_code and seller_code=:seller_code) as category_name_two"
										+" from uc_sellercategory n where n.category_code="
										+"(select parent_code from uc_sellercategory where category_code=:category_code and seller_code=:seller_code)"  
										+"and n.seller_code=:seller_code"; 
								Map<String, Object> listCategory = DbUp.upTable("uc_sellercategory").dataSqlOne(queryCategory, new MDataMap("category_code",splitValue[i],"seller_code",sellerCode));
								if(null==listCategory || "".equals(listCategory) || listCategory.size()<=0){
									oneId.add(null);
									oneName.add(null);
									twoId.add(null);
									twoName.add(null);
								}else{
									
									//为了兼容二级分类和三级分类  如果查询商品挂在12位的编号下，证明该商品没有三级分类
									if(splitValue[i].trim().length()==12){
										oneId.add(splitValue[i]);
										oneName.add((null==listCategory.get("category_name_two") || "".equals(listCategory.get("category_name_two")))? "":listCategory.get("category_name_two").toString());
										twoId.add("");
										twoName.add("");
									}
									
									//为了兼容二级分类和三级分类  如果查询商品挂在16位的编号下，证明该商品挂在三级分类
									if(splitValue[i].trim().length()==16){
										twoId.add(splitValue[i]);
										twoName.add((null==listCategory.get("category_name_two") || "".equals(listCategory.get("category_name_two")))? "":listCategory.get("category_name_two").toString());
										oneId.add((null==listCategory.get("parent_code_one") || "".equals(listCategory.get("parent_code_one"))) ? "":listCategory.get("parent_code_one").toString());
										oneName.add((null==listCategory.get("category_name_one") || "".equals(listCategory.get("category_name_one")))?"":listCategory.get("category_name_one").toString());
									}
								}
								
							}
						}
						
					}
					
					propertyValue.add(null==mapValue.get(map.get("product_code").toString().trim()) ? null :mapValue.get(map.get("product_code").toString().trim()).toString().trim());
					product.setPropertyValue(propertyValue);
					product.setOneId(oneId);
					product.setOneName(oneName);
					product.setTwoId(twoId);
					product.setTwoName(twoName);
					product.setSellerCode(sellerCode);
					/******惠家有有人气公式，家有汇人气为0*****/
					if("SI2003".equals(sellerCode)){
						product.setPopularityNum(null==mapPopularityNum.get(map.get("product_code").toString().trim())?0.0:mapPopularityNum.get(map.get("product_code").toString().trim()));
					}else if("SI3003".equals(sellerCode)){
						product.setPopularityNum(null==mapPopularityNum.get(map.get("product_code").toString().trim())?0.0:mapPopularityNum.get(map.get("product_code").toString().trim()));
					}else{
						product.setPopularityNum(0.0);
					}
					/**************结束*******************/
					listProductSet.add(product);
				}
 				SolrQueryUtil.delete(sellerCode);
 				SolrQueryUtil.addList(listProductSet, sellerCode);
			    num=1;
		}catch(Exception e){
			  num=0;
			e.printStackTrace();
		}
		return num;
	}
	
}
