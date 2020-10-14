package com.cmall.groupcenter.support;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.ReckonOrderInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
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
 * 批量清分执行支撑类
 * 
 * @author panwei
 * 
 */
public class BatchGroupReckonSupport extends BaseClass implements IBaseInstance {

	/**
	 * 清分所有订单 在有流程变更时触发
	 * 
	 * @return
	 */
	/**
	 * @return
	 */
	public MWebResult batchReckonOrders(List<String> orderCodes) {

		MWebResult mWebResult = new MWebResult();
		for(String orderCode:orderCodes){
			// 首先执行正向清分订单
			reckonOrderByType(GroupConst.RECKON_ORDER_EXEC_TYPE_IN,orderCode);
			// 接着执行逆向清分订单
			reckonOrderByType(GroupConst.RECKON_ORDER_EXEC_TYPE_BACK,orderCode);
			//执行第三方退货
			reckonOrderByType(GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK,orderCode);

			//执行“取消退货清分流程”
			reckonOrderByType(GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE,orderCode);
		}
		return mWebResult;
	}

	/**
	 * 执行清分流程根据传入的清分类型
	 * 
	 * @param sType
	 * @return
	 */
	private MWebResult reckonOrderByType(String sType,String orderCode) {

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
						"flag_success=:flag_success and exec_type=:exec_type and (exec_start_time='' or exec_start_time<:exec_start_time) and order_code=:order_code ",
						mQueryMap)) {

			ReckonStep reckonStep = new ReckonStep();
			reckonStep.setAccountCode(mMap.get("account_code"));
			reckonStep.setOrderCode(mMap.get("order_code"));
			reckonStep.setStepCode(mMap.get("step_code"));
			reckonStep.setFlagSucces(Integer.valueOf(mMap.get("flag_success")));
			reckonStep.setExecType(mMap.get("exec_type"));
			reckonStep.setUqcode(mMap.get("uqcode"));
			reckonStep.setCreateTime(mMap.get("create_time"));

			GroupReckonSupport support=new GroupReckonSupport();
			support.doReckonOrder(reckonStep);

		}

		return new MWebResult();

	}

}
