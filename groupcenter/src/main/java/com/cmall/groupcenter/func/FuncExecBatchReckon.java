package com.cmall.groupcenter.func;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.support.BatchGroupReckonSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 执行清分
 * 
 * @author srnpr
 * 
 */
public class FuncExecBatchReckon extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult=new MWebResult();
		String orderCode = mDataMap.get("zw_f_order_code");
		if(mWebResult.upFlagTrue()){
			if(StringUtils.isNotBlank(orderCode)){
				//按照换行符截取提款单号
				String[] orderAry = orderCode.split("\n");
				List<String> orderCodes=Arrays.asList(orderAry);
				if(orderAry.length >0){
					BatchGroupReckonSupport groupReckonSupport = new BatchGroupReckonSupport();
					groupReckonSupport.batchReckonOrders(orderCodes);
				}
			}else{
				mWebResult.setResultMessage("请输入订单号！");
			}
		}
	   
		

		return mWebResult;

	}

}
