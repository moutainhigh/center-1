package com.cmall.groupcenter.support;

import java.math.BigDecimal;
import java.security.acl.Group;
import java.text.ParseException;
import java.util.*;

import com.cmall.groupcenter.homehas.RsyncDoCancelReturnOrder;
import com.cmall.groupcenter.homehas.RsyncHomeHas;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.groupcenter.txservice.TxGroupCancelReturnOrderService;
import com.cmall.groupcenter.txservice.TxRebateOrderServiceForConsumptionAmount;
import com.cmall.groupcenter.txservice.TxReckonOrderServiceForConsumptionAmount;
import com.cmall.groupcenter.util.StringHelper;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcTraderInfo;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.ReckonOrderInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.VersionSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 清分执行支撑类
 * 
 * @author srnpr
 * 
 */
public class GroupReckonSupport extends BaseClass implements IBaseInstance {

	/**
	 * 清分所有订单 在有流程变更时触发
	 * 
	 * @return
	 */
	/**
	 * @return
	 */
	public MWebResult reckonAllOrders() {

		MWebResult mWebResult = new MWebResult();

		// 首先执行正向清分订单
		reckonOrderByType(GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
		// 接着执行逆向清分订单
		reckonOrderByType(GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
		//执行第三方退货
		reckonOrderByType(GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK);
		//执行取消退货流程
		reckonOrderByType(GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE);

		return mWebResult;
	}

	/**
	 * 执行清分流程根据传入的清分类型
	 * 
	 * @param sType
	 * @return
	 */
	private MWebResult reckonOrderByType(String sType) {
		
		MDataMap mQueryMap = new MDataMap();
		mQueryMap.put("flag_success", "0");
		mQueryMap.put("exec_type", sType);

		mQueryMap.put("exec_start_time", DateHelper.upDateTimeAdd("-1d"));

		// 取出所有未成功执行过 且流程等于传入的流程参数 且开始执行日期为空或者小于昨天的 以保证同一条在同一天最多执行一次
		for (MDataMap mMap : DbUp
				.upTable("gc_reckon_order_step")
				.queryAll(
						"",
						"create_time",
						"flag_success=:flag_success and exec_type=:exec_type and (exec_start_time='' or exec_start_time<:exec_start_time)",
						mQueryMap)) {

			ReckonStep reckonStep = new ReckonStep();
			reckonStep.setAccountCode(mMap.get("account_code"));
			reckonStep.setOrderCode(mMap.get("order_code"));
			reckonStep.setStepCode(mMap.get("step_code"));
			reckonStep.setFlagSucces(Integer.valueOf(mMap.get("flag_success")));
			reckonStep.setExecType(mMap.get("exec_type"));
			reckonStep.setUqcode(mMap.get("uqcode"));
			reckonStep.setCreateTime(mMap.get("create_time"));

			doReckonOrder(reckonStep);

		}

		return new MWebResult();

	}

	/**
	 * 执行清分流程
	 * 
	 * @param mDataMap
	 * @return
	 */
	public MWebResult doReckonOrder(ReckonStep reckonStep) {
		MWebResult mWebResult = new MWebResult();

		// 判断是否已执行
		if (mWebResult.upFlagTrue()) {
			if (reckonStep.getFlagSucces() != 0) {
				mWebResult.inErrorMessage(918505134, reckonStep.getStepCode());
			}
		}
		
		// 再次强制判断是否可执行
		if (mWebResult.upFlagTrue()) {
			if (!DbUp.upTable("gc_reckon_order_step")
					.one("step_code", reckonStep.getStepCode())
					.get("flag_success").equals("0")) {
				return mWebResult;
			}
		}
		
		//判断是否已执行成功
		if(mWebResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step_log").count("step_code",reckonStep.getStepCode(),"flag_success","1")>0){
				//再次校验gc_reckon_order_step中的flag_success是否为1，否则更新此标记为1
				if(DbUp.upTable("gc_reckon_order_step").count("step_code",reckonStep.getStepCode(),"flag_success","1")<1){
					MDataMap upMap = new MDataMap();
					upMap.put("flag_success", "1");
					upMap.put("step_code", reckonStep.getStepCode());
					DbUp.upTable("gc_reckon_order_step").dataUpdate(upMap, "flag_success", "step_code");
				}
				return mWebResult;
			}
		}
		
		// 账户编号
		String sAccountCode = reckonStep.getAccountCode();

		String logCode=WebHelper.upCode("GRSL");
		// 更新开始执行时间
		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();
			mDataMap.put("step_code", reckonStep.getStepCode());

			// 设置开始执行时间
			mDataMap.put("exec_start_time", FormatHelper.upDateTime());
			// 更新开始执行时间
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mDataMap,
					"exec_start_time", "step_code");
			
			//插入执行日志,日志默认是还未成功
			mDataMap.put("flag_success", "0");
			mDataMap.put("log_code", logCode);
			mDataMap.put("create_time", FormatHelper.upDateTime());
			mDataMap.put("order_code", reckonStep.getOrderCode());
			DbUp.upTable("gc_reckon_order_step_log").dataInsert(mDataMap);

		}

		GroupAccountSupport groupAccountSupport = new GroupAccountSupport();

		// 取出订单创建时间
		MDataMap rInfoDataMap  = DbUp
				.upTable("gc_reckon_order_info")
				.oneWhere("manage_code,order_create_time", "", "", "order_code",
						reckonStep.getOrderCode());
		String sOrderCreateTime = rInfoDataMap.get("order_create_time");
		
		// 获取订单创建时间之前的所有该用户的上线
		List<AccountRelation> listRelations = groupAccountSupport
				.upAccountRelations(reckonStep.getAccountCode(),
						sOrderCreateTime);

		// 定义list 存放账户编号
		List<String> listMaps = new ArrayList<String>();

		// 开始判断所有关联用户是否有微公社账户信息 没有则自动创建
		if (mWebResult.upFlagTrue()) {

			for (AccountRelation accountRelation : listRelations) {
				listMaps.add(accountRelation.getAccountCode());
			}

			mWebResult.inOtherResult(groupAccountSupport
					.checkAndCreateGroupAccount(listMaps
							.toArray(new String[] {})));

		}

		// 开始锁定执行流程编号 防止并发执行
		String sLock = "";

		// 开始加锁
		if (mWebResult.upFlagTrue()) {
			// 将流程编号也加入list中 以进行锁操作
			listMaps.add(reckonStep.getStepCode());
			sLock = WebHelper.addLock(30, listMaps.toArray(new String[] {}));

			if (StringUtils.isEmpty(sLock)) {

				mWebResult.inErrorMessage(918505133, reckonStep.getStepCode());
			}
		}

		//获取订单所属商户的返利方式
		String rebateType = "";
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			String sManageCode = rInfoDataMap.get("manage_code");
			GcTraderInfo gcTraderInfo=null;
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			if(appMap!=null&&StringUtils.isNotBlank(appMap.get("trade_code"))){
				gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
			}
			if(gcTraderInfo != null && StringUtils.isNotBlank(gcTraderInfo.getTraderCode())){
				//商户返利方式
				rebateType = gcTraderInfo.getRebateType();
			}
		}

		// 开始执行流程
		if (mWebResult.upFlagTrue()) {

			TxReckonOrderService txReckonOrderService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			TxReckonOrderServiceForConsumptionAmount txReckonOrderServiceForConsumptionAmount = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderServiceForConsumptionAmount");
			// 这里开始执行 如果执行失败则返回失败消息
			try {
				// 开始执行正向清分流程
				if (reckonStep.getExecType().equals(
						GroupConst.RECKON_ORDER_EXEC_TYPE_IN)) {
					// 加入清分执行流程
					if(VersionHelper.checkServerVersion("11.9.41.59")){
						if (mWebResult.upFlagTrue()){
							if("4497472500080001".equals(rebateType)){
								//按照等级方式清分
								mWebResult.inOtherResult(txReckonOrderService.doReckonInFifth(reckonStep, listRelations));
							}else if("4497472500080002".equals(rebateType)){
								//按照消费金额清分
								mWebResult.inOtherResult(txReckonOrderServiceForConsumptionAmount.doReckonInByMoneyForOne(reckonStep, listRelations));
							}else{
								//默认按照等级方式清分
								mWebResult.inOtherResult(txReckonOrderService.doReckonInFifth(reckonStep, listRelations));
							}
						}
					}else{
						//按照等级方式清分
						mWebResult.inOtherResult(txReckonOrderService.doReckonInFifth(reckonStep, listRelations));
					}
					
					// 添加重置返利流程******
					// ********
					// *******
					// ******
					if (mWebResult.upFlagTrue()) {
						//取消退货后无需在执行重置预计返利流程
						String sWhere = " order_code=:order_code and account_code=:account_code and exec_type=:exec_type ";
						MDataMap mWhereMap=new MDataMap();
						mWhereMap.put("order_code", reckonStep.getOrderCode());
						mWhereMap.put("account_code", reckonStep.getAccountCode());
						mWhereMap.put("exec_type", "4497465200050007");
						if(DbUp.upTable("gc_reckon_order_step").dataCount(sWhere, mWhereMap) < 1){
							ReckonStep rebateStep = new ReckonStep();
							rebateStep.setAccountCode(reckonStep.getAccountCode());
							rebateStep.setExecType(GroupConst.REBATE_ORDER_EXEC_TYPE_RESET);
							rebateStep.setOrderCode(reckonStep.getOrderCode());
							createReckonStep(rebateStep);
						}
					}
				}
				// 开始执行逆向清分流程
				else if (reckonStep.getExecType().equals(
						GroupConst.RECKON_ORDER_EXEC_TYPE_BACK)) {
					if(VersionHelper.checkServerVersion("11.9.41.59")){
						if (mWebResult.upFlagTrue()){
							if("4497472500080001".equals(rebateType)){
								//按照等级方式逆向清分
								mWebResult.inOtherResult(txReckonOrderService.doReckonBackForFourth(reckonStep, listRelations));
							}else if("4497472500080002".equals(rebateType)){
								//按照消费金额逆向清分
								mWebResult.inOtherResult(txReckonOrderServiceForConsumptionAmount.doReckonBackByMoneyForOne(reckonStep, listRelations));
							}else{
								//默认按照等级方式逆向清分
								mWebResult.inOtherResult(txReckonOrderService.doReckonBackForFourth(reckonStep, listRelations));
							}
						}
					}else{
						mWebResult.inOtherResult(txReckonOrderService.doReckonBackForFourth(reckonStep, listRelations));
					}
				}
				//开始执行第三方退货
				else if(reckonStep.getExecType().equals(GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)){
					
					if(VersionHelper.checkServerVersion("11.9.41.59")){
						if (mWebResult.upFlagTrue()){
							if("4497472500080001".equals(rebateType)){
								//按照等级方式逆向清分
								mWebResult.inOtherResult(txReckonOrderService.doThirdReckonBackForFifth(reckonStep, listRelations));
							}else if("4497472500080002".equals(rebateType)){
								//按照消费金额逆向清分
								mWebResult.inOtherResult(txReckonOrderServiceForConsumptionAmount.doThirdReckonBackByMoneyForOne(reckonStep, listRelations));
							}else{
								//默认按照等级方式逆向清分
								mWebResult.inOtherResult(txReckonOrderService.doThirdReckonBackForFifth(reckonStep, listRelations));
							}
						}
					}else{
						//按照等级方式逆向清分
						mWebResult.inOtherResult(txReckonOrderService.doThirdReckonBackForFifth(reckonStep, listRelations));
					}
						
				}else if(reckonStep.getExecType().equals(GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE)){
                    //开始执行“取消退货清分流程”
                    mWebResult.inOtherResult(doCancelReturnOrderProcess(reckonStep.getOrderCode()));
                }
				// 如果没有 则报失败信息
				else {

                    //如果是正常的execType加上一个 _c ，则代表是参与了取消退货清分流程的数据，属于正常的，但并不报错
                    Map<String,String>  validExecType= new HashMap<String,String>();
                    validExecType.put(GroupConst.RECKON_ORDER_EXEC_TYPE_IN+"_"+GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG,GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
                    validExecType.put(GroupConst.RECKON_ORDER_EXEC_TYPE_BACK+"_"+GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG,GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
                    validExecType.put(GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK+"_"+GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG,GroupConst.RECKON_ORDER_EXEC_TYPE_IN);

                    //如果也不存在于取消退货清分流程的流程，才不属于正常流程
                    if (validExecType.get(reckonStep.getExecType())==null){
                        mWebResult.inErrorMessage(918505135,
                                reckonStep.getStepCode(), reckonStep.getExecType());
                    }

				}
			} catch (Exception e) {
				e.printStackTrace();
				mWebResult.inErrorMessage(918505136, e.getMessage());
			}

		}

		MDataMap mUpdatemMap = new MDataMap();
		
		mUpdatemMap.put("exec_finish_time", FormatHelper.upDateTime());
		mUpdatemMap.put("exec_result", mWebResult.upJson());
		mUpdatemMap.put("step_code", reckonStep.getStepCode());
		
		// 如果都执行成功，则将执行成功标记位置为1
		if (mWebResult.upFlagTrue()) {
			mUpdatemMap.put("flag_success", "1");
		} else {
			mUpdatemMap.put("flag_success",
					String.valueOf(reckonStep.getFlagSucces()));
			if(reckonStep.getExecType().equals(
						GroupConst.RECKON_ORDER_EXEC_TYPE_BACK)){
			        //逆向未成功的判断时间差，执行频率依据时间差
			        try {
			        	Calendar cal = Calendar.getInstance(); 
						cal.setTime(FormatHelper.parseDate(reckonStep.getCreateTime()));
						long time1 = cal.getTimeInMillis();               
				        cal.setTime(FormatHelper.parseDate(FormatHelper.upDateTime()));  
				        long time2 = cal.getTimeInMillis();       
				        long between_days=(time2-time1)/(1000*3600*24);  
				        int days=Integer.parseInt(String.valueOf(between_days));
				        if(days>20){
				        	mUpdatemMap.put("exec_start_time", FormatHelper.upDateTimeAdd(String.valueOf(days)+"d"));
				        	DbUp.upTable("gc_reckon_order_step").dataUpdate(mUpdatemMap,"exec_start_time", "step_code");
				        }
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
			            
			}
		}
		
		if(VersionHelper.checkServerVersion("11.9.41.59")){
			mUpdatemMap.put("rebate_type", rebateType);
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mUpdatemMap,
					"exec_finish_time,exec_result,flag_success,rebate_type", "step_code");
		}else{
			DbUp.upTable("gc_reckon_order_step").dataUpdate(mUpdatemMap,
					"exec_finish_time,exec_result,flag_success", "step_code");
		}

		
		//更新执行日志结果
        mUpdatemMap.put("log_code", logCode);
		DbUp.upTable("gc_reckon_order_step_log").dataUpdate(mUpdatemMap, "exec_finish_time,exec_result,flag_success", "log_code");
		
		// 该逻辑放在最后 如果锁定成功后 则开始解锁流程
		if (StringUtils.isNotEmpty(sLock)) {
			WebHelper.unLock(sLock);
		}

		return mWebResult;
	}

	/**
	 * 插入清分执行步骤表
	 * 
	 * @param reckonStep
	 * @return
	 */
	public MWebResult createReckonStep(ReckonStep reckonStep) {
		MWebResult mWebResult = new MWebResult();
		// 定义唯一约束
		String sUqcode = reckonStep.getExecType() + WebConst.CONST_SPLIT_DOWN
				+ reckonStep.getOrderCode();

		// 如果唯一约束不存在
		if (DbUp.upTable("gc_reckon_order_step").count("uqcode", sUqcode) == 0) {

			if (StringUtils.isEmpty(reckonStep.getStepCode())) {
				reckonStep.setStepCode(WebHelper.upCode("GCROS"));
			}

			DbUp.upTable("gc_reckon_order_step").insert("step_code",
					reckonStep.getStepCode(), "order_code",
					reckonStep.getOrderCode(), "exec_type",
					reckonStep.getExecType(), "create_time",
					FormatHelper.upDateTime(), "account_code",
					reckonStep.getAccountCode(), "uqcode", sUqcode, "remark",
					reckonStep.getRemark());

		} else {
			mWebResult.inErrorMessage(915805140);
		}
		return mWebResult;

	}

	/**
	 * 根据ERP订单初始化微公社订单信息
	 * 
	 * @param sOrderCode
	 *            订单编号
	 * @param sFinishTime
	 *            订单签收时间
	 * @return
	 */
	public MWebResult initByErpOrder(String sOrderCode, String sFinishTime) {

		MDataMap mOrderMap = DbUp.upTable("oc_orderinfo").one("order_code",
				sOrderCode);
		MWebResult mResult = new MWebResult();
		if (mOrderMap != null && mResult.upFlagTrue()) {

			// 如果清分订单不存在 则插入清分订单列表中
			if (DbUp.upTable("gc_reckon_order_info").count("order_code",
					sOrderCode) == 0) {

				TxReckonOrderService txReckonOrderService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");

				ReckonOrderInfo reckonOrderInfo = new ReckonOrderInfo();

				GcReckonOrderInfo gcReckonOrderInfo = new GcReckonOrderInfo();

				// 这个字段由插入逻辑来判断
				// gcReckonOrderInfo.setAccountCode("");

				gcReckonOrderInfo.setCreateTime(FormatHelper.upDateTime());
				gcReckonOrderInfo.setFlagReckon(1);
				gcReckonOrderInfo.setManageCode(mOrderMap.get("seller_code"));
				gcReckonOrderInfo.setMemberCode(mOrderMap.get("buyer_code"));
				gcReckonOrderInfo.setOrderCode(mOrderMap.get("order_code"));
				gcReckonOrderInfo.setOrderCreateTime(mOrderMap
						.get("create_time"));
				gcReckonOrderInfo.setOrderFinishTime(sFinishTime);
				gcReckonOrderInfo.setOrderMoney(new BigDecimal(mOrderMap
						.get("due_money")));
				gcReckonOrderInfo.setProcessRemark("");

				gcReckonOrderInfo.setUid(WebHelper.upUuid());

				reckonOrderInfo
						.setOrderList(new ArrayList<GcReckonOrderDetail>());

				gcReckonOrderInfo.setReckonMoney(BigDecimal.ZERO);

				ReckonOrderSupport reckonOrderSupport = new ReckonOrderSupport();
				for (MDataMap mDetailMap : DbUp.upTable("oc_orderdetail")
						.queryByWhere("order_code", sOrderCode)) {

					GcReckonOrderDetail gcReckonOrderDetail = new GcReckonOrderDetail();

					String sDetailCode = mDetailMap.get("detail_code");
					if (StringUtils.isEmpty(sDetailCode)) {
						sDetailCode = mDetailMap.get("order_code")
								+ WebConst.CONST_SPLIT_DOWN
								+ mDetailMap.get("zid");
					}

					gcReckonOrderDetail.setDetailCode(sDetailCode);

					gcReckonOrderDetail.setFlagReckon(1);
					gcReckonOrderDetail.setOrderCode(gcReckonOrderInfo
							.getOrderCode());
					gcReckonOrderDetail.setPriceBase(new BigDecimal(mDetailMap
							.get("sku_price")));

					gcReckonOrderDetail.setPriceCost(new BigDecimal(mDetailMap
							.get("sku_price")));

					gcReckonOrderDetail.setPriceSell(new BigDecimal(mDetailMap
							.get("sku_price")));

					if (StringUtils.isNotEmpty(mDetailMap.get("sku_code"))) {
						gcReckonOrderDetail.setProductCode(mDetailMap
								.get("sku_code"));
					}

					if (StringUtils.isEmpty(gcReckonOrderDetail
							.getProductCode())) {
						gcReckonOrderDetail.setProductCode(mDetailMap
								.get("product_code"));
					}

					// 设置清分订单
					gcReckonOrderDetail.setPriceReckon(reckonOrderSupport
							.upReckonProduct(gcReckonOrderInfo.getManageCode(),
									gcReckonOrderDetail.getProductCode(),
									gcReckonOrderInfo.getOrderCreateTime(),
									gcReckonOrderDetail.getPriceSell()));

					gcReckonOrderDetail.setProductName(mDetailMap
							.get("sku_name"));
					gcReckonOrderDetail.setProductNumber(Integer
							.valueOf(mDetailMap.get("sku_num")));
					gcReckonOrderDetail.setSumReckonMoney(gcReckonOrderDetail
							.getPriceReckon().multiply(
									BigDecimal.valueOf(gcReckonOrderDetail
											.getProductNumber())));

					gcReckonOrderDetail.setUid(WebHelper.upUuid());

					reckonOrderInfo.getOrderList().add(gcReckonOrderDetail);

					gcReckonOrderInfo.setReckonMoney(gcReckonOrderInfo
							.getReckonMoney().add(
									gcReckonOrderDetail.getSumReckonMoney()));

				}

				reckonOrderInfo.setOrderInfo(gcReckonOrderInfo);

				mResult.inOtherResult(txReckonOrderService
						.insertReckonOrder(reckonOrderInfo));
			}

		}

		// 开始判断是否传入签收时间
		if (mResult.upFlagTrue()) {

			// 如果传入了订单签收时间 则开始插入清分流程
			if (StringUtils.isNotEmpty(sFinishTime)) {

				String sReckon_Type = GroupConst.RECKON_ORDER_EXEC_TYPE_IN;

				// 更新清分订单上的订单完成时间
				MDataMap mUpdateOrderMap = new MDataMap();
				mUpdateOrderMap.inAllValues("order_code", sOrderCode,
						"order_finish_time", sFinishTime);

				// 更新表
				DbUp.upTable("gc_reckon_order_info").dataUpdate(
						mUpdateOrderMap, "order_finish_time", "order_code");

				// 开始插入正向清分流程

				ReckonStep reckonStep = new ReckonStep();
				reckonStep.setAccountCode(DbUp
						.upTable("mc_member_info")
						.dataGet(
								"account_code",
								"",
								new MDataMap("member_code", mOrderMap
										.get("buyer_code"))).toString());
				reckonStep.setExecType(sReckon_Type);
				reckonStep.setOrderCode(sOrderCode);

				mResult.inOtherResult(new GroupReckonSupport()
						.createReckonStep(reckonStep));

			}

		}

		return mResult;

	}

	/**
	 * 通过订单号流程号检查创建流程
	 * 
	 * @param orderCode
	 * @return
	 */
	public MWebResult checkCreateStep(String orderCode, String execType) {
		MWebResult mWebResult = new MWebResult();
		MDataMap mOrderMap = DbUp.upTable("gc_reckon_order_info").one(
				"order_code", orderCode);
		// 判断订单存在且订单参加清分流程
		if (mOrderMap != null && mOrderMap.get("flag_reckon").equals("1")) {
			ReckonStep reckonStep = new ReckonStep();
			reckonStep.setAccountCode(mOrderMap.get("account_code"));
			reckonStep.setExecType(execType);
			reckonStep.setOrderCode(orderCode);
			if (execType.equals(GroupConst.REBATE_ORDER_EXEC_TYPE_IN)) {
				Date initDate = DateHelper.parseDate("2015-02-15 00:00:00");
				if (DateHelper.parseDate(mOrderMap.get("order_create_time"))
						.after(initDate)) {
					mWebResult.inOtherResult(createReckonStep(reckonStep));
				}
			} else {
				mWebResult.inOtherResult(createReckonStep(reckonStep));
			}
		} else {
			bLogInfo(918505132, orderCode);
			mWebResult.inErrorMessage(918505132, orderCode);
		}

		return mWebResult;
	}


	//取消退货的流程

	/**
	 * @param orderCode 执行取消退货流程
	 */
	public MWebResult doCancelReturnOrderProcess(String orderCode){
		TxGroupCancelReturnOrderService txGroupCancelReturnOrderService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupCancelReturnOrderService");
        MWebResult result = new MWebResult();

		MDataMap mWhereMap = new MDataMap();

		//先找到其的正向清分的数据
		mWhereMap.put("exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
		mWhereMap.put("order_code", orderCode);

		Map stepData = DbUp.upTable("gc_reckon_order_step").dataSqlOne(" SELECT * FROM gc_reckon_order_step step " +
				"WHERE step.order_code=:order_code  " +
				"AND step.exec_type=:exec_type ",mWhereMap);

		//该数据若存在，则开始判断其是否含有退货历程的数据，
		if (stepData!=null){

			//如果含有退货流程，则可以进行取消退货的流程，否则不能进行。
			String flagSuccess = checkHasReturnProcess(orderCode);
			if(flagSuccess!=null){

				//如果之前没有过取消退货流程的订单方可进行此次流程
				if(!checkIfBeenCanceledReturnProcess(stepData)){

					//初始化欲要插入新的该订单的正向清分的数据。
//                        MDataMap newReckonExecTypeInMap = new MDataMap();

					ReckonStep newReckonStep = new ReckonStep();

					newReckonStep.setOrderCode(orderCode);
					newReckonStep.setExecType(GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
					newReckonStep.setAccountCode(StringHelper.getStringFromMap(stepData, "account_code"));
					result.inOtherResult(updateHistoryOrderExecTypeAndInsert(newReckonStep,orderCode,flagSuccess));
				}else {
					txGroupCancelReturnOrderService.faildCancelReturnProcess(orderCode);
                    result.inErrorMessage(918548003,orderCode);
                }
			}else{
				txGroupCancelReturnOrderService.faildCancelReturnProcess(orderCode);
                result.inErrorMessage(918548002,orderCode);
            }

		}else {

			//流程状态修改为失败
			txGroupCancelReturnOrderService.faildCancelReturnProcess(orderCode);
            result.inErrorMessage(918548001,orderCode);
        }

        return result;
	}


	/**
	 * 判断该订单是否有退货流程，
	 * 如果没有退货流程，则不需要走取消退货的流程,如果含有退货流程，则可以进行取消退货的流程
	 * @param orderCode
	 * @return
	 */
	public String checkHasReturnProcess(String orderCode){

		boolean hasReturnProcess=true;

		MDataMap mWhereMap = new MDataMap();

//        mWhereMap.put("exec_type", GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK);
		mWhereMap.put("exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
		mWhereMap.put("order_code", orderCode);

		Map data = DbUp.upTable("gc_reckon_order_step").dataSqlOne(" SELECT * FROM gc_reckon_order_step step " +
				"WHERE step.order_code=:order_code  " +
				"AND (step.exec_type=:exec_type) ",mWhereMap);

		if(data==null){
			hasReturnProcess=false;
		}

		//查询该数据的清分有没有执行成功,若没有成功，则不需要在最后插入正向清分的流程
		String flagSuccess=null;
		if(hasReturnProcess){
			flagSuccess=StringHelper.getStringFromMap(data,"flag_success");
		}

		return flagSuccess;
	}

	/**
	 * 检查是否已经有过一次取消退货流程的情况了。
	 * 目前取消退货的流程仅能有一次操作。
	 * @param stepData 从gc_reckon_order_step表中查到的数据
	 * @return
	 */
	public boolean checkIfBeenCanceledReturnProcess(Map stepData){

		String orderCode = String.valueOf(stepData.get("order_code"));

		boolean hasCancelReturnProcess=true;

		MDataMap mWhereMap = new MDataMap();

		//根据变异后的uqcode进行查询。因为如果改订单已经有过取消退货的程序，那么此处必然存在含有后缀码的uqcode。
		//而没有走过取消退货的流程的订单的uqcode是  ordercode+"_"+exec_type的组合
		mWhereMap.put("uqcode", GroupConst.RECKON_ORDER_EXEC_TYPE_IN+"_"+orderCode+"_"+ GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG);
		mWhereMap.put("order_code", orderCode);

		Map data = DbUp.upTable("gc_reckon_order_step").dataSqlOne(" SELECT * FROM gc_reckon_order_step step " +
				"WHERE step.order_code=:order_code  " +
				"AND step.uqcode=:uqcode ",mWhereMap);

		if(data==null){
			hasCancelReturnProcess=false;
		}
		return hasCancelReturnProcess;
	}


	/**
	 * 调用一个service，且必须走事务
	 * 1.将库中的orderCOde对应的数据的uqcode以及execType的值均update，
	 * 即在原有的值的基础上加上一个后缀 "_c"
	 * 2.同时插入一条正向清分的数据。
	 * 3.修改gc_reckon_log中的对应订单号的数据的flag_status的值为0，即代表无效
	 * @param orderCode
	 */
	private MWebResult updateHistoryOrderExecTypeAndInsert(ReckonStep newReckonStep,String orderCode,String flagSuccess){
		TxGroupCancelReturnOrderService service = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupCancelReturnOrderService");

		return service.doUpdateCancelReturnOrder(newReckonStep,orderCode,flagSuccess);
	}



}
