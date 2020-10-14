package com.cmall.groupcenter.jd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.flow.FlowForSureGetGoods;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.Constants;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.webwx.WxGateSupport;

/**
 * 京东售后服务相关
 */
public class JdAfterSaleSupport extends BaseClass {
	
	public static final Map<String,Date> noticeTimeMap = new ConcurrentHashMap<String, Date>();
	WxGateSupport support = new WxGateSupport();
	
	/**
	 * 创建京东售后单初始化定时任务
	 * @param afterSaleCode
	 */
	public void createAfterSaleServiceTask(String afterSaleCode) {
		JobExecHelper.createExecInfo(Constants.ZA_EXEC_TYPE_JD_AFTER_SALE_CREATE, afterSaleCode, "");
	}
	
	/**
	 * 初始化京东售后单
	 * @param afterSaleCode
	 */
	public RootResult initJdAfterSale(String afterSaleCode) {
		RootResult rootResult = new RootResult();
		if(DbUp.upTable("oc_order_jd_after_sale").count("asale_code", afterSaleCode) > 0) {
			rootResult.setResultMessage("数据已经存在");
			return rootResult;
		}
		
		MDataMap orderAfterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		MDataMap afterSaleDetail = DbUp.upTable("oc_order_after_sale_dtail").one("asale_code", afterSaleCode);
		MDataMap jdOrder = DbUp.upTable("oc_order_jd").one("order_code",orderAfterSale.get("order_code"),"sku_code", afterSaleDetail.get("sku_code"));
		List<Object> pickwareType = getAfsPickwareType(jdOrder.get("jd_order_id"), jdOrder.get("sku_id")).getResultList();
		
		String customerExpect = "";
		if("4497477800030001".equals(orderAfterSale.get("asale_type"))
				|| "4497477800030002".equals(orderAfterSale.get("asale_type"))) {
			customerExpect = "10"; // 退货
		} else if("4497477800030003".equals(orderAfterSale.get("asale_type"))) {
			customerExpect = "20"; // 换货
		} else {
			// "不支持的售后类型"
			rootResult.setResultCode(0);
			rootResult.setResultMessage("不支持的售后类型");
			return rootResult;
		}
		
		String pickType = "";
		if(!pickwareType.isEmpty()) {
			pickType = pickwareType.contains("4") ? "4" : "40";
		}
		
		MDataMap jdAfterSale = new MDataMap();
		jdAfterSale.put("asale_code", afterSaleCode);
		jdAfterSale.put("order_code", orderAfterSale.get("order_code"));
		jdAfterSale.put("jd_order_id", jdOrder.get("jd_order_id"));
		jdAfterSale.put("pickware_type", pickType);
		jdAfterSale.put("afs_type", customerExpect);
		jdAfterSale.put("rsync_flag", "0"); // 默认未同步状态
		jdAfterSale.put("create_time", FormatHelper.upDateTime());
		jdAfterSale.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("oc_order_jd_after_sale").dataInsert(jdAfterSale);
		
		return rootResult;
	}
	
	/**
	 * 创建京东退款单任务
	 * @param orderCode   惠家有订单号
	 * @param afterSaleCode 惠家有售后单号
	 */
	public RootResult createOrderRefundTask(String orderCode,String afterSaleCode) {
		RootResult rootResult = new RootResult();
		MDataMap jdOrder = DbUp.upTable("oc_order_jd").one("order_code", orderCode);
		MDataMap jdAfterSale = null;
		
		if(StringUtils.isNotBlank(afterSaleCode)) {
			jdAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", afterSaleCode);
			if(jdAfterSale == null) {
				rootResult.setResultCode(0);
				rootResult.setResultMessage("京东售后数据不存在: "+afterSaleCode);
				return rootResult;
			}
			
			if(StringUtils.isBlank(jdAfterSale.get("afs_service_id"))) {
				rootResult.setResultCode(0);
				rootResult.setResultMessage("京东售后单号为空: "+afterSaleCode);
				return rootResult;
			}
		}
		
		// 检查是否重复
		if(DbUp.upTable("oc_order_jd_refund").count("order_code",orderCode,"after_sale_code",StringUtils.trimToEmpty(afterSaleCode)) > 0) {
			rootResult.setResultCode(1);
			rootResult.setResultMessage("已经存在");
			return rootResult;
		}
		
		MDataMap dataMap = new MDataMap();
		dataMap.put("order_code", orderCode);
		dataMap.put("jd_order_id", jdOrder.get("jd_order_id"));
		dataMap.put("after_sale_code", StringUtils.trimToEmpty(afterSaleCode));
		dataMap.put("afs_service_id", jdAfterSale == null ? "" : jdAfterSale.get("afs_service_id"));
		dataMap.put("create_time", FormatHelper.upDateTime());
		dataMap.put("update_time", dataMap.get("create_time"));
		DbUp.upTable("oc_order_jd_refund").dataInsert(dataMap);
		
		return rootResult;
	}
	
	/**
	 * 京东服务单创建申请
	 * @param afterSaleCode
	 */
	public RootResult execAfsApplyCreate(String afterSaleCode) {
		RootResult rootResult = new RootResult();
		
		MDataMap orderAfterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		if(orderAfterSale == null) {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("售后单数据不存在");
			return rootResult;
		}
		
		// 不同步已经取消的售后单
		String[] excludeStatus = {"4497477800050004","4497477800050011"};
		if(ArrayUtils.contains(excludeStatus, orderAfterSale.get("asale_status"))) {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("售后单已经取消");
			return rootResult;
		}
		
		// 下单手机号
		//String loginName = (String)DbUp.upTable("mc_login_info").dataGet("login_name", "", new MDataMap("member_code", orderAfterSale.get("buyer_code")));
		
		RootResult initResult = initJdAfterSale(afterSaleCode);
		MDataMap jdAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", afterSaleCode);
		if(jdAfterSale == null) {
			if(initResult.getResultCode() != 1){
				return initResult;
			}
			
			rootResult.setResultCode(0);
			rootResult.setResultMessage("京东售后单数据初始化失败");
			return rootResult;
		}
		
		MDataMap afterSaleDetail = DbUp.upTable("oc_order_after_sale_dtail").one("asale_code", afterSaleCode);
		MDataMap jdOrder = DbUp.upTable("oc_order_jd").one("order_code",orderAfterSale.get("order_code"),"sku_code", afterSaleDetail.get("sku_code"));
		MDataMap orderAddress = DbUp.upTable("oc_orderadress").one("order_code", orderAfterSale.get("order_code"));
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("customerExpect", jdAfterSale.get("afs_type")); // 预期售后类型
		
		// 支持的售后类型
		MWebResult afsServiceTypeResult = getAfsServiceType(jdOrder.get("jd_order_id"), jdOrder.get("sku_id"));
		if(!afsServiceTypeResult.upFlagTrue()) {
			return afsServiceTypeResult;
		}
		if(!afsServiceTypeResult.getResultList().contains(jdAfterSale.get("afs_type"))) {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("订单不支持当前的售后类型");
			return rootResult;
		}
		
		// 支持的退回方式
		MWebResult pickwareTypeResult = getAfsPickwareType(jdOrder.get("jd_order_id"), jdOrder.get("sku_id"));
		if(!pickwareTypeResult.upFlagTrue()) {
			return pickwareTypeResult;
		}
		if(pickwareTypeResult.getResultList().isEmpty()) {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("商品未查到退回方式");
			return rootResult;
		}
		
		if(StringUtils.isBlank(jdAfterSale.get("pickware_type"))) {
			jdAfterSale.put("pickware_type", pickwareTypeResult.getResultList().contains("4") ? "4" : "40");
		}
		
		// 问题描述图片
		List<String> questionPicList = new ArrayList<String>();
		MDataMap remark = DbUp.upTable("oc_order_remark").one("order_code", orderAfterSale.get("order_code"), "asale_code", afterSaleCode);
		if(remark != null) {
			if(StringUtils.isNotBlank(remark.get("remark_picurl1"))) {
				questionPicList.add(remark.get("remark_picurl1"));
			}
			if(StringUtils.isNotBlank(remark.get("remark_picurl2"))) {
				questionPicList.add(remark.get("remark_picurl2"));
			}
			if(StringUtils.isNotBlank(remark.get("remark_picurl3"))) {
				questionPicList.add(remark.get("remark_picurl3"));
			}
			if(StringUtils.isNotBlank(remark.get("remark_picurl4"))) {
				questionPicList.add(remark.get("remark_picurl4"));
			}
			if(StringUtils.isNotBlank(remark.get("remark_picurl5"))) {
				questionPicList.add(remark.get("remark_picurl5"));
			}
		}
		
		String reason = "";
		MDataMap reasonMap = DbUp.upTable("oc_return_goods_reason").one("return_reson_code", orderAfterSale.get("asale_reason"));
		if(reasonMap != null) {
			reason = reasonMap.get("return_reson");
		}
		
		param.put("jdOrderId", jdOrder.get("jd_order_id"));
		param.put("questionDesc", reason + " " + orderAfterSale.get("asale_remark") + " ["+afterSaleCode+bConfig("groupcenter.jd_after_sale_suffix")+"]"); // 产品问题描述，结尾追加售后单号便于同步状态时判断哪个售后单号
		param.put("isNeedDetectionReport", false);
		param.put("questionPic", StringUtils.join(questionPicList, ",")); // 问题描述图片，最多2000字符，支持多张图片，用英文逗号分隔
		//param.put("isHasPackage", true);
		//param.put("packageDesc", 0);
		
		// 客户实体
		Map<String,Object> asCustomerDto = new HashMap<String, Object>();
		asCustomerDto.put("customerContactName", orderAddress.get("receive_person"));
		asCustomerDto.put("customerTel", orderAddress.get("mobilephone"));
		asCustomerDto.put("customerMobilePhone", orderAddress.get("mobilephone"));
		asCustomerDto.put("customerEmail", "");
		asCustomerDto.put("customerPostcode", orderAddress.get("postcode"));
		param.put("asCustomerDto", asCustomerDto);
		
		String pickwareType = jdAfterSale.get("pickware_type");
		
		// 取件实体
		// 4 上门取件、7 客户送货、40客户发货
		//PlusSupportJdAddress.JdAddress jdAddress = new PlusSupportJdAddress().getJdAddress("");
		Map<String,Object> asPickwareDto = new HashMap<String, Object>();
		asPickwareDto.put("pickwareType", pickwareType); // 不支持上门取件的都默认40（客户发货）
		asPickwareDto.put("pickwareProvince", NumberUtils.toInt(jdOrder.get("province")));
		asPickwareDto.put("pickwareCity", NumberUtils.toInt(jdOrder.get("city")));
		asPickwareDto.put("pickwareCounty", NumberUtils.toInt(jdOrder.get("county")));
		asPickwareDto.put("pickwareVillage", NumberUtils.toInt(jdOrder.get("town")));
		asPickwareDto.put("pickwareAddress", orderAddress.get("address"));
		param.put("asPickwareDto", asPickwareDto);
		
		// 返件信息实体(同取件实体)
		// 自营配送(10),第三方配送(20)
		Map<String,Object> asReturnwareDto = new HashMap<String, Object>();
		asReturnwareDto.put("returnwareType", "4".equals(pickwareType) ? "10" : "20"); // 不支持上门取件的都默认20（第三方配）
		asReturnwareDto.put("returnwareProvince", NumberUtils.toInt(jdOrder.get("province")));
		asReturnwareDto.put("returnwareCity", NumberUtils.toInt(jdOrder.get("city")));
		asReturnwareDto.put("returnwareCounty", NumberUtils.toInt(jdOrder.get("county")));
		asReturnwareDto.put("returnwareVillage", NumberUtils.toInt(jdOrder.get("town")));
		asReturnwareDto.put("returnwareAddress", orderAddress.get("address"));
		param.put("asReturnwareDto", asReturnwareDto);
		
		// 申请单明细
		Map<String,Object> asDetailDto = new HashMap<String, Object>();
		asDetailDto.put("skuId", jdOrder.get("sku_id")); 
		asDetailDto.put("skuNum", afterSaleDetail.get("sku_num"));
		param.put("asDetailDto", asDetailDto);
		
		if(StringUtils.isNotBlank(jdAfterSale.get("afs_service_id")) || !"0".equals(jdAfterSale.get("rsync_flag"))) {
			rootResult.setResultCode(1);
			rootResult.setResultMessage("服务单已经创建");
			return rootResult;
		}
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.afsApply.create", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				jdAfterSale.put("rsync_message", "[服务单创建失败] " + resultObj.getString("errorResponse"));
				DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "pickware_type,rsync_flag,rsync_message", "asale_code");
				
				rootResult.setResultCode(99);
				rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return rootResult;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_afsApply_create_response");
			
			if(response.getBooleanValue("success")) {
				jdAfterSale.put("rsync_flag", "1");
				jdAfterSale.put("rsync_message", "");
				DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "pickware_type,rsync_flag,rsync_message", "asale_code");
			} else {
				jdAfterSale.put("rsync_message", "[服务单创建失败][" + response.getString("resultCode") + "] " + response.getString("resultMessage"));
				DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "pickware_type,rsync_flag,rsync_message", "asale_code");
				
				rootResult.setResultCode(0);
				rootResult.setResultMessage(jdAfterSale.get("rsync_message"));
			}
		}else {
			jdAfterSale.put("rsync_message", "京东接口调用失败");
			DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "pickware_type,rsync_message", "asale_code");
			
			rootResult.setResultCode(0);
			rootResult.setResultMessage("接口调用失败");
		}
		
		return rootResult;
	}
	
	/**
	 * 提交客户发运信息到京东
	 * @return
	 */
	public RootResult execSendSkuUpdate(String afterSaleCode) {
		RootResult rootResult = new RootResult();
		MDataMap jdAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", afterSaleCode);
		
		// 检查是否允许填写发货信息
		if(!(","+jdAfterSale.get("allow_operations")+",").contains(",2,")){
			rootResult.setResultCode(0);
			rootResult.setResultMessage("此售后服务单不允许填写物流信息");
			return rootResult;
		}
		
		MDataMap shipment = DbUp.upTable("oc_order_shipments").one("order_code", afterSaleCode);
		if(shipment == null) {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("未查询到物流信息");
			return rootResult;
		}
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("afsServiceId", jdAfterSale.get("afs_service_id"));
		param.put("freightMoney", shipment.get("freight_money"));
		param.put("expressCompany", shipment.get("logisticse_name"));
		param.put("deliverDate", shipment.get("create_time"));
		param.put("expressCode", shipment.get("waybill"));
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.sendSku.update", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return rootResult;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_sendSku_update_response");
			
			if(response.getBooleanValue("success")) {
				jdAfterSale.put("rsync_flag", "2");
				jdAfterSale.put("rsync_message", "");
				DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "rsync_flag,rsync_message", "asale_code");
			} else {
				jdAfterSale.put("rsync_message", "[提交客户发运信息失败][" + response.getString("resultCode") + "] " + response.getString("resultMessage"));
				DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "rsync_flag,rsync_message", "asale_code");
				
				rootResult.setResultCode(0);
				rootResult.setResultMessage(jdAfterSale.get("rsync_message"));
			}
		}else {
			jdAfterSale.put("rsync_message", "京东接口调用失败");
			DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, "rsync_message", "asale_code");
			
			rootResult.setResultCode(0);
			rootResult.setResultMessage("接口调用失败");
		}
		
		return rootResult;
	}
	
	/**
	 * 取消京东售后服务单
	 * @param afterSaleCode 惠家有的售后单号
	 * @return
	 */
	public RootResult execAuditCancelQuery(String afterSaleCode, String approveNotes) {
		RootResult rootResult = new RootResult();
		MDataMap jdAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", afterSaleCode);
		
		// 已经取消和审核不通过的不再调用取消操作
		if("60".equals(jdAfterSale.get("afs_service_step"))
				|| "20".equals(jdAfterSale.get("afs_service_step"))) {
			return rootResult;
		}
		
		if(StringUtils.isBlank(approveNotes)) {
			approveNotes = "用户取消";
		}
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("serviceIdList", Arrays.asList(jdAfterSale.get("afs_service_id")));
		param.put("approveNotes", approveNotes);
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.auditCancel.query", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return rootResult;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_auditCancel_query_response");
			
			if(!response.getBooleanValue("success")) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("取消售后服务单失败:["+response.getIntValue("resultCode")+"]"+response.getString("resultMessage"));
			}
		}else {
			rootResult.setResultCode(0);
			rootResult.setResultMessage("取消售后服务单失败");
		}
		
		return rootResult;
	}
	
	/**
	 * 查询服务单明细信息，同步售后单状态
	 * @return
	 */
	public RootResult execServiceDetailInfoQuery(String afterSaleCode) {
		RootResult rootResult = new RootResult();
		MDataMap jdAfterSale = DbUp.upTable("oc_order_jd_after_sale").one("asale_code", afterSaleCode);
		MDataMap afterSale = DbUp.upTable("oc_order_after_sale").one("asale_code", afterSaleCode);
		
		List<String> afsServiceIdList = new ArrayList<String>();
		if(StringUtils.isNotBlank(jdAfterSale.get("afs_service_id"))) {
			afsServiceIdList.add(jdAfterSale.get("afs_service_id"));
		} else {
			// 根据订单号查询售后单，购买多件商品单件退货的时候可能会有多条
			afsServiceIdList = getAfsServiceIdList(jdAfterSale.get("jd_order_id"));
		}
		
		String afsServiceId = null;
		String afsServiceStep = jdAfterSale.get("afs_service_step");
		String theDay = FormatHelper.upDateTime("yyyy-MM-dd");
		for(String serviceId : afsServiceIdList) {
			Map<String,Object> param = new HashMap<String, Object>();
			param.put("afsServiceId", serviceId);
			/**
			 * 1、代表增加获取售后地址信息，即客户发运时填写的地址
			 * 2、代表增加获取客户发货信息
			 * 4、增加获取服务单处理跟踪信息
			 * 5、获取允许的操作信息
			 */
			param.put("appendInfoSteps", Arrays.asList(1,4,5)); 
			
			Map<String, Object> paramJson = new HashMap<String, Object>();
			paramJson.put("param", param);
			String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.serviceDetailInfo.query", paramJson);
			
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				if(resultObj.containsKey("errorResponse")) {
					rootResult.setResultCode(99);
					rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
					return rootResult;
				}
				
				JSONObject response = resultObj.getJSONObject("biz_afterSale_serviceDetailInfo_query_response");
				
				if(response.getBooleanValue("success")) {
					JSONObject result = response.getJSONObject("result");
					
					// 售后单上面未绑定京东服务单时
					if(StringUtils.isBlank(jdAfterSale.get("afs_service_id"))) {
						// 根据创建售后单时的追加的售后编号判断是否跟当前要更新的售后单是同一单
						if(!result.getString("questionDesc").contains("["+afterSaleCode+bConfig("groupcenter.jd_after_sale_suffix")+"]")) {
							continue;
						}
					}
					
					// 售后单出现重复提交的情况
					if(StringUtils.isNotBlank(afsServiceId)) {
						String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
						// 粗率限制一下发送频率
						if(!noticeTimeMap.containsKey("afterSaleCode-"+afsServiceId+"-"+theDay)
								&& StringUtils.isNotBlank(noticeMail)){
							MailSupport.INSTANCE.sendMail(noticeMail, "京东重复服务单通知["+afterSaleCode+"]", "售后单号："+afterSaleCode+" \r\n重复服务单："+afsServiceId+","+serviceId);
							noticeTimeMap.put(afterSaleCode+"-"+afsServiceId+"-"+theDay,new Date());
						}
						continue;
					}
					
					afsServiceId = serviceId;
					
					// 保存追踪信息
					boolean hasUpdate = saveTrack(afterSaleCode, result.getJSONArray("serviceTrackInfoDTOs"));
					
					// 变更售后单状态
					String flowBussinessUid = "";
					String fromStatus = "";
					String toStatus = "";
					String flowType = "";
					RootResult re = null;
					String remark = "";
					MDataMap flowMap = new MDataMap();
					// 如果是40状态则表示服务单完成，需要修改售后单状态
					
					if("40".equals(result.getString("afsServiceStep")) || "50".equals(result.getString("afsServiceStep"))) {
						if("4497477800030001".equals(afterSale.get("asale_type"))) { // 退货退款
							MDataMap info = DbUp.upTable("oc_return_goods").one("return_code", afterSaleCode);
							// 如果退货单状态不一致则更新
							if(!"4497153900050001".equals(info.get("status"))){
								flowBussinessUid = info.get("uid");
								fromStatus = info.get("status");
								toStatus = "4497153900050001";
								flowType = "449715390005";
							}
							//JD 售后完成，需要写入定时，申请售后完成
							//售后完成订单
							if(DbUp.upTable("fh_agent_order_detail").count("order_code",info.get("order_code"))>0 && DbUp.upTable("za_exectimer").count("exec_info",info.get("return_code"),"exec_type","449746990027") <= 0) {
								JobExecHelper.createExecInfo("449746990027", info.get("return_code"), DateUtil.getSysDateTimeString());
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
					} else if("60".equals(result.getString("afsServiceStep"))
							|| "20".equals(result.getString("afsServiceStep"))) {
						
						if("60".equals(result.getString("afsServiceStep"))) { // 审核不通过(20)
							remark = "主动取消";
						}
						if("20".equals(result.getString("afsServiceStep"))) { // 审核不通过(20)
							remark = "客服驳回";
						}
						
						// 审核不通过(20)或取消 60都做客服驳回处理
						if("4497477800030001".equals(afterSale.get("asale_type"))) { // 退货退款
							MDataMap info = DbUp.upTable("oc_return_goods").one("return_code", afterSaleCode);
							
							// 检查是否退货的已经取消
							if(!"4497153900050006".equals(info.get("status")) && !"4497153900050007".equals(info.get("status"))){
								flowBussinessUid = info.get("uid");
								fromStatus = info.get("status");
								toStatus = "4497153900050006";
								flowType = "449715390005";
							}
							//售后订单取消
							if(DbUp.upTable("fh_agent_order_detail").count("order_code",info.get("order_code"))>0 && DbUp.upTable("za_exectimer").count("exec_info",info.get("return_code"),"exec_type","449746990026") <= 0) {
								JobExecHelper.createExecInfo("449746990026", info.get("return_code"), DateUtil.getSysDateTimeString());
							}
						} else if("4497477800030003".equals(afterSale.get("asale_type"))) { // 换货
							MDataMap info = DbUp.upTable("oc_exchange_goods").one("exchange_no", afterSaleCode);
							// 检查是否换货单已经取消
							if(!"4497153900020006".equals(info.get("status"))){
								flowBussinessUid = info.get("uid");
								fromStatus = info.get("status");
								toStatus = "4497153900020006";
								flowType = "449715390002";
							}
						}
					}
					
					if(StringUtils.isNotBlank(toStatus)) {
						flowMap.put("remark", remark);
						re = new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, "system", remark, flowMap);
						// 变更售后单的状态
						if(re.getResultCode() != 1) {
							String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
							// 服务单变更失败
							// 粗率限制一下发送频率
							if(!noticeTimeMap.containsKey("afterSaleCode-status-"+theDay)
									&& StringUtils.isNotBlank(noticeMail)){
								MailSupport.INSTANCE.sendMail(noticeMail, "售后单状态变更失败["+afterSaleCode+"]", "变更状态:"+toStatus+" \r\n失败消息："+re.getResultMessage());
								noticeTimeMap.put(afterSaleCode+"-status-"+theDay,new Date());
							}
						}
						
						// 京东客服驳回的需要做消息通知，只通知一次
						if("20".equals(result.getString("afsServiceStep")) && "0".equals(jdAfterSale.get("notice_flag_reject"))) {
							String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
							LogFactory.getLog(getClass()).warn("jd reject 京东服务单驳回!!!["+afterSaleCode+"]");
							if(StringUtils.isNotBlank(noticeMail)) {
								MailSupport.INSTANCE.sendMail(noticeMail, "京东服务单驳回["+afterSaleCode+"]", "订单号:"+afterSale.get("order_code")+" \r\n驳回原因："+StringUtils.trimToEmpty(result.getString("approveNotes")));
							}
							// 设置为已通知
							jdAfterSale.put("notice_flag_reject", "1");
							hasUpdate = true;
						}
					}
					
					// 京东把上门取件的售后单审核成客户发货时做邮件通知
					// 仅在变更时通知一次
					if("4".equals(jdAfterSale.get("pickware_type")) 
							&& "33".equals(result.getString("approvedResult"))
							&& !"33".equals(jdAfterSale.get("approved_result"))) {
						hasUpdate = true;
						
						LogFactory.getLog(getClass()).warn("京东服务单审核成客户发货!!!["+afterSaleCode+"]");
						String receives = support.bConfig("groupcenter.jd_notice_receives_aftersale");
						List<String> list = support.queryOpenId(receives);
						String msg = String.format("[%s][%s]", afterSaleCode, "请注意京东服务单从上门取件变更为客户发货！") ;
						for(String v : list) {
							support.sendWarnCountMsg("京东服务单", "退回方式变更", v, msg);
						}
					}
					
					String newAfsServiceStep = NumberUtils.toInt(result.getString("afsServiceStep"))+"";
					String newApprovedResult = NumberUtils.toInt(result.getString("approvedResult"))+"";
					String afsServiceStepName = StringUtils.trimToEmpty(result.getString("afsServiceStepName"));
					String approvedResultName = StringUtils.trimToEmpty(result.getString("approvedResultName"));
					// 状态变更时发送微信通知
					if(!jdAfterSale.get("afs_service_step").equals(newAfsServiceStep)
							|| !jdAfterSale.get("approved_result").equals(newApprovedResult)) {
						String msg = String.format("[%s][服务单状态:%s][审核结果:%s]",afterSaleCode,afsServiceStepName,approvedResultName);
						
						String receives = support.bConfig("groupcenter.jd_notice_receives_aftersale");
						List<String> list = support.queryOpenId(receives);
						for(String v : list) {
							support.sendWarnCountMsg("京东服务单", "状态变更", v, msg);
						}
					}
					
					jdAfterSale.put("afs_service_id", afsServiceId);
					jdAfterSale.put("afs_type", StringUtils.trimToEmpty(result.getString("customerExpect")));
					jdAfterSale.put("afs_apply_time", StringUtils.trimToEmpty(result.getString("afsApplyTime")));
					jdAfterSale.put("afs_service_step", newAfsServiceStep);
					jdAfterSale.put("afs_service_step_name", afsServiceStepName);
					jdAfterSale.put("approve_notes", StringUtils.trimToEmpty(result.getString("approveNotes")));
					jdAfterSale.put("approved_result", newApprovedResult);
					jdAfterSale.put("approved_result_name", approvedResultName);
					jdAfterSale.put("process_result", NumberUtils.toInt(result.getString("processResult"),0)+"");
					jdAfterSale.put("process_result_name", StringUtils.trimToEmpty(result.getString("processResultName")));
					jdAfterSale.put("update_time", FormatHelper.upDateTime());
					
					String sUpdateFields = "afs_service_id,afs_type,afs_apply_time,afs_service_step,afs_service_step_name,approve_notes,approved_result,approved_result_name,process_result,process_result_name,allow_operations,update_time";
					
					// 更新售后地址信息
					JSONObject addressInfoDTO = result.getJSONObject("serviceAftersalesAddressInfoDTO");
					if(addressInfoDTO != null) {
						jdAfterSale.put("afs_address", addressInfoDTO.getString("address"));
						jdAfterSale.put("afs_tel", addressInfoDTO.getString("tel"));
						jdAfterSale.put("afs_link_man", addressInfoDTO.getString("linkMan"));
						jdAfterSale.put("afs_post_code", addressInfoDTO.getString("postCode"));
						sUpdateFields += ",afs_address,afs_tel,afs_link_man,afs_post_code";
						
						// 客服审核(21),商家审核(22)，再更新一下售后单的售后地址
						if("21".equals(result.getString("afsServiceStep"))
								|| "22".equals(result.getString("afsServiceStep"))) {
							if("4497477800030001".equals(afterSale.get("asale_type"))) { // 退货退款
								MDataMap orgMap=DbUp.upTable("oc_return_goods").one("return_code",afterSaleCode);
								if(orgMap != null && !addressInfoDTO.getString("address").equals(orgMap.get("after_sale_address"))) {
									orgMap.put("address", addressInfoDTO.getString("address"));
									orgMap.put("contacts", addressInfoDTO.getString("linkMan"));
									orgMap.put("mobile", addressInfoDTO.getString("tel"));
									orgMap.put("receiver_area_code", addressInfoDTO.getString("postCode"));
									DbUp.upTable("oc_return_goods").dataUpdate(orgMap, "address,contacts,mobile,receiver_area_code", "zid");
								}
							} else if("4497477800030003".equals(afterSale.get("asale_type"))) { // 换货
								MDataMap orgMap=DbUp.upTable("oc_exchange_goods").one("exchange_no",afterSaleCode);
								if(orgMap != null && !addressInfoDTO.getString("address").equals(orgMap.get("address"))) {
									orgMap.put("after_sale_address", addressInfoDTO.getString("address"));
									orgMap.put("after_sale_person", addressInfoDTO.getString("linkMan"));
									orgMap.put("after_sale_mobile", addressInfoDTO.getString("tel"));
									orgMap.put("after_sale_postcode", addressInfoDTO.getString("postCode"));
									DbUp.upTable("oc_exchange_goods").dataUpdate(orgMap, "after_sale_address,after_sale_person,after_sale_mobile,after_sale_postcode", "zid");
								}
							}
						}
					}
					
					JSONArray allowOperations = result.getJSONArray("allowOperations");
					if(allowOperations != null) {
						jdAfterSale.put("allow_operations", StringUtils.join(allowOperations,","));
					} else {
						jdAfterSale.put("allow_operations", "");
					}
					
					// 有新的追踪信息或状态变更时更新服务单信息
					if(hasUpdate || !jdAfterSale.get("afs_service_step").equals(afsServiceStep)) {
						DbUp.upTable("oc_order_jd_after_sale").dataUpdate(jdAfterSale, sUpdateFields, "asale_code");
					}
				} else {
					rootResult.setResultCode(99);
					rootResult.setResultMessage(resultObj.getString("resultCode") + " : "+resultObj.getString("resultMessage"));
				}
			} else {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("同步失败，京东接口调用异常");
			}
		}
		
		return rootResult;
	}
	
	/**
	 * 同步京东售后单退款信息
	 * @param orderCode   惠家有订单号
	 * @param afterSaleCode 惠家有售后单号
	 */
	public RootResult execAfterSaleRefund(String orderCode,String afterSaleCode) {
		RootResult rootResult = new RootResult();
		MDataMap jdRefund = DbUp.upTable("oc_order_jd_refund").one("order_code",orderCode,"after_sale_code",StringUtils.trimToEmpty(afterSaleCode));
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("orderId", jdRefund.get("jd_order_id"));
		param.put("refId", jdRefund.get("afs_service_id")); 
		
		String resultText = RsyncJingdongSupport.callGateway("jd.kpl.open.aftersale.orderid", param);
				
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return rootResult;
			}
			
			JSONObject response = resultObj.getJSONObject("jd_kpl_open_aftersale_orderid_response");
			
			if(response.getBooleanValue("success")) {
				JSONArray resultList = response.getJSONArray("result");
				if(resultList != null && resultList.size() > 0) {
					JSONArray refundPaymentInfoVo = resultList.getJSONObject(0).getJSONArray("refundPaymentInfoVo");
					if(refundPaymentInfoVo != null && refundPaymentInfoVo.size() > 0) {
						JSONObject peymentInfo = refundPaymentInfoVo.getJSONObject(0);
						jdRefund.put("pay_id", peymentInfo.getString("payId"));
						jdRefund.put("pay_time", peymentInfo.getString("payTime"));
						jdRefund.put("refundable_amount", peymentInfo.getString("refundableAmount"));
						jdRefund.put("update_time", FormatHelper.upDateTime());
						
						if(StringUtils.isNotBlank(afterSaleCode)) {
							// 根据退货单生成退款单
							MDataMap returnGoodsData = DbUp.upTable("oc_return_goods").one("return_code", afterSaleCode);
							ReturnGoods returnGoods = new SerializeSupport<ReturnGoods>().serialize(returnGoodsData,new ReturnGoods());
							ReturnMoneyResult result = new FlowForSureGetGoods().creatReturnMoney(returnGoods);
							jdRefund.put("return_money_code", StringUtils.trimToEmpty(result.getReturnMoneyCode()));
						} else {
							// 整单退款
							CreateMoneyService createMoneyService = new CreateMoneyService();
							ReturnMoneyResult result = createMoneyService.creatReturnMoney(orderCode,"system","京东整单退款");
							jdRefund.put("return_money_code", StringUtils.trimToEmpty(result.getReturnMoneyCode()));
						}
						
						DbUp.upTable("oc_order_jd_refund").dataUpdate(jdRefund, "pay_id,pay_time,refundable_amount,update_time,return_money_code", "zid");
					}
				}
			}
		}
		
		return rootResult;
	}
	
	/**
	 * 根据京东订单号查询服务端编号
	 * @param jdOrderId
	 * @return
	 */
	public List<String> getAfsServiceIdList(String jdOrderId) {
		List<String> list = new ArrayList<String>();
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("jdOrderId", jdOrderId);
		param.put("pageIndex", 1);
		param.put("pageSize", 50);
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.serviceListPage.query", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				return list;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_serviceListPage_query_response");
			
			if(response.getBooleanValue("success")) {
				JSONArray serviceInfoList = response.getJSONObject("result").getJSONArray("serviceInfoList");
				for(int i = 0; i < serviceInfoList.size(); i++) {
					list.add(serviceInfoList.getJSONObject(i).getString("afsServiceId"));
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 查询京东的商品返回方式
	 * 返件方式: 上门取件(4)、客户发货(40)、客户送货(7)
	 * @return
	 */
	public MWebResult getAfsPickwareType(String jdOrderId, String skuId) {
		List<String> list = new ArrayList<String>();
		MWebResult result = new MWebResult();
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("jdOrderId", jdOrderId);
		param.put("skuId", skuId);
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.wareReturnJdComp.query", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				result.setResultCode(99);
				result.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return result;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_wareReturnJdComp_query_response");
			
			if(response.getBooleanValue("success")) {
				JSONArray nodeList = response.getJSONArray("result");
				if(nodeList != null) {
					for(int i = 0; i < nodeList.size(); i++){
						list.add(nodeList.getJSONObject(i).getString("code"));
					}
				}
				
				result.getResultList().addAll(list);
			} else {
				result.setResultCode(0);
				result.setResultMessage(StringUtils.trimToEmpty(response.getString("resultMessage")));
			}
		}
		
		return result;
	}
	
	/**
	 * 查询京东的服务类型
	 * 服务类型: 退货(10)、换货(20)、维修(30)
	 * @return
	 */
	public MWebResult getAfsServiceType(String jdOrderId, String skuId) {
		List<String> list = new ArrayList<String>();
		MWebResult result = new MWebResult();
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("jdOrderId", jdOrderId);
		param.put("skuId", skuId);
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.customerExpectComp.query", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				result.setResultCode(99);
				result.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return result;
			}
			
			JSONObject response = resultObj.getJSONObject("biz_afterSale_customerExpectComp_query_response");
			
			if(response.getBooleanValue("success")) {
				JSONArray nodeList = response.getJSONArray("result");
				if(nodeList != null) {
					for(int i = 0; i < nodeList.size(); i++){
						list.add(nodeList.getJSONObject(i).getString("code"));
					}
				}
				
				result.getResultList().addAll(list);
			} else {
				result.setResultCode(0);
				result.setResultMessage(StringUtils.trimToEmpty(response.getString("resultMessage")));
			}
		}
		
		return result;
	}
	
	/**
	 * 查询某商品是否可以提交售后服务
	 * @return
	 * 		根据resultCode值判断 <br>
	 * 		1: 可以提交售后 <br>
	 * 		99: 不可以提交售后<br>
	 */
	public RootResult checkAvailableNumber(String jdOrderId, String skuId) {
		RootResult rootResult = new RootResult();
		
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("jdOrderId", jdOrderId);
		param.put("skuId", skuId);
		
		Map<String, Object> paramJson = new HashMap<String, Object>();
		paramJson.put("param", param);
		String resultText = RsyncJingdongSupport.callGateway("biz.afterSale.availableNumberComp.query", paramJson);
		
		if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
			JSONObject resultObj = JSON.parseObject(resultText);
			if(resultObj.containsKey("errorResponse")) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("接口调用失败 "+resultObj.getString("errorResponse"));
				return rootResult;
			}
			JSONObject response = resultObj.getJSONObject("biz_afterSale_availableNumberComp_query_response");
			
			boolean success = response.getBooleanValue("success");
			String resultMessage = response.getString("resultMessage");
			int resultNum = response.getInteger("result");
		
			if(!success) {
				rootResult.setResultCode(99);
				rootResult.setResultMessage("["+resultNum+"] "+resultMessage);
			}
		}
		return rootResult;
	}
	
	/**
	 * 保存服务端追踪信息
	 * @param afterSaleCode
	 * @param ja
	 * @return true 有新的追踪信息 , false 没有更新追踪信息
	 */
	private boolean saveTrack(String afterSaleCode,JSONArray ja) {
		if(ja == null) return false;
		JSONObject obj;
		MDataMap map;
		boolean hasNew = false;
		for(int i = 0; i < ja.size(); i++) {
			obj = ja.getJSONObject(i);
			map = new MDataMap();
			map.put("asale_code", afterSaleCode);
			map.put("title", StringUtils.trimToEmpty(obj.getString("title")));
			map.put("context", StringUtils.trimToEmpty(obj.getString("context")));
			map.put("create_date", StringUtils.trimToEmpty(obj.getString("createDate")));
			map.put("create_name", StringUtils.trimToEmpty(obj.getString("createName")));
			map.put("create_pin", StringUtils.trimToEmpty(obj.getString("createPin")));
			map.put("create_time", FormatHelper.upDateTime());
			
			// 根据时间和内容判断是否重复
			if(DbUp.upTable("oc_order_jd_after_sale_track").count("asale_code", afterSaleCode, "create_date", map.get("create_date"), "context", map.get("context")) == 0) {
				DbUp.upTable("oc_order_jd_after_sale_track").dataInsert(map);
				hasNew = true;
			}
		}
		return hasNew;
	}
}
