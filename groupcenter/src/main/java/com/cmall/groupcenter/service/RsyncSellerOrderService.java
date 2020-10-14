package com.cmall.groupcenter.service;

import com.cmall.groupcenter.FamilyConfig;
import com.cmall.groupcenter.GroupConstant.PayOrderStatusEnum;
import com.cmall.groupcenter.account.model.OrderRebateInfo;
import com.cmall.groupcenter.account.model.OrderRebateResult;
import com.cmall.groupcenter.account.model.ProductInfo;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput.OrderStatusInfo;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusResult;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.systemcenter.service.ScDefineService;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.ApiFactory;
import com.srnpr.zapweb.websupport.ApiCallSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 与微公社同步订单返利业务实现
 * 
 * @author pangjh
 *
 */

public class RsyncSellerOrderService extends BaseClass {
	
	
	public static void main(String[] args) {
		try {
			new RsyncSellerOrderService().doRsync();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 与微公社接口同步订单
	 * @return
	 * 		接口响应信息
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public void doRsync() throws ParseException{
		
		
		
		doRsyncHAS();
		
		//doRsyncSPG();
		
		
		
	}
	
	/**
	 * 惠家有与微公社接口同步订单
	 * @return
	 * 		接口响应信息
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public void doRsyncHAS() throws ParseException{
		
		String mamageCode = bConfig("familyhas.app_code");
		
		List<MDataMap> orderList = getRsyncSellerOrderList();
		/*同步订单信息*/
		rsyncOrderInfo(mamageCode,orderList);
		/*同步订单状态*/
		List<MDataMap> statusList = getRsyncSellerOrderStatusList();
		/*同步订单状态信息*/
		rsyncOrderStatus(mamageCode,statusList);
		/*更新同步日期*/
		updateRsyncDate();
		
	}
	
	/**
	 * 同步沙皮狗
	 */
	public void doRsyncSPG(){
		
		String mamageCode = "SI3003";
		
		List<MDataMap> orderList = getRsyncSellerOrderListSPG();
		/*同步订单信息*/
		rsyncOrderInfo(mamageCode,orderList);
		/*同步订单状态*/
		List<MDataMap> statusList = getRsyncSellerOrderStatusListSPG();
		/*同步订单状态信息*/
		rsyncOrderStatus(mamageCode,statusList);
		
	}
	
	/**
	 * 同步订单信息
	 * @param orderList
	 * 		需同步订单信息列表
	 */
	public synchronized  void rsyncOrderInfo(String mamageCode,List<MDataMap> orderList){
		
		JsonHelper<OrderRebateResult> orderRebateHelper = new JsonHelper<OrderRebateResult>();
		
		for(MDataMap mDataMap : orderList){
			
			boolean flag = true;
			
			String orderCode = mDataMap.get("order_code");			
			
			/*根据订单编号查询日志信息*/
			MDataMap logDataMap = RsyncOrderBeanFactory.getInstance().getLcRsyncOrderCGroupService().queryByOrderCode(orderCode);
			
			try {
				
				if(logDataMap == null){
					
					logDataMap = new MDataMap();
					
					logDataMap.put("order_code", orderCode);
					/*初始日志信息*/
					RsyncOrderBeanFactory.getInstance().getLcRsyncOrderCGroupService().save(logDataMap);
					
				}else{
					
					/*判断日志中错误次数是否超过最大限制*/
					flag = moreThanLimit(logDataMap);
					
				}
				
				if(flag){			
					/*获取订单详情*/
					Order order = RsyncOrderBeanFactory.getInstance().getOrderService().getOrder(orderCode);
					/*与微公社订单同步信息*/
					OrderRebateInfo orderInfo = new OrderRebateInfo();
					
					if(order != null){
						
						orderInfo.setFreight(BigDecimal.ZERO);
						orderInfo.setIsReckon(FamilyConfig.ISRECKON_YES);
						orderInfo.setOrderCode(order.getOrderCode());
						orderInfo.setOrderCreateTime(order.getCreateTime());
						orderInfo.setOrderTotalAmount(order.getOrderMoney());
						orderInfo.setUserCode(order.getBuyerCode());
						orderInfo.setProducts(convertProductList(order.getProductList()));
						
					}
					
					ApiCallSupport<OrderRebateInfo,OrderRebateResult> apicall=new ApiCallSupport<OrderRebateInfo,OrderRebateResult>();
					OrderRebateResult apiresult= apicall.doCallApi(
							bConfig("xmassystem.group_pay_url"),
							"com.cmall.groupcenter.account.api.ApiOrderRebate",
							bConfig("xmassystem.group_pay_key"),
							bConfig("xmassystem.group_pay_pass"),orderInfo,new OrderRebateResult());
					
					
					logDataMap.put("process_data",new JsonHelper<OrderRebateInfo>().ObjToString(orderInfo));
					
					if(StringUtils.isNotBlank(apiresult.getOrderId())){
						saveOrderRelCgroup(order, apiresult.getOrderId());
					}
					
					RsyncOrderBeanFactory.getInstance().getLcRsyncOrderCGroupService().update(String.valueOf(apiresult.getResultCode()), logDataMap);
					//System.out.println("结果编号:"+apiresult.getResultCode()+",结果信息:"+apiresult.getResultMessage());
				}
						
								
			} catch (Exception e) {
				
				bLogError(0, "与微公社同步报错 , 订单编号："+orderCode+" "+e.getMessage());	
			    e.printStackTrace();
				
			}			
			
		}
		
	}
	
	/**
	 * 同步订单状态
	 * @param orderList
	 */
	public synchronized  void rsyncOrderStatus(String mamageCode,List<MDataMap> orderList){
		
		/*转换订单状态json参数*/
		String sInputJson = convertOrderStatusParams(orderList);
		
		try {

            //组装请求参数
			SyncOrderStatusInput iBaseInput=new SyncOrderStatusInput();
			JsonHelper<SyncOrderStatusInput> jsonInput = new JsonHelper<SyncOrderStatusInput>();
			iBaseInput = jsonInput.StringToObjExp(sInputJson, iBaseInput);
			
			//组装返回参数
			SyncOrderStatusResult iBaseResult=new SyncOrderStatusResult();
			
			ApiCallSupport<IBaseInput,IBaseResult> apicall=new ApiCallSupport<IBaseInput,IBaseResult>();
			iBaseResult=(SyncOrderStatusResult) apicall.doCallApi(
					bConfig("xmassystem.group_pay_url"),"com.cmall.groupcenter.sync.orderstatus.ApiSynOrderStatus",
					bConfig("xmassystem.group_pay_key"),
					bConfig("xmassystem.group_pay_pass"),iBaseInput,iBaseResult);
			
			MDataMap mDataMap=new MDataMap();
			mDataMap.put("process_data", new JsonHelper<SyncOrderStatusInput>().ObjToString(iBaseInput));
			mDataMap.put("resultCode",String.valueOf(iBaseResult.getResultCode()));
			mDataMap.put("resultMessage",iBaseResult.getResultMessage()!=null?iBaseResult.getResultMessage():"");
			mDataMap.put("statusSerialNum",iBaseResult.getStatusSerialNum()!=null?iBaseResult.getStatusSerialNum():"");
			
			RsyncOrderBeanFactory.getInstance().getLcRsyncOrderCGroupService().updateOrderStatusLog(mDataMap);
			//System.out.println("请求参数:"+sInputJson);
			//System.out.println("同步订单结果："+iBaseResult.getResultCode()+",同步订单结果信息："+iBaseResult.getResultMessage());
		     
			
		} catch (Exception e) {
			
			bLogError(0, "与微公社同步订单状态失败 "+e.getMessage());
			e.printStackTrace();
			
		}
		
		
		
	}
	
	
	/**
	 * 根据订单信息转换接口参数
	 * @param orderCode
	 * 		订单编号
	 * @return
	 * 		json格式数据
	 */
	public String convertOrderParams(String orderCode){		
		/*获取订单详情*/
		Order order = RsyncOrderBeanFactory.getInstance().getOrderService().getOrder(orderCode);
		/*与微公社订单同步信息*/
		OrderRebateInfo orderInfo = new OrderRebateInfo();
		
		if(order != null){
			
			orderInfo.setFreight(BigDecimal.ZERO);
			orderInfo.setIsReckon(FamilyConfig.ISRECKON_YES);
			orderInfo.setOrderCode(order.getOrderCode());
			orderInfo.setOrderCreateTime(order.getCreateTime());
			orderInfo.setOrderTotalAmount(order.getOrderMoney());
			orderInfo.setUserCode(order.getBuyerCode());
			orderInfo.setProducts(convertProductList(order.getProductList()));
			
		}
		
		JsonHelper<OrderRebateInfo> jsonHelper = new JsonHelper<OrderRebateInfo>();		
		
		return jsonHelper.ObjToString(orderInfo);
		
	}
	
	/**
	 * 根据订单信息转换订单状态同步接口参数
	 * @param orderCode
	 * 		订单编号
	 * @return
	 * 		json格式数据
	 */
	public String convertOrderStatusParams(List<MDataMap> list){		
		
		SyncOrderStatusInput input = new SyncOrderStatusInput();
		
		List<OrderStatusInfo> orderStatusList = new ArrayList<OrderStatusInfo>();
				
		OrderStatusInfo orderStatusInfo = null;
		
		String groupOrderCode = "";
		
		for(MDataMap mDataMap : list){
			
			MDataMap groupDataMap = DbUp.upTable("oc_order_cgroup").one("order_code", mDataMap.get("order_code"));
			
			/*微公社订单不存在*/
			if(groupDataMap == null){
				continue;
			}
			
			groupOrderCode = groupDataMap.get("out_order_code");
			
			// 清分订单号为空时不同步订单 状态
			if(StringUtils.isBlank(groupOrderCode)) continue;
			
			orderStatusInfo = new OrderStatusInfo();
			orderStatusInfo.setOrderCode(groupOrderCode);
			orderStatusInfo.setOrderStatus(getGroupNO(mDataMap.get("order_status")));
			orderStatusInfo.setRemark("");
			orderStatusInfo.setUpdateTime(mDataMap.get("update_time"));
			
			orderStatusList.add(orderStatusInfo);
			
			
		} 
		
		input.setOrderStatusInfos(orderStatusList);
		
		JsonHelper<SyncOrderStatusInput> jsonHelper = new JsonHelper<SyncOrderStatusInput>();		
		
		return jsonHelper.ObjToString(input);
		
	}
	
	
	/**
	 * 根据惠家有的订单编号获取微公社订单信息
	 * @param order_code3
	 * 		惠家有订单编号
	 * @return 微公社订单信息
	 */
	public MDataMap getGroupOrderInfo(String order_code){
		
		return DbUp.upTable("gc_reckon_order_info").one("out_order_code",order_code);
		
		
	}
	
	
	public String getGroupNO(String status){
		
		for(PayOrderStatusEnum ps : PayOrderStatusEnum.values()){
			if(StringUtils.equals(ps.getMarkCode(), status)){
				return Integer.toString(ps.getsNo());
			}
		}
		
		return null;
	}
	
	/**
	 * 将惠家有的订单详情信息转换为微公社同步参数
	 * @param details
	 * 		订单详情
	 * @return List<ProductInfo>
	 * 		与微公社同步产品参数集合
	 */
	public List<ProductInfo> convertProductList(List<OrderDetail> details){
		
		List<ProductInfo> products = new ArrayList<ProductInfo>();
		
		int i = 1;
		
		for(OrderDetail orderDetail : details){
			
			ProductInfo productInfo = new ProductInfo();
			
			productInfo.setBuyNum(orderDetail.getSkuNum());
			productInfo.setCostprice(orderDetail.getCostPrice());
			productInfo.setDetailCode(orderDetail.getOrderCode()+"_"+i);
			productInfo.setIsReckon(FamilyConfig.ISRECKON_YES);
			productInfo.setOriginalPrice(BigDecimal.ZERO);
			productInfo.setProductCode(orderDetail.getProductCode());
			productInfo.setProductName(orderDetail.getSkuName());
			productInfo.setReckonAmount(orderDetail.getSkuPrice());
			productInfo.setSalePrice(orderDetail.getSkuPrice());
			productInfo.setSkuCode(orderDetail.getSkuCode());
			
			products.add(productInfo);
			
			i++;
			
		}
		
		return products;
		
	}
	
	/**
	 * 获取需同步的订单信息(下单成功未付款、未发货)
	 * @return List<MDataMap>
	 * 		同步的订单信息
	 * @throws ParseException 
	 */
	public List<MDataMap> getRsyncSellerOrderList() throws ParseException{
		
		MDataMap statusMap = new MDataMap();
		
		statusMap.put("order_status1", FamilyConfig.ORDER_STATUS_UNPAY);
		
		statusMap.put("order_status2", FamilyConfig.ORDER_STATUS_PAYED);
		
		statusMap.put("order_status3", FamilyConfig.ORDER_STATUS_DELIVERED);
		
		statusMap.put("order_status4", FamilyConfig.ORDER_STATUS_TRADE_SUCCESS);
		/*惠家有订单集合*/
		List<MDataMap> orderList = RsyncOrderBeanFactory.getInstance().
				getRsyncSellerOrderDao().queryRsyncSellerOrderList(statusMap,getRsyncDate(),getRsyncEndDate());
		
		for (Iterator<MDataMap> iterator = orderList.iterator(); iterator.hasNext();) {
			
			MDataMap mDataMap = iterator.next();
			
			MDataMap groupOrderInfo = getGroupOrderInfo(mDataMap.get("order_code"));
			
			if(groupOrderInfo != null){
				iterator.remove();
			}
		}
		
		return orderList;
		
	}
	
	/**
	 * 获取需同步的订单信息(下单成功未付款、未发货)
	 * @return List<MDataMap>
	 * 		同步的订单信息
	 */
	public List<MDataMap> getRsyncSellerOrderListSPG(){
		
		MDataMap statusMap = new MDataMap();
		
		statusMap.put("order_status1", FamilyConfig.ORDER_STATUS_UNPAY);
		
		statusMap.put("order_status2", FamilyConfig.ORDER_STATUS_PAYED);
		
		/*沙皮狗订单集合*/
		List<MDataMap> spgOrderList = RsyncOrderBeanFactory.getInstance().
				getRsyncSellerOrderDao().queryRsyncSellerOrderListForSPG(statusMap);
		
		return spgOrderList;
		
	}
	
	/**
	 * 获取需同步的订单信息(下单成功未付款、未发货、已发货、发货成功、发货失败)
	 * @return List<MDataMap>
	 * 		同步的订单信息
	 * @throws ParseException 
	 */
	public List<MDataMap> getRsyncSellerOrderStatusList() throws ParseException{
		
		MDataMap statusMap = new MDataMap();
		
		statusMap.put("order_status1", FamilyConfig.ORDER_STATUS_UNPAY);
		
		statusMap.put("order_status2", FamilyConfig.ORDER_STATUS_PAYED);
		
		statusMap.put("order_status3", FamilyConfig.ORDER_STATUS_DELIVERED);
		
		statusMap.put("order_status4", FamilyConfig.ORDER_STATUS_TRADE_SUCCESS);
		
		statusMap.put("order_status5", FamilyConfig.ORDER_STATUS_TRADE_FAILURE);
		
		List<MDataMap> statusList = RsyncOrderBeanFactory.getInstance().
				getRsyncSellerOrderDao().queryRsyncSellerOrderList(statusMap,getRsyncDate(),getRsyncEndDate());
		
		 
		return statusList;
		
	}
	
	/**
	 * 获取需同步的订单信息(下单成功未付款、未发货、已发货、发货成功、发货失败)
	 * @return List<MDataMap>
	 * 		同步的订单信息
	 */
	public List<MDataMap> getRsyncSellerOrderStatusListSPG(){
		
		MDataMap statusMap = new MDataMap();
		
		statusMap.put("order_status1", FamilyConfig.ORDER_STATUS_UNPAY);
		
		statusMap.put("order_status2", FamilyConfig.ORDER_STATUS_PAYED);
		
		statusMap.put("order_status3", FamilyConfig.ORDER_STATUS_DELIVERED);
		
		statusMap.put("order_status4", FamilyConfig.ORDER_STATUS_TRADE_SUCCESS);
		
		statusMap.put("order_status5", FamilyConfig.ORDER_STATUS_TRADE_FAILURE);
		
		List<MDataMap> statusListSPG = RsyncOrderBeanFactory.getInstance().
				getRsyncSellerOrderDao().queryRsyncSellerOrderListForSPG(statusMap);
		
		return statusListSPG;
		
	}
	
	/**
	 * 判断是否同步成功
	 * @param result
	 * 		接口返回结果
	 * @return
	 * 		是否同步成功true|false
	 */
	public boolean rsyncStatus(String result,RootResultWeb iBaseResult){
		
		JsonHelper<RootResultWeb> jsonHelper = new JsonHelper<RootResultWeb>();
		
		RootResultWeb orderRebateResult = jsonHelper.StringToObj(result,iBaseResult);
		
		return orderRebateResult.upFlagTrue();
		
	}
	
	/**
	 * 判断日志同步失败的次数是否超过最大限制
	 * @param errorCount
	 * 		同步失败的次数
	 * @return
	 * 	   true|超过限制 false|未超过限制
	 * @throws Exception 
	 */
	public boolean moreThanLimit(MDataMap mDataMap) throws Exception{
		
		boolean flag = true;
		
		String errorCount = mDataMap.get("error_count");
		
		int count = Integer.parseInt(errorCount);

		if(count > getCountLimit()){
			
			flag = false;
			
		}
		
		return flag;
		
	}
	
	/**
	 * 获取限制次数
	 * @return
	 */
	public int getCountLimit(){
		
		/*获取限制次数*/
		String error_count = bConfig("familyhas.rsync_order_cgroup_limit");
		
		return Integer.parseInt(error_count!=null?error_count:"5000");
		
	}	
	
	
	public boolean existGroupStatus(String order_code,String order_status){
		
		boolean flag = false;
		
		int count = DbUp.upTable("gc_sync_order_status").count("order_code",order_code,"order_status",order_status);
		
		if(count >= 1){
			
			flag = true;
			
		}
		
		return flag;
		
	}
	
	/**
	 * 判断微公社订单信息是否存在
	 * @param order_code
	 * 		订单编号
	 * @return 
	 */
	public boolean existGroupOrderStatus(String order_code, String order_status){
		
		StringBuffer sWhere = new StringBuffer("order_code = (select order_code from gc_reckon_order_info");
		
		sWhere.append(" where out_order_code = '").append(order_code).append("')");
		
		sWhere.append(" and order_status = '").append(order_status).append("'");
		
		MDataMap mDataMap = DbUp.upTable("gc_sync_order_status").oneWhere("order_code,order_status", "", sWhere.toString(), "");
		
		boolean flag = false;
		
		if(mDataMap != null){
			
			flag = true;
			
		}
		
		return flag;
		
		
	}
	
	/**
	 * 获取同步区间的值
	 * @param code
	 * 		值编码
	 * @return
	 */
	public int getRsyncValue(String code){
	
		String valueStr = ScDefineService.getDefineNameByCode(code);
		
		int startIndex = valueStr.indexOf("[")+1;
		
		int endIndex = valueStr.indexOf("]");
		
		valueStr = valueStr.substring(startIndex, endIndex);
		
		int value = 0;
		
		if(StringUtils.isNotBlank(valueStr)){
			
			value = Integer.parseInt(valueStr);
			
		}
		
		return value;
	
	}
	
	/**
	 * 获取同步日期
	 * @return
	 */
	public String getRsyncDate(){
		
		
		return ScDefineService.getDefineByCode(FamilyConfig.ORDER_RSYNC_DATE).get("define_name");
		
	}
	
	/**
	 * 获取同步结束日期
	 * @return
	 * @throws ParseException
	 */
	public String getRsyncEndDate() throws ParseException{
		
		Calendar startCalendar = Calendar.getInstance();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date startDate = dateFormat.parse(getRsyncDate());
		
		startCalendar.setTime(startDate);
		
		int field = getRsyncValue(FamilyConfig.ORDER_RSYNC_UNIT);
		
		int amount = getRsyncValue(FamilyConfig.ORDER_RSYNC_VALUE);
		
		startCalendar.add(field, amount);
		
		return dateFormat.format(startCalendar.getTime());
		
	}
	
	/**
	 * 更新统计日期
	 * @throws ParseException
	 */
	public void updateRsyncDate() throws ParseException{
		
		MDataMap mDataMap = ScDefineService.getDefineByCode(FamilyConfig.ORDER_RSYNC_DATE);
		
		String endDateStr = getRsyncEndDate();
		
		Calendar currCalendar = Calendar.getInstance();
		
		Calendar endCalendar = Calendar.getInstance();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date endDate = dateFormat.parse(endDateStr);
		
		endCalendar.setTime(endDate);
		
		if(currCalendar.compareTo(endCalendar) > 0){
			
			if(mDataMap != null){
				
				mDataMap.put("define_name", endDateStr);
				
			}
			
			DbUp.upTable("sc_define").update(mDataMap);
			
		}
		
	}
	
	/**
	 * 保存微公社与惠家有订单对照关系
	 * @param order_code
	 * @param out_order_code
	 */
	public void saveOrderRelCgroup(Order order, String out_order_code){
		
		if(StringUtils.isNotBlank(out_order_code)){
			
			int count = DbUp.upTable("oc_order_cgroup").count("order_code",order.getOrderCode());
			
			if(count == 0){
				
				if(StringUtils.isNotBlank(out_order_code)){
					
					MDataMap mDataMap = new MDataMap();
					
					mDataMap.put("order_code", order.getOrderCode());
					
					mDataMap.put("order_time", order.getCreateTime());
					
					mDataMap.put("out_order_code", out_order_code);
					
					mDataMap.put("create_time", DateUtil.getSysDateTimeString());	
					
					DbUp.upTable("oc_order_cgroup").dataInsert(mDataMap);
					
				}
				
			}
			
		}
		
	}
		

}
