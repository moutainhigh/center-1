/**
 * Project Name:ordercenter
 * File Name:RetExchGoods.java
 * Package Name:com.cmall.ordercenter.service
 * Date:2013年11月7日上午11:17:55
 *
*/

package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.ExchangegoodsDetailModelChild;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.model.RetExchGoodsResult;
import com.cmall.ordercenter.model.RetuGoodDetail;
import com.cmall.ordercenter.model.api.ApiGetOrderInput;
import com.cmall.ordercenter.model.api.ApiGetOrderResult;
import com.cmall.ordercenter.service.api.ApiGetOrder;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * ClassName:RetExchGoods <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月7日 上午11:17:55 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class RetExchGoodsApi extends RootApi<RetExchGoodsResult, ApiGetOrderInput>
{

	public RetExchGoodsResult Process(ApiGetOrderInput inputParam,MDataMap mRequestMap) {
		ApiGetOrder apiGetOrder= new ApiGetOrder();
		ApiGetOrderResult rs = apiGetOrder.Process(inputParam, mRequestMap);
		RetExchGoodsResult result = new RetExchGoodsResult();
		if(StringUtils.isBlank(inputParam.getBuyerCode()))
		{
			result.setResultCode(939301035);
			result.setResultMessage(bInfo(939301035));
			return result;
		}
		if(StringUtils.isBlank(inputParam.getOrderCode()))
		{
			result.setResultCode(939301036);
			result.setResultMessage(bInfo(939301036));
			return result;
		}
		if(validateOrderStatus(inputParam.getOrderCode()))
		{
			List<OrderDetail> productList = new ArrayList<OrderDetail>();
			List<RetuGoodDetail> detail = new ArrayList<RetuGoodDetail>();
			List<RetuGoodDetail> detail3 = new ArrayList<RetuGoodDetail>();
			//获取订单详情
			productList= rs.getOrder().getProductList();
			if(null == productList)
			{
				result.setResultCode(rs.getResultCode());
				result.setResultMessage(rs.getResultMessage());
				return result;
			}
			for(int i=0;i<productList.size();i++)
			{
				RetuGoodDetail dt = new RetuGoodDetail();
	     		dt.setSerial_number(productList.get(i).getZid());
	     		dt.setSku_code(productList.get(i).getProductCode());
	     		//dt.setProduct_name(productList.get(i).getSkuName());
	     		dt.setCurrent_price(productList.get(i).getSkuPrice());
	     		//dt.setSku_status(getStatuByReturnCode(getReturnCodeBySerialNo(productList.get(i).getZid())));
	     		detail.add(dt);
			}
			//查找退单详情中有流水号记录的所有记录
			String sql = getInSqlForMap(detail);
			List<MDataMap> list = DbUp.upTable("oc_return_goods_detail").queryAll("", "", "serial_number "+sql, new MDataMap());
			
			for(int j=0;j<list.size();j++)
			{
				RetuGoodDetail deta = new RetuGoodDetail();
				deta.setSerial_number(list.get(j).get("serial_number"));
				deta.setSku_code(list.get(j).get("sku_code"));
				deta.setSku_name(list.get(j).get("sku_name"));
				deta.setProduct_picurl(list.get(j).get("product_picurl"));
				deta.setCurrent_price(new BigDecimal(list.get(j).get("current_price")));
				deta.setSku_status(getStatuByReturnCode(list.get(j).get("return_code")));
				detail3.add(deta);
			}
			result.setReturnGoods(detail3);
			
			//获取换货信息
			result.setExchangeGoodDetail(getExchangeGoodInfo(productList));
			result.setOrder(rs.getOrder());
			return  result;
		}
		else
		{
			result.setResultCode(939301084);
			result.setResultMessage(bInfo(939301084));
			return  result;
		}
	}
	
	/**
	 * 获取换货信息
	 * @param productList 订单信息列表
	 * @param resultExGoodList 
	 * @return 
	 */
	private List<ExchangegoodsDetailModelChild> getExchangeGoodInfo(List<OrderDetail> productList) {
		List<ExchangegoodsDetailModelChild> tempExGoodList = new ArrayList<ExchangegoodsDetailModelChild>();
		List<ExchangegoodsDetailModelChild> resultExGoodList = new ArrayList<ExchangegoodsDetailModelChild>();
		//获取订单中的所有流水号
		for(int ps=0;ps<productList.size();ps++){
			ExchangegoodsDetailModelChild tempExGoodDetail = new ExchangegoodsDetailModelChild();
			tempExGoodDetail.setSerialNumber(productList.get(ps).getZid());
			tempExGoodList.add(tempExGoodDetail);
		}
		//查找换货详情中有流水号记录的所有记录
		String inSql = getInExGoodSqlForMap(tempExGoodList);
		List<MDataMap> list = DbUp.upTable("oc_exchange_goods_detail").queryAll("", "", "serial_number "+inSql, new MDataMap());
		for(int j=0;j<list.size();j++)
		{
			ExchangegoodsDetailModelChild data = new ExchangegoodsDetailModelChild();
			data.setExchangeNo(list.get(j).get("exchange_no"));
			data.setSkuCode(list.get(j).get("sku_code"));
			data.setSkuName(list.get(j).get("sku_name"));
			data.setCount(Integer.parseInt(list.get(j).get("count")));
			data.setCurrentPrice(Float.valueOf(list.get(j).get("current_price")));
			data.setSerialNumber(list.get(j).get("serial_number"));
			data.setProductPicurl(list.get(j).get("product_picurl"));
			data.setSkuStatus(getStatuByExchangeNo(list.get(j).get("exchange_no")));
			resultExGoodList.add(data);
		}
		return resultExGoodList;
	}

	/**
	 * 根据换货单号获取状态
	 * @param exchangeNo 换货单号
	 * @return 状态
	 */
	private String getStatuByExchangeNo(String exchangeNo) {
		MDataMap prodcutData = DbUp.upTable("oc_exchange_goods").one("exchange_no", exchangeNo);
		return prodcutData.get("status");
	}

	/**
	 * 拼接换货SQL语句
	 * @param tempExGoodList
	 * @return
	 */
	private String getInExGoodSqlForMap(
			List<ExchangegoodsDetailModelChild> tempExGoodList) {
		String sql = "";
		if(!tempExGoodList.isEmpty()){
			for(ExchangegoodsDetailModelChild m:tempExGoodList){
				String code = m.getSerialNumber();
				if("".equals(sql)&&code!=null&&!"".equals(code)){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code)){
					sql+=",'"+code+"'";
				}
			}
		}
		if(!"".equals(sql)){
			sql+=")";
		}
		return sql;
	}

	/**
	 * getInSqlForMap:(拼接sql语句). <br/>
	 * @author hxd
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	private String getInSqlForMap(List<RetuGoodDetail> list){
		String sql = "";
		if(!list.isEmpty()){
			for(RetuGoodDetail m:list){
				String code = m.getSerial_number();
				if("".equals(sql)&&code!=null&&!"".equals(code)){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code)){
					sql+=",'"+code+"'";
				}
			}
		}
		if(!"".equals(sql)){
			sql+=")";
		}
		return sql;
	}
	/**
	 * getStatuByReturnCode:(这里用一句话描述这个方法的作用). <br/>
	 * @author hxd
	 * @param returnCode
	 * @return
	 * @since JDK 1.6
	 */
	public String getStatuByReturnCode(String returnCode)
	{
		MDataMap prodcutData = DbUp.upTable("oc_return_goods").one("return_code", returnCode);
		return prodcutData.get("status");
	}
	
	
	/**
	 * 判断订单是否已发货
	 * @return
	 */
	private  boolean validateOrderStatus(String orderCode)
	{
		int count = DbUp.upTable("lc_orderstatus").count("code",orderCode,"now_status","4497153900010003");
		if(count>0)
			return true;
		else
			return false;
	}

}

