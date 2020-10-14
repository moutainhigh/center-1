package com.cmall.ordercenter.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 订单基本数据
 * 
 * @author wangkecheng
 * 
 */
public class OrderAnalyseServiceJob extends RootJob {

	public void doExecute(JobExecutionContext context) {
		String flag = this.doStatis();
		//System.out.println("flag :" + flag);
	}

	public String doStatis_() {
		//System.out.println("statistic .........");

		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_analyse").upTemplate();
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("flag", Types.INTEGER));

		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(
				new CallableStatementCreator() {
					public CallableStatement createCallableStatement(Connection conn) throws SQLException {
						final String callFunctionSql = "{call p_order_alys(?)}";
						CallableStatement cstmt = conn.prepareCall(callFunctionSql);
						cstmt.registerOutParameter("flag", Types.INTEGER);
						return cstmt;
					}
				}, params);
		Object _return = outValues.get("flag");
		return _return == null ? "-1" : _return.toString();
	}

	public String doStatis() {
		//System.out.println("statistic .........");

		DbTemplate jdbcTemplate = DbUp.upTable("oc_order_analyse").upTemplate();
		
		Object objReturn = jdbcTemplate.getJdbcOperations().execute(
				
				new CallableStatementCreator() {
					public CallableStatement createCallableStatement(Connection con) throws SQLException {
						CallableStatement cs = con.prepareCall("{call p_order_alys(?)}");
						cs.registerOutParameter("flag", Types.INTEGER);// 注册输出参数的类型
						return cs;
					}
				}, new CallableStatementCallback<Object>() {
					public Object doInCallableStatement(CallableStatement cs)throws SQLException, DataAccessException {
						cs.execute();
						return cs.getObject("flag");// 获取输出参数的值
					}
				});
		//System.out.println("objReturn :"+objReturn);
		return objReturn.toString();
	}

	public static void main(String[] d) {
		OrderAnalyseServiceJob xx = new OrderAnalyseServiceJob();
		//System.out.println("d :" + xx.doStatis());
	}
}
