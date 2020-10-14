package com.cmall.groupcenter.hserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.hserver.model.FuncProductStatusRequest;
import com.cmall.groupcenter.hserver.model.ProductStatus;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webclass.WarnCount;

/**
 * 商品状态变更推送处理逻辑
 * 
 * @author zhaojunling
 */
public class FuncProductStatus implements IAcceptFunc<FuncProductStatusRequest> {

	
	@Override
	public AcceptResult doProcess(FuncProductStatusRequest request) {
		AcceptResult acceptResult = new AcceptResult();
		
		List<ProductStatus> productStatusList = request.getResults();
		if(productStatusList == null || productStatusList.isEmpty()) return acceptResult;
		
		MDataMap statusLog = null;
		MDataMap productMap = null;
		MDataMap skuMap = null;
		List<ProductStatus> itemList = filterProductStatusList(productStatusList);
		
		// 商品分组，多SKU的情况等全部SKU更新完后再判断商品上下架状态
		Map<String,List<ProductStatus>> productStatusMap = new HashMap<String, List<ProductStatus>>();
		for(ProductStatus p : itemList) {
			if(!productStatusMap.containsKey(p.getGoodId())) {
				productStatusMap.put(p.getGoodId(), new ArrayList<ProductStatus>());
			}
			
			productStatusMap.get(p.getGoodId()).add(p);
		}
		
		for(Entry<String, List<ProductStatus>> entry : productStatusMap.entrySet()) {
			// 商品编号
			String productCode = entry.getKey();
			// 更新标识
			boolean updateFlag = false;
			// 可以代表商品上下架状态的SKU对象
			ProductStatus skuStatus = null;
			for(ProductStatus status : entry.getValue()){
				skuMap = DbUp.upTable("pc_skuinfo").oneWhere("", "", "", "product_code", productCode,"sku_key",String.format("color_id=%s&style_id=%s", status.getColorId(),status.getStyleId()));
				
				//===========兼容家有汇商品==========
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("product_code", "9"+productCode);
				mDataMap.put("sku_key", String.format("color_id=%s&style_id=%s", status.getColorId(),status.getStyleId()));
				mDataMap.put("sale_yn", status.getSaleYn());
				DbUp.upTable("pc_skuinfo").dataUpdate(mDataMap, "sale_yn", "product_code,sku_key");
				//===========兼容家有汇商品==========
				
				// 状态一致的则不更新
				if(skuMap == null || skuMap.get("sale_yn").equalsIgnoreCase(status.getSaleYn())) {
					continue;
				}
				
				updateFlag = true;
				skuMap.put("sale_yn", status.getSaleYn());
				DbUp.upTable("pc_skuinfo").dataUpdate(skuMap, "sale_yn", "zid");

				statusLog = new MDataMap();
				statusLog.put("sku_code", skuMap.get("sku_code"));
				statusLog.put("product_code", productCode);
				statusLog.put("seller_code", "SI2003");
				statusLog.put("good_id", status.getGoodId());
				statusLog.put("color_id", status.getColorId());
				statusLog.put("style_id", status.getStyleId());
				statusLog.put("site_no", "");
				statusLog.put("sale_yn", status.getSaleYn());
				statusLog.put("change_cd", status.getChangeCd());
				statusLog.put("flag_success", "1");
				statusLog.put("remark", "");
				statusLog.put("create_time", FormatHelper.upDateTime());
				DbUp.upTable("lc_accept_skustatus_log").dataInsert(statusLog);
				
				// 如果有可售的SKU则优先取可售SKU
				if(skuStatus == null || "Y".equals(status.getSaleYn())) {
					skuStatus = status;
				}
			}
			
			productMap = DbUp.upTable("pc_productinfo").oneWhere("", "", "", "product_code", productCode);
			if(productMap == null) {
				continue;
			}
			
			// 更新商品缓存
			if(updateFlag) {
				// 更新商品状态
				updateStatus(productMap, null, skuStatus);
				//商品信息更新成功，开始刷新缓存
				PlusHelperNotice.onChangeProductInfo(productMap.get("product_code"));
				//触发消息队列
				new ProductJmsSupport().onChangeForProductChangeAll(productMap.get("product_code"));
			}
		}

		return acceptResult;
	}
	
	private int updateStatus(MDataMap productMap,MDataMap skuMap, ProductStatus status){
		// 可售状态同数据库相同时无操作
//		if(skuMap.get("sale_yn").equalsIgnoreCase(status.getSaleYn())){
//			return 1001;
//		}
		
		// 更新SKU可售状态
//		skuMap.put("sale_yn", status.getSaleYn().toUpperCase());
//		DbUp.upTable("pc_skuinfo").dataUpdate(skuMap, "sale_yn", "sku_code");
		//通知多彩宝系统 2018-08-07 by zhouenzhi
//		PushSkuStatusService service = new PushSkuStatusService();
//		if("N".equals(skuMap.get("sale_yn"))){
//			service.pushSkuStatus(skuMap.get("sku_code"), "N", 1, "LD品自动下架");
//		}
		/*// 有可售SKU时，发送商品上架通知
		if("Y".equalsIgnoreCase(status.getSaleYn()) && "4497153900060003".equals(productMap.get("product_status")) && "system".equals(productMap.get("poffer"))){
			//发邮件
//			sendMail(skuMap.get("sale_yn"), productMap.get("product_name"), productMap.get("product_code"));
			//发微信
			sendWx(skuMap.get("sale_yn"), productMap.get("product_name"), productMap.get("product_code"));
		}*/
		// 是否自动上架
		String auto_sell = productMap.get("auto_sell");
		// 有可售SKU时，发送商品上架通知
		if("Y".equalsIgnoreCase(status.getSaleYn()) && "4497153900060003".equals(productMap.get("product_status"))){//商品可售,且商品未上架的情况下
			if("449748400001".equals(auto_sell)) { 
				// 如果允许自动上架,则调用上架接口直接上架商品
				String flowBussinessUid= productMap.get("uid");
				String fromStatus= "4497153900060003";
				String toStatus = "4497153900060002";
				String flowType = "449715390006";
				String userCode = "jobsystem";
				String remark="有可售SKU，系统自动上架！";
				new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
			}else {
				// 不能自动上架,走原有功能
				if("system".equals(productMap.get("poffer"))){
					//发微信
					sendWx("Y", productMap.get("product_name"), productMap.get("product_code"));
				}
			}
		}
		
		// 无可售SKU时，对商品下架
		if("N".equalsIgnoreCase(status.getSaleYn()) && "4497153900060002".equals(productMap.get("product_status"))){
			if(DbUp.upTable("pc_skuinfo").count("product_code",productMap.get("product_code"),"sale_yn","Y")<1){
				String flowBussinessUid= productMap.get("uid");
				String fromStatus= "4497153900060002";
				String toStatus = "4497153900060003";
				String flowType = "449715390006";
				String userCode = "jobsystem";
				String remark="所有SKU均不可售，系统自动下架！";
				new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
				
				DbUp.upTable("pc_productinfo_ext").dataUpdate(new MDataMap("product_code",productMap.get("product_code"),"poffer","system"), "poffer", "product_code");
				
				//发邮件
//				sendMail(skuMap.get("sale_yn"), productMap.get("product_name"), productMap.get("product_code"));
				//发微信
				sendWx("N", productMap.get("product_name"), productMap.get("product_code"));
			}
		}
		
		return 1;
	}
	
	/**
	 * 把各仓库可售状态过滤一遍，计算出SKU最终可售状态
	 */
	private List<ProductStatus> filterProductStatusList(List<ProductStatus> productStatusList){
		// 过滤后的列表
		List<ProductStatus> itemList = new ArrayList<ProductStatus>();
		String key = null;

		Map<String,ProductStatus> filterMap = new HashMap<String, ProductStatus>();
		for(ProductStatus status : productStatusList){
			key = String.format("%s-%s-%s", status.getGoodId(), status.getColorId(),status.getStyleId());
			if(filterMap.containsKey(key)){
				// 有任意可售的仓库则设置SKU为可售
				if("Y".equals(status.getSaleYn())){
					filterMap.get(key).setSaleYn("Y");
				}
			}else{
				filterMap.put(key, status);
				itemList.add(status);
			}
		}
		
		return itemList;
	}
	
	private void sendMail(String sale_yn,String product_name,String product_code){
		
		String receives[]= TopUp.upConfig("groupcenter.offPro_sendMail_receives_"+sale_yn).split(",");
		String title= TopUp.upConfig("groupcenter.offPro_sendMail_title_"+sale_yn);
		String content= TopUp.upConfig("groupcenter.offPro_sendMail_content_"+sale_yn);
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, FormatHelper.formatString(title,product_code,product_name), FormatHelper.formatString(content,product_code,product_name));
			}
		}
	}
	
	private void sendWx(String sale_yn,String product_name,String product_code){
		
		String receices[] = TopUp.upConfig("groupcenter.offPro_sendWx_receives_"+sale_yn).split(",");
		String content = TopUp.upConfig("groupcenter.offPro_sendWx_content_"+sale_yn);
		
		for (String receive : receices) {
			if(StringUtils.isNotBlank(receive)){
				WarnCount count = new WarnCount();
				count.sendWx(receive , FormatHelper.formatString(content,product_code,product_name));
			}
		}
		
	}	
}