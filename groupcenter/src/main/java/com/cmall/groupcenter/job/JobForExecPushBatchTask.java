package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.callapi.input.ApiBaiDuPushInput;
import com.srnpr.zapcom.basehelper.ALibabaJsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.ApiCallSupport;

/**
 * 批量发送PUSH推送通知
 */
public class JobForExecPushBatchTask extends RootJob {
	
	ApiCallSupport<ApiBaiDuPushInput, RootResult> apiCall = new ApiCallSupport<ApiBaiDuPushInput, RootResult>();

	public void doExecute(JobExecutionContext context) {
		List<MDataMap> taskList = DbUp.upTable("fh_push_batch_task").queryAll("task_code", "", "flag_success = 0 AND expire_time > NOW()", new MDataMap());
		final String sql = "SELECT GROUP_CONCAT(login_name) phones FROM `mc_login_info` WHERE manage_code = 'SI2003' AND member_code in (#memberCodes#)";
		
		for(MDataMap map : taskList) {
			List<MDataMap> detailList = DbUp.upTable("fh_push_batch_task_detail").queryAll("*", "","task_code = :task_code AND flag_success = 0 AND exec_number < 2", map);
			
			for(MDataMap detail : detailList) {
				RootResult rootResult = new RootResult();
				try {
					// 把逗号分割的用户编码增加单引号
					String execSql = sql.replace("#memberCodes#", "'" + detail.get("member_code").replaceAll(",{1,}", ",").replaceAll(",", "','") + "'");
					Map<String, Object> loginNameMap = DbUp.upTable("mc_login_info").dataSqlOne(execSql, new MDataMap());
					
					JSONObject payload = new JSONObject(detail.get("push_param").trim());
					String phones = (String)loginNameMap.get("phones");
					
					if(StringUtils.isNotBlank(phones)) {
						ApiBaiDuPushInput input = new ApiBaiDuPushInput();
						input.setToPage(payload.optString("toPage"));
						input.setMsgContent(payload.optString("msgContent"));
						input.setPhone(phones);
						rootResult = apiCall.doCallApiForPush(bConfig("familyhas.baidu_push_url"), 
								"com_cmall_familyhas_api_APIBaiDuPush", 
								"appfamilyhas", 
								"amiauhsnehnujiauhz",
								input, 
								new RootResult());
					}
				} catch (Exception e) {
					e.printStackTrace();
					rootResult.setResultCode(0);
					rootResult.setResultMessage(e+"");
					taskError(detail, e);
				}
				
				detail.put("push_result", ALibabaJsonHelper.toJson(rootResult));
				
				if(rootResult.getResultCode() == 1) {
					DbUp.upTable("fh_push_batch_task_detail").dataExec("update fh_push_batch_task_detail set flag_success = 1, exec_number = exec_number + 1, push_result = :push_result, update_time = now() where zid = :zid", detail);
				} else {
					DbUp.upTable("fh_push_batch_task_detail").dataExec("update fh_push_batch_task_detail set exec_number = exec_number + 1, push_result = :push_result, update_time = now() where zid = :zid", detail);
				}
			}
			
			// 如果没有未执行的任务则更新主任务状态为成功
			if(DbUp.upTable("fh_push_batch_task_detail").count("task_code", map.get("task_code"), "flag_success", "0") == 0) {
				DbUp.upTable("fh_push_batch_task").dataExec("update fh_push_batch_task set flag_success = 1,update_time = now() where task_code = :task_code", map);
			}
		}
	}
	
	private void taskError(MDataMap taskDetail, Exception e) {
		String notice = bConfig("zapweb.mail_notice").trim();
		if (StringUtils.isNotBlank(notice)) {
			MailSupport.INSTANCE.sendMail(notice, "JobForExecPushBatchTask", ExceptionUtils.getFullStackTrace(e));
		}
	}

}
