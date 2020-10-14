package com.cmall.groupcenter.homehas;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetOrderTracking;
import com.cmall.groupcenter.homehas.model.ResponseGetOrderTrackingList;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetOrderTracking;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetOrderTracking;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 订单配送轨迹查询接口   同步数据
 * @author wz
 *
 */
public class RsyncGetOrderTracking extends RsyncHomeHas<RsyncConfigGetOrderTracking,RsyncRequestGetOrderTracking,RsyncResponseGetOrderTracking>{
	private final static RsyncConfigGetOrderTracking rsyncConfigGetOrderTracking = new RsyncConfigGetOrderTracking();
	
	public RsyncConfigGetOrderTracking upConfig() {
		return rsyncConfigGetOrderTracking;
	}
	
	private RsyncRequestGetOrderTracking requestGetOrderTracking = new RsyncRequestGetOrderTracking();
	
	public RsyncRequestGetOrderTracking upRsyncRequest() {
		return requestGetOrderTracking;
	}

	public RsyncResult doProcess(RsyncRequestGetOrderTracking tRequest,
			RsyncResponseGetOrderTracking tResponse) {
		
		RsyncResult mWebResult=new RsyncResult();
		rsyncResponseGetOrderTracking = tResponse;
		if(!StringUtils.isEmpty(tRequest.getOrd_id())){
			try {
				MDataMap map = DbUp.upTable("oc_orderinfo").one("out_order_code",
						tRequest.getOrd_id()); // 查询订单(按订单号查询)
				
				if(map!=null && !"".equals(map) && map.size()>0){
					MDataMap trackingMap = new MDataMap();
					trackingMap.put("order_code", map.get("order_code"));
					trackingMap.put("out_order_code", tRequest.getOrd_id());
					
					for(ResponseGetOrderTrackingList responseGetOrderTracking : tResponse.getResult()){
						trackingMap.put("yc_update_time", StringUtils.trimToEmpty(responseGetOrderTracking.getYc_update_time()));
						trackingMap.put("yc_dis_time", StringUtils.trimToEmpty(responseGetOrderTracking.getYc_dis_time()));
						trackingMap.put("yc_express_num", StringUtils.trimToEmpty(responseGetOrderTracking.getYc_express_num()));
						trackingMap.put("yc_delivergoods_user_name", StringUtils.trimToEmpty(responseGetOrderTracking.getYc_delivergoods_user_name()));
						trackingMap.put("outgo_no", StringUtils.trimToEmpty(responseGetOrderTracking.getOutgo_no()));
						trackingMap.put("outgo_time", StringUtils.trimToEmpty(responseGetOrderTracking.getOutgo_time()));
						
						Map<String,Object> selectTracking = DbUp.upTable("oc_order_tracking").dataSqlOne("select * from oc_order_tracking where order_code=:order_code " +
								"and yc_update_time=:yc_update_time ", trackingMap);
						
						//只有同步有数据  且  oc_order_tracking中没有此值时  插入数据
						if(selectTracking!=null && !"".equals(selectTracking) && selectTracking.size()>0 && 
								trackingMap!=null && !"".equals(trackingMap) && trackingMap.size()>0){   
							
						}else{
							DbUp.upTable("oc_order_tracking").dataInsert(trackingMap); // 插入订单跟踪信息表
						}
					}
					
				}
			} catch (Exception e) {
				mWebResult.setResultCode(918507001);
				mWebResult.setResultMessage(bInfo(918507001,tRequest.getOrd_id()));
				return mWebResult;
			}
			
		}
		return mWebResult;
	}

	private RsyncResponseGetOrderTracking rsyncResponseGetOrderTracking = new RsyncResponseGetOrderTracking();
	
	public RsyncResponseGetOrderTracking upResponseObject() {
		return new RsyncResponseGetOrderTracking();
	}

	@Override
	protected boolean isSaveLog() {
		return false;
	}
	
	public RsyncResponseGetOrderTracking getResponseObject() {
		return rsyncResponseGetOrderTracking;
	}
}
