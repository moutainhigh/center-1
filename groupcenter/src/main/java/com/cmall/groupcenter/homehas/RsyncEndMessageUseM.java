package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigEndMessageUseM;
import com.cmall.groupcenter.homehas.model.RsyncRequestEndMessageUseM;
import com.cmall.groupcenter.homehas.model.RsyncResponseEndMessageUseM;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.util.SmsUtil;

public class RsyncEndMessageUseM
		extends
		RsyncHomeHas<RsyncConfigEndMessageUseM, RsyncRequestEndMessageUseM, RsyncResponseEndMessageUseM> {

	private final static RsyncConfigEndMessageUseM rsyncConfigEndMessage = new RsyncConfigEndMessageUseM();

	public RsyncConfigEndMessageUseM upConfig() {

		return rsyncConfigEndMessage;
	}

	private RsyncRequestEndMessageUseM rsyncRequestEndMessage = new RsyncRequestEndMessageUseM();

	public RsyncRequestEndMessageUseM upRsyncRequest() {
		// TODO Auto-generated method stub
		return rsyncRequestEndMessage;
	}

	public RsyncResult doProcess(RsyncRequestEndMessageUseM tRequest,
			RsyncResponseEndMessageUseM tResponse) {
		
		RsyncResult result = new RsyncResult();
		
		String hp_tel = tRequest.getHp_tel();
		String content = tRequest.getContent();
		
		SmsUtil smsUtil=new SmsUtil();
		StringBuffer error= new StringBuffer();
		boolean b=smsUtil.sendSms(hp_tel, content,error);
		
		if(!b){
			result.setResultCode(918501011);
			result.setResultMessage(bInfo(918501011,error));
		}
		
		return result;
	}

	public RsyncResponseEndMessageUseM upResponseObject() {

		return new RsyncResponseEndMessageUseM();
	}

}
