package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.ActivityShareInput;
import com.cmall.newscenter.model.ActivityShareResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动分享
 * @author yangrong
 * date 2014-8-5
 */
public class ActivityShareApi extends RootApiForToken<ActivityShareResult, ActivityShareInput> {

	public ActivityShareResult Process(ActivityShareInput inputParam,
			MDataMap mRequestMap) {
		ActivityShareResult result = new ActivityShareResult();
		
		if(result.upFlagTrue()){
			
			
		  /*查询活动分享信息*/	
		  MDataMap insertDataMap =	DbUp.upTable("nc_num").one("num_code",inputParam.getActivity(),"member_code",getUserCode(),"num_type","4497464900030004");
			
		  MDataMap ncDataMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getActivity());
		  
		  SecureRandom random = new SecureRandom();  
		  
		  if(insertDataMap!=null){
			  
			  /*返回活动是否已分享*/
			 
		      result.setShared(Integer.valueOf(insertDataMap.get("flag_enable")));
			  
			  //判断没分享过
			  if(insertDataMap.get("flag_enable").equals("0")){
				  
				  insertDataMap.put("flag_enable","1");
				  
				  DbUp.upTable("nc_num").dataUpdate(insertDataMap, "flag_enable", "zid");
				  
				  if(ncDataMap!=null){
					  
					  Integer a = Integer.valueOf(ncDataMap.get("num_share"));
					  Integer b = a + random.nextInt(5)+1;
						
					  ncDataMap.put("num_share", String.valueOf(b));
					  
					  DbUp.upTable("nc_info").dataUpdate(ncDataMap, "num_share", "zid");
				  }
				  
				  //返回分享人数
				  result.setShare_count(Integer.valueOf(ncDataMap.get("num_share")));
				  
				  
			  }else{
				
			      result.setShare_count(Integer.valueOf(ncDataMap.get("num_share")));
				  
			  }
			 
			  /*返回活动是否已分享*/
			  result.setShared(Integer.valueOf(insertDataMap.get("flag_enable")));
			  
			  
		  }else{
			  
			  
			  /*如果没有分享信息将分享信息插入表中*/
			  MDataMap mDataMap = new MDataMap();
			  
			  mDataMap.inAllValues("num_code",inputParam.getActivity(),"member_code",getUserCode(),"flag_enable","1","num_type","4497464900030004"
					  ,"create_time",FormatHelper.upDateTime());
			  
			  DbUp.upTable("nc_num").dataInsert(mDataMap);
			  
			  if(ncDataMap!=null){
				  
				  Integer a = Integer.valueOf(ncDataMap.get("num_share"));
				  Integer b = a + random.nextInt(5)+1;
					
				  ncDataMap.put("num_share", String.valueOf(b));
				  
				  DbUp.upTable("nc_info").dataUpdate(ncDataMap, "num_share", "zid");
			  }
			  
			  //返回分享人数
			  result.setShare_count(Integer.valueOf(ncDataMap.get("num_share")));
			  
			  /*返回活动是否已分享*/
			  result.setShared(Integer.valueOf(mDataMap.get("flag_enable")));
			  
		  }
		  

		  }
	
		return result;
	}

}
