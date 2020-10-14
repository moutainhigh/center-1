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
 * 商品销量
 * @author wangkecheng
 *
 */
public class SalesVolumeServiceJob  extends RootJob{
	
	public void doExecute(JobExecutionContext context) {
		String flag = this.doStatis();
	//	System.out.println("flag :"+flag);
	}
	public String doStatis(){
	//	System.out.println("statistic ..........");
		
		DbTemplate jdbcTemplate = DbUp.upTable("oc_sales_volume").upTemplate();
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("gg", Types.INTEGER));
		
		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
			       new CallableStatementCreator() {  
			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
			              final String callFunctionSql = "{call p_product_sales_volume(?)}";
			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
			              cstmt.registerOutParameter(1, Types.INTEGER);
			              return cstmt;  
			            }
			       },
			       params); 
		Object _return = outValues.get("gg");
		return _return==null?"-1":_return.toString();
	}
}
