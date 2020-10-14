package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.txservice.TxProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap sellerInfoExt = DbUp.upTable("uc_seller_info_extend").one("small_seller_code", UserFactory.INSTANCE.create().getManageCode());
		try {

			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();

				MDataMap mSubDataMap = mDataMap
						.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

				PcProductinfo pcProductinfo = new PcProductinfo();
				pcProductinfo = new JsonHelper<PcProductinfo>().StringToObj(
						mSubDataMap.get("json"), pcProductinfo);
				pcProductinfo.setLabels(mDataMap.get("zw_f_labels"));
//				if(mSubDataMap.get("c_sellercategory")!=null&&!"".equals(mSubDataMap.get("c_sellercategory"))){
//					List<UcSellercategoryProductRelation> usprList = new ArrayList<UcSellercategoryProductRelation>();
//					String c_sellercategory[] = mSubDataMap.get("c_sellercategory").split(","); 
//					for(int i=0;i<c_sellercategory.length;i++){
//						UcSellercategoryProductRelation relation = new UcSellercategoryProductRelation();
//						relation.setCategoryCode(c_sellercategory[i]);
//						relation.setSellerCode(UserFactory.INSTANCE.create().getManageCode());
//						usprList.add(relation);
//					}
//					pcProductinfo.setUsprList(usprList);
//				}
				
				// 商品规格->基本属性不得为空 页面验证完成后此处再次进行后台验证 - Yangcl
				List<PcProductproperty> list = pcProductinfo.getPcProductpropertyList();
				for(PcProductproperty p : list){
					if(StringUtils.isBlank(p.getPropertyValue())){
						mResult.inErrorMessage(941901143);
						return mResult; 
					}
				}
				
				// 资质品类判断
				if(StringUtils.isNotBlank(pcProductinfo.getQualificationCategoryCode())){
					MDataMap map = new MDataMap();
					map.put("small_seller_code",UserFactory.INSTANCE.create().getManageCode());
					List<MDataMap> userList = DbUp.upTable("uc_seller_info_extend").queryAll(
							"uc_seller_type ", 
							"", 
							"small_seller_code=:small_seller_code", 
							map);
					if(userList != null && userList.size() != 0){
						String ustype = userList.get(0).get("uc_seller_type"); //  非跨境类商户进行资质效验 - Yangcl  添加缤纷商户- Renhb
						if(StringUtils.isNotBlank(ustype) && (ustype.equals("4497478100050001") || ustype.equals("4497478100050004")|| ustype.equals("4497478100050000")|| ustype.equals("4497478100050005") )){
							map.put("brand_code", pcProductinfo.getBrandCode());
							map.put("category_code", pcProductinfo.getQualificationCategoryCode());
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
					if(StringUtils.isBlank(pcProductinfo.getTaxCode()) || DbUp.upTable("sc_tax_category").count("tax_code", pcProductinfo.getTaxCode()) == 0) {
						mResult.setResultCode(0);
						mResult.setResultMessage("税收分类编码不正确");
						return mResult;
					}
				}
				
				// 配送仓库类别默认以当前商户的为准
				pcProductinfo.getPcProductinfoExt().setDeliveryStoreType(sellerInfoExt.get("delivery_store_type"));
				
				pcProductinfo.setSellerCode(AppConst.MANAGE_CODE_HOMEHAS);
				/**
				 * 增加商户编码 
				 */
				pcProductinfo.setSmallSellerCode(UserFactory.INSTANCE.create().getManageCode());
				//标志为虚拟商品，拆单用
				pcProductinfo.setValidate_flag("Y");
				//设为不可售
				pcProductinfo.setFlagSale(0);
				pcProductinfo.setCreateTime(DateUtil.getSysDateTimeString());
				pcProductinfo.setUpdateTime(DateUtil.getSysDateTimeString());
				StringBuffer error = new StringBuffer();
				List<String> skuKey = new ArrayList<String>();
				List<String> skuKeyValue = new ArrayList<String>();
				if (pcProductinfo.getProductSkuInfoList() != null) {
					for (int i = 0; i < pcProductinfo.getProductSkuInfoList().size(); i++) {
						ProductSkuInfo sku = pcProductinfo.getProductSkuInfoList().get(i);
						skuKey.add(sku.getSkuKey());
						skuKeyValue.add(sku.getSkuKeyvalue());
					}
				}
				//检查sku规格是否存在重复
				if (1 == new ProductService().checkRepeatSku(skuKey, skuKeyValue)) {
					mResult.inErrorMessage(941901124);
					return mResult;
				}
				pService.AddProduct(pcProductinfo, error);
				
				// 保存商户提交的商品原始数据
				if(StringUtils.isNotBlank(pcProductinfo.getProductCode())){
					new TxProductService().addProductInfoBackup(pcProductinfo);
				}
				
				if (StringUtils.isEmpty(error.toString())) {
					mResult.setResultMessage(bInfo(909701002));

				} else {
					mResult.inErrorMessage(909701003, error.toString());
				}

				if(StringUtils.isNotBlank(pcProductinfo.getProductCode())){
					// 商品保障
					DbUp.upTable("pc_product_authority_logo").delete("product_code",pcProductinfo.getProductCode());
					if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))){
						String[] vs = mSubDataMap.get("authority_logo").split(",");
						for(String v : vs){
							if(StringUtils.isBlank(v)) continue;
							MDataMap data = new MDataMap("product_code",pcProductinfo.getProductCode(),"authority_logo_uid",v,"create_time",FormatHelper.upDateTime());
							DbUp.upTable("pc_product_authority_logo").dataInsert(data);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909701004);
		}

		return mResult;

	}

}
