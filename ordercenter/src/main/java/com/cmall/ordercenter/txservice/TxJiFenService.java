package com.cmall.ordercenter.txservice;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.cmall.dborm.txmodel.JifenInfo;
import com.cmall.dborm.txmodel.JifenInfoExample;
import com.cmall.dborm.txmodel.JifenLog;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbface.ITxService;

public class TxJiFenService extends BaseClass implements ITxService {

	
	
	/**
	 * @param jf 积分实体
	 * @param type 0 加积分 1 减积分
	 * @param ret 
	 * @param operator 操作人
	 * @param operatorName 操作人编号
	 * @param tradeCode 流水编号
	 * @throws Exception
	 */
	public void updateJiFen(JifenInfo jf,int type, RootResult ret, String operator,String operatorName,String tradeCode) {

		if(type !=1&& type !=0)
		{
			ret.setResultCode(939301092);
			ret.setResultMessage(bInfo(939301092));
			return;
		}
		
		com.cmall.dborm.txmapper.JifenInfoMapperForD lcom = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_JifenInfoMapperForD");
		com.cmall.dborm.txmapper.JifenLogMapper lsom = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_JifenLogMapper");
		
		JifenInfoExample selectExample = new JifenInfoExample();
		selectExample.createCriteria().andObjectEqualTo(jf.getObject()).andTypeEqualTo(3);
		List<JifenInfo> list = lcom.selectByExample(selectExample);
		
		if(list == null || list.size() == 0){
			ret.setResultCode(939301093);
			ret.setResultMessage(bInfo(939301093));
			return;
		}
		float balanceMoney = 0;
		if(type == 1){
			balanceMoney = list.get(0).getValue()-jf.getValue();
		}else{
			balanceMoney = list.get(0).getValue()+jf.getValue();
		}
		
		
		JifenInfo updateEntity = new JifenInfo();
		int timeStamp = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(0, 10));
		updateEntity.setOptime(timeStamp);
		//0是系统，1是代理商，2是商家，3是用户
		//updateEntity.setType(3);
		if(type == 0)
			updateEntity.setValue(-jf.getValue());
		else
			updateEntity.setValue(jf.getValue());
		
		
		updateEntity.setObject(jf.getObject());
		
		
		selectExample = new JifenInfoExample();
		selectExample.createCriteria().andObjectEqualTo(jf.getObject()).andTypeEqualTo(3).andValueGreaterThanOrEqualTo(updateEntity.getValue());
		
		//如果返回值为 1 则继续，否则抛异常
		int count = lcom.updateByExampleSelective(updateEntity, selectExample);
		
		if(count<=0){
			ret.setResultCode(939301094);
			ret.setResultMessage(bInfo(939301094));
			return ;
		}
				
		JifenLog log = new JifenLog();
		
		//0是充值，1是发放
		log.setAction(type);
		//积分操作者余额
		log.setFromBalance(0f);
		//操作者ID
		log.setFromId(operator);
		//操作者名称
		log.setFromName(operatorName);
		
		//积分操作者类型，0是系统，1是代理商，2是商家，3是用户
		log.setFromType(0);
		//操作者积分值
		log.setFromValue("0");
		//操作时间
		log.setOpTime(DateUtil.getSysDateTimeString());
		//支付方式，1是支付宝，2是预付款
		log.setPayment(0);
		//积分操作状态 0是已发放，1是已到帐，2是实时充值
		if(type == 0){
			log.setStatus(2);
			log.setStatusName("实时充值");
		}
		else{
			log.setStatus(0);
			log.setStatusName("已发放");
		}
		
		
		
		//积分操作对象余额
		log.setToBalance(balanceMoney);
		//操作对象ID
		log.setToId(jf.getObject());
		//操作对象名称,暂时未知
		log.setToName("");
		//积分操作对象类型，0是系统，1是代理商，2是商家，3是用户
		log.setToType(3);
		//操作对象积分值
		log.setToValue(list.get(0).getValue().toString());
		log.setTradeCode(tradeCode);
		log.setUid(UUID.randomUUID().toString().replace("-", ""));
		log.setValue(jf.getValue());
		
		lsom.insertSelective(log);
		
	}
}
