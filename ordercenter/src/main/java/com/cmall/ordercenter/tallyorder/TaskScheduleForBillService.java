package com.cmall.ordercenter.tallyorder;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.tallyorder.exceldata.DeductMoneyImport;
import com.cmall.ordercenter.tallyorder.exceldata.FinalStatementExport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 订单结算
 * 
 * @author zmm
 *
 */
public class TaskScheduleForBillService extends RootJob {

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
				getPaySuccessCodes(start, end,tuiStart,tuiEnd);
//				if(flag){
//					bLogInfo(0,"结算任务结束，现在时间为：",DateUtil.getSysDateTimeString());
//				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据付款成功的订单号结算各个商户并生成财务结算单
	 * @param start_time 开始结算时间
	 * @param end_time 结算结束时间
	 * @param tuiStart 退货开始时间
	 * @param tuiEnd 退货结束时间
	 * @return
	 */
	private MDataMap getPaySuccessCodes(String start_time, String end_time,String tuiStart,String tuiEnd) {
		MDataMap map = new MDataMap();
		try {
			map.put("create_time_from", start_time);
			map.put("create_time_end", end_time);
			map.put("status", "4497153900010005");//4497153900010002测试
			map.put("pay_type", "449716200001");//目前测试需要更改sql
			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus")
					.dataSqlList("select a.code,a.create_time from logcenter.lc_orderstatus a,ordercenter.oc_orderinfo b "
									+ "where a.code=b.order_code and b.seller_code='SI2003' and b.small_seller_code like 'SF03%' and b.pay_type=:pay_type "
									+ "and a.create_time>=:create_time_from and a.create_time<=:create_time_end and now_status=:status",map);
			map.clear();
			if (!list.isEmpty()) {
				Iterator<Map<String, Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					Map<String, Object> m = iterator.next();
					map.put(m.get("code").toString(), m.get("create_time").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		settleProductDetailBill(map,tuiStart,tuiEnd);//结算自然月里各个商品详情
		settleMerchantBill(start_time,end_time);//结算自然月里各个商户详情
		settleFinanceBill(start_time,end_time,tuiStart,tuiEnd);//自然月财务总结算
		try {
			//System.out.println("All Data Successful !!!!!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 商品明细订单结算
	 * 根据有效订单在oc_orderdetail 获取每一个商品编号以及每一个商品所包含的sku_num的数量
	 * @param map 有效订单
	 */
	private void settleProductDetailBill(MDataMap map,String tuiStart,String tuiEnd) {
		MDataMap map1 = new MDataMap();
		MDataMap smallcodemap = new MDataMap();
		//MDataMap returnmap=getReturnCodeMapTest("2014-02-11 00:00:00", "2014-02-23 17:40:31");//获取退款的商品编号以及商品数量
		MDataMap returnmap=getReturnCodeMapTest(tuiStart, tuiEnd);//获取退款的商品编号以及商品数量
		String sql = JoinSql.getJoinOrderCode(map);
		//根据每一个订单获取每一组商品所包含的sku数量
		List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataSqlList("select order_code,product_code,sum(sku_num ) as sku_num from oc_orderdetail where order_code "+sql+" group by order_code,product_code", null);
		//取出sku_num product_code
		if (!list.isEmpty()) {
			for (Map<String, Object> mDataMap : list) {
				String product_code = mDataMap.get("product_code").toString();
				map1.put(product_code, product_code);
			}
		}
		String sql1 = JoinSql.getJoinProductCode(map1);// 拼接product_code in()
		//根据product_code 在pc_productinfo中查询 商品编号 商品名称 成本价 商户编号
		List<Map<String, Object>> productlist = DbUp.upTable("pc_productinfo")
				.dataSqlList("select product_code,product_name,cost_price,tax_rate,small_seller_code from pc_productinfo where product_code "+ sql1, null);
		Map<String, Map<String, Object>> ma = (Map<String, Map<String, Object>>) new HashMap<String, Map<String, Object>>();
		for (Map<String, Object> map3 : productlist) {
			ma.put((String) map3.get("product_code"), map3);
		}
		
		BigDecimal sku_num = null;
		BigDecimal return_sku=null;
		//根据product_code 迭代商品相关的信息 并存入 商户商品结算单明细表
		for (Map<String, Object> map3 : list) {
			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString().replace("-", "");
			Map<String, Object> md = ma.get((String) map3.get("product_code"));
			String order_code=map3.get("order_code").toString();
			 sku_num = new BigDecimal(Double.valueOf(map3.get("sku_num").toString()));
			BigDecimal first_sku_num = new BigDecimal(Double.valueOf(map3.get("sku_num").toString()));
			String product_code = md.get("product_code").toString();
		//循环迭代此商品是否属于退货的商品 若是 则用付款成功的总数量-退货的总数量
			if(returnmap.containsKey(order_code)){
				return_sku=first_sku_num;
			}else{
				return_sku =  new BigDecimal(0.00);
			}
			sku_num=first_sku_num.subtract(return_sku);//实际交易成功的数量
			smallcodemap.put(product_code, product_code);//提前准备excel表格查询条件
			String product_name = md.get("product_name").toString();
			BigDecimal cost_price = new BigDecimal(Double.valueOf((md.get("cost_price") == null ? 0.00 : md.get("cost_price")).toString()));
			//BigDecimal tax_rate = (BigDecimal) md.get("tax_rate");//税率
			BigDecimal invoice = (cost_price.multiply(sku_num));
			//BigDecimal period_money = invoice.multiply(tax_rate);
			String small_seller_code = md.get("small_seller_code").toString();
			BigDecimal rat=new BigDecimal(Double.valueOf((getMoneyProportionBySmallSellerCode(small_seller_code)==null? 0.00 :getMoneyProportionBySmallSellerCode(small_seller_code)).toString()));//质保金比例
			BigDecimal period_money=invoice.multiply(rat);//质保金
			BigDecimal actual_pay_amount=invoice.subtract(period_money);//实际付款
			DbUp.upTable("oc_bill_product_detail_new").insert("uid", uid,
					"product_code", product_code, "product_name", product_name,
					"cost_price", cost_price.toString(), "settle_count",sku_num.toString(),
					"invoice_amount", invoice.toString(),"period_money", period_money.toString(),
					"small_seller_code", small_seller_code.toString(),"actual_pay_amount",actual_pay_amount.toString());
		}
		//解析扣费模板 导入扣费信息 促销费用 邮费 平台管理费 其他
		//File file = new File("d:/test2.xls");
		//DeductMoneyImport.readExcel(file, smallcodemap);
		// 在商品结算明细表中存入所属  商户编号  商户名称
		getSellerNameBySmallSellerCode();
		//System.out.println("---------Insert dataBase successs!");
		//return null;
	}
	
	/**
	 * 根据商户编号 获取商户名称
	 * @return0
	 */
	private MDataMap getSellerNameBySmallSellerCode() {
		MDataMap map1 = new MDataMap();
		// 取出small_seller_code
		String sql1 = "select small_seller_code from oc_bill_product_detail_new where small_seller_code !=''";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new").dataSqlList(sql1, null);
		if (!list.isEmpty()) {
			Iterator<Map<String, Object>> iterator = list.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> m = iterator.next();
				map1.put(m.get("small_seller_code").toString(),m.get("small_seller_code").toString());
			}
		}
		String sql = JoinSql.getJoinSmallCode(map1);
		// 根据small_seller_code取出seller_name
		String sql2 = "select seller_name,small_seller_code from uc_sellerinfo where small_seller_code "+ sql;
		List<Map<String, Object>> list2 = DbUp.upTable("uc_sellerinfo").dataSqlList(sql2, null);
		if (!list2.isEmpty()) {
			Iterator<Map<String, Object>> iterator = list2.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> m = iterator.next();
				DbUp.upTable("oc_bill_product_detail_new").dataUpdate(new MDataMap("small_seller_code", m.get("small_seller_code").toString(), "seller_name",
								m.get("seller_name").toString()),"seller_name", "small_seller_code");
			}
		}
		return map1;
	}


	/**
	 * 商户结算
	 * @param start_time
	 * @param end_time
	 */
	private void settleMerchantBill(String start_time, String end_time) {
		String sql = "select small_seller_code,seller_name,sum(invoice_amount) as invoice_amount,"
				+ "sum(period_money) period_money,sum(actual_pay_amount) actual_pay_amount,"
				+ "sum(sale_money) as sale_money,sum(postage) as postage,sum(manage_money) as manage_money,"
				+ "sum(others) as others,sum(sale_money+postage+manage_money+others) as add_deduction from "
				+ "ordercenter.oc_bill_product_detail_new group by small_seller_code";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_product_detail_new").dataSqlList(sql, null);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		for (Map<String, Object> map1 : list) {
			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString().replace("-", "");
			String small_seller_code = map1.get("small_seller_code").toString();
			String seller_name = map1.get("seller_name").toString();
			String invoice_amount = map1.get("invoice_amount").toString();
			String period_money = map1.get("period_money").toString();
			String actual_pay_amount = map1.get("actual_pay_amount").toString();
			String settle_code = (small_seller_code + df.format(new Date()).toString());
			String add_deduction=map1.get("add_deduction").toString();
			DbUp.upTable("oc_bill_merchant_new").insert("uid", uid, "settle_code",settle_code, "merchant_code", small_seller_code,
					"merchant_name", seller_name, "invoice_amount",	invoice_amount, "period_money", period_money,"actual_pay_amount", actual_pay_amount,
					"add_deduction",add_deduction,"start_time",start_time,"end_time",end_time);
		}
		//System.out.println("oc_bill_merchant insert success! ");
	}

	/**
	 * 根据商户编号查出商户的质保金比例
	 * @param smallSellerCode
	 */
	private String getMoneyProportionBySmallSellerCode(String smallSellerCode) {
		String  sql="select money_proportion from uc_seller_info_extend where small_seller_code = " + "'"+smallSellerCode+"'";
		//System.out.println(sql);
		String moneyProportion = null ;//质保金比例
		//BigDecimal rat = null;
		Map<String, Object> map=DbUp.upTable("uc_seller_info_extend").dataSqlOne(sql, null);
		if (map!=null) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				moneyProportion = map.get(key).toString();
				//System.out.println(moneyProportion);
				if (moneyProportion == null) {
					moneyProportion = "0.00";
				}
				//rat = new BigDecimal(moneyProportion);
			}
		}
		//return rat;
		return moneyProportion;
	}

	
	/**
	 * 财务结算
	 */
	private void settleFinanceBill(String start_time,String end_time,String tuistart,String tuiend) {
		String sql = "select merchant_code,sum(invoice_amount) as invoice_amount,sum(period_money) as period_money ,sum(add_deduction) as add_deduction,"
				+ "sum(actual_pay_amount) as actual_pay_amount from oc_bill_merchant_new group by merchant_code";//需要加上结算条件
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_merchant_new")
				.dataSqlList(sql, null);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		for (Map<String, Object> map : list) {
			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString().replace("-", "");
			String small_seller_code = map.get("merchant_code").toString();
			String invoice_amount = map.get("invoice_amount").toString();
			String period_money = map.get("period_money").toString();
			String add_deduction = map.get("add_deduction").toString();
			String actual_pay_amount = map.get("actual_pay_amount").toString();
			DbUp.upTable("oc_bill_finance_amount").insert("uid",uid,"settle_code",df.format(new Date()).toString(),"settle_amount",
					invoice_amount,"current_period_money",period_money,"related_charges",add_deduction,"settle_pay_moeny",actual_pay_amount,
					"settle_period",start_time.substring(0, 10)+"至"+end_time.substring(0, 10),"settle_status","1","start_time",start_time,"end_time",end_time,
					"tuistart",tuistart,"tuiend",tuiend,"small_seller_code",small_seller_code);
		}
	}

	/**
	 * 获取退货的订单信息
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	private MDataMap getReturnCodeMapTest(String start_time, String end_time) {
		MDataMap map = new MDataMap();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("create_time_from", start_time);
		mDataMap.put("create_time_end", end_time);
		mDataMap.put("status", "4497465200190002");//退款成功
		// 在退货单中 根据订单号查询 已退款的订单
		String sql = "select order_code from oc_return_goods where create_time>=:create_time_from and create_time<=:create_time_end and status=:status ";
		List<Map<String, Object>> list = DbUp.upTable("oc_return_goods").dataSqlList(sql, mDataMap);
		//System.out.println("returnsize-----"+list.size());
		for (Map<String, Object> map1 : list) {
			String order_code = map1.get("order_code").toString();
				map.put(order_code, order_code);
		}
		return map;
	}
	
	public static void main(String[] args) {
		TaskScheduleForBillService tsf = new TaskScheduleForBillService();
		 tsf.getPaySuccessCodes("2014-02-11 00:00:00", "2014-02-23 17:40:31","2014-02-11 00:00:00", "2014-02-23 17:40:31");
	}
}
