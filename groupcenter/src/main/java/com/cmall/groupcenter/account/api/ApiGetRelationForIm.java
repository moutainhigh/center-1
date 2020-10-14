package com.cmall.groupcenter.account.api;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.GetRelationForImInfo;
import com.cmall.groupcenter.account.model.GetRelationForImInput;
import com.cmall.groupcenter.account.model.GetRelationForImResult;
import com.cmall.groupcenter.service.GroupCommonService;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class ApiGetRelationForIm extends RootApiForManage<GetRelationForImResult, GetRelationForImInput>{

	public GetRelationForImResult Process(GetRelationForImInput inputParam,
			MDataMap mRequestMap) {
		
		GetRelationForImResult getRelationForImResult=new GetRelationForImResult();
		
		String hostMemberCode=inputParam.getHostMemberCode();
		String memberCodes = inputParam.getMemberCode();
		
		
		String[] memberCodesArray = memberCodes.split(",");
		
		StringBuffer condition = new StringBuffer();
		
		if(memberCodesArray.length==0){//只有一个编码
			condition.append(memberCodes);
			memberCodesArray = new String[]{memberCodes};
		}else{
			for (int i = 0; i < memberCodesArray.length; i++) {
				String string = memberCodesArray[i];
				if(StringUtils.isNotBlank(string.trim())){
					condition.append("'");
					condition.append(string);
					condition.append("'");
					
					if(i!=memberCodesArray.length-1){//最后一个不需要加，
						condition.append(",");
					}
				}
			}
		}
		
		
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("memberCodes", condition.toString());
		
		List<Map<String, Object>> list = DbUp.upTable("mc_extend_info_groupcenter").dataSqlList("SELECT extendInfo.`nickname`,extendInfo.`head_icon_url`,extendInfo.`member_code` FROM mc_extend_info_groupcenter extendInfo " +
				"WHERE member_code IN("+condition.toString()+") ", mWhereMap);
		
		

		/**
		 * 防止有的membercode数据在mc_extend_info_groupcenter表中不存在，
		 * 若不存在的话需要再去mc_login_info表中查询电话号码作为昵称,于是需要做如下处理
		 */
		
		//将数据放入map,以member_code作为key，方便hash查找出来后比对哪些membercode没有数据，
		//没有数据的需要继续查找,将查找的时间复杂度由O(n的平方) 转化成O(n)
		Map<String, Map<String, Object>> memberInfoMap = new HashMap<String, Map<String, Object>>();
		
		for (Map<String, Object> map : list) {
			memberInfoMap.put(StringHelper.getStringFromMap(map, "member_code"),map);
		}
		

		List<GetRelationForImInfo> infoList = getRelationForImResult.getInfoList();
		
		GetRelationForImInfo imInfo;
		//查看哪些memberCode在mc_extend_info_groupcenter中木有数据的,如果木有的话就要从mc_login_info表中查找了
		for (String string : memberCodesArray) {
			
			imInfo = new GetRelationForImInfo();
			
			//存在，则放入结果集中
			if(memberInfoMap.containsKey(string)){
				Map<String, Object> temp = memberInfoMap.get(string);
				
				
				imInfo.setHeadImage(StringHelper.getStringFromMap(temp, "head_icon_url"));
				imInfo.setMemberCode(string);
				imInfo.setNickName(StringHelper.getStringFromMap(temp, "nickname"));
				if(StringUtils.isEmpty(imInfo.getNickName())){
					MDataMap mWhereMap2 = new MDataMap();
					mWhereMap2.put("member_code", string);
					
					Object value = DbUp.upTable("mc_login_info").dataGet("login_name", "member_code=:member_code", mWhereMap2);
					String login_name ="";
					
					if(value!=null){
						login_name = String.valueOf(value);
					}
					if(StringUtils.isNotBlank(login_name)){
						imInfo.setNickName(login_name.substring(0, 3) + "****" + login_name.substring(7));
					}
					
				}
			}else {
				//在mc_extend_info_groupcenter表中不存在，此时需要去mc_login_info表中查找
				
				MDataMap mWhereMap2 = new MDataMap();
				mWhereMap2.put("member_code", string);
				
				Object value = DbUp.upTable("mc_login_info").dataGet("login_name", "member_code=:member_code", mWhereMap2);
				String login_name ="";
				
				if(value!=null){
					login_name = String.valueOf(value);
				}
				
				imInfo.setHeadImage("");
				imInfo.setMemberCode(string);
				if(StringUtils.isNotBlank(login_name)){
					imInfo.setNickName(login_name.substring(0, 3) + "****" + login_name.substring(7));
				}
				
			}
			
			infoList.add(imInfo);
		}
		
		if(StringUtils.isNotBlank(hostMemberCode)){
			GroupCommonService groupCommonService=new GroupCommonService();
			String hostAccountCode=groupCommonService.getAccountCodeByMemberCode(hostMemberCode);
			for(GetRelationForImInfo getRelationForImInfo:infoList){
				Map<String, String> map=new HashMap<String,String>();
				map.put("member_code", getRelationForImInfo.getMemberCode());
				map.put("account_code_wo",hostAccountCode);
				map.put("account_code_ta", groupCommonService.getAccountCodeByMemberCode(getRelationForImInfo.getMemberCode()));
				String nickName=NickNameHelper.getNickName(map);
				if(StringUtils.isNotEmpty(nickName)){
					getRelationForImInfo.setNickName(nickName);
				}
				//新增加好友关系 fengl 2015.11.13     好友关系 0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人

				String taAccountCode=groupCommonService.getAccountCodeByMemberCode(getRelationForImInfo.getMemberCode());
				GroupCommonService commonService=new GroupCommonService();
				String relationLevel=commonService.getRelationLevelByAccountCode(hostAccountCode,taAccountCode)+"";
				if(relationLevel.equals("5")){
					relationLevel="";
				}
				getRelationForImInfo.setRelativeLevel(relationLevel);
				//新增加好友关系 fengl 2015.12.10
				MDataMap mDataMap = new MDataMap();
				String remark="";
				mDataMap.put("account_code_wo",hostAccountCode);
				mDataMap.put("account_code_ta",taAccountCode);
				Object object = DbUp.upTable("gc_alter_nickname").dataGet("nick_name", "account_code_wo=:account_code_wo and account_code_ta=:account_code_ta", mDataMap);
				if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
					remark = String.valueOf(object);
				} 
				getRelationForImInfo.setRemark(remark);
				
			}
		}
		
		return getRelationForImResult;
	}

}
