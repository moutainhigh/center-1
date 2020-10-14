package com.cmall.groupcenter.third.api;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.third.model.GroupPayInput;
import com.cmall.groupcenter.third.model.GroupPayResult;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class GroupPay extends RootApiForManage<GroupPayResult, GroupPayInput>{

	public GroupPayResult Process(GroupPayInput inputParam, MDataMap mRequestMap) {
		GroupPayResult groupPayResult=new GroupPayResult();
		try {
			inputParam.setTradeName(URLDecoder.decode(inputParam.getTradeName(),"UTF-8"));
			inputParam.setRemark(URLDecoder.decode(inputParam.getRemark(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GroupPayService groupPayService=new GroupPayService();

        //加入版本号控制
        //判断用户是否开通了支付功能
        TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();

        MWebResult result = traderOperationFilterService.checkOperationPayByManageCode(getManageCode());
        groupPayResult.inOtherResult(result);
        if (groupPayResult.upFlagTrue()){
            groupPayResult=groupPayService.GroupPay(inputParam,getManageCode());
        }

        return groupPayResult;
	}

}
