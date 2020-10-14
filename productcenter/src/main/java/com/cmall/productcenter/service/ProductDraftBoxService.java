package com.cmall.productcenter.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.PcProductDraftbox;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductflow;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.PropertiesProductRelation;
import com.cmall.productcenter.model.ScStoreSkunum;
import com.cmall.productcenter.txservice.TxProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 项目名称：productcenter 类名称：ProductDraftBoxService 类描述： 商品草稿箱 创建人：ligj
 * 创建时间：2015-11-20
 * 
 * @version
 * 
 */
public class ProductDraftBoxService extends BaseClass {
	/**
	 * 添加商品到草稿箱(保存草稿)
	 * 
	 * @param product
	 * @param error
	 *            错误信息
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int addProductToDraftBox(PcProductinfo product, StringBuffer error) {
		return productManageDraftBox(product, "449747670002", 0, error);
	}

	/**
	 * 添加商品到草稿箱(审批驳回)
	 * 
	 * @param productJson
	 */
	public int addProductToDraftBoxForReject(String productJson) {
		return this.manageDraftbox(productJson, "449747670001", 0, new StringBuffer());
	}

	/**
	 * 修改草稿箱商品信息
	 * 
	 * @param product
	 * @param error
	 *            错误信息
	 * @return 如果正确，返回 1 ，否则，返回错误的编号
	 */
	public int updateProductToDraftBox(PcProductinfo product, StringBuffer error) {

		MDataMap proMap = DbUp.upTable("pc_product_draftbox").oneWhere("uid,flow_status", "zid desc",
				"product_code=:product_code and flag_del=:flag_del", "product_code", product.getProductCode(),
				"flag_del", "449746250002");
		if (null == proMap || proMap.isEmpty()) {
			return 0;
		}
		return this.productManageDraftBox(product, proMap.get("flow_status"), 1, error);
	}

	/**
	 * @param product
	 *            商品信息
	 * @param flowStatus
	 *            流程状态(449747670001:审核未通过，449747670002:草稿)
	 * @param flag
	 *            0 添加 1 修改
	 * @return
	 */
	private int productManageDraftBox(PcProductinfo product, String flowStatus, int flag, StringBuffer error) {
		// 根据商户编码查询商户类型 2016-11-14 zhy
		MDataMap seller = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "", "small_seller_code",
				product.getSmallSellerCode());
		String product_code_start = ProductService.ProductHead;
		if(seller != null){
			product_code_start = bConfig("productcenter.product"+seller.get("uc_seller_type"));	
		}
		
		PcProductDraftbox draftbox = new PcProductDraftbox();
		String sysTime = DateUtil.getSysDateTimeString();
		if (flag == 0 && "449747670002".equals(flowStatus)) {
//			String productCode = WebHelper.upCode(ProductService.ProductHead);
			String productCode = WebHelper.upCode(product_code_start);
			product.setProductCode(productCode);
			List<PropertiesProductRelation> pprList = product.getPprList();
			for (PropertiesProductRelation propertiesProductRelation : pprList) {
				propertiesProductRelation.setProduct_code(productCode);
			}
		}
		if (product.getProductSkuInfoList() != null) {
			int size = product.getProductSkuInfoList().size();
			BigDecimal tempMinCostPrice = new BigDecimal(0.00);
			BigDecimal tempMaxCostPrice = new BigDecimal(0.00);

			BigDecimal tempMinSellPrice = new BigDecimal(0.00);
			BigDecimal tempMaxSellPrice = new BigDecimal(0.00);
			for (int i = 0; i < size; i++) {
				ProductSkuInfo pic = product.getProductSkuInfoList().get(i);
				BigDecimal costPrice = (null == pic.getCostPrice() ? BigDecimal.ZERO : pic.getCostPrice());
				if (i == 0) {
					tempMinCostPrice = costPrice;
					tempMaxCostPrice = costPrice;
				} else {
					if (tempMinCostPrice.compareTo(costPrice) == 1)
						tempMinCostPrice = costPrice;
					if (tempMaxCostPrice.compareTo(costPrice) == -1)
						tempMaxCostPrice = costPrice;
				}

				BigDecimal sellPrice = (null == pic.getSellPrice() ? BigDecimal.ZERO : pic.getSellPrice());
				if (i == 0) {
					tempMinSellPrice = sellPrice;
					tempMaxSellPrice = sellPrice;
				} else {
					if (tempMinSellPrice.compareTo(sellPrice) == 1)
						tempMinSellPrice = sellPrice;
					if (tempMaxSellPrice.compareTo(sellPrice) == -1)
						tempMaxSellPrice = sellPrice;
				}
			}
			draftbox.setMinCostPrice(tempMinCostPrice);
			draftbox.setMaxCostPrice(tempMaxCostPrice);
			draftbox.setMinSellPrice(tempMinSellPrice);
			draftbox.setMaxSellPrice(tempMaxSellPrice);
		}
		List<String> categoryCodes = new ArrayList<String>();
		// 商品虚类
		if (null != product.getUsprList() && product.getUsprList().size() > 0) {
			for (int i = 0; i < product.getUsprList().size(); i++) {
				categoryCodes.add(product.getUsprList().get(i).getCategoryCode());
			}
		}

		draftbox.setProductCode(product.getProductCode());
		draftbox.setProductName(product.getProductName());
		draftbox.setCategoryCode(StringUtils.join(categoryCodes, ","));
		draftbox.setProductStatus(product.getProductStatus());
		draftbox.setFlowStatus(flowStatus);
		JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
		draftbox.setProductJson(pHelper.ObjToString(product));
		MUserInfo userInfo = null;
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				userInfo = new MUserInfo();
			}

			if (userInfo != null) {
				userCode = userInfo.getUserCode();
			}
		}
		/**
		 * 增加商户编码
		 */
		draftbox.setSellerCode(product.getSellerCode());
		draftbox.setSmallSellerCode(product.getSmallSellerCode());
		draftbox.setUpdateTime(sysTime);
		draftbox.setUpdator(userCode);
		draftbox.setFlagDel("449746250002");
		RootResult rr = new RootResult();
		if (flag == 0) { // 添加
			draftbox.setCreateTime(sysTime);
			draftbox.setCreator(userCode);
			try {
				new TxProductService().insertProductDraftbox(draftbox, rr);
			} catch (Exception e) {
				rr.setResultCode(941901049);
				rr.setResultMessage(bInfo(941901049, e.getMessage()));
			}
		} else if (flag == 1) { // 修改
			MDataMap proMap = DbUp.upTable("pc_product_draftbox").oneWhere("uid", "zid desc",
					"product_code=:product_code and flag_del=:flag_del", "product_code", draftbox.getProductCode(),
					"flag_del", draftbox.getFlagDel());
			draftbox.setUid(proMap.get("uid"));
			new TxProductService().updateProductDraftbox(draftbox, rr);
		}
		error.append(rr.getResultMessage());
		return rr.getResultCode();
	}

	/**
	 * 
	 * @param productJson
	 *            商品对象json串
	 * @param flowStatus
	 *            流程状态(449747670001:审核未通过，449747670002:草稿)
	 * @param flag
	 *            0:添加，1:修改
	 * @return
	 */
	public int manageDraftbox(String productJson, String flowStatus, int flag, StringBuffer error) {
		if (StringUtils.isBlank(productJson)) {
			return 0;
		}
		PcProductinfo product = new PcProductinfo();
		JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
		product = pHelper.StringToObj(productJson, product);
		return this.productManageDraftBox(product, flowStatus, flag, error);
	}

	public String upProductInfoJson(String sProduct) {
		JsonHelper<PcProductinfo> jsonHelper = new JsonHelper<PcProductinfo>();

		if (StringUtils.isNotEmpty(sProduct)) {
			return jsonHelper.ObjToString(this.getProduct(sProduct));
		} else {
			return jsonHelper.ObjToString(new PcProductinfo());
		}
	}

	/**
	 * 获取草稿箱商品信息
	 * 
	 * @param productCode
	 * @return
	 */
	public PcProductinfo getProduct(String productCode) {

		PcProductinfo product = new PcProductinfo();
		if (productCode != null && productCode.length() > 0) {
			MDataMap productData = new MDataMap();
			// 此处查询根据flag_del倒排序的目的为了优先取未删除的数据
			List<MDataMap> productDataList = DbUp.upTable("pc_product_draftbox").query("", "flag_del desc,zid desc",
					"product_code='" + productCode + "'", null, 0, 1);
			if (null != productDataList && productDataList.size() > 0) {
				productData = productDataList.get(0);
			}

			if (productData == null)
				return product;

			String pValue = productData.get("product_json");
			JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
			try {
				product = pHelper.StringToObjExp(pValue, product);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return product;
	}

	/**
	 * 标记删除草稿箱商品数据
	 * 
	 * @param uid
	 * @param productCode
	 * @return
	 */
	public int delDraftBoxProduct(String uid, String productCode, String userCode) {
		if (StringUtils.isBlank(uid) && StringUtils.isBlank(productCode)) {
			return 0;
		}
		if (StringUtils.isBlank(uid)) {
			MDataMap proMap = DbUp.upTable("pc_product_draftbox").oneWhere("uid", "zid desc",
					"product_code=:product_code and flag_del=:flag_del", "product_code", productCode, "flag_del",
					"449746250002");
			if (null != proMap && !proMap.isEmpty()) {
				uid = proMap.get("uid");
			} else {
				return 0;
			}
		}
		MDataMap mDataMap = new MDataMap();
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String retStrFormatNowDate = sdFormatter.format(nowTime);
		mDataMap.put("uid", uid);
		mDataMap.put("flag_del", "449746250001");
		mDataMap.put("update_time", retStrFormatNowDate);
		mDataMap.put("updator", userCode);
		return DbUp.upTable("pc_product_draftbox").dataUpdate(mDataMap, "flag_del,update_time,updator", "uid");
	}

	public int addProduct(PcProductinfo product, StringBuffer error) {
		// 标志为虚拟商品，拆单用
		product.setValidate_flag("Y");
		// 设为不可售
		product.setFlagSale(0);
		product.setCreateTime(DateUtil.getSysDateTimeString());
		product.setUpdateTime(DateUtil.getSysDateTimeString());
		int errorCode = 0;
		String productCode = product.getProductCode();
		// 如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张
		if ((null == product.getMainPicUrl() || "".equals(product.getMainPicUrl())) && product.getPcPicList() != null
				&& product.getPcPicList().size() > 0) {
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
			// 根据商户编码查询商户类型 2016-11-14 zhy
			MDataMap seller = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "", "small_seller_code",
					product.getSmallSellerCode());
			String sku_code_start = SkuCommon.SKUHead;
			if(seller != null){
				sku_code_start = bConfig("productcenter.sku"+seller.get("uc_seller_type"));	
			}
			
			for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
				ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
				sku.setProductCode(productCode);
//				sku.setSkuCode(WebHelper.upCode(SkuCommon.SKUHead)); // 添加商品时候需要自动生成skucode
				sku.setSkuCode(WebHelper.upCode(sku_code_start));
				sku.setSellerCode(product.getSellerCode());
				sku.setFlagEnable("1");
				sku.setSaleYn("Y");
				// 如果当前商品的sku图片不存在，则 设置sku图片。
				if (StringUtils.isBlank(sku.getSkuPicUrl())) {
					sku.setSkuPicUrl(product.getMainPicUrl());
				}

				// 商户后台添加商品保存库存信息
//				if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
//						&& StringUtils.isNotEmpty(product.getSmallSellerCode())
//						&& product.getSmallSellerCode().startsWith("SF03")
//						&& !product.getSmallSellerCode().equals("SF03KJT")) {
				/**
				 * 修改商户判断条件2016-12-02 zhy
				 */
				String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
				if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
						&& StringUtils.isNotEmpty(product.getSmallSellerCode())
						&& StringUtils.isNotBlank(seller_type)
						&& !product.getSmallSellerCode().equals("SF03KJT")) {
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

		// 商品名字不能为空
		if (product.getProdutName() == null || product.getProdutName().trim().equals("")) {
			errorCode = 941901012;
			error.append(bInfo(errorCode));
			return errorCode;
		}

		// 品牌不能为空
		if ((product.getBrandCode() == null || product.getBrandCode().trim().equals(""))) {
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

		PcProductflow pcProdcutflow = new PcProductflow();
		pcProdcutflow.setProductCode(product.getProductCode());
		pcProdcutflow.setFlowCode(WebHelper.upCode(ProductService.ProductFlowHead));
		pcProdcutflow.setFlowStatus(SkuCommon.ProAddInit);
		MUserInfo userInfo = null;
		String manageCode = "";
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
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
		String currentStatus = bConfig("productcenter.AddProductStatus");
		product.setProductStatus(currentStatus);
		ProductService ps = new ProductService();
		// 校验商品名字的外链合法性
		RootResult rr = ps.checkContent(product.getProdutName());
		if (rr.getResultCode() != 1) {
			error.append(rr.getResultMessage());
			return rr.getResultCode();
		}
		// 校验商品描述的外链合法性
		if (product.getDescription() != null) {
			rr = ps.checkContent(product.getDescription().getDescriptionInfo());
			if (rr.getResultCode() != 1) {
				error.append(rr.getResultMessage());
				return rr.getResultCode();
			}
		}
		// 校验商品属性外链的合法性
		if (product.getPcProductpropertyList() != null) {
			for (PcProductproperty ppp : product.getPcProductpropertyList()) {
				rr = ps.checkContent(ppp.getPropertyKey());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}

				rr = ps.checkContent(ppp.getPropertyValue());
				if (rr.getResultCode() != 1) {
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}
			}
		}

		product.getPcProductinfoExt().setProductCode(product.getProductCode());
		product.getPcProductinfoExt().setPrchType("20");
		product.getPcProductinfoExt().setDlrId(product.getSmallSellerCode());
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
			// 创建商品默认下一级审批人为空 zhy 2016 -06-27
			// flow.setNext_operator_id(product.getPcProductinfoExt().getMdId());
			// 创建的审批流程
			(new FlowService()).CreateFlow(flow);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ps.AddProductTx(product, error, manageCode);

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
					"select uc_seller_type from uc_seller_info_extend where small_seller_code =:seller_code ",
					new MDataMap("seller_code", seller_code));
			if (map != null && map.get("uc_seller_type") != null
					&& ("4497478100050002".equals(map.get("uc_seller_type").toString())
							|| "4497478100050003".equals(map.get("uc_seller_type").toString()))) {
				flag = true;
			}
		}
		return flag;
	}
}
