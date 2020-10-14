package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.productcenter.model.GoodsProduct;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.load.LoadProductSales;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductSales;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class EventProductService {

	public List<GoodsProduct> getGoodsProduct(List<String> listEventCode){
		List<GoodsProduct> listPro = new ArrayList<GoodsProduct>();
		
		String sql="SELECT inf.*, con.product_code,con.favorable_price,con.purchase_num,con.purchase_num,con.selling_price,con.sku_code,"+
				   "con.sku_name FROM systemcenter.sc_event_conglobation con,systemcenter.sc_event_info inf WHERE con.event_code = inf.event_code "+
				   " AND con.event_code IN ('"+StringUtils.join(listEventCode,"','")+"') order by inf.end_time desc";
		
		List<Map<String, Object>> list = DbUp.upTable("sc_event_conglobation").dataSqlList(sql, new MDataMap());
		
		for(Map<String, Object> map : list ){
			PlusModelProductInfo productInfo = new LoadProductInfo().topInitInfo(new PlusModelProductQuery(map.get("product_code").toString()));
			PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(map.get("product_code").toString());		
			PlusModelProductSales productSalesValue = new LoadProductSales().upInfoByCode(plusModelProductQuery);
			GoodsProduct gp = new GoodsProduct();
			gp.setEventCode(map.get("event_code").toString());
			gp.setEndTime(map.get("end_time").toString());
			gp.setBeginTime(map.get("begin_time").toString());
			gp.setProductCode(map.get("product_code").toString());
			gp.setSkuCode(map.get("sku_code").toString());
			gp.setSkuName(map.get("sku_name")+"");
			gp.setSellingPrice(new BigDecimal(map.get("selling_price").toString()).setScale(2, RoundingMode.HALF_UP));
			gp.setFavorablePrice(new BigDecimal(map.get("favorable_price").toString()).setScale(2, RoundingMode.HALF_UP));
			gp.setPurchaseNum(Integer.parseInt(map.get("purchase_num").toString()));
			gp.setMainpicUrl(productInfo.getMainpicUrl());
			gp.setPcPicList(productInfo.getPcPicList());
			gp.setDescription(productInfo.getDescription());
			gp.setFictitionSales(productSalesValue.getFictitionSales30());
			listPro.add(gp);
			
		}
		
		return listPro;
	}
}
