package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelOrderInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.txservice.TxPurchaseOrderService;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步家有订单<br>
 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
 * 对原同步逻辑重写 ，原类：
 * @see com.cmall.groupcenter.homehas.RsyncSyncOrders
 */
public class RsyncSyncOrdersV2 extends RsyncHomeHas<RsyncConfigSyncOrders, RsyncRequestSyncOrders, RsyncResponseSyncOrders> {

	final static RsyncConfigSyncOrders RSYNC_CONFIG_SYNC_ORDERS = new RsyncConfigSyncOrders();

	public RsyncConfigSyncOrders upConfig() {
		return RSYNC_CONFIG_SYNC_ORDERS;
	}
	
	public RsyncResponseSyncOrders upResponseObject() {
		return new RsyncResponseSyncOrders();
	}

	public RsyncRequestSyncOrders upRsyncRequest() {
		// 返回输入参数
		RsyncRequestSyncOrders request = new RsyncRequestSyncOrders();

		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_date(rsyncDateCheck.getStartDate());
		request.setEnd_date(rsyncDateCheck.getEndDate());

		return request;
	}

	public RsyncResult doProcess(RsyncRequestSyncOrders tRequest, RsyncResponseSyncOrders tResponse) {
		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (tResponse != null && tResponse.getResult() != null) {
			result.setProcessNum(tResponse.getResult().size());
		} else {
			result.setProcessNum(0);
		}

		// 判断有需要处理的数据才开始处理
		if (result.getProcessNum() > 0) {
			// 循环一下列表，把订单商品明细上面的订单状态统一
			Map<String,String> map = new HashMap<String, String>();
			for (RsyncModelOrderInfo orderInfo : tResponse.getResult()) {
				map.put(orderInfo.getYc_orderform_num(), orderInfo.getYc_orderform_status());
			}

			for (RsyncModelOrderInfo orderInfo : tResponse.getResult()) {
				orderInfo.setLast_yc_orderform_status(map.get(orderInfo.getYc_orderform_num()));
				
				MWebResult mResult = syncOrderInfo(orderInfo);

				// 如果成功则将成功计数加1
				if (mResult.upFlagTrue()) {
					iSuccessSum++;
				} else {
					if (result.getResultList() == null) {
						result.setResultList(new ArrayList<Object>());
					}
					result.getResultList().add(mResult.getResultMessage()+"["+orderInfo.getYc_orderform_num()+"]");
				}
			}
		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		result.setSuccessNum(iSuccessSum);
		result.setStatusData(tRequest.getEnd_date());
		result.setProcessData(bInfo(918501102, result.getProcessNum(), iSuccessSum, result.getProcessNum() - iSuccessSum));
		return result;
	}

	private MWebResult syncOrderInfo(RsyncModelOrderInfo orderInfo){
		MWebResult mWebResult = new MWebResult();

		String memberCode = orderInfo.getWeb_id();
		if(StringUtils.isBlank(memberCode)){
			MDataMap orderMap = DbUp.upTable("oc_orderinfo").oneWhere("", "", "", "out_order_code",orderInfo.getYc_orderform_num());
			if(orderMap != null){
				memberCode = orderMap.get("buyer_code");
			}
		}
		
		if(StringUtils.isBlank(memberCode)){
			// 如果用户不存在则自动注册
			mWebResult = new HomehasSupport().registerByHomeHasCode(orderInfo.getYc_vipuser_num());
			if(!mWebResult.upFlagTrue()) {
				return mWebResult;
			}
			
			memberCode = (String)mWebResult.getResultObject();
			if(memberCode == null){
				mWebResult.inErrorMessage(918505131, orderInfo.getYc_vipuser_num());
				return mWebResult;
			}
		}
		
		// 保存或更新订单
		TxPurchaseOrderService purchaseOrderService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxPurchaseOrderService");
		purchaseOrderService.insertOrder(orderInfo, MemberConst.MANAGE_CODE_HOMEHAS, memberCode);
		
		return mWebResult;
	}		
}
