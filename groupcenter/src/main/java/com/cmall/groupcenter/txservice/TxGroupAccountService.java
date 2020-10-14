package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcAccountChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupLevelMapper;
import com.cmall.dborm.txmapper.groupcenter.GcLevelLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderAccountChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderAccountLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderFoundsChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcWithdrawLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcAccountChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccount;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccountExample;
import com.cmall.dborm.txmodel.groupcenter.GcGroupLevelExample;
import com.cmall.dborm.txmodel.groupcenter.GcLevelLog;
import com.cmall.dborm.txmodel.groupcenter.GcLevelLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLog;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderAccountChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderAccountLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.ordercenter.model.api.AccountConfirmResult;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class TxGroupAccountService {

	/**
	 * 创建微公社账户表
	 * 
	 * @param sAccountCode
	 * @param sManageCode
	 * @return
	 */
	public MWebResult doCreateAccount(String sAccountCode, String sManageCode) {

		MWebResult mWebResult = new MWebResult();

		if (mWebResult.upFlagTrue()) {

			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

			GcAccountChangeLogMapper gcAccountChangeLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcAccountChangeLogMapper");

			GcGroupAccount gcGroupAccount = new GcGroupAccount();
			gcGroupAccount.setUid(WebHelper.upUuid());

			gcGroupAccount.setAccountCode(sAccountCode);

			MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
					"level_code", GroupConst.DEFAULT_LEVEL_CODE);
			gcGroupAccount.setAccountLevel(mLevelMap.get("level_code"));
			gcGroupAccount.setScaleReckon(new BigDecimal(mLevelMap
					.get("scale_reckon")));
			gcGroupAccount.setLevelType(mLevelMap.get("level_type"));
			gcGroupAccount.setCreateTime(FormatHelper.upDateTime());

			gcGroupAccountMapper.insertSelective(gcGroupAccount);

			GcAccountChangeLog gcAccountChangeLog = new GcAccountChangeLog();

			gcAccountChangeLog.setUid(WebHelper.upUuid());

			gcAccountChangeLog.setAccountCode(gcGroupAccount.getAccountCode());

			gcAccountChangeLog.setCreateTime(FormatHelper.upDateTime());

			gcAccountChangeLogMapper.insertSelective(gcAccountChangeLog);

		}

		return mWebResult;

	}

	/**
	 * 更新账户信息 根据日志更新 调用该方法前必须lock账户编号
	 * 
	 * @param listReckonLogs
	 * @param listWithdrawLogs
	 */
	public void updateAccount(List<GcReckonLog> listReckonLogs,
			List<GcWithdrawLog> listWithdrawLogs) {

		// 定义账户的可清分金额
		BigDecimal bAddReckon = BigDecimal.ZERO;

		// 定义账户可提现金额
		BigDecimal bAddWithdram = BigDecimal.ZERO;

		// 定义总计清分账户变动金额
		BigDecimal bTotalReckonChange = BigDecimal.ZERO;

		// 定义总计可提现账户变动金额
		BigDecimal bTotalWithdrawChange = BigDecimal.ZERO;

		// 定义账户变动日志流水

		String sAccountCode = "";

		List<String> changeCodes = new ArrayList<String>();

		if (listReckonLogs != null && listReckonLogs.size() > 0) {

			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

			// 循环并插入清分流水
			for (GcReckonLog item : listReckonLogs) {

				if (StringUtils.isEmpty(sAccountCode)) {
					sAccountCode = item.getAccountCode();
				}

				item.setUid(WebHelper.upUuid());
				item.setCreateTime(FormatHelper.upDateTime());
				item.setLogCode(WebHelper.upCode("GCRL"));

				bAddReckon = bAddReckon.add(item.getReckonMoney());

				gcReckonLogMapper.insertSelective(item);

				changeCodes.add(item.getLogCode());

				// 限定只有特定类型时才更新总账
				if (StringUtils.indexOf(
						"4497465200030001,4497465200030002,4497465200030003",
						item.getReckonChangeType()) > -1) {
					bTotalReckonChange = bTotalReckonChange.add(item
							.getReckonMoney());
				}

			}
		}

		if (listWithdrawLogs != null && listWithdrawLogs.size() > 0) {

			GcWithdrawLogMapper gcWithdrawLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcWithdrawLogMapper");

			for (GcWithdrawLog item : listWithdrawLogs) {
				if (StringUtils.isEmpty(sAccountCode)) {
					sAccountCode = item.getAccountCode();
				}

				item.setUid(WebHelper.upUuid());
				item.setCreateTime(FormatHelper.upDateTime());
				item.setLogCode(WebHelper.upCode("GCWL"));
				bAddWithdram = bAddWithdram.add(item.getWithdrawMoney());
				gcWithdrawLogMapper.insertSelective(item);

				changeCodes.add(item.getLogCode());

				// 限定只有特定类型时才更新总账
				if (StringUtils.indexOf(
						"4497465200040001,4497465200040003,4497465200040004,4497465200040010,4497465200040011",
						item.getWithdrawChangeType()) > -1) {
					bTotalWithdrawChange = bTotalWithdrawChange.add(item
							.getWithdrawMoney());
				}

			}
		}

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
		GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
		gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
				sAccountCode);

		// 定义当前微公社账户信息
		GcGroupAccount gcGroupAccount = gcGroupAccountMapper.selectByExample(
				gcGroupAccountExample).get(0);

		GcGroupAccount gcUpdateAccount = new GcGroupAccount();
		gcUpdateAccount.setAccountReckonMoney(gcGroupAccount
				.getAccountReckonMoney().add(bAddReckon));

		gcUpdateAccount.setTotalReckonMoney(gcGroupAccount
				.getTotalReckonMoney().add(bTotalReckonChange));

		gcUpdateAccount.setAccountWithdrawMoney(gcGroupAccount
				.getAccountWithdrawMoney().add(bAddWithdram));
		gcUpdateAccount.setTotalWithdrawMoney(gcGroupAccount
				.getTotalWithdrawMoney().add(bTotalWithdrawChange));

		// 更新账户信息
		gcGroupAccountMapper.updateByExampleSelective(gcUpdateAccount,
				gcGroupAccountExample);

		// 插入账户变更历史
		GcAccountChangeLogMapper gcAccountChangeLogMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcAccountChangeLogMapper");

		GcAccountChangeLog gcAccountChangeLog = new GcAccountChangeLog();

		gcAccountChangeLog.setUid(WebHelper.upUuid());
		gcAccountChangeLog.setAccountCode(gcGroupAccount.getAccountCode());
		gcAccountChangeLog.setChangeCodes(StringUtils.join(changeCodes,
				WebConst.CONST_SPLIT_COMMA));
		gcAccountChangeLog.setCreateTime(FormatHelper.upDateTime());
		gcAccountChangeLog.setCurrentReckonMoney(gcUpdateAccount
				.getAccountReckonMoney());
		gcAccountChangeLog.setLastReckonMoney(gcGroupAccount
				.getAccountReckonMoney());

		gcAccountChangeLog.setLastWithdrawMoney(gcGroupAccount
				.getAccountWithdrawMoney());
		gcAccountChangeLog.setCurrentWithdrawMoney(gcUpdateAccount
				.getAccountWithdrawMoney());
		
		gcAccountChangeLog.setLastRebateMoney(gcGroupAccount.getAccountRebateMoney());
		gcAccountChangeLog.setCurrentRebateMoney(gcGroupAccount.getAccountRebateMoney());

		gcAccountChangeLogMapper.insertSelective(gcAccountChangeLog);

	}

	/**
	 * 自动转换账户
	 * 
	 * @return
	 */
	public MWebResult convertAccount(String sAccountCode) {

		MWebResult mWebResult = new MWebResult();
		
		if(mWebResult.upFlagTrue()){
			// 定义最早时间
			String sTimer = DateHelper
					.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
			TxReckonOrderService txReckonOrderService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			// 条件为所有正向清分的可转入可提现账户中的并且小于最早可转入时间的
			/**gcReckonLogExample.createCriteria()
					.andReckonChangeTypeEqualTo("4497465200030001")
					.andFlagWithdrawEqualTo(1)
					.andReckonMoneyGreaterThan(BigDecimal.ZERO)
					.andOrderReckonTimeNotEqualTo("")
					.andAccountCodeEqualTo(sAccountCode)
					.andOrderReckonTimeLessThan(sTimer);**/
			
			gcReckonLogExample.createCriteria()
			.andReckonChangeTypeEqualTo("4497465200030001")
			.andFlagWithdrawEqualTo(1)
			.andReckonMoneyNotEqualTo(BigDecimal.ZERO)
			.andOrderReckonTimeNotEqualTo("")
			.andAccountCodeEqualTo(sAccountCode)
			.andOrderReckonTimeLessThan(sTimer);

			List<GcReckonLog> listToLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listToLogs != null) {
				for (GcReckonLog item : listToLogs) {
					GcReckonOrderInfo gcReckonOrderInfo=txReckonOrderService.upGcReckonOrderInfo(item.getOrderCode());
					
					//测试时关注下锁的情况
					// 锁定对应的信息
					String sLockKey = "";
					
						GcTraderInfo gcTraderInfo=null;
						//通过manageCode获取商户编号
						MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",gcReckonOrderInfo.getManageCode());
						//获取商户信息
						if(manageMap!=null&&manageMap.get("trade_code")!=null){
							sLockKey = WebHelper.addLock(20,item.getLogCode(),item.getAccountCode(),manageMap.get("trade_code"));
							if(StringUtils.isNotEmpty(sLockKey)){
							gcTraderInfo=getTraderInfo(manageMap.get("trade_code"));
						
						
						
						//商户的保证金余额要大于等于此reckonlog的金额
						if(gcTraderInfo!=null&&gcTraderInfo.getGurranteeBalance().compareTo(item.getReckonMoney())!=-1){
							//校验是否转过
							if(DbUp.upTable("gc_withdraw_uq").count("uq_code","4497465200040001_"+item.getLogCode())>0){
								WebHelper.unLock(sLockKey);
								continue;
							}
							DbUp.upTable("gc_withdraw_uq").insert("uq_code","4497465200040001_"+item.getLogCode());
							//扣掉对应的保证金金额
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(item.getReckonMoney().negate());
							gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
							gcTraderFoundsChangeLog.setRelationCode(item.getLogCode());
							updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
							gcTraderDepositLog.setOrderCode(item.getOrderCode());
							gcTraderDepositLog.setAccountCode(item.getAccountCode());
							gcTraderDepositLog.setOrderAccountCode(item.getOrderAccountCode());
							gcTraderDepositLog.setRelationLevel(item.getRelationLevel());
							gcTraderDepositLog.setSkuCode(item.getSkuCode());
						    gcTraderDepositLog.setDeposit(item.getReckonMoney().negate());
						    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
						    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
						    gcTraderDepositLog.setRelationCode(item.getLogCode());
						    addTraderDepositOrderLog(gcTraderDepositLog);
						    
						    //处理订单
						    // -------------------- 更新记录上的可提现标记和提现时间
							GcReckonLog gcUpdatereReckonLog = new GcReckonLog();
							gcUpdatereReckonLog.setFlagWithdraw(0);
							gcUpdatereReckonLog.setWithdrawTime(FormatHelper
									.upDateTime());

							GcReckonLogExample gcUpdateReckonLogExample = new GcReckonLogExample();
							gcUpdateReckonLogExample.createCriteria()
									.andLogCodeEqualTo(item.getLogCode());
							gcReckonLogMapper.updateByExampleSelective(
									gcUpdatereReckonLog, gcUpdateReckonLogExample);

							// -------------------- 插入反向记录
							GcReckonLog gcInsertrReckonLog = new GcReckonLog();

							gcInsertrReckonLog
									.setAccountCode(item.getAccountCode());
							gcInsertrReckonLog.setChangeCodes(item.getLogCode());
							gcInsertrReckonLog.setFlagWithdraw(0);
							gcInsertrReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcInsertrReckonLog.setOrderCode(item.getOrderCode());
							gcInsertrReckonLog
									.setReckonChangeType("4497465200030004");
							gcInsertrReckonLog.setReckonMoney(item.getReckonMoney()
									.negate());
							gcInsertrReckonLog.setRelationLevel(item
									.getRelationLevel());
							gcInsertrReckonLog
									.setScaleReckon(item.getScaleReckon());
							gcInsertrReckonLog.setWithdrawTime(FormatHelper
									.upDateTime());
							gcInsertrReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcInsertrReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());

							// -------------------- 插入提现日志
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(item
									.getLogCode()));
							gcWithdrawLog.setWithdrawChangeType("4497465200040001");
							gcWithdrawLog.setWithdrawMoney(item.getReckonMoney());

							List<GcReckonLog> listInsertLogs = new ArrayList<GcReckonLog>();
							listInsertLogs.add(gcInsertrReckonLog);
							List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
							listWithdrawLogs.add(gcWithdrawLog);
							
							List<GcRebateLog> listRebateLogs=new ArrayList<GcRebateLog>();
							//******将预返利记录转入可提现账户
							GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
							gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andOrderCodeEqualTo(item.getOrderCode())
							    .andRebateChangeTypeEqualTo("4497465200140001").andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
							List<GcRebateLog> rebateList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
							if(rebateList!=null&&rebateList.size()>0){
								BigDecimal newMoney=BigDecimal.ZERO;
								for(GcRebateLog gcRebateLog:rebateList){
									newMoney=newMoney.add(gcRebateLog.getRebateMoney());
									GcRebateLog insertRebateLog=new GcRebateLog();
									insertRebateLog.setAccountCode(gcRebateLog.getAccountCode());
									insertRebateLog.setFlagWithdraw(0);
									insertRebateLog.setOrderAccountCode(gcRebateLog.getOrderAccountCode());
									insertRebateLog.setOrderCode(gcRebateLog.getOrderCode());
									insertRebateLog.setRebateChangeType("4497465200140004");//转入提现账户
									insertRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(gcRebateLog.getRebateMoney()));
									insertRebateLog.setRelationLevel(gcRebateLog.getRelationLevel());
									insertRebateLog.setScaleReckon(gcRebateLog.getScaleReckon());
									insertRebateLog.setChangeCodes(gcRebateLog.getLogCode());
									insertRebateLog.setOrderRebateTime(gcRebateLog.getOrderRebateTime());
									insertRebateLog.setRebateType(gcRebateLog.getRebateType());
									insertRebateLog.setFlagStatus(1);
									insertRebateLog.setWithdrawTime(FormatHelper.upDateTime());
									listRebateLogs.add(insertRebateLog);
								}
								GcRebateLog updateRebateLog=new GcRebateLog();
								updateRebateLog.setFlagWithdraw(0);
								updateRebateLog.setWithdrawTime(FormatHelper.upDateTime());
								gcRebateLogMapper.updateByExampleSelective(updateRebateLog, gcRebateLogExample);
								//push签收消息
								try {   
									AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
									addSinglePushCommentInput.setAccountCode(sAccountCode);
									addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
									addSinglePushCommentInput.setType("44974720000400010001");
									
									addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
									addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
									addSinglePushCommentInput.setTitle("您有返利到账");
									MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
									if(memberMap!=null){
										addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
									}
									
								    String content="您有"+newMoney+"元返利已到账，请查看账户余额哦~";
								    addSinglePushCommentInput.setContent(content);
									if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","267a5afe48c847f1be2d2656b0d716c5","push_type_onoff","449747100002")<1){
									    addSinglePushCommentInput.setSendStatus("4497465000070001");
									}
									else{
										addSinglePushCommentInput.setSendStatus("4497465000070002");
									}
									SinglePushComment.addPushComment(addSinglePushCommentInput);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							updateAccount(listInsertLogs, listWithdrawLogs,listRebateLogs);
						}
							}
					}
					WebHelper.unLock(sLockKey);
				}
		}
		
		}
		
		return mWebResult;

	}
	
	/**
	 * 自动转换账户
	 * 
	 * @return
	 */
	public MWebResult convertAccountForSecond(String sAccountCode) {

		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultMessage("convertAccountForSecond");
		if(mWebResult.upFlagTrue()){
			// 定义最早时间
			String sTimer = DateHelper
					.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
			TxReckonOrderService txReckonOrderService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			// 条件为所有正向清分的可转入可提现账户中的并且小于最早可转入时间的
			/**gcReckonLogExample.createCriteria()
					.andReckonChangeTypeEqualTo("4497465200030001")
					.andFlagWithdrawEqualTo(1)
					.andReckonMoneyGreaterThan(BigDecimal.ZERO)
					.andOrderReckonTimeNotEqualTo("")
					.andAccountCodeEqualTo(sAccountCode)
					.andOrderReckonTimeLessThan(sTimer);**/
			
			gcReckonLogExample.createCriteria()
			.andReckonChangeTypeEqualTo("4497465200030001")
			.andFlagWithdrawEqualTo(1)
			.andReckonMoneyNotEqualTo(BigDecimal.ZERO)
			.andOrderReckonTimeNotEqualTo("")
			.andAccountCodeEqualTo(sAccountCode)
			.andOrderReckonTimeLessThan(sTimer);

			List<GcReckonLog> listToLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listToLogs != null) {
				for (GcReckonLog item : listToLogs) {
					GcReckonOrderInfo gcReckonOrderInfo=txReckonOrderService.upGcReckonOrderInfo(item.getOrderCode());
					
					//测试时关注下锁的情况
					// 锁定对应的信息
					String sLockKey = "";
					
					GcTraderInfo gcTraderInfo=null;
					//通过manageCode获取商户编号
					MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",gcReckonOrderInfo.getManageCode());
						//获取商户信息
					if(manageMap!=null&&manageMap.get("trade_code")!=null){
						sLockKey = WebHelper.addLock(20,item.getLogCode(),item.getAccountCode(),manageMap.get("trade_code"));
						if(StringUtils.isNotEmpty(sLockKey)){
							gcTraderInfo=getTraderInfo(manageMap.get("trade_code"));
							//商户的保证金余额要大于等于此reckonlog的金额
							if(gcTraderInfo!=null&&gcTraderInfo.getGurranteeBalance().compareTo(item.getReckonMoney())!=-1){
								//校验是否转过
								if(DbUp.upTable("gc_withdraw_uq").count("uq_code","4497465200040001_"+item.getLogCode())>0){
									WebHelper.unLock(sLockKey);
									continue;
								}
								DbUp.upTable("gc_withdraw_uq").insert("uq_code","4497465200040001_"+item.getLogCode());
								
								//此处的商户扣款是为了升级时兼容老数据(上线过程中可能出现的状态满足 但不能扣款的数据),这里先查询是否扣过款，Y:无处理 N:直接扣除.如果以后新版本运行正常，应该考虑删掉这部分代码。
								MDataMap mWhereMap = new MDataMap();
								mWhereMap.put("order_code", item.getOrderCode());
								int checkCount = DbUp.upTable("gc_trader_deposit_log").dataCount("order_code=:order_code and LEFT(relation_code,4) ='GCRB'", mWhereMap);
								if(checkCount > 0){
									//这是新数据,已经扣过款,无处理
								}else{
									//处理老数据
									GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
									gcTraderDepositLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
									List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
									if(depositLogList!=null&&depositLogList.size()>0){
										//已经扣过款，无处理
									}else{
										//没有扣过款，则扣掉对应的保证金金额
										GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
										gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
										gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
										gcTraderFoundsChangeLog.setGurranteeChangeAmount(item.getReckonMoney().negate());
										gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
										gcTraderFoundsChangeLog.setRelationCode(item.getLogCode());
										gcTraderFoundsChangeLog.setOrderCode(item.getOrderCode());
										updateTraderDeposit(gcTraderFoundsChangeLog);
										
										//添加保证金订单日志
										GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
										gcTraderDepositLog.setOrderCode(item.getOrderCode());
										gcTraderDepositLog.setAccountCode(item.getAccountCode());
										gcTraderDepositLog.setOrderAccountCode(item.getOrderAccountCode());
										gcTraderDepositLog.setRelationLevel(item.getRelationLevel());
										gcTraderDepositLog.setSkuCode(item.getSkuCode());
									    gcTraderDepositLog.setDeposit(item.getReckonMoney().negate());
									    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
									    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
									    gcTraderDepositLog.setRelationCode(item.getLogCode());
									    addTraderDepositOrderLog(gcTraderDepositLog);
									}
								}

								
							    //处理订单
							    // -------------------- 更新记录上的可提现标记和提现时间
								GcReckonLog gcUpdatereReckonLog = new GcReckonLog();
								gcUpdatereReckonLog.setFlagWithdraw(0);
								gcUpdatereReckonLog.setWithdrawTime(FormatHelper
										.upDateTime());
	
								GcReckonLogExample gcUpdateReckonLogExample = new GcReckonLogExample();
								gcUpdateReckonLogExample.createCriteria()
										.andLogCodeEqualTo(item.getLogCode());
								gcReckonLogMapper.updateByExampleSelective(
										gcUpdatereReckonLog, gcUpdateReckonLogExample);
	
								// -------------------- 插入反向记录
								GcReckonLog gcInsertrReckonLog = new GcReckonLog();
	
								gcInsertrReckonLog
										.setAccountCode(item.getAccountCode());
								gcInsertrReckonLog.setChangeCodes(item.getLogCode());
								gcInsertrReckonLog.setFlagWithdraw(0);
								gcInsertrReckonLog.setOrderAccountCode(item
										.getOrderAccountCode());
								gcInsertrReckonLog.setOrderCode(item.getOrderCode());
								gcInsertrReckonLog
										.setReckonChangeType("4497465200030004");
								gcInsertrReckonLog.setReckonMoney(item.getReckonMoney()
										.negate());
								gcInsertrReckonLog.setRelationLevel(item
										.getRelationLevel());
								gcInsertrReckonLog
										.setScaleReckon(item.getScaleReckon());
								gcInsertrReckonLog.setWithdrawTime(FormatHelper
										.upDateTime());
								gcInsertrReckonLog.setOrderReckonTime(item
										.getOrderReckonTime());
								gcInsertrReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
								gcInsertrReckonLog.setDetailCode(item.getDetailCode());
	
								// -------------------- 插入提现日志
								GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
								gcWithdrawLog.setAccountCode(item.getAccountCode());
								gcWithdrawLog.setChangeCodes(FormatHelper.join(item
										.getLogCode()));
								gcWithdrawLog.setWithdrawChangeType("4497465200040001");
								gcWithdrawLog.setWithdrawMoney(item.getReckonMoney());
	
								List<GcReckonLog> listInsertLogs = new ArrayList<GcReckonLog>();
								listInsertLogs.add(gcInsertrReckonLog);
								List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
								listWithdrawLogs.add(gcWithdrawLog);
								
								List<GcRebateLog> listRebateLogs=new ArrayList<GcRebateLog>();
								//******将预返利记录转入可提现账户
								GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
								gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andOrderCodeEqualTo(item.getOrderCode())
								    .andRebateChangeTypeEqualTo("4497465200140001").andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
								List<GcRebateLog> rebateList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
								if(rebateList!=null&&rebateList.size()>0){
									BigDecimal newMoney=BigDecimal.ZERO;
									for(GcRebateLog gcRebateLog:rebateList){
										newMoney=newMoney.add(gcRebateLog.getRebateMoney());
										GcRebateLog insertRebateLog=new GcRebateLog();
										insertRebateLog.setAccountCode(gcRebateLog.getAccountCode());
										insertRebateLog.setFlagWithdraw(0);
										insertRebateLog.setOrderAccountCode(gcRebateLog.getOrderAccountCode());
										insertRebateLog.setOrderCode(gcRebateLog.getOrderCode());
										insertRebateLog.setRebateChangeType("4497465200140004");//转入提现账户
										insertRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(gcRebateLog.getRebateMoney()));
										insertRebateLog.setRelationLevel(gcRebateLog.getRelationLevel());
										insertRebateLog.setScaleReckon(gcRebateLog.getScaleReckon());
										insertRebateLog.setChangeCodes(gcRebateLog.getLogCode());
										insertRebateLog.setOrderRebateTime(gcRebateLog.getOrderRebateTime());
										insertRebateLog.setRebateType(gcRebateLog.getRebateType());
										insertRebateLog.setFlagStatus(1);
										insertRebateLog.setWithdrawTime(FormatHelper.upDateTime());
										insertRebateLog.setSkuCode(gcRebateLog.getSkuCode());
										insertRebateLog.setDetailCode(gcRebateLog.getDetailCode());
										listRebateLogs.add(insertRebateLog);
									}
									GcRebateLog updateRebateLog=new GcRebateLog();
									updateRebateLog.setFlagWithdraw(0);
									updateRebateLog.setWithdrawTime(FormatHelper.upDateTime());
									gcRebateLogMapper.updateByExampleSelective(updateRebateLog, gcRebateLogExample);
									//push签收消息
									try {   
										AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
										addSinglePushCommentInput.setAccountCode(sAccountCode);
										addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
										addSinglePushCommentInput.setType("44974720000400010001");
										
										addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
										addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
										addSinglePushCommentInput.setTitle("您有返利到账");
										MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
										if(memberMap!=null){
											addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
										}
										
									    String content="您有"+newMoney+"元返利已到账，请查看账户余额哦~";
									    addSinglePushCommentInput.setContent(content);
										if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","267a5afe48c847f1be2d2656b0d716c5","push_type_onoff","449747100002")<1){
										    addSinglePushCommentInput.setSendStatus("4497465000070001");
										}
										else{
											addSinglePushCommentInput.setSendStatus("4497465000070002");
										}
										SinglePushComment.addPushComment(addSinglePushCommentInput);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
								updateAccount(listInsertLogs, listWithdrawLogs,listRebateLogs);
							}
						}
					}
					WebHelper.unLock(sLockKey);
				}
			}
		}
		
		return mWebResult;

	}

	/**
	 * 自动转换账户  第三版 20151022 修改 去掉商户的保证金余额要大于等于此reckonlog的金额的条件限制
	 * 
	 * @return
	 */
	public MWebResult doConvertAccountForThird(String sAccountCode) {

		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultMessage("convertAccountForSecond");
		if(mWebResult.upFlagTrue()){
			// 定义最早时间
			String sTimer = DateHelper
					.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
			TxReckonOrderService txReckonOrderService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			// 条件为所有正向清分的可转入可提现账户中的并且小于最早可转入时间的
			/**gcReckonLogExample.createCriteria()
					.andReckonChangeTypeEqualTo("4497465200030001")
					.andFlagWithdrawEqualTo(1)
					.andReckonMoneyGreaterThan(BigDecimal.ZERO)
					.andOrderReckonTimeNotEqualTo("")
					.andAccountCodeEqualTo(sAccountCode)
					.andOrderReckonTimeLessThan(sTimer);**/
			
			gcReckonLogExample.createCriteria()
			.andReckonChangeTypeEqualTo("4497465200030001")
			.andFlagWithdrawEqualTo(1)
			.andReckonMoneyNotEqualTo(BigDecimal.ZERO)
			.andOrderReckonTimeNotEqualTo("")
			.andAccountCodeEqualTo(sAccountCode)
			.andOrderReckonTimeLessThan(sTimer);

			List<GcReckonLog> listToLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listToLogs != null) {
				for (GcReckonLog item : listToLogs) {
					//校验是否存在逆向清分流程.存在：不转提现账户
					int bFlag = DbUp.upTable("gc_reckon_order_step").count("order_code",
							item.getOrderCode(), "flag_success", "1",
							"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
					//检验是否应经重置与返利.没重置：不转提现账户
					String rWhere = " order_code=:order_code and exec_type like '4497465200050005%' and flag_success=:flag_success ";
					MDataMap rWhereMap=new MDataMap();
					rWhereMap.put("order_code", item.getOrderCode());
					rWhereMap.put("flag_success", "1");
					int rFlag = DbUp.upTable("gc_reckon_order_step").dataCount(rWhere, rWhereMap);
					
					if (bFlag<=0 && rFlag>0) {
						GcReckonOrderInfo gcReckonOrderInfo=txReckonOrderService.upGcReckonOrderInfo(item.getOrderCode());
						
						//测试时关注下锁的情况
						// 锁定对应的信息
						String sLockKey = "";
						
						GcTraderInfo gcTraderInfo=null;
						//通过manageCode获取商户编号
						MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",gcReckonOrderInfo.getManageCode());
							//获取商户信息
						if(manageMap!=null&&manageMap.get("trade_code")!=null){
							sLockKey = WebHelper.addLock(20,item.getLogCode(),item.getAccountCode(),manageMap.get("trade_code"));
							if(StringUtils.isNotEmpty(sLockKey)){
								gcTraderInfo=getTraderInfo(manageMap.get("trade_code"));
								
								if(gcTraderInfo!=null){
									//校验是否转过
									if(DbUp.upTable("gc_withdraw_uq").count("uq_code","4497465200040001_"+item.getLogCode())>0){
										WebHelper.unLock(sLockKey);
										continue;
									}
									DbUp.upTable("gc_withdraw_uq").insert("uq_code","4497465200040001_"+item.getLogCode());
									
									//此处的商户扣款是为了升级时兼容老数据(上线过程中可能出现的状态满足 但不能扣款的数据),这里先查询是否扣过款，Y:无处理 N:直接扣除.如果以后新版本运行正常，应该考虑删掉这部分代码。
									MDataMap mWhereMap = new MDataMap();
									mWhereMap.put("order_code", item.getOrderCode());
									int checkCount = DbUp.upTable("gc_trader_deposit_log").dataCount("order_code=:order_code and LEFT(relation_code,4) ='GCRB'", mWhereMap);
									if(checkCount > 0){
										//这是新数据,已经扣过款,无处理
									}else{
										//处理老数据
										GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
										gcTraderDepositLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
										List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
										if(depositLogList!=null&&depositLogList.size()>0){
											//已经扣过款，无处理
										}else{
											//没有扣过款，则扣掉对应的保证金金额
											GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
											gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
											gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
											gcTraderFoundsChangeLog.setGurranteeChangeAmount(item.getReckonMoney().negate());
											gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
											gcTraderFoundsChangeLog.setRelationCode(item.getLogCode());
											gcTraderFoundsChangeLog.setOrderCode(item.getOrderCode());
											updateTraderDeposit(gcTraderFoundsChangeLog);
											
											//添加保证金订单日志
											GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
											gcTraderDepositLog.setOrderCode(item.getOrderCode());
											gcTraderDepositLog.setAccountCode(item.getAccountCode());
											gcTraderDepositLog.setOrderAccountCode(item.getOrderAccountCode());
											gcTraderDepositLog.setRelationLevel(item.getRelationLevel());
											gcTraderDepositLog.setSkuCode(item.getSkuCode());
										    gcTraderDepositLog.setDeposit(item.getReckonMoney().negate());
										    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
										    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
										    gcTraderDepositLog.setRelationCode(item.getLogCode());
										    addTraderDepositOrderLog(gcTraderDepositLog);
										}
									}
	
									
								    //处理订单
								    // -------------------- 更新记录上的可提现标记和提现时间
									GcReckonLog gcUpdatereReckonLog = new GcReckonLog();
									gcUpdatereReckonLog.setFlagWithdraw(0);
									gcUpdatereReckonLog.setWithdrawTime(FormatHelper
											.upDateTime());
		
									GcReckonLogExample gcUpdateReckonLogExample = new GcReckonLogExample();
									gcUpdateReckonLogExample.createCriteria()
											.andLogCodeEqualTo(item.getLogCode());
									gcReckonLogMapper.updateByExampleSelective(
											gcUpdatereReckonLog, gcUpdateReckonLogExample);
		
									// -------------------- 插入反向记录
									GcReckonLog gcInsertrReckonLog = new GcReckonLog();
		
									gcInsertrReckonLog
											.setAccountCode(item.getAccountCode());
									gcInsertrReckonLog.setChangeCodes(item.getLogCode());
									gcInsertrReckonLog.setFlagWithdraw(0);
									gcInsertrReckonLog.setOrderAccountCode(item
											.getOrderAccountCode());
									gcInsertrReckonLog.setOrderCode(item.getOrderCode());
									gcInsertrReckonLog
											.setReckonChangeType("4497465200030004");
									gcInsertrReckonLog.setReckonMoney(item.getReckonMoney()
											.negate());
									gcInsertrReckonLog.setRelationLevel(item
											.getRelationLevel());
									gcInsertrReckonLog
											.setScaleReckon(item.getScaleReckon());
									gcInsertrReckonLog.setWithdrawTime(FormatHelper
											.upDateTime());
									gcInsertrReckonLog.setOrderReckonTime(item
											.getOrderReckonTime());
									gcInsertrReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
									gcInsertrReckonLog.setDetailCode(item.getDetailCode());
		
									// -------------------- 插入提现日志
									GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
									gcWithdrawLog.setAccountCode(item.getAccountCode());
									gcWithdrawLog.setChangeCodes(FormatHelper.join(item
											.getLogCode()));
									gcWithdrawLog.setWithdrawChangeType("4497465200040001");
									gcWithdrawLog.setWithdrawMoney(item.getReckonMoney());
		
									List<GcReckonLog> listInsertLogs = new ArrayList<GcReckonLog>();
									listInsertLogs.add(gcInsertrReckonLog);
									List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
									listWithdrawLogs.add(gcWithdrawLog);
									
									List<GcRebateLog> listRebateLogs=new ArrayList<GcRebateLog>();
									//******将预返利记录转入可提现账户
									GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
									gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andOrderCodeEqualTo(item.getOrderCode())
									    .andRebateChangeTypeEqualTo("4497465200140001").andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
									List<GcRebateLog> rebateList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
									if(rebateList!=null&&rebateList.size()>0){
										BigDecimal newMoney=BigDecimal.ZERO;
										for(GcRebateLog gcRebateLog:rebateList){
											newMoney=newMoney.add(gcRebateLog.getRebateMoney());
											GcRebateLog insertRebateLog=new GcRebateLog();
											insertRebateLog.setAccountCode(gcRebateLog.getAccountCode());
											insertRebateLog.setFlagWithdraw(0);
											insertRebateLog.setOrderAccountCode(gcRebateLog.getOrderAccountCode());
											insertRebateLog.setOrderCode(gcRebateLog.getOrderCode());
											insertRebateLog.setRebateChangeType("4497465200140004");//转入提现账户
											insertRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(gcRebateLog.getRebateMoney()));
											insertRebateLog.setRelationLevel(gcRebateLog.getRelationLevel());
											insertRebateLog.setScaleReckon(gcRebateLog.getScaleReckon());
											insertRebateLog.setChangeCodes(gcRebateLog.getLogCode());
											insertRebateLog.setOrderRebateTime(gcRebateLog.getOrderRebateTime());
											insertRebateLog.setRebateType(gcRebateLog.getRebateType());
											insertRebateLog.setFlagStatus(1);
											insertRebateLog.setWithdrawTime(FormatHelper.upDateTime());
											insertRebateLog.setSkuCode(gcRebateLog.getSkuCode());
											insertRebateLog.setDetailCode(gcRebateLog.getDetailCode());
											listRebateLogs.add(insertRebateLog);
										}
										GcRebateLog updateRebateLog=new GcRebateLog();
										updateRebateLog.setFlagWithdraw(0);
										updateRebateLog.setWithdrawTime(FormatHelper.upDateTime());
										gcRebateLogMapper.updateByExampleSelective(updateRebateLog, gcRebateLogExample);
										//push签收消息
										try {   
											AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
											addSinglePushCommentInput.setAccountCode(sAccountCode);
											addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
											addSinglePushCommentInput.setType("44974720000400010001");
											
											addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
											addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
											addSinglePushCommentInput.setTitle("您有返利到账");
											MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
											if(memberMap!=null){
												addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
											}
											
										    String content="您有"+newMoney+"元返利已到账，请查看账户余额哦~";
										    addSinglePushCommentInput.setContent(content);
											if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","267a5afe48c847f1be2d2656b0d716c5","push_type_onoff","449747100002")<1){
											    addSinglePushCommentInput.setSendStatus("4497465000070001");
											}
											else{
												addSinglePushCommentInput.setSendStatus("4497465000070002");
											}
											SinglePushComment.addPushComment(addSinglePushCommentInput);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									
									updateAccount(listInsertLogs, listWithdrawLogs,listRebateLogs);
								}
							}
						}
						WebHelper.unLock(sLockKey);
					}
				}
			}
		}
		
		return mWebResult;

	}
	
	/**
	 * 自动转换账户  第四版 20151210 修改 单个用户处理时添加事务控制
	 * @param orderCode 
	 * @param accountCode 
	 * @return
	 */
	public MWebResult convertAccountForFourth(String orderCode, String sAccountCode) {

		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultMessage("doConvertAccountForFourth");
		if(mWebResult.upFlagTrue()){
			// 定义最早时间
			String sTimer = DateHelper.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
			GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
			TxReckonOrderService txReckonOrderService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			// 条件为所有正向清分的可转入可提现账户中的并且小于最早可转入时间的
			gcReckonLogExample.createCriteria()
			.andReckonChangeTypeEqualTo("4497465200030001")
			.andFlagWithdrawEqualTo(1)
			.andReckonMoneyNotEqualTo(BigDecimal.ZERO)
			.andOrderReckonTimeNotEqualTo("")
			.andAccountCodeEqualTo(sAccountCode)
			.andOrderCodeEqualTo(orderCode)
			.andOrderReckonTimeLessThan(sTimer);

			List<GcReckonLog> listToLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			
			if (listToLogs != null) {
				for (GcReckonLog item : listToLogs) {
					String acountLockKey = "";
					acountLockKey= WebHelper.addLock(20,item.getAccountCode());
					if(StringUtils.isNotEmpty(acountLockKey)){
						doConvertToAccount(gcReckonLogMapper,gcRebateLogMapper,gcTraderDepositLogMapper,txReckonOrderService,item,sAccountCode);
					}
					WebHelper.unLock(acountLockKey);
				}
			}
		}
		
		return mWebResult;
	}

	/**
	 * 转提现给账户
	 * @param gcReckonLogMapper
	 * @param gcRebateLogMapper
	 * @param gcTraderDepositLogMapper
	 * @param txReckonOrderService
	 * @param item
	 * @param sAccountCode 
	 * @return
	 */
	private MWebResult doConvertToAccount(GcReckonLogMapper gcReckonLogMapper,
			GcRebateLogMapper gcRebateLogMapper,
			GcTraderDepositLogMapper gcTraderDepositLogMapper,
			TxReckonOrderService txReckonOrderService, GcReckonLog item, String sAccountCode) {
		
		MWebResult mWebResult = new MWebResult();

		//校验是否存在逆向清分流程.存在：不转提现账户
		int bFlag = DbUp.upTable("gc_reckon_order_step").count("order_code",
				item.getOrderCode(), "flag_success", "1",
				"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
		//检验是否有执行失败的流程.有：不转提现账户
		String rWhere = " order_code=:order_code and flag_success=:flag_success ";
		MDataMap rWhereMap=new MDataMap();
		rWhereMap.put("order_code", item.getOrderCode());
		rWhereMap.put("flag_success", "0");
		int rFlag = DbUp.upTable("gc_reckon_order_step").dataCount(rWhere, rWhereMap);
		
		if (bFlag<=0 && rFlag==0) {
			GcReckonOrderInfo gcReckonOrderInfo=txReckonOrderService.upGcReckonOrderInfo(item.getOrderCode());
			
			//测试时关注下锁的情况
			// 锁定对应的信息
			String sLockKey = "";
			
			GcTraderInfo gcTraderInfo=null;
			//通过manageCode获取商户编号
			MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",gcReckonOrderInfo.getManageCode());
				//获取商户信息
			if(manageMap!=null&&manageMap.get("trade_code")!=null){
				sLockKey = WebHelper.addLock(20,item.getLogCode(),manageMap.get("trade_code"));
				if(StringUtils.isNotEmpty(sLockKey)){
					gcTraderInfo=getTraderInfo(manageMap.get("trade_code"));
					
					if(gcTraderInfo!=null){
						//校验是否转过
						if(DbUp.upTable("gc_withdraw_uq").count("uq_code","4497465200040001_"+item.getLogCode())>0){
							WebHelper.unLock(sLockKey);
						}else{
							DbUp.upTable("gc_withdraw_uq").insert("uq_code","4497465200040001_"+item.getLogCode());
							
							//此处的商户扣款是为了升级时兼容老数据(上线过程中可能出现的状态满足 但不能扣款的数据),这里先查询是否扣过款，Y:无处理 N:直接扣除.如果以后新版本运行正常，应该考虑删掉这部分代码。
							MDataMap mWhereMap = new MDataMap();
							mWhereMap.put("order_code", item.getOrderCode());
							int checkCount = DbUp.upTable("gc_trader_deposit_log").dataCount("order_code=:order_code and LEFT(relation_code,4) ='GCRB'", mWhereMap);
							if(checkCount > 0){
								//这是新数据,已经扣过款,无处理
							}else{
								//处理老数据
								GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
								gcTraderDepositLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
								List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
								if(depositLogList!=null&&depositLogList.size()>0){
									//已经扣过款，无处理
								}else{
									//没有扣过款，则扣掉对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
									gcTraderFoundsChangeLog.setGurranteeChangeAmount(item.getReckonMoney().negate());
									gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
									gcTraderFoundsChangeLog.setRelationCode(item.getLogCode());
									gcTraderFoundsChangeLog.setOrderCode(item.getOrderCode());
									updateTraderDeposit(gcTraderFoundsChangeLog);
									
									//添加保证金订单日志
									GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
									gcTraderDepositLog.setOrderCode(item.getOrderCode());
									gcTraderDepositLog.setAccountCode(item.getAccountCode());
									gcTraderDepositLog.setOrderAccountCode(item.getOrderAccountCode());
									gcTraderDepositLog.setRelationLevel(item.getRelationLevel());
									gcTraderDepositLog.setSkuCode(item.getSkuCode());
								    gcTraderDepositLog.setDeposit(item.getReckonMoney().negate());
								    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
								    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
								    gcTraderDepositLog.setRelationCode(item.getLogCode());
								    addTraderDepositOrderLog(gcTraderDepositLog);
								}
							}
						    //处理订单
						    // -------------------- 更新记录上的可提现标记和提现时间
							GcReckonLog gcUpdatereReckonLog = new GcReckonLog();
							gcUpdatereReckonLog.setFlagWithdraw(0);
							gcUpdatereReckonLog.setWithdrawTime(FormatHelper
									.upDateTime());

							GcReckonLogExample gcUpdateReckonLogExample = new GcReckonLogExample();
							gcUpdateReckonLogExample.createCriteria()
									.andLogCodeEqualTo(item.getLogCode());
							gcReckonLogMapper.updateByExampleSelective(
									gcUpdatereReckonLog, gcUpdateReckonLogExample);

							// -------------------- 插入反向记录
							GcReckonLog gcInsertrReckonLog = new GcReckonLog();

							gcInsertrReckonLog
									.setAccountCode(item.getAccountCode());
							gcInsertrReckonLog.setChangeCodes(item.getLogCode());
							gcInsertrReckonLog.setFlagWithdraw(0);
							gcInsertrReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcInsertrReckonLog.setOrderCode(item.getOrderCode());
							gcInsertrReckonLog
									.setReckonChangeType("4497465200030004");
							gcInsertrReckonLog.setReckonMoney(item.getReckonMoney()
									.negate());
							gcInsertrReckonLog.setRelationLevel(item
									.getRelationLevel());
							gcInsertrReckonLog
									.setScaleReckon(item.getScaleReckon());
							gcInsertrReckonLog.setWithdrawTime(FormatHelper
									.upDateTime());
							gcInsertrReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcInsertrReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcInsertrReckonLog.setDetailCode(item.getDetailCode());

							// -------------------- 插入提现日志
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(item
									.getLogCode()));
							gcWithdrawLog.setWithdrawChangeType("4497465200040001");
							gcWithdrawLog.setWithdrawMoney(item.getReckonMoney());

							List<GcReckonLog> listInsertLogs = new ArrayList<GcReckonLog>();
							listInsertLogs.add(gcInsertrReckonLog);
							List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
							listWithdrawLogs.add(gcWithdrawLog);
							
							List<GcRebateLog> listRebateLogs=new ArrayList<GcRebateLog>();
							//******将预返利记录转入可提现账户
							GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
							gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andOrderCodeEqualTo(item.getOrderCode())
							    .andRebateChangeTypeEqualTo("4497465200140001").andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
							List<GcRebateLog> rebateList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
							if(rebateList!=null&&rebateList.size()>0){
								BigDecimal newMoney=BigDecimal.ZERO;
								for(GcRebateLog gcRebateLog:rebateList){
									newMoney=newMoney.add(gcRebateLog.getRebateMoney());
									GcRebateLog insertRebateLog=new GcRebateLog();
									insertRebateLog.setAccountCode(gcRebateLog.getAccountCode());
									insertRebateLog.setFlagWithdraw(0);
									insertRebateLog.setOrderAccountCode(gcRebateLog.getOrderAccountCode());
									insertRebateLog.setOrderCode(gcRebateLog.getOrderCode());
									insertRebateLog.setRebateChangeType("4497465200140004");//转入提现账户
									insertRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(gcRebateLog.getRebateMoney()));
									insertRebateLog.setRelationLevel(gcRebateLog.getRelationLevel());
									insertRebateLog.setScaleReckon(gcRebateLog.getScaleReckon());
									insertRebateLog.setChangeCodes(gcRebateLog.getLogCode());
									insertRebateLog.setOrderRebateTime(gcRebateLog.getOrderRebateTime());
									insertRebateLog.setRebateType(gcRebateLog.getRebateType());
									insertRebateLog.setFlagStatus(1);
									insertRebateLog.setWithdrawTime(FormatHelper.upDateTime());
									insertRebateLog.setSkuCode(gcRebateLog.getSkuCode());
									insertRebateLog.setDetailCode(gcRebateLog.getDetailCode());
									listRebateLogs.add(insertRebateLog);
								}
								GcRebateLog updateRebateLog=new GcRebateLog();
								updateRebateLog.setFlagWithdraw(0);
								updateRebateLog.setWithdrawTime(FormatHelper.upDateTime());
								gcRebateLogMapper.updateByExampleSelective(updateRebateLog, gcRebateLogExample);
								//push签收消息
								try {   
									AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
									addSinglePushCommentInput.setAccountCode(sAccountCode);
									addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
									addSinglePushCommentInput.setType("44974720000400010001");
									
									addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
									addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
									addSinglePushCommentInput.setTitle("您有返利到账");
									MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
									if(memberMap!=null){
										addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
									}
									
								    String content="您有"+newMoney+"元返利已到账，请查看账户余额哦~";
								    addSinglePushCommentInput.setContent(content);
									if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","267a5afe48c847f1be2d2656b0d716c5","push_type_onoff","449747100002")<1){
									    addSinglePushCommentInput.setSendStatus("4497465000070001");
									}
									else{
										addSinglePushCommentInput.setSendStatus("4497465000070002");
									}
									SinglePushComment.addPushComment(addSinglePushCommentInput);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							updateAccount(listInsertLogs, listWithdrawLogs,listRebateLogs);
						}
					}
				}
				WebHelper.unLock(sLockKey);
			}
		}
		
		return mWebResult;
	}

	/**
	 * 定义降级流程
	 * 
	 * @param sAccountCode
	 * @return
	 */
	public MWebResult fallAccountLevel(String sAccountCode) {
		MWebResult mWebResult = new MWebResult();

		String sLastMonthFirstDay = DateHelper.upDate(
				DateHelper.parseDate(DateHelper.upDateTimeAdd("-1M")),
				DateHelper.CONST_PARSE_MONTH_FIRST_DAY);

		MDataMap mDataMap = new MDataMap();
		mDataMap.inAllValues("account_code", sAccountCode, "create_time",
				sLastMonthFirstDay);

		MDataMap mAccountMap = DbUp.upTable("gc_group_account").one(
				"account_code", sAccountCode);

		if (mAccountMap != null) {

			// 默认是降级
			boolean bFlagFall = true;

			// 如果当月和上一个月没有级别变化时 开始判断上上月是否满足级别要求
			if (DbUp.upTable("gc_level_log").dataCount(
					"account_code=:account_code and create_time>:create_time",
					mDataMap) == 0) {

				MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level",
						"", "level_code", mAccountMap.get("account_level"));

				// 判断如果级别不存在 或者级别不可再降 则设置降级为否
				if (mLevelMap == null || mLevelMap.get("flag_fall").equals("0")) {
					bFlagFall = false;
				}

				// 获取上一级别 这里为了防止级别的限制 按照排序来决定取哪一条
				MDataMap mLastLevelMap = DbUp.upTable("gc_group_level")
						.oneWhere("", "-level_sort", "", "flag_enable", "1",
								"next_level", mLevelMap.get("level_code"));

				if (bFlagFall && mLastLevelMap != null) {
					String sMonth = DateHelper.upMonth(DateHelper
							.upDateTimeAdd("-2M"));

					MDataMap mActiveMap = DbUp.upTable("gc_active_month").one(
							"account_code", sAccountCode, "active_month",
							sMonth);

					if (mActiveMap != null) {

						// 判断如果上上月的消费和社友数大于级别要求 则不降级
						if (new BigDecimal(mActiveMap.get("sum_consume"))
								.compareTo(new BigDecimal(mLastLevelMap
										.get("upgrade_consume"))) > -1
								&& new BigDecimal(mActiveMap.get("sum_member"))
										.compareTo(new BigDecimal(mLastLevelMap
												.get("upgrade_members"))) > -1) {
							bFlagFall = false;
						}

					}
				}

				// 判断如果需要降级 且降级的级别存在
				if (bFlagFall && mLastLevelMap != null) {

					// 级别变动日志
					GcLevelLog gcLevelLog = new GcLevelLog();
					gcLevelLog.setAccountCode(sAccountCode);
					gcLevelLog.setCreateTime(FormatHelper.upDateTime());

					gcLevelLog.setLastMemberLevel(mLevelMap.get("level_code"));

					gcLevelLog.setCurrentMemberLevel(mLastLevelMap
							.get("level_code"));
					gcLevelLog.setChangeType("4497465200080002");
					gcLevelLog.setUid(WebHelper.upUuid());
					// 插入级别变动日志表
					GcLevelLogMapper gcLevelLogMapper = BeansHelper
							.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");
					gcLevelLogMapper.insertSelective(gcLevelLog);

					// 账户变动
					GcGroupAccount gcGroupAccount = new GcGroupAccount();
					gcGroupAccount
							.setLevelChangeTime(FormatHelper.upDateTime());
					gcGroupAccount.setAccountLevel(gcLevelLog
							.getCurrentMemberLevel());
					gcGroupAccount.setScaleReckon(new BigDecimal(mLastLevelMap
							.get("scale_reckon")));
					gcGroupAccount
							.setLevelType(mLastLevelMap.get("level_type"));
					gcGroupAccount.setFallCheckTime(FormatHelper.upDateTime());

					// 更新账户的级别变更表
					GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
							.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
					GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
					gcGroupAccountExample.createCriteria()
							.andAccountCodeEqualTo(sAccountCode);
					gcGroupAccountMapper.updateByExampleSelective(
							gcGroupAccount, gcGroupAccountExample);

				}

			}

			if (mWebResult.upFlagTrue()) {

				mAccountMap.put("fall_check_time", FormatHelper.upDateTime());
				// 更新账户上的降级检查时间
				DbUp.upTable("gc_group_account").dataUpdate(mAccountMap,
						"fall_check_time", "account_code");

			}
		}
		return mWebResult;
	}
	
	/**
	 * 更新账户信息 根据日志更新 调用该方法前必须lock账户编号
	 * 
	 * @param listReckonLogs
	 * @param listWithdrawLogs
	 */
	public void updateAccount(List<GcReckonLog> listReckonLogs,
			List<GcWithdrawLog> listWithdrawLogs, List<GcRebateLog> listRebateLogs) {

		// 定义账户的可清分金额
		BigDecimal bAddReckon = BigDecimal.ZERO;

		// 定义账户可提现金额
		BigDecimal bAddWithdram = BigDecimal.ZERO;

		// 定义总计清分账户变动金额
		BigDecimal bTotalReckonChange = BigDecimal.ZERO;

		// 定义总计可提现账户变动金额
		BigDecimal bTotalWithdrawChange = BigDecimal.ZERO;
		
		//定义账户可返利金额
		BigDecimal bAddRebate = BigDecimal.ZERO;
		
		//定义总计返利账户变动金额
		BigDecimal bTotalRebateChange = BigDecimal.ZERO;

		// 定义账户变动日志流水

		String sAccountCode = "";

		List<String> changeCodes = new ArrayList<String>();

		if (listReckonLogs != null && listReckonLogs.size() > 0) {

			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

			// 循环并插入清分流水
			for (GcReckonLog item : listReckonLogs) {

				if (StringUtils.isEmpty(sAccountCode)) {
					sAccountCode = item.getAccountCode();
				}

				item.setUid(WebHelper.upUuid());
				item.setCreateTime(FormatHelper.upDateTime());
				item.setLogCode(WebHelper.upCode("GCRL"));

				bAddReckon = bAddReckon.add(item.getReckonMoney());

				gcReckonLogMapper.insertSelective(item);

				changeCodes.add(item.getLogCode());

				// 限定只有特定类型时才更新总账
				if (StringUtils.indexOf(
						"4497465200030001,4497465200030002,4497465200030003",
						item.getReckonChangeType()) > -1) {
					bTotalReckonChange = bTotalReckonChange.add(item
							.getReckonMoney());
				}

			}
		}

		if (listWithdrawLogs != null && listWithdrawLogs.size() > 0) {

			GcWithdrawLogMapper gcWithdrawLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcWithdrawLogMapper");

			for (GcWithdrawLog item : listWithdrawLogs) {
				if (StringUtils.isEmpty(sAccountCode)) {
					sAccountCode = item.getAccountCode();
				}

				item.setUid(WebHelper.upUuid());
				item.setCreateTime(FormatHelper.upDateTime());
				item.setLogCode(WebHelper.upCode("GCWL"));
				bAddWithdram = bAddWithdram.add(item.getWithdrawMoney());
				gcWithdrawLogMapper.insertSelective(item);

				changeCodes.add(item.getLogCode());

				// 限定只有特定类型时才更新总账
				if (StringUtils.indexOf(
						"4497465200040001,4497465200040003,4497465200040004,4497465200040010,4497465200040011",
						item.getWithdrawChangeType()) > -1) {
					bTotalWithdrawChange = bTotalWithdrawChange.add(item
							.getWithdrawMoney());
				}

			}
		}
		
		if (listRebateLogs != null && listRebateLogs.size() > 0) {

			GcRebateLogMapper gcRebateLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");

			// 循环并插入返利流水
			for (GcRebateLog item : listRebateLogs) {

				if (StringUtils.isEmpty(sAccountCode)) {
					sAccountCode = item.getAccountCode();
				}

				item.setUid(WebHelper.upUuid());
				item.setCreateTime(FormatHelper.upDateTime());
				if(StringUtils.isBlank(item.getLogCode())){
					item.setLogCode(WebHelper.upCode("GCRBL"));
				}

				bAddRebate = bAddRebate.add(item.getRebateMoney());

				gcRebateLogMapper.insertSelective(item);

				changeCodes.add(item.getLogCode());

				// 限定只有特定类型时才更新总账
				if (StringUtils.indexOf(
						"4497465200140001,4497465200140003",
						item.getRebateChangeType()) > -1) {
					bTotalRebateChange = bTotalRebateChange.add(item
							.getRebateMoney());
				}

			}
		}

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
		GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
		gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
				sAccountCode);

		// 定义当前微公社账户信息
		GcGroupAccount gcGroupAccount = gcGroupAccountMapper.selectByExample(
				gcGroupAccountExample).get(0);

		GcGroupAccount gcUpdateAccount = new GcGroupAccount();
		gcUpdateAccount.setAccountReckonMoney(gcGroupAccount
				.getAccountReckonMoney().add(bAddReckon));

		gcUpdateAccount.setTotalReckonMoney(gcGroupAccount
				.getTotalReckonMoney().add(bTotalReckonChange));

		gcUpdateAccount.setAccountWithdrawMoney(gcGroupAccount
				.getAccountWithdrawMoney().add(bAddWithdram));
		gcUpdateAccount.setTotalWithdrawMoney(gcGroupAccount
				.getTotalWithdrawMoney().add(bTotalWithdrawChange));
		
		gcUpdateAccount.setAccountRebateMoney(gcGroupAccount.getAccountRebateMoney().add(bAddRebate));
		
		gcUpdateAccount.setTotalRebateMoney(gcGroupAccount.getTotalRebateMoney().add(bTotalRebateChange));

		// 更新账户信息
		gcGroupAccountMapper.updateByExampleSelective(gcUpdateAccount,
				gcGroupAccountExample);

		// 插入账户变更历史
		GcAccountChangeLogMapper gcAccountChangeLogMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcAccountChangeLogMapper");

		GcAccountChangeLog gcAccountChangeLog = new GcAccountChangeLog();

		gcAccountChangeLog.setUid(WebHelper.upUuid());
		gcAccountChangeLog.setAccountCode(gcGroupAccount.getAccountCode());
		gcAccountChangeLog.setChangeCodes(StringUtils.join(changeCodes,
				WebConst.CONST_SPLIT_COMMA));
		gcAccountChangeLog.setCreateTime(FormatHelper.upDateTime());
		gcAccountChangeLog.setCurrentReckonMoney(gcUpdateAccount
				.getAccountReckonMoney());
		gcAccountChangeLog.setLastReckonMoney(gcGroupAccount
				.getAccountReckonMoney());

		gcAccountChangeLog.setLastWithdrawMoney(gcGroupAccount
				.getAccountWithdrawMoney());
		gcAccountChangeLog.setCurrentWithdrawMoney(gcUpdateAccount
				.getAccountWithdrawMoney());
		
		gcAccountChangeLog.setLastRebateMoney(gcGroupAccount.getAccountRebateMoney());
		gcAccountChangeLog.setCurrentRebateMoney(gcUpdateAccount.getAccountRebateMoney());

		gcAccountChangeLogMapper.insertSelective(gcAccountChangeLog);

		
	}
	
	/**
	 * 获取商户信息
	 * @param traderCode
	 * @return
	 */
	public GcTraderInfo getTraderInfo(String traderCode){
		GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
		GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
		gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(traderCode);
		GcTraderInfo gcTraderInfo=null;
		List<GcTraderInfo> infoList=gcTraderInfoMapper.selectByExample(gcTraderInfoExample);
		if(infoList!=null&&infoList.size()>0){
			gcTraderInfo=infoList.get(0);
		}
		return gcTraderInfo;
	}
	
	/**
	 * 更新商户保证金账户
	 * @param gcTraderFoundsChangeLog
	 */
	public void updateTraderDeposit(GcTraderFoundsChangeLog gcTraderFoundsChangeLog) {
		GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
		GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
		gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(gcTraderFoundsChangeLog.getTraderCode());
		GcTraderInfo gcTraderInfo=gcTraderInfoMapper.selectByExample(gcTraderInfoExample).get(0);
		if(gcTraderInfo!=null){
			//添加保证金变动日志
			GcTraderFoundsChangeLogMapper gcTraderFoundsChangeLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderFoundsChangeLogMapper");
			gcTraderFoundsChangeLog.setUid(WebHelper.upUuid());
			gcTraderFoundsChangeLog.setGurranteeBalanceBefore(gcTraderInfo.getGurranteeBalance());
			gcTraderFoundsChangeLog.setGurranteeBalanceAfter(gcTraderInfo.getGurranteeBalance().add(gcTraderFoundsChangeLog.getGurranteeChangeAmount()));
			gcTraderFoundsChangeLog.setCreateTime(FormatHelper.upDateTime());
			gcTraderFoundsChangeLogMapper.insertSelective(gcTraderFoundsChangeLog);
			
			//更新保证金金额
			GcTraderInfo updateGcTraderInfo=new GcTraderInfo();
			updateGcTraderInfo.setGurranteeBalance(gcTraderInfo.getGurranteeBalance().add(gcTraderFoundsChangeLog.getGurranteeChangeAmount()));
			gcTraderInfoMapper.updateByExampleSelective(updateGcTraderInfo, gcTraderInfoExample);
		}
	}
	
	/**
	 * 增加商户保证金订单对账日志
	 * @param gcTraderDepositLog
	 */
	public void addTraderDepositOrderLog(GcTraderDepositLog gcTraderDepositLog){
		GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
		if(StringUtils.isBlank(gcTraderDepositLog.getLogCode())){
			gcTraderDepositLog.setLogCode(WebHelper.upCode("TDO"));
		}
		if(StringUtils.isBlank(gcTraderDepositLog.getUid())){
			gcTraderDepositLog.setUid(WebHelper.upUuid());
		}
		if(StringUtils.isBlank(gcTraderDepositLog.getCreateTime())){
			gcTraderDepositLog.setCreateTime(FormatHelper.upDateTime());
		}
		gcTraderDepositLogMapper.insertSelective(gcTraderDepositLog);
	}
	
	/**
	 * 更新商户账户余额和添加日志
	 * @param gcTraderAccountChangeLog
	 */
	public void updateTraderBalance(GcTraderAccountChangeLog gcTraderAccountChangeLog){
		GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
		GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
		if(gcTraderAccountChangeLog != null && StringUtils.isNotBlank(gcTraderAccountChangeLog.getTraderCode())){
			gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(gcTraderAccountChangeLog.getTraderCode());
			List<GcTraderInfo> gcTraderInfoList=gcTraderInfoMapper.selectByExample(gcTraderInfoExample);
			if(gcTraderInfoList != null && gcTraderInfoList.size() > 0){
				GcTraderInfo gcTraderInfo = gcTraderInfoList.get(0);
				if(gcTraderInfo != null){
					//添加账户余额变动日志
					GcTraderAccountChangeLogMapper gcTraderAccountChangeLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderAccountChangeLogMapper");
					if(StringUtils.isBlank(gcTraderAccountChangeLog.getUid())){
						gcTraderAccountChangeLog.setUid(WebHelper.upUuid());
					}
					if(StringUtils.isBlank(gcTraderAccountChangeLog.getCreateTime())){
						gcTraderAccountChangeLog.setCreateTime(FormatHelper.upDateTime());
					}
					gcTraderAccountChangeLog.setPreAvailableMoney(gcTraderInfo.getAccountBalance());
					gcTraderAccountChangeLog.setNowAvailableMoney(gcTraderInfo.getAccountBalance().add(gcTraderAccountChangeLog.getChangeMoney()));
					gcTraderAccountChangeLogMapper.insert(gcTraderAccountChangeLog);
					
					//更新账户余额
					GcTraderInfo updateGcTraderInfo=new GcTraderInfo();
					updateGcTraderInfo.setAccountBalance(gcTraderInfo.getAccountBalance().add(gcTraderAccountChangeLog.getChangeMoney()));
					gcTraderInfoMapper.updateByExampleSelective(updateGcTraderInfo, gcTraderInfoExample);
				}
			}
		}
	}
	
	/**
	 * 添加商户账户余额变动日志
	 * @param gcTraderAcccountLog
	 */
	public void createTraderChangeBalangeLog(GcTraderAccountLog gcTraderAcccountLog){
		GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
		GcTraderAccountLogMapper gcTraderAccountLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderAccountLogMapper");
		GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
		if(gcTraderAcccountLog != null && StringUtils.isNotBlank(gcTraderAcccountLog.getTraderCode())){
			gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(gcTraderAcccountLog.getTraderCode());
			List<GcTraderInfo> gcTraderInfoList=gcTraderInfoMapper.selectByExample(gcTraderInfoExample);
			if(gcTraderInfoList != null && gcTraderInfoList.size() > 0){
				if(StringUtils.isBlank(gcTraderAcccountLog.getLogCode())){
					gcTraderAcccountLog.setLogCode(WebHelper.upCode("TDB"));
				}
				if(StringUtils.isBlank(gcTraderAcccountLog.getUid())){
					gcTraderAcccountLog.setUid(WebHelper.upUuid());
				}
				if(StringUtils.isBlank(gcTraderAcccountLog.getCreateTime())){
					gcTraderAcccountLog.setCreateTime(FormatHelper.upDateTime());
				}
				gcTraderAccountLogMapper.insertSelective(gcTraderAcccountLog);
			}
		}
	}
	
	/**
	 * 更新商户账户余额和添加相关日志
	 * @param gcTraderAccountChangeLog
	 * @param gcTraderAcccountLog
	 * @param logCode 可以为null或者""，不传自动生成
	 */
	public void updateTraderBalanceAndAddLog(GcTraderAccountChangeLog gcTraderAccountChangeLog,String logCode){
		GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
		GcTraderAccountLogMapper gcTraderAccountLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderAccountLogMapper");
		GcTraderAccountChangeLogMapper gcTraderAccountChangeLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderAccountChangeLogMapper");
		GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
		if(gcTraderAccountChangeLog != null && StringUtils.isNotBlank(gcTraderAccountChangeLog.getTraderCode())){
			gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(gcTraderAccountChangeLog.getTraderCode());
			List<GcTraderInfo> gcTraderInfoList=gcTraderInfoMapper.selectByExample(gcTraderInfoExample);
			if(gcTraderInfoList != null && gcTraderInfoList.size() > 0){
				GcTraderInfo gcTraderInfo = gcTraderInfoList.get(0);
				GcTraderAccountLog gcTraderAcccountLog = new GcTraderAccountLog();
				if(gcTraderAccountChangeLog !=null){
					//添加商户账户余额变动日志
					if(StringUtils.isBlank(gcTraderAccountChangeLog.getUid())){
						gcTraderAccountChangeLog.setUid(WebHelper.upUuid());
					}
					if(StringUtils.isBlank(gcTraderAccountChangeLog.getCreateTime())){
						gcTraderAccountChangeLog.setCreateTime(FormatHelper.upDateTime());
					}
					gcTraderAccountChangeLog.setPreAvailableMoney(gcTraderInfo.getAccountBalance());
					gcTraderAccountChangeLog.setNowAvailableMoney(gcTraderInfo.getAccountBalance().add(gcTraderAccountChangeLog.getChangeMoney()));
					gcTraderAccountChangeLogMapper.insert(gcTraderAccountChangeLog);
					
					//更新账户余额
					GcTraderInfo updateGcTraderInfo=new GcTraderInfo();
					updateGcTraderInfo.setAccountBalance(gcTraderInfo.getAccountBalance().add(gcTraderAccountChangeLog.getChangeMoney()));
					gcTraderInfoMapper.updateByExampleSelective(updateGcTraderInfo, gcTraderInfoExample);
					
					//添加商户账户余额日志
					if(StringUtils.isBlank(logCode)){
						logCode=WebHelper.upCode("TDB");
					}
					gcTraderAcccountLog.setUid(WebHelper.upUuid());
					gcTraderAcccountLog.setLogCode(logCode);
					gcTraderAcccountLog.setTraderCode(gcTraderAccountChangeLog.getTraderCode());
					gcTraderAcccountLog.setChangeMoney(gcTraderAccountChangeLog.getChangeMoney());
					gcTraderAcccountLog.setChangeType(gcTraderAccountChangeLog.getChangeType());
					gcTraderAcccountLog.setCreateTime(FormatHelper.upDateTime());
					gcTraderAcccountLog.setChangeCodes(gcTraderAccountChangeLog.getChangeCodes());
					gcTraderAcccountLog.setRemark(gcTraderAccountChangeLog.getRemark());
					gcTraderAccountLogMapper.insertSelective(gcTraderAcccountLog);
				}
			}
		}
	}
	

}
