package com.cmall.groupcenter.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.api.ApiAccountInfoNew;
import com.cmall.groupcenter.account.model.AccountFridensInfo;
import com.cmall.groupcenter.account.model.AccountFriendsListInput;
import com.cmall.groupcenter.account.model.AccountFriendsListResult;
import com.cmall.groupcenter.account.model.AccountInfoResultNew;
import com.cmall.groupcenter.account.model.AccountPersonalHomepageInput;
import com.cmall.groupcenter.account.model.AccountPersonalHomepageResult;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 微公社相关
 * @author GaoYang
 * @CreateDate 2015年6月4日下午6:13:56
 * 
 */
public class GroupNewService extends BaseClass{

	/**
	 * 获取好友列表信息
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public AccountFriendsListResult ShowAccountFriendsList(String accountCode,
			AccountFriendsListInput inputParam) {
		
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		AccountFriendsListResult friendsListResult = new AccountFriendsListResult();
		
		String selectionType = inputParam.getSelectionType();
		String relationLevel = inputParam.getRelationLevel();
		
		String sysFormat = "yyyy-MM"; // 年/月
		SimpleDateFormat sFormat = new SimpleDateFormat(sysFormat);
		//系统时间
		java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
		//现在时间
		String nowYM = sFormat.format(timestamp);
		
		PageResults pageResults = new PageResults();
		PageOption pageOption=inputParam.getPageOption();
		int start=pageOption.getLimit() * pageOption.getOffset();
	    int pageLimit=pageOption.getLimit();
	    String limitString="";
	    if (start> -1 && pageLimit > 0) {
			limitString=" limit " + String.valueOf(start) + ","
					+ String.valueOf(pageLimit);
		}
	    
		//筛选类型 1:我的好友 2:本月活跃 3:土豪 4:地主 5:富农 6:中农
		if("1".equals(selectionType)){
			
			//一度好友数量
			int oneFridensNumber = 0;
			//二度好友数量
			int twoFridensNumber = 0;
			
			// 1:我的好友
			if(StringUtils.isBlank(relationLevel)){
				friendsListResult.setResultCode(918519020);
				friendsListResult.setResultMessage(bInfo(918519020,"【好友级别】"));
				return friendsListResult;
			}
			
			//账户编号
			friendsListResult.setAccountCode(accountCode);
			
			//一度好友人数
		    List<Map<String, Object>> oneTotalRelationList = new ArrayList<Map<String, Object>>();
			MDataMap in1Map=new MDataMap("parent_code",accountCode,"flag_enable", "1");
			String totalRelSql="select account_code  from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable ";
			oneTotalRelationList=DbUp.upTable("gc_member_relation").dataSqlList(totalRelSql, in1Map);
			if(oneTotalRelationList != null && oneTotalRelationList.size()>0){
				oneFridensNumber = oneTotalRelationList.size();
			}
			friendsListResult.setOneLevelFriendsNumber(String.valueOf(oneFridensNumber));
			
			//二度好友人数
			List<Map<String, Object>> twoTotalRelationList = new ArrayList<Map<String, Object>>();
			MDataMap in2Map=new MDataMap("account_code",accountCode);
			String sql="select account_code from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1";
			twoTotalRelationList=DbUp.upTable("gc_member_relation").dataSqlList(sql, in2Map);
			if(twoTotalRelationList != null && twoTotalRelationList.size()>0){
				twoFridensNumber = twoTotalRelationList.size();
			}
			friendsListResult.setTwoLevelFriendsNumber(String.valueOf(twoFridensNumber));
			
			//我的推荐人数
			int refereeNumber = 0;
			String myRefereeCode = "";//推荐人
			String myTwoRefereeCode = "";//二度推荐人(推荐人的推荐人)
			//获取推荐人
			MDataMap myLelationMap=DbUp.upTable("gc_member_relation").one("account_code",accountCode);
			if(myLelationMap != null ){
				myRefereeCode = myLelationMap.get("parent_code");//推荐人的account_code
				refereeNumber = refereeNumber + 1;
			}
			
			//获取二度推荐人
			if(StringUtils.isNotBlank(myRefereeCode)){
				MDataMap myTwoLelationMap=DbUp.upTable("gc_member_relation").one("account_code",myRefereeCode);
				if(myTwoLelationMap != null){
					myTwoRefereeCode = myTwoLelationMap.get("parent_code");//二度推荐人的account_code
					refereeNumber = refereeNumber + 1;
				}
			}
			friendsListResult.setRefereeNumber(String.valueOf(refereeNumber));
			
			//一度好友，二度好友
			if("1".equals(relationLevel) || "2".equals(relationLevel)){
				
				//当月消费额
				HashMap monthMap = new HashMap();
				HashMap tempMonthMap = new HashMap();
			    //总消费额
			    HashMap totalMap = new HashMap();
			    LinkedHashMap<String, String> acCompareMap = new LinkedHashMap<String, String>();//好友比较用MAP
				List<Map<String, Object>> oneFridendsList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> twoFridendsList = new ArrayList<Map<String, Object>>();
				List<AccountFridensInfo> friendsInfoList=new ArrayList<AccountFridensInfo>();
				
				if("1".equals(relationLevel)){
					//一度好友信息
					String relSql="select account_code from gc_member_relation where parent_code=:parent_code and flag_enable =:flag_enable order by create_time "+limitString;
					oneFridendsList=DbUp.upTable("gc_member_relation").dataSqlList(relSql, in1Map);
					
					//一度好友总条数
					if(oneTotalRelationList != null && oneTotalRelationList.size()>0){
						pageResults.setTotal(Integer.valueOf(oneTotalRelationList.size()));
					}else{
						pageResults.setTotal(0);
					}
					
					//返回的条数
					pageResults.setCount(oneFridendsList==null?0:oneFridendsList.size());
					//判断是否还有更多数据
					pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
									.getCount()) < pageResults.getTotal() ? 1 : 0);
					
					StringBuffer monthSql = new StringBuffer();
					StringBuffer totalSql = new StringBuffer();
					List<String> lAdd = new ArrayList<String>();//查询条件
					
					if(oneFridendsList != null && oneFridendsList.size()>0){
						for(int i = 0;i < oneFridendsList.size();i++){
							String acCode = (String) oneFridendsList.get(i).get("account_code");
							lAdd.add("'"+acCode+"'");
							acCompareMap.put(acCode, acCode);
						}
						if(lAdd.size()>0){
							
							//一度好友月消费额
							monthSql.append(" select order_account_code,IFNULL(sum(consume_money),0) as monthConsumeMoney ");
							monthSql.append(" from gc_active_log ");
							monthSql.append(" where account_code = '").append(accountCode).append("' ");
							monthSql.append(" and order_account_code in (").append(StringUtils.join(lAdd, ",")).append(") ");
							monthSql.append(" and left(active_time,7) = '").append(nowYM).append("' ");
							monthSql.append(" group by order_account_code ");
							List<Map<String, Object>> monthList=DbUp.upTable("gc_active_log").dataSqlList(monthSql.toString(), new MDataMap());
							//临时保存月消费额数据
							if(monthList != null && monthList.size()>0){
								for(int t = 0;t<monthList.size();t++){
									String tOrderAccountCode = String.valueOf(monthList.get(t).get("order_account_code"));//一度好友月消费额
									String monthConsumeMoney = String.valueOf(monthList.get(t).get("monthConsumeMoney"));//一度好友自己的月消费额
									
//									//查询一度好友的一度好友
//									List<String> fridendsList = GetOneFridendsList(tOrderAccountCode);
//									//查询一度好友的一度好友的月消费额
//									String fridendMonthConsume = GetFridendMonthConsume(tOrderAccountCode,fridendsList,nowYM);
//									
//									//月消费额 = 一度好友自己的月消费额 + 一度好友的一度好友的月消费额
//									float fAndOnefMonthConsume = Float.parseFloat(monthConsumeMoney) + Float.parseFloat(fridendMonthConsume);
//									monthMap.put(tOrderAccountCode, fAndOnefMonthConsume);
									tempMonthMap.put(tOrderAccountCode, monthConsumeMoney);
								}
							}
							
							//计算月消费额(数据中存在一度好友本月没有消费 但是一度好友的一度好友的存在本月消费的情况，所以在单独获取一度好友的一度好友的月消费额，然后在累加)
							for(int i = 0;i < lAdd.size();i++){
								String tOrderAccountCode = (String) lAdd.get(i).replaceAll("'", "");
								//查询一度好友的一度好友
								List<String> fridendsList = GetOneFridendsList(tOrderAccountCode);
								//查询一度好友的一度好友的月消费额
								String fridendMonthConsume = GetFridendMonthConsume(tOrderAccountCode,fridendsList,nowYM);
								
								//一度好友自己的月消费额
								String monthConsumeMoney = "0";
								if(tempMonthMap.get(tOrderAccountCode) != null){
									monthConsumeMoney = String.valueOf(tempMonthMap.get(tOrderAccountCode));
								}
								
								//月消费额 = 一度好友自己的月消费额 + 一度好友的一度好友的月消费额
								float fAndOnefMonthConsume = Float.parseFloat(monthConsumeMoney) + Float.parseFloat(fridendMonthConsume);
								monthMap.put(tOrderAccountCode, decimalFormat.format(fAndOnefMonthConsume));
							}
							
							//一度好友总消费额
							totalSql.append(" select order_account_code,IFNULL(sum(consume_money),0) as totalConsumeMoney ");
							totalSql.append(" from gc_active_log ");
							totalSql.append(" where account_code = '").append(accountCode).append("' ");
							totalSql.append(" and order_account_code in (").append(StringUtils.join(lAdd, ",")).append(") ");
							totalSql.append(" group by order_account_code ");
							List<Map<String, Object>> totalList=DbUp.upTable("gc_active_log").dataSqlList(totalSql.toString(), new MDataMap());
							//临时保存总消费额数据
							if(totalList != null && totalList.size()>0){
								for(int t = 0;t<totalList.size();t++){
									String tOrderAccountCode = String.valueOf(totalList.get(t).get("order_account_code"));//一度好友
									String totalConsumeMoney = String.valueOf(totalList.get(t).get("totalConsumeMoney"));//一度好友自己的总消费额
									
									//查询一度好友的一度好友
									List<String> fridendsList = GetOneFridendsList(tOrderAccountCode);
									//查询一度好友的一度好友的总消费额
									String fridendTotalConsume = GetFridendTotalConsume(tOrderAccountCode,fridendsList);
									
									//月消费额 = 一度好友自己总消费额 + 一度好友的一度好友的总消费额
									float fAndOnefTotalConsume = Float.parseFloat(totalConsumeMoney) + Float.parseFloat(fridendTotalConsume);
									totalMap.put(tOrderAccountCode, decimalFormat.format(fAndOnefTotalConsume));
								}
							}
						}
					}
					
				}else if("2".equals(relationLevel)){
					//二度好友信息
					String relSql="select account_code from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1 order by create_time "+limitString;
					twoFridendsList=DbUp.upTable("gc_member_relation").dataSqlList(relSql, in2Map);
					
					//二度好友总条数
					if(twoTotalRelationList != null && twoTotalRelationList.size()>0){
						pageResults.setTotal(Integer.valueOf(twoTotalRelationList.size()));
					}else{
						pageResults.setTotal(0);
					}
					
					//返回的条数
					pageResults.setCount(twoFridendsList==null?0:twoFridendsList.size());
					//判断是否还有更多数据
					pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
									.getCount()) < pageResults.getTotal() ? 1 : 0);
					
					StringBuffer monthSql = new StringBuffer();
					StringBuffer totalSql = new StringBuffer();
					List<String> lAdd = new ArrayList<String>();//查询条件
					
					if(twoFridendsList != null && twoFridendsList.size()>0){
						for(int i = 0;i < twoFridendsList.size();i++){
							String acCode = (String) twoFridendsList.get(i).get("account_code");
							lAdd.add("'"+acCode+"'");
							acCompareMap.put(acCode, acCode);
						}
						if(lAdd.size()>0){
							
							//二度好友月消费额
							monthSql.append(" select order_account_code,IFNULL(sum(consume_money),0) as monthConsumeMoney ");
							monthSql.append(" from gc_active_log ");
							monthSql.append(" where account_code = '").append(accountCode).append("' ");
							monthSql.append(" and order_account_code in (").append(StringUtils.join(lAdd, ",")).append(") ");
							monthSql.append(" and left(active_time,7) = '").append(nowYM).append("' ");
							monthSql.append(" group by order_account_code ");
							List<Map<String, Object>> monthList=DbUp.upTable("gc_active_log").dataSqlList(monthSql.toString(), new MDataMap());
							//临时保存月消费额数据
							if(monthList != null && monthList.size()>0){
								for(int t = 0;t<monthList.size();t++){
									String tOrderAccountCode = String.valueOf(monthList.get(t).get("order_account_code"));//二度好友
									String monthConsumeMoney = String.valueOf(monthList.get(t).get("monthConsumeMoney"));//二度好友自己的月消费额
									monthMap.put(tOrderAccountCode, monthConsumeMoney);
								}
							}
							
							//二度好友总消费额
							totalSql.append(" select order_account_code,IFNULL(sum(consume_money),0) as totalConsumeMoney ");
							totalSql.append(" from gc_active_log ");
							totalSql.append(" where account_code = '").append(accountCode).append("' ");
							totalSql.append(" and order_account_code in (").append(StringUtils.join(lAdd, ",")).append(") ");
							totalSql.append(" group by order_account_code ");
							List<Map<String, Object>> totalList=DbUp.upTable("gc_active_log").dataSqlList(totalSql.toString(), new MDataMap());
							//临时保存总消费额数据
							if(totalList != null && totalList.size()>0){
								for(int t = 0;t<totalList.size();t++){
									String tOrderAccountCode = String.valueOf(totalList.get(t).get("order_account_code"));//二度好友
									String totalConsumeMoney = String.valueOf(totalList.get(t).get("totalConsumeMoney"));//二度好友自己的总消费额
									totalMap.put(tOrderAccountCode, totalConsumeMoney);
								}
							}
						}
					}
				}
				
				//消费比较
				Iterator acIt1 = acCompareMap.keySet().iterator();
				while(acIt1.hasNext()){
					AccountFridensInfo fInfo = new AccountFridensInfo();
					//好友编号
					Object key = acIt1.next();
					String mOrderAccountCode = String.valueOf(key);
					fInfo.setTaAccountCode(mOrderAccountCode);
					//此好友有月消费记录
					if(monthMap.containsKey(key)){
						//本月消费
						fInfo.setMonthConsumeMoney(String.valueOf(monthMap.get(mOrderAccountCode)));
						if(totalMap.containsKey(key)){
							//此好友有月消费记录，也有总消费
							fInfo.setTotalConsumeMoney(String.valueOf(totalMap.get(mOrderAccountCode)));
						}else{
							//此好友有月消费记录，但是没有总消费   总消费=0
							fInfo.setTotalConsumeMoney("0.00");
						}
					}else{
						//此好友无月消费记录 月消费 = 0
						fInfo.setMonthConsumeMoney("0.00");
						if(totalMap.containsKey(key)){
							//此好友无月消费记录，但是有总消费
							fInfo.setTotalConsumeMoney(String.valueOf(totalMap.get(mOrderAccountCode)));
						}else{
							//此好友无月消费记录，也没有总消费    总消费=0
							fInfo.setTotalConsumeMoney("0.00");
						}
					}
					
					//获取好友头像,用户编号,微公社用户标记
					String memberCode = "";
					MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",mOrderAccountCode,"manage_code","SI2011");
					
					if(membCodeMapGp != null){
						//用户编号
						memberCode = membCodeMapGp.get("member_code");
						fInfo.setMemberCode(memberCode);
						//微公社用户标记
						fInfo.setIsGroup("1");
						//根据orderAccountCode获取好友信息：头像
						String sqlf=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ mOrderAccountCode +"'";
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sqlf, new MDataMap());
						int aCount = aListMap.size();
						
						if(aListMap != null && aCount > 0){
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));						
							//头像
							fInfo.setHeadIconUrl(headIconUrl);
						}
						
					}else{
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",mOrderAccountCode);
						if(membCodeMap != null){
							String manageCode = membCodeMap.get("manage_code");
							memberCode = membCodeMap.get("member_code");
							fInfo.setMemberCode(memberCode);
							//微公社用户标记
							if("SI2011".equals(manageCode)){
								fInfo.setIsGroup("1");//是
							}else{
								fInfo.setIsGroup("0");//否
							}
						}
					}
					
					//昵称
					Map<String,String> nickMap = new HashMap<String,String>();
					nickMap.put("account_code_wo", accountCode);
					nickMap.put("account_code_ta", mOrderAccountCode);
					nickMap.put("member_code", memberCode);
					fInfo.setNickName(NickNameHelper.getNickName(nickMap));
					
					//好友关系级别
					fInfo.setRelationLevel(relationLevel);
					//好友等级
					MDataMap gAccountMap=DbUp.upTable("gc_group_account").one("account_code",mOrderAccountCode);
					if(gAccountMap != null){
						String accountLevel = gAccountMap.get("account_level");
						MDataMap gLevelMap=DbUp.upTable("gc_group_level").one("level_code",accountLevel);
						if(gLevelMap != null ){
							String levelName = gLevelMap.get("level_name");
							fInfo.setFridenLevel(levelName);
						}
					}
					//筛选类型
					fInfo.setSelectionType(selectionType);
					friendsInfoList.add(fInfo);
				}
				//好友信息
				friendsListResult.setFriendsInfoList(friendsInfoList);
				//分页信息
				friendsListResult.setPageResults(pageResults);
				
			}else if("3".equals(relationLevel)){
				List<AccountFridensInfo> refereeInfoList=new ArrayList<AccountFridensInfo>();
				
				LinkedHashMap<String,String> reMap = new LinkedHashMap<String,String>();
				String refereeCode = "";//推荐人
				String twoRefereeCode = "";//二度推荐人(推荐人的推荐人)
				//获取推荐人
				MDataMap meLelationMap=DbUp.upTable("gc_member_relation").one("account_code",accountCode);
				
				if(meLelationMap != null ){
					refereeCode = meLelationMap.get("parent_code");//推荐人的account_code
					reMap.put(refereeCode, "-1");
				}
				
				//获取二度推荐人
				if(StringUtils.isNotBlank(refereeCode)){
					MDataMap twoLelationMap=DbUp.upTable("gc_member_relation").one("account_code",refereeCode);
					if(twoLelationMap != null){
						twoRefereeCode = twoLelationMap.get("parent_code");//二度推荐人的account_code
						reMap.put(twoRefereeCode, "-2");
					}
				}
				
				//推荐人信息
				Iterator reIt = reMap.entrySet().iterator();
				while(reIt.hasNext()){
					
					Map.Entry entry = (Map.Entry) reIt.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					String reAcCode = String.valueOf(key);//account_code
					String reAcRelationLevel =String.valueOf(val);//关系级别 -1：推荐人 -2：二度推荐人
					
					AccountFridensInfo refereeInfo = new AccountFridensInfo();
					//设定账户编号
					refereeInfo.setTaAccountCode(reAcCode);
					//设定关系级别 
					refereeInfo.setRelationLevel(reAcRelationLevel);
					
					//获取好友头像,用户编号,微公社用户标记
					String memberCode = "";
					MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",reAcCode,"manage_code","SI2011");
					if(membCodeMapGp != null){
						//用户编号
						memberCode = membCodeMapGp.get("member_code");
						refereeInfo.setMemberCode(memberCode);
						//微公社用户标记
						refereeInfo.setIsGroup("1");
						//根据reAcCode获取好友信息：头像
						String sqlre=" SELECT e.member_code,e.head_icon_url,e.nickname "
								+ " FROM membercenter.mc_extend_info_groupcenter e "
								+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ reAcCode +"'";
						
						List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sqlre, new MDataMap());
						int aCount = aListMap.size();
						
						if(aListMap != null && aCount > 0){
							String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
							//头像
							refereeInfo.setHeadIconUrl(headIconUrl);
						}
					}else{
						MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",reAcCode);
						if(membCodeMap != null){
							memberCode = membCodeMap.get("member_code");
							refereeInfo.setMemberCode(memberCode);
							
							String manageCode = membCodeMap.get("manage_code");
							//微公社用户标记
							if("SI2011".equals(manageCode)){
								refereeInfo.setIsGroup("1");//是
							}else{
								refereeInfo.setIsGroup("0");//否
							}
						}
					}
					
					//昵称
					Map<String,String> nickMap = new HashMap<String,String>();
					nickMap.put("account_code_wo", accountCode);
					nickMap.put("account_code_ta", reAcCode);
					nickMap.put("member_code", memberCode);
					refereeInfo.setNickName(NickNameHelper.getNickName(nickMap));
					
					//推荐人等级
					MDataMap gReMap=DbUp.upTable("gc_group_account").one("account_code",reAcCode);
					if(gReMap != null){
						String reAcLevel = gReMap.get("account_level");
						MDataMap gReLevelMap=DbUp.upTable("gc_group_level").one("level_code",reAcLevel);
						if(gReLevelMap != null ){
							String reLevelName = gReLevelMap.get("level_name");
							refereeInfo.setFridenLevel(reLevelName);
						}
					}
					
//					//本月消费
//					String reMonthConsumeMoney = GetMonthConsumeMoneyByAccountCode(reAcCode,nowYM);
//					refereeInfo.setMonthConsumeMoney(reMonthConsumeMoney);
//					
//					//总消费
//					String reTotalConsumeMoney = GetTotalConsumeMoneyByAccountCode(reAcCode);
//					refereeInfo.setTotalConsumeMoney(reTotalConsumeMoney);
					
					//筛选类型
					refereeInfo.setSelectionType(selectionType);
					refereeInfoList.add(refereeInfo);
				}
				
				//推荐人信息
				friendsListResult.setFriendsInfoList(refereeInfoList);
			}
			
		}else if("2".equals(selectionType)){
			//2:本月活跃
			HashMap tempYmTotalMap = new HashMap();
		    HashMap ymTotalMap = new HashMap();
		    List<Map<String, Object>> totalActiveLogList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> activeLogList = new ArrayList<Map<String, Object>>();
			MDataMap inMap=new MDataMap("account_code",accountCode,"flag_enable", "1","active_time",nowYM);
			
//			String totalAcSql="select DISTINCT(order_account_code) from gc_active_log where account_code=:account_code and order_account_code !=:account_code and left(active_time,7) = '"+nowYM+"'";
			String totalAcSql="select DISTINCT(ac.order_account_code) from gc_active_log ac inner join gc_member_relation re "
					+ "ON ac.order_account_code = re.account_code and ac.account_code=:account_code and ac.order_account_code !=:account_code  and left(ac.active_time,7)=:active_time ";
			totalActiveLogList=DbUp.upTable("gc_active_log").dataSqlList(totalAcSql, inMap);
			
//			String acSql="select DISTINCT(order_account_code) from gc_active_log where account_code=:account_code and order_account_code !=:account_code and left(active_time,7) = '"+nowYM+"'" + limitString;
			String acSql="select DISTINCT(ac.order_account_code) from gc_active_log ac inner join gc_member_relation re "
					+ "ON ac.order_account_code = re.account_code and ac.account_code=:account_code and ac.order_account_code !=:account_code  and left(ac.active_time,7)=:active_time "
					+ "order by re.create_time " + limitString;
			activeLogList=DbUp.upTable("gc_active_log").dataSqlList(acSql, inMap);
			
			//总条数
			if(totalActiveLogList != null && totalActiveLogList.size()>0){
				friendsListResult.setActiveFriendsNumber(String.valueOf(totalActiveLogList.size()));
				pageResults.setTotal(Integer.valueOf(totalActiveLogList.size()));
			}else{
				pageResults.setTotal(0);
			}
			
			//返回的条数
			pageResults.setCount(activeLogList==null?0:activeLogList.size());

			//判断是否还有更多数据
			pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
							.getCount()) < pageResults.getTotal() ? 1 : 0);
			
			//账户编号
			friendsListResult.setAccountCode(accountCode);
			
			List<AccountFridensInfo> friendsYmInfoList=new ArrayList<AccountFridensInfo>();
			if(activeLogList != null && activeLogList.size()>0){
				for(int l=0;l<activeLogList.size();l++){
					String orderAccountCode = String.valueOf(activeLogList.get(l).get("order_account_code"));
					MDataMap ymMap=new MDataMap("account_code",accountCode,"order_account_code",orderAccountCode);
					String acYMSql = "select order_account_code,relation_level,ifnull(sum(consume_money),0) as monthMoney from gc_active_log"
							+ " where account_code=:account_code and order_account_code =:order_account_code and left(active_time,7) = '"+nowYM+"'"
							+ " group by order_account_code ";
					
					String acSumSql = "select order_account_code,relation_level,ifnull(sum(consume_money),0) as totalMoney from gc_active_log"
							+ " where account_code=:account_code and order_account_code =:order_account_code "
							+ " group by order_account_code ";
					
					List<Map<String, Object>> acYMList = DbUp.upTable("gc_active_log").dataSqlList(acYMSql, ymMap);
					
					List<Map<String, Object>> acSumList = DbUp.upTable("gc_active_log").dataSqlList(acSumSql, ymMap);
					//临时保存总消费额数据
					if(acSumList != null && acSumList.size()>0){
						String ymOrderAccountCode = String.valueOf(acSumList.get(0).get("order_account_code"));//好友
						String ymTotalConsumeMoney = String.valueOf(acSumList.get(0).get("totalMoney"));//月消费额
						tempYmTotalMap.put(ymOrderAccountCode, ymTotalConsumeMoney);
					}
					
					if(acYMList != null && acYMList.size() > 0){
						for(int y = 0; y<acYMList.size();y++){
							AccountFridensInfo ymInfo = new AccountFridensInfo();
							String ymOrderAccountCode = String.valueOf(acYMList.get(y).get("order_account_code"));
							String ymRelationLevel = String.valueOf(acYMList.get(y).get("relation_level"));
							String ymMonthMoney = String.valueOf(acYMList.get(y).get("monthMoney"));
							
							//如果是登录用户的一度好友，则消费金额必须加上一度好友的一度好友的消费金额
							String fridendMonthConsume = "0";
							float ymMonthMoneyf = Float.parseFloat(ymMonthMoney);
							//一度好友的总消费额
							String totalConsumeMoney = "0";
							if(tempYmTotalMap.get(ymOrderAccountCode) != null){
								totalConsumeMoney = String.valueOf(tempYmTotalMap.get(ymOrderAccountCode));
							}
							ymTotalMap.put(ymOrderAccountCode, totalConsumeMoney);
							if("1".equals(ymRelationLevel)){
								//查询一度好友的一度好友
								List<String> fridendsList = GetOneFridendsList(ymOrderAccountCode);
								//查询一度好友的一度好友的月消费额
								fridendMonthConsume = GetFridendMonthConsume(ymOrderAccountCode,fridendsList,nowYM);
								//月消费额 = 一度好友自己的月消费额 + 一度好友的一度好友的月消费额
								ymMonthMoneyf = Float.parseFloat(ymMonthMoney) + Float.parseFloat(fridendMonthConsume);
								
								//查询一度好友的一度好友的总消费额
								String fridendTotalConsume = GetFridendTotalConsume(ymOrderAccountCode,fridendsList);
								//总消费额 = 一度好友自己总消费额 + 一度好友的一度好友的总消费额
								float fAndOnefTotalConsume = Float.parseFloat(totalConsumeMoney) + Float.parseFloat(fridendTotalConsume);
								ymTotalMap.put(ymOrderAccountCode, fAndOnefTotalConsume);
							}
							
							//获取好友头像,用户编号,微公社用户标记
							String memberCode = "";
							MDataMap membCodeMapGp=DbUp.upTable("mc_member_info").one("account_code",ymOrderAccountCode,"manage_code","SI2011");
							if(membCodeMapGp != null){
								//用户编号
								memberCode = membCodeMapGp.get("member_code");
								ymInfo.setMemberCode(memberCode);
								//微公社用户标记
								ymInfo.setIsGroup("1");
								//根据reAcCode获取好友信息：头像
								String sql=" SELECT e.member_code,e.head_icon_url,e.nickname "
										+ " FROM membercenter.mc_extend_info_groupcenter e "
										+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ ymOrderAccountCode +"'";
								
								List<Map<String, Object>> aListMap=DbUp.upTable("mc_member_info").dataSqlList(sql, new MDataMap());
								int aCount = aListMap.size();
								
								if(aListMap != null && aCount > 0){
									String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));								
									//头像
									ymInfo.setHeadIconUrl(headIconUrl);
								}
							}else{
								MDataMap membCodeMap=DbUp.upTable("mc_member_info").one("account_code",ymOrderAccountCode);
								if(membCodeMap != null){
									memberCode = membCodeMap.get("member_code");
									ymInfo.setMemberCode(memberCode);
									String manageCode = membCodeMap.get("manage_code");
									//微公社用户标记
									if("SI2011".equals(manageCode)){
										ymInfo.setIsGroup("1");//是
									}else{
										ymInfo.setIsGroup("0");//否
									}
								}
							}
							
							//昵称
							Map<String,String> nickMap = new HashMap<String,String>();
							nickMap.put("account_code_wo", accountCode);
							nickMap.put("account_code_ta", ymOrderAccountCode);
							nickMap.put("member_code", memberCode);
							ymInfo.setNickName(NickNameHelper.getNickName(nickMap));
							
							//设定账户编号
							ymInfo.setTaAccountCode(ymOrderAccountCode);
							
							//好友关系级别
							ymInfo.setRelationLevel(ymRelationLevel);
							//好友等级
							MDataMap gAccountMap=DbUp.upTable("gc_group_account").one("account_code",ymOrderAccountCode);
							if(gAccountMap != null){
								String accountLevel = gAccountMap.get("account_level");
								MDataMap gLevelMap=DbUp.upTable("gc_group_level").one("level_code",accountLevel);
								if(gLevelMap != null ){
									String levelName = gLevelMap.get("level_name");
									ymInfo.setFridenLevel(levelName);
								}
							}
							
							//一度好友
							if("1".equals(ymRelationLevel)){
								//月消费额
								ymInfo.setMonthConsumeMoney(decimalFormat.format(ymMonthMoneyf));
								//总消费额
								ymInfo.setTotalConsumeMoney(decimalFormat.format(ymTotalMap.get(ymOrderAccountCode)));
							}else if("2".equals(ymRelationLevel)){
								//月消费额
								ymInfo.setMonthConsumeMoney(decimalFormat.format(Float.parseFloat(ymMonthMoney)));
								//总消费额
								ymInfo.setTotalConsumeMoney(decimalFormat.format(Float.parseFloat((String)tempYmTotalMap.get(ymOrderAccountCode))));
							}
							
							//筛选类型
							ymInfo.setSelectionType(selectionType);
							friendsYmInfoList.add(ymInfo);
						}
					}
				}
			}
			//好友信息
			friendsListResult.setFriendsInfoList(friendsYmInfoList);
			//分页信息
			friendsListResult.setPageResults(pageResults);
			
		}
		//根据级别(土豪、地主、富农、中农)筛选不做
		
		return friendsListResult;
	}
	
	/**
	 * 查询一度好友
	 * @param accountCode
	 * @return
	 */
	private List<String> GetOneFridendsList(String accountCode) {
		
		String sql1="SELECT re.parent_code,re.account_code FROM gc_member_relation re WHERE re.parent_code =:parent_code ";
		MDataMap map = new MDataMap();
		map.put("parent_code", accountCode);
		List<Map<String, Object>> oneFridenListMap=DbUp.upTable("gc_member_relation").dataSqlList(sql1, map);
		
		List<String> taOneAdd = new ArrayList<String>();
		if(oneFridenListMap != null && oneFridenListMap.size() > 0){
			for(int i = 0;i<oneFridenListMap.size();i++){
				//TA的所有一度好友
				taOneAdd.add("'"+oneFridenListMap.get(i).get("account_code")+"'");
			}
		}
		return taOneAdd;
	}

	/**
	 * 根据account_code获取总消费
	 * @param reAcCode
	 * @return
	 */
	private String GetTotalConsumeMoneyByAccountCode(String acCode) {
		String totalConsumeMoney = "0";
		StringBuffer totalSql = new StringBuffer();
		totalSql.append(" select account_code,IFNULL(sum(consume_money),0) as totalConsumeMoney ");
		totalSql.append(" from gc_active_log ");
		totalSql.append(" where account_code =:account_code ");
		MDataMap totalMap = new MDataMap();
		totalMap.put("account_code", acCode);
		List<Map<String, Object>> totalList=DbUp.upTable("gc_active_log").dataSqlList(totalSql.toString(), totalMap);
		
		if(totalList != null && totalList.size()>0){
			totalConsumeMoney = String.valueOf(totalList.get(0).get("totalConsumeMoney"));//总消费额
		}
		return totalConsumeMoney;
	}

	/**
	 * 根据account_code和年月获取月消费
	 * @param reAcCode
	 * @param nowYM
	 * @return
	 */
	private String GetMonthConsumeMoneyByAccountCode(String acCode,
			String nowYM) {
		String monthConsumeMoney = "0";
		StringBuffer monthSql = new StringBuffer();
		monthSql.append(" select account_code,IFNULL(sum(consume_money),0) as monthConsumeMoney ");
		monthSql.append(" from gc_active_log ");
		monthSql.append(" where account_code =:account_code ");
		monthSql.append(" and left(active_time,7) =:active_time ");
		MDataMap monthMap = new MDataMap();
		monthMap.put("account_code", acCode);
		monthMap.put("active_time", nowYM);
		
		List<Map<String, Object>> reMonthList=DbUp.upTable("gc_active_log").dataSqlList(monthSql.toString(), monthMap);
		
		if(reMonthList != null && reMonthList.size()>0){
			monthConsumeMoney = String.valueOf(reMonthList.get(0).get("monthConsumeMoney"));//月消费额
		}
		return monthConsumeMoney;
	}

	/**
	 * 获取手机号为默认昵称
	 * @return
	 */
	private String GetDefaultNickName(String memberCode) {
		
		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
		if(mUserMap != null){
			String loginName = mUserMap.get("login_name");
			if(StringUtils.isNotBlank(loginName)){
				return loginName.substring(0, 3) + "****" + loginName.substring(7);
			}
		}
		return "";
	}

	/**
	 * 个人主页信息
	 * @param accountCode
	 * @param inputParam
	 * @return
	 */
	public AccountPersonalHomepageResult ShowAccountPersonalHomepage(
			String accountCode, AccountPersonalHomepageInput inputParam) {
		AccountPersonalHomepageResult homepageResult = new AccountPersonalHomepageResult();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		//用户编号
		String memberCode = inputParam.getMemberCode();
		//筛选类型
		String selectionType = inputParam.getSelectionType();
		//好友关系级别 0:自己 1：一度好友 2：二度好友 -1：推荐人 -2：二度推荐人
		String relationLevel = inputParam.getRelationLevel();
		
		String taAccountCode = "";
		MDataMap mInfoMap = DbUp.upTable("mc_member_info").oneWhere("account_code", "", "", "member_code", memberCode);
		if(mInfoMap != null ){
			taAccountCode = mInfoMap.get("account_code");
		}
		//判断用户是否存在
		if(StringUtils.isBlank(taAccountCode)){
			homepageResult.setResultCode(918519024);
			homepageResult.setResultMessage(bInfo(918519024));
			return homepageResult;
		}
		
		String sysFormat = "yyyy-MM"; // 年/月
		SimpleDateFormat sFormat = new SimpleDateFormat(sysFormat);
		//系统时间
		java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
		//现在年月
		String nowYM = sFormat.format(timestamp);
		
		//设定用户编号
		homepageResult.setMemberCode(memberCode);
		
		//设定筛选类型
		homepageResult.setSelectionType(selectionType);
		
		//设定用户关系级别
		if(StringUtils.isBlank(relationLevel)){
			GroupCommonService comService = new GroupCommonService();
			int reLevel = comService.getRelationLevelByAccountCode(accountCode, taAccountCode);
			relationLevel = String.valueOf(reLevel);
		}
		homepageResult.setRelationLevel(relationLevel);
		
		//根据memberCode获取好友信息：头像，昵称，级别
		String sql=" SELECT e.head_icon_url,e.nickname "
				+ " FROM membercenter.mc_extend_info_groupcenter e "
				+ " WHERE e.member_code =:member_code";
		MDataMap mMap =  new MDataMap();
		mMap.put("member_code", memberCode);
		List<Map<String, Object>> aListMap=DbUp.upTable("mc_extend_info_groupcenter").dataSqlList(sql, mMap);
		int aCount = aListMap.size();
		if(aListMap != null && aCount > 0){
			String headIconUrl = String.valueOf(aListMap.get(0).get("head_icon_url"));
			//头像
			homepageResult.setHeadIconUrl(headIconUrl);
		}
		
		//昵称
		Map<String,String> nickMap = new HashMap<String,String>();
		nickMap.put("account_code_wo", accountCode);
		nickMap.put("account_code_ta", taAccountCode);
		nickMap.put("member_code", memberCode);
		homepageResult.setNickName(NickNameHelper.getNickName(nickMap));
		
		//等级
		MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
				"","", "", "account_code", taAccountCode);
		String sLevelCode = "";
		if (mGroupAccountMap != null){
			sLevelCode = mGroupAccountMap.get("account_level");
		}
		MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", sLevelCode);
		if(mLevelMap != null){
			homepageResult.setFridenLevel(mLevelMap.get("level_name"));
		}
		
		//手机号
		String moblieNo = "";
		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", memberCode);
		
		if("2".equals(relationLevel)){
			//二度好友主页，显示其推荐人的手机号
			MDataMap taRefereeMap = DbUp.upTable("gc_member_relation").one("account_code", taAccountCode);
			if(taRefereeMap != null){
				String taRefereeAccountCode = taRefereeMap.get("parent_code");
				
				String reSql="SELECT lg.member_code,lg.login_name FROM membercenter.mc_login_info lg INNER JOIN membercenter.mc_member_info mi "
						+ "ON lg.member_code = mi.member_code AND mi.account_code =:account_code";
				MDataMap reMobMap =  new MDataMap();
				reMobMap.put("account_code", taRefereeAccountCode);
				List<Map<String, Object>> reListMap=DbUp.upTable("mc_login_info").dataSqlList(reSql, reMobMap);
				if(reListMap != null && reListMap.size() > 0){
					moblieNo = String.valueOf(reListMap.get(0).get("login_name"));
				}
			}
		}else{
			if(mUserMap != null){
				moblieNo = mUserMap.get("login_name");
			}
		}
		
		//二度推荐人手机号码隐藏
		if("-2".equals(relationLevel)){
			if(StringUtils.isNotBlank(moblieNo)){
				moblieNo = moblieNo.substring(0, 3) + "****" + moblieNo.substring(7);
			}
		}

		homepageResult.setMoblieNo(moblieNo);
		
		//加入时间
		String joinTime = "";
		MDataMap reTimeMap = new MDataMap();
		if("0".equals(relationLevel)){
			//自己
			if(mUserMap != null){
				joinTime = mUserMap.get("create_time");
			}
		}else if("1".equals(relationLevel)){
			//一度好友
			reTimeMap = DbUp.upTable("gc_member_relation").one("account_code",taAccountCode,"parent_code",accountCode);
			if(reTimeMap != null){
				joinTime = reTimeMap.get("create_time");
			}
		}else if("2".equals(relationLevel)){
			//二度好友
			String re2TimeSql = " SELECT re.*  FROM gc_member_relation re WHERE re.account_code =:account_code AND re.parent_code ="
					+ " (SELECT re1.account_code FROM gc_member_relation re1 "
					+ " WHERE re1.account_code = (SELECT re2.parent_code "
					+ " FROM gc_member_relation re2 "
					+ " WHERE re2.account_code =:account_code) "
					+ " AND re1.parent_code =:parent_code) ";
			MDataMap re2DataMap = new MDataMap();
			re2DataMap.put("account_code", taAccountCode);
			re2DataMap.put("parent_code", accountCode);
			List<Map<String, Object>> re2TimeList=DbUp.upTable("gc_member_relation").dataSqlList(re2TimeSql, re2DataMap);
			if(re2TimeList != null && re2TimeList.size()>0){
				joinTime = String.valueOf(re2TimeList.get(0).get("create_time"));
			}
		}else if("-1".equals(relationLevel)){
			//推荐人
			reTimeMap = DbUp.upTable("gc_member_relation").one("account_code",accountCode,"parent_code",taAccountCode);
			if(reTimeMap != null){
				joinTime = reTimeMap.get("create_time");
			}
		}else if("-2".equals(relationLevel)){
			//二度推荐人
			String re2TimeSql = " SELECT re1.* FROM gc_member_relation re1 "
					+ " WHERE re1.account_code = (SELECT re2.parent_code  "
					+ " FROM gc_member_relation re2"
					+ " WHERE re2.account_code  =:account_code) "
					+ " AND re1.parent_code =:parent_code";
			MDataMap re2DataMap = new MDataMap();
			re2DataMap.put("account_code", accountCode);
			re2DataMap.put("parent_code", taAccountCode);
			List<Map<String, Object>> re2TimeList=DbUp.upTable("gc_member_relation").dataSqlList(re2TimeSql, re2DataMap);
			if(re2TimeList != null && re2TimeList.size()>0){
				joinTime = String.valueOf(re2TimeList.get(0).get("create_time"));
			}
		}
		homepageResult.setJoinTime(joinTime);
		
		//自己的个人主页信息
		if("0".equals(relationLevel)){
			if(StringUtils.isNotBlank(taAccountCode)){
				AccountInfoResultNew accountInfoResultNew=new AccountInfoResultNew();
				ApiAccountInfoNew api = new ApiAccountInfoNew();
				api.qryAccountInfo(accountInfoResultNew, memberCode);
				
				//下一级别名称
				String nextLevelName = accountInfoResultNew.getNextLevelName();
				homepageResult.setNextLevelName(nextLevelName);
				
				//升级下一级别要求消费金额
				String nextLevelConsume = accountInfoResultNew.getNextLevelConsume();
				homepageResult.setNextLevelConsume(nextLevelConsume);
				
				//升级下一级别要求好友数
				String nextLevelFriend = accountInfoResultNew.getNextLevelFriend();
				homepageResult.setNextLevelFriend(nextLevelFriend);
				
				//升级下一级别还需消费金额
				String nextLevelGapConsume = accountInfoResultNew.getNextLevelGapConsume();
				homepageResult.setNextLevelGapConsume(nextLevelGapConsume);
				
				//升级下一级别要求还需好友数
				int nextLevelGapFriend = accountInfoResultNew.getNextLevelGapFriend();
				homepageResult.setNextLevelGapFriend(String.valueOf(nextLevelGapFriend));
				
				//升级下一级别消费比例
				String nextLevelConsumePercent = accountInfoResultNew.getNextLevelConsumePercent();
				homepageResult.setNextLevelConsumePercent(nextLevelConsumePercent);
				
				//升级下一级别好友数比例
				String nextLevelFriendPercent = accountInfoResultNew.getNextLevelFriendPercent();
				homepageResult.setNextLevelFriendPercent(nextLevelFriendPercent);
				
				//当月活跃好友
				int activeFriend = accountInfoResultNew.getActiveFriend();
				homepageResult.setActiveFriend(String.valueOf(activeFriend));
				
				//当月消费金额
				String currentConsume = accountInfoResultNew.getCurrentConsume();
				homepageResult.setCurrentConsume(currentConsume);
				
				//查询一度好友
				String sql1f="SELECT re.parent_code,re.account_code FROM gc_member_relation re WHERE re.parent_code = '"
						+ taAccountCode +"' ";
				
				List<Map<String, Object>> oneFridenListMap=DbUp.upTable("gc_member_relation").dataSqlList(sql1f, new MDataMap());
				
				//一度好友人数
				int oneFridenCount = oneFridenListMap.size();
				homepageResult.setOneFridendsNumber(String.valueOf(oneFridenCount));
				List<String> taOneAdd = new ArrayList<String>();
				if(oneFridenListMap != null && oneFridenCount > 0){
					for(int i = 0;i<oneFridenCount;i++){
						//所有一度好友
						taOneAdd.add("'"+oneFridenListMap.get(i).get("account_code")+"'");
					}
				}
				
				//查询二度好友
				MDataMap twoFridenMap=new MDataMap("account_code",taAccountCode);
				String sql2f="select account_code from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1";
				List<Map<String, Object>> twoFridenList=DbUp.upTable("gc_member_relation").dataSqlList(sql2f, twoFridenMap);
				
				//二度好友人数
				int twoFridenCount = twoFridenList.size();
				homepageResult.setTwoFridendsNumber(String.valueOf(twoFridenCount));
				List<String> taTwoAdd = new ArrayList<String>();
				if(twoFridenList != null && twoFridenCount > 0){
					for(int i = 0;i<twoFridenCount;i++){
						//所有二度好友
						taTwoAdd.add("'"+twoFridenList.get(i).get("account_code")+"'");
					}
				}
				
				//一度好友月消费额
				homepageResult.setOneFridendsMonthConsumeMoney(GetFridendMonthConsume(taAccountCode,taOneAdd,nowYM));
				
				//二度好友月消费额
				homepageResult.setTwoFridendsMonthConsumeMoney(GetFridendMonthConsume(taAccountCode,taTwoAdd,nowYM));
				
//				//一度好友月返利
//				String oneFridendMonthRebate = "0";
//				oneFridendMonthRebate = GetFridendMonthRebate(taAccountCode,taOneAdd,nowYM);
//				
//				//二度好友月返利
//				String twoFridendMonthRebate = "0";
//				twoFridendMonthRebate = GetFridendMonthRebate(taAccountCode,taTwoAdd,nowYM);
				
				//本月返利=一度好友月返利+二度好友月返利
//				float monthRebateMoney = 0;
//				monthRebateMoney = Float.parseFloat(oneFridendMonthRebate) + Float.parseFloat(twoFridendMonthRebate);

				//本月返利
				String monthRebateMoney = GetAccountRebate(taAccountCode,nowYM);
				homepageResult.setMonthRebateMoney(monthRebateMoney);
				
//				//一度好友月预计返利
//				String oneFridendMonthExpectRebate = "0";
//				oneFridendMonthExpectRebate = GetFridendMonthExpectRebate(taAccountCode,taOneAdd,nowYM);
//				
//				//二度好友月预计返利
//				String twoFridendMonthExpectRebate = "0";
//				twoFridendMonthExpectRebate = GetFridendMonthExpectRebate(taAccountCode,taTwoAdd,nowYM);
//				
//				//本月预计返利=一度好友月预计返利+二度好友月预计返利
//				float monthExpectRebateMoney = 0;
//				monthExpectRebateMoney = Float.parseFloat(oneFridendMonthExpectRebate) + Float.parseFloat(twoFridendMonthExpectRebate);
				//本月预计返利
				String monthExpectRebateMoney = GetAccountExpectRebate(taAccountCode,nowYM);
				homepageResult.setMonthExpectRebateMoney(monthExpectRebateMoney);
				
				//总消费
				String taTotalConsume = GetAccountTotalConsume(taAccountCode);
				homepageResult.setTotalConsumeMoney(taTotalConsume);
				
				//总返利
				String taTotalRebate = GetAccountTotalRebate(taAccountCode);
				homepageResult.setTotalRebateMoney(taTotalRebate);
			}

		}else if("1".equals(relationLevel)){
			//一度好友的个人主页信息
			if(StringUtils.isNotBlank(taAccountCode)){
				//查询TA的一度好友
				String sql1="SELECT re.parent_code,re.account_code FROM gc_member_relation re WHERE re.parent_code = '"
						+ taAccountCode +"' ";
				
				List<Map<String, Object>> oneFridenListMap=DbUp.upTable("gc_member_relation").dataSqlList(sql1, new MDataMap());
				
				//TA的一度好友人数
				int oneFridenCount = oneFridenListMap.size();
				homepageResult.setTaOneFridendNumber(String.valueOf(oneFridenCount));
				List<String> taOneAdd = new ArrayList<String>();
				if(oneFridenListMap != null && oneFridenCount > 0){
					for(int i = 0;i<oneFridenCount;i++){
						//TA的所有一度好友
						taOneAdd.add("'"+oneFridenListMap.get(i).get("account_code")+"'");
					}
				}
				
				//TA的月消费额
				String taMonthConsume = GetTaMonthConsume(accountCode,taAccountCode,nowYM);
				homepageResult.setTaMonthConsumeMoney(taMonthConsume);
				
				//TA的一度好友月消费额
				String taOneFridendMonthConsume = GetFridendMonthConsume(accountCode,taOneAdd,nowYM);
				homepageResult.setTaOneFridendMonthConsumeMoney(taOneFridendMonthConsume);
				
				//本月消费=TA的月消费额+TA的一度好友月消费额
				float  monthConsumeMoney = 0;
				monthConsumeMoney = Float.parseFloat(taMonthConsume) + Float.parseFloat(taOneFridendMonthConsume);
				homepageResult.setMonthConsumeMoney(String.valueOf(decimalFormat.format(monthConsumeMoney)));
				
				//TA的总消费额
				String taTotalConsume = GetTaTotalConsume(accountCode,taAccountCode,nowYM);
				
				//TA的一度好友总消费额
				String taOneFridendTotalConsume = "0";
				taOneFridendTotalConsume = GetFridendTotalConsume(accountCode,taOneAdd);
				
				//总消费=TA的总消费额+TA的一度好友总消费额
				float totalConsumeMoney = 0;
				totalConsumeMoney = Float.parseFloat(taTotalConsume) + Float.parseFloat(taOneFridendTotalConsume);
				homepageResult.setTotalConsumeMoney(String.valueOf(decimalFormat.format(totalConsumeMoney)));
				
				//TA的本月返利
				String taMonthRebate = GetTaMonthRebate(accountCode,taAccountCode,nowYM);
				
				//TA的一度好友本月返利
				String taOneFridendMonthRebate = "0";
				taOneFridendMonthRebate = GetFridendMonthRebate(accountCode,taOneAdd,nowYM);
				
				//本月返利 =TA的本月返利+TA的一度好友本月返利
				float monthRebateMoney = 0;
				monthRebateMoney = Float.parseFloat(taMonthRebate) + Float.parseFloat(taOneFridendMonthRebate);
				String strMonthRebateMoney = String.valueOf(decimalFormat.format(monthRebateMoney));
				homepageResult.setMonthRebateMoney(strMonthRebateMoney);
				
				//TA的本月预计返利
				String taMonthExpectRebate = GetTaMonthExpectRebate(accountCode,taAccountCode,nowYM);
				
				//TA的一度好友本月预计返利
				String taOneFridendMonthExpectRebate = GetFridendMonthExpectRebate(accountCode,taOneAdd,nowYM);
				
				//本月预计返利 = TA的本月预计返利+TA的一度好友本月预计返利
				float monthExpectRebateMoney = 0;
				monthExpectRebateMoney = Float.parseFloat(taMonthExpectRebate) + Float.parseFloat(taOneFridendMonthExpectRebate);
				homepageResult.setMonthExpectRebateMoney(String.valueOf(decimalFormat.format(monthExpectRebateMoney)));
				
				//TA的总返利
				String taTotalRebate = GetTaTotalRebate(accountCode,taAccountCode,nowYM);
				
				//TA的一度好友总返利
				String taOneFridendTotalRebate = "0";
				taOneFridendTotalRebate = GetFridendTotalRebate(accountCode,taOneAdd);
				
				//总返利 =TA的总返利+TA的一度好友总返利
				float totalRebateMoney = 0;
				totalRebateMoney = Float.parseFloat(taTotalRebate) + Float.parseFloat(taOneFridendTotalRebate);
				String strTotalRebateMoney = String.valueOf(decimalFormat.format(totalRebateMoney));
				homepageResult.setTotalRebateMoney(strTotalRebateMoney);
			}
			
		}else if("2".equals(relationLevel)){
			//二度好友的个人主页信息
			if(StringUtils.isNotBlank(taAccountCode)){
				
				//TA的推荐人昵称
				MDataMap taRefereeMap = DbUp.upTable("gc_member_relation").one("account_code", taAccountCode);
				if(taRefereeMap != null){
					String taRefereeAccountCode = taRefereeMap.get("parent_code");
					String refereeSql=" SELECT e.member_code,e.head_icon_url,e.nickname "
							+ " FROM membercenter.mc_extend_info_groupcenter e "
							+ " INNER JOIN membercenter.mc_member_info m ON e.member_code = m.member_code AND m.manage_code = 'SI2011' AND m.account_code ='"+ taRefereeAccountCode +"'";
					
					List<Map<String, Object>> refereeListMap=DbUp.upTable("mc_member_info").dataSqlList(refereeSql, new MDataMap());
					int refereeCount = refereeListMap.size();
					if(refereeListMap != null && refereeCount > 0){
						String refereeMemberCode = String.valueOf(refereeListMap.get(0).get("member_code"));
						String taRefereeNickName = String.valueOf(refereeListMap.get(0).get("nickname"));
						//昵称
						if(StringUtils.isNotBlank(taRefereeNickName)){
							homepageResult.setTaRefereeNickName(taRefereeNickName);
						}else{
							//没有昵称时默认将手机号作为昵称
							homepageResult.setTaRefereeNickName(GetDefaultNickName(refereeMemberCode));
						}
					}else{
						//账户信息查不到时,直接获取账户的手机号作为昵称
						String reMemberCode = "";
						MDataMap refereeMembCodeMap=DbUp.upTable("mc_member_info").one("account_code",taRefereeAccountCode);
						if(refereeMembCodeMap != null){
							reMemberCode = refereeMembCodeMap.get("member_code");
							homepageResult.setTaRefereeNickName(GetDefaultNickName(reMemberCode));
						}
					}
				}
				
				//TA的本月消费
				String taMonthConsume = GetTaMonthConsume(accountCode,taAccountCode,nowYM);
				homepageResult.setTaMonthConsumeMoney(taMonthConsume);
				
				//TA的本月返利
				String taMonthRebate = GetTaMonthRebate(accountCode,taAccountCode,nowYM);
				homepageResult.setMonthRebateMoney(taMonthRebate);
				
				//TA的本月预计返利
				String taMonthExpectRebate = GetTaMonthExpectRebate(accountCode,taAccountCode,nowYM);
				homepageResult.setMonthExpectRebateMoney(taMonthExpectRebate);
				
				//TA的总消费
				String taTotalConsume = GetTaTotalConsume(accountCode,taAccountCode,nowYM);
				homepageResult.setTotalConsumeMoney(taTotalConsume);
				
				//TA的总返利
				String taTotalRebate = GetTaTotalRebate(accountCode,taAccountCode,nowYM);
				homepageResult.setTotalRebateMoney(taTotalRebate);
				
			}
			
		}else if("-1".equals(relationLevel) || "-2".equals(relationLevel)){
			//推荐人或是二度推荐人的个人主页信息
			if(StringUtils.isNotBlank(taAccountCode)){
				//一度好友人数
				homepageResult.setOneFridendsNumber(GetOneFriendNumByAccountCode(taAccountCode));
				
				//二度好友人数
				homepageResult.setTwoFridendsNumber(GetTwoFriendNumByAccountCode(taAccountCode));
			}
		}
		
		return homepageResult;
	}

	/**
	 * 账户总返利
	 * @param acCode
	 * @return
	 */
	private String GetAccountTotalRebate(String acCode) {
		
		String acTotalRebate = "0";
		String acTotalRebateSql = "select IFNULL(sum(abs(reckon_money)),0) as acTotalRebate "
				+ " from gc_reckon_log "
				+ " where account_code =:account_code "
				+ " and reckon_change_type = '4497465200030004' ";//转入提现账户
		
		MDataMap acTotalRebateMap = new MDataMap();
		acTotalRebateMap.put("account_code", acCode);
		
		List<Map<String, Object>> acTotalRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(acTotalRebateSql, acTotalRebateMap);
		if(acTotalRebateList != null && acTotalRebateList.size()>0){
			acTotalRebate = String.valueOf((acTotalRebateList.get(0).get("acTotalRebate")));
		}
		return acTotalRebate;
	}

	/**
	 * 账户总消费
	 * @param acCode
	 * @return
	 */
	private String GetAccountTotalConsume(String acCode) {
		
		String acTotalConsume = "0";
		String acTotalConsumeSql = "select IFNULL(sum(consume_money),0) as acTotalConsume "
				+ " from gc_active_log "
				+ " where account_code =:account_code ";
		
		MDataMap acTotalConsumeMap = new MDataMap();
		acTotalConsumeMap.put("account_code", acCode);
		
		List<Map<String, Object>> acTotalConsumeList=DbUp.upTable("gc_active_log").dataSqlList(acTotalConsumeSql, acTotalConsumeMap);
		if(acTotalConsumeList != null){
			acTotalConsume = String.valueOf((acTotalConsumeList.get(0).get("acTotalConsume")));
		}
		return acTotalConsume;
	}

	/**
	 * 账户本月返利
	 * @param acCode
	 * @param nowYM
	 * @return
	 */
	private String GetAccountRebate(String acCode, String nowYM) {
		
		String acMonthRebate = "0";
		StringBuffer acMonthRebateSql = new StringBuffer();
		acMonthRebateSql.append(" select IFNULL(sum(abs(reckon_money)),0) as monthRebate ");
		acMonthRebateSql.append(" from gc_reckon_log ");
		acMonthRebateSql.append(" where account_code =:account_code ");//登录用户的accountCode
		acMonthRebateSql.append(" and reckon_change_type = '4497465200030004' ");//转入提现账户
		acMonthRebateSql.append(" and left(order_reckon_time,7) =:order_reckon_time ");
		MDataMap acMonthMap = new MDataMap();
		acMonthMap.put("account_code", acCode);
		acMonthMap.put("order_reckon_time", nowYM);
		
		List<Map<String, Object>> fridendMonthList=DbUp.upTable("gc_reckon_log").dataSqlList(acMonthRebateSql.toString(), acMonthMap);
		if(fridendMonthList != null){
			acMonthRebate = String.valueOf((fridendMonthList.get(0).get("monthRebate")));
		}
		return acMonthRebate;
		
	}

	/**
	 * 账户本月预计返利
	 * @param acCode
	 * @param nowYM 
	 * @return
	 */
	private String GetAccountExpectRebate(String acCode, String nowYM) {
		
		String acMonthExpectRebate = "0";
		StringBuffer acMonthExpectRebateSql = new StringBuffer();
		acMonthExpectRebateSql.append(" select IFNULL(sum(rebate_money),0) as monthExpectRebate ");
		acMonthExpectRebateSql.append(" from gc_rebate_log ");
		acMonthExpectRebateSql.append(" where account_code =:account_code ");//登录用户的accountCode
		acMonthExpectRebateSql.append(" and rebate_change_type in ('4497465200140001','4497465200140002','4497465200140004') ");//订单预返利,取消订单预返利,转入提现账户
		acMonthExpectRebateSql.append(" and left(order_rebate_time,7) =:order_rebate_time ");
		acMonthExpectRebateSql.append(" and flag_status = 1 ");
		
		MDataMap acExpectMonthMap = new MDataMap();
		acExpectMonthMap.put("account_code", acCode);
		acExpectMonthMap.put("order_rebate_time", nowYM);
		List<Map<String, Object>> fridendMonthList=DbUp.upTable("gc_rebate_log").dataSqlList(acMonthExpectRebateSql.toString(), acExpectMonthMap);
		if(fridendMonthList != null){
			acMonthExpectRebate = String.valueOf((fridendMonthList.get(0).get("monthExpectRebate")));
		}
		return acMonthExpectRebate;
	}

	/**
	 * 好友总返利
	 * @param accountCode
	 * @param fridenAdd 好友列表
	 * @return
	 */
	private String GetFridendTotalRebate(String accountCode,
			List<String> fridenAdd) {
		
		String fridendTotalRebate = "0";
		StringBuffer fridendTotalRebateSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendTotalRebateSql.append(" select IFNULL(sum(abs(reckon_money)),0) as fridendTotalRebate ");
			fridendTotalRebateSql.append(" from gc_reckon_log ");
			fridendTotalRebateSql.append(" where account_code = '").append(accountCode).append("' ");//登录用户的accountCode
			fridendTotalRebateSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			fridendTotalRebateSql.append(" and reckon_change_type = '4497465200030004' ");
			List<Map<String, Object>> fridendTotalRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(fridendTotalRebateSql.toString(), new MDataMap());
			if(fridendTotalRebateList != null){
				fridendTotalRebate = String.valueOf((fridendTotalRebateList.get(0).get("fridendTotalRebate")));
			}
		}
		return fridendTotalRebate;
	}

	/**
	 * 好友总消费额
	 * @param accountCode
	 * @param fridenAdd 好友列表
	 * @return
	 */
	private String GetFridendTotalConsume(String accountCode,
			List<String> fridenAdd) {
		
		String fridendTotalConsume = "0";
		StringBuffer fridendTotalConsumeSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendTotalConsumeSql.append(" select IFNULL(sum(consume_money),0) as fridendTotalConsume ");
			fridendTotalConsumeSql.append(" from gc_active_log ");
			fridendTotalConsumeSql.append(" where account_code = '").append(accountCode).append("' ");//登录用户的accountCode
			fridendTotalConsumeSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			List<Map<String, Object>> fridendTotalConsumeList=DbUp.upTable("gc_active_log").dataSqlList(fridendTotalConsumeSql.toString(), new MDataMap());
			if(fridendTotalConsumeList != null){
				fridendTotalConsume = String.valueOf((fridendTotalConsumeList.get(0).get("fridendTotalConsume")));
			}
		}
		return fridendTotalConsume;
	}

	/**
	 * 好友月预计返利
	 * @param acCode
	 * @param fridenAdd 好友列表
	 * @param nowYM
	 * @return
	 */
	private String GetFridendMonthExpectRebate(String acCode,
			List<String> fridenAdd, String nowYM) {
		
		String fridendMonthExpectRebate = "0";
		StringBuffer fridendMonthExpectRebateSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendMonthExpectRebateSql.append(" select IFNULL(sum(rebate_money),0) as monthExpectRebate ");
			fridendMonthExpectRebateSql.append(" from gc_rebate_log ");
			fridendMonthExpectRebateSql.append(" where account_code = '").append(acCode).append("' ");//登录用户的accountCode
			fridendMonthExpectRebateSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//TA的所有一度好友
			fridendMonthExpectRebateSql.append(" and rebate_change_type in ('4497465200140001','4497465200140002','4497465200140004') ");//订单预返利,取消订单预返利,转入提现账户
			fridendMonthExpectRebateSql.append(" and left(order_rebate_time,7) = '").append(nowYM).append("' ");
			fridendMonthExpectRebateSql.append(" and flag_status = 1 ");
			
			List<Map<String, Object>> fridendMonthList=DbUp.upTable("gc_rebate_log").dataSqlList(fridendMonthExpectRebateSql.toString(), new MDataMap());
			if(fridendMonthList != null){
				fridendMonthExpectRebate = String.valueOf((fridendMonthList.get(0).get("monthExpectRebate")));
			}
		}
		return fridendMonthExpectRebate;
	}

	/**
	 * 好友月返利
	 * @param acCode
	 * @param fridenAdd 好友列表
	 * @param nowYM
	 * @return
	 */
	private String GetFridendMonthRebate(String acCode,
			List<String> fridenAdd, String nowYM) {
		
		String fridendMonthRebate = "0";
		StringBuffer fridendMonthRebateSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendMonthRebateSql.append(" select IFNULL(sum(abs(reckon_money)),0) as fridendMonthRebate ");
			fridendMonthRebateSql.append(" from gc_reckon_log ");
			fridendMonthRebateSql.append(" where account_code = '").append(acCode).append("' ");//登录用户的accountCode
			fridendMonthRebateSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			fridendMonthRebateSql.append(" and reckon_change_type = '4497465200030004' ");//转入提现账户
			fridendMonthRebateSql.append(" and left(order_reckon_time,7) = '").append(nowYM).append("' ");
			List<Map<String, Object>> fridendMonthList=DbUp.upTable("gc_reckon_log").dataSqlList(fridendMonthRebateSql.toString(), new MDataMap());
			if(fridendMonthList != null){
				fridendMonthRebate = String.valueOf((fridendMonthList.get(0).get("fridendMonthRebate")));
			}
		}
		return fridendMonthRebate;
	}

	/**
	 * 好友月消费额
	 * @param acCode
	 * @param fridenAdd 好友列表
	 * @param nowYM
	 * @return
	 */
	private String GetFridendMonthConsume(String acCode,
			List<String> fridenAdd, String nowYM) {
		String fridendMonthConsume = "0";
		StringBuffer fridendMonthConsumeSql = new StringBuffer();
		if(fridenAdd.size() > 0){
			fridendMonthConsumeSql.append(" select IFNULL(sum(consume_money),0) as fridendMonthConsume ");
			fridendMonthConsumeSql.append(" from gc_active_log ");
			fridendMonthConsumeSql.append(" where account_code = '").append(acCode).append("' ");
			fridendMonthConsumeSql.append(" and order_account_code in (").append(StringUtils.join(fridenAdd, ",")).append(") ");//好友
			fridendMonthConsumeSql.append(" and left(active_time,7) = '").append(nowYM).append("' ");
			List<Map<String, Object>> fridendMonthConsumeList=DbUp.upTable("gc_active_log").dataSqlList(fridendMonthConsumeSql.toString(), new MDataMap());
			if(fridendMonthConsumeList != null){
				fridendMonthConsume = String.valueOf((fridendMonthConsumeList.get(0).get("fridendMonthConsume")));
			}
		}
		return fridendMonthConsume;
	}

	/**
	 * 通过account_code获取二度好友人数
	 * @param acCode
	 * @return
	 */
	private String GetTwoFriendNumByAccountCode(String acCode) {
		String twoFridendNumber = "0";
		MDataMap twoFridenMap=new MDataMap("account_code",acCode);
		String twoFridensql="select count(account_code) as twoFriendNum from gc_member_relation where parent_code in (select account_code from gc_member_relation where parent_code=:account_code and flag_enable=1) and flag_enable=1";
		
		List<Map<String, Object>> twoFridenList=DbUp.upTable("gc_member_relation").dataSqlList(twoFridensql, twoFridenMap);
		if(twoFridenList != null){
			twoFridendNumber = String.valueOf(twoFridenList.get(0).get("twoFriendNum"));
		}
		return twoFridendNumber;
	}

	/**
	 * 通过account_code获取一度好友人数
	 * @param acCode
	 * @return
	 */
	private String GetOneFriendNumByAccountCode(String acCode) {
		String oneFridendNumber = "0";
		String oneFridendSql="SELECT re.parent_code,count(re.account_code) as oneFriendNum FROM gc_member_relation re WHERE parent_code = '" + acCode + "'";
		
		List<Map<String, Object>> oneFridendList=DbUp.upTable("gc_member_relation").dataSqlList(oneFridendSql, new MDataMap());
		int oneFriendCount = oneFridendList.size();
		if(oneFridendList != null && oneFriendCount > 0){
			oneFridendNumber = String.valueOf(oneFridendList.get(0).get("oneFriendNum"));
		}
		return oneFridendNumber;
	}

	/**
	 * TA的总返利
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaTotalRebate(String accountCode, String taAccountCode,
			String nowYM) {
		
		String taTotalRebate = "0";
		String taTotalRebateSql = "select IFNULL(sum(abs(reckon_money)),0) as taTotalRebate "
				+ " from gc_reckon_log "
				+ " where account_code = '" + accountCode + "' "
				+ " and order_account_code = '" + taAccountCode + "' " 
				+ " and reckon_change_type = '4497465200030004' "; //转入提现账户
			
		List<Map<String, Object>> taTotalRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(taTotalRebateSql, new MDataMap());
		if(taTotalRebateList != null){
			taTotalRebate = String.valueOf((taTotalRebateList.get(0).get("taTotalRebate")));
		}
		return taTotalRebate;
	}

	/**
	 * TA的总消费
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaTotalConsume(String accountCode, String taAccountCode,
			String nowYM) {
		
		String taTotalConsume = "0";
		String taTotalConsumeSql = "select IFNULL(sum(consume_money),0) as taTotalConsume "
				+ " from gc_active_log "
				+ " where account_code = '" + accountCode + "' "
				+ " and order_account_code = '" + taAccountCode + "' ";
			
		List<Map<String, Object>> taTotalConsumeList=DbUp.upTable("gc_active_log").dataSqlList(taTotalConsumeSql, new MDataMap());
		if(taTotalConsumeList != null){
			taTotalConsume = String.valueOf((taTotalConsumeList.get(0).get("taTotalConsume")));
		}
		return taTotalConsume;
	}

	/**
	 * TA的本月预计返利
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaMonthExpectRebate(String accountCode,
			String taAccountCode, String nowYM) {
		
		String taMonthExpectRebate = "0";
		String taMonthExpectRebateSql = "select IFNULL(sum(rebate_money),0) as taMonthExpectRebate "
				+ " from gc_rebate_log "
				+ " where account_code = '" + accountCode + "' "
				+ " and order_account_code = '" + taAccountCode + "' " 
				+ " and rebate_change_type in ('4497465200140001','4497465200140002','4497465200140004') " //订单预返利,取消订单预返利,转入提现账户
				+ " and flag_status = 1"
				+ " and left(order_rebate_time,7) = '" + nowYM +"' ";
			
		List<Map<String, Object>> taMonthExpectRebateList=DbUp.upTable("gc_rebate_log").dataSqlList(taMonthExpectRebateSql, new MDataMap());
		if(taMonthExpectRebateList != null){
			taMonthExpectRebate = String.valueOf((taMonthExpectRebateList.get(0).get("taMonthExpectRebate")));
		}
		return taMonthExpectRebate;
	}

	/**
	 * 获取TA的月返利
	 * @param accountCode
	 * @param taAccountCode
	 * @param nowYM
	 * @return
	 */
	private String GetTaMonthRebate(String accountCode, String taAccountCode,
			String nowYM) {
		
		String taMonthRebate = "0";
		String taMonthRebateSql = "select IFNULL(sum(abs(reckon_money)),0) as taMonthRebate "
				+ " from gc_reckon_log "
				+ " where account_code = '" + accountCode + "' "
				+ " and order_account_code = '" + taAccountCode + "' " 
				+ " and reckon_change_type = '4497465200030004' " //转入提现账户
				+ " and left(order_reckon_time,7) = '" + nowYM +"' ";
			
		List<Map<String, Object>> taMonthRebateList=DbUp.upTable("gc_reckon_log").dataSqlList(taMonthRebateSql, new MDataMap());
		if(taMonthRebateList != null){
			taMonthRebate = String.valueOf((taMonthRebateList.get(0).get("taMonthRebate")));
		}
		return taMonthRebate;
	}

	/**
	 * 获取TA的月消费
	 * @param nowYM 
	 * @param taAccountCode 
	 * @param accountCode 
	 * 
	 */
	private String GetTaMonthConsume(String accountCode, String taAccountCode, String nowYM) {
		
		String monthConsume = "0";
		String taMonthSql = "select IFNULL(sum(consume_money),0) as taMonthConsume "
				+ " from gc_active_log "
				+ " where account_code = '" + accountCode + "' "
				+ " and order_account_code = '" + taAccountCode + "' " 
				+ " and left(active_time,7) = '" + nowYM +"' ";
			
		List<Map<String, Object>> taMonthList=DbUp.upTable("gc_active_log").dataSqlList(taMonthSql, new MDataMap());
		if(taMonthList != null){
			monthConsume = String.valueOf((taMonthList.get(0).get("taMonthConsume")));
		}
		return monthConsume;
	}
}
