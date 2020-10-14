package com.cmall.groupcenter.mobilewallet;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmethod.RootControl;

/**
 * 和包支付回调
 * @author chenbin@ichsy.com
 *
 */
public class Notify extends BaseClass{

	public String process(Map params){
		
        String result="SUCCESS";
        //签名验证
		//逻辑处理，状态更新
		String signKey=bConfig("groupcenter.pay_sign_key");
		
		String merchantId = (String) params.get("merchantId");
		String returnCode = (String) params.get("returnCode");
		String message = (String) params.get("message");
		String signType = (String) params.get("signType");
		String type = (String) params.get("type");
		String version = (String) params.get("version");
		String transNo = (String) params.get("transNo");
		String amount = (String) params.get("amount");
		String reqNo = (String) params.get("reqNo");
		String transId = (String) params.get("transId");
		String transDate = (String) params.get("transDate");
		String recvDate = (String) params.get("recvDate");
		String orderStatus = (String) params.get("orderStatus");
		String transRemark = (String) params.get("transRemark");
		String hmac = (String) params.get("hmac");
		MDataMap map=new MDataMap();
		map.put("requestId", reqNo);
		try {
		    //中心平台发来的中文数据需要转码为UTF-8后来做验签
			message = URLDecoder.decode(message, "UTF-8");
			transRemark = URLDecoder.decode(transRemark, "UTF-8");
		
		    //组织验签报文
		    String signData = "";
	        signData = merchantId + returnCode + message + signType + type + version + transNo + reqNo + amount + transId + transDate + recvDate + orderStatus;

            HiiposmUtil util = new HiiposmUtil();
		    String hmac1 = util.MD5Sign(signData, signKey);
		    //验签
		    boolean sign_flag = util.MD5Verify(signData,hmac,signKey);
		    if (sign_flag) {
	        	MDataMap mDataMap=DbUp.upTable("gc_pay_money_log").one("requestId",reqNo);
	        	MDataMap infoMap=DbUp.upTable("gc_pay_order_info").one("pay_order_code",mDataMap.get("pay_order_code"));
	        	if(mDataMap.get("notity_code").length()==0){//是否已回调处理
	        		map.put("notify_code",returnCode);
	        		map.put("notify_code_message", message);
	        		if(orderStatus.equals("S")&&returnCode.equals("000000")){//返回码和付款状态均为成功时才判定成功
	        			map.put("pay_stauts", "4497465200070004");
	        		}
	        		else{
	        			//付款失败,有可能不通知，待测试下 
	        			//更新付款失败
	        			map.put("pay_stauts", "4497465200070003");//支付失败
	        			//支付失败，提现金额返回用户账户
	    				GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
	    				gcWithdrawLog.setUid(WebHelper.upUuid());
	    				gcWithdrawLog.setAccountCode(infoMap.get("account_code"));
	    				gcWithdrawLog.setMemberCode(infoMap.get("member_code"));
	    				gcWithdrawLog.setWithdrawMoney(new BigDecimal(infoMap.get("withdraw_money")));
	    				gcWithdrawLog.setWithdrawChangeType("4497465200040006");//提现单支付失败
	    				TxGroupAccountService txGroupAccountService = BeansHelper
	    						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
	    				List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
	    				listWithdrawLogs.add(gcWithdrawLog);
	    				txGroupAccountService.updateAccount(null, listWithdrawLogs);
	    				//支付信息明细对应表，撤回取消
	    				List<MDataMap> detaiList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",mDataMap.get("pay_order_code"));
	    				if(detaiList!=null&&detaiList.size()>0){
	    					for(MDataMap detail:detaiList){
	    						MDataMap detailLog=DbUp.upTable("gc_reckon_log").one("order_code",detail.get("order_code"));
	    						MDataMap update=new MDataMap();
	    						update.put("order_code", detailLog.get("order_code"));
	    						update.put("payed_money", (new BigDecimal(detailLog.get("payed_money")).subtract(new BigDecimal(detail.get("reckon_money")))).toString());
	    						DbUp.upTable("gc_reckon_log").dataUpdate(update, "payed_money", "order_code");
	    					}
	    				}
	        		}
	        		//更新日志表
	        		DbUp.upTable("gc_pay_money_log").dataUpdate(map, "notify_code,notify_code_message,pay_status", "requestId");
	        		map.put("pay_order_code", mDataMap.get("pay_order_code"));
	        		DbUp.upTable("gc_pay_order_info").dataUpdate(map, "pay_status", "pay_order_code");//更新用户付款单据表	
	        		//更新付款单据日志表
	        		MDataMap payLog=new MDataMap();
	     		    payLog.inAllValues("pay_order_code",mDataMap.get("pay_order_code"),"order_status","4497153900120002","pay_status",map.get("pay_status"),"update_time",FormatHelper.upDateTime());
	     			DbUp.upTable("gc_pay_order_log").dataInsert(payLog);
	        	}
		    } else {
			result="FAILED";
	        }
	   }
	   catch (Exception e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   map.put("error_exception","notify:"+e.getMessage());
		   DbUp.upTable("gc_pay_order_info").dataUpdate(map, "error_exception", "requestId");
	    }
	    return result;
    }
}