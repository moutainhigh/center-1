package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcActiveLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupLevelMapper;
import com.cmall.dborm.txmapper.groupcenter.GcLevelLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateOrderMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderFoundsChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderInfoMapper;
import com.cmall.dborm.txmodel.groupcenter.GcActiveLog;
import com.cmall.dborm.txmodel.groupcenter.GcActiveLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcActiveMonth;
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
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.GroupLevelInfo;
import com.cmall.groupcenter.model.ReckonOrderInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.support.ReckonOrderSupport;
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
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 订单返利相关
 * @author chenbin
 *
 */
public class TxRebateOrderService extends BaseClass{
	
	private String upInfo(long lInfoCode, String... sParams) {

		return FormatHelper.upDateTime() + bInfo(lInfoCode, sParams);
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
	 * 正向预返利流程
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult rebateIn(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();

		String sAccountCode = reckonStep.getAccountCode();

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());

		String sManageCode = gcReckonOrderInfo.getManageCode();

		GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");

		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		
		//商户判断
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
				listExec.add(upInfo(918515308,
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
				GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
				
				groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfo(gcGroupAccount.getAccountLevel(), sManageCode,String.valueOf(accountRelation.getDeep()));
				
				// 定义清分比例
				BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
				
				// 定义清分深度
				int iDeepReckon = groupLevelInfo.getDeepReckon();
				// 定义活跃统计深度
				int iDeepConsume = groupLevelInfo.getDeepConsume();

				// 定义返利日志数组
				List<GcRebateLog> listRebateLogs = new ArrayList<GcRebateLog>();
				//开始定义返利日志
				GcRebateLog gcRebateLog=new GcRebateLog();
				gcRebateLog.setScaleReckon(bScaleReckon);
				gcRebateLog.setRebateMoney(gcReckonOrderInfo
							.getReckonMoney().multiply(gcRebateLog.getScaleReckon()));
				gcRebateLog.setChangeCodes(reckonStep.getStepCode());
		
				// 判断如果能清分到级别 再次添加信息
				if (accountRelation.getDeep() <= iDeepReckon&& gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
				
                    listRebateLogs.add(gcRebateLog);
					listExec.add(upInfo(918512002,
							accountRelation.getAccountCode(),
							gcReckonOrderInfo.getReckonMoney().toString(), bScaleReckon.toString(),
							gcRebateLog.getRebateMoney().toString()));
				}

				// 判断如果数量大于0
				if (listRebateLogs.size() > 0) {
					for (int i = 0, j = listRebateLogs.size(); i < j; i++) {
						listRebateLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listRebateLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listRebateLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listRebateLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listRebateLogs.get(i).setOrderRebateTime(
								gcReckonOrderInfo.getOrderCreateTime());
						listRebateLogs.get(i).setRebateChangeType("4497465200140001");//订单预返利
						listRebateLogs.get(i).setFlagStatus(1);
						listRebateLogs.get(i).setRebateType("4497465200150001");//预返利计算类型
					}

					txGroupAccountService.updateAccount(null, null,listRebateLogs);
                    
					//创建更新返利订单表 
					insertRebateOrder(listRebateLogs,sManageCode);
					//push下单消息
					try {
							String relation="";
							String push_range="";
							String relationCode="";
							if(accountRelation.getDeep()==0){
								relation="您下单成功啦";
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
								map.put("account_code_wo", accountRelation.getAccountCode());
								map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
								String nickName=NickNameHelper.getNickName(map);
								
						    	if(accountRelation.getDeep()==1){
							  //  	relation="您的一度好友【"+nickName+"】下单啦";
//						       		修改2015-12-2 APP2.0版本  fengl
						    		relation="【"+nickName+"】下单啦";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.getDeep()==2){
//							    	relation="您的二度好友【"+nickName+"】下单啦";
//						                                修改2015-12-2 APP2.0版本  fengl
							    	relation="【"+nickName+"】的好友下单啦";
							    	push_range="449747220002";
							    }
							}
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(accountRelation.getAccountCode());
							addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
							addSinglePushCommentInput.setType("44974720000400010001");
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle(relation);
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
							
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+gcRebateLog.getRebateMoney().setScale(2,BigDecimal.ROUND_HALF_UP)+"元哦~";
						    addSinglePushCommentInput.setRelationCode(relationCode);
						    addSinglePushCommentInput.setContent(content);
							
							if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"605bbad5e1a54bac9e3ce3960053be56\" "
									+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
									+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.getAccountCode()))<1){
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
			}

		}

		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向预返利流程 第二版代码，于20150821修改 改为根据商家设置的返现范围和sku比例及商家等级比例返利
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult rebateInForSecond(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();

//		String sAccountCode = reckonStep.getAccountCode();

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
		
		//商户判断
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&appMap.get("trade_code")!=null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		 
		//商户不存在或者状态不可用，不返利
		if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
			listExec.add(upInfo(918512020, sManageCode));
			mWebResult.inErrorMessage(918512020,sManageCode);
		}
		
		//开始锁定执行流程编号 防止并发执行
		String traderLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
		if (StringUtils.isEmpty(traderLock)) {
			mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
		}
		
		//为预防并发操作，再次获取商户预存款
		gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		BigDecimal gurranteeBalance = BigDecimal.ZERO;
		if(gcTraderInfo!=null){
			gurranteeBalance = gcTraderInfo.getGurranteeBalance();
		}
		
		//是否可返利标记
		boolean rebateFlag = true;
		//判断商户预存款是否>返利额,大于则返利,否则不返利
		if (mWebResult.upFlagTrue()) {
			//总返利金额
			BigDecimal totalRabateMoney = BigDecimal.ZERO;
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
						
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					if(accountRelation.getDeep() <= iDeepReckon && rabateMoney.compareTo(BigDecimal.ZERO) > 0){
						totalRabateMoney = totalRabateMoney.add(rabateMoney);
					}
				}
			}
			//预存款<关联账户的总返利金额，不能返利
			if(gurranteeBalance.compareTo(totalRabateMoney) < 0){
				rebateFlag = false;
				listExec.add(upInfo(915805142,
						appMap.get("trade_code"),
						String.valueOf(gurranteeBalance),
						String.valueOf(totalRabateMoney)));
			}
		}
		
		//返利逻辑
		if (mWebResult.upFlagTrue() && rebateFlag) {
			
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations){
				//每单总返利金额
				BigDecimal skuTotalRabateMoney = BigDecimal.ZERO;
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				// 定义返利日志数组
				List<GcRebateLog> listRebateLogs = new ArrayList<GcRebateLog>();
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					listExec.add(upInfo(918515308,
							accountRelation.getAccountCode(),
							String.valueOf(accountRelation.getDeep())));
					
					//SKU金额可清分金额
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
					
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					skuTotalRabateMoney = skuTotalRabateMoney.add(rabateMoney);
					
					//开始定义返利日志
					GcRebateLog gcRebateLog=new GcRebateLog();
					gcRebateLog.setLogCode(WebHelper.upCode("GCRBL"));
					gcRebateLog.setScaleReckon(bScaleReckon);
					gcRebateLog.setRebateMoney(rabateMoney);//返利金额
					gcRebateLog.setChangeCodes(reckonStep.getStepCode());
					gcRebateLog.setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
					gcRebateLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
					
					// 判断如果能清分到级别 再次添加信息
					if (accountRelation.getDeep() <= iDeepReckon&& gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
					
	                    listRebateLogs.add(gcRebateLog);
						listExec.add(upInfo(918512002,
								accountRelation.getAccountCode(),
								bConsumeMoney.toString(), bScaleReckon.toString(),
								gcRebateLog.getRebateMoney().toString()));
					}
				}
				
				// 判断如果数量大于0
				if (listRebateLogs.size() > 0) {
					for (int i = 0, j = listRebateLogs.size(); i < j; i++) {
						listRebateLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listRebateLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listRebateLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listRebateLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listRebateLogs.get(i).setOrderRebateTime(
								gcReckonOrderInfo.getOrderCreateTime());
						listRebateLogs.get(i).setRebateChangeType("4497465200140001");//订单预返利
						listRebateLogs.get(i).setFlagStatus(1);
						listRebateLogs.get(i).setRebateType("4497465200150001");//预返利计算类型
					}
					
					if (mWebResult.upFlagTrue()){
						//开始扣除商户预存款
						for (int k = 0; k<listRebateLogs.size();k++){
							
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(listRebateLogs.get(k).getRebateMoney().negate());
							gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
							gcTraderFoundsChangeLog.setRelationCode(listRebateLogs.get(k).getLogCode());
							gcTraderFoundsChangeLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
							gcTraderDepositLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							gcTraderDepositLog.setAccountCode(listRebateLogs.get(k).getAccountCode());
							gcTraderDepositLog.setOrderAccountCode(listRebateLogs.get(k).getOrderAccountCode());
							gcTraderDepositLog.setRelationLevel(listRebateLogs.get(k).getRelationLevel());
							gcTraderDepositLog.setSkuCode(listRebateLogs.get(k).getSkuCode());
						    gcTraderDepositLog.setDeposit(listRebateLogs.get(k).getRebateMoney().negate());
						    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
						    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
						    gcTraderDepositLog.setRelationCode(listRebateLogs.get(k).getLogCode());
						    addTraderDepositOrderLog(gcTraderDepositLog);
						}
						
						txGroupAccountService.updateAccount(null, null,listRebateLogs);
	                    
						//创建更新返利订单表
						insertRebateOrder(listRebateLogs,sManageCode);
					}
					
					if (mWebResult.upFlagTrue()) {
						//push下单消息
						try {
								String relation="";
								String push_range="";
								String relationCode="";
								if(accountRelation.getDeep()==0){
									relation="您下单成功啦";
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
									map.put("account_code_wo", accountRelation.getAccountCode());
									map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
									String nickName=NickNameHelper.getNickName(map);
									
							    	if(accountRelation.getDeep()==1){
								    	relation="您的一度好友【"+nickName+"】下单啦";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.getDeep()==2){
								    	relation="您的二度好友【"+nickName+"】下单啦";
								    	push_range="449747220002";
								    }
								}
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(accountRelation.getAccountCode());
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010001");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle(relation);
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
								
							    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元哦~";
							    addSinglePushCommentInput.setRelationCode(relationCode);
							    addSinglePushCommentInput.setContent(content);
								
								if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"605bbad5e1a54bac9e3ce3960053be56\" "
										+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
										+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.getAccountCode()))<1){
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
				}
			}
		}

		// 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(traderLock)) {
			WebHelper.unLock(traderLock);
		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向预返利流程 第三版代码，于20151014修改 改为订单创建时间>停止返利时间,并且商户状态是停用状态,则不再给用户返利
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateInForThird(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();
		listExec.add("RebateInThird");
//		String sAccountCode = reckonStep.getAccountCode();

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
		
		//商户判断
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		 
		//商户不存在或者状态不可用，不返利
		if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
			listExec.add(upInfo(918512020, sManageCode));
			mWebResult.inErrorMessage(918512020,sManageCode);
		}
		
		//订单创建时间>停止返利时间,并且商户状态是停用状态,则此订单不再给用户返利
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
		
		//开始锁定执行流程编号 防止并发执行
		String traderLock = "";
		if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
			traderLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
			if (StringUtils.isEmpty(traderLock)) {
				listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
				mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
			}
		}
		
		//为预防并发操作，再次获取商户预存款
		BigDecimal gurranteeBalance = BigDecimal.ZERO;
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			if(gcTraderInfo!=null){
				gurranteeBalance = gcTraderInfo.getGurranteeBalance();
			}
		}
		
		//是否可返利标记
		boolean rebateFlag = true;
		//判断商户预存款是否>返利额,大于则返利,否则不返利
		if (mWebResult.upFlagTrue()) {
			//总返利金额
			BigDecimal totalRabateMoney = BigDecimal.ZERO;
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
						
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					if(accountRelation.getDeep() <= iDeepReckon && rabateMoney.compareTo(BigDecimal.ZERO) > 0){
						totalRabateMoney = totalRabateMoney.add(rabateMoney);
					}
				}
			}
			//预存款<关联账户的总返利金额，不能返利
			if(gurranteeBalance.compareTo(totalRabateMoney) < 0){
				rebateFlag = false;
				listExec.add(upInfo(915805142,String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney)));
				mWebResult.inErrorMessage(915805142, String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney));
			}
		}
		
		//返利逻辑
		if (mWebResult.upFlagTrue() && rebateFlag) {
			
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations){
				//每单总返利金额
				BigDecimal skuTotalRabateMoney = BigDecimal.ZERO;
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				// 定义返利日志数组
				List<GcRebateLog> listRebateLogs = new ArrayList<GcRebateLog>();
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					listExec.add(upInfo(918515308,
							accountRelation.getAccountCode(),
							String.valueOf(accountRelation.getDeep())));
					
					//SKU金额可清分金额
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
					
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					skuTotalRabateMoney = skuTotalRabateMoney.add(rabateMoney);
					
					//开始定义返利日志
					GcRebateLog gcRebateLog=new GcRebateLog();
					gcRebateLog.setLogCode(WebHelper.upCode("GCRBL"));
					gcRebateLog.setScaleReckon(bScaleReckon);
					gcRebateLog.setRebateMoney(rabateMoney);//返利金额
					gcRebateLog.setChangeCodes(reckonStep.getStepCode());
					gcRebateLog.setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
					gcRebateLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
					
					// 判断如果能清分到级别 再次添加信息
					if (accountRelation.getDeep() <= iDeepReckon&& gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
					
	                    listRebateLogs.add(gcRebateLog);
						listExec.add(upInfo(918512002,
								accountRelation.getAccountCode(),
								bConsumeMoney.toString(), bScaleReckon.toString(),
								gcRebateLog.getRebateMoney().toString()));
					}
				}
				
				// 判断如果数量大于0
				if (listRebateLogs.size() > 0) {
					for (int i = 0, j = listRebateLogs.size(); i < j; i++) {
						listRebateLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listRebateLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listRebateLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listRebateLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listRebateLogs.get(i).setOrderRebateTime(
								gcReckonOrderInfo.getOrderCreateTime());
						listRebateLogs.get(i).setRebateChangeType("4497465200140001");//订单预返利
						listRebateLogs.get(i).setFlagStatus(1);
						listRebateLogs.get(i).setRebateType("4497465200150001");//预返利计算类型
					}
					
					if (mWebResult.upFlagTrue()){
						//开始扣除商户预存款
						for (int k = 0; k<listRebateLogs.size();k++){
							
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(listRebateLogs.get(k).getRebateMoney().negate());
							gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
							gcTraderFoundsChangeLog.setRelationCode(listRebateLogs.get(k).getLogCode());
							gcTraderFoundsChangeLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
							gcTraderDepositLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							gcTraderDepositLog.setAccountCode(listRebateLogs.get(k).getAccountCode());
							gcTraderDepositLog.setOrderAccountCode(listRebateLogs.get(k).getOrderAccountCode());
							gcTraderDepositLog.setRelationLevel(listRebateLogs.get(k).getRelationLevel());
							gcTraderDepositLog.setSkuCode(listRebateLogs.get(k).getSkuCode());
						    gcTraderDepositLog.setDeposit(listRebateLogs.get(k).getRebateMoney().negate());
						    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
						    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
						    gcTraderDepositLog.setRelationCode(listRebateLogs.get(k).getLogCode());
						    addTraderDepositOrderLog(gcTraderDepositLog);
						}
						
						txGroupAccountService.updateAccount(null, null,listRebateLogs);
	                    
						//创建更新返利订单表
						insertRebateOrder(listRebateLogs,sManageCode);
					}
					
					if (mWebResult.upFlagTrue()) {
						//push下单消息
						try {
								String relation="";
								String push_range="";
								String relationCode="";
								if(accountRelation.getDeep()==0){
									relation="您下单成功啦";
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
									map.put("account_code_wo", accountRelation.getAccountCode());
									map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//									String nickName=NickNameHelper.getNickName(map);
									String nickName=NickNameHelper.checkToNickName(map);
									
							    	if(accountRelation.getDeep()==1){
//								    	relation="您的一度好友【"+nickName+"】下单啦";
//							    		修改2015-12-2 APP2.0版本  fengl
							    		relation="【"+nickName+"】下单啦";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.getDeep()==2){
//								    	relation="您的二度好友【"+nickName+"】下单啦";
//								    	修改2015-12-2 APP2.0版本  fengl
								    	nickName=NickNameHelper.getFirstNickName(accountRelation.getAccountCode(),gcReckonOrderInfo.getAccountCode());
								    	relation="【"+nickName+"】的好友下单啦";
								    	push_range="449747220002";
								    }
								}
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(accountRelation.getAccountCode());
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010001");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle(relation);
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
								
							    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元哦~";
							    addSinglePushCommentInput.setRelationCode(relationCode);
							    addSinglePushCommentInput.setContent(content);
								
								if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"605bbad5e1a54bac9e3ce3960053be56\" "
										+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
										+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.getAccountCode()))<1){
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
				}
			}
		}

		// 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(traderLock)) {
			WebHelper.unLock(traderLock);
		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向预返利流程 第四版代码，20151231修改 下单时通过荣云发单聊消息;预存款余额预警实时提醒
	 * 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateInForForth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();
		listExec.add("RebateInForth");
//		String sAccountCode = reckonStep.getAccountCode();

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
		
		//商户判断
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		 
		//商户不存在或者状态不可用，不返利
		if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
			listExec.add(upInfo(918512020, sManageCode));
			mWebResult.inErrorMessage(918512020,sManageCode);
		}
		
		//订单创建时间>停止返利时间,并且商户状态是停用状态,则此订单不再给用户返利
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
		
		//开始锁定执行流程编号 防止并发执行
		String traderLock = "";
		if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
			traderLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
			if (StringUtils.isEmpty(traderLock)) {
				listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
				mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
			}
		}
		
		//为预防并发操作，再次获取商户预存款
		BigDecimal gurranteeBalance = BigDecimal.ZERO;
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			if(gcTraderInfo!=null){
				gurranteeBalance = gcTraderInfo.getGurranteeBalance();
			}
		}
		
		//预存款余额预警实时提醒 panwei 2015-12-29
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			TxTraderFoundsService foundsService=new TxTraderFoundsService();
			boolean isRebate=foundsService.validatePreWithdrawShort(appMap.get("trade_code"), gurranteeBalance);
			if(!isRebate){
				listExec.add(upInfo(915805144,String.valueOf(appMap.get("trade_code"))));
				mWebResult.inErrorMessage(915805144, String.valueOf(appMap.get("trade_code")));
			}
		}
		
		//是否可返利标记
		boolean rebateFlag = true;
		//判断商户预存款是否>返利额,大于则返利,否则不返利
		if (mWebResult.upFlagTrue()) {
			//总返利金额
			BigDecimal totalRabateMoney = BigDecimal.ZERO;
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
						
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					if(accountRelation.getDeep() <= iDeepReckon && rabateMoney.compareTo(BigDecimal.ZERO) > 0){
						totalRabateMoney = totalRabateMoney.add(rabateMoney);
					}
				}
			}
			//预存款<关联账户的总返利金额，不能返利
			if(gurranteeBalance.compareTo(totalRabateMoney) < 0){
				rebateFlag = false;
				listExec.add(upInfo(915805142,String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney)));
				mWebResult.inErrorMessage(915805142, String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney));
			}
		}
		
		//返利逻辑
		if (mWebResult.upFlagTrue() && rebateFlag) {
			
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations){
				//每单总返利金额
				BigDecimal skuTotalRabateMoney = BigDecimal.ZERO;
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				// 定义返利日志数组
				List<GcRebateLog> listRebateLogs = new ArrayList<GcRebateLog>();
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					listExec.add(upInfo(918515308,
							accountRelation.getAccountCode(),
							String.valueOf(accountRelation.getDeep())));
					
					//SKU金额可清分金额
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
					
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= (bConsumeMoney.multiply(bScaleReckon)).setScale(2,BigDecimal.ROUND_DOWN);//返利金额保留到分,低于1分钱就不返利
					skuTotalRabateMoney = skuTotalRabateMoney.add(rabateMoney);
					
					//开始定义返利日志
					GcRebateLog gcRebateLog=new GcRebateLog();
					gcRebateLog.setLogCode(WebHelper.upCode("GCRBL"));
					gcRebateLog.setScaleReckon(bScaleReckon);
					gcRebateLog.setRebateMoney(rabateMoney);//返利金额
					gcRebateLog.setChangeCodes(reckonStep.getStepCode());
					gcRebateLog.setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
					gcRebateLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
					
					// 判断如果能清分到级别 再次添加信息
					if (accountRelation.getDeep() <= iDeepReckon&& gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
					
	                    listRebateLogs.add(gcRebateLog);
						listExec.add(upInfo(918512002,
								accountRelation.getAccountCode(),
								bConsumeMoney.toString(), bScaleReckon.toString(),
								gcRebateLog.getRebateMoney().toString()));
					}
				}
				
				// 判断如果数量大于0
				if (listRebateLogs.size() > 0) {
					for (int i = 0, j = listRebateLogs.size(); i < j; i++) {
						listRebateLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listRebateLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listRebateLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listRebateLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listRebateLogs.get(i).setOrderRebateTime(
								gcReckonOrderInfo.getOrderCreateTime());
						listRebateLogs.get(i).setRebateChangeType("4497465200140001");//订单预返利
						listRebateLogs.get(i).setFlagStatus(1);
						listRebateLogs.get(i).setRebateType("4497465200150001");//预返利计算类型
					}
					
					if (mWebResult.upFlagTrue()){
						//开始扣除商户预存款
						for (int k = 0; k<listRebateLogs.size();k++){
							
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(listRebateLogs.get(k).getRebateMoney().negate());
							gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
							gcTraderFoundsChangeLog.setRelationCode(listRebateLogs.get(k).getLogCode());
							gcTraderFoundsChangeLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
							gcTraderDepositLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							gcTraderDepositLog.setAccountCode(listRebateLogs.get(k).getAccountCode());
							gcTraderDepositLog.setOrderAccountCode(listRebateLogs.get(k).getOrderAccountCode());
							gcTraderDepositLog.setRelationLevel(listRebateLogs.get(k).getRelationLevel());
							gcTraderDepositLog.setSkuCode(listRebateLogs.get(k).getSkuCode());
						    gcTraderDepositLog.setDeposit(listRebateLogs.get(k).getRebateMoney().negate());
						    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
						    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
						    gcTraderDepositLog.setRelationCode(listRebateLogs.get(k).getLogCode());
						    addTraderDepositOrderLog(gcTraderDepositLog);
						}
						
						txGroupAccountService.updateAccount(null, null,listRebateLogs);
	                    
						//创建更新返利订单表
						insertRebateOrder(listRebateLogs,sManageCode);
					}
					
					if (mWebResult.upFlagTrue()) {
						//push下单消息
						try {
								String relation="";
								String push_range="";
								String relationCode="";
								if(accountRelation.getDeep()==0){
									relation="您下单成功啦";
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
									map.put("account_code_wo", accountRelation.getAccountCode());
									map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//									String nickName=NickNameHelper.getNickName(map);
									String nickName=NickNameHelper.checkToNickName(map);
									
							    	if(accountRelation.getDeep()==1){
//								    	relation="您的一度好友【"+nickName+"】下单啦";
//							    		修改2015-12-2 APP2.0版本  fengl
							    		relation="【"+nickName+"】下单啦";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.getDeep()==2){
//								    	relation="您的二度好友【"+nickName+"】下单啦";
//								    	修改2015-12-2 APP2.0版本  fengl
								    	nickName=NickNameHelper.getFirstNickName(accountRelation.getAccountCode(),gcReckonOrderInfo.getAccountCode());
								    	relation="【"+nickName+"】的好友下单啦";
								    	push_range="449747220002";
								    }
								}
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(accountRelation.getAccountCode());
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010001");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle(relation);
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
								
							    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元哦~";
							    addSinglePushCommentInput.setRelationCode(relationCode);
							    addSinglePushCommentInput.setContent(content);
								
								if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"605bbad5e1a54bac9e3ce3960053be56\" "
										+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
										+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.getAccountCode()))<1){
									addSinglePushCommentInput.setSendStatus("4497465000070001");
								}
								else{
									addSinglePushCommentInput.setSendStatus("4497465000070002");
								}
								SinglePushComment.addPushComment(addSinglePushCommentInput);
								
								//下单时通过荣云发单聊信息
								if(accountRelation.getDeep()==0){
								}else{
									if(accountRelation.getDeep()==1){
							    		//我memberCode
							    		MDataMap meMemberMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    		//我的上级memberCode
							    		MDataMap oneMemberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	
							    		if(meMemberMap!=null && oneMemberMap !=null){
								    		//通过消息队列发消息
											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
													new MDataMap("fromUserId", meMemberMap.get("member_code"),"toUserId", oneMemberMap.get("member_code"),"objectName","RC:TxtMsg",
															"content","{\"content\":\"我成功下单啦,预计会有"+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元返给你哦！\"}"));
								    	}
									}else if(accountRelation.getDeep()==2){
								    	MDataMap oneMembMap = new MDataMap();
								    	//获取我的上级accountCode
								    	MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",gcReckonOrderInfo.getAccountCode(),"flag_enable","1");
								    	if(oneMap != null){
								    		String oneAcCode = oneMap.get("parent_code");
								    		//获取我的上级的memberCode
								    		oneMembMap=DbUp.upTable("mc_member_info").one("account_code",oneAcCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	}
								    	//获取我上级的上级的memberCode
								    	MDataMap twoMembMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	if(oneMembMap != null && twoMembMap !=null){
								    		//通过消息队列发消息
											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
													new MDataMap("fromUserId", oneMembMap.get("member_code"),"toUserId", twoMembMap.get("member_code"),"objectName","RC:TxtMsg",
															"content","{\"content\":\"我的好友成功下单啦,预计会有"+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元返给你哦！\"}"));
								    	}
									}
								}
								
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(traderLock)) {
			WebHelper.unLock(traderLock);
		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 正向预返利流程 第五版代码,20160218修改 取消退货流程执行后 再次退货时 返利比例取下单时的比例(避免比例不一致产生异常返利金额)
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateInForFifth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult = new MWebResult();

		List<String> listExec = new ArrayList<String>();
		listExec.add("RebateInFifth");
//		String sAccountCode = reckonStep.getAccountCode();
		//是否执行取消退货标记,默认为否
		boolean cancelRetFlag = false;

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
		
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		
		// 判断是否有执行成功的取消退货流程 此时重新执行预返利业务时,返利比例取下单时的比例(避免比例不一致产生异常返利金额)
		if (mWebResult.upFlagTrue()){
			String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050007' ";
			MDataMap mWhereMap=new MDataMap();
			mWhereMap.put("order_code", reckonStep.getOrderCode());
			mWhereMap.put("account_code", reckonStep.getAccountCode());
			if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) > 0){
				cancelRetFlag = true;
			}
		}
		
		//商户判断
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		 
		//商户不存在或者状态不可用，不返利
		if(gcTraderInfo==null||gcTraderInfo.getTraderStatus().equals("4497472500010002")){
			listExec.add(upInfo(918512020, sManageCode));
			mWebResult.inErrorMessage(918512020,sManageCode);
		}
		
		//订单创建时间>停止返利时间,并且商户状态是停用状态,则此订单不再给用户返利
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
		
		//开始锁定执行流程编号 防止并发执行
		String traderLock = "";
		if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
			traderLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
			if (StringUtils.isEmpty(traderLock)) {
				listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
				mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
			}
		}
		
		//为预防并发操作，再次获取商户预存款
		BigDecimal gurranteeBalance = BigDecimal.ZERO;
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			if(gcTraderInfo!=null){
				gurranteeBalance = gcTraderInfo.getGurranteeBalance();
			}
		}
		
		//预存款余额预警实时提醒 panwei 2015-12-29
		if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
			TxTraderFoundsService foundsService=new TxTraderFoundsService();
			boolean isRebate=foundsService.validatePreWithdrawShort(appMap.get("trade_code"), gurranteeBalance);
			if(!isRebate){
				listExec.add(upInfo(915805144,String.valueOf(appMap.get("trade_code"))));
				mWebResult.inErrorMessage(915805144, String.valueOf(appMap.get("trade_code")));
			}
		}
		
		//是否可返利标记
		boolean rebateFlag = true;
		//判断商户预存款是否>返利额,大于则返利,否则不返利
		if (mWebResult.upFlagTrue()) {
			//总返利金额
			BigDecimal totalRabateMoney = BigDecimal.ZERO;
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations) {
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
						
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					//取消退货流程执行后, 此时重新执行预返利业务时,返利比例取下单时的比例
					if(cancelRetFlag){
						GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
						gcRebateLogExample.createCriteria()
						        .andOrderCodeEqualTo(reckonStep.getOrderCode())
						        .andAccountCodeEqualTo(reckonStep.getAccountCode())
						        .andOrderAccountCodeEqualTo(accountRelation.getAccountCode())
						        .andDetailCodeEqualTo(gcReckonOrderDetail.getDetailCode())
						        .andRebateChangeTypeEqualTo("4497465200140001");
						List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
						if(listRebateLogs!=null&&listRebateLogs.size()>0){
							bScaleReckon = listRebateLogs.get(0).getScaleReckon();
						}
					}
					
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					if(accountRelation.getDeep() <= iDeepReckon && rabateMoney.compareTo(BigDecimal.ZERO) > 0){
						totalRabateMoney = totalRabateMoney.add(rabateMoney);
					}
				}
			}
			//预存款<关联账户的总返利金额，不能返利
			if(gurranteeBalance.compareTo(totalRabateMoney) < 0){
				rebateFlag = false;
				listExec.add(upInfo(915805142,String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney)));
				mWebResult.inErrorMessage(915805142, String.valueOf(appMap.get("trade_code")),String.valueOf(gurranteeBalance),String.valueOf(totalRabateMoney));
			}
		}
		
		//返利逻辑
		if (mWebResult.upFlagTrue() && rebateFlag) {
			
			// 循环所有关联关系
			for (AccountRelation accountRelation : listRelations){
				//每单总返利金额
				BigDecimal skuTotalRabateMoney = BigDecimal.ZERO;
				// 定义当前微公社账户信息
				GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
				gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
						accountRelation.getAccountCode());

				List<GcGroupAccount> listAccounts = gcGroupAccountMapper
						.selectByExample(gcGroupAccountExample);
				GcGroupAccount gcGroupAccount = listAccounts.get(0);
				// 定义返利日志数组
				List<GcRebateLog> listRebateLogs = new ArrayList<GcRebateLog>();
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					listExec.add(upInfo(918515308,
							accountRelation.getAccountCode(),
							String.valueOf(accountRelation.getDeep())));
					
					//SKU金额可清分金额
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateLevelInfoTwo(gcGroupAccount.getAccountLevel(), 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),String.valueOf(accountRelation.getDeep()));
					
					// 定义清分比例
					BigDecimal bScaleReckon = groupLevelInfo.getScaleReckon();
					//取消退货流程执行后, 此时重新执行预返利业务时,返利比例取下单时的比例
					if(cancelRetFlag){
						GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
						gcRebateLogExample.createCriteria()
						        .andOrderCodeEqualTo(reckonStep.getOrderCode())
						        .andAccountCodeEqualTo(reckonStep.getAccountCode())
						        .andOrderAccountCodeEqualTo(accountRelation.getAccountCode())
						        .andDetailCodeEqualTo(gcReckonOrderDetail.getDetailCode())
						        .andRebateChangeTypeEqualTo("4497465200140001");
						List<GcRebateLog> listRebateLogList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
						if(listRebateLogList!=null&&listRebateLogList.size()>0){
							bScaleReckon = listRebateLogList.get(0).getScaleReckon();
						}
					}
					
					// 定义清分深度
					int iDeepReckon = groupLevelInfo.getDeepReckon();
					
					//SKU的返利金额
					BigDecimal rabateMoney= (bConsumeMoney.multiply(bScaleReckon)).setScale(2,BigDecimal.ROUND_DOWN);//返利金额保留到分,低于1分钱就不返利
					skuTotalRabateMoney = skuTotalRabateMoney.add(rabateMoney);
					
					//开始定义返利日志
					GcRebateLog gcRebateLog=new GcRebateLog();
					gcRebateLog.setLogCode(WebHelper.upCode("GCRBL"));
					gcRebateLog.setScaleReckon(bScaleReckon);
					gcRebateLog.setRebateMoney(rabateMoney);//返利金额
					gcRebateLog.setChangeCodes(reckonStep.getStepCode());
					gcRebateLog.setSkuCode(StringUtils.isBlank(gcReckonOrderDetail.getSkuCode())?gcReckonOrderDetail.getProductCode():gcReckonOrderDetail.getSkuCode());
					gcRebateLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
					
					// 判断如果能清分到级别 再次添加信息
					if (accountRelation.getDeep() <= iDeepReckon&& gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
					
	                    listRebateLogs.add(gcRebateLog);
						listExec.add(upInfo(918512002,
								accountRelation.getAccountCode(),
								bConsumeMoney.toString(), bScaleReckon.toString(),
								gcRebateLog.getRebateMoney().toString()));
					}
				}
				
				// 判断如果数量大于0
				if (listRebateLogs.size() > 0) {
					for (int i = 0, j = listRebateLogs.size(); i < j; i++) {
						listRebateLogs.get(i).setAccountCode(
								accountRelation.getAccountCode());
						listRebateLogs.get(i).setOrderCode(
								gcReckonOrderInfo.getOrderCode());
						listRebateLogs.get(i).setRelationLevel(
								accountRelation.getDeep());
						listRebateLogs.get(i).setOrderAccountCode(
								gcReckonOrderInfo.getAccountCode());
						listRebateLogs.get(i).setOrderRebateTime(
								gcReckonOrderInfo.getOrderCreateTime());
						listRebateLogs.get(i).setRebateChangeType("4497465200140001");//订单预返利
						listRebateLogs.get(i).setFlagStatus(1);
						listRebateLogs.get(i).setRebateType("4497465200150001");//预返利计算类型
					}
					
					if (mWebResult.upFlagTrue()){
						//开始扣除商户预存款
						for (int k = 0; k<listRebateLogs.size();k++){
							
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
							gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(listRebateLogs.get(k).getRebateMoney().negate());
							gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
							gcTraderFoundsChangeLog.setRelationCode(listRebateLogs.get(k).getLogCode());
							gcTraderFoundsChangeLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							updateTraderDeposit(gcTraderFoundsChangeLog);
							
							//添加保证金订单日志
							GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
							gcTraderDepositLog.setOrderCode(listRebateLogs.get(k).getOrderCode());
							gcTraderDepositLog.setAccountCode(listRebateLogs.get(k).getAccountCode());
							gcTraderDepositLog.setOrderAccountCode(listRebateLogs.get(k).getOrderAccountCode());
							gcTraderDepositLog.setRelationLevel(listRebateLogs.get(k).getRelationLevel());
							gcTraderDepositLog.setSkuCode(listRebateLogs.get(k).getSkuCode());
						    gcTraderDepositLog.setDeposit(listRebateLogs.get(k).getRebateMoney().negate());
						    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
						    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
						    gcTraderDepositLog.setRelationCode(listRebateLogs.get(k).getLogCode());
						    addTraderDepositOrderLog(gcTraderDepositLog);
						}
						
						txGroupAccountService.updateAccount(null, null,listRebateLogs);
	                    
						//创建更新返利订单表
						insertRebateOrder(listRebateLogs,sManageCode);
					}
					
					//取消退货后再次执行时不再发消息
					if (mWebResult.upFlagTrue() && !cancelRetFlag) {
						//push下单消息
						try {
								String relation="";
								String push_range="";
								String relationCode="";
								if(accountRelation.getDeep()==0){
									relation="您下单成功啦";
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
									map.put("account_code_wo", accountRelation.getAccountCode());
									map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//									String nickName=NickNameHelper.getNickName(map);
									String nickName=NickNameHelper.checkToNickName(map);
									
							    	if(accountRelation.getDeep()==1){
//								    	relation="您的一度好友【"+nickName+"】下单啦";
//							    		修改2015-12-2 APP2.0版本  fengl
							    		relation="【"+nickName+"】下单啦";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.getDeep()==2){
//								    	relation="您的二度好友【"+nickName+"】下单啦";
//								    	修改2015-12-2 APP2.0版本  fengl
								    	nickName=NickNameHelper.getFirstNickName(accountRelation.getAccountCode(),gcReckonOrderInfo.getAccountCode());
								    	relation="【"+nickName+"】的好友下单啦";
								    	push_range="449747220002";
								    }
								}
								AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
								addSinglePushCommentInput.setAccountCode(accountRelation.getAccountCode());
								addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
								addSinglePushCommentInput.setType("44974720000400010001");
								
								addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
								addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
								addSinglePushCommentInput.setTitle(relation);
								MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
								if(memberMap!=null){
									addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
								}
								
							    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元哦~";
							    addSinglePushCommentInput.setRelationCode(relationCode);
							    addSinglePushCommentInput.setContent(content);
								
								if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"605bbad5e1a54bac9e3ce3960053be56\" "
										+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
										+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",accountRelation.getAccountCode()))<1){
									addSinglePushCommentInput.setSendStatus("4497465000070001");
								}
								else{
									addSinglePushCommentInput.setSendStatus("4497465000070002");
								}
								SinglePushComment.addPushComment(addSinglePushCommentInput);
								
								//下单时通过荣云发单聊信息
								if(accountRelation.getDeep()==0){
								}else{
									if(accountRelation.getDeep()==1){
							    		//我memberCode
							    		MDataMap meMemberMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
							    		//我的上级memberCode
							    		MDataMap oneMemberMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	
							    		if(meMemberMap!=null && oneMemberMap !=null){
								    		//通过消息队列发消息
//											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//													new MDataMap("fromUserId", meMemberMap.get("member_code"),"toUserId", oneMemberMap.get("member_code"),"objectName","RC:TxtMsg",
//															"content","{\"content\":\"我成功下单啦,预计会有"+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元返给你哦！\"}"));
								    	}
									}else if(accountRelation.getDeep()==2){
								    	MDataMap oneMembMap = new MDataMap();
								    	//获取我的上级accountCode
								    	MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",gcReckonOrderInfo.getAccountCode(),"flag_enable","1");
								    	if(oneMap != null){
								    		String oneAcCode = oneMap.get("parent_code");
								    		//获取我的上级的memberCode
								    		oneMembMap=DbUp.upTable("mc_member_info").one("account_code",oneAcCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	}
								    	//获取我上级的上级的memberCode
								    	MDataMap twoMembMap=DbUp.upTable("mc_member_info").one("account_code",accountRelation.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE);
								    	if(oneMembMap != null && twoMembMap !=null){
								    		//通过消息队列发消息
//											JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//													new MDataMap("fromUserId", oneMembMap.get("member_code"),"toUserId", twoMembMap.get("member_code"),"objectName","RC:TxtMsg",
//															"content","{\"content\":\"我的好友成功下单啦,预计会有"+skuTotalRabateMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"元返给你哦！\"}"));
								    	}
									}
								}
								
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(traderLock)) {
			WebHelper.unLock(traderLock);
		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));

		return mWebResult;
	}
	
	/**
	 * 增加商户保证金订单对账日志
	 * @param gcTraderDepositLog
	 */
	public void addTraderDepositOrderLog(GcTraderDepositLog gcTraderDepositLog) {
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
	 * 更新商户保证金账户
	 * @param gcTraderFoundsChangeLog
	 */
	public void updateTraderDeposit(
			GcTraderFoundsChangeLog gcTraderFoundsChangeLog) {
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
	 * 逆向返利流程
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult rebateBack(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		MWebResult mWebResult = new MWebResult();

		// 判断是否有执行成功的正向或重置返利流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {
            if(DbUp.upTable("gc_reckon_order_step").
				dataCount("order_code=:order_code and flag_success=1 and (exec_type=:exec_type or exec_type=:exec_type_two)", 
					new MDataMap("order_code",reckonStep.getOrderCode(),"exec_type",GroupConst.REBATE_ORDER_EXEC_TYPE_IN,"exec_type_two",GroupConst.REBATE_ORDER_EXEC_TYPE_RESET))<1){
				mWebResult.inErrorMessage(918512003, reckonStep.getStepCode());
			}

		}

		if (mWebResult.upFlagTrue()) {

			//开始处理返利信息
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
			gcRebateLogExample.createCriteria()
			        .andOrderCodeEqualTo(reckonStep.getOrderCode()).andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
			List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
			if(listRebateLogs!=null&&listRebateLogs.size()>0){
				TxGroupAccountService txGroupAccountService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
				for(GcRebateLog item:listRebateLogs){
					GcRebateLog gcRebateLog=new GcRebateLog();
					gcRebateLog.setAccountCode(item.getAccountCode());
					gcRebateLog.setFlagWithdraw(0);
					gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
					gcRebateLog.setOrderCode(item.getOrderCode());
					gcRebateLog.setRebateChangeType("4497465200140002");//取消订单预返利
					gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
					gcRebateLog.setRelationLevel(item.getRelationLevel());
					gcRebateLog.setScaleReckon(item.getScaleReckon());
					gcRebateLog.setChangeCodes(item.getLogCode());
					gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
					gcRebateLog.setRebateType(item.getRebateType());
					gcRebateLog.setFlagStatus(1);
					List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
					listUpdateGcRebateLogs.add(gcRebateLog);
					txGroupAccountService.updateAccount(
							null, null,listUpdateGcRebateLogs);
				}
				// 更新所有的可转入标记为否
				GcRebateLog gcUpdateRebateLog = new GcRebateLog();
				gcUpdateRebateLog.setFlagWithdraw(0);
				gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog,
						gcRebateLogExample);
			}

		}
		//暂时推送退货消息
        	if(mWebResult.upFlagTrue()){
    			try {
    				GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
    				List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
    				if(accountList!=null&&accountList.size()>0){
    					for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
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
								String nickName=NickNameHelper.getNickName(map);
						    	
						    	if(accountRelation.get("relation_level").equals("1")){
							    	relation="您的一度好友【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
							    	relation="您的二度好友【"+nickName+"】退货成功";
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
							
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+new BigDecimal(accountRelation.get("rebate_money")).setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
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
    			} catch (Exception e) {
    				
    			}
    		}
		

		return mWebResult;

	}
	
	/**
	 * 逆向返利流程 于20150825修改 将返利扣掉的钱加回给商户
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult rebateBackForSecond(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultMessage("rebateBackSecond");
		// 判断是否有执行成功的正向或重置返利流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {
            if(DbUp.upTable("gc_reckon_order_step").
				dataCount("order_code=:order_code and flag_success=1 and (exec_type=:exec_type or exec_type=:exec_type_two)", 
					new MDataMap("order_code",reckonStep.getOrderCode(),"exec_type",GroupConst.REBATE_ORDER_EXEC_TYPE_IN,"exec_type_two",GroupConst.REBATE_ORDER_EXEC_TYPE_RESET))<1){
				mWebResult.inErrorMessage(918512003, reckonStep.getStepCode());
			}
		}
		
		if (mWebResult.upFlagTrue()) {

			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
			
			String sManageCode = gcReckonOrderInfo.getManageCode();
			//商户判断
			GcTraderInfo gcTraderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			if(appMap!=null&&appMap.get("trade_code")!=null){
				gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			//商户不存在
			if(gcTraderInfo==null){
				mWebResult.inErrorMessage(918512020,sManageCode);
			}

			//开始处理返利信息
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
			gcRebateLogExample.createCriteria()
			        .andOrderCodeEqualTo(reckonStep.getOrderCode()).andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
			List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
			
			if (mWebResult.upFlagTrue()){
				if(listRebateLogs!=null&&listRebateLogs.size()>0){
					for(GcRebateLog item:listRebateLogs){
						List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
						GcRebateLog gcRebateLog=new GcRebateLog();
						gcRebateLog.setAccountCode(item.getAccountCode());
						gcRebateLog.setFlagWithdraw(0);
						gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
						gcRebateLog.setOrderCode(item.getOrderCode());
						gcRebateLog.setRebateChangeType("4497465200140002");//取消订单预返利
						gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
						gcRebateLog.setRelationLevel(item.getRelationLevel());
						gcRebateLog.setScaleReckon(item.getScaleReckon());
						gcRebateLog.setChangeCodes(item.getLogCode());
						gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
						gcRebateLog.setRebateType(item.getRebateType());
						gcRebateLog.setFlagStatus(1);
						gcRebateLog.setSkuCode(item.getSkuCode());
						gcRebateLog.setDetailCode(item.getDetailCode());
						listUpdateGcRebateLogs.add(gcRebateLog);
						if(listUpdateGcRebateLogs != null && listUpdateGcRebateLogs.size() > 0){
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
						}
					}

					// 更新所有的可转入标记为否
					GcRebateLog gcUpdateRebateLog = new GcRebateLog();
					gcUpdateRebateLog.setFlagWithdraw(0);
					gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog,
							gcRebateLogExample);
				}
				
				if (mWebResult.upFlagTrue()){
					//开始锁定执行流程编号 防止并发执行
					String sLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
					if (StringUtils.isEmpty(sLock)) {
						mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
					}
					
					//重置商户预存款--将保证金加回
					GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
					
					GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
					gcTraderDepositLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andDepositTypeEqualTo("4497472500040001").andFlagStatusEqualTo(1);
					List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
					if(depositLogList!=null&&depositLogList.size()>0){
						for(GcTraderDepositLog gcTraderDepositLog:depositLogList){
							//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重负退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
							GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
							checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
								andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
							List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
							if(checkDepositLogList != null && checkDepositLogList.size() >0){
								//已经退单增加了，不在处理
							}else{
								GcTraderInfo traderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
								//加上对应的保证金金额
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(traderInfo==null||traderInfo.getAccountCode()==null?"":traderInfo.getAccountCode());
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
					
					// 如果锁定成功后 则开始解锁流程
					if (StringUtils.isNotEmpty(sLock)) {
						WebHelper.unLock(sLock);
					}
				}

			}
			
			//暂时推送退货消息
        	if(mWebResult.upFlagTrue()){
    			try {
    				
    				List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
    				if(accountList!=null&&accountList.size()>0){
    					for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
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
								String nickName=NickNameHelper.getNickName(map);
						    	
						    	if(accountRelation.get("relation_level").equals("1")){
							    	relation="您的一度好友【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
							    	relation="您的二度好友【"+nickName+"】退货成功";
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
							
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+new BigDecimal(accountRelation.get("rebate_money")).setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
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

    			} catch (Exception e) {
    				
    			}
    		}

		}
		
		return mWebResult;

	}
	
	/**
	 * 逆向返利流程 第三版 于20151009修改 增加退货服务时间的限制
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateBackForThird(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult = new MWebResult();
		listExec.add("RebateBackThird");
		// 判断是否有执行成功的正向返利(或是取消退货后_c)流程 如果没有则返回错误
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050003%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
			listExec.add(upInfo(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_BACK));
		}
		
		// 判断是否有执行成功的正向或重置返利流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {
            if(DbUp.upTable("gc_reckon_order_step").
				dataCount("order_code=:order_code and flag_success=1 and (exec_type=:exec_type or exec_type=:exec_type_two)", 
					new MDataMap("order_code",reckonStep.getOrderCode(),"exec_type",GroupConst.REBATE_ORDER_EXEC_TYPE_IN,"exec_type_two",GroupConst.REBATE_ORDER_EXEC_TYPE_RESET))<1){
				mWebResult.inErrorMessage(918512003, reckonStep.getStepCode());
				listExec.add(upInfo(918512003,reckonStep.getStepCode()));
			}
		}
		
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
				listExec.add(upInfo(918533009, reckonStep.getStepCode()));
			}
		}
		
		//判断退货时间是否超出设置的服务时间范围
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		//超出退货服务时间标记 false：未超出 true:超出
		boolean rebackFlag = false;
		
		String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
		String sManageCode = gcReckonOrderInfo.getManageCode();
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap != null && appMap.get("trade_code") != null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		
		if(mWebResult.upFlagTrue()){
			int returnGoodsDay = 0;
			if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",gcTraderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}else{
				//商户不存在
				listExec.add(upInfo(918512020,sManageCode));
				mWebResult.inErrorMessage(918512020,sManageCode);
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
			
			//开始处理返利信息
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
			gcRebateLogExample.createCriteria()
			        .andOrderCodeEqualTo(reckonStep.getOrderCode()).andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
			List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
			
			if (mWebResult.upFlagTrue()){
				if(listRebateLogs!=null&&listRebateLogs.size()>0){
					for(GcRebateLog item:listRebateLogs){
						List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
						GcRebateLog gcRebateLog=new GcRebateLog();
						gcRebateLog.setAccountCode(item.getAccountCode());
						gcRebateLog.setFlagWithdraw(0);
						gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
						gcRebateLog.setOrderCode(item.getOrderCode());
						gcRebateLog.setRebateChangeType("4497465200140002");//取消订单预返利
						gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
						gcRebateLog.setRelationLevel(item.getRelationLevel());
						gcRebateLog.setScaleReckon(item.getScaleReckon());
						gcRebateLog.setChangeCodes(item.getLogCode());
						gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
						gcRebateLog.setRebateType(item.getRebateType());
						gcRebateLog.setFlagStatus(1);
						gcRebateLog.setSkuCode(item.getSkuCode());
						gcRebateLog.setDetailCode(item.getDetailCode());
						listUpdateGcRebateLogs.add(gcRebateLog);
						if(listUpdateGcRebateLogs != null && listUpdateGcRebateLogs.size() > 0){
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
						}
					}

					// 更新所有的可转入标记为否
					GcRebateLog gcUpdateRebateLog = new GcRebateLog();
					gcUpdateRebateLog.setFlagWithdraw(0);
					gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog,
							gcRebateLogExample);
				}
				
				if (mWebResult.upFlagTrue()){
					//开始锁定执行流程编号 防止并发执行
					String sLock = "";
					if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
						sLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
						if (StringUtils.isEmpty(sLock)) {
							listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
							mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
						}
					}
					
					//重置商户预存款--将保证金加回
					if (mWebResult.upFlagTrue()){
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						gcTraderDepositLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andDepositTypeEqualTo("4497472500040001").andFlagStatusEqualTo(1);
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						if(depositLogList!=null&&depositLogList.size()>0){
							for(GcTraderDepositLog gcTraderDepositLog:depositLogList){
								//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重负退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
								GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
								checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
									andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
								List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
								if(checkDepositLogList != null && checkDepositLogList.size() >0){
									//已经退单增加了，不在处理
								}else{
									GcTraderInfo traderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(traderInfo==null||traderInfo.getAccountCode()==null?"":traderInfo.getAccountCode());
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
					}
					
					// 如果锁定成功后 则开始解锁流程
					if (StringUtils.isNotEmpty(sLock)) {
						WebHelper.unLock(sLock);
					}
				}

			}
			
			//暂时推送退货消息
        	if(mWebResult.upFlagTrue()){
    			try {
    				
    				List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
    				if(accountList!=null&&accountList.size()>0){
    					for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
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
//						    		relation="您的一度好友【"+nickName+"】退货成功";
//						    		修改2015-12-2 APP2.0版本  fengl
							    	relation="【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
//							    	relation="您的二度好友【"+nickName+"】退货成功";
//							    	修改2015-12-2 APP2.0版本  fengl
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
							
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+new BigDecimal(accountRelation.get("rebate_money")).setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
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

    			} catch (Exception e) {
    				
    			}
    		}

		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;

	}
	
	/**
	 * 逆向返利流程 第四版 于20151231修改 退货时通过荣云发单聊消息
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateBackForForth(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult = new MWebResult();
		listExec.add("RebateBackForth");
		// 判断是否有执行成功的正向返利(或是取消退货后_c)流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()){
			String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050003%' and flag_success=:flag_success ";
			MDataMap mWhereMap=new MDataMap();
			mWhereMap.put("order_code", reckonStep.getOrderCode());
			mWhereMap.put("account_code", reckonStep.getAccountCode());
			mWhereMap.put("flag_success", "1");
			if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
				mWebResult.inErrorMessage(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
				listExec.add(upInfo(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_BACK));
			}
		}
		
		// 判断是否有执行成功的正向或重置返利流程 如果没有则返回错误
		if (mWebResult.upFlagTrue()) {
            if(DbUp.upTable("gc_reckon_order_step").
				dataCount("order_code=:order_code and flag_success=1 and (exec_type=:exec_type or exec_type=:exec_type_two)", 
					new MDataMap("order_code",reckonStep.getOrderCode(),"exec_type",GroupConst.REBATE_ORDER_EXEC_TYPE_IN,"exec_type_two",GroupConst.REBATE_ORDER_EXEC_TYPE_RESET))<1){
				mWebResult.inErrorMessage(918512003, reckonStep.getStepCode());
				listExec.add(upInfo(918512003,reckonStep.getStepCode()));
			}
		}
		
		//判断是否已存在部分退货流程，如果有，返回错误
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.inErrorMessage(918533009, reckonStep.getStepCode());
				listExec.add(upInfo(918533009, reckonStep.getStepCode()));
			}
		}
		
		//判断退货时间是否超出设置的服务时间范围
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		//超出退货服务时间标记 false：未超出 true:超出
		boolean rebackFlag = false;
		
		String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
		String sManageCode = gcReckonOrderInfo.getManageCode();
		GcTraderInfo gcTraderInfo=null;
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		if(appMap != null && appMap.get("trade_code") != null){
			gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
		}
		
		if(mWebResult.upFlagTrue()){
			int returnGoodsDay = 0;
			if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
				MDataMap returnDayMap = DbUp.upTable("gc_trader_rebate").one("trader_code",gcTraderInfo.getTraderCode());
				if(returnDayMap != null && StringUtils.isNotBlank(returnDayMap.get("return_goods_day")) && StringUtils.isNumeric(returnDayMap.get("return_goods_day"))){
					returnGoodsDay = Integer.parseInt(returnDayMap.get("return_goods_day"));
				}
			}else{
				//商户不存在
				listExec.add(upInfo(918512020,sManageCode));
				mWebResult.inErrorMessage(918512020,sManageCode);
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
			
			//开始处理返利信息
			GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
			GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
			gcRebateLogExample.createCriteria()
			        .andOrderCodeEqualTo(reckonStep.getOrderCode()).andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
			List<GcRebateLog> listRebateLogs=gcRebateLogMapper.selectByExample(gcRebateLogExample);
			
			if (mWebResult.upFlagTrue()){
				if(listRebateLogs!=null&&listRebateLogs.size()>0){
					for(GcRebateLog item:listRebateLogs){
						List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
						GcRebateLog gcRebateLog=new GcRebateLog();
						gcRebateLog.setAccountCode(item.getAccountCode());
						gcRebateLog.setFlagWithdraw(0);
						gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
						gcRebateLog.setOrderCode(item.getOrderCode());
						gcRebateLog.setRebateChangeType("4497465200140002");//取消订单预返利
						gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
						gcRebateLog.setRelationLevel(item.getRelationLevel());
						gcRebateLog.setScaleReckon(item.getScaleReckon());
						gcRebateLog.setChangeCodes(item.getLogCode());
						gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
						gcRebateLog.setRebateType(item.getRebateType());
						gcRebateLog.setFlagStatus(1);
						gcRebateLog.setSkuCode(item.getSkuCode());
						gcRebateLog.setDetailCode(item.getDetailCode());
						listUpdateGcRebateLogs.add(gcRebateLog);
						if(listUpdateGcRebateLogs != null && listUpdateGcRebateLogs.size() > 0){
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
						}
					}

					// 更新所有的可转入标记为否
					GcRebateLog gcUpdateRebateLog = new GcRebateLog();
					gcUpdateRebateLog.setFlagWithdraw(0);
					gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog,
							gcRebateLogExample);
				}
				
				if (mWebResult.upFlagTrue()){
					//开始锁定执行流程编号 防止并发执行
					String sLock = "";
					if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
						sLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
						if (StringUtils.isEmpty(sLock)) {
							listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
							mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
						}
					}
					
					//重置商户预存款--将保证金加回
					if (mWebResult.upFlagTrue()){
						GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
						
						GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
						gcTraderDepositLogExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andDepositTypeEqualTo("4497472500040001").andFlagStatusEqualTo(1);
						List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
						if(depositLogList!=null&&depositLogList.size()>0){
							for(GcTraderDepositLog gcTraderDepositLog:depositLogList){
								//逆向返利与逆向清分先后不定,重置预存款的查询条件相同 造成重负退单增加的问题,所以首先判断是否已经退单增加了,没有的情况才退单增加
								GcTraderDepositLogExample checkGcTraderDepositLogExample=new GcTraderDepositLogExample();
								checkGcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(gcTraderDepositLog.getRelationCode()).
									andDepositTypeEqualTo("4497472500040002").andFlagStatusEqualTo(1);
								List<GcTraderDepositLog> checkDepositLogList=gcTraderDepositLogMapper.selectByExample(checkGcTraderDepositLogExample);
								if(checkDepositLogList != null && checkDepositLogList.size() >0){
									//已经退单增加了，不在处理
								}else{
									GcTraderInfo traderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(traderInfo==null||traderInfo.getAccountCode()==null?"":traderInfo.getAccountCode());
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
					}
					
					// 如果锁定成功后 则开始解锁流程
					if (StringUtils.isNotEmpty(sLock)) {
						WebHelper.unLock(sLock);
					}
				}

			}
			
			//暂时推送退货消息
        	if(mWebResult.upFlagTrue()){
    			try {
    				
    				List<MDataMap> accountList= DbUp.upTable("gc_rebate_order").queryByWhere("order_code",reckonStep.getOrderCode());
    				if(accountList!=null&&accountList.size()>0){
    					for(MDataMap accountRelation:accountList){
    						String relation="";
							String push_range="";
							String relationCode="";
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
//						    		relation="您的一度好友【"+nickName+"】退货成功";
//						    		修改2015-12-2 APP2.0版本  fengl
							    	relation="【"+nickName+"】退货成功";
							    	push_range="449747220001";
							    }
							    else if(accountRelation.get("relation_level").equals("2")){
//							    	relation="您的二度好友【"+nickName+"】退货成功";
//							    	修改2015-12-2 APP2.0版本  fengl
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
							
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+new BigDecimal(accountRelation.get("rebate_money")).setScale(2,BigDecimal.ROUND_HALF_UP)+"元已被扣除~";
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
//										JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//												new MDataMap("fromUserId", meMemberMap.get("member_code"),"toUserId", oneMemberMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我申请退单了,下次在帮你赚钱吧！\"}"));
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
//										JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnGroupSendIM,"",
//												new MDataMap("fromUserId", oneMembMap.get("member_code"),"toUserId", twoMembMap.get("member_code"),"objectName","RC:TxtMsg","content","{\"content\":\"我的好友退单了,下次在帮你赚钱吧！\"}"));
							    	}
							    }
							}
							
    					}
    				}

    			} catch (Exception e) {
    				
    			}
    		}

		}
		
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;

	}
	
	/**
	 * 正向预返利流程，这个是根据正式返利（清分）重新生成预返利日志 
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult resetRebate(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		MWebResult mWebResult=new MWebResult();
		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		GcReckonLogMapper gcReckonLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
		gcReckonLogExample.createCriteria()
				.andOrderAccountCodeEqualTo(reckonStep.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001");
		List<GcReckonLog> reckonLogList=gcReckonLogMapper.selectByExample(gcReckonLogExample);
		
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
		gcRebateLogExample.createCriteria().andOrderAccountCodeEqualTo(reckonStep.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode())
				.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
		List<GcRebateLog> gcRebateLogsList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());
		List<String> accountList=new ArrayList<String>();
		if(reckonLogList!=null&&reckonLogList.size()>0){
			//先取出各个account_code
			for(GcReckonLog item:reckonLogList){
				if(!accountList.contains(item.getAccountCode())){
					accountList.add(item.getAccountCode());
				}
			}
			//判断每个人的返利情况
			for(String account_code:accountList){
				List<GcReckonLog> accountReckonList=new ArrayList<GcReckonLog>();
				for(GcReckonLog item:reckonLogList){
					if(item.getAccountCode().equals(account_code)){
						accountReckonList.add(item);
					}
				}
				List<GcRebateLog> accountRebateList=new ArrayList<GcRebateLog>();
				if(gcRebateLogsList!=null&&gcRebateLogsList.size()>0){
					for(GcRebateLog item:gcRebateLogsList){
						if(item.getAccountCode().equals(account_code)){
							accountRebateList.add(item);
						}
					}
				}
				BigDecimal newMoney=BigDecimal.ZERO;
				if(accountRebateList!=null&&accountRebateList.size()>0){
					newMoney=accountRebateList.get(0).getRebateMoney();
				}
				//是否重置
				boolean resetFlag=false;
				if(accountReckonList.size()>1){
					resetFlag=true;
				}
				else if(accountReckonList.size()==1&&accountRebateList.size()==1){
					if(accountReckonList.get(0).getReckonMoney().compareTo(accountRebateList.get(0).getRebateMoney())!=0){
						resetFlag=true;
					}
				}
				else if(accountRebateList.size()==0){
					resetFlag=true;
				}
				
				if(resetFlag){
					//先将原先记录重置掉
					List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
					for(GcRebateLog item:accountRebateList){
						GcRebateLog gcRebateLog=new GcRebateLog();
						gcRebateLog.setAccountCode(item.getAccountCode());
						gcRebateLog.setFlagWithdraw(0);
						gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
						gcRebateLog.setOrderCode(item.getOrderCode());
						gcRebateLog.setRebateChangeType("4497465200140003");//重置订单预返利
						gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
						gcRebateLog.setRelationLevel(item.getRelationLevel());
						gcRebateLog.setScaleReckon(item.getScaleReckon());
						gcRebateLog.setChangeCodes(item.getLogCode());
						gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
						gcRebateLog.setRebateType(item.getRebateType());
						gcRebateLog.setFlagStatus(1);
						listUpdateGcRebateLogs.add(gcRebateLog);
					}
					if(listUpdateGcRebateLogs.size()>0){
						txGroupAccountService.updateAccount(
								null, null,listUpdateGcRebateLogs);
					}
					
					// 更新所有的可转入标记为否,状态为不可用
					GcRebateLog gcUpdateRebateLog = new GcRebateLog();
					GcRebateLogExample upExample=new GcRebateLogExample();
					upExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(account_code)
					.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
					gcUpdateRebateLog.setFlagWithdraw(0);
					gcUpdateRebateLog.setFlagStatus(0);
					gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog, upExample);
					
					//再生成新的记录
					List<GcRebateLog> rebateLogs=new ArrayList<GcRebateLog>();
					newMoney=BigDecimal.ZERO;
					for(GcReckonLog gcReckonLog:accountReckonList){
						GcRebateLog gcRebateLog=new GcRebateLog();
						gcRebateLog.setAccountCode(gcReckonLog.getAccountCode());
						gcRebateLog.setOrderAccountCode(gcReckonLog.getOrderAccountCode());
						gcRebateLog.setOrderCode(gcReckonLog.getOrderCode());
						gcRebateLog.setRebateMoney(gcReckonLog.getReckonMoney());
						gcRebateLog.setScaleReckon(gcReckonLog.getScaleReckon());
						gcRebateLog.setRelationLevel(gcReckonLog.getRelationLevel());
						gcRebateLog.setRebateChangeType("4497465200140001");//订单预返利
						gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
						gcRebateLog.setFlagWithdraw(1);
						gcRebateLog.setRebateType("4497465200150002");//订单正式返利计算类型
						gcRebateLog.setFlagStatus(1);
						gcRebateLog.setChangeCodes(reckonStep.getStepCode()+","+gcReckonLog.getLogCode());
						rebateLogs.add(gcRebateLog);
						newMoney=newMoney.add(gcReckonLog.getReckonMoney());
					}
					if(rebateLogs.size()>0){
						txGroupAccountService.updateAccount(null, null, rebateLogs);
					}
					
					GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
					GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
					gcRebateOrderExample.createCriteria().andAccountCodeEqualTo(account_code).andOrderCodeEqualTo(reckonStep.getOrderCode());
					
					//是否已插入返利订单表，否则插入，已插入则检查更新相应字段
					if(gcRebateOrderMapper.countByExample(gcRebateOrderExample)==0){
						insertRebateOrder(rebateLogs,gcReckonOrderInfo.getManageCode());
					}
					else{
						//更新返利字段、状态
					    GcRebateOrder gcRebateOrder=updateRebateOrder(reckonStep.getOrderCode(),account_code);
					    if(gcRebateOrder!=null){
					    	gcRebateOrder.setRebateMoney(newMoney);
							gcRebateOrderMapper.updateByExampleSelective(gcRebateOrder, gcRebateOrderExample);
					    }
					    
					}
					
				}
				//push签收消息
				try {
					AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
					addSinglePushCommentInput.setAccountCode(account_code);
					addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
					addSinglePushCommentInput.setType("44974720000400010001");
					
					addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
					addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
					MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",account_code,"manage_code","SI2011","flag_enable","1");
					if(memberMap!=null){
						addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
					}

					String relationCode="";
			    	MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code","SI2011");
			    	if(accountMap!=null){
			    		relationCode=accountMap.get("member_code");
			    	}
			    	else{
			    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode());
			    		if(otherMap!=null){
			    			relationCode=otherMap.get("member_code");
			    		}
			    	}
			        String nickName="";
			        if(accountReckonList.get(0).getRelationLevel()!=0){
			        	Map<String, String> map=new HashMap<String, String>();
						map.put("member_code",relationCode );
						map.put("account_code_wo", accountReckonList.get(0).getAccountCode());
						map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
						nickName=NickNameHelper.getNickName(map);
			        }
			    	
			    
				    String relation="您已成功签收";
				    String push_range="449747220003";
				    if(accountReckonList.get(0).getRelationLevel()==1){
				    	relation="您的一度好友【"+nickName+"】签收啦";
				    	push_range="449747220001";
				    }
				    else if(accountReckonList.get(0).getRelationLevel()==2){
				    	relation="您的二度好友【"+nickName+"】签收啦";
				    	push_range="449747220002";
				    }
				    String date=DateHelper.upDateTimeAdd(DateHelper.parseDate(accountReckonList.get(0).getOrderReckonTime()), Calendar.DATE, 9).substring(0, 10);
				    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+newMoney+"元哦，钱钱一星期后就可提现喽~";
				    addSinglePushCommentInput.setContent(content);
				    addSinglePushCommentInput.setTitle(relation);
				    addSinglePushCommentInput.setRelationCode(relationCode);
				    if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"03ad602d623d4c0c97469245292e75f5\" "
							+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
							+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",account_code))<1){
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
		}
		return mWebResult;
	}
	
	/**
	 * 正向重置返利流程第二版,于20150825修改 根据正式返利（清分）重新生成预返利日志 ,重置商户预存款
	 * 
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doResetRebateForSecond(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		MWebResult mWebResult=new MWebResult();
		mWebResult.setResultMessage("resetSecond");
		List<String> listExec = new ArrayList<String>();
		//判断是否有成功的预返利流程,没有返回错误
		String sWhere = " order_code=:order_code and account_code=:account_code and exec_type like '4497465200050003%' and flag_success=:flag_success ";
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("order_code", reckonStep.getOrderCode());
		mWhereMap.put("account_code", reckonStep.getAccountCode());
		mWhereMap.put("flag_success", "1");
		if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
			mWebResult.inErrorMessage(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_RESET);
			listExec.add(upInfo(915805143, reckonStep.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_RESET));
		}
		// 判断是否有执行成功的逆向返利流程 如果有则不在执行后续流程，成功标记置为1
		if(mWebResult.upFlagTrue()){
			if (DbUp.upTable("gc_reckon_order_step").count("order_code",
					reckonStep.getOrderCode(), "flag_success", "1",
					"exec_type", GroupConst.REBATE_ORDER_EXEC_TYPE_BACK)>0) {
				mWebResult.setResultMessage(bInfo(918512007, reckonStep.getStepCode()));
				return mWebResult;
			}
		}
		
		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		GcReckonLogMapper gcReckonLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
		gcReckonLogExample.createCriteria()
				.andOrderAccountCodeEqualTo(reckonStep.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode()).andReckonChangeTypeEqualTo("4497465200030001");
		List<GcReckonLog> reckonLogList=gcReckonLogMapper.selectByExample(gcReckonLogExample);
		
		GcRebateLogMapper gcRebateLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateLogMapper");
		GcRebateLogExample gcRebateLogExample=new GcRebateLogExample();
		gcRebateLogExample.createCriteria().andOrderAccountCodeEqualTo(reckonStep.getAccountCode()).andOrderCodeEqualTo(reckonStep.getOrderCode())
				.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
		List<GcRebateLog> gcRebateLogsList=gcRebateLogMapper.selectByExample(gcRebateLogExample);
		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep.getOrderCode());
		
		String sManageCode = gcReckonOrderInfo.getManageCode();
		//商户判断
		GcTraderInfo gcTraderInfo=null;
		if(mWebResult.upFlagTrue()){
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			if(appMap!=null&&appMap.get("trade_code")!=null){
				gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			//商户不存在
			if(gcTraderInfo==null){
				mWebResult.inErrorMessage(918512020,sManageCode);
				listExec.add(upInfo(918512020, sManageCode));
			}
		}
		
		if(mWebResult.upFlagTrue()){
			
			List<String> accountList=new ArrayList<String>();
			List<String> reckonAccountList=new ArrayList<String>();
			List<String> rebateAccountList=new ArrayList<String>();
			//以reckonList为基准
			if(reckonLogList!=null&&reckonLogList.size()>0){
				//先取出各个account_code
				for(GcReckonLog item:reckonLogList){
					if(!reckonAccountList.contains(item.getAccountCode())){
						reckonAccountList.add(item.getAccountCode());
					}
				}
				for(GcRebateLog item:gcRebateLogsList){
					if(!rebateAccountList.contains(item.getAccountCode())){
						rebateAccountList.add(item.getAccountCode());
					}
				}
				for(String string:rebateAccountList){
					if(!accountList.contains(string)){
						accountList.add(string);
					}
				}
				for(String string:reckonAccountList){
					if(!accountList.contains(string)){
						accountList.add(string);
					}
				}
				//判断每个人的返利情况
				for(String account_code:accountList){
					//重置标记
					boolean resetFlag=false;
					
					List<GcReckonLog> accountReckonList=new ArrayList<GcReckonLog>();
					for(GcReckonLog item:reckonLogList){
						if(item.getAccountCode().equals(account_code)){
							accountReckonList.add(item);
						}
					}
					List<GcRebateLog> accountRebateList=new ArrayList<GcRebateLog>();
					if(gcRebateLogsList!=null&&gcRebateLogsList.size()>0){
						for(GcRebateLog item:gcRebateLogsList){
							if(item.getAccountCode().equals(account_code)){
								accountRebateList.add(item);
							}
						}
					}
					//不重置情况下的推送金额
					BigDecimal newMoney=BigDecimal.ZERO;
					if(accountRebateList!=null&&accountRebateList.size()>0){
						for(GcRebateLog item:accountRebateList){
							newMoney=newMoney.add(item.getRebateMoney());
						}
					}
					
					//rebate里没有，reckon里有的account_code
                    if(!rebateAccountList.contains(account_code)){
						
						resetFlag=true;
					}
                    //rebate里有，但reckon里没有的account_code
                    if(!reckonAccountList.contains(account_code)){
                    	resetFlag=true;
                    }
					
					
					if(!resetFlag){
						List<String> rebateDetailCodeList=new ArrayList<String>();
						for(GcRebateLog log : gcRebateLogsList){
							if(StringUtils.isNotBlank(log.getDetailCode()) && !rebateDetailCodeList.contains(log.getDetailCode())){
								rebateDetailCodeList.add(log.getDetailCode());
							}
						}
						
						for(GcReckonLog itemReckon:reckonLogList){
							String reckonDetailCode = itemReckon.getDetailCode();
							//reckon中存在 但是中rebate不存在,需重置
							if(StringUtils.isNotBlank(reckonDetailCode) && !rebateDetailCodeList.contains(reckonDetailCode)){
								resetFlag = true;
								break;
							}
						}
					}
					
					if(!resetFlag){
						List<String> reckonDetailCodeList=new ArrayList<String>();
						for(GcReckonLog log : reckonLogList){
							if(StringUtils.isNotBlank(log.getDetailCode()) && !reckonDetailCodeList.contains(log.getDetailCode()) ){
								reckonDetailCodeList.add(log.getDetailCode());
							}
						}
						
						for(GcRebateLog itemRebate:gcRebateLogsList){
							String rebateDetailCode = itemRebate.getDetailCode();
							//rebate中存在 但是reckon中不存在,需重置
							if(StringUtils.isNotBlank(rebateDetailCode) && !reckonDetailCodeList.contains(rebateDetailCode)){
								resetFlag = true;
								break;
							}
						}
					}
					
					if(!resetFlag){
						@SuppressWarnings("rawtypes")
						HashMap<String, List<HashMap<String, List<Comparable>>>> recAcountMap = new HashMap<String, List<HashMap<String, List<Comparable>>>>();
						@SuppressWarnings("rawtypes")
						HashMap<String, List<Comparable>> recDetailMap = new HashMap<String, List<Comparable>>();
						for(GcReckonLog itemReckon:reckonLogList){
							String reckonAcountCode = itemReckon.getAccountCode();
							String reckonDetailCode = itemReckon.getDetailCode();
							BigDecimal reckonMoney = itemReckon.getReckonMoney();
							
							//reckon 按照accountCode分组，组内在按照detailCode分组,便于与rebate中的数据进行比较来判断是否需要重置
							@SuppressWarnings("rawtypes")
							List<HashMap<String, List<Comparable>>> recDetailMapList= new ArrayList<HashMap<String, List<Comparable>>>();
							if(StringUtils.isNotBlank(reckonAcountCode)){
								if(recAcountMap.containsKey(reckonAcountCode)){
									@SuppressWarnings("rawtypes")
									List<HashMap<String, List<Comparable>>> newRecDetailMapList= new ArrayList<HashMap<String, List<Comparable>>>();
									@SuppressWarnings("rawtypes")
									List<HashMap<String, List<Comparable>>> oldRecDetailMapList = recAcountMap.get(reckonAcountCode);
									
									if(recDetailMap.containsKey(reckonDetailCode)){
										@SuppressWarnings("rawtypes")
										List<Comparable> recDetailListOld = recDetailMap.get(reckonDetailCode);
										recDetailListOld.add(reckonMoney);
										recDetailMap.put(reckonDetailCode, recDetailListOld);
									}else{
										@SuppressWarnings("rawtypes")
										List<Comparable> recDetailListNew = new ArrayList<Comparable>();
										recDetailListNew.add(reckonMoney);
										recDetailMap.put(reckonDetailCode, recDetailListNew);
									}
									newRecDetailMapList.add(recDetailMap);
									oldRecDetailMapList.addAll(newRecDetailMapList);
									recAcountMap.put(reckonAcountCode, oldRecDetailMapList);
								}else{
									//account不同时 新建recDetailMapmap,否则会给相同的detailCode追加多个money
									recDetailMap = new HashMap<String, List<Comparable>>();
									@SuppressWarnings("rawtypes")
									HashMap<String, List<Comparable>> recDetailMapNew = new HashMap<String, List<Comparable>>();
									@SuppressWarnings("rawtypes")
									List<Comparable> recDetailListNew = new ArrayList<Comparable>();
									recDetailListNew.add(reckonMoney);
									recDetailMapNew.put(reckonDetailCode, recDetailListNew);
									recDetailMapList.add(recDetailMapNew);
									recAcountMap.put(reckonAcountCode, recDetailMapList);
								}
							}
						}
						
						for(GcRebateLog itemRebate:gcRebateLogsList){
							String rebateAcountCode = itemRebate.getAccountCode();
							String rebateDetailCode = itemRebate.getDetailCode();
							BigDecimal rebateMoney = itemRebate.getRebateMoney();
							
							if(StringUtils.isNotBlank(rebateAcountCode)){
								@SuppressWarnings("rawtypes")
								List<HashMap<String, List<Comparable>>> recDetailMapList = recAcountMap.get(rebateAcountCode);
								if(recDetailMapList != null && recDetailMapList.size()>0){
									for(int i = 0;i<recDetailMapList.size();i++){
										@SuppressWarnings("rawtypes")
										List<Comparable> recDetailList = recDetailMapList.get(i).get(rebateDetailCode);
										if(recDetailList != null && recDetailList.size()>0){
											//reckon中的detailCode是多个(循环升级时可能造成此情况),需重置
											if( recDetailList.size()>1){
												resetFlag = true;
												break;
											}else{
												//reckon中的detailCode是1个但是金额不等,需重置
												BigDecimal reckonMoney = (BigDecimal)recDetailList.get(0);
												if(rebateMoney.compareTo(reckonMoney) != 0){
													resetFlag = true;
													break;
												}
											}
										}
									}
								}
							}
							
							if(resetFlag){
								break;
							}
						}
					}
					
					if(resetFlag){
						//先将原先记录重置掉
						List<GcRebateLog> listUpdateGcRebateLogs = new ArrayList<GcRebateLog>();
						for(GcRebateLog item:accountRebateList){
							GcRebateLog gcRebateLog=new GcRebateLog();
							gcRebateLog.setAccountCode(item.getAccountCode());
							gcRebateLog.setFlagWithdraw(0);
							gcRebateLog.setOrderAccountCode(item.getOrderAccountCode());
							gcRebateLog.setOrderCode(item.getOrderCode());
							gcRebateLog.setRebateChangeType("4497465200140003");//重置订单预返利
							gcRebateLog.setRebateMoney(BigDecimal.ZERO.subtract(item.getRebateMoney()));
							gcRebateLog.setRelationLevel(item.getRelationLevel());
							gcRebateLog.setScaleReckon(item.getScaleReckon());
							gcRebateLog.setChangeCodes(item.getLogCode());
							gcRebateLog.setOrderRebateTime(item.getOrderRebateTime());
							gcRebateLog.setRebateType(item.getRebateType());
							gcRebateLog.setFlagStatus(1);
							gcRebateLog.setSkuCode(item.getSkuCode());
							gcRebateLog.setDetailCode(item.getDetailCode());
							listUpdateGcRebateLogs.add(gcRebateLog);
						}
						
						if(listUpdateGcRebateLogs.size()>0){
							txGroupAccountService.updateAccount(
									null, null,listUpdateGcRebateLogs);
						}
						
						// 更新所有的可转入标记为否,状态为不可用
						GcRebateLog gcUpdateRebateLog = new GcRebateLog();
						GcRebateLogExample upExample=new GcRebateLogExample();
						upExample.createCriteria().andOrderCodeEqualTo(reckonStep.getOrderCode()).andAccountCodeEqualTo(account_code)
						.andRebateChangeTypeEqualTo("4497465200140001").andFlagStatusEqualTo(1).andFlagWithdrawEqualTo(1);
						gcUpdateRebateLog.setFlagWithdraw(0);
						gcUpdateRebateLog.setFlagStatus(0);
						gcRebateLogMapper.updateByExampleSelective(gcUpdateRebateLog, upExample);
						
						//再生成新的记录
						List<GcRebateLog> rebateLogs=new ArrayList<GcRebateLog>();
						newMoney=BigDecimal.ZERO;//重置后的推送金额
						for(GcReckonLog gcReckonLog:accountReckonList){
							GcRebateLog gcRebateLog=new GcRebateLog();
							String logCode = WebHelper.upCode("GCRBL");
							gcRebateLog.setLogCode(logCode);
							gcRebateLog.setAccountCode(gcReckonLog.getAccountCode());
							gcRebateLog.setOrderAccountCode(gcReckonLog.getOrderAccountCode());
							gcRebateLog.setOrderCode(gcReckonLog.getOrderCode());
							gcRebateLog.setRebateMoney(gcReckonLog.getReckonMoney());
							gcRebateLog.setScaleReckon(gcReckonLog.getScaleReckon());
							gcRebateLog.setRelationLevel(gcReckonLog.getRelationLevel());
							gcRebateLog.setRebateChangeType("4497465200140001");//订单预返利
							gcRebateLog.setOrderRebateTime(gcReckonOrderInfo.getOrderCreateTime());
							gcRebateLog.setFlagWithdraw(1);
							gcRebateLog.setRebateType("4497465200150002");//订单正式返利计算类型
							gcRebateLog.setFlagStatus(1);
							gcRebateLog.setChangeCodes(reckonStep.getStepCode()+","+gcReckonLog.getLogCode());
							gcRebateLog.setSkuCode(gcReckonLog.getSkuCode());
							gcRebateLog.setDetailCode(gcReckonLog.getDetailCode());
							rebateLogs.add(gcRebateLog);
							newMoney=newMoney.add(gcReckonLog.getReckonMoney());
						}
						if(rebateLogs.size()>0){
							txGroupAccountService.updateAccount(null, null, rebateLogs);
						}
						
						GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
						GcRebateOrderExample gcRebateOrderExample=new GcRebateOrderExample();
						gcRebateOrderExample.createCriteria().andAccountCodeEqualTo(account_code).andOrderCodeEqualTo(reckonStep.getOrderCode());
						
						//是否已插入返利订单表，否则插入，已插入则检查更新相应字段
						if(gcRebateOrderMapper.countByExample(gcRebateOrderExample)==0){
							insertRebateOrder(rebateLogs,gcReckonOrderInfo.getManageCode());
						}
						else{
							//更新返利字段、状态
						    GcRebateOrder gcRebateOrder=updateRebateOrder(reckonStep.getOrderCode(),account_code);
						    if(gcRebateOrder!=null){
						    	gcRebateOrder.setRebateMoney(newMoney);
								gcRebateOrderMapper.updateByExampleSelective(gcRebateOrder, gcRebateOrderExample);
						    }
						}
						
						//开始锁定执行流程编号 防止并发执行
						String sLock = WebHelper.addLock(30, gcTraderInfo.getTraderCode());
						if (StringUtils.isEmpty(sLock)) {
							mWebResult.inErrorMessage(918519038, gcTraderInfo.getTraderCode());
							listExec.add(upInfo(918519038, gcTraderInfo.getTraderCode()));
						}
						
						if(mWebResult.upFlagTrue()){
							//重置商户预存款--先将保证金加回
							for(GcRebateLog item:accountRebateList){
								//将保证金加回
								GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
								GcTraderFoundsChangeLogMapper gcTraderFoundsChangeLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderFoundsChangeLogMapper");
								
								GcTraderDepositLogExample gcTraderDepositLogExample=new GcTraderDepositLogExample();
								gcTraderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andDepositTypeEqualTo("4497472500040001");
								List<GcTraderDepositLog> depositLogList=gcTraderDepositLogMapper.selectByExample(gcTraderDepositLogExample);
								if(depositLogList!=null&&depositLogList.size()>0){
									GcTraderDepositLog gcTraderDepositLog=depositLogList.get(0);
									GcTraderInfo traderInfo= txGroupAccountService.getTraderInfo(gcTraderDepositLog.getTraderCode());
									//加上对应的保证金金额
									GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
									gcTraderFoundsChangeLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									gcTraderFoundsChangeLog.setAccountCode(traderInfo==null||traderInfo.getAccountCode()==null?"":traderInfo.getAccountCode());
									gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcTraderDepositLog.getDeposit().negate());//金额是负值,需要转换为正值
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
									
									addDepositLog.setDeposit(gcTraderDepositLog.getDeposit().negate());//金额是负值,需要转换为正值
									addDepositLog.setDepositType("4497472500040002");//退单增加
									addDepositLog.setTraderCode(gcTraderDepositLog.getTraderCode());
									addDepositLog.setRelationCode(gcTraderDepositLog.getRelationCode());
								    txGroupAccountService.addTraderDepositOrderLog(addDepositLog);
								    
									//重置保证金原日志不可用
								    GcTraderFoundsChangeLogExample traderFoundsChangeLogExample = new GcTraderFoundsChangeLogExample();
								    traderFoundsChangeLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andFlagStatusEqualTo(1);
								    GcTraderFoundsChangeLog traderFoundsChangeLog = new GcTraderFoundsChangeLog();
								    traderFoundsChangeLog.setFlagStatus(0);
								    gcTraderFoundsChangeLogMapper.updateByExampleSelective(traderFoundsChangeLog, traderFoundsChangeLogExample);
								    
									GcTraderDepositLogExample traderDepositLogExample=new GcTraderDepositLogExample();
									traderDepositLogExample.createCriteria().andRelationCodeEqualTo(item.getLogCode()).andFlagStatusEqualTo(1);
									GcTraderDepositLog traderDepositLog = new GcTraderDepositLog();
									traderDepositLog.setFlagStatus(0);
									gcTraderDepositLogMapper.updateByExampleSelective(traderDepositLog, traderDepositLogExample);
								}
							}
							
							//重置商户预存款--重新扣除商户预存款
							for(int i=0;i<accountReckonList.size();i++){
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
								GcReckonLog gcReckonLog = accountReckonList.get(i);
								String relationCode = rebateLogs.get(i).getLogCode();//gcRebateLog的logCode
								
								gcTraderFoundsChangeLog.setTraderCode(gcTraderInfo.getTraderCode());
								gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(gcReckonLog.getReckonMoney().negate());//金额是正值,需要转换为负值
								gcTraderFoundsChangeLog.setChangeType("4497472500030003");//订单扣减
								gcTraderFoundsChangeLog.setRelationCode(relationCode);//此处关联的是gcRebateLog的logCode
								gcTraderFoundsChangeLog.setOrderCode(gcReckonLog.getOrderCode());
								updateTraderDeposit(gcTraderFoundsChangeLog);
								
								//添加保证金订单日志
								GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
								gcTraderDepositLog.setOrderCode(gcReckonLog.getOrderCode());
								gcTraderDepositLog.setAccountCode(gcReckonLog.getAccountCode());
								gcTraderDepositLog.setOrderAccountCode(gcReckonLog.getOrderAccountCode());
								gcTraderDepositLog.setRelationLevel(gcReckonLog.getRelationLevel());
								gcTraderDepositLog.setSkuCode(gcReckonLog.getSkuCode());
							    gcTraderDepositLog.setDeposit(gcReckonLog.getReckonMoney().negate());//金额是正值,需要转换为负值
							    gcTraderDepositLog.setDepositType("4497472500040001");//扣减
							    gcTraderDepositLog.setTraderCode(gcTraderInfo.getTraderCode());
							    gcTraderDepositLog.setRelationCode(relationCode);//此处关联的是gcRebateLog的logCode
							    addTraderDepositOrderLog(gcTraderDepositLog);
							}
						}
						
						// 如果锁定成功后 则开始解锁流程
						if (StringUtils.isNotEmpty(sLock)) {
							WebHelper.unLock(sLock);
						}
					}
						//push签收消息
						try {
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(account_code);
							addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
							addSinglePushCommentInput.setType("44974720000400010001");
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",account_code,"manage_code","SI2011","flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
		
							String relationCode="";
					    	MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode(),"manage_code","SI2011");
					    	if(accountMap!=null){
					    		relationCode=accountMap.get("member_code");
					    	}
					    	else{
					    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",gcReckonOrderInfo.getAccountCode());
					    		if(otherMap!=null){
					    			relationCode=otherMap.get("member_code");
					    		}
					    	}
					        String nickName="";
					        if(accountReckonList.get(0).getRelationLevel()!=0){
					        	Map<String, String> map=new HashMap<String, String>();
								map.put("member_code",relationCode );
								map.put("account_code_wo", accountReckonList.get(0).getAccountCode());
								map.put("account_code_ta", gcReckonOrderInfo.getAccountCode());
//								nickName=NickNameHelper.getNickName(map);
								nickName=NickNameHelper.checkToNickName(map);
					        }
					    	
						    String relation="您已成功签收";
						    String push_range="449747220003";
						    if(accountReckonList.get(0).getRelationLevel()==1){
//						    	relation="您的一度好友【"+nickName+"】签收啦";
//						    	修改2015-12-2 APP2.0版本  fengl
						    	relation="【"+nickName+"】签收啦";
						    	push_range="449747220001";
						    }
						    else if(accountReckonList.get(0).getRelationLevel()==2){
//						    	relation="您的二度好友【"+nickName+"】签收啦";
//						    	修改2015-12-2 APP2.0版本  fengl
						    	nickName=NickNameHelper.getFirstNickName(accountReckonList.get(0).getAccountCode(),gcReckonOrderInfo.getAccountCode());
						    	relation="【"+nickName+"】的好友签收啦";
						    	push_range="449747220002";
						    }
//						    String date=DateHelper.upDateTimeAdd(DateHelper.parseDate(accountReckonList.get(0).getOrderReckonTime()), Calendar.DATE, 9).substring(0, 10);
						    String content="订单金额："+gcReckonOrderInfo.getReckonMoney()+"元，预计返利："+newMoney+"元哦，钱钱一星期后就可提现喽~";
						    addSinglePushCommentInput.setContent(content);
						    addSinglePushCommentInput.setTitle(relation);
						    addSinglePushCommentInput.setRelationCode(relationCode);
						    if(DbUp.upTable("gc_account_push_set").dataCount("account_code=:account_code and push_type_id=\"03ad602d623d4c0c97469245292e75f5\" "
									+ "and ((push_type_onoff=\"449747100002\") or (push_type_onoff=\"449747100001\" and account_push_range "
									+ "not like '%"+push_range+"%'))", new MDataMap("account_push_range",push_range,"account_code",account_code))<1){
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
			}
		}
		mWebResult.setResultMessage(StringUtils.join(listExec,
				WebConst.CONST_SPLIT_LINE));
		return mWebResult;
	}
	
	/**
	 * 插入返利订单表
	 * @param rebateList
	 * @return
	 */
	public MWebResult insertRebateOrder(List<GcRebateLog> rebateList,String manageCode){
		MWebResult mWebResult=new MWebResult();
		if(mWebResult.upFlagTrue()){
			if(rebateList!=null&&rebateList.size()>0){
				GcRebateLog oneLog=rebateList.get(0);
				if(DbUp.upTable("gc_rebate_order").count("order_code",oneLog.getOrderCode(),"account_code",oneLog.getAccountCode())>0){
					mWebResult.inErrorMessage(918518001);
				}
				if(mWebResult.upFlagTrue()){
					GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
					BigDecimal rebateMoney=BigDecimal.ZERO;
					for(GcRebateLog gcRebateLog:rebateList){
						rebateMoney=rebateMoney.add(gcRebateLog.getRebateMoney());
					}
					
					GcRebateOrder gcRebateOrder=new GcRebateOrder();
					gcRebateOrder.setUid(WebHelper.upUuid());
					gcRebateOrder.setOrderCode(oneLog.getOrderCode());
					gcRebateOrder.setAccountCode(oneLog.getAccountCode());
					gcRebateOrder.setOrderAccountCode(oneLog.getOrderAccountCode());
					gcRebateOrder.setRelationLevel(oneLog.getRelationLevel());
					gcRebateOrder.setRebateMoney(rebateMoney);
                    gcRebateOrder.setManageCode(manageCode);
					//查询订单当前状态
					GcRebateOrder updateRebateOrder=updateRebateOrder(oneLog.getOrderCode(),oneLog.getAccountCode());
					if(updateRebateOrder!=null){
						gcRebateOrder.setRebateStatus(updateRebateOrder.getRebateStatus());
						gcRebateOrder.setOrderStatus(updateRebateOrder.getOrderStatus());
						gcRebateOrder.setOrderSendTime(updateRebateOrder.getOrderSendTime());
						gcRebateOrder.setOrderFinishTime(updateRebateOrder.getOrderFinishTime());
						gcRebateOrder.setOrderCancelTime(updateRebateOrder.getOrderCancelTime());
					}
					
					gcRebateOrder.setOrderCreateTime(oneLog.getOrderRebateTime());
					gcRebateOrder.setCreateTime(FormatHelper.upDateTime());
					gcRebateOrder.setUqCode(oneLog.getAccountCode()+"_"+oneLog.getOrderCode());
					gcRebateOrderMapper.insertSelective(gcRebateOrder);
				}
				
			}
		}
		return mWebResult;
	}
	
	/**
	 * 通过oc_orderinfo更新返利订单状态
	 * @param orderCode
	 * @return
	 */
	public GcRebateOrder updateRebateOrder(String orderCode,String accountCode){
		//没有HH的取out_order_code,DD的取ordder_code
		
		GcRebateOrder gcRebateOrder=new GcRebateOrder();
		MDataMap orderMap=DbUp.upTable("oc_orderinfo").oneWhere("order_status,update_time", "", "order_code=:order_code or out_order_code=:order_code", "order_code",orderCode);
		if(orderMap==null){
			try {
				MDataMap map=new MDataMap("orderCode",orderCode);
				String sql="select * from gc_sync_order_status where order_code=:orderCode order by zid desc limit 1";
				Map<String, Object> statusMap=DbUp.upTable("gc_sync_order_status").dataSqlOne(sql,map);
				if(statusMap!=null){
					orderMap=new MDataMap();
					orderMap.inAllValues("order_status",statusMap.get("order_status").toString(),"update_time",statusMap.get("update_time").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		if(orderMap!=null){
			String orderStatus=orderMap.get("order_status");
			if(StringUtils.isEmpty(orderStatus)){
				orderStatus="4497153900010002";
			}
			gcRebateOrder.setOrderStatus(orderStatus);
			if(orderStatus.equals("4497153900010001")){//下单成功-未付款 
				gcRebateOrder.setRebateStatus("4497465200170001");//未付款
			}
			else if(orderStatus.equals("4497153900010002")||orderStatus.equals("4497153900010003")){//下单成功-未发货、 已发货
				gcRebateOrder.setRebateStatus("4497465200170002");//已付款
				gcRebateOrder.setOrderSendTime(orderMap.get("update_time"));
			}
			else if(orderStatus.equals("4497153900010004")||orderStatus.equals("4497153900010005")){//已收货、交易成功
				gcRebateOrder.setOrderFinishTime(orderMap.get("update_time"));//签收时间
				if(DbUp.upTable("gc_rebate_log").count("order_code",orderCode,"account_code",accountCode,"rebate_change_type","4497465200140004","flag_status","1")>0){
					gcRebateOrder.setRebateStatus("4497465200170004");//已返利
				}
				else{
					gcRebateOrder.setRebateStatus("4497465200170002");//已付款
				}
			}
			else if(orderStatus.equals("4497153900010006")){//交易失败
				//未转入提现账户的更新为已取消
				if(DbUp.upTable("gc_rebate_log").count("order_code",orderCode,"account_code",accountCode,"rebate_change_type","4497465200140004","flag_status","1")<1){
					gcRebateOrder.setRebateStatus("4497465200170003");//已取消
					gcRebateOrder.setOrderCancelTime(orderMap.get("update_time"));
				}
				
			}
		}
		else{
			gcRebateOrder=null;
		}
		
		return gcRebateOrder;
	}

}
