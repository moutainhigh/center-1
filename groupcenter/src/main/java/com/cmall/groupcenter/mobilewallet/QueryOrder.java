package com.cmall.groupcenter.mobilewallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;

/**
 * 和包支付查询接口
 * @author chenbin@ichsy.com
 *
 */
public class QueryOrder extends BaseClass{

	public String query(String queryRequestId){
		try {
		String QueryRequestId =queryRequestId;
		String characterSet="GBK";
		String req_url=bConfig("groupcenter.pay_req_url");
		String type = "PaymentQuery";
        String merchantId=bConfig("groupcenter.pay_merchant_id");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String requestId = sdf.format(new Date());
        String signType="MD5";
        String version="2.0.0";
        String signKey=bConfig("groupcenter.pay_sign_key");
            //-- 签名
        String signData = merchantId + requestId + signType + type + version + QueryRequestId;
        HiiposmUtil util = new HiiposmUtil();
        String hmac = util.MD5Sign(signData, signKey);

        //-- 请求报文
        String buf = "merchantId=" + merchantId + "&requestId="
                    + requestId + "&signType=" + signType + "&type=" + type
                    + "&version=" + version + "&QueryRequestId=" + QueryRequestId;
        buf = "hmac=" + hmac + "&" + buf;
        Map params=new HashMap();
        params.put("hmac", hmac);
    	params.put("merchantId", merchantId);
    	params.put("requestId", requestId);
    	params.put("signType", signType);
    	params.put("type", type);
    	params.put("version", version);
    	params.put("QueryRequestId", QueryRequestId);
    	Http_Request_Post http_Request_Post=new Http_Request_Post();
    	String res= util.sendAndRecv(req_url, buf, characterSet);
		

        String hmac1 = util.getValue(res, "hmac");
        String vfsign = util.getValue(res, "merchantId")
                    + util.getValue(res, "requestId")
                    + util.getValue(res, "returnCode")
                    + util.getValue(res, "signType")
                    + util.getValue(res, "type")
                    + util.getValue(res, "version")
                    + util.getValue(res, "QueryRequestId");
        String code = util.getValue(res, "returnCode");
        String receiveCardName=URLDecoder.decode(util.getValue(res, "receiveCardName"),"GBK");
        String lbnkName=URLDecoder.decode(util.getValue(res, "lbnkName"),"GBK");
        if (!code.equals("000000")) {     
                return "error";
        }
        else{
        	return res;
        }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "error";
		}
	}
	
	public static void main(String args[]){
		QueryOrder queryOrder=new QueryOrder();
		queryOrder.query("10086140922100001");
	}
}
