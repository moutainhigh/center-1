package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.City;
import com.cmall.newscenter.beauty.model.CityInput;
import com.cmall.newscenter.beauty.model.CityResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽-获取城市API
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class CityApi extends RootApiForManage<CityResult, CityInput> {

	public CityResult Process(CityInput inputParam,
			MDataMap mRequestMap) {
		
		CityResult result = new CityResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			//查询省市区信息
			MDataMap mWhereMap = new MDataMap();
			
			MPageData mPageData = DataPaging.upPageData("sc_gov_district", "", "", mWhereMap,new PageOption());
			
			if(mPageData!=null){
				
				for( MDataMap mDataMap: mPageData.getListData()){

					if(mDataMap.get("code").substring(0,2).equals(inputParam.getId().substring(0, 2))){
						
						if(!mDataMap.get("code").substring(2, 6).equals("0000")&&mDataMap.get("code").substring(4, 6).equals("00")){
							
							City city = new City();
							
							city.setId(mDataMap.get("code"));
							city.setName(mDataMap.get("name"));
							
							result.getCity().add(city);
						}
					}
				}
			}
			
		}
		return result;
	}

}

