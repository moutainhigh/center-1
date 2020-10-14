package com.cmall.groupcenter.groupapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.groupapp.model.AccountModel;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoResult;
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
public class GetGoodsCricleListInfoApi extends RootApiForToken<GetGoodsCricleListInfoResult, GetGoodsCricleListInfoInput>{ 

	public GetGoodsCricleListInfoResult Process(GetGoodsCricleListInfoInput inputParam,
			MDataMap mRequestMap) {

		//RongYunService service = new RongYunService();
		
		//RongYunSingleChatBean bean = new RongYunSingleChatBean();
		
		//service.singleChatMessageSend();
		
		GoodsCricleService gcService= new GoodsCricleService();
		GetGoodsCricleListInfoResult result =gcService.generateGoodsCricleInfo(inputParam,null,null,this.getOauthInfo().getLoginName());
		
		//JobGoodsCricleInfo job = new JobGoodsCricleInfo();
		//job.doExecute(null);
		return result;
		
		//return result;
	}
	
	public String getApiKey(){
		String apkKey=null;
		 String productShareSql ="select api_key from  zapdata.za_apiauthorize where  manage_code=:manage_code";
    	 Map<String, Object> rmap = DbUp.upTable("gc_product_share_log").dataSqlOne(productShareSql, new MDataMap("manage_code",null));
		 if(rmap!=null && rmap.get("api_key")!=null){
			 apkKey  = String .valueOf(rmap.get("api_key"));
		 }
    	 return apkKey;
	}
	
	
}
