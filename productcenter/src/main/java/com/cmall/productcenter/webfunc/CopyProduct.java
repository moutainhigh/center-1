package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductflow;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.ScStoreSkunum;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.txservice.TxProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.service.SystemCheck;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *家有汇尊享商品复制 
 * @author jack
 */
public class CopyProduct extends RootFunc {
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")&&!"".equals(mDelMaps.get("uid"))&&mDelMaps.get("uid")!=null ) {
				ProductService pService = new ProductService();
				PcProductinfo product = pService.getProduct(mDelMaps.get("uid"));
				MDataMap one = DbUp.upTable("pc_productinfo").one("product_code","7"+product.getProductCodeOld());
				if(one!=null&&!one.isEmpty()){
					mResult.setResultMessage(bInfo(941901114));
				}else {
					StringBuffer error = new StringBuffer();
					product.setProductCode("7"+product.getProductCodeOld());//商品编号
					product.setProductStatus("4497153900060003");//商品状态
					product.setFlagSale(0);//下架状态
					if(product.getSellerCode()==null||"".equals(product.getSellerCode())){
						product.setSellerCode(UserFactory.INSTANCE.create().getManageCode());//店铺编号
					}
					product.setSmallSellerCode(product.getSellerCode());
					product.setZid(0);
					product.setUid("");
					product.setUsprList(null);
					product.getCategory().setCategoryCode("123456");
//					pService.copyProduct(product, error);
					productManage(product, error, 2);
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(941901112));
					} else {
						mResult.inErrorMessage(941901113, error.toString());
					}
				}
			}else{
				mResult.inErrorMessage(941901113, "商品编号为空！");
			}
		}
		return mResult;
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
	private int productManage(PcProductinfo product, StringBuffer error,
			int flag) {

		int errorCode = 0;
		// 根据商户编码查询商户类型 2016-11-14 zhy
		//MDataMap seller = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "", "small_seller_code",
		//		product.getSmallSellerCode());
		//String product_code_start = bConfig("productcenter.product"+seller.get("uc_seller_type"));
		String sku_code_start = "8019";
		if (flag == 0 || flag == 2) {
			String productCode = "7"+product.getProductCodeOld();
			//if(flag == 0){
				//productCode = WebHelper.upCode(ProductService.ProductHead);
				//productCode = WebHelper.upCode(product_code_start);
				//product.setProductCode(productCode);
			//}
//			if(flag == 0&&!(product.getSmallSellerCode().startsWith("SF03")&&!product.getSmallSellerCode().equals("SF03KJT")&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())))//惠家有第三方商户的商品不需要设置
			/**
			 *  修改商品判断条件 2016-12-02 zhy
			 */
			String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
			if(flag == 0&&!(StringUtils.isNotBlank(seller_type)&&!product.getSmallSellerCode().equals("SF03KJT")&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())))//惠家有第三方商户的商品不需要设置
				//设置默认为在售
				{
					product.setFlagSale(1);
				}
			//如果商品主图为空并且轮播图列表不为空时，商品主图设为轮播图第一张  
			if ((null == product.getMainPicUrl() || "".equals(product.getMainPicUrl())) 
						&&product.getPcPicList() != null && product.getPcPicList().size() > 0) {
				product.setMainPicUrl(product.getPcPicList().get(0).getPicUrl());
			}
			if (null != product.getDescription() && null != product.getDescription().getKeyword() && !"".equals(product.getDescription().getKeyword())) {
				String[] labelsArr = product.getDescription().getKeyword().trim().split(" ");
				String labelStr = "";						//将空格替换掉
				for (int i = 0; i < labelsArr.length; i++) {
					if ("".endsWith(labelsArr[i])) {
						continue;
					}
					labelStr += labelsArr[i]+",";
				}
				product.getDescription().setKeyword(labelStr.substring(0, labelStr.length()-1));	//截去最后一个逗号并把商品标签添加到商品描述表中
			}
			if (StringUtils.isNotBlank(product.getBrandCode())) {
				MDataMap brandInfo = DbUp.upTable("pc_brandinfo").oneWhere("brand_name", "", "" ,"brand_code",product.getBrandCode());
				product.setBrandName(brandInfo.get("brand_name"));
			}
			//商品虚类
			if(null != product.getUsprList() && product.getUsprList().size() > 0){
				for(int i=0;i<product.getUsprList().size();i++){
					product.getUsprList().get(i).setProductCode(productCode);
					product.getUsprList().get(i).setSellerCode(product.getSellerCode());	//这个字段应该与虚类所属的sell_code一致，故此不能用small_seller_code
				}
			}
			//商品实类
			if(null != product.getPcProductcategoryRel()){
					product.getPcProductcategoryRel().setProductCode(productCode);
					product.getPcProductcategoryRel().setFlagMain(1);
			}
			//商品扩展信息
			if(null != product.getPcProductinfoExt()){
				product.getPcProductinfoExt().setProductCode(productCode);
			}
			if (product.getProductSkuInfoList() != null) {
				for (int i = 0; i < product.getProductSkuInfoList().size(); i++) {
					ProductSkuInfo sku = product.getProductSkuInfoList().get(i);
					sku.setProductCode(productCode);
//					sku.setSkuCode(WebHelper.upCode(ProductService.SKUHead));
					sku.setSkuCode(WebHelper.upCode(sku_code_start));
					sku.setSellerCode(product.getSellerCode());
					sku.setFlagEnable("1");
					//如果当前商品的sku图片不存在，则 设置sku图片。
					if(sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")){
						sku.setSkuPicUrl(product.getMainPicUrl());
					}
					
					//商户后台添加商品保存库存信息
//					if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
//							&& StringUtils.isNotEmpty(product.getSmallSellerCode()) 
//							&& product.getSmallSellerCode().startsWith("SF03") 
//							&& !product.getSmallSellerCode().equals("SF03KJT")) {
					/**
					 * 修改商户判断条件 2016-12-02 zhy
					 */
					if (AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())
							&& StringUtils.isNotEmpty(product.getSmallSellerCode()) 
							&& StringUtils.isNotBlank(seller_type) 
							&& !product.getSmallSellerCode().equals("SF03KJT")) {
						List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
						ScStoreSkunum store = new ScStoreSkunum();
						store.setBatchCode("");
						store.setSkuCode(sku.getSkuCode());
						store.setStockNum(Long.parseLong(sku.getStockNum()+""));
						store.setStoreCode(AppConst.THIRD_STORE_CODE);
						scStoreSkunumList.add(store);
						sku.setScStoreSkunumList(scStoreSkunumList);
					}else if (AppConst.MANAGE_CODE_CDOG.equals(product.getSellerCode()) 
							&& AppConst.MANAGE_CODE_CDOG.equals(product.getSmallSellerCode())) {	//沙皮狗保存库存
						List<ScStoreSkunum> scStoreSkunumList = new ArrayList<ScStoreSkunum>();
						ScStoreSkunum store = new ScStoreSkunum();
						store.setBatchCode("");
						store.setSkuCode(sku.getSkuCode());
						store.setStockNum(Long.parseLong(sku.getStockNum()+""));
						store.setStoreCode(AppConst.CDOG_STORE_CODE);
						scStoreSkunumList.add(store);
						sku.setScStoreSkunumList(scStoreSkunumList);
					}
				}
			}
		}

		// 商品名字不能为空
		if (product.getProdutName() == null
				|| product.getProdutName().trim().equals("")) {
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
		if (flag!=2&&(product.getBrandCode() == null
				|| product.getBrandCode().trim().equals(""))) {
			errorCode = 941901010;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		// 分类不能为空
		if (product.getCategory() == null
				|| product.getCategory().getCategoryCode().equals("")) {
			errorCode = 941901011;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		
		// 商品的sku数量不能为空!
		if (product.getProductSkuInfoList() == null
				|| product.getProductSkuInfoList().size() == 0) {
			errorCode = 941901015;
			error.append(bInfo(errorCode));
			return errorCode;
		}
		//检查价格是否符合规则
		//1) 商品的市场价格必须不小于商品sku的销售价格.   2)商品的sku成本价小于销售价
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
			
			//商品的市场价格必须不小于商品sku的销售价格.
//			if (sku.getSellPrice().compareTo(product.getMarketPrice())>0) {
//				errorCode = 941901134;
//				error.append(bInfo(errorCode));
//				return errorCode;
//			}
			//商品的sku成本价小于销售价
//			if (sku.getSellPrice().compareTo(sku.getCostPrice())<=0) {
//				errorCode = 941901135;
//				error.append(bInfo(errorCode));
//				return errorCode;
//			}
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
//					product.setMarketPrice(pic.getMarketPrice());
				} else {
					if (tempMin.compareTo(pic.getSellPrice())==1)
						tempMin = pic.getSellPrice();
					if (tempMax.compareTo(pic.getSellPrice())==-1)
						tempMax = pic.getSellPrice();
				}
			}

			product.setMinSellPrice(tempMin);
			product.setMaxSellPrice(tempMax);
		}

//		if (product.getUsprList() != null && product.getUsprList().size() > 0) {
//			for (UcSellercategoryProductRelation uspr : product.getUsprList()) {
//
//				MDataMap mDataMap = new MDataMap();
//
//				mDataMap.put("category_code", uspr.getCategoryCode());
//				mDataMap.put("product_code", product.getProductCode());
//				mDataMap.put("seller_code", product.getSellerCode());
//
//				DbUp.upTable("uc_sellercategory_product_relation").dataInsert(
//						mDataMap);
//
//			}
//		}

		PcProductflow pcProdcutflow = new PcProductflow();
		pcProdcutflow.setProductCode(product.getProductCode());
		pcProdcutflow.setFlowCode(WebHelper.upCode(ProductService.ProductFlowHead));
//		if(product.getSmallSellerCode().startsWith("SF03") &&!product.getSmallSellerCode().startsWith("SF03KJT")&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())){
		/**
		 * 修改商户判断条件 2016-12-02 zhy
		 */
		String seller_type = WebHelper.getSellerType(product.getSmallSellerCode());
		if(StringUtils.isNotBlank(seller_type) &&!product.getSmallSellerCode().startsWith("SF03KJT")&& AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())){
			if(flag==0){
				pcProdcutflow.setFlowStatus(SkuCommon.ProAddInit);
			}else if(flag==1){
				pcProdcutflow.setFlowStatus(SkuCommon.ProUpaInit);
			}
		}else {
			if(flag==0){
				pcProdcutflow.setFlowStatus(SkuCommon.FlowStatusInit);
			}else if(flag==1){
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
				if(manageCode==null||"".equals(manageCode)){
					manageCode=userCode;
				}
			}
		}

		pcProdcutflow.setUpdator(userCode);
		product.setPcProdcutflow(pcProdcutflow);
		String currentStatus = "";
		if(flag == 0){
			// 取得当前的商品的状态
			currentStatus = bConfig("productcenter.AddProductStatus");
			// 设置当前的商品的状态
			product.setProductStatus(currentStatus);
			//沙皮狗项目商品初始状态为已下架
			if (AppConst.MANAGE_CODE_CDOG.equals(product.getSellerCode())) {
				product.setProductStatus("4497153900060003");	
			}
//			ProductCheck pc = new ProductCheck();
//			//如果当前上架的商品超过某个数量，则把商品置成下架
//			if(pc.upSalesScopeType(manageCode).equals("")){
//				if(product.getProductStatus().equals("4497153900060002")){
//					product.setProductStatus("4497153900060003");
//				}
//			}
		}
		
		//校验商品名字的外链合法性
		RootResult rr = checkContent(product.getProdutName());
		if(rr.getResultCode() != 1){
			error.append(rr.getResultMessage());
			return rr.getResultCode();
		}
		//校验商品描述的外链合法性
//		if(product.getDescription()!=null){
//			rr = checkContent(product.getDescription().getDescriptionInfo());
//			if(rr.getResultCode() != 1){
//				error.append(rr.getResultMessage());
//				return rr.getResultCode();
//			}
//		}
		//校验商品属性外链的合法性 
		if(product.getPcProductpropertyList()!=null){
			for(PcProductproperty ppp : product.getPcProductpropertyList()){
				rr = checkContent(ppp.getPropertyKey());
				if(rr.getResultCode() != 1){
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}
				
				rr = checkContent(ppp.getPropertyValue());
				if(rr.getResultCode() != 1){
					error.append(rr.getResultMessage());
					return rr.getResultCode();
				}
			}
		}
		
		
//		if(product.getSmallSellerCode().startsWith("SF03")&&!product.getSmallSellerCode().startsWith("SF03KJT")&&AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())){
		/**
		 * 修改商户判断条件2016-12-02 zhy
		 */
		if(StringUtils.isNotBlank(seller_type)&&!product.getSmallSellerCode().startsWith("SF03KJT")&&AppConst.MANAGE_CODE_HOMEHAS.equals(product.getSellerCode())){
			product.getPcProductinfoExt().setProductCode(product.getProductCode());
			product.getPcProductinfoExt().setPrchType("20");
			product.getPcProductinfoExt().setDlrId(product.getSmallSellerCode());
//		product.getPcProductinfoExt().setDlrNm(UserFactory.INSTANCE.create().getLoginName());
			//供应商名称
			MDataMap dlrMap = DbUp.upTable("uc_sellerinfo").oneWhere("seller_company_name","","","small_seller_code",product.getSmallSellerCode());
			if (null != dlrMap && !dlrMap.isEmpty()) {
				product.getPcProductinfoExt().setDlrNm(dlrMap.get("seller_company_name"));
			}else{
				product.getPcProductinfoExt().setDlrNm(product.getSmallSellerCode());
			}
			product.getPcProductinfoExt().setOaSiteNo(AppConst.THIRD_STORE_CODE);
			product.getPcProductinfoExt().setValidateFlag("Y");
			String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl")+product.getProductCode()+"_1";
			try {
				//加入审批的流程
				ScFlowMain flow = new ScFlowMain();
				flow.setCreator(userCode);
				flow.setCurrentStatus("4497153900060005");
				String title = bInfo(941901036, product.getProductCode());
				flow.setFlowTitle(product.getProductCode());
				flow.setFlowType("449717230011");
				flow.setFlowUrl(preViewUrl);
				flow.setCreator(userCode);
				flow.setOuterCode(product.getProductCode());
				flow.setFlowRemark(title);
				flow.setNext_operator_id(product.getPcProductinfoExt().getMdId());
				//创建的审批流程
				(new FlowService()).CreateFlow(flow);
			} catch (Exception e) {
			}
		}
		return this.AddProductTx(product, error, manageCode);
	}
	
	/**
	 * 校验内容的合法性
	 * @param content
	 * @return
	 */
	public RootResult checkContent(String content){
		RootResult mResult = new RootResult();
		SystemCheck systemCheck=new SystemCheck();
		MWebResult mCheckLinkResult=systemCheck.checkLink(content);
		if(!mCheckLinkResult.upFlagTrue())
		{
			
			mResult.setResultCode(mCheckLinkResult.getResultCode());
			mResult.setResultMessage(mCheckLinkResult.getResultMessage());
		}
		return mResult;
	}

	public int AddProductTx(PcProductinfo pc,StringBuffer error,String manageCode){
		
		RootResult rr= new RootResult();
		TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

		
		try {
			txs.insertProduct(pc, rr, manageCode);
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
					skuCodes += product.getProductSkuInfoList().get(i)
							.getSkuCode();
				} else {
					skuCodes += product.getProductSkuInfoList().get(i)
							.getSkuCode()
							+ ",";
				}
			}
			if (j > 0) {
				String jsonData = "{\"type\":\"sku\",\"data\":\"" + skuCodes
						+ "\"}";
				pjs.OnChangeSku(jsonData);
			}
		}
	}
}