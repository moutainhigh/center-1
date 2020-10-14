package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.RelationInfo;
import com.cmall.groupcenter.account.model.ShowActionRelationResult;
import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.groupcenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 当活跃好友
 * @author fengls
 *
 */
public class ApiShowActionRelation extends
		RootApiForToken<ShowActionRelationResult, WithdrawRecordInput> {

	public ShowActionRelationResult Process(WithdrawRecordInput inputParam, MDataMap mRequestMap) {
		
		//原先是利沙写的，暂作注释备份，探索优化下
		/*ShowActionRelationResult actionRelationResult = new ShowActionRelationResult();
		
		List<RelationInfo> list = new ArrayList<RelationInfo>();
		
		//根据用户编号查账户编号（oneWhere取一条记录）
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
//		本月活跃总数
		int actionSumAccount = 0;
//		一度好友总数
		int oneSumAccount = 0;
//		二度好友总数
		int twoSumAccount = 0;
		
		// 取出一度社友（queryByWhere取多条记录）
		List<MDataMap> listOnes = DbUp.upTable("gc_member_relation")
				.queryByWhere("parent_code", sAccountCode, "flag_enable", "1");
		oneSumAccount = listOnes.size();
//		为取一度好友做准备
		List<String> listRes = new ArrayList<String>();
		for (MDataMap mDataMap : listOnes) {
			listRes.add(mDataMap.get("account_code"));
			
//			取出一度活跃
			
			String sCurrentMonth = DateHelper
					.upMonth(FormatHelper.upDateTime());
			List<MDataMap> actionOnes = DbUp.upTable("gc_active_month").queryByWhere("account_code",mDataMap.get("account_code"),"active_month",sCurrentMonth );
			
			if(actionOnes.size()>0){
				String accountCode = mDataMap.get("account_code");
				getActionInfo( list,sAccountCode,accountCode,0);
			}
			}
		
		List<MDataMap> listTwos = new ArrayList<MDataMap>();
		if(listRes.size()>0){
			// 取出二度社友
			 listTwos = DbUp.upTable("gc_member_relation")
					.queryInSafe(
							"account_code",
							"",
							" flag_enable=1 ",
							new MDataMap(),
							-1,
							-1,
							"parent_code",
							StringUtils.join(listRes,
									WebConst.CONST_SPLIT_COMMA));
			
		
		
		}
		
		
		for (MDataMap mDataMap : listTwos) {
			String sCurrentMonth = DateHelper
					.upMonth(FormatHelper.upDateTime());
			List<MDataMap> actionOnes = DbUp.upTable("gc_active_month").queryByWhere("account_code",mDataMap.get("account_code"),"active_month",sCurrentMonth );
			
			if(actionOnes.size()>0){
				String accountCode = actionOnes.get(0).get("account_code");
				getActionInfo( list,sAccountCode, accountCode,2);
				}
		}
	
		twoSumAccount = listTwos.size();	
		

		
		
//		二度活跃
//		我自己
		List<RelationInfo> listmys = new ArrayList<RelationInfo>();
		List<MDataMap> listmyacount = new ArrayList<MDataMap>();
		listmyacount.add(new MDataMap("account_code",sAccountCode));
		
		getActionInfo(listmys,sAccountCode,sAccountCode,1);
		RelationInfo myselfInfo = new RelationInfo();
		if(listmys!=null&&listmys.size()>0){
			 myselfInfo = listmys.get(0);
		}
		
		RelationInfo relationInfo = new RelationInfo();
		relationInfo.setRebateMoney("9999999999999");
		list.add(relationInfo);
		
		//返利金额逆序排序
		if(list!=null&&list.size()>0){
			Collections.sort(list, new Comparator<Object>() {
			      public int compare(Object RelationInfo, Object RelationInfo2) {
			    	  String one = ((RelationInfo)RelationInfo).getRebateMoney();
			    	  String two = ((RelationInfo)RelationInfo2).getRebateMoney();
			        return two.compareTo(one);
			      }
			    });
		
			
			list.set(0, myselfInfo);
			actionRelationResult.setRelationInfos(list);
		}
	
		actionSumAccount = list.size()-1;
		
		actionRelationResult.setActionSumAccount(actionSumAccount);
		actionRelationResult.setOneSumAccount(oneSumAccount);
		actionRelationResult.setTwoSumAccount(twoSumAccount);
		return actionRelationResult;*/
		
		ShowActionRelationResult actionRelationResult = new ShowActionRelationResult();
		String activeMonth = DateHelper.upMonth(FormatHelper.upDateTime());
		List<RelationInfo> list = new ArrayList<RelationInfo>();
		
		//根据用户编号查账户编号（oneWhere取一条记录）
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
//		本月活跃总数
		int actionSumAccount = 0;
//		一度好友总数
		int oneSumAccount = 0;
//		二度好友总数
		int twoSumAccount = 0;
		
		MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
				"sum_consume,sum_member", "", "", "account_code",
				sAccountCode, "active_month", activeMonth);
		// 开始取出用户的活跃信息
		if (mActiveMap != null) {
			actionSumAccount=Integer.valueOf(mActiveMap.get("sum_member"));
		}

		
		// 取出一度社友总数（queryByWhere取多条记录）
		oneSumAccount = DbUp.upTable("gc_member_relation").count("parent_code", sAccountCode, "flag_enable", "1");
		
		//取出二度好友
		MDataMap inMap=new MDataMap("account_code",sAccountCode,"active_month",activeMonth);
		String sql="select count(1) as totalCount from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1";
		Map<String, Object> twosMap=DbUp.upTable("gc_member_relation").dataSqlOne(sql, inMap);
		if(twosMap!=null){
			twoSumAccount=Integer.valueOf(twosMap.get("totalCount").toString());
		}
		
		
		//取活跃好友返利情况
		PageOption pageOption=inputParam.getPageOption();
		int start=pageOption.getLimit() * pageOption.getOffset();
	    int pageLimit=pageOption.getLimit();
	    String limitString="";
	    if (start> -1 && pageLimit > 0) {
			limitString=" limit " + String.valueOf(start) + ","
					+ String.valueOf(pageLimit);
		}
		String activeSql="select sum(reckon_money) as reckonMoney,order_account_code,relation_level   from gc_reckon_log where account_code=:account_code and order_account_code!=:account_code and reckon_change_type in('4497465200030001','4497465200030002') "+
            "and left(order_reckon_time,7)=:active_month GROUP BY order_account_code order by reckonMoney desc "+limitString;
		List<Map<String, Object>> activeList=DbUp.upTable("gc_reckon_log").dataSqlList(activeSql, inMap);
		PageResults pageResults = new PageResults();

		// 设置根据条件查询出的所有的结果的数量
		String totalSql="select count(DISTINCT(order_account_code)) as totalCount  from gc_reckon_log where  account_code=:account_code and order_account_code!=:account_code and reckon_change_type in('4497465200030001','4497465200030002') "+
		"and left(order_reckon_time,7)=:active_month";
		Map<String,Object> totalMap=DbUp.upTable("gc_reckon_log").dataSqlOne(totalSql, inMap);
		if(totalMap!=null){
			pageResults.setTotal(Integer.valueOf(totalMap.get("totalCount").toString()));
		}
		else{
			pageResults.setTotal(0);
		}
		
		// 返回的条数
		pageResults.setCount(activeList==null?0:activeList.size());

		// 判断是否还有更多数据
		pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
						.getCount()) < pageResults.getTotal() ? 1 : 0);	
		//第一页本人情况
		if(pageOption.getOffset()==0){
			Map<String, Object> ownMap=new HashMap();
			ownMap.put("relation_level", 0);
			ownMap.put("order_account_code", sAccountCode);
			String ownSql="select ifnull(sum(reckon_money),0) as reckonMoney from gc_reckon_log where account_code=:account_code and order_account_code=:account_code and reckon_change_type in('4497465200030001','4497465200030002') "+
					"and left(order_reckon_time,7)=:active_month";
			Map<String, Object> ownMapSql=DbUp.upTable("gc_reckon_log").dataSqlOne(ownSql, inMap);
			if(ownMapSql!=null){
				ownMap.put("reckonMoney", ownMapSql.get("reckonMoney"));
			}
			else{
				ownMap.put("reckonMoney", 0);
			}
			activeList.add(0, ownMap);
		}
		if(activeList!=null&&activeList.size()>0){
			for(Map<String, Object> activeMap:activeList){
				RelationInfo relationInfo = new RelationInfo();
				if(activeMap.get("relation_level")!=null){
					relationInfo.setRelationType(relationDscription(activeMap.get("relation_level").toString()));
				}
				relationInfo.setRebateMoney(activeMap.get("reckonMoney").toString());
				//得到电话号码。
				 Map<String, Object> loginNameMap = DbUp.upTable("mc_login_info").dataSqlOne("select a.login_name from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = '"+activeMap.get("order_account_code").toString()+"'", null);
				 if(loginNameMap != null){
					 relationInfo.setMobile(loginNameMap.get("login_name").toString());
				 }else {
					 relationInfo.setMobile("");
				 }
 
				//查询个人头像
				 String headIconSql = "select head_icon_url,nickname from mc_extend_info_groupcenter ig INNER JOIN mc_login_info mli ON  ig.member_code=mli.member_code and ig.app_code=mli.manage_code   where ig.app_code='"+this.getManageCode()+"' and mli.login_name='"+relationInfo.getMobile()+"'";
				 Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
			     if(headIconMap!=null && headIconMap.get("head_icon_url")!=null){
			    	 relationInfo.setHeadIconUrl(headIconMap.get("head_icon_url")==null ? "" : String.valueOf(headIconMap.get("head_icon_url")));
			     } 
			     
			     relationInfo.setNickName(headIconMap==null || StringUtils.isBlank(String.valueOf(headIconMap.get("nickname"))) ? "" :  String.valueOf(headIconMap.get("nickname")) );
				 
				 //得等级
				 MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere("account_level","", "", "account_code",activeMap.get("order_account_code").toString());
				 if(mGroupAccountMap!=null){
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", mGroupAccountMap.get("account_level"));  
					  
					  relationInfo.setLevelName(mLevelMap.get("level_name"));
					  
				  }else{
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", GroupConst.DEFAULT_LEVEL_CODE);  
					  
					  relationInfo.setLevelName(mLevelMap.get("level_name"));
				  }
				  
			      //得最后活跃时间（这个是签收的时间）
				  Map<String, Object> accountCodeMap = DbUp.upTable("gc_active_log").dataSqlOne("select active_time from gc_active_log where account_code = '"+activeMap.get("order_account_code").toString()+"' order by active_time desc limit 0,1;", null);
				
				  if(accountCodeMap!=null){
					  relationInfo.setActiveTime( accountCodeMap.get("active_time").toString());
				  }
				  else {
					  MDataMap dataMap =   DbUp.upTable("gc_member_relation").one("parent_code",sAccountCode,"account_code",activeMap.get("order_account_code").toString(),"flag_enable","1");
					  if(dataMap !=null){
						  relationInfo.setActiveTime( dataMap.get("create_time").toString());
					  }
					  else {
						  relationInfo.setActiveTime("");
					  }
				 }
				 list.add(relationInfo);
			}
				
		}
		actionRelationResult.setRelationInfos(list);
		actionRelationResult.setActionSumAccount(actionSumAccount);
		actionRelationResult.setOneSumAccount(oneSumAccount);
		actionRelationResult.setTwoSumAccount(twoSumAccount);
		actionRelationResult.setPageResults(pageResults);
		return actionRelationResult;
		
}

	/**
	 * 获取时间
	 * 
	 * @param sDate
	 * @return
	 */
	private String upDay(String sDate) {
		return StringUtils.substringBefore(sDate, " ");
	}

	
	/**
	 * 
	 * 得到活跃好友的信息，根据账号
	 * @param list
	 * @param listOnes
	 * @param sAccountCode
	 * ismyself 1 是自己 自己是必须显示出来的。0是一度 ，2是二度。只有0需要判断一下。
	 * 如果是二度好友能在活跃中查出来，，，证明它是消费了，可能分不到本人里面。。。总结：二度好友和本人必须加到里面
	 */
	public void getActionInfo( List<RelationInfo> list,String sAccountCode,String accountCode,int ismyself){
			RelationInfo RelationInfo = new RelationInfo();
//			得到关系类别，一度好友，，，二度好友。。。本人
			String relationType = "";
			MDataMap dataMaps = 	DbUp.upTable("gc_member_relation").one("account_code",accountCode,"flag_enable","1");
			String parent_code = "";
			if(dataMaps!=null){
				parent_code = dataMaps.get("parent_code");
			}
		
			if(sAccountCode.equals(accountCode)){
				relationType = "本人";
			}else if(sAccountCode.equals(parent_code)){
				relationType = "一度好友";
			}else {
				relationType = "二度好友";
			}
			RelationInfo.setRelationType(relationType);
//			得到电话号码。
			 Map<String, Object> loginNameMap = DbUp.upTable("mc_login_info").dataSqlOne("select a.login_name from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = '"+accountCode+"'", null);
			 
			 if(loginNameMap != null){
				 
				 RelationInfo.setMobile(loginNameMap.get("login_name").toString());
			 }else {
				 RelationInfo.setMobile("");
			 }
//			 得等级
			  MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
						"account_level",
						"", "", "account_code", accountCode);
			  if(mGroupAccountMap!=null){
				  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
							"level_code", mGroupAccountMap.get("account_level"));  
				  
				  RelationInfo.setLevelName(mLevelMap.get("level_name"));
				  
			  }else{
				  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
							"level_code", GroupConst.DEFAULT_LEVEL_CODE);  
				  
				  RelationInfo.setLevelName(mLevelMap.get("level_name"));
			  }
			  
//				 得最后活跃时间（这个是签收的时间）
			  Map<String, Object> accountCodeMap = DbUp.upTable("gc_active_log").dataSqlOne("select active_time from gc_active_log where account_code = '"+accountCode+"' order by active_time desc limit 0,1;", null);
			
			  if(accountCodeMap!=null){
				  RelationInfo.setActiveTime( accountCodeMap.get("active_time").toString());
			  }else {
				MDataMap dataMap =   DbUp.upTable("gc_member_relation").one("parent_code",sAccountCode,"account_code",accountCode,"flag_enable","1");
				
				if(dataMap !=null){
					  RelationInfo.setActiveTime( dataMap.get("create_time").toString());
				}else {
					  RelationInfo.setActiveTime("");
				}
			   }
//			得到返利
				String sCurrentMonth = DateHelper
						.upMonth(FormatHelper.upDateTime());
				
				int count = DbUp.upTable("gc_reckon_log").count("account_code",sAccountCode,"order_account_code",accountCode);
				
				if(ismyself == 0&&count==0){
					 Map<String, Object> reckonMoneyMap = DbUp.upTable("gc_reckon_log").dataSqlOne("select sum(reckon_money) as reckon_money  from gc_reckon_log where account_code = '"+sAccountCode+"' and order_account_code ='"+accountCode+"'and reckon_change_type in('4497465200030001','4497465200030002') and order_reckon_time like '%"+sCurrentMonth+"%'",null);
					  if(reckonMoneyMap.get("reckon_money")!=null){
						  RelationInfo.setRebateMoney(reckonMoneyMap.get("reckon_money").toString());
						  list.add(RelationInfo); 
					  }
				}else {
					 Map<String, Object> reckonMoneyMap = DbUp.upTable("gc_reckon_log").dataSqlOne("select ifnull(sum(reckon_money),0) as reckon_money  from gc_reckon_log where account_code = '"+sAccountCode+"' and order_account_code ='"+accountCode+"'and reckon_change_type in('4497465200030001','4497465200030002') and order_reckon_time like '%"+sCurrentMonth+"%'",null);
						  RelationInfo.setRebateMoney(reckonMoneyMap.get("reckon_money").toString());
						  list.add(RelationInfo); 
				}
			 

		}
	
	/**
	 * 关系描述
	 * @param relationLevel
	 * @return
	 */
	public String relationDscription(String relationLevel){
		String description="";
		if(relationLevel.equals("0")){
			description="本人";
		}
		else if(relationLevel.equals("1")){
			description="一度好友";
		}
		else if(relationLevel.equals("2")){
			description="二度好友";
		}
		return description;
	}
	
}
