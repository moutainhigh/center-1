package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.systemcenter.dcb.PushSkuStatusService;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadActivityAgent;
import com.srnpr.xmassystem.load.LoadCouponListForProduct;
import com.srnpr.xmassystem.load.LoadSkuInfo;
import com.srnpr.xmassystem.load.LoadSkuPriceChange;
import com.srnpr.xmassystem.plusquery.PlusModelQuery;
import com.srnpr.xmassystem.support.PlusSupportFenxiao;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.websupport.UserSupport;

/**
 * 商品价格审批
 * @author pang_jhui
 * @author editor: LHY 2016-4-6
 */
public class ProductSkuPriceService extends BaseClass {
	
	// 执行价格变更的方法同步锁
	static Object updateSkupriceTimeScopeNewLock = new Object();
	
	/**
	 * 创建商品价格审批流
	 * @param flowList
	 * @param flowStatus Constants.FLOW_STATUS_SKUPRICE_CW(4497172300130001:待财务审核),Constants.FLOW_STATUS_SKUPRICE_YY(4497172300130004:待运营审核)
	 */
	public void createProductSkuPriceFlow(List<MDataMap> flowList,MDataMap mDataMap, String flowStatus){
		
		if(flowList != null && !flowList.isEmpty()){
			/*商品编号*/
			String product_Code = flowList.get(0).get("product_code");
			
			if(StringUtils.isNotBlank(product_Code)){
				
//				List<MDataMap> list = new ArrayList<MDataMap>();
//				if(CollectionUtils.isNotEmpty(flowList)) {
//					for(MDataMap map: flowList) {
//						MDataMap dataMap = new MDataMap();
//						dataMap.put("start_time", map.get("start_time"));
//						dataMap.put("end_time", map.get("end_time"));
//						dataMap.put("sku_code", map.get("sku_code"));
//						map.remove("start_time");
//						map.remove("end_time");
//						list.add(dataMap);
//					}
//				}
				
				String flow_code = createFlow(product_Code,mDataMap, flowStatus);
				
				saveProductSkuPriceFlow(flow_code, flowList, flowStatus);
				
//				saveProductSkuPriceChangeTime(flow_code, list, product_Code);
				
			}
			
		}
		
	}
	
//	private void saveProductSkuPriceChangeTime(String flowCode, List<MDataMap> flowList, String productCode) {
//		if(CollectionUtils.isNotEmpty(flowList)) {
//			for(MDataMap map: flowList) {
//				MDataMap mDataMap = new MDataMap();
//				mDataMap.put("start_time", map.get("start_time"));
//				mDataMap.put("end_time", map.get("end_time"));
//				mDataMap.put("create_time", DateUtil.getSysDateTimeString());
//				mDataMap.put("is_delete", "0");
//				mDataMap.put("sku_code", map.get("sku_code"));
//				mDataMap.put("product_code", productCode);
//				mDataMap.put("flow_code", flowCode);
//				DbUp.upTable("pc_skuprice_change_flow_time").dataInsert(mDataMap);
//			}
//		}
//	}
	/**
	 * 保存商品价格变更信息
	 * @param flowCode
	 * 		流程编号
	 * @param flowList
	 * 		流程列表
	 */
	public void saveProductSkuPriceFlow(String flowCode,List<MDataMap> flowList, String flowStatus){	
		
		if(flowList != null){
			
			for(MDataMap mDataMap : flowList){
				
				mDataMap.put("flow_code", flowCode);
				
//				mDataMap.put("status", Constants.FLOW_STATUS_SKUPRICE_CW);
				mDataMap.put("status", flowStatus);
				mDataMap.put("is_delete", "0");
				DbUp.upTable("pc_skuprice_change_flow").dataInsert(mDataMap);
				
			}
			
		}
		
		
	}
	
	/**
	 * 创建工作流程
	 * @param productCode
	 * 		商品编号
	 * @return 流程编号
	 */
	public String createFlow(String productCode,MDataMap mDataMap, String flowStatus){
		
		String flowCode = "";
		
		UserSupport userSupport = new UserSupport();
		/*审批流程信息*/
		ScFlowMain flow = new ScFlowMain();
		
		flow.setCreator(userSupport.getUserInfo().getUserCode());
		
		/*待财务审批*/
//		flow.setCurrentStatus(Constants.FLOW_STATUS_SKUPRICE_CW);
		flow.setCurrentStatus(flowStatus);
		
		flow.setFlowUrl("");
		
		flow.setFlowTitle(mDataMap.get("small_seller_code"));
		
		flow.setFlowType(Constants.FLOW_TYPE_SKUPRICE_APPROVE);
		
		flow.setOuterCode(productCode);
		
		flow.setFlowRemark(mDataMap.get("flow_mark"));
		
		/*创建工作流*/
		RootResult rrFlow = new FlowService().CreateFlow(flow);
		
		if(rrFlow != null){
			
			flowCode = rrFlow.getResultMessage();
			
		}
		
		return flowCode;
		
	}
	
	/**
	 * 获取价格更新列表
	 * @param flowCode
	 * 		流程编号
	 * @param productCode
	 * 		产品编号
	 * @param status
	 * 		状态
	 * @return 商品价格更新信息
	 */
	public List<Map<String,Object>> getSkuPriceFlowList(String flowCode, String productCode, String status){
		
		return DbUp.upTable("pc_skuprice_change_flow")
				.listByWhere("flow_code", flowCode, "product_code", productCode, "status",status);
		
	}
	
	/**
	 * 更新商品价格审批过程信息
	 * @param mDataMap
	 */
	public void update(MDataMap mDataMap){
		DbUp.upTable("pc_skuprice_change_flow").update(mDataMap); 
	}
	
	/**
	 * 更新状态
	 * @param flowCode
	 * 		流程编号
	 * @param productCode
	 * 		商品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 */
	public void updateStatus(String flowCode, String productCode, String fromStatus, String toStatus){
		
		List<Map<String, Object>> dataMaps = getSkuPriceFlowList(flowCode, productCode, fromStatus);
		
		if(dataMaps != null){
			
			for(Map<String, Object> dataMap : dataMaps){
				
				MDataMap updateDataMap = new MDataMap(dataMap);
				
				updateDataMap.put("status", toStatus);
				
				update(updateDataMap);
				
			}
			
		}
		
	
		
	}
	
	/**
	 * 更新sku价格且更新过程状态（加上时间有效期后不会更新价格 edit by ligj）
	 * @param flowCode
	 * 		流程编号
	 * @param productCode
	 * 		商品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 * 
	 */
	public void updateSkuPriceAndStatus(String flowCode, String productCode, String fromStatus, String toStatus){
		
		List<Map<String, Object>> dataMaps = getSkuPriceFlowList(flowCode, productCode, fromStatus);
		
		if(dataMaps != null){
			
			boolean doExecutePriceChange = false;
			for(Map<String, Object> dataMap : dataMaps){
				
				MDataMap updateDataMap = new MDataMap(dataMap);
				
				updateDataMap.put("status", toStatus);
				
				update(updateDataMap);
				
				//updateSkuPrice(updateDataMap);
				
				//如果存在开始日期小于等于今天日期的，则执行修改价格操作,如果没有日期条件的，也执行修改操作
				if(updateDataMap.get("start_time")==null||updateDataMap.get("start_time").equals("")){
					doExecutePriceChange = true;
				}else{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date temp1;
					try {
						temp1 = sdf.parse(updateDataMap.get("start_time").toString());
						Date temp2 = new Date();
						if(temp1.getTime()<=temp2.getTime()){
							doExecutePriceChange = true;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
//				if (DateUtil.getSysDateString().equals(updateDataMap.get("start_time"))) {
//					doExecutePriceChange = true;
//				}				
			}
			
			//调用定时执行的方法
			if (doExecutePriceChange) {
				Thread thread = new Thread(new ProductSkuPriceThread());
				thread.start();
			}
			/**  2016-05-11注释，时间更新会走定时任务，根据价格有效期去更新。ligj
			MDataMap productPriceRange = calSellPriceRange(dataMaps);
			
			productPriceRange.put("product_code", productCode);
			
			DbUp.upTable("pc_productinfo").dataUpdate(productPriceRange, "min_sell_price,max_sell_price", "product_code");
			
			PlusHelperNotice.onChangeProductInfo(productCode);
			
			new ProductJmsSupport().onChangeForProductChangeAll(productCode);
			**/
		}	
		
	}
	
	/**
	 * 更新价格信息
	 * @param mDataMap
	 */
	public void updateSkuPrice(MDataMap mDataMap){
		
		Map<String, Object> sku = DbUp.upTable("pc_skuinfo").dataSqlOne("select * from productcenter.pc_skuinfo where sku_code=:sku_code", new MDataMap("sku_code",mDataMap.get("sku_code")));
		if(sku != null){
			MDataMap updateDataMap = new MDataMap();
			updateDataMap.put("cost_price", mDataMap.get("cost_price"));
			updateDataMap.put("sell_price", mDataMap.get("sell_price"));
			updateDataMap.put("sku_code", mDataMap.get("sku_code"));
			updateDataMap.put("product_code", mDataMap.get("product_code"));
			DbUp.upTable("pc_skuinfo").dataUpdate(updateDataMap, "cost_price,sell_price", "product_code,sku_code");
			//还原完成后更新状态为已变更
			this.updateSkupriceRestoreStatus(mDataMap.get("zid"), "2");
			/**
			 * 记录sku价格变更日志 2017-06-15 zhy
			 */
			MDataMap lc_skuprice_change_flow = new MDataMap();
			lc_skuprice_change_flow.put("product_code", sku.get("product_code").toString());
			lc_skuprice_change_flow.put("sku_code", sku.get("sku_code").toString());
			lc_skuprice_change_flow.put("cur_cost_price", sku.get("cost_price").toString());
			lc_skuprice_change_flow.put("cur_sell_price",sku.get("sell_price").toString());
			lc_skuprice_change_flow.put("change_cost_price", mDataMap.get("cost_price"));
			lc_skuprice_change_flow.put("change_sell_price", mDataMap.get("sell_price"));
			lc_skuprice_change_flow.put("create_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("lc_skuprice_change_flow").dataInsert(lc_skuprice_change_flow);
			
			try{
				//判断成本价是否更改
				String oldCost = sku.get("cost_price").toString();
				String newCost = mDataMap.get("cost_price");
				if(!oldCost.equals(newCost)) {
					if(!"".equals(oldCost) && !"".equals(newCost)) {
						BigDecimal old = new BigDecimal(oldCost);
						BigDecimal nw = new BigDecimal(newCost);
						if(old.compareTo(nw) != 0) {
							changeBfStatus(mDataMap.get("sku_code"));
						}
					}else {
						changeBfStatus(mDataMap.get("sku_code"));
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 还原价格信息
	 * @param mDataMap
	 */
	public void restoreSkuPrice(MDataMap mDataMap){
		
		MDataMap updateDataMap = new MDataMap();
		
		updateDataMap.put("cost_price", mDataMap.get("cost_price_old"));
		
		updateDataMap.put("sell_price", mDataMap.get("sell_price_old"));
		
		updateDataMap.put("sku_code", mDataMap.get("sku_code"));
		
		updateDataMap.put("product_code", mDataMap.get("product_code"));
		
		DbUp.upTable("pc_skuinfo").dataUpdate(updateDataMap, "", "product_code,sku_code");
		
		//还原完成后更新状态为已还原
		this.updateSkupriceRestoreStatus(mDataMap.get("zid"), "1");
		
		
		//判断成本价是否更改
		String oldCost = mDataMap.get("cost_price_old");
		String newCost = mDataMap.get("cost_price");
		if(!oldCost.equals(newCost)) {
			if(!"".equals(oldCost) && !"".equals(newCost)) {
				BigDecimal old = new BigDecimal(oldCost);
				BigDecimal nw = new BigDecimal(newCost);
				if(old.compareTo(nw) != 0) {
					changeBfStatus(mDataMap.get("sku_code"));
				}
			}else {
				changeBfStatus(mDataMap.get("sku_code"));
			}
		}
	}
	
	/**
	 * 更改缤纷推送状态
	 * 判断当前sku_code 是否为已在多彩宝上架，已上架，要做强制下架处理(调用多彩宝接口下架,变更sku_status为30,强制下架状态)
	 * 										其他状态，则变更多彩宝状态为40(成本价变更)
	 * @param skuCode
	 */
	public void changeBfStatus(String skuCode) {
		MDataMap bfSkuInfo = DbUp.upTable("pc_bf_skuinfo").one("sku_code", skuCode);
		if(bfSkuInfo != null) {
			String newStatus = "";
			String remark = "";
			String sku_status = bfSkuInfo.get("sku_status");
			if("1".equals(sku_status) || "2".equals(sku_status) || "10".equals(sku_status) || "20".equals(sku_status)) {
				if("10".equals(sku_status)) {//已上架
					//调用多彩宝接口，让多彩宝对应的sku下架
					PushSkuStatusService pushSkuStatusService = new PushSkuStatusService();
					pushSkuStatusService.pushSkuStatus(skuCode, "N", 0, "");
					
					newStatus = "30";
					remark = "变更成本价导致强制下架";
				}else {//其他状态
					newStatus = "40";
					remark = "成本价变更导致自动驳回";
				}
				
				//更新pc_bf_skuinfo表状态
				MDataMap updateMap = new MDataMap();
				updateMap.put("zid", bfSkuInfo.get("zid"));
				updateMap.put("uid", bfSkuInfo.get("uid"));
				updateMap.put("sku_status", newStatus);
				DbUp.upTable("pc_bf_skuinfo").update(updateMap);
				
				//添加pc_bf_review_log 日志表                 
				String createTime = DateUtil.getSysDateTimeString();
				MDataMap skuInfo = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode);
				DbUp.upTable("pc_bf_review_log").insert("sku_code", skuCode, "sku_name", skuInfo.get("sku_name"), "operate_status", "成本价变更", "operator", "系统", 
						"operate_time", createTime, "remark", remark);
			}
		}
	}
	
	/**
	 * 计算最大最小销售价格
	 * @param dataMaps
	 * 		待计算数据
	 * @return 计算后价格数据
	 */
	public MDataMap calSellPriceRange(List<Map<String, Object>> dataMaps){
		
		BigDecimal tempMin = BigDecimal.ZERO;
		
		BigDecimal tempMax = BigDecimal.ZERO;
		
		if(dataMaps != null){
			
			boolean init = true;
			
			for(Map<String, Object> dataMap : dataMaps){
				
				MDataMap mDataMap = new MDataMap(dataMap);
				
				BigDecimal sell_price = new BigDecimal(mDataMap.get("sell_price"));
				
				if (init) {
					
					tempMin = sell_price;
					
					tempMax = sell_price;
					
					init = false;
					
				} else {
					if (tempMin.compareTo(sell_price)==1)
						
						tempMin = sell_price;
					
					if (tempMax.compareTo(sell_price)==-1)
						
						tempMax = sell_price;
					
				}
				
			}
			
			
		}
		
		return new MDataMap("min_sell_price",tempMin.toString(),"max_sell_price",tempMax.toString());		
		
	}
	
	/**
	 * 更新sku的还原状态
	 * @param zid
	 * @param isDelete  0未变更，1已还原，2已变更，3已删除
	 */
	public void updateSkupriceRestoreStatus(String zid,String isDelete) {
		MDataMap flowMap = new MDataMap();
		flowMap.put("is_delete", isDelete);
		flowMap.put("zid",zid);
		DbUp.upTable("pc_skuprice_change_flow").dataUpdate(flowMap, "is_delete", "zid");
	}
	
	/**
	 * 根据时间段更新sku价格<br>
	 * 走新的调价方法updateSkupriceTimeScopeNew(临时调价和永久调价)
	 */
	@Deprecated
	public void updateSkupriceTimeScope() {
		updateSkupriceTimeScopeNew();
		
		//****************原调价逻辑废弃*******************
		/**
		String currentDate = DateUtil.getSysDateString();	//yyyy-MM-dd
		//查询出开始时间小于等于当前时间的flow_code，然后再进行价格更新（价格变更或价格还原）
		String sWhere = "start_time<='"+currentDate+"' and (is_delete='0' or is_delete='2') and status='4497172300130002' ";
		List<MDataMap> flowTimeMapList = DbUp.upTable("pc_skuprice_change_flow").queryAll("", "zid desc", sWhere, null);
		
		//需要进行价格变更的sku编号(结束时间大于等于当前时间的)
		Map<String,MDataMap> updateNewPrice = new HashMap<String,MDataMap>();
		//需要进行价格还原的sku编号(结束时间小于当前时间的)
		Map<String,MDataMap> updateOldPrice = new HashMap<String,MDataMap>();

		//需要进行标记删除的数据，key为数据的zid
		Map<String,MDataMap> updateDelPrice = new HashMap<String, MDataMap>();

		//需要进行还原为初始状态的记录数据，key为数据的zid
		Map<String,MDataMap> updateInitPrice = new HashMap<String, MDataMap>();
		
		//需要进行修改状态为已还原的记录数据，key为数据的zid
		Map<String,MDataMap> updateRestorePrice = new HashMap<String, MDataMap>();
		
		//需要更新的sku
		for (MDataMap mDataMap : flowTimeMapList) {
			String endTime = mDataMap.get("end_time");
			String sku_code = mDataMap.get("sku_code");
			
			String isDelete = mDataMap.get("is_delete");
			if (currentDate.compareTo(endTime) <= 0) {
				if (updateNewPrice.containsKey(sku_code)) {
					//不是此商品最新的时间段，并且结束时间小于等于最新时间段的结束时间，则此商品的这种数据都标记删除，不再进行价格更新
					if (endTime.compareTo(updateNewPrice.get(sku_code).get("end_time")) <= 0) {
						updateDelPrice.put(mDataMap.get("zid"), mDataMap);
					}else if(!"0".equals(isDelete)){
						//不是此商品最新时间段，并且结束时间大于最新时间段的结束时间，则此商品的这种数据都标记还原为0状态，最近数据过期后进行价格还原操作
						updateInitPrice.put(mDataMap.get("zid"), mDataMap);
					}
				}else{
					updateNewPrice.put(sku_code, mDataMap);
				}
			}else if ("2".equals(isDelete)) {
				//如果2已变更状态的数据过期，则把此数据改为1已还原状态
				updateRestorePrice.put(mDataMap.get("zid"), mDataMap);
			}
		}
		
		//需要还原的sku
		for (MDataMap mDataMap : flowTimeMapList) {
			String endTime = mDataMap.get("end_time");
			String sku_code = mDataMap.get("sku_code");
			if(StringUtils.isNotBlank(endTime) &&  currentDate.compareTo(endTime) > 0 && !updateNewPrice.containsKey(sku_code) && !updateOldPrice.containsKey(sku_code)){
				updateOldPrice.put(mDataMap.get("sku_code"), mDataMap);
			}
		}
		ProductSkuPriceService skuPriceService = new ProductSkuPriceService();
		//商品map，用来更新商品的最大销售价以及最小销售价
		Map<String,List<Map<String, Object>>> productMap = new HashMap<String,List<Map<String, Object>>>();
		
		//商品编号Map。用来储存所有更新过价格的商品编号
		Map<String,Integer> productCodeMap = new HashMap<String,Integer>();
		
		//开始更新sku价格
		for (String sku_code : updateNewPrice.keySet()) {
			MDataMap priceMap = updateNewPrice.get(sku_code);
			String productCode = priceMap.get("product_code");
			
			List<Map<String, Object>> listPriceMap = new ArrayList<Map<String, Object>>();
			if (productMap.containsKey(productCode)) {
				listPriceMap = productMap.get(productCode);
			}
			Map<String, Object> sellPriceMap = new HashMap<String, Object>();
			sellPriceMap.put("sell_price", priceMap.get("sell_price"));
			sellPriceMap.put("sku_code", sku_code);
			listPriceMap.add(sellPriceMap);
			productMap.put(productCode, listPriceMap);
			
			//如果状态为已更新，则不再进行更新操作
			if ("2".equals(priceMap.get("is_delete"))) {
				continue;
			}
			
			productCodeMap.put(productCode, 1);
			
			//更新sku表中的价格
			skuPriceService.updateSkuPrice(priceMap);
		}
		
		//开始标记删除sku价格更新记录
		for (String zid : updateDelPrice.keySet()) {
			this.updateSkupriceRestoreStatus(zid, "3");
		}
		//开始标记初始还原sku价格更新记录
		for (String zid : updateInitPrice.keySet()) {
			this.updateSkupriceRestoreStatus(zid, "0");
		}
		//开始标记还原sku价格更新记录
		for (String zid : updateRestorePrice.keySet()) {
			this.updateSkupriceRestoreStatus(zid, "1");
		}
		//开始还原sku价格
		for (String sku_code : updateOldPrice.keySet()) {
			MDataMap priceMap = updateOldPrice.get(sku_code);
			String productCode = priceMap.get("product_code");
			
			List<Map<String, Object>> listPriceMap = new ArrayList<Map<String, Object>>();
			if (productMap.containsKey(productCode)) {
				listPriceMap = productMap.get(productCode);
			}
			Map<String, Object> sellPriceMap = new HashMap<String, Object>();
			sellPriceMap.put("sell_price", priceMap.get("sell_price_old"));		//因为要还原价格，所以这里取old价格
			sellPriceMap.put("sku_code", sku_code);
			listPriceMap.add(sellPriceMap);
			productMap.put(productCode, listPriceMap);
			
			productCodeMap.put(productCode, 1);
			//还原sku表中的价格
			skuPriceService.restoreSkuPrice(priceMap);
		}
		
		//更新product表中的最大销售价与最小销售价	
		ProductJmsSupport jmsSupport = new ProductJmsSupport();
		ProductService service = new ProductService();
		JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
		
		//此处for循环取用productCodeMap中的数据。
		//因为此map中储存的都是本次修改过sku价格的商品，需要更新最大销售价格以及最小销售价格和商品的缓存
		for (String productCode : productCodeMap.keySet()) {
			//计算出最大销售价与最小销售价
			List<Map<String, Object>> listPriceMaps = productMap.get(productCode);
			MDataMap productPriceRange = skuPriceService.calSellPriceRange(listPriceMaps);
			productPriceRange.put("product_code", productCode);
			//添加修改时间 2016-11-08 zhy
			productPriceRange.put("update_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("pc_productinfo").dataUpdate(productPriceRange, "min_sell_price,max_sell_price,update_time", "product_code");
			
			//更新完商品价格信息后需要插入到flow表中一条数据
			PcProductinfo pro = service.getProduct(productCode+"_1");
			if (null != pro && StringUtils.isNotBlank(pro.getProductCode())) {
				pro.setMaxSellPrice(new BigDecimal(productPriceRange.get("max_sell_price")));
				pro.setMinSellPrice(new BigDecimal(productPriceRange.get("min_sell_price")));
				
				for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
					ProductSkuInfo psku= pro.getProductSkuInfoList().get(i);
					
					for (Map<String, Object> priceMap : listPriceMaps) {
						if(psku.getSkuCode().equals(priceMap.get("sku_code"))){
							pro.getProductSkuInfoList().get(i).setSellPrice(new BigDecimal(priceMap.get("sell_price").toString()));
							break;
						}
					}
				}
			}
			//插入商品历史流水信息
//			com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
//					BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
//			com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
//			
//			ppf.setCreateTime(DateUtil.getSysDateTimeString());
//			ppf.setCreator("PcSkuPriceChangeTimeJob");
//			ppf.setFlowCode(WebHelper.upCode(ProductService.ProductFlowHead));
//			ppf.setFlowStatus(SkuCommon.FlowStatusInit);
//			ppf.setProductCode(productCode);
//			
//			ppf.setProductJson(pHelper.ObjToString(pro));
//			ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
//			ppf.setUpdateTime(DateUtil.getSysDateTimeString());
//			ppf.setUpdator("PcSkuPriceChangeTimeJob");
//			ppfm.insertSelective(ppf);
			
			PlusHelperNotice.onChangeProductInfo(productCode);
			jmsSupport.onChangeForProductChangeAll(productCode);

			MDataMap productFlow = new MDataMap();
			productFlow.put("flow_code", WebHelper.upCode(ProductService.ProductFlowHead));
			productFlow.put("product_code", productCode);
			productFlow.put("product_json",pHelper.ObjToString(pro));
			productFlow.put("flow_status", SkuCommon.FlowStatusInit);
			productFlow.put("creator", "PcSkuPriceChangeTimeJob");
			productFlow.put("create_time",DateUtil.getSysDateTimeString());
			productFlow.put("updator", "PcSkuPriceChangeTimeJob");
			productFlow.put("update_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("pc_productflow").dataInsert(productFlow);
			
		}
		*/
	}
	
	/**
	 * 商品调价生效方法
	 * changeType 1永久调价 、2临时调价
	 */
	public void updateSkupriceTimeScopeNew() {
		synchronized(updateSkupriceTimeScopeNewLock) {
			String[] types = {"1","2"};
			for(String changeType : types) {
				String currentDate = DateUtil.getSysDateString();	//yyyy-MM-dd
				//查询出开始时间小于等于当前时间的flow_code，然后再进行价格更新（价格变更或价格还原）
				//is_delete: 0未变更;1已还原;2已变更;3已删除
				String sWhere = "(is_delete='0' or is_delete='2') and status='4497172300130002' and do_type = '"+changeType+"'";
				
				// 永久调价
				if("1".equals(changeType)){
					// 最后提交的排到前面，后面变更档案价的时候会用到
					List<MDataMap> flowTimeMapList = DbUp.upTable("pc_skuprice_change_flow").queryAll("", "zid desc", sWhere, null);
					
					updateSkupriceForSkuInfo(flowTimeMapList);
				}else if("2".equals(changeType)){ // 临时调价
					// 临时调价需要限定一下开始和结束时间
					sWhere += " and start_time<='"+currentDate+"' and end_time >= '" + currentDate + "'";
					// 最后提交的排到前面，后面变更档案价的时候会用到
					List<MDataMap> flowTimeMapList = DbUp.upTable("pc_skuprice_change_flow").queryAll("", "zid desc", sWhere, null);
					
					updateSkupriceForActivity(flowTimeMapList);
				}
			}
		}
	}
	
	// 永久调价
	private void updateSkupriceForSkuInfo(List<MDataMap> flowTimeMapList){
		Map<String,List<MDataMap>> productListMap = new HashMap<String, List<MDataMap>>();
		List<MDataMap> listPriceMaps = new ArrayList<MDataMap>();
		
		Map<String,String> skuCodeMap = new HashMap<String, String>();
		MDataMap price;
		for(MDataMap m : flowTimeMapList) {
			// 如果存在多条变更档案价的记录，则以最后提交的为准，其他的都设置为已删除
			if(skuCodeMap.containsKey(m.get("sku_code"))) {
				MDataMap update = new MDataMap();
				update.put("zid", m.get("zid"));
				update.put("is_delete", "3");
				DbUp.upTable("pc_skuprice_change_flow").dataUpdate(update, "is_delete", "zid");
				continue;
			}
			
			if("0".equals(m.get("is_delete"))){
				price = new MDataMap("sku_code", m.get("sku_code"), "sell_price", m.get("sell_price"));
				listPriceMaps.add(price);
				
				if(!productListMap.containsKey(m.get("product_code"))) {
					productListMap.put(m.get("product_code"), new ArrayList<MDataMap>());
				}
				productListMap.get(m.get("product_code")).add(m);
			}
			
			skuCodeMap.put(m.get("sku_code"), "");
		}
		
		JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
		ProductJmsSupport jmsSupport = new ProductJmsSupport();
		LoadSkuInfo loadSkuInfo = new LoadSkuInfo();
		
		Set<Entry<String, List<MDataMap>>> entryList = productListMap.entrySet();
		for(Entry<String, List<MDataMap>> entry : entryList) {
			String productCode = entry.getKey();
			List<MDataMap> skuPriceList = entry.getValue();
			
			// 更新单个SKU的价格
			for(MDataMap m : skuPriceList) {
				// 先清除一下缓存避免更新数据库成功但是缓存刷新失败造成的不一致
				loadSkuInfo.deleteInfoByCode(m.get("sku_code"));
				updateSkuPrice(m);
				// 更新完成再清除一下缓存，确保缓存数据是最新的
				loadSkuInfo.deleteInfoByCode(m.get("sku_code"));
			}
			
			//计算出最大销售价与最小销售价
			MDataMap priceMap = DbUp.upTable("pc_skuinfo").oneWhere("min(sell_price) min_sell_price, max(sell_price) max_sell_price", "", "", "product_code", productCode);
			MDataMap productPriceRange = new MDataMap();
			productPriceRange.put("min_sell_price", priceMap.get("min_sell_price"));
			productPriceRange.put("max_sell_price", priceMap.get("max_sell_price"));
			productPriceRange.put("product_code", productCode);
			productPriceRange.put("update_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("pc_productinfo").dataUpdate(productPriceRange, "min_sell_price,max_sell_price,update_time", "product_code");
			
			//更新完商品价格信息后需要插入到flow表中一条数据
			ProductService service = new ProductService();
			PcProductinfo pro = service.getProduct(productCode+"_1");
			if (null != pro && StringUtils.isNotBlank(pro.getProductCode())) {
				pro.setMaxSellPrice(new BigDecimal(productPriceRange.get("max_sell_price")));
				pro.setMinSellPrice(new BigDecimal(productPriceRange.get("min_sell_price")));
				
				for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
					ProductSkuInfo psku= pro.getProductSkuInfoList().get(i);
					
					for (MDataMap m : listPriceMaps) {
						if(psku.getSkuCode().equals(m.get("sku_code"))){
							pro.getProductSkuInfoList().get(i).setSellPrice(new BigDecimal(m.get("sell_price").toString()));
							break;
						}
					}
				}
			}
			//插入商品历史流水信息
			MDataMap productFlow = new MDataMap();
			productFlow.put("flow_code", WebHelper.upCode(ProductService.ProductFlowHead));
			productFlow.put("product_code", productCode);
			productFlow.put("product_json",pHelper.ObjToString(pro));
			productFlow.put("flow_status", SkuCommon.FlowStatusInit);
			productFlow.put("creator", "PcSkuPriceChangeTimeJob");
			productFlow.put("create_time",DateUtil.getSysDateTimeString());
			productFlow.put("updator", "PcSkuPriceChangeTimeJob");
			productFlow.put("update_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("pc_productflow").dataInsert(productFlow);
			
			PlusHelperNotice.onChangeProductInfo(productCode);
			jmsSupport.onChangeForProductChangeAll(productCode);
			
			// 更新分销券金额
			updateFxProduct(productCode);
		}
	}
	
	// 临时调价
	private void updateSkupriceForActivity(List<MDataMap> flowTimeMapList){
		// 根据开始时间倒序排列
		Collections.sort(flowTimeMapList, new Comparator<MDataMap>() {
			@Override
			public int compare(MDataMap o1, MDataMap o2) {
				return o2.get("start_time").compareTo(o1.get("start_time"));
			}
		});
		
		Map<String,List<MDataMap>> productListMap = new HashMap<String, List<MDataMap>>();
		List<MDataMap> listPriceMaps = new ArrayList<MDataMap>();
		
		Map<String,String> skuCodeMap = new HashMap<String, String>();
		MDataMap price;
		for(MDataMap m : flowTimeMapList) {
			// 如果存在一个SKU在当前时间有多条有效调价记录，则只检查开始时间最近的那一条，其他记录不做处理
			if(skuCodeMap.containsKey(m.get("sku_code"))) {
				continue;
			}
			
			if("0".equals(m.get("is_delete"))){
				price = new MDataMap("sku_code", m.get("sku_code"), "sell_price", m.get("sell_price"));
				listPriceMaps.add(price);
				
				if(!productListMap.containsKey(m.get("product_code"))) {
					productListMap.put(m.get("product_code"), new ArrayList<MDataMap>());
				}
				productListMap.get(m.get("product_code")).add(m);
			}
			
			// 加入到临时变量里面以便于循环时判断是否已经存在
			skuCodeMap.put(m.get("sku_code"), "");
		}
		
		ProductJmsSupport jmsSupport = new ProductJmsSupport();
		LoadSkuPriceChange loadSkuPriceChange = new LoadSkuPriceChange();
		
		Set<Entry<String, List<MDataMap>>> entryList = productListMap.entrySet();
		for(Entry<String, List<MDataMap>> entry : entryList) {
			String productCode = entry.getKey();
			List<MDataMap> skuPriceList = entry.getValue();
			
			// 更新单个SKU的价格
			for(MDataMap m : skuPriceList) {
				// 先清除一下缓存避免更新数据库成功但是缓存刷新失败造成的不一致
				loadSkuPriceChange.deleteInfoByCode(m.get("sku_code"));
				
				//更新状态为已变更
				this.updateSkupriceRestoreStatus(m.get("zid"), "2");
				
				MDataMap skuInfo = DbUp.upTable("pc_skuinfo").oneWhere("sell_price,cost_price", "", "", "sku_code",m.get("sku_code"));
				MDataMap lc_skuprice_change_flow = new MDataMap();
				lc_skuprice_change_flow.put("product_code", m.get("product_code").toString());
				lc_skuprice_change_flow.put("sku_code", m.get("sku_code").toString());
				lc_skuprice_change_flow.put("cur_cost_price", skuInfo.get("cost_price").toString());
				lc_skuprice_change_flow.put("cur_sell_price",skuInfo.get("sell_price").toString());
				lc_skuprice_change_flow.put("change_cost_price", m.get("cost_price"));
				lc_skuprice_change_flow.put("change_sell_price", m.get("sell_price"));
				lc_skuprice_change_flow.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_skuprice_change_flow").dataInsert(lc_skuprice_change_flow);
				
				// 更新完成再清除一下缓存，确保缓存数据是最新的
				loadSkuPriceChange.deleteInfoByCode(m.get("sku_code"));
			}
			
			PlusHelperNotice.onChangeProductInfo(productCode);
			jmsSupport.onChangeForProductChangeAll(productCode);
		}
	}
	
	public List<String> getAllPriceAndStatus(String skuCode){
		
		String sql = "SELECT "
				+ "ifnull((SELECT `bfsku`.`sku_cost`FROM `productcenter`.`pc_bf_skuinfo` `bfsku` WHERE (`bfsku`.`sku_code` = `sku`.`sku_code`) AND ( bfsku.sku_status = '1' OR bfsku.sku_status = '2' OR bfsku.sku_status = '10')),sku.cost_price) AS cost_price,"
				+ "ifnull((SELECT `bfsku`.`sku_supply` FROM `productcenter`.`pc_bf_skuinfo` `bfsku` WHERE(`bfsku`.`sku_code` = `sku`.`sku_code`) AND (bfsku.sku_status = '1' OR bfsku.sku_status = '2' OR bfsku.sku_status = '10')),(SELECT p_bf_getSupplyPrice (product.product_code, sku.cost_price,product.small_seller_code) FROM DUAL)) AS sku_supply,"
				+ "ifnull((SELECT `bfsku`.`sku_mission` FROM `productcenter`.`pc_bf_skuinfo` `bfsku` WHERE(`bfsku`.`sku_code` = `sku`.`sku_code`) AND (bfsku.sku_status = '1' OR bfsku.sku_status = '2' OR bfsku.sku_status = '10')),(SELECT p_bf_getCommission (product.product_code,sku.cost_price,product.small_seller_code) FROM DUAL)) AS commission_price,"
				+ "ifnull((SELECT `bfsku`.`sku_operate_cost` FROM `productcenter`.`pc_bf_skuinfo` `bfsku` WHERE(`bfsku`.`sku_code` = `sku`.`sku_code`) AND (bfsku.sku_status = '1' OR bfsku.sku_status = '2' OR bfsku.sku_status = '10')),(SELECT p_bf_getOperatePrice (product.product_code,sku.cost_price,product.small_seller_code) FROM DUAL)) AS operate_price,"
				+ "(SELECT p_bf_getSkuStatus(sku.sku_code)) AS sku_status "
				+ "FROM productcenter.pc_skuinfo sku,productcenter.pc_productinfo product WHERE sku.product_code = product.product_code AND sku.sku_code = :sku_code";
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("sku_code", skuCode);
		
		Map<String, Object> resultMap = DbUp.upTable("pc_skuinfo").dataSqlOne(sql, mWhereMap);
		
		List<String> result = new ArrayList<String>();
		result.add(resultMap.get("cost_price") + "");
		result.add(resultMap.get("sku_supply") + "");
		result.add(resultMap.get("commission_price") + "");
		result.add(resultMap.get("operate_price") + "");
		result.add(resultMap.get("sku_status") + "");
		
		return result;
	}
	
	// 查询如果有分销的券则进行重新计算优惠券金额
	public void updateFxProduct(String productCode) {
		String sql = "SELECT p.zid,p.coupon_type_code,o.activity_code,p.coupon_money FROM oc_activity_agent_product p,oc_activity o "
				+ " WHERE p.activity_code = o.activity_code AND o.end_time > NOW() AND o.flag = 1 AND p.flag_enable = 1"
				+ " AND p.produt_code = :product_code";
		
		List<Map<String, Object>> list = DbUp.upTable("oc_activity_agent_product").dataSqlPriLibList(sql, new MDataMap("product_code",productCode));
		if(list.isEmpty()) return;
		
		for(Map<String, Object> temp : list) {
			MDataMap couponInfo = new PlusSupportFenxiao().getFenxiaoCouponInfo(productCode);
			String couponMoney = couponInfo.get("coupon_money");
			
			MDataMap mInsertMap = new MDataMap();
			mInsertMap.put("coupon_money", couponMoney);
			mInsertMap.put("activity_code", temp.get("activity_code")+"");
			mInsertMap.put("coupon_type_code", temp.get("coupon_type_code")+"");
			mInsertMap.put("produt_code", productCode);
			mInsertMap.put("sell_price", couponInfo.get("sell_price"));
			mInsertMap.put("cost_price", couponInfo.get("cost_price"));
			DbUp.upTable("oc_activity_agent_product").dataUpdate(mInsertMap, "coupon_money,cost_price,sell_price", "activity_code,coupon_type_code,produt_code");
			
			// 优惠金额不变则不更新
			if(new BigDecimal(couponMoney).compareTo(new BigDecimal(temp.get("coupon_money").toString())) == 0) {
				continue;
			}
			
			mInsertMap = new MDataMap();
			mInsertMap.put("money", couponMoney);
			mInsertMap.put("activity_code", temp.get("activity_code")+"");
			mInsertMap.put("coupon_type_code", temp.get("coupon_type_code")+"");
			DbUp.upTable("oc_coupon_type").dataUpdate(mInsertMap, "money", "activity_code,coupon_type_code");
		}
		
		new LoadCouponListForProduct().deleteInfoByCode(productCode);
		new LoadActivityAgent().refresh(new PlusModelQuery("SI2003"));		
	}
}
