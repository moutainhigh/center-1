package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderAccountChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcVpayOrder;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.third.model.GroupPayInput;
import com.cmall.groupcenter.third.model.GroupPayResult;
import com.cmall.groupcenter.third.model.GroupReconciliationDetail;
import com.cmall.groupcenter.third.model.GroupReconciliationInput;
import com.cmall.groupcenter.third.model.GroupReconciliationResult;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.groupcenter.txservice.TxGroupPayService;
import com.cmall.groupcenter.util.WgsMailSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupPayService extends BaseClass{
    
	/**
	 * 支付
	 * @param groupPayInput
	 * @param manageCode
	 * @return
	 */
	public GroupPayResult GroupPay(GroupPayInput groupPayInput,String manageCode){
		
		GroupPayResult groupPayResult=new GroupPayResult();
		
		//记录请求
	    HttpServletRequest request=WebSessionHelper.create().upHttpRequest();
	    MDataMap payMap=new MDataMap();
	    payMap.put("request_code", WebHelper.upCode("RPAY"));
	    payMap.put("order_code", groupPayInput.getOrderCode());
	    payMap.put("request_target", "grouppay");
	    payMap.put("request_url",  "http://" + request.getServerName()+":"+ request.getServerPort()+request.getRequestURI());
	    payMap.put("request_data", new JsonHelper<GroupPayInput>().GsonToJson(groupPayInput));
		payMap.put("request_time", FormatHelper.upDateTime());
		payMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataInsert(payMap);
		
		//判断是否存在
		if(groupPayResult.upFlagTrue()){
			if(DbUp.upTable("gc_vpay_order").count("uq_code",manageCode+"_"+GroupConst.GROUP_PAY+"_"+groupPayInput.getOrderCode(),
					"flag_enable","1","status","1")>0){
				groupPayResult.inErrorMessage(918523001);//存在订单,返回已支付成功
			}
		}
		
		//取account_code
		String accountCode="";
		MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",groupPayInput.getMemberCode(),"flag_enable","1","manage_code",manageCode);
		if(groupPayResult.upFlagTrue()){
			if(memberMap==null){
				groupPayResult.inErrorMessage(918523002);//不存在
			}
			else{
				accountCode=memberMap.get("account_code");
			}
		}
		
		//锁定账户
		String sLockCode="";
		if(groupPayResult.upFlagTrue()){
			sLockCode = WebHelper.addLock(60, accountCode);
			if(StringUtils.isEmpty(sLockCode)){
				groupPayResult.inErrorMessage(918505211, accountCode);
			}
		}
		
		BigDecimal tradeMoney=new BigDecimal(groupPayInput.getTradeMoney());
		BigDecimal zero=BigDecimal.ZERO;
		
		
		//判断黑名单
		if(groupPayResult.upFlagTrue()){
			GroupService groupService=new GroupService();
			String mobileNo=groupService.getMobileByAccountCode(accountCode);
			if(StringUtils.isNotEmpty(mobileNo)){
				if(DbUp.upTable("gc_account_blacklist").count("mobile_no",mobileNo)>0){
					groupPayResult.inErrorMessage(918523014);
				}
			}
		}
		
		
		//支付金额要大于0
		if(groupPayResult.upFlagTrue()){
			if(tradeMoney.compareTo(zero)!=1){
				groupPayResult.inErrorMessage(918523003);
			}
		}
		
		//判断余额是否充足
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(groupPayResult.upFlagTrue()){
			if(accountMap!=null){
				if(new BigDecimal(accountMap.get("account_withdraw_money")).compareTo(tradeMoney)==-1){
					groupPayResult.inErrorMessage(918523004);
				}
			}
		}
		if(groupPayResult.upFlagTrue()){
			//系统审核
			BigDecimal sumMoney=(BigDecimal) DbUp.upTable("gc_withdraw_log").dataGet("sum(withdraw_money)","account_code=:account_code", 
					new MDataMap("account_code",accountCode));
			if(sumMoney==null||sumMoney.compareTo(tradeMoney)==-1){
				groupPayResult.inErrorMessage(918523017);
				
				String title= bConfig("groupcenter.wgs_group_pay_vertify_title");
				String content= bConfig("groupcenter.wgs_roup_pay_vertify_content");
				WgsMailSupport.INSTANCE.sendMail("余额支付系统审核通知", title, 
						FormatHelper.formatString(content,accountCode,tradeMoney));
				
			}			
		}
		
		String tradeCode=WebHelper.upCode("VPAY");
		//添加事物控制
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			if(groupPayResult.upFlagTrue()){
				TxGroupPayService txGroupPayService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupPayService");
				MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
				MDataMap traderMap=new MDataMap();
				if(manageMap!=null){
					traderMap=DbUp.upTable("gc_trader_info").one("trader_code",manageMap.get("trade_code"));
				}
				GcVpayOrder gcVpayOrder=new GcVpayOrder();
				gcVpayOrder.setUid(WebHelper.upUuid());
				gcVpayOrder.setTradeCode(tradeCode);
				gcVpayOrder.setAccountCode(accountCode);
				gcVpayOrder.setMemberCode(groupPayInput.getMemberCode());
				gcVpayOrder.setTradeMoney(tradeMoney);
				gcVpayOrder.setTradeName(groupPayInput.getTradeName());
				gcVpayOrder.setBusinessOrderCode(groupPayInput.getOrderCode());
				gcVpayOrder.setBusinessOrderCreateTime(groupPayInput.getOrderCreateTime());
				gcVpayOrder.setBusinessName(traderMap.get("trader_name")==null?"":traderMap.get("trader_name"));
				gcVpayOrder.setBusinessCode(traderMap.get("trader_code")==null?"":traderMap.get("trader_code"));
				gcVpayOrder.setTradeType(GroupConst.GROUP_PAY);
				gcVpayOrder.setTradeClassify("4497465200210001");
				gcVpayOrder.setTradeStatus("4497465200190001");
				gcVpayOrder.setCreateTime(FormatHelper.upDateTime());
				gcVpayOrder.setFlagEnable(1);
				gcVpayOrder.setStatus(1);
				gcVpayOrder.setManageCode(manageCode);
				gcVpayOrder.setRemark(groupPayInput.getRemark());
				gcVpayOrder.setUqCode(manageCode+"_"+GroupConst.GROUP_PAY+"_"+groupPayInput.getOrderCode());
				
				try{
					txGroupPayService.doGroupPay(gcVpayOrder,groupPayInput,tradeCode,accountCode,tradeMoney,manageCode);
				}catch(Exception e){
					groupPayResult.inErrorMessage(918580103);
					e.printStackTrace();
				}
				
				if(groupPayResult.upFlagTrue()){
					
					//根据金额匹配订单
					String sql="select zid,order_code,reckon_money,relation_level,payed_money from gc_reckon_log where account_code='"+accountCode+"' and reckon_change_type='4497465200030001' and flag_withdraw=0 "
							+ " and payed_money<reckon_money and order_code not in (select order_code from gc_reckon_order_step where exec_type='4497465200050002') order by order_reckon_time ";
					List<Map<String, Object>> orderList=DbUp.upTable("gc_reckon_log").dataSqlList(sql,null);
					BigDecimal newWithdrawMoney = tradeMoney;
					
					List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
					List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
					for (Map<String, Object> map : orderList) {
						GcPayOrderDetail gcPayOrderDetail=new GcPayOrderDetail();
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
							//gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
							logList.add(updateReckon);
							gcPayOrderDetail.setPayOrderCode(tradeCode);
							gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
							gcPayOrderDetail.setReckonMoney(money);
							gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
							gcPayOrderDetail.setUid(WebHelper.upUuid());
							gcPayOrderDetail.setReferZid(map.get("zid").toString());
							//gcPayOrderDetailMapper.insertSelective(gcPayOrderDetail);
							detailList.add(gcPayOrderDetail);
							newWithdrawMoney = newWithdrawMoney.subtract(money);
						}

						else if (money.compareTo(newWithdrawMoney) == 1
								|| money.compareTo(newWithdrawMoney) == 0) {
							GcReckonLog updateReckon = new GcReckonLog();
							updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
							updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(newWithdrawMoney));
							//gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
							logList.add(updateReckon);
							gcPayOrderDetail.setPayOrderCode(tradeCode);
							gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
							gcPayOrderDetail.setReckonMoney(newWithdrawMoney);
							gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
							gcPayOrderDetail.setUid(WebHelper.upUuid());
							gcPayOrderDetail.setReferZid(map.get("zid").toString());
							//gcPayOrderDetailMapper.insertSelective(gcPayOrderDetail);
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
						DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
					}
				}
			}
		}else{
			//支付，账户扣减交易金额
			if(groupPayResult.upFlagTrue()){
				TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
	            List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
				GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
	            gcWithdrawLog.setAccountCode(accountCode);
	            gcWithdrawLog.setMemberCode(groupPayInput.getMemberCode());
	            gcWithdrawLog.setWithdrawMoney(tradeMoney.negate());
	            gcWithdrawLog.setWithdrawChangeType("4497465200040008");//支付类型
	            gcWithdrawLog.setChangeCodes(tradeCode);
	            withdrawList.add(gcWithdrawLog);
				txGroupAccountService.updateAccount(null, withdrawList);
			}
			
			//支付记录
			
			if(groupPayResult.upFlagTrue()){
				MDataMap manageMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
				MDataMap traderMap=new MDataMap();
				if(manageMap!=null){
					traderMap=DbUp.upTable("gc_trader_info").one("trader_code",manageMap.get("trade_code"));
				}
				MDataMap vpayMap=new MDataMap();
				vpayMap.put("trade_code",tradeCode);
				vpayMap.put("account_code", accountCode);
				vpayMap.put("member_code", groupPayInput.getMemberCode());
				vpayMap.put("trade_money", tradeMoney.toString());
				vpayMap.put("trade_name", groupPayInput.getTradeName());
				vpayMap.put("business_order_code", groupPayInput.getOrderCode());
				vpayMap.put("business_order_create_time", groupPayInput.getOrderCreateTime());
				vpayMap.put("business_name", traderMap.get("trader_name")==null?"":traderMap.get("trader_name"));
				vpayMap.put("business_code", traderMap.get("trader_code")==null?"":traderMap.get("trader_code"));
				vpayMap.put("trade_type", GroupConst.GROUP_PAY);
				vpayMap.put("trade_classify", "4497465200210001");
				vpayMap.put("trade_status", "4497465200190001");
				vpayMap.put("create_time", FormatHelper.upDateTime());
				vpayMap.put("flag_enable", "1");
				vpayMap.put("status", "1");
				vpayMap.put("manage_code",manageCode);
				vpayMap.put("remark",groupPayInput.getRemark());
				vpayMap.put("uq_code", manageCode+"_"+GroupConst.GROUP_PAY+"_"+groupPayInput.getOrderCode());
				DbUp.upTable("gc_vpay_order").dataInsert(vpayMap);
			}
			
			//根据金额匹配订单
			if(groupPayResult.upFlagTrue()){
				String sql="select zid,order_code,reckon_money,relation_level,payed_money from gc_reckon_log where account_code='"+accountCode+"' and reckon_change_type='4497465200030001' and flag_withdraw=0 "
						+ " and payed_money<reckon_money and order_code not in (select order_code from gc_reckon_order_step where exec_type='4497465200050002') order by order_reckon_time ";
				List<Map<String, Object>> orderList=DbUp.upTable("gc_reckon_log").dataSqlList(sql,null);
				BigDecimal newWithdrawMoney = tradeMoney;
				
				
				List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
				List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
				for (Map<String, Object> map : orderList) {
					GcPayOrderDetail gcPayOrderDetail=new GcPayOrderDetail();
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
						//gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
						logList.add(updateReckon);
						gcPayOrderDetail.setPayOrderCode(tradeCode);
						gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
						gcPayOrderDetail.setReckonMoney(money);
						gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
						gcPayOrderDetail.setUid(WebHelper.upUuid());
						gcPayOrderDetail.setReferZid(map.get("zid").toString());
						//gcPayOrderDetailMapper.insertSelective(gcPayOrderDetail);
						detailList.add(gcPayOrderDetail);
						newWithdrawMoney = newWithdrawMoney.subtract(money);
					}

					else if (money.compareTo(newWithdrawMoney) == 1
							|| money.compareTo(newWithdrawMoney) == 0) {
						GcReckonLog updateReckon = new GcReckonLog();
						updateReckon.setZid(Integer.valueOf(map.get("zid").toString()));
						updateReckon.setPayedMoney(new BigDecimal(map.get("payed_money").toString()).add(newWithdrawMoney));
						//gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
						logList.add(updateReckon);
						gcPayOrderDetail.setPayOrderCode(tradeCode);
						gcPayOrderDetail.setOrderCode(map.get("order_code").toString());
						gcPayOrderDetail.setReckonMoney(newWithdrawMoney);
						gcPayOrderDetail.setCreateTime(FormatHelper.upDateTime());
						gcPayOrderDetail.setUid(WebHelper.upUuid());
						gcPayOrderDetail.setReferZid(map.get("zid").toString());
						//gcPayOrderDetailMapper.insertSelective(gcPayOrderDetail);
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
					DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
				}
			}
			

			//都进行成功，支付成功，在商户账户明细表中插入支付数据 fengl  2016-1-19
			if(groupPayResult.upFlagTrue()){
				String changeType="4497465200270001";
				try{
					RootResultWeb rootResult=saveTraderAccountLog(manageCode,tradeMoney.toString(),tradeCode,changeType);
	                if(rootResult.getResultCode()!=1){
	                	groupPayResult.inErrorMessage(rootResult.getResultCode());
	                }
				}catch(Exception e){
					groupPayResult.inErrorMessage(918570014);
					e.printStackTrace();
					
				}

			}
		}

		//-------------------end---------------------
		//都进行成功，返回支付成功
		if(groupPayResult.upFlagTrue()){
				groupPayResult.setResultMessage(TopUp.upLogInfo(918523005));
				groupPayResult.setOrderCode(groupPayInput.getOrderCode());
				groupPayResult.setTradeCode(tradeCode);
				groupPayResult.setTradeMoney(tradeMoney.toString());
		}
		
		//更新日志中返回结果
		payMap.put("response_data", new JsonHelper<GroupPayResult>().GsonToJson(groupPayResult));
		payMap.put("response_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataUpdate(payMap, "response_data,response_time","request_code");
		
		WebHelper.unLock(sLockCode);
		
	    return groupPayResult;
	}
	
	/**
	 * 退款
	 * @param groupRefundInput
	 * @param manageCode
	 * @return
	 */
	public GroupRefundResult groupRefund(GroupRefundInput groupRefundInput,String manageCode){
		GroupRefundResult groupRefundResult=new GroupRefundResult();
		
		//记录请求
//		HttpServletRequest request=WebSessionHelper.create().upHttpRequest();
	    MDataMap payMap=new MDataMap();
	    payMap.put("request_code", WebHelper.upCode("RPAY"));
	    payMap.put("order_code", groupRefundInput.getOrderCode());
	    payMap.put("request_target", "grouprefund");
//	    payMap.put("request_url",  "http://" + request.getServerName()+":"+ request.getServerPort()+request.getRequestURI());
	    payMap.put("request_data", new JsonHelper<GroupRefundInput>().GsonToJson(groupRefundInput));
		payMap.put("request_time", FormatHelper.upDateTime());
		payMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataInsert(payMap);
		
		MDataMap vpayMap=DbUp.upTable("gc_vpay_order").one("trade_code",groupRefundInput.getTradeCode(),"trade_type","4497465200200001","flag_enable","1","status","1");
		
		//取account_code
		String accountCode="";
		
		//支付流水号不存在
		if(groupRefundResult.upFlagTrue()){
			if(vpayMap==null){
				groupRefundResult.inErrorMessage(918523006);
			}
			else{
				accountCode=vpayMap.get("account_code");
			}
		}
		//锁定账户
		String sLockCode="";
		if(groupRefundResult.upFlagTrue()){
			sLockCode = WebHelper.addLock(60, accountCode);
			if(StringUtils.isEmpty(sLockCode)){
				groupRefundResult.inErrorMessage(918505211, accountCode);
			}
		}
		
		//是否已退款
		if(groupRefundResult.upFlagTrue()){
			if(new BigDecimal(vpayMap.get("refund_money")).compareTo(BigDecimal.ZERO)==1){
				groupRefundResult.inErrorMessage(918523007);
			}
		}
		
		//退款金额
		BigDecimal tradeMoney=new BigDecimal(groupRefundInput.getRefundMoney());
		
		//退款金额数为正数
		if(groupRefundResult.upFlagTrue()){
			if(tradeMoney.compareTo(BigDecimal.ZERO)!=1){
				groupRefundResult.inErrorMessage(918523008);
			}
		}
		
		//退款金额大于支付金额
		if(groupRefundResult.upFlagTrue()){
			if(tradeMoney.compareTo(new BigDecimal(vpayMap.get("trade_money")))==1){
				groupRefundResult.inErrorMessage(918523009);
			}
		}
		
		//验证用户编号 
		if(groupRefundResult.upFlagTrue()){
			if(!vpayMap.get("member_code").equals(groupRefundInput.getMemberCode())){
				groupRefundResult.inErrorMessage(918523010);
			}
		}
		
		//订单号是否一致
		if(groupRefundResult.upFlagTrue()){
			if(!groupRefundInput.getOrderCode().equals(vpayMap.get("business_order_code"))){
				groupRefundResult.inErrorMessage(918523011);
			}
		}

		
		


		String tradeCode=WebHelper.upCode("VPAY");
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			if(groupRefundResult.upFlagTrue()){
				TxGroupPayService txGroupPayService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupPayService");
				GcVpayOrder gcVpayOrder=new GcVpayOrder();
				gcVpayOrder.setUid(WebHelper.upUuid());
				gcVpayOrder.setTradeCode(tradeCode);
				gcVpayOrder.setAccountCode(accountCode);
				gcVpayOrder.setMemberCode(groupRefundInput.getMemberCode());
				gcVpayOrder.setTradeMoney(tradeMoney);
				gcVpayOrder.setTradeName(vpayMap.get("trade_name"));
				gcVpayOrder.setBusinessOrderCode(vpayMap.get("business_order_code"));
				gcVpayOrder.setBusinessOrderCreateTime(vpayMap.get("business_order_create_time"));
				gcVpayOrder.setBusinessName(vpayMap.get("business_name"));
				gcVpayOrder.setBusinessCode(vpayMap.get("business_code"));
				gcVpayOrder.setTradeType(GroupConst.GROUP_REFUND);
				gcVpayOrder.setTradeClassify("4497465200210001");
				gcVpayOrder.setTradeStatus("4497465200190002");
				gcVpayOrder.setCreateTime(FormatHelper.upDateTime());
				gcVpayOrder.setRefundTime(groupRefundInput.getRefundTime());
				gcVpayOrder.setFlagEnable(1);
				gcVpayOrder.setStatus(1);
				gcVpayOrder.setManageCode(manageCode);
				gcVpayOrder.setRemark(groupRefundInput.getRemark());
				gcVpayOrder.setUqCode(manageCode+"_"+GroupConst.GROUP_REFUND+"_"+groupRefundInput.getOrderCode());
				MDataMap MapTrans=new MDataMap();
				MapTrans.put("refundCode","");
				MapTrans.put("manageCode",manageCode);
				MapTrans.put("newtradeMoney",groupRefundInput.getRefundMoney());
				try{

					txGroupPayService.doGroupRefundSome(gcVpayOrder,groupRefundInput,MapTrans);
				}catch(Exception e){
					groupRefundResult.inErrorMessage(918580102);
					e.printStackTrace();
				}
			}		
			if(groupRefundResult.upFlagTrue()){
				//根据退款额更新相关联订单，保证准确对应

				List<MDataMap> payList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",groupRefundInput.getTradeCode());
				if(payList!=null&&payList.size()>0){
					BigDecimal newWithdrawMoney = tradeMoney;
					List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
					List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
					for (MDataMap map : payList) {
						GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
						
						BigDecimal reckonMoney = new BigDecimal(map.get("reckon_money").toString());
						BigDecimal deRefundMoney = new BigDecimal(map.get("refund_money").toString());
						BigDecimal money=reckonMoney.subtract(deRefundMoney); //reckon_money 与refund_money 差额
						if(money.equals(BigDecimal.ZERO)){ //reckon_money 与refund_money 相等 就匹配下一个支付流水号
							continue;
						}else{
							MDataMap reckonMap=DbUp.upTable("gc_reckon_log").one("zid",map.get("refer_zid"));
							
							if (money.compareTo(newWithdrawMoney) == -1) {//reckon_money 与refund_money 差额 比退款小
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
								updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(money));
								logList.add(updateReckon);
								
								gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
						//		gcPayOrderDetail.setRefundMoney(money);
								gcPayOrderDetail.setRefundMoney(reckonMoney);
								detailList.add(gcPayOrderDetail);
								newWithdrawMoney = newWithdrawMoney.subtract(money);
							}
			
							else if (money.compareTo(newWithdrawMoney) == 1
									|| money.compareTo(newWithdrawMoney) == 0) { //reckon_money 与refund_money 差额 比退款大或者相等 
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
								updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(newWithdrawMoney));
								logList.add(updateReckon);
								
								gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
					//			gcPayOrderDetail.setRefundMoney(newWithdrawMoney);
								gcPayOrderDetail.setRefundMoney(newWithdrawMoney.add(deRefundMoney));
								detailList.add(gcPayOrderDetail);
								
								break;
							}
						}
			
					}
					
					//更新付款单单详情
					StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(zid,refund_money) values ");
					for(int i=0;i<detailList.size();i++){
						if(i==detailList.size()-1){
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append(")");
						}
						else{
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append("),");
						}
						
					}
					detailbBuilder.append(" ON DUPLICATE KEY UPDATE refund_money=VALUES(refund_money)");
					if(detailList.size()>0){					
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
						DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
					}
					
				}
			}
			
			
		}else{
		
			//支付退款记录		
			if(groupRefundResult.upFlagTrue()){
				MDataMap refundMap=new MDataMap();
				refundMap.put("trade_code",tradeCode);
				refundMap.put("account_code", accountCode);
				refundMap.put("member_code", groupRefundInput.getMemberCode());
				refundMap.put("trade_money", tradeMoney.toString());
				refundMap.put("trade_name", vpayMap.get("trade_name"));
				refundMap.put("business_order_code", vpayMap.get("business_order_code"));
				refundMap.put("business_order_create_time", vpayMap.get("business_order_create_time"));
				refundMap.put("business_name", vpayMap.get("business_name"));
				refundMap.put("business_code", vpayMap.get("business_code"));
				refundMap.put("trade_type", GroupConst.GROUP_REFUND);
				refundMap.put("trade_classify", "4497465200210001");//支付彩票
				refundMap.put("trade_status", "4497465200190002");//退款成功
				refundMap.put("create_time", FormatHelper.upDateTime());
				refundMap.put("refund_time", groupRefundInput.getRefundTime());
				refundMap.put("flag_enable", "1");
				refundMap.put("status", "1");
				refundMap.put("manage_code",manageCode);
				refundMap.put("remark",groupRefundInput.getRemark());
				refundMap.put("uq_code", manageCode+"_"+GroupConst.GROUP_REFUND+"_"+groupRefundInput.getOrderCode());
				DbUp.upTable("gc_vpay_order").dataInsert(refundMap);
			}
			
			//退款，账户增加退款
			if(groupRefundResult.upFlagTrue()){
				TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
	            List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
				GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
	            gcWithdrawLog.setAccountCode(accountCode);
	            gcWithdrawLog.setMemberCode(groupRefundInput.getMemberCode());
	            gcWithdrawLog.setWithdrawMoney(tradeMoney);
	            gcWithdrawLog.setWithdrawChangeType("4497465200040009");//支付退款类型
	            gcWithdrawLog.setChangeCodes(tradeCode);
	            withdrawList.add(gcWithdrawLog);
				txGroupAccountService.updateAccount(null, withdrawList);
			}
			
			//更新对应支付单相应信息
			if(groupRefundResult.upFlagTrue()){
				MDataMap updateMap=new MDataMap();
				updateMap.put("trade_code", groupRefundInput.getTradeCode());
				updateMap.put("refund_money", groupRefundInput.getRefundMoney());
				updateMap.put("refund_code", tradeCode);
				updateMap.put("update_time", FormatHelper.upDateTime());
				updateMap.put("refund_time", groupRefundInput.getRefundTime());
				DbUp.upTable("gc_vpay_order").dataUpdate(updateMap, "refund_money,refund_code,update_time,refund_time", "trade_code");
			}
			
			//根据退款额更新相关联订单，保证准确对应
			if(groupRefundResult.upFlagTrue()){
				List<MDataMap> payList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",vpayMap.get("trade_code"));
				if(payList!=null&&payList.size()>0){
					BigDecimal newWithdrawMoney = tradeMoney;
					List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
					List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
					for (MDataMap map : payList) {
						GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
						BigDecimal money = new BigDecimal(map.get("reckon_money").toString());
	
						MDataMap reckonMap=DbUp.upTable("gc_reckon_log").one("zid",map.get("refer_zid"));
						
						if (money.compareTo(newWithdrawMoney) == -1) {
							GcReckonLog updateReckon = new GcReckonLog();
							updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
							updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(money));
							logList.add(updateReckon);
							
							gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
							gcPayOrderDetail.setRefundMoney(money);
							detailList.add(gcPayOrderDetail);
							newWithdrawMoney = newWithdrawMoney.subtract(money);
						}
	
						else if (money.compareTo(newWithdrawMoney) == 1
								|| money.compareTo(newWithdrawMoney) == 0) {
							GcReckonLog updateReckon = new GcReckonLog();
							updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
							updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(newWithdrawMoney));
							logList.add(updateReckon);
							
							gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
							gcPayOrderDetail.setRefundMoney(newWithdrawMoney);
							detailList.add(gcPayOrderDetail);
							
							break;
						}
					}
					
					//更新付款单单详情
					StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(zid,refund_money) values ");
					for(int i=0;i<detailList.size();i++){
						if(i==detailList.size()-1){
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append(")");
						}
						else{
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append("),");
						}
						
					}
					detailbBuilder.append(" ON DUPLICATE KEY UPDATE refund_money=VALUES(refund_money)");
					if(detailList.size()>0){					
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
						DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
					}
					
				}
			}
			//都进行成功，返回退款成功,在商户账户明细表中插入数据   fengl 2016-1-19
			if(groupRefundResult.upFlagTrue()){	
				String changeType="4497465200270002";
				try{
					RootResultWeb rootResult=saveTraderAccountLog(manageCode,tradeMoney.toString(),tradeCode,changeType);
	                if(rootResult.getResultCode()!=1){
	                	groupRefundResult.inErrorMessage(rootResult.getResultCode());
	                }
				}catch(Exception e){
					groupRefundResult.inErrorMessage(918570014);
					e.printStackTrace();
					
				}
			}
		}
		
		
		
		//-------------------end---------------------
		

		//都进行成功，返回支付成功
		if(groupRefundResult.upFlagTrue()){
			groupRefundResult.setResultMessage(TopUp.upLogInfo(918523012));
			groupRefundResult.setRefundCode(tradeCode);
		}
		
		//更新日志中返回结果
		payMap.put("response_data", new JsonHelper<GroupRefundResult>().GsonToJson(groupRefundResult));
		payMap.put("response_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataUpdate(payMap, "response_data,response_time","request_code");
		
		WebHelper.unLock(sLockCode);
		
		return groupRefundResult;
    }
	
	/**
	 * 退款 修改2015-11-18 fengl  退款修改为可多次退款,传入退款编号,同一退款编号处理一次
	 * @param groupRefundInput
	 * @param manageCode
	 * @return
	 */
	public GroupRefundResult groupRefundSome(GroupRefundInput groupRefundInput,String manageCode){
		GroupRefundResult groupRefundResult=new GroupRefundResult();
		//记录请求
//		HttpServletRequest request=WebSessionHelper.create().upHttpRequest();
	    MDataMap payMap=new MDataMap();
	    payMap.put("request_code", WebHelper.upCode("RPAY"));
	    payMap.put("order_code", groupRefundInput.getOrderCode());
	    payMap.put("request_target", "grouprefund");
//	    payMap.put("request_url",  "http://" + request.getServerName()+":"+ request.getServerPort()+request.getRequestURI());
	    payMap.put("request_data", new JsonHelper<GroupRefundInput>().GsonToJson(groupRefundInput));
		payMap.put("request_time", FormatHelper.upDateTime());
		payMap.put("create_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataInsert(payMap);

		MDataMap vpayMap=DbUp.upTable("gc_vpay_order").one("trade_code",groupRefundInput.getTradeCode(),"trade_type","4497465200200001","flag_enable","1","status","1");
		
		//取account_code
		String accountCode="";
		//取总退款金额 退款流水号  fengl
		String refundMoney="";
		String refundCode="";
		
		//支付流水号不存在
		if(groupRefundResult.upFlagTrue()){
			if(vpayMap==null){
				groupRefundResult.inErrorMessage(918523006);
			}
			else{
				accountCode=vpayMap.get("account_code");
				// 取总退款金额  退款流水号  fengl
				refundMoney=vpayMap.get("refund_money");
				refundCode=vpayMap.get("refund_code");
			}
		}
		//锁定账户
		String sLockCode="";
		if(groupRefundResult.upFlagTrue()){
			sLockCode = WebHelper.addLock(60, accountCode);
			if(StringUtils.isEmpty(sLockCode)){
				groupRefundResult.inErrorMessage(918505211, accountCode);
			}
		}
//      ----------start-------
		//判断是否存在退款编号,同一退款编号只能进行一次退款
		String businessTradeCode=groupRefundInput.getBusinessTradeCode();
		if(groupRefundResult.upFlagTrue()){
			if(StringUtil.isBlank(businessTradeCode)){
				groupRefundResult.inErrorMessage(918523018);//退款编号不能为空
			}
		}
		if(groupRefundResult.upFlagTrue()){
				if(DbUp.upTable("gc_vpay_order").count("uq_code",manageCode+"_"+GroupConst.GROUP_REFUND+"_"+vpayMap.get("business_order_code")+"_"+businessTradeCode,
						"flag_enable","1","status","1")>0){
					groupRefundResult.inErrorMessage(918523015);//同一退款编号已进行退款
				}
			
		}
		
		//退款金额
		BigDecimal tradeMoney=new BigDecimal(groupRefundInput.getRefundMoney());
		
		//退款金额数为正数
		if(groupRefundResult.upFlagTrue()){
			if(tradeMoney.compareTo(BigDecimal.ZERO)!=1){
				groupRefundResult.inErrorMessage(918523008);
			}
		}
		
		//退款金额大于支付金额
		if(groupRefundResult.upFlagTrue()){
			if(tradeMoney.compareTo(new BigDecimal(vpayMap.get("trade_money")))==1){
				groupRefundResult.inErrorMessage(918523009);
			}
		}
		
		//退款金额 +之前的总退款金额  大于支付金额   fengl
		BigDecimal  newtradeMoney=tradeMoney.add(new BigDecimal(refundMoney));
		if(groupRefundResult.upFlagTrue()){
			if(newtradeMoney.compareTo(new BigDecimal(vpayMap.get("trade_money")))==1){
				groupRefundResult.inErrorMessage(918523016);  //退款总金额大于支付金额
			}
		}
		
		//验证用户编号 
		if(groupRefundResult.upFlagTrue()){
			if(!vpayMap.get("member_code").equals(groupRefundInput.getMemberCode())){
				groupRefundResult.inErrorMessage(918523010);
			}
		}
		
		//订单号是否一致
		if(groupRefundResult.upFlagTrue()){
			if(!groupRefundInput.getOrderCode().equals(vpayMap.get("business_order_code"))){
				groupRefundResult.inErrorMessage(918523011);
			}
		}
		String tradeCode=WebHelper.upCode("VPAY");
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			if(groupRefundResult.upFlagTrue()){
				TxGroupPayService txGroupPayService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupPayService");
				GcVpayOrder gcVpayOrder=new GcVpayOrder();
				gcVpayOrder.setUid(WebHelper.upUuid());
				gcVpayOrder.setTradeCode(tradeCode);
				gcVpayOrder.setAccountCode(accountCode);
				gcVpayOrder.setMemberCode(groupRefundInput.getMemberCode());
				gcVpayOrder.setTradeMoney(tradeMoney);
				gcVpayOrder.setTradeName(vpayMap.get("trade_name"));
				gcVpayOrder.setBusinessOrderCode(vpayMap.get("business_order_code"));
				gcVpayOrder.setBusinessOrderCreateTime(vpayMap.get("business_order_create_time"));
				gcVpayOrder.setBusinessName(vpayMap.get("business_name"));
				gcVpayOrder.setBusinessCode(vpayMap.get("business_code"));
				gcVpayOrder.setTradeType(GroupConst.GROUP_REFUND);
				gcVpayOrder.setTradeClassify("4497465200210001");
				gcVpayOrder.setTradeStatus("4497465200190002");
				gcVpayOrder.setCreateTime(FormatHelper.upDateTime());
				gcVpayOrder.setRefundTime(groupRefundInput.getRefundTime());
				gcVpayOrder.setFlagEnable(1);
				gcVpayOrder.setStatus(1);
				gcVpayOrder.setManageCode(manageCode);
				gcVpayOrder.setRemark(groupRefundInput.getRemark());
				gcVpayOrder.setUqCode(manageCode+"_"+GroupConst.GROUP_REFUND+"_"+vpayMap.get("business_order_code")+"_"+businessTradeCode);
				gcVpayOrder.setBusinessTradeCode(businessTradeCode);
				MDataMap MapTrans=new MDataMap();
				MapTrans.put("refundCode",refundCode);
				MapTrans.put("manageCode",manageCode);
				MapTrans.put("newtradeMoney",newtradeMoney.toString());
				try{

					txGroupPayService.doGroupRefundSome(gcVpayOrder,groupRefundInput,MapTrans);
				}catch(Exception e){
					groupRefundResult.inErrorMessage(918580102);
					e.printStackTrace();
				}
			}
			
			
		}else{
			//支付退款记录		
			if(groupRefundResult.upFlagTrue()){
				MDataMap refundMap=new MDataMap();
				refundMap.put("trade_code",tradeCode);
				refundMap.put("account_code", accountCode);
				refundMap.put("member_code", groupRefundInput.getMemberCode());
				refundMap.put("trade_money", tradeMoney.toString());
				refundMap.put("trade_name", vpayMap.get("trade_name"));
				refundMap.put("business_order_code", vpayMap.get("business_order_code"));
				refundMap.put("business_order_create_time", vpayMap.get("business_order_create_time"));
				refundMap.put("business_name", vpayMap.get("business_name"));
				refundMap.put("business_code", vpayMap.get("business_code"));
				refundMap.put("trade_type", GroupConst.GROUP_REFUND);
				refundMap.put("trade_classify", "4497465200210001");//支付彩票
				refundMap.put("trade_status", "4497465200190002");//退款成功
				refundMap.put("create_time", FormatHelper.upDateTime());
				refundMap.put("refund_time", groupRefundInput.getRefundTime());
				refundMap.put("flag_enable", "1");
				refundMap.put("status", "1");
				refundMap.put("manage_code",manageCode);
				refundMap.put("remark",groupRefundInput.getRemark());
				refundMap.put("uq_code", manageCode+"_"+GroupConst.GROUP_REFUND+"_"+vpayMap.get("business_order_code")+"_"+businessTradeCode);
				refundMap.put("business_trade_code",businessTradeCode); 
				DbUp.upTable("gc_vpay_order").dataInsert(refundMap);
			}
			
			//退款，账户增加退款
			if(groupRefundResult.upFlagTrue()){
				TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
	            List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
				GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
	            gcWithdrawLog.setAccountCode(accountCode);
	            gcWithdrawLog.setMemberCode(groupRefundInput.getMemberCode());
	            gcWithdrawLog.setWithdrawMoney(tradeMoney);
	            gcWithdrawLog.setWithdrawChangeType("4497465200040009");//支付退款类型
	            gcWithdrawLog.setChangeCodes(tradeCode);
	            withdrawList.add(gcWithdrawLog);
				txGroupAccountService.updateAccount(null, withdrawList);
			}
			
			//更新对应支付单相应信息
			String newRefundCode="";
			if(StringUtil.isBlank(refundCode)){
				newRefundCode=tradeCode;
			}else{
				newRefundCode=refundCode+","+tradeCode;
			}
			
			if(groupRefundResult.upFlagTrue()){
				MDataMap updateMap=new MDataMap();
				updateMap.put("trade_code", groupRefundInput.getTradeCode());
	//			updateMap.put("refund_money", groupRefundInput.getRefundMoney());
				updateMap.put("refund_money", newtradeMoney.toString());
				updateMap.put("refund_code", newRefundCode);
				updateMap.put("update_time", FormatHelper.upDateTime());
				updateMap.put("refund_time", groupRefundInput.getRefundTime());
				DbUp.upTable("gc_vpay_order").dataUpdate(updateMap, "refund_money,refund_code,update_time,refund_time", "trade_code");
			}

			//都进行成功，返回退款成功,在商户账户明细表中插入数据   fengl 2016-1-19
			if(groupRefundResult.upFlagTrue()){				
				String changeType="4497465200270002";
				try{
					RootResultWeb rootResult=saveTraderAccountLog(manageCode,tradeMoney.toString(),tradeCode,changeType);
	                if(rootResult.getResultCode()!=1){
	                	groupRefundResult.inErrorMessage(rootResult.getResultCode());
	                }
				}catch(Exception e){
					groupRefundResult.inErrorMessage(918570014);
					e.printStackTrace();
					
				}
			}
		}
		//-------------------end---------------------
		
		if(groupRefundResult.upFlagTrue()){	
			//根据退款额更新相关联订单，保证准确对应
			if(groupRefundResult.upFlagTrue()){
				List<MDataMap> payList=DbUp.upTable("gc_pay_order_detail").queryByWhere("pay_order_code",vpayMap.get("trade_code"));
				if(payList!=null&&payList.size()>0){
					BigDecimal newWithdrawMoney = tradeMoney;
					List<GcReckonLog> logList=new ArrayList<GcReckonLog>();
					List<GcPayOrderDetail> detailList=new ArrayList<GcPayOrderDetail>();
					for (MDataMap map : payList) {
						GcPayOrderDetail gcPayOrderDetail = new GcPayOrderDetail();
						
						BigDecimal reckonMoney = new BigDecimal(map.get("reckon_money").toString());
						BigDecimal deRefundMoney = new BigDecimal(map.get("refund_money").toString());
						BigDecimal money=reckonMoney.subtract(deRefundMoney); //reckon_money 与refund_money 差额
						if(money.equals(BigDecimal.ZERO)){ //reckon_money 与refund_money 相等 就匹配下一个支付流水号
							continue;
						}else{
							MDataMap reckonMap=DbUp.upTable("gc_reckon_log").one("zid",map.get("refer_zid"));
							
							if (money.compareTo(newWithdrawMoney) == -1) {//reckon_money 与refund_money 差额 比退款小
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
								updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(money));
								logList.add(updateReckon);
								
								gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
						//		gcPayOrderDetail.setRefundMoney(money);
								gcPayOrderDetail.setRefundMoney(reckonMoney);
								detailList.add(gcPayOrderDetail);
								newWithdrawMoney = newWithdrawMoney.subtract(money);
							}
	
							else if (money.compareTo(newWithdrawMoney) == 1
									|| money.compareTo(newWithdrawMoney) == 0) { //reckon_money 与refund_money 差额 比退款大或者相等 
								GcReckonLog updateReckon = new GcReckonLog();
								updateReckon.setZid(Integer.valueOf(map.get("refer_zid")));
								updateReckon.setPayedMoney(new BigDecimal(reckonMap.get("payed_money").toString()).subtract(newWithdrawMoney));
								logList.add(updateReckon);
								
								gcPayOrderDetail.setZid(Integer.valueOf(map.get("zid")));
					//			gcPayOrderDetail.setRefundMoney(newWithdrawMoney);
								gcPayOrderDetail.setRefundMoney(newWithdrawMoney.add(deRefundMoney));
								detailList.add(gcPayOrderDetail);
								
								break;
							}
						}
	
					}
					
					//更新付款单单详情
					StringBuilder detailbBuilder=new StringBuilder(" insert into gc_pay_order_detail(zid,refund_money) values ");
					for(int i=0;i<detailList.size();i++){
						if(i==detailList.size()-1){
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append(")");
						}
						else{
							detailbBuilder.append("(").append(detailList.get(i).getZid()).append(",").append(detailList.get(i).getRefundMoney()).append("),");
						}
						
					}
					detailbBuilder.append(" ON DUPLICATE KEY UPDATE refund_money=VALUES(refund_money)");
					if(detailList.size()>0){					
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
						DbUp.upTable("gc_reckon_log").dataExec(logBuilder.toString(), new MDataMap());
					}
					
				}
			}
		}
		//都进行成功，返回退款成功
		if(groupRefundResult.upFlagTrue()){

			groupRefundResult.setResultMessage(TopUp.upLogInfo(918523012));
			groupRefundResult.setRefundCode(tradeCode);
			
		}
		
		
		//更新日志中返回结果
		payMap.put("response_data", new JsonHelper<GroupRefundResult>().GsonToJson(groupRefundResult));
		payMap.put("response_time", FormatHelper.upDateTime());
		DbUp.upTable("lc_vpay_log").dataUpdate(payMap, "response_data,response_time","request_code");
		
		WebHelper.unLock(sLockCode);
		
		return groupRefundResult;
    }
	
	/**
	 * 对账单
	 * @param groupReconciliationInput                                                                      
	 * @param manageCode
	 * @return
	 */
	public GroupReconciliationResult groupReconciliation(GroupReconciliationInput groupReconciliationInput,String manageCode){
		GroupReconciliationResult groupReconciliationResult=new GroupReconciliationResult();
		MDataMap queryMap=new MDataMap();
		queryMap.inAllValues("manage_code", manageCode,"flag_enable","1","status","1","startTime",groupReconciliationInput.getStartTime(),
				"endTime",groupReconciliationInput.getEndTime());
		String whereString=" manage_code=:manage_code and flag_enable=1 and status=1 and create_time>=:startTime and create_time<=:endTime ";
		if(StringUtils.isNotBlank(groupReconciliationInput.getType())){
			queryMap.put("trade_type", groupReconciliationInput.getType());
			whereString=whereString+" and trade_type=:trade_type ";
		}
		List<GroupReconciliationDetail> detailList=new ArrayList<GroupReconciliationDetail>();
		List<MDataMap> payList= DbUp.upTable("gc_vpay_order").queryAll("", "create_time", whereString,queryMap);
		for(MDataMap order:payList){
			GroupReconciliationDetail groupReconciliationDetail=new GroupReconciliationDetail();
			groupReconciliationDetail.setType(order.get("trade_type"));
			groupReconciliationDetail.setTradeCode(order.get("trade_code"));
			groupReconciliationDetail.setOrderCode(order.get("business_order_code"));
			groupReconciliationDetail.setTradeMoney(order.get("trade_money"));
			groupReconciliationDetail.setTradeStatus(order.get("trade_status"));
			groupReconciliationDetail.setTime(order.get("create_time"));
			detailList.add(groupReconciliationDetail);
		}
		groupReconciliationResult.setDetailList(detailList);
		return groupReconciliationResult;
	}
	/**
	 * 
	 * @param manageCode 应用编号，通过应用编号查询商户编号
	 * @param tradeMoney 金额
	 * @param tradeCode  交易流水号
	 * @return
	 */
	public RootResultWeb  saveTraderAccountLog(String manageCode,String tradeMoney,String tradeCode,String changeType){
		
		RootResultWeb rootResult=new RootResultWeb(); 
		try{
			MDataMap  mDataMap =DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(mDataMap!=null&&StringUtils.isNotBlank(mDataMap.get("trade_code"))){
			  	String traderCode=mDataMap.get("trade_code");
			  	BigDecimal aTradeMoney=new BigDecimal(tradeMoney);
			  	boolean Istrue=true;
			  	if(changeType.equals("4497465200270002")){
			  		aTradeMoney=aTradeMoney.negate();
			  		if(DbUp.upTable("gc_trader_account_change_log").count("change_type","4497465200270001","change_codes",tradeCode)<1){
			  			Istrue=false;
			  		}
			  		
			  	}
			  	if(Istrue){
			        TxGroupAccountService txGroupAccountService = BeansHelper
			                .upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			        
			        GcTraderAccountChangeLog gcTraderAccountChangeLog = new GcTraderAccountChangeLog();
			        gcTraderAccountChangeLog.setTraderCode(traderCode);
			        gcTraderAccountChangeLog.setChangeMoney(aTradeMoney);//支付单 应该为正值，退款单应该为负值
			        gcTraderAccountChangeLog.setChangeType(changeType);//4497465200270001--支付单 4497465200270002--退款单  
			        gcTraderAccountChangeLog.setChangeCodes(tradeCode);                                 
			        
	
		            
			        txGroupAccountService.updateTraderBalanceAndAddLog(gcTraderAccountChangeLog,null);
			  	}
			}else{
				rootResult.inErrorMessage(918570013);
			}
		}catch(Exception e){
			rootResult.inErrorMessage(918570014);
			e.printStackTrace();
			
		}
		return rootResult;
		
	}
}
