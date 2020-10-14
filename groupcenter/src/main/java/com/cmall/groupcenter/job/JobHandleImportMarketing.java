package com.cmall.groupcenter.job;



import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.ctc.wstx.util.StringUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.MWebResult;

public class JobHandleImportMarketing extends RootJob{

	public void doExecute(JobExecutionContext context) {
		//System.out.println("start success start:"+new Date());
		List<MDataMap> list=DbUp.upTable("gc_account_marketing").query("", "", "flag_code=4497465200130001", new MDataMap(), 0, 100);
		MemberLoginSupport memberLoginSupport=new MemberLoginSupport();
		GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
		for(MDataMap mDataMap:list){
			MDataMap updateMap=new MDataMap();
			updateMap.put("zid", mDataMap.get("zid"));
			if(DbUp.upTable("gc_account_marketing").dataCount(" zid=:zid and (flag_code=4497465200130002 or flag_code=4497465200130003) ", updateMap)!=0){
				continue;
			}
			MWebResult result=new MWebResult();
			String mobile=mDataMap.get("mobileno");
			String parentMobile=mDataMap.get("parent_mobile");
			if(!RegexHelper.checkRegexField(mobile, "base=mobile")){
				result.inErrorMessage(918506012);
			}
			if(result.upFlagTrue()){
				if(parentMobile!=null&&parentMobile.length()>0){					
					if(DbUp.upTable("mc_login_info").count("login_name",parentMobile)==0){
						result.inErrorMessage(918506001);
					}
				}
			}
			if(result.upFlagTrue()){
				//下线存在，自动忽略
				if(DbUp.upTable("mc_login_info").count("login_name",mobile)!=0){
					//手机号存在，但要维护为小件员，进行判断
				    if(mDataMap.get("member_type")!=null&&mDataMap.get("member_type").equals("小件员")){
				        if(DbUp.upTable("gc_special_member").count("mobile",mobile)==0){
				        	String memberCode=DbUp.upTable("mc_login_info").one("login_name",mobile).get("member_code");
						    String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
				    	    DbUp.upTable("gc_special_member").insert("mobile",mobile,"account_code",accountCode,"member_type","4497465200120001","create_time",FormatHelper.upDateTime());
				    	    result.inErrorMessage(918506014);
				    	    updateMap.put("flag_code", "4497465200130002");//处理成功
				    	    updateMap.put("description", result.getResultMessage());
							DbUp.upTable("gc_account_marketing").dataUpdate(updateMap, "flag_code,description", "zid");
							String upSql="update gc_account_marketing_file_info set success_count=success_count+1,unsolved_count=unsolved_count-1 where batch_code=:batch_code";
							DbUp.upTable("gc_account_marketing_file_info").dataExec(upSql, new MDataMap("batch_code",mDataMap.get("batch_code")));
							continue;
				        }
				    }
					result.inErrorMessage(918506011);
				}
				else{
					//创建下线
					String appCode="";
					if(StringUtils.isBlank(mDataMap.get("extend_manage"))){
						result.inErrorMessage(918506013);
					}
					else {
						appCode=DbUp.upTable("uc_appinfo").one("app_name",mDataMap.get("extend_manage")).get("app_code");
						if(!appCode.contains("SI")){
							result.inErrorMessage(918506013);
						}
					}
					if(result.upFlagTrue()){
						result.inOtherResult(memberLoginSupport.checkOrCreateUserByMobile(mobile, appCode));
					}
				    
				    //正常创建，有上线手机号
				    if(result.upFlagTrue()){
				    	String memberCode=DbUp.upTable("mc_login_info").one("login_name",mobile).get("member_code");
					    String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
					    //记小件员
					    if(mDataMap.get("member_type")!=null&&mDataMap.get("member_type").equals("小件员")){
					        if(DbUp.upTable("gc_special_member").count("mobile",mobile)==0){
					    	    DbUp.upTable("gc_special_member").insert("mobile",mobile,"account_code",accountCode,"member_type","4497465200120001","create_time",FormatHelper.upDateTime());
					        }
					    }
				    	if(parentMobile.length()>0){
				    		String parentMemberCode=DbUp.upTable("mc_login_info").one("login_name",parentMobile).get("member_code");
							String parentAccountCode=DbUp.upTable("mc_member_info").one("member_code",parentMemberCode).get("account_code");	 
				    		result.inOtherResult(groupAccountSupport.createRelation(accountCode, parentAccountCode, "", ""));
				    	}
				    }
				}
			}
			
			if(DbUp.upTable("gc_account_marketing").dataCount(" zid=:zid and (flag_code=4497465200130002 or flag_code=4497465200130003) ", updateMap)==0){
				if (result.upFlagTrue()) {
					updateMap.put("flag_code", "4497465200130002");//处理成功
					DbUp.upTable("gc_account_marketing").dataUpdate(updateMap, "flag_code", "zid");
					String upSql="update gc_account_marketing_file_info set success_count=success_count+1,unsolved_count=unsolved_count-1 where batch_code=:batch_code";
					DbUp.upTable("gc_account_marketing_file_info").dataExec(upSql, new MDataMap("batch_code",mDataMap.get("batch_code")));
				}
				else {
					updateMap.put("flag_code", "4497465200130003");//处理失败
					updateMap.put("description", result.getResultMessage());
					DbUp.upTable("gc_account_marketing").dataUpdate(updateMap, "flag_code,description", "zid");
					String upSql="update gc_account_marketing_file_info set failed_count=failed_count+1,unsolved_count=unsolved_count-1 where batch_code=:batch_code";
					DbUp.upTable("gc_account_marketing_file_info").dataExec(upSql, new MDataMap("batch_code",mDataMap.get("batch_code")));
				}
			}
		}
		//System.out.println("handle end:"+new Date());
	}


}
