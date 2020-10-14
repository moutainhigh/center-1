package com.cmall.ordercenter.tallyorder;

import com.cmall.ordercenter.tallyorder.ApiTaskForGenerateFinancialStatements.TFGFInput;
import com.cmall.ordercenter.tallyorder.ApiTaskForGenerateFinancialStatements.TFGFResult;
import com.cmall.ordercenter.tallyorder.settle.TaskGenFinancialStatement;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiTaskForGenerateFinancialStatements extends RootApi<TFGFResult, TFGFInput> {
	public static class TFGFInput implements IBaseInput
	{
		
	}
	
	public static class TFGFResult implements IBaseResult
	{

		@Override
		public int getResultCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getResultMessage() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public TFGFResult Process(TFGFInput inputParam, MDataMap mRequestMap) {
		TFGFResult r = new TFGFResult();
		TaskGenFinancialStatement t = new TaskGenFinancialStatement();
		t.doExecute(null);
		return r;
	}
}
