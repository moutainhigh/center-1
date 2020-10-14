package com.cmall.groupcenter.mobilewallet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.systemcenter.ali.sign.MD5;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 利用和包支付单笔代发接口完成微公社提现
 * @author chenbin@ichsy.com
 *
 */
public class PayOrder extends BaseClass{

	public void order(MDataMap mDataMap){
		MDataMap params=new MDataMap();
		params.put("requestId", WebHelper.upCode("10086"));
		params.put("lbnkName", mDataMap.get("bank_name"));
		params.put("capCorg", mDataMap.get("bank_code"));
		params.put("receiveCardNo", mDataMap.get("card_code"));
		params.put("amount", mDataMap.get("pay_money"));
		params.put("receiveCardName", mDataMap.get("member_name"));
		params.put("remark","groupcenter_withdraw");
		postData(params,mDataMap.get("pay_order_code"));
	}
	
	public void postData(MDataMap map,String payOrderCode){
		try {
		    String req_url=bConfig("groupcenter.pay_req_url");
		    String signKey=bConfig("groupcenter.pay_sign_key");
		    String merchantId=bConfig("groupcenter.pay_merchant_id");
		    String characterSet="GBK";
		    String notifyUrl=bConfig("groupcenter.pay_notify_url");
		    String signType="MD5";
		    String type="PaymentAsy";
		    String version="2.0.0";
		    String accType="6";
		    String hmac="";
		    String requestId=(String) map.get("requestId");
		    String receiveCardName=(String) map.get("receiveCardName");
		    String receiveCardNo=(String) map.get("receiveCardNo");
		    String amount=String.valueOf((int)(Double.valueOf(map.get("amount").toString())*100));
		    String lbnkName=(String) map.get("lbnkName");
		    String capCorg=(String) map.get("capCorg");
		    String remark=(String) map.get("remark").trim();
		    String signData = merchantId + requestId + signType + type + version + receiveCardNo + amount + accType + notifyUrl;
		    HiiposmUtil util=new HiiposmUtil();
		    hmac=util.MD5Sign(signData, signKey);
		
		    String buf ="merchantId=" + merchantId + "&requestId="
				+ requestId + "&signType=" + signType
				+ "&type=" + type + "&version=" + version
				+ "&lbnkName=" +lbnkName + "&capCorg=" + capCorg
				+ "&receiveCardNo=" + receiveCardNo + "&amount=" 
				+ amount + "&accType=" + accType + "&receiveCardName=" 
				+ receiveCardName + "&remark=" +remark + "&notifyUrl=" +notifyUrl;
		    buf = "hmac=" + hmac + "&" + buf;
		   
		
		    Http_Request_Post http_Request_Post=new Http_Request_Post();
		    map.put("pay_order_code", payOrderCode);
		    map.put("request_time", FormatHelper.upDateTime());
		    map.put("request_data", req_url+"?"+buf);
		    map.put("pay_type", "4497465200100002");//和包支付方式
		    DbUp.upTable("gc_pay_money_log").dataInsert(map);
		    MDataMap payMap=new MDataMap();
		    payMap.inAllValues("pay_order_code",payOrderCode,"pay_status","4497465200070002");//更新状态为已支付
		    DbUp.upTable("gc_pay_order_info").dataUpdate(payMap, "pay_status", "pay_order_code");
		    String res=util.sendAndRecv(req_url, buf, characterSet);
		    map.put("response_time", FormatHelper.upDateTime());
		    map.put("response_data", res);
		    DbUp.upTable("gc_pay_money_log").dataUpdate(map, "response_time,response_data", "requestId");
		    String code = util.getValue(res, "returnCode");
		    String message = URLDecoder.decode(util.getValue(res, "message"),"UTF-8");
		    map.put("order_code", code);
		    map.put("order_code_message", message);
		    DbUp.upTable("gc_pay_money_log").dataUpdate(map, "order_code,order_code_message", "requestId");
		    if(code.equals("000000")){
		    String hmac1 = util.getValue(res, "hmac");
		    String vfsign = util.getValue(res, "merchantId")
				+ util.getValue(res, "requestId")
				+ util.getValue(res, "returnCode")
				+ util.getValue(res, "signType")
				+ util.getValue(res, "type")
                + util.getValue(res, "version");
		    // -- 验证签名
		    boolean flag = false;
			flag = util.MD5Verify(vfsign, hmac1, signKey);
	        
		    
		    if (!flag) {
		    	//payMap.put("pay_status","4497465200070001");
				//DbUp.upTable("gc_pay_order_info").dataUpdate(payMap, "pay_status", "pay_order_code");
				
			}
		    }
		    else{
		    	//payMap.put("pay_status","4497465200070001");
				//DbUp.upTable("gc_pay_order_info").dataUpdate(payMap, "pay_status", "pay_order_code");
		    }
		} catch (IOException e) {
			e.printStackTrace();
			map.put("error_exception", "order:"+e.getMessage());
			DbUp.upTable("gc_pay_order_info").dataUpdate(map, "error_exception", "requestId");
		}
	}
	
	public static void main(String args[]){
		PayOrder payOrder=new PayOrder();
		payOrder.order(null);
	}

}
