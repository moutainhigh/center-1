package com.cmall.newscenter.api;

import com.cmall.membercenter.model.ScoredChange;
import com.cmall.newscenter.model.ActivityUnFavInput;
import com.cmall.newscenter.model.ActivityUnFavResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动取消收藏API
 * @author yangrong
 * date 2014-8-5
 * @version 1.0
 */
public class ActivityUnFavApi  extends RootApiForToken<ActivityUnFavResult, ActivityUnFavInput>{

	public ActivityUnFavResult Process(ActivityUnFavInput inputParam,
			MDataMap mRequestMap) {

		ActivityUnFavResult result = new ActivityUnFavResult();
		
		if(result.upFlagTrue()){
			

			/*查询活动收藏信息*/
			MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getActivity(),"member_code",getUserCode(),"num_type","4497464900030005");
			
			/*查询活动统计表有多少人收藏过*/
			MDataMap ncMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());
			
			if(mDataMap!=null){
				
//				//统计是否被收藏过
//				if(mDataMap.get("flag_enable").equals("")){
//					
//					result.setFaved(0);
//					
//				}else{
//					
//					result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
//				}
//				
//				if(mDataMap.get("flag_enable").equals("1")){
//					
//					 mDataMap.put("flag_enable", "0");
//					
//					 /*更新数据*/
//					 DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
//					
//					if(ncMap!=null){
//						
//						ncMap.put("num_favorite", ncMap.get("num_favorite"));
//						
//						DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
//						
//						result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//					}
//				}else{
//					
//					if(ncMap!=null){
//						
//						result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//					}else{
//						result.setFav_count(0);
//					}
//				}
				/*统计是否被收藏过，如果为1则变为0 ，如果为0则变为1*/
				mDataMap.put("flag_enable", mDataMap.get("flag_enable").equals("1")?"0":"1");
				
				/*更新数据*/
				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
				
				result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
				
				result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//				if(ncMap!=null){
//					
//					Integer a = Integer.valueOf(ncMap.get("num_favorite"));
//					Integer b = a + 1;
//					
//					ncMap.put("num_favorite", String.valueOf(b));
//					
//					DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
//					
//					result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//				}
				
		 }else{
				
				//等于空  没收藏过
				result.setFaved(1);
				
				if(ncMap!=null){
					
					result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
				}else{
					result.setFav_count(0);
				}
			}
			
			
		}
		
		return result;
	}

}
