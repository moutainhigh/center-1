package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 
 * 描述: 同步开户行支行信息到LD <br>
 */
public class RsyncBankinfoBranchToLD extends RsyncHomeHas<RsyncBankinfoBranchToLD.TConfig, RsyncBankinfoBranchToLD.TRequest, RsyncBankinfoBranchToLD.TResponse> {

	private TRequest tRequest = new TRequest();
	private TResponse tResponse = new TResponse();
	
	public TConfig upConfig() {
		return new TConfig();
	}
	public TRequest upRsyncRequest() {
		return tRequest;
	}
	public TResponse upResponseObject() {
		return tResponse;
	}

	public RsyncResult doProcess(TRequest tRequest, TResponse tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult mWebResult = new RsyncResult();
		if(!tResponse.success){
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("同步支行信息失败："+tResponse.message);
			return mWebResult;
		}
		
		return mWebResult;
	}
	
	public static class TConfig extends RsyncConfigRsyncBase{
		@Override
		public String getRsyncTarget() {
			return "DlrBankImport";
		}
	}
	
	public static class TResponse implements IRsyncResponse{
		public boolean success;
		public String message;
	}
	
	public static class TRequest implements IRsyncRequest{
		public String etr_id = "app";
		public List<Bankinfo> bank_info = new ArrayList<Bankinfo>();
	}
	
	public static class Bankinfo{
		public String bank_cd = "";
		public String city_code = "";
		public String bankdoc = "";
		public String bankdoc_cd = "";
	}
}
