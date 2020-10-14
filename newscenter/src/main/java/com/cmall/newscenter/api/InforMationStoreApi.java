package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.InforMationStoreInput;
import com.cmall.newscenter.model.InforMationStoreResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 资讯收藏API
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class InforMationStoreApi extends RootApiForToken<InforMationStoreResult, InforMationStoreInput> {

	public InforMationStoreResult Process(InforMationStoreInput inputParam,
			MDataMap mRequestMap) {
		
		InforMationStoreResult result = new InforMationStoreResult();
		
		if(result.upFlagTrue()){
			
		SecureRandom random = new SecureRandom();  
			
			/*查询用户资讯收藏信息*/
		MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",inputParam.getFeed(),"member_code",getUserCode(),"num_type","4497464900030005");
		
		/*查询资讯统计表有多少人收藏过*/
		MDataMap ncMap =  DbUp.upTable("nc_info").one("info_code",inputParam.getFeed());
		
		if(ncMap!=null){
			
		if(mDataMap!=null){
			
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
			/*修改收藏数量*/
			DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
			
			result.setFaved(Integer.valueOf(mDataMap.get("flag_enable")));
			
			result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));

			
		}else{
			
		    ncMap.put("num_favorite", String.valueOf(Integer.valueOf(ncMap.get("num_favorite"))+random.nextInt(5)+1));
		    
		    DbUp.upTable("nc_info").dataUpdate(ncMap, "num_favorite", "zid");
			
		    MDataMap insertMap = new MDataMap();
		    
			/*将数据放入map中*/
			insertMap.inAllValues("num_code",inputParam.getFeed(),"member_code",getUserCode(),"create_time",FormatHelper.upDateTime()
					,"flag_enable","1","num_type","4497464900030005");
			/*将用户收藏记录数据插入表中*/
			DbUp.upTable("nc_num").dataInsert(insertMap);
			
			result.setFaved(Integer.valueOf(insertMap.get("flag_enable")));
			
			result.setFav_count(Integer.valueOf(ncMap.get("num_favorite")));

			
		}
		
		}
		
		}
		
		return result;
	}

}
