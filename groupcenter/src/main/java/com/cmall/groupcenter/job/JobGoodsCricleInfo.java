package com.cmall.groupcenter.job;


import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoResult;
import com.cmall.groupcenter.groupapp.model.GoodsCricleInfo;
import com.cmall.groupcenter.groupapp.service.GoodsCricleService;
import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobGoodsCricleInfo extends RootJob{

	public void doExecute(JobExecutionContext context) {
		/*//热销榜
		String  hotMarketGoodListJson =getContent("0");
		DbUp.upTable("gc_goods_cricle_temp_info").dataUpdate(new MDataMap("content",hotMarketGoodListJson,"type","0"), "content", "type");
		
		//超级饭
		String  rebateGoodListJson =getContent("1");
		DbUp.upTable("gc_goods_cricle_temp_info").dataUpdate(new MDataMap("content",rebateGoodListJson,"type","1"), "content", "type");
*/		
	}
	
	
	public String getContent(String type){
		
		GetGoodsCricleListInfoInput inputParam = new GetGoodsCricleListInfoInput();
		PageOption page = new PageOption();
		page.setLimit(50);
		page.setOffset(0);
		inputParam.setPaging(page);
		inputParam.setSectionType(type);
		GoodsCricleService  service = new GoodsCricleService();
		/*GetGoodsCricleListInfoResult result = service.generateGoodsCricleInfo(inputParam, null, null);
		
		JsonHelper<List<GoodsCricleInfo>> jH = new JsonHelper<List<GoodsCricleInfo>>();*/
		//String content = jH.ObjToString(result);

		return null;
	}
	
}
