package com.cmall.productcenter.webfunc;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuinfoForCf extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String picEdit_flag = mAddMaps.get("picEdit_flag");
		ProductService service = new ProductService();
		MUserInfo sys = UserFactory.INSTANCE.create(); 
		if(sys==null){
			mResult.inErrorMessage(941901061, bInfo(941901064));
		}else {
			int miniOrder = Integer.parseInt(mAddMaps.get("mini_order"));
			if (miniOrder <= 0 ) {
				mResult.inErrorMessage(941901061, bInfo(941901115));
				return mResult;
			}
//			String barcode = mAddMaps.get("barcode");
//			if (StringUtils.isNotBlank(barcode)) {
//				if (DbUp.upTable("pc_skuinfo").count("barcode",barcode) > 0) {
//					mResult.inErrorMessage(941901061, bInfo(941901116));
//					return mResult;
//				}
//			}
			MDataMap one = DbUp.upTable("pc_skuinfo").one("uid",mAddMaps.get("uid"));
			String productCode = one.get("product_code");
			MDataMap productInfoMap = DbUp.upTable("pc_productinfo").one("product_code",productCode);
//			if (productInfoMap.get("small_seller_code").startsWith("SF03")
			/**
			 * 修改商品判断条件 2016-12-02 zhy
			 */
			String seller_type = WebHelper.getSellerType(productInfoMap.get("small_seller_code"));
			if (StringUtils.isNotBlank(seller_type)
					&& !productInfoMap.get("small_seller_code").equals("SF03KJT")
					&& AppConst.MANAGE_CODE_HOMEHAS.equals(productInfoMap.get("seller_code"))) {
//				productCode = productCode+"_1";			//注掉此句话，商品会读pc_productinfo表数据
			}
			PcProductinfo pro = service.getProduct(productCode);
//			if ("SF03KJT".equals(pro.getSmallSellerCode())&&AppConst) {
//				
//			}
			for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
				ProductSkuInfo psku= pro.getProductSkuInfoList().get(i);
				if(mAddMaps.get("sku_code").equals(psku.getSkuCode())){
					pro.getProductSkuInfoList().get(i).setSkuName(mAddMaps.get("sku_name"));
					pro.getProductSkuInfoList().get(i).setSecurityStockNum(Integer.valueOf(mAddMaps.get("security_stock_num")));
					pro.getProductSkuInfoList().get(i).setSkuPicUrl(mAddMaps.get("sku_picurl"));
					pro.getProductSkuInfoList().get(i).setSellProductcode(mAddMaps.get("sell_productcode"));
					pro.getProductSkuInfoList().get(i).setSkuAdv(mAddMaps.get("sku_adv"));
//					pro.getProductSkuInfoList().get(i).setBarcode(barcode);
					pro.getProductSkuInfoList().get(i).setMiniOrder(miniOrder);
					
				}
				pro.getProductSkuInfoList().get(i).setScStoreSkunumList(null);			//设置为null后就不会修改库存了
			}
			StringBuffer error = new StringBuffer();
			service.updateProduct(pro,error);
			if(StringUtils.isEmpty(error.toString())){
				mResult.setResultMessage(bInfo(941901060));
			}else {
				mResult.inErrorMessage(941901061, error.toString());
			}
			//判断有无修改sku图片，如果修改了sku图片，将此商品下其他与当前sku颜色相同的sku也改为这个图片
			if("1".equals(picEdit_flag)) {
				String sku_code = mAddMaps.get("sku_code");
				//String sku_keyvalue = mAddMaps.get("sku_keyvalue");//颜色=紫色&款式=XL
				String sql1 = "select sku_keyvalue from pc_skuinfo where sku_code =  '" + sku_code + "'";
				List<Map<String, Object>> dataSqlList1 = DbUp.upTable("pc_skuinfo").dataSqlList(sql1, null);
				String sku_keyvalue = dataSqlList1==null?null:dataSqlList1.get(0).get("sku_keyvalue").toString();
				String color = "";
				if(null!=sku_keyvalue) {
					color = sku_keyvalue.split("&")[0].split("=")[1];
				}
				String sql = "select * from pc_skuinfo where product_code = '"+productCode+"'";
				List<Map<String, Object>> dataSqlList = DbUp.upTable("pc_skuinfo").dataSqlList(sql, null);
				if(null!=dataSqlList&&dataSqlList.size()>1) {
					for(Map<String, Object> map  : dataSqlList) {
						if(!sku_code.equals(map.get("sku_code").toString())) {
							String sku_keyvalue2 = map.get("sku_keyvalue")==null?"":map.get("sku_keyvalue").toString();
							String color2 = sku_keyvalue2.split("&")[0].split("=")[1];
							if(color.equals(color2)) {
								MDataMap mdata  = new MDataMap();
								mdata.put("sku_picurl", mAddMaps.get("sku_picurl"));
								//mdata.put("sku_name", mAddMaps.get("sku_name"));
								mdata.put("zid", map.get("zid").toString());
								DbUp.upTable("pc_skuinfo").dataUpdate(mdata, "sku_picurl", "zid");
							}
						};
					}
				}
			}
			//修改一个sku的商品名称则其他sku商品名称都更改
			//String sku_picurl = mAddMaps.get("sku_picurl").toString();
			MDataMap mWhereMap  = new MDataMap();
			if("1".equals(MapUtils.getString(mAddMaps, "sel", ""))) {
				mWhereMap.put("product_code", productCode);
				List<Map<String,Object>> dataSqlList = DbUp.upTable("pc_skuinfo").dataSqlList("select * from pc_skuinfo where product_code = :product_code ", mWhereMap);
				for(Map<String,Object> map : dataSqlList) {
					String sku_name = mAddMaps.get("sku_name").toString();
					String[] split = map.get("sku_keyvalue").toString().split("&");
					//颜色
					String[] split2 = split[0].split("=");
					//款式
					String[] split3 = split[1].split("=");
					String color = one.get("sku_keyvalue").toString().split("&")[0].split("=")[1];
					if(color.equals(split2[1])) {
						if(!"共同".endsWith(split2[1] )) {
							sku_name = sku_name.concat(" ").concat(split2[1]);
						}
						if(!"共同".endsWith(split3[1] )) {
							sku_name = sku_name.concat(" ").concat(split3[1]);
						}
						mWhereMap.put("sku_name", sku_name);
						mWhereMap.put("zid", map.get("zid").toString());
						DbUp.upTable("pc_skuinfo").dataUpdate(mWhereMap, "sku_name", "zid");
					}
				}
			}else {
				String[] split = one.get("sku_keyvalue").toString().split("&");
				//颜色
				String[] split2 = split[0].split("=");
				//款式
				String[] split3 = split[1].split("=");
				String sku_name = mAddMaps.get("sku_name").toString();
				if(!"共同".endsWith(split2[1] )) {
					sku_name = sku_name.concat(" ").concat(split2[1]);
				}
				if(!"共同".endsWith(split3[1] )) {
					sku_name = sku_name.concat(" ").concat(split3[1]);
				}
				mWhereMap.put("sku_name", sku_name);
				mWhereMap.put("zid", one.get("zid").toString());
				DbUp.upTable("pc_skuinfo").dataUpdate(mWhereMap, "sku_name", "zid");
			}
			PlusHelperNotice.onChangeProductInfo(productCode);
		}
		
		
		
		return mResult;
	}

}
