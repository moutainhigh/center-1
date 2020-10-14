package com.cmall.groupcenter.express.shentong.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 接受申通快递信息
 * @author zmm
 *
 */
public class RsyncShentongGoods{
	public MWebResult insertData(String orderCode,String waybill,String company_code){
		MWebResult mWebResult = new MWebResult();
		//获取订单锁
		String lock_id = WebHelper.addLock(60, "exp100-"+orderCode);
		if(StringUtils.isBlank(lock_id)){
			mWebResult.inErrorMessage(918558004, "");
			return mWebResult;
		}
		String logisticse_code = "";
		UUID uuid = UUID.randomUUID();
		String uid = uuid.toString().replace("-", "");
			Map<String, String> order_code=DbUp.upTable("oc_order_shipments").oneWhere("order_code","","order_code=:order_code","order_code",orderCode);
			if(order_code==null||order_code.equals("")){
		    List<MDataMap> list=WebTemp.upTempDataList("sc_logisticscompany", "company_name,company_code", "", "");
		    if(list.size()>0){
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					MDataMap mDataMap = (MDataMap) iterator.next();
					if (mDataMap.containsKey("company_name")&& StringUtils.contains(company_code,mDataMap.get("company_name"))) {
						logisticse_code = mDataMap.get("company_code");
					}
				}
			}
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			DbUp.upTable("oc_order_shipments").insert("uid", uid, "waybill",waybill, "order_code", orderCode, "logisticse_code",
					logisticse_code, "logisticse_name", company_code,"creator","SI3003","create_time",df.format(new Date()).toString());
			mWebResult.setResultMessage(orderCode + "成功入库");
		}
			
			WebHelper.unLock(lock_id);
			return mWebResult;
	}
	
	// only for test
	public static void main(String[] args) {
		RsyncShentongGoods aa=new RsyncShentongGoods();
		aa.insertData("1232224444444", "12342244444444442", "STO");
		System.out.println("-----------success--------------");
	}
}

	