package com.cmall.newscenter.group.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.group.model.ApiForAdvertiseMoreInput;
import com.cmall.newscenter.group.model.ApiForAdvertiseMoreResult;
import com.cmall.newscenter.group.model.ApiForAdvertiseMoreResult.Advertise;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/***
 * 微公社的广告
 * 支持多个图片
 * @author dyc
 *
 */
public class ApiForAdvertiseMore extends RootApiForManage<ApiForAdvertiseMoreResult, ApiForAdvertiseMoreInput> {

	public ApiForAdvertiseMoreResult Process(ApiForAdvertiseMoreInput inputParam, MDataMap mRequestMap) {
		ApiForAdvertiseMoreResult re = new ApiForAdvertiseMoreResult();
		
		String now = DateUtil.getSysDateTimeString();
		
		String sql="SELECT ad_name,ad_code,genre_code,place_code,page_code,column_code,start_time,end_time,adImg,adImg_url,ad_title,ad_prompt from nc_advertise WHERE place_code=:place_code AND start_time<=:now and end_time >=:now  ORDER BY create_time DESC ";
		// 暂时不加状态条件   and status=:status
		
		List<Map<String, Object>> list=DbUp.upTable("nc_advertise").dataSqlList(sql, new MDataMap("now",now,"status","449746690001","place_code",inputParam.getPosition()));
		 if(list!=null&&list.size()>0){
			 
			 Map<String,List<Advertise>> rmap=re.getAdvertiseMap();
			 
			 for (Map<String, Object> map : list) {
				 String ad_name = (String)map.get("ad_name");
				 String place_code = (String)map.get("place_code");
				 String adImg = (String)map.get("adImg");
				 String adImg_url = (String)map.get("adImg_url");
				 
				 Advertise advertise = new Advertise();
				 advertise.setAd_name(ad_name);
				 advertise.setAdImg(adImg);
				 advertise.setAdImg_url(adImg_url);
				 advertise.setPlace_code(place_code);
				 
				 List<Advertise> alist=rmap.get(place_code);
				 if(alist==null){
					 alist=new ArrayList<Advertise>();
					 rmap.put(place_code, alist);
				 }
				 
				 alist.add(advertise);
			}
		 }
		return re;
	}

}
