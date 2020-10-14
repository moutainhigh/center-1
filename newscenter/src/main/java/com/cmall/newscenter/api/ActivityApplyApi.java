package com.cmall.newscenter.api;


import com.cmall.newscenter.model.ActivityApplyInput;
import com.cmall.newscenter.model.ActivityApplyResult;
import com.cmall.newscenter.model.ApplyUser;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 活动 -报名列表
 * @author yangrong
 * date 2014-8-21
 * @version 1.0
 */
public class ActivityApplyApi extends RootApiForToken<ActivityApplyResult, ActivityApplyInput> {

	public ActivityApplyResult Process(ActivityApplyInput inputParam,
			MDataMap mRequestMap) {
		
		ActivityApplyResult result = new ActivityApplyResult();
		
		//设置相关信息
		if(result.upFlagTrue()){
			
			MDataMap userMDataMap =  DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode(),"member_group","4497465000020002");
			
			if(userMDataMap!=null){
			
			MDataMap mWhereMap = new MDataMap();
			
			mWhereMap.put("info_code", inputParam.getActivity());
			
			
            MDataMap whereInfoMember = new MDataMap();
			
			whereInfoMember.put("info_category", "44974650000100060001");
			
			MPageData userActTableList= new MPageData();
			
			whereInfoMember.put("manage_code", getManageCode());
			
			whereInfoMember.put("info_code", inputParam.getActivity());
			
			whereInfoMember.put("create_member", getUserCode());
			
			userActTableList = DataPaging.upPageData("nc_info", "", "-create_time", whereInfoMember, inputParam.getPaging());
			
			if(userActTableList.getListData().size()!=0){
				
			MPageData mPageData = DataPaging.upPageData("nc_registration", "", "", mWhereMap,new PageOption());

			for( MDataMap mDataMap: mPageData.getListData()){
				
				//查出用户信息                           
				MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("info_member"));
				
				ApplyUser applyUser = new ApplyUser();
				
				applyUser.setNickname(mUserMap.get("nickname"));	

				applyUser.setMobile(mUserMap.get("mobile_phone"));
				
				applyUser.setApply_time(mDataMap.get("registration_time"));
				
				result.getUsers().add(applyUser);
			}
			
            result.setUnread_count(DbUp.upTable("nc_registration").count("info_code", inputParam.getActivity(),"manage_code",getManageCode(),"is_enable","1"));
			
			result.setPaged(mPageData.getPageResults());
			
			}else {
				
                MDataMap WhereMap = new MDataMap();
				
                WhereMap.put("info_code", inputParam.getActivity());
				
                WhereMap.put("info_member", getUserCode());
				
				MPageData mPageData = DataPaging.upPageData("nc_registration", "", "", WhereMap,new PageOption());

				for( MDataMap mDataMap: mPageData.getListData()){
					
					//查出用户信息                           
					MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("info_member"));
					
					ApplyUser applyUser = new ApplyUser();
					
					applyUser.setNickname(mUserMap.get("nickname"));	

					applyUser.setMobile(mUserMap.get("mobile_phone"));
					
					applyUser.setApply_time(mDataMap.get("registration_time"));
					
					result.getUsers().add(applyUser);
				}
				result.setUnread_count(DbUp.upTable("nc_registration").count("info_code", inputParam.getActivity(),"manage_code",getManageCode(),"is_enable","1"));
				
				result.setPaged(mPageData.getPageResults());
				
			}
			
			
			
			}else {
				

				
				MDataMap mWhereMap = new MDataMap();
				
				mWhereMap.put("info_code", inputParam.getActivity());
				
				mWhereMap.put("info_member", getUserCode());
				
				MPageData mPageData = DataPaging.upPageData("nc_registration", "", "", mWhereMap,new PageOption());

				for( MDataMap mDataMap: mPageData.getListData()){
					
					//查出用户信息                           
					MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("info_member"));
					
					ApplyUser applyUser = new ApplyUser();
					
					applyUser.setNickname(mUserMap.get("nickname"));	

					applyUser.setMobile(mUserMap.get("mobile_phone"));
					
					applyUser.setApply_time(mDataMap.get("registration_time"));
					
					result.getUsers().add(applyUser);
				}
				result.setUnread_count(DbUp.upTable("nc_registration").count("info_code", inputParam.getActivity(),"manage_code",getManageCode(),"is_enable","1"));
				
				result.setPaged(mPageData.getPageResults());
				
				
				
			}
		}
		return result;
	}

}
