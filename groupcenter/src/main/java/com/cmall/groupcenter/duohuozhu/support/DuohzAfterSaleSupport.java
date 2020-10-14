package com.cmall.groupcenter.duohuozhu.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.duohuozhu.model.ApiCode;
import com.srnpr.xmassystem.duohuozhu.model.RequestModel;
import com.srnpr.xmassystem.duohuozhu.model.ResponseModel;
import com.srnpr.xmassystem.duohuozhu.support.RsyncDuohuozhuSupport;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 多货主售后服务相关
 * @remark 
 * @author 任宏斌
 * @date 2019年6月18日
 */
public class DuohzAfterSaleSupport {
	
	/**
	 * 创建多货主售后单初始化定时任务
	 * @param afterSaleCode
	 */
	public void createAfterSaleServiceTask(String afterSaleCode) {
		JobExecHelper.createExecInfo("449746990019", afterSaleCode, "");
	}
	
	/**
	 * 创建多货主售后单定时任务
	 * @param afterSaleCode
	 */
	public void createApplyAfterSaleTask(String afterSaleCode) {
		JobExecHelper.createExecInfo("449746990020", afterSaleCode, "");
	}
	
	/**
	 * 初始化多货主售后单
	 * @param afterSaleCode
	 */
	public RootResult initDuohzAfterSale(String afterSaleCode) {
		RootResult rootResult = new RootResult();
		if(DbUp.upTable("oc_order_duohz_after").count("asale_code", afterSaleCode) > 0) {
			rootResult.setResultMessage("数据已经存在");
			return rootResult;
		}
		
		MDataMap orderAfterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		List<MDataMap> afterSaleDetails = DbUp.upTable("oc_order_after_sale_dtail").queryByWhere("asale_code", afterSaleCode);
		
		//多货主售后主表
		MDataMap duohzAfterSale = new MDataMap();
		duohzAfterSale.put("asale_code", afterSaleCode);
		duohzAfterSale.put("order_code", orderAfterSale.get("order_code"));
		duohzAfterSale.put("cod_status", "0");
		duohzAfterSale.put("create_time", FormatHelper.upDateTime());
		duohzAfterSale.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("oc_order_duohz_after").dataInsert(duohzAfterSale);
		
		//多货主售后明细表
		for (MDataMap afterSaleDetail : afterSaleDetails) {
			String sku_code = afterSaleDetail.get("sku_code");
			String sku_num = afterSaleDetail.get("sku_num");
			
			String sSql = "select d.seq from oc_order_duohz_after_detail d,oc_order_after_sale s where d.asale_code=s.asale_code and s.order_code=:order_code"
					+ " and s.asale_status not in ('4497477800050004','4497477800050007','4497477800050009','4497477800050011') and d.sku_code=:sku_code";
			List<Map<String, Object>> alreadySeqList = DbUp.upTable("oc_order_after_sale_dtail").dataSqlList(sSql, new MDataMap("order_code",orderAfterSale.get("order_code"),"sku_code",sku_code));
			
			String alreadySeq = ",";
			for (Map<String, Object> map : alreadySeqList) {
				alreadySeq += map.get("seq") + ",";
			}
			
			int readyCount = 0;
			List<MDataMap> orderSeqList = DbUp.upTable("oc_order_duohz_detail").queryAll("seq", "seq", "", new MDataMap("order_code",orderAfterSale.get("order_code"),"sku_code",sku_code));
			for (MDataMap seq : orderSeqList) {
				if(!alreadySeq.contains(","+seq.get("seq")+",")) {
					//多货主售后明细表
					MDataMap duohzAfterSaleDetail = new MDataMap();
					duohzAfterSaleDetail.put("asale_code", afterSaleCode);
					duohzAfterSaleDetail.put("sku_code", sku_code);
					duohzAfterSaleDetail.put("seq", seq.get("seq"));
					DbUp.upTable("oc_order_duohz_after_detail").dataInsert(duohzAfterSaleDetail);
					
					readyCount++;
					if(readyCount == Integer.parseInt(sku_num)) {
						break;
					}
				}
			}
		}
		return rootResult;
	}
	
	/**
	 * 多货主服务单创建
	 * @param afterSaleCode
	 * @return
	 */
	public RootResult execApplyAfterSale(String afterSaleCode) {
		MWebResult mWebResult = new MWebResult();
		
		MDataMap orderAfterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		if(orderAfterSale == null) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("售后单数据不存在");
			return mWebResult;
		}
		
		// 不同步已经取消的售后单
		String[] excludeStatus = {"4497477800050004","4497477800050011"};
		if(ArrayUtils.contains(excludeStatus, orderAfterSale.get("asale_status"))) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("售后单已经取消");
			return mWebResult;
		}
		
		RootResult initResult = initDuohzAfterSale(afterSaleCode);
		MDataMap duohzAfterSale = DbUp.upTable("oc_order_duohz_after").one("asale_code", afterSaleCode);
		if(duohzAfterSale == null) {
			if(initResult.getResultCode() != 1){
				return initResult;
			}
			
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("多货主售后单数据初始化失败");
			return mWebResult;
		}
		
		MDataMap afterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		
		//退货
		if("4497477800030001".equals(afterSale.get("asale_type")) || "4497477800030002".equals(afterSale.get("asale_type")) ) {
			RootResult rootResult = execApplyReturnGoods(afterSaleCode, orderAfterSale.get("order_code"));
			mWebResult.inOtherResult(rootResult);
		}
		
		//换货
		if("4497477800030003".equals(afterSale.get("asale_type"))) {
			RootResult rootResult = execApplyChangeGoods(afterSaleCode, orderAfterSale.get("order_code"));
			mWebResult.inOtherResult(rootResult);
		}
		
		//更新售后表状态
		if(mWebResult.upFlagTrue()) {
			DbUp.upTable("oc_order_duohz_after").dataUpdate(new MDataMap("asale_code", afterSaleCode, "cod_status", "R00"), "", "asale_code");
		}
		
		return mWebResult;
	}

	/**
	 * 创建换货单
	 * @param afterSaleCode 售后单号
	 * @param orderCode 订单编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private RootResult execApplyChangeGoods(String afterSaleCode, String orderCode) {
		RootResult rootResult = new RootResult();
		
		List<Map<String, Object>> afterSaleDetails = DbUp.upTable("oc_order_duohz_after_detail").listByWhere("asale_code", afterSaleCode);
		
		RequestModel requestModel = new RequestModel();
		requestModel.getHead().setFunction_id(ApiCode.CP000006.toString());
		List<LinkedHashMap<String, Object>> changeorderList = new ArrayList<LinkedHashMap<String, Object>>();
		LinkedHashMap<String, Object> changeorderMap = new LinkedHashMap<String, Object>();
		changeorderMap.put("cp_ord_id", orderCode);//第三方订单编号
		changeorderMap.put("cp_rtn_id", afterSaleCode);//第三方退货编号
		changeorderMap.put("cp_new_ord_id", afterSaleCode);//第三方新单编号 暂时设置为售后单号
		changeorderMap.put("back_type", "02");//退回方式 写死02自行邮寄
		changeorderMap.put("desc", "");//取件备注
		List<LinkedHashMap<String, Object>> changeorder = new ArrayList<LinkedHashMap<String, Object>>();
		for (Map<String, Object> afterSaleDetail : afterSaleDetails) {
			LinkedHashMap<String, Object> detailMap = new LinkedHashMap<String, Object>();
			detailMap.put("cp_ord_seq", afterSaleDetail.get("seq"));
			changeorder.add(detailMap);
		}
		changeorderMap.put("detail", changeorder);
		changeorderList.add(changeorderMap);
		
		LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("changeorder", changeorderList);
		requestModel.setBody(bodyMap);
		
		RsyncDuohuozhuSupport rsyncDuohuozhuSupport = new RsyncDuohuozhuSupport();
		ResponseModel responseModel = rsyncDuohuozhuSupport.callGateway(requestModel);
		if(null != responseModel && "00".equals(responseModel.getHeader().getResp_code())) {
			Map<String, Object> body = responseModel.getBody();
			List<Map<String, Object>> changeorders = (List<Map<String, Object>>) body.get("changeorder");
			Map<String, Object> resChangeorder = changeorders.get(0);
			String err_code = resChangeorder.get("err_code")+"";
			String err_msg = resChangeorder.get("err_msg")+"";
			if("00".equals(err_code)) {
				String rtn_id = resChangeorder.get("rtn_id")+"";
				String new_ord_id = resChangeorder.get("new_ord_id")+"";//换货的新单订单编号
				DbUp.upTable("oc_order_duohz_after").dataUpdate(new MDataMap("dhz_asale_code", rtn_id, "asale_code", afterSaleCode, "new_ord_id",new_ord_id), "", "asale_code");
			}else {
				rootResult.setResultCode(88);
				rootResult.setResultMessage(err_msg);
				return rootResult;
			}
		}else {
			rootResult.setResultCode(99);
			rootResult.setResultMessage("换货售后单创建失败！");
			return rootResult;
		}
		return rootResult;
		
	}

	/**
	 * 创建退货单
	 * @param orderAfterSale
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private RootResult execApplyReturnGoods(String afterSaleCode, String order_code) {
		RootResult rootResult = new RootResult();
		List<Map<String, Object>> afterSaleDetails = DbUp.upTable("oc_order_duohz_after_detail").listByWhere("asale_code", afterSaleCode);
		
		RequestModel requestModel = new RequestModel();
		requestModel.getHead().setFunction_id(ApiCode.CP000004.toString());
		LinkedHashMap<String, Object> rtnorderMap = new LinkedHashMap<String, Object>();
		rtnorderMap.put("cp_rtn_id", afterSaleCode);//第三方退货编号
		rtnorderMap.put("cp_ord_id", order_code);//第三方订单编号
		rtnorderMap.put("back_type", "02");//退回方式 写死02自行邮寄
		rtnorderMap.put("desc", "");//取件备注
		List<LinkedHashMap<String, Object>> detail = new ArrayList<LinkedHashMap<String, Object>>();
		for (Map<String, Object> afterSaleDetail : afterSaleDetails) {
			LinkedHashMap<String, Object> detailMap = new LinkedHashMap<String, Object>();
			detailMap.put("cp_ord_seq", afterSaleDetail.get("seq"));
			detail.add(detailMap);
		}
		rtnorderMap.put("detail", detail);
		
		LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("rtnorder", Arrays.asList(rtnorderMap));
		requestModel.setBody(bodyMap);
		
		RsyncDuohuozhuSupport rsyncDuohuozhuSupport = new RsyncDuohuozhuSupport();
		ResponseModel responseModel = rsyncDuohuozhuSupport.callGateway(requestModel);
		if(null != responseModel && "00".equals(responseModel.getHeader().getResp_code())) {
			Map<String, Object> body = responseModel.getBody();
			List<Map<String, Object>> rtnorders = (List<Map<String, Object>>) body.get("rtnorder");
			Map<String, Object> rtnorder = rtnorders.get(0);
			String err_code = rtnorder.get("err_code")+"";
			String err_msg = rtnorder.get("err_msg")+"";
			if("00".equals(err_code)) {
				String rtn_id = rtnorder.get("rtn_id")+"";
				DbUp.upTable("oc_order_duohz_after").dataUpdate(new MDataMap("dhz_asale_code", rtn_id, "asale_code", afterSaleCode), "", "asale_code");
			}else {
				rootResult.setResultCode(88);
				rootResult.setResultMessage(err_msg);
				return rootResult;
			}
		}else {
			rootResult.setResultCode(99);
			rootResult.setResultMessage("退后售后单创建失败！");
			return rootResult;
		}
		return rootResult;
	}

	/**
	 * 更改订单状态
	 * @param orderCode
	 * @param status
	 * @return 
	 */
	public RootResult changeOrderStatus(String orderCode, String status) {
		RootResult result = new RootResult();
		
		//不是DD开头的订单不处理
		if(!orderCode.startsWith("DD")) {
			return result;
		}
		
		//先校验一下流程顺序是否倒置
		MDataMap duohzOrder = DbUp.upTable("oc_order_duohz").one("order_code", orderCode);
		String oldStatus = duohzOrder.get("cod_status");
		if(StringUtils.isNotEmpty(oldStatus) && !"0".equals(oldStatus) && !"-1".equals(oldStatus) && !"P00".equals(oldStatus)) {
			if(("P01".equals(oldStatus) && !"P02".equals(status)) 
					|| ("P02".equals(oldStatus) && !"P03".equals(status) && !"P04".equals(status))
					|| ("P04".equals(oldStatus) && !"P03".equals(status) && !"P05".equals(status))) {
				result.setResultCode(11);
				result.setResultMessage("流程顺序异常");
				return result;
			}
		}else if(!"P01".equals(status)) {
			result.setResultCode(11);
			result.setResultMessage("流程顺序异常");
			return result;
		}
		
		String toStatus = "";
		if("P02".equals(status)) {
			toStatus = "4497153900010003";
		} else if("P03".equals(status)) {
			toStatus = "4497153900010005";
		} else if("P05".equals(status)) {
			toStatus = "4497153900010006";
		}
		
		if(StringUtils.isNotEmpty(toStatus)) {
			
			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", orderCode);
			String fromStatus = dm.get("order_status");
			if(!fromStatus.equals(toStatus)) {
				FlowBussinessService fs = new FlowBussinessService();
				String flowBussinessUid = dm.get("uid");
				String flowType = "449715390008";
				String userCode = "system";
				String remark = "change by duohz";
				MDataMap md = new MDataMap();
				md.put("order_code", orderCode);
				
				result = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, md);
			}
		}
		
		//更新多货主订单表
		if(1==result.getResultCode()) {
			DbUp.upTable("oc_order_duohz").dataUpdate(new MDataMap("order_code", orderCode, "cod_status", status, "update_time", DateUtil.getSysDateTimeString()), "", "order_code");
			
			//拒收生成退款单
			if("4497153900010006".equals(toStatus)) {
				CreateMoneyService createMoneyService = new CreateMoneyService();
				createMoneyService.creatReturnMoney(orderCode,"duohz","多货主商品订单拒收");
			}
		}
		
		return result;
	}

	/**
	 * 创建发货单
	 * @param orderCode 订单编号
	 * @param dlver_nm 物流公司名称
	 * @param invc_id 运单号
	 * @return
	 */
	public RootResult createShipment(String orderCode, String dlver_nm, String invc_id) {
		int count = DbUp.upTable("oc_order_shipments").count("order_code", orderCode);
		if(0 == count) {
			DbUp.upTable("oc_order_shipments").insert("order_code", orderCode, "logisticse_name", dlver_nm, "waybill",
					invc_id, "creator", "duohz", "create_time", DateUtil.getSysDateTimeString(),"is_send100_flag", "1");
		}
		return new RootResult();
	}

	/**
	 * 更改售后单状态
	 * @param afterSaleCode
	 * @param status
	 * @return
	 */
	public RootResult changeAfterSaleStatus(String afterSaleCode, String status) {
		
		RootResult rootResult = new RootResult();
		
		if("R04".equals(status)) {
			
			String flowBussinessUid = "";
			String fromStatus = "";
			String toStatus = "";
			String flowType = "";
			MDataMap afterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
			if("4497477800030001".equals(afterSale.get("asale_type"))) { // 退货退款
				MDataMap info = DbUp.upTable("oc_return_goods").one("return_code", afterSaleCode);
				// 如果退货单状态不一致则更新
				if(!"4497153900050001".equals(info.get("status"))){
					flowBussinessUid = info.get("uid");
					fromStatus = info.get("status");
					toStatus = "4497153900050001";
					flowType = "449715390005";
				}
			} else if("4497477800030003".equals(afterSale.get("asale_type"))) { // 换货
				MDataMap info = DbUp.upTable("oc_exchange_goods").one("exchange_no", afterSaleCode);
				// 如果换货单状态不一致则更新
				if(!"4497153900020004".equals(info.get("status"))){
					flowBussinessUid = info.get("uid");
					fromStatus = info.get("status");
					toStatus = "4497153900020004";
					flowType = "449715390002";
				}
			}
			rootResult = new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, "system", "change by duohz", new MDataMap());
		}
		
		if(1==rootResult.getResultCode()) {
			//跟新多货主售后表
			DbUp.upTable("oc_order_duohz_after").dataUpdate(new MDataMap("asale_code", afterSaleCode, "cod_status", status), "", "asale_code");
		}
		return rootResult;
	}

	/**
	 * 根据订单号获取多货主售后地址
	 * @param orderCode
	 * @return
	 */
	public MDataMap getDuohzAfterSaleAddr(String orderCode) {
		MDataMap result = null;
		MDataMap duohzOrder = DbUp.upTable("oc_order_duohz").one("order_code", orderCode);
		String site_no = duohzOrder.get("site_no");
		String uid = "";
		switch (site_no) {
			case "C01":
				uid = TopUp.upConfig("groupcenter.duohz_c01_uid");
				break;
			case "C02":
				uid = TopUp.upConfig("groupcenter.duohz_c02_uid");
				break;
			case "C04":
				uid = TopUp.upConfig("groupcenter.duohz_c04_uid");
				break;
			case "C10":
				uid = TopUp.upConfig("groupcenter.duohz_c10_uid");
				break;
			case "C28":
				uid = TopUp.upConfig("groupcenter.duohz_c28_uid");
				break;
			default:
				break;
		}
		
		if(StringUtils.isNotBlank(uid)) {
			result = DbUp.upTable("oc_address_info").one("uid",uid);
		}
		
		return result;
	}
	
	/**
	 * 根据售后单号判断是否为多货主订单
	 * @param asaleCode
	 * @return
	 */
	public boolean checkDuohzStore(String asaleCode) {
		return DbUp.upTable("oc_order_duohz_after").count("asale_code", asaleCode) > 0;
	}
}
