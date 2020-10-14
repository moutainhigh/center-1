package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.ActivityFavInput;
import com.cmall.newscenter.model.ActivityFavResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动收藏API
 * @author yangrong
 * date 2014-8-5
 * @version 1.0
 */
public class ActivityFavApi  extends RootApiForToken<ActivityFavResult, ActivityFavInput>  {

	public ActivityFavResult Process(ActivityFavInput inputParam,
			MDataMap mRequestMap) {
		
		ActivityFavResult result = new ActivityFavResult();
		
		SecureRandom random = new SecureRandom();  
		
		if(result.upFlagTrue()){
		
			
		/*查询活动收藏信息*/
		MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getActivity(),"member_code",getUserCode(),"num_type","4497464900030005");
		
		/*查询活动统计表有多少人收藏过*/
		MDataMap ncMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());
		
		if(mDataMap!=null){
			
//			/*统计是否被收藏过*/
//			if(mDataMap.get("flag_enable").equals("")){
//				
//				result.setFaved(0);
//				
//			}else{
//				
//				result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
//			}
//			
//			//没有收藏过  改为1
//			if(mDataMap.get("flag_enable").equals("0")){
//				
//				//返回收藏人数
//				if(ncMap!=null){
//					
//					result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//				}else{
//					result.setFav_count(0);
//				}
//				
//				
//				mDataMap.put("flag_enable", "1");
//				
//				/*更新数据*/
//				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
//				
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
//			}else{
//				if(ncMap!=null){
//			
//					result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
//				}else{
//					result.setFav_count(0);
//				}
//				
//			}
			/*统计是否被收藏过，如果为1则变为0 ，如果为0则变为1*/
			mDataMap.put("flag_enable", mDataMap.get("flag_enable").equals("1")?"0":"1");
			
			/*更新数据*/
			DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
			
			
			if(mDataMap.get("flag_enable").equals("0")){
				/*如果值为0，则减1*/
				ncMap.put("num_favorite", String.valueOf(Integer.valueOf(ncMap.get("num_favorite"))-1));	
				
			}else{
				/*如果值为1，则增加1到5的随机整数*/
				ncMap.put("num_favorite", String.valueOf(Integer.valueOf(ncMap.get("num_favorite"))+random.nextInt(5)+1));
				
			}
			
			DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
			
			result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
			
			result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
			
		}else{
			result.setFaved(1);
			
			//返回收藏人数
			if(ncMap!=null){
				
				result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
			}else{
				result.setFav_count(0);
			}
			
			
		    MDataMap insertMap = new MDataMap();
		    
			/*将数据放入map中*/
			insertMap.inAllValues("num_code",inputParam.getActivity(),"member_code",getUserCode(),"create_time",FormatHelper.upDateTime()
					,"flag_enable","1","num_type","4497464900030005");
			/*将用户收藏记录数据插入表中*/
			DbUp.upTable("nc_num").dataInsert(insertMap);
			
			if(ncMap!=null){
				
				Integer a = Integer.valueOf(ncMap.get("num_favorite"));
				Integer b = a + random.nextInt(5)+1;
				
				ncMap.put("num_favorite", String.valueOf(b));
				
				DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
				
				result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));
			}
			
			
		}
		
		}
		
		return result;
	}

}
