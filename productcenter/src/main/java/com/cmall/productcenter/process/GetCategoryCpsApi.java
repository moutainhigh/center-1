package com.cmall.productcenter.process;

import java.util.List;

import com.cmall.productcenter.model.GetCategoryCpsApiInput;
import com.cmall.productcenter.model.GetCategoryCpsApiResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * ClassName:店铺私有商品分类Api<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class GetCategoryCpsApi extends RootApi<GetCategoryCpsApiResult, GetCategoryCpsApiInput> {

	public GetCategoryCpsApiResult Process(GetCategoryCpsApiInput inputParam, MDataMap mRequestMap) {
		GetCategoryCpsApiResult result  = new GetCategoryCpsApiResult();
		if(inputParam.getCategory_code()!=null&&!inputParam.getCategory_code().isEmpty()){
			MDataMap map = new MDataMap();
			map.put("category_code", inputParam.getCategory_code());
			List<MDataMap> list = DbUp.upTable("pc_categoryinfo").queryAll("cpsrate", "", "", map);
			if(!list.isEmpty()){
				result.setCpsrate(list.get(0).get("cpsrate"));
			}
		}
		return result;
	}
}

