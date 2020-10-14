package com.cmall.groupcenter.kjt;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigGetKjtProductInventoryById;
import com.cmall.groupcenter.kjt.request.RsyncRequestGetKjtProductChannelInventoryById;
import com.cmall.groupcenter.kjt.response.RsyncResponseGetKjtProductInventoryById;
import com.cmall.groupcenter.kjt.response.RsyncResponseGetKjtProductInventoryById.Data;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商品分销渠道库存批量获取
 * 
 * @author zmm
 *
 */
public class RsyncGetKjtProductChannelInventoryById
		extends
		RsyncKjt<RsyncConfigGetKjtProductInventoryById, RsyncRequestGetKjtProductChannelInventoryById, RsyncResponseGetKjtProductInventoryById> {
	final static RsyncConfigGetKjtProductInventoryById CONFIG_GET_TV_BY_ID = new RsyncConfigGetKjtProductInventoryById();

	public RsyncConfigGetKjtProductInventoryById upConfig() {
		return CONFIG_GET_TV_BY_ID;
	}

	RsyncRequestGetKjtProductChannelInventoryById ChannelInventoryById = new RsyncRequestGetKjtProductChannelInventoryById();

	public RsyncRequestGetKjtProductChannelInventoryById upRsyncRequest() {
		return ChannelInventoryById;
	}

	public RsyncResult doProcess(
			RsyncRequestGetKjtProductChannelInventoryById tRequest,
			RsyncResponseGetKjtProductInventoryById tResponse) {
		
		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getData() != null) {
				result.setProcessNum(tResponse.getData().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {
			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				// 设置预期处理数量
				result.setProcessNum(tResponse.getData().size());
				for (Data info :tResponse.getData()) {
					MWebResult mResult = saveProductData(info);
					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;
					} else {
						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}
						result.getResultList().add(mResult.getResultMessage());
					}
				}
				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));
			}
		}
		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			// 特殊处理 由于时间格式不对 状态数据需要切换掉
			RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
			result.setStatusData(rsyncDateCheck.getEndDate());
		}
		return result;
	}

	private MWebResult saveProductData(Data info) {
		MWebResult result = new MWebResult();
		try {
			MDataMap mDataMap1=new MDataMap();
			String productId=info.getProductID();//跨境通的商品编号
			String onlineQty=String.valueOf(info.getOnlineQty());//库存数
			String wareGouse=String.valueOf(info.getWareHouseID());//仓库编号
			MDataMap productMap = DbUp.upTable("pc_productinfo").oneWhere("product_code", "", "", "product_code_old",productId);
			if(productMap != null && StringUtils.isNotBlank(productMap.get("product_code"))){
				MDataMap skuMap = DbUp.upTable("pc_skuinfo").oneWhere("sku_code", "", "", "product_code",productMap.get("product_code"));
				String skuCode = skuMap.get("sku_code");
				mDataMap1.put("store_code",wareGouse );
				mDataMap1.put("sku_code",skuCode);
				mDataMap1.put("stock_num", onlineQty);
				MDataMap mDataMap = DbUp.upTable("sc_store_skunum").one("store_code",wareGouse,"sku_code",skuCode);
				//处理info数据逻辑在此写
				if(mDataMap!=null){
					DbUp.upTable("sc_store_skunum").dataUpdate(mDataMap1, "stock_num", "sku_code,store_code");
				}else{
					int storeCount = DbUp.upTable("sc_store").count("store_code",wareGouse);
					if(storeCount == 0){
						MDataMap storeMap = new MDataMap();
						storeMap.put("store_code", wareGouse);
						storeMap.put("store_name", "跨境通");
						storeMap.put("app_name", "惠家有");
						storeMap.put("app_code", MemberConst.MANAGE_CODE_HOMEHAS);
						DbUp.upTable("sc_store").dataInsert(storeMap);//插入仓库信息
					}
					DbUp.upTable("sc_store_skunum").dataInsert(mDataMap1);//插入库存信息
				}
				
				//
				PlusHelperNotice.onChangeSkuStock(skuCode);
				
				MDataMap updateMap = new MDataMap();
				updateMap.put("product_code", productMap.get("product_code"));
				updateMap.put("oa_site_no", wareGouse);
				DbUp.upTable("pc_productinfo_ext").dataUpdate(updateMap, "oa_site_no", "product_code");
				
				PlusHelperNotice.onChangeProductInfo(productMap.get("product_code"));
				//触发消息队列
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeForProductChangeAll(productMap.get("product_code"));
				
			}
		} catch (Exception e) {
			result.inErrorMessage(918519034, info.toString());
			e.printStackTrace();
		}

		return result;
	}

	public RsyncResponseGetKjtProductInventoryById upResponseObject() {

		return new RsyncResponseGetKjtProductInventoryById();
	}

}
