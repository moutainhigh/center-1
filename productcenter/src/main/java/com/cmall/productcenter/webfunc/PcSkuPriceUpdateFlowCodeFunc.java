package com.cmall.productcenter.webfunc;

import com.cmall.productcenter.common.Constants;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 校验sku价格变更是否在流程中
 * @author pangjh
 *
 */
public class PcSkuPriceUpdateFlowCodeFunc extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		
		String product_code = mDataMap.get("product_code");
		
		int count = DbUp.upTable("sc_flow_main").count("outer_code",product_code,
				"flow_type",Constants.FLOW_TYPE_SKUPRICE_APPROVE,"current_status",Constants.FLOW_STATUS_SKUPRICE_CW);
		
		if(count >= 1){
			
			mWebResult.inErrorMessage(941901125, product_code);
			
		}
		
		return mWebResult;
	}

}
