package com.cmall.systemcenter.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.CallableStatementCreator;

import com.cmall.systemcenter.model.ScFlowMain;


public class CreateFlowCallableStatementCreator implements
		CallableStatementCreator {

	
	
	private ScFlowMain sfm = new ScFlowMain();
	
	public CreateFlowCallableStatementCreator(ScFlowMain sfm) {
		this.sfm=sfm;
	}

	public CallableStatement createCallableStatement(Connection con)
			throws SQLException {
		
		
		final String callProcedureSql = "{call proc_flow_create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
		CallableStatement cstmt = con.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		

		cstmt.setString(3, this.sfm.getFlowCode());
		cstmt.setString(4, this.sfm.getFlowType());
		cstmt.setString(5, this.sfm.getCurrentStatus());
		cstmt.setInt(6, this.sfm.getFlowIsend());
		cstmt.setString(7, this.sfm.getOuterCode());
		cstmt.setString(8, this.sfm.getCreator());
		cstmt.setString(9,this.sfm.getFlowTitle() );
		cstmt.setString(10, this.sfm.getFlowUrl());
		cstmt.setString(11, this.sfm.getFlowRemark());
		cstmt.setString(12, this.sfm.getNextOperators());
		cstmt.setString(13, this.sfm.getNextOperatorStatus());
		cstmt.setString(14, this.sfm.getNext_operator_id());
		
		return cstmt;
	}

}
