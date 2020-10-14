package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncSellerInfoToLD;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 把惠家有的商户导入到LD系统<br>
 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
 */
public class JobForPushSellerInfoToLD extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		// 只同步已经审核通过的普通商户
		List<MDataMap> dataList = DbUp.upTable("uc_sellerinfo").queryAll("small_seller_code,seller_company_name", "", "seller_status = '4497172300040004' AND small_seller_code != 'SF03150703100001' AND flow_status = '4497172300140005' AND ld_dlr_id = ''", null);
		RsyncSellerInfoToLD rsync = null;
		MDataMap pushlog;
		List<MDataMap> failedList = new ArrayList<MDataMap>();
		for(MDataMap map : dataList){
			rsync = new RsyncSellerInfoToLD();
			if(rsync.buildTRequest(map.get("small_seller_code")) != null){
				pushlog = DbUp.upTable("lc_rsync_push_sellerinfo_log").oneWhere("", "", "", "small_seller_code", map.get("small_seller_code"));
				// 未超过最大失败次数则继续同步
				if(pushlog == null || (NumberUtils.toInt(pushlog.get("error_count")) <  NumberUtils.toInt(pushlog.get("max_count")))){
					// 忽略曾经同步成功的商户
					if(pushlog!= null && "1".equals(pushlog.get("flag_success"))) continue;
					
					try {
						rsync.doRsync();
					} catch (Exception e) {
						rsync.upResponseObject().message = ExceptionUtils.getStackTrace(e);
					}
					
					if(pushlog == null){
						pushlog = new MDataMap();
						pushlog.put("small_seller_code", map.get("small_seller_code"));
						pushlog.put("create_time", FormatHelper.upDateTime());
						pushlog.put("error_count", "0");
						pushlog.put("max_count", "5");  // 失败次数限制
					}
					
					pushlog.put("update_time", FormatHelper.upDateTime());
					pushlog.put("remark", StringUtils.trimToEmpty(rsync.upResponseObject().message));
					pushlog.put("error_count", (NumberUtils.toInt(pushlog.get("error_count"), 0) + 1) + "");
					pushlog.put("flag_success", rsync.upResponseObject().success ? "1" : "0");
					if(rsync.upResponseObject().success){
						if(rsync.upResponseObject().result != null && rsync.upResponseObject().result.length > 0){
							pushlog.put("ld_dlr_id", rsync.upResponseObject().result[0].dlr_id);
						}
					}else{
						// 同步失败记录下来，最后发送一次通知邮件
						map.put("error_count", pushlog.get("error_count"));
						map.put("remark", pushlog.get("remark"));
						failedList.add(map);
					}
					
					if(pushlog.get("zid") == null){
						DbUp.upTable("lc_rsync_push_sellerinfo_log").dataInsert(pushlog);
					}else{
						DbUp.upTable("lc_rsync_push_sellerinfo_log").update(pushlog);
					}
					
				}
			}
		}

		// 发送失败邮件
		String[] mails = StringUtils.trimToEmpty(bConfig("groupcenter.push_seller_failed")).split(",");
		for(String mail : mails){
			if(StringUtils.isBlank(mail)) continue;
			
			StringBuilder builder = new StringBuilder();
			for(MDataMap map : failedList){
				if(builder.length() > 0) builder.append("<br>");
				builder.append("商户编号：").append(map.get("small_seller_code")).append("<br>");
				builder.append("商户名称：").append(map.get("seller_company_name")).append("<br>");
				builder.append("失败次数：").append(map.get("error_count")).append("<br>");
				builder.append("失败原因：").append(map.get("remark")).append("<br>");
			}
			
			if(!failedList.isEmpty()){
				MailSupport.INSTANCE.sendMail(StringUtils.trimToEmpty(mail), "同步商户到LD失败", builder.toString());
			}
		}
	}
}
