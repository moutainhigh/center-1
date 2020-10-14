package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.RelationInfo;
import com.cmall.groupcenter.account.model.ShowOneRelationResult;
import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 
 * @author fengls
 *
 */
public class ApiShowOneRelation extends
		RootApiForToken<ShowOneRelationResult, WithdrawRecordInput> {
	
	public ShowOneRelationResult Process(WithdrawRecordInput inputParam, MDataMap mRequestMap) {
		
		ShowOneRelationResult oneRelationResult = new ShowOneRelationResult();
		List<RelationInfo> list = new ArrayList<RelationInfo>();
		//根据用户编号查账户编号（oneWhere取一条记录）
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		//原先利沙写的，先做注释，暂改为按绑定关系时间
		/*// 取出一度社友（queryByWhere取多条记录）
		List<MDataMap> listOnes = DbUp.upTable("gc_member_relation")
				.queryByWhere("parent_code", sAccountCode, "flag_enable", "1");

		if (listOnes != null && listOnes.size() > 0) {

			Map<String, RelationInfo> mapOneRelation = new ConcurrentHashMap<String, RelationInfo>();

			
			// 定义一度社友
	
			for (MDataMap mDataMap : listOnes) {
				RelationInfo oneRelation = new RelationInfo();
//				得到电话号码。
				String accountCode = mDataMap.get("account_code");
				 
				 Map<String, Object> loginNameMap = DbUp.upTable("mc_login_info").dataSqlOne("select a.login_name from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = '"+accountCode+"'", null);
				 
				 if(loginNameMap != null){
					 
					 oneRelation.setMobile(loginNameMap.get("login_name").toString());
				 }else {
					 oneRelation.setMobile("");
				 }
//				 得等级
				 
				 
				  MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
							"account_level",
							"", "", "account_code", accountCode);
				  
				  
				  if(mGroupAccountMap!=null){
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", mGroupAccountMap.get("account_level"));  
					  
					  oneRelation.setLevelName(mLevelMap.get("level_name"));
					  
				  }else{
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", GroupConst.DEFAULT_LEVEL_CODE);  
					  
					  oneRelation.setLevelName(mLevelMap.get("level_name"));
				  }
				  
					 
				 
					 
//					 得最后活跃时间（这个是签收的时间）
				  
				  
				  Map<String, Object> accountCodeMap = DbUp.upTable("gc_active_log").dataSqlOne("select active_time from gc_active_log where account_code = '"+accountCode+"' order by active_time desc limit 0,1;", null);
				
				  if(accountCodeMap!=null){
					  oneRelation.setActiveTime( accountCodeMap.get("active_time").toString());
				  }else {
					MDataMap dataMap =   DbUp.upTable("gc_member_relation").one("parent_code",sAccountCode,"account_code",accountCode,"flag_enable","1");
					
					if(dataMap !=null){
						  oneRelation.setActiveTime( dataMap.get("create_time").toString());
					}else {
						  oneRelation.setActiveTime("");
					}
				
				
				   }
				  
				
//				得到返利
				  
				  Map<String, Object> reckonMoneyMap = DbUp.upTable("gc_reckon_log").dataSqlOne("select ifnull(sum(reckon_money),0.00) as reckon_money  from gc_reckon_log where account_code = '"+sAccountCode+"' and order_account_code ='"+accountCode+"'and reckon_change_type in('4497465200030001','4497465200030002')",null);
				  oneRelation.setRebateMoney( reckonMoneyMap.get("reckon_money").toString());
				  list.add(oneRelation);
			}
			
			
			
			//时间逆序排序
			Collections.sort(list, new Comparator<Object>() {
			      public int compare(Object oneRelation, Object oneRelation2) {
			    	  String one = ((RelationInfo)oneRelation).getActiveTime();
			    	  String two = ((RelationInfo)oneRelation2).getActiveTime();
			        return two.compareTo(one);
			      }
			    });
		

			oneRelationResult.setRelationInfos(list);
			
			
		}*/
		
		// 取出一度社友（queryByWhere取多条记录）,分页
		MPageData mPageData= DataPaging.upPageData("gc_member_relation", "", "-create_time", new MDataMap("parent_code",sAccountCode,"flag_enable","1"),inputParam.getPageOption());
		List<MDataMap> listOnes=mPageData.getListData();
		if (listOnes != null && listOnes.size() > 0) {
			// 定义一度社友
			for (MDataMap mDataMap : listOnes) {
				RelationInfo oneRelation = new RelationInfo();
				//得到电话号码。
				String accountCode = mDataMap.get("account_code");
				 List<Map<String, Object>> manageCodeList = DbUp.upTable("mc_login_info").dataSqlList("select a.login_name,a.member_code,a.manage_code from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = '"+accountCode+"' " , null);
				 if(manageCodeList != null && manageCodeList.size()>0){
					 oneRelation.setMobile(manageCodeList.get(0).get("login_name").toString());
					 for(Map<String,Object> managecodeMap : manageCodeList) {
						 if(this.getManageCode().toString().equals(String.valueOf(managecodeMap.get("manage_code")))){
							 //查询个人头像
							 String headIconSql = "select head_icon_url,nickname from mc_extend_info_groupcenter where app_code='"+ managecodeMap.get("manage_code").toString() +"' and member_code= '"+managecodeMap.get("member_code").toString() +"'";
							 Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
						     if(headIconMap!=null && headIconMap.get("head_icon_url")!=null){
						    	 oneRelation.setHeadIconUrl(headIconMap.get("head_icon_url")==null ? "" : String.valueOf(headIconMap.get("head_icon_url")));
						     }
						     oneRelation.setNickName(headIconMap==null || StringUtils.isBlank(String.valueOf(headIconMap.get("nickname"))) ? "" :  String.valueOf(headIconMap.get("nickname")) );
						 }
					 }
				 }else {
					 oneRelation.setMobile("");
				 }
				
				 //得等级
				  MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
							"account_level",
							"", "", "account_code", accountCode);
				  
				  
				  if(mGroupAccountMap!=null){
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", mGroupAccountMap.get("account_level"));  
					  
					  oneRelation.setLevelName(mLevelMap.get("level_name"));
					  
				  }else{
					  MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
								"level_code", GroupConst.DEFAULT_LEVEL_CODE);  
					  
					  oneRelation.setLevelName(mLevelMap.get("level_name"));
				  }
				  
				 //绑定关系时间
				 oneRelation.setActiveTime(mDataMap.get("create_time"));
				//得到返利
				  
				Map<String, Object> reckonMoneyMap = DbUp.upTable("gc_reckon_log").dataSqlOne("select ifnull(sum(reckon_money),0.00) as reckon_money  from gc_reckon_log where account_code = '"+sAccountCode+"' and order_account_code ='"+accountCode+"'and reckon_change_type in('4497465200030001','4497465200030002')",null);
				oneRelation.setRebateMoney( reckonMoneyMap.get("reckon_money").toString());
				list.add(oneRelation);
			}
			oneRelationResult.setPageResults(mPageData.getPageResults());
			oneRelationResult.setRelationInfos(list);
		}

		return oneRelationResult;
	}


	/*@Test
	public void test1() {
		// TODO Auto-generated method stub
//		得到活动时间。
		 Map<String, Object> map = DbUp.upTable("gc_active_log").dataSqlOne("select active_time from gc_active_log where account_code = 'AI140807100076' order by active_time desc limit 0,1;", null);
		 System.out.println(map.get("active_time"));

	
		 //得到级别
		 MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
					"account_reckon_money,account_withdraw_money,account_level,total_reckon_money",
					"", "", "account_code", "AI140807100076");
			 MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
			"level_code", mGroupAccountMap.get("account_level"));
		 
		 System.out.println("level_name===>"+mLevelMap.get("level_name"));
		 
		 
		 
		 
//		 根据账户编号查询手机号码
		 
		 Map<String, Object> map2 = DbUp.upTable("mc_login_info").dataSqlOne("select a.login_name from membercenter.mc_login_info a ,membercenter.mc_member_info b  where  a.member_code = b.member_code and b.account_code = 'AI140807100076'", null);
		 
		 
		 

		 
	     System.out.println(map2.get("login_name"));
	
	
	
	}
	*/



}
