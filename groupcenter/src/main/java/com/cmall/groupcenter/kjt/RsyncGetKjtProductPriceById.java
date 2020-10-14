package com.cmall.groupcenter.kjt;

import java.util.ArrayList;

import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigGetKjtProductPriceById;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProductPrice;
import com.cmall.groupcenter.kjt.request.RsyncRequestGetKjtProductPriceById;
import com.cmall.groupcenter.kjt.response.RsyncResponseGetKjtProductPriceById;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 根据商品ID批量获取商品价格信息
 * 
 * @author liqt
 * 
 */
public class RsyncGetKjtProductPriceById
		extends
		RsyncKjt<RsyncConfigGetKjtProductPriceById, RsyncRequestGetKjtProductPriceById, RsyncResponseGetKjtProductPriceById> {

	final static RsyncConfigGetKjtProductPriceById CONFIG_GET_TV_BY_ID = new RsyncConfigGetKjtProductPriceById();

	public RsyncConfigGetKjtProductPriceById upConfig() {
		return CONFIG_GET_TV_BY_ID;
	}

	private RsyncRequestGetKjtProductPriceById RsyncRequestGetKjtProductPriceById = new RsyncRequestGetKjtProductPriceById();
	public RsyncRequestGetKjtProductPriceById upRsyncRequest() {

		return RsyncRequestGetKjtProductPriceById;
	}

	public RsyncResult doProcess(RsyncRequestGetKjtProductPriceById tRequest,
			RsyncResponseGetKjtProductPriceById tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getData() != null) {
				result.setProcessNum(tResponse.getData().getProductPriceList().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {

				// 设置预期处理数量
				result.setProcessNum(tResponse.getData().getProductPriceList().size());

				for (RsyncModelGetKjtProductPrice info : tResponse.getData().getProductPriceList()) {
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

	private MWebResult saveProductData(RsyncModelGetKjtProductPrice info) {
		MWebResult result = new MWebResult();
		try {		
			//处理info数据逻辑在此写
			MDataMap mDataMap1=new MDataMap();
			mDataMap1.put("product_code", info.getProductId());
			mDataMap1.put("cost_price", info.getProductPrice().toString());
			String productID=info.getProductId();
			int status=info.getStatus();
			MDataMap mDataMap = DbUp.upTable("pc_productinfo").oneWhere("product_status", "","","product_code",productID);
			
			if(mDataMap!=null){
				if(status==1&&mDataMap.get("product_status").equals("4497153900060002")){
					DbUp.upTable("pc_productinfo").dataUpdate(mDataMap1, "cost_price", "product_code");
				}else {
					mDataMap1.put("product_status", "4497153900060003");
					DbUp.upTable("pc_productinfo").dataUpdate(mDataMap1, "cost_price,product_status", "product_code");
				}	
			}
			
		} catch (Exception e) {
			result.inErrorMessage(918519034, info.toString());
			e.printStackTrace();
		}

		return result;
	}


	public RsyncResponseGetKjtProductPriceById upResponseObject() {

		return new RsyncResponseGetKjtProductPriceById();
	}
}
