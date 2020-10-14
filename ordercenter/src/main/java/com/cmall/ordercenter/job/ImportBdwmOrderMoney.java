package com.cmall.ordercenter.job;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * 类: ImportBdwmOrderMoney <br>
 * 描述: 定时任务：同步百度外卖订单相关金额到惠家有订单表中 <br>
 * 作者: zhy<br>
 * 时间: 2016年8月12日 上午10:51:57
 */
public class ImportBdwmOrderMoney extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		try {
			/*
			 * 查询导入的百度订单集合
			 */
			// List<MDataMap> list =
			// DbUp.upTable("oc_orderinfo_bdwm").queryByWhere("is_sync", "0");
			List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo_bdwm").dataSqlList(
					"select * from ordercenter.oc_orderinfo_bdwm where  is_sync=0 and import_status='4497479500010002' ",
					null);
			if (list != null && list.size() > 0) {
				for (Map<String, Object> bdwm : list) {
					MDataMap map = new MDataMap(bdwm);
					// 商品总金额
					BigDecimal product_money_total = BigDecimal.valueOf(Double.valueOf(map.get("product_total_money")));
					// 运费
					BigDecimal freight = BigDecimal.valueOf(Double.valueOf(map.get("freight")));
					MDataMap orderMap = new MDataMap();
					// 订单编码
					orderMap.put("order_code", map.get("oc_order_code"));
					// 商品金额
					orderMap.put("product_money", product_money_total.toString());
					// 商品运费
					orderMap.put("transport_money", freight.toString());
					// 订单金额=商品总金额
					orderMap.put("order_money", product_money_total.toString());
					// 应付款
					orderMap.put("due_money", (product_money_total.add(freight)).toString());

					if (StringUtils.isNotBlank(map.get("oc_order_code"))) {
						int resultOrder = DbUp.upTable("oc_orderinfo").dataUpdate(orderMap,
								"product_money,transport_money,order_money,due_money", "order_code");
						if (resultOrder >= 0) {
							/*
							 * 修改订单信息表oc_orderinfo_upper
							 */
							MDataMap upper = new MDataMap();
							// 订单编码
							upper.put("order_code", map.get("oc_order_code"));
							// 订单金额
							upper.put("order_money", (product_money_total.add(freight)).toString());
							// 应付款
							upper.put("due_money", (product_money_total.add(freight)).toString());
							// 总的总金额
							upper.put("all_money", product_money_total.toString());
							upper.put("update_time", DateUtil.getSysDateTimeString());
							DbUp.upTable("oc_orderinfo_upper").dataExec(
									"UPDATE ordercenter.oc_orderinfo_upper SET order_money=:order_money,due_money=:due_money,all_money=:all_money,update_time=:update_time where big_order_code = (select big_order_code from ordercenter.oc_orderinfo where order_code=:order_code)",
									upper);
							// 修改百度订单表信息为已同步订单金额相关信息
							MDataMap editBdwm = new MDataMap();
							editBdwm.put("zid", map.get("zid"));
							editBdwm.put("is_sync", "1");
							editBdwm.put("remark", "同步成功,同步时间为" + DateUtil.getSysDateTimeString());
							DbUp.upTable("oc_orderinfo_bdwm").dataUpdate(editBdwm, "is_sync,remark", "zid");
						} else {
							// 添加错误日志到百度外卖订单表
							MDataMap editBdwm = new MDataMap();
							editBdwm.put("zid", map.get("zid"));
							editBdwm.put("error", "同步失败,同步时间为" + DateUtil.getSysDateTimeString());
							DbUp.upTable("oc_orderinfo_bdwm").dataUpdate(editBdwm, "is_sync,remark", "zid");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
