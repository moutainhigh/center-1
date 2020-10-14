package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcActiveLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcActiveMonthMapper;
import com.cmall.dborm.txmapper.groupcenter.GcActiveMonthMoneyMapper;
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
import com.cmall.dborm.txmodel.groupcenter.GcActiveMonthMoney;
import com.cmall.dborm.txmodel.groupcenter.GcActiveMonthMoneyExample;
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
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.ReckonOrderSupport;
import com.cmall.membercenter.helper.NickNameHelper;
import com.cmall.ordercenter.common.DateUtil;
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
 * 
 * 按照消费金额返利和清分相关
 * @author gaoYang
 * @date 2016年3月11日下午3:39:57
 *
 */
public class TxReckonOrderServiceForConsumptionAmount extends BaseClass {

	/**
	 * 按照消费金额清分
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public IBaseResult doReckonInByMoneyForOne(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		MWebResult mWebResult = new MWebResult();
		
		List<String> listExec = new ArrayList<String>();
		listExec.add("doReckonInByMoneyForOne");
		
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

						// 开始更新消费金额
						GcActiveLog gcActiveLog = new GcActiveLog();
						gcActiveLog.setAccountCode(accountRelation
								.getAccountCode());
						gcActiveLog.setConsumeMoney(bConsumeMoney);
						gcActiveLog.setRelationLevel(accountRelation.getDeep());
						gcActiveLog.setOrderAccountCode(sAccountCode);
						gcActiveLog.setOrderCode(gcReckonOrderInfo
								.getOrderCode());
						gcActiveLog.setActiveTime(gcReckonOrderInfo
								.getOrderFinishTime());

						gcActiveLog.setChangeCodes(FormatHelper.join(reckonStep
								.getStepCode()));
                        gcActiveLog.setDetailCode(gcReckonOrderDetail.getDetailCode());
                        gcActiveLog.setTraderCode(gcTraderInfo.getTraderCode());
						// 定义增加的活跃数量 此版本默认0
						int iAddMembers = 0;

						listExec.add(upInfo(918515314, accountRelation
								.getAccountCode(), gcActiveLog
								.getConsumeMoney().toString()));

						//更新消费金额
						GcActiveMonth gcActiveMonth = updateActiveCount(
								gcActiveLog, iAddMembers);

						// 定义清分日志数组
						List<GcReckonLog> listReckonLogs = new ArrayList<GcReckonLog>();

						// 定义当前消费金额
						BigDecimal bNowReckon = gcActiveLog.getConsumeMoney();

						// 判断如果能清分金额>0
						if (bNowReckon.compareTo(BigDecimal.ZERO) > 0) {
							
							//清分金额必须>0
							BigDecimal reckonMoney = (bNowReckon.multiply(bScaleReckon)).setScale(2,BigDecimal.ROUND_DOWN);//清分金额保留到分,低于1分钱不在清分
							if(reckonMoney.compareTo(BigDecimal.ZERO)>0){
								GcReckonLog gcReckonLog = new GcReckonLog();

								gcReckonLog.setScaleReckon(bScaleReckon);
								gcReckonLog.setChangeCodes(reckonStep.getStepCode());
								gcReckonLog.setReckonMoney(reckonMoney);
								listReckonLogs.add(gcReckonLog);

								listExec.add(upInfo(918515304, accountRelation
										.getAccountCode(), bNowReckon.toString(),
										bScaleReckon.toString(), gcReckonLog
												.getReckonMoney().toString()));
							}else{
								noReckonReasonFlag="3";//可清分金额*清分比例后的金额<=0
							}
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
							txGroupAccountService.updateAccount(listReckonLogs,null);
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
	 * 更新消费金额统计
	 * @param gcActiveLog
	 * @param iAddMembers
	 * @return
	 */
	public GcActiveMonth updateActiveCount(GcActiveLog gcActiveLog,
			int iAddMembers) {
		
		GcActiveLogMapper gcActiveLogMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcActiveLogMapper");

		GcActiveMonthMapper gcActiveMonthMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcActiveMonthMapper");
		
		GcActiveMonthMoneyMapper gcActiveMonthMoneyMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcActiveMonthMoneyMapper");
		// 定义月份
		String sMonth = DateHelper.upMonth(gcActiveLog.getActiveTime());

		if (StringUtils.isEmpty(gcActiveLog.getLogCode())) {
			gcActiveLog.setLogCode(WebHelper.upCode("GCAL"));
		}

		// 定义编号
		String sActiveCode = gcActiveLog.getAccountCode()
				+ WebConst.CONST_SPLIT_DOWN + sMonth;

		//旧的月度消费依旧统计(不分商家来统计)
		GcActiveMonthExample gcActiveMonthExample = new GcActiveMonthExample();
		gcActiveMonthExample.createCriteria()
				.andAccountCodeEqualTo(gcActiveLog.getAccountCode())
				.andActiveMonthEqualTo(sMonth);

		List<GcActiveMonth> listMonths = gcActiveMonthMapper.selectByExample(gcActiveMonthExample);

		//新的月度消费统计(按照商家来统计)
		GcActiveMonthMoneyExample gcActiveMonthMoneyExample = new GcActiveMonthMoneyExample();
		gcActiveMonthMoneyExample.createCriteria()
				.andAccountCodeEqualTo(gcActiveLog.getAccountCode())
				.andActiveMonthEqualTo(sMonth)
				.andTraderCodeEqualTo(gcActiveLog.getTraderCode());
		List<GcActiveMonthMoney> listMonthsMoney = gcActiveMonthMoneyMapper.selectByExample(gcActiveMonthMoneyExample);
		
		GcActiveMonth gcActiveMonth = new GcActiveMonth();
		GcActiveMonthMoney gcActiveMonthMoney = new GcActiveMonthMoney();

		// 如果存在则更新 不存在插入
		if (listMonths != null && listMonths.size() == 1) {

			gcActiveMonth.setSumMember(listMonths.get(0).getSumMember());
			gcActiveMonth.setSumConsume(listMonths.get(0).getSumConsume()
					.add(gcActiveLog.getConsumeMoney()));
			gcActiveMonth.setUpdateTime(FormatHelper.upDateTime());

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
		}

		//统计gc_active_month_money表商家月度数据
		if (listMonthsMoney != null && listMonthsMoney.size() == 1) {
			gcActiveMonthMoney.setSumMember(listMonthsMoney.get(0).getSumMember());//活跃人数暂时如此,若后续产品需要 在参照上面的代码修正

			gcActiveMonthMoney.setSumConsume(listMonthsMoney.get(0).getSumConsume()
					.add(gcActiveLog.getConsumeMoney()));
			gcActiveMonthMoney.setUpdateTime(FormatHelper.upDateTime());

			// 更新日志上的统计信息
			gcActiveLog.setLastSumConsume(listMonthsMoney.get(0).getSumConsume());
			gcActiveLog.setCurrentSumConsume(gcActiveMonthMoney.getSumConsume());

			// 更新主表信息
			gcActiveMonthMoneyMapper.updateByExampleSelective(gcActiveMonthMoney, gcActiveMonthMoneyExample);
			
		}else{
			gcActiveMonthMoney.setUid(WebHelper.upUuid());
			gcActiveMonthMoney.setAccountCode(gcActiveLog.getAccountCode());
			gcActiveMonthMoney.setActiveCode(WebHelper.upCode("GCAMM"));
			gcActiveMonthMoney.setUqcode(sActiveCode);
			gcActiveMonthMoney.setActiveMonth(sMonth);
			gcActiveMonthMoney.setCreateTime(FormatHelper.upDateTime());
			gcActiveMonthMoney.setSumConsume(gcActiveLog.getConsumeMoney());
			gcActiveMonthMoney.setSumMember(iAddMembers);
			gcActiveMonthMoney.setTraderCode(gcActiveLog.getTraderCode());
			
			gcActiveMonthMoneyMapper.insertSelective(gcActiveMonthMoney);
			
			gcActiveLog.setLastSumConsume(BigDecimal.ZERO);
			gcActiveLog.setCurrentSumConsume(gcActiveLog.getConsumeMoney());
		}
		
		gcActiveLog.setUid(WebHelper.upUuid());
		gcActiveLog.setCreateTime(FormatHelper.upDateTime());

		gcActiveLogMapper.insertSelective(gcActiveLog);

		return gcActiveMonth;
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

	private String upInfo(long lInfoCode, String... sParams) {

		return FormatHelper.upDateTime() + bInfo(lInfoCode, sParams);
	}

	/**
	 * 获取清分订单信息
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
	 * 逆向清分流程第一版
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public IBaseResult doReckonBackByMoneyForOne(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult = new MWebResult();
		listExec.add("doReckonBackByMoneyForOne");
		
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
		GcTraderInfo traderInfo=null;
		//判断退货时间是否超出设置的服务时间范围
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			//商户不存在或者状态不可用，不返利
			if(traderInfo==null||StringUtils.isBlank(traderInfo.getTraderCode())){
				listExec.add(upInfo(918512020, manageCode));
				mWebResult.setResultCode(918512020);
			}
			
			if(mWebResult.upFlagTrue()){
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
					.andTraderCodeEqualTo(traderInfo.getTraderCode())
					.andConsumeMoneyGreaterThan(BigDecimal.ZERO);

			List<GcActiveLog> listGcActiveLogs = gcActiveLogMapper
					.selectByExample(gcActiveLogExample);

			// 开始循环插入反记录的消费记录
			if (listGcActiveLogs != null && listGcActiveLogs.size() > 0) {

				for (GcActiveLog item : listGcActiveLogs) {

					GcActiveLog gcActiveLog = new GcActiveLog();

					gcActiveLog.setAccountCode(item.getAccountCode());
					gcActiveLog.setActiveTime(item.getActiveTime());
					gcActiveLog.setConsumeMoney(item.getConsumeMoney().negate());
					gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
					gcActiveLog.setOrderCode(item.getOrderCode());
					gcActiveLog.setRelationLevel(item.getRelationLevel());
					gcActiveLog.setManageCode(item.getManageCode());
					gcActiveLog.setChangeCodes(reckonStep.getStepCode());
					gcActiveLog.setTraderCode(traderInfo.getTraderCode());
					gcActiveLog.setDetailCode(item.getDetailCode());
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
						gcReckonLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());

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
	 * 第三方退货流程 第一版
	 * @param reckonStep
	 * @param listRelations
	 * @return
	 */
	public IBaseResult doThirdReckonBackByMoneyForOne(ReckonStep reckonStep,
			List<AccountRelation> listRelations) {
		
		List<String> listExec = new ArrayList<String>();
		MWebResult mWebResult=new MWebResult();
		listExec.add("doThirdReckonBackByMoneyForOne");
		
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
		GcTraderInfo traderInfo=null;
		if(mWebResult.upFlagTrue()){
			String orderFinishTime = gcReckonOrderInfo.getOrderFinishTime();//交易成功时间
			String manageCode = gcReckonOrderInfo.getManageCode();
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
			if(appMap != null && appMap.get("trade_code") != null){
				traderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			
			//商户不存，不返利
			if(traderInfo==null||StringUtils.isBlank(traderInfo.getTraderCode())){
				listExec.add(upInfo(918512020, manageCode));
				mWebResult.setResultCode(918512020);
			}
			
			if(mWebResult.upFlagTrue()){
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
			    gcActiveLogExample.createCriteria().andOrderCodeEqualTo(orderCode).
			    	andDetailCodeEqualTo(gcReckonOrderReturnDetail.getDetailCode()).
			    	andTraderCodeEqualTo(traderInfo.getTraderCode()).
			    	andConsumeMoneyGreaterThan(BigDecimal.ZERO);
			    List<GcActiveLog> activeLogList = gcActiveLogMapper.selectByExample(gcActiveLogExample);
				if (activeLogList != null && activeLogList.size() > 0) {
					BigDecimal backMoney=gcReckonOrderDetail.getSumReckonMoney().divide(new BigDecimal(gcReckonOrderDetail.getProductNumber()),2)
							.multiply(new BigDecimal(gcReckonOrderReturnDetail.getProductNumber()));
					for (GcActiveLog item : activeLogList) {
						GcActiveLog gcActiveLog = new GcActiveLog();

						gcActiveLog.setAccountCode(item.getAccountCode());
						gcActiveLog.setActiveTime(item.getActiveTime());
						gcActiveLog.setConsumeMoney(backMoney.negate());
						gcActiveLog.setOrderAccountCode(item.getOrderAccountCode());
						gcActiveLog.setOrderCode(item.getOrderCode());
						gcActiveLog.setRelationLevel(item.getRelationLevel());
						gcActiveLog.setManageCode(item.getManageCode());
						gcActiveLog.setChangeCodes(reckonStep.getStepCode());
						gcActiveLog.setDetailCode(item.getDetailCode());
						gcActiveLog.setTraderCode(traderInfo.getTraderCode());
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
							gcRebateLog.setSkuCode(item.getSkuCode()==null?"":item.getSkuCode());
							gcRebateLog.setDetailCode(item.getDetailCode()==null?"":item.getDetailCode());
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

}
