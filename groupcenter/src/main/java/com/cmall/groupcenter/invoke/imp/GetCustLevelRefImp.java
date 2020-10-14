package com.cmall.groupcenter.invoke.imp;

import com.cmall.groupcenter.homehas.GetCustLevel;
import com.cmall.groupcenter.homehas.GetCustLevel.RsyncResponse;
import com.srnpr.xmassystem.invoke.ref.GetCustLevelRef;
import com.srnpr.xmassystem.invoke.ref.model.GetCustLevelResult;

public class GetCustLevelRefImp implements GetCustLevelRef {

	@Override
	public GetCustLevelResult getCustLevel(String mobile) {
		GetCustLevel rsync = new GetCustLevel();
		rsync.upRsyncRequest().setMobile(mobile);
		rsync.doRsync();
		
		RsyncResponse resp = rsync.upProcessResult();
		
		GetCustLevelResult result = new GetCustLevelResult();
		if(resp == null) {
			result.setResultCode(0);
			result.setCust_id("");
			result.setCustlvl("");
			result.setPlus_end_date("");
			result.setPlus_start_date("");
		} else {
			result.setResultCode(resp.isSuccess() ? 1 : 0);
			result.setCust_id(resp.getCust_id());
			result.setCustlvl(resp.getCustlvl());
			result.setPlus_end_date(resp.getPlus_end_date());
			result.setPlus_start_date(resp.getPlus_start_date());
			
			if(!"true".equalsIgnoreCase(resp.getIs_plus())) {
				result.setPlus_end_date("");
				result.setPlus_start_date("");
			}
		}
		return result;
	}


}
