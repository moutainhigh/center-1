package com.cmall.ordercenter.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.quartz.JobExecutionContext;
import com.cmall.ordercenter.model.AccountDetail;
import com.cmall.ordercenter.model.AccountInfo;
import com.cmall.ordercenter.service.AccountInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 根据配置时间段进行商家的结算单跑批
 * 根据配置的每月几号进行开始结算，当配置的号为空时，代表月底
 * @author jack
 * @version 1.0
 * 
 * 
 */
public class TaskScheduleForAccount extends RootJob {

	/**
	 *总调度 
	 */
	public void doExecute(JobExecutionContext context) {
		try {
			Calendar orStart = Calendar.getInstance();//订单统计开始时间(上个月第一天)
			Calendar orEnd = Calendar.getInstance();//订单统计结束时间(上个月最后一天)
			Calendar reStart = Calendar.getInstance();//退单统计开始时间(上个月结算日一天)
			Calendar reEnd = Calendar.getInstance();//退单统计结束时间(当前结算日)
			int day=orStart.get(Calendar.DATE);//获取当前日
			String dayNum = bConfig("familyhas.account_date");
			if(day==Integer.valueOf(dayNum)){
				orStart.add(Calendar.MONTH, -1);
				orStart.set(Calendar.DAY_OF_MONTH, 1);
				orEnd.set(Calendar.DAY_OF_MONTH, 1); 
				MDataMap dataMap = DbUp.upTable("oc_account_task").oneWhere("", "zid = (select max(zid) from oc_account_task)", "");
				if(dataMap!=null&&!dataMap.isEmpty()){
					String str=dataMap.get("end_time");//上一次结算结束日期作为本次开始日期
					SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date =sdf.parse(str);
					reStart.setTime(date);
				}else {
					reStart.add(Calendar.MONTH, -1);
					reStart.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayNum));
				}
				reEnd.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayNum));
				bLogInfo(0, "结算任务开始，现在时间为：",DateUtil.getSysDateTimeString());
				String start = DateUtil.toString(orStart.getTime(), "yyyy-MM-dd")+" 00:00:00";
				String end = DateUtil.toString(orEnd.getTime(),"yyyy-MM-dd")+" 00:00:00";
				String tuiStart = DateUtil.toString(reStart.getTime(), "yyyy-MM-dd")+" 00:00:00";
				String tuiEnd = DateUtil.toString(reEnd.getTime(),"yyyy-MM-dd")+" 00:00:00";
				bLogInfo(0, "结算起始日期:",start);
				bLogInfo(0, "结算截止日期:",end);
				boolean flag = taskForAccount(start, end,tuiStart,tuiEnd);
				if(flag){
					bLogInfo(0,"结算任务结束，现在时间为：",DateUtil.getSysDateTimeString());
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 结算总方法
	 * 
	 * @param list 需要结算的店铺
	 */
	public boolean taskForAccount(String start_time, String end_time,String tuiStart,String tuiEnd) {
		
		boolean flag = true;
		try {
			//判断是否存在本时间段的结算数据，如存在先删除
			flag = deRedundantData(start_time, end_time);
			if(flag){
				//成功订单号
				MDataMap orderMap = getDDCodes(start_time, end_time);
				String orderCodes = getInSql(orderMap,null,null, "");
				
				//所有付款成功订单
				Map<String, AccountInfo> orderMoney = OrderAmount(orderCodes,orderMap,start_time,end_time);
				
				//已退货成功订单
				Map<String, AccountInfo> returnMoney = reAmount(orderMoney,tuiStart,tuiEnd);
				
				//根据店铺进行总结算
				flag = accountinfo(returnMoney);
				//记录跑批日志
				DbUp.upTable("oc_account_task").insert("start_time",tuiStart,"end_time",tuiEnd,"create_time",DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
			}else {
				bLogInfo(0,"结算任务出现错误，无法清除同时间段内的冗余数据,请人工删除");
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 *订单和退单总结算 
	 * 
	 * @param Map<seller_code,AccountInfo>
	 */
	private boolean accountinfo(Map<String, AccountInfo> accountinfos){
		boolean flag = true;
		try {
			Iterator<String> sellerCodes = accountinfos.keySet().iterator();
			AccountInfoService service = new AccountInfoService(); 
			while (sellerCodes.hasNext()) {
				AccountInfo info = accountinfos.get(sellerCodes.next());//店铺结算信息
				List<AccountDetail> reOrder = info.getReOrders();//退款订单
				Iterator<AccountDetail> iterator = reOrder.iterator();
				while (iterator.hasNext()) {
					AccountDetail detail = iterator.next();
					info.setAccount_amount(info.getAccount_amount().add(detail.getAccount_money()));
					info.setSellershare_amount(info.getSellershare_amount().add(detail.getAccount_money()));
				}
				service.saveAccountInfo(info);
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 所有付款成功的订单总金额(手续费金额按照订单表的payed_money进行计算)
	 * 
	 * @param ddMap<订单编号,完成时间>
	 * @param  map<订单编号,cps金额> 订单cps金额
	 * @param  start_time 结算起始日期
	 * @param  end_time 结算结束日期
	 * 
	 * @return Map<店铺号，Map<付款成功总金额>>
	 */
	private Map<String, AccountInfo> OrderAmount(String orderCodes,MDataMap orderMap,String start_time,String end_time) {
		//所有付款成功的订单 map<店铺号,本店铺成功订单>
		Map<String,List<MDataMap>> orders = getSuccessOrders(orderCodes);
		//获取所有订单内商品的总的成本价
		MDataMap productCPMap = this.getProductCostPrice(orderCodes);
		Map<String, AccountInfo> re = new HashMap<String, AccountInfo>();
		if(!orders.isEmpty()){
			Iterator<String> iterator = orders.keySet().iterator();
			while (iterator.hasNext()) {//循环店铺
				String sellerCode = iterator.next();
				AccountInfo info = new AccountInfo();//结算单
//AccountInfo    account_amount结算总金额--return_amount退货手续费金额--procedure_amount手续费金额 --sellershare_amount结算金额（商家结算金额）--storeshare_amount商城分成金额   
				info.setSeller_code(sellerCode);
				info.setStart_time(start_time);
				info.setEnd_time(end_time);
				info.setAccount_status("4497153900030001");
				info.setCreate_time(DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
				info.setCreate_user("系统");
				List<MDataMap> orderLi = orders.get(sellerCode);
				if(!orderLi.isEmpty()){
					List<AccountDetail> details = new ArrayList<AccountDetail>();
					for (int i = 0; i < orderLi.size(); i++) {//循环店铺下所有订单
						AccountDetail detail = new AccountDetail();
						MDataMap order = orderLi.get(i);
						detail.setOrder_code(order.get("order_code"));
						detail.setPay_type(order.get("pay_type"));
						detail.setOrder_money(new BigDecimal(Double.valueOf(productCPMap.get(detail.getOrder_code()))));//单个订单总金额为订单金额之和
						detail.setRemark("交易成功订单");
						detail.setAccount_time(DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
						detail.setOrder_time(orderMap.get(order.get("order_code")));
						if(re.containsKey(sellerCode)){//已存在此商家的结算
							//商家结算金额sellershare_amount
							info.setSellershare_amount(info.getSellershare_amount().add(detail.getOrder_money()));
						}else {//不存在此商家的结算
							//商家结算金额sellershare_amount=订单金额-cps金额
							info.setSellershare_amount(detail.getOrder_money());
						}
						//结算总金额
						info.setAccount_amount(info.getSellershare_amount());
						//本单结算金额=订单金额-商城分成金额
						detail.setAccount_money(detail.getOrder_money());
						details.add(detail);
						info.setList(details);
						re.put(info.getSeller_code(), info);
					}
				}
			}
		}
		return re;
	}

	/**
	 * 付款成功的订单
	 * 
	 * @param codes
	 *            {@link #getCodes(String, String)}
	 * 
	 * @return map<店铺号,本店铺成功订单>
	 */
	private Map<String,List<MDataMap>> getSuccessOrders(String codes) {
		List<MDataMap> list = new ArrayList<MDataMap>();
		if(codes!=null&&!"".equals(codes)){
			list = DbUp.upTable("oc_orderinfo")
					.queryAll("order_code,small_seller_code,pay_type","", "order_code "+codes, new MDataMap());
		}
		Map<String, List<MDataMap>> map = new HashMap<String, List<MDataMap>>();
		if(!list.isEmpty()){
			for(int i=0;i<list.size();i++) {
				MDataMap m = list.get(i);
				List<MDataMap> value = new ArrayList<MDataMap>();
				if(map.containsKey(m.get("small_seller_code"))){
					value = map.get(m.get("small_seller_code"));
				}
				value.add(m);
				map.put(m.get("small_seller_code"), value);
			}
		}
		return map;
	}

	/**
	 * 付款成功的订单号
	 * 
	 * @param start_time
	 * @param end_time
	 * 
	 * @return String
	 */
	private MDataMap getDDCodes(String start_time, String end_time) {
		MDataMap map = new MDataMap();
		try {
			map.put("create_time_from", start_time);
			map.put("create_time_end", end_time);
			map.put("status", "4497153900010005");
			map.put("pay_type", "449716200001");
			List<Map<String, Object>> list= DbUp.upTable("lc_orderstatus").dataSqlList("select a.code,a.create_time from logcenter.lc_orderstatus a,ordercenter.oc_orderinfo b " +
					"where a.code=b.order_code and b.small_seller_code like 'SF03%' and b.pay_type=:pay_type " +
					"and a.create_time>=:create_time_from and a.create_time<=:create_time_end and now_status=:status", map);
			map.clear();
			if(!list.isEmpty()){
				Iterator<Map<String, Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					Map<String, Object> m =  iterator.next();
					map.put(m.get("code").toString(), m.get("create_time").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 所有退款成功的订单总金额
	 * 
	 * @param orderMoney<商家编号,结算信息>
	 * @param  start_time 结算起始日期
	 * @param  end_time 结算结束日期
	 * 
	 * @return Map<店铺号，Map<付款成功总金额>>
	 */
	private Map<String, AccountInfo> reAmount(Map<String, AccountInfo> AllOrder,String start_time,String end_time) {
		List<MDataMap> returnOrders = getReturnMoneyMap(start_time,end_time);
		Iterator<MDataMap> iterator = returnOrders.iterator();
		String reorderCodes = getInSql(null, null,returnOrders, "order_code");
		MDataMap reOrderMon = this.getProductCostPrice(reorderCodes);
		while (iterator.hasNext()) {//遍历存在退款数据的订单
			/**已退款的退货单  <return_money_code,online_money,poundage,return_reason,seller_code,order_code,create_time>>*/
			MDataMap reOrder = iterator.next();//退款单
			AccountInfo info = new AccountInfo();
			List<AccountDetail> details = new ArrayList<AccountDetail>();
			AccountDetail de = new AccountDetail();
			if(reOrder.get("small_seller_code")!=null&&!"".equals(reOrder.get("small_seller_code"))){//成功订单中已有本店铺数据
				if(AllOrder.containsKey(reOrder.get("small_seller_code")))
				{
					info = AllOrder.get(reOrder.get("small_seller_code"));
					details = info.getReOrders();
				}else{//未有成功付款订单
					info.setSeller_code(reOrder.get("small_seller_code").toString());
					info.setStart_time(start_time);
					info.setEnd_time(end_time);
					info.setAccount_status("4497153900030001");
					info.setCreate_time(DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
					info.setCreate_user("系统");
				}
				de.setReturn_code(reOrder.get("return_code"));
				de.setOrder_code(reOrder.get("order_code"));
				de.setAccount_time(DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));//结算时间
				de.setReturn_money(new BigDecimal(0.00).subtract(new BigDecimal(Double.valueOf(reOrderMon.get(de.getOrder_code())))));
				de.setAccount_money(de.getReturn_money());//结算金额  负
				de.setRemark("买家退货");//备注
				details.add(de);
				info.setReOrders(details);
				AllOrder.put(reOrder.get("small_seller_code").toString(), info);
			}
		}
		return AllOrder;
	}
	

	/**
	 * 将Map或者list转为sql语句
	 * 
	 */
	private String getInSql(MDataMap map,List<Map<String, Object>> list,List<MDataMap> list2,String name){
		String sql = "";
		if(list!=null&&!list.isEmpty()&&name!=null&&!"".equals(name)){
			for(Map<String, Object> m:list){
				Object code = m.get(name);
				if("".equals(sql)&&code!=null&&!"".equals(code.toString().trim())){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code.toString().trim())){
					sql+=",'"+code+"'";
				}
			}
		}else if(map!=null&&!map.isEmpty()){
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String code = iterator.next().toString();
				if("".equals(sql)&&code!=null&&!"".equals(code.toString().trim())){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code.toString().trim())){
					sql+=",'"+code+"'";
				}
			}
		}else if(list2!=null&&!list2.isEmpty()&&name!=null&&!"".equals(name)){
			for(MDataMap m:list2){
				Object code = m.get(name);
				if("".equals(sql)&&code!=null&&!"".equals(code.toString().trim())){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code.toString().trim())){
					sql+=",'"+code+"'";
				}
			}
		}
		if(!"".equals(sql)){
			sql+=")";
		}
		return sql;
	}
	
	/**
	 *判断是否存在本时间段的结算信息，如存在先删除 (用于结算跑批出错再次跑批时针对出错结算数据进行删除)
	 * 
	 * @param start_time
	 * @param end_time
	 */
	private boolean  deRedundantData(String start_time,String end_time){
		boolean flag = true;
		try {
			MDataMap map = new MDataMap();
			map.put("start_time", start_time);
			map.put("end_time", end_time);
			List<Map<String, Object>> result =  DbUp.upTable("oc_accountinfo").dataQuery("account_code", "", "", map, 0, 0);
			String sql = getInSql(null, result, null,"account_code");
			DbUp.upTable("oc_accountinfo").dataDelete(" account_code "+sql, new MDataMap(), "");
			DbUp.upTable("oc_accountinfo_relation").dataDelete(" account_code "+sql, new MDataMap(), "");
		} catch (Exception e) {
			flag =  false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 *获取退货单对应的退单金额 
	 * 
	 * @param start_time 
	 * @param end_time
	 * 
	 * @return List<return_money_code,online_money,poundage,return_reason,seller_code,order_code,create_time>
	 */
	private List<MDataMap> getReturnMoneyMap(String start_time,String end_time){
		List<MDataMap> result = new ArrayList<MDataMap>();
		try {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("create_time_from", start_time);
			mDataMap.put("create_time_end", end_time);
//			mDataMap.put("status", "4497153900040001");
			//退款单号、订单编号、店铺编号
			result = DbUp.upTable("oc_return_goods").queryAll("return_code,order_code,small_seller_code", "", "create_time>=:create_time_from and create_time<=:create_time_end and small_seller_code like 'SF03%'", mDataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 *获取订单内商品成本价之和
	 * @orderCodes
	 * 
	 */
	private MDataMap getProductCostPrice(String orderCodes){
		MDataMap map = new MDataMap();
		String sql = "select a.order_code as order_code,SUM(b.cost_price*a.sku_num) as sellerCount " +
				"from ordercenter.oc_orderdetail a,productcenter.pc_productinfo b " +
				"where a.product_code = b.product_code and a.gift_flag='1' and a.order_code "+orderCodes+" GROUP BY a.order_code ";
		List<Map<String, Object>> li = DbUp.upTable("pc_productinfo").dataSqlList(sql, new MDataMap());
		if(li!=null&&!li.isEmpty()){
			for (Map<String, Object> mm : li) {
				if(mm!=null&&!mm.isEmpty()&&mm.containsKey("order_code")&&!"".equals(mm.get("order_code")))
				map.put(mm.get("order_code").toString(), mm.get("sellerCount").toString());
			}
		}
		return map;
	}
}
