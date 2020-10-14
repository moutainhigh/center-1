package com.cmall.productcenter.service.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.GoodsProduct;
import com.cmall.productcenter.model.api.ApiForEventGoodsProductInput;
import com.cmall.productcenter.model.api.ApiForEventGoodsProductResult;
import com.cmall.productcenter.service.EventProductService;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.load.LoadProductSales;
import com.srnpr.xmassystem.modelevent.PlusModelEventGoodsProduct;
import com.srnpr.xmassystem.modelevent.PlusModelGoodsProduct;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductSales;
import com.srnpr.xmassystem.service.PlusServiceGoodsProduct;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 拼好货接口
 * @author zhouguohui
 *
 */
public class ApiForEventGoodsProduct extends RootApiForManage<ApiForEventGoodsProductResult,ApiForEventGoodsProductInput> {

	public ApiForEventGoodsProductResult Process(
			ApiForEventGoodsProductInput inputParam, MDataMap mRequestMap) {
		ApiForEventGoodsProductResult result = new ApiForEventGoodsProductResult();
		List<GoodsProduct> listPro = new ArrayList<GoodsProduct>();
		List<String> listEventCode = inputParam.getListEventCode();
		/**如果用户查看**/
		if(listEventCode!=null && !listEventCode.isEmpty()){
			
			listPro = new EventProductService().getGoodsProduct(listEventCode);
			
			/*String sql="SELECT inf.*, con.product_code,con.favorable_price,con.purchase_num,con.purchase_num,con.selling_price,con.sku_code,"+
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
				
			}*/
			
			
	    /**查看单个拼好货基础数据**/		
		}else{
		
			PlusModelEventGoodsProduct pmegp = new PlusServiceGoodsProduct().getEventGoodsProduct(getManageCode());
			if(pmegp!=null){
				List<PlusModelGoodsProduct> listProduct = pmegp.getGoodsProduct();
				for(int j=0;j<listProduct.size();j++){
					PlusModelGoodsProduct pro = listProduct.get(j);
					//取单个商品的时间  也走缓存拿数据  只是过滤掉别的商品数据
					if(inputParam.getEventCoe()!=null&&!inputParam.getEventCoe().equals("")&&!inputParam.getEventCoe().equals(pro.getEventCode())){
							continue;
					}
					PlusModelProductInfo productInfo = new LoadProductInfo().topInitInfo(new PlusModelProductQuery(pro.getProductCode()));
					PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(pro.getProductCode());		
					PlusModelProductSales productSalesValue = new LoadProductSales().upInfoByCode(plusModelProductQuery);
					GoodsProduct gp = new GoodsProduct();
					gp.setEventCode(pro.getEventCode());
					gp.setEndTime(pro.getEndTime());
					gp.setBeginTime(pro.getBeginTime());
					gp.setProductCode(pro.getProductCode());
					gp.setSkuCode(pro.getSkuCode());
					gp.setSkuName(pro.getSkuName());
					gp.setSellingPrice(new BigDecimal(pro.getSellingPrice()).setScale(2, RoundingMode.HALF_UP));
					gp.setFavorablePrice(new BigDecimal(pro.getFavorablePrice()).setScale(2, RoundingMode.HALF_UP));
					gp.setPurchaseNum(pro.getPurchaseNum());
					gp.setMainpicUrl(productInfo.getMainpicUrl());
					gp.setPcPicList(productInfo.getPcPicList());
					gp.setDescription(productInfo.getDescription());
					gp.setFictitionSales(productSalesValue.getFictitionSales30());
					listPro.add(gp);
				}
			}
		}
		result.setListPro(listPro);
		return result;
	}
	

}
