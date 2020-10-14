package com.cmall.newscenter.beauty.api;

import java.util.Map;

import com.cmall.newscenter.beauty.model.CosmeticBagPostTitleInput;
import com.cmall.newscenter.beauty.model.CosmeticBagPostTitleResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—发帖标题API
 * 
 * @author yangrong date: 2015-01-30
 * @version1.3.2
 */
public class CosmeticBagPostTitleApi extends RootApiForManage<CosmeticBagPostTitleResult, CosmeticBagPostTitleInput> {

	public CosmeticBagPostTitleResult Process(CosmeticBagPostTitleInput inputParam, MDataMap mRequestMap) {

		CosmeticBagPostTitleResult result = new CosmeticBagPostTitleResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			String sql = "SELECT title_content FROM nc_pots_title ";
			
			Map<String, Object> map = DbUp.upTable("nc_pots_title").dataSqlOne(sql, null);
			
			if(map!=null){
			
				result.setTitle_content(map.get("title_content").toString());
			}
			
		}
		return result;
	}

}

