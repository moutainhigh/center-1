package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobRelSuperior extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("is_usable_send_link", "1");
		mWhereMap.put("flag_establishment_rel", "0");
		List<MDataMap> relDate = DbUp.upTable("gc_recommend_info").query("zid,uid,mobile,recommended_mobile", "link_usable_time", "is_usable_send_link = :is_usable_send_link  and" +
				"  flag_establishment_rel = :flag_establishment_rel", mWhereMap,0,100);
		for (MDataMap mDataMap : relDate) {
			String recommended_mobile = mDataMap.get("recommended_mobile");
			String parent_mobile = mDataMap.get("mobile");
			
			MDataMap one = DbUp.upTable("mc_login_info").one("login_name",recommended_mobile);
			MDataMap pone = DbUp.upTable("mc_login_info").one("login_name",parent_mobile);
			if(one != null){
				String member_code = one.get("member_code");
				String p_member_code = pone.get("member_code");//mc_member_info
				
				MDataMap acountCodeModel = DbUp.upTable("mc_member_info").one("member_code",member_code);
				MDataMap p_acountCodeModel = DbUp.upTable("mc_member_info").one("member_code",p_member_code);
				String accountCode = acountCodeModel.get("account_code");
				String parentAccountCode = p_acountCodeModel.get("account_code");
				int count = DbUp.upTable("gc_member_relation").count("account_code",accountCode);
				if(count == 0) {
					//添加上下级关系
//					DbUp.upTable("gc_member_relation").insert("account_code",accountCode,"parent_code",parentAccountCode,
//							"create_time",FormatHelper.upDateTime(),"flag_enable","1");
					GroupAccountSupport groupAccountSupport = new GroupAccountSupport();
					groupAccountSupport.createRelation(accountCode,parentAccountCode, "",FormatHelper.upDateTime());
					//标志推荐关系已经建立
					mDataMap.put("flag_establishment_rel", "1");
					mDataMap.put("establishment_rel_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_recommend_info").update(mDataMap);
				} else {
					//推荐关系失效
					mDataMap.put("flag_establishment_rel", "-1");
					mDataMap.put("establishment_rel_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_recommend_info").update(mDataMap);
				}
			}
			
		}
	}
	public static void main(String[] args) {
		JobRelSuperior test = new JobRelSuperior();
		test.doExecute(null);
	}
	
}
