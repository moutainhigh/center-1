package com.cmall.groupcenter.unionpay;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.groupcenter.txservice.TxTraderWalletService;
import com.cmall.groupcenter.wallet.model.TraderWalletMoneyOptionModel;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;

/**
 * 银联支付回调
 * @author chenbin@ichsy.com
 *
 */
public class Notify extends BaseClass{

	public String process() {
		JSONObject resultObject = new JSONObject();
		MDataMap updateMap=new MDataMap();
		try {
			HttpServletRequest request=WebSessionHelper.create().upHttpRequest();
			ServletInputStream inputStream= request.getInputStream();
			
			String messageString = IOUtils.toString(inputStream,"UTF-8");
			//String messageString="{\"ordernum\": \"2014092012122421\",\"txntime\": \"2014-09-22 15:30:47\",\"accountname\": \"王薇薇\",\"bankaccount\": \"6013820500983405949\",\"txnamount\": 2.25,\"responsecode\": null,\"responsemsg\": null,\"settledate\": null}";
//			String messageString="{\"ordernum\": \"8888150812100004\",\"txntime\": \"2015-11-19 15:50:57\",\"accountname\": \"韩超b\",\"bankaccount\": \"6225881001761237\",\"txnamount\": 1.00,\"responsecode\": \"01\",\"responsemsg\": \"返回code为01\",\"settledate\": \"2015-11-19 15:43:57\"}";
			JSONObject dataObject=new JSONObject();
			dataObject=dataObject.parseObject(messageString);
			String ordernum = dataObject.get("ordernum").toString();
			String txntime = dataObject.get("txntime").toString();
			String bankaccount = dataObject.get("bankaccount").toString();
			String accountname = dataObject.get("accountname").toString();
			String txnamount = dataObject.get("txnamount").toString();
			String responsecode = dataObject.get("responsecode").toString();// 00// 表示扣费成功	// 01失败响应码
			String responsemsg = dataObject.get("responsemsg")==null?"":dataObject.get("responsemsg").toString();// 00时为空// 01失败信息响应信息
			String settledate = dataObject.get("settledate").toString();// Masget，银联处理时间清算日期
		
			try{
				if(!ordernum.startsWith("9999")){
					MDataMap logMap=DbUp.upTable("gc_pay_money_log").one("requestId",ordernum);
					MDataMap infoMap=DbUp.upTable("gc_pay_order_info").one("pay_order_code",logMap.get("pay_order_code"));
					
					updateMap.put("pay_order_code", logMap.get("pay_order_code"));
					updateMap.put("requestId", ordernum);
					updateMap.put("notify_code", responsecode);
					updateMap.put("notify_code_message", responsemsg);
					updateMap.put("notify_time", FormatHelper.upDateTime());
					updateMap.put("notify_data", messageString);
					if(logMap!=null&&logMap.size()>0&&StringUtils.isEmpty(logMap.get("notify_code"))){
						//扣费成功，更新成功
						if(responsecode.equals("00")){
							updateMap.put("pay_status", "4497465200070004");//支付成功
							updateMap.put("notify_code_message", bConfig("groupcenter.pay_success"));
							//push提现成功
							try {
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(infoMap.get("account_code"));
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010001");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle("您的提现到账啦");
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",infoMap.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
								
							    String content="您于"+infoMap.get("create_time").substring(0, 10)+"的提现申请已到账，金额:"+(new BigDecimal(infoMap.get("pay_money")).setScale(2,BigDecimal.ROUND_HALF_UP)).toString()+"元，请查收。如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
							    addSinglePushCommentInput.setContent(content);
								
								if(DbUp.upTable("gc_account_push_set").count("account_code",infoMap.get("account_code"),"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
								    addSinglePushCommentInput.setSendStatus("4497465000070001");    	
								}
								else{
									addSinglePushCommentInput.setSendStatus("4497465000070002");
								}
								SinglePushComment.addPushComment(addSinglePushCommentInput);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else{
							if(StringUtils.isNotBlank(responsecode)){
								updateMap.put("pay_status", "4497465200070003");//支付失败
								//支付失败，提现金额返回用户账户
								GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
								gcWithdrawLog.setUid(WebHelper.upUuid());
								gcWithdrawLog.setAccountCode(infoMap.get("account_code"));
								gcWithdrawLog.setMemberCode(infoMap.get("member_code"));
								gcWithdrawLog.setWithdrawMoney(new BigDecimal(infoMap.get("withdraw_money")));
								gcWithdrawLog.setWithdrawChangeType("4497465200040006");//提现单支付失败
								gcWithdrawLog.setChangeCodes(infoMap.get("pay_order_code"));
								TxGroupAccountService txGroupAccountService = BeansHelper
										.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
								List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
								listWithdrawLogs.add(gcWithdrawLog);
								txGroupAccountService.updateAccount(null, listWithdrawLogs);
								//支付信息明细对应表，撤回取消
								List<MDataMap> detaiList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",logMap.get("pay_order_code"));
								if(detaiList!=null&&detaiList.size()>0){
									for(MDataMap map:detaiList){
										MDataMap detailLog=DbUp.upTable("gc_reckon_log").one("order_code",map.get("order_code"),"account_code",infoMap.get("account_code"),"reckon_change_type","4497465200030001");
										MDataMap update=new MDataMap();
										update.put("order_code", detailLog.get("order_code"));
										update.put("zid", detailLog.get("zid"));
										update.put("payed_money", (new BigDecimal(detailLog.get("payed_money")).subtract(new BigDecimal(map.get("reckon_money")))).toString());
										DbUp.upTable("gc_reckon_log").dataUpdate(update, "payed_money", "zid");
									}
								}
								//push提现失败
								try {
									AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
									addSinglePushCommentInput.setAccountCode(infoMap.get("account_code"));
									addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
									addSinglePushCommentInput.setType("44974720000400010002");
									
									addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
									addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
									addSinglePushCommentInput.setTitle("非常抱歉，您于"+infoMap.get("create_time").substring(0, 10)+"的提现申请失败");
									MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",infoMap.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
									if(memberMap!=null){
										addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
									}
									
								    String content="请核对个人信息后重新申请，如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
								    addSinglePushCommentInput.setContent(content);
									
									if(DbUp.upTable("gc_account_push_set").count("account_code",infoMap.get("account_code"),"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
									    addSinglePushCommentInput.setSendStatus("4497465000070001");    	
									}
									else{
										addSinglePushCommentInput.setSendStatus("4497465000070002");
									}
									SinglePushComment.addPushComment(addSinglePushCommentInput);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
						
						if(StringUtils.isNotBlank(responsecode)){
							DbUp.upTable("gc_pay_money_log").dataUpdate(updateMap, "notify_code,notify_code_message,notify_time,pay_status,notify_data","requestId");//更新日志表
							DbUp.upTable("gc_pay_order_info").dataUpdate(updateMap, "pay_status", "pay_order_code");//更新用户付款单据表	
						    //更新付款单据日志表
							MDataMap payLog=new MDataMap();
						    payLog.inAllValues("pay_order_code",logMap.get("pay_order_code"),"order_status","4497153900120002","pay_status",updateMap.get("pay_status"),"update_time",FormatHelper.upDateTime());
							DbUp.upTable("gc_pay_order_log").dataInsert(payLog);
							
							//成功或者失败相应处理
							//
							resultObject.put("response", "00");
							resultObject.put("Message", "");
						}else{
							DbUp.upTable("gc_pay_money_log").dataUpdate(updateMap, "notify_code,notify_code_message,notify_time,notify_data","requestId");//更新日志表
							resultObject.put("response", "02");
							resultObject.put("Message", "not exist");
						}
					}
					else{
						resultObject.put("response", "02");
						resultObject.put("Message", "not exist");
					}
				}else{
					//微公社钱包支付--回调
					MDataMap logMap=DbUp.upTable("gc_wallet_pay_log").one("requestId",ordernum);
					
					if(logMap!=null&&logMap.size()>0&&StringUtils.isEmpty(logMap.get("notify_code"))){
						
						MDataMap infoMap=DbUp.upTable("gc_wallet_withdraw_info").one("withdraw_code",logMap.get("withdraw_code"));
						String traderCode = infoMap.get("trader_code");
						String accountCode = infoMap.get("account_code");
						String withdrawMoney= infoMap.get("withdraw_money");
						String memberCode=infoMap.get("member_code");
						
						updateMap.put("withdraw_code", logMap.get("withdraw_code"));
						updateMap.put("requestId", ordernum);
						updateMap.put("notify_code", responsecode);
						updateMap.put("notify_code_message", responsemsg);
						updateMap.put("notify_time", FormatHelper.upDateTime());
						updateMap.put("notify_data", messageString);
						updateMap.put("update_time", FormatHelper.upDateTime());
						//扣费成功，更新成功
						if(responsecode.equals("00")){
							updateMap.put("pay_status", "4497476000010005");//支付成功
							updateMap.put("withdraw_status", "4497476000010005");//提现成功
							updateMap.put("notify_code_message", bConfig("groupcenter.pay_success"));
						}else{
							if(StringUtils.isNotBlank(responsecode)){
								updateMap.put("pay_status", "4497476000010006");//支付失败
								updateMap.put("withdraw_status", "4497476000010006");//提现失败
								//支付失败，提现金额返回用户账户
						    	TxTraderWalletService txTraderWalletService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxTraderWalletService");
				                TraderWalletMoneyOptionModel traderWalletMoneyOptionModel = new TraderWalletMoneyOptionModel();
				                traderWalletMoneyOptionModel.setTraderCode(traderCode);
				                traderWalletMoneyOptionModel.setAccountCode(accountCode);
				                traderWalletMoneyOptionModel.setMoneyOccured(withdrawMoney);
				                traderWalletMoneyOptionModel.setChangeLogType("4497476000020004");//提现失败
				                traderWalletMoneyOptionModel.setRelationCode(logMap.get("withdraw_code"));
				                traderWalletMoneyOptionModel.setMemberCode(memberCode);
				                txTraderWalletService.doDeposit(traderWalletMoneyOptionModel);
							}

						}
						updateMap.put("pay_time", FormatHelper.upDateTime());//支付时间
						

						if(StringUtils.isNotBlank(responsecode)){
							//更新钱包支付日志表
							DbUp.upTable("gc_wallet_pay_log").dataUpdate(updateMap, "notify_code,notify_code_message,notify_time,pay_status,pay_time,notify_data","requestId");
							//更新用户付款单据表
							DbUp.upTable("gc_wallet_withdraw_info").dataUpdate(updateMap, "withdraw_status,update_time", "withdraw_code");
						    //更新付款单据日志表
							MDataMap payLog=new MDataMap();
						    payLog.inAllValues("withdraw_code",logMap.get("withdraw_code"),"withdraw_status",updateMap.get("pay_status"),"update_time",FormatHelper.upDateTime());
							DbUp.upTable("gc_wallet_withdraw_log").dataInsert(payLog);
							
							//成功相应处理
							resultObject.put("response", "00");
							resultObject.put("Message", "");
						}else{
							//更新钱包支付日志表
							DbUp.upTable("gc_wallet_pay_log").dataUpdate(updateMap, "notify_code,notify_code_message,notify_time,notify_data","requestId");
							resultObject.put("response", "02");
							resultObject.put("Message", "not exist");
						}
						
					}else{
						resultObject.put("response", "02");
						resultObject.put("Message", "not exist");
					}
					
				}
			}catch(Exception e){
				e.printStackTrace();
				resultObject.put("response","01");
				resultObject.put("Message", "handle error");
				if(!ordernum.startsWith("9999")){
				    updateMap.put("error_exception", "notify:"+e.getMessage());
				    updateMap.put("requestId", ordernum);
				    DbUp.upTable("gc_pay_money_log").dataUpdate(updateMap, "error_exception", "requestId");
				}else{
				    updateMap.put("error_exception", "notify:"+e.getMessage());
				    updateMap.put("requestId", ordernum);
				    DbUp.upTable("gc_wallet_pay_log").dataUpdate(updateMap, "error_exception", "requestId");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultObject.put("response","01");
			resultObject.put("Message", "handle error");
		}
		return resultObject.toString();
	}
	
	public static void main(String args[]){
		//String
//		Notify nt = new Notify();
//		nt.process();
		
	}
}
