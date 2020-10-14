package com.cmall.productcenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.params.FacetParams;

import com.cmall.productcenter.model.Facet;
import com.cmall.productcenter.model.Product;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * SolrQuery的封装类
 * @author zhouguohui
 *
 */
public class SolrjUtil extends BaseClass{
	
	/**
	 * 封装query查询值value封装
	 * @param mRequestDate   所有参数
	 * @param numLong    1代表分页查询    2代表全部数据的条数
	 * @return
	 */
    public static Map<String,SolrQuery> queryValue(MDataMap mRequestDate){
    	    Map<String,SolrQuery> mapSolrQuery = new HashMap<String, SolrQuery>();
    	    SolrQuery queryLongCount = new SolrQuery(); //返回查询总数量  用于分页
	    	SolrQuery queryValueProduct = new SolrQuery();//返回查询商品信息 用于显示
	    	StringBuffer queryValue = new StringBuffer();//查询返回的字符串
	    	
	    	
    		String key =(null==mRequestDate.get("search") || "".equals(mRequestDate.get("search")))?"":mRequestDate.get("search");
    		int page = Integer.parseInt((null==mRequestDate.get("page") || "".equals(mRequestDate.get("page")))?"1":mRequestDate.get("page"));
        	int size = Integer.parseInt((null==mRequestDate.get("size") || "".equals(mRequestDate.get("size")))?"20":mRequestDate.get("size"));
        	String selectFacetOne =(null==mRequestDate.get("selectFacetOne"))?"":mRequestDate.get("selectFacetOne").toString().trim();
	    	String selectFacetTwo =(null==mRequestDate.get("selectFacetTwo"))?"":mRequestDate.get("selectFacetTwo").toString().trim();
	    	int newArrival =(null==mRequestDate.get("newArrival"))?0:Integer.parseInt(mRequestDate.get("newArrival"));
	    	Double startP =(null==mRequestDate.get("startP")|| "".equals(mRequestDate.get("startP")))?0.0:Double.parseDouble(mRequestDate.get("startP"));
	    	Double endP =(null==mRequestDate.get("endP") || "".equals(mRequestDate.get("endP")))?0.0:Double.parseDouble(mRequestDate.get("endP"));
	    	String categroyCodeKey =(null==mRequestDate.get("categroyCodeKey"))?"":mRequestDate.get("categroyCodeKey").toString().trim();
        	page =(page-1) * size;
        	
        	//全为字母
	    	 if(IsLetter(escapeQueryChars(key).trim())){	
	    		queryValue.append("(productName:*"+escapeQueryChars(key).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePinYin:*"+escapeQueryChars(key).trim().replaceAll(" ", "").toLowerCase()+"* OR productNamePY:*"+escapeQueryChars(key).trim().replaceAll(" ", "").toLowerCase()+"* OR productName:*"+escapeQueryChars(key).trim().replaceAll(" ", "").toUpperCase()+"*)");
        	//如果参数全为数字或字母和数字不拆分	
        	}else if(IsNumeric(key) || IsString(key)){
        		queryValue.append("(productName:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"* OR productCode:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"* OR productDetails:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"* OR twoName:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"* OR brandCodeName:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"*  OR oneName:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"* OR propertyValue:*"+escapeQueryChars(key).trim().replaceAll(" ", "")+"*)");	
    		//拆分每个字符进行搜索	
			}else{
				StringBuffer keyValue = new StringBuffer();
			    String[] keyName = key.trim().split(" ");
		    	if(keyName!=null || !"".equals(keyName)){
		    		keyValue.append("(");
		        	for(int i=0;i<keyName.length;i++){
		        		if("".equals(keyName[i])){
		        			
		        		}else{
		        			if(i==0){
		            			keyValue.append("productName:*"+keyName[i]+"* ");
		            		}else{
		            			keyValue.append(" AND productName:*"+keyName[i]+"*");
		            		}
		        		}
		        	}
		        	keyValue.append(")");
		    	}
				
		    	
				//商品名称搜索
			    if(!"".equals(key.trim()) || key.trim()!=null){ //按名称查询
			    	queryValue.append("( "+keyValue+" OR productDetails:*"+key.trim().replaceAll(" ", "")+"* OR productCode:*"+key.trim().replaceAll(" ", "")+"* OR twoName:*"+key.trim().replaceAll(" ", "")+"* OR brandCodeName:*"+key.trim().replaceAll(" ", "")+"*  OR oneName:*"+key.trim().replaceAll(" ", "")+"* OR propertyValue:*"+key.trim().replaceAll(" ", "")+"*) ");
			    }
			    if(!"".equals(selectFacetOne.trim()) && selectFacetOne.trim()!=null ){//一级分类查询
			    	queryValue.append(" AND oneId:"+selectFacetOne);
			    }
			    
			    if( !"".equals(categroyCodeKey.trim())){//点击一级分类的时间查询
			    	queryValue.append(" AND oneId:"+categroyCodeKey);
			    }
			    
			    if(!"".equals(selectFacetTwo.trim()) && !"1".equals(selectFacetTwo.trim())){ //按分类查询
			    	queryValue.append(" AND twoId:"+selectFacetTwo);
			    }
			    
			    if(startP!=0.0 && endP!=0.0){//按价格查询
			    	if(startP>=endP){
			    		queryValue.append(" AND currentPrice:["+endP+" TO "+startP+"]");
			    	}else{
			    		queryValue.append(" AND currentPrice:["+startP+" TO "+endP+"]");
			    	}
			    }else if(startP==0.0 && endP!=0.0){//按价格查询
			    	queryValue.append(" AND currentPrice:["+startP+" TO "+endP+"]");
			    }else if(startP!=0.0 && endP==0.0){
			    	queryValue.append(" AND currentPrice:["+endP+" TO "+startP+"]");
			    }
			}
    		
    		
    		if(newArrival==1){//按销量排序
    			   queryValueProduct.addSort(new SortClause("productNumber", ORDER.desc));
		    	   queryLongCount.addSort(new SortClause("productNumber", ORDER.desc));
		    }
		    
		    if(newArrival==2){//按最新排序
		    	   queryValueProduct.addSort(new SortClause("updateTime", ORDER.desc)); 
		    	   queryLongCount.addSort(new SortClause("updateTime", ORDER.desc));
		    }
		    
		    queryLongCount.setQuery(queryValue.toString().trim());
		    queryValueProduct.setQuery(queryValue.toString().trim()); 
    		queryValueProduct.set("defType","edismax");
	    	queryValueProduct.set("qf","productName^70.0 oneName^50.0 twoName^50.0  pinyin^70.0 py^70.0 brandCodeName^20");	
	    	queryValueProduct.set("bf", "sum(abs(stockNum),sqrt(log(ms(updateTime))))^300.0");
	    	queryValueProduct.setStart(page); // query的开始行数(分页使用)
	    	queryValueProduct.setRows(size); // query的返回行数(分页使用)
	    	queryValueProduct.setFacet(true); // 设置使用facet
	    	queryValueProduct.setFacetMinCount(1); // 设置facet最少的统计数量
	    	queryValueProduct.setFacetLimit(size); // facet结果的返回行数
	    	queryValueProduct.addFacetField("twoId"); // facet的字段
	    	queryValueProduct.setFacetSort(FacetParams.FACET_SORT_COUNT);
    		
	    	mapSolrQuery.put("queryLongCount", queryLongCount);
	    	mapSolrQuery.put("queryValueProduct", queryValueProduct);
	        
    	return mapSolrQuery;
    }
    
    
  
    
    /**
     * 高亮值替换显示
     * @param key
     * @param value
     * @return
     */
    public static String higiLight(String key,String value){  
        String newregions="<font color=\"red\">"+value+"</font>";  
        String content=key.replaceAll(value, newregions);  
        return content;  
    }
    
    /**
     * 一个字符串是不是全为数字
     * @param value
     * @return
     */
    public static boolean IsNumeric(String value) {
    	value = value.trim();
    	Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m = p.matcher(value);
		return m.matches();
    } 
    
    /**
     * 特殊字符过滤
     * @param value
     * @return
     */
    public static boolean IsSpecial(String value){
    	Pattern p = Pattern.compile("\\w+|[\u4e00-\u9fa5]+");
		Matcher m = p.matcher(value);
		if(m.find()){
			return  true;
		}else{
			return false;
		}
    }
    
    /**
     * 一个字符串必须字母开头，而且是用字母和数字组合
     * @param value
     * @return
     */
    public static boolean IsString(String value) {
    	value = value.trim();
    	Pattern p = Pattern.compile("^[a-zA-Z]\\w{0,300}$");
		Matcher m = p.matcher(value);
		return m.matches();
    } 
    
    /**
     * 判断一个字符串是不是全是字母
     * @param value
     * @return
     */
    public static boolean IsLetter(String value) {
	  value = value.trim();
	  Pattern p = Pattern.compile("^[A-Za-z]+$");
	  Matcher m = p.matcher(value);
	  return m.matches();
    }
    
    
    /***
	 * 日期格式化
	 * @param year
	 * @return
	 */
	public static Date getYear(String year){
		SimpleDateFormat sdfDate =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date dateRiQi= null;
		Date date = null;
		String  value = "";
		try {
			date= sdfDate.parse(year);
			value = sdf.format(date);
			dateRiQi = sdf.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateRiQi;
	}
	
	/**
	 *根据商品分类编号查询商品分类名称 
	 * @param facetValue
	 * @param fatherFacetValue 如果fatherFacetValue不为空 商品分类比如在当前的一级分类下
	 * @return
	 */
	public static Map<String, Object> serach(String facetValue,String fatherFacetValue){
		String queryProductSql = "select category_name from uc_sellercategory where seller_code ='SI2009' and  category_code=:category_code ";
		
		MDataMap map=new MDataMap();
		map.inAllValues("seller_code","SI2009","category_code",facetValue);
		
		if(null!=fatherFacetValue){
			
			queryProductSql+=" and parent_code=:parent_code ";
			
			map.inAllValues("parent_code",fatherFacetValue);
			
		}
		
		Map<String, Object> listProduct =DbUp.upTable("uc_sellercategory").dataSqlOne(queryProductSql, map);
		return listProduct;
	}
    
	/**
	 * 商品数据
	 * @param items_rep   查询出来的商品List集合
	 * @param facetFields   高亮显示替换值
	 * @return
	 */
	public static Map<String, Object> getProduct(List<Product> items_rep,String key){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Product> pro = new ArrayList<Product>();
    	Product product = null;
    	StringBuffer sb = new StringBuffer();
        for (Product pd : items_rep) {
        	product = new Product();
        	sb.append(pd.getProductCode()).append(",");
        	product.setProductCode(pd.getProductCode());
        	if(null==key){
        		product.setProductName(pd.getProductName());
        	}else{
        		product.setProductName(higiLight(pd.getProductName(),(null==key || "".equals(key))?"":key));
        	}
        	product.setMainpicUrl(pd.getMainpicUrl());
        	product.setCurrentPrice(pd.getCurrentPrice());
        	product.setStockNum(pd.getStockNum());
            pro.add(product);
        }
        map.put("pro", pro);
        map.put("productCode", sb.length()<=0?"":sb.toString().trim().substring(0, sb.toString().length()-1));
		return map;
	}
	
	/**
	 * 分类数据
	 * @param facetFields   查询出来的商品对应的分类信息
	 * @param categroyCode   categroyCode为null代表走文本框搜索，  不为null代表走分类查询(判断当前分类是不是在当前一级分类下)
	 * @return
	 */
	public static Map<String,Object> getFacet(List<FacetField> facetFields,String categroyCode){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> search = null;
		List<Facet>  facetList= new ArrayList<Facet>();
		
    	Facet  facetValue =  null;
    	for (FacetField ff : facetFields) {
    		if(ff.getName().trim().equals("twoId")){
    			for(Count counts:ff.getValues()){
    				if(null!=categroyCode){
    					search = serach(counts.getName(),categroyCode);
    					if(null==search|| "".equals(search)){
	        				continue;
	        			}
	        			facetValue = new Facet();
	        			facetValue.setFacetCode(counts.getName());
	        			facetValue.setName((null==search.get("category_name")|| "".equals(search.get("category_name")))?"":search.get("category_name").toString());
	        			facetValue.setCount(counts.getCount());
	        			facetList.add(facetValue);
    				}else{
    					search =serach(counts.getName(),null);
    					if(null==search || "".equals(search)){
        					continue;
        				}
        				facetValue = new Facet();
        				facetValue.setFacetCode(counts.getName());
        				facetValue.setName((null==search.get("category_name")||"".equals(search))?"":search.get("category_name").toString());
        				facetValue.setCount(counts.getCount());
        				facetList.add(facetValue);
    				}
    			}
    		}
    	}
    	Collections.sort(facetList);
        map.put("facetList", facetList);
		return map;
		
	}
	
	
    /**
     * 字符串特殊字符过滤
     * @param s
     * @return
     */
    public static String escapeQueryChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if (c == '\\' || c == '+' || c == '-' || c == '!'  || c == '(' || c == ')' || c == ':'
            || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
            || c == '*' || c == '?' || c == '|' || c == '&'  || c == ';' || c == '/'
            || Character.isWhitespace(c)) {
            sb.append(' ');
          }
          sb.append(c);
        }
        return sb.toString();
      }

}
