package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcActiveLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcActiveMonthMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupLevelMapper;
import com.cmall.dborm.txmapper.groupcenter.GcLevelLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateOrderMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderReturnDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcActiveLog;
import com.cmall.dborm.txmodel.groupcenter.GcActiveLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcActiveMonth;
import com.cmall.dborm.txmodel.groupcenter.GcActiveMonthExample;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccount;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccountExample;
import com.cmall.dborm.txmodel.groupcenter.GcLevelLog;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLog;
import com.cmall.dborm.txmodel.groupcenter.GcRebateLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcRebateOrder;
import com.cmall.dborm.txmodel.groupcenter.GcRebateOrderExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderReturnDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderReturnDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.GroupLevelInfo;
import com.cmall.groupcenter.model.ReckonOrderInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.ReckonOrderSupport;
import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DataConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 清分订单相关
 * 
 * @author srnpr
 * 
 */
public class TxReckonOrderService extends BaseClass {

	/**
	 * 插入清分订单表
	 * 
	 * @param reckonOrderInfo
	 * @return
	 */
	public MWebResult insertReckonOrder(ReckonOrderInfo reckonOrderInfo) {
		MWebResult mResult = new MWebResult();

		GcReckonOrderInfo orderInfo = reckonOrderInfo.getOrderInfo();

		// 取出账户编号
		if (mResult.upFlagTrue()) {
			String sAccountCode="";
			MDataMap accountMap= DbUp.upTable("mc_member_info")
					.one("member_code", orderInfo.getMemberCode());
			if(accountMap!=null){
				sAccountCode=accountMap.get("account_code");
			}

			if (!StringUtils.isEmpty(sAccountCode)) {

				orderInfo.setAccountCode(sAccountCode);
			} else {
				mResult.inErrorMessage(918505130, orderInfo.getMemberCode());
			}
		}

		// 开始执行插入操作
		if (mResult.upFlagTrue()) {

			GcReckonOrderInfoMapper gcReckonOrderInfoMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderInfoMapper");

			orderInfo.setUid(WebHelper.upUuid());
			orderInfo.setCreateTime(FormatHelper.upDateTime());

			// 如果没有设置是否可清分 则默认可清分
			if (orderInfo.getFlagReckon() == null) {
				orderInfo.setFlagReckon(1);
			}

			gcReckonOrderInfoMapper.insertSelective(orderInfo);

			for (GcReckonOrderDetail detail : reckonOrderInfo.getOrderList()) {
				insertDetail(detail);
			}
		}

		return mResult;
	}

	/**
	 * 插入明细表
	 * 
	 * @param detail
	 */
	public void insertDetail(GcReckonOrderDetail detail) {
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		detail.setUid(WebHelper.upUuid());

		gcReckonOrderDetailMapper.insertSelective(detail);
	}

	/**
	 * 获取清分订单信息
	 * 
	 * @param sOrderCode
	 * @return
	 */
	public ReckonOrderInfo upReckonOrderInfo(String sOrderCode) {
		ReckonOrderInfo reckonOrderInfo = new ReckonOrderInfo();

		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");

		GcReckonOrderDetailExample gcReckonOrderDetailExample = new GcReckonOrderDetailExample();
		gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(
				sOrderCode);

		reckonOrderInfo.setOrderInfo(upGcReckonOrderInfo(sOrderCode));
		reckonOrderInfo.setOrderList(gcReckonOrderDetailMapper
				.selectByExample(gcReckonOrderDetailExample));

		return reckonOrderInfo;

	}

	/**
	 * 获取清分订单信息
	 * 
	 * @param sOrderCode
	 * @return
	 */
	public GcReckonOrderInfo upGcReckonOrderInfo(String sOrderCode) {
		GcReckonOrderInfoMapper gcReckonOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderInfoMapper");
		GcReckonOrderInfoExample gcReckonOrderInfoExample = new GcReckonOrderInfoExample();
		gcReckonOrderInfoExample.createCriteria().andOrderCodeEqualTo(
				sOrderCode);
		return gcReckonOrderInfoMapper
				.selectByExample(gcReckonOrderInfoExample).get(0);
	}

	/**
	 * 获取清分订单信息
	 * 
	 * @param sOrderCode
	 * @return
	 */
	public List<GcReckonOrderDetail> upGcReckonOrderDetail(String sOrderCode) {
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonOrderDetailExample gcReckonOrderDetailExample = new GcReckonOrderDetailExample();
		gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(
				sOrderCode);
		return gcReckonOrderDetailMapper
				.selectByExample(gcReckonOrderDetailExample);
	}
	
	/**
	 * 获取清分订单信息通过detailCode
	 * 
	 * @param sDetailCode  fengl
	 * @return
	 */
	public List<GcReckonOrderDetail> upGcReckonOrderDetailCode(String sDetailCode) {
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonOrderDetailExample gcReckonOrderDetailExample = new GcReckonOrderDetailExample();
		gcReckonOrderDetailExample.createCriteria().andDetailCodeEqualTo(
				sDetailCode);
		return gcReckonOrderDetailMapper
				.selectByExample(gcReckonOrderDetailExample);
	}

	/**
	 * 刷新订单金额
	 * 
	 * @param sOrderCode
	 */
	public void refreshOrder(String sOrderCode) {

		GcReckonOrderInfoMapper gcReckonOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderInfoMapper");

		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");

		GcReckonOrderDetailExample gcReckonOrderDetailExample = new GcReckonOrderDetailExample();
		gcReckonOrderDetailExample.createCriteria()
				.andOrderCodeEqualTo(sOrderCode).andFlagReckonEqualTo(1);

		BigDecimal bReckonSum = BigDecimal.ZERO;
		BigDecimal bOrderSum = BigDecimal.ZERO;

		// 循环所有单据明细
		for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetailMapper
				.selectByExample(gcReckonOrderDetailExample)) {

			// 如果行项目参与清分
			if (gcReckonOrderDetail.getFlagReckon() == 1) {
				bReckonSum = bReckonSum.add(gcReckonOrderDetail
						.getSumReckonMoney());
			}
			bOrderSum = bOrderSum.add(gcReckonOrderDetail.getPriceSell()
					.multiply(
							new BigDecimal(gcReckonOrderDetail
									.getProductNumber())));

		}

		GcReckonOrderInfo gcReckonOrderInfo = new GcReckonOrderInfo();
		gcReckonOrderInfo.setOrderMoney(bOrderSum);
		gcReckonOrderInfo.setReckonMoney(bReckonSum);

		GcReckonOrderInfoExample gcReckonOrderInfoExample = new GcReckonOrderInfoExample();
		gcReckonOrderInfoExample.createCriteria().andOrderCodeEqualTo(
				sOrderCode);

		// 更新订单和清分的统计
		gcReckonOrderInfoMapper.updateByExampleSelective(gcReckonOrderInfo,
				gcReckonOrderInfoExample);

	}

	private String upInfo(long lInfoCode, String... sParams) {

		return FormatHelper.upDateTime() + bInfo(lInfoCode, sParams);
	}

	/**
	 * 正向清分流程
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonIn(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
			}
		}

		List<String> listExec = new ArrayList<String>();

		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		if (mWebResult.upFlagTrue()) {

			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {

				listExec.add(upInfo(918515301,
						accountRelation.getAccountCode(),
						String.valueOf(accountRelation.getDeep())));

				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);

				// 定义当前微公社账户信息
				GcGroupAccount gcGroupAccount = listAccounts.get(0);

				// 开始获取当前级别信息的缓存信息
				// MDataMap mNowLevelInfo =
				// WebTemp.upTempDataMap("gc_group_level", "",
				// "level_code",gcGroupAccount.getAccountLevel());
				GroupLevelInfo groupLevelInfo = ReckonOrderSupport.INSTANCE
						.upLevelInfo(gcGroupAccount.getAccountLevel(),
								sManageCode);

				// 定义升级所需消费金额
				BigDecimal bUpgradeConsume = groupLevelInfo.getUpgradeConsume();
				// 定义清分比例
				BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
				// 定义升级所需社员数量
				int iUpgradeMembers = groupLevelInfo.getUpgradeMembers();
				// 定义清分深度
				int iDeepReckon = groupLevelInfo.getDeepReckon();
				// 定义活跃统计深度
				int iDeepConsume = groupLevelInfo.getDeepConsume();

				// 定义下一级别
				String sNextLevel = groupLevelInfo.getNextLevel();
				// 定义当前用户级别
				String sLevelCode = groupLevelInfo.getLevelCode();
				// 定义当前级别类型
				String sLevelType = groupLevelInfo.getLevelType();

				// 开始更新消费金额
				GcActiveLog gcActiveLog = new GcActiveLog();
				gcActiveLog.setAccountCode(accountRelation.getAccountCode());

				boolean bFlagDeepConsume = accountRelation.getDeep() <= iDeepConsume;

				// 判断如果能计算该深度 才加上消费金额 否则只记活跃人数
				if (bFlagDeepConsume) {
					gcActiveLog.setConsumeMoney(gcReckonOrderInfo
							.getReckonMoney());
				} else {
					gcActiveLog.setConsumeMoney(BigDecimal.ZERO);
				}

				gcActiveLog.setRelationLevel(accountRelation.getDeep());
				gcActiveLog.setOrderAccountCode(sAccountCode);
				gcActiveLog.setOrderCode(gcReckonOrderInfo.getOrderCode());
				gcActiveLog.setActiveTime(gcReckonOrderInfo
						.getOrderFinishTime());

				gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
						.getStepCode()));

				// 定义增加的活跃数量 如果可以计算到该消费记录且关系度数大于0才加1
				int iAddMembers = (bFlagDeepConsume && accountRelation
						.getDeep() > 0) ? 1 : 0;

				listExec.add(upInfo(918515303,
						accountRelation.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString(), String
								.valueOf(iAddMembers)));

				GcActiveMonth gcActiveMonth = updateActiveCount(gcActiveLog,
						iAddMembers);

				// 定义清分日志数组
				List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

				// 定义当前消费金额
				BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();
				// 定义之前消费金额
				BigDecimal bLastReckon = gcActiveLog.getLastSumConsume();

				// 判断如果当月消费金额和社友人数超过级别限制时 开启差量升级模式
				if (gcActiveMonth.getSumConsume().compareTo(bUpgradeConsume) >= 0
						&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

					listExec.add(upInfo(918515302, accountRelation
							.getAccountCode(), groupLevelInfo.getLevelName(),
							gcActiveMonth.getSumConsume().toString(), String
									.valueOf(gcActiveMonth.getSumMember())));
					GcGroupLevelMapper gcGroupLevelMapper = BeansHelper
							.upBean("bean_com_cmall_dborm_txmapper_GcGroupLevelMapper");

					GcLevelLogMapper gcLevelLogMapper = BeansHelper
							.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");

					// 当能升级时 首先判断处理当前升級的等级差
					BigDecimal bReckonSave = bUpgradeConsume
							.subtract(bLastReckon);
					if (bReckonSave.compareTo(BigDecimal.ZERO) < 0) {
						bReckonSave = BigDecimal.ZERO;
					}
					GcReckonLog gcSaveLog = new GcReckonLog();
					gcSaveLog.setScaleReckon(bScaleReckon);
					gcSaveLog.setReckonMoney(bReckonSave.multiply(gcSaveLog
							.getScaleReckon()));

					// 如果能清到该记录 则添加进list中
					if (iDeepReckon >= accountRelation.getDeep()
							&& gcSaveLog.getReckonMoney().compareTo(
									BigDecimal.ZERO) > 0) {
						listReckonLogs.add(gcSaveLog);
						listExec.add(upInfo(918515304, accountRelation
								.getAccountCode(), bReckonSave.toString(),
								bScaleReckon.toString(), gcSaveLog
										.getReckonMoney().toString()));

					}
					bNowReckon = bNowReckon.subtract(bReckonSave);

					// 循环触发升级模型
					while (gcActiveMonth.getSumConsume().compareTo(
							bUpgradeConsume) >= 0
							&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

						// 初始化实际存入值为0
						bReckonSave = BigDecimal.ZERO;

						GroupLevelInfo groupUpdateLevel = ReckonOrderSupport.INSTANCE
								.upLevelInfo(sNextLevel, sManageCode);

						listExec.add(upInfo(918515305,
								accountRelation.getAccountCode(),
								groupUpdateLevel.getLevelName()));

						// 当历史金额小于级别升级所需金额时
						if (bLastReckon.compareTo(bUpgradeConsume) < 0) {
							// 定义级别所需要的金额
							BigDecimal bLevelDecimal = groupUpdateLevel
									.getUpgradeConsume().subtract(
											bUpgradeConsume);

							// 判断如果当前金额小于差量
							if (bNowReckon.compareTo(bLevelDecimal) <= 0) {
								bReckonSave = bNowReckon;
							} else {
								bReckonSave = bLevelDecimal;
							}

							bNowReckon = bNowReckon.subtract(bReckonSave);

						}

						// 级别变动日志
						GcLevelLog gcLevelLog = new GcLevelLog();
						gcLevelLog.setAccountCode(accountRelation
								.getAccountCode());
						gcLevelLog.setCreateTime(FormatHelper.upDateTime());

						gcLevelLog.setLastMemberLevel(sLevelCode);
						sLevelCode = groupUpdateLevel.getLevelCode();
						gcLevelLog.setCurrentMemberLevel(sLevelCode);
						gcLevelLog.setChangeType("4497465200080001");
						gcLevelLog.setUid(WebHelper.upUuid());
						// 插入级别变动日志表
						gcLevelLogMapper.insertSelective(gcLevelLog);

						// 开始定义清分日志
						GcReckonLog gcReckonLog = new GcReckonLog();
						// 设置清分比例
						bScaleReckon = groupUpdateLevel.getScaleReckon();
						// 设置清分深度
						iDeepReckon = groupUpdateLevel.getDeepReckon();

						gcReckonLog.setScaleReckon(bScaleReckon);
						gcReckonLog.setReckonMoney(bReckonSave
								.multiply(gcReckonLog.getScaleReckon()));
						gcReckonLog.setChangeCodes(reckonStep.getStepCode());

						// 如果能清到该记录 则添加进list中
						if (iDeepReckon >= accountRelation.getDeep()
								&& gcReckonLog.getReckonMoney().compareTo(
										BigDecimal.ZERO) > 0) {
							listReckonLogs.add(gcReckonLog);

							listExec.add(upInfo(918515304, accountRelation
									.getAccountCode(), bReckonSave.toString(),
									bScaleReckon.toString(), gcReckonLog
											.getReckonMoney().toString()));

						}

						bUpgradeConsume = groupUpdateLevel.getUpgradeConsume();
						iUpgradeMembers = groupUpdateLevel.getUpgradeMembers();
						sNextLevel = groupUpdateLevel.getNextLevel();

					}

					// 循环升级完成后 设置更新信息
					// 定义更新的微公社账户信息
					GcGroupAccount gcUpdateAccount = new GcGroupAccount();
					gcUpdateAccount.setAccountLevel(sLevelCode);
					gcUpdateAccount.setScaleReckon(bScaleReckon);
					gcUpdateAccount.setLevelChangeTime(FormatHelper
							.upDateTime());
					gcUpdateAccount.setLevelType(sLevelType);
					gcGroupAccountMapper.updateByExampleSelective(
							gcUpdateAccount, gcGroupAccountExample);

				}

				// 判断如果能清分到级别 再次添加信息
				if (accountRelation.getDeep() <= iDeepReckon
						&& bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
					GcReckonLog gcReckonLog = new GcReckonLog();

					gcReckonLog.setScaleReckon(bScaleReckon);
					gcReckonLog.setChangeCodes(reckonStep.getStepCode());
					gcReckonLog.setReckonMoney(bNowReckon.multiply(gcReckonLog
							.getScaleReckon()));
					listReckonLogs.add(gcReckonLog);

					listExec.add(upInfo(918515304,
							accountRelation.getAccountCode(),
							bNowReckon.toString(), bScaleReckon.toString(),
							gcReckonLog.getReckonMoney().toString()));

					// gcReckonLog.setChangeCodes("");
				}

				// 判断如果数量大于0
				if (listReckonLogs.size() > 0) {
					for (int i = 0, j = listReckonLogs.size(); i < j; i++) {
						listReckonLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listReckonLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listReckonLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listReckonLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listReckonLogs.get(i).setOrderReckonTime(
								gcReckonOrderInfo.getOrderFinishTime());
						listReckonLogs.get(i).setReckonChangeType(
								"4497465200030001");
					}

					txGroupAccountService.updateAccount(listReckonLogs, null);

				}

			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}

	/**
	 * 正向清分流程第二版代码，本轮代码于20150325修改 主要用于加入根据SKU清分的流程
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonInTwo(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();

		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());

		// 获取订单明细
		List<GcReckonOrderDetail> gcReckonOrderDetails = upGcReckonOrderDetail(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		if (mWebResult.upFlagTrue()) {

			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {

				// 循环所有明细
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {

					// 只有明细上可清分并且清分金额大于0 才开始清分
					if (gcReckonOrderDetail.getFlagReckon() == 1
							&& gcReckonOrderDetail.getSumReckonMoney().compareTo(
									BigDecimal.ZERO) > 0) {
						
						BigDecimal bConsumeMoney = gcReckonOrderDetail
								.getSumReckonMoney();
						
						String sSkuCode=gcReckonOrderDetail.getSkuCode();
						if(StringUtils.isBlank(sSkuCode)){
							sSkuCode=gcReckonOrderDetail.getProductCode();
						}
						
						

						listExec.add(upInfo(918515307,
								accountRelation.getAccountCode(),
								String.valueOf(accountRelation.getDeep())));

						GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
						gcGroupAccountExample.createCriteria()
								.andAccountCodeEqualTo(
										accountRelation.getAccountCode());

						List<GcGroupAccount> listAccounts = gcGroupAccountMapper
								.selectByExample(gcGroupAccountExample);

						// 定义当前微公社账户信息
						GcGroupAccount gcGroupAccount = listAccounts.get(0);

						// 开始获取当前级别信息的缓存信息
						// MDataMap mNowLevelInfo =
						// WebTemp.upTempDataMap("gc_group_level", "",
						// "level_code",gcGroupAccount.getAccountLevel());
						GroupLevelInfo groupLevelInfo = ReckonOrderSupport.INSTANCE
								.upLevelInfoForTwo(gcGroupAccount.getAccountLevel(),
										sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime());

						// 定义升级所需消费金额
						BigDecimal bUpgradeConsume = groupLevelInfo
								.getUpgradeConsume();
						// 定义清分比例
						BigDecimal bScaleReckon = groupLevelInfo
								.getScaleReckon();
						// 定义升级所需社员数量
						int iUpgradeMembers = groupLevelInfo
								.getUpgradeMembers();
						// 定义清分深度
						int iDeepReckon = groupLevelInfo.getDeepReckon();
						// 定义活跃统计深度
						int iDeepConsume = groupLevelInfo.getDeepConsume();

						// 定义下一级别
						String sNextLevel = groupLevelInfo.getNextLevel();
						// 定义当前用户级别
						String sLevelCode = groupLevelInfo.getLevelCode();
						// 定义当前级别类型
						String sLevelType = groupLevelInfo.getLevelType();

						// 开始更新消费金额
						GcActiveLog gcActiveLog = new GcActiveLog();
						gcActiveLog.setAccountCode(accountRelation
								.getAccountCode());

						boolean bFlagDeepConsume = accountRelation.getDeep() <= iDeepConsume;

						// 判断如果能计算该深度 才加上消费金额 否则只记活跃人数
						if (bFlagDeepConsume) {
							gcActiveLog.setConsumeMoney(bConsumeMoney);
						} else {
							gcActiveLog.setConsumeMoney(BigDecimal.ZERO);
						}

						gcActiveLog.setRelationLevel(accountRelation.getDeep());
						gcActiveLog.setOrderAccountCode(sAccountCode);
						gcActiveLog.setOrderCode(gcReckonOrderInfo
								.getOrderCode());
						gcActiveLog.setActiveTime(gcReckonOrderInfo
								.getOrderFinishTime());

						gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
								.getStepCode()));

						// 定义增加的活跃数量 如果可以计算到该消费记录且关系度数大于0才加1
						int iAddMembers = (bFlagDeepConsume && accountRelation
								.getDeep() > 0) ? 1 : 0;

						listExec.add(upInfo(918515303, accountRelation
								.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString(), String
								.valueOf(iAddMembers)));

						GcActiveMonth gcActiveMonth = updateActiveCount(
								gcActiveLog, iAddMembers);

						// 定义清分日志数组
						List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

						// 定义当前消费金额
						BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();
						// 定义之前消费金额
						BigDecimal bLastReckon = gcActiveLog
								.getLastSumConsume();

						// 判断如果当月消费金额和社友人数超过级别限制时 开启差量升级模式
						if (gcActiveMonth.getSumConsume().compareTo(
								bUpgradeConsume) >= 0
								&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

							listExec.add(upInfo(918515302, accountRelation
									.getAccountCode(), groupLevelInfo
									.getLevelName(), gcActiveMonth
									.getSumConsume().toString(), String
									.valueOf(gcActiveMonth.getSumMember())));
							GcGroupLevelMapper gcGroupLevelMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcGroupLevelMapper");

							GcLevelLogMapper gcLevelLogMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");

							// 当能升级时 首先判断处理当前升級的等级差
							BigDecimal bReckonSave = bUpgradeConsume
									.subtract(bLastReckon);
							if (bReckonSave.compareTo(BigDecimal.ZERO) < 0) {
								bReckonSave = BigDecimal.ZERO;
							}
							GcReckonLog gcSaveLog = new GcReckonLog();
							gcSaveLog.setScaleReckon(bScaleReckon);
							gcSaveLog.setReckonMoney(bReckonSave
									.multiply(gcSaveLog.getScaleReckon()));

							// 如果能清到该记录 则添加进list中
							if (iDeepReckon >= accountRelation.getDeep()
									&& gcSaveLog.getReckonMoney().compareTo(
											BigDecimal.ZERO) > 0) {
								listReckonLogs.add(gcSaveLog);
								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bReckonSave
										.toString(), bScaleReckon.toString(),
										gcSaveLog.getReckonMoney().toString()));

							}
							bNowReckon = bNowReckon.subtract(bReckonSave);

							// 循环触发升级模型
							while (gcActiveMonth.getSumConsume().compareTo(
									bUpgradeConsume) >= 0
									&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

								// 初始化实际存入值为0
								bReckonSave = BigDecimal.ZERO;

								GroupLevelInfo groupUpdateLevel = ReckonOrderSupport.INSTANCE
										.upLevelInfoForTwo(sNextLevel, sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime());

								listExec.add(upInfo(918515305,
										accountRelation.getAccountCode(),
										groupUpdateLevel.getLevelName()));

								// 当历史金额小于级别升级所需金额时
								if (bLastReckon.compareTo(bUpgradeConsume) < 0) {
									// 定义级别所需要的金额
									BigDecimal bLevelDecimal = groupUpdateLevel
											.getUpgradeConsume().subtract(
													bUpgradeConsume);

									// 判断如果当前金额小于差量
									if (bNowReckon.compareTo(bLevelDecimal) <= 0) {
										bReckonSave = bNowReckon;
									} else {
										bReckonSave = bLevelDecimal;
									}

									bNowReckon = bNowReckon
											.subtract(bReckonSave);

								}

								// 级别变动日志
								GcLevelLog gcLevelLog = new GcLevelLog();
								gcLevelLog.setAccountCode(accountRelation
										.getAccountCode());
								gcLevelLog.setCreateTime(FormatHelper
										.upDateTime());

								gcLevelLog.setLastMemberLevel(sLevelCode);
								sLevelCode = groupUpdateLevel.getLevelCode();
								gcLevelLog.setCurrentMemberLevel(sLevelCode);
								gcLevelLog.setChangeType("4497465200080001");
								gcLevelLog.setUid(WebHelper.upUuid());
								// 插入级别变动日志表
								gcLevelLogMapper.insertSelective(gcLevelLog);

								// 开始定义清分日志
								GcReckonLog gcReckonLog = new GcReckonLog();
								// 设置清分比例
								bScaleReckon = groupUpdateLevel
										.getScaleReckon();
								// 设置清分深度
								iDeepReckon = groupUpdateLevel.getDeepReckon();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setReckonMoney(bReckonSave
												.multiply(gcReckonLog
														.getScaleReckon()));
								gcReckonLog.setChangeCodes(reckonStep
										.getStepCode());

								// 如果能清到该记录 则添加进list中
								if (iDeepReckon >= accountRelation.getDeep()
										&& gcReckonLog.getReckonMoney()
												.compareTo(BigDecimal.ZERO) > 0) {
									listReckonLogs.add(gcReckonLog);

									listExec.add(upInfo(918515304,
											accountRelation.getAccountCode(),
											bReckonSave.toString(),
											bScaleReckon.toString(),
											gcReckonLog.getReckonMoney()
													.toString()));

								}

								bUpgradeConsume = groupUpdateLevel
										.getUpgradeConsume();
								iUpgradeMembers = groupUpdateLevel
										.getUpgradeMembers();
								sNextLevel = groupUpdateLevel.getNextLevel();

							}

							// 循环升级完成后 设置更新信息
							// 定义更新的微公社账户信息
							GcGroupAccount gcUpdateAccount = new GcGroupAccount();
							gcUpdateAccount.setAccountLevel(sLevelCode);
							gcUpdateAccount.setScaleReckon(bScaleReckon);
							gcUpdateAccount.setLevelChangeTime(FormatHelper
									.upDateTime());
							gcUpdateAccount.setLevelType(sLevelType);
							gcGroupAccountMapper.updateByExampleSelective(
									gcUpdateAccount, gcGroupAccountExample);

						}

						// 判断如果能清分到级别 再次添加信息
						if (accountRelation.getDeep() <= iDeepReckon
								&& bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
							GcReckonLog gcReckonLog = new GcReckonLog();

							gcReckonLog.setScaleReckon(bScaleReckon);
							gcReckonLog
									.setChangeCodes(reckonStep.getStepCode());
							gcReckonLog.setReckonMoney(bNowReckon
									.multiply(gcReckonLog.getScaleReckon()));
							listReckonLogs.add(gcReckonLog);

							listExec.add(upInfo(918515304, accountRelation
									.getAccountCode(), bNowReckon.toString(),
									bScaleReckon.toString(), gcReckonLog
											.getReckonMoney().toString()));

							// gcReckonLog.setChangeCodes("");
						}

						// 判断如果数量大于0
						if (listReckonLogs.size() > 0) {
							for (int i = 0, j = listReckonLogs.size(); i < j; i++) {
								listReckonLogs.get(i).setAccountCode(
										accountRelation.getAccountCode());
								listReckonLogs.get(i).setOrderCode(
										gcReckonOrderInfo.getOrderCode());
								listReckonLogs.get(i).setRelationLevel(
										accountRelation.getDeep());
								listReckonLogs.get(i).setOrderAccountCode(
										gcReckonOrderInfo.getAccountCode());
								listReckonLogs.get(i).setOrderReckonTime(
										gcReckonOrderInfo.getOrderFinishTime());
								listReckonLogs.get(i).setReckonChangeType(
										"4497465200030001");
								listReckonLogs.get(i).setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
							}

							txGroupAccountService.updateAccount(listReckonLogs,
									null);

						}

					}
				}
			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向清分流程第三版代码，本轮代码于20150625修改 改为根据商家设置的返现范围和sku比例及商家等级比例清分
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonInThird(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();

		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());

		// 获取订单明细
		List<GcReckonOrderDetail> gcReckonOrderDetails = upGcReckonOrderDetail(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&appMap.get("trade_code")!=null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		//商户不存在或者状态不可用，不返利
		if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
			listExec.add(upInfo(918512020, sManageCode));
			mWebResult.setResultCode(918512020);
		}
		
		if (mWebResult.upFlagTrue()) {

			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {

				// 循环所有明细
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {

					// 只有明细上可清分并且清分金额大于0 才开始清分
					if (gcReckonOrderDetail.getFlagReckon() == 1
							&& gcReckonOrderDetail.getSumReckonMoney().compareTo(
									BigDecimal.ZERO) > 0) {
						
						BigDecimal bConsumeMoney = gcReckonOrderDetail
								.getSumReckonMoney();
						
						String sSkuCode=gcReckonOrderDetail.getSkuCode();
						if(StringUtils.isBlank(sSkuCode)){
							sSkuCode=gcReckonOrderDetail.getProductCode();
						}
						
						

						listExec.add(upInfo(918515308,
								accountRelation.getAccountCode(),
								String.valueOf(accountRelation.getDeep())));

						GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
						gcGroupAccountExample.createCriteria()
								.andAccountCodeEqualTo(
										accountRelation.getAccountCode());

						List<GcGroupAccount> listAccounts = gcGroupAccountMapper
								.selectByExample(gcGroupAccountExample);

						// 定义当前微公社账户信息
						GcGroupAccount gcGroupAccount = listAccounts.get(0);

						// 开始获取当前级别信息的缓存信息
						// MDataMap mNowLevelInfo =
						// WebTemp.upTempDataMap("gc_group_level", "",
						// "level_code",gcGroupAccount.getAccountLevel());
						GroupLevelInfo groupLevelInfo = ReckonOrderSupport.INSTANCE
								.upLevelInfoForThird(gcGroupAccount.getAccountLevel(),
										sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

						// 定义升级所需消费金额
						BigDecimal bUpgradeConsume = groupLevelInfo
								.getUpgradeConsume();
						// 定义清分比例
						BigDecimal bScaleReckon = groupLevelInfo
								.getScaleReckon();
						// 定义升级所需社员数量
						int iUpgradeMembers = groupLevelInfo
								.getUpgradeMembers();
						// 定义清分深度
						int iDeepReckon = groupLevelInfo.getDeepReckon();
						// 定义活跃统计深度
						int iDeepConsume = groupLevelInfo.getDeepConsume();

						// 定义下一级别
						String sNextLevel = groupLevelInfo.getNextLevel();
						// 定义当前用户级别
						String sLevelCode = groupLevelInfo.getLevelCode();
						// 定义当前级别类型
						String sLevelType = groupLevelInfo.getLevelType();

						// 开始更新消费金额
						GcActiveLog gcActiveLog = new GcActiveLog();
						gcActiveLog.setAccountCode(accountRelation
								.getAccountCode());

						boolean bFlagDeepConsume = accountRelation.getDeep() <= iDeepConsume;

						// 判断如果能计算该深度 才加上消费金额 否则只记活跃人数
						if (bFlagDeepConsume) {
							gcActiveLog.setConsumeMoney(bConsumeMoney);
						} else {
							gcActiveLog.setConsumeMoney(BigDecimal.ZERO);
						}

						gcActiveLog.setRelationLevel(accountRelation.getDeep());
						gcActiveLog.setOrderAccountCode(sAccountCode);
						gcActiveLog.setOrderCode(gcReckonOrderInfo
								.getOrderCode());
						gcActiveLog.setActiveTime(gcReckonOrderInfo
								.getOrderFinishTime());

						gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
								.getStepCode()));
                        gcActiveLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
						// 定义增加的活跃数量 如果可以计算到该消费记录且关系度数大于0才加1
						int iAddMembers = (bFlagDeepConsume && accountRelation
								.getDeep() > 0) ? 1 : 0;

						listExec.add(upInfo(918515303, accountRelation
								.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString(), String
								.valueOf(iAddMembers)));

						GcActiveMonth gcActiveMonth = updateActiveCount(
								gcActiveLog, iAddMembers);

						// 定义清分日志数组
						List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

						// 定义当前消费金额
						BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();
						// 定义之前消费金额
						BigDecimal bLastReckon = gcActiveLog
								.getLastSumConsume();

						// 判断如果当月消费金额和社友人数超过级别限制时 开启差量升级模式
						if (gcActiveMonth.getSumConsume().compareTo(
								bUpgradeConsume) >= 0
								&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

							listExec.add(upInfo(918515302, accountRelation
									.getAccountCode(), groupLevelInfo
									.getLevelName(), gcActiveMonth
									.getSumConsume().toString(), String
									.valueOf(gcActiveMonth.getSumMember())));
							GcGroupLevelMapper gcGroupLevelMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcGroupLevelMapper");

							GcLevelLogMapper gcLevelLogMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");

							// 当能升级时 首先判断处理当前升級的等级差
							BigDecimal bReckonSave = bUpgradeConsume
									.subtract(bLastReckon);
							if (bReckonSave.compareTo(BigDecimal.ZERO) < 0) {
								bReckonSave = BigDecimal.ZERO;
							}
							GcReckonLog gcSaveLog = new GcReckonLog();
							gcSaveLog.setScaleReckon(bScaleReckon);
							gcSaveLog.setReckonMoney(bReckonSave
									.multiply(gcSaveLog.getScaleReckon()));

							// 如果能清到该记录 则添加进list中
							if (iDeepReckon >= accountRelation.getDeep()
									&& gcSaveLog.getReckonMoney().compareTo(
											BigDecimal.ZERO) > 0) {
								listReckonLogs.add(gcSaveLog);
								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bReckonSave
										.toString(), bScaleReckon.toString(),
										gcSaveLog.getReckonMoney().toString()));

							}
							bNowReckon = bNowReckon.subtract(bReckonSave);

							// 循环触发升级模型
							while (gcActiveMonth.getSumConsume().compareTo(
									bUpgradeConsume) >= 0
									&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

								// 初始化实际存入值为0
								bReckonSave = BigDecimal.ZERO;

								GroupLevelInfo groupUpdateLevel = ReckonOrderSupport.INSTANCE
										.upLevelInfoForThird(sNextLevel, sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

								listExec.add(upInfo(918515305,
										accountRelation.getAccountCode(),
										groupUpdateLevel.getLevelName()));

								// 当历史金额小于级别升级所需金额时
								if (bLastReckon.compareTo(bUpgradeConsume) < 0) {
									// 定义级别所需要的金额
									BigDecimal bLevelDecimal = groupUpdateLevel
											.getUpgradeConsume().subtract(
													bUpgradeConsume);

									// 判断如果当前金额小于差量
									if (bNowReckon.compareTo(bLevelDecimal) <= 0) {
										bReckonSave = bNowReckon;
									} else {
										bReckonSave = bLevelDecimal;
									}

									bNowReckon = bNowReckon
											.subtract(bReckonSave);

								}

								// 级别变动日志
								GcLevelLog gcLevelLog = new GcLevelLog();
								gcLevelLog.setAccountCode(accountRelation
										.getAccountCode());
								gcLevelLog.setCreateTime(FormatHelper
										.upDateTime());

								gcLevelLog.setLastMemberLevel(sLevelCode);
								sLevelCode = groupUpdateLevel.getLevelCode();
								gcLevelLog.setCurrentMemberLevel(sLevelCode);
								gcLevelLog.setChangeType("4497465200080001");
								gcLevelLog.setUid(WebHelper.upUuid());
								// 插入级别变动日志表
								gcLevelLogMapper.insertSelective(gcLevelLog);

								// 开始定义清分日志
								GcReckonLog gcReckonLog = new GcReckonLog();
								// 设置清分比例
								bScaleReckon = groupUpdateLevel
										.getScaleReckon();
								// 设置清分深度
								iDeepReckon = groupUpdateLevel.getDeepReckon();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setReckonMoney(bReckonSave
												.multiply(gcReckonLog
														.getScaleReckon()));
								gcReckonLog.setChangeCodes(reckonStep
										.getStepCode());

								// 如果能清到该记录 则添加进list中
								if (iDeepReckon >= accountRelation.getDeep()
										&& gcReckonLog.getReckonMoney()
												.compareTo(BigDecimal.ZERO) > 0) {
									listReckonLogs.add(gcReckonLog);

									listExec.add(upInfo(918515304,
											accountRelation.getAccountCode(),
											bReckonSave.toString(),
											bScaleReckon.toString(),
											gcReckonLog.getReckonMoney()
													.toString()));

								}

								bUpgradeConsume = groupUpdateLevel
										.getUpgradeConsume();
								iUpgradeMembers = groupUpdateLevel
										.getUpgradeMembers();
								sNextLevel = groupUpdateLevel.getNextLevel();

							}

							// 循环升级完成后 设置更新信息
							// 定义更新的微公社账户信息
							GcGroupAccount gcUpdateAccount = new GcGroupAccount();
							gcUpdateAccount.setAccountLevel(sLevelCode);
							gcUpdateAccount.setScaleReckon(bScaleReckon);
							gcUpdateAccount.setLevelChangeTime(FormatHelper
									.upDateTime());
							gcUpdateAccount.setLevelType(sLevelType);
							gcGroupAccountMapper.updateByExampleSelective(
									gcUpdateAccount, gcGroupAccountExample);

						}

						// 判断如果能清分到级别 再次添加信息
						if (accountRelation.getDeep() <= iDeepReckon
								&& bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
							
							//清分金额必须>0
							BigDecimal reckonMoney = bNowReckon.multiply(bScaleReckon);
							if(reckonMoney.compareTo(BigDecimal.ZERO)>0){
								GcReckonLog gcReckonLog = new GcReckonLog();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setChangeCodes(reckonStep.getStepCode());
//								gcReckonLog.setReckonMoney(bNowReckon
//										.multiply(gcReckonLog.getScaleReckon()));
								gcReckonLog.setReckonMoney(reckonMoney);
								listReckonLogs.add(gcReckonLog);

								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bNowReckon.toString(),
										bScaleReckon.toString(), gcReckonLog
												.getReckonMoney().toString()));
							}


							// gcReckonLog.setChangeCodes("");
						}

						// 判断如果数量大于0
						if (listReckonLogs.size() > 0) {
							for (int i = 0, j = listReckonLogs.size(); i < j; i++) {
								listReckonLogs.get(i).setAccountCode(
										accountRelation.getAccountCode());
								listReckonLogs.get(i).setOrderCode(
										gcReckonOrderInfo.getOrderCode());
								listReckonLogs.get(i).setRelationLevel(
										accountRelation.getDeep());
								listReckonLogs.get(i).setOrderAccountCode(
										gcReckonOrderInfo.getAccountCode());
								listReckonLogs.get(i).setOrderReckonTime(
										gcReckonOrderInfo.getOrderFinishTime());
								listReckonLogs.get(i).setReckonChangeType(
										"4497465200030001");
								listReckonLogs.get(i).setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
								listReckonLogs.get(i).setDetailCode(gcReckonOrderDetail.getDetailCode());
							}

							txGroupAccountService.updateAccount(listReckonLogs,
									null);

						}

					}
				}
			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}

	/**
	 * 正向清分流程第四版代码，本轮代码于20151008修改 按照创建预计返利时的返利比例进行清分;订单创建时间>停止返利时间,并且商户状态是停用状态,则不再给用户清分
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doReckonInFourth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();
		
		List<String> listExec = new ArrayList<String>();
		listExec.add("reckonInFourth");
		
		//校验是否存在执行成功的预返利流程,没有返回错误
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050003%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805143, reckonStep.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
			listExec.add(upInfo(915805143, reckonStep.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_IN));
		}
		
		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());
		//因为订单状态同步时会出现订单签收时间为空的情况(原因待查)，所以再强制校验一次
		if(StringUtils.isEmpty(gcReckonOrderInfo.getOrderFinishTime())){
			listExec.add(upInfo(918515313));
			mWebResult.setResultCode(918515313);
		}

		// 获取订单明细
		List<GcReckonOrderDetail> gcReckonOrderDetails = upGcReckonOrderDetail(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&appMap.get("trade_code")!=null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		//商户不存在或者状态不可用，不返利
		if(mWebResult.upFlagTrue()){
			if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
				listExec.add(upInfo(918512020, sManageCode));
				mWebResult.setResultCode(918512020);
			}
		}
		
		//订单创建时间>停止返利时间,并且商户状态是停用状态,则此订单不再给用户清分
		if (mWebResult.upFlagTrue() && StringUtils.isNotBlank(gcReckonOrderInfo.getOrderCreateTime())){
			String stopSql = "select trader_status,update_time from gc_trader_status_log where trader_code =:traderCode and update_time <=:orderCreateTime order by update_time desc ";
			MDataMap stopMap = new MDataMap();
			stopMap.put("traderCode", appMap.get("trade_code"));
			stopMap.put("orderCreateTime", gcReckonOrderInfo.getOrderCreateTime());
			List<Map<String, Object>> stopList = DbUp.upTable("gc_trader_status_log").dataSqlList(stopSql, stopMap);
			if(stopList != null && stopList.size() > 0){
				String tStatus = String.valueOf(stopList.get(0).get("trader_status"));
				//订单创建时，商户已经是停用状态
				if("4497472500010002".equals(tStatus)){
					listExec.add(upInfo(918512022, sManageCode));
					mWebResult.inErrorMessage(918512022,sManageCode);
				}
			}
		}
		
		if (mWebResult.upFlagTrue()) {

			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {

				// 循环所有明细
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {

					// 只有明细上可清分并且清分金额大于0 才开始清分
					if (gcReckonOrderDetail.getFlagReckon() == 1
							&& gcReckonOrderDetail.getSumReckonMoney().compareTo(
									BigDecimal.ZERO) > 0) {
						
						BigDecimal bConsumeMoney = gcReckonOrderDetail
								.getSumReckonMoney();
						
						String sSkuCode=gcReckonOrderDetail.getSkuCode();
						if(StringUtils.isBlank(sSkuCode)){
							sSkuCode=gcReckonOrderDetail.getProductCode();
						}
						
						

						listExec.add(upInfo(918515308,
								accountRelation.getAccountCode(),
								String.valueOf(accountRelation.getDeep())));

						GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
						gcGroupAccountExample.createCriteria()
								.andAccountCodeEqualTo(
										accountRelation.getAccountCode());

						List<GcGroupAccount> listAccounts = gcGroupAccountMapper
								.selectByExample(gcGroupAccountExample);

						// 定义当前微公社账户信息
						GcGroupAccount gcGroupAccount = listAccounts.get(0);

						// 开始获取当前级别信息的缓存信息
						// MDataMap mNowLevelInfo =
						// WebTemp.upTempDataMap("gc_group_level", "",
						// "level_code",gcGroupAccount.getAccountLevel());
						GroupLevelInfo groupLevelInfo = ReckonOrderSupport.INSTANCE
								.upLevelInfoForThird(gcGroupAccount.getAccountLevel(),
										sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

						// 定义升级所需消费金额
						BigDecimal bUpgradeConsume = groupLevelInfo
								.getUpgradeConsume();
						// 定义清分比例  若过程中修改返利比例，则此订单依然按照最初创建的比例清分 20151008 modify start
//						BigDecimal bScaleReckon = groupLevelInfo
//								.getScaleReckon();
						BigDecimal bScaleReckon = null;
						//获取预返利的返利比例
						GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
						gcRebateLogExample.createCriteria()
								.andAccountCodeEqualTo(accountRelation.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode())
								.andRebateChangeTypeEqualTo("4497465200140001").andDetailCodeEqualTo(gcReckonOrderDetail.getDetailCode()).andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
						List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
						if(listRebateLogs != null && listRebateLogs.size() > 0){
							bScaleReckon = listRebateLogs.get(0).getScaleReckon();
						}
						//没有获取到预返利的返利比例,继续下一条数据
						if(bScaleReckon == null){
							continue;
						}
						// 定义清分比例  若过程中修改返利比例，则此订单依然按照最初创建的比例清分 20151008 modify end
						
						// 定义升级所需社员数量
						int iUpgradeMembers = groupLevelInfo
								.getUpgradeMembers();
						// 定义清分深度
						int iDeepReckon = groupLevelInfo.getDeepReckon();
						// 定义活跃统计深度
						int iDeepConsume = groupLevelInfo.getDeepConsume();

						// 定义下一级别
						String sNextLevel = groupLevelInfo.getNextLevel();
						// 定义当前用户级别
						String sLevelCode = groupLevelInfo.getLevelCode();
						// 定义当前级别类型
						String sLevelType = groupLevelInfo.getLevelType();

						// 开始更新消费金额
						GcActiveLog gcActiveLog = new GcActiveLog();
						gcActiveLog.setAccountCode(accountRelation
								.getAccountCode());

						boolean bFlagDeepConsume = accountRelation.getDeep() <= iDeepConsume;

						// 判断如果能计算该深度 才加上消费金额 否则只记活跃人数
						if (bFlagDeepConsume) {
							gcActiveLog.setConsumeMoney(bConsumeMoney);
						} else {
							gcActiveLog.setConsumeMoney(BigDecimal.ZERO);
						}

						gcActiveLog.setRelationLevel(accountRelation.getDeep());
						gcActiveLog.setOrderAccountCode(sAccountCode);
						gcActiveLog.setOrderCode(gcReckonOrderInfo
								.getOrderCode());
						gcActiveLog.setActiveTime(gcReckonOrderInfo
								.getOrderFinishTime());

						gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
								.getStepCode()));
                        gcActiveLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
						// 定义增加的活跃数量 如果可以计算到该消费记录且关系度数大于0才加1
						int iAddMembers = (bFlagDeepConsume && accountRelation
								.getDeep() > 0) ? 1 : 0;

						listExec.add(upInfo(918515303, accountRelation
								.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString(), String
								.valueOf(iAddMembers)));

						GcActiveMonth gcActiveMonth = updateActiveCount(
								gcActiveLog, iAddMembers);

						// 定义清分日志数组
						List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

						// 定义当前消费金额
						BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();
						// 定义之前消费金额
						BigDecimal bLastReckon = gcActiveLog
								.getLastSumConsume();

						// 判断如果当月消费金额和社友人数超过级别限制时 开启差量升级模式
						if (gcActiveMonth.getSumConsume().compareTo(
								bUpgradeConsume) >= 0
								&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

							listExec.add(upInfo(918515302, accountRelation
									.getAccountCode(), groupLevelInfo
									.getLevelName(), gcActiveMonth
									.getSumConsume().toString(), String
									.valueOf(gcActiveMonth.getSumMember())));
							GcGroupLevelMapper gcGroupLevelMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcGroupLevelMapper");

							GcLevelLogMapper gcLevelLogMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");

							// 当能升级时 首先判断处理当前升級的等级差
							BigDecimal bReckonSave = bUpgradeConsume
									.subtract(bLastReckon);
							if (bReckonSave.compareTo(BigDecimal.ZERO) < 0) {
								bReckonSave = BigDecimal.ZERO;
							}
							GcReckonLog gcSaveLog = new GcReckonLog();
							gcSaveLog.setScaleReckon(bScaleReckon);
							gcSaveLog.setReckonMoney(bReckonSave
									.multiply(gcSaveLog.getScaleReckon()));

							// 如果能清到该记录 则添加进list中
							if (iDeepReckon >= accountRelation.getDeep()
									&& gcSaveLog.getReckonMoney().compareTo(
											BigDecimal.ZERO) > 0) {
								listReckonLogs.add(gcSaveLog);
								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bReckonSave
										.toString(), bScaleReckon.toString(),
										gcSaveLog.getReckonMoney().toString()));

							}
							bNowReckon = bNowReckon.subtract(bReckonSave);

							// 循环触发升级模型
							while (gcActiveMonth.getSumConsume().compareTo(
									bUpgradeConsume) >= 0
									&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

								// 初始化实际存入值为0
								bReckonSave = BigDecimal.ZERO;

								GroupLevelInfo groupUpdateLevel = ReckonOrderSupport.INSTANCE
										.upLevelInfoForThird(sNextLevel, sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

								listExec.add(upInfo(918515305,
										accountRelation.getAccountCode(),
										groupUpdateLevel.getLevelName()));

								// 当历史金额小于级别升级所需金额时
								if (bLastReckon.compareTo(bUpgradeConsume) < 0) {
									// 定义级别所需要的金额
									BigDecimal bLevelDecimal = groupUpdateLevel
											.getUpgradeConsume().subtract(
													bUpgradeConsume);

									// 判断如果当前金额小于差量
									if (bNowReckon.compareTo(bLevelDecimal) <= 0) {
										bReckonSave = bNowReckon;
									} else {
										bReckonSave = bLevelDecimal;
									}

									bNowReckon = bNowReckon
											.subtract(bReckonSave);

								}

								// 级别变动日志
								GcLevelLog gcLevelLog = new GcLevelLog();
								gcLevelLog.setAccountCode(accountRelation
										.getAccountCode());
								gcLevelLog.setCreateTime(FormatHelper
										.upDateTime());

								gcLevelLog.setLastMemberLevel(sLevelCode);
								sLevelCode = groupUpdateLevel.getLevelCode();
								gcLevelLog.setCurrentMemberLevel(sLevelCode);
								gcLevelLog.setChangeType("4497465200080001");
								gcLevelLog.setUid(WebHelper.upUuid());
								// 插入级别变动日志表
								gcLevelLogMapper.insertSelective(gcLevelLog);

								// 开始定义清分日志
								GcReckonLog gcReckonLog = new GcReckonLog();
								
								// 清分规则调整内容 用户升级,则此订单依然按照最初创建的比例清分 20151008 delete Start 
								// 设置清分比例
//								bScaleReckon = groupUpdateLevel
//										.getScaleReckon();
								// 清分规则调整内容 用户升级,则此订单依然按照最初创建的比例清分  20151008 delete end
								
								// 设置清分深度
								iDeepReckon = groupUpdateLevel.getDeepReckon();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setReckonMoney(bReckonSave
												.multiply(gcReckonLog
														.getScaleReckon()));
								gcReckonLog.setChangeCodes(reckonStep
										.getStepCode());

								// 如果能清到该记录 则添加进list中
								if (iDeepReckon >= accountRelation.getDeep()
										&& gcReckonLog.getReckonMoney()
												.compareTo(BigDecimal.ZERO) > 0) {
									listReckonLogs.add(gcReckonLog);

									listExec.add(upInfo(918515304,
											accountRelation.getAccountCode(),
											bReckonSave.toString(),
											bScaleReckon.toString(),
											gcReckonLog.getReckonMoney()
													.toString()));

								}

								bUpgradeConsume = groupUpdateLevel
										.getUpgradeConsume();
								iUpgradeMembers = groupUpdateLevel
										.getUpgradeMembers();
								sNextLevel = groupUpdateLevel.getNextLevel();

							}

							// 循环升级完成后 设置更新信息
							// 定义更新的微公社账户信息
							GcGroupAccount gcUpdateAccount = new GcGroupAccount();
							gcUpdateAccount.setAccountLevel(sLevelCode);
							gcUpdateAccount.setScaleReckon(bScaleReckon);
							gcUpdateAccount.setLevelChangeTime(FormatHelper
									.upDateTime());
							gcUpdateAccount.setLevelType(sLevelType);
							gcGroupAccountMapper.updateByExampleSelective(
									gcUpdateAccount, gcGroupAccountExample);

						}

						// 判断如果能清分到级别 再次添加信息
						if (accountRelation.getDeep() <= iDeepReckon
								&& bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
							
							//清分金额必须>0
							BigDecimal reckonMoney = (bNowReckon.multiply(bScaleReckon)).setScale(2,BigDecimal.ROUND_DOWN);//清分金额保留到分,低于1分钱不在清分
							if(reckonMoney.compareTo(BigDecimal.ZERO)>0){
								GcReckonLog gcReckonLog = new GcReckonLog();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setChangeCodes(reckonStep.getStepCode());
//								gcReckonLog.setReckonMoney(bNowReckon
//										.multiply(gcReckonLog.getScaleReckon()));
								gcReckonLog.setReckonMoney(reckonMoney);
								listReckonLogs.add(gcReckonLog);

								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bNowReckon.toString(),
										bScaleReckon.toString(), gcReckonLog
												.getReckonMoney().toString()));
							}


							// gcReckonLog.setChangeCodes("");
						}

						// 判断如果数量大于0
						if (listReckonLogs.size() > 0) {
							for (int i = 0, j = listReckonLogs.size(); i < j; i++) {
								listReckonLogs.get(i).setAccountCode(
										accountRelation.getAccountCode());
								listReckonLogs.get(i).setOrderCode(
										gcReckonOrderInfo.getOrderCode());
								listReckonLogs.get(i).setRelationLevel(
										accountRelation.getDeep());
								listReckonLogs.get(i).setOrderAccountCode(
										gcReckonOrderInfo.getAccountCode());
								listReckonLogs.get(i).setOrderReckonTime(
										gcReckonOrderInfo.getOrderFinishTime());
								listReckonLogs.get(i).setReckonChangeType(
										"4497465200030001");
								listReckonLogs.get(i).setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
								listReckonLogs.get(i).setDetailCode(gcReckonOrderDetail.getDetailCode());
							}

							txGroupAccountService.updateAccount(listReckonLogs,
									null);

						}

					}
				}
			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向清分流程第五版代码，20160114修改 没有形成清分数据时记录原因
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doReckonInFifth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();
		
		List<String> listExec = new ArrayList<String>();
		listExec.add("reckonInFifth");
		
		//校验是否存在执行成功的预返利流程,没有返回错误
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type ='4497465200050003' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805143, reckonStep.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
			listExec.add(upInfo(915805143, reckonStep.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_IN));
		}
		
		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());
		//因为订单状态同步时会出现订单签收时间为空的情况(原因待查)，所以再强制校验一次
		if(StringUtils.isEmpty(gcReckonOrderInfo.getOrderFinishTime())){
			listExec.add(upInfo(918515313));
			mWebResult.setResultCode(918515313);
		}
		
		// 获取订单明细
		List<GcReckonOrderDetail> gcReckonOrderDetails = upGcReckonOrderDetail(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&appMap.get("trade_code")!=null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		//商户不存在或者状态不可用，不返利
		if(mWebResult.upFlagTrue()){
			if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
				listExec.add(upInfo(918512020, sManageCode));
				mWebResult.setResultCode(918512020);
			}
		}
		
		//订单创建时间>停止返利时间,并且商户状态是停用状态,则此订单不再给用户清分
		if (mWebResult.upFlagTrue() && StringUtils.isNotBlank(gcReckonOrderInfo.getOrderCreateTime())){
			String stopSql = "select trader_status,update_time from gc_trader_status_log where trader_code =:traderCode and update_time <=:orderCreateTime order by update_time desc,zid desc ";
			MDataMap stopMap = new MDataMap();
			stopMap.put("traderCode", appMap.get("trade_code"));
			stopMap.put("orderCreateTime", gcReckonOrderInfo.getOrderCreateTime());
			List<Map<String, Object>> stopList = DbUp.upTable("gc_trader_status_log").dataSqlList(stopSql, stopMap);
			if(stopList != null && stopList.size() > 0){
				String tStatus = String.valueOf(stopList.get(0).get("trader_status"));
				//订单创建时，商户已经是停用状态
				if("4497472500010002".equals(tStatus)){
					listExec.add(upInfo(918512022, sManageCode));
					mWebResult.inErrorMessage(918512022,sManageCode);
				}
			}
		}
		
		if (mWebResult.upFlagTrue()) {

			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {
				
				// 循环所有明细
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					//没有形成清分数据的原因标记
					String noReckonReasonFlag="";
					//最终是否形成清分数据的标价
					boolean noReckonDataFlag=false;//默认未生成清分数据
					
					// 只有明细上可清分并且清分金额大于0 才开始清分
					if (gcReckonOrderDetail.getFlagReckon() == 1
							&& gcReckonOrderDetail.getSumReckonMoney().compareTo(
									BigDecimal.ZERO) > 0) {
						
						BigDecimal bConsumeMoney = gcReckonOrderDetail
								.getSumReckonMoney();
						
						String sSkuCode=gcReckonOrderDetail.getSkuCode();
						if(StringUtils.isBlank(sSkuCode)){
							sSkuCode=gcReckonOrderDetail.getProductCode();
						}
						
						listExec.add(upInfo(918515308,
								accountRelation.getAccountCode(),
								String.valueOf(accountRelation.getDeep())));

						GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
						gcGroupAccountExample.createCriteria()
								.andAccountCodeEqualTo(
										accountRelation.getAccountCode());

						List<GcGroupAccount> listAccounts = gcGroupAccountMapper
								.selectByExample(gcGroupAccountExample);

						// 定义当前微公社账户信息
						GcGroupAccount gcGroupAccount = listAccounts.get(0);

						// 开始获取当前级别信息的缓存信息
						// MDataMap mNowLevelInfo =
						// WebTemp.upTempDataMap("gc_group_level", "",
						// "level_code",gcGroupAccount.getAccountLevel());
						GroupLevelInfo groupLevelInfo = ReckonOrderSupport.INSTANCE
								.upLevelInfoForThird(gcGroupAccount.getAccountLevel(),
										sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

						// 定义升级所需消费金额
						BigDecimal bUpgradeConsume = groupLevelInfo
								.getUpgradeConsume();
						// 定义清分比例  若过程中修改返利比例，则此订单依然按照最初创建的比例清分 20151008 modify start
//						BigDecimal bScaleReckon = groupLevelInfo
//								.getScaleReckon();
						BigDecimal bScaleReckon = null;
						//获取预返利的返利比例
						GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
						gcRebateLogExample.createCriteria()
								.andAccountCodeEqualTo(accountRelation.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode())
								.andRebateChangeTypeEqualTo("4497465200140001").andDetailCodeEqualTo(gcReckonOrderDetail.getDetailCode()).andFlagWithdrawEqualTo(1).andFlagStatusEqualTo(1);
						List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
						if(listRebateLogs != null && listRebateLogs.size() > 0){
							bScaleReckon = listRebateLogs.get(0).getScaleReckon();
						}
						//没有获取到预返利的返利比例,继续下一条数据
						if(bScaleReckon == null){
							noReckonReasonFlag = "1";//没有找到预返利原因标记
							//不会形成清分数据,记录原因
							listExec.add(upInfo(918515310, accountRelation.getAccountCode(),gcReckonOrderDetail.getDetailCode()));
							continue;
						}
						
						// 定义清分比例  若过程中修改返利比例，则此订单依然按照最初创建的比例清分 20151008 modify end
						
						// 定义升级所需社员数量
						int iUpgradeMembers = groupLevelInfo
								.getUpgradeMembers();
						// 定义清分深度
						int iDeepReckon = groupLevelInfo.getDeepReckon();
						// 定义活跃统计深度
						int iDeepConsume = groupLevelInfo.getDeepConsume();

						// 定义下一级别
						String sNextLevel = groupLevelInfo.getNextLevel();
						// 定义当前用户级别
						String sLevelCode = groupLevelInfo.getLevelCode();
						// 定义当前级别类型
						String sLevelType = groupLevelInfo.getLevelType();

						// 开始更新消费金额
						GcActiveLog gcActiveLog = new GcActiveLog();
						gcActiveLog.setAccountCode(accountRelation
								.getAccountCode());

						boolean bFlagDeepConsume = accountRelation.getDeep() <= iDeepConsume;

						// 判断如果能计算该深度 才加上消费金额 否则只记活跃人数
						if (bFlagDeepConsume) {
							gcActiveLog.setConsumeMoney(bConsumeMoney);
						} else {
							gcActiveLog.setConsumeMoney(BigDecimal.ZERO);
						}

						gcActiveLog.setRelationLevel(accountRelation.getDeep());
						gcActiveLog.setOrderAccountCode(sAccountCode);
						gcActiveLog.setOrderCode(gcReckonOrderInfo
								.getOrderCode());
						gcActiveLog.setActiveTime(gcReckonOrderInfo
								.getOrderFinishTime());

						gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
								.getStepCode()));
                        gcActiveLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
						// 定义增加的活跃数量 如果可以计算到该消费记录且关系度数大于0才加1
						int iAddMembers = (bFlagDeepConsume && accountRelation
								.getDeep() > 0) ? 1 : 0;

						listExec.add(upInfo(918515303, accountRelation
								.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString(), String
								.valueOf(iAddMembers)));

						GcActiveMonth gcActiveMonth = updateActiveCount(
								gcActiveLog, iAddMembers);

						// 定义清分日志数组
						List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

						// 定义当前消费金额
						BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();
						// 定义之前消费金额
						BigDecimal bLastReckon = gcActiveLog
								.getLastSumConsume();

						// 判断如果当月消费金额和社友人数超过级别限制时 开启差量升级模式
						if (gcActiveMonth.getSumConsume().compareTo(
								bUpgradeConsume) >= 0
								&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

							listExec.add(upInfo(918515302, accountRelation
									.getAccountCode(), groupLevelInfo
									.getLevelName(), gcActiveMonth
									.getSumConsume().toString(), String
									.valueOf(gcActiveMonth.getSumMember())));
							GcGroupLevelMapper gcGroupLevelMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcGroupLevelMapper");

							GcLevelLogMapper gcLevelLogMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcLevelLogMapper");

							// 当能升级时 首先判断处理当前升級的等级差
							BigDecimal bReckonSave = bUpgradeConsume
									.subtract(bLastReckon);
							if (bReckonSave.compareTo(BigDecimal.ZERO) < 0) {
								bReckonSave = BigDecimal.ZERO;
							}
							GcReckonLog gcSaveLog = new GcReckonLog();
							gcSaveLog.setScaleReckon(bScaleReckon);
							gcSaveLog.setReckonMoney(bReckonSave
									.multiply(gcSaveLog.getScaleReckon()));

							// 如果能清到该记录 则添加进list中
							if (iDeepReckon >= accountRelation.getDeep()
									&& gcSaveLog.getReckonMoney().compareTo(
											BigDecimal.ZERO) > 0) {
								listReckonLogs.add(gcSaveLog);
								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bReckonSave
										.toString(), bScaleReckon.toString(),
										gcSaveLog.getReckonMoney().toString()));

							}
							bNowReckon = bNowReckon.subtract(bReckonSave);

							// 循环触发升级模型
							while (gcActiveMonth.getSumConsume().compareTo(
									bUpgradeConsume) >= 0
									&& gcActiveMonth.getSumMember() >= iUpgradeMembers) {

								// 初始化实际存入值为0
								bReckonSave = BigDecimal.ZERO;

								GroupLevelInfo groupUpdateLevel = ReckonOrderSupport.INSTANCE
										.upLevelInfoForThird(sNextLevel, sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));

								listExec.add(upInfo(918515305,
										accountRelation.getAccountCode(),
										groupUpdateLevel.getLevelName()));

								// 当历史金额小于级别升级所需金额时
								if (bLastReckon.compareTo(bUpgradeConsume) < 0) {
									// 定义级别所需要的金额
									BigDecimal bLevelDecimal = groupUpdateLevel
											.getUpgradeConsume().subtract(
													bUpgradeConsume);

									// 判断如果当前金额小于差量
									if (bNowReckon.compareTo(bLevelDecimal) <= 0) {
										bReckonSave = bNowReckon;
									} else {
										bReckonSave = bLevelDecimal;
									}

									bNowReckon = bNowReckon
											.subtract(bReckonSave);

								}

								// 级别变动日志
								GcLevelLog gcLevelLog = new GcLevelLog();
								gcLevelLog.setAccountCode(accountRelation
										.getAccountCode());
								gcLevelLog.setCreateTime(FormatHelper
										.upDateTime());

								gcLevelLog.setLastMemberLevel(sLevelCode);
								sLevelCode = groupUpdateLevel.getLevelCode();
								gcLevelLog.setCurrentMemberLevel(sLevelCode);
								gcLevelLog.setChangeType("4497465200080001");
								gcLevelLog.setUid(WebHelper.upUuid());
								// 插入级别变动日志表
								gcLevelLogMapper.insertSelective(gcLevelLog);

								// 开始定义清分日志
								GcReckonLog gcReckonLog = new GcReckonLog();
								
								// 清分规则调整内容 用户升级,则此订单依然按照最初创建的比例清分 20151008 delete Start 
								// 设置清分比例
//								bScaleReckon = groupUpdateLevel
//										.getScaleReckon();
								// 清分规则调整内容 用户升级,则此订单依然按照最初创建的比例清分  20151008 delete end
								
								// 设置清分深度
								iDeepReckon = groupUpdateLevel.getDeepReckon();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setReckonMoney(bReckonSave
												.multiply(gcReckonLog
														.getScaleReckon()));
								gcReckonLog.setChangeCodes(reckonStep
										.getStepCode());

								// 如果能清到该记录 则添加进list中
								if (iDeepReckon >= accountRelation.getDeep()
										&& gcReckonLog.getReckonMoney()
												.compareTo(BigDecimal.ZERO) > 0) {
									listReckonLogs.add(gcReckonLog);

									listExec.add(upInfo(918515304,
											accountRelation.getAccountCode(),
											bReckonSave.toString(),
											bScaleReckon.toString(),
											gcReckonLog.getReckonMoney()
													.toString()));

								}

								bUpgradeConsume = groupUpdateLevel
										.getUpgradeConsume();
								iUpgradeMembers = groupUpdateLevel
										.getUpgradeMembers();
								sNextLevel = groupUpdateLevel.getNextLevel();

							}

							// 循环升级完成后 设置更新信息
							// 定义更新的微公社账户信息
							GcGroupAccount gcUpdateAccount = new GcGroupAccount();
							gcUpdateAccount.setAccountLevel(sLevelCode);
							gcUpdateAccount.setScaleReckon(bScaleReckon);
							gcUpdateAccount.setLevelChangeTime(FormatHelper
									.upDateTime());
							gcUpdateAccount.setLevelType(sLevelType);
							gcGroupAccountMapper.updateByExampleSelective(
									gcUpdateAccount, gcGroupAccountExample);

						}

						// 判断如果能清分到级别 再次添加信息
						if (accountRelation.getDeep() <= iDeepReckon
								&& bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
							
							//清分金额必须>0
							BigDecimal reckonMoney = (bNowReckon.multiply(bScaleReckon)).setScale(2,BigDecimal.ROUND_DOWN);//清分金额保留到分,低于1分钱不在清分
							if(reckonMoney.compareTo(BigDecimal.ZERO)>0){
								GcReckonLog gcReckonLog = new GcReckonLog();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog
										.setChangeCodes(reckonStep.getStepCode());
//								gcReckonLog.setReckonMoney(bNowReckon
//										.multiply(gcReckonLog.getScaleReckon()));
								gcReckonLog.setReckonMoney(reckonMoney);
								listReckonLogs.add(gcReckonLog);

								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bNowReckon.toString(),
										bScaleReckon.toString(), gcReckonLog
												.getReckonMoney().toString()));
							}else{
								noReckonReasonFlag="3";//可清分金额*清分比例后的金额<=0
							}


							// gcReckonLog.setChangeCodes("");
						}else{
							noReckonReasonFlag="2";//不能清分到级别或是当前消费金额<=0
						}
						
						// 判断如果数量大于0
						if (listReckonLogs.size() > 0) {
							for (int i = 0, j = listReckonLogs.size(); i < j; i++) {
								noReckonDataFlag = true;//形成清分数据标记为真
								listReckonLogs.get(i).setAccountCode(
										accountRelation.getAccountCode());
								listReckonLogs.get(i).setOrderCode(
										gcReckonOrderInfo.getOrderCode());
								listReckonLogs.get(i).setRelationLevel(
										accountRelation.getDeep());
								listReckonLogs.get(i).setOrderAccountCode(
										gcReckonOrderInfo.getAccountCode());
								listReckonLogs.get(i).setOrderReckonTime(
										gcReckonOrderInfo.getOrderFinishTime());
								listReckonLogs.get(i).setReckonChangeType(
										"4497465200030001");
								listReckonLogs.get(i).setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
								listReckonLogs.get(i).setDetailCode(gcReckonOrderDetail.getDetailCode());
							}

							txGroupAccountService.updateAccount(listReckonLogs,
									null);

						}
						
						//如果没有形成清分数据,记录原因
						if(!noReckonDataFlag){
							if("2".equals(noReckonReasonFlag)){
								//不能清分到级别或是当前消费金额<=0
								listExec.add(upInfo(918515311, accountRelation.getAccountCode(),gcReckonOrderDetail.getDetailCode()));
							}else if("3".equals(noReckonReasonFlag)){
								//可清分金额*清分比例后的金额<=0
								listExec.add(upInfo(918515312, accountRelation.getAccountCode(),gcReckonOrderDetail.getDetailCode()));
							}
						}

					}else{
						//不能清分或是sku的清分金额<=0时,记录原因
						listExec.add(upInfo(918515309, accountRelation.getAccountCode(),gcReckonOrderDetail.getDetailCode()));
					}
				}
			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 逆向清分流程
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonBack(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		MWebResult mWebResult = new MWebResult();
		
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
			}
		}
		
		// 判断是否有执行成功的正向清分流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {

			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN) != 1) {
				mWebResult.inErrorMessage(918505137, reckonStep.getStepCode());
			}

		}
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
			}
		}

		if (mWebResult.upFlagTrue()) {

			// -------------------- 开始更新消费统计信息
			GcActiveLogMapper gcActiveLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
			GcActiveLogExample gcActiveLogExample = new GcActiveLogExample();
			// 查询条件设置为所有该订单引起的大于0的消费记录
			gcActiveLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andConsumeMoneyGreaterThan(BigDecimal.ZERO);

			List<GcActiveLog> listGcActiveLogs = gcActiveLogMapper
					.selectByExample(gcActiveLogExample);

			// 开始循环插入反记录的消费记录
			if (listGcActiveLogs != null && listGcActiveLogs.size() > 0) {

				for (GcActiveLog item : listGcActiveLogs) {

					GcActiveLog gcActiveLog = new GcActiveLog();

					gcActiveLog.setAccountCode(item.getAccountCode());
					gcActiveLog.setActiveTime(item.getActiveTime());
					gcActiveLog
							.setConsumeMoney(item.getConsumeMoney().negate());
					gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
					gcActiveLog.setOrderCode(item.getOrderCode());
					gcActiveLog.setRelationLevel(item.getRelationLevel());
					gcActiveLog.setManageCode(item.getManageCode());
					gcActiveLog.setChangeCodes(reckonStep.getStepCode());
					updateActiveCount(gcActiveLog, 0);
				}

			}

			// -------------------- 开始处理清分信息
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			gcReckonLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andReckonChangeTypeEqualTo("4497465200030001");

			List<GcReckonLog> listReckonLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listReckonLogs != null && listReckonLogs.size() > 0) {

				TxGroupAccountService txGroupAccountService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
				for (GcReckonLog item : listReckonLogs) {
					// 判断是否已转入可提现账户
					if (item.getFlagWithdraw().equals(0)) {
						// -------------------- 开始反向可提现账户记录
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(item.getAccountCode());
						gcWithdrawLog.setWithdrawChangeType("4497465200040003");
						gcWithdrawLog.setWithdrawMoney(item.getReckonMoney()
								.negate());
						gcWithdrawLog.setChangeCodes(FormatHelper.join(
								reckonStep.getStepCode(), item.getLogCode()));
						List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
						listWithdrawLogsUpdate.add(gcWithdrawLog);
                        
						//强校验一次
						if(DbUp.upTable("gc_withdraw_log").count("account_code",item.getAccountCode(),"withdraw_money",item.getReckonMoney().negate().toString(),
								"withdraw_change_type","4497465200040003","change_codes",FormatHelper.join(reckonStep.getStepCode(), item.getLogCode()))<1){
							txGroupAccountService.updateAccount(null,
									listWithdrawLogsUpdate);
							//将保证金加回
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							if(depositLogList!=null&&depositLogList.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcTraderDepositLog.getDeposit().negate());
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());

								addDepositLog.setDeposit(gcTraderDepositLog.getDeposit().negate());
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
							
						}
						

					} else {

						// -------------------- 开始反向清分账户记录

						GcReckonLog gcReckonLog = new GcReckonLog();
						gcReckonLog.setAccountCode(item.getAccountCode());
						gcReckonLog.setFlagWithdraw(0);
						gcReckonLog.setOrderAccountCode(item
								.getOrderAccountCode());
						gcReckonLog.setOrderCode(item.getOrderCode());
						gcReckonLog.setReckonChangeType("4497465200030002");
						gcReckonLog.setReckonMoney(BigDecimal.ZERO
								.subtract(item.getReckonMoney()));
						gcReckonLog.setRelationLevel(item.getRelationLevel());
						gcReckonLog.setScaleReckon(item.getScaleReckon());
						gcReckonLog.setChangeCodes(item.getLogCode());
						gcReckonLog.setOrderReckonTime(item
								.getOrderReckonTime());
						gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());

						List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
						listUpdateGcReckonLogs.add(gcReckonLog);
						txGroupAccountService.updateAccount(
								listUpdateGcReckonLogs, null);

					}

				}

				// 更新所有的可转入标记为否
				GcReckonLog gcUpdateReckonLog = new GcReckonLog();
				gcUpdateReckonLog.setFlagWithdraw(0);
				gcReckonLogMapper.updateByExampleSelective(gcUpdateReckonLog,
						gcReckonLogExample);

			}

		}
		
		if(mWebResult.upFlagTrue()){
			MDataMap upMap=new MDataMap();
			upMap.inAllValues("flag_success", "1","step_code", reckonStep.getStepCode());
			DbUp.upTable("gc_reckon_order_step").dataUpdate(upMap, "flag_success", "step_code");
		}

		return mWebResult;

	}

	/**
	 * 逆向清分流程 20150916 商户预存款退单增加前 增加是否已经退单增加的判断
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonBackForSecond(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultMessage("reckonbackSecond");
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
			}
		}
		
		// 判断是否有执行成功的正向清分流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {

			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN) != 1) {
				mWebResult.inErrorMessage(918505137, reckonStep.getStepCode());
			}

		}
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
			}
		}

		if (mWebResult.upFlagTrue()) {

			// -------------------- 开始更新消费统计信息
			GcActiveLogMapper gcActiveLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
			GcActiveLogExample gcActiveLogExample = new GcActiveLogExample();
			// 查询条件设置为所有该订单引起的大于0的消费记录
			gcActiveLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andConsumeMoneyGreaterThan(BigDecimal.ZERO);

			List<GcActiveLog> listGcActiveLogs = gcActiveLogMapper
					.selectByExample(gcActiveLogExample);

			// 开始循环插入反记录的消费记录
			if (listGcActiveLogs != null && listGcActiveLogs.size() > 0) {

				for (GcActiveLog item : listGcActiveLogs) {

					GcActiveLog gcActiveLog = new GcActiveLog();

					gcActiveLog.setAccountCode(item.getAccountCode());
					gcActiveLog.setActiveTime(item.getActiveTime());
					gcActiveLog
							.setConsumeMoney(item.getConsumeMoney().negate());
					gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
					gcActiveLog.setOrderCode(item.getOrderCode());
					gcActiveLog.setRelationLevel(item.getRelationLevel());
					gcActiveLog.setManageCode(item.getManageCode());
					gcActiveLog.setChangeCodes(reckonStep.getStepCode());
					updateActiveCount(gcActiveLog, 0);
				}

			}

			// -------------------- 开始处理清分信息
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			gcReckonLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andReckonChangeTypeEqualTo("4497465200030001");

			List<GcReckonLog> listReckonLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listReckonLogs != null && listReckonLogs.size() > 0) {

				TxGroupAccountService txGroupAccountService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
				for (GcReckonLog item : listReckonLogs) {
					// 判断是否已转入可提现账户
					if (item.getFlagWithdraw().equals(0)) {
						// -------------------- 开始反向可提现账户记录
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(item.getAccountCode());
						gcWithdrawLog.setWithdrawChangeType("4497465200040003");
						gcWithdrawLog.setWithdrawMoney(item.getReckonMoney()
								.negate());
						gcWithdrawLog.setChangeCodes(FormatHelper.join(
								reckonStep.getStepCode(), item.getLogCode()));
						List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
						listWithdrawLogsUpdate.add(gcWithdrawLog);
                        
						//强校验一次
						if(DbUp.upTable("gc_withdraw_log").count("account_code",item.getAccountCode(),"withdraw_money",item.getReckonMoney().negate().toString(),
								"withdraw_change_type","4497465200040003","change_codes",FormatHelper.join(reckonStep.getStepCode(), item.getLogCode()))<1){
							txGroupAccountService.updateAccount(null,
									listWithdrawLogsUpdate);
							//将保证金加回
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							if(depositLogList!=null&&depositLogList.size()>0){
								
								GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								
								//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重复退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
								GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
								checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
									andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
								List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
								if(checkDepositLogList != null && checkDepositLogList.size() >0){
									//已经退单增加了，不在处理
								}else{
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
									gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcTraderDepositLog.getDeposit().negate());
									gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
									gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
									gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
									
									//添加保证金订单日志
									GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
									addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
									addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
									addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
									addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());

									addDepositLog.setDeposit(gcTraderDepositLog.getDeposit().negate());
									addDepositLog.setDepositType("4497472500040002");//退单增加
									addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
								}
							}
							
						}
						

					} else {

						// -------------------- 开始反向清分账户记录

						GcReckonLog gcReckonLog = new GcReckonLog();
						gcReckonLog.setAccountCode(item.getAccountCode());
						gcReckonLog.setFlagWithdraw(0);
						gcReckonLog.setOrderAccountCode(item
								.getOrderAccountCode());
						gcReckonLog.setOrderCode(item.getOrderCode());
						gcReckonLog.setReckonChangeType("4497465200030002");
						gcReckonLog.setReckonMoney(BigDecimal.ZERO
								.subtract(item.getReckonMoney()));
						gcReckonLog.setRelationLevel(item.getRelationLevel());
						gcReckonLog.setScaleReckon(item.getScaleReckon());
						gcReckonLog.setChangeCodes(item.getLogCode());
						gcReckonLog.setOrderReckonTime(item
								.getOrderReckonTime());
						gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());

						List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
						listUpdateGcReckonLogs.add(gcReckonLog);
						txGroupAccountService.updateAccount(
								listUpdateGcReckonLogs, null);

					}

				}

				// 更新所有的可转入标记为否
				GcReckonLog gcUpdateReckonLog = new GcReckonLog();
				gcUpdateReckonLog.setFlagWithdraw(0);
				gcReckonLogMapper.updateByExampleSelective(gcUpdateReckonLog,
						gcReckonLogExample);

			}

		}
		
		if(mWebResult.upFlagTrue()){
			MDataMap upMap=new MDataMap();
			upMap.inAllValues("flag_success", "1","step_code", reckonStep.getStepCode());
			DbUp.upTable("gc_reckon_order_step").dataUpdate(upMap, "flag_success", "step_code");
		}

		return mWebResult;

	}
	
	/**
	 * 逆向清分流程第三版  20151009修改  增加退货服务时间的限制
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult reckonBackForThird(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult = new MWebResult();
		
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
				listExec.add(upInfo(918533009, reckonStep.getStepCode()));
			}
		}
		
		// 判断是否有执行成功的正向清分流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {

			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN) != 1) {
				mWebResult.inErrorMessage(918505137, reckonStep.getStepCode());
				listExec.add(upInfo(918505137, reckonStep.getStepCode()));
			}

		}
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
				listExec.add(upInfo(918505134, reckonStep.getStepCode()));
			}
		}
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		//超出退货服务时间标记 false：未超出 true:超出
		boolean rebackFlag = false;
		//判断退货时间是否超出设置的服务时间范围
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			
			GcTraderInfo traderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			int returnGoodsDay = 0;
			if(traderInfo != null){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",traderInfo.getTraderCode());
				if(returnDayMap != null  && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}
			String returnDate = "";//交易成功时间+退货服务天数
			if(StringUtils.isNotBlank(orderFinishTime) && returnGoodsDay > 0){
				returnDate = DateUtil.toString(DateUtil.addDays(DateUtil.toDate(orderFinishTime, DateUtil.DATE_FORMAT_DATEONLY), returnGoodsDay), DateUtil.DATE_FORMAT_DATEONLY);
			}
			if(StringUtils.isNotBlank(returnDate)){
				String sysTime = FormatHelper.upDateTime();
				//超出退货服务时间
				if(DateUtil.compareTime(returnDate, sysTime, DateUtil.DATE_FORMAT_DATEONLY) < 0){
					rebackFlag = true;
					listExec.add(upInfo(918512021, orderFinishTime,String.valueOf(returnGoodsDay)));
					mWebResult.inErrorMessage(918512021,orderFinishTime,String.valueOf(returnGoodsDay));
				}
			}
		}
		
		//若用户在退货服务时间段退货则微公社扣除用户返利,同时增增加商户的预存款,若超出退货服务时间则不再扣除用户返利和增加商户预存款
		if (mWebResult.upFlagTrue() && !rebackFlag) {

			// -------------------- 开始更新消费统计信息
			GcActiveLogMapper gcActiveLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
			GcActiveLogExample gcActiveLogExample = new GcActiveLogExample();
			// 查询条件设置为所有该订单引起的大于0的消费记录
			gcActiveLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andConsumeMoneyGreaterThan(BigDecimal.ZERO);

			List<GcActiveLog> listGcActiveLogs = gcActiveLogMapper
					.selectByExample(gcActiveLogExample);

			// 开始循环插入反记录的消费记录
			if (listGcActiveLogs != null && listGcActiveLogs.size() > 0) {

				for (GcActiveLog item : listGcActiveLogs) {

					GcActiveLog gcActiveLog = new GcActiveLog();

					gcActiveLog.setAccountCode(item.getAccountCode());
					gcActiveLog.setActiveTime(item.getActiveTime());
					gcActiveLog
							.setConsumeMoney(item.getConsumeMoney().negate());
					gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
					gcActiveLog.setOrderCode(item.getOrderCode());
					gcActiveLog.setRelationLevel(item.getRelationLevel());
					gcActiveLog.setManageCode(item.getManageCode());
					gcActiveLog.setChangeCodes(reckonStep.getStepCode());
					updateActiveCount(gcActiveLog, 0);
				}

			}

			// -------------------- 开始处理清分信息
			GcReckonLogMapper gcReckonLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

			GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
			gcReckonLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andReckonChangeTypeEqualTo("4497465200030001");

			List<GcReckonLog> listReckonLogs = gcReckonLogMapper
					.selectByExample(gcReckonLogExample);
			if (listReckonLogs != null && listReckonLogs.size() > 0) {

				for (GcReckonLog item : listReckonLogs) {
					// 判断是否已转入可提现账户
					if (item.getFlagWithdraw().equals(0)) {
						// -------------------- 开始反向可提现账户记录
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(item.getAccountCode());
						gcWithdrawLog.setWithdrawChangeType("4497465200040003");
						gcWithdrawLog.setWithdrawMoney(item.getReckonMoney()
								.negate());
						gcWithdrawLog.setChangeCodes(FormatHelper.join(
								reckonStep.getStepCode(), item.getLogCode()));
						List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
						listWithdrawLogsUpdate.add(gcWithdrawLog);
                        
						//强校验一次
						if(DbUp.upTable("gc_withdraw_log").count("account_code",item.getAccountCode(),"withdraw_money",item.getReckonMoney().negate().toString(),
								"withdraw_change_type","4497465200040003","change_codes",FormatHelper.join(reckonStep.getStepCode(), item.getLogCode()))<1){
							
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
							//将保证金加回
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							if(depositLogList!=null&&depositLogList.size()>0){
								
								GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								
								//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重复退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
								GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
								checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
									andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
								List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
								if(checkDepositLogList != null && checkDepositLogList.size() >0){
									//已经退单增加了，不在处理
								}else{
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
									gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcTraderDepositLog.getDeposit().negate());
									gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
									gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
									gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
									
									//添加保证金订单日志
									GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
									addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
									addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
									addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
									addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());

									addDepositLog.setDeposit(gcTraderDepositLog.getDeposit().negate());
									addDepositLog.setDepositType("4497472500040002");//退单增加
									addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
								}
							}
						}

					} else {

						// -------------------- 开始反向清分账户记录

						GcReckonLog gcReckonLog = new GcReckonLog();
						gcReckonLog.setAccountCode(item.getAccountCode());
						gcReckonLog.setFlagWithdraw(0);
						gcReckonLog.setOrderAccountCode(item
								.getOrderAccountCode());
						gcReckonLog.setOrderCode(item.getOrderCode());
						gcReckonLog.setReckonChangeType("4497465200030002");
						gcReckonLog.setReckonMoney(BigDecimal.ZERO
								.subtract(item.getReckonMoney()));
						gcReckonLog.setRelationLevel(item.getRelationLevel());
						gcReckonLog.setScaleReckon(item.getScaleReckon());
						gcReckonLog.setChangeCodes(item.getLogCode());
						gcReckonLog.setOrderReckonTime(item
								.getOrderReckonTime());
						gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());

						List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
						listUpdateGcReckonLogs.add(gcReckonLog);
						txGroupAccountService.updateAccount(
								listUpdateGcReckonLogs, null);

					}

				}

				// 更新所有的可转入标记为否
				GcReckonLog gcUpdateReckonLog = new GcReckonLog();
				gcUpdateReckonLog.setFlagWithdraw(0);
				gcReckonLogMapper.updateByExampleSelective(gcUpdateReckonLog,
						gcReckonLogExample);

			}

		}
		
		if(mWebResult.upFlagTrue()){
			MDataMap upMap=new MDataMap();
			upMap.inAllValues("flag_success", "1","step_code", reckonStep.getStepCode());
			DbUp.upTable("gc_reckon_order_step").dataUpdate(upMap, "flag_success", "step_code");
		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;

	}
	
	/**
	 * 逆向清分流程第四版  20151126修改  增加退款账户日志
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doReckonBackForFourth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult = new MWebResult();
		
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
				listExec.add(upInfo(918533009, reckonStep.getStepCode()));
			}
		}
		
		// 判断是否有执行成功的正向清分流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {

			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN) != 1) {
				mWebResult.inErrorMessage(918505137, reckonStep.getStepCode());
				listExec.add(upInfo(918505137, reckonStep.getStepCode()));
			}

		}
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
				listExec.add(upInfo(918505134, reckonStep.getStepCode()));
			}
		}
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		//超出退货服务时间标记 false：未超出 true:超出
		boolean rebackFlag = false;
		//判断退货时间是否超出设置的服务时间范围
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			
			GcTraderInfo traderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			int returnGoodsDay = 0;
			if(traderInfo != null && StringUtils.isNotBlank(traderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",traderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}
			String returnDate = "";//交易成功时间+退货服务天数
			if(StringUtils.isNotBlank(orderFinishTime) && returnGoodsDay > 0){
				returnDate = DateUtil.toString(DateUtil.addDays(DateUtil.toDate(orderFinishTime, DateUtil.DATE_FORMAT_DATEONLY), returnGoodsDay), DateUtil.DATE_FORMAT_DATEONLY);
			}
			if(StringUtils.isNotBlank(returnDate)){
				String sysTime = FormatHelper.upDateTime();
				//超出退货服务时间
				if(DateUtil.compareTime(returnDate, sysTime, DateUtil.DATE_FORMAT_DATEONLY) < 0){
					rebackFlag = true;
					listExec.add(upInfo(918512021, orderFinishTime,String.valueOf(returnGoodsDay)));
					mWebResult.inErrorMessage(918512021,orderFinishTime,String.valueOf(returnGoodsDay));
				}
			}
		}
		
		//清分信息
		GcReckonLogMapper gcReckonLogMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");

		GcReckonLogExample gcReckonLogExample = new GcReckonLogExample();
		gcReckonLogExample.createCriteria()
				.andOrderCodeEqualTo(reckonStep.getOrderCode())
				.andReckonChangeTypeEqualTo("4497465200030001");
		List<GcReckonLog> listReckonLogs = gcReckonLogMapper
				.selectByExample(gcReckonLogExample);
		//若用户在退货服务时间段退货则微公社扣除用户返利,同时增增加商户的预存款,若超出退货服务时间则不再扣除用户返利和增加商户预存款
		if (mWebResult.upFlagTrue() && !rebackFlag) {

			// -------------------- 开始更新消费统计信息
			GcActiveLogMapper gcActiveLogMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
			GcActiveLogExample gcActiveLogExample = new GcActiveLogExample();
			// 查询条件设置为所有该订单引起的大于0的消费记录
			gcActiveLogExample.createCriteria()
					.andOrderCodeEqualTo(reckonStep.getOrderCode())
					.andConsumeMoneyGreaterThan(BigDecimal.ZERO);

			List<GcActiveLog> listGcActiveLogs = gcActiveLogMapper
					.selectByExample(gcActiveLogExample);

			// 开始循环插入反记录的消费记录
			if (listGcActiveLogs != null && listGcActiveLogs.size() > 0) {

				for (GcActiveLog item : listGcActiveLogs) {

					GcActiveLog gcActiveLog = new GcActiveLog();

					gcActiveLog.setAccountCode(item.getAccountCode());
					gcActiveLog.setActiveTime(item.getActiveTime());
					gcActiveLog
							.setConsumeMoney(item.getConsumeMoney().negate());
					gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
					gcActiveLog.setOrderCode(item.getOrderCode());
					gcActiveLog.setRelationLevel(item.getRelationLevel());
					gcActiveLog.setManageCode(item.getManageCode());
					gcActiveLog.setChangeCodes(reckonStep.getStepCode());
					updateActiveCount(gcActiveLog, 0);
				}

			}

			// -------------------- 开始处理清分信息
			if (listReckonLogs != null && listReckonLogs.size() > 0) {

				for (GcReckonLog item : listReckonLogs) {
					// 判断是否已转入可提现账户
					if (item.getFlagWithdraw().equals(0)) {
						// -------------------- 开始反向可提现账户记录
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(item.getAccountCode());
						gcWithdrawLog.setWithdrawChangeType("4497465200040003");
						gcWithdrawLog.setWithdrawMoney(item.getReckonMoney()
								.negate());
						gcWithdrawLog.setChangeCodes(FormatHelper.join(
								reckonStep.getStepCode(), item.getLogCode()));
						List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
						listWithdrawLogsUpdate.add(gcWithdrawLog);
                        
						//强校验一次
						if(DbUp.upTable("gc_withdraw_log").count("account_code",item.getAccountCode(),"withdraw_money",item.getReckonMoney().negate().toString(),
								"withdraw_change_type","4497465200040003","change_codes",FormatHelper.join(reckonStep.getStepCode(), item.getLogCode()))<1){
							
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
							//将保证金加回
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							if(depositLogList!=null&&depositLogList.size()>0){
								
								GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								
								//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重复退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
								GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
								checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
									andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
								List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
								if(checkDepositLogList != null && checkDepositLogList.size() >0){
									//已经退单增加了，不在处理
								}else{
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
									gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcTraderDepositLog.getDeposit().negate());
									gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
									gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
									gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
									
									//添加保证金订单日志
									GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
									addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
									addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
									addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
									addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
									addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());

									addDepositLog.setDeposit(gcTraderDepositLog.getDeposit().negate());
									addDepositLog.setDepositType("4497472500040002");//退单增加
									addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
								}
							}
						}

					} else {

						// -------------------- 开始反向清分账户记录

						GcReckonLog gcReckonLog = new GcReckonLog();
						gcReckonLog.setAccountCode(item.getAccountCode());
						gcReckonLog.setFlagWithdraw(0);
						gcReckonLog.setOrderAccountCode(item
								.getOrderAccountCode());
						gcReckonLog.setOrderCode(item.getOrderCode());
						gcReckonLog.setReckonChangeType("4497465200030002");
						gcReckonLog.setReckonMoney(BigDecimal.ZERO
								.subtract(item.getReckonMoney()));
						gcReckonLog.setRelationLevel(item.getRelationLevel());
						gcReckonLog.setScaleReckon(item.getScaleReckon());
						gcReckonLog.setChangeCodes(item.getLogCode());
						gcReckonLog.setOrderReckonTime(item
								.getOrderReckonTime());
						gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());

						List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
						listUpdateGcReckonLogs.add(gcReckonLog);
						txGroupAccountService.updateAccount(
								listUpdateGcReckonLogs, null);

					}

				}

				// 更新所有的可转入标记为否
				GcReckonLog gcUpdateReckonLog = new GcReckonLog();
				gcUpdateReckonLog.setFlagWithdraw(0);
				gcReckonLogMapper.updateByExampleSelective(gcUpdateReckonLog,
						gcReckonLogExample);

			}

		}
		
		if(mWebResult.upFlagTrue()){
			MDataMap upMap=new MDataMap();
			upMap.inAllValues("flag_success", "1","step_code", reckonStep.getStepCode());
			DbUp.upTable("gc_reckon_order_step").dataUpdate(upMap, "flag_success", "step_code");
		}
		
		//记录账户退款日志
		if (mWebResult.upFlagTrue() || rebackFlag){
			insertAccountRefundLog(listReckonLogs,reckonStep.getOrderCode(),rebackFlag);
		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;

	}
	
	/**
	 * 逆向清分是增加账户退款日志
	 * @param listReckonLogs
	 * @param reckonStepOrder
	 * @param rebackFlag
	 */
	private void insertAccountRefundLog(List<GcReckonLog> listReckonLogs,String reckonStepOrder,boolean rebackFlag) {
		
		if (listReckonLogs != null && listReckonLogs.size() > 0) {
			String outOrderCode="";//外部订单编号
			String orderCode = reckonStepOrder;//清分订单编号
			String refundTime = DateUtil.getSysDateTimeString();//退款时间
			MDataMap outMap= DbUp.upTable("gc_reckon_order_info").one("order_code",orderCode);
			if(outMap != null){
				outOrderCode = outMap.get("out_order_code");
			}
			
			Map<String, BigDecimal> acMoneyMap = new HashMap<String, BigDecimal>();
			for (GcReckonLog item : listReckonLogs) {
				//判断是否已转入可提现账户,已经转入可提现账户的才记录退款日志
				if (item.getFlagWithdraw().equals(0)) {
					String account = item.getAccountCode();
					BigDecimal reckonMoney = item.getReckonMoney();
					if(!acMoneyMap.containsKey(account)){
						acMoneyMap.put(account, reckonMoney);
					}else{
						BigDecimal acMoney = acMoneyMap.get(account);//账户已有的钱
						BigDecimal addMoney=reckonMoney.add(acMoney);//累计金额
						acMoneyMap.remove(account);//清除账户已有的金额
						acMoneyMap.put(account, addMoney);//重新记录账户累计的金额
					}
				}
			}
			//开始统计每个账户的退款信息
			Iterator<Entry<String, BigDecimal>> iter = acMoneyMap.entrySet().iterator();
			while(iter.hasNext()){
				MDataMap inMap = new MDataMap();
				String refundStatus = "4497465200250001";//退款状态,默认是成功
				String refundDes = "4497465200260001";//退款记录,默认是正常退款
				
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();//account
				Object val = entry.getValue();//money
				String accountCode = String.valueOf(key);//账户编号
				BigDecimal accountBalance = BigDecimal.ZERO;//账户余额
				BigDecimal refundMoney = new BigDecimal(String.valueOf(val)).setScale(2, BigDecimal.ROUND_HALF_UP);//退款金额,保留2位小数
				//获取账户余额
				MDataMap balanceMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
				if(balanceMap != null ){
					accountBalance = new BigDecimal(String.valueOf(balanceMap.get("account_withdraw_money")));
				}
				if(accountBalance.compareTo(BigDecimal.ZERO) < 0){
					refundStatus = "4497465200250001";//余额不足时,退款状态为成功
					refundDes = "4497465200260003";//余额不足时,退款记录为余额不足
				}
				if(rebackFlag){
					refundStatus = "4497465200250002";//退款超时，退款状态为失败
					refundDes = "4497465200260002";//退款超时，退款记录为退款超时
				}
				inMap.put("account_code", accountCode);
				inMap.put("out_order_code", outOrderCode);
				inMap.put("order_code", orderCode);
				if(refundMoney.compareTo(BigDecimal.ZERO) > 0){
					inMap.put("refund_money", refundMoney.negate().toString());//格式以“-”展示
				}else{
					inMap.put("refund_money", refundMoney.toString());
				}
				
				inMap.put("refund_status", refundStatus);
				inMap.put("refund_description", refundDes);
				inMap.put("account_balance", accountBalance.toString());
				inMap.put("refund_time", refundTime);
				DbUp.upTable("gc_account_refund_log").dataInsert(inMap);
			}
		}
	}

	/**
	 * 更新消费金额统计 并返回最新信息
	 * 
	 * @param gcActiveLog
	 * @param iAddMembers
	 *            该参数表示增加的活跃账户数量
	 * @return
	 */
	public GcActiveMonth updateActiveCount(GcActiveLog gcActiveLog,
			int iAddMembers) {

		GcActiveLogMapper gcActiveLogMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");

		GcActiveMonthMapper gcActiveMonthMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcActiveMonthMapper");

		// 定义月份
		String sMonth = DateHelper.upMonth(gcActiveLog.getActiveTime());

		if (StringUtils.isEmpty(gcActiveLog.getLogCode())) {
			gcActiveLog.setLogCode(WebHelper.upCode("GCAL"));
		}

		// 定义编号
		String sActiveCode = gcActiveLog.getAccountCode()
				+ WebConst.CONST_SPLIT_DOWN + sMonth;

		GcActiveMonthExample gcActiveMonthExample = new GcActiveMonthExample();
		gcActiveMonthExample.createCriteria()
				.andAccountCodeEqualTo(gcActiveLog.getAccountCode())
				.andActiveMonthEqualTo(sMonth);

		List<GcActiveMonth> listMonths = gcActiveMonthMapper
				.selectByExample(gcActiveMonthExample);

		GcActiveMonth gcActiveMonth = new GcActiveMonth();

		// 如果存在则更新 不存在插入
		if (listMonths != null && listMonths.size() == 1) {

			gcActiveMonth.setSumMember(listMonths.get(0).getSumMember());

			// 判断如果传入的关联订单的账户编号不为空时 则开始判断两人的关系来判定是否增加活跃社友数
			if (StringUtils.isNotEmpty(gcActiveLog.getOrderAccountCode())) {
				GcActiveLogExample gcCurrentMonthActiveLogExample = new GcActiveLogExample();
				// 设置判断用户是否有关联关系
				gcCurrentMonthActiveLogExample
						.createCriteria()
						.andAccountCodeEqualTo(gcActiveLog.getAccountCode())
						.andOrderAccountCodeEqualTo(
								gcActiveLog.getOrderAccountCode())
						.andActiveTimeLike(
								sMonth + DataConst.CONST_DATA_SQL_LIKE);

				// 如果当月该两人没有关联关系 则更新用户的活跃社友数+1
				if (gcActiveLogMapper
						.countByExample(gcCurrentMonthActiveLogExample) == 0) {

					gcActiveMonth.setSumMember(listMonths.get(0).getSumMember()
							+ iAddMembers);

				}
			} else {
				gcActiveMonth.setSumMember(listMonths.get(0).getSumMember()
						+ iAddMembers);
			}

			gcActiveMonth.setSumConsume(listMonths.get(0).getSumConsume()
					.add(gcActiveLog.getConsumeMoney()));
			gcActiveMonth.setUpdateTime(FormatHelper.upDateTime());

			// 更新日志上的统计信息
			gcActiveLog.setLastSumConsume(listMonths.get(0).getSumConsume());
			gcActiveLog.setCurrentSumConsume(gcActiveMonth.getSumConsume());

			// 更新主表信息
			gcActiveMonthMapper.updateByExampleSelective(gcActiveMonth,
					gcActiveMonthExample);

		}
		// 插入月度消费统计表
		else {

			gcActiveMonth.setUid(WebHelper.upUuid());
			gcActiveMonth.setAccountCode(gcActiveLog.getAccountCode());
			gcActiveMonth.setActiveCode(WebHelper.upCode("GCAM"));
			gcActiveMonth.setUqcode(sActiveCode);
			gcActiveMonth.setActiveMonth(sMonth);
			gcActiveMonth.setCreateTime(FormatHelper.upDateTime());
			gcActiveMonth.setSumConsume(gcActiveLog.getConsumeMoney());
			gcActiveMonth.setSumMember(iAddMembers);

			gcActiveMonthMapper.insertSelective(gcActiveMonth);
			// 设置日志表上的当前消费
			gcActiveLog.setLastSumConsume(BigDecimal.ZERO);
			gcActiveLog.setCurrentSumConsume(gcActiveLog.getConsumeMoney());

		}

		gcActiveLog.setUid(WebHelper.upUuid());
		gcActiveLog.setCreateTime(FormatHelper.upDateTime());

		gcActiveLogMapper.insertSelective(gcActiveLog);

		return gcActiveMonth;

	}
	
	/**
	 * 第三方退货流程
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult thirdReckonBack(ReckonStep reckonStep,List<AccountRelation> listRelations) {
   
		MWebResult mWebResult=new MWebResult();
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		GcReckonOrderReturnDetailMapper gcReckonOrderReturnDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderReturnDetailMapper");
		String returnCode=reckonStep.getUqcode().substring(reckonStep.getUqcode().indexOf("_")+1);
		GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailExample=new GcReckonOrderReturnDetailExample();
		gcReckonOrderReturnDetailExample.createCriteria().andReturnCodeEqualTo(returnCode);
		List<GcReckonOrderReturnDetail> detailList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailExample);
		String orderCode=reckonStep.getOrderCode();
		
		if(detailList==null||detailList.size()<1){
			mWebResult.inErrorMessage(918533007);//没有详情
		}
		
		//是否已逆向清分
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				mWebResult.inErrorMessage(918533003,orderCode);
			}
		}
		
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcActiveLogMapper gcActiveLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		
		if(mWebResult.upFlagTrue()){
			for(GcReckonOrderReturnDetail gcReckonOrderReturnDetail:detailList){
				//具体详情
				GcReckonOrderDetailExample gcReckonOrderDetailExample=new GcReckonOrderDetailExample();
				gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
				GcReckonOrderDetail gcReckonOrderDetail= gcReckonOrderDetailMapper.selectByExample(gcReckonOrderDetailExample).get(0);
				
				// 开始处理相应逆向消费
			    GcActiveLogExample gcActiveLogExample=new GcActiveLogExample();
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog
								.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						updateActiveCount(gcActiveLog, 0);
					}

				}
				
				//开始处理相应逆向清分
				GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
				gcReckonLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001")
				.andReckonMoneyGreaterThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());

				List<GcReckonLog> reckonLogList = gcReckonLogMapper
						.selectByExample(gcReckonLogExample);
				if (reckonLogList != null && reckonLogList.size() > 0) {
		
					TxGroupAccountService txGroupAccountService = BeansHelper
							.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
					for (GcReckonLog item : reckonLogList) {
						BigDecimal backReckonMoney=item.getReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
								.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
						// 判断是否已转入可提现账户
						if (item.getFlagWithdraw().equals(0)) {
							// -------------------- 开始反向可提现账户记录
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setWithdrawChangeType("4497465200040003");
							gcWithdrawLog.setWithdrawMoney(backReckonMoney.negate());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(
									reckonStep.getStepCode(), item.getLogCode()));
							List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
							listWithdrawLogsUpdate.add(gcWithdrawLog);
		                    
							txGroupAccountService.updateAccount(null,
									listWithdrawLogsUpdate);
							//将保证金加回
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							if(depositLogList!=null&&depositLogList.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
								addDepositLog.setDeposit(backReckonMoney);
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
								
						} else {
		
							// -------------------- 开始反向清分账户记录
		
							GcReckonLog gcReckonLog = new GcReckonLog();
							gcReckonLog.setAccountCode(item.getAccountCode());
							gcReckonLog.setFlagWithdraw(1);
							gcReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcReckonLog.setOrderCode(item.getOrderCode());
							gcReckonLog.setReckonChangeType("4497465200030001");
							gcReckonLog.setReckonMoney(backReckonMoney.negate());
							gcReckonLog.setRelationLevel(item.getRelationLevel());
							gcReckonLog.setScaleReckon(item.getScaleReckon());
							gcReckonLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
		
							List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
							listUpdateGcReckonLogs.add(gcReckonLog);
							txGroupAccountService.updateAccount(
									listUpdateGcReckonLogs, null);
							
							//处理预返利数据
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140001");//类型为正向，金额为负
							gcRebateLog.setRebateMoney(backReckonMoney.negate());
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setRebateType("4497465200150001");
							gcRebateLog.setFlagStatus(1);
							List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
							listUpdateGcRebateLogs.add(gcRebateLog);
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
							
							//更新返利金额
							GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
							gcRebateOrderExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(item.getAccountCode());
							List<GcRebateOrder> rebateOrderList=gcRebateOrderMapper.selectByExample(gcRebateOrderExample);
							if(rebateOrderList!=null&&rebateOrderList.size()>0){
								GcRebateOrder gcRebateOrder=rebateOrderList.get(0);
								GcRebateOrder updateGcRebateOrder=new GcRebateOrder();
								updateGcRebateOrder.setRebateMoney(gcRebateOrder.getRebateMoney().add(backReckonMoney.negate()));
								gcRebateOrderMapper.updateByExampleSelective(updateGcRebateOrder, gcRebateOrderExample);
							}
						}
		
					}

			}
		}
			
		
		
	}
		return mWebResult;
}
	
	/**
	 * 第三方退货流程 20150902第二版 修改商户保证金逻辑
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult thirdReckonBackForSecond(ReckonStep reckonStep,List<AccountRelation> listRelations) {
   
		MWebResult mWebResult=new MWebResult();
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		GcReckonOrderReturnDetailMapper gcReckonOrderReturnDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderReturnDetailMapper");
		String returnCode=reckonStep.getUqcode().substring(reckonStep.getUqcode().indexOf("_")+1);
		GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailExample=new GcReckonOrderReturnDetailExample();
		gcReckonOrderReturnDetailExample.createCriteria().andReturnCodeEqualTo(returnCode);
		List<GcReckonOrderReturnDetail> detailList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailExample);
		String orderCode=reckonStep.getOrderCode();
		
		if(detailList==null||detailList.size()<1){
			mWebResult.inErrorMessage(918533007);//没有详情
		}
		
		//是否已逆向清分
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				mWebResult.inErrorMessage(918533003,orderCode);
			}
		}
		
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcActiveLogMapper gcActiveLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		
		if(mWebResult.upFlagTrue()){
			for(GcReckonOrderReturnDetail gcReckonOrderReturnDetail:detailList){
				//具体详情
				GcReckonOrderDetailExample gcReckonOrderDetailExample=new GcReckonOrderDetailExample();
				gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
				GcReckonOrderDetail gcReckonOrderDetail= gcReckonOrderDetailMapper.selectByExample(gcReckonOrderDetailExample).get(0);
				
				// 开始处理相应逆向消费
			    GcActiveLogExample gcActiveLogExample=new GcActiveLogExample();
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog
								.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						updateActiveCount(gcActiveLog, 0);
					}

				}
				
				//开始处理相应逆向清分
				GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
				gcReckonLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001")
				.andReckonMoneyGreaterThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());

				List<GcReckonLog> reckonLogList = gcReckonLogMapper
						.selectByExample(gcReckonLogExample);
				if (reckonLogList != null && reckonLogList.size() > 0) {
		
					TxGroupAccountService txGroupAccountService = BeansHelper
							.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
					for (GcReckonLog item : reckonLogList) {
						
						BigDecimal backReckonMoney=item.getReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
								.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
						
						//根据rebate的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcRebateLogExample gcRebateLogExample = new GcRebateLogExample();
						gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andDetailCodeEqualTo(item.getDetailCode())
							.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1);
						List<GcRebateLog> rebateLogList = gcRebateLogMapper.selectByExample(gcRebateLogExample);
						
						for(GcRebateLog gcRebateLog : rebateLogList){
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcRebateLog.getLogCode()).andDepositNotEqualTo(BigDecimal.ZERO).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogListRebate=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							
							//将保证金加回
							if(depositLogListRebate!=null&&depositLogListRebate.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogListRebate.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
								addDepositLog.setDeposit(backReckonMoney);
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
						}
						
						//为新版本上线后兼容老数据，根据reckonLog的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						
						gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						
						//将保证金加回
						if(depositLogList!=null&&depositLogList.size()>0){
							GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
							GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
							//加上对应的保证金金额
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
							gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
							gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
							addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
							addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
							addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
							addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
							addDepositLog.setDeposit(backReckonMoney);
							addDepositLog.setDepositType("4497472500040002");//退单增加
							addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
						    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
						}
						
						// 判断是否已转入可提现账户
						if (item.getFlagWithdraw().equals(0)) {
							// -------------------- 开始反向可提现账户记录
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setWithdrawChangeType("4497465200040003");
							gcWithdrawLog.setWithdrawMoney(backReckonMoney.negate());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(
									reckonStep.getStepCode(), item.getLogCode()));
							List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
							listWithdrawLogsUpdate.add(gcWithdrawLog);
		                    
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
						} else {
		
							// -------------------- 开始反向清分账户记录
		
							GcReckonLog gcReckonLog = new GcReckonLog();
							gcReckonLog.setAccountCode(item.getAccountCode());
							gcReckonLog.setFlagWithdraw(1);
							gcReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcReckonLog.setOrderCode(item.getOrderCode());
							gcReckonLog.setReckonChangeType("4497465200030001");
							gcReckonLog.setReckonMoney(backReckonMoney.negate());
							gcReckonLog.setRelationLevel(item.getRelationLevel());
							gcReckonLog.setScaleReckon(item.getScaleReckon());
							gcReckonLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
		
							List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
							listUpdateGcReckonLogs.add(gcReckonLog);
							txGroupAccountService.updateAccount(
									listUpdateGcReckonLogs, null);
							
							//处理预返利数据
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140001");//类型为正向，金额为负
							gcRebateLog.setRebateMoney(backReckonMoney.negate());
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setRebateType("4497465200150001");
							gcRebateLog.setFlagStatus(1);
							List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
							listUpdateGcRebateLogs.add(gcRebateLog);
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
							
							//更新返利金额
							GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
							gcRebateOrderExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(item.getAccountCode());
							List<GcRebateOrder> rebateOrderList=gcRebateOrderMapper.selectByExample(gcRebateOrderExample);
							if(rebateOrderList!=null&&rebateOrderList.size()>0){
								GcRebateOrder gcRebateOrder=rebateOrderList.get(0);
								GcRebateOrder updateGcRebateOrder=new GcRebateOrder();
								updateGcRebateOrder.setRebateMoney(gcRebateOrder.getRebateMoney().add(backReckonMoney.negate()));
								gcRebateOrderMapper.updateByExampleSelective(updateGcRebateOrder, gcRebateOrderExample);
							}
						}
		
					}
				}
			}
		}
		return mWebResult;
	}
	
	/**
	 * 第三方退货流程 第三版 20151009修改  增加退货服务时间的限制
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doThirdReckonBackForThird(ReckonStep reckonStep,List<AccountRelation> listRelations) {
   
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult=new MWebResult();
		listExec.add("ThirdReckonBackThird");
		//首先判断返利是否重置完毕。重置完毕才能执行退换货流程
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050005%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805141, reckonStep.getOrderCode());
			listExec.add(upInfo(915805141, reckonStep.getOrderCode()));
		}
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		GcReckonOrderReturnDetailMapper gcReckonOrderReturnDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderReturnDetailMapper");
		String returnCode=reckonStep.getUqcode().substring(reckonStep.getUqcode().indexOf("_")+1);
		GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailExample=new GcReckonOrderReturnDetailExample();
		gcReckonOrderReturnDetailExample.createCriteria().andReturnCodeEqualTo(returnCode);
		List<GcReckonOrderReturnDetail> detailList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailExample);
		String orderCode=reckonStep.getOrderCode();
		
		if(mWebResult.upFlagTrue()){
			if(detailList==null||detailList.size()<1){
				mWebResult.inErrorMessage(918533007);//没有详情
				listExec.add(upInfo(918533007));
			}
		}
		
		//是否已逆向清分
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				mWebResult.inErrorMessage(918533003,orderCode);
				listExec.add(upInfo(918533003, orderCode));
			}
		}
		
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcActiveLogMapper gcActiveLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		//判断退货时间是否超出设置的服务时间范围
		boolean rebackFlag = false;//超出退货服务时间标记 false：未超出 true:超出
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			GcTraderInfo traderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			int returnGoodsDay = 0;
			if(traderInfo != null && StringUtils.isNotBlank(traderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",traderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}
			String returnDate = "";//交易成功时间+退货服务天数
			if(StringUtils.isNotBlank(orderFinishTime) && returnGoodsDay > 0){
				returnDate = DateUtil.toString(DateUtil.addDays(DateUtil.toDate(orderFinishTime, DateUtil.DATE_FORMAT_DATEONLY), returnGoodsDay), DateUtil.DATE_FORMAT_DATEONLY);
			}
			if(StringUtils.isNotBlank(returnDate)){
				String sysTime = FormatHelper.upDateTime();
				//超出退货服务时间
				if(DateUtil.compareTime(returnDate, sysTime, DateUtil.DATE_FORMAT_DATEONLY) < 0){
					rebackFlag = true;
					listExec.add(upInfo(918512021, orderFinishTime,String.valueOf(returnGoodsDay)));
					mWebResult.inErrorMessage(918512021,orderFinishTime,String.valueOf(returnGoodsDay));
				}
			}
		}
		
		//若用户在退货服务时间段退货则微公社扣除用户返利,同时增增加商户的预存款,若超出退货服务时间则不再扣除用户返利和增加商户预存款
		if(mWebResult.upFlagTrue() && !rebackFlag){
			//记录账户退款金额用Map
			HashMap<String, BigDecimal> acReMap = new HashMap<String, BigDecimal>();
			
			for(GcReckonOrderReturnDetail gcReckonOrderReturnDetail:detailList){
				//具体详情
				GcReckonOrderDetailExample gcReckonOrderDetailExample=new GcReckonOrderDetailExample();
				gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
				GcReckonOrderDetail gcReckonOrderDetail= gcReckonOrderDetailMapper.selectByExample(gcReckonOrderDetailExample).get(0);
				
				// 开始处理相应逆向消费
			    GcActiveLogExample gcActiveLogExample=new GcActiveLogExample();
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog
								.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						updateActiveCount(gcActiveLog, 0);
					}

				}
				
				//开始处理相应逆向清分
				GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
				gcReckonLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001")
				.andReckonMoneyGreaterThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());

				List<GcReckonLog> reckonLogList = gcReckonLogMapper
						.selectByExample(gcReckonLogExample);
				if (reckonLogList != null && reckonLogList.size() > 0) {
					
					for (GcReckonLog item : reckonLogList) {
						//为了确保小数计算后的精度,除后保留10位小数,乘完后在获取2位小数
						BigDecimal backReckonMoney=(item.getReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),10,BigDecimal.ROUND_HALF_UP)
								.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()))).setScale(2, BigDecimal.ROUND_HALF_UP);
						//记录账户退款金额
						String aCode = item.getAccountCode();
						if(!acReMap.containsKey(aCode)){
							acReMap.put(aCode, backReckonMoney);
						}else{
							BigDecimal reMoney = BigDecimal.valueOf(Double.parseDouble(acReMap.get(aCode).toString()));
							BigDecimal newReMoney = reMoney.add(backReckonMoney);
							acReMap.put(aCode, newReMoney);
						}
						
						//根据rebate的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcRebateLogExample gcRebateLogExample = new GcRebateLogExample();
						gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andDetailCodeEqualTo(item.getDetailCode())
							.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1);
						List<GcRebateLog> rebateLogList = gcRebateLogMapper.selectByExample(gcRebateLogExample);
						
						for(GcRebateLog gcRebateLog : rebateLogList){
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcRebateLog.getLogCode()).andDepositNotEqualTo(BigDecimal.ZERO).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogListRebate=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							
							//将保证金加回
							if(depositLogListRebate!=null&&depositLogListRebate.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogListRebate.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
								addDepositLog.setDeposit(backReckonMoney);
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
						}
						
						//为新版本上线后兼容老数据，根据reckonLog的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						
						gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						
						//将保证金加回
						if(depositLogList!=null&&depositLogList.size()>0){
							GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
							GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
							//加上对应的保证金金额
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
							gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
							gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
							addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
							addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
							addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
							addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
							addDepositLog.setDeposit(backReckonMoney);
							addDepositLog.setDepositType("4497472500040002");//退单增加
							addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
						    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
						}
						
						// 判断是否已转入可提现账户
						if (item.getFlagWithdraw().equals(0)) {
							// -------------------- 开始反向可提现账户记录
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setWithdrawChangeType("4497465200040003");
							gcWithdrawLog.setWithdrawMoney(backReckonMoney.negate());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(
									reckonStep.getStepCode(), item.getLogCode()));
							List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
							listWithdrawLogsUpdate.add(gcWithdrawLog);
		                    
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
						} else {
		
							// -------------------- 开始反向清分账户记录
		
							GcReckonLog gcReckonLog = new GcReckonLog();
							gcReckonLog.setAccountCode(item.getAccountCode());
							gcReckonLog.setFlagWithdraw(1);
							gcReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcReckonLog.setOrderCode(item.getOrderCode());
							gcReckonLog.setReckonChangeType("4497465200030001");
							gcReckonLog.setReckonMoney(backReckonMoney.negate());
							gcReckonLog.setRelationLevel(item.getRelationLevel());
							gcReckonLog.setScaleReckon(item.getScaleReckon());
							gcReckonLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
		
							List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
							listUpdateGcReckonLogs.add(gcReckonLog);
							txGroupAccountService.updateAccount(
									listUpdateGcReckonLogs, null);
							
							//处理预返利数据
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140001");//类型为正向，金额为负
							gcRebateLog.setRebateMoney(backReckonMoney.negate());
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setRebateType("4497465200150001");
							gcRebateLog.setFlagStatus(1);
							List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
							listUpdateGcRebateLogs.add(gcRebateLog);
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
							
							//更新返利金额
							GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
							gcRebateOrderExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(item.getAccountCode());
							List<GcRebateOrder> rebateOrderList=gcRebateOrderMapper.selectByExample(gcRebateOrderExample);
							if(rebateOrderList!=null&&rebateOrderList.size()>0){
								GcRebateOrder gcRebateOrder=rebateOrderList.get(0);
								GcRebateOrder updateGcRebateOrder=new GcRebateOrder();
								updateGcRebateOrder.setRebateMoney(gcRebateOrder.getRebateMoney().add(backReckonMoney.negate()));
								gcRebateOrderMapper.updateByExampleSelective(updateGcRebateOrder, gcRebateOrderExample);
							}
						}
		
					}
				}
			}
			
			//推送消息
			if(mWebResult.upFlagTrue()){
				try{
					List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
					if(accountList!=null&&accountList.size()>0){
						for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
							BigDecimal reMoney=BigDecimal.ZERO;
							if(acReMap.containsKey(accountRelation.get("account_code"))){
								reMoney = acReMap.get(accountRelation.get("account_code"));
							}
							if(accountRelation.get("relation_level").equals("0")){
								relation="您已退货成功";
								push_range="449747220003";
							}
							else{
						    	MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
						    	if(accountMap!=null){
						    		relationCode=accountMap.get("member_code");
						    	}
						    	else{
						    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode());
						    		if(otherMap!=null){
						    			relationCode=otherMap.get("member_code");
						    		}
						    	}
						   
						    	Map<String, String> map=new HashMap<String, String>();
								map.put("member_code",relationCode );
								map.put("account_code_wo", accountRelation.get("account_code"));
								map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//								String nickName=NickNameHelper.getNickName(map);
								String nickName=NickNameHelper.checkToNickName(map);
						    	
						    	if(accountRelation.get("relation_level").equals("1")){
							    	relation="【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
							    	nickName=NickNameHelper.getFirstNickName(accountRelation.get("account_code"),gcReckonOrderInfo.getAccountCode());
							    	relation="【"+nickName+"】的好友退货成功";
							    	push_range="449747220002";
							    }
							}
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(accountRelation.get("account_code"));
							addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
							addSinglePushCommentInput.setType("44974720000400010002");
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle(relation);
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+reMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
						    addSinglePushCommentInput.setContent(content);
						    addSinglePushCommentInput.setRelationCode(relationCode);
							
							if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"c2aa54c704614b598b8d64376c8b653e\" "
									+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
									+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.get("account_code")))<1){
								addSinglePushCommentInput.setSendStatus("4497465000070001");
							}
							else{
								addSinglePushCommentInput.setSendStatus("4497465000070002");
							}
							SinglePushComment.addPushComment(addSinglePushCommentInput);
							
						}
					}
				}catch(Exception e){
					
				}
			}
			
		}
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;
	}
	
	/**
	 * 第三方退货流程 第四版 2015123修改  退货时通过荣云发单聊消息
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doThirdReckonBackForForth(ReckonStep reckonStep,List<AccountRelation> listRelations) {
   
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult=new MWebResult();
		listExec.add("ThirdReckonBackForth");
		//首先判断返利是否重置完毕。重置完毕才能执行退换货流程
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050005%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805141, reckonStep.getOrderCode());
			listExec.add(upInfo(915805141, reckonStep.getOrderCode()));
		}
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		GcReckonOrderReturnDetailMapper gcReckonOrderReturnDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderReturnDetailMapper");
		String returnCode=reckonStep.getUqcode().substring(reckonStep.getUqcode().indexOf("_")+1);
		GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailExample=new GcReckonOrderReturnDetailExample();
		gcReckonOrderReturnDetailExample.createCriteria().andReturnCodeEqualTo(returnCode);
		List<GcReckonOrderReturnDetail> detailList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailExample);
		String orderCode=reckonStep.getOrderCode();
		
		if(mWebResult.upFlagTrue()){
			if(detailList==null||detailList.size()<1){
				mWebResult.inErrorMessage(918533007);//没有详情
				listExec.add(upInfo(918533007));
			}
		}
		
		//是否已逆向清分
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				mWebResult.inErrorMessage(918533003,orderCode);
				listExec.add(upInfo(918533003, orderCode));
			}
		}
		
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcActiveLogMapper gcActiveLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		//判断退货时间是否超出设置的服务时间范围
		boolean rebackFlag = false;//超出退货服务时间标记 false：未超出 true:超出
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			GcTraderInfo traderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			int returnGoodsDay = 0;
			if(traderInfo != null && StringUtils.isNotBlank(traderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",traderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}
			String returnDate = "";//交易成功时间+退货服务天数
			if(StringUtils.isNotBlank(orderFinishTime) && returnGoodsDay > 0){
				returnDate = DateUtil.toString(DateUtil.addDays(DateUtil.toDate(orderFinishTime, DateUtil.DATE_FORMAT_DATEONLY), returnGoodsDay), DateUtil.DATE_FORMAT_DATEONLY);
			}
			if(StringUtils.isNotBlank(returnDate)){
				String sysTime = FormatHelper.upDateTime();
				//超出退货服务时间
				if(DateUtil.compareTime(returnDate, sysTime, DateUtil.DATE_FORMAT_DATEONLY) < 0){
					rebackFlag = true;
					listExec.add(upInfo(918512021, orderFinishTime,String.valueOf(returnGoodsDay)));
					mWebResult.inErrorMessage(918512021,orderFinishTime,String.valueOf(returnGoodsDay));
				}
			}
		}
		
		//若用户在退货服务时间段退货则微公社扣除用户返利,同时增增加商户的预存款,若超出退货服务时间则不再扣除用户返利和增加商户预存款
		if(mWebResult.upFlagTrue() && !rebackFlag){
			//记录账户退款金额用Map
			HashMap<String, BigDecimal> acReMap = new HashMap<String, BigDecimal>();
			
			for(GcReckonOrderReturnDetail gcReckonOrderReturnDetail:detailList){
				//具体详情
				GcReckonOrderDetailExample gcReckonOrderDetailExample=new GcReckonOrderDetailExample();
				gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
				GcReckonOrderDetail gcReckonOrderDetail= gcReckonOrderDetailMapper.selectByExample(gcReckonOrderDetailExample).get(0);
				
				// 开始处理相应逆向消费
			    GcActiveLogExample gcActiveLogExample=new GcActiveLogExample();
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog
								.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						updateActiveCount(gcActiveLog, 0);
					}

				}
				
				//开始处理相应逆向清分
				GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
				gcReckonLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001")
				.andReckonMoneyGreaterThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());

				List<GcReckonLog> reckonLogList = gcReckonLogMapper
						.selectByExample(gcReckonLogExample);
				if (reckonLogList != null && reckonLogList.size() > 0) {
					
					for (GcReckonLog item : reckonLogList) {
						//为了确保小数计算后的精度,除后保留10位小数,乘完后在获取2位小数
						BigDecimal backReckonMoney=(item.getReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),10,BigDecimal.ROUND_HALF_UP)
								.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()))).setScale(2, BigDecimal.ROUND_HALF_UP);
						//记录账户退款金额
						String aCode = item.getAccountCode();
						if(!acReMap.containsKey(aCode)){
							acReMap.put(aCode, backReckonMoney);
						}else{
							BigDecimal reMoney = BigDecimal.valueOf(Double.parseDouble(acReMap.get(aCode).toString()));
							BigDecimal newReMoney = reMoney.add(backReckonMoney);
							acReMap.put(aCode, newReMoney);
						}
						
						//根据rebate的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcRebateLogExample gcRebateLogExample = new GcRebateLogExample();
						gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andDetailCodeEqualTo(item.getDetailCode())
							.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1);
						List<GcRebateLog> rebateLogList = gcRebateLogMapper.selectByExample(gcRebateLogExample);
						
						for(GcRebateLog gcRebateLog : rebateLogList){
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcRebateLog.getLogCode()).andDepositNotEqualTo(BigDecimal.ZERO).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogListRebate=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							
							//将保证金加回
							if(depositLogListRebate!=null&&depositLogListRebate.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogListRebate.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
								addDepositLog.setDeposit(backReckonMoney);
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
						}
						
						//为新版本上线后兼容老数据，根据reckonLog的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						
						gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						
						//将保证金加回
						if(depositLogList!=null&&depositLogList.size()>0){
							GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
							GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
							//加上对应的保证金金额
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
							gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
							gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
							addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
							addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
							addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
							addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
							addDepositLog.setDeposit(backReckonMoney);
							addDepositLog.setDepositType("4497472500040002");//退单增加
							addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
						    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
						}
						
						// 判断是否已转入可提现账户
						if (item.getFlagWithdraw().equals(0)) {
							// -------------------- 开始反向可提现账户记录
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setWithdrawChangeType("4497465200040003");
							gcWithdrawLog.setWithdrawMoney(backReckonMoney.negate());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(
									reckonStep.getStepCode(), item.getLogCode()));
							List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
							listWithdrawLogsUpdate.add(gcWithdrawLog);
		                    
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
						} else {
		
							// -------------------- 开始反向清分账户记录
		
							GcReckonLog gcReckonLog = new GcReckonLog();
							gcReckonLog.setAccountCode(item.getAccountCode());
							gcReckonLog.setFlagWithdraw(1);
							gcReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcReckonLog.setOrderCode(item.getOrderCode());
							gcReckonLog.setReckonChangeType("4497465200030001");
							gcReckonLog.setReckonMoney(backReckonMoney.negate());
							gcReckonLog.setRelationLevel(item.getRelationLevel());
							gcReckonLog.setScaleReckon(item.getScaleReckon());
							gcReckonLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
		
							List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
							listUpdateGcReckonLogs.add(gcReckonLog);
							txGroupAccountService.updateAccount(
									listUpdateGcReckonLogs, null);
							
							//处理预返利数据
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140001");//类型为正向，金额为负
							gcRebateLog.setRebateMoney(backReckonMoney.negate());
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setRebateType("4497465200150001");
							gcRebateLog.setFlagStatus(1);
							List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
							listUpdateGcRebateLogs.add(gcRebateLog);
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
							
							//更新返利金额
							GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
							gcRebateOrderExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(item.getAccountCode());
							List<GcRebateOrder> rebateOrderList=gcRebateOrderMapper.selectByExample(gcRebateOrderExample);
							if(rebateOrderList!=null&&rebateOrderList.size()>0){
								GcRebateOrder gcRebateOrder=rebateOrderList.get(0);
								GcRebateOrder updateGcRebateOrder=new GcRebateOrder();
								updateGcRebateOrder.setRebateMoney(gcRebateOrder.getRebateMoney().add(backReckonMoney.negate()));
								gcRebateOrderMapper.updateByExampleSelective(updateGcRebateOrder, gcRebateOrderExample);
							}
						}
		
					}
				}
			}
			
			//推送消息
			if(mWebResult.upFlagTrue()){
				try{
					List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
					if(accountList!=null&&accountList.size()>0){
						for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
							BigDecimal reMoney=BigDecimal.ZERO;
							if(acReMap.containsKey(accountRelation.get("account_code"))){
								reMoney = acReMap.get(accountRelation.get("account_code"));
							}
							if(accountRelation.get("relation_level").equals("0")){
								relation="您已退货成功";
								push_range="449747220003";
							}
							else{
						    	MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
						    	if(accountMap!=null){
						    		relationCode=accountMap.get("member_code");
						    	}
						    	else{
						    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode());
						    		if(otherMap!=null){
						    			relationCode=otherMap.get("member_code");
						    		}
						    	}
						   
						    	Map<String, String> map=new HashMap<String, String>();
								map.put("member_code",relationCode );
								map.put("account_code_wo", accountRelation.get("account_code"));
								map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//								String nickName=NickNameHelper.getNickName(map);
								String nickName=NickNameHelper.checkToNickName(map);
						    	
						    	if(accountRelation.get("relation_level").equals("1")){
							    	relation="【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
							    	nickName=NickNameHelper.getFirstNickName(accountRelation.get("account_code"),gcReckonOrderInfo.getAccountCode());
							    	relation="【"+nickName+"】的好友退货成功";
							    	push_range="449747220002";
							    }
							}
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(accountRelation.get("account_code"));
							addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
							addSinglePushCommentInput.setType("44974720000400010002");
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle(relation);
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+reMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
						    addSinglePushCommentInput.setContent(content);
						    addSinglePushCommentInput.setRelationCode(relationCode);
							
							if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"c2aa54c704614b598b8d64376c8b653e\" "
									+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
									+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.get("account_code")))<1){
								addSinglePushCommentInput.setSendStatus("4497465000070001");
							}
							else{
								addSinglePushCommentInput.setSendStatus("4497465000070002");
							}
							SinglePushComment.addPushComment(addSinglePushCommentInput);
							
							//单聊时通过荣云发退货消息
							if(accountRelation.get("relation_level").equals("0")){
							}else{
						    	//一度好友下单时,给上级发消息
						    	if(accountRelation.get("relation_level").equals("1")){
						    		//我memberCode
						    		MDataMap meMemberMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
						    		//我的上级memberCode
						    		MDataMap oneMemberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    	
						    		if(meMemberMap!=null && oneMemberMap !=null){
							    		//通过消息队列发消息
										JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
												new MDataMap("fromUserId", meMemberMap.get("member_code"),"toUserId", oneMemberMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我申请退单了,下次在帮你赚钱吧！\"}"));
							    	}
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
							    	MDataMap oneMembMap = new MDataMap();
							    	//获取我的上级accountCode
							    	MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",gcReckonOrderInfo.getAccountCode(),"flag_enable","1");
							    	if(oneMap != null){
							    		String oneAcCode = oneMap.get("parent_code");
							    		//获取我的上级的memberCode
							    		oneMembMap=DbUp.upTable("mc_member_info").one("account_code",oneAcCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    	}
							    	//获取我上级的上级的memberCode
							    	MDataMap twoMembMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    	if(oneMembMap != null && twoMembMap !=null){
							    		//通过消息队列发消息
										JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
												new MDataMap("fromUserId", oneMembMap.get("member_code"),"toUserId", twoMembMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我的好友退单了,下次在帮你赚钱吧！\"}"));
							    	}
							    }
							}
						}
					}
				}catch(Exception e){
					
				}
			}
			
		}
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;
	}
	
	/**
	 * 第三方退货流程 第五版 20160111修改  修改返利金额不满足多次退货的问题
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doThirdReckonBackForFifth(ReckonStep reckonStep,List<AccountRelation> listRelations) {
   
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult=new MWebResult();
		listExec.add("ThirdReckonBackFifth");
		//首先判断返利是否重置完毕。重置完毕才能执行退换货流程
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050005%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805141, reckonStep.getOrderCode());
			listExec.add(upInfo(915805141, reckonStep.getOrderCode()));
		}
		
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		GcReckonOrderReturnDetailMapper gcReckonOrderReturnDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderReturnDetailMapper");
		String returnCode=reckonStep.getUqcode().substring(reckonStep.getUqcode().indexOf("_")+1);
		GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailExample=new GcReckonOrderReturnDetailExample();
		gcReckonOrderReturnDetailExample.createCriteria().andReturnCodeEqualTo(returnCode);
		List<GcReckonOrderReturnDetail> detailList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailExample);
		String orderCode=reckonStep.getOrderCode();
		
		if(mWebResult.upFlagTrue()){
			if(detailList==null||detailList.size()<1){
				mWebResult.inErrorMessage(918533007);//没有详情
				listExec.add(upInfo(918533007));
			}
		}
		
		//是否已逆向清分
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				mWebResult.inErrorMessage(918533003,orderCode);
				listExec.add(upInfo(918533003, orderCode));
			}
		}
		
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonLogMapper gcReckonLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcActiveLogMapper gcActiveLogMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		//判断退货时间是否超出设置的服务时间范围
		boolean rebackFlag = false;//超出退货服务时间标记 false：未超出 true:超出
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			GcTraderInfo traderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			int returnGoodsDay = 0;
			if(traderInfo != null && StringUtils.isNotBlank(traderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",traderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}
			String returnDate = "";//交易成功时间+退货服务天数
			if(StringUtils.isNotBlank(orderFinishTime) && returnGoodsDay > 0){
				returnDate = DateUtil.toString(DateUtil.addDays(DateUtil.toDate(orderFinishTime, DateUtil.DATE_FORMAT_DATEONLY), returnGoodsDay), DateUtil.DATE_FORMAT_DATEONLY);
			}
			if(StringUtils.isNotBlank(returnDate)){
				String sysTime = FormatHelper.upDateTime();
				//超出退货服务时间
				if(DateUtil.compareTime(returnDate, sysTime, DateUtil.DATE_FORMAT_DATEONLY) < 0){
					rebackFlag = true;
					listExec.add(upInfo(918512021, orderFinishTime,String.valueOf(returnGoodsDay)));
					mWebResult.inErrorMessage(918512021,orderFinishTime,String.valueOf(returnGoodsDay));
				}
			}
		}
		
		//若用户在退货服务时间段退货则微公社扣除用户返利,同时增增加商户的预存款,若超出退货服务时间则不再扣除用户返利和增加商户预存款
		if(mWebResult.upFlagTrue() && !rebackFlag){
			//记录账户退款金额用Map
			HashMap<String, BigDecimal> acReMap = new HashMap<String, BigDecimal>();
			for(GcReckonOrderReturnDetail gcReckonOrderReturnDetail:detailList){
				
				//具体详情
				GcReckonOrderDetailExample gcReckonOrderDetailExample=new GcReckonOrderDetailExample();
				gcReckonOrderDetailExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
				GcReckonOrderDetail gcReckonOrderDetail= gcReckonOrderDetailMapper.selectByExample(gcReckonOrderDetailExample).get(0);
				
				// 开始处理相应逆向消费
			    GcActiveLogExample gcActiveLogExample=new GcActiveLogExample();
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog
								.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						updateActiveCount(gcActiveLog, 0);
					}

				}
				
				//开始处理相应逆向清分
				GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
				gcReckonLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001")
				.andReckonMoneyGreaterThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());

				List<GcReckonLog> reckonLogList = gcReckonLogMapper
						.selectByExample(gcReckonLogExample);
				if (reckonLogList != null && reckonLogList.size() > 0) {
					
					//最后一次退货标记
					boolean lastRerutnFlag = false;//默认不是最后一次退货
					//判断是否为最后一次退货,若每次退货都都小于1分钱,则最后一次就统一退一次
					int detailNum = 0;
					if(gcReckonOrderDetail != null && (gcReckonOrderDetail.getProductNumber() > 0)){
						detailNum = gcReckonOrderDetail.getProductNumber();//detail_code的购买数量
					}
					//订单,detail_code的退货数量
					int detailReturnNum=0;
					GcReckonOrderReturnDetailExample gcReckonOrderReturnDetailCheckExample=new GcReckonOrderReturnDetailExample();
					gcReckonOrderReturnDetailCheckExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
					List<GcReckonOrderReturnDetail> detailCheckList=gcReckonOrderReturnDetailMapper.selectByExample(gcReckonOrderReturnDetailCheckExample);
					for(GcReckonOrderReturnDetail checkItem : detailCheckList){
						detailReturnNum = detailReturnNum+checkItem.getProductNumber().intValue();
					}
					//退货数量>=购买数量
					if(detailReturnNum >= detailNum){
						lastRerutnFlag = true;//最后一次退货
					}
					for (GcReckonLog item : reckonLogList) {
						
						BigDecimal backReckonMoney = BigDecimal.ZERO;//退款金额
						GcReckonLogExample checkGcReckonLogEmpl=new GcReckonLogExample();
						checkGcReckonLogEmpl.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode())
						.andReckonChangeTypeEqualTo("4497465200030001").andReckonMoneyLessThan(BigDecimal.ZERO).andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode());
						List<GcReckonLog> checkReckonLogList = gcReckonLogMapper.selectByExample(checkGcReckonLogEmpl);
						BigDecimal totalReckonMoney = BigDecimal.ZERO;
						
						//最后一次退且以前的退货都没记录(例：每次小于一分钱等情况),统一退一次
						if(lastRerutnFlag && (checkReckonLogList ==null || checkReckonLogList.size() < 1)){
							backReckonMoney=item.getReckonMoney();
						}else{
							for(GcReckonLog ckItem:checkReckonLogList){
								totalReckonMoney = totalReckonMoney.add(ckItem.getReckonMoney().abs());//account累计已经退的钱(负数,取绝对值)
							}
							
							//account累计已经退的钱已经>=返利的钱,不再退款,不进行后续处理
							if(totalReckonMoney.compareTo(item.getReckonMoney()) >= 0){
								continue;
							}
							
							//为了确保小数计算后的精度,除后保留10位小数,乘完后在获取2位小数
							backReckonMoney=(item.getReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),10,BigDecimal.ROUND_HALF_UP)
									.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()))).setScale(2, BigDecimal.ROUND_DOWN);//直接截取小数点后2位数
							//返利金额<=0时,不再退款,不进行后续处理
							if(backReckonMoney.compareTo(BigDecimal.ZERO)<=0){
								continue;
							}
							
							//现在退的钱+account累计已经退的钱>返利的钱,将返利金额-累计返利的差额作为退款
							BigDecimal currentReckonMoney = backReckonMoney.add(totalReckonMoney);
							if(currentReckonMoney.compareTo(item.getReckonMoney()) > 0){
								backReckonMoney=item.getReckonMoney().subtract(backReckonMoney).abs();
							}
						}
						
						//记录账户退款金额
						String aCode = item.getAccountCode();
						if(!acReMap.containsKey(aCode)){
							acReMap.put(aCode, backReckonMoney);
						}else{
							BigDecimal reMoney = BigDecimal.valueOf(Double.parseDouble(acReMap.get(aCode).toString()));
							BigDecimal newReMoney = reMoney.add(backReckonMoney);
							acReMap.put(aCode, newReMoney);
						}
						
						//根据rebate的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcRebateLogExample gcRebateLogExample = new GcRebateLogExample();
						gcRebateLogExample.createCriteria().andAccountCodeEqualTo(item.getAccountCode()).andDetailCodeEqualTo(item.getDetailCode())
							.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1);
						List<GcRebateLog> rebateLogList = gcRebateLogMapper.selectByExample(gcRebateLogExample);
						
						for(GcRebateLog gcRebateLog : rebateLogList){
							GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
							GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
							
							gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcRebateLog.getLogCode()).andDepositNotEqualTo(BigDecimal.ZERO).andDepositTypeEqualTo("4497472500040001");
							List<GcTraderDepositLog> depositLogListRebate=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
							
							//将保证金加回
							if(depositLogListRebate!=null&&depositLogListRebate.size()>0){
								GcTraderDepositLog gcTraderDepositLog=depositLogListRebate.get(0);
								GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
								gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
								gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
								addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
								addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
								addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
								addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
								addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
								addDepositLog.setDeposit(backReckonMoney);
								addDepositLog.setDepositType("4497472500040002");//退单增加
								addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
							}
						}
						
						//为新版本上线后兼容老数据，根据reckonLog的logCode判断是否给商户扣过钱.y:把钱加回 n:无处理
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						
						gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						
						//将保证金加回
						if(depositLogList!=null&&depositLogList.size()>0){
							GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
							GcTraderInfo gcTraderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
							//加上对应的保证金金额
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo==null||gcTraderInfo.getAccountCode()==null?"":gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(backReckonMoney);
							gcTraderFoundsChangeLog.setChangeType("4497472500030004");//退单增加
							gcTraderFoundsChangeLog.setRelationCode(gcTraderDepositLog.getRelationCode());
							gcTraderFoundsChangeLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog addDepositLog=new GcTraderDepositLog();
							addDepositLog.setOrderCode(gcTraderDepositLog.getOrderCode());
							addDepositLog.setAccountCode(gcTraderDepositLog.getAccountCode());
							addDepositLog.setOrderAccountCode(gcTraderDepositLog.getOrderAccountCode());
							addDepositLog.setRelationLevel(gcTraderDepositLog.getRelationLevel());
							addDepositLog.setSkuCode(gcTraderDepositLog.getSkuCode());
							addDepositLog.setDeposit(backReckonMoney);
							addDepositLog.setDepositType("4497472500040002");//退单增加
							addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
							addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
						    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
						}
						
						// 判断是否已转入可提现账户
						if (item.getFlagWithdraw().equals(0)) {
							// -------------------- 开始反向可提现账户记录
							GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
							gcWithdrawLog.setAccountCode(item.getAccountCode());
							gcWithdrawLog.setWithdrawChangeType("4497465200040003");
							gcWithdrawLog.setWithdrawMoney(backReckonMoney.negate());
							gcWithdrawLog.setChangeCodes(FormatHelper.join(
									reckonStep.getStepCode(), item.getLogCode()));
							List<GcWithdrawLog> listWithdrawLogsUpdate = new ArrayList<GcWithdrawLog>();
							listWithdrawLogsUpdate.add(gcWithdrawLog);
		                    
							txGroupAccountService.updateAccount(null,listWithdrawLogsUpdate);
							
						} else {
		
							// -------------------- 开始反向清分账户记录
		
							GcReckonLog gcReckonLog = new GcReckonLog();
							gcReckonLog.setAccountCode(item.getAccountCode());
							gcReckonLog.setFlagWithdraw(1);
							gcReckonLog.setOrderAccountCode(item
									.getOrderAccountCode());
							gcReckonLog.setOrderCode(item.getOrderCode());
							gcReckonLog.setReckonChangeType("4497465200030001");
							gcReckonLog.setReckonMoney(backReckonMoney.negate());
							gcReckonLog.setRelationLevel(item.getRelationLevel());
							gcReckonLog.setScaleReckon(item.getScaleReckon());
							gcReckonLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcReckonLog.setOrderReckonTime(item
									.getOrderReckonTime());
							gcReckonLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
		
							List<GcReckonLog> listUpdateGcReckonLogs = new ArrayList<GcReckonLog>();
							listUpdateGcReckonLogs.add(gcReckonLog);
							txGroupAccountService.updateAccount(
									listUpdateGcReckonLogs, null);
							
							//处理预返利数据
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140001");//类型为正向，金额为负
							gcRebateLog.setRebateMoney(backReckonMoney.negate());
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(gcReckonOrderReturnDetail.getReturnCode());
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setRebateType("4497465200150001");
							gcRebateLog.setFlagStatus(1);
							List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
							listUpdateGcRebateLogs.add(gcRebateLog);
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
							
							//更新返利金额
							GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
							gcRebateOrderExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(item.getAccountCode());
							List<GcRebateOrder> rebateOrderList=gcRebateOrderMapper.selectByExample(gcRebateOrderExample);
							if(rebateOrderList!=null&&rebateOrderList.size()>0){
								GcRebateOrder gcRebateOrder=rebateOrderList.get(0);
								GcRebateOrder updateGcRebateOrder=new GcRebateOrder();
								updateGcRebateOrder.setRebateMoney(gcRebateOrder.getRebateMoney().add(backReckonMoney.negate()));
								gcRebateOrderMapper.updateByExampleSelective(updateGcRebateOrder, gcRebateOrderExample);
							}
						}
		
					}
				}
			}
			
			//推送消息
			if(mWebResult.upFlagTrue()){
				try{
					List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
					if(accountList!=null&&accountList.size()>0){
						for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
							BigDecimal reMoney=BigDecimal.ZERO;
							if(acReMap.containsKey(accountRelation.get("account_code"))){
								reMoney = acReMap.get(accountRelation.get("account_code"));
							}
							//如果退款金额是不为空且>0时 才给用户推送消息,否则不推送信息
							if(reMoney != null && (reMoney.compareTo(BigDecimal.ZERO)>0)){
								if(accountRelation.get("relation_level").equals("0")){
									relation="您已退货成功";
									push_range="449747220003";
								}
								else{
							    	MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    	if(accountMap!=null){
							    		relationCode=accountMap.get("member_code");
							    	}
							    	else{
							    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode());
							    		if(otherMap!=null){
							    			relationCode=otherMap.get("member_code");
							    		}
							    	}
							   
							    	Map<String, String> map=new HashMap<String, String>();
									map.put("member_code",relationCode );
									map.put("account_code_wo", accountRelation.get("account_code"));
									map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
	//								String nickName=NickNameHelper.getNickName(map);
									String nickName=NickNameHelper.checkToNickName(map);
							    	
							    	if(accountRelation.get("relation_level").equals("1")){
								    	relation="【"+nickName+"】退货成功";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.get("relation_level").equals("2")){
								    	nickName=NickNameHelper.getFirstNickName(accountRelation.get("account_code"),gcReckonOrderInfo.getAccountCode());
								    	relation="【"+nickName+"】的好友退货成功";
								    	push_range="449747220002";
								    }
								}
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(accountRelation.get("account_code"));
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010002");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle(relation);
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
							    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+reMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";//???内容待定
							    addSinglePushCommentInput.setContent(content);
							    addSinglePushCommentInput.setRelationCode(relationCode);
								
								if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"c2aa54c704614b598b8d64376c8b653e\" "
										+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
										+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.get("account_code")))<1){
									addSinglePushCommentInput.setSendStatus("4497465000070001");
								}
								else{
									addSinglePushCommentInput.setSendStatus("4497465000070002");
								}
								SinglePushComment.addPushComment(addSinglePushCommentInput);
								
								//单聊时通过荣云发退货消息
								if(accountRelation.get("relation_level").equals("0")){
								}else{
							    	//一度好友下单时,给上级发消息
							    	if(accountRelation.get("relation_level").equals("1")){
							    		//我memberCode
							    		MDataMap meMemberMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    		//我的上级memberCode
							    		MDataMap oneMemberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	
							    		if(meMemberMap!=null && oneMemberMap !=null){
								    		//通过消息队列发消息
//											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//													new MDataMap("fromUserId", meMemberMap.get("member_code"),"toUserId", oneMemberMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我申请退单了,下次在帮你赚钱吧！\"}"));
								    	}
								    }
								    else if(accountRelation.get("relation_level").equals("2")){
								    	MDataMap oneMembMap = new MDataMap();
								    	//获取我的上级accountCode
								    	MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",gcReckonOrderInfo.getAccountCode(),"flag_enable","1");
								    	if(oneMap != null){
								    		String oneAcCode = oneMap.get("parent_code");
								    		//获取我的上级的memberCode
								    		oneMembMap=DbUp.upTable("mc_member_info").one("account_code",oneAcCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	}
								    	//获取我上级的上级的memberCode
								    	MDataMap twoMembMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.get("account_code"),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	if(oneMembMap != null && twoMembMap !=null){
								    		//通过消息队列发消息
//											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//													new MDataMap("fromUserId", oneMembMap.get("member_code"),"toUserId", twoMembMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我的好友退单了,下次在帮你赚钱吧！\"}"));
								    	}
								    }
								}
							}
						}
					}
				}catch(Exception e){
					
				}
			}
			
		}
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;
	}
	
/**
 * fengl 同步家有订单当详细表中有数据是更新清分详细表中的数据
 * @param gcReckonOrderDetail
 */
	public void updateReckonDetail(GcReckonOrderDetail gcReckonOrderDetail){
		GcReckonOrderDetailMapper gcReckonOrderDetailMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcReckonOrderDetailMapper");
		GcReckonOrderDetailExample gcReckonOrderDetailExample = new GcReckonOrderDetailExample();
		gcReckonOrderDetailExample.createCriteria().andDetailCodeEqualTo(
				gcReckonOrderDetail.getDetailCode());

		// 更新订单和清分的统计
		gcReckonOrderDetailMapper.updateByExampleSelective(gcReckonOrderDetail,
				gcReckonOrderDetailExample);

	}

	/**
	 *panwei 清分检测
	 */
	public void reckonOrderVertify() {
		
		/**
		 * 1.多次清分，二次清分需检测有无退货
		 * 2.清分一次，到账多次
		 * 3.清分未到账（清分n天后未到账）
		 * 4.未清分到账（change_code在gc_reckon_log中不存在，无法分次查询）
		 * 5.一笔订单退货重复扣减账户余额
		 * 6.退货后未扣减账户余额
		 * 7.清分未到账退货，扣减账户余额
		 */
		
		//1、清分记录在withdraw_log只能存在1条或0条记录
		MDataMap map=new MDataMap();
		map.put("now_time", FormatHelper.upDateTime());
		List<MDataMap> reckonList=DbUp.upTable("gc_reckon_log").query(
				"", "zid DESC", "(next_vertify_time is null or next_vertify_time<:now_time) and reckon_change_type in ('4497465200030001','4497465200030002') and reckon_money>0 ", 
				map, 0, 1000);
		String codeList1="";//问题清分单号
		String codeList2="";
		String codeList3="";
		String codeList4="";
		String codeList5="";
		String codeList6="";
		String codeList7="";
		for(MDataMap reckon:reckonList){
			String logCode=reckon.get("log_code");
			String reckonTime=reckon.get("create_time");
			
			//正向清分
			BigDecimal reckonMoney=new BigDecimal(reckon.get("reckon_money"));
			if(reckon.get("reckon_change_type").equals("4497465200030001")&&(reckonMoney.compareTo(BigDecimal.ZERO)==1)){
				//2、查看是否多次转入提现账户
				MDataMap mWhereMap=new MDataMap();
				mWhereMap.put("reckon_money", reckon.get("reckon_money"));
				mWhereMap.put("log_code", logCode);
				int withdrawZ=DbUp.upTable("gc_withdraw_log").dataCount("change_codes =:log_code and withdraw_change_type='4497465200040001' and withdraw_money=:reckon_money", mWhereMap);
//				int withdrawCountZ=DbUp.upTable("gc_withdraw_log").count("change_codes",logCode,"withdraw_change_type","4497465200040001","withdraw_money",reckon.get("reckon_money"));
				if(withdrawZ>1){
					codeList2+=logCode+"、";
				}
				
				
				//3、清分未到账（清分n天后未到账 排除退货)
				//判断是否退货和清分金额是否为0
				if(new BigDecimal(reckon.get("reckon_money")).compareTo(BigDecimal.ZERO)==1){
					int isReturnList=DbUp.upTable("gc_reckon_log").dataCount("reckon_change_type=4497465200030002 and change_codes='"+logCode+"'",new MDataMap());
					if(isReturnList==0){
						String sTimer = DateHelper
								.upDateTimeAdd("-10d");
						String cTimer = DateHelper
								.upDateTimeAdd("-2d");
						if(DateUtil.toDate(reckon.get("order_reckon_time"), DateUtil.DATE_FORMAT_DATETIME).before(DateUtil.toDate(sTimer, DateUtil.DATE_FORMAT_DATETIME))&&
								DateUtil.toDate(reckon.get("create_time"), DateUtil.DATE_FORMAT_DATETIME).before(DateUtil.toDate(cTimer, DateUtil.DATE_FORMAT_DATETIME))){
							if(withdrawZ<1){
								codeList3+=logCode+"、";
							}
						}
					}
				}
				
				//5、一笔订单退货重复扣减账户余额（已转入提现账户  ）
				//判断该订单是否已转入可提现账户
				if(reckon.get("flag_withdraw").equals("0")){
					//判断是否退货
					MDataMap stepMap=DbUp.upTable("gc_reckon_order_step").one("exec_type","4497465200050002","order_code",reckon.get("order_code"),"flag_success","1");
					if(stepMap!=null){
						mWhereMap.put("reckon_money", new BigDecimal(reckon.get("reckon_money")).negate().toString());
						int returnList=DbUp.upTable("gc_withdraw_log").dataCount("change_codes like '%"+logCode+"%' and withdraw_change_type='4497465200040003' and withdraw_money=:reckon_money", mWhereMap);
						if(returnList>1){
							codeList5+=logCode+"、";
						}
						
						//6.退货后未扣减账户余额
						//判断是否是转入可提现账户
						MDataMap reckonReturn=DbUp.upTable("gc_reckon_log").one("order_code",reckon.get("order_code"),"reckon_change_type","4497465200030001");
						if(returnList==0&&reckonReturn==null){
							codeList6+=logCode+"、";
						}
					}
					
					//判断退货金额超过返利金额（第三方退货接口）
					int stepThirdCount=DbUp.upTable("gc_reckon_order_step").count("exec_type","4497465200050006","order_code",reckon.get("order_code"),"flag_success","1");
					if(stepThirdCount>0){
						Map<String,Object> thirdReturnReckonMap=DbUp.upTable("gc_reckon_log").dataSqlOne("select sum(reckon_money) from gc_reckon_log where order_code='"+reckon.get("order_code")+"' and account_code='"+reckon.get("account_code")+"' and reckon_change_type='4497465200030001' ", new MDataMap());
						Map<String,Object> thirdReturnWithdrawMap=DbUp.upTable("gc_withdraw_log").dataSqlOne("select sum(withdraw_money) from gc_withdraw_log where change_codes like '%"+logCode+"%' and withdraw_change_type='4497465200040003' ", new MDataMap());
						BigDecimal returnWithdrawMoney=BigDecimal.ZERO;
						if(thirdReturnWithdrawMap.get("sum(withdraw_money)")!=null){
							returnWithdrawMoney=new BigDecimal(thirdReturnWithdrawMap.get("sum(withdraw_money)").toString()).negate();
						}
						
						BigDecimal thirdReturnReckonMoney=new BigDecimal(thirdReturnReckonMap.get("sum(reckon_money)").toString());
						if(thirdReturnReckonMoney.compareTo(returnWithdrawMoney)==-1){
							codeList5+=logCode+"、";
						}
					}
				}
				
			}
			
			
			//逆向清分
			if(reckon.get("reckon_change_type").equals("4497465200030002")){
				
				//清分完成后退货withdraw_change_type
				int withdrawCountF=DbUp.upTable("gc_withdraw_log").dataCount("change_codes like '%"+reckon.get("change_codes")+"%' and withdraw_change_type='4497465200040003' and withdraw_money='"+reckon.get("reckon_money")+"'", new MDataMap());
//				int withdrawCountF=DbUp.upTable("gc_withdraw_log").count("change_codes",logCode,"withdraw_change_type","4497465200040003","withdraw_money",reckon.get("reckon_money"));
				//7.清分未到账退货，扣减账户余额

				if(withdrawCountF>0){
					codeList7+=logCode+"、";
				}
				
			}
			
			
			
			//存入下次检测时间
			
			long time=new Date().getTime()-DateUtil.toDate(reckonTime).getTime();
			//判断距现在天数
			long day=time/1000/3600/24;
			String next_vertify_time;
			if(day<15){
				next_vertify_time=DateUtil.toString(DateUtil.addDays(new Date(), 1),DateUtil.DATE_FORMAT_DATETIME);
			}else{
				next_vertify_time=DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt(String.valueOf(day))),DateUtil.DATE_FORMAT_DATETIME);
			}
			
			reckon.put("next_vertify_time", next_vertify_time);
			DbUp.upTable("gc_reckon_log").update(reckon);
		}
		
		String content="";
		String receives[]= bConfig("groupcenter.wgs_reckon_log_sendMail_receives").split(",");
		String title= bConfig("groupcenter.wgs_reckon_log_title");
		if(codeList2.length()>1){
			codeList2="多次转入提现账户订单："+codeList2.substring(0, codeList2.length()-1);
			content+=codeList2+"\r\n";
		}
		
		if(codeList3.length()>1){
			codeList3="清分未到账订单："+codeList3.substring(0, codeList3.length()-1);
			content+=codeList3+"\r\n";
		}
		
		
		if(codeList5.length()>1){
			codeList5="退货重复扣减账户余额订单："+codeList5.substring(0, codeList5.length()-1);
			content+=codeList5+"\r\n";
		}
		
		if(codeList6.length()>1){
			codeList6="退货后未扣减账户余额订单："+codeList6.substring(0, codeList6.length()-1);
			content+=codeList6+"\r\n";
		}
		
		if(codeList7.length()>1){
			codeList7="清分未到账退货，扣减账户余额订单："+codeList7.substring(0, codeList7.length()-1);
			content+=codeList7+"\r\n";
		}
		if(content.length()>0){
			WgsMailSupport.INSTANCE.sendMail("清分问题订单通知", title,content);
		}
		
	}
	
	/**
	 * 检测未清分到账订单
	 */
	public void noReckonOrderVertify() {
		
		String title= bConfig("groupcenter.wgs_reckon_log_title");
		
		String errorList="";
		List<MDataMap> errorWithdrawList=DbUp.upTable("gc_withdraw_log").queryAll("", "", "change_codes not in (select log_code from gc_reckon_log where reckon_change_type='4497465200030001' and flag_withdraw='0')  and withdraw_change_type='4497465200040001'", new MDataMap());
		if(errorWithdrawList.size()>1){
			for(MDataMap map:errorWithdrawList){
				errorList+=map.get("change_codes")+"、";
			}
		}
		
		errorList="未清分到账订单："+errorList.substring(0, errorList.length()-1);
		
		WgsMailSupport.INSTANCE.sendMail("清分问题订单通知", title,errorList);
		
	}
	
}
