package com.cmall.groupcenter.groupapp.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupapp.model.AccountModel;
import com.cmall.groupcenter.groupapp.model.AddProductShareNumInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoInput;
import com.cmall.groupcenter.groupapp.model.GetGoodsCricleListInfoResult;
import com.cmall.groupcenter.groupapp.model.GoodsCricleInfo;
import com.cmall.groupcenter.groupapp.model.GoodsInfo;
import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.cmall.groupcenter.groupapp.service.ProductShareService;
import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商圈数据（热销榜,超返利）
 * 
 * @author wangzx 
 * 
 */
public class AddProductShareNumApi extends RootApiForToken<RootResultWeb, AddProductShareNumInput>{ 

	public RootResultWeb Process(AddProductShareNumInput inputParam,
			MDataMap mRequestMap) {
		ProductShareService service=new ProductShareService();
		RootResultWeb result = service.AddShareNum(inputParam.getProdctCode(), inputParam.getShareType());
		return result;
	}
}
