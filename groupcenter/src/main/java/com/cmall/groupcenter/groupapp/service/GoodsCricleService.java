package com.cmall.groupcenter.groupapp.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.accountmarketing.util.LongShortUtil;
import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoResult;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoResult;
import com.cmall.groupcenter.groupapp.model.GoodComparatorDesc;
import com.cmall.groupcenter.groupapp.model.GoodsCricleInfo;
import com.cmall.groupcenter.groupapp.model.GoodsInfo;
import com.cmall.groupcenter.groupapp.model.Person;
import com.cmall.groupcenter.groupapp.model.ShareBigPicUrlModel;
import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.groupcenter.model.QueryBankInfoResult;
import com.cmall.groupcenter.pc.model.PcVirtualPager;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.google.gson.internal.LinkedTreeMap;
import com.srnpr.xmasproduct.api.ApiSkuInfo;
import com.srnpr.xmasproduct.model.SkuInfos;
import com.srnpr.xmassystem.modelevent.PlusModelFullMoney;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.LogInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

public class GoodsCricleService extends BaseClass{
	
	
	/**
	 * 查询 热销榜 超级返 数据
	 * @param inputParam
	 * @param apikey
	 * @param token
	 * @return
	 */
	public GetGoodsCricleListInfoResult GetGoodsCricleListInfo(GetGoodsCricleListInfoInput inputParam,String apikey,String token){
   	 	
	  int qtype= Integer.parseInt(inputParam.getSectionType());

   	  String goodCricleSql ="select content from  gc_goods_cricle_temp_info where  type=:type";
	  Map<String, Object> rmap = DbUp.upTable("gc_goods_cricle_temp_info").dataSqlOne(goodCricleSql, new MDataMap("type",String.valueOf(qtype)));
	  String content= String.valueOf(rmap.get("content"));
	  
	 JsonHelper<List<GoodsCricleInfo>> jH = new JsonHelper<List<GoodsCricleInfo>>();
	 List<GoodsCricleInfo> list = jH.StringToObj(content,new ArrayList() );
	 int pagesize = inputParam.getPaging().getLimit();
	 pagesize = pagesize==0 ? 10 :pagesize;
	 // 构造一个分页器
	 PcVirtualPager<GoodsCricleInfo> pager = new PcVirtualPager<GoodsCricleInfo>(list.size(), inputParam.getPaging().getOffset()+1, pagesize,list);
	
     GetGoodsCricleListInfoResult result = new GetGoodsCricleListInfoResult();
     result.setGoodsContentList(pager.getCurrentPageData());
     
     PageResults pageResult = new PageResults();
     pageResult.setCount(pager.getTotalResults());
     pageResult.setMore(pager.getPageIndex()<pager.getPageCount() ? 1 :0 );
     pageResult.setTotal(pager.getCurrentPageData().size());
     result.setPaged(pageResult);
		
	  return result;
	}
	
	
	//生成热销榜 超级返的对象，将对象转换json，入库
     public GetGoodsCricleListInfoResult generateGoodsCricleInfo(GetGoodsCricleListInfoInput inputParam,String apikey,String token,String mobile){
    	 GetGoodsCricleListInfoResult result=new GetGoodsCricleListInfoResult();
    	
		int qtype= Integer.parseInt(inputParam.getSectionType());
    	String qtypename; 
    	if(qtype==0)
    		qtypename="热销榜";
		else
			qtypename="超返利";
    			
    	List<Map<String, Object>> sukProductList = this.getProductList(inputParam,result);
    	
    	
		List<GoodsCricleInfo> goodsContentList = new ArrayList<GoodsCricleInfo>();

    	for(Map<String, Object> sukProduct: sukProductList){
        	//分享实体
    		ShareModel smodel = new ShareModel();

    		
    		String productCode = String.valueOf(sukProduct.get("product_code"));
        	//System.out.println("productCode:"+productCode);
    		HashMap<String, Object> httpProductMap = this.getHttpResponseMap(productCode,token,inputParam.getPicWidth());
        	//SkuInfos skuInfos = this.getSkuInfos(productCode);
    		HashMap<String, Object> skuMap = this.getApiGetEventSkuInfoForHttp(productCode);
        	GoodsInfo gInfo=this.getGoodInfoObjFromHttpMap(new GoodsInfo(),skuMap,httpProductMap,smodel,qtype,mobile);
        	//查询剩余库存
        	gInfo.setLimitStock(String.valueOf(this.getLimitStock(skuMap)));
        	GoodsCricleInfo ci = new GoodsCricleInfo();
        	
        	
	    	String prex = bConfig("groupcenter.wei_shop_url");
	    	gInfo.setGoodDetailUrl(prex+"Product_Detail.html?pid="+productCode);
        	gInfo.setGoodsSourceUrl("惠家有");
        	//超返利
        	if(qtype==1 && sukProduct.get("rebatef")!=null){
        		
        		if(sukProduct.get("rebate_range")!=null && sukProduct.get("rebate_range").toString().indexOf("4497472500020001")!=-1 ){
        			//本人参加返利	
        			gInfo.setIsSelfRebate("1");
        		}
        		gInfo.setFlagEnable(Integer.parseInt(sukProduct.get("flag_enable").toString()));
        		gInfo.setRebateScale(Double.parseDouble(sukProduct.get("rebatef").toString()));
        		Double rebateMoney =Double.parseDouble(sukProduct.get("rebatef").toString())/100 * Double.parseDouble(gInfo.getCurrentPrice());
        		NumberFormat nf = NumberFormat.getNumberInstance(); 
            	nf.setMaximumFractionDigits(2); 
       		 	gInfo.setRebateMoney(String.valueOf(nf.format(rebateMoney)));
        		//剩余时间
        		String endTimStr = String.valueOf(sukProduct.get("end_time"));
        		//endTimStr="2010-12-31 23:59:00";
        		Date endTime = DateHelper.parseDate(endTimStr);
        		Long offsetTime = endTime.getTime()- new Date().getTime();
        		ci.setOffsetTime(offsetTime);
        	} 
        	//热销榜
        	else {
        		//返利比数
        		String salesCount ="0";
        		if(sukProduct.get("salesCount")!=null){
        			salesCount =sukProduct.get("salesCount").toString();
        		}
        		if(StringUtils.isBlank(salesCount)){
        			salesCount="0";
        		}
        		gInfo.setSalesCount(salesCount);
        		Double reRate = this.getHuiRebateRate();
        		if(reRate!=null) {
        			//热销榜
            		Double rebateMoney =reRate/100 * Double.parseDouble(gInfo.getCurrentPrice());
            		NumberFormat nf = NumberFormat.getNumberInstance(); 
                	nf.setMaximumFractionDigits(2); 
           		 	gInfo.setRebateMoney(String.valueOf(nf.format(rebateMoney)));
        		}
        	}
        	
        	
			smodel.setShareContent(String.valueOf(httpProductMap.get("discriptInfo")));
			String shareTitle = gInfo.getGoodsName()+","+gInfo.getCurrentPrice()+"元";
			smodel.setShareTitle(shareTitle);
			//图片(旧图)
			LinkedTreeMap map = (LinkedTreeMap)httpProductMap.get("mainpicUrl");
	    	if(map!=null) {
	    		smodel.setSharePicUrl(String.valueOf(map.get("picOldUrl")));
	    	}
	    	
	    	//String shareUrl = prex+"/cgroup/web/grouppageSecond/productdetail.html?productUrl="+bConfig("familyhas.shareUrl")+gInfo.getGoodsCode()+"&api_key="+apikey;
	    	//String shareUrl = prex+"/cgroup/web/grouppageSecond/productdetail.html?productUrl="+bConfig("familyhas.shareUrl")+gInfo.getGoodsCode()+"&api_key="+apikey;

	    	//smodel.setShareUrl(prex+"Product_Detail.html?pid="+productCode);
			
	    	
	    	String supperMobile = this.encodeMobile(mobile);
	    	String weiShopUrl = bConfig("groupcenter.wei_shop_url");
	    	String shareUrl =bConfig("groupcenter.app_recommendPageUrl")+"/cgroup/web/grouppageshare/product_detail.ftl?pid="+productCode+"&sectiontype="+qtype+"&supermobile="+supperMobile+"&weishopurl="+weiShopUrl;
	    	smodel.setShareUrl(shareUrl);
	    	
	    	
	    	ci.setShareModel(smodel);
			//查询微公社分享数
			ProductShareService shareService = new ProductShareService();
			int shareCount =shareService.getShareNumByProductCode(gInfo.getGoodsCode());
			ci.setShareCount(shareCount);
			ci.setGoodsInfo(gInfo);
			
			goodsContentList.add(ci);
    	}
    	/*//超级返的重新排序
    	if(qtype==1){
        	result.setGoodsContentList(this.getSortGoodInfo(goodsContentList));
    	} else {
        	result.setGoodsContentList(goodsContentList);
    	}*/
    	result.setGoodsContentList(goodsContentList);
    	return  result;
     }
     
     
     
     
     
    
     
     //获取商品剩余库存
     public int getLimitStock(HashMap<String, Object> skusMap){
    	 int limitStock=0;
    	 /*if(skus!=null && skus.size()>0){
    		 for(int i=0;i<skus.size();i++){
    			 limitStock=limitStock+new Long(skus.get(i).getLimitStock()).intValue();
        	 }
    	 }*/
    	 
    	 
    	List skus =(ArrayList)skusMap.get("skus");
     	if(skus!=null && skus.size()>0 ){
     		for(int i=0;i<skus.size();i++){
         		LinkedTreeMap treeMap =(LinkedTreeMap)skus.get(i);
         		Double dobLimitStock=Double.parseDouble(String.valueOf(treeMap.get("limitStock")));
         		limitStock=limitStock+dobLimitStock.intValue();
     		}
     	}
     	return limitStock;
         		
     }
     		
     
     //转换 商品信息
     public GoodsInfo  getGoodInfoObjFromHttpMap(GoodsInfo gdInfo,HashMap<String, Object> skusMap,HashMap<String, Object> httpProductMap,ShareModel smodel,int qtype,String mobile){
    	//商品-获取商品基本信息 -api获取
    	 //商品编号
    	String productCode= String.valueOf(httpProductMap.get("productCode"));
    	//商品名称
    	String productName= String.valueOf(httpProductMap.get("productName"));
    	//返现金额
    	String disMoney= String.valueOf(httpProductMap.get("disMoney"));
    	//售价
    	//String sellPrice = String.valueOf(httpProductMap.get("sellPrice"));
    	//原价
    	String discount = String.valueOf(httpProductMap.get("marketPrice"));//国杰
    	
    	String productStatus = String.valueOf(httpProductMap.get("productStatus"));//商品上下架状态
    	
    	List dpList = (List) httpProductMap.get("pcPicList");//商品轮播图	
    	
    	List propertyList =(List)httpProductMap.get("propertyInfoList");//规格参数
    	
    	List discriptPicList =(List)httpProductMap.get("discriptPicList");//图文

	    
    	String prex = bConfig("groupcenter.app_recommendPageUrl");

    	
    	List <ShareBigPicUrlModel> descPicList=new ArrayList<ShareBigPicUrlModel>();
    	String supperMobile = this.encodeMobile(mobile);
    	
    	String weiShopUrl = bConfig("groupcenter.wei_shop_url");

    	String productUrl =prex+"/cgroup/getTwoDimensionCode?url="+prex+"/cgroup/web/grouppageshare/product_detail.ftl?pid="+productCode+"%26sectiontype="+qtype+"&supermobile="+supperMobile+"&type=one&weishopurl="+weiShopUrl;
    	System.out.println("productUrl:"+productUrl);
    	
    	ShareBigPicUrlModel picBean = new ShareBigPicUrlModel();
		picBean.setImageUrl(productUrl);
		descPicList.add(picBean);
    	for(int i=0;i<dpList.size();i++){
    		LinkedTreeMap treeMap =(LinkedTreeMap)dpList.get(i);
    		String newPicUrl =(String)treeMap.get("picNewUrl");
    		ShareBigPicUrlModel picBeanT = new ShareBigPicUrlModel();
    		picBeanT.setImageUrl(newPicUrl);
    		descPicList.add(picBeanT);
    	}
    	smodel.setShareBigPicUrlList(descPicList);
    	
    	
    	BigDecimal sellPriceT =new BigDecimal(0);
    	int count=0;
    	List skus =(ArrayList)skusMap.get("skus");
    	if(skus!=null && skus.size()>0 ){
    		for(int i=0;i<skus.size();i++){
        		LinkedTreeMap treeMap =(LinkedTreeMap)skus.get(i);
    			BigDecimal currSellPrice = new BigDecimal(String.valueOf(treeMap.get("sellPrice")));
        		if(count==0){
        			sellPriceT= currSellPrice;
	    		}
	    		count++;
	    		if(sellPriceT.compareTo(currSellPrice)==1){
	    			sellPriceT= currSellPrice;
	        		//System.out.println("sellPriceT="+ sellPriceT);
	    		}
    		}
    	}
    	/*SkuInfos skuInfos=null;
    	List<PlusModelSkuInfo> listMoney = skuInfos.getSkus();
    	
    	for(PlusModelSkuInfo p:listMoney){
	    		if(count==0){
	    			sellPriceT=      p.getSellPrice();
	    		}
	    		count++;
	    		if(sellPriceT.compareTo(p.getSellPrice()) ==1){
	    			sellPriceT= p.getSellPrice();
	        		//System.out.println("sellPriceT="+ sellPriceT);
	    		}
	    	}*/
    	String sellPrice= String.valueOf(sellPriceT);
    	//System.out.println("noo:"+sellPrice);
    	
    	gdInfo.setGoodsCode(productCode);
    	gdInfo.setGoodsName(productName);
    	gdInfo.setRebateMoney(disMoney);
    	
    	
    	gdInfo.setCurrentPrice(sellPrice);
    	gdInfo.setOriginalPrice(discount);
    	gdInfo.setProductStatus(productStatus);
    	
    	gdInfo.setDiscountPrice(Double.parseDouble(sellPrice)/Double.parseDouble(sellPrice));
    	
    	gdInfo.setPropertyList(propertyList);
    	//图片
    	LinkedTreeMap map = (LinkedTreeMap)httpProductMap.get("mainpicUrl");
    	if(map!=null) {
        	gdInfo.setGoodsIcon(String.valueOf(map.get("picNewUrl")));
    	}
    	
    	gdInfo.setDiscriptPicList(discriptPicList);
    	//商品-获取SKU的价格和库存 api
    	//gdInfo.setCurrentPrice(skuInfos.getSellPrice());
    	
    	
    	return gdInfo;
     }
     
     //获取商品列表
     public  List<Map<String, Object>> getProductList(GetGoodsCricleListInfoInput inputParam,GetGoodsCricleListInfoResult result){
    	 String productCode=inputParam.getProductCode();
    	 String sql = "";
    	 int qtype= Integer.parseInt(inputParam.getSectionType());

    	 //热销榜
    	 if(qtype==0){
    		
    		 	    sql="     SELECT" +
    		 		"        pp.product_code," +
    		 		"				count(DISTINCT order_code) AS salesCount" +
    		 		"			FROM" +
    		 		"				groupcenter.gc_reckon_log gr" +
    		 		"			INNER JOIN productcenter.pc_skuinfo sku ON gr.sku_code = sku.sku_code  AND gr.reckon_change_type = '4497465200030001'" +
    		 		"			INNER JOIN productcenter.pc_productinfo pp ON pp.product_code = sku.product_code" +
    		 		"			AND sku.sale_yn = 'Y'" +
    		 		"			WHERE " +
    		 		"				1=1 " +
    		 		"			AND gr.sku_code IS NOT NULL" +
    		 		"			AND gr.sku_code != ''";
    		 	    //商品详情
    		 	    if(!StringUtils.isBlank(productCode)){
    		 	    	sql=sql+" AND pp.product_code ='" +productCode +"' "; 
    		 	    }else{
    		 	    	sql=sql+" AND (date(gr.create_time) >= date_sub(curdate(),interval 7 day) and date(gr.create_time) < curdate())" ;
    		 	    } 
    		 	   
    		 	    sql =sql+"	GROUP BY" +
			   		 		"		   pp.product_code	" +
			   		 		"      ORDER BY 	count(DISTINCT(order_code)) DESC," +
			   		 		"			pp.create_time " 
			   				 +"  ";
    		 	    sql=sql+" LIMIT 100 ";

    	 }
 		else
 		{	
 			//返利榜
 			sql="SELECT" +
 					"	ff.product_code,ff.rebatef,ff.end_time,ff.rebate_range,ff.flag_enable " +
 					" FROM" +
 					"	(" +
 					"		SELECT" +
 					"			srs.product_code,srs.rebatef,srs.end_time,srs.rebate_range,srs.flag_enable " +
 					"		FROM" +
 					"			(" +
 					"				SELECT" +
 					"					" +
 					" 					max(substring_index(srs.rebate_scale, \",\", 1)) rebatef "+
					" 					,min(end_time) as end_time, "+
					" 					srs.product_code, "+
					" 					max(srs.create_time) as create_time,max(srs.rebate_range) as rebate_range, "+ 
					" 					max(srs.flag_enable) AS flag_enable "+ 
					"				FROM" +
 					"					groupcenter.gc_sku_rebate_scale srs" +
 					"				WHERE" +
 					"					srs.supper_rebate_flag = '4497472500070001' ";
 					
				//商品详情
				 if(!StringUtils.isBlank(productCode)){
				 	sql=sql+" AND srs.product_code ='" +productCode +"' "; 
				 }else{
				 	sql=sql+" AND srs.flag_enable=1 AND   ( NOW() > start_time  and now()<end_time)  " ;
				 } 
 					
			sql=sql+"" +
 					"   			 GROUP BY srs.product_code "+
 					"			) srs" +
 					"		LEFT JOIN (" +
 					"			SELECT" +
 					"				product_code," +
 					"				SUM(" +
 					"					qq_share_cout + qq_space_share_cout + wei_share_count + wei_friendcircle_share_count + sina_share_count + sms_share_count + wei_gs_share_count" +
 					"				) AS mcount," +
 					"				count(1)" +
 					"			FROM" +
 					"				groupcenter.gc_product_share_log" +
 					"			WHERE" +
 					"				1 = 1" +
 					"			GROUP BY" +
 					"				product_code" +
 					"			ORDER BY" +
 					"				mcount DESC" +
 					"			LIMIT 50" +
 					"		) sh ON sh.product_code = srs.product_code" +
 					"		WHERE" +
 					"			1 = 1" +
 					"		ORDER BY" +
 					"			sh.mcount DESC," +
 					"			cast(srs.rebatef AS signed) DESC,srs.end_time ASC," +
 					"		 srs.create_time ASC " +
 					"		LIMIT 50" +
 					"	) ff " +
 					" WHERE " +
 					"	1 = 1 ";
 			
 			/*if(!StringUtils.isBlank(productCode)){
	 	    	sql=sql+" and ff.product_code ='" +productCode +"' "; 
	 	    } */
 			
 		}
    	 
    	 List<Map<String, Object>> list = DbUp.upTable("pc_skuinfo").dataSqlList(sql, null);
     	
    	 int pagesize = inputParam.getPaging().getLimit();
    	 pagesize = pagesize==0 ? 10 :pagesize;
    	 // 构造一个分页器
    	 PcVirtualPager<Map<String, Object>> pager = new PcVirtualPager<Map<String, Object>>(list.size(), inputParam.getPaging().getOffset()+1, pagesize,list);
         
         PageResults pageResult = new PageResults();
         pageResult.setCount(pager.getTotalResults());
         pageResult.setMore(pager.getPageIndex()<pager.getPageCount() ? 1 :0 );
         pageResult.setTotal(pager.getCurrentPageData().size());
         result.setPaged(pageResult);
    	return pager.getCurrentPageData();
     }
     
     //判断是否还有下一页
     public  int getMore(int totalResults,int pageSize,int currentPage){
    	 int pageCount=0;
    	 pageSize = pageSize ==0 ? 10 : pageSize;
    	 if (totalResults % pageSize == 0) {
             pageCount = totalResults / pageSize;
         } else {
             pageCount = totalResults / pageSize + 1;
         }
    	if(currentPage+1<pageCount){
    		return 1;
    	}
    	 
    	 return 0;
     }
     
     
     /**
      * 获取商品信息
      * @param productCode
      * @param token
      * @return
      */
     public HashMap<String,Object>  getHttpResponseMap(String productCode,String token,int picWidth) {
 		Map<String, String> map = new HashMap<String, String>();
 		map.put("api_key", "betafamilyhas");
 		map.put("api_target", "com_cmall_familyhas_api_ApiGetEventSkuInfo");
 		//map.put("api_secret", token);
 		map.put("api_input", "{\"version\":1,\"productCode\":\""+productCode+"\",\"picWidth\":\""+picWidth+"\",\"buyerType\":\"4497469400050002\"}");
 		
 		String weiPrefix = bConfig("groupcenter.app_recommendPageUrl");
 		String url =weiPrefix+"/cgroup/jsonapi/com_cmall_familyhas_api_ApiGetEventSkuInfo";
 		String responseStr = Http_Request_Post.doPost(url, map, "utf-8");
		HashMap<String,Object> responMap = new JsonHelper<HashMap<String,Object>>().GsonFromJson(responseStr, new HashMap<String,Object>());
 		//System.out.println("product code :"+productCode+"  返回的消息是:" + responseStr);
 		return responMap;
 	}
     
     public SkuInfos getSkuInfos(String productCode){
    	 PlusModelSkuQuery inputParam = new PlusModelSkuQuery();
    	 inputParam.setCode(productCode);
    	 SkuInfos skuInfos = new ApiSkuInfo().Process(inputParam, null);
    	 return skuInfos;
     }
     /**
      * 获取商品信息
      * @param productCode
      * @param token
      * @return
      */
     public HashMap<String,Object>  getApiGetEventSkuInfoForHttp(String productCode) {
 		Map<String, String> map = new HashMap<String, String>();
 		map.put("api_key", "betagroup");
 		map.put("api_target", "com_srnpr_xmasproduct_api_ApiSkuInfo");
 		//map.put("api_secret", token);
 		map.put("api_input", "{\"version\":1,\"code\":\""+productCode+"\",\"memberCode\":\"\",\"areaCode\":\"\",\"sourceCode\":\"\"}");
 		
 		String weiPrefix = bConfig("groupcenter.app_recommendPageUrl");
 		String url =weiPrefix+"/cgroup/jsonapi/com_srnpr_xmasproduct_api_ApiSkuInfo";
 		String responseStr = Http_Request_Post.doPost(url, map, "utf-8");
		HashMap<String,Object> responMap = new JsonHelper<HashMap<String,Object>>().GsonFromJson(responseStr, new HashMap<String,Object>());
 		//System.out.println("product code :"+productCode+"  返回的消息是:" + responseStr);
 		return responMap;
 	}
     
     //获取惠家友，返利比例
     public Double getHuiRebateRate(){
    	 
    	 String sql="SELECT" +
    			 "	substring_index(gtr.rebate_rate, \",\", 1) rebatef  " +
    			 "FROM" +
    			 "	`gc_wopen_appmanage` gwa" +
    			 " INNER JOIN gc_trader_rebate gtr ON gwa.trade_code = gtr.trader_code and gwa.app_code=:app_code";

    	 Map<String, Object> rmap = DbUp.upTable("gc_trader_rebate").dataSqlOne(sql, new MDataMap("app_code","SI2003"));
    	 if(rmap!=null && rmap.get("rebatef")!=null){
    		 return Double.parseDouble(String.valueOf(rmap.get("rebatef")));
    	 }
    	 return null;
    	 
     }
    	 
    	 
    	
     //折扣排序
     public List<GoodsCricleInfo> getSortGoodInfo(List<GoodsCricleInfo> cricleInfo){
   	 List<GoodsCricleInfo> newgcInfo = new ArrayList<GoodsCricleInfo> ();
    	 List <GoodsInfo > gs = new ArrayList<GoodsInfo>();
	   	 for(GoodsCricleInfo old : cricleInfo ){
	   		 System.out.println("原来的：分享数:"+old.getShareCount() +" 返利比例:"+old.getGoodsInfo().getRebateScale()+" 毫秒:"+old.getOffsetTime() );
	   		 

	   		 
	   		  //gs.add(old.getGoodsInfo());
	   	 }
   	 
	   	 Collections.sort(cricleInfo, new GoodComparatorDesc());
	   	 /*for(GoodsInfo newGood : gs ){
	   		 //System.out.println("现在的：分享数："+f.getDiscountPrice());
	   		 for(GoodsCricleInfo old : cricleInfo){
	   			if(old.getGoodsInfo().getGoodsCode().equals(newGood.getGoodsCode())){
	   				GoodsCricleInfo gc = new GoodsCricleInfo();
	   				gc.setGoodsInfo(newGood);
	   				gc.setOffsetTime(old.getOffsetTime());
	   				gc.setShareCount(old.getShareCount());
	   				gc.setShareModel(old.getShareModel());
	   				newgcInfo.add(gc);
	   			}
	   		 }
	   	 }*/
	   	
	   	 for(GoodsCricleInfo old : cricleInfo ){
	   		 System.out.println("现在的：分享数："+old.getShareCount()+ " 返利比例:"+old.getGoodsInfo().getRebateScale()+" 毫秒:"+old.getOffsetTime());

	   	 }
   	
   	 return cricleInfo;
    }
     
     
     
    
     
   //手机加密
   	 public String encodeMobile(String mobile) {
   	    	if(mobile==null || mobile.length()==0){
   	    		return "";
   	    	}
   	    	String[] encryptArr = new String[]{"j","c","a","b","u","i","p","o","y","q","w","x","m"};
   	    	String encodeMobile = "";
   			for(int i=0;i<mobile.length();i++){
   				for(int j=0;j<encryptArr.length;j++){
   					encodeMobile +=encryptArr[Integer.parseInt(mobile.substring(i,i+1))];
   						break;
   				}
   			}
   			return encodeMobile;
   	    }
     
     
     
    
}
