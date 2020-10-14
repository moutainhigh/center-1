package com.cmall.systemcenter.dcb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.cmall.systemcenter.util.AESCipher;
import com.cmall.systemcenter.util.MD5Util;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * @desc SKU上下架推送信息
 * @author AngelJoy
 * @date 2018-01-02 17:25:00
 */
public class PushSkuStatusService extends BaseClass{
	
	private static Logger log = Logger.getLogger(PushSkuStatusService.class);
	
	/**
	 * 
	 * @param skuCode sku编码，多个用“,”隔开
	 * @param saleYn 是否可卖 Y：可卖，N：不可卖
	 * @param type 是否需要写日志，区别于轮询调用，1：需要，其他值不需要
	 * @param remark 可为空，日志中填写。
	 * @return
	 */
	public MWebResult pushSkuStatus(String skuCode,String saleYn,int type,String remark){
		MWebResult mResult = new MWebResult();
		String sku_codes[] = skuCode.split(",");
		String sale_yn = saleYn;
		List<Map<String,Object>> array = new ArrayList<Map<String,Object>>();
		
		List<String> skuCodeList = new ArrayList<String>();
		for (int i = 0; i < sku_codes.length; i++) {
			if(StringUtils.isBlank(sku_codes[i])){
				continue;
			}
			MDataMap skuinfo =  DbUp.upTable("pc_skuinfo").one("sku_code",sku_codes[i]);
			if(skuinfo == null){
				continue;
			}
			MDataMap bfskuinfo =  DbUp.upTable("pc_bf_skuinfo").one("sku_code",sku_codes[i]);
			if(bfskuinfo == null){
				continue;
			}
			String productCode = "";
			if(skuinfo != null && skuinfo.get("product_code")!=null){
				productCode = skuinfo.get("product_code").toString();
			}
			MDataMap product = DbUp.upTable("pc_productinfo").one("product_code",productCode);
			if(product == null){
				continue;
			}
			boolean flag = this.checkStatus(bfskuinfo,saleYn,skuinfo,product);
			if(!flag){
				String sku_status = bfskuinfo.get("sku_status").toString();
				if("1".equals(sku_status)||"2".equals(sku_status)){
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("sku_code", sku_codes[i]);
					mDataMap.put("product_code", productCode);
					mDataMap.put("sku_status", "0");
					int count = DbUp.upTable("pc_bf_skuinfo").dataUpdate(mDataMap, "sku_status", "sku_code");
					if(count != 1){
						log.error(remark+",变更数据库状态为未推时发生失败，skuCode为："+sku_codes[i]);
					}
					//写入日志
					if(1 == type){
						this.writeOperateLog(sku_codes[i],sale_yn,remark);
					}
				}
				skuCodeList.add(sku_codes[i]);
				continue;
			}
			Map<String,Object> item = new HashMap<String,Object>();
			item.put("skuCode", sku_codes[i]);
			item.put("productCode", productCode);
			item.put("saleYn", sale_yn);
			array.add(item);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("skuList", array);
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
		try {
			if(array.size() != 0){
				Date request_time = new Date();
				String exception = "";
				String url = bConfig("gogpay.dcb_interface_url")+"online-shop/_3rd/huijiayou/sku-status";
				String input = JSONObject.toJSONString(requestParams);
				String logType = "push";
				response = WebClientSupport.upPostToDC(url, requestParams,headerDataMap);
				Date response_time = new Date();
				RecordInterfaceLogForDcb logForDcb = new RecordInterfaceLogForDcb();
				String original_input = JSONObject.toJSONString(sendMap);
				logForDcb.insertLogTable(url, request_time, response_time, original_input,input, response, logType, exception);
			}else{
				mResult.setResultMessage("推送合法数据为空");
				mResult.setResultCode(1);
				return mResult;
			}
		} catch (Exception e) {
			mResult.setResultMessage("多彩宝推送失败");
			e.printStackTrace();
			return mResult;
		}
		if(StringUtils.isBlank(response)){
			mResult.setResultMessage("系统异常");
			mResult.setResultCode(0);
			return mResult;
		}
		JSONObject responseJSON = (JSONObject) JSONObject.parse(response);
		boolean flag = false;
		if(responseJSON != null){
			flag = responseJSON.getBoolean("success");
		}
		if(!flag){
			mResult.setResultMessage("推送失败，多彩返回值为:"+flag);
			mResult.setResultCode(1);
			return mResult;
		}
		for(int i = 0;i<sku_codes.length;i++){
			String sku_code = sku_codes[i];
			MDataMap dataMap = new MDataMap();
			dataMap.put("sku_code", sku_code);
			if("Y".equals(sale_yn)){//可卖状态推送，推送成功，本地置为上架状态 10.
				dataMap.put("sku_status", "10");//上架状态
			}else{
				dataMap.put("sku_status", "20");//下架状态。
			}
			boolean contionFlag = this.checkContions(sku_codes[i],skuCodeList);//true为包含，则不进行数据库更新
			if(!contionFlag){
				DbUp.upTable("pc_bf_skuinfo").dataUpdate(dataMap, "sku_status", "sku_code");
			}
			if(1 == type){
				this.writeOperateLog(sku_code,sale_yn,remark);
			}
		}
		mResult.setResultCode(0);
		mResult.setResultMessage("全部推送成功");
		return mResult;
	}
	
	private boolean checkContions(String skuCode,List<String> skuCodeList){
		boolean flag = false;
		for(String code: skuCodeList){
			if(skuCode.equals(code)){
				return true;
			}
		}
		return flag;
	}
	
	/**
	 * @desc 判定商品是都推送方法
	 * @param sku_code
	 * @return false时此商品不做推送 
	 */
	private boolean checkStatus(MDataMap bfskuinfo,String sale_yn,MDataMap skuMap,MDataMap productMap){
		String status = bfskuinfo.get("sku_status")!=null?bfskuinfo.get("sku_status").toString():"0";
		if("N".equals(sale_yn)){
			//10状态是在缤纷商户为上架状态，做下架处理。需要推送判定值为true
			if("10".equals(status)){
				return true;
			}
			return false;
		}
		//是否可售值为空时不做推送
		if(skuMap.get("sale_yn") == null){
			return false;
		}
		//不可售状态下不做上架推送
		if("N".equals(skuMap.get("sale_yn").toString())){
			return false;
		}
		//商品信息为空时不做推送
		if(productMap == null){
			return false;
		}
		//商品状态为空时 不做推送
		if(productMap.get("product_status") == null){
			return false;
		}
		//商品状态为已上架状态时推送
		if("4497153900060002".equals(productMap.get("product_status").toString())){
			return true;
		}
		return false;
	}
	
	/**
	 * 写日志方法
	 * @param sku_code
	 * @param sale_yn
	 * @param remark
	 */
	private void writeOperateLog(String sku_code,String sale_yn,String remark){
		MDataMap dataLogMap = new MDataMap();
		MDataMap logMap = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
		dataLogMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
		dataLogMap.put("sku_code", sku_code);
		dataLogMap.put("sku_name", logMap.get("sku_name").toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dataLogMap.put("operate_time", sdf.format(new Date()));
		String user = "";
		try{
			user = UserFactory.INSTANCE.create().getRealName();
		}catch(Exception e){
			e.getStackTrace();
		}
		dataLogMap.put("operator", user);
		if("Y".equals(sale_yn)){
			dataLogMap.put("operate_status", "缤纷商贸上架推送");
		}else{
			dataLogMap.put("operate_status", "缤纷商贸下架推送");
		}
		dataLogMap.put("remark", remark);
		DbUp.upTable("pc_bf_review_log").dataInsert(dataLogMap);
	}
}
