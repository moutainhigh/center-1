package com.cmall.groupcenter.homehas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;
import com.cmall.groupcenter.homehas.config.RsyncConfigSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelOrderInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCustInfo.CustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncOrders;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.model.ReckonOrderInfo;
import com.cmall.groupcenter.service.OrderForLD;
import com.cmall.groupcenter.support.ReckonOrderSupport;
import com.cmall.groupcenter.txservice.TxPurchaseOrderService;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInputHomehas;
import com.cmall.membercenter.txservice.TxMemberForHomeHas;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 已废弃，替换类：
 * @see com.cmall.groupcenter.homehas.RsyncSyncOrdersV2
 */
@Deprecated
public class RsyncSyncOrders
		extends
		RsyncHomeHas<RsyncConfigSyncOrders, RsyncRequestSyncOrders, RsyncResponseSyncOrders> {

	final static RsyncConfigSyncOrders RSYNC_CONFIG_SYNC_ORDERS = new RsyncConfigSyncOrders();

	public RsyncConfigSyncOrders upConfig() {
		// TODO Auto-generated method stub
		return RSYNC_CONFIG_SYNC_ORDERS;
	}

	public RsyncRequestSyncOrders upRsyncRequest() {
		// 返回输入参数
		RsyncRequestSyncOrders request = new RsyncRequestSyncOrders();

		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_date(rsyncDateCheck.getStartDate());
		request.setEnd_date(rsyncDateCheck.getEndDate());

		return request;
	}
    
	private TxReckonOrderService txReckonOrderService;

	private HomehasOrderProcess homehasOrderProcess = new HomehasOrderProcess();

	/**
	 * 插入清分订单表
	 * 
	 * @param orderInfo
	 * @return
	 */
	public MWebResult insertReckonOrder(RsyncModelOrderInfo orderInfo) {

		MWebResult mWebResult = new MWebResult();

		String sMemberCode = "";

		String sManageCode = "";

		String sAppProducts = "133293,133293,133294,133296,133297,133298,133303,133300,133302,";

		// 特定判断 如果是嘉玲圈的商品 则将manageCode置为嘉玲圈
		if (StringUtils.contains(sAppProducts, orderInfo.getYc_goods_num()
				+ ",")) {
			sManageCode = MemberConst.MANAGE_CODE_APP;
		} else if (orderInfo.getMedi_mclss_id().equals("41")) {
			sManageCode = MemberConst.MANAGE_CODE_APP;
		} else if (orderInfo.getMedi_mclss_id().equals("49")) {
			sManageCode = MemberConst.MANAGE_CODE_SPDOG;
		} else {
			sManageCode = MemberConst.MANAGE_CODE_HOMEHAS;
		}

		// 获取家有会员对应的用户编号
		if (mWebResult.upFlagTrue()) {

			MDataMap mMemberMap = DbUp.upTable("mc_extend_info_homehas").one(
					"homehas_code", orderInfo.getYc_vipuser_num());
			
			if(mMemberMap==null){
				//调用会员信息查询接口,添加用户信息
				try {
					RsyncCustInfo rsyncCustInfo=new RsyncCustInfo();
					rsyncCustInfo.upRsyncRequest().setCust_id(orderInfo.getYc_vipuser_num());
					rsyncCustInfo.doRsync();
					if(rsyncCustInfo.getResponseObject()!=null&&rsyncCustInfo.getResponseObject().getResult()!=null&&rsyncCustInfo.getResponseObject().getResult().size()>0){
					    mWebResult.inOtherResult(reginUser(rsyncCustInfo.getResponseObject().getResult().get(0)));
					}
					mMemberMap= DbUp.upTable("mc_extend_info_homehas").one("homehas_code", orderInfo.getYc_vipuser_num());
				} catch (Exception e) {
					e.printStackTrace();
					mWebResult.setResultCode(0);
					mWebResult.setResultMessage(e.getMessage());
				}
				
			}
			
			if (mMemberMap != null) {
				sMemberCode = mMemberMap.get("member_code");
			}

			if (StringUtils.isBlank(sMemberCode)) {
				mWebResult.inErrorMessage(918505131,
						orderInfo.getYc_vipuser_num());
			}

			// 如果是嘉玲圈的订单 则根据account_code找到嘉玲圈的对应的用户编号
			if (mWebResult.upFlagTrue()) {
				if (sManageCode.equals(MemberConst.MANAGE_CODE_APP)) {

					String sAccountCode = DbUp
							.upTable("mc_member_info")
							.dataGet("account_code",
									"member_code='" + sMemberCode + "'",
									new MDataMap()).toString();

					// 根据account_code编号取出对应的app中的用户编号

					/*
					 * sMemberCode = DbUp .upTable("mc_member_info") .dataGet(
					 * "member_code",
					 * "account_code=:account_code and manage_code=:manage_code"
					 * , new MDataMap("account_code", sAccountCode,
					 * "manage_code", sManageCode)).toString();
					 */

					MDataMap mAppMemberMap = DbUp.upTable("mc_member_info")
							.one("account_code", sAccountCode, "manage_code",
									sManageCode);

					// 判断是否有订单
					if (mAppMemberMap != null && mAppMemberMap.size() > 0) {
						sMemberCode = mAppMemberMap.get("member_code");
					} else {
						sMemberCode = "";
					}

				}

				if (StringUtils.isBlank(sMemberCode)) {
					mWebResult.inErrorMessage(918505131,
							orderInfo.getYc_vipuser_num());
				}
			}

		}

		if (mWebResult.upFlagTrue()) {

			// 定义默认不进微公社
			boolean bFlagReckon = false;

			String sMediaClass = orderInfo.getMedi_mclss_id();

			// 特定校验 34app 39扫码购 42微信商城 进微公社清分体系
			if (sMediaClass.equals("34") || sMediaClass.equals("39")
					|| sMediaClass.equals("42")) {
				bFlagReckon = true;
			}

			if (bFlagReckon) {
				ReckonOrderSupport reckonOrderSupport = new ReckonOrderSupport();

				String sOrderCreateTime = orderInfo.getYc_orderform_time();

				// 这里初始化是由于家有同步订单是单条数据同步过来
				GcReckonOrderDetail gcOrderDetail = new GcReckonOrderDetail();

				gcOrderDetail.setOrderCode(orderInfo.getYc_orderform_num());
				// 设置明细的编号为订单号加流水号,以确保唯一约束
				gcOrderDetail.setDetailCode(orderInfo.getYc_orderform_num()
						+ WebConst.CONST_SPLIT_DOWN + orderInfo.getOrd_seq());
				gcOrderDetail.setPriceBase(orderInfo.getYc_after_base_price());
				gcOrderDetail.setPriceCost(orderInfo.getYc_cost_price());

				// gcOrderDetail.setPriceReckon(orderInfo.getYc_after_base_price());
				// 这里价格为黑名单清分价格

				gcOrderDetail.setPriceSell(orderInfo.getYc_after_base_price());
				gcOrderDetail.setProductCode(orderInfo.getYc_goods_num());
				gcOrderDetail.setProductName(orderInfo.getYc_goods_name());
				gcOrderDetail.setProductNumber(orderInfo.getYc_goods_count());

				BigDecimal bAfterPrice = reckonOrderSupport.upReckonProduct(
						sManageCode, orderInfo.getYc_goods_num(),
						sOrderCreateTime, orderInfo.getYc_after_base_price());

				gcOrderDetail.setPriceReckon(bAfterPrice);

				gcOrderDetail.setSumReckonMoney(gcOrderDetail.getPriceReckon()
						.multiply(
								BigDecimal.valueOf(gcOrderDetail
										.getProductNumber())));

				// 如果商品价格没被黑名单设置调整且商品的实付应付金额不为空 则按照家有传过来的实际应付金额计算
				if (bAfterPrice.compareTo(orderInfo.getYc_after_base_price()) == 0
						&& StringUtils.isNotBlank(orderInfo
								.getAcctf_send_schd_amt())) {

					gcOrderDetail.setSumReckonMoney(

					new BigDecimal(orderInfo.getAcctf_send_schd_amt()));

					// gcOrderDetail.setSumReckonMoney(sumReckonMoney);

				}
				
				//新增sku_code
				String sku_code=new ProductService().getSkuByKey(orderInfo.getYc_goods_num(), orderInfo.getYc_goods_color(), orderInfo.getGoods_style(), false);
				if(StringUtils.isNotBlank(sku_code)){
					gcOrderDetail.setSkuCode(sku_code);
				}else{
					gcOrderDetail.setSkuCode(orderInfo.getYc_goods_num());
				}
				

				// 如果订单不存在 则插入清分订单表
				if (DbUp.upTable("gc_reckon_order_info").count("order_code",
						orderInfo.getYc_orderform_num()) == 0) {

					ReckonOrderInfo reckonOrderInfo = new ReckonOrderInfo();

					GcReckonOrderInfo gcOrderInfo = new GcReckonOrderInfo();

					gcOrderInfo.setOrderCode(orderInfo.getYc_orderform_num());
					//扫码购订单归为扫码购商户
					if(sMediaClass.equals("39")){
						gcOrderInfo.setManageCode(MemberConst.MANAGE_CODE_SCANBUY);
					}
					else{
						gcOrderInfo.setManageCode(MemberConst.MANAGE_CODE_HOMEHAS);
					}
					gcOrderInfo.setMemberCode(sMemberCode);
					gcOrderInfo.setOrderCreateTime(sOrderCreateTime);
					gcOrderInfo.setOutOrderCode(orderInfo.getYc_orderform_num());

					reckonOrderInfo.setOrderInfo(gcOrderInfo);

					List<GcReckonOrderDetail> listDetails = new ArrayList<GcReckonOrderDetail>();
					listDetails.add(gcOrderDetail);
					reckonOrderInfo.setOrderList(listDetails);

					// 插入订单
					mWebResult.inOtherResult(txReckonOrderService
							.insertReckonOrder(reckonOrderInfo));
					// 刷新订单统计
					txReckonOrderService.refreshOrder(orderInfo
							.getYc_orderform_num());

				}
				// 如果订单存在 则判断是否需要插入明细表
				else {
					//如果订单存在 则更新明细表中的相应字段内容--sum_reckon_money 修改 时间 2015.8.19 fengl
					//开发的版本号定为11.9.41.51     时间为2014.8.21
				    //===============================start=====================================
					// 判断明细是否存在 如果不存在则插入
					if (DbUp.upTable("gc_reckon_order_detail").count(
							"detail_code", gcOrderDetail.getDetailCode()) == 0) {

						txReckonOrderService.insertDetail(gcOrderDetail);
						// 刷新订单统计
						txReckonOrderService.refreshOrder(orderInfo
								.getYc_orderform_num());

					}else{
						//如果存在则更新 清分明细表  fengl
						//清分已经完成不更新，没成功清分需要更新
						if(DbUp.upTable("gc_reckon_order_step").count("uqcode","4497465200050001_"+orderInfo.getYc_orderform_num(),"flag_success","1")==0){
														
							List<GcReckonOrderDetail> list=txReckonOrderService.upGcReckonOrderDetailCode(gcOrderDetail.getDetailCode());
							if(list!=null&&list.size()>0){
								for(GcReckonOrderDetail de:list){
									de.setSumReckonMoney(gcOrderDetail.getSumReckonMoney()); //更新总清分金额
									de.setPriceBase(gcOrderDetail.getPriceBase());
									de.setPriceCost(gcOrderDetail.getPriceCost());
									de.setPriceReckon(gcOrderDetail.getPriceReckon());
									de.setPriceSell(gcOrderDetail.getPriceSell());
									de.setProductCode(gcOrderDetail.getProductCode());
									de.setProductName(gcOrderDetail.getProductName());
									de.setProductNumber(gcOrderDetail.getProductNumber());
									de.setSkuCode(gcOrderDetail.getSkuCode());
									// 更新清分详情表中的订单
									txReckonOrderService.updateReckonDetail(de);
									// 刷新订单统计
									txReckonOrderService.refreshOrder(orderInfo
											.getYc_orderform_num());
								}
							}
						}
					}
						
						
				 //===============================end=====================================		
						
				}

				// 开始更新订单状态 执行各自清分流程
				GcExtendOrderStatusHomehas gcExtendOrderStatusHomehas = new GcExtendOrderStatusHomehas();
				gcExtendOrderStatusHomehas.setChangeStatus(orderInfo
						.getChg_cd());
				gcExtendOrderStatusHomehas.setOrderCode(orderInfo
						.getYc_orderform_num());
				gcExtendOrderStatusHomehas.setOrderStatus(orderInfo
						.getYc_orderform_status());
				gcExtendOrderStatusHomehas.setUpdateTime(orderInfo
						.getYc_update_time());
				mWebResult.inOtherResult(homehasOrderProcess
						.insertOrderStatus(gcExtendOrderStatusHomehas));
			}
		}

		// 生成供货单
		TxPurchaseOrderService purchaseOrderService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxPurchaseOrderService");
		purchaseOrderService.insertOrder(orderInfo, sManageCode, sMemberCode);

		return mWebResult;

	}

	public RsyncResult doProcess(RsyncRequestSyncOrders tRequest,
			RsyncResponseSyncOrders tResponse) {
		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				txReckonOrderService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");

				result.setProcessNum(tResponse.getResult().size());
				
				//LD 分销的订单
				List<RsyncModelOrderInfo> distributorOrders = new ArrayList<RsyncModelOrderInfo>();
				
				for (RsyncModelOrderInfo orderInfo : tResponse.getResult()) {
					
					if("44".equals(orderInfo.getMedi_mclss_id())){// 44 爱奇艺订单
						distributorOrders.add(orderInfo);
					}
					
					
					MWebResult mResult = insertReckonOrder(orderInfo);

					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;

					} else {

						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}

						result.getResultList().add(mResult.getResultMessage());
					}

				}

				// 设置处理信息
				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));
				
				//分销订单的操作
				new OrderForLD().distOrder(distributorOrders);
			}
		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {

			result.setSuccessNum(iSuccessSum);

			result.setStatusData(tRequest.getEnd_date());
		}

		return result;
	}

	public RsyncResponseSyncOrders upResponseObject() {
		// TODO Auto-generated method stub
		return new RsyncResponseSyncOrders();
	}

	/**
	 * 注册用户
	 * 
	 * @param custInfo
	 * @return
	 */
	public MWebResult reginUser(CustInfo custInfo) {

		MWebResult mWebResult = new MWebResult();

		// 定义手机号码
		String sMobilePhone = "";

		// 取出家有的用户手机号
		if (StringUtils.isNotEmpty(custInfo.getHp_teld())) {
			sMobilePhone = custInfo.getHp_teld() + custInfo.getHp_telh()
					+ custInfo.getHp_teln();
		}
		// 判断如果手机号为空 则尝试以电话号码作为手机号码的标记 以兼容家有信息中的错误
		if (StringUtils.isEmpty(sMobilePhone)) {
			if (StringUtils.isNotEmpty(custInfo.getTeld()))
				sMobilePhone = custInfo.getTeld() + custInfo.getTelh()
						+ custInfo.getTeln();

		}

		// 判断手机号非空
		if (mWebResult.upFlagTrue()) {
			if (StringUtils.isEmpty(sMobilePhone)) {
				mWebResult.inErrorMessage(918505101, custInfo.getCust_id());
			}
		}

		// 判断手机号格式是否正确
		if (mWebResult.upFlagTrue()) {
			if (!RegexHelper.checkRegexField(sMobilePhone,
					RegexConst.MOBILE_PHONE)) {
				mWebResult.inErrorMessage(918505104, custInfo.getCust_id(),
						sMobilePhone);
			}
		}

		if (mWebResult.upFlagTrue()) {

			// 判断是否存在会员信息 如果存在会员信息则不处理之
			if (DbUp.upTable("mc_extend_info_homehas").count("homehas_code",
					custInfo.getCust_id()) <= 0) {

				MDataMap mMemberMap = DbUp.upTable("mc_login_info").one(
						"login_name", sMobilePhone, "manage_code",
						MemberConst.MANAGE_CODE_HOMEHAS);

				// 判断是否存在该账户信息 如果不存在则添加 否则更新关联会员编号
				if (mMemberMap == null) {

					MLoginInputHomehas mLoginInput = new MLoginInputHomehas();

					mLoginInput.setHomeHasCode(custInfo.getCust_id());
					mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
					mLoginInput.setLoginName(sMobilePhone);
					// mLoginInput.setLoginPassword("");
					mLoginInput.setManageCode(MemberConst.MANAGE_CODE_HOMEHAS);

					if (StringUtils.isNotEmpty(custInfo.getCust_nm())) {
						mLoginInput.setMemberName(custInfo.getCust_nm());
					}
					
					
					//如果 客户等级为 家有员工 70 则把密码设置成手机的后6位
					if("70".equals(custInfo.getCust_lvl_cd())){
						 mLoginInput.setLoginPassword(sMobilePhone.substring(5));
					}
					
					// 创建会员
					TxMemberForHomeHas txMemberForHomeHas = BeansHelper
							.upBean("bean_com_cmall_membercenter_txservice_TxMemberForHomeHas");
					txMemberForHomeHas.createMemberInfo(mLoginInput);
				} else {
					
					MDataMap mExtendMap = DbUp
							.upTable("mc_extend_info_homehas").one(
									"member_code",
									mMemberMap.get("member_code"));
					
					if(mExtendMap==null||mExtendMap.size()<1){
						//如果mc_extend_info_homehas 不存在会员信息，则新增一条
						MDataMap hinsertMap=new MDataMap();
						hinsertMap.put("member_code", mMemberMap.get("member_code"));
						hinsertMap.put("homehas_code", custInfo.getCust_id());
						hinsertMap.put("member_name", custInfo.getCust_nm());
//						hinsertMap.put("old_code", "");
//						hinsertMap.put("member_sign", "");
						DbUp.upTable("mc_extend_info_homehas").dataInsert(hinsertMap);
					}else{
						
						if (StringUtils.isEmpty(mExtendMap.get("homehas_code")))

						{
							mExtendMap.put("homehas_code", custInfo.getCust_id());
							DbUp.upTable("mc_extend_info_homehas").dataUpdate(
									mExtendMap, "homehas_code", "zid");
						} else {
							//在此修改，支持多个家有homehas_code
							//homehas_code相等，返回已存在的提示
							if(mExtendMap.get("homehas_code").equals(custInfo.getCust_id())){
								mWebResult.inErrorMessage(918505102,
										custInfo.getCust_id());
							}
							else{
								//homehas_code不相等,新增一条
								MDataMap hinsertMap=new MDataMap();
								hinsertMap.put("member_code", mMemberMap.get("member_code"));
								hinsertMap.put("homehas_code", custInfo.getCust_id());
								hinsertMap.put("member_name", custInfo.getCust_nm());
//								hinsertMap.put("old_code", "");
//								hinsertMap.put("member_sign", "");
								DbUp.upTable("mc_extend_info_homehas").dataInsert(hinsertMap);
							}
							
						}
						
					}
				
				}

			}
			
			//此处处理家有汇的扩展信息 规则：LD同步过来的信息的手机号码与我们相匹配
			DbUp.upTable("mc_extend_info_homepool").dataExec("update mc_extend_info_homepool set old_code=:old_code where mobile=:mobile and old_code=''", new MDataMap("mobile",sMobilePhone,"old_code",custInfo.getCust_id()));
		}

		/*
		 * MLoginInputHomehas mLoginInput = new MLoginInputHomehas();
		 * 
		 * return txMemberForHomeHas.createMemberInfo(mLoginInput);
		 */

		return mWebResult;

	}
}
