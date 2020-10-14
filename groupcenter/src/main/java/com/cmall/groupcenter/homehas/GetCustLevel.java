package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;

public class GetCustLevel extends RsyncHomeHas<GetCustLevel.RsyncConfig,GetCustLevel.RsyncRequest, GetCustLevel.RsyncResponse>{

	private RsyncRequest req = new RsyncRequest();
	private RsyncResponse resp = new RsyncResponse();
	
	@Override
	public RsyncConfig upConfig() {
		return new RsyncConfig();
	}

	@Override
	public RsyncRequest upRsyncRequest() {
		return req;
	}

	@Override
	public RsyncResult doProcess(RsyncRequest tRequest, RsyncResponse tResponse) {
		return new RsyncResult();
	}

	@Override
	public RsyncResponse upResponseObject() {
		return resp;
	}
	

	public static class RsyncConfig extends RsyncConfigRsyncBase {
		public String getRsyncTarget() {
			return "getCustLvlByMobile";
		}
	}
	
	public static class RsyncRequest implements IRsyncRequest{
		private String mobile;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
	}
	
	public static class RsyncResponse implements IRsyncResponse{
		private String cust_id;
		private String custlvl;
		private String plus_start_date;
		private String plus_end_date;
		private String is_plus;
		private boolean success;
		
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getCustlvl() {
			return custlvl;
		}
		public void setCustlvl(String custlvl) {
			this.custlvl = custlvl;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getPlus_start_date() {
			return plus_start_date;
		}
		public void setPlus_start_date(String plus_start_date) {
			this.plus_start_date = plus_start_date;
		}
		public String getPlus_end_date() {
			return plus_end_date;
		}
		public void setPlus_end_date(String plus_end_date) {
			this.plus_end_date = plus_end_date;
		}
		public String getIs_plus() {
			return is_plus;
		}
		public void setIs_plus(String is_plus) {
			this.is_plus = is_plus;
		}
		
	}

}
