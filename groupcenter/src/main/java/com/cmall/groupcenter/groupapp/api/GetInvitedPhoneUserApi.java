package com.cmall.groupcenter.groupapp.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupapp.model.GetInvitedPhoneUserInput;
import com.cmall.groupcenter.groupapp.model.GetInvitedPhoneUserResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 邀请手机联系人接口
 * @author wangzx
 *
 */
public class GetInvitedPhoneUserApi extends RootApiForToken<GetInvitedPhoneUserResult,GetInvitedPhoneUserInput> {

	@Override
	public GetInvitedPhoneUserResult Process(
			GetInvitedPhoneUserInput inputParam, MDataMap mRequestMap) {
		Map<String, String> map = this.GetIninvitedList(this.getOauthInfo().getLoginName(), inputParam.getPhones());
		GetInvitedPhoneUserResult result = new GetInvitedPhoneUserResult();
		result.setInvitedList(map.get("already_recommend"));
		result.setUninvitedList(map.get("no_recommended"));
		return result;
	}
	
	/**
	 * 获取列表接口
	 * @param vipuser_mobile
	 * @param tels
	 * @return
	 */
	public  Map<String,String> GetIninvitedList(String vipuser_mobile,String tels){
		Map<String,String> map = new HashMap<String,String>();
		//vipuser_mobile="15712849368";
		String[] recommendTelArr = tels.split(",");
		List<String> already_recommend = new ArrayList<String>();
		List<String> no_recommended = new ArrayList<String>();

		//手机好号验证失败
		List<String> error_list = new ArrayList<String>();
		//手机号验证成功
		List<String> success_list = new ArrayList<String>();
		for (int i = 0; i < recommendTelArr.length; i++) {
			String recommendTel = recommendTelArr[i];
			
			//校验开始---------
			//11位手机号
			 String regEx1 = "^1[0-9]{10}$"; 
			 Pattern p1 = Pattern.compile(regEx1); 
			 
			Matcher m1 = p1.matcher(recommendTel); 
			boolean rs1 = m1.matches(); 
			 if(rs1 && !vipuser_mobile.equals(recommendTel)){
				 success_list.add(recommendTel);
			 } else {
				 error_list.add(recommendTel);
			 }
		}
		
		String sFieldValue=StringUtils.join(success_list, ",");
		if(StringUtils.isNotEmpty(sFieldValue)){
			MDataMap pmap = new MDataMap("mobile",vipuser_mobile,"app_code","SI2011");
			List<MDataMap> list = DbUp.upTable("gc_recommend_info").queryInSafe("DISTINCT recommended_mobile", "", "mobile=:mobile and app_code=:app_code", pmap, -1, -1, "recommended_mobile", sFieldValue);
			for(int i=0;i<success_list.size();i++){
				//未被邀请过
				if(!this.isExitsForList(list, success_list.get(i),"recommended_mobile")){
					no_recommended.add(success_list.get(i));
				}
				else {//已被邀请过
					already_recommend.add(success_list.get(i));
				}
			}
		}
		//未推荐过
		filterUserPhone(no_recommended);
		
		//已推荐过
		filterUserPhone(already_recommend);

		map.put("no_recommended", StringUtils.join(no_recommended,","));
		map.put("already_recommend", StringUtils.join(already_recommend,","));
		return map;
	}
	
	//判断手机号是否注册过（如果已注册过从集合中删除掉）
	public void filterUserPhone(List<String> phoneList){
		String no_recommended_phones=StringUtils.join(phoneList, ",");
		if(StringUtils.isNotEmpty(no_recommended_phones)){
			//MDataMap pmap = new MDataMap("manage_code","SI2011");
			//List<MDataMap> list = DbUp.upTable("mc_login_info").queryInSafe("login_name", "", "manage_code=:manage_code", pmap, -1, -1, "login_name", no_recommended_phones);
			List<MDataMap> list = DbUp.upTable("mc_login_info").queryInSafe("login_name", "", "", null, -1, -1, "login_name", no_recommended_phones);

			//存在的用户
			List<String> exitsUsers=new ArrayList<String>();
			for(int i=0;i<phoneList.size();i++){
				//已经存在了 说明已经注册过微公社
				if(this.isExitsForList(list, phoneList.get(i),"login_name")){
					exitsUsers.add(phoneList.get(i));
				}
			}
			phoneList.removeAll(exitsUsers);
		}
	}
	
	/**
	 * 集合中判断是否存在
	 * @param list
	 * @param recommended_mobile
	 * @param mapKey
	 * @return
	 */
	public boolean isExitsForList(List<MDataMap> list,String recommended_mobile,String mapKey){
		boolean flag=false;
		if(list!=null && list.size()>0){
			for(MDataMap map:list){
				if(map.get(mapKey).equals(recommended_mobile)){
					flag=true;
					break;
				}
			}
		}
		return flag;
	}

}
