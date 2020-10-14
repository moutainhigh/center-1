package com.cmall.groupcenter.homehas;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.cmall.groupcenter.homehas.config.RsyncEmployAccountConfig;
import com.cmall.groupcenter.homehas.model.AccountSubOrderInfo;
import com.cmall.groupcenter.homehas.model.RsyncEmployAccountRequest;
import com.cmall.groupcenter.homehas.model.RsyncEmployAccountResponse;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmasorder.model.TeslaModelOrderDetail;
import com.srnpr.xmasorder.model.TeslaModelOrderPay;
import com.srnpr.xmasorder.service.TeslaEmployAmtService;
import com.srnpr.xmasorder.x.TeslaXOrder;
import com.srnpr.xmaspay.util.BeanComponent;
import com.srnpr.zapcom.basemodel.MDataMap;

/**
 * 储备金、暂存款（占用 取消 使用）
 * @author pang_jhui
 *
 */
public class RsyncEmployAccountInfo extends RsyncHomeHas<RsyncEmployAccountConfig, RsyncEmployAccountRequest, RsyncEmployAccountResponse> {

	private RsyncEmployAccountConfig config = new RsyncEmployAccountConfig();
	
	private RsyncEmployAccountRequest request = new RsyncEmployAccountRequest();
	
	private RsyncEmployAccountResponse response = new RsyncEmployAccountResponse();
	
	@Override
	public RsyncEmployAccountConfig upConfig() {
		
		return config;
	}

	@Override
	public RsyncEmployAccountRequest upRsyncRequest() {
		
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncEmployAccountRequest tRequest, RsyncEmployAccountResponse tResponse) {
		
		RsyncResult mWebResult = new RsyncResult();
		
		if(tResponse != null && !tResponse.getSuccess()){
			
			mWebResult.setResultCode(-1);
			
			mWebResult.setResultMessage(tResponse.getMessage());
			
		}
		
		return mWebResult;
	}

	@Override
	public RsyncEmployAccountResponse upResponseObject() {
		
		return response;
	}
	
	/**
	 * 初始化请求对象
	 */
	public void initRequest(MDataMap mDataMap){
		
		try {
			
			request = BeanComponent.getInstance().invoke(RsyncEmployAccountRequest.class, mDataMap,false);
			
		} catch (Exception e) {
			
			bLogError(0, e.getMessage());
			
		}
		
	}
	
	/**
	 * 初始化占用金请求信息
	 * @param teslaXOrder
	 * @return
	 */
	public void initAmtRequest(TeslaXOrder teslaXOrder){

//		MDataMap extendMap = new TeslaEmployAmtService().getMemberInfoHas(teslaXOrder.getUorderInfo().getBuyerCode());
		MDataMap extendMap = new TeslaEmployAmtService().getMemberInfoHas1(teslaXOrder.getUorderInfo().getBuyerMobile());
		
		request.setCust_id(extendMap.get("homehas_code"));
		
		request.setCust_nm(extendMap.get("member_name"));

		request.setAddress(teslaXOrder.getAddress().getAddress());

		request.setApp_ord_id(teslaXOrder.getUorderInfo().getBigOrderCode());
		
		request.setCrdt_amt(new BigDecimal(teslaXOrder.getUse().getZck_money()));
		
		request.setPpc_amt(new BigDecimal(teslaXOrder.getUse().getCzj_money()));
		
		Map<String, AccountSubOrderInfo> orderPayMap = new HashMap<String, AccountSubOrderInfo>();
		
		Map<String, TeslaModelOrderDetail> orderDetailMap = convertOrderDetailInfos(teslaXOrder);
		
		for(TeslaModelOrderPay teslaModelOrderPay : teslaXOrder.getOcOrderPayList()){
			
			if(orderPayMap.containsKey(teslaModelOrderPay.getOrderCode())){
				
				AccountSubOrderInfo subOrderInfo = orderPayMap.get(teslaModelOrderPay.getOrderCode());	
				
				initAmt(subOrderInfo, teslaModelOrderPay);
				
				orderPayMap.put(teslaModelOrderPay.getOrderCode(), subOrderInfo);
				
			}else{
				
				AccountSubOrderInfo subOrderInfo = new AccountSubOrderInfo();
				
				subOrderInfo.setApp_child_ord_id(teslaModelOrderPay.getOrderCode());
				
				initAmt(subOrderInfo, teslaModelOrderPay);
				
				TeslaModelOrderDetail teslaModelOrderDetail = orderDetailMap.get(teslaModelOrderPay.getOrderCode());
				
				if(teslaModelOrderDetail != null){
					
					subOrderInfo.setGood_id(teslaModelOrderDetail.getSkuCode());
					
					subOrderInfo.setGood_nm(teslaModelOrderDetail.getSkuName());
					
				}
				
				orderPayMap.put(teslaModelOrderPay.getOrderCode(), subOrderInfo);
				
			}			
			
		}
		
		request.getOrders().addAll(orderPayMap.values());
		
	}
	
	/**
	 * 初始化储值金等参数
	 * @param accountSubOrderInfo
	 * 		子订单信息
	 * @param teslaModelOrderPay
	 * 		支付信息
	 */
	public void initAmt(AccountSubOrderInfo subOrderInfo,TeslaModelOrderPay teslaModelOrderPay){
		
		/*储值金*/
		if("449746280006".equals(teslaModelOrderPay.getPayType())){
			
			subOrderInfo.setChild_ppc_amt(teslaModelOrderPay.getPayedMoney());
			
		}
		
		/*暂存款*/
		if("449746280007".equals(teslaModelOrderPay.getPayType())){
			
			subOrderInfo.setChild_crdt_amt(teslaModelOrderPay.getPayedMoney());
			
		}
		
	}
	
	/**
	 * 获取订单详情信息
	 * @param orderCode
	 * 		订单编号
	 * @return 订单详情
	 */
	public Map<String ,TeslaModelOrderDetail> convertOrderDetailInfos(TeslaXOrder teslaXOrder){
		
		Map<String ,TeslaModelOrderDetail> orderDetails = new HashMap<String ,TeslaModelOrderDetail>();
		
		for (TeslaModelOrderDetail orderDetail : teslaXOrder.getOrderDetails()) {
			
			if(orderDetail != null){
				
				orderDetails.put(orderDetail.getOrderCode(), orderDetail);
				
			}
			
		}
		
		return orderDetails;
		
	}

}
