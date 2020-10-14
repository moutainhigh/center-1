package com.cmall.productcenter.service.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cmall.productcenter.model.BigContent;
import com.cmall.productcenter.model.DetailContent;
import com.cmall.productcenter.model.Item;
import com.cmall.productcenter.model.Pager;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.SolrData;
import com.cmall.productcenter.model.api.ApiSearchResultsInput;
import com.cmall.productcenter.model.api.ApiSearchResultsResult;
import com.cmall.productcenter.service.ProductGiftsSearch;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.util.Base64Util;
import com.srnpr.xmasorder.model.TagInfo;
import com.srnpr.xmasproduct.api.ApiSkuInfo;
import com.srnpr.xmasproduct.model.SkuInfos;
import com.srnpr.xmassystem.Constants;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.modelevent.PlusModelEventInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductLabel;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.xmassystem.service.ProductLabelService;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.support.PlusSupportEvent;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.MapHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForVersion;

/**
 * 搜索结果返回|app端在使用
 * 
 * @author zhouguohui
 * 
 */
public class ApiSearchResults extends
		RootApiForVersion<ApiSearchResultsResult, ApiSearchResultsInput> {

	@SuppressWarnings({ "unused", "rawtypes" })
	public ApiSearchResultsResult Process(ApiSearchResultsInput inputParam,
			MDataMap mRequestMap) {
		if(StringUtils.isBlank(inputParam.getBuyerType()))
		{
			inputParam.setBuyerType("4497469400050002");
		}
		ProductService productService = new ProductService();
		ApiSearchResultsResult re = new ApiSearchResultsResult();
		Integer isPurchase = inputParam.getIsPurchase();
		/**添加列表显示的默认样式 ：1、小图列表；2、大图列表***/
		re.setStyleValue(Integer.parseInt(TopUp.upConfig("productcenter.styleValue")));
		String sellercode = getManageCode();
		List<Item> listItem = new ArrayList<Item>();
		List<String> sl = null;
		Item item = null;
		Pager pager = new Pager();
		String keyWord = null;
		BigDecimal minPrice = inputParam.getMinPrice();
		BigDecimal maxPrice = inputParam.getMaxPrice();
		String baseValue= inputParam.getBaseValue();
		String buyerType = inputParam.getBuyerType();
		String isMemberCode = getFlagLogin() ? getOauthInfo().getUserCode() : "";//用户编号
		String categoryCode = inputParam.getCategoryCode();
		String couponTypeCode = inputParam.getCouponTypeCode();
		String channelId = getChannelId();
		String jyfw = inputParam.getJyfw();
		MDataMap transfer = transfer(jyfw);
		String hasPromotion = transfer.get("hasPromotion");
		String isLD =transfer.get("isLD");
		/**
		 * 版本控制 3.7.0 以后的数据加密为：base64
		 */
		if(baseValue.equals("base64")){
			keyWord = (null==Base64Util.getFromBASE64(inputParam.getKeyWord()))?"": Base64Util.getFromBASE64(inputParam.getKeyWord());
		}else{
			keyWord= inputParam.getKeyWord();
		}
		//校验keyword
		if(StringUtils.isEmpty(keyWord)&&StringUtils.isEmpty(couponTypeCode)&&StringUtils.isEmpty(categoryCode)) {
			re.setResultCode(916423700);
			re.setResultMessage(bInfo(916423700));
			return re;
		}
		
		keyWord = StringUtils.trimToEmpty(keyWord);
		
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
		String categoryOrBrand = inputParam.getCategoryOrBrand();
		int screenWidth = Constants.IMG_WIDTH_SP02;
		
		String tag = null;
		String[] tagList = null;
			
		MDataMap dataMap = new MDataMap();
		dataMap.put("keyWord", keyWord);
		dataMap.put("sortType",sortType+"");
		dataMap.put("sortFlag",sortFlag+"");
		dataMap.put("pageNo", pageNo+"");
		dataMap.put("pageSize", pageSize+"");
		dataMap.put("sellercode",getManageCode());
		dataMap.put("minPrice",minPrice+"");
		dataMap.put("maxPrice",maxPrice+"");
		dataMap.put("couponTypeCode", couponTypeCode);
		//小程序临时兼容
		dataMap.put("os",AppVersionUtils.compareTo(
					null == getApiClient().get("app_vision") ? "" : getApiClient().get("app_vision"),"5.1.4") == 0 
					? "weapphjy" : (null == getApiClient().get("os") ? "" : getApiClient().get("os")));
		if(!"0".equals(isLD)) {
			dataMap.put("isLD", isLD);
		}
		if(!"0".equals(hasPromotion)) {
			dataMap.put("hasPromotion", hasPromotion);
		}
					
		if(categoryOrBrand.equals("category")){
			dataMap.put("category","category");
			if(StringUtils.isNotBlank(getApiClient().get("app_vision")) && AppVersionUtils.compareTo(getApiClient().get("app_vision"), "5.3.2") >= 0) {
				//商品分类前后台分离功能改动
				//传入的商品类型为前台，转换为后台类型
				String[] split = categoryCode.split(",");
				List<String> temp = new ArrayList<String>();
				for (String s : split) {
					temp.add("'" + s + "'");
				}
							
				String categorySql = "select * from uc_sellercategory_pre where seller_code = '" + sellercode 
						+ "'" + " and category_code in (" + StringUtils.join(temp, ",") + ") and flaginable = '449746250001'";
				List<Map<String, Object>> categoryList = DbUp.upTable("uc_sellercategory_pre").dataSqlList(categorySql, new MDataMap());
							
				if(null != categoryList && !categoryList.isEmpty()) {
					List<String> category4List = new ArrayList<String>();
					Map<String,String> allCategory = new HashMap<String, String>(); //category_code,category_type
					for (Map<String, Object> categoryMap : categoryList) {
	
						String level = MapUtils.getString(categoryMap, "level", "");
						if ("3".equals(level)) {
							String threeCategorySql = "select * from uc_sellercategory_pre where seller_code = '"
									+ sellercode + "'" + " and parent_code = '" + MapUtils.getString(categoryMap, "category_code", "") + "' and flaginable = '449746250001'";
										
							List<Map<String, Object>> fourCategoryList = DbUp.upTable("uc_sellercategory_pre")
									.dataSqlList(threeCategorySql, new MDataMap());
										
							for (int i = 0; i < fourCategoryList.size(); i++) {
								allCategory.put(MapUtils.getString(fourCategoryList.get(i), "category_code",""), MapUtils.getString(fourCategoryList.get(i), "category_type",""));
							}
						} else {
							allCategory.put(MapUtils.getString(categoryMap, "category_code", ""), MapUtils.getString(categoryMap, "category_type", ""));
						}
					}
					if(!allCategory.isEmpty()) {
						List<String> rearCategorys = new ArrayList<String>();
						List<String> frontCategorys = new ArrayList<String>();
						List<String> propertiesCodes = new ArrayList<String>();
						for (String sCategoryCode : allCategory.keySet()) {
							//商品分类
							if("449748510001".equals(allCategory.get(sCategoryCode))) rearCategorys.add(sCategoryCode);
							//添加商品
							else if("449748510002".equals(allCategory.get(sCategoryCode))) frontCategorys.add(sCategoryCode);
							//维护属性值
							else if("449748510003".equals(allCategory.get(sCategoryCode))) {
								List<MDataMap> propertiesList = DbUp.upTable("uc_sellercategory_pre_properties_value").queryByWhere("category_code", sCategoryCode);
								if(null != propertiesList && propertiesList.size() > 0) {
									for (MDataMap propertiesMap : propertiesList) {
										propertiesCodes.add(propertiesMap.get("properties_value_code"));
									}
								}
							}
						}
						if(rearCategorys.size() > 0) dataMap.put("rearCategoryCode", StringUtils.join(rearCategorys, ","));
						if(frontCategorys.size() > 0) dataMap.put("frontCategoryCode", StringUtils.join(frontCategorys, ","));
						if(propertiesCodes.size() > 0) dataMap.put("propertiesCode", StringUtils.join(propertiesCodes, ","));
					}
				}
							
			}
		}else if(categoryOrBrand.equals("brand")){
			dataMap.put("brand","brand");
		}else if(categoryOrBrand.equals("top50")){
			dataMap.put("top50","top50");
		}else{
			dataMap.put("key","key");
		}
		
		if("key".equals(dataMap.get("key"))) {
			if(keyWord.length() > 150) {
				keyWord = keyWord.substring(0,150);
			}
			MDataMap logMap = new MDataMap();
			logMap.put("member_code", isMemberCode);
			logMap.put("os", StringUtils.trimToEmpty(getApiClient().get("os")));
			logMap.put("app_vision", StringUtils.trimToEmpty(getApiClient().get("app_vision")));
			logMap.put("keyword", keyWord);
			logMap.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_search_log").dataInsert(logMap);
		}
				
		try {
			String pro = WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturlselect"), dataMap);
			if(pro!=null && !pro.equals("")){
				List<SolrData> list = JSON.parseObject(pro,new TypeReference<List<SolrData>>(){}); 
				long longSum = 0;
				if(!list.isEmpty()){
					ProductGiftsSearch pgs = new ProductGiftsSearch();
					ProductService ps = new ProductService();
					ProductLabelService productLabelService = new ProductLabelService();
					for(int i=0;i<list.size();i++){
						if(i==0){
							longSum= list.get(i).getCounts();
						}
						item = new Item();
						/**添加主播按钮**/
						if(inputParam.getHostessId()==1){
							item.setHostessButton(1);
						}else{
								item.setHostessButton(0);
						}
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
						item.setOriginalPrice(new BigDecimal(list.get(i).getD1()).setScale(2, RoundingMode.HALF_UP));//市场价
						item.setCurrentPrice(new BigDecimal(list.get(i).getD2()).setScale(2, RoundingMode.HALF_UP));//销售价
						item.setFlagTheSea(list.get(i).getI3()==0?"0":"1");//是否海外购  1代表是  0代表不是
						item.setStockNum(list.get(i).getI1()==0?"抢光了":"有货");
						item.setProductNumber(list.get(i).getI2());//销量
						item.setTagList(list.get(i).getL6());//标签
						item.setOtherShow(listOtherShow);//赠品
						item.setActivityList(list.get(i).getL7());//闪购  内购  等标签
							
						//524：添加商品分类标签
						PlusModelProductInfo productInfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(list.get(i).getK1()));
						String ssc =productInfo.getSmallSellerCode();
						String st="";
						if("SI2003".equals(ssc)) {
							st="4497478100050000";
						}
						else {
							st = WebHelper.getSellerType(ssc);
						}
							
						if(StringUtils.isNotBlank(productInfo.getMainpicUrl())) {
							PicInfo imgUrl = ps.getPicInfoOprBig(screenWidth, productInfo.getMainpicUrl());
							item.setImgUrl(imgUrl==null?"":imgUrl.getPicNewUrl());
						}
							
						//获取所属商品字段值：map中存放的为商品分类的列表标签，和详情标签
						Map productTypeMap = WebHelper.getAttributeProductType(st);
						item.setProClassifyTag(productTypeMap.get("proTypeListPic").toString());
						// 546添加,返回商品列表标签的宽高
						item.setProClassifyTagH(productTypeMap.get("proTypeListPicHeight").toString());
						item.setProClassifyTagW(productTypeMap.get("proTypeListPicWidth").toString());
						/**添加商品标签**/
						item.setLabelsPic(productLabelService.getLabelInfo(productInfo.getProductCode()).getListPic());
						if (null!=productInfo.getLabelsList() && productInfo.getLabelsList().size()>0) {
							item.setLabelsList(productInfo.getLabelsList());
						}
							
						//5.5.8增加所有sku实际库存 前端用于库存提示
						int allSkuRealStock = new PlusSupportStock().upAllStockForProduct(list.get(i).getK1());
						item.setAllSkuRealStock(allSkuRealStock);

						listItem.add(item);
					}	
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
							
			}else{
				
				List<String> productCodes = new ArrayList<String>();
				
				List<MDataMap> recommendGoods = DbUp.upTable("pc_search_recommend_goods")
						.queryAll("", "-product_num,-zid", "", new MDataMap("product_appcode", sellercode));
				for (MDataMap mDataMap : recommendGoods) {
					if(!productCodes.contains(mDataMap.get("product_code"))) {
						productCodes.add(mDataMap.get("product_code"));
					}
				}
				
				//558 增加今日热卖商品
				String sSql = "SELECT e.product_code FROM pc_productsales_everyday e, pc_productinfo p "
						+ " WHERE e.`day` = :day and e.product_code = p.product_code and e.zid > :startZid "
						+ " and p.product_status = '4497153900060002' "
						+ " ORDER BY sales desc ";
				MDataMap paraMap = new MDataMap();
				paraMap.put("day", FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1),"yyyy-MM-dd"));
				paraMap.put("startZid", bConfig("familyhas.start_zid"));
				
				List<Map<String, Object>> hotSaleList = DbUp.upTable("pc_productinfo").dataSqlList(sSql, paraMap);
				for (Map<String, Object> map : hotSaleList) {
					if(!productCodes.contains(MapUtils.getString(map, "product_code"))) {
						productCodes.add(MapUtils.getString(map, "product_code"));
					}
				}
				
				ProductService ps = new  ProductService();
				int number=0;
				String sProducts = StringUtils.join(productCodes, ",");
				if (StringUtils.isNotEmpty(sProducts)) {
					Map<String, MDataMap> mapProduct = MapHelper.restoreListMap(
							DbUp.upTable("pc_productinfo")
									.queryIn("", "",
											"product_status='4497153900060002' ",
											new MDataMap(), 0, 0, "product_code",
											sProducts), "product_code");
					
					//先循环一遍 过滤掉未上架、无库存的商品
					Iterator<String> iterator = productCodes.iterator();
					while(iterator.hasNext()) {
						String productCode = iterator.next();
						//不存在的商品移除
						if (!mapProduct.containsKey(productCode)) { 
							iterator.remove();
							continue;
						}
						//无库存的商品移除
						if(new PlusSupportStock().upAllStockForProduct(productCode) <= 0) { 
							iterator.remove();
							continue;
						}
					}
					
					//只取100件商品
					if(productCodes.size() > 100) { productCodes.subList(0, 100);}
					
					if(productCodes.size() > 0) {
						// 处理分页
						pager.setRecordNum(productCodes.size());
						if(pageNo < productCodes.size()) {
							if((pageNo + pageSize) <= productCodes.size()) {
								productCodes = productCodes.subList(pageNo, pageNo + pageSize);
							}else {
								productCodes = productCodes.subList(pageNo, productCodes.size());
							}
						}
						
						MDataMap productSalesMapHjy =  ps.getProductFictitiousSales("SI2003",productCodes,30);
						
						sProducts = StringUtils.join(productCodes, ",");
						
						PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
						skuQuery.setCode(sProducts);
						skuQuery.setMemberCode(getFlagLogin() ? getOauthInfo().getUserCode() : "");
						skuQuery.setIsPurchase(isPurchase);
						Map<String, PlusModelSkuInfo> sSellerPrice = new ProductPriceService().getProductMinPriceIncloudGroupPrice(skuQuery);// 获取商品最低销售价格
						
						Map<String, MDataMap> mapDescs = MapHelper.restoreListMap(
								
								DbUp.upTable("pc_productdescription").queryIn(
										"product_code,keyword", "", "", new MDataMap(),
										0, 0, "product_code", sProducts),
								"product_code");
						
						// 封装商品信息
						for (String productCode : productCodes) {
							
							MDataMap mProduct = mapProduct.get(productCode);
							
							
							Item i2 = new Item();
							/**添加主播按钮**/
							if(inputParam.getHostessId()==1){
								i2.setHostessButton(1);
							}else{
								i2.setHostessButton(0);
							}
							i2.setProductCode(productCode);
							i2.setProductName(mProduct.get("product_name"));
							i2.setOriginalPrice(new BigDecimal(mProduct.get("market_price")).setScale(2, RoundingMode.DOWN));
							i2.setImgUrl(mProduct.get("mainpic_url"));
							if(mProduct.get("small_seller_code")==null || mProduct.get("small_seller_code").equals("")){
								i2.setFlagTheSea("0");
							}else{
								if(new PlusServiceSeller().isKJSeller(mProduct.get("small_seller_code"))){
									i2.setFlagTheSea("1");
								}else{
									i2.setFlagTheSea("0");
								}
							}
							if( null == sSellerPrice.get(productCode) || "".equals(sSellerPrice.get(productCode))){
								i2.setCurrentPrice((mProduct.get("min_sell_price")==null || mProduct.get("min_sell_price").equals(""))?BigDecimal.ZERO:new BigDecimal(mProduct.get("min_sell_price")));
							}else{
								i2.setCurrentPrice(sSellerPrice.get(productCode).getSellPrice());
							}
							
							String mapNumber =productSalesMapHjy.get(productCode);
							if(StringUtils.isNotEmpty(mapNumber)){
								number=Integer.parseInt(mapNumber);
							}else{
								number=0;
							}
							i2.setProductNumber(number);
							
							tag = mapDescs.get(productCode).get("keyword");
							sl = new ArrayList<String>();
							
							tagList = tag.split(",");
							for (int m = 0; m < tagList.length; m++) {
								sl.add(tagList[m]);
							}

							PlusModelProductInfo productInfo = new LoadProductInfo().topInitInfo(new PlusModelProductQuery(productCode));
							
							i2.setTagList(sl);
							i2.setLabelsList(productInfo.getLabelsList());
							listItem.add(i2);
							
						}
					}
					
					if(0==listItem.size()){
						re.setItem(null);
						re.setPager(null);
					}else{
						pager.setPageNo(page);
						pager.setPageNum((pager.getRecordNum() % pageSize) == 0 ? (int) pager.getRecordNum() / pageSize : (int) pager.getRecordNum() / pageSize + 1);
						pager.setPageSize(pageSize);
						re.setPager(pager);
						re.setItem(listItem);
					}
					re.setNumber(2);
				}else{ 
					re.setPager(null);
					re.setItem(null);
					re.setNumber(2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//把筛选下内容返给前端
		//demo:家有服务:1-家有自营,1-促销商品,1-非促销商品;价格区间:2-最低价&最高价
		String upConfig = TopUp.upConfig("productcenter.allSelectContents");
		String[] split = upConfig.split(";");
		List<BigContent> lists = new ArrayList<>();
		for(String str : split) {
			BigContent bigContent = new BigContent();
			String[] split2 = str.split(":");
			bigContent.setTitle(split2[0]);
			List<DetailContent> contents = new ArrayList<DetailContent>();
			String[] split3 = split2[1].split(",");
			for(String str2 : split3) {
				DetailContent detailContent = new DetailContent();
				String[] split4 = str2.split("-");
				detailContent.setType(split4[0]);
				detailContent.setText(split4[1]);
				contents.add(detailContent);
			}
			bigContent.setContents(contents);
			lists.add(bigContent);
		}
		re.setLists(lists);
		//新增拼团标识
		//只有5.4.0之后版本走此逻辑。
		String appVersion = StringUtils.trimToEmpty(getApiClient().get("app_vision"));
		if(StringUtils.isEmpty(appVersion)) {
			appVersion = "5.4.2";
		}
		
		if(AppVersionUtils.compareTo(appVersion,"5.4.0")>=0 && re.getItem() != null){//当版本号高于或等于5.4.0的时候才会执行以下代码，添加拼团标识
			List<Item> items = re.getItem();
			
			for(Item itemEntity : items){
				String productCode = itemEntity.getProductCode();
				//根据商品编号查询商品所参与的活动
				PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
				skuQuery.setCode(productCode);
				skuQuery.setMemberCode(isMemberCode);
				skuQuery.setChannelId(channelId);
				//判断是否为分销商品
				/*if("449747430023".equals(channelId)) {
					RootResult rootResult = new RootResult();
					checkIfFXProduct(productCode,rootResult);
					if(1==rootResult.getResultCode()) {
						skuQuery.setFxFlag("1");
						Map<String, BigDecimal>  temMap = new ProductPriceService().getProductMinPrice(skuQuery);
						BigDecimal bigDecimal = temMap.get(productCode);
						itemEntity.setCurrentPrice(bigDecimal.subtract(new BigDecimal(rootResult.getResultMessage())));
						List<String> tagLists = new ArrayList<>();
						tagLists.add("优惠券￥"+rootResult.getResultMessage()+"元");
						itemEntity.setTagList(tagLists);
						itemEntity.setProductType("4497472000050002");
						continue;
						}
				}*/
				Map<String,PlusModelSkuInfo> map = new ProductPriceService().getProductMinPriceIncloudGroupPrice(skuQuery);
				PlusModelSkuInfo skuInfo = map.get(productCode);
				if("4497472600010024".equals(skuInfo.getEventType())){//拼团单
					itemEntity.setProductType("4497472000050001");
					itemEntity.setGroupBuying("4497472600010024");
					itemEntity.setGroupBuyingPrice(skuInfo.getGroupBuyingPrice());//设置拼团价
					itemEntity.setSkuPrice(skuInfo.getSkuPrice());
					String eventCode = skuInfo.getEventCode();
					PlusModelEventInfo eventInfo = new PlusSupportEvent().upEventInfoByCode(eventCode);
					String collagePersonCount = eventInfo.getCollagePersonCount();//拼团人数
					itemEntity.setCollagePersonCount(collagePersonCount);
				}else{
					itemEntity.setProductType("4497472000050002");//不是拼团单
				}
				itemEntity.setCurrentPrice(skuInfo.getSellPrice());
				
				// 如果有活动则需要展示划线价
				if(skuInfo.getSellPrice().compareTo(skuInfo.getSkuPrice()) < 0) {
					itemEntity.setSkuPrice(skuInfo.getSkuPrice());
				}
				
				//542需求，新增字段展示活动标签
				ProductService ps = new ProductService();
				List<String> tags = ps.getTagListByProductCode(productCode,isMemberCode,channelId);
				itemEntity.setTagList(tags);
				
				if(StringUtils.isBlank(appVersion) || AppVersionUtils.compareTo("5.5.80", appVersion) <= 0) {
					List<TagInfo> tagInfoList = ps.getProductTagInfoList(productCode, isMemberCode, channelId);
					itemEntity.setTagInfoList(tagInfoList);
				}
			}
		}
		//542增加优惠券搜索
		re.setCouponTypeCode(couponTypeCode);
		
		//558需求增加商品标签自定义位置
		for(Item info:re.getItem()){
			info.setLabelsInfo(new ProductLabelService().getLabelInfoList(info.getProductCode()));
			//562版本对于商品列表标签做版本兼容处理
			if(appVersion.compareTo("5.6.2")<0){
				Iterator<PlusModelProductLabel> iter = info.getLabelsInfo().iterator();
				while (iter.hasNext()) {
					PlusModelProductLabel plusModelProductLabel = (PlusModelProductLabel) iter.next();
					if(plusModelProductLabel.getLabelPosition().equals("449748430005")){
						iter.remove();
					}
				}
			}
		}
		return re;
	}
	
	public MDataMap transfer(String jyfw) {
		String hasPromotion = "0";
		String isLD ="0";
		String[] split5 = jyfw.split(",");
		for(String str : split5) {
			if("家有自营".equals(str)) {
				isLD =  "4497471600380001";
			}else if ("促销商品".equals(str)) {
				if("135".equals(hasPromotion)) {
					continue;
				}else {
					if("4497471600380004".equals(hasPromotion)) {
						hasPromotion = "135";
					}else {
						hasPromotion = "4497471600380003";
					}
				}
			}else if("用券商品".equals(str)) {
				if("135".equals(hasPromotion)) {
					continue;
				}else {
					if("4497471600380003".equals(hasPromotion)) {
						hasPromotion = "135";
					}else {
						hasPromotion = "4497471600380004";
					}
				}
			}
		}
		MDataMap mData = new MDataMap();
		mData.put("isLD", isLD);
		mData.put("hasPromotion", hasPromotion);
		return mData;
	}
	
	private void checkIfFXProduct(String productCode, RootResult rootResult) {
		// TODO Auto-generated method stub
		    rootResult.setResultCode(0);
			List<Map<String, Object>> listMap= DbUp.upTable("oc_activity").dataSqlList("select * from oc_activity where activity_type='449715400008' and flag=1 and begin_time<=now() and end_time>now() order by zid desc", null);
		    if(listMap!=null&&listMap.size()>0) {
		    	Map<String, Object> map = listMap.get(0);
		    	 Map<String, Object> dataSqlOne = DbUp.upTable("oc_activity_agent_product").dataSqlOne("select * from oc_activity_agent_product where activity_code=:activity_code and produt_code=:produt_code and flag_enable=1 ",new MDataMap("activity_code",map.get("activity_code").toString(),"produt_code",productCode));
			     if(dataSqlOne!=null) {
			    	 String coupon_money = dataSqlOne.get("coupon_money").toString();
			    	 rootResult.setResultCode(1);
			    	 rootResult.setResultMessage(coupon_money);
			     }
		    }
	}
	
	/**
	 * 返回结果为 true 的话，则过滤
	 * @param productCode
	 * @return
	 * 2020年7月6日
	 * Angel Joy
	 * boolean
	 */
	private boolean checkIfIgnoreCategoey(String productCode) {
		List<Map<String,Object>> mapList = DbUp.upTable("uc_program_del_category").dataSqlList("SELECT * FROM usercenter.uc_program_del_category",new MDataMap());
		List<String> categoryListFour = new ArrayList<String>();
		List<String> categoryListTwo = new ArrayList<String>();
		List<String> categoryListThree = new ArrayList<String>();
		for(Map<String,Object> map : mapList) {
			String lvl = MapUtils.getString(map,"level","4");
			if("4".equals(lvl) && StringUtils.isNotEmpty(MapUtils.getString(map,"category_code",""))) {
				categoryListFour.add( MapUtils.getString(map,"category_code",""));
			}
			if("3".equals(lvl) && StringUtils.isNotEmpty(MapUtils.getString(map,"category_code",""))) {
				categoryListThree.add("'" +MapUtils.getString(map,"category_code","")+"'");
			}
			if("2".equals(lvl) && StringUtils.isNotEmpty(MapUtils.getString(map,"category_code",""))) {
				categoryListTwo.add("'"+MapUtils.getString(map,"category_code","")+"'");
			}
		}
		if(categoryListTwo.size() > 0) {
			String sql = "SELECT category_code FROM usercenter.uc_sellercategory_pre where parent_code in ("+StringUtils.join(categoryListTwo, ",")+") AND flaginable = '449746250001' AND level = '3'";
			List<Map<String,Object>> mapThreeList = DbUp.upTable("uc_sellercategory_pre").dataSqlList(sql, new MDataMap());
			for(Map<String,Object> map : mapThreeList) {
				if(StringUtils.isNotEmpty(MapUtils.getString(map,"category_code",""))) {
					categoryListThree.add("'"+MapUtils.getString(map,"category_code","")+"'");
				}
			}
		}
		if(categoryListThree.size() > 0) {
			String sql = "SELECT category_code FROM usercenter.uc_sellercategory_pre where parent_code in ("+StringUtils.join(categoryListThree, ",")+") AND flaginable = '449746250001' AND level = '4'";
			List<Map<String,Object>> mapThreeList = DbUp.upTable("uc_sellercategory_pre").dataSqlList(sql, new MDataMap());
			for(Map<String,Object> map : mapThreeList) {
				if(StringUtils.isNotEmpty(MapUtils.getString(map,"category_code",""))) {
					categoryListFour.add(MapUtils.getString(map,"category_code",""));
				}
			}
		}
		MDataMap map = DbUp.upTable("uc_sellercategory_product_relation").one("product_code",productCode);
		String category_code = map != null&&!map.isEmpty()?map.get("category_code"):"";
		if(categoryListFour.contains(category_code)) {
			return true;
		}
		return false;
	}
	
}
