package com.cmall.groupcenter.kjt;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigGetKjtProductChangeById;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProductChange;
import com.cmall.groupcenter.kjt.request.RsyncRequestGetKjtProductChangeById;
import com.cmall.groupcenter.kjt.response.RsyncResponseGetKjtProductChangeById;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 时间段内价格变化的商品ID列表获取
 * 
 * @author liqt
 * 
 */
public class RsyncGetKjtProductChangeById
		extends
		RsyncKjt<RsyncConfigGetKjtProductChangeById, RsyncRequestGetKjtProductChangeById, RsyncResponseGetKjtProductChangeById> {

	final static RsyncConfigGetKjtProductChangeById CONFIG_GET_TV_BY_ID = new RsyncConfigGetKjtProductChangeById();

	public RsyncConfigGetKjtProductChangeById upConfig() {
		return CONFIG_GET_TV_BY_ID;
	}

	private RsyncRequestGetKjtProductChangeById RsyncRequestGetKjtProductChangeById = new RsyncRequestGetKjtProductChangeById();
	public RsyncRequestGetKjtProductChangeById upRsyncRequest() {

		return RsyncRequestGetKjtProductChangeById;
	}

	public RsyncResult doProcess(RsyncRequestGetKjtProductChangeById tRequest,
			RsyncResponseGetKjtProductChangeById tResponse) {

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

				for (RsyncModelGetKjtProductChange info : tResponse.getData().getProductPriceList()) {
					MWebResult mResult = changeProductPrice(info);

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

	private MWebResult changeProductPrice(RsyncModelGetKjtProductChange info) {
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

	public RsyncResponseGetKjtProductChangeById upResponseObject() {

		return new RsyncResponseGetKjtProductChangeById();
	}
}
