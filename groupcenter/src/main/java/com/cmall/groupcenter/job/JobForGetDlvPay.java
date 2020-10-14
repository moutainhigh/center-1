package com.cmall.groupcenter.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetDlvPay;
import com.cmall.groupcenter.homehas.model.RsyncModelDlvPay;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 4.56.查询货到付款地区配置信息接口
 */
public class JobForGetDlvPay extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		RsyncGetDlvPay rsyncGetDlvPay = new RsyncGetDlvPay();
		if(!rsyncGetDlvPay.doRsync()) return;
		
		List<RsyncModelDlvPay> dlvPayList = rsyncGetDlvPay.upProcessResult().getResult();
		MDataMap mDataMap = null;
		String updateDime = FormatHelper.upDateTime();
		
		for(RsyncModelDlvPay dlvPay : dlvPayList) {
			if(StringUtils.isBlank(dlvPay.getSrgn_cd())){
				continue;
			}
			
			mDataMap = new MDataMap();
			mDataMap.put("lrgn_cd", StringUtils.trimToEmpty(dlvPay.getLrgn_cd()));
			mDataMap.put("mrgn_cd", StringUtils.trimToEmpty(dlvPay.getMrgn_cd()));
			mDataMap.put("srgn_cd", StringUtils.trimToEmpty(dlvPay.getSrgn_cd()));
			mDataMap.put("update_time", updateDime);
			
			MDataMap district = DbUp.upTable("sc_dlv_district").one("srgn_cd",mDataMap.get("srgn_cd"));
			if(district != null){
				// 已经存在则更新
				mDataMap.put("uid", district.get("uid"));
				mDataMap.put("zid", district.get("zid"));
				DbUp.upTable("sc_dlv_district").update(mDataMap);
			}else{
				// 没有则插入
				DbUp.upTable("sc_dlv_district").dataInsert(mDataMap);
			}
		}
		
		// 删除失效的
		DbUp.upTable("sc_dlv_district").dataExec("delete from sc_dlv_district where update_time != :update_time", new MDataMap("update_time", updateDime));
	}

}
