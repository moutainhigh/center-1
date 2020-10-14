package com.cmall.groupcenter.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 统计每天新增下线人数
 * @author chenbin@ichsy.com
 *
 */
public class CountSubordinateNumber extends RootJob{

	public void doExecute(JobExecutionContext context) {
		// TODO Auto-generated method stub
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String endDate=DateHelper.upDate(new Date(), DateHelper.CONST_PARSE_DATETIME).substring(0, 10);
        String beginDate="";
        Map timeMap=null;
        List gsclist=DbUp.upTable("gc_subordinate_count").dataQuery("create_time", " create_time desc ","", null,0,1);
        if(gsclist!=null&&gsclist.size()>0){
        	timeMap=(Map) gsclist.get(0);
        }

        if(timeMap==null||timeMap.get("create_time")==null){
        	Map map=DbUp.upTable("gc_member_relation").dataQuery("create_time", " create_time ", "", null,0,1).get(0);
        	if(map!=null&&map.get("create_time")!=null){
        		beginDate=map.get("create_time").toString();
        		
        	}
        }
        else{
        	beginDate=timeMap.get("create_time").toString();
        	Calendar begin=Calendar.getInstance();
    		try {
				begin.setTime(format.parse(beginDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		begin.add(Calendar.DAY_OF_MONTH, 1);
    		beginDate=DateHelper.upDate(begin.getTime());
        }
        beginDate=beginDate.substring(0, 10);
        
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
			start.setTime(format.parse(beginDate));
			end.setTime(format.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        while(start.before(end))
        {
        	MDataMap mDataMap=new MDataMap();
        	String dateString=DateHelper.upDate(start.getTime()).substring(0,10);
        	mDataMap.put("dateString", dateString);
        	List<MDataMap> list=DbUp.upTable("gc_member_relation").queryAll("parent_code,count(parent_code) as number ", "", " left(create_time,10)=:dateString group by parent_code", mDataMap);
            for(MDataMap map:list){
            	DbUp.upTable("gc_subordinate_count").insert("date",dateString,"account_code",map.get("parent_code"),
            			"number",map.get("number"),"create_time",FormatHelper.upDateTime());
            }
        	start.add(Calendar.DAY_OF_MONTH,1);
        }
	}
	

}
