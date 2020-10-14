package com.cmall.groupcenter.job;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * 统计商户结算管理的预存款对账 数据
 * @author GaoYang
 *
 */
public class JobCountPreDepositOrderData extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		
		DbTemplate jdbcTemplate = DbUp.upTable("gc_count_preDeposit_data").upTemplate();
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("flag", Types.INTEGER));
//		System.out.print("begintime:"+ DateUtil.getSysDateTimeString());
		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
			       new CallableStatementCreator() {  
			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
			              final String callFunctionSql = "{call p_count_preDeposit_data(?)}";
			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
			              cstmt.registerOutParameter(1, Types.INTEGER);
			              return cstmt;  
			            }
			       },
			       params); 
		Object returnFlag = outValues.get("flag");
//		System.out.print("flag:"+returnFlag+",endtime:"+ DateUtil.getSysDateTimeString());
	}
	
//	public static void main(String[] args) {
//
//		JobCountPreDepositOrderData job = new JobCountPreDepositOrderData();
//
//		job.doExecute(null);
//	}

}
