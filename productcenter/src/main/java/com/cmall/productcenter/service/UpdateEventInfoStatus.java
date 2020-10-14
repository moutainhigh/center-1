package com.cmall.productcenter.service;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 每天凌晨一点过滤 活动作废的数据
 * @author zhouguohui
 *
 */
public class UpdateEventInfoStatus extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		 List<MDataMap> list = DbUp.upTable("sc_event_info").queryAll("event_code", "", "(event_status='4497472700020002' or event_status='4497472700020004') and end_time<now()", new MDataMap());
		 
		 if(list!=null && list.size()>0){
			 for(MDataMap eventCode : list){
				 try{
					 MDataMap md = new MDataMap();
					 md.put("event_status", "4497472700020003");
					 md.put("event_code", eventCode.get("event_code"));
					 DbUp.upTable("sc_event_info").dataUpdate(md, "event_status", "event_code");
				 }catch(Exception e){
					 e.printStackTrace();
				 }finally{
					 PlusHelperNotice.onChangeEvent(eventCode.get("event_code"));
				 }
			 }
		 }
	
	}

}
