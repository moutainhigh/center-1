package com.cmall.productcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.Constants;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 商品库存审批
 * @author pangjh
 *
 */
public class PcStockApproveFlowFunc implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {

		RootResult rootResult = new RootResult();

		/* 审核完成 */
		if (StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUSTOCK_FINISH)) {

			try {
				doFinish(flowCode, outCode, fromStatus, toStatus, mSubMap);
			} catch (Exception e) {

				rootResult.setResultCode(-1);

				rootResult.setResultMessage(e.getMessage());

			}

		}

		return rootResult;
	}

	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		
		RootResult rootResult = new RootResult();
		
		/*审核拒绝*/
		if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUSTOCK_REJECT)){
			
			doReject(flowCode, outCode, fromStatus, toStatus, mSubMap);
			
		}
		
		return rootResult;
	}
	
	/**
	 * 当流程完成时更新库存信息
	 * @param flowCode
	 * 		流程编号
	 * @param outCode
	 * 		产品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 * @param mSubMap
	 * 		参数集合
	 * @throws Exception 
	 */
	public void doFinish(String flowCode, String outCode, String fromStatus, String toStatus,MDataMap mSubMap) throws Exception{
		
		/*更新sku库存*/
		new ProductSkuStockService().updateSkuStockAndStatus(flowCode, outCode);
		
	}
	
	/**
	 * 当流程拒绝时更新库存信息状态
	 * @param flowCode
	 * 		流程编号
	 * @param outCode
	 * 		产品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 * @param mSubMap
	 * 		参数集合
	 */
	public void doReject(String flowCode, String outCode, String fromStatus, String toStatus,MDataMap mSubMap){
		
		/*更新过程表状态*/
		new ProductSkuStockService().updateStatus(flowCode, outCode);
		
	}


}
