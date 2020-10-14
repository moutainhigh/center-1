package com.cmall.productcenter.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;


import com.cmall.productcenter.common.SkuStockChangeCallableStatement;
import com.cmall.productcenter.common.SkuStockChangeLogCallableStatement;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.StockChangeLog;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;

/**   
*    
* 项目名称：productcenter   
* 类名称：StockChangeLogService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-3 下午2:21:40   
* 修改人：yanzj
* 修改时间：2013-9-3 下午2:21:40   
* 修改备注：   
* @version    
*    
*/
public class StockChangeLogService extends BaseClass {
	
	
	/**
	 * @param orderCode
	 * @param list
	 * @param changeType
	 * @param createUser
	 */
	public void AddStockChangeLog(String orderCode,List<ProductSkuInfo> list,String changeType,String createUser)
	{
		try {
			
			//调用 添加订单的存储过程
			List<SqlParameter> params = new ArrayList<SqlParameter>();
			params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
			params.add(new SqlOutParameter("error", Types.VARCHAR));
			params.add(new SqlParameter("detailStr", Types.VARCHAR));
			params.add(new SqlParameter("productsplit", Types.VARCHAR));
			params.add(new SqlParameter("itemsplit", Types.VARCHAR));
			params.add(new SqlParameter("orderCode", Types.VARCHAR));
			params.add(new SqlParameter("changeType", Types.VARCHAR));
			params.add(new SqlParameter("createUser", Types.VARCHAR));
			
			SkuStockChangeLogCallableStatement cscc = new SkuStockChangeLogCallableStatement(list, orderCode, changeType, createUser);

			DbTemplate dt = DbUp.upTable("lc_stockchange").upTemplate();
			Map<String, Object> outValues = dt.getJdbcOperations().call(cscc,params);

			
			String returnCode = outValues.get("outFlag").toString();
			if(!returnCode.equals("1")){
				bLogError(941901005, orderCode,returnCode);
			}
			
		} catch (Exception e) {
			bLogError(941901005, orderCode,"");
		}
	}
	
	/**
	 * @param statusLog
	 */
	public void AddStockChangeLog(StockChangeLog statusLog)
	{
		try
		{
			UUID uuid = UUID.randomUUID();
			
			MDataMap insertDatamap = new MDataMap();
			
			insertDatamap.put("uid", uuid.toString().replace("-", ""));
			insertDatamap.put("code", statusLog.getCode());
			insertDatamap.put("info", statusLog.getInfo());
			insertDatamap.put("create_time", statusLog.getCreateTime());
			insertDatamap.put("create_user", statusLog.getCreateUser());
			insertDatamap.put("change_stock", String.valueOf(statusLog.getChagneStock()));
			insertDatamap.put("change_type", statusLog.getChangeType());
			DbUp.upTable("lc_stockchange").dataInsert(insertDatamap);
		}
		catch(Exception ex)
		{
			bLogError(941901005, statusLog.getInfo(),statusLog.getCode());
		}
	} 

}
