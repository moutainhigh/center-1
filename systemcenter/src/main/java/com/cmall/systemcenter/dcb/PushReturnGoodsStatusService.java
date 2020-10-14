package com.cmall.systemcenter.dcb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.systemcenter.util.AESCipher;
import com.cmall.systemcenter.util.MD5Util;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * @desc SKU上下架推送信息
 * @author AngelJoy
 * @date 2018-01-02 17:25:00
 */
public class PushReturnGoodsStatusService extends BaseClass{
	
	/**
	 * 
	 * @param code 售后单号：退货单或是换货单
	 * @param statusCode  当前状态吗
	 * @param type 类型 1换货，0退货
	 * @param status 当前状态
	 * @return
	 */
	public MWebResult pushReturnGoodsStatus(String code,String statusCode,String type,String status){
		MDataMap map = new MDataMap();
		MWebResult result = new MWebResult();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MDataMap mDataMap = new MDataMap();
		mDataMap = DbUp.upTable("oc_order_after_sale").one("asale_code",code);
		if(!"多彩宝业务".equals(mDataMap.get("asale_remark"))){
			result.setResultMessage("不是多彩售后单，不推送");
			result.setResultCode(1);
			return result;
		}
		if("4497153900050001".equals(statusCode)){//商家通过审核，生成零元退款单
			//生成退款单
			MDataMap m = DbUp.upTable("oc_return_goods").one("return_code",code);
			String order_code = m.get("order_code");
			String operater = "";
			try{
				operater = UserFactory.INSTANCE.create().getUserCode();
			}catch(Exception e){
				e.getStackTrace();
			}
			this.creatReturnMoney(order_code,operater,"商家通过审核（确认收货入库）");
		}
		map.put("code", code);
		map.put("type", type);
		map.put("status", status);
		map.put("statusCode", statusCode);
		map.put("updateTime", sdf.format(new Date()));
		String response = "";
		//排序
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        Map<String,Object> sendMap = new LinkedHashMap<>();
        for (String str : list){
        	sendMap.put(str,map.get(str));
        }
        //签名字符串
        String md5Str = "";
        //加密字符串
        String encrypt = "";
		try {
			md5Str = MD5Util.MD5Encode(JSONObject.toJSONString(sendMap),"utf-8");
			encrypt = AESCipher.encryptAES(JSONObject.toJSONString(map),AESCipher.key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        MDataMap requestParams = new MDataMap();
        requestParams.put("signature",md5Str);
        requestParams.put("inputstring",encrypt);
		MDataMap headerDataMap = new MDataMap();
		headerDataMap.put("Content-Type", "application/json");
		Date request_time = new Date();
		String exception = "";
		String url = bConfig("gogpay.dcb_interface_url")+"online-shop/_3rd/huijiayou/refund_order_state";
		String input = JSONObject.toJSONString(requestParams);
		String logType = "push";
		try {
			response = WebClientSupport.upPostToDC(url, requestParams,headerDataMap);
		} catch (Exception e) {
			e.printStackTrace();
			exception = e.getClass().getName();
		}
		Date response_time = new Date();
		RecordInterfaceLogForDcb recordForDcb = new RecordInterfaceLogForDcb();
		String original_input = JSONObject.toJSONString(sendMap);
		recordForDcb.insertLogTable(url, request_time, response_time,original_input, input, response, logType, exception);
		JSONObject jo = (JSONObject) JSONObject.parse(response);
		if(jo == null){
			result.setResultMessage("推送失败,返回值异常");
			result.setResultCode(2);
			return result;
		}
		if(jo.getBooleanValue("success")){
			result.setResultMessage("成功推送");
			result.setResultCode(1);
		}else{
			result.setResultMessage("推送失败");
			result.setResultCode(2);
		}
		return result;
	}
	
	/**
	 * 生成退款单
	 * @param order_code
	 * @param remark
	 * @return
	 */
	private MWebResult creatReturnMoney(String order_code,String operator,String remark) {
		MWebResult result = new MWebResult();
		// 判断此单是否已经完成退款
		MDataMap mp = DbUp.upTable("oc_return_money").one("order_code",order_code);
		if (null != mp) {
			result.setResultCode(939301064);
			result.setResultMessage(bInfo(939301064));
			return result;
		}
		
		MDataMap orderMap = null;
		//判断是否是在线支付449716200001，或第三方代收449716200010  若不是，则不生成退款单
		MDataMap orderMap1=DbUp.upTable("oc_orderinfo").one("order_code",order_code,"pay_type","449716200001");
		MDataMap orderMap2=DbUp.upTable("oc_orderinfo").one("order_code",order_code,"pay_type","449716200010");
		if(orderMap1==null && orderMap2==null){
			return result;
		}else if(orderMap1 == null){
			orderMap = orderMap2;
		}else{
			orderMap = orderMap1;
		}
		
		// 取支付方式
		MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_type", "zid desc","order_code=:order_code", "order_code", order_code);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MDataMap map = new MDataMap();
		String money_no = WebHelper.upCode("RTM");
		map.put("return_money_code", money_no);
		map.put("return_goods_code", "");
		map.put("buyer_code", orderMap.get("buyer_code"));
		map.put("seller_code", orderMap.get("seller_code"));
		map.put("small_seller_code", orderMap.get("small_seller_code"));
		map.put("contacts", "");//联系人
		map.put("status", "4497153900040003");
		map.put("return_money", null == payMap ? "0" :"449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0 多彩宝也为0 --rhb
		map.put("mobile", this.getMobilePhone(orderMap.get("buyer_code")));
		map.put("create_time", sdf.format(new Date()));
		map.put("poundage", "0");
		map.put("order_code", orderMap.get("order_code"));
		map.put("pay_method", orderMap.get("pay_type"));
		map.put("online_money", null == payMap ? "0" :"449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0 多彩宝也为0 --rhb
		map.put("out_order_code", orderMap.get("out_order_code")!=null?orderMap.get("out_order_code"):"");
		DbUp.upTable("oc_return_money").dataInsert(map);
		
		// 创建流水日志
		MDataMap logMap = new MDataMap();
		logMap.put("return_money_no", money_no);
		logMap.put("info", remark);
		logMap.put("create_time",  sdf.format(new Date()));
		logMap.put("create_user", operator);
		logMap.put("status", map.get("status"));
		DbUp.upTable("lc_return_money_status").dataInsert(logMap);
		
		return result;
	}
	
	private String getMobilePhone(String member_code){
		String mobile = "";
		if (StringUtils.isBlank(member_code)) {
			return mobile;
		}
		MDataMap dataMap = DbUp.upTable("mc_login_info").one("member_code",
				member_code);
		if (dataMap != null) {
			mobile = dataMap.get("login_name");
		}
		return mobile;
	}
}
