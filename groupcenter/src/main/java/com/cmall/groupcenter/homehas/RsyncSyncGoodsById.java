package com.cmall.groupcenter.homehas;


import com.cmall.groupcenter.homehas.config.RsyncConfigSyncGoodsById;
import com.cmall.groupcenter.homehas.model.RsyncModelGoods;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncGoodsById;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncGoodsById;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步家有商品处理类
 * @author jl
 *
 */
public class RsyncSyncGoodsById extends RsyncHomeHas<RsyncConfigSyncGoodsById, RsyncRequestSyncGoodsById, RsyncResponseSyncGoodsById> {

	final static RsyncConfigSyncGoodsById RSYNC_CONFIG_SYNC_GOODSBYID = new RsyncConfigSyncGoodsById();

	public RsyncConfigSyncGoodsById upConfig() {
		return RSYNC_CONFIG_SYNC_GOODSBYID;
	}

	
	private RsyncRequestSyncGoodsById requestSyncGoodsById = new RsyncRequestSyncGoodsById();
	public RsyncRequestSyncGoodsById upRsyncRequest() {
		return requestSyncGoodsById;
	}

	private boolean success=false;
	
	public RsyncResult doProcess(RsyncRequestSyncGoodsById tRequest,RsyncResponseSyncGoodsById tResponse) {
		RsyncResult result = new RsyncResult();
		
		if("true".equals(tResponse.getSuccess())){
			
			//这里不确定会返回几条商品的信息
			RsyncSyncGoods rsyncSyncGoods = new RsyncSyncGoods();
			for (RsyncModelGoods modelGoods : tResponse.getResult()) {
				MWebResult insertResult= rsyncSyncGoods.insertGoods(modelGoods, DateUtil.getSysDateTimeString());
				if(insertResult.upFlagTrue()){
					success=true;
				}else{
					success=false;
					break;
				}
			}
		}
		
		return result;
	}

	public RsyncResponseSyncGoodsById upResponseObject() {
		return new RsyncResponseSyncGoodsById();
	}
	
	public boolean isSuccess(){
		return success;
	}
}
