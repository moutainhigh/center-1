package com.cmall.productcenter.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.Product;
import com.cmall.productcenter.model.SolrDataFacet;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.xmassystem.load.LoadProductSales;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductSales;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/***
 * 该方法只为创建solr索引库提供
 * @author zhouguohui
 * @version 1.0 2015-08-15
 */
public class SolrDataUtil extends BaseClass{
	
	/**
	 * 获取人气值的方法  
	 * 暂时只支持  惠家有 沙皮狗
	 * 家有汇暂时没有人气值默认为0
	 * @param sellerCode 对应系统编号（惠家有 SI2003  沙皮狗 SI3003 ）
	 * @return 
	 */
	public Map<String,Double> getPopularityNum(String sellerCode){
		Map<String,Double> popularityNum = new HashMap<String,Double>();
		if(sellerCode.equals("SI2009")){
			return popularityNum;
		}
		String sql = "select product_code,popularity_num from fh_popularity where seller_code=:sellerCode";
		List<Map<String, Object>> list = DbUp.upTable("fh_popularity").dataSqlList(sql, new MDataMap("sellerCode",sellerCode));
		for(Map<String, Object> map : list){
			popularityNum.put((String)map.get("product_code"),Double.parseDouble((map.get("popularity_num")==null || map.get("popularity_num").equals(""))?"0":map.get("popularity_num").toString()));
		}
		return popularityNum;
	}
	
	/**
	 * 取当前sellerCode下所有上架商品啊的属性值
	 * @param sellerCode 对应系统编号（惠家有 SI2003  沙皮狗 SI3003  家有汇 SI2009）
	 * @return
	 */
	public  Map<String,String> getPropertyValue(String sellerCode){
		
		String sql = "select  prop.product_code,group_concat(prop.property_value separator ',') as property_value from pc_productproperty prop join "+
					 " (select product_code from pc_productinfo where seller_code=:sellerCode and product_status='4497153900060002')  proi "+
				     " on prop.product_code = proi.product_code "+
				     " where prop.property_type='449736200004'  GROUP BY prop.product_code";
		List<Map<String, Object>> list = DbUp.upTable("pc_productproperty").dataSqlList(sql, new MDataMap("sellerCode",sellerCode));
		Map<String,String> propertyValue =new HashMap<String,String>();
		
		for(Map<String, Object> map : list){
			propertyValue.put((String)map.get("product_code"), map.get("property_value")==null?"":map.get("property_value").toString());
		}
		return propertyValue;
	}
	
	/***
	 * 获取当前sellerCode所对应的商品分类信息（一级ID 一级 Name 二级 ID 二级Name）
	 * @param sellCode 对应系统编号（惠家有 SI2003  沙皮狗 SI3003  家有汇 SI2009）
	 * @return
	 */
	public  Map<String,SolrDataFacet> getFacet(String sellerCode){
		String sql ="select group_concat(ucs.category_code separator ',') as category_code,ucs.product_code from "+
					" usercenter.uc_sellercategory_product_relation  ucs join "+
					" (select product_code from productcenter.pc_productinfo where seller_code=:sellerCode and product_status='4497153900060002') pcp "+ 
					" on  ucs.product_code = pcp.product_code "+
					" where  ucs.seller_code=:sellerCode group by ucs.product_code";
		List<Map<String, Object>> list = DbUp.upTable("uc_sellercategory_product_relation").dataSqlList(sql, new MDataMap("sellerCode",sellerCode));
		
		String sqlCategory = "select category_code,category_name,parent_code from usercenter.uc_sellercategory where seller_code=:sellerCode";
		List<Map<String, Object>> listCategory = DbUp.upTable("uc_sellercategory_product_relation").dataSqlList(sqlCategory, new MDataMap("sellerCode",sellerCode));
		
		/**
		 * 先把结构树封装到map
		 */
		Map<String,String> categoryValue =new HashMap<String,String>();
		Map<String,String> categoryFatherValue =new HashMap<String,String>();
		for(Map<String, Object> map : listCategory){
			categoryValue.put((String)map.get("category_code"), map.get("category_name")==null?"":map.get("category_name").toString());
			categoryFatherValue.put((String)map.get("category_code"), map.get("parent_code")==null?"":map.get("parent_code").toString());
		}
		
		/**
		 * 封装每个商品对应的结构关系
		 */
		Map<String,SolrDataFacet> facet =new HashMap<String,SolrDataFacet>();
		for(Map<String, Object> map : list){
			
			SolrDataFacet dataFacet = new SolrDataFacet();
			List<String> twoId =  new ArrayList<String>();
			List<String> twoName = new ArrayList<String>();
			List<String> oneId = new ArrayList<String>();
			List<String> oneName = new ArrayList<String>();
			
			if(map.get("category_code")!=null || !map.get("category_code").equals("")){
				
				String[] splitValue = map.get("category_code").toString().split(",");
				if(null == splitValue || "".equals(splitValue) || splitValue.length<=0){
				}else{
					
					for (int i = 0; i < splitValue.length; i++) {
						
						/****为了兼容商品挂在二级分类和三级分类   如果查询商品挂在12位的编号下，证明该商品没有三级分类****/
						if(splitValue[i].trim().length()==12){
							oneId.add(splitValue[i]);
							oneName.add(categoryValue.get(splitValue[i]));
						}
						
						/****为了兼容商品挂在二级分类和三级分类    如果查询商品挂在16位的编号下，证明该商品挂在三级分类 ****/
						if(splitValue[i].trim().length()==16){
							twoId.add(splitValue[i]);
							twoName.add(categoryValue.get(splitValue[i]));
							oneId.add(categoryFatherValue.get(splitValue[i]));
							oneName.add(categoryValue.get(categoryFatherValue.get(splitValue[i])));
						}
						dataFacet.setOneId(oneId);
						dataFacet.setOneName(oneName);
						dataFacet.setTwoId(twoId);
						dataFacet.setTwoName(twoName);
					}
					
				}
				
			}
			
			facet.put((String)map.get("product_code"), dataFacet);
		}
		
		return facet;
		
	}
	
	/**
	 * 查询当前sellerCode下的上架商品
	 * @param sellerCode 对应系统编号（惠家有 SI2003  沙皮狗 SI3003  家有汇 SI2009）
	 * @return
	 */
	public  List<Map<String, Object>> getProduct(String sellerCode){
			
			String query = "select p.product_code,p.product_name,p.mainpic_url,p.market_price,p.min_sell_price,p.update_time,p.brand_code,p.labels,p.small_seller_code,t.description_info,t.keyword,"+
					"(select group_concat(s.sku_code separator ',') from pc_skuinfo s where s.product_code=p.product_code group by s.product_code) as sku_code_string,"+
					"(select f.brand_name from pc_brandinfo f where p.brand_code=f.brand_code) as brand_name "+
					"  from pc_productinfo p  left join pc_productdescription t  on t.product_code = p.product_code    where p.seller_code=:seller_code and p.product_status='4497153900060002'";
			return DbUp.upTable("pc_productinfo").dataSqlList(query, new MDataMap("seller_code",sellerCode));
	}
	
	/***
	 * 每天晚上凌晨会往数据库重新写数据
	 * 该方法暂时只支持  惠家有和沙皮狗
	 * sellcode为null 代表是全部人气值计算  
	 * 人气公式 购物车*60% + 收藏%*40%
	 */
	public void addPopularitySum(String sellercode){
		String[] sellerCode = null;
		if(sellercode!=null){
			sellerCode = new String[1];
			sellerCode[0]=sellercode;
		}else{
			sellerCode = new String[2];
			sellerCode[0]="SI2003";
			sellerCode[1]="SI3003";
		}
		String cartNum=null;
		String OperateTypeNum=null;
		
		for(int i = 0;i<sellerCode.length;i++){
			/***查询当前系统所对应的收藏数***/
			String queryOperateTypeNum="select count(*) as operate_type,fhp.product_code from familyhas.fh_product_collection  fhp "+
						" join  (select member_code from membercenter.mc_login_info where manage_code='"+sellerCode[i]+"' and flag_enable=1) mem "+
						" on fhp.member_code=mem.member_code "+
						" where fhp.operate_type='4497472000020001' group by fhp.product_code";
			List<Map<String, Object>> listOperateTypeNum =  DbUp.upTable("pc_skuinfo").dataSqlList(queryOperateTypeNum, new MDataMap());
			
			
			 /**查询购物车下商品的数量**/
			String queryCartNum = "select a.product_code product_code,sum(b.sumSkuNum) skunum"+
					       " from (select s.sku_code ,s.product_code from productcenter.pc_skuinfo s inner join productcenter.pc_productinfo p on s.product_code= p.product_code where p.seller_code='"+sellerCode[i]+"' and p.product_status='4497153900060002') a left join "+  
			               " (select SUM(sku_num) sumSkuNum,sku_code from ordercenter.oc_shopCart  GROUP BY sku_code) b "+
		                   " on a.sku_code = b.sku_code where b.sku_code is not null group by product_code" ;
			List<Map<String, Object>> listCartNum = DbUp.upTable("pc_skuinfo").dataSqlList(queryCartNum, new MDataMap());
			
			
			/**
			 * 查询全部的product_code
			 */
			String querySku="select product_code from  productcenter.pc_productinfo  where seller_code='"+sellerCode[i]+"' and product_status='4497153900060002'";
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
			
			for (Iterator k = mapPopularityNum.keySet().iterator(); k.hasNext();) {
				if(mapPopularityNum.get(k.next()).toString().trim().equals("0.0")){
					continue;
				}
				
				String sSql =  "select * from fh_popularity where product_code=:productCode";
				if(DbUp.upTable("fh_popularity").dataSqlOne(sSql, new MDataMap("productCode",(String)k.next()))== null || DbUp.upTable("fh_popularity").dataSqlOne(sSql, new MDataMap("productCode",(String)k.next())).size()<1){
					MDataMap md = new MDataMap();
					md.put("product_code", (String)k.next());
					md.put("popularity_num", mapPopularityNum.get(k.next())+"");
					md.put("seller_code", sellerCode[i]);
					DbUp.upTable("fh_popularity").dataInsert(md);
				}else{
					MDataMap md = new MDataMap();
					md.put("product_code", (String)k.next());
					md.put("popularity_num", mapPopularityNum.get(k.next())+"");
					DbUp.upTable("fh_popularity").dataUpdate(md, "popularity_num", "product_code");
				}
			   }
		}
		
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
	
	
	/** 人气方法
	 * 人气公式 购物车*60% + 收藏%*40%
	 * @param sellerCode  SI2003为惠家有  SI2009为家有汇
	 * @return 返回数量
	 */
	public  Map<String,Double> popularitySumNum(String sellerCode){
		String cartNum=null;
		String OperateTypeNum=null;
		
		/***查询当前系统所对应的收藏数***/
		String queryOperateTypeNum="select count(*) as operate_type,fhp.product_code from familyhas.fh_product_collection  fhp "+
					" join  (select member_code from membercenter.mc_login_info where manage_code='"+sellerCode+"' and flag_enable=1) mem "+
					" on fhp.member_code=mem.member_code "+
					" where fhp.operate_type='4497472000020001' group by fhp.product_code";
		List<Map<String, Object>> listOperateTypeNum =  DbUp.upTable("pc_skuinfo").dataSqlList(queryOperateTypeNum, new MDataMap());
		
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
	
	
	/**
	 * 索引库添加方法
	 * @param sellerCode 对应系统编号（惠家有 SI2003  沙皮狗 SI3003  家有汇 SI2009）
	 */
	public void addSolrData(String sellerCode){
		PlusSupportProduct psp = new PlusSupportProduct();
		List<Product> listProduct = new ArrayList<Product>();
		List<String> property=null;
		Product pro = null;
		ProductService ps = new ProductService();
		/***获取人气值的方法***/
		Map<String,Double> mapPopularityNum = null;
		if(sellerCode.equals("SI2003") || sellerCode.equals("SI3003")){
			mapPopularityNum = popularitySumNum(sellerCode);
		}
		/***获取属性值方法***/
		Map<String,String> propertyValue = getPropertyValue(sellerCode);
		/***获取商品分类的方法***/
	    Map<String,SolrDataFacet> facetValue= getFacet(sellerCode);
	   /***统计是否有货     3.8.0的库存加版空***/
	    PlusSupportStock pssStock= new PlusSupportStock();
		/*** 获取销量***/
		Map<String,Map<String,String>> productSalesJyh = null;
		/***获取最低的价格值 因为价格只是用来排序显示，现在不会在去实时的去算****/
	   Map<String,BigDecimal> price = null;
		
		if("SI2009".equals(sellerCode)){
			productSalesJyh = ps.getProductSales(sellerCode, "");
		}
		
		
		/***拿到所有商品***/
		List<Map<String, Object>> list = getProduct(sellerCode);
		/**
		 * 开始循环遍历封装List<Product>
		 * 为创建索引提供数据
		 */
		for(Map<String,Object> map : list){
			
			String skuCodeString=(map.get("sku_code_string") == null || map.get("sku_code_string").equals(""))?"":map.get("sku_code_string").toString();
			pro = new Product();
			property = new ArrayList<String>();
			pro.setProductCode((map.get("product_code") == null || map.get("product_code").equals(""))?"":map.get("product_code").toString());
			pro.setStockNum(pssStock.upAllStockForProduct(map.get("product_code").toString())<=0?0:1);
			pro.setRemarkName((map.get("labels") == null || map.get("labels").equals(""))?"":map.get("labels").toString());
			pro.setBrandCode((map.get("brand_code") == null || map.get("brand_code").equals(""))?"":map.get("brand_code").toString());
			pro.setBrandCodeName((map.get("brand_name") == null || map.get("brand_name").equals(""))?"":map.get("brand_name").toString());
			pro.setProductName((map.get("product_name") == null || map.get("product_name").equals(""))?"":map.get("product_name").toString());
			pro.setProductDetails((map.get("description_info") == null || map.get("description_info").equals(""))?"":map.get("description_info").toString());
			pro.setTagList((map.get("keyword") == null || map.get("keyword").equals(""))?"":map.get("keyword").toString());
			pro.setMainpicUrl((map.get("mainpic_url") == null || map.get("mainpic_url").equals(""))?"":map.get("mainpic_url").toString());
			pro.setOriginalPrice((map.get("market_price") == null || map.get("market_price").equals(""))?0.0:Double.parseDouble(map.get("market_price").toString()));
			pro.setUpdateTime((map.get("update_time") == null || map.get("update_time").equals(""))?null:SolrjUtil.getYear(map.get("update_time").toString()));
			property.add(null==propertyValue.get(map.get("product_code").toString().trim()) ? null :propertyValue.get(map.get("product_code").toString().trim()).toString().trim());
			pro.setPropertyValue(property);
			pro.setOneId(facetValue.get(map.get("product_code").toString())==null?null : facetValue.get(map.get("product_code").toString()).getOneId() == null?null:facetValue.get(map.get("product_code").toString()).getOneId());
			pro.setOneName(facetValue.get(map.get("product_code").toString())==null?null : facetValue.get(map.get("product_code").toString()).getOneName()==null?null:facetValue.get(map.get("product_code").toString()).getOneName());
			pro.setTwoId(facetValue.get(map.get("product_code").toString())==null?null :facetValue.get(map.get("product_code").toString()).getTwoId()==null?null:facetValue.get(map.get("product_code").toString()).getTwoId());
			pro.setTwoName(facetValue.get(map.get("product_code").toString())==null?null :facetValue.get(map.get("product_code").toString()).getTwoName()==null?null:facetValue.get(map.get("product_code").toString()).getTwoName());
			pro.setSellerCode(sellerCode);
			
			
			/**
			 * 价格暂时直接去市场价，不在取封装的价格了 
			 * 销量 两个系统的算法不一样 需要分开写  
			 */
			if("SI2009".equals(sellerCode)){
				/**最后价格**/
				pro.setCurrentPrice((map.get("min_sell_price") == null || map.get("min_sell_price").equals(""))?0.0:Double.parseDouble(map.get("min_sell_price").toString()));
				pro.setProductNumber((null==productSalesJyh.get(map.get("product_code").toString().trim()) || productSalesJyh.get(map.get("product_code").toString().trim()).equals(""))?0:Integer.parseInt(productSalesJyh.get(map.get("product_code").toString().trim()).get("thirty_day").toString()));
				pro.setSmallSellerCode(0);
			}else if("SI2003".equals(sellerCode)){
				/**最后价格**/
				String[] skuCode = skuCodeString==null || skuCodeString.equals("") ? new String[0] : skuCodeString.split(",");
				if(skuCode.length>0){
					List<Double> listPrice = new ArrayList<Double>();
					for(int j=0;j<skuCode.length;j++){
						PlusModelSkuInfo skuInfo = psp.upSkuInfoBySkuCode(skuCode[j],null);
						if(skuInfo!=null){
							listPrice.add(skuInfo.getSellPrice().doubleValue());
						}
					}
					Collections.sort(listPrice);  
					pro.setCurrentPrice(listPrice.size()<0?0.0:listPrice.get(0)); //销售价
				}else{
					pro.setCurrentPrice(0.0); //有问题的商品销售价
				}
				
				//pro.setCurrentPrice((null==price.get(map.get("product_code").toString()) || "".equals(price.get(map.get("product_code").toString())))?0.0 : Double.parseDouble(price.get(map.get("product_code").toString()).toString()));
				pro.setPopularityNum(mapPopularityNum.get(map.get("product_code").toString().trim())==null?0.0:mapPopularityNum.get(map.get("product_code").toString()));
				//销量走缓存
				PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(map.get("product_code").toString());		
				PlusModelProductSales productSales = new LoadProductSales().upInfoByCode(plusModelProductQuery);
				if(productSales!=null){
					pro.setProductNumber(productSales.getFictitionSales30());
				}else{
					pro.setProductNumber(0);
				}
				
//			    pro.setSmallSellerCode((map.get("small_seller_code") == null || map.get("small_seller_code").equals(""))?0:map.get("small_seller_code").equals("SF03KJT")||map.get("small_seller_code").equals("SF03MLG")
//			    		||map.get("small_seller_code").equals("SF03100294")
//			    		||map.get("small_seller_code").equals("SF03100327")
//			    		||map.get("small_seller_code").equals("SF03100329")?1:0);
			    pro.setSmallSellerCode((map.get("small_seller_code") == null || map.get("small_seller_code").equals(""))?0:new PlusServiceSeller().isKJSeller(map.get("small_seller_code").toString())?1:0);
			}else if("SI3003".equals(sellerCode)){
				/**最后价格**/
				String[] skuCode = skuCodeString==null || skuCodeString.equals("") ? new String[0] : skuCodeString.split(",");
				if(skuCode.length>0){
					List<Double> listPrice = new ArrayList<Double>();
					for(int j=0;j<skuCode.length;j++){
						PlusModelSkuInfo skuInfo = psp.upSkuInfoBySkuCode(skuCode[j],null);
						if(skuInfo!=null){
							listPrice.add(skuInfo.getSellPrice().doubleValue());
						}
					}
					Collections.sort(listPrice);  
					pro.setCurrentPrice(listPrice.size()<0?0.0:listPrice.get(0)); //销售价
				}else{
					pro.setCurrentPrice(0.0); //有问题的商品销售价
				}
				//pro.setCurrentPrice((null==price.get(map.get("product_code").toString()) || "".equals(price.get(map.get("product_code").toString())))?0.0 : Double.parseDouble(price.get(map.get("product_code").toString()).toString()));
				pro.setPopularityNum(mapPopularityNum.get(map.get("product_code").toString().trim())==null?0.0:mapPopularityNum.get(map.get("product_code").toString()));
				//销量走缓存
				PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(map.get("product_code").toString());		
				PlusModelProductSales productSales = new LoadProductSales().upInfoByCode(plusModelProductQuery);
				if(productSales!=null){
					pro.setProductNumber(productSales.getFictitionSales30());
				}else{
					pro.setProductNumber(0);
				}
//			    pro.setSmallSellerCode((map.get("small_seller_code") == null || map.get("small_seller_code").equals(""))?0:map.get("small_seller_code").equals("SF03KJT")||map.get("small_seller_code").equals("SF03MLG")
//			    		||map.get("small_seller_code").equals("SF03100294")
//			    		||map.get("small_seller_code").equals("SF03100327")
//			    		||map.get("small_seller_code").equals("SF03100329")?1:0);
				pro.setSmallSellerCode((map.get("small_seller_code") == null || map.get("small_seller_code").equals(""))?0:new PlusServiceSeller().isKJSeller(map.get("small_seller_code").toString())?1:0);
			}
			
			listProduct.add(pro);
		}
		SolrQueryUtil.delete(sellerCode);
		SolrQueryUtil.addList(listProduct, sellerCode);
		
		
	}
	
	
}
