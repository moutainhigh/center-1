package com.cmall.groupcenter.homehas;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetRtnOrdDate;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelReturnGoods;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetRtnOrdDate;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetRtnOrdDate;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 4.65.惠家有TV品销退拒收退货相关信息同步接口
 */
public class RsyncGetRtnOrdDate extends RsyncHomeHas<RsyncConfigGetRtnOrdDate, RsyncRequestGetRtnOrdDate, RsyncResponseGetRtnOrdDate> {

	final static RsyncConfigGetRtnOrdDate CONFIG = new RsyncConfigGetRtnOrdDate();
	
	private RsyncRequestGetRtnOrdDate tRequest = new RsyncRequestGetRtnOrdDate();
	private RsyncResponseGetRtnOrdDate tResponse = new RsyncResponseGetRtnOrdDate();

	public RsyncConfigGetRtnOrdDate upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetRtnOrdDate upRsyncRequest() {
		// 返回输入参数
		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		tRequest.setStart_date(rsyncDateCheck.getStartDate());
		tRequest.setEnd_date(rsyncDateCheck.getEndDate());

		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetRtnOrdDate tRequest, RsyncResponseGetRtnOrdDate tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
			return result;
		}
		
		List<RsyncModelReturnGoods> itemList = tResponse.getResult();
		int processNum = 0;
		int successNum = 0;
		if(itemList != null){
			int count = 0;
			MDataMap dataMap;
			String orderCode;
			String skuCode;
			String skuKey;
			for(RsyncModelReturnGoods returnGoods : itemList){
				processNum++;
				// 数据已经存在则忽略，确定唯一规则： 订单号+颜色+款式+确认入口时间
				count = DbUp.upTable("oc_return_goods_detail_ld").count("ld_order_code",returnGoods.getOrd_id(),"color_id",returnGoods.getColor_id(),"style_id",returnGoods.getStyle_id(),"stat_date",returnGoods.getRtn_cnfm_date());
				if(count > 0){
					continue;
				}
				
				// 查询惠家有订单号
				orderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "", new MDataMap("out_order_code",returnGoods.getOrd_id(),"small_seller_code","SI2003"));
				
				// 查询SKU编号
				skuKey = "color_id="+returnGoods.getColor_id()+"&style_id="+returnGoods.getStyle_id();
				skuCode = (String)DbUp.upTable("pc_skuinfo").dataGet("sku_code", "", new MDataMap("seller_code","SI2003","product_code",returnGoods.getGood_id(),"sku_key",skuKey));
				
				if(StringUtils.isBlank(orderCode)){
					continue;
				}
				
				dataMap = new MDataMap();
				dataMap.put("order_code", orderCode);
				dataMap.put("ld_order_code", returnGoods.getOrd_id());
				dataMap.put("product_code", returnGoods.getGood_id());
				dataMap.put("sku_code", StringUtils.trimToEmpty(skuCode));
				dataMap.put("color_id", returnGoods.getColor_id());
				dataMap.put("style_id", returnGoods.getStyle_id());
				dataMap.put("rtn_qty", returnGoods.getRtn_qty());
				dataMap.put("cod_stat_cd", returnGoods.getCod_stat_cd());
				dataMap.put("stat_date", returnGoods.getRtn_cnfm_date());
				dataMap.put("create_time", FormatHelper.upDateTime());
				DbUp.upTable("oc_return_goods_detail_ld").dataInsert(dataMap);
				
				successNum++;
			}
		}
		
		result.setProcessNum(processNum);
		result.setSuccessNum(successNum);
		result.setStatusData(tRequest.getEnd_date());
		
		return result;
	}

	public RsyncResponseGetRtnOrdDate upResponseObject() {
		return tResponse;
	}

}
