package com.cmall.groupcenter.service;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSInput;

import com.cmall.groupcenter.account.model.OrderRebateInfo;
import com.cmall.groupcenter.account.model.OrderRebateResult;
import com.cmall.groupcenter.account.model.ProductInfo;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootCheckApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;


/**
 * 创建订单返利接口
 * @author dyc
 * 创建订单返利接口是用于给微公社用户创建订单返利，第三方对接系统通过该接口将需要返利的订单传入微公社
 *
 */
public class OrderRebateService {

	public OrderRebateResult saveOrderStatusInfo(OrderRebateInfo order,String manageCode){
		
		OrderRebateResult result = checkInputParam(order.getProducts());
		
		if(result.getResultCode()==1){
			
			//校验传入的detailCode是否重复
			HashMap<String, String> detailMap = new HashMap<String, String>();
			for(ProductInfo pro : order.getProducts()){
				String dCode = pro.getDetailCode();
				if(StringUtils.isNotBlank(dCode)){
					if(detailMap.containsKey(dCode)){
						result.inErrorMessage(918519044,dCode);
						return result;
					}else{
						detailMap.put(dCode, dCode);
					}
				}
			}
			
			//校验用户编号是否存在
			MDataMap map = DbUp.upTable("mc_member_info").one("member_code",order.getUserCode());
			
			if(map!=null){//用户编号存在
				
				//校验数据在日志表中是否已存在
				int checkLogNum = DbUp.upTable("lc_order_reckon").count("uq_code",manageCode+"_"+order.getOrderCode());
				//日志存在则报错,后续不处理
				if(checkLogNum > 0){
					result.inErrorMessage(918519043,order.getOrderCode());
					return result;
				}
				
				//校验订单是否存在
				int num = DbUp.upTable("gc_reckon_order_info").count("manage_code",manageCode,"out_order_code",order.getOrderCode());
				//订单存在则报错,后续不处理
				if(num > 0){
					result.inErrorMessage(918519009,order.getOrderCode());
					return result;
				}
				
				//校验detailCode是否存在.存在则报错,后续不处理
				for(ProductInfo pro : order.getProducts()){
					String detailCode = pro.getDetailCode();
					if(StringUtils.isNotBlank(detailCode));{
						int dNum = DbUp.upTable("gc_reckon_order_detail").count("detail_code",detailCode);
						if(dNum > 0){
							result.inErrorMessage(918519045,detailCode);
							return result;
						}
					}
				}
				
				String time = FormatHelper.upDateTime();
				//将数据存入日志表
				MDataMap logMap = new MDataMap();
				logMap.put("rquest_content", new JsonHelper<OrderRebateInfo>().GsonToJson(order));
				String orderCode = WebHelper.upCode("OC");//内部订单号
				logMap.put("order_code", orderCode);
				logMap.put("out_order_code", order.getOrderCode());
				logMap.put("create_time", time);
				logMap.put("manage_code", manageCode);
				logMap.put("product_item_num", order.getProducts().size()+"");
				logMap.put("uq_code", manageCode+"_"+order.getOrderCode());
				try {
					DbUp.upTable("lc_order_reckon").dataInsert(logMap);
				} catch (Exception e1) {
					result.inErrorMessage(918519041,orderCode,order.getOrderCode());
					e1.printStackTrace();
					return result;
				}
					
				double reckonSum = 0.0;
				if(result.upFlagTrue()){
					//清分订单明细
					for(ProductInfo pro : order.getProducts()){
						MDataMap reckonDetailMap = new MDataMap();
						reckonDetailMap.put("detail_code", pro.getDetailCode());
						reckonDetailMap.put("order_code", orderCode);
						reckonDetailMap.put("product_code", pro.getProductCode());
						reckonDetailMap.put("product_name", pro.getProductName());
						reckonDetailMap.put("product_number", pro.getBuyNum()+"");
						reckonDetailMap.put("price_base", pro.getOriginalPrice()+"");
						reckonDetailMap.put("price_cost", pro.getCostprice()+"");
						reckonDetailMap.put("price_sell", pro.getSalePrice()+"");
						reckonDetailMap.put("price_reckon", pro.getReckonAmount()+"");
						reckonDetailMap.put("flag_reckon", pro.getIsReckon());
						double sumRec = pro.getReckonAmount().doubleValue()*pro.getBuyNum();
						reckonSum += sumRec;
						reckonDetailMap.put("sum_reckon_money", sumRec+"");
						reckonDetailMap.put("sku_code", pro.getSkuCode());
						try {
							DbUp.upTable("gc_reckon_order_detail").dataInsert(reckonDetailMap);
						} catch (Exception e) {
							DbUp.upTable("lc_order_reckon").delete("order_code",orderCode);
							DbUp.upTable("gc_reckon_order_detail").delete("order_code",orderCode);
							result.inErrorMessage(918519042,order.getOrderCode());
							e.printStackTrace();
							return result;
						}
					}
				}
				
				//清分订单
				if(result.upFlagTrue()){
					MDataMap reckonMap = new MDataMap();
					reckonMap.put("order_code", orderCode);
					reckonMap.put("manage_code", manageCode);
					reckonMap.put("member_code", order.getUserCode());
					reckonMap.put("account_code", map.get("account_code"));
					reckonMap.put("reckon_money", reckonSum+"");
					reckonMap.put("order_money", order.getOrderTotalAmount()+"");
					reckonMap.put("flag_reckon", order.getIsReckon());
					reckonMap.put("create_time", time);
					reckonMap.put("order_create_time", order.getOrderCreateTime());
					reckonMap.put("process_remark", "");
					reckonMap.put("order_finish_time", "");
					reckonMap.put("out_order_code", order.getOrderCode());
					reckonMap.put("channel_code", order.getChannel_code());
					try{
						DbUp.upTable("gc_reckon_order_info").dataInsert(reckonMap);
					}catch(Exception e){
						DbUp.upTable("lc_order_reckon").delete("order_code",orderCode);
						DbUp.upTable("gc_reckon_order_detail").delete("order_code",orderCode);
						result.inErrorMessage(918519046,orderCode,order.getOrderCode());
						e.printStackTrace();
						return result;
					}
				}
				
				result.setOrderId(orderCode);
				
			}else{
				result.inErrorMessage(915805334);
			}
			
		}
		
		//创建正向返利流程
		if(result.upFlagTrue()){
			new GroupReckonSupport().checkCreateStep(result.getOrderId(),GroupConst.REBATE_ORDER_EXEC_TYPE_IN);
		}
		return result;
	}
	
	public OrderRebateResult checkInputParam(List<ProductInfo> pros){
		OrderRebateResult r = new OrderRebateResult();
		RootResult re = new RootResult();
		for(ProductInfo pro : pros){
			re = new RootCheckApi().checkField(pro);
			if(re.getResultCode()!=1){
				r.setResultCode(re.getResultCode());
				r.setResultMessage(re.getResultMessage());
				break;
			}
		}
		
		return r;
	}
}
