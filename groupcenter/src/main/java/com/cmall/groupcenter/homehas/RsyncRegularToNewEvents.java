package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCancelReturnOrder;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncRegularToNew;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelCancelReturnOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncCancelReturnOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncRegularToNew;
import com.cmall.groupcenter.homehas.model.RsyncResponseRecordTvOrderStat;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncCancelReturnOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncRegularToNew;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 同步LD老推新活动
 * @author AngelJoy
 * @date 2020-03-18
 * @time 14:26
 *
 */
public class RsyncRegularToNewEvents extends
        RsyncHomeHas<RsyncConfigRsyncRegularToNew, RsyncRequestSyncRegularToNew, RsyncResponseSyncRegularToNew>{

	RsyncConfigRsyncRegularToNew RSYNC_REGULAR_TO_NEW = new RsyncConfigRsyncRegularToNew();

	RsyncRequestSyncRegularToNew request = new RsyncRequestSyncRegularToNew();
	RsyncResponseSyncRegularToNew  rsyncResponse = new RsyncResponseSyncRegularToNew();
    public RsyncConfigRsyncRegularToNew upConfig() {
        return RSYNC_REGULAR_TO_NEW;
    }

    public RsyncRequestSyncRegularToNew upRsyncRequest() {
        return request;
    }

    public RsyncResponseSyncRegularToNew upResponseObject() {
        return new RsyncResponseSyncRegularToNew();
    }

    public static void main(String[] args) {


        RsyncRegularToNewEvents RsyncRegularToNewEvents = new RsyncRegularToNewEvents();
        RsyncRegularToNewEvents.doRsync();
    }



	@Override
	public RsyncResult doProcess(RsyncRequestSyncRegularToNew tRequest, RsyncResponseSyncRegularToNew tResponse) {
		rsyncResponse = tResponse;
		return new RsyncResult();
	}

	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseSyncRegularToNew getResponseObject() {
		return rsyncResponse;
	}

}
