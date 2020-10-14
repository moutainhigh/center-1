package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.baidupush.core.utility.DismantlOrderUtil;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.modelproduct.PlusModelMediMclassGift;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 同步家有商品赠品
 */
public class RsyncGoodGiftList {

	public boolean doRsync(String productCode) {
		if (StringUtils.isBlank(productCode)) {
			return false;
		}
		
		String productSql = "select pi.product_code productCode,pi.validate_flag flag,pie.prch_type type,pie.oa_site_no siteNo, " +
				" pi.seller_code sellerCode from pc_productinfo pi,pc_productinfo_ext pie "+
				" where pi.product_code = pie.product_code and pi.product_status='4497153900060002' and pi.seller_code='SI2003' and pi.small_seller_code='SI2003' and pi.product_code = :product_code limit 1";
		Map<String, Object> productMap = DbUp.upTable("pc_productinfo").dataSqlOne(productSql, new MDataMap("product_code",productCode));
		
		if(productMap == null) return false;
		
		DismantlOrderUtil dismant = new DismantlOrderUtil();
		Object sellerCode = productMap.get("sellerCode");
		List<ModelGoodGiftInfo> outerGiftList = dismant.getGifts(productMap);
		String outerGiftId = "";
		String outerGiftName = "";
		String styleId = "";
		String colorId = "";
		String eventId = "";
		String giftCd = "";
		String frDate = "";
		String endDate = "";
		List<PlusModelMediMclassGift> mediMclssNmlist = new ArrayList<PlusModelMediMclassGift>();
		
		//先删除此product对应的赠品信息
		DbUp.upTable("pc_product_gifts_new").delete("product_code",productCode.toString());
		//删除商品赠品对应通路的信息
		DbUp.upTable("pc_product_gifts_medimclass").delete("product_code",productCode.toString());
		for (ModelGoodGiftInfo modelGoodGiftInfo : outerGiftList) {
			outerGiftId = modelGoodGiftInfo.getGood_id();
			outerGiftName = modelGoodGiftInfo.getGood_nm();
			styleId = modelGoodGiftInfo.getStyle_id();
			colorId = modelGoodGiftInfo.getColor_id();
			eventId = modelGoodGiftInfo.getEvent_id();
			giftCd = modelGoodGiftInfo.getGift_cd();
			frDate = modelGoodGiftInfo.getFr_date();
			endDate = modelGoodGiftInfo.getEnd_date();
			mediMclssNmlist =  modelGoodGiftInfo.getMedi_mclss_nm();
			String sUid = UUID.randomUUID().toString().replace("-", "");
			
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("uid", sUid);
			mDataMap.put("product_code", productCode.toString());
			mDataMap.put("seller_code", sellerCode.toString());
			mDataMap.put("gift_id", outerGiftId);
			mDataMap.put("gift_name", outerGiftName);
			mDataMap.put("style_id", styleId);
			mDataMap.put("color_id", colorId);
			mDataMap.put("event_id", eventId);
			mDataMap.put("gift_cd", giftCd);
			mDataMap.put("fr_date", frDate);
			mDataMap.put("end_date", endDate);
			
			mDataMap.put("update_time", DateUtil.getSysDateTimeString());
			
			DbUp.upTable("pc_product_gifts_new").dataInsert(mDataMap);
			
			//赠品通路信息
			if (null != mediMclssNmlist) {
				for (PlusModelMediMclassGift mediMclassGift : mediMclssNmlist) {
					MDataMap mDataMClassMap = new MDataMap();
					mDataMClassMap.put("product_code", productCode.toString());
					mDataMClassMap.put("gift_id", outerGiftId);
					mDataMClassMap.put("uid_ref", sUid);
					if (StringUtils.isNotBlank(mediMclassGift.getMEDI_MCLSS_ID())) {
						mDataMClassMap.put("medi_mclass_id", mediMclassGift.getMEDI_MCLSS_ID());
					}
					if (StringUtils.isNotBlank(mediMclassGift.getMEDI_MCLSS_NM())) {
						mDataMClassMap.put("medi_mclass_nm", mediMclassGift.getMEDI_MCLSS_NM());
					}
					
					DbUp.upTable("pc_product_gifts_medimclass").dataInsert(mDataMClassMap);
				}
			}
			//刷新缓存
			XmasKv.upFactory(EKvSchema.Gift).del(productCode.toString());
		}
		
		return true;
	}
}
