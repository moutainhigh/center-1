package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcMemberBankMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderLogMapper;
import com.cmall.dborm.txmapper.membercenter.McExtendInfoHomehasMapper;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccount;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccountExample;
import com.cmall.dborm.txmodel.groupcenter.GcMemberBank;
import com.cmall.dborm.txmodel.groupcenter.GcMemberBankExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomehas;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomehasExample;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.api.WithdrawApiResult;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.LogHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 微公社用户提现
 * 
 * @author chenbin@ichsy.com
 * 
 */
public class TxWithdrawCashService extends BaseClass{

	public WithdrawApiResult doWithdrawCash(String sMemberCode, String sAccountCode,
			String sWithdrawAmount,String bankCode) {
		
		WithdrawApiResult mWebResult=new WithdrawApiResult();
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			mWebResult=doWithdrawCashConfigNew(sMemberCode,sAccountCode,
					sWithdrawAmount,bankCode);
		}else{
			mWebResult=doWithdrawCashNew(sMemberCode,sAccountCode,
					sWithdrawAmount,bankCode);
		}
//		else{
//			mWebResult=doWithdrawCashOld(sMemberCode,sAccountCode,
//					sWithdrawAmount,bankCode);
//		}

		return mWebResult;
	}
	
	//旧版
	public WithdrawApiResult doWithdrawCashOld(String sMemberCode, String sAccountCode,
			String sWithdrawAmount,String bankCode){
		WithdrawApiResult mWebResult=new WithdrawApiResult();
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, sAccountCode);
		if (StringUtils.isEmpty(sLockCode)) {
			mWebResult.inErrorMessage(918505211, sAccountCode);
		}
		
		//判断黑名单
		if(mWebResult.upFlagTrue()){
			GroupService groupService=new GroupService();
			String mobileNo=groupService.getMobileByAccountCode(sAccountCode);
			if(StringUtils.isNotEmpty(mobileNo)){
				if(DbUp.upTable("gc_account_blacklist").count("mobile_no",mobileNo)>0){
					mWebResult.inErrorMessage(915805219);
				}
			}
		}
		
		//判断是否绑定证件
		MDataMap papersMap=DbUp.upTable("gc_member_papers_info").one("account_code",sAccountCode,"flag_enable","1");
		if(mWebResult.upFlagTrue()){
			if(papersMap==null||StringUtils.isBlank(papersMap.get("user_name"))||StringUtils.isBlank(papersMap.get("papers_code"))){
				mWebResult.inErrorMessage(915805333);
			}
		}
		
		//判断银行卡信息
		GcMemberBank gcMemberBank=new GcMemberBank();
		if(mWebResult.upFlagTrue()){
			List<GcMemberBank> bankList=new ArrayList<GcMemberBank>();
			GcMemberBankMapper gcMemberBankMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcMemberBankMapper");
			GcMemberBankExample gcMemberBankExample=new GcMemberBankExample();
			//银行卡编号为空，去数据库取最新信息
			if(StringUtils.isEmpty(bankCode)){
				gcMemberBankExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andFlagEnableEqualTo(1);
				gcMemberBankExample.setOrderByClause(" zid desc ");
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805217);
				}
				
			}
			else{
				//判断银行卡信息是否存在
				gcMemberBankExample.createCriteria().andBankCodeEqualTo(bankCode);
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805215);
				}
			}
			if(bankList!=null&&bankList.size()>0){
				gcMemberBank=bankList.get(0);
			}
		}
		
		GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
		
		//判断有否有未完成的付款单
		if(mWebResult.upFlagTrue()){
		    List<String> statusList=new ArrayList<String>();
		    statusList.add("4497465200070001");//未支付
		    statusList.add("4497465200070002");//已支付
			GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
			//gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			if(bConfig("groupcenter.tax_type").equals("account")){//账户编号方式
				gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			else if(bConfig("groupcenter.tax_type").equals("certificate")){//证件号码姓名
				gcPayOrderInfoExample.createCriteria().andCertificateNoEqualTo(papersMap.get("papers_code")).andMemberNameEqualTo(papersMap.get("user_name")).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			
			List<GcPayOrderInfo> infoList=gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample);
			if(infoList!=null&&infoList.size()>0){
				mWebResult.inErrorMessage(915805216);
			}
		}
		
		// 提现相关
		if (mWebResult.upFlagTrue()) {
			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
			GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
			gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
					sAccountCode);
			// 定义当前微公社账户信息
			GcGroupAccount gcGroupAccount = gcGroupAccountMapper
					.selectByExample(gcGroupAccountExample).get(0);

			BigDecimal withdrawMoney = new BigDecimal(sWithdrawAmount);
			BigDecimal bg0 = BigDecimal.ZERO;
			BigDecimal accountWithdrawMoney = gcGroupAccount
					.getAccountWithdrawMoney();
            if(withdrawMoney.compareTo(bg0)==0){//传0,默认提现全部金额，说是兼容旧版本
				withdrawMoney=accountWithdrawMoney;
			}
			if(withdrawMoney.compareTo(new BigDecimal("10"))==-1){
				mWebResult.inErrorMessage(918505212);
			}
			if(mWebResult.upFlagTrue()){
				if (withdrawMoney.compareTo(bg0) == 1) {
					if (withdrawMoney.compareTo(accountWithdrawMoney) == 0
							|| withdrawMoney.compareTo(accountWithdrawMoney) == -1) {
						
						// 计算扣税
						Map<String,BigDecimal> taxMap = calculateTax(sAccountCode, withdrawMoney,papersMap.get("papers_code"),papersMap.get("user_name"));
						BigDecimal tax=taxMap.get("taxMoney");

						// 计算手续费
						BigDecimal feeMoney = new BigDecimal(0);
						if (withdrawMoney.compareTo(new BigDecimal(100)) == -1) {
							feeMoney = new BigDecimal(1);
						}

						mWebResult.setFeeMoney(feeMoney);
						mWebResult.setTaxMoney(tax);
						mWebResult.setWithdrawMoney(withdrawMoney);
						mWebResult.setRealPayMoney(withdrawMoney.subtract(tax).subtract(feeMoney));
	                    
	                    //查询ld系统编号、姓名
	                    McExtendInfoHomehasMapper mcExtendInfoHomehasMapper=BeansHelper
	        					.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomehasMapper");
	                    McExtendInfoHomehasExample mcExtendInfoHomehasExample=new McExtendInfoHomehasExample();
	                    mcExtendInfoHomehasExample.createCriteria().andMemberCodeEqualTo(sMemberCode);
	                    List<McExtendInfoHomehas> extendInfoList=mcExtendInfoHomehasMapper.selectByExample(mcExtendInfoHomehasExample);
	                    
						// 插入用户付款单据表
						GcPayOrderInfo gcPayOrderInfo = new GcPayOrderInfo();
						gcPayOrderInfo.setUid(WebHelper.upUuid());
						gcPayOrderInfo.setPayOrderCode(WebHelper.upCode("WGS"));
						gcPayOrderInfo.setAccountCode(sAccountCode);
						gcPayOrderInfo.setMemberCode(sMemberCode);
						gcPayOrderInfo.setPayMoney(withdrawMoney.subtract(tax)
								.subtract(feeMoney));
						gcPayOrderInfo.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderInfo.setPayStatus("4497465200070001");// 未支付
						gcPayOrderInfo.setWithdrawMoney(withdrawMoney);
						gcPayOrderInfo.setTaxMoney(tax);
						gcPayOrderInfo.setTotalTaxMoney(taxMap.get("dueTaxMoney"));
						gcPayOrderInfo.setCreateTime(FormatHelper.upDateTime());
						gcPayOrderInfo.setMemberName(papersMap.get("user_name"));
						gcPayOrderInfo.setCardCode(gcMemberBank.getCardCode());
						gcPayOrderInfo.setFeeMoney(feeMoney);
						gcPayOrderInfo.setBeforeWithdrawMoney(accountWithdrawMoney);
						gcPayOrderInfo.setAfterWithdrawMoney(accountWithdrawMoney
								.subtract(withdrawMoney));
						gcPayOrderInfo.setBankCode(gcMemberBank.getBankCode());
						gcPayOrderInfo.setBankName(gcMemberBank.getBankName());
						/*if(StringUtils.isNotEmpty(gcMemberBank.getBankName())){
							MDataMap map=DbUp.upTable("gc_mobile_wallet_bank").one("bank_name",gcMemberBank.getBankName());
							if(map!=null&&map.size()>0){
								gcPayOrderInfo.setBankCode(map.get("bank_code"));
								gcPayOrderInfo.setBankName(map.get("bank_name"));
							}
						}*/
						if(extendInfoList!=null&&extendInfoList.size()>0){
							McExtendInfoHomehas mcExtendInfoHomehas=extendInfoList.get(0);
							gcPayOrderInfo.setLdCode(mcExtendInfoHomehas.getHomehasCode());
							gcPayOrderInfo.setLdName(mcExtendInfoHomehas.getMemberName());
						}
						gcPayOrderInfo.setCertificateType(papersMap.get("papers_type"));
						gcPayOrderInfo.setCertificateNo(papersMap.get("papers_code"));
						gcPayOrderInfo.setAfterTaxMoney(withdrawMoney.subtract(tax));//完税金额
						gcPayOrderInfoMapper.insert(gcPayOrderInfo);
						
						// 提现日志增加、账户变动
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(sAccountCode);
						gcWithdrawLog.setMemberCode(sMemberCode);
						gcWithdrawLog.setUid(WebHelper.upUuid());
						gcWithdrawLog.setWithdrawMoney(withdrawMoney.negate());
						gcWithdrawLog.setWithdrawChangeType("4497465200040002");//用户提现
						gcWithdrawLog.setChangeCodes(gcPayOrderInfo.getPayOrderCode());
						TxGroupAccountService txGroupAccountService = BeansHelper
								.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
						List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
						listWithdrawLogs.add(gcWithdrawLog);
						txGroupAccountService.updateAccount(null, listWithdrawLogs);

						// 插入用户付款单据日志表
						GcPayOrderLogMapper gcPayOrderLogMapper = BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
						GcPayOrderLog gcPayOrderLog = new GcPayOrderLog();
						gcPayOrderLog.setUid(WebHelper.upUuid());
						gcPayOrderLog.setPayOrderCode(gcPayOrderInfo
								.getPayOrderCode());
						gcPayOrderLog.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderLog.setPayStatus("4497465200070001");// 未支付
						gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
						gcPayOrderLog.setUpdateUser("");
						gcPayOrderLogMapper.insertSelective(gcPayOrderLog);

						// 插入支付信息明细对应表
						// 取出清分订单日志
						// 对应提现金额
						// 相应改动更新
						try{
						GcPayOrderDetailMapper gcPayOrderDetailMapper=BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderDetailMapper");
						String sql="select zid,order_code,reckon_money,relation_level,payed_money from gc_reckon_log where account_code='"+sAccountCode+"' and reckon_change_type='4497465200030001' and flag_withdraw=0 "
								+ " and payed_money<reckon_money and order_code not in (select order_code from gc_reckon_order_step where exec_type='4497465200050002') order by order_reckon_time ";
						List<Map<String, Object>> orderList=DbUp.upTable("gc_reckon_log").dataSqlList(sql,null);
						BigDecimal newWithdrawMoney = withdrawMoney;
						
						
						List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
						List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
						for (Map<String, Object> map : orderList) {
							GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
							if(map.get("relation_level").toString().equals("0")){
								gcPayOrderDetail.setIsOwn("1");//自己订单
							}
							else{
								gcPayOrderDetail.setIsOwn("0");//非自己订单
							}
							BigDecimal money = new BigDecimal(map.get("reckon_money").toString()).subtract(new BigDecimal(map.get("payed_money").toString()));

							if (money.compareTo(newWithdrawMoney) == -1) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(money));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(money);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								newWithdrawMoney = newWithdrawMoney.subtract(money);
							}

							else if (money.compareTo(newWithdrawMoney) == 1
									|| money.compareTo(newWithdrawMoney) == 0) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(newWithdrawMoney));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(newWithdrawMoney);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								break;
							}
						}
						
						//插入付款单单详情
						StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(uid,pay_order_code,order_code,reckon_money,create_time,is_own) values ");
						for(int i=0;i<detailList.size();i++){
							if(i==detailList.size()-1){
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("')");
							}
							else{
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("'),");
							}
							
						}
						if(detailList.size()>0){
							//gcPayOrderDetailMapper.execSql(detailbBuilder.toString());
							
							DbUp.upTable("gc_pay_order_detail").dataExec(detailbBuilder.toString(), new MDataMap());
						}
						
						//更新日志金额
						StringBuilder logBuilder=new StringBuilder("insert into gc_reckon_log(zid,payed_money) values ");
						for(int i=0;i<logList.size();i++){
							if(i==logList.size()-1){
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append(")");
							}
							else{
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append("),");
							}
							
						}
						logBuilder.append(" ON DUPLICATE KEY UPDATE payed_money=VALUES(payed_money)");
						if(logList.size()>0){
							//gcPayOrderDetailMapper.execSql(logBuilder.toString());
							DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
						}
						
						
						
						}
						catch(Exception e){
							e.printStackTrace();
							LogHelper.addLogString("groupcenter_withdraw_exception", gcPayOrderInfo.getPayOrderCode()+":"+e.getMessage());
						}
						//push申请提现消息
						try {
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(sAccountCode);
							addSinglePushCommentInput.setAppCode("SI2011");
							addSinglePushCommentInput.setType("44974720000400010001");//好消息
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle("您的提现申请已收到");
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
							
						    String content="收到您的提现申请，金额："+withdrawMoney.toString()+"元,预计3至7个工作日到账。如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
						    addSinglePushCommentInput.setContent(content);
							if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
							    addSinglePushCommentInput.setSendStatus("4497465000070001");
							}
							else{
								addSinglePushCommentInput.setSendStatus("4497465000070002");
							}
							SinglePushComment.addPushComment(addSinglePushCommentInput);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						mWebResult.inErrorMessage(918505210);
					}
				}
			}
			
		}

		// 解鎖
		WebHelper.unLock(sLockCode);
		return mWebResult;
	}
	
	//新版
	public WithdrawApiResult doWithdrawCashNew(String sMemberCode, String sAccountCode,
			String sWithdrawAmount,String bankCode){
		WithdrawApiResult mWebResult=new WithdrawApiResult();
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, sAccountCode);
		if (StringUtils.isEmpty(sLockCode)) {
			mWebResult.inErrorMessage(918505211, sAccountCode);
		}
		
		//判断黑名单
		if(mWebResult.upFlagTrue()){
			GroupService groupService=new GroupService();
			String mobileNo=groupService.getMobileByAccountCode(sAccountCode);
			if(StringUtils.isNotEmpty(mobileNo)){
				if(DbUp.upTable("gc_account_blacklist").count("mobile_no",mobileNo)>0){
					mWebResult.inErrorMessage(915805219);
				}
			}
		}
		
		//判断是否绑定证件
		MDataMap papersMap=DbUp.upTable("gc_member_papers_info").one("account_code",sAccountCode,"flag_enable","1");
		if(mWebResult.upFlagTrue()){
			if(papersMap==null||StringUtils.isBlank(papersMap.get("user_name"))||StringUtils.isBlank(papersMap.get("papers_code"))){
				mWebResult.inErrorMessage(915805333);
			}
		}
		
		//判断银行卡信息
		GcMemberBank gcMemberBank=new GcMemberBank();
		if(mWebResult.upFlagTrue()){
			List<GcMemberBank> bankList=new ArrayList<GcMemberBank>();
			GcMemberBankMapper gcMemberBankMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcMemberBankMapper");
			GcMemberBankExample gcMemberBankExample=new GcMemberBankExample();
			//银行卡编号为空，去数据库取最新信息
			if(StringUtils.isEmpty(bankCode)){
				gcMemberBankExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andFlagEnableEqualTo(1);
				gcMemberBankExample.setOrderByClause(" zid desc ");
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805217);
				}
				
			}
			else{
				//判断银行卡信息是否存在
				gcMemberBankExample.createCriteria().andBankCodeEqualTo(bankCode);
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805215);
				}
			}
			if(bankList!=null&&bankList.size()>0){
				gcMemberBank=bankList.get(0);
			}
		}
		
		GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
		
		//判断有否有未完成的付款单
		if(mWebResult.upFlagTrue()){
		    List<String> statusList=new ArrayList<String>();
		    statusList.add("4497465200070001");//未支付
		    statusList.add("4497465200070002");//已支付
			GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
			//gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			if(bConfig("groupcenter.tax_type").equals("account")){//账户编号方式
				gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			else if(bConfig("groupcenter.tax_type").equals("certificate")){//证件号码姓名
				gcPayOrderInfoExample.createCriteria().andCertificateNoEqualTo(papersMap.get("papers_code")).andMemberNameEqualTo(papersMap.get("user_name")).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			
			List<GcPayOrderInfo> infoList=gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample);
			if(infoList!=null&&infoList.size()>0){
				mWebResult.inErrorMessage(915805216);
			}
		}
		
		// 提现相关
		if (mWebResult.upFlagTrue()) {
			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
			GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
			gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
					sAccountCode);
			// 定义当前微公社账户信息
			GcGroupAccount gcGroupAccount = gcGroupAccountMapper
					.selectByExample(gcGroupAccountExample).get(0);

			BigDecimal withdrawMoney = new BigDecimal(sWithdrawAmount);
			BigDecimal bg0 = BigDecimal.ZERO;
			BigDecimal accountWithdrawMoney = gcGroupAccount
					.getAccountWithdrawMoney();
            if(withdrawMoney.compareTo(bg0)==0){//传0,默认提现全部金额，说是兼容旧版本
				withdrawMoney=accountWithdrawMoney;
			}
			if(withdrawMoney.compareTo(new BigDecimal("10"))==-1){
				mWebResult.inErrorMessage(918505212);
			}
			if(mWebResult.upFlagTrue()){
				if (withdrawMoney.compareTo(bg0) == 1) {
					if (withdrawMoney.compareTo(accountWithdrawMoney) == 0
							|| withdrawMoney.compareTo(accountWithdrawMoney) == -1) {
						
						// 计算扣税  11-24更改 增加当月累计计税金额
						Map<String,BigDecimal> taxMap = calculateTax(sAccountCode, withdrawMoney,papersMap.get("papers_code"),papersMap.get("user_name"));
						BigDecimal tax=taxMap.get("taxMoney");
						// 计算手续费
						BigDecimal feeMoney = new BigDecimal(0);
						if (withdrawMoney.compareTo(new BigDecimal(100)) == -1) {
							feeMoney = new BigDecimal(1);
						}

						mWebResult.setFeeMoney(feeMoney);
						mWebResult.setTaxMoney(tax);
						mWebResult.setWithdrawMoney(withdrawMoney);
						mWebResult.setRealPayMoney(withdrawMoney.subtract(tax).subtract(feeMoney));
	                    
	                    //查询ld系统编号、姓名
	                    McExtendInfoHomehasMapper mcExtendInfoHomehasMapper=BeansHelper
	        					.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomehasMapper");
	                    McExtendInfoHomehasExample mcExtendInfoHomehasExample=new McExtendInfoHomehasExample();
	                    mcExtendInfoHomehasExample.createCriteria().andMemberCodeEqualTo(sMemberCode);
	                    List<McExtendInfoHomehas> extendInfoList=mcExtendInfoHomehasMapper.selectByExample(mcExtendInfoHomehasExample);
	                    
						// 插入用户付款单据表
						GcPayOrderInfo gcPayOrderInfo = new GcPayOrderInfo();
						gcPayOrderInfo.setUid(WebHelper.upUuid());
						gcPayOrderInfo.setPayOrderCode(WebHelper.upCode("WGS"));
						gcPayOrderInfo.setAccountCode(sAccountCode);
						gcPayOrderInfo.setMemberCode(sMemberCode);
						gcPayOrderInfo.setPayMoney(withdrawMoney.subtract(tax)
								.subtract(feeMoney));
						//gcPayOrderInfo.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderInfo.setOrderStatus("4497153900120004");// 系统待审核
						gcPayOrderInfo.setPayStatus("4497465200070001");// 未支付
						gcPayOrderInfo.setWithdrawMoney(withdrawMoney);
						gcPayOrderInfo.setTaxMoney(tax);
						gcPayOrderInfo.setTotalTaxMoney(taxMap.get("dueTaxMoney"));
						gcPayOrderInfo.setCreateTime(FormatHelper.upDateTime());
						gcPayOrderInfo.setMemberName(papersMap.get("user_name"));
						gcPayOrderInfo.setCardCode(gcMemberBank.getCardCode());
						gcPayOrderInfo.setFeeMoney(feeMoney);
						gcPayOrderInfo.setBeforeWithdrawMoney(accountWithdrawMoney);
						gcPayOrderInfo.setAfterWithdrawMoney(accountWithdrawMoney
								.subtract(withdrawMoney));
						gcPayOrderInfo.setBankCode(gcMemberBank.getBankCode());
						gcPayOrderInfo.setBankName(gcMemberBank.getBankName());
						/*if(StringUtils.isNotEmpty(gcMemberBank.getBankName())){
							MDataMap map=DbUp.upTable("gc_mobile_wallet_bank").one("bank_name",gcMemberBank.getBankName());
							if(map!=null&&map.size()>0){
								gcPayOrderInfo.setBankCode(map.get("bank_code"));
								gcPayOrderInfo.setBankName(map.get("bank_name"));
							}
						}*/
						if(extendInfoList!=null&&extendInfoList.size()>0){
							McExtendInfoHomehas mcExtendInfoHomehas=extendInfoList.get(0);
							gcPayOrderInfo.setLdCode(mcExtendInfoHomehas.getHomehasCode());
							gcPayOrderInfo.setLdName(mcExtendInfoHomehas.getMemberName());
						}
						gcPayOrderInfo.setCertificateType(papersMap.get("papers_type"));
						gcPayOrderInfo.setCertificateNo(papersMap.get("papers_code"));
						gcPayOrderInfo.setAfterTaxMoney(withdrawMoney.subtract(tax));//完税金额
						gcPayOrderInfoMapper.insert(gcPayOrderInfo);
						
						// 提现日志增加、账户变动
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(sAccountCode);
						gcWithdrawLog.setMemberCode(sMemberCode);
						gcWithdrawLog.setUid(WebHelper.upUuid());
						gcWithdrawLog.setWithdrawMoney(withdrawMoney.negate());
						gcWithdrawLog.setWithdrawChangeType("4497465200040002");//用户提现
						gcWithdrawLog.setChangeCodes(gcPayOrderInfo.getPayOrderCode());
						TxGroupAccountService txGroupAccountService = BeansHelper
								.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
						List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
						listWithdrawLogs.add(gcWithdrawLog);
						txGroupAccountService.updateAccount(null, listWithdrawLogs);

						// 插入用户付款单据日志表
						GcPayOrderLogMapper gcPayOrderLogMapper = BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
						GcPayOrderLog gcPayOrderLog = new GcPayOrderLog();
						gcPayOrderLog.setUid(WebHelper.upUuid());
						gcPayOrderLog.setPayOrderCode(gcPayOrderInfo
								.getPayOrderCode());
						
						//gcPayOrderLog.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderLog.setOrderStatus("4497153900120004");// 系统待审核
						gcPayOrderLog.setPayStatus("4497465200070001");// 未支付
						gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
						gcPayOrderLog.setUpdateUser("");
						gcPayOrderLogMapper.insertSelective(gcPayOrderLog);

						// 插入支付信息明细对应表
						// 取出清分订单日志
						// 对应提现金额
						// 相应改动更新
						try{
						GcPayOrderDetailMapper gcPayOrderDetailMapper=BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderDetailMapper");
						String sql="select zid,order_code,reckon_money,relation_level,payed_money from gc_reckon_log where account_code='"+sAccountCode+"' and reckon_change_type='4497465200030001' and flag_withdraw=0 "
								+ " and payed_money<reckon_money and order_code not in (select order_code from gc_reckon_order_step where exec_type='4497465200050002') order by order_reckon_time ";
						List<Map<String, Object>> orderList=DbUp.upTable("gc_reckon_log").dataSqlList(sql,null);
						BigDecimal newWithdrawMoney = withdrawMoney;
						
						
						List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
						List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
						for (Map<String, Object> map : orderList) {
							GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
							if(map.get("relation_level").toString().equals("0")){
								gcPayOrderDetail.setIsOwn("1");//自己订单
							}
							else{
								gcPayOrderDetail.setIsOwn("0");//非自己订单
							}
							BigDecimal money = new BigDecimal(map.get("reckon_money").toString()).subtract(new BigDecimal(map.get("payed_money").toString()));

							if (money.compareTo(newWithdrawMoney) == -1) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(money));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(money);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								newWithdrawMoney = newWithdrawMoney.subtract(money);
							}

							else if (money.compareTo(newWithdrawMoney) == 1
									|| money.compareTo(newWithdrawMoney) == 0) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(newWithdrawMoney));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(newWithdrawMoney);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								break;
							}
						}
						
						//插入付款单单详情
						StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(uid,pay_order_code,order_code,reckon_money,create_time,is_own,refer_zid) values ");
						for(int i=0;i<detailList.size();i++){
							if(i==detailList.size()-1){
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("','").append(detailList.get(i).getReferZid()).append("')");
							}
							else{
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("','").append(detailList.get(i).getReferZid()).append("'),");
							}
							
						}
						if(detailList.size()>0){
							//gcPayOrderDetailMapper.execSql(detailbBuilder.toString());
							
							DbUp.upTable("gc_pay_order_detail").dataExec(detailbBuilder.toString(), new MDataMap());
						}
						
						//更新日志金额
						StringBuilder logBuilder=new StringBuilder("insert into gc_reckon_log(zid,payed_money) values ");
						for(int i=0;i<logList.size();i++){
							if(i==logList.size()-1){
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append(")");
							}
							else{
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append("),");
							}
							
						}
						logBuilder.append(" ON DUPLICATE KEY UPDATE payed_money=VALUES(payed_money)");
						if(logList.size()>0){
							//gcPayOrderDetailMapper.execSql(logBuilder.toString());
							DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
						}
						
						
						
						}
						catch(Exception e){
							e.printStackTrace();
							LogHelper.addLogString("groupcenter_withdraw_exception", gcPayOrderInfo.getPayOrderCode()+":"+e.getMessage());
						}
						//push申请提现消息
						try {
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(sAccountCode);
							addSinglePushCommentInput.setAppCode("SI2011");
							addSinglePushCommentInput.setType("44974720000400010001");//好消息
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle("您的提现申请已收到");
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
							
						    String content="收到您的提现申请，金额："+withdrawMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString()+"元,预计3至7个工作日到账。如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
						    addSinglePushCommentInput.setContent(content);
							if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
							    addSinglePushCommentInput.setSendStatus("4497465000070001");
							}
							else{
								addSinglePushCommentInput.setSendStatus("4497465000070002");
							}
							SinglePushComment.addPushComment(addSinglePushCommentInput);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						mWebResult.inErrorMessage(918505210);
					}
				}
			}
			
		}

		// 解鎖
		WebHelper.unLock(sLockCode);
		
		return mWebResult;
	}
	
	//新版(新版中提现按照配置中来.)
	public WithdrawApiResult doWithdrawCashConfigNew(String sMemberCode, String sAccountCode,
			String sWithdrawAmount,String bankCode){
		WithdrawApiResult mWebResult=new WithdrawApiResult();
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, sAccountCode);
		if (StringUtils.isEmpty(sLockCode)) {
			mWebResult.inErrorMessage(918505211, sAccountCode);
		}
		
		//判断黑名单
		if(mWebResult.upFlagTrue()){
			GroupService groupService=new GroupService();
			String mobileNo=groupService.getMobileByAccountCode(sAccountCode);
			if(StringUtils.isNotEmpty(mobileNo)){
				if(DbUp.upTable("gc_account_blacklist").count("mobile_no",mobileNo)>0){
					mWebResult.inErrorMessage(915805219);
				}
			}
		}
		
		//判断是否绑定证件
		MDataMap papersMap=DbUp.upTable("gc_member_papers_info").one("account_code",sAccountCode,"flag_enable","1");
		if(mWebResult.upFlagTrue()){
			if(papersMap==null||StringUtils.isBlank(papersMap.get("user_name"))||StringUtils.isBlank(papersMap.get("papers_code"))){
				mWebResult.inErrorMessage(915805333);
			}
		}
		
		//判断银行卡信息
		GcMemberBank gcMemberBank=new GcMemberBank();
		if(mWebResult.upFlagTrue()){
			List<GcMemberBank> bankList=new ArrayList<GcMemberBank>();
			GcMemberBankMapper gcMemberBankMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcMemberBankMapper");
			GcMemberBankExample gcMemberBankExample=new GcMemberBankExample();
			//银行卡编号为空，去数据库取最新信息
			if(StringUtils.isEmpty(bankCode)){
				gcMemberBankExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andFlagEnableEqualTo(1);
				gcMemberBankExample.setOrderByClause(" zid desc ");
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805217);
				}
				
			}
			else{
				//判断银行卡信息是否存在
				gcMemberBankExample.createCriteria().andBankCodeEqualTo(bankCode);
				bankList=gcMemberBankMapper.selectByExample(gcMemberBankExample);
				if(bankList==null||bankList.size()<1){
					mWebResult.inErrorMessage(915805215);
				}
			}
			if(bankList!=null&&bankList.size()>0){
				gcMemberBank=bankList.get(0);
			}
		}
		
		GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
		
		//判断有否有未完成的付款单
		if(mWebResult.upFlagTrue()){
		    List<String> statusList=new ArrayList<String>();
		    statusList.add("4497465200070001");//未支付
		    statusList.add("4497465200070002");//已支付
			GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
			//gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			if(bConfig("groupcenter.tax_type").equals("account")){//账户编号方式
				gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(sAccountCode).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			else if(bConfig("groupcenter.tax_type").equals("certificate")){//证件号码姓名
				gcPayOrderInfoExample.createCriteria().andCertificateNoEqualTo(papersMap.get("papers_code")).andMemberNameEqualTo(papersMap.get("user_name")).andPayStatusIn(statusList).andOrderStatusNotEqualTo("4497153900120003");//审核失败
			}
			
			List<GcPayOrderInfo> infoList=gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample);
			if(infoList!=null&&infoList.size()>0){
				mWebResult.inErrorMessage(915805216);
			}
		}
		
		// 提现相关
		if (mWebResult.upFlagTrue()) {
			GcGroupAccountMapper gcGroupAccountMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcGroupAccountMapper");
			GcGroupAccountExample gcGroupAccountExample = new GcGroupAccountExample();
			gcGroupAccountExample.createCriteria().andAccountCodeEqualTo(
					sAccountCode);
			// 定义当前微公社账户信息
			GcGroupAccount gcGroupAccount = gcGroupAccountMapper
					.selectByExample(gcGroupAccountExample).get(0);

			BigDecimal withdrawMoney = new BigDecimal(sWithdrawAmount);
			BigDecimal bg0 = BigDecimal.ZERO;
			BigDecimal accountWithdrawMoney = gcGroupAccount
					.getAccountWithdrawMoney();
            if(withdrawMoney.compareTo(bg0)==0){//传0,默认提现全部金额，说是兼容旧版本
				withdrawMoney=accountWithdrawMoney;
			}
			//如果设置了提现配置，则低于提现金额就不予提现。
            BigDecimal minimumWithdrawMoney=new BigDecimal(0);
        	MDataMap withdrawMap=DbUp.upTable("gc_withdraw_config").one("withdraw_source","449747770001","flag_status","1");
        	if(withdrawMap!=null){
        		minimumWithdrawMoney=new BigDecimal(withdrawMap.get("minimum_withdraw_money"));
				if(withdrawMoney.compareTo(minimumWithdrawMoney) == -1){
					mWebResult.inErrorMessage(915805228,minimumWithdrawMoney);
				}
			}
        	//如果没有设置提现配置则按照默认的提现规则(提现金额必须大于10元)。
        	else{
				if(withdrawMoney.compareTo(new BigDecimal("10"))==-1){
					mWebResult.inErrorMessage(918505212);
				}
            }
        	
			if(mWebResult.upFlagTrue()){
				if (withdrawMoney.compareTo(bg0) == 1) {
					if (withdrawMoney.compareTo(accountWithdrawMoney) == 0
							|| withdrawMoney.compareTo(accountWithdrawMoney) == -1) {
						
						// 计算扣税  11-24更改 增加当月累计计税金额
						Map<String,BigDecimal> taxMap = calculateTax(sAccountCode, withdrawMoney,papersMap.get("papers_code"),papersMap.get("user_name"));
						BigDecimal tax=taxMap.get("taxMoney");
						// 计算手续费
						BigDecimal feeMoney = new BigDecimal(0);
						BigDecimal maximumMoneyRange=new BigDecimal(0);
						//配置提现配置就按照配置来计算手续费
						if(withdrawMap!=null){
							maximumMoneyRange=new BigDecimal(withdrawMap.get("maximum_money_range"));
							if(withdrawMoney.compareTo(maximumMoneyRange) == -1){
								feeMoney=new BigDecimal(withdrawMap.get("fee_money"));
							}
						}
						//没有配置的话按照默认计算手续费，默认提现范围小于100收取一元后续费
						else{
							if(withdrawMoney.compareTo(new BigDecimal(100)) == -1){
								feeMoney = new BigDecimal(1);
							}
						}
						
						mWebResult.setFeeMoney(feeMoney);
						mWebResult.setTaxMoney(tax);
						mWebResult.setWithdrawMoney(withdrawMoney);
						mWebResult.setRealPayMoney(withdrawMoney.subtract(tax).subtract(feeMoney));
	                    
	                    //查询ld系统编号、姓名
	                    McExtendInfoHomehasMapper mcExtendInfoHomehasMapper=BeansHelper
	        					.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomehasMapper");
	                    McExtendInfoHomehasExample mcExtendInfoHomehasExample=new McExtendInfoHomehasExample();
	                    mcExtendInfoHomehasExample.createCriteria().andMemberCodeEqualTo(sMemberCode);
	                    List<McExtendInfoHomehas> extendInfoList=mcExtendInfoHomehasMapper.selectByExample(mcExtendInfoHomehasExample);
	                    
						// 插入用户付款单据表
						GcPayOrderInfo gcPayOrderInfo = new GcPayOrderInfo();
						gcPayOrderInfo.setUid(WebHelper.upUuid());
						gcPayOrderInfo.setPayOrderCode(WebHelper.upCode("WGS"));
						gcPayOrderInfo.setAccountCode(sAccountCode);
						gcPayOrderInfo.setMemberCode(sMemberCode);
						gcPayOrderInfo.setPayMoney(withdrawMoney.subtract(tax)
								.subtract(feeMoney));
						//gcPayOrderInfo.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderInfo.setOrderStatus("4497153900120004");// 系统待审核
						gcPayOrderInfo.setPayStatus("4497465200070001");// 未支付
						gcPayOrderInfo.setWithdrawMoney(withdrawMoney);
						gcPayOrderInfo.setTaxMoney(tax);
						gcPayOrderInfo.setTotalTaxMoney(taxMap.get("dueTaxMoney"));
						gcPayOrderInfo.setCreateTime(FormatHelper.upDateTime());
						gcPayOrderInfo.setMemberName(papersMap.get("user_name"));
						gcPayOrderInfo.setCardCode(gcMemberBank.getCardCode());
						gcPayOrderInfo.setFeeMoney(feeMoney);
						gcPayOrderInfo.setBeforeWithdrawMoney(accountWithdrawMoney);
						gcPayOrderInfo.setAfterWithdrawMoney(accountWithdrawMoney
								.subtract(withdrawMoney));
						gcPayOrderInfo.setBankCode(gcMemberBank.getBankCode());
						gcPayOrderInfo.setBankName(gcMemberBank.getBankName());
						/*if(StringUtils.isNotEmpty(gcMemberBank.getBankName())){
							MDataMap map=DbUp.upTable("gc_mobile_wallet_bank").one("bank_name",gcMemberBank.getBankName());
							if(map!=null&&map.size()>0){
								gcPayOrderInfo.setBankCode(map.get("bank_code"));
								gcPayOrderInfo.setBankName(map.get("bank_name"));
							}
						}*/
						if(extendInfoList!=null&&extendInfoList.size()>0){
							McExtendInfoHomehas mcExtendInfoHomehas=extendInfoList.get(0);
							gcPayOrderInfo.setLdCode(mcExtendInfoHomehas.getHomehasCode());
							gcPayOrderInfo.setLdName(mcExtendInfoHomehas.getMemberName());
						}
						gcPayOrderInfo.setCertificateType(papersMap.get("papers_type"));
						gcPayOrderInfo.setCertificateNo(papersMap.get("papers_code"));
						gcPayOrderInfo.setAfterTaxMoney(withdrawMoney.subtract(tax));//完税金额
						gcPayOrderInfoMapper.insert(gcPayOrderInfo);
						
						// 提现日志增加、账户变动
						GcWithdrawLog gcWithdrawLog = new GcWithdrawLog();
						gcWithdrawLog.setAccountCode(sAccountCode);
						gcWithdrawLog.setMemberCode(sMemberCode);
						gcWithdrawLog.setUid(WebHelper.upUuid());
						gcWithdrawLog.setWithdrawMoney(withdrawMoney.negate());
						gcWithdrawLog.setWithdrawChangeType("4497465200040002");//用户提现
						gcWithdrawLog.setChangeCodes(gcPayOrderInfo.getPayOrderCode());
						TxGroupAccountService txGroupAccountService = BeansHelper
								.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
						List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
						listWithdrawLogs.add(gcWithdrawLog);
						txGroupAccountService.updateAccount(null, listWithdrawLogs);

						// 插入用户付款单据日志表
						GcPayOrderLogMapper gcPayOrderLogMapper = BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
						GcPayOrderLog gcPayOrderLog = new GcPayOrderLog();
						gcPayOrderLog.setUid(WebHelper.upUuid());
						gcPayOrderLog.setPayOrderCode(gcPayOrderInfo
								.getPayOrderCode());
						
						//gcPayOrderLog.setOrderStatus("4497153900120001");// 未审核
						gcPayOrderLog.setOrderStatus("4497153900120004");// 系统待审核
						gcPayOrderLog.setPayStatus("4497465200070001");// 未支付
						gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
						gcPayOrderLog.setUpdateUser("");
						gcPayOrderLogMapper.insertSelective(gcPayOrderLog);

						// 插入支付信息明细对应表
						// 取出清分订单日志
						// 对应提现金额
						// 相应改动更新
						try{
						GcPayOrderDetailMapper gcPayOrderDetailMapper=BeansHelper
								.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderDetailMapper");
						String sql="select zid,order_code,reckon_money,relation_level,payed_money from gc_reckon_log where account_code='"+sAccountCode+"' and reckon_change_type='4497465200030001' and flag_withdraw=0 "
								+ " and payed_money<reckon_money and order_code not in (select order_code from gc_reckon_order_step where exec_type='4497465200050002') order by order_reckon_time ";
						List<Map<String, Object>> orderList=DbUp.upTable("gc_reckon_log").dataSqlList(sql,null);
						BigDecimal newWithdrawMoney = withdrawMoney;
						
						
						List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
						List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
						for (Map<String, Object> map : orderList) {
							GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
							if(map.get("relation_level").toString().equals("0")){
								gcPayOrderDetail.setIsOwn("1");//自己订单
							}
							else{
								gcPayOrderDetail.setIsOwn("0");//非自己订单
							}
							BigDecimal money = new BigDecimal(map.get("reckon_money").toString()).subtract(new BigDecimal(map.get("payed_money").toString()));

							if (money.compareTo(newWithdrawMoney) == -1) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(money));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(money);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								newWithdrawMoney = newWithdrawMoney.subtract(money);
							}

							else if (money.compareTo(newWithdrawMoney) == 1
									|| money.compareTo(newWithdrawMoney) == 0) {
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
								updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(newWithdrawMoney));
								logList.add(updateReckon);
								gcPayOrderDetail.setPayOrderCode(gcPayOrderInfo
										.getPayOrderCode());
								gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
								gcPayOrderDetail.setReckonMoney(newWithdrawMoney);
								gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
								gcPayOrderDetail.setUid(WebHelper.upUuid());
								gcPayOrderDetail.setReferZid(map.get("zid").toString());
								detailList.add(gcPayOrderDetail);
								break;
							}
						}
						
						//插入付款单单详情
						StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(uid,pay_order_code,order_code,reckon_money,create_time,is_own,refer_zid) values ");
						for(int i=0;i<detailList.size();i++){
							if(i==detailList.size()-1){
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("','").append(detailList.get(i).getReferZid()).append("')");
							}
							else{
								detailbBuilder.append("('").append(WebHelper.upUuid()).append("','").append(detailList.get(i).getPayOrderCode()).append("','")
								.append(detailList.get(i).getOrderCode()).append("','").append(detailList.get(i).getReckonMoney()).append("','").append(FormatHelper.upDateTime()).append("','").append(detailList.get(i).getIsOwn())
								.append("','").append(detailList.get(i).getReferZid()).append("'),");
							}
							
						}
						if(detailList.size()>0){
							//gcPayOrderDetailMapper.execSql(detailbBuilder.toString());
							
							DbUp.upTable("gc_pay_order_detail").dataExec(detailbBuilder.toString(), new MDataMap());
						}
						
						//更新日志金额
						StringBuilder logBuilder=new StringBuilder("insert into gc_reckon_log(zid,payed_money) values ");
						for(int i=0;i<logList.size();i++){
							if(i==logList.size()-1){
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append(")");
							}
							else{
								logBuilder.append("(").append(logList.get(i).getZid()).append(",").append(logList.get(i).getPayedMoney()).append("),");
							}
							
						}
						logBuilder.append(" ON DUPLICATE KEY UPDATE payed_money=VALUES(payed_money)");
						if(logList.size()>0){
							//gcPayOrderDetailMapper.execSql(logBuilder.toString());
							DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
						}
						
						
						
						}
						catch(Exception e){
							e.printStackTrace();
							LogHelper.addLogString("groupcenter_withdraw_exception", gcPayOrderInfo.getPayOrderCode()+":"+e.getMessage());
						}
						//push申请提现消息
						try {
							AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
							addSinglePushCommentInput.setAccountCode(sAccountCode);
							addSinglePushCommentInput.setAppCode("SI2011");
							addSinglePushCommentInput.setType("44974720000400010001");//好消息
							
							addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
							addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="+System.currentTimeMillis());
							addSinglePushCommentInput.setTitle("您的提现申请已收到");
							MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code","SI2011","flag_enable","1");
							if(memberMap!=null){
								addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
							}
							
						    String content="收到您的提现申请，金额："+withdrawMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString()+"元,预计3至7个工作日到账。如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
						    addSinglePushCommentInput.setContent(content);
							if(DbUp.upTable("gc_account_push_set").count("account_code",sAccountCode,"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
							    addSinglePushCommentInput.setSendStatus("4497465000070001");
							}
							else{
								addSinglePushCommentInput.setSendStatus("4497465000070002");
							}
							SinglePushComment.addPushComment(addSinglePushCommentInput);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						mWebResult.inErrorMessage(918505210);
					}
				}
			}
			
		}

		// 解鎖
		WebHelper.unLock(sLockCode);
		
		return mWebResult;
	}
	
	/**
	 * 计算扣税
	 * @param accountCode
	 * @param withdrawMoney
	 * @return
	 */
	public Map<String,BigDecimal> calculateTax(String accountCode,BigDecimal withdrawMoney,String papersCode,String memberName) {
		String beginDate=DateHelper.upDate(new Date(),
				DateHelper.CONST_PARSE_MONTH_FIRST_DAY);
		Calendar calender = Calendar.getInstance();
        calender.setTime(DateHelper.parseDate(beginDate));
        calender.add(Calendar.MONTH, 1);
        String endDate=DateHelper.upDate(calender.getTime(), DateHelper.CONST_PARSE_MONTH_FIRST_DAY);
		GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
		GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
		if(bConfig("groupcenter.tax_type").equals("account")){//账户编号方式计算扣税
		    gcPayOrderInfoExample.createCriteria().andAccountCodeEqualTo(accountCode)
		    .andOrderStatusEqualTo("4497153900120002").andPayStatusEqualTo("4497465200070004")
		    .andCreateTimeGreaterThanOrEqualTo(beginDate).andCreateTimeLessThan(endDate);
		}
		else if(bConfig("groupcenter.tax_type").equals("certificate")){//证件号码姓名计算扣税
			gcPayOrderInfoExample.createCriteria().andCertificateNoEqualTo(papersCode)
			.andMemberNameEqualTo(memberName).andOrderStatusEqualTo("4497153900120002").andPayStatusEqualTo("4497465200070004")
		    .andCreateTimeGreaterThanOrEqualTo(beginDate).andCreateTimeLessThan(endDate);
		}
		List<GcPayOrderInfo> list=gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample);
		BigDecimal totalWithdrawMoney=withdrawMoney;
		BigDecimal totalTaxMoney=BigDecimal.ZERO;
		BigDecimal taxMoney=BigDecimal.ZERO;
		BigDecimal dueTaxMoney=BigDecimal.ZERO;
		for(GcPayOrderInfo gcPayOrderInfo:list){
			totalTaxMoney=totalTaxMoney.add(gcPayOrderInfo.getTaxMoney());
			totalWithdrawMoney=totalWithdrawMoney.add(gcPayOrderInfo.getWithdrawMoney());
		}
		BigDecimal bd800=new BigDecimal("800");
		BigDecimal bd2000=new BigDecimal("2000");
		BigDecimal bd4000=new BigDecimal("4000");
		BigDecimal bd7000=new BigDecimal("7000");
		BigDecimal bd20000=new BigDecimal("20000");
		BigDecimal bd50000=new BigDecimal("50000");
		BigDecimal bd02=new BigDecimal("0.2");
		BigDecimal bd03=new BigDecimal("0.3");
		BigDecimal bd04=new BigDecimal("0.4");
		BigDecimal bd08=new BigDecimal("0.8");
		
		//计算应纳税所得额
		if(totalWithdrawMoney.compareTo(bd800)==1&&totalWithdrawMoney.compareTo(bd4000)!=1){
			dueTaxMoney=totalWithdrawMoney.subtract(bd800);
		}
		else if(totalWithdrawMoney.compareTo(bd4000)==1){
			dueTaxMoney=totalWithdrawMoney.multiply(bd08);
		}
		
		//计算扣税金额
		if(dueTaxMoney.compareTo(bd20000)!=1){
			taxMoney=dueTaxMoney.multiply(bd02).subtract(totalTaxMoney);
		}
		else if(dueTaxMoney.compareTo(bd20000)==1&&dueTaxMoney.compareTo(bd50000)!=1){
			taxMoney=dueTaxMoney.multiply(bd03).subtract(bd2000).subtract(totalTaxMoney);
		}
		else if(dueTaxMoney.compareTo(bd50000)==1){
			taxMoney=dueTaxMoney.multiply(bd04).subtract(bd7000).subtract(totalTaxMoney);
		}
		
		//小于0，置为0
		if(taxMoney.compareTo(BigDecimal.ZERO)==-1){
			taxMoney=BigDecimal.ZERO;
		}
		Map<String,BigDecimal> taxMap=new HashMap<String,BigDecimal>();
		taxMap.put("taxMoney", taxMoney);
		taxMap.put("dueTaxMoney", totalWithdrawMoney);
		return taxMap;
	}
	
	//系统审核
	public void WithdrawSystemVertify() {
		
		//检验提现金额是否超过账户余额
		List<MDataMap> withdrawList=DbUp.upTable("gc_pay_order_info").queryByWhere("order_status","4497153900120004");
		if(null!=withdrawList&&withdrawList.size()>0){
			for(MDataMap info:withdrawList){
				BigDecimal money=(BigDecimal) DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)","account_code=:account_code and create_time<:create_time ", 
						new MDataMap("account_code",info.get("account_code"),"create_time",info.get("create_time")));
				MDataMap orderInfo=new MDataMap();
				orderInfo=info;
				
				
				// 插入用户付款单据日志表
				GcPayOrderLogMapper gcPayOrderLogMapper = BeansHelper
						.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
				GcPayOrderLog gcPayOrderLog = new GcPayOrderLog();
				gcPayOrderLog.setUid(WebHelper.upUuid());
				gcPayOrderLog.setPayOrderCode(info.get("pay_order_code"));
				gcPayOrderLog.setPayStatus("4497465200070001");// 未支付
				gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
				gcPayOrderLog.setUpdateUser("");
				
				if(money.compareTo(new BigDecimal(info.get("withdraw_money")))==-1){
					//提款金额大于现有余额
					orderInfo.put("order_status", "4497153900120005");//系统审核失败
					orderInfo.put("audit_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_pay_order_info").update(orderInfo);
					
					// 插入用户付款单据日志表
					gcPayOrderLog.setOrderStatus("4497153900120005");// 系统审核失败
					gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
					sendMail(info);
				}else{
					
					//提现金额小于等于现有余额
					//提款金额大于现有余额
					orderInfo.put("order_status", "4497153900120001");//待审核
					orderInfo.put("audit_time", FormatHelper.upDateTime());
					DbUp.upTable("gc_pay_order_info").update(orderInfo);
					
					// 插入用户付款单据日志表
					gcPayOrderLog.setOrderStatus("4497153900120001");//待审核
					gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
				}
			}
		}

	}
	
	private void sendMail(MDataMap info){
		String title= bConfig("groupcenter.wgs_withdraw_title");
		String content= bConfig("groupcenter.wgs_withdraw_content");
		
		WgsMailSupport.INSTANCE.sendMail("提现系统审核通知", FormatHelper.formatString(title,info.get("account_code"),info.get("withdraw_money")), 
				FormatHelper.formatString(content,info.get("account_code"),info.get("withdraw_money")));
		
	}
}
