package com.cmall.newscenter.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.PopularSearch;
import com.cmall.newscenter.model.TopRankingInput;
import com.cmall.newscenter.model.TopRankingResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 热门排行
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class TopRankingApi extends RootApiForManage<TopRankingResult, TopRankingInput> {

	public TopRankingResult Process(TopRankingInput inputParam,
			MDataMap mRequestMap) {
		TopRankingResult result = new TopRankingResult();
		if(result.upFlagTrue()){
			
			
			
			//String sSql = "select  COUNT(search_keyword) count,search_keyword from nc_record_search  GROUP BY search_keyword ORDER BY COUNT(search_keyword) DESC LIMIT 7";
			/*排序查询规定的关键词*/
			String sSql = "select * from nc_top_ranking order by top_num  desc limit 10";
			
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			
			List<Map<String,Object>> searchMaps = new ArrayList<Map<String,Object>>();
			
			mapList = DbUp.upTable("nc_top_ranking").dataSqlList(sSql, new MDataMap());
			
			/*当天日期*/
			String time = FormatHelper.upDateTime().substring(0, 10);
			
			
			
			if(!mapList.isEmpty()){
				
				for(int i =0;i<mapList.size();i++){
					
					
					PopularSearch search = new PopularSearch();
					
					String sql = "select  COUNT(search_keyword) count,search_keyword from nc_record_search where LEFT(search_catetime,10)='"+time+"'   GROUP BY search_keyword ORDER BY COUNT(search_keyword) DESC";
					
					searchMaps =  DbUp.upTable("nc_record_search").dataSqlList(sql, new MDataMap());
					
					
						
					if(!searchMaps.isEmpty()){
						
						for(int j=0;j<searchMaps.size();j++){
							
							if(String.valueOf(searchMaps.get(j).get("search_keyword")).equals(String.valueOf(mapList.get(i).get("top_keyword")))){
								
								int sequence = (j+1)-(i+1);
								
								if(sequence!=0){
									
									search.setOrder(i+1);
									
									search.setTitle(String.valueOf(mapList.get(i).get("top_keyword")));
									
									search.setChange(sequence);
									
									result.getRanks().add(search);
									
								}else {
									
								search.setOrder(i+1);
								
								search.setTitle(String.valueOf(mapList.get(i).get("top_keyword")));
								
								result.getRanks().add(search);
								
							}
							
						}else{
							
							/*判断是否重复*/
							
							
								search.setOrder(i+1);
								
								search.setTitle(String.valueOf(mapList.get(i).get("top_keyword")));
								
								result.getRanks().add(search);
								
							continue;
							
						}
						
					}
					}
					
					
				}
				
			}
			
		}
		/*去掉重复项*/
		if(result.getRanks().size()!=0){
			
			for(int m=0;m<result.getRanks().size();m++){
				
				
				for(int j=m+1;j<result.getRanks().size();j++){
					
					if(result.getRanks().get(m).getTitle().equals(result.getRanks().get(j).getTitle())){
					
						
						result.getRanks().remove(j);
						
						j--;
						
					}
					
				}
				
			}
			
		}
		
		return result;
	}

}
