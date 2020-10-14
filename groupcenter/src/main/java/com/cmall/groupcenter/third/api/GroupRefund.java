package com.cmall.groupcenter.third.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.jsoup.helper.StringUtil;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

public class GroupRefund extends RootApiForManage<GroupRefundResult, GroupRefundInput>{

	public GroupRefundResult Process(GroupRefundInput inputParam,
			MDataMap mRequestMap) {
		GroupRefundResult groupRefundResult=new GroupRefundResult();
		try {
			inputParam.setRemark(URLDecoder.decode(inputParam.getRemark(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//判断用户是否开通了支付功能
		TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();
		MWebResult webResult = traderOperationFilterService.checkOperationPayByManageCode(getManageCode());
		groupRefundResult.inOtherResult(webResult);

		if (groupRefundResult.upFlagTrue()){//如果开通了支付功能，才进行后面的事情
			GroupPayService groupPayService=new GroupPayService();
			if(StringUtil.isBlank(inputParam.getBusinessTradeCode())){
				groupRefundResult=groupPayService.groupRefund(inputParam, getManageCode());
			}else{
				groupRefundResult=groupPayService.groupRefundSome(inputParam, getManageCode());
			}
				
			

			
    	}
		return groupRefundResult;
	}

	
	

}
