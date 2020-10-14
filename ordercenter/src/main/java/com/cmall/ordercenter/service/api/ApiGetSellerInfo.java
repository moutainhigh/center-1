package com.cmall.ordercenter.service.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.service.api.ApiGetSellerInfo.ApiGetSellerInfoInput;
import com.cmall.ordercenter.service.api.ApiGetSellerInfo.ApiGetSellerInfoResult;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户结算3.0
 * 创建商户结算付款申请单时根据选择的商户UID反查商户属性
 * @author zht
 *
 */
public class ApiGetSellerInfo extends RootApi<ApiGetSellerInfoResult, ApiGetSellerInfoInput> {

	public ApiGetSellerInfoResult Process(ApiGetSellerInfoInput input, MDataMap mRequestMap) {
		ApiGetSellerInfoResult result = new ApiGetSellerInfoResult();
		String sellerUID = input.getSellerUid();
		if(StringUtils.isEmpty(sellerUID))
			return result;
		
		String sWhere = "uid='" + sellerUID + "'";
		String sFields = "uid,small_seller_code,seller_name";
		List<MDataMap> map=DbUp.upTable("uc_sellerinfo").queryAll(sFields, "", sWhere, null);
		for (MDataMap mDataMap : map) {
			SellerInfo si = new SellerInfo();
			si.setUid(mDataMap.get("uid"));
			si.setSmallSellerCode(mDataMap.get("small_seller_code"));
			si.setSellerName(mDataMap.get("seller_name"));
			result.setSellerInfo(si);
		}
		return result;
	}
	
	public static class ApiGetSellerInfoInput extends RootInput
	{
		public ApiGetSellerInfoInput() {}
		@ZapcomApi(value = "商户UID", require=1)
		private String sellerUid;
		public String getSellerUid() {
			return sellerUid;
		}
		public void setSellerUid(String sellerUid) {
			this.sellerUid = sellerUid;
		}
	}
	
	public static class ApiGetSellerInfoResult extends RootResult
	{
		public ApiGetSellerInfoResult() {}
		private SellerInfo sellerInfo = new SellerInfo();
		public SellerInfo getSellerInfo() {
			return sellerInfo;
		}
		public void setSellerInfo(SellerInfo sellerInfo) {
			this.sellerInfo = sellerInfo;
		}
	}
	
	public static class SellerInfo 
	{
		private String uid;
		private String smallSellerCode;
		private String sellerName;
		
		
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getSmallSellerCode() {
			return smallSellerCode;
		}
		public void setSmallSellerCode(String smallSellerCode) {
			this.smallSellerCode = smallSellerCode;
		}
		public String getSellerName() {
			return sellerName;
		}
		public void setSellerName(String sellerName) {
			this.sellerName = sellerName;
		}
	}
}
