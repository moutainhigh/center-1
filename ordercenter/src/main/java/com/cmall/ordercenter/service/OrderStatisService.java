package com.cmall.ordercenter.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.model.Statis;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 订单统计
 * @author wangkecheng
 *
 */
public class OrderStatisService  extends RootJob{
	
	public void doExecute(JobExecutionContext context) {
		//String flag = this.getSatisData(this.getDate(-1));
		//String flag = this.getSatisData("2013-11-08");
		//String flag = this.getSatisDataJDBC("2014-02-21");
		//String flag = this.getSatisDataJDBC(this.getDate(-1));
		
		String flag = this.getSatisDataJDBC();
	//	System.out.println("flag :"+flag);
	}
//	public String getSatisData(final String date){
//		System.out.println("statistic order ..........");
//		
//		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_statistics").upTemplate();
//		List<SqlParameter> params = new ArrayList<SqlParameter>();
//		params.add(new SqlParameter("startDate", Types.VARCHAR));
//		params.add(new SqlOutParameter("flag", Types.INTEGER));
//		
//		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
//			       new CallableStatementCreator() {  
//			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
//			              final String callFunctionSql = "{call p_order_statistics(?, ?)}";
//			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
//			              cstmt.setString(1, date); 
//			              cstmt.registerOutParameter(2, Types.INTEGER);
//			              return cstmt;  
//			            }
//			       },
//			       params); 
//		Object _return = outValues.get("flag");
//		return _return==null?"-1":_return.toString();
//	}
	/**
	 * 使用JDBC
	 * 统计前一天的订单数据
	 * @param date : 前一天的日期
	 * @return
	 */
//	public String getSatisDataJDBC(final String date){
//		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_statistics").upTemplate();
//		
//		StringBuilder sb = new StringBuilder();
//		sb.append("select a.seller_code, a.product_name ,a.order_status , b.product_code,b.sku_code, b.sku_name, sum(b.sku_num) as total_sku_sort_num");
//		sb.append(" FROM oc_orderinfo a ,oc_orderdetail b");
//		sb.append(" where a.order_code = b.order_code and a.order_status <> '4497153900010007' and a.create_time BETWEEN :create_time1 and :create_time2");
//		sb.append(" GROUP BY b.sku_code,a.order_status");
//		
//		 Map<String, Object> paramMap = new HashMap<String, Object>(); 
//		 paramMap.put("create_time1", date);
//		 paramMap.put("create_time2", this.getSpecifiedDay(date, 1));
//		 
//		 List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString(), paramMap);
//		 System.out.println("list size : "+list.size());
//		 for(Map<String, Object> map : list){
//			 map.put("create_time", date);//订单日期
//			 paramMap.put("sku_code", map.get("sku_code").toString());
//			 Integer n = jdbcTemplate.queryForObject("SELECT COUNT(zid) FROM `ordercenter`.`oc_order_statistics` WHERE sku_code =:sku_code and create_time =:create_time1", paramMap,Integer.class);
//			 //插入
//			 if(n == 0){
//				 Integer total_num = jdbcTemplate.queryForObject("select sum(b.sku_num) FROM oc_orderinfo a ,oc_orderdetail b where a.order_code = b.order_code and a.order_status <> '4497153900010007' and b.sku_code =:sku_code and a.create_time BETWEEN :create_time1 and :create_time2", paramMap,Integer.class);
//				map.put("total_num", total_num);
//				
//				jdbcTemplate.update("INSERT INTO `ordercenter`.`oc_order_statistics`(seller_code, product_name ,create_time, product_code,sku_code,sku_name,total_num) value (:seller_code, :product_name ,:create_time, :product_code,:sku_code,:sku_name,:total_num)", map);
//			 }
//			 //根据sku_code，create_time 插入分类总数
//			 String volume = "";
//			 String order_status = map.get("order_status").toString();
//			 if("4497153900010001".equals(order_status)){//未付款
//				 volume = "no_pay_num";
//			 }else if("4497153900010002".equals(order_status)){//未发货
//				 volume = "no_send_num";
//			 }else if("4497153900010003".equals(order_status)){//已发货
//				 volume = "send_num";
//			 }else if("4497153900010005".equals(order_status)){//交易成功
//				 volume = "success_num";
//			 }else if("4497153900010006".equals(order_status)){//交易失败
//				 volume = "failure_num";
//			 }
//			  
//			 jdbcTemplate.update("UPDATE `ordercenter`.`oc_order_statistics` SET "+volume+" = :total_sku_sort_num WHERE sku_code = :sku_code AND create_time = :create_time", map);
//		 }
//		 
//		 return "ok";
//	}
	
	/**
	 * 新版 2014-5-21
	 * 
	 * 不统计当天的
	 * 
	 * @param date
	 * @return
	 */
	public String getSatisDataJDBC(){
		
		//存放所有SKU
		Map<String, Statis> skuMap = new HashMap<String, Statis>();
		 
		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_statistics").upTemplate();
		
		StringBuilder sb = new StringBuilder();
		sb.append("select a.seller_code, a.product_name ,a.order_status , b.product_code,b.sku_code, b.sku_name, sum(b.sku_num) as total_sku_sort_num, DATE_FORMAT(a.create_time,'%Y-%m-%d') as orderDate");
		sb.append(" FROM oc_orderinfo a ,oc_orderdetail b");
		sb.append(" where a.order_code = b.order_code and a.order_status <> '4497153900010007' AND DATE_FORMAT(a.create_time,'%Y-%m-%d') <> DATE_FORMAT(NOW(),'%Y-%m-%d')");
		sb.append(" GROUP BY b.sku_code,a.order_status,orderDate");
		sb.append(" ORDER BY a.create_time ");
		
		 List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString(), new HashMap<String, Object>());
		// System.out.println("list size : "+list.size());
		 for(Map<String, Object> map : list){
		
			 Integer n = jdbcTemplate.queryForObject("SELECT COUNT(zid) FROM `ordercenter`.`oc_order_statistics` WHERE sku_code =:sku_code and create_time =:orderDate", map,Integer.class);
			 //插入
			 if(n == 0){
				 Integer total_num = jdbcTemplate.queryForObject("select sum(b.sku_num) FROM oc_orderinfo a ,oc_orderdetail b where a.order_code = b.order_code and a.order_status <> '4497153900010007' and b.sku_code =:sku_code and DATE_FORMAT(a.create_time,'%Y-%m-%d') = :orderDate", map,Integer.class);
				map.put("total_num", total_num);
				
				jdbcTemplate.update("INSERT INTO `ordercenter`.`oc_order_statistics`(seller_code, product_name ,create_time, product_code,sku_code,sku_name,total_num) value (:seller_code, :product_name ,:orderDate, :product_code,:sku_code,:sku_name,:total_num)", map);
			 }
			
			 String key = map.get("sku_code").toString()+map.get("orderDate");
			 Statis s = skuMap.get(key);
			 if(s == null){
				 s = new Statis();
			 }
			
			 
			 //根据sku_code，create_time 插入分类总数
			 String volume = "";
			 String order_status = map.get("order_status").toString();
			 int total_sku_sort_num = Integer.valueOf(map.get("total_sku_sort_num").toString());
			 
			 if("4497153900010001".equals(order_status)){//未付款
				 //volume = "no_pay_num";
				 s.setNo_pay_num(total_sku_sort_num);
			 }else if("4497153900010002".equals(order_status)){//未发货
				 //volume = "no_send_num";
				 s.setNo_send_num(total_sku_sort_num);
			 }else if("4497153900010003".equals(order_status)){//已发货
				 //volume = "send_num";
				 s.setSend_num(total_sku_sort_num);
			 }else if("4497153900010005".equals(order_status)){//交易成功
				 //volume = "success_num";
				 s.setSuccess_num(total_sku_sort_num);
			 }else if("4497153900010006".equals(order_status)){//交易失败
				 //volume = "failure_num";
				 s.setFailure_num(total_sku_sort_num);
			 }
			  
			// jdbcTemplate.update("UPDATE `ordercenter`.`oc_order_statistics` SET "+volume+" = :total_sku_sort_num WHERE sku_code = :sku_code AND create_time = :orderDate", map);
			
			 map.put("no_pay_num", s.getNo_pay_num());
			 map.put("no_send_num", s.getNo_send_num());
			 map.put("send_num", s.getSend_num());
			 map.put("success_num", s.getSuccess_num());
			 map.put("failure_num", s.getFailure_num());
			 
			 jdbcTemplate.update("UPDATE `ordercenter`.`oc_order_statistics` SET no_pay_num = :no_pay_num,  no_send_num = :no_send_num,  send_num = :send_num,  success_num = :success_num, failure_num = :failure_num  WHERE sku_code = :sku_code AND create_time = :orderDate", map);
			 
			 skuMap.put(key, s);
		 }
		 
		 return "ok";
	}
	
	
	public String getSatisDataJDBCTest(final String date){
		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_statistics").upTemplate();
		 Map<String, Object> paramMap = new HashMap<String, Object>(); 
		 paramMap.put("seller_code", "SI10000");
//		 List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from oc_orderinfo where seller_code=:seller_code", paramMap);
//		 System.out.println("list size : "+list.size());
		 Integer n = jdbcTemplate.queryForObject("select count(1) from oc_orderinfo where seller_code=:seller_code", paramMap, Integer.class);
		// System.out.println("n :"+n);
		 return "ok";
	} 
//	public void getSatisDateTest(final String productCode,final String productName){
//		
//		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_statistics").upTemplate();
//		List<SqlParameter> params = new ArrayList<SqlParameter>();
//		params.add(new SqlOutParameter("productCode", Types.VARCHAR));
//		params.add(new SqlOutParameter("productName", Types.VARCHAR));
//		params.add(new SqlParameter("sellerCode", Types.VARCHAR));
//		params.add(new SqlParameter("createDate", Types.VARCHAR));
//		
//		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
//				
//			       new CallableStatementCreator() {  
//			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
//			              final String callFunctionSql = "{call p_test(?, ?, ?,?)}";
//			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
//			              cstmt.registerOutParameter(1, Types.VARCHAR);
//			              cstmt.registerOutParameter(2, Types.VARCHAR);
//			              cstmt.setString(3, productCode);  
//			              cstmt.setString(4, productName); 
//			              return cstmt;  
//			            }
//			       }, 
//			       params); 
//		
//		System.out.println("productCode :"+ outValues.get("productCode"));
//		System.out.println("productName :"+ outValues.get("productName"));
//	}
	
	public  String getDate(int d){
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.DATE, d); 
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()).trim();
	}
	/**
	 * 取指定日期的前几天或 后几天
	 * @param specifiedDay
	 * @param n
	 * @return
	 */
	public String getSpecifiedDay(String specifiedDay,int n) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + n);

        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }
}
