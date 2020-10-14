package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.model.TraderNotifyModel;
import com.cmall.groupcenter.service.TraderFoundsChangeLogService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.message.SendMessageBase;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.websupport.MessageSupport;


/**
 * 商户充值保证金的相关service
 *
 * @author lipengfei
 * @date 2015-6-23
 * email:lipf@ichsy.com
 *
 */
public class TxTraderFoundsService extends BaseClass{

	public void  doWithdraw(String traderUid,String withdrawMoney,String createUserCode){

		String createDate = CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss");

		MDataMap  traderInfo  = DbUp.upTable("gc_trader_info").oneWhere(null, null, null, "uid",traderUid);
		//账户当前的余额
		Object gurranteeBalance =traderInfo.get("gurrantee_balance");
		BigDecimal balance = new BigDecimal(String.valueOf(gurranteeBalance));

		//当前需要充值的保证金的金额
		BigDecimal founds = new BigDecimal(withdrawMoney);

		BigDecimal newBalance = balance.add(founds);

		MDataMap foundsChangeLog = new MDataMap();
		//记录日志
		foundsChangeLog.put("uid", WebHelper.upUuid());
		foundsChangeLog.put("trader_code", traderInfo.get("trader_code"));
		foundsChangeLog.put("account_code", traderInfo.get("account_code"));
		foundsChangeLog.put("gurrantee_balance_before",gurranteeBalance.toString());
		foundsChangeLog.put("gurrantee_change_amount",founds.toString());
		foundsChangeLog.put("gurrantee_balance_after",newBalance.toString());
		foundsChangeLog.put("create_user",createUserCode);
		foundsChangeLog.put("create_time",createDate);
		foundsChangeLog.put("REMARK","修改商户的充值保证金");
		//充值类型
		foundsChangeLog.put("CHANGE_TYPE","4497472500030002");

		MDataMap gc_trader_infoUpdate  = new MDataMap();
		gc_trader_infoUpdate.put("gurrantee_balance", newBalance.toString());
		gc_trader_infoUpdate.put("uid",traderUid);

		DbUp.upTable("gc_trader_info").dataUpdate(gc_trader_infoUpdate, "gurrantee_balance", "uid");

		new TraderFoundsChangeLogService().addFoundsChangeLog(foundsChangeLog);
		
		//修改预存款预警状态
		MDataMap pre=DbUp.upTable("gc_pre_withdraw_notify").one("trader_code",traderInfo.get("trader_code"));
		traderInfo  = DbUp.upTable("gc_trader_info").oneWhere(null, null, null, "uid",traderUid);
		if(pre!=null){
			pre.put("second_notify_flag", "0");
			pre.put("first_notify_flag", "0");
			pre.put("stop_rebate_notify_flag", "0");
			
			pre.put("sec_unenough_bal_notify_flag", "0");
			pre.put("first_unenough_bal_flag", "0");
			
			if(traderInfo.get("trader_status").equals("4497472500010002")){
				boolean is_stop=validateGurrantee(traderInfo,Integer.parseInt(pre.get("stop_rebate_notify")));
				//商户预存款余额
				BigDecimal gurrantee=new BigDecimal(traderInfo.get("gurrantee_balance"));
				//停止返利金额
				BigDecimal stopMoney=new BigDecimal(pre.get("sec_unenough_bal_notify"));
				if(!is_stop&&gurrantee.compareTo(stopMoney)==1){
					traderInfo.put("trader_status", "4497472500010001");
					String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050002").get("define_name");
					//插入状态日志
					DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010001",
							"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050002","reason_desc",reasonDesc);

					DbUp.upTable("gc_trader_info").update(traderInfo);
				}
			}
			
			DbUp.upTable("gc_pre_withdraw_notify").update(pre);
			
		}
		

	}

	//预存款预警提醒
	public void preWithdrawNotify() {
		List<MDataMap> preList=DbUp.upTable("gc_pre_withdraw_notify").queryAll("", "", "stop_rebate_notify_flag=0", new MDataMap());
		for(MDataMap pre:preList){
			MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("trader_code",pre.get("trader_code"));
			String stop_content=bConfig("groupcenter.wgs_deposit_balance_stop_notify");
			String notify_content=bConfig("groupcenter.wgs_deposit_balance_short_notify");
			String email_title=bConfig("groupcenter.wgs_deposit_balance_email_title");
			//提醒信息
			TraderNotifyModel notifyModel=new TraderNotifyModel();
			notifyModel.setTraderCode(pre.get("trader_code"));
			notifyModel.setTitle(email_title);
			
			//判断是否需要停止返利
			int notify_day=Integer.parseInt(pre.get("stop_rebate_notify"));
			if(traderInfo!=null&&traderInfo.get("trader_status").equals("4497472500010001")){
				boolean is_stop=validateGurrantee(traderInfo,notify_day);
				
				if(is_stop&&pre.get("stop_rebate_notify_open").equals("1")&&pre.get("sec_unenough_bal_notify_flag").equals("0")){
					
					String content=FormatHelper.formatString(stop_content, traderInfo.get("trader_name"));
					notifyModel.setContent(content);
					if(pre.get("send_message_flag").equals("1")){
						notifyModel.setPhone(true);
					}
					if(pre.get("send_mail_flag").equals("1")){
						notifyModel.setEmail(true);
					}
					sendInfo(notifyModel);
					
					pre.put("stop_rebate_notify_flag", "1");
					pre.put("second_notify_flag", "1");
					pre.put("first_notify_flag", "1");
					DbUp.upTable("gc_pre_withdraw_notify").update(pre);
					if(traderInfo.get("trader_status").equals("4497472500010001")){
						//停止返利
						traderInfo.put("trader_status", "4497472500010002");
						DbUp.upTable("gc_trader_info").update(traderInfo);
						String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050005").get("define_name");
						//插入状态日志
						DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010002",
								"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050005","reason_desc",reasonDesc);
					}
					
				}else{
					//判断是否需要第二次提醒
					notify_day=Integer.parseInt(pre.get("second_notify"));
					boolean is_second_notify=validateGurrantee(traderInfo,notify_day);
					String content=FormatHelper.formatString(notify_content, traderInfo.get("trader_name"),notify_day);
					if(is_second_notify&&pre.get("second_notify_flag").equals("0")&&pre.get("second_notify_open").equals("1")&&pre.get("second_notify_flag").equals("0")){
						notifyModel.setContent(content);
						if(pre.get("send_message_flag").equals("1")){
							notifyModel.setPhone(true);
						}
						if(pre.get("send_mail_flag").equals("1")){
							notifyModel.setEmail(true);
						}
						sendInfo(notifyModel);
						
						pre.put("second_notify_flag", "1");
						pre.put("first_notify_flag", "1");
						DbUp.upTable("gc_pre_withdraw_notify").update(pre);
					}else{
						//判断是否需要第一次提醒
						if(pre.get("first_notify_flag").equals("0")&&pre.get("first_notify_open").equals("1")&&pre.get("first_notify_flag").equals("0")){
							notify_day=Integer.parseInt(pre.get("first_notify"));
							boolean is_first_notify=validateGurrantee(traderInfo,notify_day);
							content=FormatHelper.formatString(notify_content, traderInfo.get("trader_name"),notify_day);
							if(is_first_notify){
								notifyModel.setContent(content);
								if(pre.get("send_message_flag").equals("1")){
									notifyModel.setPhone(true);
								}
								if(pre.get("send_mail_flag").equals("1")){
									notifyModel.setEmail(true);
								}
								sendInfo(notifyModel);
								
								pre.put("first_notify_flag", "1");
								DbUp.upTable("gc_pre_withdraw_notify").update(pre);
							}
						}
					}
				}
			}			
		}
	}
	
	//检测保证金余额是否需要预警
	public boolean validateGurrantee(MDataMap traderInfo,int day){
		String sSql="select SUM(gurrantee_change_amount) from gc_trader_founds_change_log where trader_code=:trader_code and "
				+ "change_type=4497472500030003 and create_time>:startTime and create_time<:endTime";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("trader_code", traderInfo.get("trader_code"));
		
		Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.addDays(new Date(), -1));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        Date nowTime=c.getTime();
        Date startTime=DateUtil.addDays(nowTime, -day);
		mWhereMap.put("startTime",DateUtil.toString(startTime, DateUtil.DATE_FORMAT_DATETIME));
		mWhereMap.put("endTime",DateUtil.toString(nowTime, DateUtil.DATE_FORMAT_DATETIME));
		Map<String,Object> deposit_val=DbUp.upTable("gc_trader_founds_change_log").dataSqlOne(sSql, mWhereMap);
		BigDecimal gurrantee_balance=new BigDecimal(traderInfo.get("gurrantee_balance"));
		BigDecimal gurrantee_use=BigDecimal.ZERO;
		if(deposit_val.get("SUM(gurrantee_change_amount)")!=null){
			gurrantee_use=new BigDecimal(deposit_val.get("SUM(gurrantee_change_amount)").toString());
		}
		if(gurrantee_balance.compareTo(gurrantee_use.negate())==-1){
			return true;
		}
		return false;
	}
	
	/**
	 * 预存款余额不足提醒(true:可返利，false:停止返利)
	 * @param traderCode
	 * @param gurranteeBalance
	 * @author panwei
	 * @return
	 */
	public boolean validatePreWithdrawShort(String traderCode,BigDecimal gurranteeBalance){
		MDataMap preInfo=DbUp.upTable("gc_pre_withdraw_notify").one("trader_code",traderCode);
		
		MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("trader_code",traderCode);
		String stop_content=bConfig("groupcenter.wgs_deposit_money_stop_notify");
		String notify_content=bConfig("groupcenter.wgs_deposit_money_short_notify");
		String email_title=bConfig("groupcenter.wgs_deposit_balance_email_title");
		//提醒信息
		TraderNotifyModel notifyModel=new TraderNotifyModel();
		notifyModel.setTraderCode(traderCode);
		notifyModel.setTitle(email_title);
		
		//判断是否需要停止返利
		BigDecimal notify_money=new BigDecimal(preInfo.get("sec_unenough_bal_notify"));
		
		if(traderInfo!=null){
			
			//如果商户已手动停用 继续按原来规则返利，但不在提醒
			if(traderInfo.get("trader_status").equals("4497472500010002")){
				return true;
			}
			//提醒开启则提醒   // fengl 修改已经发送短信 邮件提醒的不再发送提醒
			if(notify_money.compareTo(gurranteeBalance)!=-1&&preInfo.get("sec_unenough_bal_notify_open").equals("1")&&preInfo.get("sec_unenough_bal_notify_flag").equals("0")){
				
					String content=FormatHelper.formatString(stop_content, traderInfo.get("trader_name"));
					notifyModel.setContent(content);
					if(preInfo.get("send_message_flag").equals("1")){
						notifyModel.setPhone(true);
					}
					if(preInfo.get("send_mail_flag").equals("1")){
						notifyModel.setEmail(true);
					}
					sendInfo(notifyModel);
					preInfo.put("stop_rebate_notify_flag", "1");
					preInfo.put("second_notify_flag", "1");
					preInfo.put("first_notify_flag", "1");
					preInfo.put("sec_unenough_bal_notify_flag", "1");
					preInfo.put("first_unenough_bal_flag", "1");
					DbUp.upTable("gc_pre_withdraw_notify").update(preInfo);
					if(traderInfo.get("trader_status").equals("4497472500010001")){
						//停止返利
						traderInfo.put("trader_status", "4497472500010002");
						DbUp.upTable("gc_trader_info").update(traderInfo);
						String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050006").get("define_name");
						//插入状态日志
						DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010002",
								"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050006","reason_desc",reasonDesc);
					}
					return false;			
				
			}else{
				//判断是否需要提醒
				notify_money=new BigDecimal(preInfo.get("first_unenough_bal_notify"));
				String content=FormatHelper.formatString(notify_content, traderInfo.get("trader_name"),gurranteeBalance);
				// fengl 修改已经发送短信 邮件提醒的不再发送提醒
				if(notify_money.compareTo(gurranteeBalance)!=-1&&preInfo.get("first_unenough_bal_notify_open").equals("1")&&preInfo.get("first_unenough_bal_flag").equals("0")){
						notifyModel.setContent(content);
						if(preInfo.get("send_message_flag").equals("1")){
							notifyModel.setPhone(true);
						}
						if(preInfo.get("send_mail_flag").equals("1")){
							notifyModel.setEmail(true);
						}
						sendInfo(notifyModel);
						preInfo.put("first_unenough_bal_flag", "1");
						DbUp.upTable("gc_pre_withdraw_notify").update(preInfo);
					}
					
				
			}
			
			
		}
		return true;
	}
	
	/**
	 * 发送预存款提醒信息
	 * @param model
	 * @author panwei
	 */
	public void sendInfo(TraderNotifyModel model){
		//
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("trader_code", model.getTraderCode());
		List<MDataMap> notifyList=DbUp.upTable("gc_trader_notify_info").queryAll("", "", " trader_code=:trader_code", mWhereMap);

		MMessage message=new MMessage();
		
		SendMessageBase base=new SendMessageBase();
		message.setSendSource(base.upSendSourceByManageCode("SI2011"));
		message.setMessageContent(model.getContent());
		
		
		if(model.isPhone()){
			if(notifyList!=null){
				for(MDataMap notify:notifyList){
					String phone=notify.get("phone");
					if(phone!=null&&phone.length()>0){
						message.setMessageReceive(phone);
						MessageSupport.INSTANCE.sendMessage(message);
					}
				}
			}
			
		}
		if(model.isEmail()){
			if(notifyList!=null){
				for(MDataMap notify:notifyList){
					String email=notify.get("email");
					if(email!=null&&email.length()>0){
						MailSupport.INSTANCE.sendMail(email, model.getTitle(), model.getContent());
					}
				}
			}
		}
	}

}  
