package com.cmall.groupcenter.jd.job;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

/**
 * 定时获取京东、考拉账户余额
 */
public class JobForCheckBalance extends RootJob{
	
	static Log log = LogFactory.getLog(JobForCheckBalance.class);

	// 京东余额不足的标识
	static final String NO_MONTY_JD = "noMoneyJD";
	// 考拉余额不足的标识
	static final String NO_MONTY_KL = "noMoneyKL";
	// 微信通知的标识
	static Map<String,Object> map = new HashMap<String, Object>();
	
	static BigDecimal defaultMoney = new BigDecimal(-1);
	
	@Override
	public void doExecute(JobExecutionContext context) {
		BigDecimal jdMoney = queryJingDongBalance();
		BigDecimal klMoney = queryKaolaBalance();
		
		// 余额不足通知
		noticeResultNoMoney(jdMoney,klMoney);
		
		// 余额充足时发送恢复的通知
		noticeResultRecover(jdMoney,klMoney);
		
		// 固定每天的余额通知
		noticeResultMoney(jdMoney,klMoney);
	}
	
	// 每天一次余额通知
	private void noticeResultMoney(BigDecimal jdMoney,BigDecimal klMoney) {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		
		String day = DateFormatUtils.format(cal.getTime(), "yyyy-MM-dd");
		
		// 两个余额都没取到时直接忽略
		if(defaultMoney.compareTo(jdMoney) == 0 && defaultMoney.compareTo(klMoney) == 0) {
			return;
		}
		
		// 余额通知的标识
		boolean jdFlag = false;
		boolean klFlag = false;
		
		// 每天在0点到10点之间通知一次
		if(hour >= 0 && hour < 10) {
			StringBuilder builder = new StringBuilder();
			
			if(defaultMoney.compareTo(jdMoney) != 0 && !map.containsKey(day+NO_MONTY_JD)) {
				builder.append("京东余额: "+jdMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
				jdFlag = true;
			} 
			
			if(defaultMoney.compareTo(klMoney) != 0 && !map.containsKey(day+NO_MONTY_KL)) {
				builder.append("考拉余额: "+klMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
				klFlag = true;
			}
			
			if(builder.length() > 0) {
				boolean success = sendWxMsg("余额通知", "当前剩余金额", builder.toString());
				
				// 通知成功则缓存下来，避免下次执行时重复通知
				if(success) {
					if(jdFlag) {
						map.put(day+NO_MONTY_JD, jdMoney);
					}
					
					if(klFlag) {
						map.put(day+NO_MONTY_KL, klMoney);
					}
				} else {
					log.warn("发送余额通知通知失败："+builder);
				}
			}
		}
	}
	
	// 检查是否余额不足，低于2000发送报警
	private void noticeResultNoMoney(BigDecimal jdMoney,BigDecimal klMoney) {
		// 余额不足的标识
		boolean jdFlag = false;
		boolean klFlag = false;
		
		StringBuilder builder = new StringBuilder();
		// 判断一下是否已经发送过通知，只发送一次通知
		if(!map.containsKey(NO_MONTY_JD) && jdMoney.compareTo(new BigDecimal(2000)) < 0 && jdMoney.compareTo(BigDecimal.ZERO) >= 0) {
			builder.append("京东余额: "+jdMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
			jdFlag = true;
		}
		
		// 判断一下是否已经发送过通知，只发送一次通知
		if(!map.containsKey(NO_MONTY_KL) && klMoney.compareTo(new BigDecimal(2000)) < 0 && klMoney.compareTo(BigDecimal.ZERO) >= 0) {
			builder.append("考拉余额: "+klMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
			klFlag = true;
		}
		
		if(builder.length() > 0) {
			builder.append("请尽快充值~");
			
			boolean success = sendWxMsg("余额通知", "余额不足", builder.toString());
			if(success) {
				if(jdFlag) {
					map.put(NO_MONTY_JD, jdMoney);
				}
				
				if(klFlag) {
					map.put(NO_MONTY_KL, klMoney);
				}
			} else {
				log.warn("发送余额不足通知失败："+builder);
			}
		}
	}
	
	// 余额充值完成的通知
	private void noticeResultRecover(BigDecimal jdMoney,BigDecimal klMoney) {
		// 余额恢复的标识
		boolean jdFlag = false;
		boolean klFlag = false;
		
		StringBuilder builder = new StringBuilder();
		// 判断一下是否已经发送过京东余额不足通知，余额大于指定金额时发送恢复通知
		if(map.containsKey(NO_MONTY_JD) && jdMoney.compareTo(new BigDecimal(5000)) > 0) {
			builder.append("京东余额: "+jdMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
			jdFlag = true;
		}
		
		// 判断一下是否已经发送过考拉余额不足通知，余额大于指定金额时发送恢复通知
		if(map.containsKey(NO_MONTY_KL) && klMoney.compareTo(new BigDecimal(5000)) > 0) {
			builder.append("考拉余额: "+klMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\r\n");
			klFlag = true;
		}
		
		if(builder.length() > 0) {
			boolean success = sendWxMsg("余额通知", "余额不足恢复", builder.toString());
			if(success) {
				if(jdFlag) {
					map.remove(NO_MONTY_JD);
				}
				
				if(klFlag) {
					map.remove(NO_MONTY_KL);
				}
			} else {
				log.warn("发送余额不足恢复通知失败："+builder);
			}
		}
	}
	
	private BigDecimal queryKaolaBalance() {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		// {"recMsg":"成功","balance":15471.21,"recCode":200}
		
		BigDecimal money = new BigDecimal(-1);
		String returnStr = RsyncKaoLaSupport.doPostRequest("queryAccountBalance", "channelId", treeMap);
		if(returnStr != null && returnStr.trim().startsWith("{")) {
			JSONObject obj = JSON.parseObject(returnStr);
			String balance = obj.getString("balance");
			
			if(balance != null) {
				money = new BigDecimal(balance);
			}
		}
		
		if(money.compareTo(defaultMoney) == 0) {
			log.warn("查询考拉余额接口失败："+returnStr);
		}
		
		return money;
	}
	
	private BigDecimal queryJingDongBalance() {
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("payType", "4");
		
		// {"biz_price_balance_get_response":{"success":true,"resultMessage":"","resultCode":"0000","result":"18074.4000","code":"0"}}
		String resultText = RsyncJingdongSupport.callGateway("biz.price.balance.get", paramJson);
		
		BigDecimal money = new BigDecimal(-1);
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			JSONObject response = resultObj.getJSONObject("biz_price_balance_get_response");
			
			if(response != null) {
				boolean success = response.getBooleanValue("success");
				String result = response.getString("result");
				
				if(success && StringUtils.isNotBlank(result)) {
					money = new BigDecimal(result);
				}
			}
		}
		
		if(money.compareTo(defaultMoney) == 0) {
			log.warn("查询京东余额接口失败："+resultText);
		}
		
		return money;
	}
	
	private boolean sendWxMsg(String title,String type,String msg) {
		WxGateSupport support = new WxGateSupport();
		String receives = support.bConfig("groupcenter.jd_notice_receives_balance");
		List<String> list = support.queryOpenId(receives);
		StringBuilder result = new StringBuilder();
		for(String v : list) {
			result.append(support.sendWarnCountMsg(title, type, v, msg));
		}
		
		return result.toString().contains("resultcode=0");
	}
}
