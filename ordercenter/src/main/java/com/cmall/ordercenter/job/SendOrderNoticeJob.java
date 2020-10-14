package com.cmall.ordercenter.job;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.ordercenter.alipay.util.MD5Util;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webwx.WxGateSupport;

public class SendOrderNoticeJob  extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String obj) {
		JSONObject parseObject = JSON.parseObject(obj);
		String big_order_code = parseObject.getString("big_order_code");
		boolean isPayed = parseObject.getBoolean("isPayed");
		RootResult result = new RootResult();
		
		MDataMap bigOrder = DbUp.upTable("oc_orderinfo_upper").one("big_order_code", big_order_code);
		
		if(null != bigOrder) {
			
			String due_money = bigOrder.get("due_money");
			
			String buyer_code = bigOrder.get("buyer_code");
			MDataMap member = DbUp.upTable("mc_login_info").one("member_code", buyer_code);
			if(null != member) {
				
				String unionId = member.get("unionId");
				String openid_gzh = member.get("openid_gzh");
				String openid_xch = member.get("openid_xch");
				
				WxGateSupport wxGateSupport = new WxGateSupport(bConfig("ordercenter.wx_url"), bConfig("ordercenter.merchant_key"), bConfig("ordercenter.merchant_id"), bConfig("ordercenter.word_color"));
				String sendResult = "";
				
				//判断用户是否关注公众号
				openid_gzh = getMemberInfoByWx(unionId);
				
				if(null != openid_gzh && !"".equals(openid_gzh)) {
					
					if(isPayed) {//已付款
						String receivers = openid_gzh + "|" + bConfig("ordercenter.send_payed_template_num") +"||" + bConfig("ordercenter.gzh_details_link");;
						sendResult = wxGateSupport.sendOrderNoticeByGzh7(receivers, "您已订购成功", big_order_code, due_money+"元", bConfig("ordercenter.remark_word"));
						bLogInfo(0, big_order_code + "调用微信公众号发送付款成功通知响应结果:" + sendResult);
					}else {//货到付款
						String receivers = openid_gzh + "|" + bConfig("ordercenter.send_nopay_template_num") + "||" + bConfig("ordercenter.gzh_details_link");;
						sendResult = wxGateSupport.sendOrderNoticeByGzh8(receivers, "您已订购成功", big_order_code, due_money+"元", "", bConfig("ordercenter.remark_word"));
						bLogInfo(0, big_order_code + "调用微信公众号发送下单成功通知响应结果:" + sendResult);
					}
				}else {
					//未关注公众号 判断能否用小程序发消息
					MDataMap payInfo = DbUp.upTable("oc_payment_paygate").one("c_order", big_order_code);
					
					//如果是微信小程序支付的 则用小程序发通知
					if(null != payInfo && "764".equals(payInfo.get("c_paygate")) && null!=openid_xch && !"".equals(openid_xch)) {
						
						String prepayId = XmasKv.upFactory(EKvSchema.XcxOrderPrepareId).get(big_order_code);
						if(null ==prepayId || "".equals(prepayId)) {
							result.setResultCode(0);
							result.setResultMessage("未找到" + big_order_code + "订单的prepayId");
							return result;
						}
						//2020-01-10号之后，微信小程序发送消息模式变更。
						String nowDate = DateUtil.getSysDateTimeString();
						String lineDate = bConfig("ordercenter.dead_line");
						if(DateUtil.compareDateTime(nowDate, lineDate)) {
							String receivers = openid_xch + "|8||" + bConfig("ordercenter.xcx_details_link");;
							sendResult = wxGateSupport.sendOrderNoticeByXcx(receivers, big_order_code, due_money+"元", bConfig("ordercenter.remark_word"), prepayId);
							bLogInfo(0, big_order_code + "调用微信小程序发送下单成功通知响应结果:" + sendResult);
						}
					}
				}
				
				result.setResultMessage(sendResult + "");
				
				MDataMap resultMap = decode(sendResult);
				if(null == resultMap || !"0".equals(resultMap.get("resultcode"))) {
					result.setResultCode(0);
					
					if("".equals(sendResult)) {
						result.setResultMessage("该订单即没关注公众号又非微信小程序支付!");
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public ConfigJobExec getConfig() {

		ConfigJobExec config = new ConfigJobExec();
		config.setExecType("449746990005");
		config.setMaxExecNumber(1);
		return config;
	}
	
	/**
	 * 根据unionid获取用户公众号信息 判断用户是否关注公众号
	 * @param  unionId
	 * @return  用户的openid
	 */
	private String getMemberInfoByWx(String unionId) {
		
		if(null == unionId || "".equals(unionId)) {
			return null;
		}
		
		MDataMap params = new MDataMap();
		params.put("merchantid", bConfig("ordercenter.merchant_id"));
		params.put("tradetype", "SendWX");
		params.put("orderno", String.valueOf(System.currentTimeMillis()));
		params.put("tradetime", FormatHelper.upDateTime("yyyyMMddHHmmss"));
		params.put("TradeCode", "Wx_Get_User_info");
		/**
		 * TODO 
		 * 测试环境 公众号的channelid=5或者12，其中5为临时非可靠数据，可以取到unionID；12是我们目前的测试公众号海豚
		 * 正式环境 公众号的channelid=6
		 * 模拟时可用1 
		 */
		params.put("channelid", bConfig("ordercenter.get_member_info_channelid"));
		params.put("v", "1.1");
		params.put("wxtype", "USER");
		/**
		 * 1根据UnionId，
		 * 0根据OpenID
		 */
		params.put("sender", "1");
		/**
		 * 用户的UnionID或者OpenID
		 */
		params.put("receivers", unionId);//TODO 测试环境写死值
		params.put("message", "");
		params.put("tradekeyid", "zh_CN");
		
		//排序
        Collection<String> valueset = params.values();
        List<String> list = new ArrayList<String>(valueset);
        Collections.sort(list);
        
        StringBuffer mac = new StringBuffer();
        for (String str : list){
        	mac.append(str);
        }
        mac.append(bConfig("ordercenter.merchant_key"));//商户秘钥
        
		params.put("mac", MD5Util.MD5Encode(mac.toString(), "utf-8"));
		
		String url = bConfig("ordercenter.wx_url");
		
		String resultStr = null;
		
		try {
			resultStr = WebClientSupport.upPost(url, params);
			if(null != resultStr && !"".equals(resultStr)) {
				MDataMap resultMap = decode(resultStr);
			    if(null != resultMap && resultMap.containsKey("resultcode") && "00".equals(resultMap.get("resultcode"))
						&& resultMap.containsKey("SyncReturn") && null != resultMap.get("SyncReturn")) {
			    	JSONObject parseObject = JSON.parseObject(resultMap.get("SyncReturn"));
			    	String subscribe = parseObject.getString("subscribe");
					if("1".equals(subscribe)) {
						return parseObject.getString("openid");
					}
			    }
			    
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	/**
	 * 解码
	 * @param param eg: resultcode=0&resultmessage=操作成功&successreceivers=&failedreceivers=&mac=c6c9297bf57a77fd4ff0955864c2ba70
	 * @return
	 */
	private MDataMap decode(String param) {
		
		if(null == param || "".equals(param)) {
			return null;
		}
		
		try {//解码
			param = URLDecoder.decode(param, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		MDataMap resultMap = new MDataMap();
		String[] resultArr = param.split("&");
	    for(String strSplit:resultArr){
	          String[] arrSplitEqual=null;         
	          arrSplitEqual= strSplit.split("=");
	         
	          //解析出键值
	          if(arrSplitEqual.length>1){
	              //正确解析
	        	  resultMap.put(arrSplitEqual[0], arrSplitEqual[1]);
	          }else{
	              if(arrSplitEqual[0]!=""){
		              //只有参数没有值，不加入
	            	  resultMap.put(arrSplitEqual[0], "");       
	              }
	          }
	    }
	    return resultMap;
	}
	
}
