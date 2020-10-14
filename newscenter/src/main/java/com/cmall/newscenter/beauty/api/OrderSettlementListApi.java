package com.cmall.newscenter.beauty.api;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aj.org.objectweb.asm.Type;

import com.cmall.newscenter.beauty.model.OrderSettlementInput;
import com.cmall.newscenter.beauty.model.OrderSettlementResult;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;


/**
 * 订单预结算接口
 * @author houwen
 *  2014-10-13
 */
public class OrderSettlementListApi extends RootApiForManage<OrderSettlementResult, OrderSettlementInput>{

	public OrderSettlementResult Process(OrderSettlementInput inputParam,
			MDataMap mRequestMap) {
		
		OrderSettlementResult result = new OrderSettlementResult();

		ProductService productService = new ProductService();
		StoreService service = new StoreService();
		
		List<Map<String, Object>> map = new ArrayList<Map<String,Object>>() ;
		
		boolean flag = false;
		int size = inputParam.getPurchaseGoods().size();
		int num = 0; //库存量
		int count = 0;//商品数量
		String sku_code = null ;
		String skuCode = "";
		BigDecimal order_money  ;//传入的订单金额
		BigDecimal orderMoney = new BigDecimal(0.00) ;  //实时查到的订单金额
		BigDecimal postage = new BigDecimal(0.00);
		boolean flagCode = true ;
		List<Map<String,Object>> skuListMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> MapList = new HashMap<String, Object>();
		 DecimalFormat   df   =   new   DecimalFormat( "########0.00 "); 
		for(int i =0;i<size;i++){
			
			sku_code = inputParam.getPurchaseGoods().get(i).getSku_code();
			if(AppConst.MANAGE_CODE_CAPP.equals(getManageCode())){
				num  = service.getStockNumByMaxFor7(sku_code);
			}else if(AppConst.MANAGE_CODE_CYOUNG.equals(getManageCode())){
				num  = service.getStockNumByMaxFor13(sku_code);
			}
			
			count = Integer.valueOf(inputParam.getPurchaseGoods().get(i).getOrder_count());
			if (count>num){  //如果库存不足
				
				flagCode = false;
				skuCode = skuCode + sku_code + ",";
				
			}
			    Map<String,Object> mapString = new HashMap<String,Object>();
				mapString.put("skuCode",inputParam.getPurchaseGoods().get(i).getSku_code());
				mapString.put("count", inputParam.getPurchaseGoods().get(i).getOrder_count());
				mapString.put("appCode", getManageCode());
				map.add(mapString);
		}
		
		if(!flagCode){
			result.setResultCode(941901003); //订单库存不足
			skuCode = skuCode.substring(0, skuCode.length()-1);
			result.setResultMessage(skuCode);
		}else {
	      
			if(inputParam.getType().equals("2")){
				orderMoney = BigDecimal.valueOf(Double.parseDouble("0"));
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				list = productService.getMyTryOutGoodsForSkuCode(sku_code, "449746930002","", getManageCode(),null);
				if(list.size()!=0){
					PcFreeTryOutGood pcFreeTryOutGood = (PcFreeTryOutGood) list.get(0).get("freeGood");
					
					postage = pcFreeTryOutGood.getPostage(); //付邮试用运费为10
				}else {
					postage = new BigDecimal(10);
				}
				
			}else {
				MapList = productService.getSkuTotalManey(map);  //不包括运费
				orderMoney = BigDecimal.valueOf(Double.parseDouble(MapList.get("totalMoney").toString()));
			}
			order_money = BigDecimal.valueOf(Double.parseDouble(inputParam.getOrder_money()));
			int sum = order_money.compareTo(orderMoney);
			if(sum!=0){  //订单金额不相等
				result.setResultCode(3);
				result.setResultMessage("提交订单金额与查出订单金额不相等");
				
			}
		
			if(inputParam.getType().equals("2")){
					flag = true;
				}else{
				skuListMap = (List<Map<String, Object>>) MapList.get("skuList");
				for(int j = 0;j<skuListMap.size();j++){
					String isflag = skuListMap.get(j).get("isActivitySku").toString();
					if(isflag.equals("1")){
						flag = true;
						break;
					}
				}
			}
				
			
			if(flag){
				result.setPrompt("请在15分钟内完成付款，否则系统将自动取消该订单。");
			}else {
				result.setPrompt("请在24小时内完成付款，否则系统将自动取消该订单。");
			}
		    result.setPostage(df.format(postage));
		    result.setOrder_money(df.format(orderMoney));
			
		}
		
		return result;
	}

}
