package com.cmall.productcenter.service.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cmall.productcenter.model.CategoryBrand;
import com.cmall.productcenter.model.Item;
import com.cmall.productcenter.model.Pager;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.SolrData;
import com.cmall.productcenter.model.api.ApiSearchResultsInput;
import com.cmall.productcenter.model.api.ApiSearchResultsResult;
import com.cmall.productcenter.service.ProductGiftsSearch;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.util.Base64Util;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.load.LoadProductSales;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductSales;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductLabelService;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.zapcom.basehelper.MapHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForVersion;

/**
 * 惠家有搜索最新返回 该版本基于solr5.2.1
 * 
 * @author zhouguohui
 * 
 */
public class ApiSearchResultsHjyNew extends
		RootApiForVersion<ApiSearchResultsResult, ApiSearchResultsInput> {

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
		BigDecimal minPrice = inputParam.getMinPrice();
		BigDecimal maxPrice = inputParam.getMaxPrice();
		String baseValue= inputParam.getBaseValue();
		String buyerType = inputParam.getBuyerType();
		String categoryOrBrand = inputParam.getCategoryOrBrand();
		String categoryCode = inputParam.getCategoryCode();
		String channelId = getChannelId();
		String memberCode = "";
		// 如果存在登录状态，则优先取登录状态中的用户编码
		if(getFlagLogin()){
			memberCode = getOauthInfo().getUserCode();
		}
		
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
		if(TopUp.upConfig("productcenter.jyhwebclient").equals("yes")){
		
			MDataMap dataMap = new MDataMap();
			dataMap.put("keyWord", keyWord);
			dataMap.put("sortType",sortType+"");
			dataMap.put("sortFlag",sortFlag+"");
			dataMap.put("pageNo", pageNo+"");
			dataMap.put("pageSize", pageSize+"");
			dataMap.put("minPrice",minPrice+"");
			dataMap.put("maxPrice",maxPrice+"");
			dataMap.put("sellercode",getManageCode());
			dataMap.put("categerkeyWord", inputParam.getCategerkeyWord());
			dataMap.put("brandKeyWord", inputParam.getBrandKeyWord());
			if(categoryOrBrand.equals("category")){
				dataMap.put("category","category");
				if(StringUtils.isNotBlank(getApiClient().get("app_vision")) && AppVersionUtils.compareTo(getApiClient().get("app_vision"), "5.3.2") >= 0) {
					dataMap.put("categoryCode",categoryCode);
				}
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
				List<String> categoryTwoName = new ArrayList<String>();
				List<String> brandName = new ArrayList<String>();
				long longSum = 0;
				if(!list.isEmpty()){
					ProductGiftsSearch pgs = new ProductGiftsSearch();
					ProductService ps = new ProductService();
					ProductLabelService productLabelService = new ProductLabelService();
					for(int i=0;i<list.size();i++){
						if(i==0){
							longSum= list.get(i).getCounts();
							categoryTwoName=list.get(i).getCategoryName();
							brandName=list.get(i).getBrandName();
						}
						item = new Item();
						
						Map<String, String> map = pgs.getProductGiftsSearch(list.get(i).getK1(), inputParam.getChannelId());
						String productCode = list.get(i).getK1();
						// 根据product code 在缓存中取商品信息  
						PlusModelProductInfo plusModelProductinfo = new PlusModelProductInfo(); 
						try {
							plusModelProductinfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
						} catch (Exception e) { 
							XmasKv.upFactory(EKvSchema.Product).del(productCode);
							plusModelProductinfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
						}
						Boolean lcflag = true;
						 // 抄底价商品 449747110001:否，449747110002:是  
						if(StringUtils.isNotBlank(plusModelProductinfo.getLowGood()) && plusModelProductinfo.getLowGood().equals("449747110002")){
							lcflag = false;
						}
						List<String> listOtherShow =null;
						if(lcflag && map!=null && !map.isEmpty()){
							listOtherShow = new ArrayList<String>();
							listOtherShow.add("赠品");
						}
						item.setProductCode(list.get(i).getK1());
						item.setProductName(list.get(i).getS9());
						PicInfo imgUrl = ps.getPicInfoOprBig(screenWidth, list.get(i).getS4());
						item.setImgUrl(imgUrl==null?"":imgUrl.getPicNewUrl());
						item.setOriginalPrice(new BigDecimal(list.get(i).getD1()).setScale(2, RoundingMode.HALF_UP));//市场价
						item.setCurrentPrice(new BigDecimal(list.get(i).getD2()).setScale(2, RoundingMode.HALF_UP));//销售价
						item.setFlagTheSea(list.get(i).getI3()==0?"0":"1");//是否海外购  1代表是  0代表不是
						item.setStockNum(list.get(i).getI1()==0?"抢光了":"有货");
						item.setProductNumber(list.get(i).getI2());//销量
						item.setOtherShow(listOtherShow);//赠品
						item.setTagList(list.get(i).getL6());//标签
						item.setActivityList(list.get(i).getL7());//闪购  内购  等标签
						
						/**添加商品标签**/
						PlusModelProductInfo productInfo = new LoadProductInfo().topInitInfo(new PlusModelProductQuery(list.get(i).getK1()));
						item.setLabelsPic(productLabelService.getLabelInfo(productInfo.getProductCode()).getListPic());
						if (null!=productInfo.getLabelsList() && productInfo.getLabelsList().size()>0) {
							item.setLabelsList(productInfo.getLabelsList());
						}
						
						//根据商品编号查询商品价格
						PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
						skuQuery.setCode(productCode);
						skuQuery.setMemberCode(memberCode);
						skuQuery.setChannelId(channelId);
						skuQuery.setIsPurchase(1);
						Map<String,BigDecimal> moneyMap = new ProductPriceService().getProductMinPrice(skuQuery);
						if(moneyMap.containsKey(productCode)) {
							item.setCurrentPrice(moneyMap.get(productCode).setScale(2, RoundingMode.HALF_UP));
						}
						
						
						listItem.add(item);
					}
					CategoryBrand cb = new CategoryBrand();
					cb.setBrandName(brandName);
					cb.setCategoryTwoName(categoryTwoName);
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
					re.setKeyWord(keyWord);
					re.setMaxPrice(maxPrice);
					re.setMinPrice(minPrice);
					re.setPager(pager);
					re.setCategoryBrand(cb);
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
					PlusSupportStock pss= new PlusSupportStock();
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
									if(mProduct.get("small_seller_code").equals("SF03KJT")){
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
								
								tag = mapDescs.get(mDataMap.get("product_code"))
										.get("keyword");

								tagList = tag.split(",");
								sl = new ArrayList<String>();
								for (int m = 0; m < tagList.length; m++) {
									sl.add(tagList[m]);
								}
								i2.setTagList(sl);
								
								PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(mDataMap.get("product_code").toString());		
								PlusModelProductSales productSalesValue = new LoadProductSales().upInfoByCode(plusModelProductQuery);
								
								i2.setProductNumber(productSalesValue.getFictitionSales30());//销量
								i2.setStockNum(pss.upAllStockForProduct(mDataMap.get("product_code").toString())<=0?"抢光了":"有货" );//是否有货      
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
