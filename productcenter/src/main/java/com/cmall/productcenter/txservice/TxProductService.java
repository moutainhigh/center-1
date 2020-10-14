package com.cmall.productcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.BcPurchaseDetail;
import com.cmall.dborm.txmodel.LcPricechange;
import com.cmall.dborm.txmodel.LcStockchange;
import com.cmall.dborm.txmodel.NcFreetryoutApply;
import com.cmall.dborm.txmodel.NcFreetryoutApplyExample;
import com.cmall.dborm.txmodel.NcPostOperate;
import com.cmall.dborm.txmodel.NcPostOperateExample;
import com.cmall.dborm.txmodel.OcTryoutProducts;
import com.cmall.dborm.txmodel.OcTryoutProductsExample;
import com.cmall.dborm.txmodel.PcProductDraftbox;
import com.cmall.dborm.txmodel.PcProductDraftboxExample;
import com.cmall.dborm.txmodel.PcProductcategoryRel;
import com.cmall.dborm.txmodel.PcProductdescriptionExample;
import com.cmall.dborm.txmodel.PcProductdescriptionWithBLOBs;
import com.cmall.dborm.txmodel.PcProductflow;
import com.cmall.dborm.txmodel.PcProductinfoExample;
import com.cmall.dborm.txmodel.PcProductinfoExt;
import com.cmall.dborm.txmodel.PcProductinfoExtExample;
import com.cmall.dborm.txmodel.PcProductpicExample;
import com.cmall.dborm.txmodel.PcProductpropertyExample;
import com.cmall.dborm.txmodel.PcSkuinfoExample;
import com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs;
import com.cmall.dborm.txmodel.PcSkuinfoWithBLOBsForD;
import com.cmall.dborm.txmodel.PcStockDetail;
import com.cmall.dborm.txmodel.PcStockDetailExample;
import com.cmall.dborm.txmodel.PcStockInfo;
import com.cmall.dborm.txmodel.PcStockInfoExample;
import com.cmall.dborm.txmodel.ScStoreSkunumExample;
import com.cmall.dborm.txmodel.UcSellercategoryProductRelation;
import com.cmall.dborm.txmodel.UcSellercategoryProductRelationExample;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.MProductProperty;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductChangeFlag;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.ScStoreSkunum;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.txservice.TxStockService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

public class TxProductService  extends BaseClass implements ITxService{

	public void insertProduct(PcProductinfo pc,RootResult ret,String operator){
		
		com.cmall.dborm.txmapper.UcSellercategoryProductRelationMapper usprm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_UcSellercategoryProductRelationMapper");
		com.cmall.dborm.txmapper.PcProductdescriptionMapper ppsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductdescriptionMapper");
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.PcProductinfoMapper pcpm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoMapper");
		com.cmall.dborm.txmapper.PcProductpicMapper pppm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductpicMapper");
		com.cmall.dborm.txmapper.PcProductpropertyMapper pppmr = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductpropertyMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.ScStoreSkunumMapper sssm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		com.cmall.dborm.txmapper.PcProductinfoExtMapper ppem = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoExtMapper");
		com.cmall.dborm.txmapper.PcProductcategoryRelMapper pprm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductcategoryRelMapper");
		
		String createTime = DateUtil.getSysDateTimeString();
		
		//插入商品基本信息
		com.cmall.dborm.txmodel.PcProductinfo pptModel = new com.cmall.dborm.txmodel.PcProductinfo();
		pptModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		pptModel.setCreateTime(createTime);
		pptModel.setBrandCode(pc.getBrandCode());
		//pptModel.setBrandName(pc.getBrandName());
		pptModel.setFlagPayway(pc.getFlagPayway());
		pptModel.setFlagSale(pc.getFlagSale());
		pptModel.setLabels(pc.getLabels());
		pptModel.setMainpicUrl(pc.getMainPicUrl());
		pptModel.setMarketPrice(pc.getMarketPrice());
		pptModel.setMaxSellPrice(pc.getMaxSellPrice());
		pptModel.setMinSellPrice(pc.getMinSellPrice());
		pptModel.setProductCode(pc.getProductCode());
		pptModel.setProductCodeOld(pc.getProductCodeOld()==null?"":pc.getProductCodeOld());
		pptModel.setProductName(pc.getProdutName());
		pptModel.setProductStatus(pc.getProductStatus());
		pptModel.setProductVolume(pc.getProductVolume());
		pptModel.setProductVolumeItem(pc.getProductVolumeItem());
		pptModel.setProductWeight(pc.getProductWeight());
		pptModel.setSellerCode(pc.getSellerCode());
		pptModel.setSmallSellerCode(pc.getSmallSellerCode());
		pptModel.setSellProductcode(pc.getSellProductcode());
		pptModel.setTransportTemplate(pc.getTransportTemplate());
		pptModel.setAreaTemplate(pc.getAreaTemplate());  		//限购地区
		pptModel.setUpdateTime(createTime);
		pptModel.setCostPrice(pc.getCostPrice());
		pptModel.setProductShortname(pc.getProductShortname());
		pptModel.setSupplierName(pc.getSupplierName());
		pptModel.setVideoUrl(pc.getVideoUrl());
		pptModel.setVideoMainPic(pc.getVideoMainPic());
		pptModel.setProductDescVideo(pc.getProductDescVideo());
		pptModel.setValidateFlag(pc.getValidate_flag());//添加是否是虚拟商品字段
		pptModel.setTaxRate(pc.getTaxRate());
		pptModel.setProductCodeCopy(pc.getProductCodeCopy());
		pptModel.setAdpicUrl(pc.getAdPicUrl());
		pptModel.setExpiryDate(pc.getExpiryDate());//保质期
		pptModel.setExpiryUnit(pc.getExpiryUnit());//保质期单位
		pptModel.setQualificationCategoryCode(pc.getQualificationCategoryCode());//资质品类
		pptModel.setVoucherGood(pc.getVoucherGood());
		pptModel.setVipdayFlag(pc.getVipdayFlag());
		pptModel.setPrchCd(pc.getPrchCd());
		pptModel.setLowGood(pc.getLowGood());
		pptModel.setAccmYn(pc.getAccmYn());
		pptModel.setVlOrs(pc.getVlOrs());
		pptModel.setAfterSaleAddressUid(pc.getAfterSaleAddressUid());
		pptModel.setTaxCode(pc.getTaxCode());
		pptModel.setOnlinepayFlag(pc.getOnlinepayFlag());
		pptModel.setCspsFlag(pc.getCspsFlag());
		pptModel.setDlrCharge(pc.getDlrCharge());
		pptModel.setSoId(pc.getSoId());
		pcpm.insertSelective(pptModel);
		
		//添加商品的分类信息
//		if(pc.getCategory()!=null){
//			com.cmall.dborm.txmodel.PcProductcategoryRel ppcrModel = new PcProductcategoryRel();
//			ppcrModel.setCategoryCode(pc.getCategory().getCategoryCode());
//			ppcrModel.setProductCode(pc.getProductCode());
//			ppcrModel.setUid(UUID.randomUUID().toString().replace("-", ""));
//			ppcrModel.setFlagMain(1l);
//			pptrm.insertSelective(ppcrModel);
//		}
		//添加商品的分类信息
		if(pc.getUsprList()!=null && pc.getUsprList().size() > 0){
			
			for (int i = 0; i < pc.getUsprList().size(); i++) {
				com.cmall.dborm.txmodel.UcSellercategoryProductRelation usprModel = new UcSellercategoryProductRelation();
				usprModel.setCategoryCode(pc.getUsprList().get(i).getCategoryCode());
				usprModel.setProductCode(pc.getUsprList().get(i).getProductCode());
				usprModel.setSellerCode(pc.getUsprList().get(i).getSellerCode());
				usprModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				usprm.insertSelective(usprModel);
			}
		}
		//添加商品的实类信息
		if(null != pc.getPcProductcategoryRel()){
			com.cmall.dborm.txmodel.PcProductcategoryRel pprModel = new PcProductcategoryRel();
			pprModel.setCategoryCode(pc.getPcProductcategoryRel().getCategoryCode());
			pprModel.setFlagMain(Long.parseLong(pc.getPcProductcategoryRel().getFlagMain()+""));
			pprModel.setProductCode(pc.getPcProductcategoryRel().getProductCode());
			pprModel.setUid(UUID.randomUUID().toString().replace("-", ""));
			pprm.insertSelective(pprModel);
		}
		//添加 描述信息
		if(pc.getDescription()!=null){
			com.cmall.dborm.txmodel.PcProductdescriptionWithBLOBs ppdModel = new PcProductdescriptionWithBLOBs();
			ppdModel.setProductCode(pc.getProductCode());
			ppdModel.setKeyword(pc.getDescription().getKeyword());
			ppdModel.setDescriptionPic(pc.getDescription().getDescriptionPic());
			ppdModel.setDescriptionInfo(pc.getDescription().getDescriptionInfo());
			ppdModel.setUid(UUID.randomUUID().toString().replace("-", ""));
			ppsm.insertSelective(ppdModel);
		}
	
		//插入商品图片信息
		if(pc.getPcPicList()!=null){
			
			List<PcProductpic> picList = pc.getPcPicList();
			for(PcProductpic pic:picList){
				com.cmall.dborm.txmodel.PcProductpic picModel = new com.cmall.dborm.txmodel.PcProductpic();
				picModel.setPicUrl(pic.getPicUrl());
				picModel.setProductCode(pc.getProductCode());
				picModel.setSkuCode(pic.getSkuCode());
				picModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				pppm.insertSelective(picModel);
			}
		}
		//插入sku信息
		if(pc.getProductSkuInfoList()!=null){
			List<ProductSkuInfo> skuList = pc.getProductSkuInfoList();
			
			for(ProductSkuInfo sku : skuList){
				com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
				// 替换SKU名称中的"共同"为空白
				sku.setSkuName(sku.getSkuName().replaceAll("\\s+共同", "").replaceAll("\\s+", " ").trim());
				
				psModel.setMarketPrice(sku.getMarketPrice());
				psModel.setProductCode(pc.getProductCode());
				psModel.setProductCodeOld("");
				psModel.setQrcodeLink(sku.getQrcodeLink());
				psModel.setSecurityStockNum(Long.valueOf(sku.getSecurityStockNum()));
				psModel.setSellerCode(sku.getSellerCode());
				psModel.setSellPrice(sku.getSellPrice());
				psModel.setSellProductcode(sku.getSellProductcode());
				psModel.setSkuCode(sku.getSkuCode());
				psModel.setSkuCodeOld(sku.getSkuCodeOld());
				psModel.setSkuKey(sku.getSkuKey());
				psModel.setSkuKeyvalue(sku.getSkuValue());
				psModel.setSkuPicurl(sku.getSkuPicUrl());
				psModel.setSkuName(sku.getSkuName());
				psModel.setSkuAdv(sku.getSkuAdv());
				psModel.setStockNum(Long.valueOf(sku.getStockNum()));
				psModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				psModel.setSaleYn(sku.getSaleYn());
				psModel.setCostPrice(sku.getCostPrice());
				psModel.setMiniOrder(sku.getMiniOrder());
				pcsm.insertSelective(psModel);
				//插入商品sku库存
				if(sku.getScStoreSkunumList()!=null){
					List<ScStoreSkunum> skuStoreList = sku.getScStoreSkunumList();
					for(ScStoreSkunum skuStore : skuStoreList){
						com.cmall.dborm.txmodel.ScStoreSkunum sssModel = new com.cmall.dborm.txmodel.ScStoreSkunum();
						sssModel.setUid(UUID.randomUUID().toString().replace("-", ""));
						sssModel.setSkuCode(skuStore.getSkuCode());
						sssModel.setStockNum(skuStore.getStockNum());
						sssModel.setStoreCode(skuStore.getStoreCode());
						sssModel.setBatchCode(skuStore.getBatchCode());
						sssm.insertSelective(sssModel);
					}
				}
			}
		}
		//插入商品属性信息
		if(pc.getPcProductpropertyList()!=null){
			
			List<PcProductproperty> pppList = pc.getPcProductpropertyList();
			
			for(PcProductproperty ppp : pppList){
				
				com.cmall.dborm.txmodel.PcProductproperty pppModel = new com.cmall.dborm.txmodel.PcProductproperty();
				
				pppModel.setProductCode(pc.getProductCode());
				pppModel.setPropertyCode(ppp.getPropertyCode());
				pppModel.setPropertyKey(ppp.getPropertyKey());
				pppModel.setPropertyKeycode(ppp.getPropertyKeycode());
				pppModel.setPropertyType(ppp.getPropertyType());
				pppModel.setPropertyValue(ppp.getPropertyValue());
				pppModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				
				pppmr.insertSelective(pppModel);
			}
		}
		//插入商品扩展信息
		if(pc.getPcProductinfoExt() != null){
			
			com.cmall.dborm.txmodel.PcProductinfoExt ppe = new PcProductinfoExt();
			ppe.setUid(UUID.randomUUID().toString().replace("-", ""));
			ppe.setProductCode(pc.getProductCode());
			ppe.setPrchType(pc.getPcProductinfoExt().getPrchType());
			ppe.setDlrId(pc.getPcProductinfoExt().getDlrId());
			ppe.setDlrNm(pc.getPcProductinfoExt().getDlrNm());
			ppe.setOaSiteNo(pc.getPcProductinfoExt().getOaSiteNo());
			ppe.setValidateFlag(pc.getPcProductinfoExt().getValidateFlag());
			ppe.setProductCodeOld(pc.getProductCodeOld());
			ppe.setProductStoreType(pc.getPcProductinfoExt().getProductStoreType());
			ppe.setProductTradeType(pc.getPcProductinfoExt().getProductTradeType());
			ppe.setPoffer(pc.getPcProductinfoExt().getPoffer());
			String fictitious = pc.getPcProductinfoExt().getFictitiousSales();
			ppe.setFictitiousSales(Integer.parseInt(StringUtils.isBlank(fictitious)? "0" : fictitious));
			String grossProfit = pc.getPcProductinfoExt().getGrossProfit();
			ppe.setGrossProfit(Long.parseLong(StringUtils.isBlank(grossProfit)? "0" : grossProfit));
			String accmrng = pc.getPcProductinfoExt().getAccmRng();
			ppe.setAccmRng(Double.parseDouble(StringUtils.isBlank(accmrng)? "0" : accmrng));
			ppe.setMdId(pc.getPcProductinfoExt().getMdId());
			ppe.setMdNm(pc.getPcProductinfoExt().getMdNm());
			ppe.setSettlementType(pc.getPcProductinfoExt().getSettlementType());
			ppe.setPurchaseType(pc.getPcProductinfoExt().getPurchaseType());
			ppe.setPicMaterialUrl(pc.getPcProductinfoExt().getPicMaterialUrl());
			ppe.setPicMaterialUpload(pc.getPcProductinfoExt().getPicMaterialUpload());
			ppe.setKjtSellerCode(pc.getPcProductinfoExt().getKjtSellerCode());
			ppe.setDeliveryStoreType(pc.getPcProductinfoExt().getDeliveryStoreType());
			ppem.insertSelective(ppe);
		}
				
		//插入商品历史流水信息
		if(pc.getPcProdcutflow() != null){
			
			com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
			
			ppf.setCreateTime(createTime);
			ppf.setCreator(operator);
			ppf.setFlowCode(pc.getPcProdcutflow().getFlowCode());
			ppf.setFlowStatus(pc.getPcProdcutflow().getFlowStatus());
			ppf.setProductCode(pc.getProductCode());
			ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
			ppf.setUpdateTime(createTime);
			ppf.setUpdator(operator);
			
			JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
			ppf.setProductJson(pHelper.ObjToString(pc));
			
			ppfm.insertSelective(ppf);
		}
		
		//插入商品库存流水
		if(pc.getProductSkuInfoList()!=null){
			List<ProductSkuInfo> skuList = pc.getProductSkuInfoList();
			
			for(ProductSkuInfo sku : skuList){
				com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
				
				lsModel.setChangeStock(sku.getStockNum());
				lsModel.setChangeType(SkuCommon.SkuStockChangeTypeCreateProduct);
				lsModel.setCode(sku.getSkuCode());
				lsModel.setCreateTime(createTime);
				lsModel.setCreateUser(operator);
				lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				lsom.insertSelective(lsModel);
			}
		}
	}
	
	/**
	 * 更新商品
	 * @param pc
	 * @param ret
	 * @param operator
	 */
	public void updateProduct(PcProductinfo pc,RootResult ret,String operator,ProductChangeFlag pcf){
		com.cmall.dborm.txmapper.PcProductcategoryRelMapper pptrm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductcategoryRelMapper");
		com.cmall.dborm.txmapper.PcProductdescriptionMapper ppsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductdescriptionMapper");
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.PcProductinfoMapper pcpm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoMapper");
		com.cmall.dborm.txmapper.PcProductpicMapper pppm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductpicMapper");
		com.cmall.dborm.txmapper.PcProductpropertyMapper pppmr = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductpropertyMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.ScStoreSkunumMapper sssm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsmd = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		com.cmall.dborm.txmapper.PcProductinfoExtMapper pcpem = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoExtMapper");
		com.cmall.dborm.txmapper.UcSellercategoryProductRelationMapper usprm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_UcSellercategoryProductRelationMapper");
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		//更新商品的基本信息
		com.cmall.dborm.txmodel.PcProductinfo ppModel = new com.cmall.dborm.txmodel.PcProductinfo();
		
		ppModel.setProductCode(pc.getProductCode());
		ppModel.setProductName(pc.getProdutName());
		ppModel.setMarketPrice(pc.getMarketPrice());
		ppModel.setBrandCode(pc.getBrandCode());
		ppModel.setProductWeight(pc.getProductWeight());
		ppModel.setProductVolume(pc.getProductVolume());
		ppModel.setProductVolumeItem(pc.getProductVolumeItem());
		ppModel.setSellProductcode(pc.getSellProductcode());
		ppModel.setLabels(pc.getLabels());
		ppModel.setTransportTemplate(pc.getTransportTemplate());
		ppModel.setFlagPayway(pc.getFlagPayway());
		ppModel.setCostPrice(pc.getCostPrice());
		ppModel.setProductShortname(pc.getProductShortname());
		ppModel.setSupplierName(pc.getSupplierName());
		ppModel.setVideoUrl(pc.getVideoUrl());
		ppModel.setProductDescVideo(pc.getProductDescVideo());
		ppModel.setVideoMainPic(pc.getVideoMainPic());
		ppModel.setTaxRate(pc.getTaxRate());//税率
		
		if(StringUtils.isNotBlank(pc.getTaxCode())) {
			ppModel.setTaxCode(pc.getTaxCode());
		}
		
		ppModel.setValidateFlag(pc.getValidate_flag());//添加 是否是虚拟商品 字段
		ppModel.setUpdateTime(createTime);
		ppModel.setProductCodeCopy(pc.getProductCodeCopy());
		ppModel.setMinSellPrice(pc.getMinSellPrice());
		ppModel.setMaxSellPrice(pc.getMaxSellPrice());
		
		// TV直播促销语
		ppModel.setTvTips(pc.getTvTips());
		
		ppModel.setAdpicUrl(pc.getAdPicUrl());			//广告图
		ppModel.setProductAdv(pc.getProductAdv());      //商品广告“卖点”
		ppModel.setAreaTemplate(pc.getAreaTemplate());	//限购地区
		ppModel.setExpiryDate(pc.getExpiryDate());     //保质期
		ppModel.setExpiryUnit(pc.getExpiryUnit());    //保质期单位
		ppModel.setQualificationCategoryCode(pc.getQualificationCategoryCode());//资质品类
		
		// 仅支持在线支付
		ppModel.setOnlinepayFlag(pc.getOnlinepayFlag());
		ppModel.setOnlinepayStart(pc.getOnlinepayStart());
		ppModel.setOnlinepayEnd(pc.getOnlinepayEnd());
		//是否参与会员日
		ppModel.setVipdayFlag(pc.getVipdayFlag());
		ppModel.setAccmYn(pc.getAccmYn());
		ppModel.setAutoSell(pc.getAutoSell());
		
		ppModel.setAfterSaleAddressUid(pc.getAfterSaleAddressUid());
		
		//更新商品的扩展信息
		com.cmall.dborm.txmodel.PcProductinfoExt ppExtModel = new com.cmall.dborm.txmodel.PcProductinfoExt();
//		ppExtModel.setUid(pc.getPcProductinfoExt().getUid());
		ppExtModel.setProductCode(pc.getPcProductinfoExt().getProductCode());
		ppExtModel.setPrchType(pc.getPcProductinfoExt().getPrchType());
		ppExtModel.setDlrId(pc.getPcProductinfoExt().getDlrId());
		ppExtModel.setDlrNm(pc.getPcProductinfoExt().getDlrNm());
		ppExtModel.setOaSiteNo(pc.getPcProductinfoExt().getOaSiteNo());
		
		String grossProfit = pc.getPcProductinfoExt().getGrossProfit();
		String accmRng = pc.getPcProductinfoExt().getAccmRng();
		String fictitiousSales = pc.getPcProductinfoExt().getFictitiousSales();
		ppExtModel.setGrossProfit((null == grossProfit || "".equals(grossProfit)) ? 0L : Long.parseLong(grossProfit));
		ppExtModel.setAccmRng((null == accmRng || "".equals(accmRng)) ? 0.0 : Double.parseDouble(accmRng));
		ppExtModel.setValidateFlag(pc.getPcProductinfoExt().getValidateFlag());
		ppExtModel.setMdId(pc.getPcProductinfoExt().getMdId());
		ppExtModel.setMdNm(pc.getPcProductinfoExt().getMdNm());
		ppExtModel.setKjtSellerCode(pc.getPcProductinfoExt().getKjtSellerCode());
		
		//暂时只支持家有汇修改虚拟销售量基数（2015-08-13开始支持惠家有与沙皮狗）
		try {
			ppExtModel.setFictitiousSales((null == fictitiousSales || "".equals(fictitiousSales)) ? 0 : Integer.parseInt(fictitiousSales));		
		} catch (NumberFormatException e) {
			ppExtModel.setFictitiousSales(0);
		}
		ppExtModel.setSettlementType(pc.getPcProductinfoExt().getSettlementType());	//结算方式 2015-08-24添加
		ppExtModel.setPurchaseType(pc.getPcProductinfoExt().getPurchaseType());		//采购类型2015-10-08添加
		
		ppExtModel.setPicMaterialUrl(pc.getPcProductinfoExt().getPicMaterialUrl());	//图片相关素材地址2015-12-03添加
		ppExtModel.setPicMaterialUpload(pc.getPcProductinfoExt().getPicMaterialUpload());//图片相关素材上传2015-12-03添加
		
		ppExtModel.setProductTradeType(pc.getPcProductinfoExt().getProductTradeType());
		ppExtModel.setDeliveryStoreType(pc.getPcProductinfoExt().getDeliveryStoreType());
		
		//商品扩展属性(暂只支持家有汇)(2015-08-13开始支持惠家有与沙皮狗)
		if (pcf.isChangeProductExt() && ("SI2009".equals(pc.getSellerCode()) || "SI2003".equals(pc.getSellerCode()) || "SI3003".equals(pc.getSellerCode()))) {
			PcProductinfoExtExample extExample = new PcProductinfoExtExample();
			extExample.createCriteria().andProductCodeEqualTo(ppModel.getProductCode());
			pcpem.updateByExampleSelective(ppExtModel, extExample);
		}
		if(pcf.isChangeProductPic()){

			//图片操作--先删除--再添加
			PcProductpicExample ppeModel = new PcProductpicExample();
			ppeModel.createCriteria().andProductCodeEqualTo(pc.getProductCode());
			pppm.deleteByExample(ppeModel);
			
			//插入商品图片信息
			if(pc.getPcPicList()!=null){
				
				//如果商品主图不为空和轮播图列表不为0的时候商品主图为轮播图第一张 
				//edit 内容 （新增判断条件(null == pc.getMainPicUrl() || "".equals(pc.getMainPicUrl())）
				if((null == pc.getMainPicUrl() || "".equals(pc.getMainPicUrl())) && pc.getPcPicList().size()>0){	
					ppModel.setMainpicUrl(pc.getPcPicList().get(0).getPicUrl());
				}else{
					ppModel.setMainpicUrl(pc.getMainPicUrl());
				}
				
				List<PcProductpic> picList = pc.getPcPicList();
				
				for(PcProductpic pic:picList){
					com.cmall.dborm.txmodel.PcProductpic picModel = new com.cmall.dborm.txmodel.PcProductpic();
					picModel.setPicUrl(pic.getPicUrl());
					picModel.setProductCode(pc.getProductCode());
					picModel.setSkuCode(pic.getSkuCode());
					picModel.setUid(UUID.randomUUID().toString().replace("-", ""));
					pppm.insertSelective(picModel);
				}
			}
		}
		
		PcProductinfoExample example = new PcProductinfoExample();
		example.createCriteria().andProductCodeEqualTo(ppModel.getProductCode());
		
		//如果图片换了
		if(!ppModel.getMainpicUrl().equals(pcf.getOldPicUrl())){
			
			PcSkuinfoExample psexample = new PcSkuinfoExample();
			psexample.createCriteria().andProductCodeEqualTo(ppModel.getProductCode()).andSkuPicurlEqualTo(pcf.getOldPicUrl());
			PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
			psModel.setSkuPicurl(ppModel.getMainpicUrl());
			pcsm.updateByExampleSelective(psModel , psexample);
		}
		//如果名字改了
		if(!ppModel.getProductName().equals(pcf.getOldProductName())){
			
			PcSkuinfoExample psexample = new PcSkuinfoExample();
			psexample.createCriteria().andProductCodeEqualTo(ppModel.getProductCode());
			PcSkuinfoWithBLOBsForD psModel = new PcSkuinfoWithBLOBsForD();
			psModel.setOldProductName(pcf.getOldProductName());
			psModel.setNewProductName(ppModel.getProductName());
			pcsmd.updateByExampleSelectiveForSkuName(psModel, psexample);
			
		}
		
		if(pcf.isChangeProductMain())
			pcpm.updateByExampleSelective(ppModel, example);
		
		if(pcf.isChangeProductSku()){
			//插入或者更新sku信息
			if(pc.getProductSkuInfoList()!=null){
				List<ProductSkuInfo> skuList = pc.getProductSkuInfoList();
				
				for (int i = 0; i < skuList.size(); i++) {
					ProductSkuInfo sku = skuList.get(i);
					
					// 替换SKU名称中的"共同"为空白
					sku.setSkuName(sku.getSkuName().replaceAll("\\s+共同", "").replaceAll("\\s+", " ").trim());
					
					//新增DSF开头，只是用来对第三方商户商品做特殊判断，入库时需要把DSF去掉，并且skuCode无须再次生成--lgj
					if(sku.getSkuCode() == null || sku.getSkuCode().equals("") || sku.getSkuCode().startsWith("WSP") || sku.getSkuCode().startsWith("DSF")){
						if(sku.getSkuKey() == null || sku.getSkuKey().equals("")){
							continue;
						}
						com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
						psModel.setSellerCode(pc.getSellerCode());
						if (sku.getSkuCode().startsWith("DSF")) {
							pc.getProductSkuInfoList().get(i).setSkuCode(sku.getSkuCode().replace("DSF", ""));
							psModel.setSkuCode(sku.getSkuCode().replace("DSF", ""));
						}else{
							psModel.setSkuCode(WebHelper.upCode(SkuCommon.SKUHead));
						}
						psModel.setSkuCodeOld(sku.getSkuCodeOld());
						pc.getProductSkuInfoList().get(i).setSkuCode(psModel.getSkuCode());
						psModel.setMarketPrice(sku.getMarketPrice());
						psModel.setProductCode(pc.getProductCode());
						psModel.setProductCodeOld("");
						psModel.setQrcodeLink(sku.getQrcodeLink());
						psModel.setSecurityStockNum(Long.valueOf(sku.getSecurityStockNum()));
						psModel.setSellPrice(sku.getSellPrice());
						psModel.setSellProductcode(sku.getSellProductcode());
						psModel.setSkuKey(sku.getSkuKey());
						psModel.setSkuKeyvalue(sku.getSkuValue());
						psModel.setSkuName(sku.getSkuName());
						psModel.setSkuAdv(sku.getSkuAdv());
						psModel.setStockNum(Long.valueOf(sku.getStockNum()));
						psModel.setUid(UUID.randomUUID().toString().replace("-", ""));
						psModel.setBarcode(sku.getBarcode());
						psModel.setMiniOrder(sku.getMiniOrder());
						psModel.setCostPrice(sku.getCostPrice());
						psModel.setStockNum(Long.parseLong(sku.getStockNum()+""));
						
						if(sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")){
							psModel.setSkuPicurl(pc.getMainPicUrl());
						}else{
							psModel.setSkuPicurl(sku.getSkuPicUrl());
						}
						
						pcsm.insertSelective(psModel);
						
						if (sku.getScStoreSkunumList() != null) {
							int changeStock = 0;		//库存变化总量
							for (ScStoreSkunum scStore : sku.getScStoreSkunumList()) {
								//插入库存
								com.cmall.dborm.txmodel.ScStoreSkunum sssModel = new com.cmall.dborm.txmodel.ScStoreSkunum();
								sssModel.setUid(UUID.randomUUID().toString().replace("-", ""));
								sssModel.setSkuCode(psModel.getSkuCode());		//此处取skuCode一定要取psModel里面的。
								sssModel.setStockNum(scStore.getStockNum());
								sssModel.setStoreCode(scStore.getStoreCode());
								sssm.insertSelective(sssModel);
								changeStock += Integer.parseInt(scStore.getStockNum()+"");
							}
							com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
							lsModel.setChangeStock(changeStock);
							lsModel.setChangeType(SkuCommon.SkuStockChangeTypeCreateProduct);
							lsModel.setCode(psModel.getSkuCode());
							lsModel.setCreateTime(createTime);
							lsModel.setCreateUser(operator);
							lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
							lsom.insertSelective(lsModel);
						}
					}else{
						com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
						psModel.setSkuCode(sku.getSkuCode());
						psModel.setSkuCodeOld(sku.getSkuCodeOld());
						psModel.setSkuAdv(sku.getSkuAdv());
						psModel.setSecurityStockNum(Long.valueOf(sku.getSecurityStockNum()));
						psModel.setSkuName(sku.getSkuName());
						psModel.setSellPrice(sku.getSellPrice());
						psModel.setSellProductcode(sku.getSellProductcode());
						psModel.setBarcode(sku.getBarcode());
						psModel.setMiniOrder(sku.getMiniOrder());
						psModel.setSaleYn(sku.getSaleYn());
						psModel.setCostPrice(sku.getCostPrice());
						psModel.setStockNum(Long.parseLong(sku.getStockNum()+""));
						//解决商户后台草稿箱中被驳回的商品修改规格型号名字不存库的bug
						psModel.setSkuKey(sku.getSkuKey());
						psModel.setSkuKeyvalue(sku.getSkuKeyvalue());
						if(sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")){
							psModel.setSkuPicurl(pc.getMainPicUrl());
						}else{
							psModel.setSkuPicurl(sku.getSkuPicUrl());
						}
						
						PcSkuinfoExample psexample = new PcSkuinfoExample();
						psexample.createCriteria().andSkuCodeEqualTo(sku.getSkuCode());
						pcsm.updateByExampleSelective(psModel, psexample);
						if (sku.getScStoreSkunumList() != null) {
							int changeStock = 0;		//库存变化总量
							int oldStock = 0;
							int nowStock = 0;
							boolean flagModStock = false;
							for (ScStoreSkunum scStore : sku.getScStoreSkunumList()) {
								//更新库存
								MDataMap mDataMap = DbUp.upTable("sc_store_skunum").one("sku_code",sku.getSkuCode());
								com.cmall.dborm.txmodel.ScStoreSkunum sssModel = new com.cmall.dborm.txmodel.ScStoreSkunum();
								if (null != mDataMap) {
									sssModel.setUid(mDataMap.get("uid"));
									sssModel.setSkuCode(sku.getSkuCode());
									sssModel.setStockNum(scStore.getStockNum());
									sssModel.setStoreCode(scStore.getStoreCode());
									sssModel.setBatchCode(mDataMap.get("batch_code"));
									
									nowStock = Integer.parseInt(scStore.getStockNum()+"");
									oldStock = Integer.parseInt(mDataMap.get("stock_num"));
								}
								changeStock += (nowStock - oldStock);
								if (nowStock != oldStock ) {
									ScStoreSkunumExample sssexample = new ScStoreSkunumExample();
									sssexample.createCriteria().andUidEqualTo(sssModel.getUid());
									sssm.updateByExampleSelective(sssModel, sssexample);
									flagModStock = true;
								}
								//这个if必须写在这里。不能再这个if (nowStock != oldStock ) {}上面。
								if(null == mDataMap){
									//如果没有库存则添加
									sssModel.setUid(UUID.randomUUID().toString().replace("-", ""));
									sssModel.setSkuCode(psModel.getSkuCode());		//此处取skuCode一定要取psModel里面的。
									sssModel.setStockNum(scStore.getStockNum());
									sssModel.setStoreCode(scStore.getStoreCode());
									sssm.insertSelective(sssModel);
									nowStock = Integer.parseInt(scStore.getStockNum()+""); 
									flagModStock = true;
								}
							}
							if (flagModStock) {
								com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
								lsModel.setCode(sku.getSkuCode());
								lsModel.setCreateTime(createTime);
								lsModel.setCreateUser(operator);
								lsModel.setChangeStock(changeStock);
								lsModel.setOldStock(oldStock);
								lsModel.setNowStock(nowStock);
								lsModel.setInfo("商品库存被修改！");
								lsModel.setChangeType(SkuCommon.SkuStockChangeTypeChangeProduct);
								lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
								lsom.insertSelective(lsModel);
							}
						}
					}
				}
			}
		}
		
		
		if(pcf.isChangeSkuPropertyMain()){
			
			//属性信息操作--先删除--再添加
			PcProductpropertyExample ppteample = new PcProductpropertyExample();
			List<String> values = new ArrayList<String>();
			values.add("449736200001");
			values.add("449736200002");
			ppteample.createCriteria().andProductCodeEqualTo(pc.getProductCode())
			.andPropertyTypeIn(values );
			pppmr.deleteByExample(ppteample);
			
			//插入商品属性信息
			if(pc.getPcProductpropertyList()!=null){
				
				List<PcProductproperty> pppList = pc.getPcProductpropertyList();
				
				for(PcProductproperty ppp : pppList){
					
					if(ppp.getPropertyType().equals("449736200001") || ppp.getPropertyType().equals("449736200002")){
						com.cmall.dborm.txmodel.PcProductproperty pppModel = new com.cmall.dborm.txmodel.PcProductproperty();
						
						pppModel.setProductCode(pc.getProductCode());
						pppModel.setPropertyCode(ppp.getPropertyCode());
						pppModel.setPropertyKey(ppp.getPropertyKey());
						pppModel.setPropertyKeycode(ppp.getPropertyKeycode());
						pppModel.setPropertyType(ppp.getPropertyType());
						pppModel.setPropertyValue(ppp.getPropertyValue());
						pppModel.setUid(UUID.randomUUID().toString().replace("-", ""));
						
						pppmr.insertSelective(pppModel);
					}
				}
			}
		}
		
		
		if(pcf.isChangeSkuPropertySub()){
			
			//属性信息操作--先删除--再添加
			PcProductpropertyExample ppteample = new PcProductpropertyExample();
			List<String> values = new ArrayList<String>();
			values.add("449736200003");
			values.add("449736200004");
			ppteample.createCriteria().andProductCodeEqualTo(pc.getProductCode())
			.andPropertyTypeIn(values );
			pppmr.deleteByExample(ppteample);
			
			//插入商品属性信息
			if(pc.getPcProductpropertyList()!=null){
				
				List<PcProductproperty> pppList = pc.getPcProductpropertyList();
				
				for(PcProductproperty ppp : pppList){
					
					if(ppp.getPropertyType().equals("449736200003") || ppp.getPropertyType().equals("449736200004")){
						com.cmall.dborm.txmodel.PcProductproperty pm = new com.cmall.dborm.txmodel.PcProductproperty();
						
						pm.setType(ppp.getType());
						pm.setStartDate(ppp.getStartDate());
						pm.setEndDate(ppp.getEndDate()); 
						pm.setProductCode(pc.getProductCode());
						pm.setPropertyCode(ppp.getPropertyCode());
						pm.setPropertyKey(ppp.getPropertyKey());
						pm.setPropertyKeycode(ppp.getPropertyKeycode());
						pm.setPropertyType(ppp.getPropertyType());
						pm.setPropertyValue(ppp.getPropertyValue());
						pm.setUid(UUID.randomUUID().toString().replace("-", ""));
						
						pppmr.insertSelective(pm);
					}
				}
			}
			
		}
		
		if(pcf.isChangeProductFlow()){
			//插入商品历史流水信息
			if(pc.getPcProdcutflow() != null){
				
				com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
				
				ppf.setCreateTime(createTime);
				ppf.setCreator(operator);
				ppf.setFlowCode(pc.getPcProdcutflow().getFlowCode());
				ppf.setFlowStatus(pc.getPcProdcutflow().getFlowStatus());
				ppf.setProductCode(pc.getProductCode());
				
				JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
				ppf.setProductJson(pHelper.ObjToString(pc));
				
				ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
				ppf.setUpdateTime(createTime);
				ppf.setUpdator(operator);
				
				ppfm.insertSelective(ppf);
			}
		}
	
		
		if(pcf.isChangeDescription()){
			
			//描述信息操作--先删除--再添加
			PcProductdescriptionExample pppeamle = new PcProductdescriptionExample();
			pppeamle.createCriteria().andProductCodeEqualTo(pc.getProductCode());
			ppsm.deleteByExample(pppeamle);
			//添加 描述信息
			if(pc.getDescription()!=null){
				com.cmall.dborm.txmodel.PcProductdescriptionWithBLOBs ppdModel = new PcProductdescriptionWithBLOBs();
				ppdModel.setProductCode(pc.getProductCode());
				ppdModel.setKeyword(pc.getDescription().getKeyword());
				ppdModel.setDescriptionInfo(pc.getDescription().getDescriptionInfo());
				ppdModel.setDescriptionPic(pc.getDescription().getDescriptionPic());	
				ppdModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				
				ppsm.insertSelective(ppdModel);
			}
			
		}
		if(pcf.isChangeProductCategory()){
		if(pc.getUsprList()!=null && pc.getUsprList().size() > 0){
			UcSellercategoryProductRelationExample uspreamle = new UcSellercategoryProductRelationExample();
			uspreamle.createCriteria().andProductCodeEqualTo(pc.getProductCode());
			usprm.deleteByExample(uspreamle);
			for (int i = 0; i < pc.getUsprList().size(); i++) {
				com.cmall.dborm.txmodel.UcSellercategoryProductRelation usprModel = new UcSellercategoryProductRelation();
				usprModel.setCategoryCode(pc.getUsprList().get(i).getCategoryCode());
				usprModel.setProductCode(pc.getProductCode());
				usprModel.setSellerCode(pc.getSellerCode());
				usprModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				usprm.insertSelective(usprModel);
			}
		}
		}
	}
	
	/**
	 * 添加sku
	 * @param psi
	 * @param ret
	 * @param operator
	 */
	public void addSku(ProductSkuInfo psi,RootResult ret,String operator){
		
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.ScStoreSkunumMapper sssm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		psModel.setSellerCode(psi.getSellerCode());
		psModel.setSkuCode(WebHelper.upCode(SkuCommon.SKUHead));
		//psModel.setMarketPrice(BigDecimal.valueOf(psi.getMarketPrice()));
		psModel.setProductCode(psi.getProductCode());
		psModel.setSecurityStockNum(Long.valueOf(psi.getSecurityStockNum()));
		psModel.setSellPrice(psi.getSellPrice());
		psModel.setSellProductcode(psi.getSellProductcode());
		psModel.setSkuKey(psi.getSkuKey());
		psModel.setSkuKeyvalue(psi.getSkuValue());
		psModel.setSkuName(psi.getSkuName());
		psModel.setSkuAdv(psi.getSkuAdv());
		psModel.setSkuPicurl(psi.getSkuPicUrl());
		psModel.setStockNum(Long.valueOf(psi.getStockNum()));
		psModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		psModel.setBarcode(psi.getBarcode());
		pcsm.insertSelective(psModel);
		//插入商品sku库存
		if(psi.getScStoreSkunumList()!=null){
			List<ScStoreSkunum> skuStoreList = psi.getScStoreSkunumList();
			for(ScStoreSkunum skuStore : skuStoreList){
				com.cmall.dborm.txmodel.ScStoreSkunum sssModel = new com.cmall.dborm.txmodel.ScStoreSkunum();
				sssModel.setUid(UUID.randomUUID().toString().replace("-", ""));
				sssModel.setSkuCode(psi.getSkuCode());
				sssModel.setStockNum(skuStore.getStockNum());
				sssModel.setStoreCode(skuStore.getStoreCode());
				sssModel.setBatchCode(skuStore.getBatchCode());
				sssm.insertSelective(sssModel);
			}
		}
		//库存变动日志
		com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
		lsModel.setChangeStock(psi.getStockNum());
		lsModel.setChangeType(SkuCommon.SkuStockChangeTypeCreateProduct);
		lsModel.setOldStock(0);
		lsModel.setNowStock(psi.getStockNum());
		lsModel.setCode(psi.getSkuCode());
		lsModel.setCreateTime(createTime);
		lsModel.setCreateUser(operator);
		lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		
		
		lsom.insertSelective(lsModel);	
		
		com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
		//添加日志
		ppf.setCreateTime(createTime);
		ppf.setCreator(operator);
		ppf.setFlowCode(psi.getSkuCode());
		ppf.setFlowStatus("add-sku");
		ppf.setProductCode(psi.getProductCode());
		//ppf.setProductJson(pc.getPcProdcutflow().getProductJson());
		ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppf.setUpdateTime(createTime);
		ppf.setUpdator(operator);
		
		JsonHelper<ProductSkuInfo> pHelper=new JsonHelper<ProductSkuInfo>();
		ppf.setProductJson(pHelper.ObjToString(psi));
		ppfm.insertSelective(ppf);
	}
	
	/**
	 * 更新sku基本
	 * @param sku
	 * @param ret
	 * @param operator
	 */
	public void updateSkuBase(ProductSkuInfo sku,RootResult ret,String operator){
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		
		
		psModel.setSellPrice(sku.getSellPrice());
		psModel.setStockNum(Long.valueOf(sku.getStockNum()));
		
		PcSkuinfoExample psexample = new PcSkuinfoExample();
		psexample.createCriteria().andSkuCodeEqualTo(sku.getSkuCode());
		pcsm.updateByExampleSelective(psModel, psexample);
		
		//库存变动日志
		com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
		lsModel.setChangeStock(sku.getStockNum());
		lsModel.setChangeType(SkuCommon.SkuStockChangeTypeChangeProduct);
		lsModel.setCode(sku.getSkuCode());
		lsModel.setCreateTime(createTime);
		lsModel.setCreateUser(operator);
		lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		
		
		lsom.insertSelective(lsModel);	
		
		
		//添加日志
		com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
		
		ppf.setCreateTime(createTime);
		ppf.setCreator(operator);
		ppf.setFlowCode(sku.getSkuCode());
		ppf.setFlowStatus("update-sku-base");
		ppf.setProductCode(sku.getProductCode());
		//ppf.setProductJson(pc.getPcProdcutflow().getProductJson());
		ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppf.setUpdateTime(createTime);
		ppf.setUpdator(operator);
		
		JsonHelper<ProductSkuInfo> pHelper=new JsonHelper<ProductSkuInfo>();
		ppf.setProductJson(pHelper.ObjToString(sku));
		ppfm.insertSelective(ppf);
	}
	
	
	/**
	 * 更新sku基本
	 * @param sku
	 * @param ret
	 * @param operator
	 */
	public void updateSkuOther(ProductSkuInfo sku,RootResult ret,String operator){
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		
	
		// 替换SKU名称中的"共同"为空白
		sku.setSkuName(sku.getSkuName().replaceAll("\\s+共同", "").replaceAll("\\s+", " ").trim());
		
		psModel.setSkuAdv(sku.getSkuAdv());
		psModel.setSecurityStockNum(Long.valueOf(sku.getSecurityStockNum()));
		psModel.setSkuName(sku.getSkuName());
		psModel.setSellProductcode(sku.getSellProductcode());
		psModel.setSkuPicurl(sku.getSkuPicUrl());
		
		//家有汇修改sku的市场价 add by ligj
		if (sku.getMarketPrice().compareTo(new BigDecimal(0)) > 0) {
			psModel.setMarketPrice(sku.getMarketPrice());
		}
		
		/*
		if(sku.getVirtualMoneyDeduction() == 0){
			psModel.setVirtualMoneyDeduction(0l);
		}else{
			psModel.setVirtualMoneyDeduction(Long.parseLong(String.valueOf(sku.getVirtualMoneyDeduction())));
		}
		*/
		
		
		PcSkuinfoExample psexample = new PcSkuinfoExample();
		psexample.createCriteria().andSkuCodeEqualTo(sku.getSkuCode());
		pcsm.updateByExampleSelective(psModel, psexample);
		
		//添加日志
		com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
		
		ppf.setCreateTime(createTime);
		ppf.setCreator(operator);
		ppf.setFlowCode(sku.getSkuCode());
		ppf.setFlowStatus(SkuCommon.ProUpaOr);
		ppf.setProductCode(sku.getProductCode());
		//ppf.setProductJson(pc.getPcProdcutflow().getProductJson());
		ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppf.setUpdateTime(createTime);
		ppf.setUpdator(operator);
		
		JsonHelper<ProductSkuInfo> pHelper=new JsonHelper<ProductSkuInfo>();
		ppf.setProductJson(pHelper.ObjToString(sku));
		ppfm.insertSelective(ppf);
	}

	/**
	 * 删除sku
	 * @param skuCode
	 * @param ret
	 * @param operator
	 */
	public void deleteSku(ProductSkuInfo sku,RootResult ret,String operator){
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		PcSkuinfoExample psexample = new PcSkuinfoExample();
		psexample.createCriteria().andSkuCodeEqualTo(sku.getSkuCode());
		pcsm.deleteByExample(psexample);
		
		//添加日志
		com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		ppf.setCreateTime(createTime);
		ppf.setCreator(operator);
		ppf.setFlowCode(sku.getSkuCode());
		ppf.setFlowStatus("delete-sku");
		ppf.setProductCode(sku.getProductCode());
		//ppf.setProductJson(pc.getPcProdcutflow().getProductJson());
		ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppf.setUpdateTime(createTime);
		ppf.setUpdator(operator);
		
	
		ppfm.insertSelective(ppf);
	}
	
	
	public void updatetestSku(){
		/*com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		
		
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		psModel.setSellerCode("");
		psModel.setSkuCode("8019400053");
		//psModel.setMarketPrice(BigDecimal.valueOf(psi.getMarketPrice()));
		psModel.setProductCode("");
		psModel.setSecurityStockNum(1l);
		psModel.setSellPrice(BigDecimal.valueOf(1l));
		psModel.setSellProductcode("aa");
		psModel.setSkuKey("");
		psModel.setSkuKeyvalue("");
		psModel.setSkuName("");
		psModel.setSkuAdv("");
		psModel.setSkuPicurl("");
		psModel.setStockNum(12l);
		
		
		PcSkuinfoExample psexample = new PcSkuinfoExample();
		psexample.createCriteria().andSkuCodeEqualTo(psModel.getSkuCode());
		
		int count = pcsm.updateByExampleSelective(psModel, psexample);
		if(count == 0){
			psModel.setUid(UUID.randomUUID().toString().replace("-", ""));
			pcsm.insertSelective(psModel);
		}
		
		psModel.setStockNum(4l);
		pcsm.updateByExampleSelective(psModel, psexample);
		
		psModel.setStockNum(5l);
		pcsm.updateByExampleSelective(psModel, psexample);*/
		
	}
	
	/**
	 * 保存属性
	 * @param list
	 */
	public void updateProductProperty(List<MProductProperty> list){
		
		com.cmall.dborm.txmapper.PcProductpropertyMapper pppmr = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductpropertyMapper");
		
		for(MProductProperty mp : list){
			com.cmall.dborm.txmodel.PcProductproperty record = new com.cmall.dborm.txmodel.PcProductproperty();
			PcProductpropertyExample example = new PcProductpropertyExample();
			
			example.createCriteria().andProductCodeEqualTo(mp.getProductCode()).andPropertyCodeEqualTo(mp.getPropertyCode())
			.andPropertyKeycodeEqualTo(mp.getPropertyKeycode()).andPropertyTypeEqualTo(mp.getPropertyType());
			
			
			record.setBigSort(mp.getBigSort());
			record.setSmallSort(mp.getSmallSort());
			record.setProductCode(mp.getProductCode());
			record.setPropertyCode(mp.getPropertyCode());
			record.setPropertyKeycode(mp.getPropertyKeycode());
			record.setPropertyType(mp.getPropertyType());
			
			
			pppmr.updateByExampleSelective(record, example);
			
		
		}
	}
	
	
	/**
	 * @param sku
	 * @param ret
	 * @param operator
	 * @throws Exception 
	 */
	public void updateSkuStock(ProductSkuInfo sku,RootResult ret,String operator,String appCode) throws Exception {
		com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.LcPricechangeMapper lporm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcPricechangeMapper");
		
		TxStockService txStockService = BeansHelper.upBean("bean_com_cmall_systemcenter_txservice_TxStockService");//分库存操作
		
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		
		
		MDataMap oldinfo = DbUp.upTable("pc_skuinfo").one("sku_code",sku.getSkuCode(),"product_code",sku.getProductCode());
		PcSkuinfoWithBLOBs pswb = new PcSkuinfoWithBLOBs();
		PcSkuinfoExample example = new PcSkuinfoExample();
		long stockNum = Long.parseLong(String.valueOf(-sku.getStockNum()));
		//赋值
		pswb.setSkuCode(sku.getSkuCode());
		pswb.setStockNum(stockNum);
		pswb.setSellPrice(sku.getSellPrice());
		
		//-------------------------
		//这里加入惠美丽分库的东东
		if(appCode!=null&&AppConst.MANAGE_CODE_CAPP.equals(appCode)){
			txStockService.doChangeStock(ret, stockNum, sku.getSkuCode());
			pswb.setStockNum(0l);//pc_skuinfo中的库存不改，并且可以避免异常问题
		}
		if(appCode!=null&&AppConst.MANAGE_CODE_CYOUNG.equals(appCode)){
			txStockService.doChangeStock(ret, stockNum, sku.getSkuCode(), AppConst.CYOUNG_STORE_CODE);
			pswb.setStockNum(0l);//pc_skuinfo中的库存不改，并且可以避免异常问题
		}
		if(appCode!=null&&AppConst.MANAGE_CODE_CDOG.equals(appCode)){
			txStockService.doChangeStock(ret, stockNum, sku.getSkuCode(), AppConst.CDOG_STORE_CODE);
			pswb.setStockNum(0l);//pc_skuinfo中的库存不改，并且可以避免异常问题
		}
		
		//------------------------
		if(AppConst.MANAGE_CODE_CDOG.equals(UserFactory.INSTANCE.create().getManageCode())){//沙皮狗修改内存
			txStockService.doChangeStock(ret, stockNum, sku.getSkuCode(), AppConst.CDOG_STORE_CODE);
			pswb.setStockNum(0l);//pc_skuinfo中的库存不改，并且可以避免异常问题
		}
		//------------------------
		
		
		//条件
		//等于
//		example.createCriteria().andSkuCodeEqualTo(sku.getSkuCode()).andStockNumGreaterThanOrEqualTo(stockNum);
		example.createCriteria().andSkuCodeEqualTo(sku.getSkuCode()).andStockNumGreaterThanOrEqualTo(pswb.getStockNum());
		//大于等于
		//example.createCriteria();
		//减库存
		//如果返回值为 1 则继续，否则抛异常
		int count = pcsm.updateByExampleSelective(pswb, example);
		
		if(count<=0){
			
			ret.setResultCode(941901003);
			ret.setResultMessage(bInfo(941901003, sku.getSkuCode()));
			throw new Exception(ret.getResultMessage());
		}
		
		
		
		//库存变动日志
		com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
		lsModel.setChangeStock(sku.getStockNum());
		lsModel.setChangeType(SkuCommon.SkuStockChangeTypeChangeProduct);
		lsModel.setOldStock(Integer.valueOf(oldinfo.get("stock_num")));
		lsModel.setNowStock(Integer.valueOf(oldinfo.get("stock_num"))+sku.getStockNum());
		lsModel.setCode(sku.getSkuCode());
		lsModel.setCreateTime(createTime);
		lsModel.setCreateUser(operator);
		lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		lsom.insertSelective(lsModel);	
		
		
		//添加日志
		com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
		
		ppf.setCreateTime(createTime);
		ppf.setCreator(operator);
		ppf.setFlowCode(sku.getSkuCode());
		ppf.setFlowStatus("update-sku-base");
		ppf.setProductCode(sku.getProductCode());
		//ppf.setProductJson(pc.getPcProdcutflow().getProductJson());
		ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppf.setUpdateTime(createTime);
		ppf.setUpdator(operator);
//		
		JsonHelper<ProductSkuInfo> pHelper=new JsonHelper<ProductSkuInfo>();
		ppf.setProductJson(pHelper.ObjToString(sku));
		ppfm.insertSelective(ppf);
		
		//sku价格变动日志
		com.cmall.dborm.txmodel.LcPricechange lpModel = new LcPricechange();
		lpModel.setUid(UUID.randomUUID().toString().replace("-", ""));
		lpModel.setProductCode(oldinfo.get("product_code"));
		lpModel.setSkuCode(oldinfo.get("sku_code"));
		lpModel.setOldPrice(new BigDecimal(Double.parseDouble(oldinfo.get("sell_price"))));
		lpModel.setNowPrice(sku.getSellPrice());
		lpModel.setInfo(pHelper.ObjToString(sku));
		lpModel.setCreateTime(createTime);
		lpModel.setCreateUser(operator);
		lporm.insertSelective(lpModel);
		
	}
	
	
	private void updateSkuStock() {
		

	}
	
	/**
	 * 入库存
	 * @param list
	 * @param ret
	 * @param operator
	 * @throws Exception 
	 */
	public void updateSkuStockIn(List<BcPurchaseDetail> list,RootResult ret,String operator) throws Exception{
		
		com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		
		com.cmall.dborm.txmapper.PcStockDetailMapperForD pcdm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcStockDetailMapperForD");
		
		com.cmall.dborm.txmapper.PcStockInfoMapperForD psm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcStockInfoMapperForD");
		
		
		
		for(BcPurchaseDetail bd : list){
			//sku表 加库存
			PcSkuinfoWithBLOBs pswb = new PcSkuinfoWithBLOBs();
			PcSkuinfoExample example = new PcSkuinfoExample();
			Integer stockNum = -bd.getInNumber();
			pswb.setStockNum(Long.parseLong(String.valueOf(stockNum)));
			example.createCriteria().andSkuCodeEqualTo(bd.getGoodsCode());
			
			int count = pcsm.updateByExampleSelective(pswb, example);
			if(count<=0){
				
				ret.setResultCode(941901070);
				ret.setResultMessage(bInfo(941901070, bd.getGoodsCode()));
				throw new Exception(ret.getResultMessage());
			}			
			//库存主表 加库存
			PcStockInfo psi = new PcStockInfo();
			PcStockInfoExample psie = new PcStockInfoExample();
			stockNum = -bd.getInNumber();
			psi.setStockNumber(stockNum);
			psie.createCriteria().andSkuCodeEqualTo(bd.getGoodsCode());
			count = psm.updateByExampleSelective(psi, psie);
			
			//如果不存在，则插入
			if(count<=0){
				psi.setSkuCode(bd.getGoodsCode());
				psi.setStockNumber(bd.getInNumber());
				psi.setUid(UUID.randomUUID().toString().replace("-", ""));
				psm.insertSelective(psi);
			}
			
			//库存明细表  加库存
			PcStockDetail psd = new PcStockDetail();
			PcStockDetailExample psde = new PcStockDetailExample();
			stockNum = -bd.getInNumber();
			
			psd.setStockNumber(stockNum);
			psd.setStockArea("449746450001");
			
			//如果是采购单，批次是采购编号,否则如果为后台调整，则为空!
			psd.setStockBatch(bd.getPurchaseorderCode());
			psd.setQualityTime(bd.getEndDate());
			psd.setSkuCode(bd.getGoodsCode());
			
			psde.createCriteria()
				.andSkuCodeEqualTo(bd.getGoodsCode())
				.andStockAreaEqualTo(psd.getStockArea())
				.andStockBatchEqualTo(psd.getStockBatch());
			
			count = pcdm.updateByExampleSelective(psd, psde);
			if(count<=0){
				psd.setUid(UUID.randomUUID().toString().replace("-", ""));
				psd.setStockNumber(bd.getInNumber());
				pcdm.insertSelective(psd);
			}
		}
	}
	
	
	/**
	 * 出库- 
	 * @param sku
	 * @param ret
	 * @param operator
	 * @throws Exception 
	 */
	public void updateSkuStockOut(List<PcStockDetail> list,RootResult ret,String operator) throws Exception{
		com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		
		com.cmall.dborm.txmapper.PcStockDetailMapperForD pcdm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcStockDetailMapperForD");
		
		com.cmall.dborm.txmapper.PcStockInfoMapperForD psm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcStockInfoMapperForD");
		
		
		
		//对出库的数据进行整合，把 多个sku 整合成一个
		
		List<PcStockDetail> resultlist = new ArrayList<PcStockDetail>();
		MDataMap mdmHash = new MDataMap();
		
		for(PcStockDetail bd : list){
			if(mdmHash.containsKey(bd.getSkuCode())){
				mdmHash.put(bd.getSkuCode(), String.valueOf(Integer.parseInt(mdmHash.get(bd.getSkuCode())) + bd.getStockNumber()));
			}else{
				mdmHash.put(bd.getSkuCode(),String.valueOf(bd.getStockNumber()));
			}
		}
		
		List<String> keys = mdmHash.upKeys();
		
		for(String skuCode : keys){
			
			//sku表 减库存
			PcSkuinfoWithBLOBs pswb = new PcSkuinfoWithBLOBs();
			PcSkuinfoExample example = new PcSkuinfoExample();
			Integer stockNum = Integer.parseInt(mdmHash.get(skuCode));
			pswb.setStockNum(Long.parseLong(String.valueOf(stockNum)));
			example.createCriteria()
				.andSkuCodeEqualTo(skuCode)
				.andStockNumGreaterThanOrEqualTo(Long.parseLong(String.valueOf(stockNum)));
			
			int count = pcsm.updateByExampleSelective(pswb, example);
			if(count<=0){
				
				ret.setResultCode(941901003);
				ret.setResultMessage(bInfo(941901003, skuCode));
				throw new Exception(ret.getResultMessage());
			}			
			//库存主表 减库存
			PcStockInfo psi = new PcStockInfo();
			PcStockInfoExample psie = new PcStockInfoExample();
			
			psi.setStockNumber(stockNum);
			psie.createCriteria()
			.andSkuCodeEqualTo(skuCode)
			.andStockNumberGreaterThanOrEqualTo(stockNum);
			count = psm.updateByExampleSelective(psi, psie);
			
			//如果不存在，则报异常
			if(count<=0){
				ret.setResultCode(941901003);
				ret.setResultMessage(bInfo(941901003, skuCode));
				throw new Exception(ret.getResultMessage());
			}
			
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("sku_code", skuCode);
			
			PcStockDetailExample psde1 = new PcStockDetailExample();
			psde1.createCriteria()
				.andSkuCodeEqualTo(skuCode)
				.andStockNumberGreaterThan(0);
			psde1.setOrderByClause("stock_batch");

			List<PcStockDetail> mList = pcdm.selectByExample(psde1);
			
			
			int allNeedNum = stockNum;
			int tempNum = 0;
			//校验库存是否充足
			for(PcStockDetail detail : mList){
				tempNum += detail.getStockNumber();
			}
			
			if(allNeedNum<=tempNum){
				boolean isBreak = false;
				for(PcStockDetail detail : mList){
					
					int currentSubNum = 0;
					if(allNeedNum>detail.getStockNumber()){
						currentSubNum = detail.getStockNumber();
						allNeedNum = allNeedNum-currentSubNum;
					}else{
						currentSubNum = allNeedNum;
						isBreak = true;
					}
					
					
					//库存明细表  加库存
					PcStockDetail psd = new PcStockDetail();
					PcStockDetailExample psde = new PcStockDetailExample();
					
					
					psd.setStockNumber(currentSubNum);
					
					psde.createCriteria()
						.andSkuCodeEqualTo(skuCode)
						.andStockAreaEqualTo(detail.getStockArea())
						.andStockBatchEqualTo(detail.getStockBatch())
						.andStockNumberGreaterThanOrEqualTo(currentSubNum);
					
					count = pcdm.updateByExampleSelective(psd, psde);
					
					if(count<=0){
						ret.setResultCode(941901003);
						ret.setResultMessage(bInfo(941901003, skuCode));
						throw new Exception(ret.getResultMessage());
					}
					
					if(isBreak)
						break;
				}
				
			}else{
				ret.setResultCode(941901003);
				ret.setResultMessage(bInfo(941901003, skuCode));
				throw new Exception(ret.getResultMessage());
			}
		}
	}
	
	
	/**
	 * 终止试用商品
	 * @param updateData
	 * @return
	 * @throws Exception 
	 */
	public void updateTryoutInfo(MDataMap updateData){
		
		com.cmall.dborm.txmapper.OcTryoutProductsMapper otpm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcTryoutProductsMapper");

		com.cmall.dborm.txmapper.NcFreetryoutApplyMapper nfam = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_NcFreetryoutApplyMapper");

		com.cmall.dborm.txmapper.NcPostOperateMapper npom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_NcPostOperateMapper");
		
		OcTryoutProductsExample otpexample = new OcTryoutProductsExample();
		
		NcFreetryoutApplyExample nftexample = new NcFreetryoutApplyExample();
		
		NcPostOperateExample npoexample = new NcPostOperateExample();
		
		String skuCode = updateData.get("sku_code");
		String endTimeBefore = updateData.get("endTimeBefore");
		String endTimeNow = updateData.get("end_time");
		
		//修改试用商品表
		otpexample.createCriteria().andUidEqualTo(updateData.get("uid"));
		OcTryoutProducts otpModel = new OcTryoutProducts();
		otpModel.setEndTime(endTimeNow);
		otpModel.setUpdateTime(updateData.get("update_time"));
		otpModel.setUpdateUser(updateData.get("update_user"));
		otpm.updateByExampleSelective(otpModel, otpexample);
		
//		int count = DbUp.upTable("oc_tryout_products").dataUpdate(updateData,"end_time,update_time,update_user", "uid");
		if("449746930003".equals(updateData.get("isFreeShipping"))){
			//修改免费试用商品申请表
			nftexample.createCriteria().andSkuCodeEqualTo(skuCode).andEndTimeEqualTo(endTimeBefore);
			NcFreetryoutApply nftModel = new NcFreetryoutApply();
			nftModel.setEndTime(endTimeNow);
			nfam.updateByExampleSelective(nftModel, nftexample);
//			String sql = "update nc_freetryout_apply set end_time= '"+endTimeNow+"' where  sku_code = '"+skuCode+"' and end_time='"+endTimeBefore+"' ";
//		    DbUp.upTable("nc_freetryout_apply").dataExec(sql, null);
		}
		
		
		npoexample.createCriteria().andInfoCodeEqualTo(skuCode).andEndTimeEqualTo(endTimeBefore);
		NcPostOperate npoModel = new NcPostOperate();
		npoModel.setEndTime(endTimeNow);
		npom.updateByExampleSelective(npoModel, npoexample);
		//操作成功   将分享表的结束时间同时更改
//		String sqlString = "select * from nc_post_operate where info_code = '"+skuCode+"' and end_time='"+endTimeBefore+"'";
//		MDataMap map = new MDataMap();
//		map.put("app_code","SI2007");
//		List<Map<String, Object>> list = DbUp.upTable("nc_post_operate").dataSqlList(sqlString, map);
//		if(list!=null&&list.size()>0){
//			String sql = "update nc_post_operate set end_time= '"+endTimeNow+"' where  info_code = '"+skuCode+"' and end_time='"+endTimeBefore+"' ";

//		    DbUp.upTable("nc_post_operate").dataExec(sql, null);
//		}
		
	}
	/**
	 * 商品插入草稿箱
	 * @param pdb
	 * @param ret
	 */
	public void insertProductDraftbox(PcProductDraftbox pdb,RootResult ret){
		com.cmall.dborm.txmapper.PcProductDraftboxMapper ppdm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductDraftboxMapper");
		pdb.setUid(UUID.randomUUID().toString().replace("-", ""));
		ppdm.insertSelective(pdb);
	}
	/**
	 * 更新草稿箱商品信息
	 * @param pc
	 * @param ret
	 * @param operator
	 */
	public void updateProductDraftbox(PcProductDraftbox pdb,RootResult ret){
		com.cmall.dborm.txmapper.PcProductDraftboxMapper ppdm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductDraftboxMapper");
		PcProductDraftboxExample draftboxExample = new PcProductDraftboxExample();
		draftboxExample.createCriteria().andUidEqualTo(pdb.getUid());
		ppdm.updateByExampleSelective(pdb, draftboxExample);
	}
	
	
	/**
	 * 批量添加商品
	 * @param pc
	 * @param ret
	 * @param operator
	 */
	public void addSkuBatch(PcProductinfo pc,RootResult ret,String operator,ProductChangeFlag pcf){
		com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapper pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.ScStoreSkunumMapper sssm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		//创建时间
		String createTime = DateUtil.getSysDateTimeString();
		
		if(pcf.isChangeProductSku()){
			//插入或者更新sku信息
			if(pc.getProductSkuInfoList()!=null){
				List<ProductSkuInfo> skuList = pc.getProductSkuInfoList();
				
				for (int i = 0; i < skuList.size(); i++) {
					ProductSkuInfo sku = skuList.get(i);
					
					if(sku.getSkuCode() == null || sku.getSkuCode().equals("")){
						if(sku.getSkuKey() == null || sku.getSkuKey().equals("")){
							continue;
						}
						com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs psModel = new PcSkuinfoWithBLOBs();
						psModel.setSellerCode(pc.getSellerCode());
						psModel.setSkuCode(WebHelper.upCode(SkuCommon.SKUHead));
						psModel.setSkuCodeOld(sku.getSkuCodeOld());
						pc.getProductSkuInfoList().get(i).setSkuCode(psModel.getSkuCode());
						psModel.setMarketPrice(sku.getMarketPrice());
						psModel.setProductCode(pc.getProductCode());
						psModel.setProductCodeOld("");
						psModel.setQrcodeLink(sku.getQrcodeLink());
						psModel.setSecurityStockNum(Long.valueOf(sku.getSecurityStockNum()));
						psModel.setSellPrice(sku.getSellPrice());
						psModel.setSellProductcode(sku.getSellProductcode());
						psModel.setSkuKey(sku.getSkuKey());
						psModel.setSkuKeyvalue(sku.getSkuValue());
						psModel.setSkuName(sku.getSkuName());
						psModel.setSkuAdv(sku.getSkuAdv());
						psModel.setStockNum(Long.valueOf(sku.getStockNum()));
						psModel.setUid(UUID.randomUUID().toString().replace("-", ""));
						psModel.setBarcode(sku.getBarcode());
						psModel.setMiniOrder(sku.getMiniOrder());
						psModel.setCostPrice(sku.getCostPrice());
						psModel.setStockNum(Long.parseLong(sku.getStockNum()+""));
						
						if(sku.getSkuPicUrl() == null || sku.getSkuPicUrl().equals("")){
							psModel.setSkuPicurl(pc.getMainPicUrl());
						}else{
							psModel.setSkuPicurl(sku.getSkuPicUrl());
						}
						
						pcsm.insertSelective(psModel);
						
						if (sku.getScStoreSkunumList() != null) {
							int changeStock = 0;		//库存变化总量
							for (ScStoreSkunum scStore : sku.getScStoreSkunumList()) {
								//插入库存
								com.cmall.dborm.txmodel.ScStoreSkunum sssModel = new com.cmall.dborm.txmodel.ScStoreSkunum();
								sssModel.setUid(UUID.randomUUID().toString().replace("-", ""));
								sssModel.setSkuCode(psModel.getSkuCode());		//此处取skuCode一定要取psModel里面的。
								sssModel.setStockNum(scStore.getStockNum());
								sssModel.setStoreCode(scStore.getStoreCode());
								sssm.insertSelective(sssModel);
								changeStock += Integer.parseInt(scStore.getStockNum()+"");
							}
							com.cmall.dborm.txmodel.LcStockchange lsModel = new LcStockchange();
							lsModel.setChangeStock(changeStock);
							lsModel.setChangeType(SkuCommon.SkuStockChangeTypeCreateProduct);
							lsModel.setCode(psModel.getSkuCode());
							lsModel.setCreateTime(createTime);
							lsModel.setCreateUser(operator);
							lsModel.setUid(UUID.randomUUID().toString().replace("-", ""));
							lsom.insertSelective(lsModel);
						}
					}
				}
			}
		}
		if(pcf.isChangeProductFlow()){
			//插入商品历史流水信息
			if(pc.getPcProdcutflow() != null){
				
				com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
				
				ppf.setCreateTime(createTime);
				ppf.setCreator(operator);
				ppf.setFlowCode(pc.getPcProdcutflow().getFlowCode());
				ppf.setFlowStatus(pc.getPcProdcutflow().getFlowStatus());
				ppf.setProductCode(pc.getProductCode());
				
				JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
				ppf.setProductJson(pHelper.ObjToString(pc));
				
				ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
				ppf.setUpdateTime(createTime);
				ppf.setUpdator(operator);
				
				ppfm.insertSelective(ppf);
			}
		}
	}
	
	/**
	 * 创建商品时保存一份数据到备份表
	 */
	public void addProductInfoBackup(PcProductinfo pi){
		DbUp.upTable("pc_productinfo_create").delete("product_code",pi.getProductCode());
		MDataMap map = new MDataMap();
		map.put("product_code", pi.getProductCode());
		map.put("content", new JsonHelper<PcProductinfo>().ObjToString(pi));
		map.put("creator", UserFactory.INSTANCE.create().getUserCode());
		map.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("pc_productinfo_create").dataInsert(map);
	}
	
	/**
	 * 获取创建商品时的备份数据
	 * @param productCode
	 * @return
	 */
	public PcProductinfo getProductInfoBackup(String productCode){
		MDataMap map = DbUp.upTable("pc_productinfo_create").one("product_code",productCode);
		if(map == null) return null;
		
		return new JsonHelper<PcProductinfo>().StringToObj(map.get("content"), new PcProductinfo());
	}
}
