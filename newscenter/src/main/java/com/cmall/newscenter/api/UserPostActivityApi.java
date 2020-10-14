package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.UserPostActivityInput;
import com.cmall.newscenter.model.UserPostActivityResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 粉丝头 - 发布活动
 * @author shiyz
 * date 2014-7-31
 * @version 1.0
 */
public class UserPostActivityApi extends RootApiForToken<UserPostActivityResult, UserPostActivityInput> {

	public UserPostActivityResult Process(UserPostActivityInput inputParam,
			MDataMap mRequestMap) {
		UserPostActivityResult result = new UserPostActivityResult();
		
		MDataMap userMap = new MDataMap(); 
		userMap = DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode());
		
		if(userMap!=null){
			
			if(userMap.get("member_group").equals("4497465000020002")){
		
		
		MDataMap mDataMap = new MDataMap();
		
		/*内容编号*/
		String info_code = WebHelper.upCode("JL");
		
		/*标题*/
		mDataMap.put("info_title", inputParam.getActivity().getTitle());
		
		/*内容*/
		mDataMap.put("info_content", inputParam.getActivity().getText());
		
		/*内容编号*/
		mDataMap.put("info_code", info_code);
		
		/*前台创建人*/
		mDataMap.put("create_member", getUserCode());
		
		/*创建时间*/
		mDataMap.put("create_time", FormatHelper.upDateTime());
		
		/**图片*/
		List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>(); 
		
		photos = inputParam.getActivity().getPhoto();
		
		CommentdityAppPhotos photo = new CommentdityAppPhotos();
		
		String large = "";
		
		if(photos.size()!=0){
			for(int i=0;i<photos.size();i++){
				
				photo = photos.get(i);
				
				 String largephoto = photo.getLarge();
				   
				 large += largephoto+"|";
				
			}
			/*图片*/
		mDataMap.put("photos", large.substring(0,large.length()-1));
			
		}
		
		/*等级限制*/
		mDataMap.put("min_level", inputParam.getActivity().getLevel());
		
		/*地点*/
		mDataMap.put("address", inputParam.getActivity().getLocation().getName());
		
		
		/*所属分类*/
		
		mDataMap.put("info_category", "44974650000100060001");
		
		mDataMap.put("manage_code", getManageCode());
		
		
		if(result.upFlagTrue()){
			
			DbUp.upTable("nc_info").dataInsert(mDataMap);
			
		}
		
			}else{
				
				result.setResultCode(969905923);
				
				result.setResultMessage(bInfo(969905923));
				
			}
			}
		
		return result;
	}

}
