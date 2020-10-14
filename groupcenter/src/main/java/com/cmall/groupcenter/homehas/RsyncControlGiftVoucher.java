package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigControlGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncRequestControlGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncResponseControlGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncResult;

public class RsyncControlGiftVoucher extends RsyncHomeHas<RsyncConfigControlGiftVoucher, RsyncRequestControlGiftVoucher, RsyncResponseControlGiftVoucher> {

	private final static RsyncConfigControlGiftVoucher rsyncConfigControlGiftVoucher = new RsyncConfigControlGiftVoucher(); 
	
	@Override
	public RsyncConfigControlGiftVoucher upConfig() {
		
		return rsyncConfigControlGiftVoucher;
	}

	private RsyncRequestControlGiftVoucher rsyncRequestControlGiftVoucher = new RsyncRequestControlGiftVoucher();
	
	@Override
	public RsyncRequestControlGiftVoucher upRsyncRequest() {
		
		return rsyncRequestControlGiftVoucher;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestControlGiftVoucher tRequest, RsyncResponseControlGiftVoucher tResponse) {
		
		RsyncResult mWebResult = new RsyncResult();
		if(!tResponse.isSuccess()) {
			mWebResult.setResultCode(918501013);
			mWebResult.setResultMessage(bInfo(918501013));
			return mWebResult;
		}
		
		return mWebResult;
	}

	private RsyncResponseControlGiftVoucher rsyncResponseControlGiftVoucher = new RsyncResponseControlGiftVoucher();
	
	@Override
	public RsyncResponseControlGiftVoucher upResponseObject() {
		
		return rsyncResponseControlGiftVoucher;
	}

}
