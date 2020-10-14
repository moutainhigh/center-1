package com.cmall.bbcenter.webfunc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 结算 
 * @author wangkecheng
 *s
 */
public class FuncBalance extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String flag = ""; 
		if (mResult.upFlagTrue()) {
			
			String user = UserFactory.INSTANCE.create().getLoginName();
					//UserFactory.INSTANCE.create().getManageCode();
			String balanceCode = WebHelper.upCode("BLA");
			String purchaseorder_code = _mDataMap.get("purchaseorder_code");
			String balance_money = _mDataMap.get("balance_money");
			String tax_code = _mDataMap.get("tax_code");
			String tax_date = _mDataMap.get("tax_date");
			String tax_total = _mDataMap.get("tax_total");
			String tax = _mDataMap.get("tax");
			
			//System.out.println(purchaseorder_code);
			//System.out.println(balance_money);
			//System.out.println(tax_code);
			//System.out.println(tax_date);
			//System.out.println(tax_total);
			//System.out.println(tax);
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("purchaseorder_code", purchaseorder_code);
			map.put("balance_money",balance_money );
			
			map.put("tax_code", tax_code);
			map.put("tax_date",tax_date );
			map.put("tax_total", tax_total);
			map.put("tax", tax);
			
			map.put("balance_code", balanceCode);
			map.put("balance_time", "");
			map.put("balance_user",user );
			
			//DbUp.upTable(mPage.getPageTable()).dataInsert(map);
			flag = this.doProcedure(map);
			//System.out.println("procdure return str :"+flag);
		}
//		if (mResult.upFlagTrue()) {
//			mResult.setResultMessage(bInfo(969909001));
//		}
		if("0".equals(flag)){
			mResult.setResultMessage("不能大于结算金额");
		}else if("1".equals(flag)){
			mResult.setResultMessage("结算成功");
		}else if("3".equals(flag)){
			mResult.setResultMessage("已结算完成，不需再结算");
		}else if("-1".equals(flag)){
			mResult.setResultMessage("结算失败");
		}
		return mResult;
	}
	
	
	private String doProcedure(final Map<String,String> map){
		
		DbTemplate jdbcTemplate = DbUp.upTable("bc_purchase_order").upTemplate();
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("flag", Types.INTEGER));
		
		Map<String, Object> outValues = jdbcTemplate.getJdbcOperations().call(  
			       new CallableStatementCreator() {  
			            public CallableStatement createCallableStatement(Connection conn) throws SQLException {  
			              final String callFunctionSql = "{call p_balance(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			              
			              double balance_money = Double.valueOf(map.get("balance_money"));
			              double tax_total ;
			              double tax;
//			              double balance_money = Double.valueOf(map.get("balance_money"));
//			              double balance_money = Double.valueOf(map.get("balance_money"));
			              
			              String reg = "^(\\d)+(\\.(\\d)+)?$";
			              Pattern p = Pattern.compile(reg);
			              
			              if(p.matcher(map.get("tax_total")).find()){
			            	  tax_total = Double.valueOf(map.get("tax_total"));
			              }else{
			            	  tax_total = 0;
			              }
			              if(p.matcher(map.get("tax")).find()){
			            	  tax = Double.valueOf(map.get("tax"));
			              }else{
			            	  tax = 0;
			              }
			              
			              CallableStatement cstmt = conn.prepareCall(callFunctionSql); 
			              cstmt.registerOutParameter(1, Types.INTEGER);
			              cstmt.setString(2, map.get("balance_code"));
			              cstmt.setString(3, map.get("purchaseorder_code"));
			              cstmt.setBigDecimal(4,BigDecimal.valueOf(balance_money));
			              cstmt.setString(5, map.get("balance_user"));
			              cstmt.setString(6, map.get("tax_code"));
			              cstmt.setString(7, map.get("tax_date"));
			              cstmt.setBigDecimal(8, BigDecimal.valueOf(tax_total));
			              cstmt.setBigDecimal(9, BigDecimal.valueOf(tax));
			              
			              
			              return cstmt;  
			            }
			       },
			       params); 
		Object _return = outValues.get("flag");
		return _return==null?"-1":_return.toString();
	}

}
