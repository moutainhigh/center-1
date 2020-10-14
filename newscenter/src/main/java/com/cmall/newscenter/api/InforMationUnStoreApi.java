package com.cmall.newscenter.api;

import com.cmall.newscenter.model.InforMationUnStoreInput;
import com.cmall.newscenter.model.InforMationUnStoreResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 资讯取消收藏API
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationUnStoreApi extends RootApiForToken<InforMationUnStoreResult, InforMationUnStoreInput> {

	public InforMationUnStoreResult Process(InforMationUnStoreInput inputParam,
			MDataMap mRequestMap) {
		
		InforMationUnStoreResult result = new InforMationUnStoreResult();
		
		RootResultWeb rootResultWeb = new RootResultWeb();
		
		if(result.upFlagTrue()){
			
			
			
			/*查询用户资讯收藏信息*/
			MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getFeed(),"member_code",getUserCode());
			
			/*查询资讯统计表有多少人收藏过*/
			MDataMap ncMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getFeed());
			
			if(ncMap!=null){
				
			if(mDataMap!=null){
				
				/*统计是否被收藏过，如果为1则变为0 ，如果为0则变为1*/
				mDataMap.put("flag_enable", "0");
				
				/*更新数据*/
				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
				
			}else{
				
				rootResultWeb.inErrorMessage(934205101);
				
				
			}
			result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
			
			result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
			
			}
		}
		
		return result;
	}

}
