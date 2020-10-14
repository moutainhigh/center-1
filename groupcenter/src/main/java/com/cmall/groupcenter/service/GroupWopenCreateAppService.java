package com.cmall.groupcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cmall.groupcenter.third.model.GroupMemberTraderRelDetail;
import com.cmall.groupcenter.third.model.GroupMemberTraderRelResult;
import com.cmall.groupcenter.third.model.GroupWopenCreateAppInput;
import com.cmall.groupcenter.third.model.GroupWopenCreateAppResult;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class GroupWopenCreateAppService extends BaseClass{
    
	/**
	 * 创建应用
	 * @param groupPayInput
	 * @param manageCode
	 * @return
	 */
	public GroupWopenCreateAppResult createApp(GroupWopenCreateAppInput inputParam,String manageCode){
		GroupWopenCreateAppResult Result=new GroupWopenCreateAppResult();
		MDataMap  appNameMap =new MDataMap();
		appNameMap.put("app_name", inputParam.getAppName());
		
	    MDataMap  mWhereMap =new MDataMap();
	    mWhereMap.put("app_code", manageCode);
	    
		List<Map<String,Object>> appMap=DbUp.upTable("gc_wopen_appmanage").dataQuery("", "","app_code=:app_code", mWhereMap, 0, 1); //获取商户code
		List<Map<String,Object>> appNameMaplist=new ArrayList<Map<String,Object>>();
		if(appMap!=null&&!appMap.isEmpty()){ //不为空
	  			Map maptest=new HashMap();
	  			maptest=appMap.get(0);
			    appNameMap.put("trade_code", maptest.get("trade_code").toString());
		  		appNameMaplist=DbUp.upTable("gc_wopen_appmanage").dataQuery("", "","trade_code=:trade_code and app_name=:app_name", appNameMap, 0, 1);
		  		if(null!=appNameMaplist&&!appNameMaplist.isEmpty()){
	      			Map map=appNameMaplist.get(0);
	      			String testApikey=map.get("test_apikey").toString();
	      			String testApipassword=map.get("test_apipassword").toString();
	      			String manageCodeTemp=map.get("app_code").toString();
	      			
	      			Result.setApikey(testApikey);
	      			Result.setApipassword(testApipassword);
	      			Result.setAppcode(manageCodeTemp);
	    			Result.inErrorMessage(918547001);//此名称的应用已存在
//	    			Result.setResultMessage("此名称的应用已存在");
		    
				}else{
				//记录请求
				    MDataMap appmap=new MDataMap();

		  			Map map=new HashMap();
		  			map=appMap.get(0);
		  			String manageCodeTemp=WebHelper.upCode("APPM");
		  			
		  			appmap.put("account_code",map.get("account_code").toString());
		  			appmap.put("login_code", map.get("login_code").toString());
		  			appmap.put("trade_code", map.get("trade_code").toString());
		  			appmap.put("app_name",inputParam.getAppName());
		  			appmap.put("app_description",inputParam.getAppDescription());
		  			appmap.put("create_time", DateUtil.getSysDateTimeString());
		  			
		  			String testApikey = UUID.randomUUID().toString().replace("-", "");
		  			String testApipassword = UUID.randomUUID().toString().replace("-", "");
		  			appmap.put("test_apikey", testApikey);
		  			appmap.put("test_apipassword", testApipassword);
		  			appmap.put("app_code",manageCodeTemp);
		  			appmap.put("service_status","4497473700010001");
		  			appmap.put("online_status","4497473700020002");
		  			appmap.put("verify_status","4497473700030003");
		  			
		  			DbUp.upTable("gc_wopen_appmanage").dataInsert(appmap);
		  			
		  			
		  			
					MDataMap mDataMapNew =new MDataMap();
					mDataMapNew.put("api_key", testApikey);
					mDataMapNew.put("api_pass", testApipassword);
					
					mDataMapNew.put("api_able", "com");
					mDataMapNew.put("remark", inputParam.getAppName());
					mDataMapNew.put("api_roles", "469923200004");
					mDataMapNew.put("manage_code", manageCodeTemp);
					
					
					DbUp.upTable("za_apiauthorize").dataInsert(mDataMapNew);
					
					Result.setApikey(testApikey);
					Result.setApipassword(testApipassword);
					Result.setAppcode(manageCodeTemp);
					Result.setResultCode(1);
					Result.setResultMessage("应用创建成功");	
			  		
			}
		}else{
			Result.inErrorMessage(918547002);
        
        }
  		return Result;
	}

	//查询用户与店铺的关系
	public GroupMemberTraderRelResult getMemberTraderRel(List<String> mobileList, String manageCode) {
		GroupMemberTraderRelResult result=new GroupMemberTraderRelResult();
		List<GroupMemberTraderRelDetail> detailList=new ArrayList<GroupMemberTraderRelDetail>();
		if(null!=mobileList&&mobileList.size()>0){
			//查询店铺accountCode
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			MDataMap traderMap=DbUp.upTable("mc_member_info").one("member_code",appMap.get("app_name"));
			String traderAccountCode=traderMap.get("account_code");
			//查询用户与店铺关系
			for(String mobile:mobileList){
				GroupMemberTraderRelDetail detail=new GroupMemberTraderRelDetail();
				detail.setMobile(mobile);
				MDataMap loginInfo=DbUp.upTable("mc_login_info").one("login_name",mobile);
				if(loginInfo!=null){
					MDataMap memberInfo=DbUp.upTable("mc_member_info").one("member_code",loginInfo.get("member_code"));
					//查询是否是一度好友
					MDataMap oneRelationInfo=DbUp.upTable("gc_member_relation").one("account_code",memberInfo.get("account_code"),
							"parent_code",traderAccountCode);
					if(oneRelationInfo!=null){
						detail.setRelation(1);
					}else{
						//查询是否是二度好友
						MDataMap inMap=new MDataMap("parent_code",traderAccountCode);
						inMap.put("account_code", memberInfo.get("account_code"));
						String sql="select * from gc_member_relation where account_code=:account_code and parent_code in (select account_code from gc_member_relation where parent_code=:parent_code) and flag_enable=1 ";
						Map<String, Object> twoRelMap=DbUp.upTable("gc_member_relation").dataSqlOne(sql, inMap);
						if(null!=twoRelMap){
							detail.setRelation(2);
						}else{
							detail.setRelation(0);
						}
					}
				}else{
					result.inErrorMessage(918534001,mobile);
					return result;
				}
				detailList.add(detail);
			}
			result.setResultCode(1);
			result.setRelationList(detailList);
		}
		
		return result;
	}
	
	
	
}
