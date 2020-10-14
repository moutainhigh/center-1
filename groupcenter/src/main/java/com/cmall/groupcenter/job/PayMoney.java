package com.cmall.groupcenter.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Query;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.mobilewallet.PayOrder;
import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.systemcenter.ali.sign.MD5;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * @author chenbin@ichsy.com
 *
 */
public class PayMoney extends RootJob{

	public void doExecute(JobExecutionContext context) {
		PayOrder payOrder=new PayOrder();
		com.cmall.groupcenter.unionpay.PayOrder unionPayOrder=new com.cmall.groupcenter.unionpay.PayOrder();
		List<MDataMap> list=DbUp.upTable("gc_pay_order_info").queryByWhere("order_status","4497153900120002","pay_status","4497465200070001");
		for(MDataMap mDataMap:list){
			/*if(StringUtils.isNotEmpty(mDataMap.get("bank_name"))){//采用和包支付
				payOrder.order(mDataMap);
			}
			else{//采用银联支付
				unionPayOrder.pay(mDataMap);
			}*/
			unionPayOrder.pay(mDataMap);
		}
	}
	
	
	public static void main(String args[]){
		PayMoney payMoney=new PayMoney();
		payMoney.doExecute(null);
		/*try {
			query();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
