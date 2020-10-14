package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.ReturnMoneyExt;
import com.cmall.ordercenter.model.ReturnMoneyExtStr;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 退款
 * @author jlin
 *
 */
public class ReturnMoneyService {

	/***
	 * 获取扩展信息
	 * @param order_code
	 * @return
	 */
	public ReturnMoneyExt extInfo(String order_code) {

		ReturnMoneyExt returnMoneyExt = new ReturnMoneyExt();

		if (StringUtils.isBlank(order_code)) {
			return returnMoneyExt;
		}

		MDataMap orderMap = DbUp.upTable("oc_orderinfo").one("order_code", order_code);
		if (orderMap == null) {
			return returnMoneyExt;
		}
		returnMoneyExt.setBig_order_code(orderMap.get("big_order_code"));
		MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_sequenceid,pay_type,php_code,pay_bank", "zid desc","order_code=:order_code", "order_code", order_code);
		if (payMap == null) {
			return returnMoneyExt;
		}
		returnMoneyExt.setPay_sequenceid(payMap.get("pay_sequenceid"));
		returnMoneyExt.setPay_type(payMap.get("pay_type"));
		returnMoneyExt.setPhp_code(payMap.get("php_code"));
		returnMoneyExt.setPay_type_name(DbUp.upTable("sc_define").one("define_code",returnMoneyExt.getPay_type()).get("define_name"));
		returnMoneyExt.setPay_bank(payMap.get("pay_bank"));
		
		// 查询支付网关返回的支付单号
		MDataMap map = DbUp.upTable("oc_payment_paygate").one("c_order", orderMap.get("big_order_code"));
		if(map != null){
			returnMoneyExt.setPaygate_order_code(map.get("c_transnum"));
		}
		
		return returnMoneyExt;
	}
	
	/**
	 * 已退款金额
	 * @param order_code
	 * @return
	 */
	public BigDecimal returnMoney(String order_code){
		
		Map<String, Object> dataMap=DbUp.upTable("oc_return_money").dataSqlOne("SELECT sum(return_money) as return_money from oc_return_money where status='4497153900040001' and order_code=:order_code", new MDataMap("order_code", order_code));
		if(dataMap!=null&&!dataMap.isEmpty()){
			BigDecimal return_money=(BigDecimal)dataMap.get("return_money");
			if(return_money!=null){
				return return_money;
			}
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * 订单金额
	 * @param order_code
	 * @return
	 */
	public BigDecimal orderMoney(String order_code){
		MDataMap dataMap=DbUp.upTable("oc_orderinfo").one("order_code", order_code);
		return new BigDecimal(dataMap.get("order_money"));
	}
	
	/**
	 * 订单金额
	 * @param order_code
	 * @return
	 */
	public String orderMoneyStr(String order_code){
		MDataMap dataMap=DbUp.upTable("oc_orderinfo").one("order_code", order_code);
		return dataMap.get("order_money");
	}
	
	/**
	 * 退款单的商品总售价
	 * @param order_code
	 * @return
	 */
	public String productMoneyStr(String returnCode){
		String sql = "SELECT d.show_price*rd.count as product_money FROM `oc_return_goods` rg,oc_return_goods_detail rd,oc_orderdetail d WHERE rg.return_code = rd.return_code and rg.order_code = d.order_code and  rd.sku_code = d.sku_code and rg.return_code = :return_code";
		Map<String, Object> map = DbUp.upTable("oc_return_goods").dataSqlOne(sql, new MDataMap("return_code", returnCode));
		return map.get("product_money")+"";
	}
	
	/**
	 * 退款的扩展信息
	 * @param order_code
	 * @param return_money_code
	 * @return
	 */
	public ReturnMoneyExt extMoneyInfo(String order_code,String return_money_code){
		
		ReturnMoneyExt ext = extInfo(order_code);
		if(StringUtils.isNotBlank(ext.getBig_order_code())){
			
			MDataMap dataMap=DbUp.upTable("oc_orderinfo").one("order_code", order_code);
			ext.setOrder_payed_money(new BigDecimal(dataMap.get("due_money")));
			ext.setOrder_money(new BigDecimal(dataMap.get("order_money")));
			
			ext.setOrder_returned_money(returnMoney(order_code));
			
			if(StringUtils.isNotBlank(return_money_code)){//退款单生成
				MDataMap returnMoney=DbUp.upTable("oc_return_money").one("return_money_code",return_money_code);
				ext.setOrder_current_money(new BigDecimal(returnMoney.get("return_money")));
				
				MDataMap returnGoods=DbUp.upTable("oc_return_goods").one("return_code", StringUtils.trimToEmpty(returnMoney.get("return_goods_code")));
				// 查询退货单的运费
				if(returnGoods != null){
					ext.setOrder_rr_money(ext.getOrder_current_money().subtract(new BigDecimal(returnGoods.get("transport_money"))));
					ext.setOrder_rr_transport_money(new BigDecimal(returnGoods.get("transport_money")));
				}else{
					// 没有退货单时使用订单的金额
					ext.setOrder_rr_money(ext.getOrder_payed_money());
					ext.setOrder_rr_transport_money(new BigDecimal(dataMap.get("transport_money")));
				}
			}else{
				//其他方式生成
				ext.setOrder_current_money(ext.getOrder_payed_money());
				BigDecimal trans=new BigDecimal(dataMap.get("transport_money"));
				ext.setOrder_rr_money(ext.getOrder_current_money().compareTo(trans)>0?ext.getOrder_current_money().subtract(trans):ext.getOrder_current_money());
			}
		}
		return ext;
	}
	
	
	public ReturnMoneyExtStr extMoneyInfoStr(String order_code,String return_goods_code){
		
		ReturnMoneyExt ext = extMoneyInfo(order_code, return_goods_code);
		
		ReturnMoneyExtStr extStr = new ReturnMoneyExtStr();
		extStr.setBig_order_code(ext.getBig_order_code());
		extStr.setPay_type(ext.getPay_type());
		extStr.setPay_sequenceid(ext.getPay_sequenceid());
		extStr.setPay_type_name(ext.getPay_type_name());
		extStr.setOrder_payed_money(ext.getOrder_payed_money());
		extStr.setOrder_returned_money(ext.getOrder_returned_money());
		extStr.setOrder_rr_money(ext.getOrder_rr_money());
		extStr.setOrder_rr_transport_money(ext.getOrder_rr_transport_money());
		extStr.setOrder_current_money(ext.getOrder_current_money());
		extStr.setOrder_money(ext.getOrder_money());
		
		return extStr;
	}
}
