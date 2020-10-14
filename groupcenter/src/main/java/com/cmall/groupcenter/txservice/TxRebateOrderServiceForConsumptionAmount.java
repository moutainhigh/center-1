package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcActiveLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcActiveMonthMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupLevelMapper;
import com.cmall.dborm.txmapper.groupcenter.GcLevelLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcRebateOrderMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderFoundsChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderInfoMapper;
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
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfoExample;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.GroupLevelInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.ReckonOrderSupport;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DataConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 按照消费金额返利和清分相关
 * @author gaoYang
 * @date 2016年3月7日下午2:31:48
 *
 */
public class TxRebateOrderServiceForConsumptionAmount  extends BaseClass{

	/**
	 * 按照消费金额返利
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public MWebResult doRebateInByMoneyForOne(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {

		MWebResult mWebResult = new MWebResult();
		List<String> listExec = new ArrayList<String>();
		listExec.add("RebateInByMoneyForOne");
		
		//是否执行取消退货标记,默认为否
		boolean cancelRetFlag = false;

		GcReckonOrderInfo gcReckonOrderInfo = upGcReckonOrderInfo(reckonStep
				.getOrderCode());

		// 获取订单明细
		List<GcReckonOrderDetail> gcReckonOrderDetails = upGcReckonOrderDetail(reckonStep
				.getOrderCode());
		
		String sManageCode = gcReckonOrderInfo.getManageCode();
		//订单金额
		BigDecimal orderMoney = gcReckonOrderInfo.getOrderMoney();

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
		
		//预存款余额预警实时提醒
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
				
				for (GcReckonOrderDetail gcReckonOrderDetail : gcReckonOrderDetails) {
					
					BigDecimal bConsumeMoney = gcReckonOrderDetail.getSumReckonMoney();
					String sSkuCode=gcReckonOrderDetail.getSkuCode();
					if(StringUtils.isBlank(sSkuCode)){
						sSkuCode=gcReckonOrderDetail.getProductCode();
					}
					
					// 开始获取当前级别信息的缓存信息
					GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateScaleByMoneyForOne( 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),
							String.valueOf(accountRelation.getDeep()),
							String.valueOf(accountRelation.getAccountCode()),
							String.valueOf(reckonStep.getOrderCode()),orderMoney);
						
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
					
					//SKU的返利金额
					BigDecimal rabateMoney= bConsumeMoney.multiply(bScaleReckon);
					if(rabateMoney.compareTo(BigDecimal.ZERO) > 0){
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
					groupLevelInfo=ReckonOrderSupport.INSTANCE.upRebateScaleByMoneyForOne( 
							sManageCode,sSkuCode,gcReckonOrderInfo.getOrderCreateTime(),
							String.valueOf(accountRelation.getDeep()),
							String.valueOf(accountRelation.getAccountCode()),
							String.valueOf(reckonStep.getOrderCode()),orderMoney);
					
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
					
					// 判断清分金额>0 再次添加信息
					if (gcRebateLog.getRebateMoney().compareTo(BigDecimal.ZERO) > 0) {
					
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
									String nickName=NickNameHelper.checkToNickName(map);
									
							    	if(accountRelation.getDeep()==1){
							    		relation="【"+nickName+"】下单啦";
								    	push_range="449747220001";
								    }
								    else if(accountRelation.getDeep()==2){
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


	private String upInfo(long lInfoCode, String... sParams) {

		return FormatHelper.upDateTime() + bInfo(lInfoCode, sParams);
	}


	/**
	 * 获取清分订单详情信息
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

}
