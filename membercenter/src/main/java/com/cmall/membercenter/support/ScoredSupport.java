package com.cmall.membercenter.support;

import java.util.List;
import java.util.Map;

import com.cmall.membercenter.memberdo.ScoredEnumer;
import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ScoredSupport extends BaseClass {

	/**
	 * 当修改资料时积分变动时调用该方法
	 * 
	 * @param sMemberInfo
	 * @param scoredEnumer
	 * @return
	 */
	public ScoredChange ChangeScored(String sMemberInfo,
			ScoredEnumer scoredEnumer) {
		
		ScoredChange scordChange = new ScoredChange();
		
		
		/*查询用户信息*/
		MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
		/*所属APP*/
		String app_code = bConfig("membercenter.member_default_appcode");
		/*查询等级表*/
		List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
		
		/*积分过程表*/
		MDataMap scodeDataMap = new MDataMap();
		
		/**MDataMap mWhereMap = new MDataMap();
		

		String sWhere = "where member_code='"+sMemberInfo+"' and rule_code='4497464900040004'";
		*/
		if(mDataMap!=null){
			/*查询是否有修改记录*/
		 List<MDataMap> scodeDataMaps =  DbUp.upTable("nc_integral_process").queryByWhere("member_code",sMemberInfo,"rule_code","4497464900040004","operation_code","459746500006");
		  
		  if(scodeDataMaps.size()==0){
			  
			  /*查询等级限制*/
			  MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500006","operation_enable","449746560001");
			  
			  if(aDataMap!=null){
			  
			  
			  /*首次修改，用户积分*/
			  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  /*如果积分需要升级，修改等级编号*/
			  for(MDataMap sMDataMap:map){
				  
				  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
					  
					  mDataMap.put("member_level", sMDataMap.get("level_code")); 
					  
				  }
				  
			  }
			  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
			  
			  scodeDataMap.put("create_time", FormatHelper.upDateTime());
			  
			  scodeDataMap.put("member_code", sMemberInfo);
			  
			  scodeDataMap.put("rule_code", "4497464900040004");
			  /*操作类型编号-完善资料*/
			  scodeDataMap.put("operation_code", "459746500006");
			  
			  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
			  
			  /*积分过程表*/
			  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
			  
			  /*用户积分*/
			  mDataMap.put("member_score",String.valueOf(score).toString());
			  /*用户累计积分*/
			  mDataMap.put("add_score", String.valueOf(add_score).toString());
			  
			  /*更新积分*/
			  DbUp.upTable("mc_extend_info_star").update(mDataMap);
			  
			  
			  /*查询用户等级信息*/
			  
			  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", sMemberInfo);
			
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
			
			 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
              scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
			  
			  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
			  
			  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
			  
			  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
			  
		  }
		  }
		  }
		}

		return scordChange;
	}
	/**
	 * 当首次登陆时获取的积分
	 * @param sMemberInfo
	 * @param scoredEnumer
	 * @return
	 */
	public ScoredChange FirstLandingScored(String sMemberInfo) {
		
		ScoredChange scordChange = new ScoredChange();
		
		/*查询用户信息*/
		MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
		/*所属APP*/
		String app_code = bConfig("membercenter.member_default_appcode");
		/*查询等级表*/
		List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
		
		/*积分过程表*/
		MDataMap scodeDataMap = new MDataMap();
		
		/**MDataMap mWhereMap = new MDataMap();
		

		String sWhere = "where member_code='"+sMemberInfo+"' and rule_code='4497464900040004'";
		*/
		if(mDataMap!=null){
			/*登陆时是否存在登陆记录*/
		 List<MDataMap> scodeDataMaps =  DbUp.upTable("nc_integral_process").queryByWhere("member_code",sMemberInfo,"rule_code","4497464900040004","operation_code","459746500005");
		  
		  if(scodeDataMaps.size()==0){
			  
			  /*查询等级限制*/
			  MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500005","operation_enable","449746560001");
			  
			  if(aDataMap!=null){
				  
			  
			  /*首次登陆后，用户积分*/
			  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  /*如果积分需要升级，修改等级编号*/
			  for(MDataMap sMDataMap:map){
				  
				  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
					  
					  mDataMap.put("member_level", sMDataMap.get("level_code")); 
					  
				  }
				  
			  }
			  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
			  
			  scodeDataMap.put("create_time", FormatHelper.upDateTime());
			  
			  scodeDataMap.put("member_code", sMemberInfo);
			  
			  scodeDataMap.put("rule_code", "4497464900040004");
			  /*操作类型编号-首次登陆*/
			  scodeDataMap.put("operation_code", "459746500005");
			  
			  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
			  
			  /*积分过程表*/
			  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
			  
			  /*用户积分*/
			  mDataMap.put("member_score",String.valueOf(score).toString());
			  /*用户累计积分*/
			  mDataMap.put("add_score", String.valueOf(add_score).toString());
			  
			  /*更新积分*/
			  DbUp.upTable("mc_extend_info_star").update(mDataMap);
			  
			  /*查询用户等级信息*/
			  
			  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", sMemberInfo);
			
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
			
			 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
				 scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
			  
			  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
			  
			  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
			  
			  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
			  
		  }
			  }
		  }
		}

		return scordChange;
	}
	
	/**
	 * 商品文字评论时获取的积分
	 * @param sMemberInfo
	 * @param scoredEnumer
	 * @return
	 */
	public ScoredChange reviewScored(String sMemberInfo,String order_code,String skuId) {
		
     ScoredChange scordChange = new ScoredChange();
		
		/*查询用户信息*/
		MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
		/*所属APP*/
		String app_code = bConfig("membercenter.member_default_appcode");
		/*查询等级表*/
		List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
		
		/*积分过程表*/
		MDataMap scodeDataMap = new MDataMap();
		
		if(mDataMap!=null){
		  
			 /*查询等级限制*/
			  MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500002","operation_enable","449746560001");
			
			  if(aDataMap!=null){
			  
			  /*用户文字评价*/
			  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  
			  /*如果积分需要升级，修改等级编号*/
			  for(MDataMap sMDataMap:map){
				  
				  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
					  
					  mDataMap.put("member_level", sMDataMap.get("level_code")); 
					  
				  }
				  
			  }
			  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
			  
			  scodeDataMap.put("create_time", FormatHelper.upDateTime());
			  
			  scodeDataMap.put("member_code", sMemberInfo);
			  
			  scodeDataMap.put("rule_code", "4497464900040005");
			  /*操作类型-文字评价*/
			  scodeDataMap.put("operation_code", "459746500002");
			  
			  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
			  
			  /*积分过程表*/
			  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
			  
			  /*用户积分*/
			  mDataMap.put("member_score",String.valueOf(score).toString());
			  /*用户累计积分*/
			  mDataMap.put("add_score", String.valueOf(add_score).toString());
			  
			  /*更新积分*/
			  DbUp.upTable("mc_extend_info_star").update(mDataMap);
			  
			  
			  /*查询用户等级信息*/
			  
			  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", sMemberInfo);
			
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
			
			 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
			  scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
			  
			  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
			  
			  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
			  
			  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
			 }
			  
			  }
		  }

		return scordChange;
	}

	/**
	 * 商品图片评论时获取的积分
	 * @param sMemberInfo
	 * @param scoredEnumer
	 * @return
	 */
	public ScoredChange reviewPhotoScored(String sMemberInfo,String order_code,String skuId) {
		
     ScoredChange scordChange = new ScoredChange();
		
		/*查询用户信息*/
		MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
		/*所属APP*/
		String app_code = bConfig("membercenter.member_default_appcode");
		/*查询等级表*/
		List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
		
		/*积分过程表*/
		MDataMap scodeDataMap = new MDataMap();
		
		if(mDataMap!=null){
			
			  /*查询等级限制*/
			  MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500003","operation_enable","449746560001");
		  
			  if(aDataMap!=null){
			  
			  /*用户图片评价*/
			  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
			  
			  
			  /*如果积分需要升级，修改等级编号*/
			  for(MDataMap sMDataMap:map){
				  
				  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
					  
					  mDataMap.put("member_level", sMDataMap.get("level_code")); 
					  
				  }
				  
			  }
			  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
			  
			  scodeDataMap.put("create_time", FormatHelper.upDateTime());
			  
			  scodeDataMap.put("member_code", sMemberInfo);
			  
			  scodeDataMap.put("rule_code", "4497464900040005");
			  /*操作类型-图片评价*/
			  scodeDataMap.put("operation_code", "459746500003");
			  
			  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
			  /*商品编号*/
			  scodeDataMap.put("evaluation_code", order_code);
			  
			  /*积分过程表*/
			  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
			  
			  /*用户积分*/
			  mDataMap.put("member_score",String.valueOf(score).toString());
			  /*用户累计积分*/
			  mDataMap.put("add_score", String.valueOf(add_score).toString());
			  
			  /*更新积分*/
			  DbUp.upTable("mc_extend_info_star").update(mDataMap);
			  
			  /*查询用户等级信息*/
			  
			  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", sMemberInfo);
			
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
			
			 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
			  scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
			  
			  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
			  
			  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
			  
			  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
			  
		  }
			  }
		}

		return scordChange;
	}
	
	/**
	 * 内容评论回复积分返回
	 * @param sMemberInfo
	 * @return
	 */
	public ScoredChange contentScored(String sMemberInfo,String info_code) {
		
	     ScoredChange scordChange = new ScoredChange();
			
			/*查询用户信息*/
			MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
			/*所属APP*/
			String app_code = bConfig("membercenter.member_default_appcode");
			/*查询等级表*/
			List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
			
			/*积分过程表*/
			MDataMap scodeDataMap = new MDataMap();
			
			if(mDataMap!=null){
				
				String time = FormatHelper.upDateTime().substring(0, 10);
				
				String sWhere = "member_code='"+sMemberInfo+"' and rule_code = '4497464900040001' and operation_code='459746500001' and LEFT(create_time,10)='"+time+"'";
				
				
				/*查询是否有评价记录*/
				 List<MDataMap> scodeDataMaps =  DbUp.upTable("nc_integral_process").queryAll("", "", sWhere, new MDataMap());
				
				 /*查询等级限制*/
				  MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500001","operation_enable","449746560001");
				 
				 if(aDataMap!=null){
				  
				 if(scodeDataMaps.size()<(Integer.valueOf(aDataMap.get("operation_limit"))/Integer.valueOf(aDataMap.get("operation_integral")))){
				  
			  
				  /*用户内容评价*/
				  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
				  
				  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
				  
				  /*如果积分需要升级，修改等级编号*/
				  for(MDataMap sMDataMap:map){
					  
					  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
						  
						  mDataMap.put("member_level", sMDataMap.get("level_code")); 
						  
					  }
					  
				  }
				  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
				  
				  scodeDataMap.put("create_time", FormatHelper.upDateTime());
				  
				  scodeDataMap.put("member_code", sMemberInfo);
				  
				  scodeDataMap.put("rule_code", "4497464900040001");
				  /*操作类型-评价*/
				  scodeDataMap.put("operation_code", "459746500001");
				  
				  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
				  
				  scodeDataMap.put("evaluation_code", info_code);
				  
				  /*积分过程表*/
				  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
				  
				  /*用户积分*/
				  mDataMap.put("member_score",String.valueOf(score).toString());
				  /*用户累计积分*/
				  mDataMap.put("add_score", String.valueOf(add_score).toString());
				  
				  /*更新积分*/
				  DbUp.upTable("mc_extend_info_star").update(mDataMap);
				  
				  /*查询用户等级信息*/
				  
				  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
					
					MDataMap  mberMap = new MDataMap();
					
					mberMap.put("member_code", sMemberInfo);
				
				    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
				
				 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
					
				  scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
				  
				  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
				  
				  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
				  
				  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
				  
			  }
			  }
			}
            
			}
				
			return scordChange;
		}

	
	
	/**
	 * 活动报名积分返回
	 * @param sMemberInfo
	 * @return
	 */
	public ScoredChange activitiesScored(String sMemberInfo,String info_code) {
		
	     ScoredChange scordChange = new ScoredChange();
			
			/*查询用户信息*/
			MDataMap mDataMap =DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
			/*所属APP*/
			String app_code = bConfig("membercenter.member_default_appcode");
			/*查询等级表*/
			List<MDataMap> map = DbUp.upTable("mc_member_level").queryByWhere("manage_code",app_code); 
			
			/*积分过程表*/
			MDataMap scodeDataMap = new MDataMap();
			
			if(mDataMap!=null){
				
				String time = FormatHelper.upDateTime().substring(0, 10);
				
				String sWhere = "member_code='"+sMemberInfo+"' and rule_code = '4497464900040001' and operation_code='459746500004' and LEFT(create_time,10)='"+time+"'";
				
				
				/*查询是否有评价记录*/
				 List<MDataMap> scodeDataMaps =  DbUp.upTable("nc_integral_process").queryAll("", "", sWhere, new MDataMap());
				
				 /*查询等级限制*/
				 MDataMap aDataMap = DbUp.upTable("nc_integral").one("operation_code","459746500004","operation_enable","449746560001");
				 
				 if(aDataMap!=null){
				  
				 if(scodeDataMaps.size()<(Integer.valueOf(aDataMap.get("operation_limit"))/Integer.valueOf(aDataMap.get("operation_integral")))){
				  
			  
				  /*用户内容评价*/
				  int score = Integer.valueOf(mDataMap.get("member_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
				  
				  int add_score = Integer.valueOf(mDataMap.get("add_score"))+Integer.valueOf(aDataMap.get("operation_integral"));
				  
				  /*如果积分需要升级，修改等级编号*/
				  for(MDataMap sMDataMap:map){
					  
					  if(score>Integer.valueOf(sMDataMap.get("need_scope"))){
						  
						  mDataMap.put("member_level", sMDataMap.get("level_code")); 
						  
					  }
					  
				  }
				  scodeDataMap.put("socre", aDataMap.get("operation_integral"));
				  
				  scodeDataMap.put("create_time", FormatHelper.upDateTime());
				  
				  scodeDataMap.put("member_code", sMemberInfo);
				  
				  scodeDataMap.put("rule_code", "4497464900040001");
				  /*操作类型-评价*/
				  scodeDataMap.put("operation_code", "459746500004");
				  
				  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
				  
				  scodeDataMap.put("evaluation_code", info_code);
				  
				  /*积分过程表*/
				  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
				  
				  /*用户积分*/
				  mDataMap.put("member_score",String.valueOf(score).toString());
				  /*用户累计积分*/
				  mDataMap.put("add_score", String.valueOf(add_score).toString());
				  
				  /*更新积分*/
				  DbUp.upTable("mc_extend_info_star").update(mDataMap);
				  
				  /*查询用户等级信息*/
				  
				  String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
					
					MDataMap  mberMap = new MDataMap();
					
					mberMap.put("member_code", sMemberInfo);
				
				    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
				
				 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
					
				  scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
				  
				  scordChange.setScore(Integer.valueOf(aDataMap.get("operation_integral")));
				  
				  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
				  
				  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
				  
			  }
			  }
			}
			}

			return scordChange;
		}
	/**
	 * 试用商品在线下单扣除用户积分
	 * @param sMemberInfo
	 * @return
	 */
	public boolean isNotScored(String sMemberInfo,int scord,String sku_code) {
		
		MDataMap mDataMap = DbUp.upTable("mc_extend_info_star").one("member_code",sMemberInfo);
		
		
		if(mDataMap!=null){
			
			try{
				
				int userScore = Integer.valueOf(mDataMap.get("member_score"));
				
				if(scord>userScore){
					
					return false;
					
				}else{
				
				
			/*扣除后的积分*/
			int deductionScored = userScore-scord;
				
			mDataMap.put("member_score",String.valueOf(deductionScored));
			/*更新用户积分*/
			DbUp.upTable("mc_extend_info_star").update(mDataMap);
			
			MDataMap scodeDataMap = new MDataMap();
			
			  scodeDataMap.put("socre", String.valueOf(scord));
			  
			  scodeDataMap.put("create_time", FormatHelper.upDateTime());
			  
			  scodeDataMap.put("member_code", sMemberInfo);
			  
			  scodeDataMap.put("process_code", WebHelper.upCode("DZ"));
			  
			  scodeDataMap.put("evaluation_code", sku_code);
			  
			  /*积分过程表*/
			  DbUp.upTable("nc_integral_process").dataInsert(scodeDataMap);
			
			return true;
			
			}
			}catch(Exception e){
				
				return false;
			}
			
		}else{
			return false;
		}
		
	}
	
	public MWebResult existsNickName(String nickName){
		
		MWebResult mWebResult = new MWebResult();
		
		MDataMap mDataMap = DbUp.upTable("mc_extend_info_star").one("nickname",nickName);
		
		if(mDataMap!=null){
			
			mWebResult.setResultCode(969905924);
			
			mWebResult.setResultMessage(bInfo(969905924));
		}
		
		return mWebResult;
		
	}	
	
	//判断用户是否被冻结
    public MWebResult FreezeAccounts(String mobile_phone, String memberCode){
		
		MWebResult mWebResult = new MWebResult();
		
		MDataMap mDataMap = DbUp.upTable("mc_extend_info_star").one("mobile_phone",mobile_phone,"app_code",memberCode,"status","449746600002");
		
		if(mDataMap!=null){
			
			mWebResult.setResultCode(934105106);
			
			mWebResult.setResultMessage(bInfo(934105106));
		}
		
		return mWebResult;
	 	
	 }
	
	
	
	/**
	 * 登录名是否重复
	 * @param loginName  用户名
	 * @return
	 * @author ligj
	 */
//	public MWebResult existsLoginName(String memberCode,String loginName){
//		
//		MWebResult mWebResult = new MWebResult();
//		
//		int count = DbUp.upTable("mc_login_info").count("login_name",loginName);
//		
//		if(count > 0){
//			mWebResult.setResultCode(969905925);				//该用户名或手机号已被注册
//			mWebResult.setResultMessage(bInfo(969905925));
//		}
//		return mWebResult;
//	}
    
	/**
	 * 昵称是否包含敏感词
	 * @param nickName  用户名
	 * @return
	 * @author yangrong
	 */
    public MWebResult existsSensitiveWord(String nickName){
		
		MWebResult mWebResult = new MWebResult();
		
		Boolean logo = true;
		
		// 查出敏感词库
		String datawhere = "SELECT * from nc_sensitive_word";
		List<Map<String, Object>> sensitiveList = DbUp.upTable("nc_sensitive_word").dataSqlList(datawhere, null);

		if (sensitiveList != null && sensitiveList.size() > 0) {
			for (int i = 0; i < sensitiveList.size(); i++) {
				// 包含敏感词不让修改
				if (nickName.contains(sensitiveList.get(i).get("sensitive_word").toString())) {

					logo = false;

				}
			}
		}
		
		if(!logo){
			
			mWebResult.setResultCode(969905020);
			
			mWebResult.setResultMessage(bInfo(969905020));
		}
		
		return mWebResult;
		
	}	
	
}
