package com.cmall.groupcenter.groupapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.groupapp.model.GetFriendsListInput;
import com.cmall.groupcenter.groupapp.model.GetFriendsListResult;
import com.cmall.groupcenter.groupapp.model.Person;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * @author GaoYang
 * @CreateDate 2015年11月10日下午4:11:46
 *
 */
public class GetFriendsListService  extends BaseClass{

	public GetFriendsListResult ShowAccountFriendsList(String accountCode,
			GetFriendsListInput inputParam) {
		
		GetFriendsListResult fListResult = new GetFriendsListResult();
		LinkedHashMap<String,String> fMap = new LinkedHashMap<String,String>();
		
		PageResults pageResults = new PageResults();
		PageOption pageOption=inputParam.getPageOption();
		int start=pageOption.getLimit() * pageOption.getOffset();
	    int pageLimit=pageOption.getLimit();
	    String limitString="";
	    if (start> -1 && pageLimit > 0) {
			limitString=" limit " + String.valueOf(start) + ","
					+ String.valueOf(pageLimit);
		}
	    
		//一度好友数量
		int oneFridensNumber = 0;
	    List<Map<String, Object>> oneTotalRelationList = new ArrayList<Map<String, Object>>();
		MDataMap in1Map=new MDataMap("parent_code",accountCode,"flag_enable", "1");
		String totalRelSql="select account_code  from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable ";
		oneTotalRelationList=DbUp.upTable("gc_member_relation").dataSqlList(totalRelSql, in1Map);
		if(oneTotalRelationList != null && oneTotalRelationList.size()>0){
			oneFridensNumber = oneTotalRelationList.size();
		}
		fListResult.setOneLevelFriendsNumber(String.valueOf(oneFridensNumber));
	    
		//推荐人
		String myRefereeCode = "";
		//获取推荐人(只有第一页时 才返回推荐人，分页后不包含推荐人)
		if(pageOption.getOffset() < 1){
			MDataMap myLelationMap=DbUp.upTable("gc_member_relation").one("account_code",accountCode);
			if(myLelationMap != null){
				myRefereeCode = myLelationMap.get("parent_code");//推荐人的account_code
				fMap.put(myRefereeCode, "-1");
			}
		}
		
		//一度好友信息
		List<Map<String, Object>> oneFridendsList = new ArrayList<Map<String, Object>>();
		String relSql="select account_code from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable order by create_time desc "+limitString;
		oneFridendsList=DbUp.upTable("gc_member_relation").dataSqlList(relSql, in1Map);
		for(int i = 0;i<oneFridendsList.size();i++){
			String aCode = String.valueOf(oneFridendsList.get(i).get("account_code"));
			fMap.put(aCode, aCode);
		}
		
		//总条数,返回的条数
		if(StringUtils.isNotBlank(myRefereeCode)){
			pageResults.setTotal(oneTotalRelationList == null?0:oneTotalRelationList.size() + 1);
			pageResults.setCount(oneFridendsList==null?0:oneFridendsList.size()+1);
		}else{
			pageResults.setTotal(oneTotalRelationList == null?0:oneTotalRelationList.size());
			pageResults.setCount(oneFridendsList==null?0:oneFridendsList.size());
		}
		
		//判断是否还有更多数据
		pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
						.getCount()) < pageResults.getTotal() ? 1 : 0);
		fListResult.setPageResults(pageResults);
		
		//推荐人和一度好友信息
		List<Person> pInfoList=new ArrayList<Person>();
		Iterator<Entry<String, String>> reIt = fMap.entrySet().iterator();
		while(reIt.hasNext()){
			
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) reIt.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			
			Person p = new Person();
			//account_code
			String acCode = String.valueOf(key);
			
			//关系级别 -1：推荐人 1：一度好友
			String relationLevel ="1";
			if("-1".equals(String.valueOf(val))){
				relationLevel = "-1";
			}
			p.setRelativeLevel(relationLevel);
			
			String memberCode = "";
			MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",acCode,"manage_code","SI2011");
			if(membCodeMapGp != null){
				//用户编号
				memberCode = membCodeMapGp.get("member_code");
				p.setMemberCode(memberCode);
				//根据reAcCode获取好友信息：头像
				String sqlre=" SELECT e.member_code,e.head_icon_url,e.nickname "
						+ " FROM membercenter.mc_extend_info_groupcenter e "
						+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ acCode +"'";
				
				List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sqlre, new MDataMap());
				int aCount = aListMap.size();
				
				if(aListMap != null && aCount > 0){
					String headerUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
					//头像
					p.setHeaderUrl(headerUrl);
				}
			}else{
				MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",acCode);
				if(membCodeMap != null){
					memberCode = membCodeMap.get("member_code");
					p.setMemberCode(memberCode);
				}
			}
			
			//昵称
			Map<String,String> nickMap = new HashMap<String,String>();
			nickMap.put("account_code_wo", accountCode);
			nickMap.put("account_code_ta", acCode);
			nickMap.put("member_code", memberCode);
			p.setNickName(NickNameHelper.getNickName(nickMap));
			
			//备注
			String reMarkName="";
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("account_code_wo", accountCode);
			mDataMap.put("account_code_ta", acCode);
			Object object = DbUp.upTable("gc_alter_nickname").dataGet("nick_name", "account_code_wo=:account_code_wo and account_code_ta=:account_code_ta", mDataMap);
			if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
				reMarkName = String.valueOf(object);
			}
			p.setRemarkName(reMarkName);
			
			//等级
			String levelDescription = "";
			MDataMap gReMap=DbUp.upTable("gc_group_account").one("account_code",acCode);
			if(gReMap != null){
				String reAcLevel = gReMap.get("account_level");
				MDataMap gReLevelMap=DbUp.upTable("gc_group_level").one("level_code",reAcLevel);
				if(gReLevelMap != null ){
					levelDescription = gReLevelMap.get("level_name");
				}
			}
			p.setLevel(levelDescription);
			
			pInfoList.add(p);
			
		}
		fListResult.setFriendInfoModelList(pInfoList);
		
		return fListResult;
	}

}
