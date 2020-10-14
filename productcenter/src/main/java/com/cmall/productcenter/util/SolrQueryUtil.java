package com.cmall.productcenter.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.cmall.productcenter.model.Product;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopUp;

/**
 * 2015-06-06优化搜索
 * @author zhouguohui
 *
 */
public class SolrQueryUtil extends BaseClass{

	public static final String ZKHOST=TopUp.upConfig("productcenter.zkHost");
    public static final String SIJYH=TopUp.upConfig("productcenter.si2009_solr");
    public static final String SIHJY=TopUp.upConfig("productcenter.si2003_solr");
    public static final String SISPG=TopUp.upConfig("productcenter.si3003_solr");
    public static final String SIJYHASSOCIATE=TopUp.upConfig("productcenter.Associate2009");
    public static final String CLUSTER=TopUp.upConfig("productcenter.cluster").trim();
	public static final String JYH = "SI2009";
	public static final String HJY = "SI2003";
	public static final String SPG = "SI3003";
	public static final String ASSOCIATE = "Associate2009";
	public static final int ZKCLIENTTIMEOUT = 20000;
	public static final int ZKCONNECTTIMEOUT = 20000;
	private static  CloudSolrServer coludJyh ;
	private static  CloudSolrServer coludHjy ;
	private static  CloudSolrServer coludSpg ;
	private static  CloudSolrServer coludAssociate;
	private static  HttpSolrServer httpSpg ;
	private static  HttpSolrServer httpJyh ;
	private static  HttpSolrServer httpHjy ;
	private static  HttpSolrServer httpAssociate;
	private static SolrServer solrServer;
	
	
	/**
	 * 单利模式对外输出方法
	 * @param 
	 * @param collection 判断那个分片单利
	 * @return 返回父对象
	 */
	public static SolrServer getSolrServer(final String collection){
		
		if(CLUSTER.equals("yes")){
			if(collection.equals(JYH)){
				solrServer = getColudJyh();
			}else if(collection.equals(ASSOCIATE)){
				solrServer=getColudAssociate();
			}else if(collection.equals(HJY)){
				solrServer=getColudHjy();
			}else if(collection.equals(SPG)){
				solrServer=getColudSpg();
			}
		}else{
			if(collection.equals(JYH)){
				solrServer = getHttpJyh();
			}else if(collection.equals(ASSOCIATE)){
				solrServer=getHttpAssociate();
			}else if(collection.equals(HJY)){
				solrServer=getHttpHjy();
			}else if(collection.equals(SPG)){
				solrServer=getHttpSpg();
			}
		}
		return solrServer;
	}
	
	/**
	 * 家有汇搜索集群单利
	 * @return
	 */
	private static CloudSolrServer getColudJyh(){
		if(coludJyh==null){
            synchronized(CloudSolrServer.class) {
            	if(coludJyh==null){
            		try {   
            			coludJyh = new CloudSolrServer(ZKHOST);
            			coludJyh.setDefaultCollection(JYH);
            			coludJyh.setZkClientTimeout(ZKCLIENTTIMEOUT);
            			coludJyh.setZkConnectTimeout(ZKCONNECTTIMEOUT);
            			coludJyh.connect(); 
            		}catch(Exception e) {  
            			coludJyh.shutdown();
            			coludJyh=null;
            			e.printStackTrace();                    
            		} 
            	}
            }
		 }
		return coludJyh;
	}
	
	/**
	 * 惠家有搜索集群单利
	 * @return
	 */
	private static CloudSolrServer getColudHjy(){
		if(coludHjy==null){
            synchronized(CloudSolrServer.class) {
            	if(coludHjy==null){
            		try {   
            			coludHjy = new CloudSolrServer(ZKHOST);
            			coludHjy.setDefaultCollection(HJY);
            			coludHjy.setZkClientTimeout(ZKCLIENTTIMEOUT);
            			coludHjy.setZkConnectTimeout(ZKCONNECTTIMEOUT);
            			coludHjy.connect(); 
            		}catch(Exception e) {  
            			coludHjy.shutdown();
            			coludHjy=null;
            			e.printStackTrace();                    
            		} 
            	}
            }
		 }
		return coludHjy;
	}
	/**
	 * 沙皮狗搜索集群单利
	 * @return
	 */
	private static CloudSolrServer getColudSpg(){
		if(coludSpg==null){
            synchronized(CloudSolrServer.class) {
            	if(coludSpg==null){
            		try {   
            			coludSpg = new CloudSolrServer(ZKHOST);
            			coludSpg.setDefaultCollection(SPG);
            			coludSpg.setZkClientTimeout(ZKCLIENTTIMEOUT);
            			coludSpg.setZkConnectTimeout(ZKCONNECTTIMEOUT);
            			coludSpg.connect(); 
            		}catch(Exception e) {  
            			coludSpg.shutdown();
            			coludSpg=null;
            			e.printStackTrace();                    
            		} 
            	}
            }
		 }
		return coludSpg;
	}
	
	/**
	 * 家有汇联想集群单利
	 * @return
	 */
	private static CloudSolrServer getColudAssociate(){
		if(coludAssociate==null){
            synchronized(CloudSolrServer.class) {
            	if(coludAssociate==null){
            		try {   
            			coludAssociate = new CloudSolrServer(ZKHOST);
            			coludAssociate.setDefaultCollection(ASSOCIATE);
            			coludAssociate.setZkClientTimeout(ZKCLIENTTIMEOUT);
            			coludAssociate.setZkConnectTimeout(ZKCONNECTTIMEOUT);
            			coludAssociate.connect(); 
            		}catch(Exception e) {  
            			coludAssociate.shutdown();
            			coludAssociate=null;
            			e.printStackTrace();                    
            		} 
            	}
            }
		 }
		return coludAssociate;
	}
	
	  /**
		 * 家有汇 搜索单利 单机
		 * @param zkHost
		 * @return
		 */
		 private static HttpSolrServer getHttpJyh() {
			 if(httpJyh==null){
	            synchronized(HttpSolrServer.class) {
	            	if(httpJyh==null){
	            		try {   
	            			httpJyh = new HttpSolrServer(SIJYH);
	            			httpJyh.setAllowCompression(true);
	            			httpJyh.setSoTimeout(60000); 
	            			httpJyh.setConnectionTimeout(100); 
	            			httpJyh.setDefaultMaxConnectionsPerHost(100); 
	            			httpJyh.setMaxTotalConnections(100); 
	            			httpJyh.setFollowRedirects(false); 
	            			httpJyh.setAllowCompression(true); 
	            			httpJyh.setMaxRetries(1); // defaults to 0.  > 1 not recommended. 
	            		}catch(Exception e) {  
	            			httpJyh.shutdown();
	            			httpJyh=null;
	            			e.printStackTrace();                    
	            		} 
	            	}
	            }
			 }
	        return httpJyh;    
		 } 
		 
	
		 /**
		 * 家有汇 联想词单利 单击
		 * @param zkHost
		 * @return
		 */
		 private static HttpSolrServer getHttpAssociate() {
			 if(httpAssociate==null){
	            synchronized(HttpSolrServer.class) {
	            	if(httpAssociate==null){
	            		try {   
	            			httpAssociate = new HttpSolrServer(SIJYHASSOCIATE);
	            			httpAssociate.setAllowCompression(true);
	            			httpAssociate.setSoTimeout(60000); 
	            			httpAssociate.setConnectionTimeout(100); 
	            			httpAssociate.setDefaultMaxConnectionsPerHost(100); 
	            			httpAssociate.setMaxTotalConnections(100); 
	            			httpAssociate.setFollowRedirects(false); 
	            			httpAssociate.setAllowCompression(true); 
	            			httpAssociate.setMaxRetries(1); // defaults to 0.  > 1 not recommended. 
	            		}catch(Exception e) {  
	            			httpAssociate.shutdown();
	            			httpAssociate=null;
	            			e.printStackTrace();                    
	            		} 
	            	}
	            }
			 }
	        return httpAssociate;    
		 } 
		 
		 /**
		 * 惠家有 联想词单利 单击
		 * @param zkHost
		 * @return
		 */
		 private static HttpSolrServer getHttpHjy() {
			 if(httpHjy==null){
	            synchronized(HttpSolrServer.class) {
	            	if(httpHjy==null){
	            		try {   
	            			httpHjy = new HttpSolrServer(SIHJY);
	            			httpHjy.setAllowCompression(true);
	            			httpHjy.setSoTimeout(60000); 
	            			httpHjy.setConnectionTimeout(100); 
	            			httpHjy.setDefaultMaxConnectionsPerHost(100); 
	            			httpHjy.setMaxTotalConnections(100); 
	            			httpHjy.setFollowRedirects(false); 
	            			httpHjy.setAllowCompression(true); 
	            			httpHjy.setMaxRetries(1); // defaults to 0.  > 1 not recommended. 
	            		}catch(Exception e) {  
	            			httpHjy.shutdown();
	            			httpHjy=null;
	            			e.printStackTrace();                    
	            		} 
	            	}
	            }
			 }
	        return httpHjy;    
		 } 

		/**
		 * 沙皮狗 联想词单利 单击
		 * @param zkHost
		 * @return
		 */
		 private static HttpSolrServer getHttpSpg() {
			 if(httpSpg==null){
	            synchronized(HttpSolrServer.class) {
	            	if(httpSpg==null){
	            		try {   
	            			httpSpg = new HttpSolrServer(SISPG);
	            			httpSpg.setAllowCompression(true);
	            			httpSpg.setSoTimeout(60000); 
	            			httpSpg.setConnectionTimeout(100); 
	            			httpSpg.setDefaultMaxConnectionsPerHost(100); 
	            			httpSpg.setMaxTotalConnections(100); 
	            			httpSpg.setFollowRedirects(false); 
	            			httpSpg.setAllowCompression(true); 
	            			httpSpg.setMaxRetries(1); // defaults to 0.  > 1 not recommended. 
	            		}catch(Exception e) {  
	            			httpSpg.shutdown();
	            			httpSpg=null;
	            			e.printStackTrace();                    
	            		} 
	            	}
	            }
			 }
	        return httpSpg;    
		 }
		/**
	     * 定义往索引库要添加的字段
	     * @param pro 字段对象实体类
	     * @return
	     */
	    public static SolrInputDocument parese(Product pro) {
	        SolrInputDocument doc = new SolrInputDocument();
	        doc.addField("productCode", pro.getProductCode());
	        doc.addField("productName", pro.getProductName());
	        doc.addField("remarkName", pro.getRemarkName());
	        doc.addField("productDetails", pro.getProductDetails());
	        doc.addField("mainpicUrl", pro.getMainpicUrl());
	        doc.addField("originalPrice", pro.getOriginalPrice());
	        doc.addField("currentPrice", pro.getCurrentPrice());
	        doc.addField("updateTime", pro.getUpdateTime());
	        doc.addField("oneId", pro.getOneId());
	        doc.addField("oneName", pro.getOneName());
	        doc.addField("twoId", pro.getTwoId());
	        doc.addField("twoName", pro.getTwoName());
	        doc.addField("brandCode", pro.getBrandCode());
	        doc.addField("brandCodeName", pro.getBrandCodeName());
	        doc.addField("productNumber", pro.getProductNumber());
	        doc.addField("sellerCode", pro.getSellerCode());
	        doc.addField("tagList", pro.getTagList());
	        doc.addField("propertyValue", pro.getPropertyValue());
	        doc.addField("stockNum",pro.getStockNum());
	        doc.addField("popularityNum",pro.getPopularityNum());
	        doc.addField("smallSellerCode",pro.getSmallSellerCode());
	        return doc;
	    }
		
	  
	    
	    /**
	     * 添加单个商品到索引库
	     * @param Product
	     */
	    public static void addOne(Product pro,String sellerCode){
	        SolrInputDocument doc = parese(pro);
				try {
					if("SI2003".equals(sellerCode)){
						if(CLUSTER.equals("yes")){
						    coludHjy =  (CloudSolrServer)getSolrServer(HJY);
						    coludHjy.add(doc);
						    coludHjy.commit();
						}else{
							httpHjy = (HttpSolrServer) getSolrServer(HJY);
							httpHjy.add(doc);
							httpHjy.commit();
						}
					}else if("SI2009".equals(sellerCode)){
						if(CLUSTER.equals("yes")){
						    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
						    coludJyh.add(doc);
						    coludJyh.commit();
						}else{
							httpJyh = (HttpSolrServer) getSolrServer(JYH);
							httpJyh.add(doc);
							httpJyh.commit();
						}
					}else if("SI3003".equals(sellerCode)){
						if(CLUSTER.equals("yes")){
						    coludSpg =  (CloudSolrServer)getSolrServer(SPG);
						    coludSpg.add(doc);
						    coludSpg.commit();
						}else{
							httpSpg = (HttpSolrServer) getSolrServer(SPG);
							httpSpg.add(doc);
							httpSpg.commit();
						}
					}
					
				}catch (IOException e) {
					e.printStackTrace();
				} catch (SolrServerException e) {
					closeSolr(sellerCode);
					e.printStackTrace();
				} 
	    }

	    /**
	     * 添加多个商品到索引库
	     * @param Product
	     */
	    public static void addList(List<Product> pro,String sellerCode){
	        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	        for (Product pinfo : pro) {
	            SolrInputDocument doc = parese(pinfo);
	            docs.add(doc);
	        }
	        try {
	        	if("SI2003".equals(sellerCode)){
	        		if(CLUSTER.equals("yes")){
					    coludHjy =  (CloudSolrServer)getSolrServer(HJY);
					    coludHjy.add(docs);
					    coludHjy.optimize();
					    coludHjy.commit();
					}else{
						httpHjy = (HttpSolrServer) getSolrServer(HJY);
						httpHjy.add(docs);
						httpHjy.optimize();
						httpHjy.commit();
					}
				}else if("SI2009".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
					    coludJyh.add(docs);
					    coludJyh.optimize();
					    coludJyh.commit();
					}else{
						httpJyh = (HttpSolrServer) getSolrServer(JYH);
						httpJyh.add(docs);
						httpJyh.optimize();
						httpJyh.commit();
					}
				}else if("SI3003".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludSpg =  (CloudSolrServer)getSolrServer(SPG);
					    coludSpg.add(docs);
					    coludSpg.optimize();
					    coludSpg.commit();
					}else{
						httpSpg = (HttpSolrServer) getSolrServer(SPG);
						httpSpg.add(docs);
						httpSpg.optimize();
						httpSpg.commit();
					}
					
				}
	        	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				closeSolr(sellerCode);
				e.printStackTrace();
			} 
	    }


	    /**
	     * 删除全部商品索引,尽量不要使用
	     */
	    public static void delete(String sellerCode){
	        try {
	        	if("SI2003".equals(sellerCode)){
	        		if(CLUSTER.equals("yes")){
					    coludHjy =  (CloudSolrServer)getSolrServer(HJY);
					    coludHjy.deleteByQuery("*:*");
					    coludHjy.commit();
					}else{
						httpHjy = (HttpSolrServer) getSolrServer(HJY);
						httpHjy.deleteByQuery("*:*");
						httpHjy.commit();
					}
				}else if("SI2009".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
					    coludJyh.deleteByQuery("*:*");
					    coludJyh.commit();
					}else{
						httpJyh = (HttpSolrServer) getSolrServer(JYH);
						httpJyh.deleteByQuery("*:*");
						httpJyh.commit();
					}
				}else if("SI3003".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludSpg =  (CloudSolrServer)getSolrServer(SPG);
					    coludSpg.deleteByQuery("*:*");
					    coludSpg.commit();
					}else{
						httpSpg = (HttpSolrServer) getSolrServer(SPG);
						httpSpg.deleteByQuery("*:*");
						httpSpg.commit();
					}
				}
				   
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				closeSolr(sellerCode);
				e.printStackTrace();
			} 
	    }
	    
	    /**
	     * 根据商品的zid删除单个商品索引
	     */
	    public static void deleteById(String productCode,String sellerCode){
	        try {
	        	if("SI2003".equals(sellerCode)){
	        		if(CLUSTER.equals("yes")){
					    coludHjy =  (CloudSolrServer)getSolrServer(HJY);
					    coludHjy.deleteById(productCode);
					    coludHjy.commit();
					}else{
						httpHjy = (HttpSolrServer) getSolrServer(HJY);
						httpHjy.deleteById(productCode);
						httpHjy.commit();
					}
				}else if("SI2009".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
					    coludJyh.deleteById(productCode);
					    coludJyh.commit();
					}else{
						httpJyh = (HttpSolrServer) getSolrServer(JYH);
						httpJyh.deleteById(productCode);
						httpJyh.commit();
					}
				}else if("SI3003".equals(sellerCode)){
					if(CLUSTER.equals("yes")){
					    coludSpg =  (CloudSolrServer)getSolrServer(SPG);
					    coludSpg.deleteById(productCode);
					    coludSpg.commit();
					}else{
						httpSpg = (HttpSolrServer) getSolrServer(SPG);
						httpSpg.deleteById(productCode);
						httpSpg.commit();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				closeSolr(sellerCode);
				e.printStackTrace();
			} 
	    }
	    
	    /**
	     * 异常关闭方法
	     * @param sellerCode
	     */
	    private static void closeSolr(String sellerCode){
	    	if("SI2003".equals(sellerCode)){
        		if(CLUSTER.equals("yes")){
				    coludHjy.shutdown();
				    coludHjy=null;
				}else{
					httpHjy.shutdown();
					httpHjy=null;
				}
			}else if("SI2009".equals(sellerCode)){
				if(CLUSTER.equals("yes")){
				    coludJyh.shutdown();
				    coludJyh=null;
				}else{
					httpJyh.shutdown();
					httpJyh=null;
				}
			}else if("SI3003".equals(sellerCode)){
				if(CLUSTER.equals("yes")){
				    coludSpg.shutdown();
				    coludSpg=null;
				}else{
					httpSpg.shutdown();
					httpSpg=null;
				}
			}
	    }
	    
		 
		 /**
	     * 家有汇搜索接口 
	     * 单台 和集群 服务器solr的搜索方法
	     * @param mRequestDate 前端传入搜索参数
	     * @return 返回一个map集合
	     */
	    public static Map<String,Object>  getSearchJyh(MDataMap mRequestDate){
	    	Map<String,Object> mapList = new HashMap<String, Object>();
	    	QueryResponse responseProdut = null;
	    	QueryResponse responseLongCount = null;
	    	long count = 0;
	    	SolrDocumentList docs = null;
	    	String key =(null==mRequestDate.get("search") || "".equals(mRequestDate.get("search")))?"":mRequestDate.get("search").trim();
	    	//如果为空字符串直接返回
	    	if( !SolrjUtil.IsSpecial(key.trim()) || StringUtils.isEmpty(key.trim())){
	    		return mapList;
	    	}
	    	
			try {
				//拼接SolrQuery返回值
				Map<String,SolrQuery> map = SolrjUtil.queryValue(mRequestDate);
				if(map==null && map.isEmpty() ){
					return mapList;
				}
				
				if(CLUSTER.equals("yes")){
				    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
				    responseProdut = coludJyh.query(map.get("queryValueProduct"));
				    responseLongCount=coludJyh.query(map.get("queryLongCount"));
				}else{
					httpJyh = (HttpSolrServer) getSolrServer(JYH);
					 responseProdut = httpJyh.query(map.get("queryValueProduct"));
					 responseLongCount=httpJyh.query(map.get("queryLongCount"));
				}
				docs = responseLongCount.getResults();
			    count = docs.getNumFound();
        		Map<String,Object> mapProduct =SolrjUtil.getProduct(responseProdut.getBeans(Product.class),mRequestDate.get("search"));
        		Map<String,Object> mapFacet =SolrjUtil.getFacet(responseProdut.getFacetFields(),null);
        		mapList.put("key", (null==mRequestDate.get("search") || "".equals(mRequestDate.get("search")))?"":mRequestDate.get("search"));
        		mapList.put("searchPage", Integer.parseInt((null==mRequestDate.get("page") || "".equals(mRequestDate.get("page")))?"1":mRequestDate.get("page")));
        		mapList.put("searchSize", Integer.parseInt((null==mRequestDate.get("size") || "".equals(mRequestDate.get("size")))?"20":mRequestDate.get("size")));
        		mapList.put("productInfo", null==mapProduct?"":mapProduct.get("pro"));
        		mapList.put("facet", null==mapFacet?"":mapFacet.get("facetList"));
        		mapList.put("productCode", null==mapProduct?"":mapProduct.get("productCode"));
        		mapList.put("count", count);
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludJyh.shutdown();
					coludJyh=null;
				}else{
					httpJyh.shutdown();
					httpJyh=null;
				}
				e.printStackTrace();
			}
	    	return mapList;
	    }
	 
	    
	    /**
	     * 家有汇
	     * 按商品所在分类查询商品  
	     * 单台或集群服务器下的查询方法
	     * @param categroyCode 商品对应的分类编号
	     * @return 返回一个map数据集合
	     */
	    public static  Map<String,Object> getSearchFacet(String categroyCode){
	    	Map<String,Object> mapList = new HashMap<String, Object>();
	    	QueryResponse responseProdut = null;
	    	QueryResponse responseLongCount = null;
	    	long count = 0;
	    	SolrDocumentList docs = null;
	    	SolrQuery queryProduct = new SolrQuery();
	    	SolrQuery queryLongCount = new SolrQuery();
	    	/***家有尊享参数拼接***/
    		int pageSize = 20;
    		String[] code = categroyCode.split("-");
    		if(code.length == 2)
    			pageSize = 16;
    		categroyCode = code[0];
    		/*****************/
	    
	    	if(!SolrjUtil.IsSpecial(categroyCode.trim()) || StringUtils.isEmpty(categroyCode.trim()) || !SolrjUtil.IsNumeric(categroyCode.trim())){
	    		return mapList;
	    	}
	    	try {
	    		queryLongCount.setQuery("oneId:"+categroyCode.trim()+" OR twoId:"+categroyCode.trim());
	    		queryProduct.setQuery("oneId:"+categroyCode.trim()+" OR twoId:"+categroyCode.trim());
	    		queryProduct.set("defType","edismax");
	    		queryProduct.set("bf", "sum(abs(stockNum),sqrt(log(ms(updateTime))))^300.0");
	    		queryProduct.setStart(0); // query的开始行数(分页使用)
	    		queryProduct.setRows(pageSize); // query的返回行数(分页使用)
	    		queryProduct.setFacet(true); // 设置使用facet
	    		queryProduct.setFacetMinCount(1); // 设置facet最少的统计数量
	    		queryProduct.setFacetLimit(pageSize); // facet结果的返回行数
	    		queryProduct.addFacetField("twoId"); // facet的字段
	    		//queryProduct.setFacetSort(FacetParams.FACET_SORT_INDEX);
	    		
				if(CLUSTER.equals("yes")){
				    coludJyh =  (CloudSolrServer)getSolrServer(JYH);
				    responseProdut=coludJyh.query(queryProduct);
				    responseLongCount=coludJyh.query(queryLongCount);
				}else{
					httpJyh = (HttpSolrServer) getSolrServer(JYH);
					responseProdut=httpJyh.query(queryProduct);
				    responseLongCount=httpJyh.query(queryLongCount);
				}
				docs = responseLongCount.getResults();
				count=docs.getNumFound();
		        Map<String,Object> mapProduct =SolrjUtil.getProduct(responseProdut.getBeans(Product.class),null);
		        Map<String,Object> mapFacet =SolrjUtil.getFacet(responseProdut.getFacetFields(),categroyCode);
		        Map<String, Object> keyMap = SolrjUtil.serach(categroyCode,null);
		        if(null!=keyMap){
		        	mapList.put("key", null == keyMap.get("category_name")?"":keyMap.get("category_name").toString());
		        }else{
		        	mapList.put("key", categroyCode);
		        }
		        mapList.put("productInfo", null==mapProduct?"":mapProduct.get("pro"));
		        mapList.put("facet", null==mapFacet?"":mapFacet.get("facetList"));
		        mapList.put("productCode", null==mapProduct?"":mapProduct.get("productCode"));
		        mapList.put("count",count);
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludJyh.shutdown();
					coludJyh=null;
				}else{
					httpJyh.shutdown();
					httpJyh=null;
				}
				e.printStackTrace();
			}
	        return mapList;
	    }
	    
	    /**
	     * 联想词  家有汇
	     * @param selectValue 联想词
	     * @param num         返回数量默认为10条
	     * @return
	     */
	    public static List<String> getSearchSuggest(String selectValue,int num){
	    	 List<String> keyValue = new ArrayList<String>();
	    	 QueryResponse responseSuggest = null;
	    	 SolrQuery params = new SolrQuery();
			 params.set("q", "kw:"+selectValue.trim().replace(" ", "")+"* OR pinyin:"+selectValue.trim().replace(" ", "")+" OR py:"+selectValue.trim().replace(" ", ""));	
			 params.set("fl", "kw");
			 params.setStart(1); 
			 params.setRows(num); 
			 params.set("defType","edismax");
			 params.set("bf","abs(weight)^40");
			 params.set("wt","json");
			 try {
				 
					if(CLUSTER.equals("yes")){
						coludAssociate =  (CloudSolrServer)getSolrServer(ASSOCIATE);
					    responseSuggest=coludAssociate.query(params);
					}else{
						httpAssociate = (HttpSolrServer) getSolrServer(ASSOCIATE);
						responseSuggest=httpAssociate.query(params);
					}
				    if(responseSuggest != null ){ 
				    	 SolrDocumentList results = responseSuggest.getResults();
				    	 if(null==results){
				    		 
				    	 }else{
				    		 for (SolrDocument doc : results) {
				    			 keyValue.add(doc.get("kw").toString());
				    		 }
				    	 }
				    }
				} catch (SolrServerException e) {
					if(CLUSTER.equals("yes")){
						coludAssociate.shutdown();
						coludAssociate=null;
					}else{
						httpAssociate.shutdown();
						httpAssociate=null;
					}
					e.printStackTrace();
				}
				
			return keyValue;
	    }
	    
	    /**
	     * 联想词 惠家友集群
	     * @param selectValue 联想词
	     * @param num	      返回数量 默认为10条
	     * @param cluster  是否为集群  以及不在用
	     * @return
	     */
	    public static List<String> getSearchSuggest(String selectValue,int num,String cluster ){
	    	List<String> keyValue = new ArrayList<String>();
	    	QueryResponse responseSuggest = null;
	    	SolrQuery params = new SolrQuery();
			params.set("qt", "/terms"); 
			params.set("terms.prefix", SolrjUtil.escapeQueryChars(selectValue));		
			params.set("terms.fl", "productName");			
	        params.set("terms.mincount", "1");  
	        params.set("terms.maxcount", "100");  
	        params.set("terms.limit", num);  
	        params.set("terms.raw", "true");  
	        params.set("terms.sort", "count");  
			params.set("wt","json");
			try {
				
				if(CLUSTER.equals("yes")){
					coludHjy =  (CloudSolrServer)getSolrServer(HJY);
				    responseSuggest=coludHjy.query(params);
				}else{
					httpHjy = (HttpSolrServer) getSolrServer(HJY);
					responseSuggest=httpHjy.query(params);
				}
			    if(responseSuggest != null ){ 
			    	 TermsResponse termsResponse = responseSuggest.getTermsResponse(); 
			    	 if(termsResponse != null) {  
			                Map<String, List<TermsResponse.Term> > termsMap = termsResponse.getTermMap();  
			                for(Map.Entry<String, List<TermsResponse.Term> > termsEntry : termsMap.entrySet()) {  
			                    List<TermsResponse.Term> termList = termsEntry.getValue();  
			                    for(TermsResponse.Term term : termList) { 
			                    	keyValue.add(term.getTerm().toString());
			                    }  
			                }  
				    }
			    }
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludHjy.shutdown();
					coludHjy=null;
				}else{
					httpHjy.shutdown();
					httpHjy=null;
				}
				e.printStackTrace();
			}
		return keyValue;
	 }
	    
	    
	    
	    /**
	     * 搜索惠家有 3.7.0    3.7.2版本走的搜索   单击代码
	     * @param keyWord  搜索关键字
	     * @param sortType 0、默认；1、销量；2、上架时间；3、价格    默认为：0
	     * @param sortFlag 1、正序；2、倒序   默认为：2
	     * @param pageSize  每页读取记录数 默认为10
	     * @param pageNo    读取页码 默认为1
	     * @param screenWidth   屏幕宽度
	     * @param categoryOrBrand 是否精确查询
	     * @return
	     */
	    public  static Map<String,Object> getSearchHjy(String keyWord,int sortType,int sortFlag,int pageSize,int pageNo,int screenWidth,String sellercode,String categoryOrBrand){
	    	 QueryResponse responseProduct = null;
	    	 QueryResponse responseLongCount = null;
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	long count = 0;
	    	SolrDocumentList docs = null;
	    	List<Product> pro = null;
	    	List<String> productCodeValue = new ArrayList<String>();
	    	List<String> imgUrlValue = new ArrayList<String>();
	    	Product product = null;
	    	StringBuffer sb = new StringBuffer();
	    	StringBuffer keyValue = new StringBuffer();
	    	StringBuffer pc = new StringBuffer();
	    	String[] key = keyWord.trim().split(" ");//SolrjQueryValueUtil.escapeQueryChars(keyWord)keyWord.trim().split(" ");
	    	SolrQuery query = new SolrQuery();
	    	 if(null != categoryOrBrand && !"top50".equals(categoryOrBrand) && "category".equals(categoryOrBrand)){
	    		sb.append("( twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* )");
	    	 }else if(null != categoryOrBrand && !"top50".equals(categoryOrBrand) && "brand".equals(categoryOrBrand)){
	    		 sb.append("( brandCodeName:*"+keyWord.trim().replaceAll(" ", "*")+"* )");
	    		//月消top50的wap页面调用
	    	 }else if("top50".equals(categoryOrBrand)){
	    		 sb.append("*:* ");
	    	}else{
			    	//如果全为特殊字符
			    	if(!SolrjUtil.IsSpecial(keyWord.trim())){
			    		//sb.append("*:*");
			    	//全为字母
			    	}else if(SolrjUtil.IsLetter(SolrjUtil.escapeQueryChars(keyWord).trim())){	
			    		sb.append("(productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePinYin:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePY:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toUpperCase()+"* OR remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");
			    	//如果参数全为数字或字母数字组合不拆分
			       }else if(SolrjUtil.IsNumeric(SolrjUtil.escapeQueryChars(keyWord).trim())  || SolrjUtil.IsString(SolrjUtil.escapeQueryChars(keyWord).trim())){
						sb.append("(productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productCode:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*  OR twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR brandCodeName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*  OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR propertyValue:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");
					//拆分每个字符进行搜索	
					}else{
						if(key!=null && !"".equals(key)){
				    		keyValue.append("(");
				        	for(int i=0;i<key.length;i++){
				        		if("".equals(key[i])){
				        			
				        		}else{
				        			if(i==0){
				            			keyValue.append("productName:*"+key[i].trim().replaceAll(" ", "")+"* ");
				            		}else{
				            			keyValue.append(" AND productName:*"+key[i].trim().replaceAll(" ", "")+"*");
				            		}
				        		}
				        		
				        	}
				        	keyValue.append(")");
				    	}
						
						sb.append("*:*");
					    if(!"".equals(keyWord.trim()) || keyWord.trim()!=null){ //按名称查询
					    	sb.append(" AND ( "+keyValue+" OR kw_ik:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productCode:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR brandCodeName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR propertyValue:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");
							query.addHighlightField("productName");//高亮字段
					    }
					    
					}
	    	}
			try {
				if(SolrjUtil.IsSpecial(keyWord.trim())){
		        
				    sb.append(" AND sellerCode:"+sellercode.trim());
				    query.set("defType","edismax");
				    query.set("qf","productName^70.0 kw_ik^70.0  oneName^50.0 twoName^50.0  brandCodeName^20");	
			        query.set("bf", "sum(abs(stockNum),sqrt(log(ms(updateTime))))^80.0");
				    query.setQuery(sb.toString());
			        query.setStart(pageNo); // query的开始行数(分页使用)
			        query.setRows(pageSize); // query的返回行数(分页使用)
				    if(sortType==1){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("productNumber", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("productNumber", ORDER.desc));
			        	}
				    }
				    
				    
				    if(sortType==2){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("updateTime", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("updateTime", ORDER.desc));
			        	}
				    }
				    
				    if(sortType==3){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("currentPrice", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("currentPrice", ORDER.desc));
			        	}
				    }
				    
				    if(sortType==4){//人气排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("popularityNum", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("popularityNum", ORDER.desc));
			        	}
				    }
				    
				  //host大于1为走集群 否则单击
					if(CLUSTER.equals("yes")){
						coludHjy =  (CloudSolrServer)getSolrServer(HJY);
						responseProduct=coludHjy.query(query);
						query.remove("start");
					    query.remove("rows");
						responseLongCount = coludHjy.query(query);
					}else{
						httpHjy = (HttpSolrServer) getSolrServer(HJY);
						responseProduct=httpHjy.query(query);
						query.remove("start");
					    query.remove("rows");
						responseLongCount = httpHjy.query(query);
					}
				    docs = responseLongCount.getResults();
				    count = docs.getNumFound();
			        List<Product> items_rep = responseProduct.getBeans(Product.class);
			        pro = new ArrayList<Product>();
			        //数据信息封装
			        for (Product pd : items_rep) {
			        	 product = new Product();
			        	 productCodeValue.add(pd.getProductCode());
			        	 pc.append(pd.getProductCode()).append(",");
			        	 product.setProductCode(pd.getProductCode());
			        	 product.setProductName(pd.getProductName());
			        	 product.setTagList(pd.getTagList());
			        	 product.setMainpicUrl(pd.getMainpicUrl());
			        	 imgUrlValue.add(pd.getMainpicUrl());
			        	 product.setOriginalPrice(pd.getOriginalPrice());
			        	 product.setCurrentPrice(pd.getCurrentPrice());
			        	 product.setStockNum(pd.getStockNum());
			        	 product.setProductNumber(pd.getProductNumber());
			        	 product.setSmallSellerCode(pd.getSmallSellerCode());
			             pro.add(product);
			        }
			        if(!items_rep.isEmpty()){
			        	map.put("productCodeValue", productCodeValue);
			        	map.put("product", pro);
				        map.put("productCode", pc.length()<=0?"":pc.toString().trim().substring(0, pc.toString().length()-1));
				        map.put("imgUrl", imgUrlValue);
				        map.put("count",count);
			        }
				} 
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludHjy.shutdown();
					coludHjy=null;
				}else{
					httpHjy.shutdown();
					httpHjy=null;
				}
				e.printStackTrace();
			}
	        return map;
	    }
	    /**
	     * 沙皮狗搜索 
	     * @param keyWord  搜索关键字
	     * @param sortType 0、默认；1、销量；2、上架时间；3、价格    默认为：0
	     * @param sortFlag 1、正序；2、倒序   默认为：2
	     * @param pageSize  每页读取记录数 默认为10
	     * @param pageNo    读取页码 默认为1
	     * @param screenWidth   屏幕宽度
	     * @param categoryOrBrand 是否精确查询
	     * @return
	     */
	    public  static Map<String,Object> getSearchSpg(String keyWord,int sortType,int sortFlag,int pageSize,int pageNo,int screenWidth,String sellercode,String categoryOrBrand){
	    	 QueryResponse responseProduct = null;
	    	 QueryResponse responseLongCount = null;
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	long count = 0;
	    	SolrDocumentList docs = null;
	    	List<Product> pro = null;
	    	List<String> productCodeValue = new ArrayList<String>();
	    	List<String> imgUrlValue = new ArrayList<String>();
	    	Product product = null;
	    	StringBuffer sb = new StringBuffer();
	    	StringBuffer keyValue = new StringBuffer();
	    	StringBuffer pc = new StringBuffer();
	    	String[] key = keyWord.trim().split(" ");//SolrjQueryValueUtil.escapeQueryChars(keyWord)keyWord.trim().split(" ");
	    	SolrQuery query = new SolrQuery();
	    	 if(null != categoryOrBrand && !"top50".equals(categoryOrBrand) && "category".equals(categoryOrBrand)){
	    		sb.append("( twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* )");
	    	 }else if(null != categoryOrBrand && !"top50".equals(categoryOrBrand) && "brand".equals(categoryOrBrand)){
	    		 sb.append("( brandCodeName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* )");
	    		//月消top50的wap页面调用
	    	 }else if("top50".equals(categoryOrBrand)){
	    		 sb.append("*:* ");
	    	}else{
			    	//如果全为特殊字符
			    	if(!SolrjUtil.IsSpecial(keyWord.trim())){
			    		//sb.append("*:*");
			    	//全为字母
			    	}else if(SolrjUtil.IsLetter(SolrjUtil.escapeQueryChars(keyWord).trim())){	
			    		sb.append("(productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePinYin:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePY:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toLowerCase()+"* OR productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "").toUpperCase()+"* OR remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");
			    	//如果参数全为数字或字母数字组合不拆分
			       }else if(SolrjUtil.IsNumeric(SolrjUtil.escapeQueryChars(keyWord).trim())  || SolrjUtil.IsString(SolrjUtil.escapeQueryChars(keyWord).trim())){
						sb.append("(productName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productCode:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productDetails:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR brandCodeName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*  OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR propertyValue:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");	
					//拆分每个字符进行搜索	
					}else{
						if(key!=null && !"".equals(key)){
				    		keyValue.append("(");
				        	for(int i=0;i<key.length;i++){
				        		if("".equals(key[i])){
				        			
				        		}else{
				        			if(i==0){
				            			keyValue.append("productName:*"+key[i].trim().replaceAll(" ", "")+"* ");
				            		}else{
				            			keyValue.append(" AND productName:*"+key[i].trim().replaceAll(" ", "")+"*");
				            		}
				        		}
				        		
				        	}
				        	keyValue.append(")");
				    	}
						
						sb.append("*:*");
					    if(!"".equals(keyWord.trim()) || keyWord.trim()!=null){ //按名称查询
					    	sb.append(" AND ( "+keyValue+" OR kw_ik:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productCode:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR productDetails:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR oneName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR twoName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR brandCodeName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR propertyValue:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"* OR  remarkName:*"+SolrjUtil.escapeQueryChars(keyWord).trim().replaceAll(" ", "")+"*) ");
							query.addHighlightField("productName");//高亮字段
					    }
					    
					}
	    	}
			try {
				if(SolrjUtil.IsSpecial(keyWord.trim())){
		        
				    sb.append(" AND sellerCode:"+sellercode.trim());
				    query.set("defType","edismax");
				    query.set("qf","productName^70.0 kw_ik^70.0  oneName^50.0 twoName^50.0  brandCodeName^20");	
			        query.set("bf", "sum(abs(stockNum),sqrt(log(ms(updateTime))))^80.0");
				    query.setQuery(sb.toString());
			        query.setStart(pageNo); // query的开始行数(分页使用)
			        query.setRows(pageSize); // query的返回行数(分页使用)
				    if(sortType==1){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("productNumber", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("productNumber", ORDER.desc));
			        	}
				    }
				    
				    
				    if(sortType==2){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("updateTime", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("updateTime", ORDER.desc));
			        	}
				    }
				    
				    if(sortType==3){//按销量排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("currentPrice", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("currentPrice", ORDER.desc));
			        	}
				    }
				    
				    if(sortType==4){//人气排序
				    	if(sortFlag==1){
			        		query.addSort(new SortClause("popularityNum", ORDER.asc));
			        	}else{
			        		query.addSort(new SortClause("popularityNum", ORDER.desc));
			        	}
				    }
				    
				  //host大于1为走集群 否则单击
					if(CLUSTER.equals("yes")){
						coludSpg =  (CloudSolrServer)getSolrServer(SPG);
						responseProduct=coludSpg.query(query);
						query.remove("start");
					    query.remove("rows");
						responseLongCount = coludSpg.query(query);
					}else{
						httpSpg = (HttpSolrServer) getSolrServer(SPG);
						responseProduct=httpSpg.query(query);
						query.remove("start");
					    query.remove("rows");
						responseLongCount = httpSpg.query(query);
					}
				    docs = responseLongCount.getResults();
				    count = docs.getNumFound();
			        List<Product> items_rep = responseProduct.getBeans(Product.class);
			        pro = new ArrayList<Product>();
			        //数据信息封装
			        for (Product pd : items_rep) {
			        	 product = new Product();
			        	 productCodeValue.add(pd.getProductCode());
			        	 pc.append(pd.getProductCode()).append(",");
			        	 product.setProductCode(pd.getProductCode());
			        	 product.setProductName(pd.getProductName());
			        	 product.setTagList(pd.getTagList());
			        	 product.setMainpicUrl(pd.getMainpicUrl());
			        	 imgUrlValue.add(pd.getMainpicUrl());
			        	 product.setOriginalPrice(pd.getOriginalPrice());
			        	 product.setCurrentPrice(pd.getCurrentPrice());
			        	 product.setStockNum(pd.getStockNum());
			        	 product.setProductNumber(pd.getProductNumber());
			        	 product.setSmallSellerCode(pd.getSmallSellerCode());
			             pro.add(product);
			        }
			        if(!items_rep.isEmpty()){
			        	map.put("productCodeValue", productCodeValue);
			        	map.put("product", pro);
				        map.put("productCode", pc.length()<=0?"":pc.toString().trim().substring(0, pc.toString().length()-1));
				        map.put("imgUrl", imgUrlValue);
				        map.put("count",count);
			        }
				} 
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludSpg.shutdown();
					coludSpg=null;
				}else{
					httpSpg.shutdown();
					httpSpg=null;
				}
				e.printStackTrace();
			}
	        return map;
	    }
	
	    /**
	     * 联想词 沙皮狗
	     * @param selectValue 联想词
	     * @param num	      返回数量 默认为10条
	     * @param cluster  是否为集群  以及不在用
	     * @return
	     */
	    public static List<String> getSearchSuggestSpg(String selectValue,int num,String cluster ){
	    	List<String> keyValue = new ArrayList<String>();
	    	QueryResponse responseSuggest = null;
	    	SolrQuery params = new SolrQuery();
			params.set("qt", "/terms"); 
			params.set("terms.prefix", SolrjUtil.escapeQueryChars(selectValue));		
			params.set("terms.fl", "productName");			
	        params.set("terms.mincount", "1");  
	        params.set("terms.maxcount", "100");  
	        params.set("terms.limit", num);  
	        params.set("terms.raw", "true");  
	        params.set("terms.sort", "count");  
			params.set("wt","json");
			try {
				
				if(CLUSTER.equals("yes")){
					coludSpg =  (CloudSolrServer)getSolrServer(SPG);
				    responseSuggest=coludSpg.query(params);
				}else{
					httpSpg = (HttpSolrServer) getSolrServer(SPG);
					responseSuggest=httpSpg.query(params);
				}
			    if(responseSuggest != null ){ 
			    	 TermsResponse termsResponse = responseSuggest.getTermsResponse(); 
			    	 if(termsResponse != null) {  
			                Map<String, List<TermsResponse.Term> > termsMap = termsResponse.getTermMap();  
			                for(Map.Entry<String, List<TermsResponse.Term> > termsEntry : termsMap.entrySet()) {  
			                    List<TermsResponse.Term> termList = termsEntry.getValue();  
			                    for(TermsResponse.Term term : termList) { 
			                    	keyValue.add(term.getTerm().toString());
			                    }  
			                }  
				    }
			    }
			} catch (SolrServerException e) {
				if(CLUSTER.equals("yes")){
					coludSpg.shutdown();
					coludSpg=null;
				}else{
					httpSpg.shutdown();
					httpSpg=null;
				}
				e.printStackTrace();
			}
		return keyValue;
	 }
}
