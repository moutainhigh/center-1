package com.cmall.groupcenter.support;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.groupcenter.txservice.TxRebateOrderService;
import com.cmall.groupcenter.txservice.TxRebateOrderServiceForConsumptionAmount;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 返利执行
 * @author chenbin
 *
 */
public class RebateOrderSupport extends BaseClass implements IBaseInstance{

	public final static RebateOrderSupport INSTANCE=new RebateOrderSupport();
	
	/**
	 * 对订单进行返利
	 * 
	 * @return
	 */
	/**
	 * @return
	 */
	public MWebResult rebateAllOrders() {

		MWebResult mWebResult = new MWebResult();

		// 首先执行正向返利订单
		rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_IN);
		//执行重置返利订单
		rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_RESET);
		// 接着执行逆向返利订单
		rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);

		return mWebResult;
	}

	/**
	 * 执行返利流程根据传入的返利类型
	 * 
	 * @param sType
	 * @return
	 */
	private MWebResult rebateOrderByType(String sType) {

		MDataMap mQueryMap = new MDataMap();
		mQueryMap.put("flag_success", "0");
		mQueryMap.put("exec_type", sType);

		mQueryMap.put("exec_start_time", DateHelper.upDateTimeAdd("-1d"));

		// 取出所有未成功执行过 且流程等于传入的流程参数 且开始执行日期为空或者小于昨天的 以保证同一条在同一天最多执行一次
		for (MDataMap mMap : DbUp
				.upTable("gc_reckon_order_step")
				.queryAll(
						"",
						"create_time",
						"flag_success=:flag_success and exec_type=:exec_type and (exec_start_time='' or exec_start_time<:exec_start_time)",
						mQueryMap)) {

			ReckonStep reckonStep = new ReckonStep();
			reckonStep.setAccountCode(mMap.get("account_code"));
			reckonStep.setOrderCode(mMap.get("order_code"));
			reckonStep.setStepCode(mMap.get("step_code"));
			reckonStep.setFlagSucces(Integer.valueOf(mMap.get("flag_success")));
			reckonStep.setExecType(mMap.get("exec_type"));

			doRebateOrder(reckonStep);

		}

		return new MWebResult();

	}
	
	/**
	 * 执行返利流程
	 * 
	 * @param mDataMap
	 * @return
	 */
	public MWebResult doRebateOrder(ReckonStep reckonStep) {
		MWebResult mWebResult = new MWebResult();

		// 账户编号
		String sAccountCode = reckonStep.getAccountCode();
		
		// 再次强制判断是否可执行
		if (mWebResult.upFlagTrue()) {
			if (!DbUp.upTable("gc_reckon_order_step")
					.one("step_code", reckonStep.getStepCode())
					.get("flag_success").equals("0")) {
				return mWebResult;
			}
		}

		// 判断是否已执行
		if (mWebResult.upFlagTrue()) {
			if (reckonStep.getFlagSucces() != 0) {
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
			}
		}
				
		// 更新开始执行时间
		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();
			mDataMap.put("step_code", reckonStep.getStepCode());

			// 设置开始执行时间
			mDataMap.put("exec_start_time", FormatHelper.upDateTime());
			// 更新开始执行时间
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mDataMap,
					"exec_start_time", "step_code");

		}
		
		GroupAccountSupport groupAccountSupport = new GroupAccountSupport();

		// 取出订单创建时间
		MDataMap rInfoDataMap = DbUp
				.upTable("gc_reckon_order_info")
				.oneWhere("manage_code,order_create_time", "", "", "order_code",
						reckonStep.getOrderCode());
		String sOrderCreateTime = rInfoDataMap.get("order_create_time");
		
		// 获取订单创建时间之前的所有该用户的上线
		List<AccountRelation> listRelations = groupAccountSupport
				.upAccountRelations(reckonStep.getAccountCode(),
						sOrderCreateTime);

		// 定义list 存放账户编号
		List<String> listMaps = new ArrayList<String>();

		// 开始判断所有关联用户是否有微公社账户信息 没有则自动创建
		if (mWebResult.upFlagTrue()) {

			for (AccountRelation accountRelation : listRelations) {
				listMaps.add(accountRelation.getAccountCode());
			}

			mWebResult.inOtherResult(groupAccountSupport
					.checkAndCreateGroupAccount(listMaps
							.toArray(new String[] {})));

		}

		// 开始锁定执行流程编号 防止并发执行
		String sLock = "";

		// 开始加锁
		if (mWebResult.upFlagTrue()) {
			// 将流程编号也加入list中 以进行锁操作
			listMaps.add(reckonStep.getStepCode());
			sLock = WebHelper.addLock(30, listMaps.toArray(new String[] {}));

			if (StringUtils.isEmpty(sLock)) {

				mWebResult.inErrorMessage(918505133, reckonStep.getStepCode());
			}
		}
		
		//获取订单所属商户的返利方式和使用时间
		String rebateType = "";
		String applyTime = "";
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			String sManageCode = rInfoDataMap.get("manage_code");
			GcTraderInfo gcTraderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
				gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
				//商户返利方式
				rebateType = gcTraderInfo.getRebateType();
				//开始应用时间
				applyTime = gcTraderInfo.getTypeApplyTime();
			}
		}
		
		// 开始执行流程
		if (mWebResult.upFlagTrue()) {
			
			TxRebateOrderService txRebateOrderService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxRebateOrderService");
			TxRebateOrderServiceForConsumptionAmount txRebateOrderServiceForConsumptionAmount = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxRebateOrderServiceForConsumptionAmount");
			// 这里开始执行 如果执行失败则返回失败消息
			try {
				
				// 开始执行正向返利流程
				if (reckonStep.getExecType().equals(
						GroupConst.REBATE_ORDER_EXEC_TYPE_IN)) {
					if(VersionHelper.checkServerVersion("11.9.41.59")){
						if (mWebResult.upFlagTrue()){
							//按照等级方式返利
							if("4497472500080001".equals(rebateType)){
								//订单创建时间必须在执行时间以后才能计算返利(考虑以后的需求,此处先添加判断)
//								if(StringUtils.isNotBlank(applyTime) && comperDate(applyTime,sOrderCreateTime)<0){
//									mWebResult.inErrorMessage(918512008,applyTime,sOrderCreateTime);
//								}
								if (mWebResult.upFlagTrue()){
									mWebResult.inOtherResult(txRebateOrderService.doRebateInForFifth(reckonStep, listRelations));
								}
							}else if("4497472500080002".equals(rebateType)){
								//按照消费金额返利
								//订单创建时间必须在执行时间以后才能按照消费金额返利,否则按照等级方式返利
								if(comperDate(applyTime,sOrderCreateTime)<0){
//									mWebResult.inErrorMessage(918512008,applyTime,sOrderCreateTime);
									//按照等级方式返利
									mWebResult.inOtherResult(txRebateOrderService.doRebateInForFifth(reckonStep, listRelations));
								}else{
									if (mWebResult.upFlagTrue()){
										mWebResult.inOtherResult(txRebateOrderServiceForConsumptionAmount.doRebateInByMoneyForOne(reckonStep, listRelations));
									}
								}
							}else{
								//默认按照等级方式返利
								//订单创建时间必须在执行时间以后才能计算返利(考虑以后的需求,此处先添加判断)
//								if(StringUtils.isNotBlank(applyTime) && comperDate(applyTime,sOrderCreateTime)<0){
//									mWebResult.inErrorMessage(918512008,applyTime,sOrderCreateTime);
//								}
								if (mWebResult.upFlagTrue()){
									mWebResult.inOtherResult(txRebateOrderService.doRebateInForFifth(reckonStep, listRelations));
								}
							}
						}
					}else{
						mWebResult.inOtherResult(txRebateOrderService.doRebateInForFifth(reckonStep, listRelations));
					}
				}
				// 开始执行逆向返利流程
				else if (reckonStep.getExecType().equals(
						GroupConst.REBATE_ORDER_EXEC_TYPE_BACK)) {
					mWebResult.inOtherResult(txRebateOrderService.doRebateBackForForth(reckonStep, listRelations));
				}
				//开始执行重置返利流程
				else if(reckonStep.getExecType().equals(GroupConst.REBATE_ORDER_EXEC_TYPE_RESET)){
					mWebResult.inOtherResult(txRebateOrderService.doResetRebateForSecond(reckonStep, listRelations));
				}
				// 如果没有 则报失败信息
				else {

					mWebResult.inErrorMessage(918505135,
							reckonStep.getStepCode(), reckonStep.getExecType());
				}
			} catch (Exception e) {
				e.printStackTrace();
				mWebResult.inErrorMessage(918505136, e.getMessage());
			}

		}

		MDataMap mUpdatemMap = new MDataMap();

		// 如果都执行成功，则将执行成功标记位置为1
		if (mWebResult.upFlagTrue()) {
			mUpdatemMap.put("flag_success", "1");
		} else {
			mUpdatemMap.put("flag_success",
					String.valueOf(reckonStep.getFlagSucces()));
		}

		mUpdatemMap.put("exec_finish_time", FormatHelper.upDateTime());
		mUpdatemMap.put("exec_result", mWebResult.upJson());
		mUpdatemMap.put("step_code", reckonStep.getStepCode());
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			mUpdatemMap.put("rebate_type", rebateType);
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mUpdatemMap,
					"exec_finish_time,exec_result,flag_success,rebate_type", "step_code");
		}else{
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mUpdatemMap,
					"exec_finish_time,exec_result,flag_success", "step_code");
		}

		// 该逻辑放在最后 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(sLock)) {
			WebHelper.unLock(sLock);
		}

		return mWebResult;
	}

	/**
	 * 判断执行时间
	 * @param applyTime
	 * @param sOrderCreateTime
	 * @return
	 */
	private int comperDate(String applyTime, String sOrderCreateTime) {
		try {
			
			if(StringUtils.isBlank(applyTime)){
				return -1;
			}
			if(StringUtils.isBlank(sOrderCreateTime)){
				return -1;
			}
			
			Date date1=DateUtil.sdfDateTime.parse(sOrderCreateTime);
			Date date2=DateUtil.sdfDateTime.parse(applyTime);
			return date1.compareTo(date2);
		} catch (ParseException e) {
			return -1;
		}
		
	}

}
