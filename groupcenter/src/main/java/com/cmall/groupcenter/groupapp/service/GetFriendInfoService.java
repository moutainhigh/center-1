package com.cmall.groupcenter.groupapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoResult;
import com.cmall.groupcenter.groupapp.model.Person;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.service.GroupCommonService;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

public class GetFriendInfoService extends BaseClass{
	/**
	 * @author fengl
	 * @param memberCode 传入的好友参数 member_code 
	 * @param appCode 通过token获取的app_code
	 * @param woMemberCode 通过token获取本人的member_code
	 * @return  
	 */
     public GetFriendInformationInfoResult GetFriendInfo(String memberCode,String appCode,String woMemberCode){
    	 GetFriendInformationInfoResult result=new GetFriendInformationInfoResult();
    	 
     	//根据用户编号查账户编号（oneWhere取一条记录）好友的账户编号
    	 MDataMap mDataMap= DbUp.upTable("mc_member_info")
  				.oneWhere("account_code", "", "member_code='"+memberCode+"'");
    	 if(mDataMap==null){
    		 result.inErrorMessage(918570002);
    		 return result;
    	 }
   		
    	 String sAccountCode =mDataMap.get("account_code");

		Person person=new Person();

		//查询个人头像
		String headIconSql = "select head_icon_url,nickname,birthday,gender,region from mc_extend_info_groupcenter where app_code='"+ appCode +"' and member_code= '"+memberCode +"'";
		 Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
	     if(headIconMap!=null){
	    	// && headIconMap.get("head_icon_url")!=null
	    	person.setHeaderUrl(headIconMap.get("head_icon_url")==null ? "" : String.valueOf(headIconMap.get("head_icon_url")));
	    	person.setNickName(StringUtils.isBlank(String.valueOf(headIconMap.get("nickname"))) ? "" :  String.valueOf(headIconMap.get("nickname")) );
	    	person.setBrithday(StringUtils.isBlank(String.valueOf(headIconMap.get("birthday"))) ? "" :  String.valueOf(headIconMap.get("birthday")) );
	    	String gender=String.valueOf(headIconMap.get("gender"));

	    	person.setGender(StringUtils.isBlank(String.valueOf(headIconMap.get("gender"))) ? "" : gender );
	    	person.setRegion(StringUtils.isBlank(String.valueOf(headIconMap.get("region"))) ? "" :  String.valueOf(headIconMap.get("region")) );

	     }else {
	    	 person.setNickName(this.getDefaultNickName(memberCode, appCode));
	     }

		 //级别
		  MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
					"account_level",
					"", "", "account_code", sAccountCode);
		  
		  
		  if(mGroupAccountMap!=null){
			  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
						"level_code", mGroupAccountMap.get("account_level"));  
			  
			  person.setLevel(mLevelMap.get("level_name"));
			  
		  }else{
			  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
						"level_code", GroupConst.DEFAULT_LEVEL_CODE);  
			  
			  person.setLevel(mLevelMap.get("level_name"));
		  }
			  
		 // 电话 加入时间

		  List<Map<String, Object>> manageCodeList = DbUp.upTable("mc_login_info").dataSqlList("select a.login_name, a.create_time,a.member_code,a.manage_code from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = '"+sAccountCode+"' " , null);	  
		  if(manageCodeList != null && manageCodeList.size()>0){
			  Map map=manageCodeList.get(0);
			  Object loginName=map.get("login_name");
			  Object createTime=map.get("create_time");
			  if(loginName!=null&&!loginName.equals("")){
				  person.setTelephone(loginName.toString());  
			  }else{
				  person.setTelephone("");
			  }
			  
			  if(createTime!=null&&!createTime.equals("")){
				  person.setJoinTime(createTime.toString());  
				  
			  }else{
				  person.setJoinTime(""); 
			  }
		 }
		//备注
		Map<String, String> map = new HashMap<String, String>();
     	//根据用户编号查账户编号（oneWhere取一条记录） 本人的账户编号
   		 String accountCode = DbUp.upTable("mc_member_info")
  				.oneWhere("account_code", "", "member_code='"+woMemberCode+"'")
  				.get("account_code");
		String reMarkName="";
		MDataMap mDataMapN = new MDataMap();
		mDataMapN.put("account_code_wo", accountCode);
		mDataMapN.put("account_code_ta", sAccountCode);
		Object object = DbUp.upTable("gc_alter_nickname").dataGet("nick_name", "account_code_wo=:account_code_wo and account_code_ta=:account_code_ta", mDataMapN);
		if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
			reMarkName = String.valueOf(object);
		}
		person.setRemarkName(reMarkName);
		
		person.setQrCodeUrl(String.format(this.bConfig("groupcenter.qrCode_view_url"), memberCode));
		
		GroupCommonService commonService=new GroupCommonService();
		String relationLevel=commonService.getRelationLevelByAccountCode(accountCode,sAccountCode)+"";
		if(relationLevel.equals("5")){
			relationLevel="";
		}
		person.setRelativeLevel(relationLevel);
		person.setMemberCode(memberCode);
        result.setFriendInfo(person);
        result.setResultCode(1);
        result.setResultMessage("好友信息获取成功");
 		 
    	return  result;
     }
     
     private String getDefaultNickName(String memberCode,String manageCode){
 		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode, "manage_code", manageCode);
 		if(mUserMap != null){
 			String loginName = mUserMap.get("login_name");
 			return loginName.substring(0, 3) + "****" + loginName.substring(7);
 		}
 		return "";
 	}
}
