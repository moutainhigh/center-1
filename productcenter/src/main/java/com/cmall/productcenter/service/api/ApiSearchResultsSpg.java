package com.cmall.productcenter.service.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cmall.productcenter.model.Item;
import com.cmall.productcenter.model.Pager;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.Product;
import com.cmall.productcenter.model.SolrData;
import com.cmall.productcenter.model.api.ApiSearchResultsInput;
import com.cmall.productcenter.model.api.ApiSearchResultsResult;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.util.Base64Util;
import com.cmall.productcenter.util.SolrQueryUtil;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapcom.basehelper.MapHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 搜索结果返回
 * 
 * @author zhouguohui
 * 
 */
public class ApiSearchResultsSpg extends
		RootApiForManage<ApiSearchResultsResult, ApiSearchResultsInput> {

	@SuppressWarnings("unchecked")
	public ApiSearchResultsResult Process(ApiSearchResultsInput inputParam,
			MDataMap mRequestMap) {
		if(StringUtils.isBlank(inputParam.getBuyerType()))
		{
			inputParam.setBuyerType("4497469400050002");
		}
		ProductService productService = new ProductService();
		ApiSearchResultsResult re = new ApiSearchResultsResult();
		/**添加列表显示的默认样式 1、小图列表；2、大图列表***/
		re.setStyleValue(Integer.parseInt(TopUp.upConfig("productcenter.styleValue")));
		String sellercode = getManageCode();
		List<Item> listItem = new ArrayList<Item>();
		List<String> sl = null;
		Item item = null;
		String tag = null;
		String[] tagList = null;
		Pager pager = new Pager();
		String keyWord = null;
		String baseValue= inputParam.getBaseValue();
		String buyerType = inputParam.getBuyerType();
		String categoryOrBrand = inputParam.getCategoryOrBrand();
		
		if(baseValue.equals("base64")){
			keyWord = (null==Base64Util.getFromBASE64(inputParam.getKeyWord()))?"": Base64Util.getFromBASE64(inputParam.getKeyWord());
		}else{
			keyWord= inputParam.getKeyWord();
		}
		
		/***
		 * 往消息AQ里面放搜索关键词
		 */
		/*MDataMap mDataMapKeyWord = new MDataMap();
		mDataMapKeyWord.put("sellerCode", sellercode);
		mDataMapKeyWord.put("keyWord", keyWord);
		mDataMapKeyWord.put("userName", inputParam.getUserName());
		mDataMapKeyWord.put("source", categoryOrBrand);
		JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnSearchKeyWord,null,mDataMapKeyWord);*/
		
		
		// 0、默认；1、销量；2、上架时间；3、价格；4、人气     默认为：0
		int sortType = inputParam.getSortType();
		if (sortType <= 0) {
			sortType = 0;
		}
		// 1、正序；2、倒序 默认为：2
		int sortFlag = inputParam.getSortFlag();
		if (sortFlag <= 0) {
			sortFlag = 2;
		}
		// 每页读取记录数 默认为10
		int pageSize = inputParam.getPageSize();
		if (pageSize <= 0) {
			pageSize = 10;
		}
		// 读取页码 默认为1
		int pageNo = inputParam.getPageNo();
		if (pageNo <= 0) {
			pageNo = 1;
		}
		int page = pageNo;
		pageNo = (pageNo - 1) * pageSize;
		// 屏幕宽度
		int screenWidth = inputParam.getScreenWidth();
		if( screenWidth== 0 || screenWidth== 1 ){
			screenWidth=200;
		}
		try{
			
		/**20150909添加 新版solr5.2.1**/
		if(TopUp.upConfig("productcenter.spgwebclient").equals("yes")){
		
			MDataMap dataMap = new MDataMap();
			dataMap.put("keyWord", keyWord);
			dataMap.put("sortType",sortType+"");
			dataMap.put("sortFlag",sortFlag+"");
			dataMap.put("pageNo", pageNo+"");
			dataMap.put("pageSize", pageSize+"");
			dataMap.put("sellercode",getManageCode());
			if(categoryOrBrand.equals("category")){
				dataMap.put("category","category");
			}else if(categoryOrBrand.equals("brand")){
				dataMap.put("brand","brand");
			}else if(categoryOrBrand.equals("top50")){
				dataMap.put("top50","top50");
			}else{
				dataMap.put("key","key");
			}
			String pro = WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturlselect"), dataMap);
			if(pro!=null && !pro.equals("")){
				List<SolrData> list = JSON.parseObject(pro,new TypeReference<List<SolrData>>(){}); 
				long longSum = 0;
				if(!list.isEmpty()){
					/*String countValue = WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturlselectcount"), dataMap);
					longSum = Long.parseLong(countValue);*/
					ProductService ps = new ProductService();
					PlusSupportProduct psp = new PlusSupportProduct();
					/**封装商品Code和**/
					for(int i=0;i<list.size();i++){
						if(i==0){
							longSum= list.get(i).getCounts();
						}
						item = new Item();
						item.setProductCode(list.get(i).getK1());
						item.setProductName(list.get(i).getS9());
						PicInfo imgUrl = ps.getPicInfoOprBig(screenWidth, list.get(i).getS4());
						item.setImgUrl(imgUrl==null?"":imgUrl.getPicNewUrl());
						item.setOriginalPrice(new BigDecimal(list.get(i).getD1()).setScale(2, RoundingMode.HALF_UP));//市场价
						String[] skuCode = list.get(i).getS7()==null ? new String[0] : list.get(i).getS7().split(",");
						if(skuCode.length>0){
							List<Double> listPrice = new ArrayList<Double>();
							for(int j=0;j<skuCode.length;j++){
								PlusModelSkuInfo skuInfo = psp.upSkuInfoBySkuCode(skuCode[j],null);
								if(skuInfo!=null){
									listPrice.add(skuInfo.getSellPrice().doubleValue());
								}
							}
							Collections.sort(listPrice);  
							item.setCurrentPrice(listPrice.size()<0?BigDecimal.ZERO:new BigDecimal(listPrice.get(0)).setScale(2, RoundingMode.HALF_UP));
						}else{
							item.setCurrentPrice(BigDecimal.ZERO);//格有问题价
						}
						item.setFlagTheSea(list.get(i).getI3()==0?"0":"1");//是否海外购  1代表是  0代表不是
						item.setStockNum(list.get(i).getI1()==0?"售罄":"有货");
						item.setProductNumber(list.get(i).getI2());//销量
						item.setTagList(list.get(i).getL6());//标签
						listItem.add(item);
					}
					pager.setPageNo(page);
					int lengNum = 0;
					if ((longSum % pageSize) == 0) {
						lengNum = (int) longSum / pageSize;
					} else {
						lengNum = (int) longSum / pageSize + 1;
					}

					pager.setPageNum(lengNum);
					pager.setPageSize(pageSize);
					pager.setRecordNum((int) longSum);
					re.setPager(pager);
					re.setItem(listItem);
					re.setNumber(1);
				}
				
			}else{

				
				List<MDataMap> listMaps = DbUp.upTable("pc_search_recommend_goods")
						.queryAll("", "-product_num,-zid", "",
								new MDataMap("product_appcode", sellercode));

				List<String> productCodes = new ArrayList<String>();
				for (MDataMap mDataMap : listMaps) {
					productCodes.add(mDataMap.get("product_code"));
				}

				ProductService ps = new  ProductService();
				MDataMap productSalesMapHjy =  ps.getProductFictitiousSales(sellercode,productCodes,30);
				int number=0;
				String sProducts = StringUtils.join(productCodes, ",");
				if (StringUtils.isNotEmpty(sProducts)) {
					
					Map<String,BigDecimal> sSellerPrice = productService.getMinProductActivityNew(productCodes,buyerType);
					
					
					Map<String, MDataMap> mapProduct = MapHelper.restoreListMap(
							DbUp.upTable("pc_productinfo")
									.queryIn("", "",
											"product_status='4497153900060002' ",
											new MDataMap(), 0, 0, "product_code",
											sProducts), "product_code");

					Map<String, MDataMap> mapDescs = MapHelper.restoreListMap(

							DbUp.upTable("pc_productdescription").queryIn(
									"product_code,keyword", "", "", new MDataMap(),
									0, 0, "product_code", sProducts),
							"product_code");

					// 循环所有商品
					for (MDataMap mDataMap : listMaps) {

						if (listItem.size() < pageSize) {
							
							if (mapProduct
									.containsKey(mDataMap.get("product_code"))) {
								MDataMap mProduct = mapProduct.get(mDataMap
										.get("product_code"));
								//商品出于上架状态
								if(!"4497153900060002".equals(mProduct.get("product_status").trim())){
									continue;
								}
								
								Item i2 = new Item();
								i2.setProductCode(mDataMap.get("product_code"));
								i2.setProductName(mProduct.get("product_name"));
								i2.setOriginalPrice(new BigDecimal(mProduct.get("market_price")));
								i2.setImgUrl(mProduct.get("mainpic_url"));
								if(mProduct.get("small_seller_code")==null || mProduct.get("small_seller_code").equals("")){
									i2.setFlagTheSea("0");
								}else{
//									if(mProduct.get("small_seller_code").equals("SF03KJT")||mProduct.get("small_seller_code").equals("SF03MLG")
//											||mProduct.get("small_seller_code").equals("SF03100294")
//											||mProduct.get("small_seller_code").equals("SF03100327")
//											||mProduct.get("small_seller_code").equals("SF03100329")){
									if(new PlusServiceSeller().isKJSeller(mProduct.get("small_seller_code"))){
										i2.setFlagTheSea("1");
									}else{
										i2.setFlagTheSea("0");
									}
								}
								if( null == sSellerPrice.get(mDataMap.get("product_code")) || "".equals(sSellerPrice.get(mDataMap.get("product_code")))){
									i2.setCurrentPrice((mProduct.get("min_sell_price")==null || mProduct.get("min_sell_price").equals(""))?BigDecimal.ZERO:new BigDecimal(mProduct.get("min_sell_price")));
								}else{
									i2.setCurrentPrice(sSellerPrice.get(mDataMap.get("product_code").toString()));
								}

								String mapNumber =productSalesMapHjy.get(mDataMap.get("product_code"));
								if(StringUtils.isNotEmpty(mapNumber)){
									number=Integer.parseInt(mapNumber);
								}else{
									number=0;
								}
								i2.setProductNumber(number);
								
								tag = mapDescs.get(mDataMap.get("product_code"))
										.get("keyword");

								tagList = tag.split(",");
								sl = new ArrayList<String>();
								for (int m = 0; m < tagList.length; m++) {
									sl.add(tagList[m]);
								}
								i2.setTagList(sl);

								listItem.add(i2);

							}

						}
					}

					if(0==listItem.size()){
						re.setItem(null);
						re.setPager(null);
					}else{
						pager.setPageNo(1);
						pager.setPageNum(1);
						pager.setPageSize(10);
						pager.setRecordNum(listItem.size());
						re.setPager(pager);
						re.setItem(listItem);
					}
					re.setNumber(2); // product_num 都为O的情况下
				}else{ 
					re.setPager(null);
					re.setItem(null);
					re.setNumber(2);//推荐商品没有维护，查询不到商品信息
				}
			
				
			}
			
			
			
			
		}else{
			
				Map<String,Object> mapList = null;
				
				if(StringUtils.isNotEmpty(categoryOrBrand.trim())){
					mapList = SolrQueryUtil.getSearchSpg(keyWord, sortType,
							sortFlag, pageSize, pageNo, screenWidth, sellercode,categoryOrBrand);
					
				}else{
					mapList = SolrQueryUtil.getSearchSpg(keyWord, sortType,
							sortFlag, pageSize, pageNo, screenWidth, sellercode,null);
				}
				
				if (!mapList.isEmpty() &&  null!= mapList && !"".equals(mapList)){
					List<Product> list = (List<Product>) mapList.get("product");
					List<String> listProductCode = (List<String>)mapList.get("productCodeValue");
					List<String> listUrl = (List<String>)mapList.get("imgUrl");
					
					
					List<PicInfo> listImgUrl = productService.getPicInfoForMulti(screenWidth,listUrl);
					Map<String,PicInfo> picUrlMap = new HashMap<String, PicInfo>();
					for (PicInfo picInfo : listImgUrl) {
						picUrlMap.put(picInfo.getPicOldUrl(), picInfo);
					}
					
					Map<String,BigDecimal> sSellerPrice = productService.getMinProductActivityNew(listProductCode,buyerType);
					
					for (int i = 0; i < list.size(); i++) { 
						item = new Item();
						item.setProductCode(list.get(i).getProductCode());
						item.setFlagTheSea(list.get(i).getSmallSellerCode()+"");
						item.setProductName(list.get(i).getProductName());
						item.setImgUrl(picUrlMap.get(list.get(i).getMainpicUrl()) == null?"" :picUrlMap.get(list.get(i).getMainpicUrl()).getPicNewUrl());
						item.setOriginalPrice(new BigDecimal(list.get(i).getOriginalPrice()).setScale(2, RoundingMode.DOWN));
						item.setProductNumber(list.get(i).getProductNumber());
						if(list.get(i).getStockNum()==1){
							item.setStockNum(null==TopUp.upConfig("productcenter.available")?"有货":TopUp.upConfig("productcenter.available"));
						}else if(list.get(i).getStockNum()==0){
							item.setStockNum(null==TopUp.upConfig("productcenter.soldOut")?"售罄":TopUp.upConfig("productcenter.soldOut"));
						}
				
						
						
						if( null == sSellerPrice.get(list.get(i).getProductCode()) || "".equals(sSellerPrice.get(list.get(i).getProductCode()))){
							item.setCurrentPrice(new BigDecimal(list.get(i).getCurrentPrice()));
						}else{
							item.setCurrentPrice(sSellerPrice.get(list.get(i).getProductCode()));
						}
						

						tag = (null != list.get(i).getTagList()) ? list.get(i)
								.getTagList() : "";
						if (null == tag || "".equals(tag.trim())) {
							item.setTagList(null);
						} else {
							tagList = tag.split(",");
							sl = new ArrayList<String>();
							for (int k = 0; k < tagList.length; k++) {
								sl.add(tagList[k]);
							}
							item.setTagList(sl);
						}

						listItem.add(item);
					}
					
					
					long longSum =Long.parseLong(mapList.get("count").toString());
					pager.setPageNo(page);
					int lengNum = 0;
					if ((longSum % pageSize) == 0) {
						lengNum = (int) longSum / pageSize;
					} else {
						lengNum = (int) longSum / pageSize + 1;
					}

					pager.setPageNum(lengNum);
					pager.setPageSize(pageSize);
					pager.setRecordNum((int) longSum);
					re.setPager(pager);
					re.setItem(listItem);
					re.setNumber(1);
				} else {
					
					List<MDataMap> listMaps = DbUp.upTable("pc_search_recommend_goods")
							.queryAll("", "-product_num,-zid", "",
									new MDataMap("product_appcode", sellercode));

					List<String> productCodes = new ArrayList<String>();
					for (MDataMap mDataMap : listMaps) {
						productCodes.add(mDataMap.get("product_code"));
					}

					ProductService ps = new  ProductService();
					MDataMap productSalesMapHjy =  ps.getProductFictitiousSales(sellercode,productCodes,30);
					int number=0;
					String sProducts = StringUtils.join(productCodes, ",");
					if (StringUtils.isNotEmpty(sProducts)) {
						
						Map<String,BigDecimal> sSellerPrice = productService.getMinProductActivityNew(productCodes,buyerType);
						
						
						Map<String, MDataMap> mapProduct = MapHelper.restoreListMap(
								DbUp.upTable("pc_productinfo")
										.queryIn("", "",
												"product_status='4497153900060002' ",
												new MDataMap(), 0, 0, "product_code",
												sProducts), "product_code");

						Map<String, MDataMap> mapDescs = MapHelper.restoreListMap(

								DbUp.upTable("pc_productdescription").queryIn(
										"product_code,keyword", "", "", new MDataMap(),
										0, 0, "product_code", sProducts),
								"product_code");

						// 循环所有商品
						for (MDataMap mDataMap : listMaps) {

							if (listItem.size() < pageSize) {
								
								if (mapProduct
										.containsKey(mDataMap.get("product_code"))) {
									MDataMap mProduct = mapProduct.get(mDataMap
											.get("product_code"));
									//商品出于上架状态
									if(!"4497153900060002".equals(mProduct.get("product_status").trim())){
										continue;
									}
									
									Item i2 = new Item();
//									i2.setFlagTheSea(mProduct.get("small_seller_code").equals("SF03KJT")||mProduct.get("small_seller_code").equals("SF03MLG")
//											||mProduct.get("small_seller_code").equals("SF03100294")
//											||mProduct.get("small_seller_code").equals("SF03100327")
//											||mProduct.get("small_seller_code").equals("SF03100329")?"1":"0");
									i2.setFlagTheSea(new PlusServiceSeller().isKJSeller(mProduct.get("small_seller_code"))?"1":"0");
									i2.setProductCode(mDataMap.get("product_code"));
									i2.setProductName(mProduct.get("product_name"));
									i2.setOriginalPrice(new BigDecimal(mProduct
											.get("market_price")));
									i2.setImgUrl(mProduct.get("mainpic_url"));

									if( null == sSellerPrice.get(mDataMap.get("product_code")) || "".equals(sSellerPrice.get(mDataMap.get("product_code")))){
										i2.setCurrentPrice((mProduct.get("min_sell_price")==null || mProduct.get("min_sell_price").equals(""))?BigDecimal.ZERO:new BigDecimal(mProduct.get("min_sell_price")));
									}else{
										i2.setCurrentPrice(sSellerPrice.get(mDataMap.get("product_code")));
									}

									String mapNumber =productSalesMapHjy.get(mDataMap.get("product_code"));
									if(StringUtils.isNotEmpty(mapNumber)){
										number=Integer.parseInt(mapNumber);
									}else{
										number=0;
									}
									i2.setProductNumber(number);
									
									tag = mapDescs.get(mDataMap.get("product_code"))
											.get("keyword");

									tagList = tag.split(",");
									sl = new ArrayList<String>();
									for (int m = 0; m < tagList.length; m++) {
										sl.add(tagList[m]);
									}
									i2.setTagList(sl);

									listItem.add(i2);

								}

							}
						}

						if(0==listItem.size()){
							re.setItem(null);
							re.setPager(null);
						}else{
							pager.setPageNo(1);
							pager.setPageNum(1);
							pager.setPageSize(10);
							pager.setRecordNum(listItem.size());
							re.setPager(pager);
							re.setItem(listItem);
						}
						re.setNumber(2); // product_num 都为O的情况下
					}else{ 
						re.setPager(null);
						re.setItem(null);
						re.setNumber(2);//推荐商品没有维护，查询不到商品信息
					}
				}
			
		}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return re;
	}
}
