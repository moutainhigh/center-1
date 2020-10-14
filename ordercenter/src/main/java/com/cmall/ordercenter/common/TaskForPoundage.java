package com.cmall.ordercenter.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.alipay.config.AlipayConfig;
import com.cmall.ordercenter.alipay.util.AlipaySubmit;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时从支付宝读取成功订单的支付数据对接本系统内数据
 * 
 * 
 */
public class TaskForPoundage extends RootJob {
 
	public void doExecute(JobExecutionContext context) {
		//查询本系统内需要对接支付宝的订单数据
		bLogInfo(0,"支付宝、块钱接口手续费跑批开始，现在时间为：",DateUtil.getSysDateTimeString());
		MDataMap queryMap = new MDataMap();
		queryMap.put("pay_type", "449746280003");
		queryMap.put("status", "0");
		List<MDataMap> OrderList = DbUp.upTable("oc_order_pay").queryAll("uid,pay_sequenceid,payed_money", "", "", queryMap);//每个需要计算手续费的订单
		List<MDataMap> SequIdslist = DbUp.upTable("oc_order_pay").queryAll("pay_sequenceid", "", "", queryMap);//需要同步数据的标识
		Iterator<MDataMap> iterator =  SequIdslist.iterator();
		int i = 1;
		Map<String, Map<String, String>> data = new HashMap<String, Map<String,String>>();
		while(iterator.hasNext()){
			String trade_no = iterator.next().get("pay_sequenceid");
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("trade_no", trade_no);
				String sequData = account_page_query(map);
				if(sequData.contains("<is_success>F</is_success>")){
				//	System.out.println("第"+i+++"条error数据，序列号为‘"+trade_no+"’的数据返回有误，请人工检查 !~");
				}else {
					Map<String, String> dataMap = getvalues(sequData);
					data.put(trade_no, dataMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		boolean flag = editData(OrderList,data);//支付宝手续费
		if(flag){
			queryMap.put("pay_type", "449746280004");
			List<MDataMap> listKQ =  DbUp.upTable("oc_order_pay").queryAll("uid,pay_type,payed_money", "", "", queryMap);
			flag = editDataK(listKQ);
		}
		if(flag){
			bLogInfo(0,"支付宝、块钱接口手续费跑批结束，现在时间为：",DateUtil.getSysDateTimeString());
		}
	}
	private boolean editDataK(List<MDataMap> orList){
		boolean flag = true;
		try {
			MDataMap queryMap = new MDataMap();
			queryMap.put("pay_code", "449746280004");
			queryMap.put("use_able", "1");
			List<MDataMap> payType = DbUp.upTable("sc_paytype").queryAll("pay_scale", "", "", queryMap);
			if(!orList.isEmpty()&&!payType.isEmpty()){
				Iterator<MDataMap> iterator = orList.iterator();
				while (iterator.hasNext()) {
					MDataMap mDataMap = (MDataMap) iterator.next();
					BigDecimal mon = new BigDecimal(payType.get(0).get("pay_scale")).multiply(new BigDecimal(mDataMap.get("payed_money")));
					MDataMap insert = new MDataMap();
					insert.put("uid", mDataMap.get("uid"));
					insert.put("payed_fee", String.valueOf(mon.doubleValue()));
					insert.put("status", "1");
					DbUp.upTable("oc_order_pay").dataUpdate(insert, "payed_fee,status", "uid");
				}
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	private boolean editData(List<MDataMap> orList,Map<String, Map<String, String>> data){
		boolean flag = true;
		try {
			Iterator<String> ite = data.keySet().iterator();//序列号trade_no  总手续费actual_charge_amount  订单总金额total_fee
			while(ite.hasNext()){
				String sequId = ite.next();
				Iterator<MDataMap> iterator = orList.iterator();
				while(iterator.hasNext()){
					MDataMap md = iterator.next();//uid,pay_sequenceid,payed_money
					if(sequId!=null&&md.containsKey("pay_sequenceid")&&md.get("pay_sequenceid")!=null&&sequId.equals(md.get("pay_sequenceid"))){
						MDataMap upMap = new MDataMap();
						upMap.put("uid", md.get("uid"));
						upMap.put("status", "1");
						upMap.put("payed_all_fee", data.get(sequId).get("actual_charge_amount"));
						if("0.00".equals(data.get(sequId).get("actual_charge_amount"))||data.get(sequId).get("total_fee").equals(md.get("payed_money"))){
							upMap.put("payed_fee", data.get(sequId).get("actual_charge_amount"));
						}else{
							BigDecimal allOrderMon = new BigDecimal(data.get(sequId).get("total_fee"));
							BigDecimal orderMon = new BigDecimal(md.get("payed_money"));
							BigDecimal mon = orderMon.divide(allOrderMon, 2, BigDecimal.ROUND_HALF_UP);
							BigDecimal pay_fee = mon.multiply(new BigDecimal(data.get(sequId).get("actual_charge_amount")));
							upMap.put("payed_fee",String.valueOf(round(pay_fee.doubleValue(),2)));
						}
						DbUp.upTable("oc_order_pay").dataUpdate(upMap, "payed_all_fee,payed_fee,status", "uid");
					}
				}
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	private double round(double d,int len) {     // 进行四舍五入
	     BigDecimal b1 = new BigDecimal(d);
	     BigDecimal b2 = new BigDecimal(1);
	    return b1.divide(b2, len,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	private String account_page_query(Map<String, String> sParaTemp) throws Exception {
	    sParaTemp.put("service", "query_trade_list_partner");
	    sParaTemp.put("partner", AlipayConfig.partner);
	    sParaTemp.put("_input_charset", AlipayConfig.input_charset);
	    sParaTemp.put("page_no", "1");
	    return AlipaySubmit.buildRequest("", "", sParaTemp);
	 }
	private Map<String, String> getvalues(String xmlValue){
		Map<String, String> map = new HashMap<String, String>();
		try {
			Document document = DocumentHelper.parseText(xmlValue);
			Element root = document.getRootElement();
			Iterator<Element> iter = root.elementIterator();
			while (iter.hasNext()) {
			  Element element = (Element)iter.next();
			  if ("response".equals(element.getName())) {
			    Iterator iterator = element.elementIterator();
			    while (iterator.hasNext()) {
			      Element trade = (Element)iterator.next();
			      if ("trade".equals(trade.getName())) {
			        Iterator tradeIter = trade.elementIterator();
			        while (tradeIter.hasNext()) {
			          Element e = (Element)tradeIter.next();
			          if("actual_charge_amount".equals(e.getName())||"total_fee".equals(e.getName())||"trade_no".equals(e.getName())){
			        	  map.put(e.getName(), e.getText());
			          }
			        }
			      }
			    }
			  }
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	    return map;
	}
}
