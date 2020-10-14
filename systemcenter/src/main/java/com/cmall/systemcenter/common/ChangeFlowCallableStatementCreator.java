package com.cmall.systemcenter.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.CallableStatementCreator;

import com.cmall.systemcenter.model.ScFlowMain;

public class ChangeFlowCallableStatementCreator implements
		CallableStatementCreator {

	private ScFlowMain sfm = new ScFlowMain();
	
	private String fromStatus = "";
	private String toStatus = "";
	
	public ChangeFlowCallableStatementCreator(ScFlowMain sfm,String fromstatus,String tostatus) {
		this.sfm = sfm;
		this.fromStatus = fromstatus;
		this.toStatus = tostatus;
	}

	public CallableStatement createCallableStatement(Connection con)
			throws SQLException {
		
		
		final String callProcedureSql = "{call proc_flow_changestatus(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		CallableStatement cstmt = con.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		

		cstmt.setInt(3, this.sfm.getZid());
		cstmt.setString(4, this.sfm.getFlowCode());
		cstmt.setString(5, this.sfm.getFlowType());
		cstmt.setString(6, this.fromStatus);
		cstmt.setString(7, this.toStatus);
		cstmt.setInt(8, this.sfm.getFlowIsend());
		cstmt.setString(9,this.sfm.getUpdator());
		cstmt.setString(10, this.sfm.getFlowRemark());
		cstmt.setString(11, this.sfm.getNextOperators());
		cstmt.setString(12, this.sfm.getNextOperatorStatus());
		
		return cstmt;
	}

}
