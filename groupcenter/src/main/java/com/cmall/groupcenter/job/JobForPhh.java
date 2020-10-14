package com.cmall.groupcenter.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.xmassystem.load.LoadActivityInfo;
import com.srnpr.xmassystem.modelorder.ActivityInfoDetail;
import com.srnpr.xmassystem.modelorder.ActivityInfoQuery;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.websupport.ApiCallSupport;

/**
 * 定时处理拼好货 数据
 * 单子不够且时间结束的，做取消订单处理
 * @author fq
 *
 */
public class JobForPhh extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		
		List<Map<String, Object>> list=DbUp.upTable("oc_order_phh").dataSqlList("SELECT activity_code from oc_order_phh WHERE flag_success=:flag_success GROUP BY activity_code ", new MDataMap("flag_success","0"));
		
		if(list!=null&&!list.isEmpty()){
			
			for (Map<String, Object> map : list) {
				
				String activity_code=(String)map.get("activity_code");
				
				ActivityInfoDetail upInfoByCode = new LoadActivityInfo().upInfoByCode(new ActivityInfoQuery(activity_code));
				
				String end_time = upInfoByCode.getEnd_time();
				int purchase_num = Integer.valueOf(upInfoByCode.getPurchase_num());
				
				
				List<MDataMap> phhInfo = DbUp.upTable("oc_order_phh").queryByWhere("activity_code",activity_code);
				
				StringBuffer sqlString=new StringBuffer();
				for (MDataMap mDataMap : phhInfo) {
					String order_code=mDataMap.get("order_code");
					sqlString.append(",'").append(order_code).append("'");
				}
				
				String sqlString2="select * from oc_orderinfo where order_code in ("+sqlString.substring(1)+") and order_status=:order_status";
				
				List<Map<String, Object>> orderList=DbUp.upTable("oc_orderinfo").dataSqlList(sqlString2, new MDataMap("order_status","4497153900010002"));
				
				if(orderList.size()>=purchase_num){
					//拼团成功
					//改状态  insert za_ex
					DbUp.upTable("oc_order_phh").dataUpdate(new MDataMap("activity_code",activity_code,"flag_success","1"), "flag_success", "activity_code");
					for (Map<String, Object> map2 : orderList) {
						JobExecHelper.createExecInfo("449746990002", String.valueOf(map2.get("order_code")), null);
					}
					
				}else{
					//判断时间
					if(!end_time.isEmpty()) {
						
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
						Date endTimeDate;
						try {
							
							endTimeDate = df.parse(end_time);
							Date sysTimeDate = new Date();
							if(sysTimeDate.before(endTimeDate)) {
								//没有结束的暂时不做处理，等待下一次定时
							} else {//已经结束
								DbUp.upTable("oc_order_phh").dataUpdate(new MDataMap("activity_code",activity_code,"flag_success","2"), "flag_success", "activity_code");
								//循环取消订单
								for (Map<String, Object> map2 : orderList) {
									Object order_code = map2.get("order_code");
									Object buyer_code = map2.get("buyer_code");
									cancelOrder(String.valueOf(order_code),String.valueOf(buyer_code));
								}
							}
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					} else {
						//结束时间为空
						continue;
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 取消订单
	 * @param order_code
	 * @param buyer_code
	 */
	private void cancelOrder (String order_code,String buyer_code) {
		
		OrderService orderService = new OrderService();
		
		String loginname=UserFactory.INSTANCE.create().getUserCode();
		//此处留坑，不做事务
		RootResult res = orderService.cancelOrderByShop(order_code,loginname);
		if(res.getResultCode()==1){
			
			//退返微公社部分
			MDataMap payInfo=DbUp.upTable("oc_order_pay").one("order_code",order_code,"pay_type","449746280009");
			if(payInfo!=null&&!payInfo.isEmpty()){
				GroupRefundInput groupRefundInput = new GroupRefundInput();
				groupRefundInput.setTradeCode(payInfo.get("pay_sequenceid"));
				groupRefundInput.setMemberCode(buyer_code);
				groupRefundInput.setRefundMoney(payInfo.get("payed_money"));
				groupRefundInput.setOrderCode(order_code);
				groupRefundInput.setRefundTime(com.cmall.ordercenter.common.DateUtil.getSysDateTimeString());
				groupRefundInput.setRemark("拼好货 拼团失败 退钱");
				groupRefundInput.setBusinessTradeCode(payInfo.get("pay_sequenceid"));//一个流水值退一次
//					new GroupPayService().groupRefundSome(groupRefundInput, seller_code);
				
				ApiCallSupport<GroupRefundInput, GroupRefundResult> apiCallSupport=new ApiCallSupport<GroupRefundInput, GroupRefundResult>();
				GroupRefundResult refundResult = null;
				try {
					refundResult=apiCallSupport.doCallApi(
							bConfig("xmassystem.group_pay_url"),
							bConfig("xmassystem.group_pay_refund_face"),
							bConfig("xmassystem.group_pay_key"),
							bConfig("xmassystem.group_pay_pass"), groupRefundInput,
							new GroupRefundResult());
				} catch (Exception e) {
					//此处暂时流程，退款失败，不影响总流程
					e.printStackTrace();
				}
				
			}
		}
		
	}


}
