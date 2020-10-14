package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.Area;
import com.cmall.newscenter.beauty.model.AreaInput;
import com.cmall.newscenter.beauty.model.AreaResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽-获取区API
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class AreaApi extends RootApiForManage<AreaResult, AreaInput> {

	public AreaResult Process(AreaInput inputParam,
			MDataMap mRequestMap) {
		
		AreaResult result = new AreaResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			//查询省市区信息
			MDataMap mWhereMap = new MDataMap();
			
			MPageData mPageData = DataPaging.upPageData("sc_gov_district", "", "", mWhereMap,new PageOption());
			
			if(mPageData!=null){
				
				for( MDataMap mDataMap: mPageData.getListData()){

					if(mDataMap.get("code").substring(0,4).equals(inputParam.getId().substring(0, 4))){
						
						if(!mDataMap.get("code").substring(4, 6).equals("00")&&!mDataMap.get("name").equals("市辖区")){
							
							Area area = new Area();
							
							area.setId(mDataMap.get("code"));
							area.setName(mDataMap.get("name"));
							
							result.getArea().add(area);
						}
					}
				}
			}
			
		}
		return result;
	}

}

