package com.cmall.groupcenter.service;

import com.cmall.groupcenter.FamilyConfig;
import com.cmall.groupcenter.GroupConstant.PayOrderStatusEnum;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput.OrderStatusInfo;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusResult;
import com.cmall.systemcenter.service.ScDefineService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.ApiFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 与微公社同步退货通过审核订单务实现
 * 
 * @author zmm
 *
 */

public class RsyncReturnOrderService extends BaseClass {

	/**
	 * 与微公社接口同步订单
	 * 
	 * @return 接口响应信息
	 * @throws ParseException
	 * @throws IOException
	 */
	public void doRsync() throws ParseException {

		doRsyncHAS();

	}

	/**
	 * 惠家有与微公社接口同步订单
	 * 
	 * @return 接口响应信息
	 * @throws ParseException
	 * @throws IOException
	 */
	public void doRsyncHAS() throws ParseException {

		String mamageCode ="SI2003";
		// 获取退款成的订单编号
		List<MDataMap> statusList = getRsyncReturnOrderStatusList();
		/* 同步订单状态信息 */
		rsyncOrderStatus(mamageCode, statusList);
		/* 更新同步日期 */
		updateRsyncDate();

	}

	/**
	 * 同步订单状态
	 * 
	 * @param orderList
	 */
	public synchronized void rsyncOrderStatus(String mamageCode,List<MDataMap> orderList) {

		/* 转换订单状态json参数 */
		String sInputJson = convertOrderStatusParams(orderList);

		try {

			MDataMap mDataMap = new MDataMap();

			mDataMap.put(WebConst.CONST_APIFACTORY_FOR_PARAM+ "api_manage_code", mamageCode);

			String result = ApiFactory.INSTANCE.doProcess("com.cmall.groupcenter.sync.orderstatus.ApiSynOrderStatus",sInputJson, mDataMap);

			if (!rsyncStatus(result, new SyncOrderStatusResult())) {

				bLogInfo(0, "与微公社同步订单状态失败:" + result);

			}

		} catch (IOException e) {

			bLogError(0, "与微公社同步订单状态失败 " + e.getMessage());

		}

	}

	/**
	 * 根据订单信息转换订单状态同步接口参数
	 * @param orderCode 订单编号
	 * @return json格式数据
	 */
	public String convertOrderStatusParams(List<MDataMap> list) {

		SyncOrderStatusInput input = new SyncOrderStatusInput();

		List<OrderStatusInfo> orderStatusList = new ArrayList<OrderStatusInfo>();

		OrderStatusInfo orderStatusInfo = null;

		String groupOrderCode = "";
		String orderStatus="4497153900050006";

		for (MDataMap mDataMap : list) {

			MDataMap groupDataMap = getGroupOrderInfo(mDataMap.get("order_code"));

			/* 微公社订单不存在 */
			if (groupDataMap == null) {
				continue;
			}
			groupOrderCode = groupDataMap.get("out_order_code");
			/* 微公社同一订单状态已经存在 */
			if (existGroupStatus(groupOrderCode, orderStatus)) {
				continue;
			}
			orderStatusInfo = new OrderStatusInfo();
			orderStatusInfo.setOrderCode(groupOrderCode);
			orderStatusInfo.setOrderStatus(getGroupNO("4497153900010006"));
			orderStatusInfo.setRemark("");
			orderStatusInfo.setUpdateTime(mDataMap.get("create_time"));
			orderStatusList.add(orderStatusInfo);
		}
		input.setOrderStatusInfos(orderStatusList);
		JsonHelper<SyncOrderStatusInput> jsonHelper = new JsonHelper<SyncOrderStatusInput>();
		return jsonHelper.ObjToString(input);
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
	 * 根据惠家有的订单编号获取微公社订单信息
	 * @param order_code  惠家有订单编号
	 * @return 微公社订单信息
	 */
	public MDataMap getGroupOrderInfo(String order_code) {
		return DbUp.upTable("oc_order_cgroup").oneWhere("out_order_code","", "order_code=:order_code","order_code",order_code);
	}

	/**
	 * 获取需同步的订单信息(退货成功的)
	 * 
	 * @return List<MDataMap> 同步的订单信息
	 * @throws ParseException
	 */
	public List<MDataMap> getRsyncReturnOrderStatusList() throws ParseException {
		MDataMap statusMap = new MDataMap();
		statusMap.put("order_status", "4497153900050001");
		List<MDataMap> statusList = RsyncReturnOrderBeanFactory.getInstance().
				getRsyncReturnOrderDao().queryRsyncReturnOrderList(statusMap);
		return statusList;
	}

	/**
	 * 判断是否同步成功
	 * @param result 接口返回结果
	 * @return 是否同步成功true|false
	 */
	public boolean rsyncStatus(String result, RootResultWeb iBaseResult) {

		JsonHelper<RootResultWeb> jsonHelper = new JsonHelper<RootResultWeb>();

		RootResultWeb orderRebateResult = jsonHelper.StringToObj(result,iBaseResult);

		return orderRebateResult.upFlagTrue();

	}

	/**
	 * 判断日志同步失败的次数是否超过最大限制
	 * 
	 * @param errorCount  同步失败的次数
	 * @return true|超过限制 false|未超过限制
	 * @throws Exception
	 */
	public boolean moreThanLimit(MDataMap mDataMap) throws Exception {

		boolean flag = true;

		String errorCount = mDataMap.get("error_count");

		int count = Integer.parseInt(errorCount);

		if (count > getCountLimit()) {

			flag = false;

		}

		return flag;

	}

	/**
	 * 获取限制次数
	 * 
	 * @return
	 */
	public int getCountLimit() {

		/* 获取限制次数 */
		String error_count = bConfig("familyhas.rsync_order_cgroup_limit");

		return Integer.parseInt(error_count);

	}

	public boolean existGroupStatus(String order_code, String order_status) {

		boolean flag = false;

		int count = DbUp.upTable("gc_sync_order_status").count("order_code",order_code, "order_status", order_status);

		if (count >= 1) {

			flag = true;

		}

		return flag;

	}

	/**
	 * 获取同步区间的值
	 * 
	 * @param code
	 *            值编码
	 * @return
	 */
	public int getRsyncValue(String code) {

		String valueStr = ScDefineService.getDefineNameByCode(code);

		int startIndex = valueStr.indexOf("[") + 1;

		int endIndex = valueStr.indexOf("]");

		valueStr = valueStr.substring(startIndex, endIndex);

		int value = 0;

		if (StringUtils.isNotBlank(valueStr)) {

			value = Integer.parseInt(valueStr);

		}

		return value;

	}

	/**
	 * 获取同步日期
	 * 
	 * @return
	 */
	public String getRsyncDate() {

		return ScDefineService.getDefineByCode(FamilyConfig.ORDER_RSYNC_DATE).get("define_name");

	}

	/**
	 * 获取同步结束日期
	 * 
	 * @return
	 * @throws ParseException
	 */
	public String getRsyncEndDate() throws ParseException {

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
	 * 
	 * @throws ParseException
	 */
	public void updateRsyncDate() throws ParseException {

		MDataMap mDataMap = ScDefineService
				.getDefineByCode(FamilyConfig.ORDER_RSYNC_DATE);

		String endDateStr = getRsyncEndDate();

		Calendar currCalendar = Calendar.getInstance();

		Calendar endCalendar = Calendar.getInstance();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date endDate = dateFormat.parse(endDateStr);

		endCalendar.setTime(endDate);

		if (currCalendar.compareTo(endCalendar) > 0) {

			if (mDataMap != null) {

				mDataMap.put("define_name", endDateStr);

			}

			DbUp.upTable("sc_define").update(mDataMap);

		}

	}

}
