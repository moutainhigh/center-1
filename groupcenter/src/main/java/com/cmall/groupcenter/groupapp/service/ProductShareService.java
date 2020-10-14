package com.cmall.groupcenter.groupapp.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoResult;
import com.cmall.groupcenter.groupapp.model.GoodsCricleInfo;
import com.cmall.groupcenter.groupapp.model.Person;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebTemp;

public class ProductShareService extends BaseClass{
	
	//添加分享数
     public RootResultWeb AddShareNum(String productCode, String shareType){
    	 RootResultWeb result=new RootResultWeb();
    	 boolean exits = checkProductCode(productCode);
    	 if(!exits){
    		 result.inErrorMessage(918570007);
    	 }
    	 if(result.getResultCode()==1){
    		 String productShareSql ="select * from  groupcenter.gc_product_share_log where  product_code=:product_code";
        	 Map<String, Object> rmap = DbUp.upTable("gc_product_share_log").dataSqlOne(productShareSql, new MDataMap("product_code",productCode));

        	  //添加分享
    		  if(rmap==null || rmap.get("product_code")==null){
    			  DbUp.upTable("gc_product_share_log").insert("uid",WebHelper.upUuid(),"product_code",productCode,this.getColumByType(shareType),"1");
    		  }else{
    			  String shareNumStr =String.valueOf( rmap.get(getColumByType(shareType)));
    			  int shareNum = Integer.parseInt(shareNumStr);
    			  shareNum= shareNum+1;
    			  DbUp.upTable("gc_product_share_log").dataUpdate(new MDataMap("product_code",productCode,getColumByType(shareType),String.valueOf(shareNum)), getColumByType(shareType), "product_code");
    		  }
    		  /*//更新热销榜 分享数
    		  resetShareNumByCache(productCode,0);
    		  //更新超级返 分享数
    		  resetShareNumByCache(productCode,1);*/
    	 }
    	return  result;
     }
     
     //更新分享数
       public void resetShareNumByCache(String productCode,int type){
    	   String goodCricleSql ="select content from  gc_goods_cricle_temp_info where  type=:type";
    	   Map<String, Object> rmap = DbUp.upTable("gc_goods_cricle_temp_info").dataSqlOne(goodCricleSql, new MDataMap("type",String.valueOf(type)));
    	   String content= String.valueOf(rmap.get("content"));
    	   JsonHelper<List<LinkedHashMap>> jH = new JsonHelper<List<LinkedHashMap>>();
    	   List<LinkedHashMap> list = jH.StringToObj(content,new ArrayList<LinkedHashMap>() );
    	   if(list==null){
        	   return;
           }
    	   
    	   for(int i=0;i< list.size();i++){
    		   LinkedHashMap maps = (LinkedHashMap)list.get(i);
    		   Iterator iter = maps.keySet().iterator(); 
    		   while (iter.hasNext()) { 
    			   Object key = iter.next(); 
    			   if(key.equals("goodsInfo")){
    				   LinkedHashMap googInfoMap = (LinkedHashMap) maps.get(key); 
    				   if(googInfoMap.get("goodsCode")!=null && googInfoMap.get("goodsCode").toString().equals(productCode)){
    					   maps.put("shareCount",Integer.parseInt(String.valueOf(maps.get("shareCount")))+1);
    					   break;
    				   }
    			   }
    		   } 
    		  /* if(gi.getGoodsInfo().getGoodsCode().equals(productCode)){
    			  gi.setShareCount(gi.getShareCount()+1);
    			  break;
    		   }*/
           }
    	   
    	 JsonHelper<List<LinkedHashMap>> jhT = new JsonHelper<List<LinkedHashMap>>();
   		 String contentT = jhT.ObjToString(list);
		 DbUp.upTable("gc_goods_cricle_temp_info").dataUpdate(new MDataMap("content",contentT,"type",String.valueOf(type)), "content", "type");
        
       }
     
     //根据商品编号获取，分享数
     public int getShareNumByProductCode(String productCode){
    	 int count=0;
    	 RootResultWeb result=new RootResultWeb();
    	 String productShareSql ="SELECT   SUM(qq_share_cout+qq_space_share_cout+wei_share_count+wei_friendcircle_share_count+sina_share_count+sms_share_count+wei_gs_share_count) as mcount, count(1) FROM `gc_product_share_log` where product_code=:product_code GROUP BY product_code ";
    	 Map<String, Object> rmap = DbUp.upTable("gc_product_share_log").dataSqlOne(productShareSql, new MDataMap("product_code",productCode));
    	 if(rmap!=null && rmap.get("mcount")!=null){
    		count= Integer.parseInt(rmap.get("mcount").toString());
    	 }
    	return  count;
     }
     
     public String  getColumByType(String type){
    	 String columName="";
    	 if("4497472000100001".equals(type)){
    		 columName="qq_share_cout";
    	 }else
		 if("4497472000100002".equals(type)){
    		 columName="qq_space_share_cout";
    	 }else
		 if("4497472000100003".equals(type)){
        		 columName="wei_share_count";
    	 }else
		 if("4497472000100004".equals(type)){
    		 columName="wei_friendcircle_share_count";
    	 }else
		 if("4497472000100005".equals(type)){
    		 columName="wei_gs_share_count";
    	 }else
		 if("4497472000100006".equals(type)){
    		 columName="sina_share_count";
    	 }else
		 if("44974720001000027".equals(type)){
    		 columName="sms_share_count";
    	 }
    	 return columName;
     }
     
     //检查商品是否存在
     public boolean checkProductCode(String productCode){
    	 MDataMap pmap = new MDataMap();
    	 pmap.put("product_code", productCode);
    	 int count =DbUp.upTable("pc_productinfo").dataCount("product_code=:product_code",pmap );
    	 if(count==0){
    		 return false;
    	 }	 
    	 return  true;
     } 
     
     public static void main(String[] args) {
		ProductShareService ser = new ProductShareService();
		ser.resetShareNumByCache("8016408676", 0);
		ser.resetShareNumByCache("8016408676", 1);
	}
}
