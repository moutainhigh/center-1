package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.baidupush.core.utility.DismantlOrderUtil;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.modelproduct.PlusModelMediMclassGift;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 家有惠获取商品外联赠品
 * 
 * @author ligj
 *
 */
public class JobGetProductOuterGift extends RootJob{

	private static Lock lock = new ReentrantReadWriteLock().writeLock();
	
	public void doExecute(JobExecutionContext context) {
		try {
			// 等待30分钟如果还不能获取到锁则放弃，避免任务被同时多个线程执行
			if(!lock.tryLock(30L,TimeUnit.MINUTES)){
				return;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		try{
			String productSql = "select pi.product_code productCode,pi.validate_flag flag,pie.prch_type type,pie.oa_site_no siteNo, " +
					" pi.seller_code sellerCode from pc_productinfo pi,pc_productinfo_ext pie "+
					" where pi.product_code = pie.product_code and pi.product_status='4497153900060002' and pi.seller_code='SI2003' and pi.small_seller_code='SI2003' ";
			List<Map<String, Object>> productMapList=DbUp.upTable("pc_productinfo").dataSqlList(productSql,null);
			if (null != productMapList) {
				DismantlOrderUtil dismant = new DismantlOrderUtil();
				for (Map<String, Object> map : productMapList) {
					Object productCode = map.get("productCode");
					Object sellerCode = map.get("sellerCode");
					if (productCode != null && StringUtils.isNotEmpty(productCode.toString())) {
						List<ModelGoodGiftInfo> outerGiftList = dismant.getGifts(map);
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
					}
				}
			}			
		}finally{
			lock.unlock();
		}
	}
}
