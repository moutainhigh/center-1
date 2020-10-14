package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
/**
 * 使用商品返回数据
 * @author shiyz
 *
 */
public class TryOrderServiceApiResult extends RootResult {

	@ZapcomApi(value="商品试用")
	TrialOrderList TRIAL_ORDER =new TrialOrderList();

	public TrialOrderList getTRIAL_ORDER() {
		return TRIAL_ORDER;
	}

	public void setTRIAL_ORDER(TrialOrderList tRIAL_ORDER) {
		TRIAL_ORDER = tRIAL_ORDER;
	}

}
