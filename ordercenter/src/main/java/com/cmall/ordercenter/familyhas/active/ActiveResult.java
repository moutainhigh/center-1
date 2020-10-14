package com.cmall.ordercenter.familyhas.active;

import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ActiveResult extends RootResultWeb {

	public void inErrorMessage(int iErrorCode, Object... sParms) {

		setResultCode(iErrorCode);

		setResultMessage(TopUp.upLogInfo(iErrorCode, sParms));
	}

	public boolean upFlagTrue() {
		return getResultCode() == 1;
	}
	
}
