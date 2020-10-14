package com.cmall.groupcenter.favorites.api;


import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.favorites.model.ApiCollectionsListInput;
import com.cmall.groupcenter.favorites.model.ApiCollectionsListResult;
import com.cmall.groupcenter.favorites.model.Collections;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 帖子收藏列表
 * @author yuwyn
 *
 */
public class ApiCollectionsList extends RootApiForToken<ApiCollectionsListResult, ApiCollectionsListInput> {

	public ApiCollectionsListResult Process(ApiCollectionsListInput inputParam,
			MDataMap mRequestMap) {
		ApiCollectionsListResult result = new ApiCollectionsListResult();
		
		MPageData mPageData = DataPaging.upPageData("nc_collections", "", "-collection_time","app_code =:app_code and flag =:flag and member_code =:member_code", new MDataMap("app_code", getManageCode(),"flag","4497472000020001","member_code",getUserCode()), inputParam.getPaging());
		
		if(mPageData.getListData().size() > 0){
			MDataMap postData = null;
			for(MDataMap mDataMap : mPageData.getListData()){
				Collections collections = new Collections();
				collections.setApp_code(mDataMap.get("app_code"));
				collections.setCollection_id(mDataMap.get("collection_id"));
				collections.setCollection_time(mDataMap.get("collection_time"));
				collections.setFlag(mDataMap.get("flag"));
				collections.setPost_id(mDataMap.get("post_id"));
				collections.setImg_url(mDataMap.get("img_url"));
				collections.setPost_title(mDataMap.get("post_title"));
				//获取封面图
				try{
					postData = this.getPostByPid(collections.getPost_id());
					if(null != postData){
						collections.setImg_url(postData.get("list_img_url"));
						collections.setPost_title(postData.get("p_title"));
					}
				}catch(Exception e){
					
				}
				collections.setMember_code(mDataMap.get("member_code"));
				collections.setPost_id(mDataMap.get("post_id"));
				
				collections.setTimeLable(judgeDate(collections.getCollection_time())+"");
				collections.setIsValid(checkValidByPostId(collections.getPost_id()));
				result.getApiCollections().add(collections);
			}
			result.setPaged(mPageData.getPageResults());
		}else{
			result.inErrorMessage(918519018);
		}
		
		return result;
	}
	
	private int checkValidByPostId(String pId){
		if(DbUp.upTable("nc_post").dataCount("pid =:pid and flag_enable =:flag_enable and start_time <=:now_date and end_time >=:now_date", new MDataMap("pid", pId,"flag_enable", "4497472000010001","now_date",DateHelper.upNow())) > 0){
			return 1;
		}
		return 0;
	}
	private MDataMap getPostByPid(String pId){
		return DbUp.upTable("nc_post").one("pid",pId);
	}
	/**
	 * 判断时间
	 * 返回0代表当天，1为昨天，2是当年的 long ago,3位long long ago
	 * @return 
	 */
	private int judgeDate(String dateStr){
		if(StringUtils.isEmpty(dateStr))
			return 3;
		int[] typeArray = new int[]{Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH};
		
		int h = 0;
		int c = 0;
		int differ = 0;
		Calendar cal = Calendar.getInstance();
		for(int i=0;i<typeArray.length;i++){
			cal.setTime(DateHelper.parseDate(dateStr));
			h = cal.get(typeArray[i]);
			cal.setTime(new Date());
			c = cal.get(typeArray[i]);
			differ = c -h;
			if(i == 0 && differ > 0){
				differ = 3;
				break;
			}
			if(i == 1 && differ > 0){
				differ = 2;
				break;
			}
			if(i == 2){
				if(differ > 2){
					differ = 2;
				}
				break;
			}
		}
		return differ;
	}
}