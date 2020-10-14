package com.cmall.ordercenter.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 系统信息统计
 * @author wangkecheng
 *
 */
public class SysStatisService  extends RootJob{
	
	public void doExecute(JobExecutionContext context) {
		String flag = this.getSatisData(this.getDate(-1));
		//System.out.println("flag :"+flag);
	}
	public String getSatisData(final String date){
		//System.out.println("system statistic ..........");
		 
		DbTemplate jdbcTemplate = DbUp.upTable("pc_daily_statis").upTemplate();
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlParameter("startDate", Types.VARCHAR));
		params.add(new SqlOutParameter("flag", Types.INTEGER));
		
		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
			       new CallableStatementCreator() {  
			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
			              final String callFunctionSql = "{call p_daily_statis(?, ?)}";
			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
			              cstmt.setString(1, date); 
			              cstmt.registerOutParameter(2, Types.INTEGER);
			              return cstmt;  
			            }
			       },
			       params); 
		Object _return = outValues.get("flag");
		return _return==null?"-1":_return.toString();
	}
	public  String getDate(int d){
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.DATE, d); 
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()).trim();
	}
	
}
