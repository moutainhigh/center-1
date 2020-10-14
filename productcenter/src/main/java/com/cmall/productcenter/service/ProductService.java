package com.cmall.productcenter.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.cmall.dborm.txmodel.PcAuthorityLogo;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.ProductCallableStatement;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.CategoryProperties;
import com.cmall.productcenter.model.FlashsalesActivity;
import com.cmall.productcenter.model.FlashsalesSkuInfo;
import com.cmall.productcenter.model.NavigationVersion;
import com.cmall.productcenter.model.PcCategoryinfo;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PcProductAdpic;
import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductflow;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductinfoBase;
import com.cmall.productcenter.model.PcProductinfoExt;
import com.cmall.productcenter.model.PcProductinfoForFamily;
import com.cmall.productcenter.model.PcProductinfoPage;
import com.cmall.productcenter.model.PcProductinfoSk;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.PicInfos;
import com.cmall.productcenter.model.ProductChangeFlag;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.ProductSkuInfoForFamily;
import com.cmall.productcenter.model.ProductSkuInfoPage;
import com.cmall.productcenter.model.PropertiesProductRelation;
import com.cmall.productcenter.model.PropertiesValue;
import com.cmall.productcenter.model.ScStoreSkunum;
import com.cmall.productcenter.model.UcSellercategoryProductRelation;
import com.cmall.productcenter.model.VProductSku;
import com.cmall.productcenter.txservice.TxProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.model.AppNavigation;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.service.StoreService;
import com.cmall.systemcenter.service.SystemCheck;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.cmall.systemcenter.txservice.TxStockService;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmasorder.model.TagInfo;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.face.IPlusConfig;
import com.srnpr.xmassystem.helper.PlusHelperEvent;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.helper.PlusHelperScheduler;
import com.srnpr.xmassystem.load.LoadEventInfo;
import com.srnpr.xmassystem.load.LoadEventSale;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.load.LoadShowCouponType;
import com.srnpr.xmassystem.modelevent.PlusModelEventFull;
import com.srnpr.xmassystem.modelevent.PlusModelEventInfo;
import com.srnpr.xmassystem.modelevent.PlusModelEventInfoPlus;
import com.srnpr.xmassystem.modelevent.PlusModelEventItemProduct;
import com.srnpr.xmassystem.modelevent.PlusModelEventQuery;
import com.srnpr.xmassystem.modelevent.PlusModelEventSale;
import com.srnpr.xmassystem.modelevent.PlusModelFullCutMessage;
import com.srnpr.xmassystem.modelevent.PlusModelSaleQuery;
import com.srnpr.xmassystem.modelevent.PlusModelSkuPriceFlow;
import com.srnpr.xmassystem.modelproduct.PlusModelGiftSkuinfo;
import com.srnpr.xmassystem.modelproduct.PlusModelGitfSkuInfoList;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.PlusServiceEventPlus;
import com.srnpr.xmassystem.service.PlusServiceSale;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.support.PlusSupportEvent;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.very.PlusVeryImage;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.ImageSupport;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.cc.VideoService;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmethod.WebUpload;
import com.srnpr.zapweb.webmodel.MFileItem;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.webpage.ControlPage;
import com.srnpr.zapweb.websupport.ImageCuterUtil;

/**
 * 
 * 项目名称：productcenter 类名称：ProductService 类描述： 添加商品 创建人：yanzj 创建时间：2013-9-7
 * 下午3:49:26 修改人：yanzj 修改时间：2013-9-7 下午3:49:26 修改备注：
 * 
 * @version
 * 
 */
public class ProductService extends BaseClass implements IFlowFunc {

	public static String ProductHead = "8016";
	public static String SKUHead = "8019";
	public static String ProductFlowHead = "PF";
	public static String ColorHead = "449746200001";
	public static String MainHead = "449746200002";
	public static String NormalHead = "449746200003";

	public static String ProductStatusSJ = "4497153900060002";

	private static Object updateObj = new Object();
	
	static ProductPriceService priceService = new ProductPriceService();
	static LoadShowCouponType loadShowCouponType = new LoadShowCouponType();
	static Map<String,String> eventTypeMap = new HashMap<String, String>();
	static PlusServiceEventPlus plusServiceEventPlus = new PlusServiceEventPlus();

	/**
	 * 取得所有商品,为缓存 价格，库存做准备
	 * 
	 * @return
	 */
	public List<ProductSkuInfo> getAllProductForCache() {

		List<ProductSkuInfo> retList = new ArrayList<ProductSkuInfo>();

		SerializeSupport sSku = new SerializeSupport<ProductSkuInfo>();
		List<MDataMap> pListMap = DbUp.upTable("pc_skuinfo")
				.query("sku_code,product_code,sell_price,stock_num,seller_code", "", "", null, -1, -1);

		if (pListMap != null) {
			int size = pListMap.size();

			for (int j = 0; j < size; j++) {
				ProductSkuInfo pic = new ProductSkuInfo();
				sSku.serialize(pListMap.get(j), pic);
				retList.add(pic);
			}
		} else {
			return retList;
		}
		return retList;
	}

	public String upProductInfoJson(String sProduct) {
		JsonHelper<PcProductinfo> jsonHelper = new JsonHelper<PcProductinfo>();
		if(sProduct.contains("session")) {//包含session，说明就是JSON
			PcProductinfo pcProductinfo = new PcProductinfo();
			String productJson = (String)WebSessionHelper.create().upHttpRequest().getSession().getAttribute(sProduct);
			try {
				productJson = URLDecoder.decode(productJson,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			pcProductinfo = new JsonHelper<PcProductinfo>().StringToObj(productJson, pcProductinfo);
			List<ProductSkuInfo> skus = pcProductinfo.getProductSkuInfoList();
			BigDecimal minSellPrice = BigDecimal.ZERO;
			BigDecimal maxSellPrice = BigDecimal.ZERO;
			for(ProductSkuInfo sku : skus) {
				if(minSellPrice.intValue() == 0) {
					minSellPrice = sku.getSellPrice();
				}else {
					if(minSellPrice.compareTo(sku.getSellPrice()) == 1) {
						minSellPrice = sku.getSellPrice();
					}
				}
				if(maxSellPrice.compareTo(sku.getSellPrice()) == -1) {
					maxSellPrice = sku.getSellPrice();
				}
			}
			pcProductinfo.setMaxSellPrice(maxSellPrice);
			pcProductinfo.setMinSellPrice(minSellPrice);
			String ccvid = pcProductinfo.getProductDescVideo();
			VideoService vs = new VideoService();
			if(StringUtils.isNotEmpty(ccvid)) {
				pcProductinfo.setVideoUrlShow(vs.getCcPlayUrl(ccvid));
			}
			return jsonHelper.ObjToString(pcProductinfo);
		}
		if (StringUtils.isNotEmpty(sProduct)) {
			return jsonHelper.ObjToString(getProduct(sProduct));
		} else {
			return jsonHelper.ObjToString(new PcProductinfo());
		}
	}

	/**
	 * 取得某个店铺的商品，最新上架的单品
	 * 
	 * @param sellerCode
	 * @return
	 */
	public List<PcProductInfoForI> getProductListForSeller(String sellerCode) {

		List<PcProductInfoForI> ret = new ArrayList<PcProductInfoForI>();
		SerializeSupport sProduct = new SerializeSupport<PcProductInfoForI>();

		MDataMap urMapParam = new MDataMap();
		urMapParam.put("seller_code", sellerCode);

		List<MDataMap> pListMap = DbUp.upTable("pc_productinfo").query("", "create_time desc  limit 0,4 ",
				"seller_code=:seller_code and product_status='4497153900060002' and flag_sale=1", urMapParam, -1, -1);

		if (pListMap != null) {
			int size = pListMap.size();

			for (int j = 0; j < size; j++) {
				PcProductInfoForI pic = new PcProductInfoForI();
				sProduct.serialize(pListMap.get(j), pic);

				MDataMap skuMapParam = new MDataMap();
				skuMapParam.put("product_code", pic.getProductCode());
				pic.setProdutName(pListMap.get(j).get("product_name"));
				ret.add(pic);

				List<ProductSkuInfo> productSkuInfoList = new ArrayList<ProductSkuInfo>();

				List<MDataMap> itemMap = DbUp.upTable("pc_skuinfo").query("sku_code,sku_picurl", "",
						"product_code=:product_code", skuMapParam, -1, -1);

				if (itemMap != null) {
					for (int i = 0; i < itemMap.size(); i++) {

						ProductSkuInfo pItem = new ProductSkuInfo();
						pItem.setSkuCode(itemMap.get(i).get("sku_code"));
						pItem.setSkuPicUrl(itemMap.get(i).get("sku_picurl"));
						productSkuInfoList.add(pItem);
					}
				}
				pic.setProductSkuInfoList(productSkuInfoList);
			}
		}

		return ret;

	}

	/**
	 * 取得商品信息，只取主要信息，和sku信息,
	 * 
	 * @param productStr
	 *            用 逗号 分隔
	 * @return
	 */
	public List<PcProductInfoForI> getProductListForI(String productStr) {

		List<PcProductInfoForI> ret = new ArrayList<PcProductInfoForI>();

		if (productStr == null || productStr.equals("")) {
			return ret;
		} else {
			String[] ary = productStr.split(",");

			String whereStr = "";
			MDataMap urMapParam = new MDataMap();

			for (int i = 0; i < ary.length; i++) {

				if (!ary[i].equals("")) {
					urMapParam.put("product_code" + i, ary[i]);
					whereStr += " product_code=:product_code" + i + " or";
				}
			}

			if (whereStr.length() > 2) {

				SerializeSupport sProduct = new SerializeSupport<PcProductInfoForI>();
				SerializeSupport sSku = new SerializeSupport<ProductSkuInfo>();

				whereStr = whereStr.substring(0, whereStr.length() - 2);
				List<MDataMap> pListMap = DbUp.upTable("pc_productinfo").query("", "", whereStr, urMapParam, -1, -1);
				if (pListMap != null) {
					int size = pListMap.size();

					for (int j = 0; j < size; j++) {
						PcProductInfoForI pic = new PcProductInfoForI();
						sProduct.serialize(pListMap.get(j), pic);

						MDataMap skuMapParam = new MDataMap();
						skuMapParam.put("product_code", pic.getProductCode());
						List<MDataMap> skuListMap = DbUp.upTable("pc_skuinfo").query("", "",
								"product_code=:product_code", skuMapParam, -1, -1);

						pic.setProdutName(pListMap.get(j).get("product_name"));

						if (skuListMap != null) {
							int psize = skuListMap.size();
							for (int k = 0; k < psize; k++) {
								ProductSkuInfo oap = new ProductSkuInfo();
								sSku.serialize(skuListMap.get(k), oap);
								if (pic.getProductSkuInfoList() == null)
									pic.setProductSkuInfoList(new ArrayList<ProductSkuInfo>());

								oap.setSkuValue(skuListMap.get(k).get("sku_keyvalue"));
								pic.getProductSkuInfoList().add(oap);
							}
						}

						/*
						 * MDataMap pcCategorypropertyRelData =
						 * DbUp.upTable("pc_productcategory_rel"
						 * ).one("product_code",
						 * pic.getProductCode(),"flag_main","1"); //取得商品分类信息
						 * PcCategoryinfo category = new PcCategoryinfo();
						 * if(pcCategorypropertyRelData!=null) {
						 * category.setCategoryCode
						 * (pcCategorypropertyRelData.get("category_code")); }
						 * 
						 * pic.setCategory(category);
						 */

						/*
						 * //取得商品属性信息 MDataMap pcProductpropertyListMapParam =
						 * new MDataMap();
						 * pcProductpropertyListMapParam.put("product_code",
						 * pic.getProductCode()); List<PcProductproperty>
						 * pcProductpropertyList = null; List<MDataMap>
						 * pcProductpropertyListMap
						 * =DbUp.upTable("pc_productproperty").query("", "",
						 * "product_code=:product_code and (property_type='449736200001' or property_type='449736200002')"
						 * ,pcProductpropertyListMapParam, -1, -1);
						 * if(pcProductpropertyListMap!=null){ int sizeaa =
						 * pcProductpropertyListMap.size();
						 * pcProductpropertyList = new
						 * ArrayList<PcProductproperty>(); SerializeSupport ss =
						 * new SerializeSupport<PcProductproperty>(); for(int
						 * i=0;i<sizeaa;i++) { PcProductproperty picc = new
						 * PcProductproperty();
						 * ss.serialize(pcProductpropertyListMap.get(i), picc);
						 * pcProductpropertyList.add(picc); } }
						 * if(pcProductpropertyList!=null)
						 * pic.setPcProductpropertyList(pcProductpropertyList);
						 */
						ret.add(pic);
					}
				} else {
					return ret;
				}
			} else {
				return ret;
			}
		}
		return ret;
	}

	/**
	 * 取得商品的Sku商品信息 .
	 * 
	 * @param skuStr
	 *            用 逗号 分隔
	 * @return
	 */
	public List<ProductSkuInfo> getSkuListForI(String skuStr) {

		List<ProductSkuInfo> ret = new ArrayList<ProductSkuInfo>();

		if (skuStr == null || skuStr.equals("")) {
			return ret;
		} else {

			String[] ary = skuStr.split(",");

			String whereStr = "";
			MDataMap urMapParam = new MDataMap();

			for (int i = 0; i < ary.length; i++) {

				if (!ary[i].equals("")) {
					urMapParam.put("sku_code" + i, ary[i]);
					whereStr += " sku_code=:sku_code" + i + " or";
				}
			}

			if (whereStr.length() > 2) {

				SerializeSupport sSku = new SerializeSupport<ProductSkuInfo>();

				whereStr = whereStr.substring(0, whereStr.length() - 2);
				List<MDataMap> pListMap = DbUp.upTable("pc_skuinfo").query("", "", whereStr, urMapParam, -1, -1);
				if (pListMap != null) {
					int size = pListMap.size();

					for (int j = 0; j < size; j++) {
						ProductSkuInfo pic = new ProductSkuInfo();
						sSku.serialize(pListMap.get(j), pic);
						pic.setSkuValue(pListMap.get(j).get("sku_keyvalue"));
						ret.add(pic);
					}
				} else {
					return ret;
				}
			} else {
				return ret;
			}
		}
		return ret;

	}

	/**
	 * 取得商品的Sku商品信息 .
	 * 
	 * @param skuStr
	 *            用 逗号 分隔
	 * @return
	 */
	public List<VProductSku> getVSkuListForI(String skuStr) {

		List<VProductSku> ret = new ArrayList<VProductSku>();

		if (skuStr == null || skuStr.equals("")) {
			return ret;
		} else {

			String[] ary = skuStr.split(",");

			String whereStr = "";
			MDataMap urMapParam = new MDataMap();

			for (int i = 0; i < ary.length; i++) {

				if (!ary[i].equals("")) {
					urMapParam.put("sku_code" + i, ary[i]);
					whereStr += " sku_code=:sku_code" + i + " or";
				}
			}

			if (whereStr.length() > 2) {

				SerializeSupport sSku = new SerializeSupport<VProductSku>();

				whereStr = whereStr.substring(0, whereStr.length() - 2);
				List<MDataMap> pListMap = DbUp.upTable("v_pc_sku").query("", "", whereStr, urMapParam, -1, -1);
				if (pListMap != null) {
					int size = pListMap.size();

					for (int j = 0; j < size; j++) {
						VProductSku pic = new VProductSku();
						sSku.serialize(pListMap.get(j), pic);
						pic.setSkuValue(pListMap.get(j).get("sku_keyvalue"));
						ret.add(pic);
					}
				} else {
					return ret;
				}
			} else {
				return ret;
			}
		}
		return ret;

	}

	/**
	 * 获取商品信息-嘉玲
	 * 
	 * @param productCode
	 * @return
	 */
	public PcProductinfo getProductCode(String productCode) {

		try {

			PcProductinfo product = new PcProductinfo();

			if (productCode == null || productCode.length() <= 2)
				return null;
			else {
				if (productCode.indexOf("_1") > 0) {
					productCode = productCode.substring(0, productCode.length() - 2);

					MDataMap productData = DbUp.upTable("pc_productflow").one("product_code", productCode,
							"flow_status", "1");

					if (productData == null)
						return null;

					String pValue = productData.get("product_json");
					JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
					product = pHelper.StringToObj(pValue, product);

					return product;
				}
			}

			MDataMap prodcutData = null;
			if (productCode.trim().length() == 32) {
				prodcutData = DbUp.upTable("pc_productinfo").one("uid", productCode);
			} else {
				prodcutData = DbUp.upTable("pc_productinfo").one("product_code", productCode);
			}

			if (prodcutData == null)
				return null;
			else {
				productCode = prodcutData.get("product_code");
				product = new SerializeSupport<PcProductinfo>().serialize(prodcutData, new PcProductinfo());

				product.setProdutName(prodcutData.get("product_name"));

				MDataMap pcCategorypropertyRelData = DbUp.upTable("pc_productcategory_rel").one("product_code",
						productCode, "flag_main", "1");
				// 取得商品分类信息
				PcCategoryinfo category = new PcCategoryinfo();
				if (pcCategorypropertyRelData != null) {
					category.setCategoryCode(pcCategorypropertyRelData.get("category_code"));
				}

				product.setCategory(category);

				PcProductdescription description = null;
				MDataMap pcProductdescriptionData = DbUp.upTable("pc_productdescription").one("product_code",
						productCode);

				if (pcProductdescriptionData != null) {
					description = new SerializeSupport<PcProductdescription>().serialize(pcProductdescriptionData,
							new PcProductdescription());
				}

				// 取得商品描述信息
				if (pcProductdescriptionData != null)
					product.setDescription(description);

				MDataMap pcPicListMapParam = new MDataMap();
				pcPicListMapParam.put("product_code", productCode);
				List<PcProductpic> pcPicList = null;
				List<MDataMap> pcPicListMap = DbUp.upTable("pc_productpic").query("", "",
						"product_code=:product_code  and (sku_code='' or sku_code is null)", pcPicListMapParam, -1, -1);
				if (pcPicListMap != null) {
					int size = pcPicListMap.size();
					pcPicList = new ArrayList<PcProductpic>();
					SerializeSupport ss = new SerializeSupport<PcProductpic>();
					for (int i = 0; i < size; i++) {
						PcProductpic pic = new PcProductpic();
						ss.serialize(pcPicListMap.get(i), pic);
						pcPicList.add(pic);
					}
				}

				if (pcPicList != null)
					product.setPcPicList(pcPicList);

				// 取得商品属性信息
				MDataMap pcProductpropertyListMapParam = new MDataMap();
				pcProductpropertyListMapParam.put("product_code", productCode);
				List<PcProductproperty> pcProductpropertyList = null;

				List<MDataMap> pcProductpropertyListMap = DbUp.upTable("pc_productproperty").query(
						"zid,uid, product_code,property_keycode,property_code, property_key,REPLACE(group_concat(property_value),',',' ') as property_value ,property_type,big_sort,small_sort ",
						" property_type,small_sort desc", "product_code=:product_code GROUP BY property_key",
						pcProductpropertyListMapParam, 0, 0);
				if (pcProductpropertyListMap != null) {
					int size = pcProductpropertyListMap.size();
					pcProductpropertyList = new ArrayList<PcProductproperty>();
					SerializeSupport ss = new SerializeSupport<PcProductproperty>();
					for (int i = 0; i < size; i++) {
						PcProductproperty pic = new PcProductproperty();
						ss.serialize(pcProductpropertyListMap.get(i), pic);
						pcProductpropertyList.add(pic);
					}
				}
				if (pcProductpropertyList != null)
					product.setPcProductpropertyList(pcProductpropertyList);

				// 取得商品的sku信息
				MDataMap pcSkuMapParam = new MDataMap();
				pcSkuMapParam.put("product_code", productCode);
				List<ProductSkuInfo> productSkuInfoList = null;
				List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").query("", "",
						"product_code=:product_code", pcSkuMapParam, -1, -1);

				if (productSkuInfoListMap != null) {
					int size = productSkuInfoListMap.size();
					productSkuInfoList = new ArrayList<ProductSkuInfo>();
					SerializeSupport ss = new SerializeSupport<ProductSkuInfo>();

					for (int i = 0; i < size; i++) {
						ProductSkuInfo pic = new ProductSkuInfo();
						ss.serialize(productSkuInfoListMap.get(i), pic);
						pic.setSkuValue(productSkuInfoListMap.get(i).get("sku_keyvalue"));
						productSkuInfoList.add(pic);
					}
				}

				if (productSkuInfoList != null) {
					product.setProductSkuInfoList(productSkuInfoList);

					/*
					 * if (pcPicList != null) { for (ProductSkuInfo sku :
					 * productSkuInfoList) { for (PcProductpic pic : pcPicList)
					 * { if (sku.getSkuCode().equals(pic.getSkuCode())) {
					 * sku.setSkuPicUrl(pic.getPicUrl()); } } }
					 * 
					 * }
					 */
				}

				MDataMap brandMapParam = new MDataMap();
				brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", product.getBrandCode());
				if (brandMapParam != null) {
					product.setBrandName(brandMapParam.get("brand_name"));
				}
			}

			return product;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 获取商品信息
	 * 
	 * @param productCode
	 * @return
	 */
	public PcProductinfo getProduct(String productCode) {

		try {

			PcProductinfo product = new PcProductinfo();
			VideoService vs = new VideoService();
			if (productCode == null || productCode.length() <= 2)
				return null;
			else {
				if (productCode.indexOf("_1") > 0) {  // 如果从【商品发布审批（网站编辑）】菜单进入
					productCode = productCode.substring(0, productCode.length() - 2);

					// MDataMap productData =
					// DbUp.upTable("pc_productflow").oneWhere(
					// "product_code", productCode, "zid", "MAX(zid)");
					MDataMap productData = new MDataMap();
					List<MDataMap> productDataList = DbUp.upTable("pc_productflow").query("", "zid desc",
							"product_code='" + productCode + "'", null, 0, 1);
					if (null != productDataList && productDataList.size() > 0) {
						productData = productDataList.get(0);
					}

					if (productData == null)
						return null;

					String pValue = productData.get("product_json");
					JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
					product = pHelper.StringToObj(pValue, product);
					// 加入商户类型的判断 - Yangcl - 20170124
					String sellerType = WebHelper.getSellerType(product.getSmallSellerCode());
					product.setSellerType(sellerType); 
					if(StringUtils.isNotEmpty(product.getProductDescVideo())) {
						product.setVideoUrlShow(vs.getCcPlayUrl(product.getProductDescVideo()));
					}
					return product;
				}
			}

			MDataMap prodcutData = null;
			if (productCode.trim().length() == 32) {
				prodcutData = DbUp.upTable("pc_productinfo").one("uid", productCode);
			} else {
				prodcutData = DbUp.upTable("pc_productinfo").one("product_code", productCode);
			}

			if (prodcutData == null)
				return null;
			else {
				productCode = prodcutData.get("product_code");
				product = new SerializeSupport<PcProductinfo>().serialize(prodcutData, new PcProductinfo());

				product.setProdutName(prodcutData.get("product_name"));

				MDataMap pcCategorypropertyRelData = DbUp.upTable("pc_productcategory_rel").one("product_code",
						productCode, "flag_main", "1");
				// 取得商品分类信息
				PcCategoryinfo category = new PcCategoryinfo();
				if (pcCategorypropertyRelData != null) {
					category.setCategoryCode(pcCategorypropertyRelData.get("category_code"));
				}

				product.setCategory(category);

				PcProductdescription description = null;
				MDataMap pcProductdescriptionData = DbUp.upTable("pc_productdescription").one("product_code",
						productCode);

				if (pcProductdescriptionData != null && !pcProductdescriptionData.isEmpty()) {
					description = new SerializeSupport<PcProductdescription>().serialize(pcProductdescriptionData,
							new PcProductdescription());
				}

				// 取得商品描述信息
				if (description != null)
					product.setDescription(description);

				MDataMap pcPicListMapParam = new MDataMap();
				pcPicListMapParam.put("product_code", productCode);
				List<PcProductpic> pcPicList = null;
				List<MDataMap> pcPicListMap = DbUp.upTable("pc_productpic").query("", "",
						"product_code=:product_code  and (sku_code='' or sku_code is null)", pcPicListMapParam, -1, -1);
				if (pcPicListMap != null) {
					int size = pcPicListMap.size();
					pcPicList = new ArrayList<PcProductpic>();
					SerializeSupport ss = new SerializeSupport<PcProductpic>();
					for (int i = 0; i < size; i++) {
						PcProductpic pic = new PcProductpic();
						ss.serialize(pcPicListMap.get(i), pic);
						pcPicList.add(pic);
					}
				}

				if (pcPicList != null)
					product.setPcPicList(pcPicList);
				
				//取得商品广告图信息
				MDataMap pcAdpicListMapParam = new MDataMap();
				pcAdpicListMapParam.put("product_code", productCode);
				List<PcProductAdpic> pcAdicList = null;
				List<MDataMap> pcAdpicListMap = DbUp.upTable("pc_productadpic").query("", "ord_no",
						"product_code=:product_code  and (sku_code='' or sku_code is null)", pcAdpicListMapParam, -1, -1);
				if (pcAdpicListMap != null) {
					pcAdicList = new ArrayList<PcProductAdpic>();
					for(MDataMap map : pcAdpicListMap) {
						PcProductAdpic adpic = new PcProductAdpic();
						adpic.setPic_url(map.get("pic_url") == null ? "" : map.get("pic_url").toString());
						adpic.setStart_date(map.get("start_date") == null ? "" : map.get("start_date").toString());
						adpic.setEnd_date(map.get("end_date") == null ? "" : map.get("end_date").toString());
						adpic.setOrd_no(Integer.parseInt(map.get("ord_no")));
						pcAdicList.add(adpic);
					}
				}
				if(pcAdicList != null)
					product.setPcAdpicList(pcAdicList);

				// 取得商品属性信息
				MDataMap pcProductpropertyListMapParam = new MDataMap();
				pcProductpropertyListMapParam.put("product_code", productCode);
				List<PcProductproperty> pcProductpropertyList = null;
				List<MDataMap> pcProductpropertyListMap = DbUp.upTable("pc_productproperty").query("",
						" property_type,small_sort desc,zid asc ", "product_code=:product_code",
						pcProductpropertyListMapParam, -1, -1);
				if (pcProductpropertyListMap != null) {
					int size = pcProductpropertyListMap.size();
					pcProductpropertyList = new ArrayList<PcProductproperty>();
					SerializeSupport ss = new SerializeSupport<PcProductproperty>();
					for (int i = 0; i < size; i++) {
						PcProductproperty pic = new PcProductproperty();
						ss.serialize(pcProductpropertyListMap.get(i), pic);
						pcProductpropertyList.add(pic);
					}
				}
				if (pcProductpropertyList != null)
					product.setPcProductpropertyList(pcProductpropertyList);

				// 取得商品的sku信息
				MDataMap pcSkuMapParam = new MDataMap();
				pcSkuMapParam.put("product_code", productCode);
				List<ProductSkuInfo> productSkuInfoList = null;
				List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").query("", "",
						"product_code=:product_code", pcSkuMapParam, -1, -1);

				if (productSkuInfoListMap != null) {
					int size = productSkuInfoListMap.size();
					productSkuInfoList = new ArrayList<ProductSkuInfo>();
					SerializeSupport ss = new SerializeSupport<ProductSkuInfo>();

					for (int i = 0; i < size; i++) {
						ProductSkuInfo pic = new ProductSkuInfo();
						ss.serialize(productSkuInfoListMap.get(i), pic);
						pic.setSkuValue(productSkuInfoListMap.get(i).get("sku_keyvalue"));
						productSkuInfoList.add(pic);
					}
				}

				if (productSkuInfoList != null) {
					product.setProductSkuInfoList(productSkuInfoList);

					/*
					 * if (pcPicList != null) { for (ProductSkuInfo sku :
					 * productSkuInfoList) { for (PcProductpic pic : pcPicList)
					 * { if (sku.getSkuCode().equals(pic.getSkuCode())) {
					 * sku.setSkuPicUrl(pic.getPicUrl()); } } }
					 * 
					 * }
					 */
				}

				MDataMap brandMapParam = new MDataMap();
				brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", product.getBrandCode());
				if (brandMapParam != null) {
					product.setBrandName(brandMapParam.get("brand_name"));
				}

				// 商品的扩展属性
				MDataMap fictitiousSalesMap = DbUp.upTable("pc_productinfo_ext").one("product_code", productCode);
				if (fictitiousSalesMap != null) {
					SerializeSupport<com.cmall.productcenter.model.PcProductinfoExt> ss = new SerializeSupport<com.cmall.productcenter.model.PcProductinfoExt>();
					com.cmall.productcenter.model.PcProductinfoExt pcProductinfoExt = new com.cmall.productcenter.model.PcProductinfoExt();
					ss.serialize(fictitiousSalesMap, pcProductinfoExt);
					product.setPcProductinfoExt(pcProductinfoExt);
				}
				// 商品虚类
				List<MDataMap> categoryProductRelationMap = DbUp.upTable("uc_sellercategory_product_relation")
						.queryAll("", "", "product_code='" + productCode + "'", null);
				List<UcSellercategoryProductRelation> categoryProductRelationList = new ArrayList<UcSellercategoryProductRelation>();
				SerializeSupport<UcSellercategoryProductRelation> ss = new SerializeSupport<UcSellercategoryProductRelation>();
				for (MDataMap mDataMap : categoryProductRelationMap) {
					UcSellercategoryProductRelation categoryProductRelation = new UcSellercategoryProductRelation();
					ss.serialize(mDataMap, categoryProductRelation);
					categoryProductRelationList.add(categoryProductRelation);
				}
				product.setUsprList(categoryProductRelationList);
			}
			// 加入商户类型的判断 - Yangcl - 20170124
			String sellerType = WebHelper.getSellerType(product.getSmallSellerCode());
			product.setSellerType(sellerType); 
			String ccvid = product.getProductDescVideo();
			if(StringUtils.isNotEmpty(ccvid)) {
				String videoUrl = vs.getCcPlayUrl(ccvid);
				product.setVideoUrlShow(videoUrl);
			}
			return product;
		} catch (Exception e) {
			return null;
		}

	}


	/**
	 * 添加商品
	 * 
	 * @param product
	 * @param error
	 *            如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int AddProduct(PcProductinfo product, StringBuffer error) {
		return productManage(product, error, 0);
	}

	/**
	 * 添加商品-惠美丽
	 * 
	 * @param product
	 * @param error
	 *            如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int AddProductForCa(PcProductinfo product, StringBuffer error) {
		return productManageForCa(product, error, 0);
	}

	/**
	 * 更新商品 (商户商品审批流使用)
	 * 
	 * @param product
	 * @param error
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int updateProductForCshop(PcProductinfo product, StringBuffer error) {
		synchronized (updateObj) {
			if (product.getProductCode() == null || product.getProductCode().equals("")) {

				error.append(bInfo(941901008));
				return 941901008;
			} else {
				/**
				 * 修改商户编码前缀修改，修改判断商品商户编码 2016-11-24 zhy
				 */
				String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
				boolean codeFlag = StringUtils.isNotBlank(seller_type);
				int errorCode = 1;
				if (product == null) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				if (product.getProductCode() == null || product.getProductCode().equals("")) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				// 商品名字不能为空
				if (product.getProdutName() == null || product.getProdutName().trim().equals("")) {
					errorCode = 941901012;
					error.append(bInfo(errorCode));
					return errorCode;
				}

				String uid = WebHelper.addLock(product.getProductCode(), 60);
				int ret = 0;
				if (uid.equals("")) {
					ret = 941901033;
					error.append(bInfo(ret, ""));
				} else {

					if (uid.equals("")) {
						ret = 941901033;
						error.append(bInfo(ret, ""));
					} else {
						// 判读当前的审批流程是否存在。
						FlowService fs = new FlowService();
						ScFlowMain sfm = fs.getApprovalFlowByOurterCode(product.getProductCode(), "449717230016");

						if (sfm != null) {
							errorCode = 941901039;
							error.append(bInfo(errorCode));
							WebHelper.unLock(uid);
							return errorCode;
						}
						// 商品的sku数量不能为空!
						if (product.getProductSkuInfoList() == null || product.getProductSkuInfoList().size() == 0) {
							errorCode = 941901015;
							error.append(bInfo(errorCode));
							WebHelper.unLock(uid);
							return errorCode;
						}
						// 如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张 edit by 李国杰 start
						if ((null == product.getMainPicUrl() || "".equals(product.getMainPicUrl()))
								&& product.getPcPicList() != null && product.getPcPicList().size() > 0) {
							product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
						}
						if (null != product.getDescription() && null != product.getDescription().getKeyword()
								&& !"".equals(product.getDescription().getKeyword())) {
							String[] labelsArr = product.getDescription().getKeyword().trim().split(" ");
							String labelStr = ""; // 将空格替换掉
							for (int i = 0; i < labelsArr.length; i++) {
								if ("".endsWith(labelsArr[i])) {
									continue;
								}
								labelStr += labelsArr[i] + ",";
							}
							product.getDescription().setKeyword(labelStr.substring(0, labelStr.length() - 1)); // 截去最后一个逗号并把商品标签添加到商品描述表中
						}
						PcProductflow pcProdcutflow = new PcProductflow();
						pcProdcutflow.setProductCode(product.getProductCode());
						pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));
						pcProdcutflow.setFlowStatus(SkuCommon.ProUpaInit);

						// 商户后台添加商品保存库存信息
						if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
								&& StringUtils.isNotEmpty(product.getSmallSellerCode())
								&& codeFlag
								&& !product.getSmallSellerCode().equals("SF03KJT")) {
							for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
								ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
								List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
								ScStoreSkunum store = new ScStoreSkunum();
								store.setBatchCode("");
								store.setSkuCode(sku.getSkuCode());
								store.setStockNum(Long.parseLong(sku.getStockNum() + ""));
								store.setStoreCode(AppConst.THIRD_STORE_CODE);
								scStoreSkunumList.add(store);
								sku.setScStoreSkunumList(scStoreSkunumList);
							}
						}

						MUserInfo userInfo = null;
						String manageCode = "";
						String userCode = "";
						if (UserFactory.INSTANCE != null) {
							try {
								userInfo = UserFactory.INSTANCE.create();
							} catch (Exception e) {
								// TODO: handle exception
							}

							if (userInfo != null) {
								manageCode = userInfo.getManageCode();
								userCode = userInfo.getUserCode();
								if (userCode == null || "".equals(userCode)) {
									userCode = manageCode;
								}
							}
						}

						pcProdcutflow.setUpdator(userCode);
						product.setPcProdcutflow(pcProdcutflow);

						MDataMap prodcutData = null;

						prodcutData = DbUp.upTable("pc_productinfo").one("product_code", product.getProductCode());

						ProductChangeFlag pcf = new ProductChangeFlag();

						pcf.setOldPicUrl(StringUtils.isBlank(prodcutData.get("mainpic_url")) ? ""
								: prodcutData.get("mainpic_url"));
						pcf.setOldProductName(StringUtils.isBlank(prodcutData.get("product_name")) ? ""
								: prodcutData.get("product_name"));

						//
						// 更新最大值和最小值的 minPrice 和 maxPrice
						if (product.getProductSkuInfoList() != null && !product.getProductSkuInfoList().isEmpty()) {

							// 检查价格是否符合规则
							// 1) 商品的市场价格必须不小于商品sku的销售价格. 2)商品的sku成本价小于销售价
							// 商品的sku库存和销售价格不能小于 0 ,只能是大于等于 0
							for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
								if (sku.getStockNum() < 0) {
									errorCode = 941901016;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}

								if (sku.getSellPrice().doubleValue() < 0) {
									errorCode = 941901017;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}

								if (sku.getSkuPicUrl() == null) {
									sku.setSkuPicUrl("");
								}

								if (sku.getSkuKey() == null) {
									sku.setSkuKey("");
								}

								if (sku.getSkuValue() == null) {
									sku.setSkuValue("");
								}

								if (sku.getSellProductcode() == null) {
									sku.setSellProductcode("");
								}

								if (sku.getSkuName() == null) {
									sku.setSkuName("");
								}
								// 商品的市场价格必须不小于商品sku的销售价格.
								if (sku.getSellPrice().compareTo(product.getMarketPrice()) > 0) {
									errorCode = 941901134;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}
								// 商品的sku成本价小于销售价
								if (sku.getSellPrice().compareTo(sku.getCostPrice()) <= 0) {
									errorCode = 941901135;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}
							}

							int size = product.getProductSkuInfoList().size();

							BigDecimal tempMin = BigDecimal.ZERO;
							BigDecimal tempMax = BigDecimal.ZERO;
							boolean init = true;
							for (int i = 0; i < size; i++) {
								ProductSkuInfo pic = product.getProductSkuInfoList().get(i);
								if (StringUtils.isBlank(pic.getSkuCode()) || "N".equals(pic.getSaleYn())) {
									continue;
								}
								if (init) {
									tempMin = pic.getSellPrice();
									tempMax = pic.getSellPrice();
									init = false;
								} else {
									if (tempMin.compareTo(pic.getSellPrice()) == 1)
										tempMin = pic.getSellPrice();
									if (tempMax.compareTo(pic.getSellPrice()) == -1)
										tempMax = pic.getSellPrice();
								}
							}
							product.setMinSellPrice(tempMin);
							product.setMaxSellPrice(tempMax);
						}
						// 校验商品名字的外链合法性
						RootResult rr = checkContent(product.getProdutName());
						if (rr.getResultCode() != 1) {
							error.append(rr.getResultMessage());
							WebHelper.unLock(uid);
							return rr.getResultCode();
						}
						// 校验商品简称名字的外链合法性
						RootResult sr = checkContent(product.getProductShortname());
						if (sr.getResultCode() != 1) {
							error.append(sr.getResultMessage());
							WebHelper.unLock(uid);
							return sr.getResultCode();
						}
						// 校验商品描述的外链合法性
						// if(product.getDescription()!=null){
						// rr =
						// checkContent(product.getDescription().getDescriptionInfo());
						// if(rr.getResultCode() != 1){
						// error.append(rr.getResultMessage());
						// WebHelper.unLock(uid);
						// return rr.getResultCode();
						// }
						// }
						// 校验商品属性外链的合法性
						if (product.getPcProductpropertyList() != null) {
							for (PcProductproperty ppp : product.getPcProductpropertyList()) {
								rr = checkContent(ppp.getPropertyKey());
								if (rr.getResultCode() != 1) {
									error.append(rr.getResultMessage());
									WebHelper.unLock(uid);
									return rr.getResultCode();
								}

								rr = checkContent(ppp.getPropertyValue());
								if (rr.getResultCode() != 1) {
									error.append(rr.getResultMessage());
									WebHelper.unLock(uid);
									return rr.getResultCode();
								}
							}
						}
						List<UcSellercategoryProductRelation> usprList = product.getUsprList();
						ret = UpdateProductTx(product, error, userCode, pcf);
						// 修改分类信息
						// if (ret == 1) {
						// if(usprList!=null && usprList.size() > 0){
						// DbUp.upTable("uc_sellercategory_product_relation").delete("product_code",product.getProductCode());
						// for (int i = 0; i < usprList.size(); i++) {
						// MDataMap inserUspr = new MDataMap();
						// inserUspr.put("uid",
						// UUID.randomUUID().toString().replace("-", ""));
						// inserUspr.put("product_code",
						// product.getProductCode());
						// inserUspr.put("category_code",
						// usprList.get(i).getCategoryCode());
						// inserUspr.put("seller_code",
						// product.getSellerCode());
						// DbUp.upTable("uc_sellercategory_product_relation").dataInsert(inserUspr);
						// }
						// }
						// }
						UUID uuid = UUID.randomUUID();
						MDataMap insertDatamap = new MDataMap();

						// 取得当前预览的商品的url
						String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl") + product.getProductCode()
								+ "_1";

						try {
							// 加入审批的流程
							ScFlowMain flow = new ScFlowMain();
							flow.setCreator(userCode);
							// flow.setCurrentStatus("4497153900060005");
							// 修改添加商品跳转节点 2016-06-24 zhy
							// 查询商家是否是跨境商户如果是跨境商户，进入节点跨境商品待审批节点，如果不是进入质检员待审批节点
							if (isCrossBorder(product.getSmallSellerCode())) {
								flow.setCurrentStatus("4497172300160004");
							} else {
								//质检员待审批修改为招商经理待审批 2017-06-05 zhy
								flow.setCurrentStatus("4497172300160015");
							}
							flow.setFlowUrl(preViewUrl);
							String title = bInfo(941901040, product.getProductCode());
							flow.setFlowTitle(product.getProductCode());
							// flow.setFlowType("449717230011");
							// 修改添加商品跳转节点 2016-06-21 zhy
							flow.setFlowType("449717230016");
							flow.setCreator(userCode);
							flow.setOuterCode(product.getProductCode());
							flow.setFlowRemark(title);
							// 取消指定审批人 2016-06-27 zhy
							// flow.setNext_operator_id(product.getPcProductinfoExt().getMdId());
							// flow.setFlowRemark("创建商品");
							// 创建的审批流程
							RootResult rrFlow = fs.CreateFlow(flow);
							if (rrFlow.getResultCode() != 1) {
								insertDatamap = new MDataMap();
								insertDatamap.put("uid", uuid.toString().replace("-", ""));
								DbUp.upTable("pc_productflow").dataDelete("", insertDatamap, "uid");
								WebHelper.errorMessage(product.getProductCode(), "alterProduct", 1,
										"alterProduct-HaveFlow", rr.getResultMessage(), null);
							} else {

							}
						} catch (Exception e) {

						}
					}

				}
				WebHelper.unLock(uid);
				return ret;
			}
		}
	}

	/**
	 * 更新商品
	 * 
	 * @param product
	 * @param error
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int updateProduct(PcProductinfo product, StringBuffer error) {
		synchronized (updateObj) {

			if (product.getProductCode() == null || product.getProductCode().equals("")) {

				error.append(bInfo(941901008));
				return 941901008;
			} else {
				/**
				 * 修改商户编码前缀修改，修改判断商品商户编码 2016-11-24 zhy
				 */
				String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
				boolean codeFlag = StringUtils.isNotBlank(seller_type);
				int errorCode = 1;
				if (product == null) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				if (product.getProductCode() == null || product.getProductCode().equals("")) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				// 商品名字不能为空
				if (product.getProdutName() == null || product.getProdutName().trim().equals("")) {
					errorCode = 941901012;
					error.append(bInfo(errorCode));
					return errorCode;
				}

				String uid = WebHelper.addLock(product.getProductCode(), 60);
				int ret = 0;
				if (uid.equals("")) {
					ret = 941901033;
					error.append(bInfo(ret, ""));
				} else {

					if (uid.equals("")) {
						ret = 941901033;
						error.append(bInfo(ret, ""));
					} else {

						// 如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张 edit by 李国杰 start
						// if ((null == product.getMainPicUrl() ||
						// "".equals(product.getMainPicUrl()))
						// && product.getPcPicList() != null &&
						// product.getPcPicList().size() > 0) {
						// product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
						// }
						/*
						 * 商品主图默认取图片列表的第一张图片 2016-08-16 zhy
						 */
						if (product.getPcPicList() != null && product.getPcPicList().size() > 0) {
							product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
						}

						if (null != product.getDescription() && null != product.getDescription().getKeyword()
								&& !"".equals(product.getDescription().getKeyword())) {
							String[] labelsArr = product.getDescription().getKeyword().trim().split(" ");
							String labelStr = ""; // 将空格替换掉
							for (int i = 0; i < labelsArr.length; i++) {
								if ("".endsWith(labelsArr[i])) {
									continue;
								}
								labelStr += labelsArr[i] + ",";
							}
							product.getDescription().setKeyword(labelStr.substring(0, labelStr.length() - 1)); // 截去最后一个逗号并把商品标签添加到商品描述表中
						}
						// edit by 李国杰 end

						// ret = productManageForUpdate(newP, error);

						PcProductflow pcProdcutflow = new PcProductflow();
						pcProdcutflow.setProductCode(product.getProductCode());
						pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));

						if (codeFlag
								&& !product.getSmallSellerCode().equals("SF03KJT")
								&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())) {
							pcProdcutflow.setFlowStatus(SkuCommon.ProUpaOr); // 商户后台终审通过
						} else {
							pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusInit);
						}

						// 商户后台添加商品保存库存信息
						if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
								&& StringUtils.isNotEmpty(product.getSmallSellerCode())
								&& codeFlag
								&& !product.getSmallSellerCode().equals("SF03KJT")) {
							for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
								ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
								if (sku.getSkuCode() == null || sku.getSkuCode().equals("")
										|| sku.getSkuCode().startsWith("DSF")) {
									List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
									ScStoreSkunum store = new ScStoreSkunum();
									store.setBatchCode("");
									store.setSkuCode(sku.getSkuCode());
									store.setStockNum(Long.parseLong(sku.getStockNum() + ""));
									store.setStoreCode(AppConst.THIRD_STORE_CODE);
									scStoreSkunumList.add(store);
									sku.setScStoreSkunumList(scStoreSkunumList);
								}
							}
						}
						
						//=================================================================						
						/**
						 * 广告图修改
						 */
						//先删除再插入
						DbUp.upTable("pc_productadpic").dataExec("delete from pc_productadpic where product_code=:product_code", new MDataMap("product_code", product.getProductCode()));
						if (product.getPcAdpicList() != null && product.getPcAdpicList().size() > 0) {
							for(PcProductAdpic adpic : product.getPcAdpicList()) {
								MDataMap map = new MDataMap();
								map.put("product_code", product.getProductCode());
								map.put("pic_url", adpic.getPic_url());
								map.put("start_date", adpic.getStart_date());
								map.put("end_date", adpic.getEnd_date());
								map.put("ord_no", String.valueOf(adpic.getOrd_no()));
								DbUp.upTable("pc_productadpic").dataInsert(map);
							}
						}
						//=================================================================

						MUserInfo userInfo = null;
						String manageCode = "";
						String userCode = "";
						if (UserFactory.INSTANCE != null) {
							try {
								userInfo = UserFactory.INSTANCE.create();
							} catch (Exception e) {
								// TODO: handle exception
							}

							if (userInfo != null) {
								manageCode = userInfo.getManageCode();
								userCode = userInfo.getUserCode();
								if (userCode == null || "".equals(userCode)) {
									userCode = manageCode;
								}
							}
						}

						pcProdcutflow.setUpdator(userCode);
						product.setPcProdcutflow(pcProdcutflow);

						MDataMap prodcutData = null;

						prodcutData = DbUp.upTable("pc_productinfo").one("product_code", product.getProductCode());

						ProductChangeFlag pcf = new ProductChangeFlag();

						/**
						 * 2015-08-07
						 * 15:02:30惠家有后台修改sku信息存在不保存到数据库bug，注释掉一下这段代码恢复正常
						 */
						// if(!prodcutData.get("product_code_old").equals("")){
						// pcf.setChangeSkuPropertyMain(false);
						//// pcf.setChangeSkuPropertySub(false);
						// pcf.setChangeProductSku(false);
						// }else{
						// pcf.setChangeSkuPropertyMain(false);
						//// pcf.setChangeProductSku(false);
						// }

						pcf.setOldPicUrl(StringUtils.isBlank(prodcutData.get("mainpic_url")) ? ""
								: prodcutData.get("mainpic_url"));
						pcf.setOldProductName(StringUtils.isBlank(prodcutData.get("product_name")) ? ""
								: prodcutData.get("product_name"));

						// 校验商品名字的外链合法性
						RootResult rr = checkContent(product.getProdutName());
						if (rr.getResultCode() != 1) {
							error.append(rr.getResultMessage());
							WebHelper.unLock(uid);
							return rr.getResultCode();
						}
						// 校验商品简称名字的外链合法性
						RootResult sr = checkContent(product.getProductShortname());
						if (sr.getResultCode() != 1) {
							error.append(sr.getResultMessage());
							WebHelper.unLock(uid);
							return sr.getResultCode();
						}
						// 校验商品描述的外链合法性
						// if(product.getDescription()!=null){
						// rr =
						// checkContent(product.getDescription().getDescriptionInfo());
						// if(rr.getResultCode() != 1){
						// error.append(rr.getResultMessage());
						// WebHelper.unLock(uid);
						// return rr.getResultCode();
						// }
						// }
						// 校验商品广告语的外链合法性
						RootResult pa = checkContent(product.getProductAdv());
						if (pa.getResultCode() != 1) {
							error.append(pa.getResultMessage());
							WebHelper.unLock(uid);
							return pa.getResultCode();
						}
						// 校验商品属性外链的合法性
						if (product.getPcProductpropertyList() != null) {
							for (PcProductproperty ppp : product.getPcProductpropertyList()) {
								rr = checkContent(ppp.getPropertyKey());
								if (rr.getResultCode() != 1) {
									error.append(rr.getResultMessage());
									WebHelper.unLock(uid);
									return rr.getResultCode();
								}

								rr = checkContent(ppp.getPropertyValue());
								if (rr.getResultCode() != 1) {
									error.append(rr.getResultMessage());
									WebHelper.unLock(uid);
									return rr.getResultCode();
								}
							}
						}

						// 更新最大值和最小值的 minPrice 和 maxPrice
						if (product.getProductSkuInfoList() != null && !product.getProductSkuInfoList().isEmpty()) {
							// 检查价格是否符合规则
							// 1) 商品的市场价格必须不小于商品sku的销售价格. 2)商品的sku成本价小于销售价
							// 商品的sku库存和销售价格不能小于 0 ,只能是大于等于 0
							for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
								if (sku.getStockNum() < 0) {
									errorCode = 941901016;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}

								if (sku.getSellPrice().doubleValue() < 0) {
									errorCode = 941901017;
									error.append(bInfo(errorCode));
									WebHelper.unLock(uid);
									return errorCode;
								}

								if (sku.getSkuPicUrl() == null) {
									sku.setSkuPicUrl("");
								}

								if (sku.getSkuKey() == null) {
									sku.setSkuKey("");
								}

								if (sku.getSkuValue() == null) {
									sku.setSkuValue("");
								}

								if (sku.getSellProductcode() == null) {
									sku.setSellProductcode("");
								}

								if (sku.getSkuName() == null) {
									sku.setSkuName("");
								}
								// 商品的市场价格必须不小于商品sku的销售价格.
								// if
								// (sku.getSellPrice().compareTo(product.getMarketPrice())>0)
								// {
								// errorCode = 941901134;
								// error.append(bInfo(errorCode));
								// WebHelper.unLock(uid);
								// return errorCode;
								// }
								// 商品的sku成本价小于销售价
								// if
								// (sku.getSellPrice().compareTo(sku.getCostPrice())<=0)
								// {
								// errorCode = 941901135;
								// error.append(bInfo(errorCode));
								// WebHelper.unLock(uid);
								// return errorCode;
								// }
							}
							int size = product.getProductSkuInfoList().size();

							BigDecimal tempMin = BigDecimal.ZERO;
							BigDecimal tempMax = BigDecimal.ZERO;
							boolean init = true;
							for (int i = 0; i < size; i++) {
								ProductSkuInfo pic = product.getProductSkuInfoList().get(i);
								if (StringUtils.isBlank(pic.getSkuCode())) {
									continue;
								}
								if (init) {
									tempMin = pic.getSellPrice();
									tempMax = pic.getSellPrice();
									init = false;
								} else {
									if (tempMin.compareTo(pic.getSellPrice()) == 1)
										tempMin = pic.getSellPrice();
									if (tempMax.compareTo(pic.getSellPrice()) == -1)
										tempMax = pic.getSellPrice();
								}
							}
							product.setMinSellPrice(tempMin);
							product.setMaxSellPrice(tempMax);
						}

						ret = UpdateProductTx(product, error, userCode, pcf);

						if (ret == 1) {
							try {
								// 校验输入的数据合法性
								// ProductJmsSupport pjs = new
								// ProductJmsSupport();
								// pjs.onChangeProductText(product.getProductCode());

								// this.genarateJmsStaticPageForProductCode(product.getProductCode());

								PlusHelperNotice.onChangeProductInfo(product.getProductCode());
								// 触发消息队列
								ProductJmsSupport pjs = new ProductJmsSupport();
								pjs.onChangeForProductChangeAll(product.getProductCode());
							} catch (Exception ex) {

							}

						}
						
						// 560添加/修改商品分类属性
						List<PropertiesProductRelation> pprList = product.getPprList();
						String ppr_upDateTime = FormatHelper.upDateTime();
						if(null != pprList && pprList.size() > 0) {
							for (PropertiesProductRelation propertiesProductRelation : pprList) {
								String ppr_product_code = propertiesProductRelation.getProduct_code();
								String properties_code = propertiesProductRelation.getProperties_code();
								String properties_value_code = propertiesProductRelation.getProperties_value_code();
								String properties_value = propertiesProductRelation.getProperties_value();
								if(StringUtils.isEmpty(ppr_product_code) || StringUtils.isEmpty(properties_code)) {
									continue;
								}
								// 属性信息
								MDataMap p_key = DbUp.upTable("uc_properties_key").one("properties_code",properties_code,"is_delete","0");
								// 查询商品是否有该属性
								MDataMap ppr = DbUp.upTable("uc_properties_product_relation").one("product_code",ppr_product_code,"properties_code",properties_code);
								
								MDataMap pprMap = new MDataMap();
								pprMap.put("product_code", ppr_product_code);
								pprMap.put("properties_code", properties_code);
								if(null != ppr) { // 有该属性,说明是修改
									pprMap.put("update_time", ppr_upDateTime);
									if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
										if(StringUtils.isEmpty(properties_value_code)) {
											continue;
										}
										pprMap.put("properties_value_code", properties_value_code);
										pprMap.put("properties_value", "");
										DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");
									}else { // 自定义
										if(StringUtils.isEmpty(properties_value)) {
											continue;
										}
										// 验证属性值编号在 uc_properties_value 中是否存在:不存在则修改属性值;存在则新建属性值
										MDataMap pv = DbUp.upTable("uc_properties_value").one("properties_value_code",ppr.get("properties_value_code"),"is_delete","0");
										if(null != pv) { // 存在
											properties_value_code = WebHelper.upCode("PV");
											pprMap.put("properties_value_code", properties_value_code);
											pprMap.put("properties_value", properties_value);
											DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");										
										}else { // 不存在
											pprMap.put("properties_value", properties_value);
											DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value,update_time", "product_code,properties_code");										
										}
									}
								}else { // 没有该属性,直接添加
									if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
										if(StringUtils.isEmpty(properties_value_code)) {
											continue;
										}
										pprMap.put("properties_value_code", properties_value_code);
										pprMap.put("properties_value", "");
									}else { // 自定义
										if(StringUtils.isEmpty(properties_value)) {
											continue;
										}
										// 新生成一个属性值code
										properties_value_code = WebHelper.upCode("PV");
										pprMap.put("properties_value_code", properties_value_code);
										pprMap.put("properties_value", properties_value);
									}
									pprMap.put("create_time", ppr_upDateTime);
									DbUp.upTable("uc_properties_product_relation").dataInsert(pprMap);
								}
							}
						}
						
					}

				}
				WebHelper.unLock(uid);
				return ret;
			}
		}
	}

	public int productManageForUpdate(PcProductinfo product, StringBuffer error) {

		// 调用 添加订单的存储过程
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
		params.add(new SqlOutParameter("error", Types.VARCHAR));

		ProductCallableStatement cscc = new ProductCallableStatement(product, 1);

		DbTemplate dt = DbUp.upTable("pc_skuinfo").upTemplate();
		Map<String, Object> outValues = dt.getJdbcOperations().call(cscc, params);

		String returnCode = outValues.get("outFlag").toString();
		if (Integer.parseInt(returnCode) != SkuCommon.SuccessFlag) {
			if (error != null)
				error.append(bInfo(Integer.parseInt(returnCode), outValues.get("error").toString()));
		} else {
			// 校验输入的数据合法性
			ProductJmsSupport pjs = new ProductJmsSupport();
			pjs.onChangeProductText(product.getProductCode());
		}

		return Integer.parseInt(returnCode);

	}

	/**
	 * 操作 商品
	 * 
	 * @param product
	 * @param type
	 *            1 创建 2 更新sku库存 3更新sku价格 4下架商品 5下架sku 6更新商品图片 7更新市场价格。 8更新sku商品
	 *            9创建sku商品
	 * @return
	 */
	public RootResult operate(PcProductinfo product, int type) {

		RootResult rr = new RootResult();

		try {
			if (product == null) {
				rr.setResultCode(941901041);
				rr.setResultMessage(bInfo(941901041));
			} else {
				// 校验输入的数据合法性
				ProductJmsSupport pjs = new ProductJmsSupport();
				if (type == 1) {// 创建商品

					StringBuffer error = new StringBuffer();
					int retCode = this.AddProduct(product, error);

					rr.setResultCode(retCode);
					rr.setResultMessage(error.toString());

				} else if (type == 2 || type == 5) {// 修改sku库存
					String sql = "";

					if (product.getProductSkuInfoList() == null || product.getProductSkuInfoList().size() == 0) {
						rr.setResultCode(941901042);
						rr.setResultMessage(bInfo(941901042));
					} else {
						MDataMap mDataMap = null;
						for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
							sql = "update pc_skuinfo SET stock_num=:stock_num WHERE sku_code=:sku_code ";
							mDataMap = new MDataMap();
							if (type == 2) {
								mDataMap.put("stock_num", String.valueOf(sku.getStockNum()));
							} else {
								mDataMap.put("stock_num", "0");
							}

							mDataMap.put("sku_code", sku.getSkuCode());
							DbUp.upTable("pc_skuinfo").dataExec(sql, mDataMap);
							pjs.onChangeForSkuChangeStock(sku.getSkuCode());
						}
					}
				} else if (type == 3) {// 修改sku价格
					String sql = "";

					if (product.getProductSkuInfoList() == null || product.getProductSkuInfoList().size() == 0) {
						rr.setResultCode(941901042);
						rr.setResultMessage(bInfo(941901042));
					} else {
						MDataMap mDataMap = null;
						for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
							sql = "update pc_skuinfo SET sell_price=:sell_price WHERE sku_code=:sku_code ";
							mDataMap = new MDataMap();
							mDataMap.put("sell_price", String.valueOf(sku.getSellPrice()));
							mDataMap.put("sku_code", sku.getSkuCode());
							DbUp.upTable("pc_skuinfo").dataExec(sql, mDataMap);
							pjs.onChangeForSkuChangePrice(sku.getSkuCode());
						}
					}
				} else if (type == 4) {// 下架商品
					String sql = "";

					/*
					 * if (product.getProductSkuInfoList() == null ||
					 * product.getProductSkuInfoList().size() == 0) {
					 * rr.setResultCode(941901042);
					 * rr.setResultMessage(bInfo(941901042)); } else {
					 */
					MDataMap mapParam = new MDataMap();
					mapParam.put("product_code", product.getProductCode());
					mapParam.put("product_status", "4497153900060003");
					sql = "update pc_productinfo set product_status=:product_status where product_code=:product_code";
					DbUp.upTable("pc_productinfo").dataExec(sql, mapParam);

					this.genarateJmsStaticPageForProductCode(product.getProductCode());

					/* } */
				} else if (type == 6 || type == 7) {

					PcProductinfo newP = this.getProduct(product.getProductCode());

					if (newP == null) {
						rr.setResultCode(941901041);
						rr.setResultMessage(bInfo(941901041));
					} else {

						if (type == 6) {
							newP.setPcPicList(product.getPcPicList());
							if (product.getPcPicList() != null && product.getPcPicList().size() > 0) {
								newP.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
							}
						} else {
							newP.setMarketPrice(product.getMarketPrice());
						}

						StringBuffer error = new StringBuffer();

						rr.setResultCode(this.productManageForUpdate(newP, error));
						rr.setResultMessage(error.toString());
					}
				} else if (type == 8) {
					MDataMap mDataMap = null;
					String sql = "";
					for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
						sql = "update pc_skuinfo SET sku_name=:sku_name,market_price=:market_price WHERE sku_code=:sku_code ";
						mDataMap = new MDataMap();
						mDataMap.put("sku_name", sku.getSkuName());
						mDataMap.put("sku_code", sku.getSkuCode());
						mDataMap.put("market_price", String.valueOf(sku.getMarketPrice()));
						DbUp.upTable("pc_skuinfo").dataExec(sql, mDataMap);
					}

					if (product.getPcProductpropertyList() != null && product.getPcProductpropertyList().size() > 0) {

						for (PcProductproperty ppp : product.getPcProductpropertyList()) {

							if (ppp.getPropertyType().equals("449736200003")
									|| ppp.getPropertyType().equals("449736200004")) {
								mDataMap = new MDataMap();
								mDataMap.put("property_value", ppp.getPropertyValue());
								mDataMap.put("property_key", ppp.getPropertyKey());
								mDataMap.put("sku_code", product.getProductSkuInfoList().get(0).getSkuCode());

								String sqlExec = "update pc_productproperty set property_value=:property_value where property_key=:property_key and product_code in "
										+ "(select product_code from pc_skuinfo where sku_code=:sku_code)";

								DbUp.upTable("pc_productproperty").dataExec(sqlExec, mDataMap);
							}

						}
					}

				} else if (type == 9) {

					// 查询sku对应商品的商户类型 2016-11-14 zhy
					Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
							"SELECT u.uc_seller_type FROM usercenter.uc_seller_info_extend AS u LEFT JOIN productcenter.pc_productinfo as p ON u.small_seller_code = p.small_seller_code WHERE p.product_code =:product_code",
							new MDataMap("product_code", product.getProductCode()));
					String code_start = "";
					if (seller != null) {
						String seller_type = seller.get("uc_seller_type").toString();
						code_start = bConfig("productcenter.sku" + seller_type);
					}
					// 判断是否有此sku
					MDataMap mDataMap = new MDataMap();

					if (product.getPcProductpropertyList() == null || product.getPcProductpropertyList().size() > 0) {

						// 初始化属性
						this.InitProductProperty(product.getPcProductpropertyList(), product);

						String sqlWhere = "";
						int i = 0;
						for (PcProductproperty ppp : product.getPcProductpropertyList()) {
							if (ppp.getPropertyType().equals("449736200001")
									|| ppp.getPropertyType().equals("449736200002")) {
								mDataMap.put("sku_key" + i, "%" + ppp.getPropertyCode() + "%");
								sqlWhere += " sku_key like :sku_key" + i + " and";
							}
							i++;
						}
						if (sqlWhere.length() > 3) {
							mDataMap.put("product_code", product.getProductCode());
							sqlWhere += " product_code=:product_code ";

							int count = DbUp.upTable("pc_skuinfo").dataCount(sqlWhere, mDataMap);

							if (count >= 1) {
								rr.setResultCode(941901045);
								rr.setResultMessage(bInfo(941901045));
							} else {

								for (ProductSkuInfo psi : product.getProductSkuInfoList()) {

									mDataMap = new MDataMap();

//									psi.setSkuCode(WebHelper.upCode(ProductService.SKUHead));
									//修改sku编码前缀获取方式为根据商户类型读取配置文件获取编码前缀 2016-11-14 zhy
									psi.setSkuCode(WebHelper.upCode(code_start));
									mDataMap.put("sku_code", psi.getSkuCode());
									mDataMap.put("product_code", product.getProductCode());
									mDataMap.put("sell_price", String.valueOf(psi.getSellPrice()));
									mDataMap.put("market_price", String.valueOf(psi.getMarketPrice()));
									mDataMap.put("stock_num", String.valueOf(psi.getStockNum()));
									mDataMap.put("security_stock_num", String.valueOf(psi.getSecurityStockNum()));

									MDataMap productforpic = DbUp.upTable("pc_productinfo").one("product_code",
											product.getProductCode());
									if (productforpic != null)
										mDataMap.put("sku_picurl", productforpic.get("mainpic_url"));

									mDataMap.put("sku_name", psi.getSkuName());
									mDataMap.put("sell_productcode",
											(psi.getSellProductcode() == null ? "" : psi.getSellProductcode()));
									mDataMap.put("seller_code", product.getSellerCode());

									String skuKey = "";
									String skuValue = "";
									for (PcProductproperty ppp : product.getPcProductpropertyList()) {
										if (ppp.getPropertyType().equals("449736200001")
												|| ppp.getPropertyType().equals("449736200002")) {
											if (skuKey.equals("")) {
												skuKey += ppp.getPropertyKeycode() + "=" + ppp.getPropertyCode();
												skuValue += ppp.getPropertyKey() + "=" + ppp.getPropertyValue();
											} else {
												skuKey += "&" + ppp.getPropertyKeycode() + "=" + ppp.getPropertyCode();
												skuValue += "&" + ppp.getPropertyKey() + "=" + ppp.getPropertyValue();
											}
										}
										i++;
									}

									mDataMap.put("sku_key", skuKey);
									mDataMap.put("sku_keyvalue", skuValue);
									//
									DbUp.upTable("pc_skuinfo").dataInsert(mDataMap);
								}

								this.genarateJmsStaticPageForProductCode(product.getProductCode());

							}

						} else {
							rr.setResultCode(941901044);
							rr.setResultMessage(bInfo(941901044));
						}

					} else {
						rr.setResultCode(941901044);
						rr.setResultMessage(bInfo(941901044));
					}
				} else {
					rr.setResultCode(941901043);
					rr.setResultMessage(bInfo(941901043));
					// 941901043
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			rr.setResultCode(941901046);
			rr.setResultMessage(bInfo(941901046, e.getMessage()));
		}

		return rr;
	}

	/**
	 * 添加sku信息
	 * 
	 * @param psi
	 * @return
	 */
	public RootResult addSku(ProductSkuInfo psi) {
		RootResult ret = new RootResult();
		if (psi == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}

		if (psi.getProductCode().equals("")) {
			ret.setResultCode(941901055);
			ret.setResultMessage(bInfo(941901055));
			return ret;
		}

		String keyName = "";
		if (psi.getSkuKey().equals("")) {

		} else {

			String[] skuKeyAry = psi.getSkuKey().split("&");
			String[] skuValueAry = psi.getSkuValue().split("&");

			if (skuKeyAry == null || skuValueAry == null) {
				ret.setResultCode(941901067);
				ret.setResultMessage(bInfo(941901067));
				return ret;
			}

			MDataMap mdKey = new MDataMap();
			mdKey = mdKey.inUrlParams(psi.getSkuKey());
			MDataMap mdValue = new MDataMap();
			mdValue = mdValue.inUrlParams(psi.getSkuValue());
			// System.out.println(md.get("a"));
			// System.out.println(md.get("c"));

			if (skuKeyAry.length != skuValueAry.length) {
				ret.setResultCode(941901067);
				ret.setResultMessage(bInfo(941901067));
				return ret;
			}

			List<String> listKey = new ArrayList<String>();
			List<String> listValue = new ArrayList<String>();

			for (int i = 0; i < skuKeyAry.length; i++) {
				listKey.add(skuKeyAry[i].split("=")[0]);
				listValue.add(skuValueAry[i].split("=")[0]);
			}

			if (listKey.size() != listValue.size()) {
				ret.setResultCode(941901067);
				ret.setResultMessage(bInfo(941901067));
				return ret;
			} else {
				List<PcProductproperty> pcList = new ArrayList<PcProductproperty>();

				for (int i = 0; i < listKey.size(); i++) {
					PcProductproperty ppp = new PcProductproperty();

					ppp.setProductCode(psi.getProductCode());

					ppp.setPropertyKeycode(listKey.get(i));
					ppp.setPropertyCode(mdKey.get(listKey.get(i)));

					ppp.setPropertyKey(listValue.get(i));
					ppp.setPropertyValue(mdValue.get(listValue.get(i)));

					if (mdKey.get(listKey.get(i)).substring(0, 12).equals(ProductService.ColorHead))
						ppp.setPropertyType("449736200001");
					else
						ppp.setPropertyType("449736200002");

					pcList.add(ppp);

					keyName += " " + mdValue.get(listValue.get(i));

					// 校验当前的keycode ，如果是已经存在的
					MDataMap mdProperty = DbUp.upTable("pc_productproperty").one("product_code", psi.getProductCode(),
							"property_keycode", listKey.get(i), "property_value", mdValue.get(listValue.get(i)));

					// 如果根据当前的 product_code，property_keycode，property_value
					// 来匹配，有的话，取出当前的 property_code
					if (mdProperty != null) {
						if (mdProperty.get("property_code").equals(ppp.getPropertyCode())) {

						} else {// 如果 property_code 不同，则替换当前的 property_code
							psi.setSkuKey(
									psi.getSkuKey().replaceAll(ppp.getPropertyCode(), mdProperty.get("property_code")));
						}
					} else {
						MDataMap mDataMap = new MDataMap();

						mDataMap.put("product_code", psi.getProductCode());
						mDataMap.put("property_keycode", ppp.getPropertyKeycode());
						mDataMap.put("property_type", ppp.getPropertyType());

						MDataMap maxProperoty = DbUp.upTable("pc_productproperty").oneWhere(
								"max(property_code) as property_code", "", "", "product_code", psi.getProductCode(),
								"property_keycode", ppp.getPropertyKeycode());

						String maxCode = "";
						// 如果当前的最大值不存在，则自动生成
						if (maxProperoty == null || maxProperoty.get("property_code").equals("")) {
							if (mdKey.get(listKey.get(i)).substring(0, 12).equals(ProductService.ColorHead))
								maxCode = WebHelper.upCode(ProductService.ColorHead);
							else
								maxCode = WebHelper.upCode(ProductService.MainHead);
						} else {// 否则，如果是以 4497 开头的，则 加100，否则还是自动生成
							if (maxProperoty.get("property_code").substring(0, 4) == "4497") {
								String tempFirst = maxCode.substring(0, maxCode.length() - 4);
								int value = Integer.parseInt(maxCode.substring(maxCode.length() - 4, maxCode.length()))
										+ 100;

								if (value >= 1000) {
									maxCode = tempFirst + value;
								} else {
									maxCode = tempFirst + "0" + value;
								}

							} else {
								if (mdKey.get(listKey.get(i)).substring(0, 12).equals(ProductService.ColorHead))
									maxCode = WebHelper.upCode(ProductService.ColorHead);
								else
									maxCode = WebHelper.upCode(ProductService.MainHead);
							}
						}

						mDataMap.put("property_code", maxCode);
						mDataMap.put("property_key", ppp.getPropertyKey());
						mDataMap.put("property_value", ppp.getPropertyValue());
						if (ppp.getPropertyType().equals("449736200001")
								|| ppp.getPropertyType().equals("449736200002")) {
							DbUp.upTable("pc_productproperty").dataInsert(mDataMap);
						}

						psi.setSkuKey(psi.getSkuKey().replaceAll(ppp.getPropertyCode(), maxCode));
					}

				}
				// 添加属性
				// this.InitProductPropertyForCode(pcList,
				// psi.getProductCode());
			}
		}

		MUserInfo userInfo = null;
		String manageCode = "system";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
				if (manageCode == null || "".equals(manageCode)) {
					manageCode = userInfo.getUserCode();
				}
			}
		}

		// 校验当前的skuKey是否存在!
		int count = DbUp.upTable("pc_skuinfo").count("product_code", psi.getProductCode(), "sku_key",
				psi.getSkuKey().trim());

		if (psi.getSkuKey().equals("") && count >= 1) {
			ret.setResultCode(941901066);
			ret.setResultMessage(bInfo(941901066));
			return ret;
		}

		if (count >= 1) {
			ret.setResultCode(941901057);
			ret.setResultMessage(bInfo(941901057));
			return ret;
		}

		// skuName需要重新构建
		MDataMap dm = new MDataMap();
		dm.put("product_code", psi.getProductCode());
		MDataMap one = DbUp.upTable("pc_productinfo").oneWhere("mainpic_url,product_name", "", "", "product_code",
				psi.getProductCode());
		if (one != null) {
			String productName = one.get("product_name");
			String mainPicUrl = one.get("mainpic_url");

			if (productName != null) {
				psi.setSkuName(productName + " " + keyName);
			}

			// 如果商品的sku图片为空,则取商品的主图
			if (psi.getSkuPicUrl() == null || psi.getSkuPicUrl().equals("")) {
				if (mainPicUrl != null) {
					psi.setSkuPicUrl(mainPicUrl.toString());
				}
			}
		} else {
			ret.setResultCode(941901055);
			ret.setResultMessage(bInfo(941901055));
			return ret;
		}

		// 校验商品广告语的外链合法性
		ret = checkContent(psi.getSkuAdv());
		if (ret.getResultCode() != 1) {
			return ret;
		}
		// 沙皮狗保存库存
		if (AppConst.MANAGE_CODE_CDOG.equals(psi.getSellerCode())) {
			List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
			ScStoreSkunum store = new ScStoreSkunum();
			store.setBatchCode("");
			store.setSkuCode(psi.getSkuCode());
			store.setStockNum(Long.parseLong(psi.getStockNum() + ""));
			store.setStoreCode(AppConst.CDOG_STORE_CODE);
			scStoreSkunumList.add(store);
			psi.setScStoreSkunumList(scStoreSkunumList);
		}

		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");
		try {
			txs.addSku(psi, ret, manageCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret.setResultCode(941901049);
			ret.setResultMessage(bInfo(941901049, e.getMessage()));
		}
		try {

			// 更新最大值和最小值的 minPrice 和 maxPrice

			DbUp.upTable("pc_productinfo")
					.dataExec(
							"UPDATE pc_productinfo SET min_sell_price=(SELECT MIN(sell_price) FROM pc_skuinfo  where product_code='"
									+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
							null);
			DbUp.upTable("pc_productinfo")
					.dataExec(
							"UPDATE pc_productinfo SET max_sell_price=(SELECT MAX(sell_price) FROM pc_skuinfo  where product_code='"
									+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
							null);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			this.genarateJmsStaticPageForSkuCode(psi.getSkuCode());
		} catch (Exception ex) {

		}

		return ret;
	}

	/**
	 * 更新sku基本信息
	 * 
	 * @param psi
	 * @return
	 */
	public RootResult updateSkuBase(ProductSkuInfo psi) {
		RootResult ret = new RootResult();

		if (psi == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}

		if (psi.getUid().equals("")) {
			ret.setResultCode(941901056);
			ret.setResultMessage(bInfo(941901056));
			return ret;
		}

		MDataMap md = DbUp.upTable("pc_skuinfo").one("uid", psi.getUid());

		if (md == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}
		psi.setProductCode(md.get("product_code"));
		psi.setSkuCode(md.get("sku_code"));

		MUserInfo userInfo = null;
		String manageCode = "system";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
			}
		}

		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.updateSkuBase(psi, ret, manageCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret.setResultCode(941901049);
			ret.setResultMessage(bInfo(941901049, e.getMessage()));
		}

		try {
			// 重新获取价格
			ProductJmsSupport pjs = new ProductJmsSupport();
			pjs.onChangeForSkuChangeAll(psi.getSkuCode());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {

			// 更新最大值和最小值的 minPrice 和 maxPrice

			DbUp.upTable("pc_productinfo")
					.dataExec(
							"UPDATE pc_productinfo SET min_sell_price=(SELECT MIN(sell_price) FROM pc_skuinfo  where product_code='"
									+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
							null);
			DbUp.upTable("pc_productinfo")
					.dataExec(
							"UPDATE pc_productinfo SET max_sell_price=(SELECT MAX(sell_price) FROM pc_skuinfo  where product_code='"
									+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
							null);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	public RootResult updateSkuOther(ProductSkuInfo psi) {
		RootResult ret = new RootResult();

		if (psi == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}

		if (psi.getUid().equals("")) {
			ret.setResultCode(941901056);
			ret.setResultMessage(bInfo(941901056));
			return ret;
		}

		MDataMap md = DbUp.upTable("pc_skuinfo").one("uid", psi.getUid());

		if (md == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}
		psi.setProductCode(md.get("product_code"));
		psi.setSkuCode(md.get("sku_code"));

		MUserInfo userInfo = null;
		String operator = "system";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				operator = userInfo.getUserCode();
				if (operator == null || "".equals(operator)) {
					operator = userInfo.getUserCode();
				}
			}
		}

		// 如果商品的sku图片为空,则取商品的主图
		if (psi.getSkuPicUrl() == null || psi.getSkuPicUrl().equals("")) {

			MDataMap dm = new MDataMap();
			dm.put("product_code", psi.getProductCode());
			Object mainPicUrl = DbUp.upTable("pc_productinfo").dataGet("mainpic_url", "product_code=:product_code", dm);

			if (mainPicUrl != null) {
				psi.setSkuPicUrl(mainPicUrl.toString());
			}

		}

		// 校验商品名字的外链合法性
		ret = checkContent(psi.getSkuName());
		if (ret.getResultCode() != 1) {
			return ret;
		}

		// 校验商品名字的外链合法性
		ret = checkContent(psi.getSkuAdv());
		if (ret.getResultCode() != 1) {
			return ret;
		}

		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.updateSkuOther(psi, ret, operator);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret.setResultCode(941901049);
			ret.setResultMessage(bInfo(941901049, e.getMessage()));
		}

		// 更新商品表的更新时间 edit 2014/10/21 17:57 修改sku时更新商品修改时间
		MDataMap updateMap = new MDataMap();
		updateMap.put("update_time", DateUtil.getSysDateTimeString());
		updateMap.put("product_code", psi.getProductCode());
		DbUp.upTable("pc_productinfo").dataUpdate(updateMap, "update_time", "product_code");

		try {
			// this.genarateJmsStaticPageForSkuCode(psi.getProductCode());
			ProductJmsSupport pjs = new ProductJmsSupport();
			pjs.onChangeForSkuChangeAll(psi.getProductCode());
		} catch (Exception ex) {

		}

		return ret;
	}

	/**
	 * 删除sku
	 * 
	 * @param psi
	 * @return
	 */
	public RootResult deleteSku(ProductSkuInfo psi) {

		RootResult ret = new RootResult();

		MDataMap md = DbUp.upTable("pc_skuinfo").one("uid", psi.getUid());

		if (md == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}
		psi.setProductCode(md.get("product_code"));
		psi.setSkuCode(md.get("sku_code"));

		MUserInfo userInfo = null;
		String manageCode = "system";

		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();

			}
		}

		if (!md.get("seller_code").equals(manageCode)) {
			ret.setResultCode(941901064);
			ret.setResultMessage(bInfo(941901064));
			return ret;
		}

		// 至少保留一个sku
		int count = DbUp.upTable("pc_skuinfo").count("product_code", psi.getProductCode());
		// 不允许删除
		if (count == 1) {
			ret.setResultCode(941901052);
			ret.setResultMessage(bInfo(941901052));
			return ret;
		}

		count = DbUp.upTable("oc_orderdetail").count("sku_code", psi.getSkuCode());
		// 如果订单明细里面有，不允许删除
		if (count >= 1) {
			ret.setResultCode(941901053);
			ret.setResultMessage(bInfo(941901053));
			return ret;
		}

		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.deleteSku(psi, ret, manageCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret.setResultCode(941901049);
			ret.setResultMessage(bInfo(941901049, e.getMessage()));
		}

		try {
			this.genarateJmsStaticPageForSkuCode(psi.getSkuCode());
		} catch (Exception ex) {

		}

		// 删除关键属性
		try {

			this.RefreshMainProperty(psi.getProductCode());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	/**
	 * 改变商品库存
	 * 
	 * @param psi
	 * @return
	 */
	public RootResult changeProductSkuStock(ProductSkuInfo psi, String appCode) {

		RootResult ret = new RootResult();

		if (psi == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}

		if (psi.getUid().equals("")) {
			ret.setResultCode(941901056);
			ret.setResultMessage(bInfo(941901056));
			return ret;
		}

		MDataMap md = DbUp.upTable("pc_skuinfo").one("uid", psi.getUid());

		if (md == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}
		psi.setProductCode(md.get("product_code"));
		psi.setSkuCode(md.get("sku_code"));

		MUserInfo userInfo = null;
		String manageCode = "system";
		String userCode = "system";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
				userCode = userInfo.getUserCode();
				if (manageCode == null || "".equals(manageCode)) {
					manageCode = userInfo.getUserCode();
				}
			}
		}

		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.updateSkuStock(psi, ret, userCode, appCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ret.getResultCode() == 1) {
			try {

				// 更新最大值和最小值的 minPrice 和 maxPrice

				DbUp.upTable("pc_productinfo").dataExec(
						"UPDATE pc_productinfo SET min_sell_price=(SELECT MIN(sell_price) FROM pc_skuinfo  where product_code='"
								+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
						null);
				DbUp.upTable("pc_productinfo").dataExec(
						"UPDATE pc_productinfo SET max_sell_price=(SELECT MAX(sell_price) FROM pc_skuinfo  where product_code='"
								+ psi.getProductCode() + "') where product_code='" + psi.getProductCode() + "';",
						null);
				// 更新商品表的更新时间 edit 2014/10/21 17:57 修改sku时更新商品修改时间
				MDataMap updateMap = new MDataMap();
				updateMap.put("update_time", DateUtil.getSysDateTimeString());
				updateMap.put("product_code", psi.getProductCode());
				DbUp.upTable("pc_productinfo").dataUpdate(updateMap, "update_time", "product_code");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				PlusHelperNotice.onChangeProductInfo(psi.getProductCode());
				// 重新获取价格
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeForSkuChangeAll(psi.getProductCode());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * 改变商品库存(商户后台修改sku库存调用)
	 * 
	 * @param psi
	 * @return
	 */
	public RootResult changeProductSkuStockForCshop(ProductSkuInfo psi, String appCode) {

		RootResult ret = new RootResult();

		if (psi == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}

		if (psi.getUid().equals("")) {
			ret.setResultCode(941901056);
			ret.setResultMessage(bInfo(941901056));
			return ret;
		}

		MDataMap md = DbUp.upTable("pc_skuinfo").one("uid", psi.getUid());

		if (md == null) {
			ret.setResultCode(941901054);
			ret.setResultMessage(bInfo(941901054));
			return ret;
		}
		psi.setProductCode(md.get("product_code"));
		psi.setSkuCode(md.get("sku_code"));

		// MUserInfo userInfo = null;
		// String manageCode = "system";
		// String userCode = "system";
		// if (UserFactory.INSTANCE != null) {
		// try {
		// userInfo = UserFactory.INSTANCE.create();
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		//
		// if (userInfo != null) {
		// manageCode = userInfo.getManageCode();
		// userCode = userInfo.getUserCode();
		// if(manageCode==null||"".equals(manageCode)){
		// manageCode = userInfo.getUserCode();
		// }
		// }
		// }

		TxStockService txs = BeansHelper.upBean("bean_com_cmall_systemcenter_txservice_TxStockService");

		try {
			txs.doChangeStock(ret, psi.getStockNum(), md.get("sku_code"), AppConst.THIRD_STORE_CODE);
			if (ret.getResultCode() == 1) {
				PlusHelperNotice.onChangeSkuStock(md.get("sku_code"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 初始化商品属性，如果 pc_productproperty 表中没有，插入,有的勿理会
	 * 
	 * @param pcList
	 */
	public void InitProductProperty(List<PcProductproperty> pcList, PcProductinfo product) {
		this.InitProductProperty(pcList, product.getProductCode());
	}

	private void InitProductProperty(List<PcProductproperty> pcList, String product_code) {
		for (PcProductproperty ppp : pcList) {

			MDataMap mDataMap = new MDataMap();
			String sqlWhere = "product_code=:product_code and property_value=:property_value and property_key=:property_key and property_type=:property_type";

			mDataMap.put("product_code", product_code);
			mDataMap.put("property_value", ppp.getPropertyValue());
			mDataMap.put("property_type", ppp.getPropertyType());
			mDataMap.put("property_key", ppp.getPropertyKey());

			List<MDataMap> listPcproductProperty = DbUp.upTable("pc_productproperty").query("", "", sqlWhere, mDataMap,
					-1, -1);

			if (listPcproductProperty.size() >= 1) {
				ppp.setPropertyCode(listPcproductProperty.get(0).get("property_code"));
				ppp.setPropertyKeycode(listPcproductProperty.get(0).get("property_keycode"));
				continue;
			} else {
				sqlWhere = "product_code=:product_code and property_key=:property_key and property_type=:property_type";
				List<MDataMap> listPcproductPropertyKey = DbUp.upTable("pc_productproperty").query("", "", sqlWhere,
						mDataMap, -1, -1);

				if (listPcproductPropertyKey != null && listPcproductPropertyKey.size() >= 1) {
					ppp.setPropertyKeycode(listPcproductPropertyKey.get(0).get("property_keycode"));
				} else {
					ppp.setPropertyKeycode(WebHelper.upCode(ProductService.MainHead));
				}

				ppp.setPropertyCode(WebHelper.upCode(ProductService.MainHead));

				mDataMap.put("property_keycode", ppp.getPropertyKeycode());
				mDataMap.put("property_code", ppp.getPropertyCode());
				if (ppp.getPropertyType().equals("449736200001") || ppp.getPropertyType().equals("449736200002")) {
					DbUp.upTable("pc_productproperty").dataInsert(mDataMap);
				}
			}
		}
	}

	private void InitProductPropertyForCode(List<PcProductproperty> pcList, String product_code) {
		for (PcProductproperty ppp : pcList) {

			MDataMap mDataMap = new MDataMap();
			String sqlWhere = "product_code=:product_code and property_code=:property_code and property_keycode=:property_keycode and property_type=:property_type";

			mDataMap.put("product_code", product_code);
			mDataMap.put("property_keycode", ppp.getPropertyKeycode());
			mDataMap.put("property_type", ppp.getPropertyType());
			mDataMap.put("property_code", ppp.getPropertyCode());

			List<MDataMap> listPcproductProperty = DbUp.upTable("pc_productproperty").query("", "", sqlWhere, mDataMap,
					-1, -1);

			if (listPcproductProperty.size() >= 1) {
				sqlWhere = "product_code,property_code,property_keycode,property_type";
				// continue;
				mDataMap.put("property_value", ppp.getPropertyValue());
				DbUp.upTable("pc_productproperty").dataUpdate(mDataMap, "property_value", sqlWhere);
			} else {
				mDataMap.put("property_key", ppp.getPropertyKey());
				mDataMap.put("property_value", ppp.getPropertyValue());
				if (ppp.getPropertyType().equals("449736200001") || ppp.getPropertyType().equals("449736200002")) {
					DbUp.upTable("pc_productproperty").dataInsert(mDataMap);
				}
			}
		}
	}

	/**
	 * @param product
	 *            商品信息
	 * @param error
	 *            错误内容
	 * @param flag
	 *            0 添加 1 修改
	 * @return
	 */
	private int productManage(PcProductinfo product, StringBuffer error, int flag) {

		int errorCode = 0;
		/**
		 * 修改商户编码前缀修改，修改判断商品商户编码 2016-11-24 zhy
		 */
		String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
		boolean codeFlag = StringUtils.isNotBlank(seller_type);
		// 根据商户编码查询商户类型 2016-11-14 zhy
		MDataMap seller = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "", "small_seller_code",
				product.getSmallSellerCode());
		if (flag == 0 || flag == 2) {
			String productCode = product.getProductCode();
			if (flag == 0) {
				// 根据商户类型读取配置文件获取商品编码前缀 2016-11-14 zhy
				String code_start = ProductService.ProductHead;
				if(seller != null){
					code_start = bConfig("productcenter.product" + seller.get("uc_seller_type"));	
				}
				productCode = WebHelper.upCode(code_start);
				// productCode = WebHelper.upCode(ProductService.ProductHead);
				product.setProductCode(productCode);
			}
			if (flag == 0 && !(codeFlag
					&& !product.getSmallSellerCode().equals("SF03KJT")
					&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())))// 惠家有第三方商户的商品不需要设置
			// 设置默认为在售
			{
				product.setFlagSale(1);
			}
			// 如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张
			if ((null == product.getMainPicUrl() || "".equals(product.getMainPicUrl()))
					&& product.getPcPicList() != null && product.getPcPicList().size() > 0) {
				product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
			}
			if (null != product.getDescription() && null != product.getDescription().getKeyword()
					&& !"".equals(product.getDescription().getKeyword())) {
				String[] labelsArr = product.getDescription().getKeyword().trim().split(" ");
				String labelStr = ""; // 将空格替换掉
				for (int i = 0; i < labelsArr.length; i++) {
					if ("".endsWith(labelsArr[i])) {
						continue;
					}
					labelStr += labelsArr[i] + ",";
				}
				product.getDescription().setKeyword(labelStr.substring(0, labelStr.length() - 1)); // 截去最后一个逗号并把商品标签添加到商品描述表中
			}
			if (StringUtils.isNotBlank(product.getBrandCode())) {
				MDataMap brandInfo = DbUp.upTable("pc_brandinfo").oneWhere("brand_name", "", "", "brand_code",
						product.getBrandCode());
				product.setBrandName(brandInfo.get("brand_name"));
			}
			// 商品虚类
			if (null != product.getUsprList() && product.getUsprList().size() > 0) {
				for (int i = 0; i < product.getUsprList().size(); i++) {
					product.getUsprList().get(i).setProductCode(productCode);
					product.getUsprList().get(i).setSellerCode(product.getSellerCode()); // 这个字段应该与虚类所属的sell_code一致，故此不能用small_seller_code
				}
			}
			// 商品实类
			if (null != product.getPcProductcategoryRel()) {
				product.getPcProductcategoryRel().setProductCode(productCode);
				product.getPcProductcategoryRel().setFlagMain(1);
			}
			// 商品扩展信息
			if (null != product.getPcProductinfoExt()) {
				product.getPcProductinfoExt().setProductCode(productCode);
			}
			if (product.getProductSkuInfoList() != null) {
				// 根据商户类型读取配置文件获取sku编码前缀 2016-11-14 zhy
				String code_start = SKUHead;
				if(seller != null){
					code_start = bConfig("productcenter.sku" + seller.get("uc_seller_type"));	
				}
				
				for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
					ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
					sku.setProductCode(productCode);
					// sku.setSkuCode(WebHelper.upCode(SKUHead));
					// 修改sku编码获取方式 2016-11-14
					sku.setSkuCode(WebHelper.upCode(code_start));
					sku.setSellerCode(product.getSellerCode());
					sku.setFlagEnable("1");
					// 如果当前商品的sku图片不存在，则 设置sku图片。
					if (sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")) {
						sku.setSkuPicUrl(product.getMainPicUrl());
					}

					// 商户后台添加商品保存库存信息
					if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
							&& StringUtils.isNotEmpty(product.getSmallSellerCode())
							&& codeFlag
							&& !product.getSmallSellerCode().equals("SF03KJT")) {
						List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
						ScStoreSkunum store = new ScStoreSkunum();
						store.setBatchCode("");
						store.setSkuCode(sku.getSkuCode());
						store.setStockNum(Long.parseLong(sku.getStockNum() + ""));
						store.setStoreCode(AppConst.THIRD_STORE_CODE);
						scStoreSkunumList.add(store);
						sku.setScStoreSkunumList(scStoreSkunumList);
					} else if (AppConst.MANAGE_CODE_CDOG.equals(product.getSellerCode())
							&& AppConst.MANAGE_CODE_CDOG.equals(product.getSmallSellerCode())) { // 沙皮狗保存库存
						List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
						ScStoreSkunum store = new ScStoreSkunum();
						store.setBatchCode("");
						store.setSkuCode(sku.getSkuCode());
						store.setStockNum(Long.parseLong(sku.getStockNum() + ""));
						store.setStoreCode(AppConst.CDOG_STORE_CODE);
						scStoreSkunumList.add(store);
						sku.setScStoreSkunumList(scStoreSkunumList);
					}
				}
			}
		}

		// 商品名字不能为空
		if (product.getProdutName() == null || product.getProdutName().trim().equals("")) {
			errorCode = 941901012;
			error.append(bInfo(errorCode));
			return errorCode;
		}

		/*
		 * if(product.getSellerCode().trim().equals("")){ errorCode=941901014;
		 * error.append(bInfo(errorCode)); return errorCode; }
		 * if(product.getDescription() == null ||
		 * product.getDescription().getDescriptionInfo().trim().equals("")){
		 * errorCode=941901013; error.append(bInfo(errorCode)); return
		 * errorCode; }
		 */
		// 品牌不能为空
		if (flag != 2 && (product.getBrandCode() == null || product.getBrandCode().trim().equals(""))) {
			errorCode = 941901010;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		// 分类不能为空
		if (product.getCategory() == null || product.getCategory().getCategoryCode().equals("")) {
			errorCode = 941901011;
			error.append(bInfo(errorCode));
			return errorCode;
		}

		// 商品的sku数量不能为空!
		if (product.getProductSkuInfoList() == null || product.getProductSkuInfoList().size() == 0) {
			errorCode = 941901015;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		// 检查价格是否符合规则
		// 1) 商品的市场价格必须不小于商品sku的销售价格. 2)商品的sku成本价小于销售价
		// 商品的sku库存和销售价格不能小于 0 ,只能是大于等于 0
		for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
			if (sku.getStockNum() < 0) {
				errorCode = 941901016;
				error.append(bInfo(errorCode));
				return errorCode;
			}

			if (sku.getSellPrice().doubleValue() < 0) {
				errorCode = 941901017;
				error.append(bInfo(errorCode));
				return errorCode;
			}

			if (sku.getSkuPicUrl() == null) {
				sku.setSkuPicUrl("");
			}

			if (sku.getSkuKey() == null) {
				sku.setSkuKey("");
			}

			if (sku.getSkuValue() == null) {
				sku.setSkuValue("");
			}

			if (sku.getSellProductcode() == null) {
				sku.setSellProductcode("");
			}

			if (sku.getSkuName() == null) {
				sku.setSkuName("");
			}

			// 商品的市场价格必须不小于商品sku的销售价格.
			if (sku.getSellPrice().compareTo(product.getMarketPrice()) > 0) {
				errorCode = 941901134;
				error.append(bInfo(errorCode));
				return errorCode;
			}
			// 商品的sku成本价小于销售价
			if (sku.getSellPrice().compareTo(sku.getCostPrice()) <= 0) {
				errorCode = 941901135;
				error.append(bInfo(errorCode));
				return errorCode;
			}
		}

		if (product.getProductSkuInfoList() != null) {
			int size = product.getProductSkuInfoList().size();

			BigDecimal tempMin = new BigDecimal(0.00);
			BigDecimal tempMax = new BigDecimal(0.00);
			for (int i = 0; i < size; i++) {
				ProductSkuInfo pic = product.getProductSkuInfoList().get(i);

				if (i == 0) {
					tempMin = pic.getSellPrice();
					tempMax = pic.getSellPrice();
					// product.setMarketPrice(pic.getMarketPrice());
				} else {
					if (tempMin.compareTo(pic.getSellPrice()) == 1)
						tempMin = pic.getSellPrice();
					if (tempMax.compareTo(pic.getSellPrice()) == -1)
						tempMax = pic.getSellPrice();
				}
			}

			product.setMinSellPrice(tempMin);
			product.setMaxSellPrice(tempMax);
		}

		// if (product.getUsprList() != null && product.getUsprList().size() >
		// 0) {
		// for (UcSellercategoryProductRelation uspr : product.getUsprList()) {
		//
		// MDataMap mDataMap = new MDataMap();
		//
		// mDataMap.put("category_code", uspr.getCategoryCode());
		// mDataMap.put("product_code", product.getProductCode());
		// mDataMap.put("seller_code", product.getSellerCode());
		//
		// DbUp.upTable("uc_sellercategory_product_relation").dataInsert(
		// mDataMap);
		//
		// }
		// }

		PcProductflow pcProdcutflow = new PcProductflow();
		pcProdcutflow.setProductCode(product.getProductCode());
		pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));
		if (codeFlag && !product.getSmallSellerCode().startsWith("SF03KJT")
				&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())) {
			if (flag == 0) {
				pcProdcutflow.setFlowStatus(SkuCommon.ProAddInit);
			} else if (flag == 1) {
				pcProdcutflow.setFlowStatus(SkuCommon.ProUpaInit);
			}
		} else {
			if (flag == 0) {
				pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusInit);
			} else if (flag == 1) {
				pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusXG);
			}
		}
		MUserInfo userInfo = null;
		String manageCode = "";
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
				userCode = userInfo.getUserCode();
				if (manageCode == null || "".equals(manageCode)) {
					manageCode = userCode;
				}
			}
		}

		pcProdcutflow.setUpdator(userCode);
		product.setPcProdcutflow(pcProdcutflow);
		String currentStatus = "";
		if (flag == 0) {
			// 取得当前的商品的状态
			currentStatus = bConfig("productcenter.AddProductStatus");
			// 设置当前的商品的状态
			product.setProductStatus(currentStatus);
			// 沙皮狗项目商品初始状态为已下架
			if (AppConst.MANAGE_CODE_CDOG.equals(product.getSellerCode())) {
				product.setProductStatus("4497153900060003");
			}
			// ProductCheck pc = new ProductCheck();
			// //如果当前上架的商品超过某个数量，则把商品置成下架
			// if(pc.upSalesScopeType(manageCode).equals("")){
			// if(product.getProductStatus().equals("4497153900060002")){
			// product.setProductStatus("4497153900060003");
			// }
			// }
		}

		// 校验商品名字的外链合法性
		RootResult rr = checkContent(product.getProdutName());
		if (rr.getResultCode() != 1) {
			error.append(rr.getResultMessage());
			return rr.getResultCode();
		}
		// 校验商品描述的外链合法性
		// if(product.getDescription()!=null){
		// rr = checkContent(product.getDescription().getDescriptionInfo());
		// if(rr.getResultCode() != 1){
		// error.append(rr.getResultMessage());
		// return rr.getResultCode();
		// }
		// }
		// 校验商品属性外链的合法性
		if (product.getPcProductpropertyList() != null) {
			for (PcProductproperty ppp : product.getPcProductpropertyList()) {
				rr = checkContent(ppp.getPropertyKey());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}

				rr = checkContent(ppp.getPropertyValue());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}
			}
		}

		if (codeFlag && !product.getSmallSellerCode().startsWith("SF03KJT")
				&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())) {
			product.getPcProductinfoExt().setProductCode(product.getProductCode());
			product.getPcProductinfoExt().setPrchType("20");
			product.getPcProductinfoExt().setDlrId(product.getSmallSellerCode());
			// product.getPcProductinfoExt().setDlrNm(UserFactory.INSTANCE.create().getLoginName());
			// 供应商名称
			MDataMap dlrMap = DbUp.upTable("uc_sellerinfo").oneWhere("seller_company_name", "", "", "small_seller_code",
					product.getSmallSellerCode());
			if (null != dlrMap && !dlrMap.isEmpty()) {
				product.getPcProductinfoExt().setDlrNm(dlrMap.get("seller_company_name"));
			} else {
				product.getPcProductinfoExt().setDlrNm(product.getSmallSellerCode());
			}
			product.getPcProductinfoExt().setOaSiteNo(AppConst.THIRD_STORE_CODE);
			product.getPcProductinfoExt().setValidateFlag("Y");
			String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl") + product.getProductCode() + "_1";
			try {
				// 加入审批的流程
				ScFlowMain flow = new ScFlowMain();
				flow.setCreator(userCode);
				// flow.setCurrentStatus("4497153900060005");
				// 修改添加商品跳转节点 2016-06-24 zhy
				// 查询商家是否是跨境商户如果是跨境商户，进入节点跨境商品待审批节点，如果不是进入质检员待审批节点
				//
				if (isCrossBorder(product.getSmallSellerCode())) {
					flow.setCurrentStatus("4497172300160004");
				} else {
					//质检员待审批修改为招商经理待审批 2017-06-05 zhy
					flow.setCurrentStatus("4497172300160015");
				}
				String title = bInfo(941901036, product.getProductCode());
				flow.setFlowTitle(product.getProductCode());
				// flow.setFlowType("449717230011");
				// 修改添加商品跳转节点 2016-06-24 zhy
				flow.setFlowType("449717230016");
				flow.setFlowUrl(preViewUrl);
				flow.setCreator(userCode);
				flow.setOuterCode(product.getProductCode());
				flow.setFlowRemark(title);
				// 添加商品时取消指定审批人 2016-06-27 zhy
				// flow.setNext_operator_id(product.getPcProductinfoExt().getMdId());
				// 创建的审批流程
				(new FlowService()).CreateFlow(flow);
			} catch (Exception e) {
			}
		}
		return this.AddProductTx(product, error, manageCode);
	}

	/**
	 * 惠美丽
	 * 
	 * @param product
	 *            商品信息
	 * @param error
	 *            错误内容
	 * @param flag
	 *            0 添加 1 修改
	 * @return
	 */
	private int productManageForCa(PcProductinfo product, StringBuffer error, int flag) {

		int errorCode = 0;

		if (flag == 0 || flag == 2) {
			String productCode = WebHelper.upCode(ProductService.ProductHead);
			product.setProductCode(productCode);
			if (flag == 0)
				// 设置默认为在售
				product.setFlagSale(1);
			if (product.getProductSkuInfoList() != null) {
				for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
					sku.setProductCode(productCode);
					sku.setSkuCode(WebHelper.upCode(SKUHead));
					sku.setSellerCode(product.getSellerCode());
					// 如果当前商品的sku图片不存在，则 设置sku图片。
					if (sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")) {
						sku.setSkuPicUrl(product.getMainPicUrl());
					}
				}
			}
		}

		// 商品名字不能为空
		if (product.getProdutName() == null || product.getProdutName().trim().equals("")) {
			errorCode = 941901012;
			error.append(bInfo(errorCode));
			return errorCode;
		}

		/*
		 * if(product.getSellerCode().trim().equals("")){ errorCode=941901014;
		 * error.append(bInfo(errorCode)); return errorCode; }
		 * if(product.getDescription() == null ||
		 * product.getDescription().getDescriptionInfo().trim().equals("")){
		 * errorCode=941901013; error.append(bInfo(errorCode)); return
		 * errorCode; }
		 */
		// 品牌不能为空
		// if (product.getBrandCode() == null
		// || product.getBrandCode().trim().equals("")) {
		// errorCode = 941901010;
		// error.append(bInfo(errorCode));
		// return errorCode;
		// }
		// 分类不能为空
		if (product.getCategory() == null || product.getCategory().getCategoryCode().equals("")) {
			errorCode = 941901011;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		// 商品的sku数量不能为空!
		if (product.getProductSkuInfoList() == null || product.getProductSkuInfoList().size() == 0) {
			errorCode = 941901015;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		// 商品的sku库存和销售价格不能小于 0 ,只能是大于等于 0
		for (ProductSkuInfo sku : product.getProductSkuInfoList()) {
			if (sku.getStockNum() < 0) {
				errorCode = 941901016;
				error.append(bInfo(errorCode));
				return errorCode;
			}

			if (sku.getSellPrice().doubleValue() < 0) {
				errorCode = 941901017;
				error.append(bInfo(errorCode));
				return errorCode;
			}

			if (sku.getSkuPicUrl() == null) {
				sku.setSkuPicUrl("");
			}

			if (sku.getSkuKey() == null) {
				sku.setSkuKey("");
			}

			if (sku.getSkuValue() == null) {
				sku.setSkuValue("");
			}

			if (sku.getSellProductcode() == null) {
				sku.setSellProductcode("");
			}

			if (sku.getSkuName() == null) {
				sku.setSkuName("");
			}
		}

		if (product.getProductSkuInfoList() != null) {
			int size = product.getProductSkuInfoList().size();

			BigDecimal tempMin = new BigDecimal(0.00);
			BigDecimal tempMax = new BigDecimal(0.00);
			for (int i = 0; i < size; i++) {
				ProductSkuInfo pic = product.getProductSkuInfoList().get(i);

				if (i == 0) {
					tempMin = pic.getSellPrice();
					tempMax = pic.getSellPrice();
					product.setMarketPrice(pic.getMarketPrice());
				} else {
					if (tempMin.compareTo(pic.getSellPrice()) == 1)
						tempMin = pic.getSellPrice();
					if (tempMax.compareTo(pic.getSellPrice()) == -1)
						tempMax = pic.getSellPrice();
				}
			}

			product.setMinSellPrice(tempMin);
			product.setMaxSellPrice(tempMax);
		}
		// 如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张 edit by 李国杰 start
		if ((null == product.getMainPicUrl() || "".equals(product.getMainPicUrl())) && product.getPcPicList() != null
				&& product.getPcPicList().size() > 0) {
			product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
		}
		if (product.getUsprList() != null && product.getUsprList().size() > 0) {
			for (UcSellercategoryProductRelation uspr : product.getUsprList()) {

				MDataMap mDataMap = new MDataMap();

				mDataMap.put("category_code", uspr.getCategoryCode());
				mDataMap.put("product_code", product.getProductCode());
				mDataMap.put("seller_code", product.getSellerCode());

				DbUp.upTable("uc_sellercategory_product_relation").dataInsert(mDataMap);

			}
		}

		PcProductflow pcProdcutflow = new PcProductflow();
		pcProdcutflow.setProductCode(product.getProductCode());
		pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));
		pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusInit);

		MUserInfo userInfo = null;
		String manageCode = "";
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
				userCode = userInfo.getUserCode();
				if (manageCode == null || "".equals(manageCode)) {
					manageCode = userCode;
				}
			}
		}

		pcProdcutflow.setUpdator(manageCode);
		product.setPcProdcutflow(pcProdcutflow);
		String currentStatus = "";
		if (flag == 0) {
			// 取得当前的商品的状态
			currentStatus = bConfig("productcenter.AddProductStatus");
			// 设置当前的商品的状态
			product.setProductStatus(currentStatus);

			// ProductCheck pc = new ProductCheck();
			// //如果当前上架的商品超过某个数量，则把商品置成下架
			// if(pc.upSalesScopeType(manageCode).equals("")){
			// if(product.getProductStatus().equals("4497153900060002")){
			// product.setProductStatus("4497153900060003");
			// }
			// }

		}

		// 校验商品名字的外链合法性
		RootResult rr = checkContent(product.getProdutName());
		if (rr.getResultCode() != 1) {
			error.append(rr.getResultMessage());
			return rr.getResultCode();
		}
		// 校验商品描述的外链合法性
		// if(product.getDescription()!=null){
		// rr = checkContent(product.getDescription().getDescriptionInfo());
		// if(rr.getResultCode() != 1){
		// error.append(rr.getResultMessage());
		// return rr.getResultCode();
		// }
		// }
		// 校验商品属性外链的合法性
		if (product.getPcProductpropertyList() != null) {
			for (PcProductproperty ppp : product.getPcProductpropertyList()) {
				rr = checkContent(ppp.getPropertyKey());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}

				rr = checkContent(ppp.getPropertyValue());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}
			}
		}

		if (null != product.getDescription() && null != product.getDescription().getKeyword()
				&& !"".equals(product.getDescription().getKeyword())) {
			String[] labelsArr = product.getDescription().getKeyword().trim().split(" ");
			String labelStr = ""; // 将空格替换掉
			for (int i = 0; i < labelsArr.length; i++) {
				if ("".endsWith(labelsArr[i])) {
					continue;
				}
				labelStr += labelsArr[i].trim() + ",";
			}
			product.getDescription().setKeyword(labelStr.substring(0, labelStr.length() - 1)); // 截去最后一个逗号并把商品标签添加到商品描述表中
		}

		return this.AddProductTx(product, error, manageCode);
	}

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {

		RootResult rr = new RootResult();

		if (toStatus.equals("4497153900060002")) {
			MDataMap mapParam = new MDataMap();
			mapParam.put("product_code", outCode);
			mapParam.put("product_status", toStatus);
			mapParam.put("product_status_before", fromStatus);
			String sql = "update pc_productinfo set product_status=:product_status where product_code=:product_code and product_status=:product_status_before";
			DbUp.upTable("pc_productinfo").dataExec(sql, mapParam);

			// 加载商品图片宽高到缓存
			PlusHelperScheduler.sendSchedler(EPlusScheduler.ProductImageWidth, outCode, outCode);
			
			this.genarateJmsStaticPageForProductCode(outCode);
		}
		return rr;
	}

	/**
	 * 生成静态页面 通过商品编号
	 * 
	 * @param productCode
	 */
	public void genarateJmsStaticPageForProductCode(String productCode) {

		MDataMap mapParam = new MDataMap();
		mapParam.put("product_code", productCode);
		// 通知前端生成静态页面
		ProductJmsSupport pjs = new ProductJmsSupport();
		String skuCodes = "";
		List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").query("", "", "product_code=:product_code",
				mapParam, -1, -1);
		int j = productSkuInfoListMap.size();
		for (int i = 0; i < j; i++) {
			if (i == (j - 1)) {
				skuCodes += productSkuInfoListMap.get(i).get("sku_code");
			} else {
				skuCodes += productSkuInfoListMap.get(i).get("sku_code") + ",";
			}
		}
		if (j > 0) {
			String jsonData = "{\"type\":\"sku\",\"data\":\"" + skuCodes + "\"}";
			pjs.OnChangeSku(jsonData);
		}
	}

	/**
	 * 生成静态页面 通过商品编号
	 * 
	 * @param productCode
	 */
	public void genarateJmsStaticPageForSkuCode(String skuCode) {
		ProductJmsSupport pjs = new ProductJmsSupport();
		String jsonData = "{\"type\":\"sku\",\"data\":\"" + skuCode + "\"}";
		pjs.OnChangeSku(jsonData);

	}

	/**
	 * 生成静态页面 通过商品
	 * 
	 * @param product
	 */
	public void genarateJmsStaticPageForProduct(PcProductinfo product) {
		ProductJmsSupport pjs = new ProductJmsSupport();
		// 通知前端生成静态页面
		String skuCodes = "";

		if (product.getProductSkuInfoList() != null) {
			int j = product.getProductSkuInfoList().size();
			for (int i = 0; i < j; i++) {
				if (i == (j - 1)) {
					skuCodes += product.getProductSkuInfoList().get(i).getSkuCode();
				} else {
					skuCodes += product.getProductSkuInfoList().get(i).getSkuCode() + ",";
				}
			}
			if (j > 0) {
				String jsonData = "{\"type\":\"sku\",\"data\":\"" + skuCodes + "\"}";
				pjs.OnChangeSku(jsonData);
			}
		}
	}

	public int AddProductOld(PcProductinfo pc, StringBuffer error, String manageCode) {

		// 调用 添加订单的存储过程
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
		params.add(new SqlOutParameter("error", Types.VARCHAR));

		ProductCallableStatement cscc = new ProductCallableStatement(pc, 0);

		DbTemplate dt = DbUp.upTable("pc_skuinfo").upTemplate();
		Map<String, Object> outValues = dt.getJdbcOperations().call(cscc, params);

		String returnCode = outValues.get("outFlag").toString();
		if (Integer.parseInt(returnCode) != SkuCommon.SuccessFlag) {
			if (error != null)
				error.append(bInfo(Integer.parseInt(returnCode), outValues.get("error").toString()));
		} else {
			// 加入日志。待优化，可以写一个存储过程
			try {
				// 取得当前预览的商品的url
				String preViewUrl = bConfig("productcenter.PreviewProductUrl") + pc.getProductCode();

				if (pc.getProductStatus().equals("4497153900060001")) {
					// 加入审批的流程
					FlowService fs = new FlowService();
					ScFlowMain flow = new ScFlowMain();
					flow.setCreator(manageCode);
					flow.setCurrentStatus(pc.getProductStatus());
					flow.setFlowUrl(preViewUrl);
					// flow.setFlowRemark("创建商品");
					String title = bInfo(941901036, pc.getProductCode());
					flow.setFlowTitle(pc.getProductCode());
					flow.setFlowType("449717230010");
					flow.setCreator(manageCode);
					flow.setOuterCode(pc.getProductCode());
					flow.setFlowRemark(title);
					// 创建的审批流程
					RootResult rr = fs.CreateFlow(flow);
					if (rr.getResultCode() != 1) {
						WebHelper.errorMessage(pc.getProductCode(), "addProduct", 1, "AddProduct-NoFlow",
								rr.getResultMessage(), null);
					}
				}

				StockChangeLogService scls = new StockChangeLogService();
				scls.AddStockChangeLog("", pc.getProductSkuInfoList(), SkuCommon.SkuStockChangeTypeCreateProduct,
						manageCode);

				// 校验输入的数据合法性
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeProductText(pc.getProductCode());

				this.genarateJmsStaticPageForProduct(pc);

			} catch (Exception e) {
			}
		}

		return Integer.parseInt(returnCode);
	}

	public int AddProductTx(PcProductinfo pc, StringBuffer error, String manageCode) {

		RootResult rr = new RootResult();
		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.insertProduct(pc, rr, manageCode);
			
			// 560添加商品分类属性
			List<PropertiesProductRelation> pprList = pc.getPprList();
			if(null != pprList && pprList.size() > 0) {
				String ppr_upDateTime = FormatHelper.upDateTime();
				for (PropertiesProductRelation propertiesProductRelation : pprList) {
					String ppr_product_code = propertiesProductRelation.getProduct_code();
					if("".equals(ppr_product_code)) {						
						ppr_product_code = pc.getProductCode();
					}
					String properties_code = propertiesProductRelation.getProperties_code();
					String properties_value_code = propertiesProductRelation.getProperties_value_code();
					String properties_value = propertiesProductRelation.getProperties_value();
					if(StringUtils.isEmpty(ppr_product_code) || StringUtils.isEmpty(properties_code)) {
						continue;
					}
					// 属性信息
					MDataMap p_key = DbUp.upTable("uc_properties_key").one("properties_code",properties_code,"is_delete","0");
					// 查询商品是否有该属性
					MDataMap ppr = DbUp.upTable("uc_properties_product_relation").one("product_code",ppr_product_code,"properties_code",properties_code);
					
					MDataMap pprMap = new MDataMap();
					pprMap.put("product_code", ppr_product_code);
					pprMap.put("properties_code", properties_code);
					if(null != ppr) { // 有该属性,说明是修改
						pprMap.put("update_time", ppr_upDateTime);
						if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
							if(StringUtils.isEmpty(properties_value_code)) {
								continue;
							}
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", "");
							DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");
						}else { // 自定义
							if(StringUtils.isEmpty(properties_value)) {
								continue;
							}
							// 验证属性值编号在 uc_properties_value 中是否存在:不存在则修改属性值;存在则新建属性值
							MDataMap pv = DbUp.upTable("uc_properties_value").one("properties_value_code",ppr.get("properties_value_code"),"is_delete","0");
							if(null != pv) { // 存在
								properties_value_code = WebHelper.upCode("PV");
								pprMap.put("properties_value_code", properties_value_code);
								pprMap.put("properties_value", properties_value);
								DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");										
							}else { // 不存在
								pprMap.put("properties_value", properties_value);
								DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value,update_time", "product_code,properties_code");										
							}
						}
					}else { // 没有该属性,直接添加
						if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
							if(StringUtils.isEmpty(properties_value_code)) {
								continue;
							}
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", "");
						}else { // 自定义
							if(StringUtils.isEmpty(properties_value)) {
								continue;
							}
							// 新生成一个属性值code
							properties_value_code = WebHelper.upCode("PV");
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", properties_value);
						}
						pprMap.put("create_time", ppr_upDateTime);
						DbUp.upTable("uc_properties_product_relation").dataInsert(pprMap);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rr.setResultCode(941901049);
			rr.setResultMessage(bInfo(941901049, e.getMessage()));
		}

		try {
			// 校验输入的数据合法性
			ProductJmsSupport pjs = new ProductJmsSupport();
			pjs.onChangeProductText(pc.getProductCode());
			this.genarateJmsStaticPageForProduct(pc);

		} catch (Exception e) {
		}

		error.append(rr.getResultMessage());
		return rr.getResultCode();
	}

	public int UpdateProductTx(PcProductinfo pc, StringBuffer error, String manageCode, ProductChangeFlag pcf) {

		RootResult rr = new RootResult();
		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.updateProduct(pc, rr, manageCode, pcf);
			
			// 560添加商品分类属性
			List<PropertiesProductRelation> pprList = pc.getPprList();
			if(null != pprList && pprList.size() > 0) {
				String ppr_upDateTime = FormatHelper.upDateTime();
				for (PropertiesProductRelation propertiesProductRelation : pprList) {
					String ppr_product_code = propertiesProductRelation.getProduct_code();
					if("".equals(ppr_product_code)) {						
						ppr_product_code = pc.getProductCode();
					}
					String properties_code = propertiesProductRelation.getProperties_code();
					String properties_value_code = propertiesProductRelation.getProperties_value_code();
					String properties_value = propertiesProductRelation.getProperties_value();
					if(StringUtils.isEmpty(ppr_product_code) || StringUtils.isEmpty(properties_code)) {
						continue;
					}
					// 属性信息
					MDataMap p_key = DbUp.upTable("uc_properties_key").one("properties_code",properties_code,"is_delete","0");
					// 查询商品是否有该属性
					MDataMap ppr = DbUp.upTable("uc_properties_product_relation").one("product_code",ppr_product_code,"properties_code",properties_code);
					
					MDataMap pprMap = new MDataMap();
					pprMap.put("product_code", ppr_product_code);
					pprMap.put("properties_code", properties_code);
					if(null != ppr) { // 有该属性,说明是修改
						pprMap.put("update_time", ppr_upDateTime);
						if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
							if(StringUtils.isEmpty(properties_value_code)) {
								continue;
							}
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", "");
							DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");
						}else { // 自定义
							if(StringUtils.isEmpty(properties_value)) {
								continue;
							}
							// 验证属性值编号在 uc_properties_value 中是否存在:不存在则修改属性值;存在则新建属性值
							MDataMap pv = DbUp.upTable("uc_properties_value").one("properties_value_code",ppr.get("properties_value_code"),"is_delete","0");
							if(null != pv) { // 存在
								properties_value_code = WebHelper.upCode("PV");
								pprMap.put("properties_value_code", properties_value_code);
								pprMap.put("properties_value", properties_value);
								DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value_code,properties_value,update_time", "product_code,properties_code");										
							}else { // 不存在
								pprMap.put("properties_value", properties_value);
								DbUp.upTable("uc_properties_product_relation").dataUpdate(pprMap, "properties_value,update_time", "product_code,properties_code");										
							}
						}
					}else { // 没有该属性,直接添加
						if("449748500001".equals(p_key.get("properties_value_type"))) { // 固定值
							if(StringUtils.isEmpty(properties_value_code)) {
								continue;
							}
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", "");
						}else { // 自定义
							if(StringUtils.isEmpty(properties_value)) {
								continue;
							}
							// 新生成一个属性值code
							properties_value_code = WebHelper.upCode("PV");
							pprMap.put("properties_value_code", properties_value_code);
							pprMap.put("properties_value", properties_value);
						}
						pprMap.put("create_time", ppr_upDateTime);
						DbUp.upTable("uc_properties_product_relation").dataInsert(pprMap);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rr.setResultCode(941901049);
			rr.setResultMessage(bInfo(941901049, e.getMessage()));
		}

		error.append(rr.getResultMessage());
		return rr.getResultCode();

	}

	/**
	 * @param product
	 *            商品信息
	 * @param error
	 *            错误内容
	 * @return 返回错误编号
	 */
	public int copyProduct(PcProductinfo product, StringBuffer error) {
		return this.productManage(product, error, 2);
	}

	/**
	 * @param uid
	 * 
	 */
	public MDataMap getProductCategoryByUid(String uid) {
		MDataMap map = new MDataMap();
		map.put("uid", uid);
		List<Map<String, Object>> reList = DbUp.upTable("pc_productinfo").dataQuery("product_code", "", "", map, 0, 0);
		map.clear();
		if (!reList.isEmpty()) {
			map.put("flag_main", "1");
			map.put("product_code", reList.get(0).get("product_code").toString());
			List<Map<String, Object>> caList = DbUp.upTable("pc_productcategory_rel").dataQuery("category_code", "", "",
					map, 0, 0);
			map.clear();
			map.put("product_code", reList.get(0).get("product_code").toString());
			if (caList.size() == 1) {
				map.put("category_code", caList.get(0).get("category_code").toString());
			} else {
				map.put("category_code", "");
			}
		} else {
			map.clear();
			map.put("product_code", "");
			map.put("category_code", "");
		}
		return map;
	}

	/**
	 * 校验内容的合法性
	 * 
	 * @param content
	 * @return
	 */
	public RootResult checkContent(String content) {
		RootResult mResult = new RootResult();
		SystemCheck systemCheck = new SystemCheck();
		MWebResult mCheckLinkResult = systemCheck.checkLink(content);
		if (!mCheckLinkResult.upFlagTrue()) {

			mResult.setResultCode(mCheckLinkResult.getResultCode());
			mResult.setResultMessage(mCheckLinkResult.getResultMessage());
		}
		return mResult;
	}

	/**
	 * 刷新主属性缓存
	 * 
	 * @param productCode
	 */
	public void RefreshMainProperty(String productCode) {

		// 取得商品属性信息
		MDataMap pcProductpropertyListMapParam = new MDataMap();
		pcProductpropertyListMapParam.put("product_code", productCode);
		List<PcProductproperty> pcProductpropertyList = null;
		List<MDataMap> pcProductpropertyListMap = DbUp.upTable("pc_productproperty").query("", "",
				"product_code=:product_code  and (property_type='449736200001' or property_type='449736200002')",
				pcProductpropertyListMapParam, -1, -1);
		if (pcProductpropertyListMap != null) {
			int size = pcProductpropertyListMap.size();
			pcProductpropertyList = new ArrayList<PcProductproperty>();
			SerializeSupport ss = new SerializeSupport<PcProductproperty>();
			for (int i = 0; i < size; i++) {
				PcProductproperty pic = new PcProductproperty();
				ss.serialize(pcProductpropertyListMap.get(i), pic);
				pcProductpropertyList.add(pic);
			}
		}

		// 循环关键属性key
		if (pcProductpropertyList != null) {
			for (PcProductproperty pp : pcProductpropertyList) {
				MDataMap mWhereMap = new MDataMap();

				mWhereMap.put("product_code", pp.getProductCode());
				mWhereMap.put("sku_key", "%" + pp.getPropertyCode() + "%");

				int count = DbUp.upTable("pc_skuinfo")
						.dataCount("  product_code=:product_code AND sku_key LIKE :sku_key ", mWhereMap);

				if (count == 0) {
					DbUp.upTable("pc_productproperty").delete("zid", pp.getZid().toString());
				}
			}
		}
	}

	/**
	 * 刷新所有价格和缓存数据
	 * 
	 * @param productCode
	 */
	public void RefreshPriceAndStockByProductCode(String productCode) {

		MDataMap mapParam = new MDataMap();
		mapParam.put("product_code", productCode);
		// 通知前端生成静态页面
		ProductJmsSupport pjs = new ProductJmsSupport();
		String skuCodes = "";
		List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").query("", "", "product_code=:product_code",
				mapParam, -1, -1);
		int j = productSkuInfoListMap.size();

		for (int i = 0; i < j; i++) {
			try {
				// 生成静态页面
				pjs.onChangeForSkuChangeAll(productSkuInfoListMap.get(i).get("sku_code"));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	/**
	 * 刷新所有缓存数据
	 * 
	 * @param productCode
	 */
	public void RefreshAllByProductCode(String productCode) {
		try {

			this.genarateJmsStaticPageForProductCode(productCode);
			this.RefreshMainProperty(productCode);
			this.RefreshPriceAndStockByProductCode(productCode);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取商品所属的实类
	 * 
	 */
	public String getCategoryName(String productCode) {
		String name = "";
		if (productCode != null && !"".equals(productCode)) {
			MDataMap pcCategoryData = DbUp.upTable("pc_productcategory_rel").one("product_code", productCode,
					"flag_main", "1");
			if (pcCategoryData == null || "".equals(pcCategoryData.get("category_code"))) {// 非正常添加的数据直接返回""
																							// 比如惠家有同步的数据
				return name;
			}

			String thired = pcCategoryData.get("category_code");
			String second = pcCategoryData.get("category_code").substring(0, thired.length() - 4);
			String first = pcCategoryData.get("category_code").substring(0, thired.length() - 8);
			List<MDataMap> list = DbUp.upTable("pc_categoryinfo").queryAll("category_code,category_name", "",
					"category_code in('" + first + "','" + second + "','" + thired + "')", new MDataMap());
			if (!list.isEmpty()) {
				String nameone = "";
				String nametwo = "";
				String namethree = "";
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("category_code").equals(thired)) {
						nameone = list.get(i).get("category_name");
					} else if (list.get(i).get("category_code").equals(second)) {
						nametwo = ">" + list.get(i).get("category_name") + ">";
					} else if (list.get(i).get("category_code").equals(first)) {
						namethree = list.get(i).get("category_name");
					}
				}
				name = namethree + nametwo + nameone;
			}
		}

		return name;
	}

	/**
	 * 获取在售商品(categoryCode为空时获取本app所有分类下的商品)
	 * 
	 * @param appCode
	 *            不能为空
	 * @param categoryCode
	 *            可以为空
	 */
	public List<PcProductinfo> getSellProducts(String appCode, String categoryCode, String product) {
		MDataMap whMap = new MDataMap();
		StringBuffer swhere = new StringBuffer(" 1=1 ");
		List<PcProductinfo> list = new ArrayList<PcProductinfo>();
		if (categoryCode != null && !"".equals(categoryCode)) {

			swhere.append(" and category_code like '" + categoryCode + "%' ");

		} else if (product != null && !"".equals(product) && (categoryCode == null || "".equals(categoryCode))) {

			swhere.append(" and  product_code = '" + product + "'");
		}
		swhere.append(" and seller_code = '" + appCode + "' ");
		try {
			List<MDataMap> pcListMap = DbUp.upTable("uc_sellercategory_product_relation").queryAll("product_code", "",
					swhere.toString(), whMap);
			if (pcListMap != null && !pcListMap.isEmpty()) {
				List<String> lStrings = new ArrayList<String>();
				for (MDataMap mDataMap : pcListMap) {
					lStrings.add(mDataMap.get("product_code"));
				}
				List<MDataMap> pListMap = DbUp.upTable("pc_productinfo").queryIn("product_code", "-zid",
						" product_status='4497153900060002' and flag_sale=1 ", new MDataMap(), 0, 0, "product_code",
						StringUtils.join(lStrings, ","));

				if (pListMap != null && !pListMap.isEmpty()) {
					List<String> pcStr = new ArrayList<String>();
					for (MDataMap mp : pListMap) {
						pcStr.add(mp.get("product_code"));
					}
					List<MDataMap> pslist = DbUp.upTable("pc_skuinfo").queryIn("sku_code,product_code", "-zid", "",
							new MDataMap(), 0, 0, "product_code", StringUtils.join(pcStr, ","));
					if (pslist != null && !pslist.isEmpty()) {
						Iterator<MDataMap> it = pslist.iterator();
						while (it.hasNext()) {
							MDataMap va = it.next();
							PcProductinfo pi = getskuinfoCode(va.get("sku_code"), va.get("product_code"));
							if (pi != null) {
								list.add(pi);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 试用商品列表(categoryCode为空时获取本app下所有试用的商品)
	 * 
	 * @param appCode
	 *            不能为空
	 * @param categoryCode
	 *            可以为空
	 */
	public List<PcProductinfo> getTrialProducts(String appCode, String categoryCode, String skuId) {
		List<PcProductinfo> list = new ArrayList<PcProductinfo>();
		// 根据虚类获取skucode skucode in 活动关联表
		MDataMap whmap = new MDataMap();

		StringBuffer swhere = new StringBuffer(" 1=1 ");

		whmap.put("seller_code", appCode);

		StringBuffer where = new StringBuffer(" 1=1 ");

		/* 获取当前时间 */
		String currentTime = FormatHelper.upDateTime();

		where.append(
				" and app_code=:seller_code and start_time<='" + currentTime + "' and end_time>='" + currentTime + "'");

		if (skuId != null && !"".equals(skuId)) {

			where.append(" and  sku_code = '" + skuId + "' ");

		}

		List<MDataMap> skuList = DbUp.upTable("oc_tryout_products").queryAll("sku_code", "end_time", where.toString(),
				whmap);

		if (categoryCode != null && !"".equals(categoryCode)) {

			swhere.append(" and category_code like '" + categoryCode + "%' ");
		}

		swhere.append(" and seller_code ='" + appCode + "'  ");

		List<MDataMap> pcList = DbUp.upTable("uc_sellercategory_product_relation").queryAll("product_code", "",
				swhere.toString(), new MDataMap());

		if (skuList != null && !skuList.isEmpty() && pcList != null && !pcList.isEmpty()) {
			List<String> skuStrings = new ArrayList<String>();
			for (MDataMap mDataMap : skuList) {
				skuStrings.add(mDataMap.get("sku_code"));
			}
			String[] skuValuesStrings = StringUtils.join(skuStrings, ",").split(",");
			List<String> lAdd = new ArrayList<String>();
			MDataMap spMap = new MDataMap();
			for (int i = 0, j = skuValuesStrings.length; i < j; i++) {
				lAdd.add(" sku_code=:sku_code_" + String.valueOf(i) + " ");
				spMap.put("sku_code_" + String.valueOf(i), skuValuesStrings[i]);

			}
			String sWhere = " (" + StringUtils.join(lAdd, " or ") + ")";

			List<String> pcStrings = new ArrayList<String>();
			for (MDataMap mDataMap : pcList) {
				pcStrings.add(mDataMap.get("product_code"));
			}
			String[] pcValuesStrings = StringUtils.join(pcStrings, ",").split(",");
			List<String> lBdd = new ArrayList<String>();
			for (int i = 0, j = pcValuesStrings.length; i < j; i++) {
				lBdd.add(" product_code=:product_code_" + String.valueOf(i) + " ");
				spMap.put("product_code_" + String.valueOf(i), pcValuesStrings[i]);

			}
			sWhere = sWhere + " and " + " (" + StringUtils.join(lBdd, " or ") + ")";

			List<MDataMap> li = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code", "", sWhere, spMap);
			Iterator<MDataMap> ite = li.iterator();
			while (ite.hasNext()) {
				MDataMap next = ite.next();
				list.add(getskuinfoCode(next.get("sku_code"), next.get("product_code")));
			}
		}
		return list;
	}

	/**
	 * 根据skuCode获取sku对象
	 * 
	 * @param skuCode
	 *            不能为空
	 */
	public PcProductinfo getskuinfo(String skuCode, String productCode) {
		PcProductinfo pc = null;
		MDataMap pcSkuMapParam = new MDataMap();
		pcSkuMapParam.put("sku_code", skuCode);
		MDataMap productSkuInfoMap = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode, "sale_yn", "Y", "flag_enable",
				"1");
		if (null != productSkuInfoMap && !productSkuInfoMap.isEmpty()) {
			ProductSkuInfo sku = new ProductSkuInfo();
			SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();
			ss.serialize(productSkuInfoMap, sku);
			sku.setSkuValue(productSkuInfoMap.get("sku_keyvalue"));
			if (productCode == null || "".equals(productCode)) {
				productCode = productSkuInfoMap.get("product_code");
			}

			pc = getProduct(productCode);
			if (pc == null) {
				return new PcProductinfo();
				// edit lgj 查不到商品信息时会报空指针错误
			}
			List<ProductSkuInfo> li = new ArrayList<ProductSkuInfo>();
			li.add(sku);
			pc.setProductSkuInfoList(li);
			// label字段已经改为关键字，直接输入汉字就可以，所以在此注释掉 2015-09-22
			// if(pc!=null&&pc.getLabels()!=null&&!"".equals(pc.getLabels())){
			// List<MDataMap> list =
			// DbUp.upTable("pc_labelmanage").queryIn("label_name", "", "", new
			// MDataMap(), 0, 0, "label_code", pc.getLabels());
			// if(list!=null&&!list.isEmpty()){
			// List<String> lStrings=new ArrayList<String>();
			// for (MDataMap mDataMap : list) {
			// lStrings.add(mDataMap.get("label_name"));
			// }
			// pc.setLabels(StringUtils.join(lStrings,","));
			// }
			// }
		}
		return pc;
	}

	/**
	 * 根据skuCode获取sku对象 --嘉玲
	 * 
	 * @param skuCode
	 *            不能为空
	 */
	public PcProductinfo getskuinfoCode(String skuCode, String productCode) {
		PcProductinfo pc = null;
		MDataMap pcSkuMapParam = new MDataMap();
		pcSkuMapParam.put("sku_code", skuCode);
		MDataMap productSkuInfoMap = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode);
		if (!productSkuInfoMap.isEmpty()) {
			ProductSkuInfo sku = new ProductSkuInfo();
			SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();
			ss.serialize(productSkuInfoMap, sku);
			sku.setSkuValue(productSkuInfoMap.get("sku_keyvalue"));
			if (productCode == null || "".equals(productCode)) {
				productCode = productSkuInfoMap.get("product_code");
			}
			pc = getProductCode(productCode);
			List<ProductSkuInfo> li = new ArrayList<ProductSkuInfo>();
			li.add(sku);
			pc.setProductSkuInfoList(li);
			if (pc != null && pc.getLabels() != null && !"".equals(pc.getLabels())) {
				List<MDataMap> list = DbUp.upTable("pc_labelmanage").queryIn("label_name", "", "", new MDataMap(), 0, 0,
						"label_code", pc.getLabels());
				if (list != null && !list.isEmpty()) {
					List<String> lStrings = new ArrayList<String>();
					for (MDataMap mDataMap : list) {
						lStrings.add(mDataMap.get("label_name"));
					}
					pc.setLabels(StringUtils.join(lStrings, ","));
				}
			}
		}
		return pc;
	}

	/**
	 * 获取商品入库类型以及入库仓库编号,虚拟销售量基数(商品总览页面掉用) 获取供应商编号与供应商名称
	 * 
	 */
	public PcProductinfoExt getPrchType(String productCode) {
		PcProductinfoExt productInfoExt = new PcProductinfoExt();
		if (StringUtils.isBlank(productCode)) {
			return productInfoExt;
		}
		MDataMap pcExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere(
				"prch_type,oa_site_no,fictitious_sales,dlr_id,dlr_nm,settlement_type,purchase_type", "", "",
				"product_code", productCode);

		if (pcExtMap == null || pcExtMap.isEmpty()) {
			return productInfoExt;
		} else {
			if (!StringUtils.isEmpty(pcExtMap.get("prch_type"))) {
				//  10-商品中心一地入库
				//  20-网站一地入库
				// 00-非一地入库
				String prchTypeCode = pcExtMap.get("prch_type");
				String oaSiteNo = pcExtMap.get("oa_site_no");
				if ("10".equals(prchTypeCode)) {
					productInfoExt.setPrchType("商品中心一地入库");
					productInfoExt.setOaSiteNo(oaSiteNo);
				} else if ("20".equals(prchTypeCode)) {
					productInfoExt.setPrchType("网站一地入库");
					productInfoExt.setOaSiteNo(oaSiteNo);
				} else if ("00".equals(prchTypeCode)) {
					productInfoExt.setPrchType("非一地入库");
					// productInfoExt.setOaSiteNo("C01,C02,C04,C10");
					productInfoExt.setOaSiteNo(oaSiteNo);
				}
				productInfoExt.setFictitiousSales(
						StringUtils.isEmpty(pcExtMap.get("fictitious_sales")) ? "0" : pcExtMap.get("fictitious_sales"));
			}
			String defineCodes = pcExtMap.get("settlement_type") + "','" + pcExtMap.get("purchase_type");
			List<MDataMap> defineMap = DbUp.upTable("sc_define").queryAll("define_code,define_name", "",
					"define_code in ('" + defineCodes + "')", null);
			MDataMap defineNameMap = new MDataMap();
			if (null != defineMap && !defineMap.isEmpty()) {
				for (MDataMap mDataMap : defineMap) {
					defineNameMap.put(mDataMap.get("define_code"), mDataMap.get("define_name"));
				}
			}
			if (StringUtils.isNotEmpty(pcExtMap.get("settlement_type"))) {
				productInfoExt.setSettlementType(defineNameMap.get(pcExtMap.get("settlement_type")));
			}
			if (StringUtils.isNotEmpty(pcExtMap.get("purchase_type"))) {
				productInfoExt.setPurchaseType(defineNameMap.get(pcExtMap.get("purchase_type")));
			}
			productInfoExt.setDlrId(pcExtMap.get("dlr_id"));
			productInfoExt.setDlrNm(pcExtMap.get("dlr_nm"));

			// MDataMap productInfoMap = DbUp.upTable("pc_productinfo").
			// oneWhere("cost_price","","","product_code",productCode);

			// 审批前的毛利率
			List<MDataMap> skuInfoMapList = DbUp.upTable("pc_skuinfo").queryAll("sku_code,sell_price,cost_price", "",
					"product_code='" + productCode + "'", null);
			productInfoExt.setGrossProfit(this.getGrossProfit(skuInfoMapList));

			return productInfoExt;
		}
	}

	/**
	 * 获取商品审批前与审批后的毛利润(价格审核页面调用)（暂时没用，如有人调用请删掉此注释）
	 * 
	 * @param productCode
	 * @param flowCode
	 * @return
	 */
	public Map<String, String> getGrossProfitForFlow(String productCode, String flowCode) {
		Map<String, String> grossProfitMap = new HashMap<String, String>();
		List<MDataMap> skuInfoMapList = DbUp.upTable("pc_skuprice_change_flow").queryAll(
				"sku_code,sell_price,cost_price,cost_price_old,sell_price_old", "",
				"product_code='" + productCode + "' and flow_code='" + flowCode + "'", null);
		List<MDataMap> beforeGrossProfitMapList = new ArrayList<MDataMap>(); // 审核前的毛利润

		for (MDataMap mDataMap : skuInfoMapList) {
			MDataMap beforeMap = new MDataMap();
			beforeMap.put("sell_price", mDataMap.get("sell_price_old"));
			beforeMap.put("cost_price", mDataMap.get("cost_price_old"));
			beforeGrossProfitMapList.add(beforeMap);
		}
		String afterGrossProfit = this.getGrossProfit(skuInfoMapList);
		String beforeGrossProfit = this.getGrossProfit(beforeGrossProfitMapList);
		grossProfitMap.put("before_gross_profit", beforeGrossProfit);
		grossProfitMap.put("after_gross_profit", afterGrossProfit);
		return grossProfitMap;
	}

	/**
	 * 计算价格审批前的毛利率或价格审批后的毛利率
	 * 
	 * @param list
	 * @return
	 */
	private String getGrossProfit(List<MDataMap> list) {
		// 毛利润
		String grossProfitStr = "0";

		if (null != list && list.size() > 0) {
			BigDecimal costPrice = BigDecimal.ZERO;
			BigDecimal skuSellPrice = BigDecimal.ZERO;
			for (MDataMap skuInfo : list) {
				costPrice = costPrice.add(new BigDecimal(skuInfo.get("cost_price")));
				skuSellPrice = skuSellPrice.add(new BigDecimal(skuInfo.get("sell_price")));
			}
			BigDecimal grossProfit = BigDecimal.ZERO;
			if (BigDecimal.ZERO.compareTo(skuSellPrice) != 0) {
				grossProfit = skuSellPrice.subtract(costPrice).multiply(new BigDecimal(100)).divide(skuSellPrice, 2,
						BigDecimal.ROUND_HALF_UP);
			}
			grossProfitStr = grossProfit.toString();
		}
		return grossProfitStr + "%";
	}

	/**
	 * 取得商品的Sku商品信息 .
	 * 
	 * @param sellerCode
	 *            App编码(必须非空)
	 * @param skuCode
	 *            商品编码(必须非空)
	 * @return
	 */
	public Map<String, Object> getSkuView(String sellerCode, String skuCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		MDataMap skuMap = new MDataMap();
		PcProductinfo productInfo = new PcProductinfo();
		if (skuCode == null || "".equals(skuCode) || sellerCode == null || "".equals(sellerCode)) {
			return resultMap;
		} else {
			skuMap = DbUp.upTable("pc_skuinfo").one("seller_code", sellerCode, "sku_code", skuCode);
			if (null != skuMap) {
				List<ProductSkuInfo> skuInfoList = new ArrayList<ProductSkuInfo>();
				ProductSkuInfo skuInfo = new ProductSkuInfo();
				SerializeSupport<ProductSkuInfo> sku = new SerializeSupport<ProductSkuInfo>();
				sku.serialize(skuMap, skuInfo);
				skuInfo.setSkuValue(skuMap.get("sku_keyvalue"));

				// 购买数
				skuInfo.setSellCount(this.getSkuSellCount(skuInfo.getSkuCode()));

				skuInfoList.add(skuInfo);
				productInfo.setProductSkuInfoList(skuInfoList); // sku信息

				MDataMap pcMap = DbUp.upTable("pc_productinfo").one("seller_code", sellerCode, "product_code",
						skuMap.get("product_code"));
				if (null != pcMap) {
					SerializeSupport<PcProductinfo> product = new SerializeSupport<PcProductinfo>();
					product.serialize(pcMap, productInfo);
				}

				// 产品详情（内容图片）
				MDataMap descriptMap = DbUp.upTable("pc_productdescription").one("product_code",
						skuMap.get("product_code"));
				if (null != descriptMap) {
					PcProductdescription description = new PcProductdescription();
					SerializeSupport<PcProductdescription> ss = new SerializeSupport<PcProductdescription>();
					ss.serialize(descriptMap, description);
					productInfo.setDescription(description);

					productInfo.setLabels(description.getKeyword()); // 商品标签
				}
				// 图片列表list（轮播图）
				List<MDataMap> picUrlsMap = DbUp.upTable("pc_productpic").queryByWhere("product_code",
						skuMap.get("product_code"));
				List<PcProductpic> picUrls = new ArrayList<PcProductpic>();
				if (picUrlsMap == null || picUrlsMap.size() == 0) {
					productInfo.setPcPicList(picUrls);
				} else {
					for (MDataMap picUrlMap : picUrlsMap) {
						PcProductpic picObj = new PcProductpic();
						SerializeSupport<PcProductpic> ss = new SerializeSupport<PcProductpic>();
						ss.serialize(picUrlMap, picObj);
						picUrls.add(picObj);
					}
					productInfo.setPcPicList(picUrls);
				}
				MDataMap brandMapParam = new MDataMap();
				brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", productInfo.getBrandCode());
				if (brandMapParam != null) {
					productInfo.setBrandName(brandMapParam.get("brand_name"));
				}

			} else {
				return resultMap;
			}
			// 月销量
			resultMap.put("skuSellNum", this.getSkuMonthSellCount(skuCode, sellerCode));
			// 商品信息
			resultMap.put("productInfo", productInfo);
			return resultMap;
		}
	}

	/**
	 * 根据商品虚类取得商品信息 . 惠家有
	 * 
	 * @param categoryCode
	 *            商品分类（虚类）（为空字符串时查询全部）
	 * @param sortField
	 *            排序规则
	 * @param sellerCode
	 *            App编码(必须非空)
	 * @param offset
	 *            起码页号
	 * @param limit
	 *            每页条数
	 * @return
	 * @author lgj
	 */
	public PcProductinfoPage getProductInfoForC(String categoryCode, String sortField, String sellerCode, int offset,
			int limit) {
		PcProductinfoPage result = new PcProductinfoPage();
		List<PcProductinfo> productList = new ArrayList<PcProductinfo>();
		if (null == sellerCode || "".equals(sellerCode)) {
			return result;
		}
		if (null == categoryCode) {
			categoryCode = "";
		}
		String categoryWhere = " category_code like '" + categoryCode + "%' and seller_code='" + sellerCode + "'";
		List<MDataMap> proCodesMap = DbUp.upTable("uc_sellercategory_product_relation").queryAll("product_code", "",
				categoryWhere, null);

		String productCodesStr = ""; // 该分类下的所有product_code拼成“code1,code2,code3,.......”格式的字符串

		StringBuffer productBuffer = new StringBuffer();
		for (int i = 0; i < proCodesMap.size(); i++) {
			productBuffer.append(proCodesMap.get(i).get("product_code"));
			productBuffer.append("','");
		}
		productCodesStr = productBuffer.toString();

		MDataMap totalMap = new MDataMap();

		// 只查询 已上架商品
		if (StringUtils.isNotEmpty(productCodesStr)) {
			String queryProductCode = " product_status = '4497153900060002' " + "and  product_code in ('"
					+ productCodesStr.substring(0, productCodesStr.length() - 3) + "') ";
			totalMap = DbUp.upTable("pc_productinfo").oneWhere("count(1) as  total", "", queryProductCode);
		}

		int iStart = limit * offset;// 开始条数
		int iEnd = iStart + limit;// 结束条数

		int totalNum = Integer.parseInt(totalMap.get("total") == null ? "0" : totalMap.get("total")); // 总数
		if (iStart >= totalNum) { // 开始条数不小于总数时返回空
			return result;
		}

		if (null == proCodesMap || proCodesMap.size() == 0) {
			return result;
		}
		if ("".equals(productCodesStr)) {
			return result;
		}
		String sWhere = " product_code in ('" + productCodesStr.substring(0, productCodesStr.length() - 3)
				+ "') and product_status='4497153900060002' ";
		String sOrders = "";
		if ("449746820002".equals(sortField)) { // 排序暂时定为通过zid排序，日后需要销量等排序条件
			sOrders = "zid desc";
		} else {
			sOrders = "update_time desc";
		}
		List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").query("", sOrders, sWhere, null, iStart, limit);
		for (MDataMap mDataMap : productMapList) {
			if (null != mDataMap) {
				PcProductinfo productObj = new PcProductinfo();
				SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
				pSupport.serialize(mDataMap, productObj);

				// 产品详情（内容图片）商品标签
				MDataMap descriptMap = DbUp.upTable("pc_productdescription").oneWhere("keyword", "", "", "product_code",
						productObj.getProductCode());
				if (null != descriptMap) {
					productObj.setLabels(descriptMap.get("keyword"));
				}

				MDataMap categoryCodeMap = DbUp.upTable("uc_sellercategory_product_relation").oneWhere("category_code",
						"", "", "product_code", productObj.getProductCode(), "seller_code", sellerCode);
				if (null != productObj) { // 返回虚类code
					PcCategoryinfo category = new PcCategoryinfo();
					category.setCategoryCode(categoryCodeMap.get("category_code"));
					productObj.setCategory(category);
				}
				productList.add(productObj);
			}
		}

		int more = 1;// 有更多数据
		if (iEnd >= totalNum) {
			if (0 == totalNum) { // 如果总数为0，则开始条数也设置为0
				iStart = 0;
			}
			iEnd = totalNum;
			more = 0;
		}

		// 分页信息
		result.setTotal(totalNum);
		result.setCount(iEnd - iStart);
		result.setMore(more);

		// 列表结果
		result.setPcProducinfoList(productList);
		return result;
	}

	/**
	 * 根据商品虚类取得商品信息 . 惠美丽
	 * 
	 * @param categoryCode
	 *            商品分类（虚类）（为空字符串时查询全部）
	 * @param sortField
	 *            排序规则
	 * @param sellerCode
	 *            App编码(必须非空)
	 * @param offset
	 *            起码页号
	 * @param limit
	 *            每页条数
	 * @return
	 * @author lgj
	 */
	public ProductSkuInfoPage getSkuInfoForC(String categoryCode, String sortField, String sellerCode, int offset,
			int limit) {
		ProductSkuInfoPage result = new ProductSkuInfoPage();
		List<Map<String, Object>> skuListMap = new ArrayList<Map<String, Object>>();
		if (null == sellerCode || "".equals(sellerCode)) {
			return result;
		}
		if (null == categoryCode) {
			categoryCode = "";
		}
		String categoryWhere = " category_code like '" + categoryCode + "%' and seller_code='" + sellerCode + "'";
		List<MDataMap> proCodesMap = DbUp.upTable("uc_sellercategory_product_relation").queryAll("product_code", "",
				categoryWhere, null);

		if (null == proCodesMap || proCodesMap.size() == 0) {
			return result;
		}
		String productCodesStrOld = ""; // 该分类下的所有product_code拼成“code1,code2,code3,.......”格式的字符串
		StringBuffer productOldBuffer = new StringBuffer();
		for (int i = 0; i < proCodesMap.size(); i++) {
			productOldBuffer.append(proCodesMap.get(i).get("product_code"));
			productOldBuffer.append("','");
		}
		productCodesStrOld = productOldBuffer.toString();

		// 只查询已上架商品。
		String filterWhere = "  product_status = '4497153900060002' and product_code in ('"
				+ productCodesStrOld.substring(0, productCodesStrOld.length() - 3) + "') ";
		List<MDataMap> filterProductCode = DbUp.upTable("pc_productinfo").queryAll("", "-update_time", filterWhere,
				null);

		String sOrders = "";
		if ("449746820002".equals(sortField)) { // 销量排序 直接操作list排序
		} else if ("449746820003".equals(sortField)) { // 新品排序
			sOrders = "zid desc";
		} else {
			// 默认排序是按照商品信息的update_time排序,与销量排序一样直接操作的list
		}

		// 该分类下的所有product_code拼成“code1,code2,code3,.......”格式的字符串
		StringBuffer productBuffer = new StringBuffer();
		for (int i = 0; i < filterProductCode.size(); i++) {
			productBuffer.append(filterProductCode.get(i).get("product_code"));
			productBuffer.append("','");
		}
		String productCodesStr = productBuffer.toString();

		if ("".equals(productCodesStr)) {
			return result;
		}
		String sWhere = " product_code in ('" + productCodesStr.substring(0, productCodesStr.length() - 3) + "') ";

		List<MDataMap> skuMap = DbUp.upTable("pc_skuinfo").queryAll("", sOrders, sWhere, null);
		Map<String, Integer> skuCodesF = this.getSkuCodesFlashActivity(sellerCode); // 获取到所有正在进行闪购的商品code
		Map<String, Integer> skuCodesT = this.getSkuCodesTryoutActivity(sellerCode); // 获取到所有正在进行试用的商品code

		for (int i = 0; i < skuMap.size(); i++) {
			// 循环过滤参加闪购的商品信息
			for (String skuCode : skuCodesF.keySet()) {
				if (skuCode.equals(skuMap.get(i).get("sku_code"))) {
					skuMap.remove(i);
					i--;
					break;
				}
			}
			// 循环过滤参加试用活动的商品信息
			for (String skuCode : skuCodesT.keySet()) {
				if (skuCode.equals(skuMap.get(i).get("sku_code"))) {
					skuMap.remove(i);
					i--;
					break;
				}
			}
		}

		int totalNum = skuMap.size(); // 过滤掉限时抢购后的实际总数

		int iStart = limit * offset;// 开始条数
		int iEnd = iStart + limit;// 结束条数

		if (iStart >= totalNum) { // 开始条数不小于总数时返回空
			return result;
		}

		for (MDataMap mDataMap : skuMap) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			ProductSkuInfo skuObj = new ProductSkuInfo();
			SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
			sSupport.serialize(mDataMap, skuObj);

			// 把sku信息添加到结果集中
			mapData.put("skuInfo", skuObj);
			skuListMap.add(mapData);
		}

		// 用map主要为去重
		Map<String, String> productCodesMap = new HashMap<String, String>();

		// 查询结果中sku_code列表，格式（code1','code2','code3）,统计商品销量使用
		StringBuffer totalSellCountSkuCodesBuff = new StringBuffer();

		for (Map<String, Object> map : skuListMap) {
			ProductSkuInfo skuInfo = (ProductSkuInfo) map.get("skuInfo");
			productCodesMap.put(skuInfo.getProductCode(), ""); // product_code去重，循环此map的key可获得不重复的商品编号
			// 获取sku_code
			totalSellCountSkuCodesBuff.append(skuInfo.getSkuCode());
			totalSellCountSkuCodesBuff.append("','");
		}

		StringBuffer productCodesStrBuf = new StringBuffer();
		for (String productCode : productCodesMap.keySet()) {
			productCodesStrBuf.append(productCode);
			productCodesStrBuf.append("','");
		}
		String prodcutCodesStr = productCodesStrBuf.toString().substring(0, productCodesStrBuf.toString().length() - 3);

		// 产品详情（内容图片）商品标签
		Map<String, String> descript = new HashMap<String, String>();
		String whereDescrip = " product_code in ('" + prodcutCodesStr + "') ";
		List<MDataMap> descriptMap = DbUp.upTable("pc_productdescription").queryAll("keyword,product_code", "",
				whereDescrip, null);
		for (MDataMap mDataMap : descriptMap) {
			descript.put(mDataMap.get("product_code"), mDataMap.get("keyword"));
		}

		// 储存product对象的HashMap
		Map<String, PcProductinfo> productMap = new HashMap<String, PcProductinfo>();

		for (MDataMap mDataMap : filterProductCode) {
			PcProductinfo productObj = new PcProductinfo();
			SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
			pSupport.serialize(mDataMap, productObj);
			if (null != descript.get(productObj.getProductCode())) {
				productObj.setLabels(descript.get(productObj.getProductCode()));
			}
			if (null != productObj) { // 返回虚类code
				PcCategoryinfo category = new PcCategoryinfo();
				category.setCategoryCode(categoryCode);
				productObj.setCategory(category);
			}
			productMap.put(productObj.getProductCode(), productObj);
		}
		String totalSellCountSkuCodes = totalSellCountSkuCodesBuff.toString();
		Map<String, Integer> sellCountMap = this.getSkuSellCountForGroup(sellerCode,
				(StringUtils.isEmpty(totalSellCountSkuCodes) ? ""
						: totalSellCountSkuCodes.substring(0, totalSellCountSkuCodes.length() - 3))); // 统计商品的购买数
		List<Map<String, Object>> resultListMapPage = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> mapData : skuListMap) {
			Map<String, Object> proMap = new HashMap<String, Object>();
			ProductSkuInfo skuObj = (ProductSkuInfo) mapData.get("skuInfo");
			PcProductinfo productObj = productMap.get(skuObj.getProductCode());
			// 购买数
			Integer sellCount = sellCountMap.get(skuObj.getSkuCode());
			skuObj.setSellCount(sellCount == null ? 0 : sellCount);// 购买数

			// 把sku信息添加到结果集中
			proMap.put("skuInfo", skuObj);
			// 把product信息添加到结果集中
			proMap.put("productInfo", productObj);
			resultListMapPage.add(proMap);
		}
		if ("449746820002".equals(sortField)) { // 销量排序
			// 按照销量倒排序排序
			Collections.sort(resultListMapPage, new Comparator<Object>() {
				public int compare(Object result1, Object result2) {
					ProductSkuInfo oneObj = (ProductSkuInfo) ((Map<String, Object>) result1).get("skuInfo");
					ProductSkuInfo twoObj = (ProductSkuInfo) ((Map<String, Object>) result2).get("skuInfo");
					int oneSellCount = oneObj.getSellCount();
					int twoSellCount = twoObj.getSellCount();

					if (oneSellCount > twoSellCount) {
						return -1;
					} else if (oneSellCount < twoSellCount) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		} else if (!"449746820002".equals(sortField) && !"449746820003".equals(sortField)) {
			// 商品列表的默认排序，按照修改时间倒排序
			Collections.sort(resultListMapPage, new Comparator<Object>() {
				public int compare(Object result1, Object result2) {
					PcProductinfo oneObj = (PcProductinfo) ((Map<String, Object>) result1).get("productInfo");
					PcProductinfo twoObj = (PcProductinfo) ((Map<String, Object>) result2).get("productInfo");
					String oneUpdateTime = oneObj.getUpdateTime();
					String twoUpdateTime = twoObj.getUpdateTime();
					return twoUpdateTime.compareTo(oneUpdateTime);
				}
			});
		}

		int more = 1;// 有更多数据
		if (iEnd >= totalNum) {
			iEnd = totalNum;
			more = 0;
		}
		// 查询好的list分页。如果分页写到sql语句中则没法按照销量排序
		List<Map<String, Object>> resultListMapPageNew = resultListMapPage.subList(iStart, iEnd);
		// 分页信息
		result.setTotal(totalNum);
		result.setCount(iEnd - iStart);
		result.setMore(more);

		// 列表结果
		result.setPcSkuinfoList(resultListMapPageNew);
		return result;
	}

	/**
	 * 获取有效期内的试用商品列表 .
	 * 
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<PcFreeTryOutGood> getFreeTryOutGoodsList(String appCode) {
		List<PcFreeTryOutGood> resultList = new ArrayList<PcFreeTryOutGood>();

		if (null == appCode || "".equals(appCode)) {
			return resultList;
		}

		String tWhere = " start_time <= now() and end_time > now() and app_code = '" + appCode + "' ";
		List<MDataMap> tMapList = DbUp.upTable("oc_tryout_products").query("", "end_time asc", tWhere, null, -1, -1);
		for (MDataMap mDataMap : tMapList) {
			List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();
			PcProductinfo productObj = new PcProductinfo(); // product信息
			ProductSkuInfo skuObj = new ProductSkuInfo(); // sku信息
			PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品信息
			SerializeSupport<PcFreeTryOutGood> fSupport = new SerializeSupport<PcFreeTryOutGood>();
			fSupport.serialize(mDataMap, freeObj);

			// 有效的付邮试用中，如果总库存小于试用库存时，把试用库存设为sku总剩余库存数
			if ("449746930002".equals(freeObj.getIsFreeShipping())) {
				// 查询库存
				StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
				int totalStock = storeService.getStockNumByStore(freeObj.getSkuCode()); // 剩余总库存
				int tryoutStock = freeObj.getTryoutInventory(); // 试用库存

				if (tryoutStock > totalStock) {
					freeObj.setTryoutInventory(totalStock);
				}
			}
			// 用作排序
			if (freeObj.getTryoutInventory() <= 0) { // 库存数等于0时结束时间为当前时间
				freeObj.setSortEndTime(DateUtil.getSysDateTimeString());
			} else {
				freeObj.setSortEndTime(freeObj.getEndTime());
			}
			MDataMap sInfoMap = DbUp.upTable("pc_skuinfo").one("sku_code", mDataMap.get("sku_code"), "seller_code",
					appCode);
			if (null == sInfoMap) {
				break;
			}
			SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
			sSupport.serialize(sInfoMap, skuObj);
			skuList.add(skuObj);
			productObj.setProductSkuInfoList(skuList); // 把sku信息添加到product实体类里
			MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", skuObj.getProductCode(),
					"seller_code", appCode, "product_status", "4497153900060002");
			if (null != pInfoMap) {
				SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
				pSupport.serialize(pInfoMap, productObj);

				// 产品详情（内容图片）商品标签
				// MDataMap descriptMap =
				// DbUp.upTable("pc_productdescription").oneWhere("keyword","","","product_code"
				// ,productObj.getProductCode());
				// if (null != descriptMap) {
				// productObj.setLabels(descriptMap.get("keyword"));
				// }
				freeObj.setpInfo(productObj);
				resultList.add(freeObj);
			}
		}

		return resultList;
	}

	/**
	 * 获取规定时间内失效的试用商品列表 .
	 * 
	 * @param day天数(不可为空,必须为数字)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<PcFreeTryOutGood> getFreeTryOutGoodsListForEnd(int day, String appCode) {
		List<PcFreeTryOutGood> resultList = new ArrayList<PcFreeTryOutGood>();

		if (null == appCode || "".equals(appCode)) {
			return resultList;
		}

		String tWhere = " end_time > date_sub(curdate(),interval " + day + " day) and end_time < now() and app_code = '"
				+ appCode + "' ";
		List<MDataMap> tMapList = DbUp.upTable("oc_tryout_products").query("", "end_time desc", tWhere, null, -1, -1);
		for (MDataMap mDataMap : tMapList) {
			List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();
			PcProductinfo productObj = new PcProductinfo(); // product信息
			ProductSkuInfo skuObj = new ProductSkuInfo(); // sku信息
			PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品信息
			SerializeSupport<PcFreeTryOutGood> fSupport = new SerializeSupport<PcFreeTryOutGood>();
			fSupport.serialize(mDataMap, freeObj);

			freeObj.setSortEndTime(freeObj.getEndTime()); // 排序用结束时间

			// 有效的付邮试用中，如果总库存小于试用库存时，把试用库存设为sku总剩余库存数
			// if ("449746930002".equals(freeObj.getIsFreeShipping())) {
			// //查询库存
			// StoreService
			// storeService=BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
			// int totalStock =
			// storeService.getStockNumByStore(freeObj.getSkuCode()); //剩余总库存
			// int tryoutStock = freeObj.getTryoutInventory(); //试用库存
			//
			// if (tryoutStock > totalStock) {
			// freeObj.setTryoutInventory(totalStock);
			// }
			// }

			MDataMap sInfoMap = DbUp.upTable("pc_skuinfo").one("sku_code", mDataMap.get("sku_code"), "seller_code",
					appCode);
			if (null == sInfoMap) {
				break;
			}
			SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
			sSupport.serialize(sInfoMap, skuObj);
			skuList.add(skuObj);
			productObj.setProductSkuInfoList(skuList); // 把sku信息添加到product实体类里
			MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", skuObj.getProductCode(),
					"seller_code", appCode, "product_status", "4497153900060002");
			if (null != pInfoMap) {
				SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
				pSupport.serialize(pInfoMap, productObj);

				// 产品详情（内容图片）商品标签
				// MDataMap descriptMap =
				// DbUp.upTable("pc_productdescription").oneWhere("keyword","","","product_code"
				// ,productObj.getProductCode());
				// if (null != descriptMap) {
				// productObj.setLabels(descriptMap.get("keyword"));
				// }
				freeObj.setpInfo(productObj);
				resultList.add(freeObj);
			}
		}
		return resultList;
	}

	/**
	 * 获取用户试用商品列表 .
	 * 
	 * @param userCode
	 *            用户编号(必须非空)
	 * @param freeCode
	 *            试用类型(只能传入：积分包邮：449746930001；付邮试用：449746930002两种)；
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<PcFreeTryOutGood> getUserFreeTryOutGoodsList(String userCode, String freeCode, String appCode) {
		List<PcFreeTryOutGood> resultList = new ArrayList<PcFreeTryOutGood>();

		if (null == appCode || "".equals(appCode) || null == userCode || "".equals(userCode)) {
			return resultList;
		}
		// 只能传入：积分包邮：449746930001；付邮试用：449746930002两种
		if (!"449746930001".equals(freeCode) && !"449746930002".equals(freeCode)) {
			return resultList;
		}

		String applySql = "select od.sku_code as sku_code,od.order_code as order_code,oi.order_status as status,oi.create_time as create_time from oc_orderinfo oi,oc_orderdetail od"
				+ " where oi.order_code = od.order_code and oi.order_type='449715200003' and oi.order_status != '4497153900010006' and oi.order_status != '4497153900010001' "
				+ " and oi.seller_code = '" + appCode + "' and oi.buyer_code='" + userCode
				+ "'  order by oi.create_time desc ";
		List<Map<String, Object>> skuCodesMapList = DbUp.upTable("oc_orderdetail").dataSqlList(applySql, null);
		if (null != skuCodesMapList && skuCodesMapList.size() > 0) {
			for (Map<String, Object> mapOrder : skuCodesMapList) {
				PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品
				PcProductinfo productObj = new PcProductinfo(); // product信息
				String skuCode = String.valueOf(mapOrder.get("sku_code"));
				String orderCode = String.valueOf(mapOrder.get("order_code"));
				String tryoutStatus = String.valueOf(mapOrder.get("status"));

				MDataMap activityCodeMap = DbUp.upTable("oc_order_activity").oneWhere("activity_code", "", "",
						"order_code", orderCode);
				String activityCode = "";
				if (null != activityCodeMap) {
					activityCode = activityCodeMap.get("activity_code");

				}
				productObj = this.getProductInfoForP(skuCode, appCode);

				// 商品申请人数
				String sqlApplyNum = "select COUNT(oi.buyer_code) as applyNum from oc_order_activity oa,oc_orderinfo oi,oc_orderdetail od"
						+ " where oa.order_code = oi.order_code and od.order_code = oi.order_code  and oi.order_type='449715200003' and oi.order_status != '4497153900010006'  "
						+ " and oa.activity_code='" + activityCode + "' and od.sku_code = '" + skuCode
						+ "' and oa.activity_type='449715400005' group by od.sku_code ";
				Map<String, Object> mapObj = DbUp.upTable("oc_orderdetail").dataSqlOne(sqlApplyNum, null);
				int applyNum = 0;
				if (null != mapObj && null != mapObj.get("applyNum")) {
					applyNum = Integer.parseInt(String.valueOf(mapObj.get("applyNum")));
				}

				// 试用商品活动信息
				String whereStr = " sku_code = '" + skuCode + "' and app_code = '" + appCode
						+ "' and is_freeShipping = '" + freeCode + "' and start_time <= '" + mapOrder.get("create_time")
						+ "' and end_time >= '" + mapOrder.get("create_time") + "'";
				MDataMap activityProduct = DbUp.upTable("oc_tryout_products").oneWhere("", "", whereStr);

				if (null != activityProduct && !activityProduct.isEmpty()) {
					SerializeSupport<PcFreeTryOutGood> pSupport = new SerializeSupport<PcFreeTryOutGood>();
					pSupport.serialize(activityProduct, freeObj);

					// 有效的付邮试用中，如果总库存小于试用库存时，把试用库存设为sku总剩余库存数

					// 当前时间跟试用商品活动结束时间比较，活动失效大于0，活动生效小于0
					int compare = DateUtil.getSysDateTimeString().compareTo(freeObj.getEndTime());
					if ("449746930002".equals(freeObj.getIsFreeShipping()) && compare < 0) {
						// 查询库存
						StoreService storeService = BeansHelper
								.upBean("bean_com_cmall_systemcenter_service_StoreService");
						int totalStock = storeService.getStockNumByStore(freeObj.getSkuCode()); // 剩余总库存
						int tryoutStock = freeObj.getTryoutInventory(); // 试用库存

						if (tryoutStock > totalStock) {
							freeObj.setTryoutInventory(totalStock);
						}
					}

					freeObj.setApplyNum(applyNum); // 申请人数
					freeObj.setTryoutStatus(tryoutStatus); // 申请状态
					freeObj.setpInfo(productObj);
					resultList.add(freeObj);
				}
			}
			// 试用活动结束时间倒排序
			Collections.sort(resultList, new Comparator() {
				public int compare(Object free1, Object free2) {
					PcFreeTryOutGood one = (PcFreeTryOutGood) free1;
					PcFreeTryOutGood two = (PcFreeTryOutGood) free2;
					return two.getEndTime().compareTo(one.getEndTime());
				}
			});

		}
		return resultList;
	}

	/**
	 * 获取用户申请免费试用商品列表（审核试用商品用）
	 * 
	 * @param userCode
	 *            用户编号(必须非空)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<PcFreeTryOutGood> getUserApplyFreeTryOutGoodsList(String userCode, String appCode) {
		List<PcFreeTryOutGood> resultList = new ArrayList<PcFreeTryOutGood>();

		if (null == appCode || "".equals(appCode) || null == userCode || "".equals(userCode)) {
			return resultList;
		}
		String sWhere = " member_code = '" + userCode + "' and app_code = '" + appCode + "' ";
		List<MDataMap> applyList = DbUp.upTable("nc_freetryout_apply").queryAll("", "-create_time", sWhere, null);
		for (MDataMap mDataMap : applyList) {
			PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品
			PcProductinfo productObj = new PcProductinfo(); // product信息
			String skuCode = mDataMap.get("sku_code");
			String activityCode = mDataMap.get("activityCode");
			String endTime = mDataMap.get("end_time");
			productObj = this.getProductInfoForP(skuCode, appCode);

			// 商品申请人数
			int applyNum = DbUp.upTable("nc_freetryout_apply").count("app_code", appCode, "sku_code", skuCode,
					"activityCode", activityCode, "end_time", endTime);

			// 试用商品活动信息
			MDataMap activityProduct = DbUp.upTable("oc_tryout_products").one("app_code", appCode, "activity_code",
					activityCode, "sku_code", skuCode, "end_time", endTime);

			if (null != activityProduct && !activityProduct.isEmpty()) {
				SerializeSupport<PcFreeTryOutGood> pSupport = new SerializeSupport<PcFreeTryOutGood>();
				pSupport.serialize(activityProduct, freeObj);
			}

			freeObj.setApplyNum(applyNum); // 申请人数
			freeObj.setTryoutStatus(mDataMap.get("status")); // 审核状态
			freeObj.setpInfo(productObj);
			resultList.add(freeObj);
		}
		return resultList;
	}

	/**
	 * 根据SkuCode获取商品信息 .
	 * 
	 * @param skuCodes
	 *            skuCode(多个skuCode时中间用逗号隔开)（必须非空）
	 * @param sellerCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<Map<String, Object>> getSkuInfoForSkus(String skuCodes, String sellerCode) {
		List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();
		if (null == sellerCode || null == skuCodes || "".equals(skuCodes) || "".equals(sellerCode)) {
			return resultListMap;
		}
		skuCodes = skuCodes.replace(",", "','");
		String sWhere = "sku_code in ('" + skuCodes + "') and seller_code = '" + sellerCode + "'";
		List<MDataMap> skuMap = DbUp.upTable("pc_skuinfo").queryAll("", "", sWhere, null);
		for (MDataMap mDataMap : skuMap) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			ProductSkuInfo skuObj = new ProductSkuInfo();
			SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
			sSupport.serialize(mDataMap, skuObj);
			skuObj.setSkuValue(mDataMap.get("sku_keyvalue"));

			// 购买数
			Map<String, Integer> sellCountMap = (this.getSkuSellCountForGroup(sellerCode, skuObj.getSkuCode()));
			Integer sellCount = 0;

			if (sellCountMap != null && !sellCountMap.isEmpty()) {
				sellCount = sellCountMap.get(skuObj.getSkuCode());
			}
			skuObj.setSellCount(sellCount);

			PcProductinfo productObj = new PcProductinfo();
			MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", skuObj.getProductCode(),
					"seller_code", sellerCode);
			if (null != pInfoMap) {
				SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
				pSupport.serialize(pInfoMap, productObj);

				// 产品详情（内容图片）商品标签
				MDataMap descriptMap = DbUp.upTable("pc_productdescription").oneWhere("keyword", "", "", "product_code",
						productObj.getProductCode());
				if (null != descriptMap) {
					productObj.setLabels(descriptMap.get("keyword"));
				}
			}

			// 把sku信息添加到结果集中
			mapData.put("skuInfo", skuObj);
			// 把product信息添加到结果集中
			mapData.put("productInfo", productObj);
			resultListMap.add(mapData);
		}
		return resultListMap;
	}

	/**
	 * 获取商品详情 惠美丽
	 * 
	 * @param skuCode
	 *            商品编码(必须非空)
	 * @param sellerCode
	 *            APP编码(必须非空)
	 * @return
	 */
	public PcProductinfo getProductInfoForP(String skuCode, String sellerCode) {
		List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();
		PcProductinfo productObj = new PcProductinfo(); // product信息
		ProductSkuInfo skuObj = new ProductSkuInfo(); // sku信息

		if (null == skuCode || null == sellerCode || "".equals(skuCode) || "".equals(sellerCode)) {
			return new PcProductinfo();
		}
		MDataMap sInfoMap = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode, "seller_code", sellerCode);
		if (null == sInfoMap) {
			return productObj;
		}
		SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
		sSupport.serialize(sInfoMap, skuObj);
		skuObj.setSkuValue(sInfoMap.get("sku_keyvalue"));

		// 购买数
		skuObj.setSellCount(this.getSkuSellCount(skuObj.getSkuCode()));

		skuList.add(skuObj);
		productObj.setProductSkuInfoList(skuList); // 把sku信息添加到product实体类里
		MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", skuObj.getProductCode(), "seller_code",
				sellerCode);
		SerializeSupport<PcProductinfo> pSupport = new SerializeSupport<PcProductinfo>();
		pSupport.serialize(pInfoMap, productObj);
		// 产品详情（内容图片）
		MDataMap descriptMap = DbUp.upTable("pc_productdescription").one("product_code", skuObj.getProductCode());
		if (null != descriptMap && !descriptMap.isEmpty()) {
			PcProductdescription description = new PcProductdescription();
			SerializeSupport<PcProductdescription> ss = new SerializeSupport<PcProductdescription>();
			ss.serialize(descriptMap, description);
			productObj.setDescription(description);

			productObj.setLabels(description.getKeyword()); // 商品标签
		}
		// 图片列表list（轮播图）
		List<MDataMap> picUrlsMap = DbUp.upTable("pc_productpic").queryByWhere("product_code", skuObj.getProductCode());
		List<PcProductpic> picUrls = new ArrayList<PcProductpic>();
		if (picUrlsMap == null || picUrlsMap.size() == 0) {
			productObj.setPcPicList(picUrls);
		} else {
			for (MDataMap picUrlMap : picUrlsMap) {
				PcProductpic picObj = new PcProductpic();
				SerializeSupport<PcProductpic> ss = new SerializeSupport<PcProductpic>();
				ss.serialize(picUrlMap, picObj);
				picUrls.add(picObj);
			}
			productObj.setPcPicList(picUrls);
		}
		MDataMap brandMapParam = new MDataMap();
		brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", productObj.getBrandCode());
		if (brandMapParam != null) {
			productObj.setBrandName(brandMapParam.get("brand_name"));
		}
		return productObj;
	}

	/**
	 * 获取商品详情 惠家有
	 * 
	 * @param productCode
	 *            商品编码(必须非空)
	 * @param sellerCode
	 *            APP编码(必须非空)
	 * @return
	 */
	public PcProductinfoForFamily getProductInfoForPP(String productCode, String sellerCode) {
		PcProductinfoForFamily productObj = new PcProductinfoForFamily(); // product信息
		if (null == productCode || null == sellerCode || "".equals(productCode) || "".equals(sellerCode)) {
			return productObj;
		}
		MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", productCode, "seller_code", sellerCode);
		if (null == pInfoMap) {
			return productObj;
		}
		SerializeSupport<PcProductinfoForFamily> pSupport = new SerializeSupport<PcProductinfoForFamily>();
		pSupport.serialize(pInfoMap, productObj);
		
		PlusModelProductInfo productInfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
		
		// 产品详情（内容图片）
		if(productInfo != null){
			PcProductdescription description = new PcProductdescription();
			description.setZid(productInfo.getDescription().getZid());
			description.setUid(productInfo.getDescription().getUid());
			description.setProductCode(productCode);
			description.setKeyword(productInfo.getDescription().getKeyword());
			description.setDescriptionPic(productInfo.getDescription().getDescriptionPic());
			description.setDescriptionInfo(productInfo.getDescription().getDescriptionInfo());
			productObj.setDescription(description);
			productObj.setLabels(description.getKeyword());
		}else{
			MDataMap descriptMap = DbUp.upTable("pc_productdescription").one("product_code", productCode);
			if (null != descriptMap) {
				PcProductdescription description = new PcProductdescription();
				SerializeSupport<PcProductdescription> ss = new SerializeSupport<PcProductdescription>();
				ss.serialize(descriptMap, description);
				productObj.setDescription(description);

				productObj.setLabels(description.getKeyword()); // 商品标签
			}
		}
		
		// 图片列表list（轮播图）
		if(productInfo != null){
			PcProductpic picObj;
			for(String url : productInfo.getPcPicList()){
				picObj = new PcProductpic();
				picObj.setPicUrl(url);
				productObj.getPcPicList().add(picObj);
			}
		}else{
			List<MDataMap> picUrlsMap = DbUp.upTable("pc_productpic").queryByWhere("product_code", productCode);
			List<PcProductpic> picUrls = new ArrayList<PcProductpic>();
			for (MDataMap picUrlMap : picUrlsMap) {
				PcProductpic picObj = new PcProductpic();
				SerializeSupport<PcProductpic> ss = new SerializeSupport<PcProductpic>();
				ss.serialize(picUrlMap, picObj);
				picUrls.add(picObj);
			}
			productObj.setPcPicList(picUrls);
		}

		if(productInfo != null){
			productObj.setBrandName(productInfo.getBrandName());
		}else{
			// 品牌
			MDataMap brandMapParam = new MDataMap();
			brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", productObj.getBrandCode());
			if (brandMapParam != null) {
				productObj.setBrandName(brandMapParam.get("brand_name"));
			}
		}

		// 查询出商品下的sku
		List<MDataMap> skuListMap = DbUp.upTable("pc_skuinfo").queryByWhere("product_code", productCode, "seller_code",
				sellerCode, "sale_yn", "Y", "flag_enable", "1");
		if (null == skuListMap) {
			return productObj;
		}
		List<ProductSkuInfoForFamily> skuList = new ArrayList<ProductSkuInfoForFamily>();
		// 查询库存
		// StoreService
		// storeService=BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		// ProductStoreService productStoreService = new ProductStoreService();
		PlusSupportStock supportStock = new PlusSupportStock();
		for (MDataMap mDataMap : skuListMap) {
			ProductSkuInfoForFamily skuObj = new ProductSkuInfoForFamily();
			SerializeSupport<ProductSkuInfoForFamily> sSupport = new SerializeSupport<ProductSkuInfoForFamily>();
			sSupport.serialize(mDataMap, skuObj);
			skuObj.setSkuValue(mDataMap.get("sku_keyvalue"));

			// 对Sku规格进行字典排序
			if (null != skuObj.getSkuKey() && !"".equals(skuObj.getSkuKey())) {
				String[] keys = skuObj.getSkuKey().split("&");
				String temp = "";
				for (int i = keys.length - 1; i > 0; i--) {
					for (int j = 0; j < i; ++j) {
						if (keys[j + 1].compareToIgnoreCase(keys[j]) < 0) {
							temp = keys[j];
							keys[j] = keys[j + 1];
							keys[j + 1] = temp;
						}
					}
				}
				StringBuffer sb = new StringBuffer();
				for (String key : keys) {
					sb.append(key + "&");
				}
				String keyValue = sb.toString().substring(0, sb.length() - 1);
				skuObj.setSkuKey(keyValue);
			}

			Map<String, Object> fkiMap = this.getSkuActivity(skuObj.getSkuCode(), sellerCode); // 获取商品促销信息
			FlashsalesSkuInfo fkiObj = (fkiMap == null ? new FlashsalesSkuInfo()
					: (FlashsalesSkuInfo) fkiMap.get("flashsalesObj")); // 促销商品

			skuObj.setFkiObj(fkiObj); // 商品促销信息
			skuObj.setStockNumSum(supportStock.upSalesStock(skuObj.getSkuCode()));
			// skuObj.setStockNumSum(storeService.getStockNumByStore(skuObj.getSkuCode()));
			// //总库存
			skuList.add(skuObj); // 添加到list里
		}

		if (null == skuList || skuList.size() < 1) {
			/**
			 * 如果商品下不存在sku信息则把此商品标注为已下架状态 add by ligj time:2015-04-10 16:01:30
			 */
			productObj.setProductStatus("4497153900060003");
		}
		productObj.setProductSkuInfoList(skuList); // 把sku信息添加到product实体类里

		return productObj;
	}

	/**
	 * 获取商品详情 （惠家有390以上版本调用）
	 * 
	 * @param productCode
	 *            商品编码(必须非空)
	 * @param sellerCode
	 *            APP编码(必须非空)
	 * @return
	 */
	// public PcProductinfoForFamily getProductInfoForCache(String productCode){
	//
	// PcProductinfoForFamily productObj = new PcProductinfoForFamily();
	// //product信息
	// if (null == productCode || "".equals(productCode)) {
	// return productObj;
	// }
	// //从缓存中取出商品基本信息
	// PlusModelProductInfo plusModelProductinfo = new
	// LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
	//
	// productObj.setProductCode(productCode);
	// productObj.setProductCodeOld(plusModelProductinfo.getProductCodeOld());
	// productObj.setProductName(plusModelProductinfo.getProductName());
	// productObj.setSellerCode(plusModelProductinfo.getSellerCode());
	// productObj.setSmallSellerCode(plusModelProductinfo.getSmallSellerCode());
	// productObj.setBrandCode(plusModelProductinfo.getBrandCode());
	// productObj.setBrandName(plusModelProductinfo.getBrandName());
	// productObj.setProductWeight(plusModelProductinfo.getProductWeight());
	// productObj.setMinSellPrice(plusModelProductinfo.getMinSellPrice());
	// productObj.setMaxSellPrice(plusModelProductinfo.getMaxSellPrice());
	// productObj.setCostPrice(plusModelProductinfo.getCost_price());
	// productObj.setMainPicUrl(plusModelProductinfo.getMainpicUrl());
	// productObj.setVideoUrl(plusModelProductinfo.getVideoUrl());
	// productObj.setProductVolume(plusModelProductinfo.getProductVolume());
	// productObj.setProductVolumeItem(plusModelProductinfo.getProductVolumeItem());
	// productObj.setTransportTemplate(plusModelProductinfo.getTransportTemplate());
	// productObj.setProductStatus(plusModelProductinfo.getProductStatus());
	// productObj.setValidate_flag(plusModelProductinfo.getValidateFlag());
	//
	// //产品详情（内容图片）
	// PlusModelPcProductdescription descriptPlus =
	// plusModelProductinfo.getDescription();
	// if (null != descriptPlus) {
	// PcProductdescription description = new PcProductdescription();
	// description.setZid(descriptPlus.getZid());
	// description.setUid(descriptPlus.getUid());
	// description.setProductCode(descriptPlus.getProductCode());
	// description.setKeyword(descriptPlus.getKeyword());
	// description.setDescriptionPic(descriptPlus.getDescriptionPic());
	// description.setDescriptionInfo(descriptPlus.getDescriptionInfo());
	// productObj.setDescription(description);
	// productObj.setLabels(description.getKeyword()); //商品标签
	// }
	// //图片列表list（轮播图）
	// List<PlusModelPcProductpic> pcPicList =
	// plusModelProductinfo.getPcPicList();
	// for (PlusModelPcProductpic pcPic : pcPicList) {
	// PcProductpic picObj = new PcProductpic();
	// picObj.setZid(pcPic.getZid());
	// picObj.setUid(pcPic.getUid());
	// picObj.setSkuCode(pcPic.getSkuCode());
	// picObj.setProductCode(pcPic.getProductCode());
	// picObj.setPicUrl(pcPic.getPicUrl());
	// productObj.getPcPicList().add(picObj);
	// }
	//
	// //获取商品下的sku
	// List<PlusModelProductSkuInfo> skuList =
	// plusModelProductinfo.getSkuList();
	//
	// List<ProductSkuInfoForFamily> resultSkuList = new
	// ArrayList<ProductSkuInfoForFamily>();
	// //查询库存
	// PlusSupportStock supportStock = new PlusSupportStock();
	// for (PlusModelProductSkuInfo plusModelProductSkuInfo : skuList) {
	// ProductSkuInfoForFamily skuObj = new ProductSkuInfoForFamily();
	//
	// skuObj.setSkuCode(plusModelProductSkuInfo.getSkuCode());
	// skuObj.setSellPrice(plusModelProductSkuInfo.getSellPrice());
	// skuObj.setMarketPrice(plusModelProductSkuInfo.getMarketPrice());
	// skuObj.setSkuName(plusModelProductSkuInfo.getSkuName());
	// skuObj.setMiniOrder(plusModelProductSkuInfo.getMiniOrder());
	//
	// skuObj.setSkuValue(plusModelProductSkuInfo.getSkuKeyValue());
	// skuObj.setSkuKey(plusModelProductSkuInfo.getSkuKey());
	// skuObj.setStockNumSum(supportStock.upSalesStock(skuObj.getSkuCode()));
	// skuObj.setStockNum(skuObj.getStockNumSum());
	// resultSkuList.add(skuObj); //添加到list里
	// }
	// productObj.setProductSkuInfoList(resultSkuList); //把sku信息添加到product实体类里
	//
	// return productObj;
	// }
	/**
	 * 获取商品详情 家有汇
	 * 
	 * @param productCode
	 *            商品编码(必须非空)
	 * @return
	 */
	public PcProductinfoForFamily getProductInfoForHomePool(String productCode) {
		PcProductinfoForFamily productObj = new PcProductinfoForFamily(); // product信息
		if (null == productCode || "".equals(productCode)) {
			return productObj;
		}
		MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", productCode);
		if (null == pInfoMap) {
			return productObj;
		}
		SerializeSupport<PcProductinfoForFamily> pSupport = new SerializeSupport<PcProductinfoForFamily>();
		pSupport.serialize(pInfoMap, productObj);
		// 图片列表list（轮播图）
		List<MDataMap> picUrlsMap = DbUp.upTable("pc_productpic").queryByWhere("product_code", productCode);
		List<PcProductpic> picUrls = new ArrayList<PcProductpic>();
		for (MDataMap picUrlMap : picUrlsMap) {
			PcProductpic picObj = new PcProductpic();
			SerializeSupport<PcProductpic> ss = new SerializeSupport<PcProductpic>();
			ss.serialize(picUrlMap, picObj);
			picUrls.add(picObj);
		}
		productObj.setPcPicList(picUrls);

		// 查询出商品下的sku
		// List<MDataMap> skuListMap =
		// DbUp.upTable("pc_skuinfo").queryByWhere("product_code",productCode);
		// if (null == skuListMap) {
		// return productObj;
		// }
		// List<ProductSkuInfoForFamily> skuList = new
		// ArrayList<ProductSkuInfoForFamily>();
		// for (MDataMap mDataMap : skuListMap) {
		// ProductSkuInfoForFamily skuObj = new ProductSkuInfoForFamily();
		// SerializeSupport<ProductSkuInfoForFamily> sSupport = new
		// SerializeSupport<ProductSkuInfoForFamily>();
		// sSupport.serialize(mDataMap, skuObj);
		// skuObj.setSkuValue(mDataMap.get("sku_keyvalue"));
		//
		// //对Sku规格进行字典排序
		// if (null != skuObj.getSkuKey() && !"".equals(skuObj.getSkuKey())) {
		// String[] keys = skuObj.getSkuKey().split("&");
		// String temp = "";
		// for (int i = keys.length-1;i>0; i--) {
		// for(int j=0 ; j<i; ++j)
		// {
		// if(keys[j+1].compareToIgnoreCase(keys[j]) < 0)
		// {
		// temp=keys[j];
		// keys[j]=keys[j+1];
		// keys[j+1]=temp;
		// }
		// }
		// }
		// StringBuffer sb = new StringBuffer();
		// for (String key : keys) {
		// sb.append(key+"&");
		// }
		// String keyValue = sb.toString().substring(0, sb.length()-1);
		// skuObj.setSkuKey(keyValue);
		// }
		// skuList.add(skuObj); //添加到list里
		// }
		// productObj.setProductSkuInfoList(skuList); //把sku信息添加到product实体类里

		return productObj;
	}

	/**
	 * 获取商品详情价格 家有汇
	 * 
	 * @param productCode
	 *            商品编码(必须非空)
	 * @return
	 */
	public PcProductinfoForFamily getProductPriceForHomePool(String productCode) {
		PcProductinfoForFamily productObj = new PcProductinfoForFamily(); // product信息
		if (null == productCode || "".equals(productCode)) {
			return productObj;
		}
		MDataMap pInfoMap = DbUp.upTable("pc_productinfo").oneWhere(
				"product_code,min_sell_price,max_sell_price,market_price", "", "", "product_code", productCode);
		if (null == pInfoMap) {
			return productObj;
		}
		String min_sell_price = pInfoMap.get("min_sell_price");
		String max_sell_price = pInfoMap.get("max_sell_price");
		String market_price = pInfoMap.get("market_price");

		productObj.setProductCode(pInfoMap.get("product_code"));
		productObj.setMinSellPrice(
				new BigDecimal(Double.valueOf(StringUtils.isEmpty(min_sell_price) ? "0" : min_sell_price)));
		productObj.setMaxSellPrice(
				new BigDecimal(Double.valueOf(StringUtils.isEmpty(max_sell_price) ? "0" : max_sell_price)));
		productObj
				.setMarketPrice(new BigDecimal(Double.valueOf(StringUtils.isEmpty(market_price) ? "0" : market_price)));

		// 查询出商品下的sku
		List<MDataMap> skuListMap = DbUp.upTable("pc_skuinfo").queryByWhere("product_code", productCode, "flag_enable",
				"1", "sale_yn", "Y");
		if (null == skuListMap) {
			return productObj;
		}
		List<ProductSkuInfoForFamily> skuList = new ArrayList<ProductSkuInfoForFamily>();
		// 查询库存
		StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		StringBuffer skuCodesBuff = new StringBuffer();
		for (MDataMap mDataMap : skuListMap) {
			skuCodesBuff.append(mDataMap.get("sku_code"));
			skuCodesBuff.append("','");
		}

		// 获取sku库存数量
		Map<String, Integer> skuStockNum = storeService.getStockNumByStoreMulti(
				"'" + skuCodesBuff.toString().substring(0, skuCodesBuff.toString().length() - 3) + "'");
		// 上拆单后查询库存方法需要改为这个，分一地入库与非一地入库
		// ProductStoreService storeService = new ProductStoreService();
		for (MDataMap mDataMap : skuListMap) {
			ProductSkuInfoForFamily skuObj = new ProductSkuInfoForFamily();
			SerializeSupport<ProductSkuInfoForFamily> sSupport = new SerializeSupport<ProductSkuInfoForFamily>();
			sSupport.serialize(mDataMap, skuObj);
			// 不可卖时库存设为0
			if ("Y".equals(skuObj.getSaleYn())) {
				skuObj.setStockNumSum(
						null == skuStockNum.get(skuObj.getSkuCode()) ? 0 : skuStockNum.get(skuObj.getSkuCode())); // 总库存
			} else {
				skuObj.setStockNumSum(0);
			}
			// skuObj.setStockNumSum(storeService.getStockNumBySku(skuObj.getSkuCode()));
			skuList.add(skuObj); // 添加到list里
		}
		productObj.setProductSkuInfoList(skuList); // 把sku信息添加到product实体类里
		// 品牌名称
		MDataMap brandMapParam = new MDataMap();
		brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code", productObj.getBrandCode());
		if (brandMapParam != null) {
			productObj.setBrandName(brandMapParam.get("brand_name"));
		}
		return productObj;
	}

	/**
	 * 根据skuCode获取试用商品详情(我的试用中调用)
	 * 
	 * @param skuCode
	 *            Sku编码(必须非空)
	 * @param freeCode
	 *            试用类型(为空时查询所有)积分包邮：449746930001；付邮试用：449746930002;审核试用：
	 *            449746930003
	 * @param userCode
	 *            买家编号(为空时申请使用状态为：0：未申请)
	 * @param appCode
	 *            App编码(必须非空)
	 * @param endTime
	 *            试用活动结束时间(为空时不进行判断)
	 * @return
	 */
	public List<Map<String, Object>> getMyTryOutGoodsForSkuCode(String skuCode, String freeCode, String userCode,
			String appCode, String endTime) {
		List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();

		if (null == appCode || "".equals(appCode) || null == skuCode || "".equals(skuCode)) {
			return resultMapList;
		}

		String sInfoWhere = " sku_code = '" + skuCode + "' and seller_code='" + appCode + "' ";
		List<MDataMap> sInfoMapList = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", sInfoWhere, null);
		for (MDataMap sInfoMap : sInfoMapList) {
			PcProductinfo productObj = new PcProductinfo(); // product信息

			productObj = this.getProductInfoForP(sInfoMap.get("sku_code"), appCode);

			MDataMap dataWhere = new MDataMap();
			dataWhere.put("sku_code", sInfoMap.get("sku_code"));
			dataWhere.put("app_code", appCode);
			dataWhere.put("is_freeShipping", freeCode);
			dataWhere.put("end_time", (null == endTime ? "" : endTime));
			String sWhere = "";
			if (null == freeCode || "".equals(freeCode)) {
				sWhere = "  sku_code=:sku_code and app_code=:app_code ";
			} else {
				sWhere = "  sku_code=:sku_code and app_code=:app_code  and is_freeShipping=:is_freeShipping ";
			}
			if (null != endTime && !"".equals(endTime)) {
				sWhere += "  and end_time=:end_time ";
			} else {
				sWhere += " and start_time < now() and end_time > now() ";
			}

			List<MDataMap> tMapList = DbUp.upTable("oc_tryout_products").queryAll("", "start_time desc", sWhere,
					dataWhere);
			// 如果查询不到免费试用商品信息时取已过期的活动的end_time离当前时间最近的那条记录
			// 这个if主要是为了避免在我的试用中的免费试用列表刷新前终止商品导致商品信息为空的情况
			if ((null == tMapList || tMapList.size() == 0) && "449746930003".equals(freeCode)) {
				// 获取最近的结束试用商品，列表不刷新时终止的活动肯定都是最近结束的
				String maxEndTimeSql = "select MAX(end_time) as end_time from oc_tryout_products where end_time <= now() and sku_code=:sku_code "
						+ "and app_code=:app_code  and is_freeShipping=:is_freeShipping group by end_time";
				Map<String, Object> maxEndTime = DbUp.upTable("oc_tryout_products").dataSqlOne(maxEndTimeSql,
						dataWhere);
				sWhere = " sku_code=:sku_code and app_code=:app_code  and is_freeShipping=:is_freeShipping and end_time='"
						+ String.valueOf(maxEndTime.get("end_time")) + "' ";
				tMapList = DbUp.upTable("oc_tryout_products").queryAll("", "start_time desc", sWhere, dataWhere);
			}
			for (MDataMap mDataMap : tMapList) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品信息
				SerializeSupport<PcFreeTryOutGood> fSupport = new SerializeSupport<PcFreeTryOutGood>();
				fSupport.serialize(mDataMap, freeObj);
				freeObj.setpInfo(productObj);

				// 有效的付邮试用中，如果总库存小于试用库存时，把试用库存设为sku总剩余库存数
				if ("449746930002".equals(freeObj.getIsFreeShipping()) && (null == endTime || "".equals(endTime)
						|| DateUtil.getSysDateTimeString().compareTo(endTime) < 0)) {
					// 查询库存
					StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
					int totalStock = storeService.getStockNumByStore(freeObj.getSkuCode()); // 剩余总库存
					int tryoutStock = freeObj.getTryoutInventory(); // 试用库存

					if (tryoutStock > totalStock) {
						freeObj.setTryoutInventory(totalStock);
					}
				}

				// 申请状态
				if (null == userCode || "".equals(userCode)) {
					freeObj.setTryoutStatus("0"); // 申请状态为0时表示未申请
				} else {
					MDataMap orderWhere = new MDataMap();
					orderWhere.put("sku_code", sInfoMap.get("sku_code") == null ? "" : sInfoMap.get("sku_code"));
					orderWhere.put("seller_code", appCode);
					orderWhere.put("buyer_code", userCode);
					String applySql = "select oi.order_status as status from oc_orderinfo oi,oc_orderdetail od,oc_tryout_products tp"
							+ " where oi.order_code = od.order_code and oi.order_type='449715200003' and oi.order_status != '4497153900010006' "
							+ " and (tp.start_time < oi.create_time and tp.end_time > oi.create_time) and tp.sku_code = od.sku_code "
							+ " and oi.seller_code = :seller_code and od.sku_code= :sku_code and oi.buyer_code=:buyer_code";
					Map<String, Object> orderStatus = DbUp.upTable("oc_orderdetail").dataSqlOne(applySql, orderWhere);
					if (null != orderStatus && null != orderStatus.get("status")
							&& !"".equals(orderStatus.get("status"))) {
						freeObj.setTryoutStatus(orderStatus.get("status").toString()); // 申请状态
					} else {
						freeObj.setTryoutStatus("0"); // 申请状态为0时表示未申请
					}
				}
				// 月销量
				resultMap.put("skuSellNum", this.getSkuMonthSellCount(sInfoMap.get("sku_code"), appCode));
				resultMap.put("freeGood", freeObj); // 将试用商品对象封装到map里
				resultMapList.add(resultMap);
			}
		}
		return resultMapList;
	}

	/**
	 * 根据skuCode获取试用商品详情(试用列表中调用)
	 * 
	 * @param skuCode
	 *            Sku编码(必须非空)
	 * @param freeCode
	 *            试用类型(为空时查询所有)积分包邮：449746930001；付邮试用：449746930002;审核试用：
	 *            449746930003
	 * @param userCode
	 *            买家编号(为空时申请使用状态为：0：未申请)
	 * @param appCode
	 *            App编码(必须非空)
	 * @param endTime
	 *            试用活动结束时间(为空时不进行判断)
	 * @return
	 */
	public List<Map<String, Object>> getFreeTryOutGoodsForSkuCode(String skuCode, String freeCode, String userCode,
			String appCode, String endTime) {
		List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();

		if (null == appCode || "".equals(appCode) || null == skuCode || "".equals(skuCode)) {
			return resultMapList;
		}

		String sInfoWhere = " sku_code = '" + skuCode + "' and seller_code='" + appCode + "' ";
		List<MDataMap> sInfoMapList = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", sInfoWhere, null);
		for (MDataMap sInfoMap : sInfoMapList) {
			PcProductinfo productObj = new PcProductinfo(); // product信息

			productObj = this.getProductInfoForP(sInfoMap.get("sku_code"), appCode);

			MDataMap dataWhere = new MDataMap();
			dataWhere.put("sku_code", sInfoMap.get("sku_code"));
			dataWhere.put("app_code", appCode);
			dataWhere.put("is_freeShipping", freeCode);
			dataWhere.put("end_time", (null == endTime ? "" : endTime));
			String sWhere = "";
			if (null == freeCode || "".equals(freeCode)) {
				sWhere = "  sku_code=:sku_code and app_code=:app_code ";
			} else {
				sWhere = "  sku_code=:sku_code and app_code=:app_code  and is_freeShipping=:is_freeShipping ";
			}
			if (null != endTime && !"".equals(endTime)) {
				sWhere += "  and end_time=:end_time ";
			} else {
				sWhere += " and start_time < now() and end_time > now() ";
			}

			List<MDataMap> tMapList = DbUp.upTable("oc_tryout_products").queryAll("", "start_time desc", sWhere,
					dataWhere);
			// 如果查询不到免费试用商品信息时取已过期的活动的end_time离当前时间最近的那条记录
			// 这个if主要是为了避免在我的试用中的免费试用列表刷新前终止商品导致商品信息为空的情况
			if ((null == tMapList || tMapList.size() == 0) && "449746930003".equals(freeCode)) {
				// 获取最近的结束试用商品，列表不刷新时终止的活动肯定都是最近结束的
				String maxEndTimeSql = "select MAX(end_time) as end_time from oc_tryout_products where end_time <= now() and sku_code=:sku_code "
						+ "and app_code=:app_code  and is_freeShipping=:is_freeShipping group by end_time";
				Map<String, Object> maxEndTime = DbUp.upTable("oc_tryout_products").dataSqlOne(maxEndTimeSql,
						dataWhere);
				sWhere = " sku_code=:sku_code and app_code=:app_code  and is_freeShipping=:is_freeShipping and end_time='"
						+ String.valueOf(maxEndTime.get("end_time")) + "' ";
				tMapList = DbUp.upTable("oc_tryout_products").queryAll("", "start_time desc", sWhere, dataWhere);

			}
			for (MDataMap mDataMap : tMapList) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				PcFreeTryOutGood freeObj = new PcFreeTryOutGood(); // 试用商品信息
				SerializeSupport<PcFreeTryOutGood> fSupport = new SerializeSupport<PcFreeTryOutGood>();
				fSupport.serialize(mDataMap, freeObj);
				freeObj.setpInfo(productObj);
				endTime = freeObj.getEndTime();
				// 有效的付邮试用中，如果总库存小于试用库存时，把试用库存设为sku总剩余库存数
				if ("449746930002".equals(freeObj.getIsFreeShipping()) && (null == endTime || "".equals(endTime)
						|| DateUtil.getSysDateTimeString().compareTo(endTime) < 0)) {
					// 查询库存
					StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
					int totalStock = storeService.getStockNumByStore(freeObj.getSkuCode()); // 剩余总库存
					int tryoutStock = freeObj.getTryoutInventory(); // 试用库存

					if (tryoutStock > totalStock) {
						freeObj.setTryoutInventory(totalStock);
					}
				}

				// 申请状态
				if (null == userCode || "".equals(userCode)) {
					freeObj.setTryoutStatus("0"); // 申请状态为0时表示未申请
				} else {
					MDataMap orderWhere = new MDataMap();
					orderWhere.put("sku_code", sInfoMap.get("sku_code") == null ? "" : sInfoMap.get("sku_code"));
					orderWhere.put("seller_code", appCode);
					orderWhere.put("buyer_code", userCode);
					orderWhere.put("is_freeShipping", freeCode);
					orderWhere.put("end_time", endTime);
					String applySql = "select oi.order_status as status from oc_orderinfo oi,oc_orderdetail od,oc_tryout_products tp"
							+ " where oi.order_code = od.order_code and oi.order_type='449715200003' and oi.order_status != '4497153900010006' "
							+ " and (tp.start_time < oi.create_time and tp.end_time > oi.create_time) and tp.is_freeShipping = :is_freeShipping  and tp.sku_code = od.sku_code "
							+ " and oi.seller_code = :seller_code and od.sku_code= :sku_code and oi.buyer_code=:buyer_code and tp.end_time=:end_time";
					Map<String, Object> orderStatus = DbUp.upTable("oc_orderdetail").dataSqlOne(applySql, orderWhere);
					if (null != orderStatus && null != orderStatus.get("status")
							&& !"".equals(orderStatus.get("status"))) {
						freeObj.setTryoutStatus(orderStatus.get("status").toString()); // 申请状态
					} else {
						freeObj.setTryoutStatus("0"); // 申请状态为0时表示未申请
					}
				}
				// 月销量
				resultMap.put("skuSellNum", this.getSkuMonthSellCount(sInfoMap.get("sku_code"), appCode));
				resultMap.put("freeGood", freeObj); // 将试用商品对象封装到map里
				resultMapList.add(resultMap);
			}
		}
		return resultMapList;
	}

	/**
	 * 根据skuCode获取限时抢购详情(惠美丽 不限制活动开始结束时间)
	 * 
	 * @param skuCode
	 *            Sku编码(为空时查询所有)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<FlashsalesSkuInfo> getFlashsalesForSkuCode(String skuCode, String appCode) {
		List<FlashsalesSkuInfo> flashsaleList = new ArrayList<FlashsalesSkuInfo>();
		if (null == appCode || "".equals(appCode)) {
			return flashsaleList;
		}

		String activityWhere = "app_code = '" + appCode + "' and status='449746740002' ";
		// 获取到该app下的活动编号列表
		List<MDataMap> activityCodesList = DbUp.upTable("oc_activity_flashsales").queryAll("activity_code", "",
				activityWhere, null);
		if (null == activityCodesList || activityCodesList.size() == 0) { // 查询不到app下的活动时返回空list
			return flashsaleList;
		}
		StringBuffer activityCodeBuffer = new StringBuffer();
		for (MDataMap mDataMap : activityCodesList) {
			activityCodeBuffer.append(mDataMap.get("activity_code"));
			activityCodeBuffer.append("','");
		}
		String activityCodesStr = activityCodeBuffer.toString();

		String flashsalesWhere = "";
		if (null == skuCode || "".equals(skuCode)) {
			flashsalesWhere = " activity_code in ('" + activityCodesStr.substring(0, activityCodesStr.length() - 3)
					+ "') and status = '449746810001'";
		} else {
			flashsalesWhere = " activity_code in ('" + activityCodesStr.substring(0, activityCodesStr.length() - 3)
					+ "') and sku_code='" + skuCode + "' and status='449746810001' ";
		}
		List<MDataMap> fMapList = DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", flashsalesWhere, null); // 闪购商品信息
		if (null == fMapList || fMapList.size() == 0) { // 查询不到闪购信息时返回空list
			return flashsaleList;
		}
		PcProductinfo productObj = new PcProductinfo(); // product信息
		Map<String, Integer> surplusNumMap = this.getSkuCodesFlashActivity(appCode); // 获取到所有正在进行闪购的商品code与剩余数量
		for (MDataMap mDataMap : fMapList) {
			// 获取商品ID，判断上下架状态，商品上架进行下一步操作。
			MDataMap productCodeMap = DbUp.upTable("pc_skuinfo").oneWhere("product_code", "", "", "sku_code",
					mDataMap.get("sku_code"));
			int count = DbUp.upTable("pc_productinfo").count("product_code", productCodeMap.get("product_code"),
					"product_status", "4497153900060002");
			if (count > 0) {
				MDataMap activityMap = DbUp.upTable("oc_activity_flashsales").one("activity_code",
						mDataMap.get("activity_code"), "status", "449746740002");
				FlashsalesSkuInfo flashsalesObj = new FlashsalesSkuInfo(); // 闪购商品信息
				SerializeSupport<FlashsalesSkuInfo> fs = new SerializeSupport<FlashsalesSkuInfo>();
				fs.serialize(mDataMap, flashsalesObj);
				fs.serialize(activityMap, flashsalesObj);

				Integer surplusNum = surplusNumMap.get(flashsalesObj.getSkuCode()); // 商品促销库存剩余件数
				flashsalesObj.setSurplusNum(
						new BigDecimal(surplusNum == null ? flashsalesObj.getSalesNum().intValue() : surplusNum));

				productObj = this.getProductInfoForP(flashsalesObj.getSkuCode(), appCode);
				flashsalesObj.setProduct(productObj); // 商品信息
				flashsalesObj.setStartTime(activityMap.get("start_time")); // 活动开始时间
				flashsalesObj.setEndTime(activityMap.get("end_time")); // 活动结束时间
				flashsaleList.add(flashsalesObj);
			}
		}

		// 按照结束时间正排序
		Collections.sort(flashsaleList, new Comparator<Object>() {
			public int compare(Object flashsale1, Object flashsale2) {
				String one = ((FlashsalesSkuInfo) flashsale1).getEndTime();
				String two = ((FlashsalesSkuInfo) flashsale2).getEndTime();
				;
				return one.compareTo(two);
			}
		});

		return flashsaleList;
	}

	/**
	 * 根据skuCode获取限时抢购详情(惠美丽 限制活动开始结束时间)
	 * 
	 * @param skuCode
	 *            Sku编码(为空时查询所有)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<FlashsalesSkuInfo> getFlashsalesForSkuCodeAll(String skuCode, String appCode) {
		List<FlashsalesSkuInfo> flashsaleList = new ArrayList<FlashsalesSkuInfo>();
		if (null == appCode || "".equals(appCode)) {
			return flashsaleList;
		}

		String activityWhere = "app_code = '" + appCode
				+ "' and status='449746740002'  and start_time <= now() and end_time > now() ";
		// 获取到该app下的活动编号列表
		List<MDataMap> activityCodesList = DbUp.upTable("oc_activity_flashsales").queryAll("activity_code", "",
				activityWhere, null);
		if (null == activityCodesList || activityCodesList.size() == 0) { // 查询不到app下的活动时返回空list
			return flashsaleList;
		}

		StringBuffer activityCodesBuff = new StringBuffer();
		for (MDataMap mDataMap : activityCodesList) {
			activityCodesBuff.append(mDataMap.get("activity_code"));
			activityCodesBuff.append("','");
		}
		String activityCodesStr = activityCodesBuff.toString();

		String flashsalesWhere = "";
		if (null == skuCode || "".equals(skuCode)) {
			flashsalesWhere = " activity_code in ('" + activityCodesStr.substring(0, activityCodesStr.length() - 3)
					+ "') and status = '449746810001'";
		} else {
			flashsalesWhere = " activity_code in ('" + activityCodesStr.substring(0, activityCodesStr.length() - 3)
					+ "') and sku_code='" + skuCode + "' and status='449746810001' ";
		}
		List<MDataMap> fMapList = DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", flashsalesWhere, null); // 闪购商品信息
		if (null == fMapList || fMapList.size() == 0) { // 查询不到闪购信息时返回空list
			return flashsaleList;
		}
		// 已下架的商品codes列表
		String skuSql = " select ps.sku_code as sku_code from pc_skuinfo ps "
				+ " LEFT JOIN pc_productinfo pp on ps.product_code = pp.product_code "
				+ " where pp.product_status <> '4497153900060002' and ps.seller_code = '" + appCode + "'";
		List<Map<String, Object>> unShelveCodes = DbUp.upTable("pc_skuinfo").dataSqlList(skuSql, null);

		// 过滤掉已下架的商品
		for (int j = 0; j < fMapList.size(); j++) {
			for (Map<String, Object> map : unShelveCodes) {
				if (fMapList.get(j).get("sku_code").equals(map.get("sku_code"))) {
					fMapList.remove(j);
					j--;
				}
			}
		}
		Map<String, Integer> surplusNumMap = this.getSkuCodesFlashActivity(appCode); // 获取到所有正在进行闪购的商品code与剩余数量

		// PcProductinfo productObj = new PcProductinfo(); //product信息
		for (MDataMap mDataMap : fMapList) {
			MDataMap activityMap = DbUp.upTable("oc_activity_flashsales").one("activity_code",
					mDataMap.get("activity_code"), "status", "449746740002");
			FlashsalesSkuInfo flashsalesObj = new FlashsalesSkuInfo(); // 闪购商品信息
			SerializeSupport<FlashsalesSkuInfo> fs = new SerializeSupport<FlashsalesSkuInfo>();
			fs.serialize(mDataMap, flashsalesObj);
			fs.serialize(activityMap, flashsalesObj);

			Integer surplusNum = surplusNumMap.get(flashsalesObj.getSkuCode()); // 商品促销库存剩余件数
			flashsalesObj.setSurplusNum(
					new BigDecimal(surplusNum == null ? flashsalesObj.getSalesNum().intValue() : surplusNum));

			if (StringUtils.isEmpty(flashsalesObj.getSkuImgReplace())) { // 闪购的列表图片为空时取商品轮播图的第一张
				// 图片列表list（轮播图）第一张
				String picList = "select pic.pic_url as pic_url from pc_productpic pic LEFT JOIN pc_skuinfo sku ON sku.product_code = pic.product_code where sku.sku_code='"
						+ flashsalesObj.getSkuCode() + "'";
				Map<String, Object> picUrlsMap = DbUp.upTable("pc_productpic").dataSqlOne(picList, null);
				if (null != picUrlsMap && !picUrlsMap.isEmpty()) {
					flashsalesObj.setSkuImgReplace(String.valueOf(picUrlsMap.get("pic_url")));
				}
			}

			flashsalesObj.setStartTime(activityMap.get("start_time")); // 活动开始时间
			if (flashsalesObj.getSurplusNum().intValue() <= 0) { // 剩余库存数为0时，结束时间为当前时间
				flashsalesObj.setEndTime(DateUtil.getSysDateTimeString());
			} else {
				flashsalesObj.setEndTime(activityMap.get("end_time")); // 活动结束时间
			}
			flashsaleList.add(flashsalesObj);
		}
		return flashsaleList;
	}

	/**
	 * 根据skuCode获取限时抢购详情(惠美丽 几天内已结束的限购列表)
	 * 
	 * @param day
	 *            天数(不可为空,必须为数字)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<FlashsalesSkuInfo> getFlashsalesForSkuCodeEnd(int day, String appCode) {
		List<FlashsalesSkuInfo> flashsaleList = new ArrayList<FlashsalesSkuInfo>();
		if (null == appCode || "".equals(appCode)) {
			return flashsaleList;
		}
		// 规定天数内已结束的
		String activityWhere = "app_code = '" + appCode
				+ "' and status='449746740002'  and end_time > date_sub(curdate(),interval " + day
				+ " day) and end_time<now() ";
		// 获取到该app下的活动编号列表
		List<MDataMap> activityCodesList = DbUp.upTable("oc_activity_flashsales").queryAll("activity_code", "",
				activityWhere, null);
		if (null == activityCodesList || activityCodesList.size() == 0) { // 查询不到app下的活动时返回空list
			return flashsaleList;
		}
		String activityCodesStr = "";
		for (MDataMap mDataMap : activityCodesList) {
			activityCodesStr += (mDataMap.get("activity_code") + "','");
		}
		String flashsalesWhere = " activity_code in ('" + activityCodesStr.substring(0, activityCodesStr.length() - 3)
				+ "') and status = '449746810001'";
		List<MDataMap> fMapList = DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", flashsalesWhere, null); // 闪购商品信息
		if (null == fMapList || fMapList.size() == 0) { // 查询不到闪购信息时返回空list
			return flashsaleList;
		}
		// PcProductinfo productObj = new PcProductinfo(); //product信息
		Map<String, Integer> surplusNumMap = this.getSkuCodesFlashActivity(appCode); // 获取到所有正在进行闪购的商品code
		for (MDataMap mDataMap : fMapList) {
			// 获取商品ID，判断上下架状态，商品上架进行下一步操作。
			MDataMap productCodeMap = DbUp.upTable("pc_skuinfo").oneWhere("product_code", "", "", "sku_code",
					mDataMap.get("sku_code"));
			int count = DbUp.upTable("pc_productinfo").count("product_code", productCodeMap.get("product_code"),
					"product_status", "4497153900060002");
			if (count > 0) {
				MDataMap activityMap = DbUp.upTable("oc_activity_flashsales").one("activity_code",
						mDataMap.get("activity_code"), "status", "449746740002");
				FlashsalesSkuInfo flashsalesObj = new FlashsalesSkuInfo(); // 闪购商品信息
				SerializeSupport<FlashsalesSkuInfo> fs = new SerializeSupport<FlashsalesSkuInfo>();
				fs.serialize(mDataMap, flashsalesObj);
				fs.serialize(activityMap, flashsalesObj);

				Integer surplusNum = surplusNumMap.get(flashsalesObj.getSkuCode()); // 商品促销库存剩余件数
				flashsalesObj.setSurplusNum(
						new BigDecimal(surplusNum == null ? flashsalesObj.getSalesNum().intValue() : surplusNum));

				if (StringUtils.isEmpty(flashsalesObj.getSkuImgReplace())) { // 闪购的列表图片为空时取商品轮播图的第一张
					// 图片列表list（轮播图）第一张
					String picList = "select pic.pic_url as pic_url from pc_productpic pic LEFT JOIN pc_skuinfo sku ON sku.product_code = pic.product_code where sku.sku_code='"
							+ flashsalesObj.getSkuCode() + "'";
					Map<String, Object> picUrlsMap = DbUp.upTable("pc_productpic").dataSqlOne(picList, null);
					if (null != picUrlsMap && !picUrlsMap.isEmpty()) {
						flashsalesObj.setSkuImgReplace(String.valueOf(picUrlsMap.get("pic_url")));
					}
				}
				flashsalesObj.setStartTime(activityMap.get("start_time")); // 活动开始时间
				flashsalesObj.setEndTime(activityMap.get("end_time")); // 活动结束时间
				flashsaleList.add(flashsalesObj);
			}
		}

		return flashsaleList;
	}

	/**
	 * 根据skuCode以及地区code获得商品所在仓库的库存以及促销信息
	 * 
	 * @param skuCode
	 *            Sku编码(必须非空)
	 * @param districtCode
	 *            地区编码(必须非空)
	 * @param appCode
	 *            App编码(必须非空)
	 * @return
	 */
	public Map<String, Object> getSkuStock(String skuCode, String districtCode, String appCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (null == skuCode || null == districtCode || null == appCode || "".equals(appCode) || "".equals(districtCode)
				|| "".equals(skuCode)) {
			return resultMap;
		}
		// 查询库存
		StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		int stockNum = storeService.getStockNumByDistrict(districtCode, skuCode);
		resultMap.put("storeNum", stockNum);

		String activityWhere = "app_code = '" + appCode
				+ "' and start_time <= now() and end_time > now() and status='449746740002'";
		// 获取到该app下的活动编号列表
		List<MDataMap> activityCodesList = DbUp.upTable("oc_activity_flashsales").queryAll("activity_code", "",
				activityWhere, null);
		if (null == activityCodesList || activityCodesList.size() == 0) { // 查询不到app下的活动时返回空list
			return resultMap;
		} else {
			String activityCodesStr = "";
			for (MDataMap mDataMap : activityCodesList) {
				activityCodesStr += (mDataMap.get("activity_code") + "','");
			}
			String flashsalesWhere = "activity_code in ('"
					+ activityCodesStr.substring(0, activityCodesStr.length() - 3) + "') and sku_code='" + skuCode
					+ "' and  status = '449746810001' ";
			List<MDataMap> fMapList = DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", flashsalesWhere, null); // 闪购商品信息
			if (null == fMapList || fMapList.size() == 0) { // 查询不到闪购信息时返回空list
				return resultMap;
			} else {
				FlashsalesSkuInfo fkiObj = new FlashsalesSkuInfo();
				SerializeSupport<FlashsalesSkuInfo> sSupport = new SerializeSupport<FlashsalesSkuInfo>();
				sSupport.serialize(fMapList.get(0), fkiObj);
				resultMap.put("flashsalesObj", fkiObj);
			}
		}
		return resultMap;
	}

	public Map<String, Object> getSkuActivity(String skuCode, String appCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (null == skuCode || null == appCode || "".equals(appCode) || "".equals(skuCode)) {
			return resultMap;
		}

		// String sql="SELECT DISTINCT s.*,f.* from oc_flashsales_skuInfo s
		// RIGHT JOIN oc_activity_flashsales f on
		// f.activity_code=s.activity_code and f.start_time <= now() and
		// f.end_time > now() and f.status='449746740002' and
		// f.app_code='"+appCode+"' and s.status='449746810001' ORDER BY
		// f.create_time desc LIMIT 0,1";

		String sql = "SELECT DISTINCT s.*,f.*   from oc_flashsales_skuInfo s   JOIN oc_activity_flashsales f on f.activity_code=s.activity_code and f.start_time <= now() and f.end_time > now() and f.status='449746740002' and f.app_code='"
				+ appCode + "'  and s.sku_code='" + skuCode
				+ "' and s.status='449746810001'    ORDER BY f.create_time desc  LIMIT 0,1";

		List<Map<String, Object>> list = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(sql, null);
		if (list == null || list.size() < 1) {
			return resultMap;
		}

		Map<String, Object> data = list.get(0);
		if (data == null || data.get("sku_code") == null) {
			return resultMap;
		}
		// 判断促销库存
		if (salesNumSurplus(skuCode, data.get("activity_code").toString()) <= 0) {
			return resultMap;
		}
		MDataMap dataMap = new MDataMap(data);

		FlashsalesSkuInfo fkiObj = new FlashsalesSkuInfo();
		SerializeSupport<FlashsalesSkuInfo> sSupport = new SerializeSupport<FlashsalesSkuInfo>();
		sSupport.serialize(dataMap, fkiObj);

		FlashsalesActivity activityObj = new FlashsalesActivity();
		SerializeSupport<FlashsalesActivity> activitySupport = new SerializeSupport<FlashsalesActivity>();
		activitySupport.serialize(dataMap, activityObj);

		// 活动信息
		fkiObj.setActivityName(activityObj.getActivityName());
		fkiObj.setStartTime(activityObj.getStartTime());
		fkiObj.setEndTime(activityObj.getEndTime());
		fkiObj.setRemark(activityObj.getRemark());

		fkiObj.setActivityCode(activityObj.getActivityCode());

		resultMap.put("flashsalesObj", fkiObj);

		return resultMap;
	}

	/**
	 * 获取商品最低价格，仅支持惠家有调用（如果传入的商品编号为空，则默认为查询惠家有全部商品）
	 * 
	 * @param productCodeArr
	 * @param appCode
	 * @return
	 */
	public Map<String, BigDecimal> getMinProductActivity(List<String> productCodeArr) {
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		String productWhere = "";
		if (null != productCodeArr && productCodeArr.size() > 0) {
			productWhere = "product_code in ('" + StringUtils.join(productCodeArr, "','") + "')";
		} else {
			productWhere = "seller_code = 'SI2003'";
		}
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,sell_price", "",
				productWhere, null);
		if (null != skuInfoMap && skuInfoMap.size() > 0) {
			String skuCodes = "";
			Map<String, BigDecimal> skuPriceMap = new HashMap<String, BigDecimal>(); // sku与其销售价的map
			Map<String, Map<String, Integer>> productSkuMap = new HashMap<String, Map<String, Integer>>(); // productCodesMap
			for (MDataMap mDataMap : skuInfoMap) {
				String skuCode = mDataMap.get("sku_code");
				String proCode = mDataMap.get("product_code");

				skuCodes += (skuCode + ",");
				skuPriceMap.put(skuCode, new BigDecimal(mDataMap.get("sell_price")));
				resultMap.put(proCode, BigDecimal.ZERO); // 如果获取不到商品下sku的信息时把最小价格设置为0

				// productSkuMap赋值，key,productCode;value,skuCodeMap
				Map<String, Integer> skuMap = productSkuMap.get(proCode);
				if (null == skuMap) {
					skuMap = new HashMap<String, Integer>();
				}
				skuMap.put(skuCode, 1);
				productSkuMap.put(proCode, skuMap);

			}
			if (StringUtils.isEmpty(skuCodes) || skuCodes.length() < 1) {
				return resultMap;
			}

			// 不传入商品编码时不加skuCode条件
			String skuWhere = "";
			if (null != productCodeArr && productCodeArr.size() > 0) {
				skuWhere = " and fs.sku_code in ('" + skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','")
						+ "') ";
			}
			String activitySql = "select fs.sku_code sku_code,fs.vip_price vip_price from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() "
					+ skuWhere + " and fs.status='449746810001' and af.status='449746740002' and af.app_code='SI2003' ";
			List<Map<String, Object>> activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql,
					null);

			for (String productCode : productSkuMap.keySet()) {
				BigDecimal minPrice = new BigDecimal(0); // product最终价格

				for (String skuCode : productSkuMap.get(productCode).keySet()) {
					BigDecimal vipPrice = skuPriceMap.get(skuCode);
					for (Map<String, Object> map : activityMapList) {
						if (skuCode.equals(map.get("sku_code"))) {
							vipPrice = new BigDecimal(map.get("vip_price").toString());
							break;
						}
					}
					if (minPrice.compareTo(BigDecimal.ZERO) <= 0 || minPrice.compareTo(vipPrice) > 0) {
						minPrice = vipPrice;
					}
					resultMap.put(productCode, minPrice.setScale(2, BigDecimal.ROUND_DOWN));
				}
			}
		}
		return resultMap;
	}

	/**
	 * 获取商品最低价格，仅支持沙皮狗调用（如果传入的商品编号为空，则默认为查询沙皮狗全部商品）
	 * 
	 * @param productCodeArr
	 * @param appCode
	 * @return
	 */
	public Map<String, BigDecimal> getMinProductActivitySpg(List<String> productCodeArr) {
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		String productWhere = "";
		if (null != productCodeArr && productCodeArr.size() > 0) {
			productWhere = "product_code in ('" + StringUtils.join(productCodeArr, "','") + "')";
		} else {
			productWhere = "seller_code = 'SI3003'";
		}
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,sell_price", "",
				productWhere, null);
		if (null != skuInfoMap && skuInfoMap.size() > 0) {
			String skuCodes = "";
			Map<String, BigDecimal> skuPriceMap = new HashMap<String, BigDecimal>(); // sku与其销售价的map
			Map<String, Map<String, Integer>> productSkuMap = new HashMap<String, Map<String, Integer>>(); // productCodesMap
			for (MDataMap mDataMap : skuInfoMap) {
				String skuCode = mDataMap.get("sku_code");
				String proCode = mDataMap.get("product_code");

				skuCodes += (skuCode + ",");
				skuPriceMap.put(skuCode, new BigDecimal(mDataMap.get("sell_price")));
				resultMap.put(proCode, BigDecimal.ZERO); // 如果获取不到商品下sku的信息时把最小价格设置为0

				// productSkuMap赋值，key,productCode;value,skuCodeMap
				Map<String, Integer> skuMap = productSkuMap.get(proCode);
				if (null == skuMap) {
					skuMap = new HashMap<String, Integer>();
				}
				skuMap.put(skuCode, 1);
				productSkuMap.put(proCode, skuMap);

			}
			if (StringUtils.isEmpty(skuCodes) || skuCodes.length() < 1) {
				return resultMap;
			}

			// 不传入商品编码时不加skuCode条件
			String skuWhere = "";
			if (null != productCodeArr && productCodeArr.size() > 0) {
				skuWhere = " and fs.sku_code in ('" + skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','")
						+ "') ";
			}
			String activitySql = "select fs.sku_code sku_code,fs.vip_price vip_price from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() "
					+ skuWhere + " and fs.status='449746810001' and af.status='449746740002' and af.app_code='SI3003' ";
			List<Map<String, Object>> activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql,
					null);

			for (String productCode : productSkuMap.keySet()) {
				BigDecimal minPrice = new BigDecimal(0); // product最终价格

				for (String skuCode : productSkuMap.get(productCode).keySet()) {
					BigDecimal vipPrice = skuPriceMap.get(skuCode);
					for (Map<String, Object> map : activityMapList) {
						if (skuCode.equals(map.get("sku_code"))) {
							vipPrice = new BigDecimal(map.get("vip_price").toString());
							break;
						}
					}
					if (minPrice.compareTo(BigDecimal.ZERO) <= 0 || minPrice.compareTo(vipPrice) > 0) {
						minPrice = vipPrice;
					}
					resultMap.put(productCode, minPrice.setScale(2, BigDecimal.ROUND_DOWN));
				}
			}
		}
		return resultMap;
	}

	/**
	 * 获取商品最低价格，仅支持惠家有调用（如果传入的商品编号为空，则默认为查询惠家有全部商品）(此方法包括了内购价)
	 * 
	 * @param productCodeArr
	 * @param userType（4497469400050001:内购会员，4497469400050002:注册会员）
	 * @return
	 */
	public Map<String, BigDecimal> getMinProductActivityNew(List<String> productCodeArr, String userType) {
		Map<String, BigDecimal> resultMap = new HashMap<String,BigDecimal>();
		Map<String,PlusModelSkuInfo> skuInfo = getMinProductActivityIncloudGroupBuying(productCodeArr,userType);
		if(!skuInfo.isEmpty()) {
			for(Entry<String, PlusModelSkuInfo> entry : skuInfo.entrySet()) {
				resultMap.put(entry.getKey(), entry.getValue().getSellPrice());
			}
		}
		return resultMap;
	}
	
	/**
	 * 拓展拼团商品价格
	 * @param productCodeArr
	 * @param userType
	 * @return
	 */
	public Map<String,PlusModelSkuInfo> getMinProductActivityIncloudGroupBuying(List<String> productCodeArr, String userType){

		Map<String, PlusModelSkuInfo> resultMap = new HashMap<String, PlusModelSkuInfo>();
		PlusModelSkuInfo skuResult = new PlusModelSkuInfo();
		String productWhere = "";
		if (null != productCodeArr && productCodeArr.size() > 0) {
			productWhere = "product_code in ('" + StringUtils.join(productCodeArr, "','") + "')";
		} else {
			productWhere = "seller_code = 'SI2003'";
		}
		String sku = productWhere + " and sale_yn='Y'";
		// String sku = productWhere;

		skuResult.setSellPrice(BigDecimal.ZERO);
		for (String productCode : productCodeArr) {
			resultMap.put(productCode, skuResult); // 如果获取不到商品下sku的信息时把最小价格设置为0
		}
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,sell_price", "", sku,
				null);
		if (null != skuInfoMap && skuInfoMap.size() > 0) {
			String skuCodes = "";
			Map<String, BigDecimal> skuPriceMap = new HashMap<String, BigDecimal>(); // sku与其销售价的map
			Map<String, Map<String, Integer>> productSkuMap = new HashMap<String, Map<String, Integer>>(); // productCodesMap
			for (MDataMap mDataMap : skuInfoMap) {
				String skuCode = mDataMap.get("sku_code");
				String proCode = mDataMap.get("product_code");

				skuCodes += (skuCode + ",");
				skuPriceMap.put(skuCode, new BigDecimal(mDataMap.get("sell_price")));
				resultMap.put(proCode, skuResult); // 如果获取不到商品下sku的信息时把最小价格设置为0

				// productSkuMap赋值，key,productCode;value,skuCodeMap
				Map<String, Integer> skuMap = productSkuMap.get(proCode);
				if (null == skuMap) {
					skuMap = new HashMap<String, Integer>();
				}
				skuMap.put(skuCode, 1);
				productSkuMap.put(proCode, skuMap);

			}
			if (StringUtils.isEmpty(skuCodes) || skuCodes.length() < 1) {
				return resultMap;
			}
			boolean flagVipSpecial = this.checkIsVipSpecial(userType); // true符合参加内购的条件
			List<Map<String, Object>> activityMapList = new ArrayList<Map<String, Object>>();
			// if (!flagVipSpecial) {
			// 不传入商品编码时不加skuCode条件
			String skuWhere = "";
			if (null != productCodeArr && productCodeArr.size() > 0) {
				skuWhere = " and fs.sku_code in ('" + skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','")
						+ "') ";
			}
			String activitySql = "select fs.sku_code sku_code,fs.product_code product_code,fs.vip_price vip_price,fs.activity_code activity_code from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() "
					+ skuWhere + " and fs.status='449746810001' and af.status='449746740002' and af.app_code='SI2003' ";
			activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql, null);

			// }
			List<MDataMap> productCostPrice = new ArrayList<MDataMap>();
			if (flagVipSpecial) {
				productCostPrice = DbUp.upTable("pc_productinfo")
						.queryAll("product_code,cost_price,small_seller_code,validate_flag", "", productWhere, null);
			}
			Map<String, BigDecimal> vipSpecialPriceMap = new HashMap<String, BigDecimal>();
			Map<String, String> productSellerCodeMap = new HashMap<String, String>();
			Map<String, String> productValidateFlagMap = new HashMap<String, String>();
			for (MDataMap mDataMap : productCostPrice) {
				BigDecimal vipSpecialPrice = new BigDecimal(mDataMap.get("cost_price"))
						.add(new BigDecimal(bConfig("productcenter.vipSpecialPrice")));
				vipSpecialPriceMap.put(mDataMap.get("product_code"), vipSpecialPrice);
				productSellerCodeMap.put(mDataMap.get("product_code"), mDataMap.get("small_seller_code"));
				productValidateFlagMap.put(mDataMap.get("product_code"), mDataMap.get("validate_flag"));
			}
			// 参加闪购活动的商品
			Map<String, Integer> vipFlashMap = new HashMap<String, Integer>();
			Map<String, BigDecimal> vipFlashSkuMap = new HashMap<String, BigDecimal>();
			for (Map<String, Object> map : activityMapList) {
				// 判断促销库存
				if (salesNumSurplus(map.get("sku_code").toString(), map.get("activity_code").toString()) > 0) {
					vipFlashMap.put(map.get("product_code").toString(), 1);
					vipFlashSkuMap.put(map.get("sku_code").toString(), new BigDecimal(map.get("vip_price").toString()));
				}
			}
			// 参加特价活动的商品
			Map<String, Integer> vipSecKillProductMap = new HashMap<String, Integer>();
			Map<String, BigDecimal> vipSecKillSkuMap = new HashMap<String, BigDecimal>();
			PlusSupportProduct support = new PlusSupportProduct();
			for (String productCode : productSkuMap.keySet()) {
				for (String skuCode : productSkuMap.get(productCode).keySet()) {
					PlusModelSkuInfo skuSuppore = support.upSkuInfoBySkuCode(skuCode);
					// 特价活动
					if (StringUtils.isNotEmpty(skuSuppore.getEventCode())) {
						// if (StringUtils.isNotEmpty(skuSuppore.getEventCode())
						// && 1 == skuSuppore.getBuyStatus()) {
						vipSecKillSkuMap.put(skuCode, skuSuppore.getSellPrice());
						vipSecKillProductMap.put(productCode, 1);
					}
					//参与拼团商品的的SKU拼团标识
					if("4497472600010024".equals(skuSuppore.getEventType())){
						skuResult.setEventType(skuSuppore.getEventType());//拼团类型编号
						skuResult.setSkuPrice(skuSuppore.getSkuPrice());//拼团SKU实际价格
						skuResult.setGroupBuyingPrice(skuSuppore.getSellPrice());//拼团SKU销售价
					}
				}
			}
			for (String productCode : productSkuMap.keySet()) {
				BigDecimal minPrice = new BigDecimal(0); // product最终价格

				for (String skuCode : productSkuMap.get(productCode).keySet()) {
					BigDecimal vipPrice = skuPriceMap.get(skuCode);
					if (vipFlashMap.containsKey(productCode)) { // 参加闪购时
						if (vipFlashSkuMap.containsKey(skuCode)) {
							vipPrice = vipFlashSkuMap.get(skuCode);
						}
					} else { // 不参加闪购时判断其他活动
						// 参加特价活动
						if (vipSecKillProductMap.containsKey(productCode)) {
							if (vipSecKillSkuMap.containsKey(skuCode)) {
								vipPrice = vipSecKillSkuMap.get(skuCode);
							}
						} else {
							// 内购活动
							if (flagVipSpecial
									&& AppConst.MANAGE_CODE_HOMEHAS.equals(productSellerCodeMap.get(productCode))
									&& "N".equals(productValidateFlagMap.get(productCode))) {
								if (vipSpecialPriceMap.containsKey(productCode)) {
									minPrice = vipSpecialPriceMap.get(productCode).setScale(2,
											BigDecimal.ROUND_HALF_UP);
								}
							}
						}
					}
					// 开始比较价格大小
					if (minPrice.compareTo(BigDecimal.ZERO) <= 0 || minPrice.compareTo(vipPrice) > 0) {
						minPrice = vipPrice;
					}
					skuResult.setSellPrice(minPrice);
					resultMap.put(productCode, skuResult);
				}
			}
		}
		return resultMap;
	}

	/**
	 * 获取商品最低价格，仅支持沙皮狗调用（如果传入的商品编号为空，则默认为查询沙皮狗全部商品）
	 * 
	 * @param productCodeArr
	 * @param userType（预留字段，暂时不用）
	 * @return
	 */
	public Map<String, BigDecimal> getMinProductActivitySharpei(List<String> productCodeArr, String userType) {
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		String productWhere = "";
		if (null != productCodeArr && productCodeArr.size() > 0) {
			productWhere = "product_code in ('" + StringUtils.join(productCodeArr, "','") + "')";
		} else {
			productWhere = "seller_code = 'SI3003'";
		}
		// String sku = productWhere+" and sale_yn='Y' and flag_enable='1'";
		String sku = productWhere;
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,sell_price", "", sku,
				null);
		if (null != skuInfoMap && skuInfoMap.size() > 0) {
			String skuCodes = "";
			Map<String, BigDecimal> skuPriceMap = new HashMap<String, BigDecimal>(); // sku与其销售价的map
			Map<String, Map<String, Integer>> productSkuMap = new HashMap<String, Map<String, Integer>>(); // productCodesMap
			for (MDataMap mDataMap : skuInfoMap) {
				String skuCode = mDataMap.get("sku_code");
				String proCode = mDataMap.get("product_code");

				skuCodes += (skuCode + ",");
				skuPriceMap.put(skuCode, new BigDecimal(mDataMap.get("sell_price")));
				resultMap.put(proCode, BigDecimal.ZERO); // 如果获取不到商品下sku的信息时把最小价格设置为0

				// productSkuMap赋值，key,productCode;value,skuCodeMap
				Map<String, Integer> skuMap = productSkuMap.get(proCode);
				if (null == skuMap) {
					skuMap = new HashMap<String, Integer>();
				}
				skuMap.put(skuCode, 1);
				productSkuMap.put(proCode, skuMap);

			}
			if (StringUtils.isEmpty(skuCodes) || skuCodes.length() < 1) {
				return resultMap;
			}
			List<Map<String, Object>> activityMapList = new ArrayList<Map<String, Object>>();
			// 不传入商品编码时不加skuCode条件
			String skuWhere = "";
			if (null != productCodeArr && productCodeArr.size() > 0) {
				skuWhere = " and fs.sku_code in ('" + skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','")
						+ "') ";
			}
			String activitySql = "select fs.sku_code sku_code,fs.product_code product_code,fs.vip_price vip_price,fs.activity_code activity_code from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() "
					+ skuWhere + " and fs.status='449746810001' and af.status='449746740002' and af.app_code='SI3003' ";
			activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql, null);

			// 参加闪购活动的商品
			Map<String, Integer> vipFlashMap = new HashMap<String, Integer>();
			Map<String, BigDecimal> vipFlashSkuMap = new HashMap<String, BigDecimal>();
			for (Map<String, Object> map : activityMapList) {
				// 判断促销库存
				if (salesNumSurplus(map.get("sku_code").toString(), map.get("activity_code").toString()) > 0) {
					vipFlashMap.put(map.get("product_code").toString(), 1);
					vipFlashSkuMap.put(map.get("sku_code").toString(), new BigDecimal(map.get("vip_price").toString()));
				}
			}
			// 参加特价活动的商品
			Map<String, Integer> vipSecKillProductMap = new HashMap<String, Integer>();
			Map<String, BigDecimal> vipSecKillSkuMap = new HashMap<String, BigDecimal>();
			PlusSupportProduct support = new PlusSupportProduct();
			// for (String productCode : productSkuMap.keySet()) {
			// for (String skuCode : productSkuMap.get(productCode).keySet()) {
			// PlusModelSkuInfo skuSuppore =
			// support.upSkuInfoBySkuCode(skuCode);
			// if(productCode.equals("8016420604")||productCode.equals("6131095")){
			//
			// //特价活动
			// if (StringUtils.isNotEmpty(skuSuppore.getEventCode())) {
			//// if (StringUtils.isNotEmpty(skuSuppore.getEventCode()) && 1 ==
			// skuSuppore.getBuyStatus()) {
			// vipSecKillSkuMap.put(skuCode, skuSuppore.getSellPrice());
			// vipSecKillProductMap.put(productCode, 1);
			// }
			// }
			// }
			// }
			for (String productCode : productSkuMap.keySet()) {
				// BigDecimal minPrice = new BigDecimal(0); //product最终价格
				//
				// for (String skuCode :
				// productSkuMap.get(productCode).keySet()) {
				// BigDecimal vipPrice = skuPriceMap.get(skuCode);
				// if (vipFlashMap.containsKey(productCode)) { //参加闪购时
				// if (vipFlashSkuMap.containsKey(skuCode)) {
				// vipPrice = vipFlashSkuMap.get(skuCode);
				// }
				// }else{ //不参加闪购时判断其他活动
				// //参加特价活动
				// if (vipSecKillProductMap.containsKey(productCode)) {
				// if (vipSecKillSkuMap.containsKey(skuCode)) {
				// vipPrice = vipSecKillSkuMap.get(skuCode);
				// }
				// }
				// }
				// //开始比较价格大小
				// if (minPrice.compareTo(BigDecimal.ZERO) <= 0 ||
				// minPrice.compareTo(vipPrice) > 0) {
				// minPrice = vipPrice;
				// }
				resultMap.put(productCode,
						support.upPriceByProductCode(productCode, "").setScale(2, BigDecimal.ROUND_DOWN));
			}
		}
		return resultMap;
	}

	/**
	 * 根据skuCode查询商品价格
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return
	 */
	public PcProductPrice getSkuPrice(String skuCode, String appCode) {
		PcProductPrice priceObj = new PcProductPrice();

		String skuFields = "sku_code,product_code,sell_price,market_price,stock_num";
		MDataMap dataMap = DbUp.upTable("pc_skuinfo").oneWhere(skuFields, "", "", "sku_code", skuCode, "seller_code",
				appCode);
		if (null != dataMap) {
			SerializeSupport<PcProductPrice> fs = new SerializeSupport<PcProductPrice>();
			fs.serialize(dataMap, priceObj);

			MDataMap whereData = new MDataMap();
			whereData.put("sku_code", skuCode);
			whereData.put("app_code", appCode);
			String activitySql = "select af.start_time start_time,af.end_time end_time,fs.vip_price vip_price,fs.sell_price sell_price  from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() and fs.sku_code=:sku_code and af.app_code=:app_code"
					+ " and fs.status='449746810001' and af.status='449746740002' ";
			Map<String, Object> activityMap = DbUp.upTable("oc_flashsales_skuInfo").dataSqlOne(activitySql, whereData);
			DecimalFormat df = new DecimalFormat("######0.00");
			if (null != activityMap) {
				priceObj.setStartTime(activityMap.get("start_time").toString());
				priceObj.setEndTime(activityMap.get("end_time").toString());
				String vipPrice = activityMap.get("vip_price").toString(); // 会员价
				String marketPrice = dataMap.get("market_price").toString(); // 销售价
				priceObj.setVipPrice(vipPrice);

				// 有促销信息时折扣即为 会员价/市场价
				double discountRate = 0;
				if (!"0".equals(marketPrice)) {
					discountRate = Double.valueOf(vipPrice) / Double.valueOf(marketPrice);
				}
				String format = df.format(discountRate);
				priceObj.setDiscount(format);
			} else {
				// 无促销信息时折扣即为 销售价/市场价
				double dis = 0;
				if (!"0".equals(dataMap.get("market_price").toString())) {
					dis = Double.valueOf(dataMap.get("sell_price").toString())
							/ Double.valueOf(dataMap.get("market_price").toString());
				}
				String format = df.format(dis);

				priceObj.setDiscount(format);

				priceObj.setVipPrice(dataMap.get("sell_price"));
			}
		}
		return priceObj;
	}

	/**
	 * 根据skuCode查询商品价格
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return
	 */
	public PcProductPrice getSkuProductPrice(String skuCode, String appCode) {
		PcProductPrice priceObj = new PcProductPrice();

		String skuFields = "sku_code,product_code,sell_price,market_price,stock_num";
		MDataMap dataMap = DbUp.upTable("pc_skuinfo").oneWhere(skuFields, "", "", "sku_code", skuCode, "seller_code",
				appCode);

		if (null != dataMap) {
			SerializeSupport<PcProductPrice> fs = new SerializeSupport<PcProductPrice>();

			MDataMap pInfoMap = DbUp.upTable("pc_productinfo").one("product_code", dataMap.get("product_code"),
					"seller_code", appCode);

			String marketPrice = "";

			if (pInfoMap != null) {
				marketPrice = pInfoMap.get("market_price");
			}

			dataMap.put("market_price", marketPrice);

			fs.serialize(dataMap, priceObj);
			MDataMap whereData = new MDataMap();
			whereData.put("sku_code", skuCode);
			whereData.put("app_code", appCode);
			String activitySql = "select af.start_time start_time,af.end_time end_time,fs.vip_price vip_price,fs.sell_price sell_price  from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() and fs.sku_code=:sku_code and af.app_code=:app_code"
					+ " and fs.status='449746810001' and af.status='449746740002' ";
			Map<String, Object> activityMap = DbUp.upTable("oc_flashsales_skuInfo").dataSqlOne(activitySql, whereData);
			DecimalFormat df = new DecimalFormat("######0.00");
			if (null != activityMap) {
				priceObj.setStartTime(activityMap.get("start_time").toString());
				priceObj.setEndTime(activityMap.get("end_time").toString());
				String vipPrice = activityMap.get("vip_price").toString(); // 会员价
				priceObj.setVipPrice(vipPrice);

				// 有促销信息时折扣即为 会员价/市场价
				double discountRate = 0;
				if (!"0".equals(marketPrice)) {
					discountRate = Double.valueOf(vipPrice) / Double.valueOf(marketPrice);
				}
				String format = df.format(discountRate);
				priceObj.setDiscount(format);
			} else {
				// 无促销信息时折扣即为 销售价/市场价
				double dis = 0;
				if (!"0".equals(marketPrice)) {
					dis = Double.valueOf(dataMap.get("sell_price").toString())
							/ Double.valueOf(dataMap.get("market_price").toString());
				}
				String format = df.format(dis);
				priceObj.setDiscount(format);
			}
		}
		return priceObj;
	}

	/**
	 * 根据关键字模糊查询商品信息，关键字(sku_name）
	 * 
	 * @param keyword
	 *            关键字，模糊匹配sku_name（为空时查询全部）
	 * @param sellerCode
	 *            App编码(必须非空)
	 * @return
	 */
	public List<Map<String, Object>> getSkuInfoForFuzzy(String keyword, String sellerCode) {
		List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();
		if (null == sellerCode || "".equals(sellerCode)) {
			return resultListMap;
		}
		String sWhere = " seller_code = :seller_code ";
		if (null != keyword && !"".equals(keyword)) {
			keyword = "%" + keyword + "%"; // 此处为模糊匹配。所有值前后都加上%
			sWhere += " and (sku_name like :keyword) ";
		}
		MDataMap whereMap = new MDataMap();
		whereMap.put("seller_code", sellerCode);
		whereMap.put("keyword", keyword);
		List<MDataMap> skuMap = DbUp.upTable("pc_skuinfo").queryAll("", "", sWhere, whereMap);

		String productCodesStr = ""; // 所有product_code拼成“code1','code2','code3,.......”格式的字符串
		StringBuffer productCodesBuffer = new StringBuffer();
		for (MDataMap mDataMap : skuMap) {
			productCodesBuffer.append(mDataMap.get("product_code"));
			productCodesBuffer.append("','");
		}
		productCodesStr = productCodesBuffer.toString();
		List<MDataMap> pInfoMapList = new ArrayList<MDataMap>();
		if (StringUtils.isNotEmpty(productCodesStr)) {
			pInfoMapList = DbUp.upTable("pc_productinfo")
					.queryAll("product_code", "",
							" product_code in ('" + productCodesStr.substring(0, productCodesStr.length() - 3)
									+ "') and product_status = '4497153900060002' and seller_code='" + sellerCode + "'",
							null);
		}
		// 上架的product
		Map<String, Integer> pInfo = new HashMap<String, Integer>();
		for (MDataMap pInfoMap : pInfoMapList) {
			pInfo.put(pInfoMap.get("product_code"), 0);
		}
		String skuCodesStr = ""; // 上架状态的sku_code拼成“code1','code2','code3,.......”格式的字符串
		StringBuffer totalSellCountSkuCodesBuff = new StringBuffer();
		for (MDataMap mDataMap : skuMap) {
			if (null != pInfo.get(mDataMap.get("product_code"))) {
				totalSellCountSkuCodesBuff.append(mDataMap.get("sku_code"));
				totalSellCountSkuCodesBuff.append("','");
			}
		}
		skuCodesStr = totalSellCountSkuCodesBuff.toString();
		Map<String, Integer> sellCountMap = this.getSkuSellCountForGroup(sellerCode,
				(StringUtils.isEmpty(skuCodesStr) ? "" : skuCodesStr.substring(0, skuCodesStr.length() - 3))); // 统计商品的购买数

		for (MDataMap mDataMap : skuMap) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			ProductSkuInfo skuObj = new ProductSkuInfo();
			SerializeSupport<ProductSkuInfo> sSupport = new SerializeSupport<ProductSkuInfo>();
			sSupport.serialize(mDataMap, skuObj);

			Integer sellCount = sellCountMap.get(skuObj.getSkuCode());
			skuObj.setSellCount(sellCount == null ? 0 : sellCount); // 购买数

			PcProductinfo productObj = new PcProductinfo();
			if (null != pInfo.get(skuObj.getProductCode())) {
				// 商品标签
				MDataMap descriptMap = DbUp.upTable("pc_productdescription").one("product_code",
						skuObj.getProductCode());
				if (null != descriptMap && !descriptMap.isEmpty()) {
					productObj.setLabels(descriptMap.get("keyword"));
				}

				mapData.put("skuInfo", skuObj); // 把sku信息添加到结果集中
				mapData.put("productInfo", productObj); // 把product信息添加到结果集中
				resultListMap.add(mapData);
			}
		}
		return resultListMap;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放
	 * 
	 * @param width
	 *            宽度
	 * @param picUrl
	 *            图片地址 多张图片中间用竖线分割,不返回图片的宽高
	 * @return
	 */
	public PicInfo getPicInfoForMany(Integer width, String picUrlsStr) {
		PicInfo picInfo = new PicInfo();
		if (null == picUrlsStr || "".equals(picUrlsStr)) {
			return picInfo;
		}
		String[] picUrls = picUrlsStr.split("\\|");
		StringBuffer newPicUrlBuffer = new StringBuffer();
		for (String picUrl : picUrls) {
			// 如果是gif图片。则不进行压缩操作
			if ("gif".equals(this.getImgType(picUrl))) {
				picInfo.setPicNewUrl(picUrl);
				picInfo.setPicOldUrl(picUrl);
				// return picInfo;
			} else {
				MDataMap picInfoMap = DbUp.upTable("pc_picinfo").one("pic_oldUrl", picUrl, "width",
						String.valueOf(width));
				if (null == picInfoMap) {
					try {

						ImageSupport imageSupport = new ImageSupport(picUrl);
						if (null != width && width > 0) { // 图片宽度大于0时应该对图片进行比例缩放
							int[] ary = imageSupport.getWidthAndHeight();
							// 缩放比
							double dScale = Double.parseDouble(String.valueOf(width)) / ary[0];
							imageSupport.scale(dScale);
							String sTarget = "scale";
							String sDate = FormatHelper.upDateHex();

							String sFileName = WebHelper.upUuid();
							WebUpload webUpload = new WebUpload();
							MWebResult mBigFile = webUpload.remoteUploadCustom(
									StringUtils.substringAfterLast(picUrl, "/"), imageSupport.upTargetByte(), sTarget,
									sDate, "p0", sFileName);

							if (mBigFile.upFlagTrue()) {

								picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
								picInfo.setPicOldUrl(picUrl);
								picInfo.setWidth(width);
								Double dHeight = ary[1] * dScale; // 算出新图的高
								picInfo.setHeight(dHeight.intValue());
								picInfo.setOldWidth(ary[0]);
								picInfo.setOldHeight(ary[1]);

								MDataMap insertMap = new MDataMap();
								insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
								insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
								insertMap.put("width", String.valueOf(picInfo.getWidth()));
								insertMap.put("height", String.valueOf(picInfo.getHeight()));
								insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
								insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
								DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
							} else {
								picInfo.setPicNewUrl(picUrl);
								picInfo.setPicOldUrl(picUrl);
								picInfo.setWidth(ary[0]);
								picInfo.setHeight(ary[1]);
								picInfo.setOldWidth(ary[0]);
								picInfo.setOldHeight(ary[1]);
							}

						} else {
							// 不传入宽度时，返回传入值
							picInfo.setPicOldUrl(picUrl);
							picInfo.setPicNewUrl(picUrl);
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						return new PicInfo();
					}
				} else {
					SerializeSupport<PicInfo> sSupport = new SerializeSupport<PicInfo>();
					sSupport.serialize(picInfoMap, picInfo);
				}
			}
			newPicUrlBuffer.append(picInfo.getPicNewUrl()); // 新地址放到此
			newPicUrlBuffer.append("|");
		}
		String newPicUrlStr = newPicUrlBuffer.toString().substring(0, newPicUrlBuffer.toString().length() - 1);

		PicInfo picInfoResult = new PicInfo();
		picInfoResult.setPicOldUrl(picUrlsStr);
		picInfoResult.setPicNewUrl(newPicUrlStr);

		if (picInfo != null && width > 0 && picInfo.getWidth() > 0) {
			picInfoResult.setWidth(picInfo.getWidth());
			picInfoResult.setHeight(picInfo.getHeight());
		}

		return picInfoResult;
	}

	/**
	 * 传入宽度高度对图片按最大比例缩放图片并图片居中留白
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param picUrl
	 *            图片地址 多张图片中间用竖线分割
	 * @return
	 */
	public PicInfo getPicInfoScaleWhite(Integer width, Integer height, String picUrlsStr) {
		PicInfo picInfo = new PicInfo();
		if (null == picUrlsStr || "".equals(picUrlsStr)) {
			return picInfo;
		}
		String[] picUrls = picUrlsStr.split("\\|");
		StringBuffer newPicUrlBuffer = new StringBuffer();
		for (String picUrl : picUrls) {
			MDataMap picInfoMap = DbUp.upTable("pc_picinfo").one("pic_oldUrl", picUrl, "width", String.valueOf(width),
					"height", String.valueOf(height));
			if (null == picInfoMap) {
				try {
					ImageSupport imageSupport = null;
					if (null != width && width > 0 && null != height && height > 0) { // 图片宽度与高度大于0时应该对图片进行比例缩放
						imageSupport = new ImageSupport(picUrl);
						imageSupport.scaleWhite(width, height);
						String sTarget = "scale";
						String sDate = FormatHelper.upDateHex();

						String sFileName = WebHelper.upUuid();
						WebUpload webUpload = new WebUpload();
						MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
								imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

						if (mBigFile.upFlagTrue()) {

							picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
						}

						picInfo.setPicOldUrl(picUrl);
						picInfo.setWidth(width);
						picInfo.setHeight(height);

						MDataMap insertMap = new MDataMap();
						insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
						insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
						insertMap.put("width", String.valueOf(picInfo.getWidth()));
						insertMap.put("height", String.valueOf(picInfo.getHeight()));

						DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
					} else {
						// 不传入宽度时，返回传入值
						picInfo.setPicOldUrl(picUrl);
						picInfo.setPicNewUrl(picUrl);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					return new PicInfo();
				}
			} else {
				SerializeSupport<PicInfo> sSupport = new SerializeSupport<PicInfo>();
				sSupport.serialize(picInfoMap, picInfo);
			}
			newPicUrlBuffer.append(picInfo.getPicNewUrl()); // 新地址放到此
			newPicUrlBuffer.append("|");
		}
		String newPicUrlStr = newPicUrlBuffer.toString().substring(0, newPicUrlBuffer.toString().length() - 1);

		PicInfo picInfoResult = new PicInfo();
		picInfoResult.setPicOldUrl(picUrlsStr);
		picInfoResult.setPicNewUrl(newPicUrlStr);

		if (picInfo != null && width > 0 && picInfo.getWidth() > 0 && height > 0 && picInfo.getHeight() > 0) {
			picInfoResult.setWidth(picInfo.getWidth());
			picInfoResult.setHeight(picInfo.getHeight());
		}
		return picInfoResult;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放 图片宽度小于传入宽度：大图（宽：传入宽度，高：原图高，url),小图（按传入宽度四分之一缩小）
	 * 大于传入宽度：进行等比例缩放
	 * 
	 * @param width
	 *            宽度
	 * @param picUrl图片地址
	 *            多张图片中间用竖线分割,不返回图片的宽高
	 * @return 图片实体： 宽度，高度，url
	 */
	public List<PicAllInfo> getPicForMany(Integer width, String picUrlsStr) {

		List<PicAllInfo> picList = new ArrayList<PicAllInfo>();
		if (null == picUrlsStr || "".equals(picUrlsStr)) {
			return picList;
		}

		String[] picUrls = picUrlsStr.split("\\|");
		for (String picUrl : picUrls) {

			MDataMap picInfoMap = DbUp.upTable("pc_picinfo").one("pic_oldUrl", picUrl, "width", String.valueOf(width));
			int[] ary = null;
			try {
				ImageSupport imageSupport = new ImageSupport(picUrl);
				ary = imageSupport.getWidthAndHeight();

				if (null == ary) {
					picList.add(new PicAllInfo());
					continue;
				}

			} catch (Exception e) {
				picList.add(new PicAllInfo());
				continue;
			}
			// PicInfos bigInfo = new PicInfos();
			PicAllInfo picAllInfo = new PicAllInfo();
			PicInfos picInfo = new PicInfos();
			if (null == picInfoMap) {
				try {

					if (null != width && width > 0) { // 图片宽度大于0时应该对图片进行比例缩放

						/* if(ary[0]>width){ */
						// 缩放比 大图比例
						double dScale = Double.parseDouble(String.valueOf(width)) / ary[0];
						picAllInfo.setBigPicInfo(this.picTreatment(dScale, picUrl, width));
						// 缩放比 小图比例
						double xScale = Double.parseDouble(String.valueOf(width / 4)) / ary[0];
						picAllInfo.setSmallPicInfo(this.picTreatment(xScale, picUrl, width / 4));

						/*
						 * }else {
						 * 
						 * //如果图片宽度小于传入宽度，则大图 ：返回传入宽度，图片本身高度，原图片url MDataMap
						 * insertMap = new MDataMap();
						 * insertMap.put("pic_oldUrl", picUrl);
						 * insertMap.put("pic_newUrl", picUrl);
						 * insertMap.put("width", width.toString()); insertMap
						 * .put("height", String.valueOf(ary[1]));
						 * 
						 * DbUp.upTable("pc_picinfo").dataInsert(insertMap); //
						 * 插入
						 * 
						 * bigInfo.setPicUrl(picUrl); bigInfo.setHeight(ary[1]);
						 * bigInfo.setWidth(width);
						 * picAllInfo.setBigPicInfo(bigInfo);
						 * 
						 * //缩放比 小图比例 double xScale
						 * =Double.parseDouble(String.valueOf(width/4))/ ary[0];
						 * picAllInfo.setSmallPicInfo(this.picTreatment(xScale,
						 * picUrl, width/4)); }
						 */
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return new ArrayList<PicAllInfo>();
				}
			} else {
				List<MDataMap> picInfoMaps = DbUp.upTable("pc_picinfo").queryByWhere("pic_oldUrl", picUrl);
				for (int j = 0; j < picInfoMaps.size(); j++) {
					PicInfos picInfos = new PicInfos();

					if (picInfoMaps.get(j).get("width").equals(String.valueOf(width))) {
						// 大图
						picInfos.setWidth(Integer.parseInt(picInfoMaps.get(j).get("width")));
						picInfos.setHeight(Integer.parseInt(picInfoMaps.get(j).get("height")));
						picInfos.setPicUrl(picInfoMaps.get(j).get("pic_newUrl"));
						picAllInfo.setBigPicInfo(picInfos);

					} else if (picInfoMaps.get(j).get("width").equals(String.valueOf(width / 4))) {
						// 小图
						picInfos.setWidth(Integer.parseInt(picInfoMaps.get(j).get("width")));
						picInfos.setHeight(Integer.parseInt(picInfoMaps.get(j).get("height")));
						picInfos.setPicUrl(picInfoMaps.get(j).get("pic_newUrl"));
						picAllInfo.setSmallPicInfo(picInfos);
					}
				}
			}

			// 原图
			picInfo.setPicUrl(picUrl);
			picInfo.setWidth(ary[0]);
			picInfo.setHeight(ary[1]);
			picAllInfo.setPicInfo(picInfo);
			picList.add(picAllInfo);
		}

		return picList;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放 大图（宽：传入宽度缩放),小图（按传入宽度四分之一缩小）
	 * 
	 * @param width
	 *            宽度
	 * @param picUrl图片地址
	 *            多张图片中间用竖线分割
	 * @return 图片实体： 宽度，高度，url
	 */
	public List<PicAllInfo> getPicForProduct(Integer width, String picUrlsStr) {

		List<PicAllInfo> picList = new ArrayList<PicAllInfo>();
		if (null == picUrlsStr || "".equals(picUrlsStr)) {
			return picList;
		}

		String[] picUrls = picUrlsStr.split("\\|");
		PlusVeryImage pvi = new PlusVeryImage();
		Map<String, MFileItem> map;
		PicAllInfo picAllInfo;
		PicInfos picInfos = null;
		MFileItem item;
		for (String picUrl : picUrls) {
			if(StringUtils.isBlank(picUrl)) continue;
			picAllInfo = new PicAllInfo();
			picInfos = null;
			
			// 原图
			map = pvi.upImageZoom(picUrl, 0);
			if(!map.isEmpty()){
				item = map.values().iterator().next();
				
				picInfos = new PicInfos();
				picInfos.setWidth(item.getWidth());
				picInfos.setHeight(item.getHeight());
				picInfos.setPicUrl(item.getFileUrl());
				picAllInfo.setPicInfo(picInfos);
			}
			
			// 大图
			map = pvi.upImageZoom(picUrl, 800);
			if(!map.isEmpty()){
				item = map.values().iterator().next();
				
				picInfos = new PicInfos();
				picInfos.setWidth(item.getWidth());
				picInfos.setHeight(item.getHeight());
				picInfos.setPicUrl(item.getFileUrl());
				picAllInfo.setBigPicInfo(picInfos);
			}
			
			// 小图
			map = pvi.upImageZoom(picUrl, 200);
			if(!map.isEmpty()){
				item = map.values().iterator().next();
				
				picInfos = new PicInfos();
				picInfos.setWidth(item.getWidth());
				picInfos.setHeight(item.getHeight());
				picInfos.setPicUrl(item.getFileUrl());
				picAllInfo.setSmallPicInfo(picInfos);
			}
			
			// 确保对应尺寸的图片有值
			if(picInfos == null) picInfos = picAllInfo.getBigPicInfo();
			if(picInfos == null) picInfos = picAllInfo.getPicInfo();
			
			if(picInfos != null){
				if(StringUtils.isBlank(picAllInfo.getBigPicInfo().getPicUrl())){
					picAllInfo.setBigPicInfo(picInfos);
				}
				
				if(StringUtils.isBlank(picAllInfo.getPicInfo().getPicUrl())){
					picAllInfo.setPicInfo(picInfos);
				}
				
				if(StringUtils.isBlank(picAllInfo.getSmallPicInfo().getPicUrl())){
					picAllInfo.setSmallPicInfo(picInfos);
				}
				
				picList.add(picAllInfo);
			}
		}

		return picList;
	}

	/**
	 * @param 传入缩放比例
	 * @return 图片实体： 宽度，高度，url
	 */
	public PicInfos picTreatment(double dScale, String picUrl, Integer width) {

		PicInfo picInfo = new PicInfo();
		PicInfos picInfos = new PicInfos();
		// 如果是gif图片。则不进行压缩操作
		if ("gif".equals(this.getImgType(picUrl))) {
			picInfos.setPicUrl(picUrl);
			return picInfos;
		}
		ImageSupport imageSupport = new ImageSupport(picUrl);
		int[] ary = imageSupport.getWidthAndHeight();
		imageSupport.scale(dScale);
		String sTarget = "scale";
		String sDate = FormatHelper.upDateHex();

		String sFileName = WebHelper.upUuid();
		WebUpload webUpload = new WebUpload();
		MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
				imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

		if (mBigFile.upFlagTrue()) {

			picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
		}

		picInfo.setPicOldUrl(picUrl);
		picInfo.setWidth(width);

		Double dHeight = ary[1] * dScale; // 算出新图的高

		picInfo.setHeight(dHeight.intValue());

		MDataMap insertMap = new MDataMap();
		insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
		insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
		insertMap.put("width", String.valueOf(picInfo.getWidth()));
		insertMap.put("height", String.valueOf(picInfo.getHeight()));

		DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
		picInfos.setHeight(picInfo.getHeight());
		picInfos.setWidth(picInfo.getWidth());
		picInfos.setPicUrl(picInfo.getPicNewUrl());

		return picInfos;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放 （请注意：只能传入一张图片）
	 * 
	 * @param width
	 *            宽度
	 * @param picUrl
	 *            图片地址
	 * @return
	 * @author ligj
	 */
	public PicInfo getPicInfo(Integer width, String picUrl) {

		List<PicInfo> listPlusInfos = upImageInfo(width, picUrl);
		if (listPlusInfos.size() > 0) {
			return listPlusInfos.get(0);
		}

		PicInfo picInfo = new PicInfo();
		if (null == picUrl || "".equals(picUrl)) {
			return picInfo;
		}
		// 如果是gif图片。则不进行压缩操作
		if ("gif".equals(this.getImgType(picUrl))) {
			picInfo.setPicNewUrl(picUrl);
			picInfo.setPicOldUrl(picUrl);
			return picInfo;
		}
		MDataMap picInfoMap = DbUp.upTable("pc_picinfo").one("pic_oldUrl", picUrl, "width", String.valueOf(width));
		if (null == picInfoMap) {
			try {

				ImageSupport imageSupport = null;
				int[] ary = null;
				if (null != width && width > 0) {
					imageSupport = new ImageSupport(picUrl);// 图片宽度大于0时应该对图片进行比例缩放
					ary = imageSupport.getWidthAndHeight();
					// 缩放比
					double dScale = Double.parseDouble(String.valueOf(width)) / ary[0];
					imageSupport.scale(dScale);
					String sTarget = "scale";
					String sDate = FormatHelper.upDateHex();

					String sFileName = WebHelper.upUuid();
					WebUpload webUpload = new WebUpload();
					MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
							imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

					if (mBigFile.upFlagTrue()) {

						picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
					}

					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(width);

					Double dHeight = ary[1] * dScale; // 算出新图的高

					picInfo.setHeight(dHeight.intValue());
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));

					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
				} else {
					// 不传入宽度时，返回传入值
					picInfo.setPicOldUrl(picUrl);
					picInfo.setPicNewUrl(picUrl);
				}

			} catch (Exception ex) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);
				return picInfo;
			}
		} else {
			SerializeSupport<PicInfo> sSupport = new SerializeSupport<PicInfo>();
			sSupport.serialize(picInfoMap, picInfo);
		}
		return picInfo;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放 （请注意：只能传入一张图片,只会缩放比图片最大宽度大的图片）
	 * 
	 * @param maxWidth
	 *            图片最大宽度
	 * @param picUrl
	 *            图片地址
	 * @return
	 * @author ligj
	 */
	public PicInfo getPicInfoOprBig(Integer maxWidth, String picUrl) {

		List<PicInfo> listPlusInfos = upImageInfo(maxWidth, picUrl);
		if (listPlusInfos.size() > 0) {
			return listPlusInfos.get(0);
		}

		PicInfo picInfo = new PicInfo();
		if (null == picUrl || "".equals(picUrl) || null == maxWidth || maxWidth <= 0) {
			picInfo.setPicOldUrl(picUrl);
			picInfo.setPicNewUrl(picUrl);
			return picInfo;
		}
		if ("gif".equals(this.getImgType(picUrl))) {
			picInfo.setPicNewUrl(picUrl);
			picInfo.setPicOldUrl(picUrl);
			return picInfo;
		}
		String sFields = "pic_oldUrl,pic_newUrl,width,height,old_width,old_height";
		String sWhere = "pic_oldUrl='" + picUrl + "'";
		List<MDataMap> picInfoMapList = DbUp.upTable("pc_picinfo").queryAll(sFields, "", sWhere, null);
		for (MDataMap mDataMap : picInfoMapList) {
			String picOldUrl = mDataMap.get("pic_oldUrl");
			String picNewUrl = mDataMap.get("pic_newUrl");
			int width = StringUtils.isEmpty(mDataMap.get("width")) ? 0 : Integer.parseInt(mDataMap.get("width"));
			int height = StringUtils.isEmpty(mDataMap.get("height")) ? 0 : Integer.parseInt(mDataMap.get("height"));
			int oldWidth = StringUtils.isEmpty(mDataMap.get("old_width")) ? 0
					: Integer.parseInt(mDataMap.get("old_width"));
			int oldHeight = StringUtils.isEmpty(mDataMap.get("old_height")) ? 0
					: Integer.parseInt(mDataMap.get("old_height"));
			if (oldWidth > 0 && oldWidth <= maxWidth) {
				picInfo.setPicOldUrl(picOldUrl);
				picInfo.setPicNewUrl(picOldUrl);
				picInfo.setWidth(oldWidth);
				picInfo.setHeight(oldHeight);
				picInfo.setOldHeight(oldHeight);
				picInfo.setOldWidth(oldWidth);
				return picInfo;
			}
			if ((width > 0 && width == maxWidth)) {
				picInfo.setPicOldUrl(picOldUrl);
				picInfo.setPicNewUrl(picNewUrl);
				picInfo.setWidth(width);
				picInfo.setHeight(height);
				picInfo.setOldHeight(oldHeight);
				picInfo.setOldWidth(oldWidth);
				return picInfo;
			}
		}

		ImageSupport imageSupport = null;
		int[] ary = null;
		try {
			imageSupport = new ImageSupport(picUrl);
			ary = imageSupport.getWidthAndHeight();
			// 只操作宽度大于定义的最大宽度的图片
			picInfo.setOldWidth(ary[0]);
			picInfo.setOldHeight(ary[1]);
			if (maxWidth > ary[0]) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);
				picInfo.setWidth(ary[0]);
				picInfo.setHeight(ary[1]);

				MDataMap insertMap = new MDataMap();
				insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
				insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
				insertMap.put("width", String.valueOf(picInfo.getWidth()));
				insertMap.put("height", String.valueOf(picInfo.getHeight()));
				insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
				insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
				DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入

				return picInfo;
			}
		} catch (Exception e) {
			picInfo.setPicOldUrl(picUrl);
			picInfo.setPicNewUrl(picUrl);
			return picInfo;
		}
		try {
			// 缩放比
			double dScale = Double.parseDouble(String.valueOf(maxWidth)) / ary[0];
			imageSupport.scale(dScale);
			String sTarget = "scale";
			String sDate = FormatHelper.upDateHex();

			String sFileName = WebHelper.upUuid();
			WebUpload webUpload = new WebUpload();
			MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
					imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

			if (mBigFile.upFlagTrue()) {

				picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
				picInfo.setPicOldUrl(picUrl);
				picInfo.setWidth(maxWidth);
				Double dHeight = ary[1] * dScale; // 算出新图的高
				picInfo.setHeight(dHeight.intValue());
				picInfo.setOldWidth(ary[0]);
				picInfo.setOldHeight(ary[1]);

				MDataMap insertMap = new MDataMap();
				insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
				insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
				insertMap.put("width", String.valueOf(picInfo.getWidth()));
				insertMap.put("height", String.valueOf(picInfo.getHeight()));
				insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
				insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
				DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
			} else {
				picInfo.setPicNewUrl(picUrl);
				picInfo.setPicOldUrl(picUrl);
				picInfo.setWidth(ary[0]);
				picInfo.setHeight(ary[1]);
				picInfo.setOldWidth(ary[0]);
				picInfo.setOldHeight(ary[1]);
			}
		} catch (Exception ex) {
			picInfo.setPicOldUrl(picUrl);
			picInfo.setPicNewUrl(picUrl);
			return picInfo;
		}
		return picInfo;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放 （请注意：只会缩放比图片最大宽度大的图片）
	 * 
	 * @param maxWidth
	 *            图片最大宽度
	 * @param picUrl
	 *            图片地址
	 * @return
	 * @author ligj
	 */
	public List<PicInfo> getPicInfoOprBigForMulti(int maxWidth, List<String> picUrlArr) {

		List<PicInfo> listPlusInfos = upImageInfo(maxWidth, StringUtils.join(picUrlArr, "|"));
		if (listPlusInfos.size() > 0) {
			return listPlusInfos;
		}

		List<PicInfo> resultList = new ArrayList<PicInfo>();
		if (null == picUrlArr || picUrlArr.size() <= 0) {
			return resultList;
		}
		if (maxWidth <= 0) {
			for (String picUrl : picUrlArr) {
				PicInfo picInfo = new PicInfo();
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);
				resultList.add(picInfo);
			}
			return resultList;
		}

		String sFields = "pic_oldUrl,pic_newUrl,width,height,old_width,old_height";
		String sWhere = "pic_oldUrl in ('" + StringUtils.join(picUrlArr, "','") + "')";
		List<MDataMap> picInfoMapList = DbUp.upTable("pc_picinfo").queryAll(sFields, "", sWhere, null);
		for (String picUrl : picUrlArr) {
			PicInfo picInfo = new PicInfo();
			if ("gif".equals(this.getImgType(picUrl))) {
				picInfo.setPicNewUrl(picUrl);
				picInfo.setPicOldUrl(picUrl);
				resultList.add(picInfo);
				continue;
			}
			boolean isExist = false;
			for (MDataMap mDataMap : picInfoMapList) {
				String picOldUrl = mDataMap.get("pic_oldUrl");
				if (!picOldUrl.equals(picUrl)) {
					continue;
				}

				String picNewUrl = mDataMap.get("pic_newUrl");
				int width = StringUtils.isEmpty(mDataMap.get("width")) ? 0 : Integer.parseInt(mDataMap.get("width"));
				int height = StringUtils.isEmpty(mDataMap.get("height")) ? 0 : Integer.parseInt(mDataMap.get("height"));
				int oldWidth = StringUtils.isEmpty(mDataMap.get("old_width")) ? 0
						: Integer.parseInt(mDataMap.get("old_width"));
				int oldHeight = StringUtils.isEmpty(mDataMap.get("old_height")) ? 0
						: Integer.parseInt(mDataMap.get("old_height"));
				if ((oldWidth > 0 && oldWidth <= maxWidth) || (width > 0 && width == maxWidth)) {
					picInfo.setPicOldUrl(picOldUrl);
					picInfo.setPicNewUrl(picNewUrl);
					picInfo.setWidth(width);
					picInfo.setHeight(height);
					picInfo.setOldHeight(oldHeight);
					picInfo.setOldWidth(oldWidth);
					resultList.add(picInfo);
					isExist = true;
					break;
				}
			}
			if (isExist) {
				// 如果在数据库中查到符合条件的图片，则查询下张图片，进行下次循环
				continue;
			}
			ImageSupport imageSupport = null;
			int[] ary = null;
			try {
				imageSupport = new ImageSupport(picUrl);
				ary = imageSupport.getWidthAndHeight();
				// 只操作宽度大于定义的最大宽度的图片
				picInfo.setOldWidth(ary[0]);
				picInfo.setOldHeight(ary[1]);
				if (maxWidth > ary[0]) {
					picInfo.setPicOldUrl(picUrl);
					picInfo.setPicNewUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入

					resultList.add(picInfo);
					continue;
				}
			} catch (Exception e) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			try {
				// 缩放比
				double dScale = Double.parseDouble(String.valueOf(maxWidth)) / ary[0];
				imageSupport.scale(dScale);
				String sTarget = "scale";
				String sDate = FormatHelper.upDateHex();

				String sFileName = WebHelper.upUuid();
				WebUpload webUpload = new WebUpload();
				MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
						imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

				if (mBigFile.upFlagTrue()) {

					picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(maxWidth);
					Double dHeight = ary[1] * dScale; // 算出新图的高
					picInfo.setHeight(dHeight.intValue());
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
				} else {
					picInfo.setPicNewUrl(picUrl);
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);
				}

			} catch (Exception ex) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			resultList.add(picInfo);
		}
		return resultList;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放,
	 * 
	 * @param maxWidth
	 *            图片最大宽度
	 * @param picUrl
	 *            图片地址
	 * @param picType
	 *            图片格式
	 * @return
	 * @author ligj
	 */
	public List<PicInfo> getPicInfoOprBigForMulti(int maxWidth, List<String> picUrlArr, String picType) {
		return getPicInfoOprBigForMulti("",  maxWidth,  picUrlArr, picType);
	}
	
	/**
	 * 传入图片的宽高，按照宽高切割图片大小
	 * 
	 * @param maxWidth
	 * @param maxWeigh
	 * @param picUrlArr
	 * @return
	 * 2020年6月15日
	 * Angel Joy
	 * PicInfo
	 */
	public String getPicInfoByWH(int maxWidth, int maxHeigh,String picUrl,String productCode) {
		String key = productCode+"_"+maxWidth+"_"+maxHeigh;
		String v = XmasKv.upFactory(EKvSchema.WxSharePic).get(key);
		if(StringUtils.isEmpty(v)) {
			ImageCuterUtil imgUtil = new ImageCuterUtil();
			String newUrl = imgUtil.changeUrl(picUrl, maxWidth, maxHeigh);
			XmasKv.upFactory(EKvSchema.WxSharePic).set(key, newUrl);
			return newUrl;
		}
		return v;
	}
	
	/**
	 * 传入宽度对图片按照一定比例缩放,
	 * 
	 * @param maxWidth
	 *            图片最大宽度
	 * @param picUrl
	 *            图片地址
	 * @param picType
	 *            图片格式
	 * @return
	 * @author ligj
	 */
	public List<PicInfo> getPicInfoOprBigForMulti(String baseKey, int maxWidth, List<String> picUrlArr, String picType) {
		List<PicInfo> listPlusInfos = upImageInfoList(baseKey, maxWidth, picUrlArr, picType);
		if (listPlusInfos.size() > 0) {
			return listPlusInfos;
		}

		List<PicInfo> resultList = new ArrayList<PicInfo>();
		if (null == picUrlArr || picUrlArr.size() <= 0) {
			return resultList;
		}
		if (maxWidth <= 0) {
			for (String picUrl : picUrlArr) {
				PicInfo picInfo = new PicInfo();
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);
				resultList.add(picInfo);
			}
			return resultList;
		}

		String sFields = "pic_oldUrl,pic_newUrl,width,height,old_width,old_height";
		String sWhere = "pic_oldUrl in ('" + StringUtils.join(picUrlArr, "','") + "')";
		List<MDataMap> picInfoMapList = DbUp.upTable("pc_picinfo").queryAll(sFields, "", sWhere, null);
		for (String picUrl : picUrlArr) {
			PicInfo picInfo = new PicInfo();
			if ("gif".equals(this.getImgType(picUrl))) {
				picInfo.setPicNewUrl(picUrl);
				picInfo.setPicOldUrl(picUrl);
				resultList.add(picInfo);
				continue;
			}
			boolean isExist = false;
			for (MDataMap mDataMap : picInfoMapList) {
				String picOldUrl = mDataMap.get("pic_oldUrl");
				if (!picOldUrl.equals(picUrl)) {
					continue;
				}

				String picNewUrl = mDataMap.get("pic_newUrl");
				int width = StringUtils.isEmpty(mDataMap.get("width")) ? 0 : Integer.parseInt(mDataMap.get("width"));
				int height = StringUtils.isEmpty(mDataMap.get("height")) ? 0 : Integer.parseInt(mDataMap.get("height"));
				int oldWidth = StringUtils.isEmpty(mDataMap.get("old_width")) ? 0
						: Integer.parseInt(mDataMap.get("old_width"));
				int oldHeight = StringUtils.isEmpty(mDataMap.get("old_height")) ? 0
						: Integer.parseInt(mDataMap.get("old_height"));
				if ((oldWidth > 0 && oldWidth <= maxWidth) || (width > 0 && width == maxWidth)) {
					picInfo.setPicOldUrl(picOldUrl);
					picInfo.setPicNewUrl(picNewUrl);
					picInfo.setWidth(width);
					picInfo.setHeight(height);
					picInfo.setOldHeight(oldHeight);
					picInfo.setOldWidth(oldWidth);
					resultList.add(picInfo);
					isExist = true;
					break;
				}
			}
			if (isExist) {
				// 如果在数据库中查到符合条件的图片，则查询下张图片，进行下次循环
				continue;
			}
			ImageSupport imageSupport = null;
			int[] ary = null;
			try {
				imageSupport = new ImageSupport(picUrl);
				ary = imageSupport.getWidthAndHeight();
				// 只操作宽度大于定义的最大宽度的图片
				picInfo.setOldWidth(ary[0]);
				picInfo.setOldHeight(ary[1]);
				if (maxWidth > ary[0]) {
					picInfo.setPicOldUrl(picUrl);
					picInfo.setPicNewUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入

					resultList.add(picInfo);
					continue;
				}
			} catch (Exception e) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			try {
				// 缩放比
				double dScale = Double.parseDouble(String.valueOf(maxWidth)) / ary[0];
				imageSupport.scale(dScale);
				String sTarget = "scale";
				String sDate = FormatHelper.upDateHex();

				String sFileName = WebHelper.upUuid();
				WebUpload webUpload = new WebUpload();
				MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
						imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

				if (mBigFile.upFlagTrue()) {

					picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(maxWidth);
					Double dHeight = ary[1] * dScale; // 算出新图的高
					picInfo.setHeight(dHeight.intValue());
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
				} else {
					picInfo.setPicNewUrl(picUrl);
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);
				}

			} catch (Exception ex) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			resultList.add(picInfo);
		}
		return resultList;
	}

	/**
	 * 传入宽度对图片按照一定比例缩放
	 * 
	 * @param newWidth
	 *            图片宽度
	 * @param picUrl
	 *            图片地址
	 * @return
	 * @author ligj
	 */
	public List<PicInfo> getPicInfoForMulti(int newWidth, List<String> picUrlArr) {

		List<PicInfo> listPlusInfos = upImageInfoList("", newWidth, picUrlArr, "");
		if (listPlusInfos.size() > 0) {
			return listPlusInfos;
		}

		List<PicInfo> resultList = new ArrayList<PicInfo>();
		if (null == picUrlArr || picUrlArr.size() <= 0) {
			return resultList;
		}
		if (newWidth <= 0) {
			for (String picUrl : picUrlArr) {
				PicInfo picInfo = new PicInfo();
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);
				resultList.add(picInfo);
			}
			return resultList;
		}
		String sFields = "pic_oldUrl,pic_newUrl,width,height,old_width,old_height";
		String sWhere = "pic_oldUrl in ('" + StringUtils.join(picUrlArr, "','") + "')";
		List<MDataMap> picInfoMapList = DbUp.upTable("pc_picinfo").queryAll(sFields, "", sWhere, null);
		for (String picUrl : picUrlArr) {
			PicInfo picInfo = new PicInfo();
			if ("gif".equals(this.getImgType(picUrl))) {
				picInfo.setPicNewUrl(picUrl);
				picInfo.setPicOldUrl(picUrl);
				resultList.add(picInfo);
				continue;
			}
			boolean isExist = false;
			for (MDataMap mDataMap : picInfoMapList) {
				String picOldUrl = mDataMap.get("pic_oldUrl");
				if (!picOldUrl.equals(picUrl)) {
					continue;
				}

				String picNewUrl = mDataMap.get("pic_newUrl");
				int width = StringUtils.isEmpty(mDataMap.get("width")) ? 0 : Integer.parseInt(mDataMap.get("width"));
				int height = StringUtils.isEmpty(mDataMap.get("height")) ? 0 : Integer.parseInt(mDataMap.get("height"));
				int oldWidth = StringUtils.isEmpty(mDataMap.get("old_width")) ? 0
						: Integer.parseInt(mDataMap.get("old_width"));
				int oldHeight = StringUtils.isEmpty(mDataMap.get("old_height")) ? 0
						: Integer.parseInt(mDataMap.get("old_height"));
				if (width == newWidth) {
					picInfo.setPicOldUrl(picOldUrl);
					picInfo.setPicNewUrl(picNewUrl);
					picInfo.setWidth(width);
					picInfo.setHeight(height);
					picInfo.setOldHeight(oldHeight);
					picInfo.setOldWidth(oldWidth);
					resultList.add(picInfo);
					isExist = true;
					break;
				}
			}
			if (isExist) {
				// 如果在数据库中查到符合条件的图片，则查询下张图片，进行下次循环
				continue;
			}
			ImageSupport imageSupport = null;
			int[] ary = null;
			try {
				imageSupport = new ImageSupport(picUrl);
				ary = imageSupport.getWidthAndHeight();
				// 只操作宽度大于定义的最大宽度的图片
				picInfo.setOldWidth(ary[0]);
				picInfo.setOldHeight(ary[1]);
				if (newWidth == ary[0]) {
					picInfo.setPicOldUrl(picUrl);
					picInfo.setPicNewUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入

					resultList.add(picInfo);
					continue;
				}
			} catch (Exception e) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			try {
				// 缩放比
				double dScale = Double.parseDouble(String.valueOf(newWidth)) / ary[0];
				imageSupport.scale(dScale);
				String sTarget = "scale";
				String sDate = FormatHelper.upDateHex();

				String sFileName = WebHelper.upUuid();
				WebUpload webUpload = new WebUpload();
				MWebResult mBigFile = webUpload.remoteUploadCustom(StringUtils.substringAfterLast(picUrl, "/"),
						imageSupport.upTargetByte(), sTarget, sDate, "p0", sFileName);

				if (mBigFile.upFlagTrue()) {

					picInfo.setPicNewUrl(mBigFile.getResultObject().toString());
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(newWidth);
					Double dHeight = ary[1] * dScale; // 算出新图的高
					picInfo.setHeight(dHeight.intValue());
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);

					MDataMap insertMap = new MDataMap();
					insertMap.put("pic_oldUrl", picInfo.getPicOldUrl());
					insertMap.put("pic_newUrl", picInfo.getPicNewUrl());
					insertMap.put("width", String.valueOf(picInfo.getWidth()));
					insertMap.put("height", String.valueOf(picInfo.getHeight()));
					insertMap.put("old_width", String.valueOf(picInfo.getOldWidth()));
					insertMap.put("old_height", String.valueOf(picInfo.getOldHeight()));
					DbUp.upTable("pc_picinfo").dataInsert(insertMap); // 插入
				} else {
					picInfo.setPicNewUrl(picUrl);
					picInfo.setPicOldUrl(picUrl);
					picInfo.setWidth(ary[0]);
					picInfo.setHeight(ary[1]);
					picInfo.setOldWidth(ary[0]);
					picInfo.setOldHeight(ary[1]);
				}
			} catch (Exception ex) {
				picInfo.setPicOldUrl(picUrl);
				picInfo.setPicNewUrl(picUrl);

				resultList.add(picInfo);
				continue;
			}
			resultList.add(picInfo);
		}
		return resultList;
	}

	/**
	 * @see ProductService#upImageInfoList(String, int, List, String)
	 */
	@Deprecated
	public List<PicInfo> upImageInfo(int iWidth, String sUrls) {
		return upImageInfoList( "", iWidth, Arrays.asList(StringUtils.split(sUrls, "|")),  "");
//		Map<String, MFileItem> maps = new PlusVeryImage()
//				.upImageZoom(StringUtils.join(StringUtils.split(sUrls, "|"), ",").replace(" ", ""), iWidth);
//
//		List<PicInfo> listResult = new ArrayList<PicInfo>();
//
//		if (maps != null && maps.size() > 0) {
//			for (String sKey : maps.keySet()) {
//
//				MFileItem mFileItem = maps.get(sKey);
//
//				PicInfo picInfo = new PicInfo();
//
//				picInfo.setHeight(mFileItem.getHeight());
//				picInfo.setOldHeight(0);
//				picInfo.setOldWidth(0);
//				picInfo.setPicNewUrl(mFileItem.getFileUrl());
//				picInfo.setPicOldUrl(sKey);
//				picInfo.setWidth(iWidth);
//
//				listResult.add(picInfo);
//			}
//
//		}
//
//		return listResult;

	}

	/**
	 * @see ProductService#upImageInfoList(String, int, List, String)
	 */
	@Deprecated
	public List<PicInfo> upImageInfo(int iWidth, String sUrls, String picType) {
		return upImageInfoList( "", iWidth, Arrays.asList(StringUtils.split(sUrls, "|")),  picType);
	}
	
	public List<PicInfo> upImageInfoList(String baseKey, int iWidth, List<String> sUrls, String picType) {
		//Map<String, MFileItem> maps = new PlusVeryImage().upImageZoom(baseKey ,StringUtils.join(StringUtils.split(sUrls, "|"), ",").replace(" ", ""), iWidth, picType);
		Map<String, MFileItem> maps = new PlusVeryImage().upImageZoomList(baseKey ,sUrls, iWidth, picType);

		List<PicInfo> listResult = new ArrayList<PicInfo>();

		if (maps != null && maps.size() > 0) {
			for (String sKey : maps.keySet()) {

				MFileItem mFileItem = maps.get(sKey);

				PicInfo picInfo = new PicInfo();

				picInfo.setHeight(mFileItem.getHeight());
				picInfo.setOldHeight(0);
				picInfo.setOldWidth(0);
				picInfo.setPicNewUrl(mFileItem.getFileUrl());
				picInfo.setPicOldUrl(sKey);
				picInfo.setWidth(iWidth);
				picInfo.setOriginWidth(mFileItem.getOriginWidth());
				picInfo.setOriginHeight(mFileItem.getOriginHeight());

				listResult.add(picInfo);
			}

		}

		return listResult;

	}

	/**
	 * 根据传入的宽度和图片地址，返回新图的高度
	 * 
	 * @param picUrl
	 * @param width
	 * @return
	 */
	public PicInfo getPicHeight(String picUrl, int width) {
		if(StringUtils.isBlank(picUrl)) return new PicInfo();
		
		ImageSupport imageSupport = new ImageSupport(picUrl);
		int[] ary = imageSupport.getWidthAndHeight();
		int height = width * ary[1] / ary[0];
		PicInfo picInfo = new PicInfo();
		picInfo.setHeight(height);
		return picInfo;
	}

	/**
	 * skuCode列表获取总金额
	 * 
	 * @param skuInfo
	 *            包含skuCode与count（必须非空）
	 * @return
	 */
	public Map<String, Object> getSkuTotalManey(List<Map<String, Object>> skuInfo) {

		Map<String, Object> resultMap = new HashMap<String, Object>(); // 返回的map，包含商品总金额与商品列表信息
		List<Map<String, Object>> skuNewInfoList = new ArrayList<Map<String, Object>>(); // 商品列表信息
		BigDecimal totalMoney = new BigDecimal(0.00); // 总金额
		for (Map<String, Object> skuMap : skuInfo) {
			Map<String, Object> skuNewInfo = new HashMap<String, Object>();
			String skuCode = String.valueOf(skuMap.get("skuCode"));
			int count = Integer.valueOf(String.valueOf(skuMap.get("count")));
			// 根据skuCode查询活动信息
			List<FlashsalesSkuInfo> activitySku = this.getFlashsalesForSkuCodeAll(skuCode,
					String.valueOf(skuMap.get("appCode")));
			int isActivitySku = 0; // 是否闪购活动商品 0 否；1是
			BigDecimal sellPrice = new BigDecimal(0.00); // 商品售价
			if (activitySku.size() > 0
					&& activitySku.get(0).getEndTime().compareTo(DateUtil.getSysDateTimeString()) > 0) {
				isActivitySku = 1;
				sellPrice = activitySku.get(0).getVipPrice(); // 闪购活动价为售价
			} else {
				MDataMap skuPrice = DbUp.upTable("pc_skuinfo").oneWhere("sell_price", "", "", "sku_code", skuCode);
				sellPrice = BigDecimal
						.valueOf(Double.valueOf(skuPrice.get("sell_price") == null ? "0" : skuPrice.get("sell_price")));
			}
			totalMoney = totalMoney.add(sellPrice.multiply(BigDecimal.valueOf(count)));
			skuNewInfo.put("skuCode", skuCode);
			skuNewInfo.put("isActivitySku", isActivitySku);
			skuNewInfoList.add(skuNewInfo);
		}
		resultMap.put("skuList", skuNewInfoList);
		resultMap.put("totalMoney", totalMoney);
		return resultMap;
	}

	/**
	 * 计算返现金额,如果未登录，则userCode传入null即可
	 * 
	 * @param price
	 * @param userCode
	 * @param sellerCode
	 * @return
	 */
	public BigDecimal getDisMoney(BigDecimal price, String userCode, String sellerCode) {
		if (null == price || price.compareTo(new BigDecimal(0)) <= 0) {
			return new BigDecimal(0);
		}
		if (null != userCode && !"".equals(userCode)) {
			try {
				// 根据member_code获取account_code
				MDataMap accountCodeMap = DbUp.upTable("mc_member_info").oneWhere("account_code", "", "", "member_code",
						userCode, "manage_code", sellerCode);

				// 根据account_code获取清分比例
				MDataMap scaleReckonMap = DbUp.upTable("gc_group_account").oneWhere("scale_reckon", "", "",
						"account_code", accountCodeMap.get("account_code"));

				double scaleReckon = Double.parseDouble(scaleReckonMap.get("scale_reckon"));

				// 清分比例不足5%的设置为5%
				return (price.multiply(BigDecimal.valueOf(scaleReckon > 0.05 ? scaleReckon : 0.05))); // 返现金额
			} catch (Exception e) {
				// 清分比例不足5%的设置为5%
				return (price.multiply(BigDecimal.valueOf(0.05))); // 返现金额
			}
		} else {
			// 未登录的折扣为5%
			return (price.multiply(BigDecimal.valueOf(0.05)));// 返现金额，暂时默认是现价的5%
		}
	}

	/**
	 * 返回用户的返现率，不足5%或未登录按5%算
	 * 
	 * @param userCode
	 * @param sellerCode
	 * @return
	 */
	public BigDecimal getDisCount(String userCode, String sellerCode) {
		if (null != userCode && !"".equals(userCode)) {
			try {
				// 根据member_code获取account_code
				MDataMap accountCodeMap = DbUp.upTable("mc_member_info").oneWhere("account_code", "", "", "member_code",
						userCode, "manage_code", sellerCode);

				// 根据account_code获取清分比例
				MDataMap scaleReckonMap = DbUp.upTable("gc_group_account").oneWhere("scale_reckon", "", "",
						"account_code", accountCodeMap.get("account_code"));

				double scaleReckon = Double.parseDouble(scaleReckonMap.get("scale_reckon"));

				// 清分比例不足5%的设置为5%
				return (BigDecimal.valueOf(scaleReckon > 0.05 ? scaleReckon : 0.05)); // 返现金额
			} catch (Exception e) {
				// 清分比例不足5%的设置为5%
				return (BigDecimal.valueOf(0.05)); // 返现金额
			}
		} else {
			// 未登录的折扣为5%
			return (BigDecimal.valueOf(0.05));// 返现金额，暂时默认是现价的5%
		}
	}

	/**
	 * 获取到所有正在进行闪购活动的商品codes与闪购剩余件数
	 * 
	 * @param appCode
	 * @return
	 * @author ligj
	 */
	public Map<String, Integer> getSkuCodesFlashActivity(String appCode) {
		// 获取当前生效的活动编号列表
		StringBuffer sSqlBuffer = new StringBuffer();
		sSqlBuffer.append(
				" SELECT fs.sku_code as sku_code, (fs.sales_num - (CASE WHEN sellInfo.skuNum is null THEN '0' ELSE sellInfo.skuNum END)) as surplusNum FROM oc_flashsales_skuInfo fs   ");
		sSqlBuffer.append(" LEFT JOIN ( ");
		sSqlBuffer.append(
				" SELECT b.activity_code as activity_code,b.sku_code as sku_code,SUM(b.sku_num) as skuNum FROM ( 	 ");
		sSqlBuffer.append(" SELECT a.order_code,a.activity_code,d.sku_code,d.sku_num from (  ");
		sSqlBuffer.append(" SELECT order_code,activity_code from oc_order_activity where order_code in ( ");
		sSqlBuffer.append(
				" SELECT order_code from oc_orderinfo where order_status <>'4497153900010006') and LEFT(activity_code,2)='SG'  ");
		sSqlBuffer.append(" AND activity_code in (	 ");
		sSqlBuffer
				.append(" SELECT activity_code from oc_activity_flashsales where start_time <= now() and  end_time >= now() and status='449746740002' AND app_code = '"
						+ appCode + "' ");
		sSqlBuffer.append(" ) ) a  ");
		sSqlBuffer.append(
				" LEFT JOIN oc_orderdetail d ON a.order_code=d.order_code) b GROUP BY b.activity_code,b.sku_code ");
		sSqlBuffer.append(
				"  ) sellInfo ON fs.activity_code = sellInfo.activity_code and fs.sku_code = sellInfo.sku_code ");
		sSqlBuffer.append(" LEFT JOIN oc_activity_flashsales oa on oa.activity_code = fs.activity_code ");
		sSqlBuffer.append(" WHERE oa.start_time <= now() AND  oa.end_time >= now() AND oa.status='449746740002' ");
		sSqlBuffer.append(" AND fs.status='449746810001' ");
		sSqlBuffer.append(" AND oa.app_code = '" + appCode + "' ");
		List<Map<String, Object>> skuCodesMap = DbUp.upTable("oc_activity_flashsales")
				.dataSqlList(sSqlBuffer.toString(), null);
		Map<String, Integer> skuCodesSurplus = new HashMap<String, Integer>();

		String skuCodesStr = ""; // 查询库存用
		StringBuffer skuCodesBuff = new StringBuffer();
		for (Map<String, Object> map : skuCodesMap) {
			skuCodesBuff.append(String.valueOf(map.get("sku_code")));
			skuCodesBuff.append("','");
		}
		if (StringUtils.isNotEmpty(String.valueOf(skuCodesBuff))) {
			skuCodesStr = "'" + String.valueOf(skuCodesBuff).substring(0, String.valueOf(skuCodesBuff).length() - 2);
		}

		StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		Map<String, Integer> totalStockMap = storeService.getStockNumByStoreMulti(skuCodesStr); // 剩余总库存

		for (Map<String, Object> map : skuCodesMap) {

			int surplusNum = 0;

			if (null == totalStockMap.get(String.valueOf(map.get("sku_code")))) { // sku不存在于库存表中时取得是活动剩余件数
				surplusNum = Double.valueOf(String.valueOf(map.get("surplusNum"))).intValue();
			} else {
				// 实际库存数小于活动剩余件数时取实际库存数
				if (Double.valueOf(String.valueOf(map.get("surplusNum"))).intValue() > totalStockMap
						.get(String.valueOf(map.get("sku_code")))) {
					surplusNum = totalStockMap.get(String.valueOf(map.get("sku_code")));
				} else {
					surplusNum = Double.valueOf(String.valueOf(map.get("surplusNum"))).intValue();
				}
			}

			if (surplusNum != 0) { // 只取剩余件数大于0的
				skuCodesSurplus.put(String.valueOf(map.get("sku_code")), surplusNum);
			}
		}
		return skuCodesSurplus;
	}

	/**
	 * 获取到所有正在进行试用活动的商品codes与试用剩余件数
	 * 
	 * @param appCode
	 * @return
	 * @author ligj
	 */
	public Map<String, Integer> getSkuCodesTryoutActivity(String appCode) {

		String sWhere = " app_code='" + appCode
				+ "'  and start_time < now() and end_time > now() and tryout_inventory > 0";
		List<MDataMap> skuCodesMap = DbUp.upTable("oc_tryout_products").queryAll("sku_code,tryout_inventory", "",
				sWhere, null);
		Map<String, Integer> skuCodesSurplus = new HashMap<String, Integer>();

		String skuCodesStr = ""; // 查询库存用
		StringBuffer skuCodesBuff = new StringBuffer();
		for (MDataMap mDataMap : skuCodesMap) {
			skuCodesBuff.append(mDataMap.get("sku_code"));
			skuCodesBuff.append("','");
		}
		if (StringUtils.isNotEmpty(String.valueOf(skuCodesBuff))) {
			skuCodesStr = "'" + String.valueOf(skuCodesBuff).substring(0, String.valueOf(skuCodesBuff).length() - 2);
		}

		StoreService storeService = BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		Map<String, Integer> totalStockMap = storeService.getStockNumByStoreMulti(skuCodesStr); // 剩余总库存
		for (MDataMap map : skuCodesMap) {
			int surplusNum = 0;
			if (null == totalStockMap.get(String.valueOf(map.get("sku_code")))) { // sku不存在于库存表中时取得是活动剩余件数
				surplusNum = Double.valueOf(String.valueOf(map.get("tryout_inventory"))).intValue();
			} else {
				// 实际库存数小于活动剩余件数时取实际库存数
				if (Double.valueOf(String.valueOf(map.get("tryout_inventory"))).intValue() > totalStockMap
						.get(String.valueOf(map.get("sku_code")))) {
					surplusNum = totalStockMap.get(String.valueOf(map.get("sku_code")));
				} else {
					surplusNum = Double.valueOf(String.valueOf(map.get("tryout_inventory"))).intValue();
				}
			}
			if (surplusNum > 0) { // 只取剩余件数大于0的
				skuCodesSurplus.put(String.valueOf(map.get("sku_code")), surplusNum);
			}
		}
		return skuCodesSurplus;
	}

	/**
	 * 统计商品的销量 按照商品group by统计
	 * 
	 * @return 商品购买数
	 */
	public Map<String, Integer> getSkuSellCountForGroup(String appCode, String skuCodes) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		String skuCodesWhere = "";

		if (StringUtils.isNotEmpty(skuCodes)) {
			skuCodesWhere = " and od.sku_code in ('" + skuCodes + "') ";
		}
		// 购买数 .订单状态不是未付款和交易失败的。
		String skuNumSql = "select SUM(od.sku_num) as skuNum ,od.sku_code as sku_code from oc_orderinfo oi,oc_orderdetail od"
				+ " where od.order_code = oi.order_code  " + "   and oi.seller_code = '" + appCode + "' "
				+ skuCodesWhere + " group by od.sku_code";
		List<Map<String, Object>> sellCountMapList = DbUp.upTable("oc_orderdetail").dataSqlList(skuNumSql, null);

		for (Map<String, Object> sellCountMap : sellCountMapList) {
			int sellCount = (sellCountMap == null || sellCountMap.get("skuNum") == null
					|| "".equals(sellCountMap.get("skuNum"))) ? 0
							: Integer.parseInt(String.valueOf(sellCountMap.get("skuNum")));
			result.put(sellCountMap.get("sku_code").toString(), sellCount);
		}
		return result;
	}

	/**
	 * 查询商品活动类型（实时）
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return 0：普通商品 1：限购商品 2：试用商品
	 */
	public int getSkuActivityType(String skuCode, String appCode) {
		int activityType = 0;
		// 判断是不是闪购
		String flashsalesWhere = " sku_code='" + skuCode + "' and status='449746810001'";
		List<MDataMap> flashsalesList = DbUp.upTable("oc_flashsales_skuInfo").queryAll("activity_code", "",
				flashsalesWhere, null); // 闪购商品信息
		String activityCodesStr = "";
		for (MDataMap mDataMap : flashsalesList) {
			activityCodesStr += (mDataMap.get("activity_code") + "','");
		}
		String activityWhere = "activity_code in ('" + activityCodesStr
				+ "') and start_time <= now() and end_time > now() and status = '449746740002'";
		// 获取当前生效的活动编号列表
		List<MDataMap> activityObj = DbUp.upTable("oc_activity_flashsales").queryAll("activity_code", "", activityWhere,
				null);
		if (activityObj != null && activityObj.size() > 0) { // 闪购类型
			int surplus = this.salesNumSurplus(skuCode, activityObj.get(0).get("activity_code"));
			if (surplus > 0) { // 剩余闪购促销库存数
				activityType = 1;
			}
		}

		// 判断是不是试用商品
		String tWhere = " start_time <= now() and end_time > now() and app_code = '" + appCode + "' and sku_code = '"
				+ skuCode + "' ";
		MDataMap tryoutObj = DbUp.upTable("oc_tryout_products").oneWhere("tryout_inventory", "", tWhere);
		if (tryoutObj != null && !tryoutObj.isEmpty()) {
			String tryoutCount = tryoutObj.get("tryout_inventory").toString(); // 试用库存
			if (Integer.parseInt(tryoutCount == null || "".equals(tryoutCount) ? "0" : tryoutCount) > 0) {
				activityType = 2;
			}
		}
		return activityType;
	}

	/**
	 * 查询商品活动类型（订单）
	 * 
	 * @param skuCode
	 * @param orderCode
	 * @return 0：普通商品 1：限购商品 2：试用商品
	 */
	public int getSkuActivityTypeForOrder(String skuCode, String orderCode) {
		int activityType = 0;

		String activityWhere = "order_code = '" + orderCode + "' and sku_code = '" + skuCode + "' ";

		MDataMap activityMap = DbUp.upTable("oc_order_activity").oneWhere("activity_type", "", activityWhere);
		if (null != activityMap && !activityMap.isEmpty()) {
			if ("449715400005".equals(activityMap.get("activity_type"))) { // 商品试用
				activityType = 2;
			} else if ("449715400004".equals(activityMap.get("activity_type"))) { // 限购
				activityType = 1;
			}
		}
		return activityType;
	}

	/**
	 * 查询商品活动类型（订单)(惠美丽) 0：普通商品 1：限购商品 2：试用商品
	 */
	public int getSkuActivityTypeForOrderHuiMeiLi(String skuCode, String orderCode) {
		int activityType = 0;

		String activityWhere = "order_code = '" + orderCode + "' and sku_code = '" + skuCode + "' ";

		MDataMap activityMap = DbUp.upTable("oc_order_activity").oneWhere("activity_code", "", activityWhere);

		if (null != activityMap && !activityMap.isEmpty()) {
			if (!"".equals(activityMap.get("activity_code")) && activityMap.get("activity_code") != null
					&& "TA".equals(activityMap.get("activity_code").substring(0, 2))) { // 商品试用
				activityType = 2;
			} else if (!"".equals(activityMap.get("activity_code")) && activityMap.get("activity_code") != null
					&& "SG".equals(activityMap.get("activity_code").substring(0, 2))) { // 限购
				activityType = 1;
			}
		}

		return activityType;
	}

	/**
	 * 统计商品的销量
	 * 
	 * @return 商品购买数
	 */
	public int getSkuSellCount(String skuCode) {
		// 购买数 .订单状态不是未付款和交易失败的。
		String skuNumSql = "select SUM(od.sku_num) as skuNum ,od.sku_code from oc_orderinfo oi,oc_orderdetail od"
				+ " where od.order_code = oi.order_code  and oi.order_status != '4497153900010001' and oi.order_status != '4497153900010006' "
				+ "   and od.sku_code = '" + skuCode + "' group by od.sku_code";
		Map<String, Object> sellCountMap = DbUp.upTable("oc_orderdetail").dataSqlOne(skuNumSql, null);

		int sellCount = (sellCountMap == null || sellCountMap.get("skuNum") == null
				|| "".equals(sellCountMap.get("skuNum"))) ? 0
						: Integer.parseInt(String.valueOf(sellCountMap.get("skuNum")));
		return sellCount;
	}

	/**
	 * 统计商品的销量
	 * 
	 * @return 商品购买数-下单数
	 */
	public int getSellCount(String skuCode) {
		// 购买数 .订单状态不是未付款和交易失败的。
		String skuNumSql = "select SUM(od.sku_num) as skuNum ,od.sku_code from oc_orderinfo oi,oc_orderdetail od"
				+ " where od.order_code = oi.order_code   " + "   and od.sku_code = '" + skuCode
				+ "' group by od.sku_code";
		Map<String, Object> sellCountMap = DbUp.upTable("oc_orderdetail").dataSqlOne(skuNumSql, null);

		int sellCount = (sellCountMap == null || sellCountMap.get("skuNum") == null
				|| "".equals(sellCountMap.get("skuNum"))) ? 0
						: Integer.parseInt(String.valueOf(sellCountMap.get("skuNum")));
		return sellCount;
	}

	/**
	 * 统计sku近30天的月销量
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return
	 */
	public int getSkuMonthSellCount(String skuCode, String appCode) {
		MDataMap orderWhere = new MDataMap();
		orderWhere.put("sku_code", skuCode);
		orderWhere.put("seller_code", appCode);
		String sqlSellNum = "select SUM(od.sku_num) as skuSellNum from oc_orderdetail od where od.sku_code=:sku_code and od.order_code in ("
				+ " select oi.order_code from oc_orderinfo oi where oi.seller_code=:seller_code  "
				+ "and oi.create_time >= date_sub(curdate(), INTERVAL 30 DAY)) group by od.sku_code ";
		Map<String, Object> mapObj = DbUp.upTable("oc_orderdetail").dataSqlOne(sqlSellNum, orderWhere);
		return Integer.valueOf(
				(mapObj == null || mapObj.get("skuSellNum") == null) ? "0" : mapObj.get("skuSellNum").toString());
	}

	/***
	 * 
	 * 获取剩余的闪购促销库存
	 * 
	 * @param skuCode
	 * @param activity_code
	 * @return
	 * @author jl
	 */
	public int salesNumSurplus(String skuCode, String activityCode) {
		BigDecimal usedNmu = new BigDecimal(0);
		// 查询成功订单参与当前闪购的商品数量
		String csql = "SELECT SUM(sku_num) as sm from oc_orderdetail where order_code in (SELECT order_code from oc_order_activity where order_code in (SELECT order_code from oc_orderinfo where order_status <>'4497153900010006') AND sku_code=:skuCode and activity_code=:activityCode) and  sku_code=:skuCode  ";
		List<Map<String, Object>> list = DbUp.upTable("oc_order_activity").dataSqlList(csql,
				new MDataMap("activityCode", activityCode, "skuCode", skuCode));
		if (list != null && list.size() > 0) {
			if (list.get(0).get("sm") != null) {
				usedNmu = (BigDecimal) list.get(0).get("sm");
			}
		}

		BigDecimal sales_num = new BigDecimal(0);
		try {
			sales_num = (BigDecimal) DbUp.upTable("oc_flashsales_skuInfo").dataGet("sales_num",
					"activity_code=:activity_code and sku_code=:sku_code and status=:status ",
					new MDataMap("activity_code", activityCode, "sku_code", skuCode, "status", "449746810001"));
			if (null == sales_num || sales_num.compareTo(BigDecimal.ZERO) == 0) {
				sales_num = usedNmu;
			}
		} catch (Exception e) {
			sales_num = usedNmu;
		}

		return Integer.valueOf(String.valueOf(sales_num.subtract(usedNmu)));
	}

	/**
	 * 获取商品近30天，上月销量，以及总销量
	 * 
	 * @param seller_code
	 * @param product_code
	 * @return Map里面的值 product_code:商品编码; thirty_day:近三十天销量; last_month:上月销量;
	 *         total_all:总销量;
	 */
	public Map<String, Map<String, String>> getProductSales(String seller_code, String product_code) {
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		String whereSql = " 1=1 ";
		if (StringUtils.isNotEmpty(seller_code)) {
			whereSql += " and seller_code = '" + seller_code + "' ";
		}
		if (StringUtils.isNotEmpty(product_code)) {
			whereSql += " and product_code = '" + product_code + "' ";
		}
		List<MDataMap> mapList = DbUp.upTable("oc_product_salesCount").queryAll("", "", whereSql, null);
		for (MDataMap mDataMap : mapList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("thirty_day", mDataMap.get("thirty_day"));
			map.put("last_month", mDataMap.get("last_month"));
			map.put("total_all", mDataMap.get("total_all"));
			resultMap.put(mDataMap.get("product_code"), map);
		}
		return resultMap;
	}

	/**
	 * 获取商品最近的虚拟销售量(当productCodeList不为空时sellerCode无用)
	 * 
	 * @param sellerCode
	 *            商家编码 (可以为空)暂时只支持惠家有，请传入SI2003（2015-08-13开始可以支持沙皮狗项目传入SI3003）
	 * @param productCodeList
	 *            商品编码List(为空时查询所有商品)
	 * @param day
	 *            获取近day天的商品销量，必须为正整数
	 * @return MDataMap key:product_code(商品编码),value:sales(虚拟销量)
	 */
	public MDataMap getProductFictitiousSales(String sellerCode, List<String> productCodeList, int day) {
		MDataMap productCodesFictitiousSalesMap = new MDataMap();
		if (day <= 0 || (StringUtils.isEmpty(sellerCode) && (null == productCodeList || productCodeList.isEmpty()))) {
			return productCodesFictitiousSalesMap;
		}
		if (null == productCodeList || productCodeList.isEmpty()) {
			productCodeList = new ArrayList<String>();
			if (StringUtils.isNotEmpty(sellerCode)) {
				List<MDataMap> productSalesMapList = DbUp.upTable("pc_productinfo").queryAll("product_code", "",
						"seller_code = '" + sellerCode + "'", null);
				for (MDataMap mDataMap : productSalesMapList) {
					productCodeList.add(mDataMap.get("product_code"));
				}
			}
		}

		// 获取day天前的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String beforeDay = sdf.format(cal.getTime());
		String whereSql = " day >= '" + beforeDay + "' ";

		if (null != productCodeList && productCodeList.size() > 0) {
			whereSql += " and product_code in ('" + StringUtils.join(productCodeList, "','") + "')";
		}
		List<MDataMap> productSalesMapList = DbUp.upTable("pc_productsales_everyday").queryAll("product_code,day,sales",
				"", whereSql, null);
		// 获取到结果集里面所有的商品Code,放map里为了去重,key:product_code;value:fictitious_sales
		for (MDataMap mDataMap : productSalesMapList) {
			productCodesFictitiousSalesMap.put(mDataMap.get("product_code"), "0");
		}
		/**
		 * 2015-08-13增加虚拟销量
		 */
		List<MDataMap> productFictitiousSales = DbUp.upTable("pc_productinfo_ext").queryAll(
				"product_code,fictitious_sales", "",
				"product_code in ('" + StringUtils.join(productCodeList, "','") + "')", null);
		Map<String, Integer> fictitionMap = new HashMap<String, Integer>();
		for (MDataMap mDataMap : productFictitiousSales) {
			String fictition = StringUtils.isBlank(mDataMap.get("fictitious_sales")) ? "0"
					: mDataMap.get("fictitious_sales");
			fictitionMap.put(mDataMap.get("product_code"), Integer.parseInt(fictition));
		}
		// 循环productCode，获取到各个商品的销量mapList
		for (String productCode : productCodeList) {
			int fx = 0; // 总销量
			if (productCodesFictitiousSalesMap.containsKey(productCode)) {
				for (int i = day; i > 0; i--) {
					// 循环求出每日销量
					Calendar everyDay = Calendar.getInstance();
					everyDay.add(Calendar.DATE, -i);
					String nowDay = sdf.format(everyDay.getTime());
					int sales = 0; // 商品在这天的真实销量
					for (MDataMap productSalesMap : productSalesMapList) {
						if (productCode.equals(productSalesMap.get("product_code"))
								&& nowDay.equals(productSalesMap.get("day"))) {
							sales = Integer.parseInt(productSalesMap.get("sales"));
							break;
						}
					}
					double fictitiousSales = fx + Math.abs(1 / Math.sin(3 * (day - i + 1))) + 2 * sales;
					fx = Integer.parseInt(new java.text.DecimalFormat("0").format(fictitiousSales));
				}
			}
			if (fictitionMap.containsKey(productCode)) {
				fx = fx + fictitionMap.get(productCode); // 2015-08-13销量增加虚拟销量基数
			}
			productCodesFictitiousSalesMap.put(productCode, fx + "");
		}
		return productCodesFictitiousSalesMap;
	}

	/**
	 * 闪购限购数
	 * 
	 * @return 返回-1表示无限制
	 */
	public Integer getFlashProductLimitNum(String buyCode, String skuCode) {

		if (StringUtils.isEmpty(skuCode)) {
			return -1;
		}
		int sales_num = 0; // 促销库存
		int purchase_limit_day_num = 0; // 每日限购数
		int purchase_limit_order_num = 0; // 每单限购数
		int purchase_limit_vip_num = 0; // 会员限购数
		try {
			// 获取闪购商品的限购数
			String sSql = "select a.sku_code,a.purchase_limit_day_num,a.purchase_limit_order_num,a.purchase_limit_vip_num,a.sales_num,a.activity_code "
					+ "from gc_activity_flash_sku a,gc_activity_flash b ,gc_activity_info c "
					+ "where a.activity_code=b.activity_code and a.activity_code = c.activity_code "
					+ "and b.flag_type='4497469800030001' and a.status='449746810001' "
					+ "and b.status='4497469800020001' and c.start_time<now()  "
					+ "and c.end_time>now()  and a.sku_code='" + skuCode + "' ";
			Map<String, Object> flashSkuInfo = DbUp.upTable("gc_activity_flash_sku").dataSqlOne(sSql, null);
			if (null == flashSkuInfo || flashSkuInfo.isEmpty()) {
				return -1;
			}
			// 促销库存
			sales_num = (flashSkuInfo.get("sales_num") == null ? 0
					: Integer.parseInt(String.valueOf(flashSkuInfo.get("sales_num"))));
			if (sales_num - this.salesNumUsed(skuCode, String.valueOf(flashSkuInfo.get("activity_code"))) <= 0) {
				return 0;
			}
			purchase_limit_day_num = (flashSkuInfo.get("purchase_limit_day_num") == null ? 0
					: Integer.parseInt(String.valueOf(flashSkuInfo.get("purchase_limit_day_num"))));
			purchase_limit_order_num = (flashSkuInfo.get("purchase_limit_order_num") == null ? 0
					: Integer.parseInt(String.valueOf(flashSkuInfo.get("purchase_limit_order_num"))));
			purchase_limit_vip_num = (flashSkuInfo.get("purchase_limit_vip_num") == null ? 0
					: Integer.parseInt(String.valueOf(flashSkuInfo.get("purchase_limit_vip_num"))));

			if (StringUtils.isNotEmpty(buyCode)) {
				// 统计该用户此闪购商品的所有下单数量allBuyCount,今日下单数量dayBuyCount,不取交易失败的下单数
				String oSql = "select sum(od.sku_num) as allBuyCount,( "
						+ " select sum(od1.sku_num) from oc_orderdetail od1,oc_orderinfo oi1 where oi1.order_code=od1.order_code "
						+ " and oi1.order_code=od.order_code and left(oi1.create_time ,10) = CURRENT_DATE() "
						+ " ) as dayBuyCount from oc_orderdetail od,oc_orderinfo oi "
						+ " where od.order_code=oi.order_code and od.sku_code='" + skuCode
						+ "' and oi.order_type='449715200004' " + " and oi.buyer_code='" + buyCode
						+ "' and oi.order_status != '4497153900010006' ";
				Map<String, Object> buyCountMap = DbUp.upTable("oc_orderdetail").dataSqlOne(oSql, null);

				int allBuyCount = 0; // 会员总购买数
				int dayBuyCount = 0; // 会员今日购买数
				allBuyCount = (buyCountMap.get("allBuyCount") == null ? 0
						: Integer.parseInt(String.valueOf(buyCountMap.get("allBuyCount"))));
				dayBuyCount = (buyCountMap.get("dayBuyCount") == null ? 0
						: Integer.parseInt(String.valueOf(buyCountMap.get("dayBuyCount"))));

				purchase_limit_day_num = (purchase_limit_day_num - dayBuyCount); // 会员今日实际限购数
				purchase_limit_vip_num = (purchase_limit_vip_num - allBuyCount); // 会员总共实际限购数
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		int lowLimit = -1; // 最低限购数
		lowLimit = purchase_limit_day_num;
		if (lowLimit > purchase_limit_order_num) {
			lowLimit = purchase_limit_order_num;
		}
		if (lowLimit > purchase_limit_vip_num) {
			lowLimit = purchase_limit_vip_num;
		}

		return lowLimit;
	}

	/***
	 * 
	 * 统计已经使用的促销库存
	 * 
	 * @param skuCode
	 * @param activity_code
	 * @return
	 */
	private int salesNumUsed(String skuCode, String activityCode) {
		BigDecimal usedNmu = new BigDecimal(0);
		// 查询成功订单参与当前闪购的商品数量
		String csql = "SELECT SUM(sku_num) as sm from oc_orderdetail where order_code in (SELECT order_code from oc_order_activity where order_code in (SELECT order_code from oc_orderinfo where order_status <>'4497153900010006') AND sku_code=:skuCode and activity_code=:activityCode) and  sku_code=:skuCode  ";
		List<Map<String, Object>> list = DbUp.upTable("oc_order_activity").dataSqlList(csql,
				new MDataMap("activityCode", activityCode, "skuCode", skuCode));
		if (list != null && list.size() > 0) {
			if (list.get(0).get("sm") != null) {
				usedNmu = (BigDecimal) list.get(0).get("sm");
			}
		}

		return Integer.valueOf(String.valueOf(usedNmu));
	}

	/**
	 * 根据productCode查询商品是否是闪购，是否包含内联赠品（仅支持惠家有调用）
	 * 
	 * @param productCodes
	 *            格式商品编号中间用逗号隔开：code,code,code,...
	 * @return 外层map的key为商品编码，里层map的key为是否是闪购:isFlash和是否是赠品:isIncludeGift,
	 *         value为1:是，0:否
	 * @author ligj
	 */
	public Map<String, Map<String, Integer>> getProductSomeInfo(String productCodes) {
		// 外层map的key为商品编码，里层map的key为是否是闪购:isFlash和是否是赠品:isIncludeGift,value为1:是，0:否
		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();

		if (StringUtils.isEmpty(productCodes)) {
			return result;
		}
		String appCode = "SI2003";
		String isFlash = "isFlash"; // 是否是闪购
		String isIncludeGift = "isIncludeGift"; // 是否包含赠品
		String gift = "内联赠品";
		String[] productCodeArr = productCodes.split(",");

		String productWhere = "product_code in ('" + productCodes.replace(",", "','") + "')";
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code", "", productWhere,
				null);

		if (null != skuInfoMap) {

			// 此map中存在的商品编号都是包含赠品的
			Map<String, Integer> isIncludeGiftMap = new HashMap<String, Integer>();
			// 自定义属性列表
			String propertiesWhere = "product_code in ('" + productCodes.replace(",", "','") + "') and property_type='449736200004' ";
			List<MDataMap> productPropertiesMap = DbUp.upTable("pc_productproperty").queryAll("product_code,property_key,start_date,end_date", "", propertiesWhere, null);
			for (MDataMap mDataMap : productPropertiesMap) {
				if (gift.equals(mDataMap.get("property_key"))) {
					String startDate = mDataMap.get("start_date");
					String endDate = mDataMap.get("end_date");
					if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String currDate = sdf.format(new Date());
						if(startDate.compareTo(currDate) < 0 && currDate.compareTo(endDate) < 0){
							isIncludeGiftMap.put(mDataMap.get("product_code"), 1);
						}else{
							isIncludeGiftMap.put(mDataMap.get("product_code"), 0);
						}
					}else{    // 如果是历史数据，没有被编辑过的商品则开始时间和结束时间都是空，则不做处理 - Yangcl
						isIncludeGiftMap.put(mDataMap.get("product_code"), 1);
					}
					
				}
			}

			Map<String, String> skuProduct = new HashMap<String, String>(); // skuCode与productCode键值对
			String skuCodes = "";
			for (MDataMap mDataMap : skuInfoMap) {
				skuProduct.put(mDataMap.get("sku_code"), mDataMap.get("product_code"));
				skuCodes += (mDataMap.get("sku_code") + ",");
			}
			if (StringUtils.isEmpty(skuCodes)) {
				return result;
			}

			String activitySql = "select fs.sku_code sku_code,fs.activity_code from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() and fs.sku_code in ('"
					+ skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','") + "') "
					+ " and fs.status='449746810001' and af.status='449746740002' and af.app_code='" + appCode + "' ";
			List<Map<String, Object>> activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql,
					null);
			if (null != activityMapList) {
				for (Map<String, Object> map : activityMapList) {
					Map<String, Integer> activityMap = new HashMap<String, Integer>(); // 保存活动信息的Map
					String productCode = skuProduct.get(map.get("sku_code")); // 参加闪购的productCode
					
					Integer isInclude = (null == isIncludeGiftMap.get(productCode) ? 0 : isIncludeGiftMap.get(productCode));
					
					// 判断促销库存
					if (salesNumSurplus(map.get("sku_code").toString(), map.get("activity_code").toString()) > 0) {
						activityMap.put(isFlash, 1);
					} else {
						activityMap.put(isFlash, 0);
					}
					activityMap.put(isIncludeGift, isInclude);
					result.put(productCode, activityMap);
				}
				for (String productCode : productCodeArr) {
					if (null == result.get(productCode) || result.get(productCode).isEmpty()) {
						Map<String, Integer> activityMap = new HashMap<String, Integer>(); // 保存活动信息的Map
						Integer isInclude = (null == isIncludeGiftMap.get(productCode) ? 0
								: isIncludeGiftMap.get(productCode));
						activityMap.put(isFlash, 0);
						activityMap.put(isIncludeGift, isInclude);
						result.put(productCode, activityMap);
					}
				}

			} else {
				for (String productCode : productCodeArr) {
					Map<String, Integer> activityMap = new HashMap<String, Integer>(); // 保存活动信息的Map
					Integer isInclude = (null == isIncludeGiftMap.get(productCode) ? 0
							: isIncludeGiftMap.get(productCode));
					activityMap.put(isFlash, 0);
					activityMap.put(isIncludeGift, isInclude);
					result.put(productCode, activityMap);
				}
			}
		}
		return result;
	}

	/**
	 * 根据productCode查询商品是否是闪购，是否包含内联赠品（仅支持惠家有调用）版本号（3.5.72.55）(活动规则：内购>闪购>特价)
	 * 
	 * @param productCodes
	 *            格式商品编号中间用逗号隔开：code,code,code,...
	 * @param userType
	 *            用户类型（4497469400050001：内购，4497469400050002：会员）
	 * @return 外层map的key为商品编码，里层map的key为是否是闪购:isFlash和是否是赠品:isIncludeGift,是否特价：
	 *         isSpecial，是否内购：isSource，value为1:是，0:否
	 * @author ligj
	 */
	public Map<String, Map<String, Integer>> getProductSomeInfo(String productCodes, String userType) {
		// 外层map的key为商品编码，里层map的key为是否是闪购:isFlash和是否是赠品:isIncludeGift,value为1:是，0:否
		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();

		if (StringUtils.isEmpty(productCodes)) {
			return result;
		}
		String appCode = "SI2003";
		String isFlash = "isFlash"; // 是否是闪购
		String isIncludeGift = "isIncludeGift"; // 是否包含赠品
		String isSource = "isSource"; // 是否内购
		String isSpecial = "isSpecial"; // 是否特价
		String isSeckill = "isSeckill"; // 是否限时限量活动
		String isDutch = "isDutch"; // 是否拍卖

		// String gift ="内联赠品";
		String[] productCodeArr = productCodes.split(",");

		String productWhere = "product_code in ('" + productCodes.replace(",", "','") + "')";
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,seller_code", "",
				productWhere, null);

		// 获取商品所属供应商,用来判断商品是否参加内购，为SI2003时才能参加内购
		MDataMap productSellerMap = new MDataMap();
		MDataMap productSmallSellerMap = new MDataMap();
		// 获取到商品是否为虚拟商品，只有非虚拟商品才能参加内购
		MDataMap productValidateFlagMap = new MDataMap();
		List<MDataMap> productInfoMap = DbUp.upTable("pc_productinfo")
				.queryAll("product_code,seller_code,small_seller_code,validate_flag", "", productWhere, null);
		if (productInfoMap != null && !productInfoMap.isEmpty()) {
			for (MDataMap mDataMap : productInfoMap) {
				productSellerMap.put(mDataMap.get("product_code"), mDataMap.get("seller_code"));
				productSmallSellerMap.put(mDataMap.get("product_code"), mDataMap.get("small_seller_code"));
				productValidateFlagMap.put(mDataMap.get("product_code"), mDataMap.get("validate_flag"));
			}
		}

		if (null != skuInfoMap) {

			// 此map中存在的商品编号都是包含赠品的
			Map<String, Integer> isIncludeGiftMap = new HashMap<String, Integer>();
			// 自定义属性列表
			// String propertiesWhere = "product_code in
			// ('"+productCodes.replace(",", "','")+"') and
			// property_type='449736200004' ";
			// List<MDataMap> productPropertiesMap =
			// DbUp.upTable("pc_productproperty").queryAll("product_code,property_key","",propertiesWhere,null);
			// for (MDataMap mDataMap : productPropertiesMap) {
			// if (gift.equals(mDataMap.get("property_key"))) {
			// isIncludeGiftMap.put(mDataMap.get("product_code"), 1);
			// }
			// }
			// 赠品信息
			Map<String, String> giftMap = this.getProductGifts(productCodes);
			for (String productCode : giftMap.keySet()) {
				if (StringUtils.isNotEmpty(giftMap.get(productCode))) {
					isIncludeGiftMap.put(productCode, 1);
				}
			}
			Map<String, String> skuProduct = new HashMap<String, String>(); // skuCode与productCode键值对
			String skuCodes = "";
			Map<String, Map<String, Integer>> productSkuList = new HashMap<String, Map<String, Integer>>(); // 外层key：productCode,内层key:skuCode
			for (MDataMap mDataMap : skuInfoMap) {
				skuProduct.put(mDataMap.get("sku_code"), mDataMap.get("product_code"));
				skuCodes += (mDataMap.get("sku_code") + ",");

				Map<String, Integer> skuMap = new HashMap<String, Integer>();
				if (productSkuList.containsKey(mDataMap.get("product_code"))) {
					skuMap = productSkuList.get(mDataMap.get("product_code"));
					skuMap.put(mDataMap.get("sku_code"), 1);
				} else {
					skuMap.put(mDataMap.get("sku_code"), 1);
				}
				productSkuList.put(mDataMap.get("product_code"), skuMap);
			}
			if (StringUtils.isEmpty(skuCodes)) {
				return result;
			}
			int isSourceFlag = this.checkIsVipSpecial(userType) ? 1 : 0;
			Map<String, Integer> activityMap = null; // 保存活动信息的Map
			Map<String, Integer> flashMap = new HashMap<String, Integer>(); // 参加闪购的product
			String activitySql = "select fs.sku_code sku_code,fs.activity_code from oc_flashsales_skuInfo fs,oc_activity_flashsales af "
					+ " where fs.activity_code = af.activity_code and af.start_time <= now() and af.end_time > now() and fs.sku_code in ('"
					+ skuCodes.substring(0, skuCodes.length() - 1).replace(",", "','") + "') "
					+ " and fs.status='449746810001' and af.status='449746740002' and af.app_code='" + appCode + "' ";
			List<Map<String, Object>> activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql,
					null);
			if (null != activityMapList) {
				// 获取到所有参加闪购活动的sku
				for (Map<String, Object> map : activityMapList) {
					String productCode = skuProduct.get(map.get("sku_code")); // 参加闪购的productCode
					// 判断促销库存
					if (salesNumSurplus(productCode, map.get("activity_code").toString()) > 0) {
						flashMap.put(productCode, 1);
					}
				}
			}
			// 特价
			PlusSupportProduct support = new PlusSupportProduct();
			for (String productCode : productCodeArr) {
				activityMap = new HashMap<String, Integer>();
				if (flashMap.containsKey(productCode)) {
					activityMap.put(isFlash, 1);
					activityMap.put(isSpecial, 0);
					activityMap.put(isSeckill, 0);
					activityMap.put(isDutch, 0);
				} else {
					activityMap.put(isFlash, 0);
					Map<String, Integer> skuMap = productSkuList.get(productCode);
					for (String skuCode : skuMap.keySet()) {
						PlusModelSkuInfo skuSuppore = support.upSkuInfoBySkuCode(skuCode);
						// if (StringUtils.isNotEmpty(skuSuppore.getEventCode())
						// && 1 == skuSuppore.getBuyStatus()) {
						if (StringUtils.isNotEmpty(skuSuppore.getEventCode())) {
							String eventTypeCode = support.upEventTypeCode(skuSuppore.getEventCode());
							if ("4497472600010001".equals(eventTypeCode)) { // 限时限量活动
								activityMap.put(isSpecial, 0);
								activityMap.put(isSeckill, 1);
								activityMap.put(isDutch, 0);
								break;
							} else if ("4497472600010002".equals(eventTypeCode)) { // 特价
								activityMap.put(isSpecial, 1);
								activityMap.put(isSeckill, 0);
								activityMap.put(isDutch, 0);
								break;
							} else if ("4497472600010003".equals(eventTypeCode)) { // 拍卖
								activityMap.put(isSpecial, 0);
								activityMap.put(isSeckill, 0);
								activityMap.put(isDutch, 1);
								break;
							} else {
								activityMap.put(isSpecial, 0);
								activityMap.put(isSeckill, 0);
								activityMap.put(isDutch, 0);
							}
						} else {
							activityMap.put(isSpecial, 0);
							activityMap.put(isSeckill, 0);
							activityMap.put(isDutch, 0);
						}
					}
				}
				Integer isInclude = (null == isIncludeGiftMap.get(productCode) ? 0 : isIncludeGiftMap.get(productCode));
				activityMap.put(isIncludeGift, isInclude);
				if (0 == activityMap.get(isFlash) && 0 == activityMap.get(isSpecial) && 0 == activityMap.get(isSeckill)
						&& 0 == activityMap.get(isDutch)
						&& AppConst.MANAGE_CODE_HOMEHAS.equals(productSmallSellerMap.get(productCode))// 只有LD商品才能参加内购
						&& AppConst.MANAGE_CODE_HOMEHAS.equals(productSellerMap.get(productCode))
						&& "N".equals(productValidateFlagMap.get(productCode))// 只有LD非虚拟商品才能参加内购
				) {
					activityMap.put(isSource, isSourceFlag);
				} else {
					activityMap.put(isSource, 0);
				}
				result.put(productCode, activityMap);
			}
		}
		return result;
	}

	/**
	 * 根据skuCode查询商品是否是闪购（仅支持惠家有调用）
	 * 
	 * @param skuCodes
	 *            格式商品编号中间用逗号隔开：code,code,code,...
	 * @return map的key为sku编号，value为1:是，0:否
	 * @author ligj
	 */
	public Map<String, Integer> checkSkuIsFlash(String skuCodes) {
		Map<String, Integer> result = new HashMap<String, Integer>();

		if (StringUtils.isEmpty(skuCodes)) {
			return result;
		}
		String[] skuCodeArr = skuCodes.split(",");
		List<String> skuCodeList = new ArrayList<String>();
		for (String skuCode : skuCodeArr) {
			result.put(skuCode, 0);
			skuCodeList.add(skuCode);
		}
		String appCode = "SI2003";
		String activitySql = "select DISTINCT fs.sku_code sku_code,fs.activity_code activity_code from "
				+ " ordercenter.oc_flashsales_skuInfo fs,ordercenter.oc_activity_flashsales af "
				+ " where fs.activity_code = af.activity_code " + " and af.start_time <= now() and af.end_time > now() "
				+ " and fs.sku_code in ('" + StringUtils.join(skuCodeList, "','") + "')"
				+ " and fs.status='449746810001' and af.status='449746740002' and af.app_code='" + appCode + "' ";
		List<Map<String, Object>> activityMapList = DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(activitySql,
				null);
		if (null != activityMapList) {
			for (Map<String, Object> map : activityMapList) {
				String sc = map.get("sku_code").toString();
				// 判断促销库存
				if (salesNumSurplus(sc, map.get("activity_code").toString()) <= 0) {
					result.put(sc, 0);
				} else {
					result.put(sc, 1);
				}
			}
		}
		return result;
	}

	/**
	 * 根据skuCode查询商品是否是促销（仅支持惠家有调用）
	 * 
	 * @param skuCodes
	 *            格式商品编号中间用逗号隔开：code,code,code,...
	 * @return map的key为sku编号，value为1:是，0:否
	 * @author ligj
	 */
	public Map<String, Integer> checkSkuIsCxActivity(String skuCodes, String buyerCode) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (StringUtils.isEmpty(skuCodes)) {
			return result;
		}
		String[] skuCodeArr = skuCodes.split(",");
		for (String skuCode : skuCodeArr) {
			result.put(skuCode, 0);
		}
		for (int i = 0; i < skuCodeArr.length; i++) {
			PlusModelSkuInfo info = new PlusSupportProduct().upSkuInfoBySkuCode(skuCodeArr[i], buyerCode);
			if (info.getBuyStatus() == 1 && StringUtility.isNotNull(info.getEventCode())) {
				result.put(skuCodeArr[i], 1);
			}
		}
		return result;
	}

	/**
	 * @Description:获取满场的权威标识 @return @author 张海生 @date 2015-1-20
	 *                        下午1:42:35 @return List<PcAuthorityLogo> @throws
	 */
	public List<PcAuthorityLogo> getAuthorityLogo(String seller_code) {
		List<PcAuthorityLogo> result = new ArrayList<PcAuthorityLogo>();

		MDataMap map = new MDataMap();

		map.put("manage_code", seller_code);

		List<MDataMap> logoList = DbUp.upTable("pc_authority_logo").queryAll(null, "logo_location desc",
				"manage_code=:manage_code", map);

		SerializeSupport<PcAuthorityLogo> sSupport = new SerializeSupport<PcAuthorityLogo>();
		for (MDataMap mDataMap : logoList) {
			PcAuthorityLogo model = new PcAuthorityLogo();
			sSupport.serialize(mDataMap, model);
			result.add(model);
		}
		return result;
	}

	/**
	 * 获取需要显示的商品的基本信息
	 * 
	 * @author liqt
	 * @return
	 */
	public PcProductinfoBase getProductInfoForMabyLoveChart(String productCode) {
		PcProductinfoBase model = new PcProductinfoBase();
		String sWhere = "product_code = '" + productCode + "'";
		String sFields = "product_code,product_name,min_sell_price,market_price,product_status,mainpic_url";
		MDataMap map = DbUp.upTable("pc_productinfo").oneWhere(sFields, "", sWhere);

		if (null != map && !map.isEmpty()) {
			model.setProductCode(map.get("product_code"));
			model.setProductName(map.get("product_name"));
			model.setSellPrice(map.get("min_sell_price"));
			model.setMainPicUrl(map.get("mainpic_url"));
			model.setMarketPrice(map.get("market_price"));
			// 已上架
			if ("4497153900060002".equals(map.get("product_status"))) {
				model.setProductStatus("已上架");
			} else {
				model.setProductStatus("已下架");
			}
		}
		return model;
	}

	/**
	 * 通过sku_key 和 商品编码查找sku编号
	 * 
	 * @param product_code
	 *            商品编码或外部商品编码
	 * @param color_id
	 * @param style_id
	 * @param out_code_flag
	 *            true内部商品编码 false 外部商品编码
	 * @return
	 */
	public String getSkuByKey(String product_code, String color_id, String style_id, boolean out_code_flag) {
		String key = "color_id=" + color_id + "&style_id=" + style_id;
		List<Map<String, Object>> list = DbUp.upTable("pc_skuinfo").dataSqlList(
				"select sku_code from pc_skuinfo where " + "sku_key=:sku_key and "
						+ (out_code_flag ? "product_code" : "sell_productcode")
						+ "=:product_code order by product_code limit 1",
				new MDataMap("sku_key", key, "product_code", product_code));
		String sku_code = null;
		if (list != null && list.size() > 0) {
			sku_code = (String) list.get(0).get("sku_code");
		}
		return sku_code;
	}

	/***
	 * 查看用户使用该活动已购买的商品个数<惠家有专用>
	 * 
	 * @param sku_code
	 * @param buyer_code
	 * @param activity_code
	 * @return
	 */
	public int getUsedPurchaseLimitVipNum3(String sku_code, String buyer_code, String activity_code) {

		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(
				"SELECT o.order_code from oc_order_activity a LEFT JOIN oc_orderinfo o on a.order_code=o.order_code WHERE a.sku_code=:sku_code and o.buyer_code=:buyer_code and a.activity_code=:activity_code and o.order_status<>'4497153900010006' ",
				new MDataMap("buyer_code", buyer_code, "sku_code", sku_code, "activity_code", activity_code));

		if (list != null && list.size() > 0) {
			StringBuffer where_order = new StringBuffer("");
			for (Map<String, Object> map : list) {
				String order_code = (String) map.get("order_code");
				where_order.append("'").append(order_code).append("',");
			}

			if (where_order.length() > 0) {
				where_order.deleteCharAt(where_order.length() - 1);
			}

			Map<String, Object> map = DbUp.upTable("oc_orderinfo")
					.dataSqlOne("SELECT SUM(sku_num) as num from oc_orderdetail where order_code in (" + where_order
							+ ") and sku_code =:sku_code ", new MDataMap("sku_code", sku_code));
			if (map != null && map.size() > 0) {
				BigDecimal num = (BigDecimal) map.get("num");
				if (num != null) {
					return num.intValue();
				}
			}
			return 0;
		}

		return 0;
	}

	/***
	 * 查看用户是否收藏该商品<惠家有专用>
	 * 
	 * @param product_code
	 * @param member_code
	 * @return count 大于0时表示已收藏
	 */
	public int getIsCollectionProduct(String product_code, String member_code) {
		if (StringUtils.isEmpty(product_code) || StringUtils.isEmpty(member_code)) {
			return 0;
		}
		int count = DbUp.upTable("fh_product_collection").count("product_code", product_code, "member_code",
				member_code, "operate_type", "4497472000020001");
		if (count > 0) {
			return 1;
		}
		return 0;
	}

	/***
	 * 查看审核中的商品的最新信息（pc_productflow）
	 * 
	 * @param productCode
	 *            productCode
	 * @return 商品信息
	 */
	public List<ProductSkuInfo> upProductSkuInfoJson(String productCode) {
		List<ProductSkuInfo> skuDetail = new ArrayList<ProductSkuInfo>();
		if (StringUtils.isEmpty(productCode)) {
			return skuDetail;
		}
		PcProductinfo product = getProduct(productCode + "_1");
		skuDetail = product.getProductSkuInfoList();
		List<String> skuCodeArr = new ArrayList<String>();
		for (ProductSkuInfo productSkuInfo : skuDetail) {
			if (StringUtils.isNotEmpty(productSkuInfo.getSkuCode())) {
				skuCodeArr.add(productSkuInfo.getSkuCode());
			}
		}
		List<MDataMap> mDataMapList = DbUp.upTable("pc_skuinfo").queryAll("uid,sku_code", "",
				"sku_code in ('" + StringUtils.join(skuCodeArr, "','") + "')", null);
		MDataMap skuCode_uid = new MDataMap();
		for (MDataMap mDataMap : mDataMapList) {
			skuCode_uid.put(mDataMap.get("sku_code"), mDataMap.get("uid"));
		}

		// 设置uid
		for (int i = 0; i < skuDetail.size(); i++) {
			String skuCode = skuDetail.get(i).getSkuCode();
			if (StringUtils.isNotEmpty(skuCode)) {
				if (StringUtils.isNotEmpty(skuCode_uid.get(skuCode))) {
					skuDetail.get(i).setUid(skuCode_uid.get(skuCode));
				}
			}
		}

		return skuDetail;
	}

	/***
	 * 查看单条sku商品的最新信息（pc_productflow）
	 * 
	 * @param productCode
	 *            productCode
	 * @param skuCode
	 *            skuCode
	 * @return 商品信息
	 */
	public ProductSkuInfo upProductSkuBaseInfoJson(String productCode, String skuCode) {
		ProductSkuInfo skuDetail = new ProductSkuInfo();
		if (StringUtils.isEmpty(productCode)) {
			return skuDetail;
		}
		if (StringUtils.isEmpty(skuCode)) {
			skuCode = "";
		}
		PcProductinfo product = getProduct(productCode + "_1");
		for (ProductSkuInfo productSkuInfo : product.getProductSkuInfoList()) {
			if (skuCode.equals(productSkuInfo.getSkuCode())) {
				skuDetail = productSkuInfo;
				break;
			}
		}
		return skuDetail;
	}

	/***
	 * 查看商品下所有sku商品的最新信息（pc_productflow）
	 * 
	 * @param productCode
	 *            productCode
	 * @param skuCode
	 *            skuCode（不为空时查询单条）
	 * @return 商品信息
	 */
	public List<ProductSkuInfo> upProductAllSkuBaseInfoJson(String productCode, String skuCode) {
		List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();
		if (StringUtils.isEmpty(productCode)) {
			return skuList;
		}
		PcProductinfo product = getProduct(productCode + "_1");
		for (ProductSkuInfo productSkuInfo : product.getProductSkuInfoList()) {
			if (StringUtils.isEmpty(skuCode)) {
				skuList.add(productSkuInfo);
			} else {
				if (skuCode.equals(productSkuInfo.getSkuCode())) {
					skuList.add(productSkuInfo);
					break;
				}
			}
		}
		return skuList;
	}

	/***
	 * 查看审核中的商品的最新信息（pc_productflow）(商开查看商品详情用)
	 * 
	 * @param productCode
	 *            productCode
	 * @author Ligj
	 * @return 商品信息
	 */
	public PcProductinfoSk upProductInfoJsonForCshop(String productCode) {
		PcProductinfoSk productInfoSk = new PcProductinfoSk();
		List<ProductSkuInfo> skuDetail = new ArrayList<ProductSkuInfo>();
		if (StringUtils.isEmpty(productCode)) {
			return productInfoSk;
		}
		PcProductinfo productInfo = getProduct(productCode);
		BeanUtils.copyProperties(productInfo, productInfoSk);

		skuDetail = productInfoSk.getProductSkuInfoList();

		// 毛利润
		String grossProfitStr = "0";
		if (StringUtils.isNotBlank(productCode)) {
			if (null != productInfoSk && StringUtils.isNotBlank(productInfoSk.getCostPrice() + "")) {
				if (null != skuDetail && skuDetail.size() > 0) {
					BigDecimal costPrice = BigDecimal.ZERO;
					BigDecimal skuSellPrice = BigDecimal.ZERO;
					for (ProductSkuInfo skuInfo : skuDetail) {
						costPrice = costPrice.add(skuInfo.getCostPrice());
						skuSellPrice = skuSellPrice.add(skuInfo.getSellPrice());
					}
					BigDecimal grossProfit = BigDecimal.ZERO;
					if (skuSellPrice.compareTo(BigDecimal.ZERO) > 0) { // 销售价大于0的时候再计算毛利润
						grossProfit = skuSellPrice.subtract(costPrice).multiply(new BigDecimal(100))
								.divide(skuSellPrice, 2, BigDecimal.ROUND_HALF_UP);
					}
					grossProfitStr = grossProfit.toString();
				}
			}
		}
		productInfoSk.getPcProductinfoExt().setGrossProfit(grossProfitStr + "%");
		// 显示百分号的税率
		String taxRatePercent = "0";
		if (null != productInfoSk && null != productInfoSk.getTaxRate()
				&& productInfoSk.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {

			taxRatePercent = productInfoSk.getTaxRate().multiply(new BigDecimal(100)).toString();
			if (taxRatePercent.indexOf(".") > 0) {
				taxRatePercent = taxRatePercent.replaceAll("0+?$", "");// 去掉多余的0
				taxRatePercent = taxRatePercent.replaceAll("[.]$", "");// 如最后一位是.则去掉
			}
		}
		productInfoSk.setTaxRatePercent("0".equals(taxRatePercent) ? taxRatePercent : (taxRatePercent + "%"));

		// 入库类型，仓库编号
		if (productInfoSk.getPcProductinfoExt() != null) {
			if (!StringUtils.isEmpty(productInfoSk.getPcProductinfoExt().getPrchType())) {
				//  10-商品中心一地入库
				//  20-网站一地入库
				// 00-非一地入库
				String prchTypeCode = productInfoSk.getPcProductinfoExt().getPrchType();
				String oaSiteNo = productInfoSk.getPcProductinfoExt().getOaSiteNo();
				if ("10".equals(prchTypeCode)) {
					productInfoSk.getPcProductinfoExt().setPrchType("商品中心一地入库");
					productInfoSk.getPcProductinfoExt().setOaSiteNo(oaSiteNo);
				} else if ("20".equals(prchTypeCode)) {
					productInfoSk.getPcProductinfoExt().setPrchType("网站一地入库");
					productInfoSk.getPcProductinfoExt().setOaSiteNo(oaSiteNo);
				} else if ("00".equals(prchTypeCode)) {
					productInfoSk.getPcProductinfoExt().setPrchType("非一地入库");
					// productInfoSk.getPcProductinfoExt().setOaSiteNo("C01,C02,C04,C10");
					productInfoSk.getPcProductinfoExt().setOaSiteNo(oaSiteNo);
				}
			}
		}
		/**
		 * 4497153900060001 待上架 4497153900060002 已上架 4497153900060003 商家下架
		 * 4497153900060004 平台强制下架
		 */
		String status = "已下架";
		if ("4497153900060001".equals(productInfoSk.getProductStatus())) {
			status = "待上架";
		} else if ("4497153900060002".equals(productInfoSk.getProductStatus())) {
			status = "已上架";
		} else if ("4497153900060003".equals(productInfoSk.getProductStatus())) {
			status = "商家下架";
		} else if ("4497153900060004".equals(productInfoSk.getProductStatus())) {
			status = "平台强制下架";
		}
		productInfoSk.setProductStatus(status);
		String defineCodes = productInfoSk.getPcProductinfoExt().getSettlementType() + "','"
				+ productInfoSk.getPcProductinfoExt().getPurchaseType();
		List<MDataMap> defineMap = DbUp.upTable("sc_define").queryAll("define_code,define_name", "",
				"define_code in ('" + defineCodes + "')", null);
		MDataMap defineNameMap = new MDataMap();
		if (null != defineMap && !defineMap.isEmpty()) {
			for (MDataMap mDataMap : defineMap) {
				defineNameMap.put(mDataMap.get("define_code"), mDataMap.get("define_name"));
			}
		}
		if (StringUtils.isNotEmpty(productInfoSk.getPcProductinfoExt().getSettlementType())) {
			productInfoSk.getPcProductinfoExt()
					.setSettlementType(defineNameMap.get(productInfoSk.getPcProductinfoExt().getSettlementType()));
		}
		if (StringUtils.isNotEmpty(productInfoSk.getPcProductinfoExt().getPurchaseType())) {
			productInfoSk.getPcProductinfoExt()
					.setPurchaseType(defineNameMap.get(productInfoSk.getPcProductinfoExt().getPurchaseType()));
		}

		List<String> skuCodeArr = new ArrayList<String>();
		for (ProductSkuInfo productSkuInfo : skuDetail) {
			if (StringUtils.isNotEmpty(productSkuInfo.getSkuCode())) {
				skuCodeArr.add(productSkuInfo.getSkuCode());
			}
		}
		List<MDataMap> mDataMapList = DbUp.upTable("pc_skuinfo").queryAll("uid,sku_code", "",
				"sku_code in ('" + StringUtils.join(skuCodeArr, "','") + "')", null);
		MDataMap skuCode_uid = new MDataMap();
		for (MDataMap mDataMap : mDataMapList) {
			skuCode_uid.put(mDataMap.get("sku_code"), mDataMap.get("uid"));
		}

		// 获取库存数量
		// List<Map<String, Object>> stockList
		// =DbUp.upTable("sc_store_skunum").dataSqlList("SELECT
		// sku_code,SUM(stock_num) as stock_num from sc_store_skunum where
		// sku_code in ('"+StringUtils.join(skuCodeArr,"','")+"') group by
		// sku_code ", null);
		// Map<String,Integer> stockMap = new HashMap<String, Integer>();
		// for (Map<String, Object> stock: stockList) {
		// String skuCode = stock.get("sku_code").toString();
		// String stockNum = stock.get("stock_num").toString();
		// if (StringUtils.isNotBlank(skuCode)) {
		// stockMap.put(skuCode,Integer.parseInt(StringUtils.isEmpty(stockNum) ?
		// "0" : stockNum));
		// }
		// }
		ProductStoreService pss = new ProductStoreService();
		// 设置uid
		for (int i = 0; i < skuDetail.size(); i++) {
			String skuCode = skuDetail.get(i).getSkuCode();
			if (StringUtils.isNotEmpty(skuCode)) {
				if (StringUtils.isNotEmpty(skuCode_uid.get(skuCode))) {
					skuDetail.get(i).setUid(skuCode_uid.get(skuCode));
				}
				skuDetail.get(i).setStockNum(pss.getStockNumBySkuBySum(skuCode));
			}
		}
		productInfoSk.setProductSkuInfoList(skuDetail);
		return productInfoSk;
	}

	/**
	 * 检查商品下是否存在重复sku规格型号
	 * 
	 * @param productCode
	 *            (不可为空)
	 * @param skuKey
	 *            (格式为：颜色属性=颜色&规格属性=规格)(优先判断这个值，此值为空时再判断colorPro与stylePro)
	 * @param colorPro
	 *            颜色属性
	 * @param stylePro
	 *            规格属性
	 * @return 1：存在重复，0：不存在重复
	 */
	public int checkRepeatSku(String productCode, String skuKey, String colorPro, String stylePro) {
		// String colorKey = "4497462000010025";
		// String styleKey = "4497462000021158";
		if (StringUtils.isNotEmpty(productCode)) {
			List<ProductSkuInfo> skuList = this.upProductAllSkuBaseInfoJson(productCode, null);
			if (skuList != null && !skuList.isEmpty()) {
				if (StringUtils.isNotEmpty(skuKey)) {
					for (ProductSkuInfo productSkuInfo : skuList) {
						if (skuKey.equals(productSkuInfo.getSkuKey())) {
							return 1;
						}
					}
				} else if (StringUtils.isNotEmpty(colorPro) && StringUtils.isNotEmpty(stylePro)) {
					// addSkuKey =
					// colorKey+"="+colorPro+"&"+styleKey+"="+stylePro;
					// //要新增的sku规格
					for (ProductSkuInfo productSkuInfo : skuList) {
						String skuKey_1 = productSkuInfo.getSkuKey();
						if (StringUtils.isNotBlank(skuKey_1)) {
							if (skuKey_1.indexOf(colorPro) > 0 && skuKey_1.indexOf(stylePro) > 0) {
								return 1;
							}
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 检查商品下是否存在重复sku规格型号
	 * 
	 * @param productCode
	 *            (不可为空)
	 * @param skuKey
	 *            (格式为：颜色属性=颜色&规格属性=规格)(优先判断这个值，此值为空时再判断colorPro与stylePro)
	 * @param colorPro
	 *            颜色属性code
	 * @param stylePro
	 *            规格属性code
	 * @param colorProName
	 *            颜色属性name
	 * @param styleProName
	 *            规格属性name
	 * @return 2:name存在重复，1：code存在重复，0：不存在重复（优先判断code是否重复）
	 */
	public int checkRepeatSku(String productCode, String skuKey, String skuKeyValue, String colorPro, String stylePro,
			String colorProName, String styleProName) {
		// String colorKey = "4497462000010025";
		// String styleKey = "4497462000021158";
		if (StringUtils.isNotEmpty(productCode)) {
			List<ProductSkuInfo> skuList = this.upProductAllSkuBaseInfoJson(productCode, null);
			if (skuList != null && !skuList.isEmpty()) {
				// 判断skuKey是否存在重复
				if (StringUtils.isNotEmpty(skuKey)) {
					for (ProductSkuInfo productSkuInfo : skuList) {
						if (skuKey.equals(productSkuInfo.getSkuKey())) {
							return 1;
						}
					}
				} else if (StringUtils.isNotEmpty(colorPro) && StringUtils.isNotEmpty(stylePro)) {
					// addSkuKey =
					// colorKey+"="+colorPro+"&"+styleKey+"="+stylePro;
					// //要新增的sku规格
					for (ProductSkuInfo productSkuInfo : skuList) {
						String skuKey_1 = productSkuInfo.getSkuKey();
						if (StringUtils.isNotBlank(skuKey_1)) {
							if (skuKey_1.indexOf(colorPro) > 0 && skuKey_1.indexOf(stylePro) > 0) {
								return 1;
							}
						}
					}
				}
				// 判断skuKeyValue是否存在重复
				if (StringUtils.isNotEmpty(skuKeyValue)) {
					for (ProductSkuInfo productSkuInfo : skuList) {
						if (skuKeyValue.equals(productSkuInfo.getSkuValue())) {
							return 2;
						}
					}
				} else if (StringUtils.isNotEmpty(colorProName) && StringUtils.isNotEmpty(styleProName)) {
					for (ProductSkuInfo productSkuInfo : skuList) {
						String skuKeyValue_1 = productSkuInfo.getSkuValue();
						if (StringUtils.isNotBlank(skuKeyValue_1)) {
							if (skuKeyValue_1.indexOf(colorProName) > 0 && skuKeyValue_1.indexOf(styleProName) > 0) {
								return 2;
							}
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 检查商品下是否存在重复sku规格型号
	 * 
	 * @param skuKeyList
	 * @param skukeyValueList
	 * @return 1：所选规格存在重复
	 */
	public int checkRepeatSku(List<String> skuKeyList, List<String> skukeyValueList) {
		if (null != skukeyValueList && skukeyValueList.size() > 0) {
			Map<String, String> skukeyValueMap = new HashMap<String, String>();
			for (String skuKey : skukeyValueList) {
				skukeyValueMap.put(skuKey, "");
			}
			if (skukeyValueMap.keySet().size() < skukeyValueList.size()) {
				return 1;
			}
		}
		if (null != skuKeyList && skuKeyList.size() > 0) {
			Map<String, String> skuKeyMap = new HashMap<String, String>();
			for (String skuKey : skuKeyList) {
				skuKeyMap.put(skuKey, "");
			}
			if (skuKeyMap.keySet().size() < skuKeyList.size()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 传入用户类型判断是否参加内购
	 * 
	 * @param userType
	 * @return
	 */
	public boolean checkIsVipSpecial(String userType) {
		if (StringUtils.isEmpty(userType)) {
			return false;
		}
		String weekDay = DateUtil.getSystemWeekdayString();
		String vipSpecialDays = bConfig("productcenter.vipSpecialDays"); // 内购日
		boolean flag = false;
		for (String vipSpecialDay : vipSpecialDays.split(",")) {
			if (vipSpecialDay.equals(weekDay)) {
				flag = true;
				break;
			}
		}
		if ("4497469400050001".equals(userType) && flag) {
			return true;
		}
		return false;
	}

	/**
	 * 获取内购价格（惠家有）
	 * 
	 * @param userType
	 *            用户类型（4497469400050001：内购，4497469400050002：会员）
	 * @param productCodeArr
	 * @return
	 */
	public Map<String, BigDecimal> getVipSpecialPrice(String userType, List<String> productCodeArr) {
		Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		if (null != productCodeArr && !productCodeArr.isEmpty() && this.checkIsVipSpecial(userType)) {
			List<MDataMap> productCostPriceMap = DbUp.upTable("pc_productinfo").queryAll("product_code,cost_price", "",
					"product_code in ('" + StringUtils.join(productCodeArr, "','") + "')", null);
			if (null != productCostPriceMap && !productCostPriceMap.isEmpty()) {
				for (MDataMap mDataMap : productCostPriceMap) {
					String costPrice = mDataMap.get("cost_price");
					BigDecimal vipSpecialPrice = new BigDecimal(costPrice)
							.add(new BigDecimal(bConfig("productcenter.vipSpecialPrice")))
							.setScale(2, BigDecimal.ROUND_HALF_UP);
					result.put(mDataMap.get("product_code"), vipSpecialPrice);
				}
				return result;
			}
		}
		return null;

	}

	/**
	 * 根据productCode查询商品的赠品（仅支持惠家有调用）
	 * 
	 * @param productCodes
	 *            格式商品编号中间用逗号隔开：code,code,code,...
	 * @return
	 * @author ligj
	 */
	public Map<String, String> getProductGifts(String productCodes) {

		// key为productCode,value为赠品
		Map<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(productCodes)) {
			return resultMap;
		}
		// 内联赠品
		Map<String, String> innerGiftMap = new HashMap<String, String>();
		// 外联赠品
		Map<String, String> outerGiftMap = new HashMap<String, String>();

		String[] productCodeArr = productCodes.split(",");

		String gift = "内联赠品";
		// 自定义属性列表
		String propertiesWhere = "product_code in ('" + productCodes.replace(",", "','") + "') and property_type='449736200004' ";
		List<MDataMap> productPropertiesMap = DbUp.upTable("pc_productproperty").queryAll("product_code,property_key,property_value,start_date,end_date", "", propertiesWhere, null);
		for (MDataMap mDataMap : productPropertiesMap) {
			if (gift.equals(mDataMap.get("property_key"))) {
				String startDate = mDataMap.get("start_date");
				String endDate = mDataMap.get("end_date");
				if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currDate = sdf.format(new Date());
					if(startDate.compareTo(currDate) < 0 && currDate.compareTo(endDate) < 0){
						innerGiftMap.put(mDataMap.get("product_code"), mDataMap.get("property_value"));
					}else{
//						innerGiftMap.put(mDataMap.get("product_code"), mDataMap.get("property_value"));
					}
				}else{    // 如果是历史数据，没有被编辑过的商品则开始时间和结束时间都是空，则不做处理 - Yangcl
					innerGiftMap.put(mDataMap.get("product_code"), mDataMap.get("property_value"));
				}
				
				
			}
		}
		// 获取外联赠品的输入参数
		// List<MDataMap> giftsMapList =
		// DbUp.upTable("pc_product_gifts_new").queryAll("product_code,gift_name",
		// "",
		// "seller_code='SI2003' and product_code in ('" +
		// productCodes.replace(",", "','") + "')", null);
		// if (null != giftsMapList) {
		// for (MDataMap giftMap : giftsMapList) {
		// String productCode = giftMap.get("product_code");
		// String giftName = giftMap.get("gift_name");
		// if (StringUtils.isNotBlank(outerGiftMap.get(productCode))) {
		// giftName = outerGiftMap.get(productCode) + giftName;
		// }
		// outerGiftMap.put(productCode, giftName + ";");
		// }
		// }
		for (String productCode : productCodeArr) {
			String innerGiftName = innerGiftMap.get(productCode);
			String outerGiftName = outerGiftMap.get(productCode);
			String giftName = "";
			if (StringUtils.isNotEmpty(innerGiftName)) {
				giftName += innerGiftName + ";";

			}
			if (StringUtils.isNotEmpty(outerGiftName)) {
				giftName += outerGiftName;
			}
			resultMap.put(productCode, giftName);
		}
		return resultMap;
	}

	/**
	 * 获取网络图片的图片类型
	 * 
	 * @param sUrl
	 * @return （gif、png、jpg..）
	 */
	public String getImgType(String sUrl) {
		String imgType = "";
		if (StringUtils.isBlank(sUrl)) {
			return imgType;
		}
		BufferedInputStream bis = null;
		HttpURLConnection urlconnection = null;
		URL url = null;
		try {
			url = new URL(sUrl);
			urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.connect();
			bis = new BufferedInputStream(urlconnection.getInputStream());
			imgType = HttpURLConnection.guessContentTypeFromStream(bis);
			if (StringUtils.isNotBlank(imgType)) {
				imgType = imgType.replace("image/", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != urlconnection) {
				urlconnection.disconnect();
			}
		}
		return imgType == null ? "" : imgType.toLowerCase();
	}

	/**
	 * 沙皮狗复制惠家有商品调用，此处 惠家有复制沙皮狗商品调用
	 * 
	 * @param product
	 * @param error
	 *            如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int AddProductSharPei(PcProductinfo product, StringBuffer error) {
		product.getPcProdcutflow().setFlowStatus(SkuCommon.FlowStatusInit);
		if (null != product.getProductSkuInfoList()) {
			for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
				product.getProductSkuInfoList().get(i).setSkuCode(WebHelper.upCode(ProductService.SKUHead)); // 商品的Sku列表,skuCode
			}
		}
		product.getCategory().setCategoryCode("123456");
		MUserInfo userInfo = null;
		String manageCode = "";
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userInfo != null) {
				manageCode = userInfo.getManageCode();
				userCode = userInfo.getUserCode();
				if (manageCode == null || "".equals(manageCode)) {
					manageCode = userCode;
				}
			}
		}
		return this.AddProductTx(product, error, manageCode);
	}

	/**
	 * 获取商品毛利润(毛利率=（sku售价总和-sku成本价总和）/sku售价总和)(精确到小数点后两位，第三位四舍五入)
	 * 
	 * @param productCode
	 * @return
	 */
	public String getGrossProfit(String productCode) {
		String grossProfitStr = "0";
		if (StringUtils.isNotBlank(productCode)) {
			// MDataMap productInfoMap =
			// DbUp.upTable("pc_productinfo").oneWhere("cost_price", "", "",
			// "product_code",productCode);
			// if (null != productInfoMap && !productInfoMap.isEmpty() &&
			// StringUtils.isNotBlank(productInfoMap.get("cost_price"))) {
			List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sell_price,cost_price", "",
					"product_code='" + productCode + "'", null);
			if (null != skuInfoMap && !skuInfoMap.isEmpty()) {
				BigDecimal costPrice = BigDecimal.ZERO;
				BigDecimal skuSellPrice = BigDecimal.ZERO;
				for (MDataMap mDataMap : skuInfoMap) {
					costPrice = costPrice.add(new BigDecimal(mDataMap.get("cost_price")));
					skuSellPrice = skuSellPrice.add(new BigDecimal(mDataMap.get("sell_price")));
				}
				BigDecimal grossProfit = skuSellPrice.subtract(costPrice).multiply(new BigDecimal(100))
						.divide(skuSellPrice, 2, BigDecimal.ROUND_HALF_UP);
				grossProfitStr = grossProfit.toString();
			}
			// }
		}
		return grossProfitStr + "%";
	}

	/**
	 * 判断是否海外购商品
	 * 
	 * @param proudctCode
	 * 
	 */
	public boolean checkProductKjt(String productCode) {
		boolean flag = false;
		if (StringUtils.isNotBlank(productCode)) {
			MDataMap map = DbUp.upTable("pc_productinfo").one("product_code", productCode);
			if (MapUtils.isNotEmpty(map)) {
				// if(AppConst.MANAGE_CODE_KJT.equals(map.get("small_seller_code"))||AppConst.MANAGE_CODE_MLG.equals(map.get("small_seller_code"))
				// ||AppConst.MANAGE_CODE_QQT.equals(map.get("small_seller_code"))
				// ||AppConst.MANAGE_CODE_SYC.equals(map.get("small_seller_code"))
				// ||AppConst.MANAGE_CODE_CYGJ.equals(map.get("small_seller_code"))){
				if (new PlusServiceSeller().isKJSeller(map.get("small_seller_code"))) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 根据IC编号获取获取商品编号
	 * 
	 * @return
	 */
	public String getProductCodeForICcode(String code) {
		if (PlusHelperEvent.checkEventItem(code)) {
			PlusModelEventItemProduct eventItemtInfo = new PlusSupportEvent().upItemProductByIcCode(code);
			if (null != eventItemtInfo) {
				return eventItemtInfo.getProductCode();
			}
		}
		return code;
	}

	/**
	 * 根据IC编号获取获取sku编号
	 * 
	 * @return
	 */
	public String getSkuCodeForICcode(String code) {
		if (PlusHelperEvent.checkEventItem(code)) {
			PlusModelEventItemProduct eventItemtInfo = new PlusSupportEvent().upItemProductByIcCode(code);
			if (null != eventItemtInfo) {
				return eventItemtInfo.getSkuCode();
			}
		}
		return code;
	}

	/**
	 * 查询sku信息
	 * 
	 * @param sku_code
	 * @return
	 */
	public ProductSkuInfo getSkuInfo(String sku_code) {

		ProductSkuInfo productSkuInfo = new ProductSkuInfo();
		if (StringUtils.isBlank(sku_code)) {
			return productSkuInfo;
		}

		MDataMap dataMap = DbUp.upTable("pc_skuinfo").one("sku_code", sku_code);
		SerializeSupport<ProductSkuInfo> serializeSupport = new SerializeSupport<ProductSkuInfo>();
		serializeSupport.serialize(dataMap, productSkuInfo);

		MDataMap proMap = DbUp.upTable("pc_productinfo").oneWhere("small_seller_code", "", "product_code=:product_code",
				"product_code", productSkuInfo.getProductCode());
		productSkuInfo.setSmallSellerCode(proMap.get("small_seller_code"));
		MDataMap extMap = DbUp.upTable("pc_productinfo_ext").oneWhere("validate_flag", "", "product_code=:product_code",
				"product_code", productSkuInfo.getProductCode());
		if (extMap != null) {
			productSkuInfo.setValidateFlag(extMap.get("validate_flag"));
		}
		return productSkuInfo;
	}

	/**
	 * 根据商品编号获取商品扩展信息表
	 * 
	 * @param productCode
	 *            商品编号
	 * @return MDataMap 商品扩展信息
	 */
	public MDataMap getProductExtInfo(String productCode) {

		return DbUp.upTable("pc_productinfo_ext").one("product_code", productCode);

	}

	/**
	 * 返回导航维护集合
	 * 
	 * @param width
	 *            ios使用压缩图片的宽度
	 * @param version
	 *            上次的版本号(zid+operate_time)
	 * @param flag
	 *            是否通知前端更新 0:不更新,1:更新
	 */
	public Map<String, Object> addNavigationMaintain(int width, NavigationVersion version) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<AppNavigation> appNavigations = new ArrayList<AppNavigation>();
		map.put("nas", appNavigations);
		map.put("version", version);
		map.put("flag", "0");
		String releaseFlag = "449746250001";
		List<MDataMap> li = DbUp.upTable("fh_app_navigation").query(
				"zid,navigation_type,before_pic,after_pic,operate_time,firstpage_version,assortment_version,shoppingcart_version,mine_version,background_version,type_name,before_fontcolor,after_fontcolor",
				"",
				"release_flag=:release_flag and start_time<=now() and end_time>now() and navigation_type!='4497467900040006' and navigation_type!='4497467900040007' and navigation_type!='4497467900040008' and navigation_type!='4497467900040009' and navigation_type!='4497467900040010' and navigation_type!='4497467900040011'",
				new MDataMap("release_flag", releaseFlag), 0, 0);
		// navigation_type!='4497467900040006'
		// 2016/07/07添加的逻辑，不取广告导航图片，因为此类型的导航图片没有选中后图片这个选项，所以会报错
		if (li != null && !li.isEmpty()) {
			for (MDataMap map2 : li) {
				if (!"".equals(map2.get("firstpage_version"))
						&& version.getFirstPageVersion().equals(map2.get("firstpage_version"))) {// 如果版本号相同，则不执行更新
					continue;
				} else if (!"".equals(map2.get("assortment_version"))
						&& version.getAssortmentVersion().equals(map2.get("assortment_version"))) {
					continue;
				} else if (!"".equals(map2.get("shoppingcart_version"))
						&& version.getShoppingCartVersion().equals(map2.get("shoppingcart_version"))) {
					continue;
				} else if (!"".equals(map2.get("mine_version"))
						&& version.getMineVersion().equals(map2.get("mine_version"))) {
					continue;
				} else if (!"".equals(map2.get("background_version"))
						&& version.getMineVersion().equals(map2.get("background_version"))) {
					continue;
				} else {
					map.put("flag", "1");// 走到else则证明有需要更新的导航类型
					AppNavigation appNavigation = new AppNavigation();
					appNavigation.setNavigationType(map2.get("navigation_type"));
					appNavigation.setBefore_pic(map2.get("before_pic"));
					appNavigation.setAfter_pic(map2.get("after_pic"));
					appNavigation.setIosBeforePicHeight(getPicHeight(map2.get("before_pic"), width).getHeight());
					appNavigation.setIosAfterPicHeight(getPicHeight(map2.get("after_pic"), width).getHeight());
					appNavigation.setTypeName(map2.get("type_name"));
					appNavigation.setBeforeFontColor(map2.get("before_fontcolor"));
					appNavigation.setAfterFontColor(map2.get("after_fontcolor"));

					appNavigations.add(appNavigation);
					if ("4497467900040001".equals(map2.get("navigation_type"))) {
						version.setFirstPageVersion(map2.get("firstpage_version"));

					} else if ("4497467900040002".equals(map2.get("navigation_type"))) {
						version.setAssortmentVersion(map2.get("assortment_version"));

					} else if ("4497467900040003".equals(map2.get("navigation_type"))) {
						version.setShoppingCartVersion(map2.get("shoppingcart_version"));

					} else if ("4497467900040004".equals(map2.get("navigation_type"))) {
						version.setMineVersion(map2.get("mine_version"));

					} else if ("4497467900040005".equals(map2.get("navigation_type"))) {
						version.setBackgroundVersion(map2.get("background_version"));
					}
				}
			}
			map.put("version", version);
			map.put("nas", appNavigations);
		} else {
			map.put("flag", "1");
		}
		return map;
	}

	/**
	 * 获取成本价
	 * 
	 * @param productCode
	 * @return
	 */
	public String getSimpleProductInfo(String productCode) {
		Map<String, Object> map = DbUp.upTable("pc_productinfo").dataSqlOne(
				"select seller_code, small_seller_code, cost_price from pc_productinfo where product_code=:product_code",
				new MDataMap("product_code", productCode));
		JsonHelper helper = new JsonHelper();
		System.out.println(helper.ObjToString(map));
		return helper.ObjToString(map);
	}

	/**
	 * 批量添加sku信息
	 * 
	 * @param ligj
	 * @return
	 */
	public int addSkuBatch(PcProductinfo product, StringBuffer error) {
		synchronized (updateObj) {

			if (product.getProductCode() == null || product.getProductCode().equals("")) {

				error.append(bInfo(941901008));
				return 941901008;
			} else {
				/**
				 * 修改商户编码前缀修改，修改判断商品商户编码 2016-11-24 zhy
				 */
				String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
				boolean codeFlag = StringUtils.isNotBlank(seller_type);
				int errorCode = 1;
				if (product == null) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				if (product.getProductCode() == null || product.getProductCode().equals("")) {
					errorCode = 941901007;
					error.append(bInfo(errorCode, ""));
					return errorCode;
				}

				String uid = WebHelper.addLock(product.getProductCode(), 60);
				int ret = 0;
				if (uid.equals("")) {
					ret = 941901033;
					error.append(bInfo(ret, ""));
				} else {

					if (uid.equals("")) {
						ret = 941901033;
						error.append(bInfo(ret, ""));
					} else {

						PcProductflow pcProdcutflow = new PcProductflow();
						pcProdcutflow.setProductCode(product.getProductCode());
						pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));

						if (codeFlag
								&& !product.getSmallSellerCode().equals("SF03KJT")
								&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())) {
							pcProdcutflow.setFlowStatus(SkuCommon.ProUpaOr); // 商户后台终审通过
						} else {
							pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusInit);
						}

						// 商户后台添加商品保存库存信息
						if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
								&& StringUtils.isNotEmpty(product.getSmallSellerCode())
								&& codeFlag
								&& !product.getSmallSellerCode().equals("SF03KJT")) {
							for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
								ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
								List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
								ScStoreSkunum store = new ScStoreSkunum();
								store.setBatchCode("");
								store.setSkuCode(sku.getSkuCode());
								store.setStockNum(Long.parseLong(sku.getStockNum() + ""));
								store.setStoreCode(AppConst.THIRD_STORE_CODE);
								scStoreSkunumList.add(store);
								sku.setScStoreSkunumList(scStoreSkunumList);
							}
						}

						MUserInfo userInfo = null;
						String manageCode = "";
						String userCode = "";
						if (UserFactory.INSTANCE != null) {
							try {
								userInfo = UserFactory.INSTANCE.create();
							} catch (Exception e) {
								// TODO: handle exception
							}

							if (userInfo != null) {
								manageCode = userInfo.getManageCode();
								userCode = userInfo.getUserCode();
								if (userCode == null || "".equals(userCode)) {
									userCode = manageCode;
								}
							}
						}

						pcProdcutflow.setUpdator(userCode);
						product.setPcProdcutflow(pcProdcutflow);

						ProductChangeFlag pcf = new ProductChangeFlag();

						// 更新最大值和最小值的 minPrice 和 maxPrice
						if (product.getProductSkuInfoList() != null && !product.getProductSkuInfoList().isEmpty()) {
							int size = product.getProductSkuInfoList().size();

							BigDecimal tempMin = BigDecimal.ZERO;
							BigDecimal tempMax = BigDecimal.ZERO;
							boolean init = true;
							for (int i = 0; i < size; i++) {
								ProductSkuInfo pic = product.getProductSkuInfoList().get(i);
								if (StringUtils.isBlank(pic.getSkuCode())) {
									continue;
								}
								if (init) {
									tempMin = pic.getSellPrice();
									tempMax = pic.getSellPrice();
									init = false;
								} else {
									if (tempMin.compareTo(pic.getSellPrice()) == 1)
										tempMin = pic.getSellPrice();
									if (tempMax.compareTo(pic.getSellPrice()) == -1)
										tempMax = pic.getSellPrice();
								}
							}
							product.setMinSellPrice(tempMin);
							product.setMaxSellPrice(tempMax);
						}

						ret = addSkuBatchTx(product, error, userCode, pcf);

						if (ret == 1) {
							try {
								// 更新最大值和最小值的 minPrice 和 maxPrice
								DbUp.upTable("pc_productinfo").dataExec(
										"UPDATE pc_productinfo SET min_sell_price=(SELECT MIN(sell_price) FROM pc_skuinfo  where product_code='"
												+ product.getProductCode() + "') where product_code='"
												+ product.getProductCode() + "';",
										null);
								DbUp.upTable("pc_productinfo").dataExec(
										"UPDATE pc_productinfo SET max_sell_price=(SELECT MAX(sell_price) FROM pc_skuinfo  where product_code='"
												+ product.getProductCode() + "') where product_code='"
												+ product.getProductCode() + "';",
										null);

							} catch (Exception ex) {
								ex.printStackTrace();
							}
							try {
								PlusHelperNotice.onChangeProductInfo(product.getProductCode());
								// 触发消息队列
								ProductJmsSupport pjs = new ProductJmsSupport();
								pjs.onChangeForProductChangeAll(product.getProductCode());
							} catch (Exception ex) {

							}

						}
					}
				}
				WebHelper.unLock(uid);
				return ret;
			}
		}
	}

	public int addSkuBatchTx(PcProductinfo pc, StringBuffer error, String manageCode, ProductChangeFlag pcf) {

		RootResult rr = new RootResult();
		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		try {
			txs.addSkuBatch(pc, rr, manageCode, pcf);
		} catch (Exception e) {
			e.printStackTrace();
			rr.setResultCode(941901049);
			rr.setResultMessage(bInfo(941901049, e.getMessage()));
		}
		error.append(rr.getResultMessage());
		return rr.getResultCode();

	}

	/**
	 * 根据流程号和流程类型查询当前最新一条备注信息； 使用页面:商品价格审核页面
	 * 
	 * @param flowCode
	 * @param flowType
	 * @return
	 */
	public String getFlowRemark(String flowCode, String flowType) {
		MDataMap dataMap = new MDataMap();
		dataMap.put("flow_code", flowCode);
		dataMap.put("flow_type", flowType);
		String sql = "SELECT flow_remark FROM sc_flow_history sc WHERE sc.flow_code =:flow_code AND sc.flow_type =:flow_type ORDER BY sc.create_time DESC LIMIT 1";
		Map<String, Object> map = DbUp.upTable("sc_flow_history").dataSqlOne(sql, dataMap);
		String flowRemark = String.valueOf(map.get("flow_remark"));
		return flowRemark;
	}

	/**
	 * 根据流程号和流程类型查询当前最新一条备注信息； 使用页面:商品价格审核页面
	 * 
	 * @param flowCode
	 * @param flowType
	 * @return
	 */
	public String getMainPicUrl(String productCode) {
		String picUrl = "";
		if (StringUtils.isNotBlank(productCode)) {
			MDataMap dataMap = DbUp.upTable("pc_productinfo").oneWhere("mainpic_url", "", "", "product_code",
					productCode);
			if (null != dataMap) {
				picUrl = dataMap.get("mainpic_url");
			}
		}
		return picUrl;
	}

	/**
	 * 
	 * 方法: isCrossBorder <br>
	 * 描述: 判断当前商户是否为跨境商户 <br>
	 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
	 * 时间: 2016年6月24日 下午2:32:34
	 * 
	 * @param seller_code
	 * @return
	 */
	private boolean isCrossBorder(String seller_code) {
		boolean flag = false;
		if (StringUtils.isNotBlank(seller_code)) {
			Map<String, Object> map = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
					"select uc_seller_type from usercenter.uc_seller_info_extend where small_seller_code =:seller_code ",
					new MDataMap("seller_code", seller_code));
			// 判断商户是否为跨境商户，跨境直邮 2016-07-26 zhy
			if (map != null && map.get("uc_seller_type") != null
					&& ("4497478100050002".equals(map.get("uc_seller_type").toString())
							|| "4497478100050003".equals(map.get("uc_seller_type").toString()))) {
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 压缩图片之前进行匹配系统指定的宽度
	 * 获取传入宽度在哪个系统指定的区间中规定小的宽度作为宽度进行压缩，如果传入宽度比系统最小指定宽度还小。则取最小宽度
	 * 对  getPicInfoOprBigForMulti() 方法的上一层封装
	 * 
	 * 好处:  系统可以控制压的图片宽度只有指定的几种
	 * @param maxWidth 
	 * @param picUrlArr
	 * @param picType
	 * @return
	 */
	public List<PicInfo> compressImage(int maxWidth, List<String> picUrlArr, String picType) {
		return compressImage(false, maxWidth, picUrlArr, picType);
	}
	
	/**
	 * 压缩图片之前进行匹配系统指定的宽度
	 * 获取传入宽度在哪个系统指定的区间中规定小的宽度作为宽度进行压缩，如果传入宽度比系统最小指定宽度还小。则取最小宽度
	 * 对  getPicInfoOprBigForMulti() 方法的上一层封装
	 * 
	 * 好处:  系统可以控制压的图片宽度只有指定的几种
	 * @param isWebPcPic  是否为上列表图，如果为true，则将压缩的宽度*0.6进行实际压缩(redis 存储的key为  “宽+webTemplete”)
	 * @param maxWidth 
	 * @param picUrlArr
	 * @param picType
	 * @return
	 */
	public List<PicInfo> compressImage(boolean isWebPcPic, int maxWidth, List<String> picUrlArr, String picType) {
		
		/**
		 * 压缩图片之前，需要取最接口该宽度的尺寸进行压缩。防止压缩的种类过多而无法在上传图片时先进行图片的压缩
		 */
//		String sysWidths = bConfig("productcenter.imageWidth");//取规定尺寸的宽度
//		String[] sysWidthArr = sysWidths.split(",");
//		List<Integer> widthList = new ArrayList<Integer>();
//		for (String wid : sysWidthArr) {
//			widthList.add(Integer.valueOf(wid));
//		}
//		if(widthList.size() > 0) {
//			//从小到大进行排序
//			Collections.sort(widthList, new Comparator<Integer>() {
//				public int compare(Integer widthOne, Integer widthTwo) {
//					return widthOne.compareTo(widthTwo);
//				}
//			});
//		}
//		
//		
//		for (int i = 0; i < widthList.size(); i++) {
//			if(i == widthList.size() -1 ) {
//				//如果为最后一位元素，则直接获取
//				maxWidth = Integer.valueOf(widthList.get(i));
//			}
//			
//			if(maxWidth <= Integer.valueOf(widthList.get(i))) {
//				maxWidth = Integer.valueOf(widthList.get(i));
//				break;
//			}
//		}
		List<PicInfo> picInfoOprBigForMulti = new ArrayList<PicInfo>();
		if(isWebPcPic) {
			picInfoOprBigForMulti = getPicInfoOprBigForMulti("", BigDecimal.valueOf(maxWidth*0.6).setScale(0,BigDecimal.ROUND_FLOOR).intValue(),  picUrlArr,  picType);
		} else {
			picInfoOprBigForMulti = getPicInfoOprBigForMulti("", maxWidth,  picUrlArr,  picType);
		}
		
		return picInfoOprBigForMulti;
	}
	
	/**
	 * 查询自营商品列表，根据商品分类查询<br>
	 * 
	 */
	public MPageData upChartDataByProductCate(ControlPage cp){
		String productCate = cp.getReqMap().get("zw_f_product_cate");
		
		// 设置符合商品编号的活动条件
		if(StringUtils.isNotBlank(productCate)){			
			cp.getReqMap().put("sub_query", " product_code in (SELECT uspr.product_code FROM usercenter.uc_sellercategory_product_relation uspr JOIN usercenter.uc_sellercategory usc on LEFT(uspr.category_code,16) = usc.category_code WHERE usc.parent_code = "+productCate+")");
		}
		
		return cp.upChartData();
	}
	
	/**
	 * 设置商品活动标签样式
	 * @param tagInfoList
	 * @param style
	 * @param tagName
	 */
	public void addTagInfo(List<TagInfo> tagInfoList, TagInfo.Style style, String... tagNames) {
		if(tagNames == null) return;
		for(String name : tagNames) {
			if(StringUtils.isNotBlank(name)) {
				tagInfoList.add(new TagInfo(name, style.toString()));
			}
		}
	}
	
	/**
	 * 根据SKU编号获取商品活动标签（秒杀、闪购、拼团、特价、会员日、满减、券、赠品）
	 * @param productCode
	 * @return List<String> tagList;
	 */
	public List<String> getTagListBySkuCode(String skuCode,String memberCode,String channelId){
		Map<String, String> tagMap = new HashMap<String, String>();
		LoadEventInfo loadEventInfo = new LoadEventInfo();
		String eventCodes = "";
		PlusSupportProduct psp = new PlusSupportProduct();
		//PlusModelSkuInfo skuInfo = psp.upSkuInfoBySkuCode(skuCode,memberCode,"",1);//刷去当前活动，刷两次是因为刷不到
		PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
		skuQuery.setCode(skuCode);
		skuQuery.setMemberCode(memberCode);
		skuQuery.setChannelId(channelId);
		skuQuery.setIsPurchase(1);
		PlusModelSkuInfo skuInfo = psp.upSkuInfo(skuQuery).getSkus().get(0);
		
		String productCode = skuInfo.getProductCode();
		String eventCodeStr = skuInfo.getEventCode();//获取所有活动信息。
		if(!StringUtils.isEmpty(eventCodeStr)) {
			eventCodes += eventCodeStr+",";
		}
		Map<String, String> eventTypeCodeMap = new HashMap<String, String>();
		String eventCodeArr[] = eventCodes.split(",");
		for(int i = 0;i<eventCodeArr.length;i++) {
			String eventCode = eventCodeArr[i];
			if(!StringUtils.isEmpty(eventCode)) {
				PlusModelEventQuery query = new PlusModelEventQuery();
				query.setCode(eventCode);
				PlusModelEventInfo eventInfo = loadEventInfo.upInfoByCode(query);
				String eventType = eventInfo.getEventType();
				//（秒杀：4497472600010001）、（闪购：4497472600010005）、（拼团：4497472600010024）、（特价：4497472600010002）、（会员日：4497472600010014）、（满减：4497472600010008）才会入List
				eventTypeCodeMap.put(eventType, eventCode);
			}
		}
		//判断满减活动
		PlusModelEventSale eventSale = new PlusSupportEvent().upEventSalueByMangeCode("SI2003",channelId);
		/**添加满减信息**/
		List<PlusModelFullCutMessage> sale = new PlusServiceSale().getEventMessage(productCode, eventSale,memberCode);
		for(PlusModelFullCutMessage fullCut : sale) {
			String eventType = fullCut.getEventType();
			if("4497472600010008".equals(eventType)) {
				eventTypeCodeMap.put(eventType, "");
			}
		}
		
		for(String eventTypeCode : eventTypeCodeMap.keySet()) {
			String eventTypeName = eventTypeMap.get(eventTypeCode);
			if(eventTypeName == null) {
				if("4497472600010030".equals(skuInfo.getEventType())) {
					//打折促销 按活动 取配置标签
					PlusModelEventQuery pl = new PlusModelEventQuery();
					pl.setCode(skuInfo.getEventCode());
					PlusModelEventInfo plusModelEventInfo = loadEventInfo.upInfoByCode(pl);
					eventTypeName = plusModelEventInfo.getEventTipName();
				}else {
					MDataMap map = DbUp.upTable("sc_event_type").one("type_code", eventTypeCode);
					if(map != null) {
						eventTypeName = map.get("type_name");
					}
					eventTypeMap.put(eventTypeCode, StringUtils.trimToEmpty(eventTypeName));
				}
			}
			if(StringUtils.isNotBlank(eventTypeName)) {
				tagMap.put(eventTypeCode,eventTypeName);
			}
		}
		
//		String eventTypeStr = StringUtils.join(eventTypeCodeList, ",");
//		if(!StringUtils.isEmpty(eventTypeStr)&&!eventTypeStr.contains("4497472600010006")) {//判空以及不参与内购
//			if(eventTypeStr.contains("4497472600010018")) {//有会员日折扣活动
//				tagList.add("会员日");
//			}
//			if(eventTypeStr.contains("4497472600010001")&&!eventTypeStr.contains("4497472600010018")) {//有秒杀活动,没有会员日的时候
//				tagList.add("秒杀");
//			}
//			if(eventTypeStr.contains("4497472600010005")&&!eventTypeStr.contains("4497472600010018")) {//有闪购活动
//				tagList.add("闪购");
//			}
//			if(eventTypeStr.contains("4497472600010019")&&!eventTypeStr.contains("4497472600010018")) {//有闪购活动
//				tagList.add("小程序-闪购");
//			}
//			if(eventTypeStr.contains("4497472600010024")&&!eventTypeStr.contains("4497472600010018")) {//有拼团活动
//				tagList.add("拼团");
//			}
//			if(eventTypeStr.contains("4497472600010002")&&!eventTypeStr.contains("4497472600010018")) {//有特价活动
//				tagList.add("特价");
//			}
//			if(eventTypeStr.contains("4497472600010008")) {//有满减活动
//				tagList.add("满减");
//			}
//		}
		vertifyTagList(tagMap,"SI2003",productCode);
		if(tagMap.keySet().size()<4&&!eventTypeCodeMap.keySet().contains("4497472600010006")) {//前端最多展示三個標簽，如果活动少于三条，还需要判断是否内购 需要查询是否有券
			/*
			LoadCouponListForProduct load = new LoadCouponListForProduct();
			CouponListQuery tQuery = new CouponListQuery();
			tQuery.setMemberCode(memberCode);
			tQuery.setCode(productCode);
			PlusModelCouponListInfo info = load.upInfoByCode(tQuery);
			if(info !=null&&info.getCouponList()!=null && info.getCouponList().size()>0) {
				tagList.add("券");
			}
			*/
		}
		
		List<String> tagList = new ArrayList<String>(tagMap.values());
		if(tagList.size()<4) {//加券后如果还是少于三个，需要判断是否有赠品，如果有，添加赠品标识
			PlusModelGitfSkuInfoList giftList = psp.getProductGiftsDetailList(productCode);
			List<PlusModelGiftSkuinfo> list = giftList.getGiftSkuinfos();
			if(list.size()>0) {//有赠品
				tagList.add("赠品");
			}
		}
		return tagList;
	}
	
	/**
	 * 根据商品编号获取商品活动标签（秒杀、闪购、拼团、特价、会员日、满减、券、赠品）
	 * @param productCode
	 * @return List<String> tagList;
	 */
	public List<String> getTagListByProductCode(String productCode,String memberCode,String channelId){
		Map<String, String> tagMap = new HashMap<String, String>();
		Map<String, String> eventTypeCodeMap = new HashMap<String, String>();
		
		PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
		skuQuery.setCode(productCode);
		skuQuery.setMemberCode(memberCode);
		skuQuery.setChannelId(channelId);
		PlusModelSkuInfo skuInfo = priceService.getProductMinPriceSkuInfo(skuQuery, true).get(productCode);
	
		if(skuInfo == null) {
			return new ArrayList<String>();
		}
		
		// 商品参与的活动类型,一个商品编号只展示一个活动
		String skuEventType = null;
		if(skuInfo != null && StringUtils.isNotBlank(skuInfo.getEventCode()) && StringUtils.isNotBlank(skuInfo.getEventType())) {
			skuEventType = skuInfo.getEventType();
			eventTypeCodeMap.put(skuInfo.getEventType(),skuInfo.getEventCode());
		}
		
		//判断满减活动
		PlusModelEventSale eventSale = new PlusSupportEvent().upEventSalueByMangeCode("SI2003",channelId);
		/**添加满减信息**/
		List<PlusModelFullCutMessage> sale = new PlusServiceSale().getEventMessage(productCode, eventSale,memberCode);
		
		// 返回当前商品可以参与的满减活动
		PlusModelEventFull eventFull = null;
		for(PlusModelFullCutMessage cut : sale) {
			for(PlusModelEventFull event : eventSale.getEventFulls()) {
				if(cut.getEventCode().equals(event.getEventCode())) {
					eventFull = event;
				}
			}
		}
		
		// 判断满减是否可添加活动
		if(eventFull != null) {
			if(StringUtils.isBlank(skuEventType)
					|| eventFull.getSuprapositionType().contains(skuEventType)){
				eventTypeCodeMap.put("4497472600010008","");
			} 
		}
		
		for(String eventTypeCode : eventTypeCodeMap.keySet()) {
			String eventTypeName = eventTypeMap.get(eventTypeCode);
			if(eventTypeName == null) {
				if("4497472600010030".equals(skuInfo.getEventType())) {
					//打折促销 按活动 取配置标签
					PlusModelEventQuery pl = new PlusModelEventQuery();
					pl.setCode(skuInfo.getEventCode());
					PlusModelEventInfo plusModelEventInfo = new LoadEventInfo().upInfoByCode(pl);
					eventTypeName = plusModelEventInfo.getEventTipName();
				}else {
					MDataMap map = DbUp.upTable("sc_event_type").one("type_code", eventTypeCode);
					if(map != null) {
						eventTypeName = map.get("type_name");
					}
					eventTypeMap.put(eventTypeCode, StringUtils.trimToEmpty(eventTypeName));
				}
			}
			
			if(StringUtils.isNotBlank(eventTypeName)) {
				tagMap.put(eventTypeCode,eventTypeName);
			}
		}
		
		if(!eventTypeCodeMap.keySet().contains("4497472600010006")) {//前端最多展示三個標簽，如果活动少于三条，还需要判断是否内购 需要查询是否有券
			/*
			PlusModelShowCouponType showCouponType = loadShowCouponType.upInfoByCode(new PlusModelQuery("SI2003"));
			// 如果有优惠券设置了详情页展示再走具体查询优惠券的逻辑，减少不必要的调用
			if(!showCouponType.getCouponTypeList().isEmpty()) {
				LoadCouponListForProduct load = new LoadCouponListForProduct();
				CouponListQuery tQuery = new CouponListQuery();
				tQuery.setMemberCode(memberCode);
				tQuery.setCode(productCode);
				PlusModelCouponListInfo info = load.upInfoByCode(tQuery);
				if(info !=null&&info.getCouponList()!=null && info.getCouponList().size()>0) {
					String pl=bConfig("familyhas.no_threshold");
					for (ModelCouponForGetInfo model : info.getCouponList()) {
						String limitCondition = model.getLimitCondition();
						String channelLimit = model.getChannelLimit();
						String channelCodes = model.getChannelCodes();	
						if(StringUtils.equals(limitCondition, pl)||StringUtils.equals(channelLimit, "4497471600070001")
		            			||StringUtils.contains(channelCodes, channelId)) {
							tagList.add("券");
							break;
						}
						
					}
				}
			}
			*/
		}
		
		List<String> tagList = new ArrayList<String>(tagMap.values());
		if(tagList.size()<3) {//加券后如果还是少于三个，需要判断是否有赠品，如果有，添加赠品标识
			PlusModelGitfSkuInfoList giftList = new PlusSupportProduct().getProductGiftsDetailList(productCode);
			List<PlusModelGiftSkuinfo> list = giftList.getGiftSkuinfos();
			if(list.size()>0) {//有赠品
				tagList.add("赠品");
			}
		}
		return tagList;
	}
	
	/**
	 * 根据商品编号获取商品活动标签（秒杀、闪购、拼团、特价、会员日、满减、券、赠品）
	 * @param productCode
	 * @return List<TagInfo> tagList;
	 */
	public List<TagInfo> getProductTagInfoList(String productCode,String memberCode,String channelId){
		Map<String, String> tagMap = new HashMap<String, String>();
		Map<String, String> eventTypeCodeMap = new HashMap<String, String>(); //促销活动类型，促销活动编号
		
		PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
		skuQuery.setCode(productCode);
		skuQuery.setMemberCode(memberCode);
		skuQuery.setChannelId(channelId);
		PlusModelSkuInfo skuInfo = priceService.getProductMinPriceSkuInfo(skuQuery, true).get(productCode);
	
		if(skuInfo == null) {
			return new ArrayList<TagInfo>();
		}
		
		// 商品参与的活动类型,一个商品编号只展示一个活动
		String skuEventType = null;
		if(skuInfo != null && StringUtils.isNotBlank(skuInfo.getEventCode()) && StringUtils.isNotBlank(skuInfo.getEventType())) {
			skuEventType = skuInfo.getEventType();
			eventTypeCodeMap.put(skuInfo.getEventType(), skuInfo.getEventCode());
		}
		
		//判断满减活动
		PlusModelEventSale eventSale = new PlusSupportEvent().upEventSalueByMangeCode("SI2003", channelId);
		/**添加满减信息**/
		List<PlusModelFullCutMessage> sale = new PlusServiceSale().getEventMessage(productCode, eventSale,memberCode);
		
		// 返回当前商品可以参与的满减活动
		PlusModelEventFull eventFull = null;
		for(PlusModelFullCutMessage cut : sale) {
			for(PlusModelEventFull event : eventSale.getEventFulls()) {
				if(cut.getEventCode().equals(event.getEventCode())) {
					eventFull = event;
				}
			}
		}
		
		// 判断满减是否可添加活动
		if(eventFull != null) {
			if(StringUtils.isBlank(skuEventType)
					|| eventFull.getSuprapositionType().contains(skuEventType)){
				eventTypeCodeMap.put("4497472600010008","");
			} 
		}
		
		for(String eventTypeCode : eventTypeCodeMap.keySet()) {
			String eventTypeName = eventTypeMap.get(eventTypeCode);
			if(eventTypeName == null) {
				if("4497472600010030".equals(skuInfo.getEventType())) {
					//打折促销 按活动 取配置标签
					PlusModelEventQuery pl = new PlusModelEventQuery();
					pl.setCode(skuInfo.getEventCode());
					PlusModelEventInfo plusModelEventInfo = new LoadEventInfo().upInfoByCode(pl);
					eventTypeName = plusModelEventInfo.getEventTipName();
				}else {
					MDataMap map = DbUp.upTable("sc_event_type").one("type_code", eventTypeCode);
					if(map != null) {
						eventTypeName = map.get("type_name");
					}
					eventTypeMap.put(eventTypeCode, StringUtils.trimToEmpty(eventTypeName));
				}
			}
			
			if(StringUtils.isNotBlank(eventTypeName)) {
				tagMap.put(eventTypeCode,eventTypeName);
			}
		}
		
		if(!eventTypeCodeMap.keySet().contains("4497472600010006")) {//前端最多展示三個標簽，如果活动少于三条，还需要判断是否内购 需要查询是否有券
			/*
			PlusModelShowCouponType showCouponType = loadShowCouponType.upInfoByCode(new PlusModelQuery("SI2003"));
			// 如果有优惠券设置了详情页展示再走具体查询优惠券的逻辑，减少不必要的调用
			if(!showCouponType.getCouponTypeList().isEmpty()) {
				LoadCouponListForProduct load = new LoadCouponListForProduct();
				CouponListQuery tQuery = new CouponListQuery();
				tQuery.setMemberCode(memberCode);
				tQuery.setCode(productCode);
				tQuery.setChannelId(channelId);
				PlusModelCouponListInfo info = load.upInfoByCode(tQuery);
				if(info !=null&&info.getCouponList()!=null && info.getCouponList().size()>0) {
					tagList.add("券");
				}
			}
			*/
		}
		
		List<String> tagList = new ArrayList<String>(tagMap.values());
		if(tagList.size()<3) {//加券后如果还是少于三个，需要判断是否有赠品，如果有，添加赠品标识
			PlusModelGitfSkuInfoList giftList = new PlusSupportProduct().getProductGiftsDetailList(productCode);
			List<PlusModelGiftSkuinfo> list = giftList.getGiftSkuinfos();
			if(list.size()>0) {//有赠品
				tagList.add("赠品");
			}
		}
		
		List<TagInfo> tagInfoList = new ArrayList<TagInfo>();
		addTagInfo(tagInfoList, TagInfo.Style.Normal, tagList.toArray(new String[0]));
		
		// 橙意会员卡折扣
		PlusModelEventInfoPlus eventPlus = plusServiceEventPlus.getEventInfoPlusUseCache();
		if(plusServiceEventPlus.checkEventLimit(eventPlus, productCode, new ArrayList<String>(eventTypeCodeMap.keySet()))) {
			addTagInfo(tagInfoList, TagInfo.Style.VipCard, eventPlus.getShowName());
		}
		
		return tagInfoList;
	}
	
	/**
	 * 对标签组进行叠加筛选
	 * @param tagList 活动类型编号，标签名称
	 * @param sellerCode
	 * @param productCode
	 */
	private void vertifyTagList(Map<String, String> tagMap ,String sellerCode,String productCode) {

		if((tagMap.values().contains("满减")&&tagMap.size()==1)||!tagMap.values().contains("满减")){}
		else if(tagMap.values().contains("满减")&&tagMap.size()>1){
			PlusModelSaleQuery tQuery = new PlusModelSaleQuery();
			tQuery.setCode(sellerCode);
			PlusModelEventSale eventSale = new LoadEventSale().upInfoByCode(tQuery);
			String now = FormatHelper.upDateTime();
			List<PlusModelEventFull> delList = new LinkedList<PlusModelEventFull>();
			
			boolean ifShowMJ = false;
			//允许叠加的促销活动类型
			String[] maybeSupraositionType = {"4497472600010001","4497472600010002","4497472600010004","4497472600010005","4497472600010006",
					"4497472600010018","4497472600010019","4497472600010021","4497472600010024","4497472600010030"};
			for (String tagCode : tagMap.keySet()) {
				if(ifShowMJ) break;
				if(!Arrays.asList(maybeSupraositionType).contains(tagCode)) continue;
				for(int i = eventSale.getEventFulls().size() - 1; i >= 0; --i) {
					PlusModelEventFull currentEvent = eventSale.getEventFulls().get(i);
					if(DateUtil.compareTime(now, currentEvent.getEndTime()) >= 0 || DateUtil.compareTime(currentEvent.getBeginTime(), now) >= 0 ) {
						delList.add(currentEvent);
					}

					if(StringUtils.isBlank(currentEvent.getSuprapositionType())||!currentEvent.getSuprapositionType().contains(tagCode)) {
						delList.add(currentEvent);
					}
					
				}
				
				eventSale.getEventFulls().removeAll(delList);
				PlusModelEventFull result = null;
				
				for(PlusModelEventFull eventFull : eventSale.getEventFulls()) {
					String limitType = eventFull.getRuleSku().getLimitType();
					List<String> limitcodes = eventFull.getRuleSku().getLimitCode();
					if(limitType.equals("4497476400020001")) {
						//不限
						result = eventFull;
					} else if(limitType.equals("4497476400020002")) {
						//仅包含,双重break,优先取
						boolean found = false;
						if(limitcodes != null) {
							for(String code : limitcodes) {
								if(productCode.equals(code)) {
									result = eventFull;
									found = true;
									break;
								}
							}
						}
						if(found) break;
					} else if(limitType.equals("4497476400020003")) {
						//以下除外
						boolean found = false;
						if(limitcodes != null) {
							for(String code : limitcodes) {
								if(productCode.equals(code)) {
									found = true;
									break;
								}
							}
						}
						if(!found) {
							result = eventFull;
						}
					}
				}
				if(result!=null) {
					//item.getTagList().remove(tagMap.get(tagCode));
					ifShowMJ = true;
				break;
				}
				
			  }	
			
			if(!ifShowMJ) {tagMap.remove("4497472600010008");}
			}
		}
	
	/**
	 * 根据商品id获取当前售价区间
	 * @param productCode
	 * @return
	 */
	public String getNowPriceByProductCode(String productCode) {
		PlusSupportProduct support = new PlusSupportProduct();
		List<BigDecimal> sellPriceList = new ArrayList<BigDecimal>();
		//根据商品id查询所有skuCode
		List<MDataMap> skuCodeList = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", "product_code='" + productCode + "'", new MDataMap());
		for (MDataMap skuCodeMap : skuCodeList) {
			//根据skuCode和当前时间查询所有价格
			//PlusModelSkuPriceFlow skuPriceChange = support.getSkuPriceChange(skuCodeMap.get("sku_code"),new Date());
			PlusModelSkuInfo skuInfo = support.upSkuInfoBySkuCode(skuCodeMap.get("sku_code"));
			if(null != skuInfo) {				
				//获取当前售价存入集合
				BigDecimal sellPrice = skuInfo.getSellPrice();
				sellPriceList.add(sellPrice);
			}
		}
		String sellPrice = "";
		if(sellPriceList.size() > 0) {		
			BigDecimal max = sellPriceList.get(0);
			BigDecimal min = sellPriceList.get(0);
			for(int i = 0; i < sellPriceList.size(); i++) {
				if(min.compareTo(sellPriceList.get(i)) > 0){
					min = sellPriceList.get(i);
				}
				if(max.compareTo(sellPriceList.get(i)) < 0) {
					max = sellPriceList.get(i);				
				}
			}
			sellPrice = min + "--" + max;
		}
		return sellPrice;
	}
	
	/**
	 * 根据skuCode获取活动售价
	 * @param skuCode
	 * @return
	 */
	public String getSellPriceBySkuCode(String skuCode) {
		PlusSupportProduct support = new PlusSupportProduct();
		//PlusModelSkuPriceFlow skuPriceChange = support.getSkuPriceChange(skuCode,new Date());
		PlusModelSkuInfo skuInfo = support.upSkuInfoBySkuCode(skuCode);
		String sellPrice = "";
		if(null != skuInfo) {
			//获取当前售价存入集合
			sellPrice = skuInfo.getSellPrice()+"";
		}
		return sellPrice;
	}
	
	/**
	 * 根据skuCode获取活动成本价
	 * @param skuCode
	 * @return
	 */
	public String getCostPriceBySkuCode(String skuCode) {
		PlusSupportProduct support = new PlusSupportProduct();
		PlusModelSkuPriceFlow skuPriceChange = support.getSkuPriceChange(skuCode,new Date());
		String costPrice = "";
		if(null != skuPriceChange) {				
			//获取当前售价存入集合
			costPrice = skuPriceChange.getCostPrice()+"";
		}
		return costPrice;
	}
	
	/**
	 * 校验是否有商品没有售后地址
	 * @param smallSellerCode
	 * @return
	 */
	public int checkProductAddress(String smallSellerCode) {
		int count = 0;
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("small_seller_code", smallSellerCode);
		mWhereMap.put("product_status", "4497153900060002");
		mWhereMap.put("after_sale_address_uid", "");
		count = DbUp.upTable("pc_productinfo").dataCount("small_seller_code = :small_seller_code and product_status = :product_status and after_sale_address_uid =:after_sale_address_uid", mWhereMap);
		
		return count;
	}
	
	/**
	 * 根据商品编号获取限购地区模板
	 * @param productCode 5.6.9.1
	 * @return
	 */
	public String getAreaTemplateByProductCode(String productCode) {
		String area_template_name = "待维护";
		String area_template = DbUp.upTable("pc_productinfo").dataGet("area_template",
				"product_code=:product_code", new MDataMap("product_code", productCode)) + "";
		if(StringUtils.isNotBlank(area_template) && !area_template.equals("null")) {
			area_template_name = DbUp.upTable("sc_area_template").dataGet("template_name",
					"template_code=:template_code", new MDataMap("template_code", area_template)) + "";
			
		}
		return area_template_name;
	}
	
	
	
	
	/**
	 * 根据商品编号获取商户类型
	 * @param productCode
	 * @return
	 */
	public String getSellerTypeByProductCode(String productCode) {
		String uc_seller_type = "";
		String type_name = "";

		String small_seller_code = DbUp.upTable("pc_productinfo").dataGet("small_seller_code",
				"product_code=:product_code", new MDataMap("product_code", productCode)) + "";
		if ("SI2003".equals(small_seller_code)) {
			uc_seller_type = "4497478100050000";
			type_name = "LD";
		} else {
			uc_seller_type = DbUp.upTable("uc_seller_info_extend").dataGet("uc_seller_type",
					"small_seller_code=:small_seller_code", new MDataMap("small_seller_code", small_seller_code)) + "";
			MDataMap define = DbUp.upTable("sc_define").one("define_code", uc_seller_type, "parent_code",
					"449747810005");
			type_name = define.get("define_name");
		}

		return type_name;
	}
	
	/**
	 * 根据商品编号获取关联分类、是否显示LD品
	 * @param productCode
	 * @return
	 */
	public MDataMap getProductCorrelation(String productCode) {
		MDataMap result = new MDataMap();
		List<MDataMap> queryByWhere = DbUp.upTable("pc_product_correlation").queryByWhere("product_code", productCode);
		if(null != queryByWhere && !queryByWhere.isEmpty()) {
			result = queryByWhere.get(0);
		}
		return result;
	}
	
	/**
	 * 根据商品编号 获取关联分类
	 * @param productCode
	 * @return
	 */
	public Map<String, Object> getCorrelationByProduct(String productCode){
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> codeList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		
		MDataMap correlation = getProductCorrelation(productCode);
		String level4s = correlation.get("correlation_category");
		if(StringUtils.isNotEmpty(level4s)) {
			String[] level4Arr = level4s.split(",");
			for (String level4 : level4Arr) {
				String categoryCode = "";
				String categoryName = "";
				MDataMap level4Map = DbUp.upTable("uc_sellercategory").one("category_code", level4);
				MDataMap level3Map = DbUp.upTable("uc_sellercategory").one("category_code", level4Map.get("parent_code"));
				MDataMap level2Map = DbUp.upTable("uc_sellercategory").one("category_code", level3Map.get("parent_code"));
				categoryCode = level2Map.get("category_code") + ">" + level3Map.get("category_code") + ">" + level4Map.get("category_code");
				categoryName = level2Map.get("category_name") + ">" + level3Map.get("category_name") + ">" + level4Map.get("category_name");
				codeList.add(categoryCode);
				nameList.add(categoryName);
			}
		}
		result.put("code", codeList);
		result.put("name", nameList);
		result.put("show_ld", correlation.get("show_ld"));
		return result;
	}
	
	/**
	 * 【商品推荐关联】 无奈之举这样玩吧
	 * @param cp
	 * @return
	 */
	public MPageData specialUpChartData(ControlPage cp){
		String uc_seller_type = cp.getReqMap().get("uc_seller_type");
		String correlation_status = cp.getReqMap().get("correlation_status");
		
		if(StringUtils.isNotEmpty(uc_seller_type) || StringUtils.isNotEmpty(correlation_status)) {
			List<Object> zidList = new ArrayList<Object>();
			String sql = "SELECT pi.zid,pi.product_code,pc.correlation_category "
					+ "FROM productcenter.pc_productinfo pi "
					+ "LEFT JOIN usercenter.uc_seller_info_extend sie ON pi.small_seller_code=sie.small_seller_code "
					+ "LEFT JOIN productcenter.pc_product_correlation pc ON pi.product_code=pc.product_code ";
			MDataMap sqlParams = new MDataMap();
			//商户类型
			if(StringUtils.isNotEmpty(uc_seller_type)) {
				if("4497478100050000".equals(uc_seller_type)) {
					sql += " WHERE pi.small_seller_code='SI2003' ";
				}else {
					sql += " WHERE sie.uc_seller_type=:uc_seller_type ";
					sqlParams.put("uc_seller_type", uc_seller_type);
				}
			}
			
			//是否关联
			if(StringUtils.isNotEmpty(correlation_status)) {
				if(sql.contains("WHERE")) {
					sql += " AND ";
				}else {
					sql += " WHERE ";
				}
				sql += " (pc.correlation_category is ";
				if("1".equals(correlation_status)) {
					sql += " not null AND pc.correlation_category != '') ";
				} else {
					sql += " null OR pc.correlation_category = '') ";
				}
			}
			
			List<Map<String, Object>> dataSqlList = DbUp.upTable("pc_productinfo").dataSqlList(sql, sqlParams);
			for(Map<String, Object> map : dataSqlList) {
				zidList.add(map.get("zid"));
			}
			if(zidList.isEmpty()){
				zidList.add("0");
			}
			
			cp.getReqMap().put("sub_query", "zid IN ("+StringUtils.join(zidList,",")+")");
		}
		return cp.upChartData();
	}
	
	/**
	 * 根据productCode查询商品属性
	 * @param productCode
	 * @return
	 */
	public List<CategoryProperties> getPropertiesByProductCode (String productCode) {
		List<CategoryProperties> list = new ArrayList<CategoryProperties>();
		if(null != productCode && !"".equals(productCode)) {
			MDataMap prodMap = DbUp.upTable("pc_productinfo").one("product_code",productCode);
			if(null == prodMap) {
				MDataMap prodMap2 = DbUp.upTable("pc_productinfo").one("uid",productCode);
				if(null != prodMap2) {
					productCode = prodMap2.get("product_code");
				}else {
					return list;
				}
			}
			// 查询该商品所处分类具有的所有属性(可能有多个分类)
			// MDataMap sellercategory = DbUp.upTable("uc_sellercategory_product_relation").one("product_code",productCode,"seller_code","SI2003");
			List<Map<String, Object>> sellercategoryList = DbUp.upTable("uc_sellercategory_product_relation").dataSqlList("SELECT * FROM uc_sellercategory_product_relation WHERE product_code = '"+productCode+"' AND seller_code = 'SI2003'", new MDataMap());
			if(null != sellercategoryList && sellercategoryList.size() > 0) {
				for (Map<String, Object> sellercategory : sellercategoryList) {
					
					if(null == sellercategory) {
						return list;
					}
					String thirdCode = MapUtils.getString(sellercategory, "category_code");
					String sql1 = "SELECT s.* FROM uc_sellercategory s WHERE s.seller_code = 'SI2003' AND s.category_code = " + 
							"(SELECT u.parent_code FROM uc_sellercategory u WHERE u.category_code = '"+thirdCode+"' AND u.seller_code = 'SI2003')"; 
					Map<String, Object> parentcategory = DbUp.upTable("uc_sellercategory").dataSqlOne(sql1, new MDataMap());
					if(null == parentcategory) {
						return list;
					}
					String secondCode = MapUtils.getString(parentcategory, "category_code");
					String firstCode = MapUtils.getString(parentcategory, "parent_code");
					
					// 一级分类
					list = getCategoryProperties(list, firstCode, productCode);
					// 二级分类
					list = getCategoryProperties(list, secondCode, productCode);
					// 三级分类
					list = getCategoryProperties(list, thirdCode, productCode);
				}
			}
			
		}
		
		return list;
	}
	
	/**
	 * 查询分类属性
	 * @param list
	 * @param categoryCode
	 * @param productCode
	 * @return
	 */
	public List<CategoryProperties> getCategoryProperties(List<CategoryProperties> list, String categoryCode, String productCode){
		// 查询分类属性
		String sql2 = "SELECT k.* FROM uc_sellercategory_properties p LEFT JOIN uc_properties_key k ON p.properties_code = k.properties_code " + 
				"WHERE p.category_code = '"+categoryCode+"' AND k.is_delete = '0' ORDER BY sort_num ASC ";
		List<Map<String, Object>> firstList = DbUp.upTable("uc_sellercategory_properties").dataSqlList(sql2 , new MDataMap());
		if(firstList != null && firstList.size() > 0) {
			for (Map<String, Object> map : firstList) {
				CategoryProperties cp = new CategoryProperties();
				String properties_code = MapUtils.getString(map, "properties_code");
				// 验证list中是否已经包含该属性(去重)
				boolean includeFlag = false;
				for (CategoryProperties categoryProperties : list) {
					if(properties_code.equals(categoryProperties.getProperties_code())){
						includeFlag = true;
						break;
					}
				}
				if(includeFlag) {
					continue;
				}
				
				String properties_value_type = MapUtils.getString(map, "properties_value_type");
				cp.setIs_must(MapUtils.getString(map, "is_must"));
				cp.setProperties_code(properties_code);
				cp.setProperties_name(MapUtils.getString(map, "properties_name"));
				cp.setProperties_value_type(properties_value_type);
				// 查询商品属性选择的属性值
				MDataMap ppr = DbUp.upTable("uc_properties_product_relation").one("product_code",productCode,"properties_code",properties_code);
				if(ppr != null) {
					String properties_value_code = ppr.get("properties_value_code");
					// 如果是固定值,去 uc_properties_value 查找对应的属性值,如果没有就不传
					if("449748500001".equals(properties_value_type)) {
						MDataMap pv = DbUp.upTable("uc_properties_value").one("properties_value_code",properties_value_code,"is_delete","0");
						if(pv != null) {
							cp.setProperties_value_code(properties_value_code);
							cp.setProperties_value(MapUtils.getString(pv, "properties_value"));
						}
					}else {
						//如果是自定义,去 uc_properties_value 查找对应的属性值,如果存在则不传,不存在则取 uc_properties_product_relation 中的取属性值
						MDataMap pv = DbUp.upTable("uc_properties_value").one("properties_value_code",properties_value_code,"is_delete","0");
						if(pv == null) {
							cp.setProperties_value_code(properties_value_code);
							cp.setProperties_value(MapUtils.getString(ppr, "properties_value"));
						}
					}
					
				}
				// 如果是固定值,查询属性值列表
				if("449748500001".equals(properties_value_type)) {
					List<PropertiesValue> pvList = new ArrayList<PropertiesValue>();
					List<Map<String, Object>> pvMapList = DbUp.upTable("uc_properties_value").dataSqlList("SELECT * FROM uc_properties_value WHERE properties_code = '"+properties_code+"' AND is_delete = '0' ORDER BY sort_num ASC", new MDataMap());
					if(pvMapList != null && pvMapList.size() > 0) {
						for (Map<String, Object> map2 : pvMapList) {
							PropertiesValue propertiesValue = new PropertiesValue();
							propertiesValue.setProperties_value_code(MapUtils.getString(map2, "properties_value_code"));
							propertiesValue.setProperties_value(MapUtils.getString(map2, "properties_value"));
							pvList.add(propertiesValue);
						}
					}
					
					cp.setList(pvList);
				}
				
				list.add(cp);
			}
		}
		
		return list;
	}
	
	/**
	 * 获取sku数据库商品售价
	 * @param skuCode
	 * @return
	 */
	public String getSellPriceBySkuCode2(String skuCode) {
		MDataMap skuInfo = DbUp.upTable("pc_skuinfo").one("sku_code",skuCode);
		String sellPrice = "0.00";
		if(null != skuInfo) {
			// 获取商品售价
			sellPrice = MapUtils.getString(skuInfo, "sell_price");
		}
		return sellPrice;
	}
	
	/**
	 * 根据农场商品uid查询sku信息
	 * @param uid
	 * @return
	 */
	public Map<String, String> getSkuInfoByNcUid(String uid) {
		Map<String, String> skuInfo = new HashMap<String, String>();
		MDataMap farm_product = DbUp.upTable("sc_huodong_event_farm_product").one("uid",uid);
		String sku_stock = "0";
		String sell_price = "0.00";
		String activity_price = "0.00";
		if(null != farm_product) {
			activity_price = MapUtils.getString(farm_product, "activity_price");
			
			String sku_code = MapUtils.getString(farm_product, "sku_code");
			MDataMap sku = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
			if(null != sku) {
				sell_price = MapUtils.getString(sku, "sell_price");
			}
			
			MDataMap stockNumMap = DbUp.upTable("sc_store_skunum").oneWhere("SUM(stock_num) as stock_num", "", "", "sku_code",sku_code);
			if (null != stockNumMap) {
				sku_stock = MapUtils.getString(stockNumMap, "stock_num");
			}
		}
		skuInfo.put("sku_stock", sku_stock);
		skuInfo.put("sell_price", sell_price);
		skuInfo.put("activity_price", activity_price);
		return skuInfo;
	}
	
}
