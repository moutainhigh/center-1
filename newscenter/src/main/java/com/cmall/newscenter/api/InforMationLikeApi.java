package com.cmall.newscenter.api;


import java.security.SecureRandom;

import com.cmall.newscenter.model.InforMationLikeInput;
import com.cmall.newscenter.model.InforMationLikeResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 刘嘉玲app资讯-喜欢Api
 * @author shiyz
 * date: 2014-07-04
 * @version1.0
 */
public class InforMationLikeApi extends RootApiForToken<InforMationLikeResult, InforMationLikeInput> {

	public InforMationLikeResult Process(InforMationLikeInput inputParam,
			MDataMap mRequestMap) {
		
		InforMationLikeResult result = new InforMationLikeResult();
		 
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			/*根据资讯ID查询资讯信息*/
			MDataMap mDataMap = new MDataMap();
			
			mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getFeed(),"member_code",getUserCode(),"num_type","4497464900030003");
			
			/*如果不存在插入资讯信息并且将喜欢的数量在nc_info表中加一*/
			MDataMap mInfoMap= DbUp.upTable("nc_info").one("info_code",inputParam.getFeed());
			
			
			if(mInfoMap!=null){
			
			/*判断是否存在资讯信息*/
			if(mDataMap!=null)
			{
				/*如果存在修改数据库中喜欢的状态*/
				mDataMap.put("flag_enable", mDataMap.get("flag_enable").equals("1")?"0":"1");
				
				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable", "zid");
				/*返回状态*/
				result.setLiked(Integer.valueOf(mDataMap.get("flag_enable")));
			}
			else
			{
				SecureRandom random = new SecureRandom();  
				
				mInfoMap.put("num_like",  String.valueOf(Integer.valueOf(mInfoMap.get("num_like"))+random.nextInt(5)+1));
				
				DbUp.upTable("nc_info").dataUpdate(mInfoMap, "num_like", "zid");
				
				MDataMap mInsert=new MDataMap();
				
				mInsert.inAllValues("num_code",inputParam.getFeed(),"member_code",getUserCode(),"create_time",FormatHelper.upDateTime()
						,"flag_enable","1","num_type","4497464900030003");
				
				DbUp.upTable("nc_num").dataInsert(mInsert);
				/*返回状态*/
				result.setLiked(Integer.valueOf(mInsert.get("flag_enable")));
				
			}
			
			
		}
		
		
	}
		return result;
	}
}
