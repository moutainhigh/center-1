package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductDraftBoxService;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.txservice.TxProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 *修改商品(草稿箱里提交审批流程)
 *
 *@author ligj
 *@version 1.0 
 * 
 */
public class UpdateModProductFromDraftbox extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap sellerInfoExt = DbUp.upTable("uc_seller_info_extend").one("small_seller_code", UserFactory.INSTANCE.create().getManageCode());
		try {
			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(
						mSubDataMap.get("json"), pp);
				pp.setLabels(mDataMap.get("zw_f_labels"));  
				
				// 资质品类判断
				if(StringUtils.isNotBlank(pp.getQualificationCategoryCode())){
					MDataMap map = new MDataMap();
					map.put("small_seller_code",UserFactory.INSTANCE.create().getManageCode());
					List<MDataMap> userList = DbUp.upTable("uc_seller_info_extend").queryAll(
							"uc_seller_type ", 
							"", 
							"small_seller_code=:small_seller_code", 
							map);
					if(userList != null && userList.size() != 0){
						String ustype = userList.get(0).get("uc_seller_type"); //  非跨境类商户进行资质效验 - Yangcl
						if(StringUtils.isNotBlank(ustype) && (ustype.equals("4497478100050001") || ustype.equals("4497478100050004")|| ustype.equals("4497478100050000") )){
							map.put("brand_code", pp.getBrandCode());
							map.put("category_code", pp.getQualificationCategoryCode());
							List<MDataMap> mDataMapList = DbUp.upTable("pc_seller_qualification").queryAll("qualification_name,DATEDIFF(SYSDATE(),end_time) expired", "", "", map);
							if(mDataMapList.isEmpty()){
								mResult.inErrorMessage(941901147);
								return mResult;
							}
							
							List<String> vs = new ArrayList<String>();
							for(MDataMap mData : mDataMapList){
								if(NumberUtils.toInt(mData.get("expired")) > 0){
									vs.add(mData.get("qualification_name"));
								}
							}
							
							if(!vs.isEmpty()){
								mResult.inErrorMessage(941901148, StringUtils.join(vs,"、"));
								return mResult;
							}
						}
					}
				}
				
				// 商品保障必须勾选
				if(StringUtils.isBlank(mSubDataMap.get("authority_logo"))){
					mResult.inErrorMessage(941901145);
					return mResult; 
				}
				
				// 支持/不支持7日无理由退货不能同时勾选
				if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday_no"))){
					String msg1 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday")).get("logo_content");
					String msg2 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday_no")).get("logo_content");
					mResult.inErrorMessage(941901146,msg1,msg2);
					return mResult; 
				}
				
				// 普通商户 检查税收分类编码是否正确
				if(sellerInfoExt.get("uc_seller_type").equals("4497478100050001")) {
					if(StringUtils.isBlank(pp.getTaxCode()) || DbUp.upTable("sc_tax_category").count("tax_code", pp.getTaxCode()) == 0) {
						mResult.setResultCode(0);
						mResult.setResultMessage("税收分类编码不正确");
						return mResult;
					}
				}
				
				// 配送仓库类别默认以当前商户的为准
				pp.getPcProductinfoExt().setDeliveryStoreType(sellerInfoExt.get("delivery_store_type"));
				
				StringBuffer error = new StringBuffer();
				String sc = pp.getSellerCode();
				String msc = pp.getSmallSellerCode();
				String userCode = "";
				MUserInfo userInfo = null;
				if (UserFactory.INSTANCE != null) {
					try {
						userInfo = UserFactory.INSTANCE.create();
					} catch (Exception e) {
					}

					if (userInfo != null) {
						userCode = userInfo.getUserCode();
					}
				}
				String smallSellerCode = userInfo.getManageCode(); 
				if(smallSellerCode!=null&&!"".equals(smallSellerCode)&&sc!=null&&!"".equals(sc)&&msc!=null&&!"".equals(msc)&&!smallSellerCode.equals(msc)){
					mResult.inErrorMessage(941901123, bInfo(941901064));
				}else {
					pp.setSellerCode(AppConst.MANAGE_CODE_HOMEHAS);
					pp.setSmallSellerCode(smallSellerCode);
					List<String> skuKey = new ArrayList<String>();
					List<String> skuKeyValue = new ArrayList<String>();
					if (pp.getProductSkuInfoList() != null) {
						for (int i = 0; i < pp.getProductSkuInfoList().size(); i++) {
							ProductSkuInfo sku = pp.getProductSkuInfoList().get(i);
							sku.setProductCode(pp.getProductCode());
							sku.setSkuCode(WebHelper.upCode(ProductService.SKUHead));			//添加商品时候需要自动生成skucode
							skuKey.add(sku.getSkuKey());
							skuKeyValue.add(sku.getSkuKeyvalue());
						}
					}
					//检查sku规格是否存在重复
					if (1 == new ProductService().checkRepeatSku(skuKey, skuKeyValue)) {
						mResult.inErrorMessage(941901124);
						return mResult;
					}
					pp.setProductStatus(bConfig("productcenter.AddProductStatus"));
					//在此需要判断商品是添加还是新增操作
					if (DbUp.upTable("pc_productinfo").count("product_code",pp.getProductCode())>0) {
						
						List<MDataMap> skuCodeList = DbUp.upTable("pc_skuinfo").queryAll("", "", "product_code=:product_code", new MDataMap("product_code",pp.getProductCode()));
						if (null != skuCodeList) {
							List<ProductSkuInfo> skuInfoList = pp.getProductSkuInfoList();
							SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();
							for (int i = 0; i < skuInfoList.size(); i++) {
								skuInfoList.get(i).setSkuCode("DSF"+skuInfoList.get(i).getSkuCode());		//在skucode前加上DSF，为了标志商品新增时不会自动生成商品编号，存库前需要去掉此标志
							}
							for (MDataMap skuMap : skuCodeList) {
								int flag = 0;
								for (int i = 0; i < skuInfoList.size(); i++) {
										ProductSkuInfo skuInfo = skuInfoList.get(i);
									if (skuInfo.getSkuKey().equals(skuMap.get("sku_key"))) {
										flag = 1;
										pp.getProductSkuInfoList().get(i).setSkuCode(skuMap.get("sku_code"));
									}
								}
								if (flag == 0) {
									ProductSkuInfo skuInfo = new ProductSkuInfo();
									ss.serialize(skuMap, skuInfo);
									skuInfo.setSaleYn("N");
									pp.getProductSkuInfoList().add(skuInfo);
								}
							}
						}
						pService.updateProductForCshop(pp, error);
					}else{
						new ProductDraftBoxService().addProduct(pp, error);
					}
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(909701005));
						ProductDraftBoxService pdService = new ProductDraftBoxService();
						//更新草稿箱数据
						pdService.updateProductToDraftBox(pp,new StringBuffer());
						
						//提交完审批后把草稿箱数据标记删除
						String productCode = pp.getProductCode();
						pdService.delDraftBoxProduct("",productCode,userCode);
						
						// 保存商户提交的商品原始数据
						new TxProductService().addProductInfoBackup(pp);
					} else {
						mResult.inErrorMessage(909701006, error.toString());
					}
					
					if(StringUtils.isNotBlank(pp.getProductCode())){
						// 商品保障
						DbUp.upTable("pc_product_authority_logo").delete("product_code",pp.getProductCode());
						if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))){
							String[] vs = mSubDataMap.get("authority_logo").split(",");
							for(String v : vs){
								if(StringUtils.isBlank(v)) continue;
								MDataMap data = new MDataMap("product_code",pp.getProductCode(),"authority_logo_uid",v,"create_time",FormatHelper.upDateTime());
								DbUp.upTable("pc_product_authority_logo").dataInsert(data);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909701007);
		}
		return mResult;
	}
}
