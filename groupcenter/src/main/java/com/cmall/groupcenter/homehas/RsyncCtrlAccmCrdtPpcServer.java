package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.groupface.IRsyncConfig;
import com.cmall.groupcenter.homehas.model.RsyncRequestCtrlAccmCrdtPpcServer;
import com.cmall.groupcenter.homehas.model.RsyncResponseCtrlAccmCrdtPpcServer;
import com.cmall.groupcenter.homehas.model.RsyncResult;


/***
 * 家有积分、储值金、暂存款的占用、取消、使用接口
 */
public class RsyncCtrlAccmCrdtPpcServer extends RsyncHomeHas<IRsyncConfig, RsyncRequestCtrlAccmCrdtPpcServer, RsyncResponseCtrlAccmCrdtPpcServer> {

	private RsyncRequestCtrlAccmCrdtPpcServer request = new RsyncRequestCtrlAccmCrdtPpcServer();
	private RsyncResponseCtrlAccmCrdtPpcServer response = new RsyncResponseCtrlAccmCrdtPpcServer();
	
	public IRsyncConfig upConfig() {
		return rsyncConfig ;
	}

	public RsyncRequestCtrlAccmCrdtPpcServer upRsyncRequest() {
		return request;
	}

	public RsyncResult doProcess(RsyncRequestCtrlAccmCrdtPpcServer tRequest, RsyncResponseCtrlAccmCrdtPpcServer tResponse) {
		response = tResponse;
		return new RsyncResult();
	}

	public RsyncResponseCtrlAccmCrdtPpcServer upResponseObject() {
		return response;
	}

	public static final IRsyncConfig rsyncConfig = new IRsyncConfig(){
		@Override
		public String getRsyncTarget() {
			return "ctrlAccmCrdtPpcServer";
		}
	};
}
