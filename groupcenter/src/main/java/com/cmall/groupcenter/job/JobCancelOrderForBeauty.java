package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 取消订单<br>
 * 满足条件：订单在线支付、普通商品24小时未付款,试用商品/限时抢购商品15分钟未付款
 * <br>
 * 每五分钟执行一次
 * @author yangrong
 *
 */
public class JobCancelOrderForBeauty extends RootJob {

	
	public void doExecute(JobExecutionContext context) {
		
		
		String create_time = "";
		
		 String now15=DateUtil.addMinute(-15);
		//查看满足条件的活动订单（试用  取消的时候剩余件数加一）
		String ssql="SELECT order_code from oc_orderinfo s where s.order_status='4497153900010001' and  (s.pay_type='449716200001' or s.pay_type='449716200004') and s.order_code not in (SELECT order_code FROM oc_order_pay o where o.order_code=s.order_code and (pay_type='449746280005' or pay_type='449746280003')) and seller_code in ('SI2007','SI2013') and order_type ='449715200003'and s.create_time<:now15 ";
		 List<Map<String, Object>> trylist=DbUp.upTable("oc_orderinfo").dataSqlList(ssql, new MDataMap("now15",now15));
		 if(trylist!=null&&trylist.size()>0){
			 for (Map<String, Object> map : trylist) {
				String orderCode=(String)map.get("order_code");
				if(!"".equals(StringUtils.trimToEmpty(orderCode))){
					OrderService os = new OrderService();
					bLogInfo(0, "start to cancel order by "+orderCode);
					RootResult rr = os.CancelOrderForList(orderCode);
					if(rr.getResultCode()==1){
						/*取消订单时取消取消预返利*/
						GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
						
						groupReckonSupport.checkCreateStep(orderCode, GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
						
						bLogInfo(0, "success to cancel order by "+orderCode);
					}else{
						bLogInfo(0, "failed to cancel order by "+orderCode +" msg:"+rr.getResultMessage());
					}
					
					MDataMap maps = new MDataMap();
					maps.put("order_code", orderCode);
					List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataQuery("", "", "", maps, -1, -1);
					for(int i = 0;i<list.size();i++){
						String skuCode = list.get(i).get("sku_code").toString();
						/*获得订单下单时间*/
						MDataMap mDataMap = DbUp.upTable("oc_orderinfo").one("order_code",list.get(i).get("order_code").toString());
						
						MDataMap mapss = new MDataMap();
						
						if(mDataMap!=null){
							
							 create_time =  mDataMap.get("create_time");
						}
						
						mapss.put("sku_code", skuCode);
						
						String sssql="SELECT sku_code from oc_tryout_products s where (s.start_time <= '"+create_time+"' or s.end_time >='"+create_time+"') and s.sku_code = '"+skuCode+"'";
						List<Map<String, Object>> lists=DbUp.upTable("oc_tryout_products").dataSqlList(sssql, mapss);
						if(lists.size()!=0){
							//去试用商品表里试用库存加1
							
							MDataMap mapsss = new MDataMap();
							mapsss.put("sku_code", skuCode);
							//map2.put("tryout_inventory", );
							String sql = "update oc_tryout_products set tryout_inventory = tryout_inventory + 1 where init_inventory > tryout_inventory and start_time <= '"+create_time+"' and end_time >='"+create_time+"'  and sku_code = '"+skuCode+"'";
							DbUp.upTable("oc_tryout_products").dataExec(sql,mapsss);
							
						}
						
					}
				}
			}
		 }
		 
		//查看满足条件的活动订单（限时抢购）
			String sql="SELECT order_code from oc_orderinfo s where s.order_status='4497153900010001' and  (s.pay_type='449716200001' or s.pay_type='449716200004') and s.order_code not in (SELECT order_code FROM oc_order_pay o where o.order_code=s.order_code and (pay_type='449746280005' or pay_type='449746280003')) and seller_code in ('SI2007','SI2013') and order_type='449715200004' and s.create_time<:now15 ";
			 List<Map<String, Object>> list=DbUp.upTable("oc_orderinfo").dataSqlList(sql, new MDataMap("now15",now15));
			 if(list!=null&&list.size()>0){
				 for (Map<String, Object> map : list) {
					String orderCode=(String)map.get("order_code");
					if(!"".equals(StringUtils.trimToEmpty(orderCode))){
						OrderService os = new OrderService();
						bLogInfo(0, "start to cancel order by "+orderCode);
						RootResult rr = os.CancelOrderForList(orderCode);
						if(rr.getResultCode()==1){
							/*取消订单时取消取消预返利*/
							GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
							
							groupReckonSupport.checkCreateStep(orderCode, GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
							
							bLogInfo(0, "success to cancel order by "+orderCode);
						}else{
							bLogInfo(0, "failed to cancel order by "+orderCode +" msg:"+rr.getResultMessage());
						}
					}
				}
			 }
		 
		 String now1440 =DateUtil.addMinute(-1440);
			//查看满足条件的普通订单
			String sql2="SELECT order_code from oc_orderinfo s where s.order_status='4497153900010001' and  (s.pay_type='449716200001' or s.pay_type='449716200004') and s.order_code not in (SELECT order_code FROM oc_order_pay o where o.order_code=s.order_code and (pay_type='449746280005' or pay_type='449746280003')) and seller_code in ('SI2007','SI2013') and order_type='449715200005' and s.create_time<:now1440 ";
			 List<Map<String, Object>> list2=DbUp.upTable("oc_orderinfo").dataSqlList(sql2, new MDataMap("now1440",now1440));
			 if(list2!=null&&list2.size()>0){
				 for (Map<String, Object> map : list2) {
					String orderCode=(String)map.get("order_code");
					if(!"".equals(StringUtils.trimToEmpty(orderCode))){
						OrderService os = new OrderService();
						bLogInfo(0, "start to cancel order by "+orderCode);
						RootResult rr = os.CancelOrderForList(orderCode);
						if(rr.getResultCode()==1){
							/*取消订单时取消取消预返利*/
							GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
							
							groupReckonSupport.checkCreateStep(orderCode, GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
							
							bLogInfo(0, "success to cancel order by "+orderCode);
						}else{
							bLogInfo(0, "failed to cancel order by "+orderCode +" msg:"+rr.getResultMessage());
						}
					}
				}
			 }
	}
	
	public static void main(String[] args) {
		JobCancelOrderForBeauty cancelOrder =new JobCancelOrderForBeauty();
		cancelOrder.doExecute(null);
	}
}

