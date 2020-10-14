package com.cmall.groupcenter.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.txservice.TxRebateOrderService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 按订单号批量返利执行
 * @author panwei
 *
 */
public class BatchRebateOrderSupport extends BaseClass implements IBaseInstance{

	public final static BatchRebateOrderSupport INSTANCE=new BatchRebateOrderSupport();
	
	/**
	 * 对订单进行返利
	 * 
	 * @return
	 */
	/**
	 * @return
	 */
	public MWebResult batchRebateOrders(List<String> orderCodes) {

		MWebResult mWebResult = new MWebResult();
		for(String orderCode:orderCodes){
			// 首先执行正向返利订单
			rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_IN,orderCode);
			//执行重置返利订单
			rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_RESET,orderCode);
			// 接着执行逆向返利订单
			rebateOrderByType(GroupConst.REBATE_ORDER_EXEC_TYPE_BACK,orderCode);
		}
		return mWebResult;
	}

	/**
	 * 执行返利流程根据传入的返利类型
	 * 
	 * @param sType
	 * @return
	 */
	private MWebResult rebateOrderByType(String sType,String orderCode) {

		MDataMap mQueryMap = new MDataMap();
		mQueryMap.put("flag_success", "0");
		mQueryMap.put("exec_type", sType);

		mQueryMap.put("exec_start_time", DateHelper.upDateTimeAdd("-1d"));
		mQueryMap.put("order_code", orderCode);

		// 取出所有未成功执行过 且流程等于传入的流程参数 且开始执行日期为空或者小于昨天的 以保证同一条在同一天最多执行一次
		for (MDataMap mMap : DbUp
				.upTable("gc_reckon_order_step")
				.queryAll(
						"",
						"create_time",
						"flag_success=:flag_success and exec_type=:exec_type and (exec_start_time='' or exec_start_time<:exec_start_time) and order_code=:order_code",
						mQueryMap)) {

			ReckonStep reckonStep = new ReckonStep();
			reckonStep.setAccountCode(mMap.get("account_code"));
			reckonStep.setOrderCode(mMap.get("order_code"));
			reckonStep.setStepCode(mMap.get("step_code"));
			reckonStep.setFlagSucces(Integer.valueOf(mMap.get("flag_success")));
			reckonStep.setExecType(mMap.get("exec_type"));

			RebateOrderSupport support=new RebateOrderSupport();
			support.doRebateOrder(reckonStep);

		}

		return new MWebResult();

	}
}
