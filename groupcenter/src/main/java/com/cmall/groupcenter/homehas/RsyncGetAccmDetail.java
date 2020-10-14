package com.cmall.groupcenter.homehas;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetAccmDetail;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelAccmDetail;
import com.cmall.groupcenter.homehas.model.RsyncModelAccmDetailCancel;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetAccmDetail;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetAccmDetail;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 4.66.积分变化明细(惠家有通路)同步接口
 */
public class RsyncGetAccmDetail extends RsyncHomeHas<RsyncConfigGetAccmDetail, RsyncRequestGetAccmDetail, RsyncResponseGetAccmDetail> {

	final static RsyncConfigGetAccmDetail CONFIG = new RsyncConfigGetAccmDetail();
	
	private RsyncRequestGetAccmDetail tRequest = new RsyncRequestGetAccmDetail();
	private RsyncResponseGetAccmDetail tResponse = new RsyncResponseGetAccmDetail();

	public RsyncConfigGetAccmDetail upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetAccmDetail upRsyncRequest() {
		// 返回输入参数
		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		tRequest.setStart_date(rsyncDateCheck.getStartDate());
		tRequest.setEnd_date(rsyncDateCheck.getEndDate());
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetAccmDetail tRequest, RsyncResponseGetAccmDetail tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
			return result;
		}
		
		List<RsyncModelAccmDetail> itemList = tResponse.getResult1();
		int processNum = 0;
		int successNum = 0;
		if(itemList != null){
			int count = 0;
			MDataMap dataMap;
			String orderCode;
			String skuCode;
			String skuKey;
			for(RsyncModelAccmDetail accmDetail : itemList){
				processNum++;
				// 查询惠家有订单号
				orderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "", new MDataMap("out_order_code",accmDetail.getOrd_id(),"small_seller_code","SI2003"));
				
				// 查询SKU编号
				skuKey = "color_id="+accmDetail.getColor_id()+"&style_id="+accmDetail.getStyle_id();
				skuCode = (String)DbUp.upTable("pc_skuinfo").dataGet("sku_code", "", new MDataMap("seller_code","SI2003","product_code",accmDetail.getGood_id(),"sku_key",skuKey));
				
				dataMap = new MDataMap();
				dataMap.put("accm_id", accmDetail.getAccm_id());
				dataMap.put("order_code", StringUtils.trimToEmpty(orderCode));
				dataMap.put("ld_order_code", accmDetail.getOrd_id());
				dataMap.put("ord_seq", accmDetail.getOrd_seq());
				dataMap.put("product_code", accmDetail.getGood_id());
				dataMap.put("sku_code", StringUtils.trimToEmpty(skuCode));
				dataMap.put("color_id", accmDetail.getColor_id());
				dataMap.put("style_id", accmDetail.getStyle_id());
				dataMap.put("sku_num", "1"); // 家有系统里面每个订单序号对应的数量都是1
				dataMap.put("accm_rsn_cd", accmDetail.getAccm_rsn_cd());
				dataMap.put("accm_amt", accmDetail.getAccm_amt());
				dataMap.put("cnfm_date", accmDetail.getCnfm_date());
				dataMap.put("create_time", FormatHelper.upDateTime());
				dataMap.put("update_time", dataMap.get("create_time"));
				
				count = DbUp.upTable("oc_order_accm_detail_ld").count("accm_id",accmDetail.getAccm_id());
				if(count == 0){ // 新增
					DbUp.upTable("oc_order_accm_detail_ld").dataInsert(dataMap);
				}else{ // 已存在则更新
					dataMap.remove("create_time");
					dataMap.remove("accm_amt"); // 不更新金额，LD系统取消退货时会更新原数据的金额为0此处仍取原值
					DbUp.upTable("oc_order_accm_detail_ld").dataUpdate(dataMap, "", "accm_id");
				}
				
				successNum++;
			}
		}
		
		// 如果有取消退货则需要更新一下取消时间
		List<RsyncModelAccmDetailCancel> cancelItemList = tResponse.getResult2();
		if(cancelItemList != null){
			String sql = "update oc_order_accm_detail_ld set cancel_time = :cancel_time where ld_order_code = :ld_order_code and ord_seq = :ord_seq and cnfm_date < :cancel_time and cancel_time = '' and accm_rsn_cd = '70'";
			MDataMap updateMap = new MDataMap();
			for(RsyncModelAccmDetailCancel calcelItem : cancelItemList){
				updateMap.put("cancel_time", calcelItem.getRtn_cnfm_date());
				updateMap.put("ld_order_code", calcelItem.getOrd_id());
				updateMap.put("ord_seq", calcelItem.getOrd_seq());
				DbUp.upTable("oc_order_accm_detail_ld").dataExec(sql, updateMap);
			}
		}
		
		result.setProcessNum(processNum);
		result.setSuccessNum(successNum);
		result.setStatusData(tRequest.getEnd_date());
		
		return result;
	}

	public RsyncResponseGetAccmDetail upResponseObject() {
		return tResponse;
	}

}
