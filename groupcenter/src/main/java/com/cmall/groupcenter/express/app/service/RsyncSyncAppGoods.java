package com.cmall.groupcenter.express.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.RsyncHomeHas;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class RsyncSyncAppGoods extends RsyncHomeHas<RsyncConfigSyncAppGoods, RsyncRequestSyncAppGoods, RsyncResponseSyncAppGoods> {
	final static RsyncConfigSyncAppGoods RSYNC_CONFIG_SYNC_APP_GOODS = new RsyncConfigSyncAppGoods();

	public RsyncConfigSyncAppGoods upConfig() {
		return RSYNC_CONFIG_SYNC_APP_GOODS;
	}
	private TxReckonOrderService txReckonOrderService;
	
	/**
	 * 获取发货的运单号，订单号，快递公司编号
	 * @param expressInfo
	 * @return
	 */
	private MWebResult insertReckonOrder(RsyncModelExpressInfo expressInfo) {
		MWebResult mWebResult = new MWebResult();
		String invc_id=expressInfo.getInvc_id();
		String ord_id=expressInfo.getOrd_id();
		String dlver_nm=expressInfo.getDlver_nm();
		String logisticse_code = "";
		UUID uuid = UUID.randomUUID();
		String uid = uuid.toString().replace("-", "");
		MDataMap mo = DbUp.upTable("oc_orderinfo").one("out_order_code",ord_id);
		if(mo!=null&&!mo.isEmpty()){
			ord_id = mo.get("order_code");
			String lock_id = WebHelper.addLock(60, "exp100-"+ord_id);
			if(StringUtils.isBlank(lock_id)){
				mWebResult.inErrorMessage(918558004, "");
				return mWebResult;
			}
			Map<String, String> order_code=DbUp.upTable("oc_order_shipments").oneWhere("order_code","","order_code=:order_code","order_code",ord_id);
			if(order_code==null||order_code.equals("")){
		    List<MDataMap> list=WebTemp.upTempDataList("sc_logisticscompany", "company_name,company_code", "", "");
		    if(list.size()>0){
		    for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				MDataMap mDataMap = (MDataMap) iterator.next();
				if(mDataMap.containsKey("company_name")&&StringUtils.contains(dlver_nm, mDataMap.get("company_name"))){
					logisticse_code=mDataMap.get("company_code");
				}
			}
		   }
		        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        if(invc_id==null||dlver_nm==null){
		        	return mWebResult;
		        }
				DbUp.upTable("oc_order_shipments").insert("uid",uid,"waybill",invc_id,"order_code",ord_id,"logisticse_code",logisticse_code,
						"logisticse_name",dlver_nm,"create_time",df.format(new Date()).toString(),"is_send100_flag","1");
				mWebResult.setResultMessage(ord_id+"成功入库");
				//mWebResult.setResultMessage(bInfo(iInfoCode, sParms));
			}
			WebHelper.unLock(lock_id);
		}
		return mWebResult;
	}

	
	@Override
	public RsyncResult doProcess(RsyncRequestSyncAppGoods tRequest,
			RsyncResponseSyncAppGoods tResponse) {
		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);
			}
		}
		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				
				result.setProcessNum(tResponse.getResult().size());
				for (RsyncModelExpressInfo expressInfo : tResponse.getResult()) {
					MWebResult mResult = insertReckonOrder(expressInfo);
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
				// 设置处理信息
				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));
			}
		}
		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			result.setSuccessNum(iSuccessSum);
			result.setStatusData(tRequest.getEnd_time());
		}
		return result;
	}


	@Override
	public RsyncRequestSyncAppGoods upRsyncRequest() {
		RsyncRequestSyncAppGoods request = new RsyncRequestSyncAppGoods();
		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_time(rsyncDateCheck.getStartDate());
		request.setEnd_time(rsyncDateCheck.getEndDate());
		return request;
	}

	@Override
	public RsyncResponseSyncAppGoods upResponseObject() {
		return new RsyncResponseSyncAppGoods();
	}

}
