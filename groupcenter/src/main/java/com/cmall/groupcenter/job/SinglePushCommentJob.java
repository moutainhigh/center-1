package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.groupapp.api.SetMessageNotifactionStatusApi;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.cmall.groupcenter.func.CommentPushFunc;
import com.cmall.groupcenter.weixin.WebchatConstants;
import com.cmall.groupcenter.weixin.WeiXinUtil;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * @author dyc 
 * 百度推送
 * 每分查询一次当前时间之前所有未推送消息成功的记录并发送给指定用户
 * */
public class SinglePushCommentJob extends RootJob {

	public void doExecute(JobExecutionContext context) {
		String now = DateUtil.getNowTime();
		String sql = "pre_send_time <='"+now+"' and send_status='4497465000070001'";
 		//查出需要推送的数据
		List<MDataMap> list = DbUp.upTable("sc_comment_push_single").queryAll("", "pre_send_time", sql, new MDataMap());

 		for(MDataMap info : list){
 			if(StringUtility.isNotNull(info.get("user_code"))){
 				//查询该数据将要推送的用户是否关闭推送功能
				List<MDataMap> userInfo = DbUp.upTable("sc_user_push_info").queryByWhere("user_code",info.get("user_code"),"app_code",info.get("app_code"),"is_send", "449747100002");


				//--------------系统消息设置免打扰-----start
				//通过userCode 获取 accountCode
				MDataMap memberInfo=DbUp.upTable("mc_member_info").one("member_code",info.get("user_code"));
				String accountCode = null;
				String messagePushSet=null;

				//系统消息设置免打扰的类型
				String operationType=null;

				if (memberInfo!=null){
					accountCode = memberInfo.get("account_code");
					MDataMap accountPushSet=DbUp.upTable("gc_account_push_set").one("account_code",accountCode,"push_type_id", SetMessageNotifactionStatusApi.PUSH_TYPE_ID);
					if (accountPushSet!=null){
						messagePushSet = accountPushSet.get("push_type_onoff");
					}
				}

				if (StringUtils.isEmpty(messagePushSet)){
					//如果为空的话，就给默认值 。 即关闭免打扰。
					messagePushSet="449747100002";
				}

				if ("449747100001".equals(messagePushSet)){
					operationType="0";//开启免打扰
				}else if ("449747100002".equals(messagePushSet)){
					operationType="1";//关闭免打扰
				}
				//--------------系统消息设置免打扰-----end

                //如果开启了免打扰，不推送消息，但需要更新消息状态。否则不推送
                if("0".equals(operationType)){
                    //消息虽然没有推送，但是仍然需要更改消息的推送状态
                    info.put("send_status", "4497465000070002");
                    info.put("send_time", DateUtil.getNowTime());
                    DbUp.upTable("sc_comment_push_single").dataUpdate(info, "send_status,send_time", "uid");
                }else{
                    //消息免打扰处于关闭状态， 故而正常推送消息
                    if(userInfo==null||userInfo.size()<=0){//可向该用户推送信息
                        String apiKey = bConfig("groupcenter.apiKey_"+info.get("app_code"));
                        String secretKey = bConfig("groupcenter.secretKey_"+info.get("app_code"));
                        String deployStatus = bConfig("groupcenter.deployStatus_"+info.get("app_code"));
                        info.put("apiKey", StringUtility.isNull(apiKey)?"":apiKey);
                        info.put("secretKey", StringUtility.isNull(secretKey)?"":secretKey);
                        info.put("deployStatus", StringUtility.isNull(deployStatus)?"":deployStatus);


                        //info.get("properties")的格式为param1=xxx&param2=xxx,将其变成"param1":"xxx","param2":"xxx"这样的格式

//                    ---新添 --  加入operationType  即系统消息免打扰的开启与否字段
                        String newProperties = info.get("properties").toString()+"&operationType="+operationType;

                        String params = "\""+newProperties.replaceAll("=", "\":\"").replaceAll("&", "\",\"")+"\"";
                        info.put("params", params.equals("\"\"")?"":params);

                        //查出对应的userId和channelId
                        String lc_sql = "select s.push_token,c.os from lc_startPageLS s,lc_client_info c where s.sqNum=c.order_code and s.buyer_code='"+info.get("user_code")+"' and s.seller_code='"+info.get("app_code")+"' and s.flag='1' order by s.update_time desc";
                        List<Map<String, Object>> lcList = DbUp.upTable("lc_startPageLS").dataSqlList(lc_sql, new MDataMap());
                        //解析数据，取第一个同时拥有userId和channelId的数据
                        for(Map<String, Object> lc : lcList){
                            String pushToken = lc.get("push_token").toString();
                            if(StringUtility.isNotNull(pushToken)&&pushToken.contains("userId")&&pushToken.contains("channelId")){
                                String[] tmp = pushToken.split("&");
                                for(String s:tmp){
                                    String[] str = s.split("=");
                                    if(str.length==2){
                                        if(str[0].equals("userId")){
                                            info.put("userId", str[1]);
                                        }else if(str[0].equals("channelId")){
                                            info.put("channelId", str[1]);
                                        }
                                    }
                                }
                                info.put("os", lc.get("os").toString());
                                break;
                            }
                        }
                        CommentPushFunc.pushToSingleUser(info);

                        //向微信推送消息
                        try{
                            WeiXinUtil wxUtil=new WeiXinUtil();
                            MDataMap mBindMap = DbUp.upTable("mc_weixin_binding").one("member_code",info.get("user_code"),"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
                            if(mBindMap!=null&&StringUtility.isNotNull(info.get("content"))){
                                wxUtil.sendCustomMessage(WeiXinUtil.makeTextCustomMessage(mBindMap.get("open_id"), info.get("content")));
                            }
                        }catch(Exception e){

                        }


                    }else{
                        updateSendFlag(info);
                    }
                }
 			}else{
 				updateSendFlag(info);
 			}
		}
	}

	/**
	 * 将不符合条件的推送信息状态改为已发送
	 * */
	private void updateSendFlag(MDataMap info){
		info.put("send_status", "4497465000070002");
		info.put("send_time", DateUtil.getNowTime());
		DbUp.upTable("sc_comment_push_single").dataUpdate(info, "send_status,send_time", "uid");
	}
	
	public static void main(String[] args) {
//		AddSinglePushCommentInput inputParam = new AddSinglePushCommentInput();
//		inputParam.setTitle("11");
//		inputParam.setContent("222");
//		inputParam.setUserCode("333");
//		inputParam.setAppCode("SI2003");
//		SinglePushComment.addPushComment(inputParam);
		
		new SinglePushCommentJob().doExecute(null);
	}
}
