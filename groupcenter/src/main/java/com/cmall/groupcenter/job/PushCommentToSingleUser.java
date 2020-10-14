package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.baidupush.AndroidPushBroadcastMessageSingle;
import com.cmall.groupcenter.baidupush.IosPushBroadcastMessageSingle;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * @author yangrong
 * 每分查询一次当前时间之前所有未推送消息成功的记录并发送
 * */
public class PushCommentToSingleUser extends RootJob {

	public void doExecute(JobExecutionContext context) {
		String now = DateUtil.getNowTime();
		String sql = "push_time <='"+now+"' and push_status='4497465000070001'";
 		List<MDataMap> list = DbUp.upTable("nc_comment_push_system").queryAll("", "push_time", sql, new MDataMap());
 		
 		AndroidPushBroadcastMessageSingle androidPush = new AndroidPushBroadcastMessageSingle();
 		IosPushBroadcastMessageSingle iosPush = new IosPushBroadcastMessageSingle();
		String apiKey = bConfig("groupcenter.apiKey");
		String secretKey = bConfig("groupcenter.secretKey");
		String deployStatus = bConfig("groupcenter.deployStatus");
 		
		for(MDataMap info : list){
			StringBuffer pushRtnMsg = new StringBuffer();//推送消息返回信息
			String jumpType = ""; 
			String jumpPosition = "";
			if(info.get("jump_type").equals("449747040001")){         //试用中心列表(惠美丽,小时代)
				jumpType="2";
				jumpPosition="6";
			}else if(info.get("jump_type").equals("449747040002")){    //限时抢购(惠美丽,小时代)
				jumpType="2";
				jumpPosition="7";
			}else if(info.get("jump_type").equals("449747040003")){    //活动详情(惠美丽,小时代)
				jumpType="4";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("449747040004")){    //免费试用商品详情(惠美丽,小时代)
				jumpType="5";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("449747040005")){    //帖子详情    (惠美丽,小时代)
				jumpType="6";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("449747040006")){    //付邮试用商品详情    (惠美丽,小时代)
				jumpType="7";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("449747040007")){    //首页(惠美丽,小时代)
				jumpType="2";
				jumpPosition="0";
			}else if(info.get("jump_type").equals("449747040008")){    //商品详情(惠美丽,小时代)
				jumpType="1";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("449747040009")){    //通知  (惠美丽,小时代)
				jumpType="0";
				jumpPosition="0";
			}else if(info.get("jump_type").equals("449747040010")){    //个人中心(惠美丽,小时代)
				jumpType="2";
				jumpPosition="8";
			}else if(info.get("jump_type").equals("449747040011")){     //限时抢购商品详情(惠美丽,小时代)
				jumpType="8";
				jumpPosition=info.get("jump_position");
			}
			String params = "\"ctype\":\""+jumpType+"\",\"cvalue\":\""+jumpPosition+"\"";
			
			if(info.get("app_code").equals("SI2007") ||info.get("app_code").equals("SI2013")){//惠美丽&小时代

				String userId = info.get("accept_member");
				//发送通知给安卓用户
				MWebResult mResult = androidPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey, secretKey, userId);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(mResult.getResultMessage());
				}
				//发送通知给IOS用户
				mResult = iosPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey, secretKey, Integer.parseInt(deployStatus),userId);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(";").append(mResult.getResultMessage());
				}
				
				if(pushRtnMsg.length()==0){
					//消息推送成功更新消息状态
					info.put("push_status", "4497465000070002");
					DbUp.upTable("nc_comment_push").update(info);
					pushRtnMsg.append(bInfo(918501010));
				}

			}
			
			//记录日志信息
			MDataMap log = new MDataMap();
			log.inAllValues("send_info",info.toString(),"return_info",pushRtnMsg.toString(),"time",DateUtil.getNowTime());
			DbUp.upTable("lc_pushcomment_log").dataInsert(log);
			
		}
	}
	
    public static void main(String[] args) {
    	PushCommentToSingleUser pushCommentToSingleUser =new PushCommentToSingleUser();
    	pushCommentToSingleUser.doExecute(null);
	}


}
