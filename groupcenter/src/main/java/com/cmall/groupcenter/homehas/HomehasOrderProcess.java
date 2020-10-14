package com.cmall.groupcenter.homehas;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 家有订单处理
 * 
 * @author srnpr
 * 
 */
public class HomehasOrderProcess extends BaseClass {

	/**
	 * 插入订单状态表
	 */
	public MWebResult insertOrderStatus(GcExtendOrderStatusHomehas orderStatus) {

		MWebResult mWebResult = new MWebResult();

		// 定义清分类型
		String sReckon_Type = "";

		// 定义返利类型
		String rebateType = "";

		// 判断是否为null 如果为null则设置为空
		if (mWebResult.upFlagTrue()) {
			if (StringUtils.isEmpty(orderStatus.getChangeStatus())) {
				orderStatus.setChangeStatus("");
			}
			if (StringUtils.isEmpty(orderStatus.getOrderStatus())) {
				orderStatus.setOrderStatus("");
			}
			if (StringUtils.isEmpty(orderStatus.getSendStatus())) {
				orderStatus.setSendStatus("");
			}

			if (StringUtils.isEmpty(orderStatus.getUpdateTime())) {
				orderStatus.setUpdateTime("");
			}
		}

		if (mWebResult.upFlagTrue()) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.inAllValues("order_code", orderStatus.getOrderCode(),
					"order_status", orderStatus.getOrderStatus(),
					"send_status", orderStatus.getSendStatus(),
					"change_status", orderStatus.getChangeStatus());
			// 判断如果没有比这条更新的订单状态 则插入并判断
			if (DbUp.upTable("gc_extend_order_status_homehas").dataCount("",
					mDataMap) <= 0) {

				DbUp.upTable("gc_extend_order_status_homehas").insert(
						"order_code", orderStatus.getOrderCode(),
						"order_status", orderStatus.getOrderStatus(),
						"send_status", orderStatus.getSendStatus(),
						"change_status", orderStatus.getChangeStatus(),
						"update_time", orderStatus.getUpdateTime(),
						"create_time", FormatHelper.upDateTime());
			}
			// 如果订单状态是90 已签收 则正向清分
			if (orderStatus.getOrderStatus().equals("90")
					|| orderStatus.getSendStatus().equals("90")) {
				sReckon_Type = GroupConst.RECKON_ORDER_EXEC_TYPE_IN;

				// 更新清分订单上的订单完成时间
				MDataMap mUpdateOrderMap = new MDataMap();
				mUpdateOrderMap.inAllValues("order_code",
						orderStatus.getOrderCode(), "order_finish_time",
						orderStatus.getUpdateTime());

				// 更新表
				DbUp.upTable("gc_reckon_order_info").dataUpdate(
						mUpdateOrderMap, "order_finish_time", "order_code");

			} else {
				String sOrderStatus = ",91,92,93,94,95,96,97,98,99,";
				String sSendStatus = ",31,91,";
				String sChangestatus = ",10,";

				boolean bFlagBack = false;
				// 判断是否有订单状态
				if (StringUtils.contains(sOrderStatus,
						"," + orderStatus.getOrderStatus() + ",")) {
					bFlagBack = true;
				}
				// 判断是否有配送状态
				if (StringUtils.contains(sSendStatus,
						"," + orderStatus.getSendStatus() + ",")) {
					bFlagBack = true;
				}
				// 判断是否有退换货状态
				if (StringUtils.contains(sChangestatus,
						"," + orderStatus.getChangeStatus() + ",")) {
					// 换货订单不生成逆向清分 这条注释掉 2014-10-25
					// bFlagBack = true;
				}

				if (bFlagBack) {
					sReckon_Type = GroupConst.RECKON_ORDER_EXEC_TYPE_BACK;
					rebateType = GroupConst.REBATE_ORDER_EXEC_TYPE_BACK;
				}

			}

		}

		if (mWebResult.upFlagTrue()) {
			// 如果触发了清分流程变更 则开始插入流程变更表
			if (StringUtils.isNotEmpty(sReckon_Type)) {

				MDataMap mOrderMap = DbUp.upTable("gc_reckon_order_info").one(
						"order_code", orderStatus.getOrderCode());

				// 判断订单存在且订单参加清分流程
				if (mOrderMap != null
						&& mOrderMap.get("flag_reckon").equals("1")) {

					ReckonStep reckonStep = new ReckonStep();
					reckonStep.setAccountCode(mOrderMap.get("account_code"));
					reckonStep.setExecType(sReckon_Type);
					reckonStep.setOrderCode(mOrderMap.get("order_code"));

					mWebResult.inOtherResult(new GroupReckonSupport()
							.createReckonStep(reckonStep));

				} else {

					bLogInfo(918505132, orderStatus.getOrderCode());
				}
			}
		}

		// 进行预计返利相关,插入返利执行步骤表
		if (mWebResult.upFlagTrue()) {
			GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
			groupReckonSupport.checkCreateStep(orderStatus.getOrderCode(),
					GroupConst.REBATE_ORDER_EXEC_TYPE_IN);
			if (StringUtils.isNotEmpty(rebateType)) {
				groupReckonSupport.checkCreateStep(orderStatus.getOrderCode(),
						GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
			}

		}

		return mWebResult;

	}
}
