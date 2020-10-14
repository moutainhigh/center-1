package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmassystem.load.LoadMemberLevel;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 描述: 同步LD的PLUS会员信息，用于更新缓存 <br>
 */
public class RsyncPlusCustInfo extends RsyncHomeHas<RsyncPlusCustInfo.TConfig, RsyncPlusCustInfo.TRequest, RsyncPlusCustInfo.TResponse> {

	private TRequest tRequest = new TRequest();
	private TResponse tResponse = new TResponse();
	
	public TConfig upConfig() {
		return new TConfig();
	}
	public TRequest upRsyncRequest() {
		RsyncDateCheck dateCheck = upDateCheck(upConfig());
		tRequest.setStart_time(dateCheck.getStartDate());
		tRequest.setEnd_time(dateCheck.getEndDate());
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
			mWebResult.setResultMessage("同步失败："+tResponse.msg);
			return mWebResult;
		}
		
		HomehasSupport custSupport = new HomehasSupport();
		List<String> phones;
		String memberCode;
		LoadMemberLevel loadMemberLevel = new LoadMemberLevel();
		
		// 有plus会员变更则更新缓存
		for(Cust cust : tResponse.getCustList()) {
			phones = custSupport.getPhones(cust.getCust_id());
			for(String phone : phones) {
				memberCode = (String)DbUp.upTable("mc_login_info").dataGet("member_code", "", new MDataMap("manage_code","SI2003","login_name",phone));
				if(memberCode != null) {
					loadMemberLevel.deleteInfoByCode(memberCode);
				}
			}
		}
		
		mWebResult.setStatusData(tRequest.getEnd_time());
		
		return mWebResult;
	}
	
	public static class TConfig extends RsyncConfigRsyncBase implements IRsyncDateCheck {
		@Override
		public String getRsyncTarget() {
			return "getPlusCustInfo";
		}

		@Override
		public String getBaseStartTime() {
			return "2020-01-15 00:00:00";
		}

		@Override
		public int getMaxStepSecond() {
			return 3600;
		}

		@Override
		public int getBackSecond() {
			return 300;
		}
	}
	
	public static class TResponse implements IRsyncResponse{
		private boolean success;
		private String msg;
		private List<Cust> custList = new ArrayList<Cust>();
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public List<Cust> getCustList() {
			return custList;
		}
		public void setCustList(List<Cust> custList) {
			this.custList = custList;
		}
	}
	
	public static class Cust {
		// Ld客代号
		private String cust_id;
		private String web_id;
		// 有效期开始时间
		private String plus_start_date;
		// 有效期结束时间
		private String plus_end_date;
		
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getWeb_id() {
			return web_id;
		}
		public void setWeb_id(String web_id) {
			this.web_id = web_id;
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
	}
	
	public static class TRequest implements IRsyncRequest{
		private String start_time;
		private String end_time;
		
		public String getStart_time() {
			return start_time;
		}
		public void setStart_time(String start_time) {
			this.start_time = start_time;
		}
		public String getEnd_time() {
			return end_time;
		}
		public void setEnd_time(String end_time) {
			this.end_time = end_time;
		}
		
	}
}
