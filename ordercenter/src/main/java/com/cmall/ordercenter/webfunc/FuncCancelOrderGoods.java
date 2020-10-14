package com.cmall.ordercenter.webfunc;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商户后台取消发货
 * @author jlin
 *
 */
public class FuncCancelOrderGoods extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		
		MDataMap mEditMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String order_code = mEditMaps.get("uid");
		
		OrderService orderService = new OrderService();
		
		String loginname=UserFactory.INSTANCE.create().getUserCode();
		
		RootResult res = orderService.cancelOrderByShop(order_code,loginname);
		//需要校验该订单是否是分销商品订单，如果是，需要写入定时
		if(res.getResultCode() == 1 && DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
			JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.addMinute(5));//插入定时任务，五分钟后执行
		}
		
		mResult.setResultCode(res.getResultCode());
		mResult.setResultMessage(res.getResultMessage());
		
		return mResult;
	}
}
