package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.service.ProductSkuPriceService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商品价格变更审批
 * @author pang_jhui
 *
 */
public class ProductSkuPriceFlowFunc extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				
				List<LinkedHashMap<String, Object>> pcFlows = new ArrayList<LinkedHashMap<String, Object>>();
			
				pcFlows = new JsonHelper<List<LinkedHashMap<String, Object>>>().StringToObjExp(mSubDataMap.get("json"), pcFlows);				
				
				new ProductSkuPriceService().createProductSkuPriceFlow(initDataMap(pcFlows), mSubDataMap, Constants.FLOW_STATUS_SKUPRICE_CW);

				
			}
		} catch (Exception e) {
			
			mResult.setResultCode(-1);
			mResult.setResultMessage(e.getMessage());
			
		}
		return mResult;
	}
	
	/**
	 * 转换数据集合
	 * @param pcFlows
	 * 		审批信息
	 * @return
	 */
	public List<MDataMap> initDataMap(List<LinkedHashMap<String, Object>> pcFlows){
		
		List<MDataMap> maps = new ArrayList<MDataMap>();
		
		for(Map<String, Object> dataMap : pcFlows){
			
			MDataMap mDataMap = new MDataMap(dataMap);
			
			maps.add(mDataMap);			
			
		}
		
		return maps;
		
		
	}
	
}
