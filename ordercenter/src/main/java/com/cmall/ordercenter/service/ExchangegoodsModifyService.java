package com.cmall.ordercenter.service;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ExchangegoodsStatusLogModel;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ExchangegoodsModifyService 
 * 类描述：     增加换货状态日志
 * 创建人：     gaoy  
 * 创建时间：2013年9月16日下午1:53:03
 * 修改人：     gaoy
 * 修改时间：2013年9月16日下午1:53:03
 * 修改备注：  
 * @version
 *
 */
public class ExchangegoodsModifyService implements IFlowFunc{

	public static final String UPDATE_EXCHANGEGOODS = "oc_exchange_goods";
	
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult ret = new RootResult();
		//System.out.print("CommonFlowFunc-BeforeTest");
		
		ret.setResultCode(1);
		
		return ret;
	}

	/**
	 * 增加换货状态日志（页面点击状态修改时调用）
	 */
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		
		RootResult ret = new RootResult();
		
		//创建人
		String createUser = UserFactory.INSTANCE.create().getUserCode();
		
		ExchangegoodsStatusLogModel egLogModel = new ExchangegoodsStatusLogModel();
		ExchangegoodsStatusLogService egLogService = new ExchangegoodsStatusLogService();
		
		//获取选中数据的信息，从中取得“换货单号”
		MDataMap mdOne = DbUp.upTable(UPDATE_EXCHANGEGOODS).one("uid", outCode);
		
		//取得“换货单号”
		String strExchangeNo = mdOne.get("exchange_no");
		//换货单号
		egLogModel.setExchangeNo(strExchangeNo);
		//日志信息
		egLogModel.setInfo("");
		//创建时间
		egLogModel.setCreateTime(DateUtil.getSysDateTimeString());
		//创建人
		egLogModel.setCreateUser(createUser);
		//旧状态
		egLogModel.setOldStatus(fromStatus);
		//新状态
		egLogModel.setNowStatus(toStatus);
		
		try{
			egLogService.addExchangegoodsStatusLogService(egLogModel);
		}catch(Exception e) {
			ret.setResultCode(0);
		}
		
		ret.setResultCode(1);
		return ret;
	}
}
