package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.GroupConstant;
import com.cmall.groupcenter.account.model.GetBankInfoInput;
import com.cmall.groupcenter.account.model.GetBankInfoResult;
import com.cmall.groupcenter.account.model.GetBankInfoResult.BankInfo;
import com.cmall.groupcenter.model.QueryBankInfoResult;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取银行卡信息
 * 
 * @author chenxk
 * 
 */
public class ApiGetBankInfo extends RootApiForManage<GetBankInfoResult, GetBankInfoInput> {

	public GetBankInfoResult Process(GetBankInfoInput inputParam, MDataMap mRequestMap) {
		
		GetBankInfoResult getBankInfoResult = new GetBankInfoResult();

		//1,判断此银行卡号是否已经存在
		/*if(DbUp.upTable("gc_member_bank").count("card_code",inputParam.getBankNo(),"flag_enable","1") >0){
			getBankInfoResult.inErrorMessage(915805223);
		}*/
		List<BankInfo> bankList = null;
		if(getBankInfoResult.upFlagTrue()) {
			bankList = new ArrayList<BankInfo>();
			String result;
			try {
				result = WebClientSupport.upPost(bConfig("groupcenter.bankCardRequestUrl"), new MDataMap("command",bConfig("groupcenter.bankCardGetCardInfoCode"),"bankaccount",inputParam.getBankNo()));
				
				QueryBankInfoResult bankinfo = new JsonHelper<QueryBankInfoResult>().GsonFromJson(result, new QueryBankInfoResult());
				
				if(bankinfo != null && "00".equals(bankinfo.getResponse())){
					BankInfo banki = getBankInfoResult.new BankInfo();
					banki.setBankName(bankinfo.getBankname());
					banki.setCardKind("1".equals(bankinfo.getCardtype()) ? GroupConstant.cardTypeMap.get("1") : ("2".equals(bankinfo.getCardtype()) ? GroupConstant.cardTypeMap.get("2") : GroupConstant.cardTypeMap.get("1")));
					bankList.add(banki);
				}else{
					getBankInfoResult.setResultCode(-2);
				}
				getBankInfoResult.setBankList(bankList);
			} catch (Exception e) {
				this.bLogError(915805225, inputParam.getBankNo(),e.getMessage());
				getBankInfoResult.setResultCode(-2);
			}
		}
		
		//2,如果第三方接口返回信息为空，那么数据库初始化数据
		if(getBankInfoResult.getResultCode() == -2){
			
			bankList = new ArrayList<BankInfo>();
			
			String bankNoLength = String.valueOf(inputParam.getBankNo().length());
			StringBuffer sb = new StringBuffer();
			MDataMap mDateMap = new MDataMap();
			mDateMap.put("bank_no_length",bankNoLength);
			sb.append("bank_no_length=:bank_no_length and bank_no_prefix in (");
			
			if(inputParam.getBankNo().length() <10){
				sb.append(":bank_no_prefix");
				mDateMap.put("bank_no_prefix",inputParam.getBankNo());
			}else{
				for(int i=1;i<6;i++){
					sb.append(":bank_no_prefix"+i);
					if(i != 5)
						sb.append(",");
					mDateMap.put("bank_no_prefix"+i, inputParam.getBankNo().substring(0, 3+i));
				}
			}
			sb.append(")");
			List<MDataMap> mdate = DbUp.upTable("gc_bank_no_info").query("bank_name,bank_card_alias,card_kind",
					"", sb.toString(), mDateMap, -1, 0);
			
			if(mdate != null && mdate.size() >0){
				for(MDataMap mDate : mdate){
					BankInfo bankInfo = getBankInfoResult.new BankInfo();
					bankInfo.setBankCardAlias(mDate.get("bank_card_alias"));
					bankInfo.setBankName(mDate.get("bank_name"));
					bankInfo.setCardKind(mDate.get("card_kind"));
					
					bankList.add(bankInfo);
				}
				getBankInfoResult.setResultCode(1);
				getBankInfoResult.setBankList(bankList);
			}else{
				getBankInfoResult.inErrorMessage(915805224);
			}
		}
		
		return getBankInfoResult;
	}
}
