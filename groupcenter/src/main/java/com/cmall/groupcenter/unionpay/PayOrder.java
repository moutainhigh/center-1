package com.cmall.groupcenter.unionpay;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;






import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.paymoney.util.Base64Util;
import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.systemcenter.ali.sign.MD5;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 银联支付
 * @author chenbin@ichsy.com
 *
 */
public class PayOrder extends BaseClass{

	public void pay(MDataMap mDataMap){
		MDataMap map=new MDataMap();
		SimpleDateFormat sdf2 = new SimpleDateFormat("ssSSS");
		String payOrderCode=mDataMap.get("pay_order_code");
		String orderNum=WebHelper.upCode("8888");
		
		map.put("requestId",orderNum);
		try {
		String remark="withdraw";
		String characterSet="UTF-8";
		JSONObject requestJsonObject = new JSONObject();
		requestJsonObject.put("command", "01003");
		requestJsonObject.put("logisticsid", bConfig("groupcenter.unionpay_logisticsid"));
		requestJsonObject.put("companyid", bConfig("groupcenter.unionpay_companyid"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		requestJsonObject.put("txntime",sdf.format(new Date()));
		requestJsonObject.put("bankaccount",mDataMap.get("card_code"));
		requestJsonObject.put("accountname",mDataMap.get("member_name"));
		requestJsonObject.put("ordernum", orderNum);
		requestJsonObject.put("bankcode", "");
		requestJsonObject.put("bankname", "");
		requestJsonObject.put("certificatetype","01");
		requestJsonObject.put("certificateno","");
		requestJsonObject.put("phone", "");
		String amount=mDataMap.get("pay_money");
		requestJsonObject.put("txntaccount", amount);
		requestJsonObject.put("reserved", "");
		String data  = Base64Util.encode(requestJsonObject.toString().getBytes());
		MD5 md5=new MD5();
		data = data.replace('_','/').replace('+', '-');
		//System.out.println(data);
		String sign =md5.sign(data, "C6FB78ADC5C58A72B1CCBC953F3", characterSet);
		String url="http://www.masget.cn:8098/api.aspx";
//		String url=bConfig("groupcenter.rbPayRequestUrl");

		String reqeustJson = "command=01003&data="+data+"&sign="+sign;
		HiiposmUtil hiiposmUtil=new HiiposmUtil();
		
		//增加支付接口日志
		map.inAllValues("pay_order_code",payOrderCode,"amount",amount,"receiveCardNo",mDataMap.get("card_code"),
				"receiveCardName",mDataMap.get("member_name"),"lbnkName",mDataMap.get("bank_name"),"capCorg",mDataMap.get("bank_code"),
				"remark",remark,"request_data",url+reqeustJson,"request_time",FormatHelper.upDateTime(),"pay_type","4497465200100001");
		DbUp.upTable("gc_pay_money_log").dataInsert(map);
		//更新付款单支付状态为已支付
	    MDataMap payMap=new MDataMap();
	    payMap.inAllValues("pay_order_code",payOrderCode,"pay_status","4497465200070002");
	    DbUp.upTable("gc_pay_order_info").dataUpdate(payMap, "pay_status", "pay_order_code");
	    MDataMap payLog=new MDataMap();
	    //增加付款单日志表-提交支付订单
	    payLog.inAllValues("pay_order_code",payOrderCode,"order_status","4497153900120002","pay_status","4497465200070002","update_time",FormatHelper.upDateTime(),"remark",bConfig("groupcenter.pay_order"));
	    DbUp.upTable("gc_pay_order_log").dataInsert(payLog);
	    //提交支付订单
		String a= hiiposmUtil.sendAndRecv(url, reqeustJson,characterSet);
		//System.out.println(a);
		map.put("response_time", FormatHelper.upDateTime());
		map.put("response_data", a);
		//更新返回信息
		DbUp.upTable("gc_pay_money_log").dataUpdate(map, "response_time,response_data", "requestId");
		JSONObject jsonObject=new JSONObject();
		jsonObject=jsonObject.parseObject(a);
		String code=jsonObject.getString("response");
		String message=jsonObject.getString("message");
		map.put("order_code", code);
	    map.put("order_code_message", message);
	    if(code.equals("00")){//成功时默认为空，赋给提交成功
	    	map.put("order_code_message", bConfig("groupcenter.pay_order_success"));
	    }
	    //更新提交状态
	    DbUp.upTable("gc_pay_money_log").dataUpdate(map, "order_code,order_code_message", "requestId");
        if(!code.equals("00")){//没有提交成功
        	//updateMap.put("pay_status", "4497465200070003");//支付失败
			//支付失败，提现金额返回用户账户
			GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
			gcWithdrawLog.setUid(WebHelper.upUuid());
			gcWithdrawLog.setAccountCode(mDataMap.get("account_code"));
			gcWithdrawLog.setMemberCode(mDataMap.get("member_code"));
			gcWithdrawLog.setWithdrawMoney(new BigDecimal(mDataMap.get("withdraw_money")));
			gcWithdrawLog.setWithdrawChangeType("4497465200040006");//提现单支付失败
			gcWithdrawLog.setChangeCodes(mDataMap.get("pay_order_code"));
			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
			listWithdrawLogs.add(gcWithdrawLog);
			txGroupAccountService.updateAccount(null, listWithdrawLogs);
			//支付信息明细对应表，撤回取消
			List<MDataMap> detaiList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",mDataMap.get("pay_order_code"));
			if(detaiList!=null&&detaiList.size()>0){
				for(MDataMap map2:detaiList){
					MDataMap detailLog=DbUp.upTable("gc_reckon_log").one("order_code",map2.get("order_code"),"account_code",mDataMap.get("account_code"),"reckon_change_type","4497465200030001");
					MDataMap update=new MDataMap();
					update.put("order_code", detailLog.get("order_code"));
					update.put("zid", detailLog.get("zid"));
					update.put("payed_money", (new BigDecimal(detailLog.get("payed_money")).subtract(new BigDecimal(map2.get("reckon_money")))).toString());
					DbUp.upTable("gc_reckon_log").dataUpdate(update, "payed_money", "zid");
				}
			}
			//push提现失败
			try {
				AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
				addSinglePushCommentInput.setAccountCode(mDataMap.get("account_code"));
				addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
				addSinglePushCommentInput.setType("44974720000400010002");
				
				addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
				addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
				addSinglePushCommentInput.setTitle("非常抱歉，您于"+mDataMap.get("create_time").substring(0, 10)+"的提现申请失败");
				MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",mDataMap.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
				if(memberMap!=null){
					addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
				}
				
			    String content="请核对个人信息后重新申请，如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
			    addSinglePushCommentInput.setContent(content);
				
				if(DbUp.upTable("gc_account_push_set").count("account_code",mDataMap.get("account_code"),"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
				    addSinglePushCommentInput.setSendStatus("4497465000070001");    	
				}
				else{
					addSinglePushCommentInput.setSendStatus("4497465000070002");
				}
				SinglePushComment.addPushComment(addSinglePushCommentInput);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//更新支付状态为支付失败
		    MDataMap payMap2=new MDataMap();
		    payMap2.inAllValues("pay_order_code",payOrderCode,"pay_status","4497465200070003");
		    DbUp.upTable("gc_pay_order_info").dataUpdate(payMap2, "pay_status", "pay_order_code");
		    MDataMap payLog2=new MDataMap();
		    //增加支付失败日志
		    payLog2.inAllValues("pay_order_code",payOrderCode,"order_status","4497153900120002","pay_status","4497465200070003","update_time",FormatHelper.upDateTime());
		    DbUp.upTable("gc_pay_order_log").dataInsert(payLog2);
			
        }
        else{//提交成功
        	
        	
        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("error_exception", "order:"+e.getMessage());
			DbUp.upTable("gc_pay_order_info").dataUpdate(map, "error_exception", "requestId");
		}
		
	}
	
	public static void main(String args[]){
		PayOrder payOrder=new PayOrder();
		List<MDataMap> list=DbUp.upTable("gc_pay_order_info").queryByWhere("order_status","4497153900120002","pay_status","4497465200070001");
		for(MDataMap mDataMap:list){
			payOrder.pay(mDataMap);
		}
	}
}
