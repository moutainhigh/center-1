package com.cmall.groupcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.homehas.RsyncGetProductStatus;
import com.cmall.groupcenter.homehas.model.RsyncModelProductStatus;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 本地商品与家有商品的相关操作
 * @author ligj
 * 2015/01/29 10:19
 */
public class ProductService extends BaseClass {
	
	/**
	 * 同步商品状态至LD系统
	 * @author ligj
	 */
	public void rsyncProductStatus(List<String> productCodes){
		
		if (null == productCodes || productCodes.size() == 0) {
			return;
		}
		RsyncGetProductStatus rsyncGetProductStatus = new RsyncGetProductStatus();
		for (String productCode : productCodes) {
			List<RsyncModelProductStatus> good_info = new ArrayList<RsyncModelProductStatus>();
			String sSql = "select p.product_code_old,p.product_code,p.product_status,s.sku_code,s.sku_key from pc_productinfo p,pc_skuinfo s "+
					" where s.product_code=p.product_code and p.seller_code='"+MemberConst.MANAGE_CODE_HOMEHAS+"' and p.product_code='"+productCode+"' "
					+ "and p.small_seller_code='SI2003'";
			
			List<Map<String,Object>> productInfoMapList = DbUp.upTable("pc_productinfo").dataSqlList(sSql, null);
			for (Map<String, Object> map : productInfoMapList) {
				RsyncModelProductStatus model = new RsyncModelProductStatus();

				Object productCodeOldObj = map.get("product_code_old");
				Object productStatus = map.get("product_status");
				Object skuKey = map.get("sku_key");
				if (null == productCodeOldObj || null == productStatus || null == skuKey) {
					continue;
				}
				String good_id = String.valueOf(productCodeOldObj);
				String sale_yn = "N";
				String style_id = "";
				String color_id = "";
				
				if ("4497153900060002".equals(String.valueOf(productStatus))) {
					sale_yn = "Y";		//已上架
				}
				
				//style_id&color_id
				String[] keyValues = String.valueOf(skuKey).split("&");
				for (String keyValue : keyValues) {
					String[] key_value = keyValue.split("=");
					if ("style_id".equals(key_value[0])) {
						style_id = key_value[1];
					}else if("color_id".equals(key_value[0])) {
						color_id = key_value[1];
					}
				}
				model.setGood_id(good_id);
				model.setSale_yn(sale_yn);
				model.setStyle_id(style_id);
				model.setColor_id(color_id);
				good_info.add(model);
			}
			if(good_info.size() > 0) {
				rsyncGetProductStatus.upRsyncRequest().setGood_info(good_info);
				rsyncGetProductStatus.upRsyncRequest().setSubsystem("app");
				rsyncGetProductStatus.doRsync();
			}
		}
		
	}
	
}
