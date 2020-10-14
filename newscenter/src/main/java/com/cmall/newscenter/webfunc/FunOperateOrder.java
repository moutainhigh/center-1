package com.cmall.newscenter.webfunc;

import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.ordercenter.model.api.ApiOperateInput;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 确认收货
 * @author shiyz
 * date: 2014-12-10
 * @version1.0
 */
public class FunOperateOrder extends RootFunc{
	

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 设置相关信息
		if (mResult.upFlagTrue()) {
			
			OrderService orderService = new OrderService();
			
			ApiOperateInput input = new ApiOperateInput();
			//3为确认收货
			input.setType(3);
			
			input.setOrderCode(mSubMap.get("uid"));
			
			RootResult rootResult = orderService.operate(input,UserFactory.INSTANCE.create().getUserCode());
			
			if(rootResult.getResultCode()==1){
				
				GroupReckonSupport  groupReckonSupport = new GroupReckonSupport();
				
				groupReckonSupport.initByErpOrder(mSubMap.get("uid"), FormatHelper.upDateTime());
			}
			
			mResult.setResultCode(rootResult.getResultCode());
			
			mResult.setResultMessage(rootResult.getResultMessage());
			
		}
		
		return mResult;
	}
}




