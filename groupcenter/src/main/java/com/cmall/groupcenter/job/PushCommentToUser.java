package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.baidupush.AndroidPushBroadcastMessage;
import com.cmall.groupcenter.baidupush.IosPushBroadcastMessage;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * @author dyc 
 * 每分查询一次当前时间之前所有未推送消息成功的记录并发送
 * */
public class PushCommentToUser extends RootJob {

	public void doExecute(JobExecutionContext context) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = df.format(new Date());
		String sql = "push_time <='"+now+"' and push_status='4497465000070001' and status='449747090001'";
 		List<MDataMap> list = DbUp.upTable("nc_comment_push").queryAll("", "push_time", sql, new MDataMap());
 		
 		AndroidPushBroadcastMessage androidPush = new AndroidPushBroadcastMessage();
		IosPushBroadcastMessage iosPush = new IosPushBroadcastMessage();
		String apiKey = bConfig("groupcenter.apiKey");
		String secretKey = bConfig("groupcenter.secretKey");
		String deployStatus = bConfig("groupcenter.deployStatus");
 		String host = bConfig("groupcenter.pushHost");
 		
 		String apiKey_SI2013 = bConfig("groupcenter.apiKey_SI2013");
		String secretKey_SI2013  = bConfig("groupcenter.secretKey_SI2013");
		String deployStatus_SI2013  = bConfig("groupcenter.deployStatus_SI2013");
 		
		for(MDataMap info : list){
			StringBuffer pushRtnMsg = new StringBuffer();//推送消息返回信息
			String jumpType = ""; 
			String jumpPosition = "";
			if(info.get("jump_type").equals("4497465000080001")){//首页
				jumpType="2";
				jumpPosition="0";
			}else if(info.get("jump_type").equals("4497465000080002")){//商品最终页
				jumpType="1";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("4497465000080003")){//电视TV
				jumpType="2";
				jumpPosition="1";
			}else if(info.get("jump_type").equals("4497465000080004")){//商品目录
				jumpType="2";
				jumpPosition="2";
			}else if(info.get("jump_type").equals("4497465000080005")){//订单列表
				jumpType="2";
				jumpPosition="7";
			}else if(info.get("jump_type").equals("4497465000080006")){//个人账户
				jumpType="2";
				jumpPosition="4";
			}else if(info.get("jump_type").equals("4497465000080007")){//微公社
				jumpType="2";
				jumpPosition="5";
			}else if(info.get("jump_type").equals("4497465000080008")){//活动详情页
				jumpType="4";
				jumpPosition=info.get("jump_position");
			}else if(info.get("jump_type").equals("4497465000080009")){//启动APP
				jumpType="3";
				jumpPosition="0";
			}else if(info.get("jump_type").equals("449747040001")){   //试用中心列表(惠美丽,小时代)
				jumpType="2";
				jumpPosition="6";
			}else if(info.get("jump_type").equals("449747040002")){    //限时抢购(惠美丽)
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
			}else if(info.get("jump_type").equals("449747040011")){     //限时抢购商品详情（惠美丽）
				jumpType="8";
				jumpPosition=info.get("jump_position");
			}
			String params = "\"ctype\":\""+jumpType+"\",\"cvalue\":\""+jumpPosition+"\"";
			
			if(info.get("app_code").equals("SI2007")){     //惠美丽

				//发送通知给安卓用户
				MWebResult mResult = androidPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey, secretKey);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(mResult.getResultMessage());
				}
				//发送通知给IOS用户
				mResult = iosPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey, secretKey, Integer.parseInt(deployStatus));
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(";").append(mResult.getResultMessage());
				}
				
				if(pushRtnMsg.length()==0){
					
						info.put("push_status", "4497465000070002");
						DbUp.upTable("nc_comment_push").update(info);
						pushRtnMsg.append(bInfo(918501010));
					
					
				}

			}else if(info.get("app_code").equals("SI2013")){ //小时代
				
				//发送通知给安卓用户
				MWebResult mResult = androidPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey_SI2013, secretKey_SI2013);
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(mResult.getResultMessage());
				}
				//发送通知给IOS用户
				mResult = iosPush.sendNotifyMsg(info.get("title"), info.get("comment"), params, apiKey_SI2013, secretKey_SI2013, Integer.parseInt(deployStatus_SI2013));
				if(mResult.getResultCode()!=1){
					pushRtnMsg.append(";").append(mResult.getResultMessage());
				}
				
				if(pushRtnMsg.length()==0){
					
						info.put("push_status", "4497465000070002");
						DbUp.upTable("nc_comment_push").update(info);
						pushRtnMsg.append(bInfo(918501010));
					
					
				}
				
			}else if(info.get("app_code").equals("SI2003")){//惠家友
				
				String content = info.get("comment");
				String pushTime = info.get("push_time").replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
				String url = host+"/52yungo/app/AddPushInfoApi?ctype="+jumpType+"&info="+content+"&pushtime="+pushTime+"&cvalue="+jumpPosition;
				String response = "";
				try {
					response = WebClientSupport.create().doGet(url);
					//消息推送成功更新消息状态
					info.put("push_status", "4497465000070002");
					DbUp.upTable("nc_comment_push").update(info);
					pushRtnMsg.append(bInfo(918501010));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					pushRtnMsg.append(response+"|"+e.getMessage());
				}
			}
			
			
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now2 = dt.format(new Date());
			//记录日志信息
			MDataMap log = new MDataMap();
			log.inAllValues("send_info",info.toString(),"return_info",pushRtnMsg.toString(),"time",now2);
			DbUp.upTable("lc_pushcomment_log").dataInsert(log);
			
		}
	}
	
}
