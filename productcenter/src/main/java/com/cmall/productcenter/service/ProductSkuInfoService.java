package com.cmall.productcenter.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.common.SkuStockChangeCallableStatement;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.StockChangeLog;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;



/**   
*    
* 项目名称：productcenter   
* 类名称：ProductSkuInfoService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 上午11:18:55   
* 修改人：yanzj
* 修改时间：2013-9-2 上午11:18:55   
* 修改备注：   
* @version    
*    
*/
public class ProductSkuInfoService extends BaseClass {
	
	/**
	 * 减少商品库存， 可以传多个sku
	 * @param orderCode 订单编号
	 * @param list skuList
	 * @param error   如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 0 ，否则，返回错误的编号
	 */
	public int DecreaseProductStockForOrder(String orderCode,List<ProductSkuInfo> list,StringBuffer error)
	{
		return	this.stockChange(orderCode,list, error, 0);
	}
	
	
	/**
	 * 添加商品库存，可以传多个sku
	 * @param orderCode 订单编号
	 * @param list skuList
	 * @param error   如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 0 ，否则，返回错误的编号
	 */
	public int AddProductStockForOrder(String orderCode,List<ProductSkuInfo> list,StringBuffer error)
	{
		return	this.stockChange(orderCode,list, error, 1);
	}
	
	/**
	 * @param list 传入的List
	 * @param orderCode 订单编号
	 * @param error 错误内容
	 * @param flag 0 减 1 加
	 * @return
	 */
	private int stockChange(String orderCode,List<ProductSkuInfo> list,StringBuffer error,int flag){
		
		
		//调用 添加订单的存储过程
		List<SqlParameter> params = new ArrayList<SqlParameter>();
		params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
		params.add(new SqlOutParameter("error", Types.VARCHAR));
		params.add(new SqlParameter("detailStr", Types.VARCHAR));
		params.add(new SqlParameter("productsplit", Types.VARCHAR));
		params.add(new SqlParameter("itemsplit", Types.VARCHAR));
		params.add(new SqlParameter("flag", Types.INTEGER));
		SkuStockChangeCallableStatement cscc = new SkuStockChangeCallableStatement(list,flag);

		DbTemplate dt = DbUp.upTable("pc_skuinfo").upTemplate();
		Map<String, Object> outValues = dt.getJdbcOperations().call(cscc,params);

		
		String returnCode = outValues.get("outFlag").toString();
		if(Integer.parseInt(returnCode) != SkuCommon.SuccessFlag){
			if(error!=null)
				error.append(bInfo(Integer.parseInt(returnCode), outValues.get("error").toString()));
		}
		else
		{
			//加入日志。待优化，可以写一个存储过程
			try {
				
				StockChangeLogService scls = new StockChangeLogService();
				
				scls.AddStockChangeLog(orderCode, list, (flag == 0?SkuCommon.SkuStockChangeTypeOrderCommit:SkuCommon.SkuStockChangeTypeOrderRollBack), "system");
				
				
				String skuStr = "";
				
				for(ProductSkuInfo psi : list){
					skuStr +=psi.getSkuCode()+",";
				}
				
				if(skuStr.length()>0){
					skuStr = skuStr.substring(0, skuStr.length()-1);
				}
				//System.out.println("begin-Jms-stockChange");
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeForSkuChangeStock(skuStr);
				//System.out.println("end-Jms-stockChange");
				
			} catch (Exception e) {}
		}
		
		return Integer.parseInt(returnCode);
	}
}
