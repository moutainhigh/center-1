package com.cmall.usercenter.service.api;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.usercenter.model.api.ApiGetContractNewResult;
import com.cmall.usercenter.model.api.ApiGetContractNewInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 
 * @author liqt
 *
 */
public class ApiGetContractNewChart extends RootApi<RootResultWeb, ApiGetContractNewInput> {
	public RootResultWeb Process(ApiGetContractNewInput inputParam, MDataMap mDataMap) {
		
		ApiGetContractNewResult conterctNewResult = new ApiGetContractNewResult();
		
		String sellerCode = inputParam.getSellerCode();
		if(sellerCode==null||sellerCode.isEmpty()){
			return conterctNewResult;
		}
		MDataMap sellerInfoExtend = DbUp.upTable("v_uc_contract_new").one("small_seller_code",sellerCode);
		MDataMap sellerInfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code",sellerCode);
		if(sellerInfoExtend!=null&&!sellerInfoExtend.isEmpty()){
			conterctNewResult.setSellerName(sellerInfo.get("seller_name"));
			conterctNewResult.setBusinessPerson(sellerInfoExtend.get("business_person"));
			conterctNewResult.setBusinessPersonPhone(sellerInfoExtend.get("business_person_phone"));
			conterctNewResult.setCompanyPhone(sellerInfoExtend.get("company_phone"));
			conterctNewResult.setInvoiceReturnAddress(sellerInfoExtend.get("invoice_return_address"));
			conterctNewResult.setInvoiceReturnPerson(sellerInfoExtend.get("invoice_return_person"));
			conterctNewResult.setInvoiceReturnPhone(sellerInfoExtend.get("invoice_return_phone"));
			conterctNewResult.setBusinessLicenseType(sellerInfoExtend.get("business_license_type"));
			conterctNewResult.setRegistrationNumber(sellerInfoExtend.get("registration_number"));
			conterctNewResult.setRegisterName(sellerInfoExtend.get("register_name"));
			conterctNewResult.setBranchName(sellerInfoExtend.get("branch_name"));
			conterctNewResult.setBankAccount(sellerInfoExtend.get("bank_account"));
			conterctNewResult.setBranchAddress(sellerInfoExtend.get("branch_address"));
			conterctNewResult.setUcSellerType(sellerInfoExtend.get("uc_seller_type"));
			conterctNewResult.setAccountClearType(sellerInfoExtend.get("account_clear_type"));
			conterctNewResult.setMoneyCollectionWay(sellerInfoExtend.get("money_collection_way"));
			conterctNewResult.setMoneyProportion(sellerInfoExtend.get("money_proportion"));
			conterctNewResult.setQualityRetentionMoney(sellerInfoExtend.get("quality_retention_money"));
		}
		
		return conterctNewResult;
	}
}
