package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.ActivityLikeInput;
import com.cmall.newscenter.model.ActivityLikeResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动-喜欢Api
 * @author yangrong
 * date: 2014-08-05
 * @version1.0
 */
public class ActivityLikeApi extends RootApiForToken<ActivityLikeResult, ActivityLikeInput>{

	public ActivityLikeResult Process(ActivityLikeInput inputParam,
			MDataMap mRequestMap) {
		
		ActivityLikeResult result = new ActivityLikeResult();
		
		SecureRandom random = new SecureRandom();  
		 
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			
			/*根据活动ID查询活动信息*/
			MDataMap mDataMap = new MDataMap();
			
			mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getActivity(),"member_code",getUserCode(),"num_type","4497464900030003");
			
			MDataMap mInfoMap= DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());
			
			/*判断是否存在活动信息*/
			if(mDataMap!=null)
			{
				
//				 /*返回活动是否已喜欢*/
//				if(mDataMap.get("flag_enable").equals("")){
//					
//					 result.setLiked(0);
//					 
//				}else{
//					
//					 result.setLiked(Integer.valueOf(mDataMap.get("flag_enable")));
//				}
//				  
//				  //判断没喜欢过
//				  if(mDataMap.get("flag_enable").equals("0")){
//					  
//					  mDataMap.put("flag_enable","1");
//					  
//					  DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
//					  
//					  if(mInfoMap!=null){
//						  
//						  Integer a = Integer.valueOf(mInfoMap.get("num_like"));
//						  Integer b = a + 1;
//							
//						  mInfoMap.put("num_like", String.valueOf(b));
//						  
//						  DbUp.upTable("nc_info").dataUpdate(mInfoMap, "num_like", "zid");
//					  }
//					  
//				  }
				/*统计是否被喜欢过，如果为1则变为0 ，如果为0则变为1*/
				mDataMap.put("flag_enable", mDataMap.get("flag_enable").equals("1")?"0":"1");
				
				/*更新数据*/
				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
				
				
				result.setLiked(Integer.valueOf(mDataMap.get("flag_enable")));
				
				//多少人喜欢过
				result.setLike_count(Integer.valueOf(mInfoMap.get("num_like")));
			}
			else
			{
				
				/*返回活动是否已喜欢*/
				result.setLiked(1);
				
				MDataMap mInsert=new MDataMap();
				
				/*如果没有喜欢信息将喜欢信息插入表中*/
				mInsert.inAllValues("num_code",inputParam.getActivity(),"member_code",getUserCode(),"create_time",FormatHelper.upDateTime()
						,"flag_enable","1","num_type","4497464900030003");
				
				DbUp.upTable("nc_num").dataInsert(mInsert);


				 if(mInfoMap!=null){
					  
					  Integer a = Integer.valueOf(mInfoMap.get("num_like"));
					  Integer b = a + random.nextInt(5)+1;
						
					  mInfoMap.put("num_like", String.valueOf(b));
					  
					  DbUp.upTable("nc_info").dataUpdate(mInfoMap, "num_like", "zid");
				  }
			
				//多少人喜欢过
					result.setLike_count(Integer.valueOf(mInfoMap.get("num_like")));
				
			}
			
		}
		
		return result;
	}

}
