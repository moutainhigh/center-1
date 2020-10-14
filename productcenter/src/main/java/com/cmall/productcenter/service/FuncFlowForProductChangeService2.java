package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.LogHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: FuncFlowForProductChangeService2 <br>
 * 描述: 商品发布审批流程 <br>
 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
 * 时间: 2016年6月21日 上午10:39:17
 */
public class FuncFlowForProductChangeService2 extends BaseClass implements IFlowFunc {

	@Override
	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		return null;
	}

	/**
	 * 
	 * 方法: afterFlowChange <br>
	 * 描述: TODO
	 * 
	 * @param flowCode
	 *            流程Code
	 * @param outCode
	 *            外部Code
	 * @param fromStatus
	 *            起始状态
	 * @param toStatus
	 *            流转的结束状态
	 * @param mSubMap
	 * @return
	 * @see com.cmall.systemcenter.systemface.IFlowFunc#afterFlowChange(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      com.srnpr.zapcom.basemodel.MDataMap)
	 */
	@Override
	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		// 商品发布审批流程状态码
		// 4497172300160001 质检员待审批
		// 4497172300160002 普通运营待审批
		// 4497172300160003 网站编辑待审批
		// 4497172300160004 跨境运营待审批
		// 4497172300160005 审批通过
		// 4497172300160006 质检员驳回
		// 4497172300160007 普通运营驳回
		// 4497172300160008 网站编辑驳回
		// 4497172300160009 跨境运营审批驳回
		// 4497172300160011 法务待审批
		// 4497172300160012 法务驳回
		// 4497172300160015 招商经理待审批
		// 4497172300160016 招商经理审批通过
		// 4497172300160017 招商经理驳回
		RootResult result = new RootResult();
		MUserInfo uc = UserFactory.INSTANCE.create();// 当前用户所属店铺编号
		// 驳回操作，流程操作人点击驳回后统一退回到草稿箱
		if (toStatus.equals("4497172300160006") || toStatus.equals("4497172300160007")
				|| toStatus.equals("4497172300160008") || toStatus.equals("4497172300160009")
				|| toStatus.equals("4497172300160017")) {
			MDataMap productData = DbUp.upTable("pc_productflow").oneWhere("", "",
					"product_code='" + outCode + "' and flow_status in ('10','20')");
			if (productData != null && !productData.isEmpty()) {

				String flstatus = productData.get("flow_status");
				if (SkuCommon.ProAddInit.equals(flstatus)) {// 新增商品处理
					productData.put("flow_status", SkuCommon.ProAddOrRe);
				} else if (SkuCommon.ProUpaInit.equals(flstatus)) {// 修改商品处理
					productData.put("flow_status", SkuCommon.ProUpaOrRe);
				}
				DbUp.upTable("pc_productflow").dataUpdate(productData, "flow_status", "zid");
				// 查询在草稿箱中是否存在未删除的商品信息
				Map<String, Object> draftbox = DbUp.upTable("pc_product_draftbox").dataSqlOne(
						"select product_code from productcenter.pc_product_draftbox where product_code=:product_code and flag_del=:flag_del",
						new MDataMap("product_code", outCode, "flag_del", "449746250002"));
				if (draftbox != null) {
					// 如果存在将其标记为已删除
					MDataMap draftMap = new MDataMap();
					draftMap.put("product_code", outCode);
					draftMap.put("flag_del", "449746250001");
					DbUp.upTable("pc_product_draftbox").dataUpdate(draftMap, "flag_del", "product_code");
				}
				// 添加商品到草稿箱
				String productJson = productData.get("product_json");
				MDataMap draft = this.getProductDraft(productJson);
				DbUp.upTable("pc_product_draftbox").dataInsert(draft);
				// 结束当前流程
				MDataMap flowMain = new MDataMap();
				flowMain.put("flow_isend", "1");
				flowMain.put("flow_code", flowCode);
				DbUp.upTable("sc_flow_main").dataUpdate(flowMain, "flow_isend", "flow_code");
			}
		} else if (toStatus.equals("4497172300160009")) {
			// 审核完成
			PcProductinfo product = new PcProductinfo();
			MDataMap productData = DbUp.upTable("pc_productflow").oneWhere("", "",
					"product_code='" + outCode + "' and flow_status in ('10','20')");
			if (productData != null && !productData.isEmpty()) {
				String pValue = productData.get("product_json");
				JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
				product = pHelper.StringToObj(pValue, product);
				StringBuffer error = new StringBuffer();
				String flstatus = productData.get("flow_status");
				MDataMap changeStatusMap = new MDataMap();
				changeStatusMap.put("flow_bussinessid", outCode);
				changeStatusMap.put("from_status", "4497153900060001");
				changeStatusMap.put("to_status", "4497153900060002");
				changeStatusMap.put("flow_type", "449715390007");
				MDataMap pone = DbUp.upTable("pc_productinfo").one("product_code", outCode);
				if (SkuCommon.ProAddInit.equals(flstatus)) {// 新增商品处理
					changeStatusMap.put("remark", "新增商品终审通过，待编辑负责人编辑上架");
					productData.put("flow_status", SkuCommon.ProAddOr);
				} else if (SkuCommon.ProUpaInit.equals(flstatus)) {// 修改商品处理
					productData.put("flow_status", SkuCommon.ProUpaOr);
					changeStatusMap.put("remark", "修改商品终审通过，待编辑负责人编辑上架");

				}
				MDataMap user = DbUp.upTable("za_userinfo").one("manage_code", pone.get("small_seller_code"),
						"user_type_did", "467721200003", "flag_enable", "0");
				if (user != null && !user.isEmpty() && ("4497153900060002".equals(pone.get("product_status"))
						|| "4497153900060001".equals(pone.get("product_status")))) {
					changeStatusMap.put("from_status", "4497153900060002");
					changeStatusMap.put("to_status", "4497153900060004");
					changeStatusMap.put("remark", "商户冻结，商品强制下架");
					if (pone != null && !pone.isEmpty()) {
						new FlowBussinessService().ChangeFlow(pone.get("uid"), "449715390007", "4497153900060002",
								"4497153900060004", UserFactory.INSTANCE.create().getUserCode(), "商户冻结，商品强制下架",
								changeStatusMap);
					}
				}
				DbUp.upTable("pc_productflow").dataUpdate(productData, "flow_status", "uid");
				if (error != null && StringUtils.isNotBlank(error.toString())
						&& Integer.valueOf(error.toString()) != 1) {
					// 更新草稿箱状态
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("flow_status", flstatus);
					mDataMap.put("product_code", outCode);
					DbUp.upTable("pc_productflow").dataUpdate(mDataMap, "flow_status", "product_code");
					result.setResultCode(Integer.valueOf(error.toString()));
					result.setResultMessage(error.toString());
				}
			}
		} else if (toStatus.equals("4497172300160012")) {
			LogFactory.getLog(FuncFlowForProductChangeService2.class)
					.info("flowCode:" + flowCode + "执行法务驳回审批结束，开始执行法务驳回到编辑待审批流程");
			/**
			 * 法务审批驳回后直接跳转到编辑待审批节点
			 */
			Map<String, Object> statusChange = DbUp.upTable("sc_flow_statuschange").dataSqlOne(
					"select to_status from systemcenter.sc_flow_statuschange where from_status=:from_status and flow_type='449717230016' order by zid desc",
					new MDataMap("from_status", toStatus));
			String flowFromStatus = toStatus;
			String flowToStatus = statusChange.get("to_status").toString();
			String flowUserCode = uc.getUserCode();
			String flowRoleCode = uc.getUserRole();
			String flowRemark = "法务驳回到编辑待审批";
			FlowService flow = new FlowService();
			String lockCode = flowCode + "_" + flowToStatus;
			result = flow.ChangeFlow(lockCode, flowCode, flowFromStatus, flowToStatus, flowUserCode, flowRoleCode,
					flowRemark, mSubMap);
			LogFactory.getLog(FuncFlowForProductChangeService2.class)
					.info("flowCode:" + flowCode + "流程审批结果:" + JSON.toJSON(result));
			if (result.getResultCode() == 1) {
				/**
				 * 法务驳回跳转到网站编辑，修改商品状态为待上架
				 */
				MDataMap updateProduct = new MDataMap();
				updateProduct.put("product_status", "4497153900060001");
				updateProduct.put("product_code", outCode);
				DbUp.upTable("pc_productinfo").dataUpdate(updateProduct, "product_status", "product_code");
			} else {
				MDataMap flowMain = DbUp.upTable("sc_flow_main").one("flow_code", flowCode);
				LogFactory.getLog(FuncFlowForProductChangeService2.class)
						.error("flowCode=" + flowCode + "的流程信息为:" + flowMain);
				String error = "flowCode:" + flowCode + "的流程审批错误，错误原因：" + JSON.toJSON(result);
				LogFactory.getLog(FuncFlowForProductChangeService2.class).error(error);
			}
		} else if (toStatus.equals("4497172300160005")) {
			/**
			 * 法务审批通过，商品上架
			 */
			MDataMap uidMap = DbUp.upTable("pc_productinfo").oneWhere("uid,small_seller_code,product_code", "", "", "product_code", outCode);
			MDataMap extMap = DbUp.upTable("pc_productinfo_ext").oneWhere("delivery_store_type", "", "", "product_code", outCode);
			if (uidMap != null && StringUtils.isNotBlank(uidMap.get("uid"))) {
				// 商品上架
				String flowBussinessUid = uidMap.get("uid"); // 商品Uid
				String productStatus = "4497153900060002"; // 更改到的状态
				String flowType = "449715390006"; // 流程类型449715390006：商家后台商品状态
				String remark = "编辑新品商品上架";
				
				// 入住家有仓库商品特殊处理
				if("4497471600430002".equals(extMap.get("delivery_store_type"))) {
					// 商品无库存默认先下架
					//productStatus = "4497153900060003"; 
					//remark = "编辑新品审核通过";
					
					// 插入多货主商品表供后续商品的同步任务使用
					if(DbUp.upTable("pc_product_duohz").count("product_code", outCode) == 0) {
						MDataMap huohzMap = new MDataMap();
						huohzMap.put("small_seller_code", uidMap.get("small_seller_code"));
						huohzMap.put("product_code", uidMap.get("product_code"));
						huohzMap.put("update_time", FormatHelper.upDateTime());
						DbUp.upTable("pc_product_duohz").dataInsert(huohzMap);
					}
				}
				
				// 更新状态
				MDataMap updMap = new MDataMap();
				updMap.put("uid", flowBussinessUid);
				updMap.put("flag_sale", "0");
				updMap.put("update_time", DateUtil.getSysDateTimeString());
				updMap.put("product_status", productStatus);
				int retcode = DbUp.upTable("pc_productinfo").dataUpdate(updMap, "flag_sale,update_time,product_status",
						"uid");
				if (1 == retcode) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MDataMap insertDatamap = new MDataMap();
					insertDatamap.put("uid", UUID.randomUUID().toString().replace("-", ""));
					insertDatamap.put("flow_code", flowBussinessUid);
					insertDatamap.put("flow_type", flowType);
					insertDatamap.put("creator", uc.getUserCode());
					insertDatamap.put("create_time", DateUtil.getSysDateTimeString());
					insertDatamap.put("flow_remark", remark);
					insertDatamap.put("current_status", toStatus);
					DbUp.upTable("sc_flow_bussiness_history").dataInsert(insertDatamap);
					PlusHelperNotice.onChangeProductInfo(outCode);
					// 触发消息队列
					ProductJmsSupport pjs = new ProductJmsSupport();
					pjs.onChangeForProductChangeAll(outCode);
				}
				result.setResultMessage(bInfo(941901102));
			}
		}
		/*
		 * 添加操作日志
		 */
		JSONObject operaData = new JSONObject();
		operaData.put("flowCode", flowCode);
		operaData.put("outCode", outCode);
		operaData.put("fromStatus", fromStatus);
		operaData.put("toStatus", toStatus);
		operaData.put("mSubMap", JSONObject.toJSON(mSubMap).toString());
		MDataMap log = new MDataMap();
		log.put("opera_user",
				null != UserFactory.INSTANCE.create() ? UserFactory.INSTANCE.create().getLoginName() : "unKnow user");
		log.put("exec_class", "com.cmall.productcenter.service.FuncFlowForProductChangeService2");
		log.put("opera_data", operaData.toJSONString());
		log.put("opera_time", FormatHelper.upDateTime());
		log.put("opera_result", JSONObject.toJSONString(result));
		DbUp.upTable("lc_operation_log").dataInsert(log);
		LogHelper.addLog("func_do", FuncFlowForProductChangeService2.class);
		return result;
	}

	private MDataMap getProductDraft(String product_json) {
		MDataMap draft = new MDataMap();
		PcProductinfo product = new PcProductinfo();
		JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
		product = pHelper.StringToObj(product_json, product);
		String sysTime = DateUtil.getSysDateTimeString();
		String flowStatus = "449747670001";
		int flag = 0;
		if (flag == 0 && "449747670002".equals(flowStatus)) {
			String productCode = WebHelper.upCode(ProductService.ProductHead);
			product.setProductCode(productCode);
		}
		if (product.getProductSkuInfoList() != null) {
			int size = product.getProductSkuInfoList().size();
			BigDecimal tempMinCostPrice = new BigDecimal(0.00);
			BigDecimal tempMaxCostPrice = new BigDecimal(0.00);

			BigDecimal tempMinSellPrice = new BigDecimal(0.00);
			BigDecimal tempMaxSellPrice = new BigDecimal(0.00);
			for (int i = 0; i < size; i++) {
				ProductSkuInfo pic = product.getProductSkuInfoList().get(i);
				BigDecimal costPrice = (null == pic.getCostPrice() ? BigDecimal.ZERO : pic.getCostPrice());
				if (i == 0) {
					tempMinCostPrice = costPrice;
					tempMaxCostPrice = costPrice;
				} else {
					if (tempMinCostPrice.compareTo(costPrice) == 1)
						tempMinCostPrice = costPrice;
					if (tempMaxCostPrice.compareTo(costPrice) == -1)
						tempMaxCostPrice = costPrice;
				}

				BigDecimal sellPrice = (null == pic.getSellPrice() ? BigDecimal.ZERO : pic.getSellPrice());
				if (i == 0) {
					tempMinSellPrice = sellPrice;
					tempMaxSellPrice = sellPrice;
				} else {
					if (tempMinSellPrice.compareTo(sellPrice) == 1)
						tempMinSellPrice = sellPrice;
					if (tempMaxSellPrice.compareTo(sellPrice) == -1)
						tempMaxSellPrice = sellPrice;
				}
			}
			draft.put("min_cost_price", tempMinCostPrice.toString());
			draft.put("max_cost_price", tempMaxCostPrice.toString());
			draft.put("min_sell_price", tempMinSellPrice.toString());
			draft.put("max_sell_price", tempMaxSellPrice.toString());
		}
		List<String> categoryCodes = new ArrayList<String>();
		// 商品虚类
		if (null != product.getUsprList() && product.getUsprList().size() > 0) {
			for (int i = 0; i < product.getUsprList().size(); i++) {
				categoryCodes.add(product.getUsprList().get(i).getCategoryCode());
			}
		}
		draft.put("product_code", product.getProductCode());
		draft.put("product_name", product.getProductName());
		draft.put("category_code", StringUtils.join(categoryCodes, ","));
		draft.put("product_status", product.getProductStatus());
		draft.put("flow_status", flowStatus);
		draft.put("product_json", product_json);
		MUserInfo userInfo = null;
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
				userInfo = new MUserInfo();
			}

			if (userInfo != null) {
				userCode = userInfo.getUserCode();
			}
		}
		/**
		 * 增加商户编码
		 */
		draft.put("seller_code", product.getSellerCode());
		draft.put("small_seller_code", product.getSmallSellerCode());
		draft.put("create_time", sysTime);
		draft.put("creator", userCode);
		draft.put("update_time", sysTime);
		draft.put("updator", userCode);
		draft.put("flag_del", "449746250002");
		draft.put("uid", UUID.randomUUID().toString().replace("-", ""));
		return draft;
	}
}