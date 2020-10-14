package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.InforMationShareInput;
import com.cmall.newscenter.model.InforMationShareResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 资讯分享API
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationShareApi extends RootApiForToken<InforMationShareResult, InforMationShareInput> {

	public InforMationShareResult Process(InforMationShareInput inputParam,
			MDataMap mRequestMap) {
		InforMationShareResult result = new InforMationShareResult();
		
		if(result.upFlagTrue()){
			
		  SecureRandom random = new SecureRandom();  
			
		  /*查询用户资讯分享信息*/	
		  MDataMap insertDataMap =	DbUp.upTable("nc_num").one("num_code",inputParam.getFeed(),"member_code",getUserCode(),"num_type","4497464900030004");
			
		  MDataMap ncDataMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getFeed());
		  
		  if(ncDataMap!=null){
			  
		  
		  if(insertDataMap!=null){
			  
			  insertDataMap.put("flag_enable", insertDataMap.get("flag_enable").equals("1")?"1":"1");
			  
			  DbUp.upTable("nc_num").dataUpdate(insertDataMap, "flag_enable", "zid");
			  
			  if(insertDataMap.get("flag_enable").equals("0")){
				 
				  
			  }else {
				
				  /*如果值为1，则增加1到5的随机整数*/
				  ncDataMap.put("num_share", String.valueOf(Integer.valueOf(ncDataMap.get("num_share"))+random.nextInt(5)+1));
			      
			  }
			  
			  DbUp.upTable("nc_info").dataUpdate(ncDataMap, "num_share", "zid");
			  
			  /*返回资讯是否已分享*/
			  result.setShared(Integer.valueOf(insertDataMap.get("flag_enable")));
			  /*返回资讯分享总数*/
			  result.setShare_count(Integer.valueOf(ncDataMap.get("num_share")));
			  
			  
		  }else{
			  
			  
			  ncDataMap.put("num_share", String.valueOf(Integer.valueOf(ncDataMap.get("num_share"))+random.nextInt(5)+1));
			  
			  DbUp.upTable("nc_info").dataUpdate(ncDataMap, "num_share", "zid");
			  
			  /*如果没有分享信息将分享信息插入表中*/
			  MDataMap mDataMap = new MDataMap();
			  
			  
			  mDataMap.inAllValues("num_code",inputParam.getFeed(),"member_code",getUserCode(),"flag_enable","1","num_type","4497464900030004"
					  ,"create_time",FormatHelper.upDateTime());
			  
			  DbUp.upTable("nc_num").dataInsert(mDataMap);
			  
			  /*返回资讯是否已分享*/
			  result.setShared(Integer.valueOf(mDataMap.get("flag_enable")));/*返回资讯分享总数*/
			  
			  result.setShare_count(Integer.valueOf(ncDataMap.get("num_share")));
			  
			  
		  }
		  
		  
		}
		}
		return result;
	}

}
