package com.cmall.groupcenter.account.api;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.account.model.CreateRelationInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 微公社推广
 * @author chenbin@ichsy.com
 *
 */
public class ApiExtendMember extends RootApiForManage<RootResultWeb, CreateRelationInput>{

	public RootResultWeb Process(CreateRelationInput inputParam,
			MDataMap mRequestMap) {
		RootResultWeb rootResultWeb=new RootResultWeb();
		
		String parentMobile=inputParam.getParentLoginName();
		String mobile=inputParam.getLoginName();
		
		//判断上线下线手机号是否一样
		if(parentMobile.equals(mobile)){
			rootResultWeb.inErrorMessage(918506004);
		}
		if(rootResultWeb.upFlagTrue()){
		    //判断上线存不存在
		    if(DbUp.upTable("mc_login_info").count("login_name",parentMobile) == 0){
			    rootResultWeb.inErrorMessage(918506001);
		    }
		}
		if(rootResultWeb.upFlagTrue()){
			String parentMemberCode=DbUp.upTable("mc_login_info").one("login_name",parentMobile).get("member_code");
			String parentAccountCode=DbUp.upTable("mc_member_info").one("member_code",parentMemberCode).get("account_code");
			String sql="select create_time from gc_member_relation where parent_code=:parent_code order by create_time desc limit 1 ";
			List<Map<String, Object>> list=DbUp.upTable("gc_member_relation").dataSqlList(sql, new MDataMap("parent_code",parentAccountCode));
			//限定30秒只能绑定一个下线
			if(list!=null&&list.size()>0){
				String createTime=(String) list.get(0).get("create_time");
				if((DateHelper.parseDate(FormatHelper.upDateTime()).getTime()-DateHelper.parseDate(createTime).getTime())/1000<=5){
					rootResultWeb.inErrorMessage(918506002);
				}
				
			}
			if(rootResultWeb.upFlagTrue()){
			    GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
			    //下线存在
			    if(DbUp.upTable("mc_login_info").count("login_name",mobile) > 0){
				    //判断是否已绑定，未绑定进行绑定
				    String memberCode=DbUp.upTable("mc_login_info").one("login_name",mobile).get("member_code");
				    String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
				    MDataMap mDataMap=DbUp.upTable("gc_member_relation").one("account_code",accountCode);
				    if(mDataMap!=null){
					    if(mDataMap.get("parent_code").equals(parentAccountCode)){
						    rootResultWeb.inErrorMessage(918506003);
						    //已于该上线绑定
						    if(DbUp.upTable("gc_extend_member_record").count("parent_mobile",parentMobile,"mobile",mobile)==0){
							    DbUp.upTable("gc_extend_member_record").insert("parent_mobile",parentMobile,"mobile",mobile,
											"result_code","4497465200110002","result_message",TopUp.upLogInfo(918506005),"create_time",FormatHelper.upDateTime());
							}
					    }
					    else{
					    	//已于其他绑定
					    	if(DbUp.upTable("gc_extend_member_record").count("parent_mobile",parentMobile,"mobile",mobile)==0){
							    DbUp.upTable("gc_extend_member_record").insert("parent_mobile",parentMobile,"mobile",mobile,
											"result_code","4497465200110002","result_message",TopUp.upLogInfo(918506006),"create_time",FormatHelper.upDateTime());
							}
					    }
				    }
				    else{
				    	if(DbUp.upTable("gc_member_relation").count("parent_code",accountCode)==0){
				    		rootResultWeb.inOtherResult(groupAccountSupport.createRelation(accountCode, parentAccountCode, "", ""));
					        //绑定成功
						    if(rootResultWeb.upFlagTrue()){
						    	if(DbUp.upTable("gc_extend_member_record").count("parent_mobile",parentMobile,"mobile",mobile)==0){
								    DbUp.upTable("gc_extend_member_record").insert("parent_mobile",parentMobile,"mobile",mobile,
												"result_code","4497465200110001","result_message",TopUp.upLogInfo(918506007),"create_time",FormatHelper.upDateTime());
								}
						    }
				    	}
				    	else{
				    		rootResultWeb.inErrorMessage(918506003);
						    //有下线
						    if(DbUp.upTable("gc_extend_member_record").count("parent_mobile",parentMobile,"mobile",mobile)==0){
							    DbUp.upTable("gc_extend_member_record").insert("parent_mobile",parentMobile,"mobile",mobile,
											"result_code","4497465200110002","result_message",TopUp.upLogInfo(918506008),"create_time",FormatHelper.upDateTime());
							}
				    	}
					    
				    }
				
			    }
			    //下线不存在
			    else{
				    //创建下线用户
				    MemberLoginSupport memberLoginSupport=new MemberLoginSupport();
				    rootResultWeb.inOtherResult(memberLoginSupport.checkOrCreateUserByMobile(mobile, getManageCode()));
				    //绑定关系
				    if(rootResultWeb.upFlagTrue()){
					    String memberCode=DbUp.upTable("mc_login_info").one("login_name",mobile).get("member_code");
					    String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
					    rootResultWeb.inOtherResult(groupAccountSupport.createRelation(accountCode, parentAccountCode, "", ""));
				    }
				    //绑定成功
				    if(rootResultWeb.upFlagTrue()){
				    	if(DbUp.upTable("gc_extend_member_record").count("parent_mobile",parentMobile,"mobile",mobile)==0){
						    DbUp.upTable("gc_extend_member_record").insert("parent_mobile",parentMobile,"mobile",mobile,
										"result_code","4497465200110001","result_message",TopUp.upLogInfo(918506007),"create_time",FormatHelper.upDateTime());
						}
				    }
			    }
		    }
		}
		return rootResultWeb;
	}
	

}
