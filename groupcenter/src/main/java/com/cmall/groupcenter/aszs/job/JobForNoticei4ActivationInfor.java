package com.cmall.groupcenter.aszs.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.aszs.RsyncNoticei4ActivationInfor;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;


/** 
* @ClassName: JobForNoticei4ActivationInfor 
* @Description: 统计爱思助手激活的用户并通知爱思助手
* @author 张海生
* @date 2016-3-7 下午4:08:44 
*  
*/
public class JobForNoticei4ActivationInfor extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.DAY_OF_MONTH, 1); 
		calendar1.add(Calendar.DATE, -1);
		
		String start = df.format(calendar.getTime()).toString();
		String end = df.format(calendar1.getTime()).toString();
		MDataMap whereMap = new MDataMap();
		whereMap.put("start", start);
		whereMap.put("end", end);
		whereMap.put("userStatus", "0");
		List<MDataMap> list = DbUp.upTable("fh_aisi_appdownload").queryAll(
				"zid,appid,mac,idfa,openudid,os,create_time", "",
				"create_time>=:start and create_time<=:end and user_status=:userStatus", whereMap);
		RsyncNoticei4ActivationInfor ra = new RsyncNoticei4ActivationInfor();
		ra.upRsyncRequest().setAisi("198565");
		ra.upRsyncRequest().setAisicid("200177");
		ra.upRsyncRequest().setRt("1");
		MDataMap updata = new MDataMap();
		for (MDataMap mDataMap : list) {
			String idfa = mDataMap.get("idfa");
			MDataMap cMap = DbUp.upTable("lc_client_info").oneWhere("create_time", "-create_time", "", "idfa",idfa);
			if(cMap != null){
				updata.put("user_status", "1");
				updata.put("activation_time", cMap.get("create_time"));
				updata.put("zid", mDataMap.get("zid"));
				int k = DbUp.upTable("fh_aisi_appdownload").dataUpdate(updata, "user_status,activation_time", "zid");
				if(k > 0){
					ra.upRsyncRequest().setAppid(mDataMap.get("appid"));
					ra.upRsyncRequest().setIdfa(mDataMap.get("idfa"));
					ra.upRsyncRequest().setMac(mDataMap.get("mac"));
					ra.upRsyncRequest().setOs(mDataMap.get("os"));
					ra.doRsync();
				}
			}
		}
	}

}
