package com.cmall.groupcenter.service;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.model.RsyncModelReturnGoods;
import com.cmall.groupcenter.homehas.model.RsyncModelShipmentStat;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class HomehasShipmentStatService {

	/**
	 * 保存配送状态日志
	 * 数据来源：配送状态同步接口
	 */
	public boolean insertCodStatLog(RsyncModelShipmentStat stat){
		if(StringUtils.isBlank(stat.getCod_stat_cd())){
			return false;
		}
		
		String orderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "", new MDataMap("out_order_code", stat.getOrd_id()));
		
		// 查询订单明细
		MDataMap codStatMap = DbUp.upTable("oc_order_ld_detail").oneWhere("zid,uid,cod_stat_cd", "", "", "orderform_num",stat.getOrd_id(),"ord_seq",stat.getOrd_seq());
		if(codStatMap == null){
			return false;
		}
		
		// 判断是否状态一致，如果不一致则更新一下状态并插入一下日志表
		if(!stat.getCod_stat_cd().equals(codStatMap.get("cod_stat_cd"))){
			MDataMap updateCodStatMap = new MDataMap();
			updateCodStatMap.put("zid", codStatMap.get("zid"));
			updateCodStatMap.put("uid", codStatMap.get("uid"));
			updateCodStatMap.put("cod_stat_cd", stat.getCod_stat_cd());
			updateCodStatMap.put("update_time", FormatHelper.upDateTime());
			DbUp.upTable("oc_order_ld_detail").update(updateCodStatMap);
			
			MDataMap insertMap = new MDataMap();
			insertMap.put("order_code", StringUtils.trimToEmpty(orderCode));
			insertMap.put("ld_order_code", stat.getOrd_id());
			insertMap.put("ord_seq", stat.getOrd_seq());
			insertMap.put("cod_stat_cd", stat.getCod_stat_cd());
			insertMap.put("stat_date", StringUtils.trimToEmpty(stat.getStat_date()));
			insertMap.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_order_cod_stat_log").dataInsert(insertMap);
		}
		
		return true;
	}
	
	/**
	 * 保存LD系统确认退货入库数据
	 * @param returnGoods
	 * @return
	 */
	public boolean insertReturnGoods(RsyncModelReturnGoods returnGoods){
		// 数据已经存在则忽略，确定唯一规则： 订单号+颜色+款式+确认入口时间
		int count = DbUp.upTable("oc_return_goods_detail_ld").count("ld_order_code",returnGoods.getOrd_id(),"color_id",returnGoods.getColor_id(),"style_id",returnGoods.getStyle_id(),"stat_date",returnGoods.getRtn_cnfm_date());
		if(count > 0){
			return false;
		}
		
		// 查询惠家有订单号
		String orderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "", new MDataMap("out_order_code",returnGoods.getOrd_id(),"small_seller_code","SI2003"));
		
		// 查询SKU编号
		String skuKey = "color_id="+returnGoods.getColor_id()+"&style_id="+returnGoods.getStyle_id();
		String skuCode = (String)DbUp.upTable("pc_skuinfo").dataGet("sku_code", "", new MDataMap("seller_code","SI2003","product_code",returnGoods.getGood_id(),"sku_key",skuKey));
		
		if(StringUtils.isBlank(orderCode)){
			return false;
		}
		
		MDataMap dataMap = new MDataMap();
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
		
		return true;
	}
}
