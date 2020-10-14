package com.cmall.groupcenter.kjt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigGetKlProductIdByDate;
import com.cmall.groupcenter.service.RsyncKLtoHJYSkuInfoService;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductinfoExt;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 定时同步考拉商品信息
 * @author Administrator
 *
 */

public class RsyncGetKlProductsInfo 
		extends RsyncKl {

	final static RsyncConfigGetKlProductIdByDate CONFIG_GET_TV_BY_DATE = new RsyncConfigGetKlProductIdByDate();
	final static int qNum = 5;//每次查询的数量
	final static String klProDown = "0";//考拉商品下架不可卖
	final static String klProUp = "1";//考拉商品上架可卖
	final static String rsyncProType = "3";//一般贸易商品类型标识
	final static String downStatus = "4497153900060003";//商品状态为下架
	final static String noProInfoReturnStr = "\"recCode\":-102";//无考拉商品信息返回标识字符串
	final static  String KL_Pro_Head = "8016";
	final static  String KL_Sku_Head = "8019";
	final static  long NODataLong = 0;
	final static  String NOData = "";//考拉接口不存在赋值为空串，以防属性拿不到
	public RsyncConfigGetKlProductIdByDate upConfig() {
		return CONFIG_GET_TV_BY_DATE;
	}

	public RsyncResult doProcess() {
		RsyncResult result = new RsyncResult();
        //调用封装的考拉接口进行数据获取(只同步一般贸易importType:3)
		TreeMap<String,String> treeMap = new TreeMap<String, String>();
		String string = RsyncKaoLaSupport.doPostRequest("queryAllGoodsIdAndSkuId","channelId",treeMap);
		JSONObject jb = JSON.parseObject(string);
		
		if(jb == null || jb.getIntValue("recCode") != 200){
			return result;
		}
		
		//获取考拉接口传递的skuid
		JSONObject goodsInfoList = jb.getJSONObject("goodsInfo");
		
		List<List<String>> listSkuGroup = new ArrayList<List<String>>();
		// 同一个商品的分到一个组
		JSONArray skus;
		Set<String> keys = goodsInfoList.keySet();
		for (String k : keys) {
			skus = goodsInfoList.getJSONArray(k);
			listSkuGroup.add(Arrays.asList(skus.toArray(new String[0])));
		}
		
		// 根据考拉商品编号分组一下考拉返回的SKU数据，处理时以商品为单位
		Map<String,List<JSONObject>> goodsGroupMap = new HashMap<String, List<JSONObject>>();
		
		List<String> tempSkuIdList = new ArrayList<String>();
		for (int i = 0; i < listSkuGroup.size(); i++) {
			tempSkuIdList.addAll(listSkuGroup.get(i));
			
			// 如果不是最后一个则预判下一次循环的数量，每次请求尽量保持接近而不超过20个
			if(((i+1) < listSkuGroup.size()) && tempSkuIdList.size() + listSkuGroup.get(i+1).size() <= 20){
				continue;
			}
			
			JSONArray klItemList = queryGoodsInfo(tempSkuIdList);
			JSONObject resultItem;
			JSONObject goodsInfo;
			for(int m = 0, n = klItemList.size(); m < n; m++){
				resultItem = klItemList.getJSONObject(m);
				
				if(resultItem.getIntValue("recCode") == 200){
					goodsInfo = resultItem.getJSONObject("goodsInfo");
					//拉取过来考拉已经上架且类型是一般贸易的商品进行同步，对下架商品进行惠家有产品查询，有则进行商品状态修改
					if(!rsyncProType.equalsIgnoreCase(goodsInfo.getString("importType"))){
						continue;
					}
					
					if(!goodsGroupMap.containsKey(goodsInfo.getString("goodsId"))){
						goodsGroupMap.put(goodsInfo.getString("goodsId"), new ArrayList<JSONObject>());
					}
					
					goodsGroupMap.get(goodsInfo.getString("goodsId")).add(goodsInfo);
				}
			}
			
			// 处理考拉商品数据
			saveProductData(goodsGroupMap);
			// 清空临时列表，开始下次循环
			tempSkuIdList.clear();
		}
	
		return result;
	}
	
	/**
	 * 批量查询SKU的信息
	 */
	private JSONArray queryGoodsInfo(List<String> skuIdList){
		int size = skuIdList.size();
		List<String> nextSkuIdList = new ArrayList<String>();
		
		//考拉接口请求参数封装
		TreeMap<String,String> treeMap = new TreeMap<String, String>();
		treeMap.put("queryType", "0");
		// 如果超过20个则需要递归请求一下
		if(size > 20){
			nextSkuIdList = skuIdList.subList(20, size);
			treeMap.put("skuIds", "[\""+StringUtils.join(skuIdList.subList(0, 20),"\",\"")+"\"]");
		}else{
			treeMap.put("skuIds", "[\""+StringUtils.join(skuIdList,"\",\"")+"\"]");
		}

		String proInfoStr =RsyncKaoLaSupport.doPostRequest("queryGoodsInfoByIds","channelId", treeMap);	

		if(StringUtils.isBlank(proInfoStr)){
			LogFactory.getLog(getClass()).warn("考拉接口调用失败 -> queryGoodsInfoByIds -> "+treeMap.get("skuIds"));
			return new JSONArray();
		}
		
		JSONArray klItemList = JSONArray.parseArray(proInfoStr);
		
		// 递归查询
		if(!nextSkuIdList.isEmpty()){
			klItemList.addAll(queryGoodsInfo(nextSkuIdList));
		}
		
		return klItemList;
	}
	
	/** 
	* @Description:查询商品信息，并入库
	*/
	private MWebResult saveProductData(Map<String,List<JSONObject>> goodsGroupMap) {
		MWebResult result = new MWebResult();
		
		// 开始循环处理结果数据
		Set<Entry<String, List<JSONObject>>> entryList = goodsGroupMap.entrySet();
		for(Entry<String, List<JSONObject>> entry : entryList){
			// 保存商品主表信息
			rsynProductData(entry.getValue());
			// 保存商品SKU信息
			RsyncKLtoHJYSkuInfoService.rsynSkuInfo(entry.getValue());
		}

		return result;
	}
	
	public MWebResult rsynProductData(List<JSONObject> itemList) {
		MWebResult result = new MWebResult();
		try {
			//处理jsonObject数据逻辑在此写
			PcProductinfo productinfo=new PcProductinfo();
			String productId = itemList.get(0).get("goodsId").toString();
			// 兼容调编情况下惠家有多个商品编号对应一个考拉编号的情况
			String sqlWhere = "product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
			List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere, new MDataMap("product_code_old", productId,"small_seller_code", TopConfig.Instance.bConfig("familyhas.seller_code_KL")));
			//添加一个商品下的其他sku的查询判断
			if(productMapList.isEmpty()){  //若果不存在，就添加
				setNewProductInfo(productinfo, itemList);//设置商品实体
				
				ProductService productService=new ProductService();
				StringBuffer error=new StringBuffer();
				int resultCode=productService.AddProductTx(productinfo, error,"");
				if(resultCode != 1){
					LogFactory.getLog(getClass()).warn("保存考拉商品失败["+productId+"]:"+error);
				}
				
				result.setResultCode(resultCode);
				result.setResultMessage(error.toString());
				
				//审批流添加
				this.rsyncKLProducToFlowMain(productinfo);
			}else {
				for(MDataMap productMap : productMapList){
					
					if(ArrayUtils.contains(new String[]{"4497153900060002","4497153900060003"}, productMap.get("product_status"))){
						// 判断考拉的商品名称是否变更
						if(!productMap.get("product_shortname").equalsIgnoreCase(itemList.get(0).getString("title"))){
							productMap.put("product_shortname", itemList.get(0).getString("title"));
							DbUp.upTable("pc_productinfo").dataUpdate(productMap, "product_shortname", "zid,product_code");
							
							//商品下架
//							String fromStatus= "4497153900060002";
//							String toStatus = "4497153900060003";
//							String flowType = "449715390006";
//							String userCode = "RsyncGetKlProductsInfo";
//							RootResult rs = new FlowBussinessService().ChangeFlow(productMap.get("uid"), flowType, fromStatus, toStatus, userCode, "考拉商品名称变更，原名称："+productMap.get("product_name"), new MDataMap());

//							if(rs.getResultCode() == 1){
//							}
						}
					}
				}
			}
		} catch (Exception e) {
			result.inErrorMessage(918519034,"同步商品出错");
			e.printStackTrace();
		}

		return result;
	}

	private void rsyncKLProducToFlowMain(PcProductinfo productinfo) {
	
		//进行审批流程添加
		String PAGEURL = "page_preview_v_pc_productDetailInfo?zw_f_product_code=";
		String pCode = productinfo.getProductCode();
		List<Map<String,Object>> lm =DbUp.upTable("sc_flow_main").dataSqlList("select * from sc_flow_main where outer_code=:outer_code", new MDataMap("outer_code",pCode));
			if(lm!=null&&lm.size()>0)  //审批流中已经存在，不做入流处理
				return; 
		//同步审批流
		MDataMap paramMap = new MDataMap();
		//String loginUser = UserFactory.INSTANCE.create().getUserCode();
		String createTime = DateUtil.getSysDateTimeString();
		paramMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
		paramMap.put("flow_code", WebHelper.upCode("SF"));
		paramMap.put("flow_type", "449717230016");
		//创建更新默认为考拉特定商户
		paramMap.put("creator",bConfig("familyhas.seller_code_KL"));
		paramMap.put("updator", bConfig("familyhas.seller_code_KL"));
		paramMap.put("create_time", createTime);
		paramMap.put("update_time", createTime);
		paramMap.put("outer_code", pCode);
		paramMap.put("flow_title", pCode);
		paramMap.put("flow_url", PAGEURL+pCode+"_1");
		paramMap.put("flow_remark", "");
		paramMap.put("flow_isend", "0");
		paramMap.put("current_status", "4497172300160003");
		paramMap.put("last_status", "4497172300160002");
		paramMap.put("next_operators", "46770318000100030005");
		paramMap.put("next_operator_status", "4677031800010001:4497172300160008;4677031800010001:4497172300160013");
		paramMap.put("next_operator_id", "");
		DbUp.upTable("sc_flow_main").dataInsert(paramMap);
		//商品流
		MDataMap productFlow = new MDataMap();
		JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
		productFlow.put("flow_code", WebHelper.upCode(ProductService.ProductFlowHead));
		productFlow.put("product_code", pCode);
		productFlow.put("product_json",pHelper.ObjToString(productinfo));
		productFlow.put("flow_status", SkuCommon.FlowStatusInit);
		productFlow.put("creator", bConfig("familyhas.seller_code_KL"));
		productFlow.put("create_time",DateUtil.getSysDateTimeString());
		productFlow.put("updator",bConfig("familyhas.seller_code_KL") );
		productFlow.put("update_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("pc_productflow").dataInsert(productFlow);
	}

	/** 
	* @Description:把商品信息赋值到待插入实体
	*/
	public void setNewProductInfo(PcProductinfo productinfo, List<JSONObject> itemList){
		JSONObject info = itemList.get(0); // 默认取第一个

		//商品名称（没有该属性，就用标题来替代）
		String productName = (info.get("title")==null?"":info.get("title")).toString().replaceAll("</?[^>]+>", "");//过滤html标签
		productinfo.setProductCode(WebHelper.upCode(KL_Pro_Head));
		String uid=UUID.randomUUID().toString().replace("-", "");
		productinfo.setUid(uid);
		
		productinfo.setSellProductcode((info.get("goodsId")==null?"":info.get("goodsId")).toString());
		productinfo.setProductCodeOld((info.get("goodsId")==null?"":info.get("goodsId")).toString());
		// 此字段暂时用来保存考拉方面的商品标题，惠家有修改商品名称时不更新这个字段
		productinfo.setProductShortname(productName);
		// 网易考拉的品牌ID跟惠家有的编号不匹配，需要重新在惠家有后台选择品牌
		//productinfo.setBrandCode((info.get("brandId")==null?"":info.get("brandId")).toString()); 
		productinfo.setBrandName((info.get("brandName")==null?"":info.get("brandName")).toString());
		productinfo.setProdutName(productName);
		// 关键字没有对应的字段可用暂时留空
		//productinfo.setLabels((info.get("brandName")==null?"":info.get("brandName")).toString());
		//最大最小价格不存在，默认为考拉价
		productinfo.setMaxSellPrice(BigDecimal.valueOf(Double.valueOf((info.get("suggestPrice")==null?"":info.get("suggestPrice")).toString())));
		productinfo.setMinSellPrice(BigDecimal.valueOf(Double.valueOf((info.get("suggestPrice")==null?"":info.get("suggestPrice")).toString())));
		//成本价设置为渠道进货价
		productinfo.setCostPrice(BigDecimal.valueOf(Double.valueOf((info.get("price")==null?"":info.get("price")).toString())));
		productinfo.setMainPicUrl((info.get("imageUrl")==null?"":info.get("imageUrl")).toString());	
		productinfo.setMarketPrice(BigDecimal.valueOf(Double.valueOf((info.get("marketPrice")==null?"":info.get("marketPrice")).toString())));
		String manageCode = MemberConst.MANAGE_CODE_HOMEHAS;
		productinfo.setSellerCode(manageCode);
		productinfo.setSmallSellerCode(bConfig("familyhas.seller_code_KL"));
		productinfo.setProductStatus("4497153900060001");//改为商品待上架状态
		productinfo.setValidate_flag("Y");//新增字段，是否是虚拟商品
		productinfo.setTaxRate(BigDecimal.valueOf(Double.valueOf("0.13")));
//		if("3".equals(info.get("importType"))) {
//			productinfo.setTaxRate(BigDecimal.valueOf(Double.valueOf("0.16")));
//		}else {
//			productinfo.setTaxRate(BigDecimal.valueOf(Double.valueOf((info.get("taxRate")==null?"0":info.get("taxRate")).toString())));
//		}
		productinfo.setProductWeight(new BigDecimal(NODataLong));
		
		// 商品图片使用固定https的域名
		//if(StringUtils.isNotBlank(productinfo.getMainPicUrl())) {
		//	int poi =  productinfo.getMainPicUrl().lastIndexOf("/");
		//	productinfo.setMainPicUrl(IMG_HOST + productinfo.getMainPicUrl().substring(poi));
		//}
		
		//运费模板字段:   1:网易考拉包邮  0：惠家有包邮
		//productinfo.setTransportTemplate("1".equals(info.get("isFreeShipping").toString())?"0":"1");
		productinfo.setTransportTemplate("0");
		
		//描述封装
		PcProductdescription productdescription=new PcProductdescription();
		productdescription.setUid(uid);
		productdescription.setProductCode(productinfo.getProductCode());
		String descriptionInfo = (info.get("detail")==null?"":info.get("detail")).toString().replaceAll("(<script).*(script>)", "");//过滤js
		descriptionInfo = descriptionInfo.replace("&lt;", "<").replace("&quot;", "\"").replace("&gt;", ">");
		productdescription.setDescriptionInfo(descriptionInfo);
	    //商品详情描述图片  
		List<String> goodsImagesList = JSONArray.parseArray((info.get("goodsImages")==null?"":info.get("goodsImages")).toString(), String.class);
		List<String> imageUrls = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String descPic ="";
		if(goodsImagesList!=null&&goodsImagesList.size()>0) {
			for (String string : goodsImagesList) {
				JSONObject jo = JSON.parseObject(string);
				String urlStr = jo.getString("imageUrl");
				imageUrls.add(urlStr);
				sb.append(urlStr+"|");
			}
		}
		//考拉商品描述图片字段校验    换取值
		//descPic=(sb.toString().substring(0,sb.toString().lastIndexOf("|")));
		descPic = this.jiequDescPicForHJY(descriptionInfo);
		//图片取值  ，待定
		productdescription.setDescriptionPic(descPic);
		productdescription.setKeyword("");
		
		productinfo.setDescription(productdescription);
		
		//图片列表封装   detail这个字段对应的是轮播？还是，与考拉对接人员商定
		if(imageUrls.size()>0) {
			for (String string : imageUrls) {
				PcProductpic pp=new PcProductpic();
				pp.setUid(UUID.randomUUID().toString().replace("-", ""));
				pp.setProductCode(productinfo.getProductCode());
				pp.setPicUrl(string);
				productinfo.getPcPicList().add(pp);
			}
		}
		
		// 商品属性
		JSONObject goodsProperty = info.getJSONObject("goodsProperty");
		if(goodsProperty != null){
			PcProductproperty pp = null;
			Set<String> keys = goodsProperty.keySet();
			for(String k : keys){
				pp = new PcProductproperty();
				pp.setType(2);
				pp.setPropertyType("449736200004");
				pp.setPropertyKey(k);
				pp.setPropertyValue(StringUtils.join(goodsProperty.getJSONArray(k).toArray()," "));
				productinfo.getPcProductpropertyList().add(pp);
			}
		}
		
		List<ProductSkuInfo> productSkuInfoList=new ArrayList<ProductSkuInfo>();
		JSONArray skuProperty;
		for(JSONObject item : itemList){
			//sku信息封装
			ProductSkuInfo productSkuInfo = new ProductSkuInfo();
			productSkuInfo.setSkuCode(WebHelper.upCode(KL_Sku_Head));
			//productSkuInfo.setSkuCodeOld(info.get("skuId").toString());
			productSkuInfo.setProductCode(productinfo.getProductCode());
			//接口中sku属性是不固定的,把查询出来的所属商品的固定属性进行sku属性封装（价格商品，sku同用）
			productSkuInfo.setSellPrice(BigDecimal.valueOf(Double.valueOf((item.get("suggestPrice")==null?"":item.get("suggestPrice")).toString())));
			productSkuInfo.setMarketPrice(BigDecimal.valueOf(Double.valueOf((item.get("marketPrice")==null?"":item.get("marketPrice")).toString())));
			productSkuInfo.setCostPrice(BigDecimal.valueOf(Double.valueOf((item.get("price")==null?"":item.get("price")).toString())));//设置sku的成本价
		   //提供的sku属性无确定的图片字段，赋值主图给sku
			productSkuInfo.setSkuPicUrl((item.get("imageUrl")==null?"":item.get("imageUrl")).toString());
			productSkuInfo.setSkuName(item.getString("title").replaceAll("</?[^>]+>", ""));//过滤html标签
			//设置外部商品id改为skuId
			productSkuInfo.setSellProductcode((item.get("skuId")==null?"":item.get("skuId")).toString());
			productSkuInfo.setStockNum(Integer.parseInt((item.get("store")==null?"":item.get("store")).toString()));
			productSkuInfo.setSaleYn(klProUp.equals(item.getString("onlineStatus")) ? "Y" : "N");//是否可卖为可买
			productSkuInfo.setFlagEnable("1");//是否可用为可用
			productSkuInfo.setSellerCode(manageCode);
			
			// 先初始为空字符串后面判断如果未匹配到内容再设置默认值
			String colorName = "";
			String styleName = "";
			
			skuProperty = item.getJSONArray("skuProperty");
			if(skuProperty != null && skuProperty.size() > 0){
				JSONObject obj;
				for(int i = 0,j = skuProperty.size(); i < j; i++){
					obj = skuProperty.getJSONObject(i);
					
					if("颜色".equals(obj.getString("propertyName"))){
						colorName = obj.getString("propertyValue").replaceAll("=", "").replaceAll("&", "");
					} else {
						// 除颜色外其他都归类于“尺码”属性
						if(!styleName.isEmpty()) {
							styleName += "/";
						}
						styleName += obj.getString("propertyValue").replaceAll("=","").replaceAll("&", "").trim();
					}
				}
			}
			
			// 默认值
			colorName = StringUtils.isBlank(colorName) ? "共同" : colorName;
			styleName = StringUtils.isBlank(styleName) ? "共同" : styleName;
			
			String colorKey = RsyncKLtoHJYSkuInfoService.getPropertyCode(productinfo.getProductCode(), RsyncKLtoHJYSkuInfoService.SKU_KEY_COLOR, "颜色", colorName);
			String styleKey = RsyncKLtoHJYSkuInfoService.getPropertyCode(productinfo.getProductCode(), RsyncKLtoHJYSkuInfoService.SKU_KEY_STYLE, "款式", styleName);
			
			productSkuInfo.setSkuKey("4497462000010001="+colorKey+"&4497462000020001="+styleKey);
			productSkuInfo.setSkuValue("颜色="+colorName+"&款式="+styleName); //默认
			productSkuInfo.setSkuKeyvalue(productSkuInfo.getSkuValue());

			productSkuInfoList.add(productSkuInfo);
		}
		
		productinfo.setProductSkuInfoList(productSkuInfoList);
		PcProductinfoExt pcProductinfoExt = new PcProductinfoExt();//设置扩展信息
		pcProductinfoExt.setProductCodeOld((info.get("goodsId")==null?"":info.get("goodsId")).toString());
		pcProductinfoExt.setProductCode(productinfo.getProductCode());
		//此属性未知（一地入库类型）
		pcProductinfoExt.setPrchType("20");
		//赋值仓库编号
		JSONObject warehouseStores = info.getJSONObject("warehouseStores");
		Set<String> keys = warehouseStores.keySet();
		for(String key : keys){
			if(StringUtils.isBlank(pcProductinfoExt.getOaSiteNo())){
				pcProductinfoExt.setOaSiteNo(warehouseStores.getJSONObject(key).getString("warehouseId"));
			}else{
				pcProductinfoExt.setOaSiteNo(pcProductinfoExt.getOaSiteNo()+","+warehouseStores.getJSONObject(key).getString("warehouseId"));
			}
		}
		//pcProductinfoExt.setOaSiteNo("TDS1");
		//供应商id
		pcProductinfoExt.setDlrId(bConfig("familyhas.seller_code_KL"));
		//供应商名称
		String sSql = "select seller_name from uc_sellerinfo where  small_seller_code=:small_seller_code";
		Map<String,Object> map =DbUp.upTable("uc_sellerinfo").dataSqlOne(sSql, new MDataMap("small_seller_code",bConfig("familyhas.seller_code_KL")));
		pcProductinfoExt.setDlrNm(map==null?"":map.get("seller_name").toString());
		pcProductinfoExt.setValidateFlag("Y");
		//惠家有：2:一般贸易
		pcProductinfoExt.setProductTradeType(String.valueOf(2));
		pcProductinfoExt.setSettlementType("4497471600110002");//特殊结算
		//采购类型：代销4497471600160001
		pcProductinfoExt.setPurchaseType("4497471600160001");
		productinfo.setPcProductinfoExt(pcProductinfoExt);
		
		//添加商品保障数据。
//		String selSql = "select authority_logo_uid from pc_product_authority_logo where product_code=:product_code";
//		List<Map<String,Object>> list =DbUp.upTable("pc_product_authority_logo").dataSqlList(selSql, new MDataMap("product_code",productinfo.getProductCode()));
//		if(list==null||list.size()==0) {
			MDataMap subMap = new MDataMap();
			subMap.put("product_code", productinfo.getProductCode());
			//七天包退换
			subMap.put("authority_logo_uid",bConfig("productcenter.authority_logo_sevenday"));
			subMap.put("create_time",DateUtil.getSysDateTimeString() );
			subMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
			DbUp.upTable("pc_product_authority_logo").dataInsert(subMap);
//		}
		
	}
	
	private String jiequDescPicForHJY(String descPic) {
		// TODO Auto-generated method stub

		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isBlank(descPic)) {
 			Pattern p = Pattern.compile("<img src=\"([^\"]+)\"");
 			Matcher mm = p.matcher(descPic);
 			while(mm.find()) {
 				if(sb.length()>0) {
 					sb.append("|");
 		         }
 			    sb.append(mm.group(1).replace("&amp;", "&"));		
 			  }
 		 }
		 return sb.toString();
	}

	
	public String bInfo(long iInfoCode, Object... sParms) {

		return FormatHelper.formatString(TopUp.upInfo(iInfoCode), sParms);
	}

}
