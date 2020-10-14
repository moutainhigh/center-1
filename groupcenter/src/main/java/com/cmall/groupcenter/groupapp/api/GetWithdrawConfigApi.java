package com.cmall.groupcenter.groupapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.groupapp.model.AccountModel;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoResult;
import com.cmall.groupcenter.groupapp.model.GetWithdrawConfigResult;
import com.cmall.groupcenter.groupapp.model.GoodsCricleInfo;
import com.cmall.groupcenter.groupapp.model.GoodsInfo;
import com.cmall.groupcenter.groupapp.model.RongYunSingleChatBean;
import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.cmall.groupcenter.groupapp.service.GoodsCricleService;
import com.cmall.groupcenter.groupapp.service.GoodsCricleServiceNew;
import com.cmall.groupcenter.groupapp.service.RongYunService;
import com.cmall.groupcenter.job.JobGoodsCricleInfo;
import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 商圈数据（热销榜,超返利）
 * 
 * @author wangzx 
 * 
 */
public class GetWithdrawConfigApi extends RootApi<GetWithdrawConfigResult, RootInput>{ 

	public GetWithdrawConfigResult Process(RootInput root,
			MDataMap mRequestMap) {
		GetWithdrawConfigResult result = new GetWithdrawConfigResult();
		String sql = "SELECT * FROM groupcenter.gc_withdraw_config c where c.withdraw_source='449747770001' and flag_status=1";
		Map<String, Object> map = DbUp.upTable("gc_withdraw_config").dataSqlOne(sql, null);
		if(map==null){
			result.setResultCode(-1);
		}else{
			result.setFee_money(Double.parseDouble(map.get("fee_money").toString()));
			result.setMaximumMoneyRange(Double.parseDouble(map.get("maximum_money_range").toString()));
			result.setMinimumWithdrawMoney(Double.parseDouble(map.get("minimum_withdraw_money").toString()));
		}
		
		return result;
	}
	
	
	
	
	
}
