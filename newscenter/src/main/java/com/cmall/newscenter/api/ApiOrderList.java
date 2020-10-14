
package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.ApiOrderListResult;
import com.cmall.newscenter.model.ApiOrderListInput;
import com.cmall.newscenter.model.ApiSellerListResult;
import com.cmall.newscenter.model.ApiSellerOrderListResult;
import com.cmall.newscenter.model.ApiSellerStandardAndStyleResult;
import com.cmall.newscenter.service.ProductCommentService;
import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;
import com.cmall.ordercenter.alipay.process.WechatProcessRequest;
import com.cmall.ordercenter.service.ApiAlipayMoveProcessService;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 订单列表接口
 * @author wz
 *
 */
public class ApiOrderList extends RootApiForToken<ApiOrderListResult, ApiOrderListInput>{

	public ApiOrderListResult Process(ApiOrderListInput inputParam,
			MDataMap mRequestMap) {
		OrderService orderService = new OrderService();
		ProductService productService = new ProductService();
		ProductCommentService productCommentService = new ProductCommentService();
		ApiAlipayMoveProcessService apiAlipayMoveProcessService = new ApiAlipayMoveProcessService();
		ApiOrderListResult apiOrderListResult = new ApiOrderListResult();
		
		inputParam.setBuyer_code(getUserCode());
		
		List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> orderSellerList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> sellerList = new ArrayList<Map<String, Object>>();
		List<MDataMap> flashSalesList = new ArrayList<MDataMap>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> orderSellerMap = new HashMap<String,Object>();
		Map<String,Object> sellerMap = new HashMap<String,Object>();
		Map<String,String> alipaySignMap = new HashMap<String,String>();
		
		List<ApiSellerOrderListResult> sellerOrderList = new ArrayList<ApiSellerOrderListResult>();
		//List<ApiSellerListResult> sellerResultList = new ArrayList<ApiSellerListResult>();
		List<PcPropertyinfoForFamily> standardAndStyleList  = new ArrayList<PcPropertyinfoForFamily>();
		
		String alipayValue = null;
		String orderCode = null;
		String sellerCode = null;
		int count = 0;
		int countPage = 0;
		int orderSellerNumberDouble = 0;
		
		if(!"".equals(inputParam.getBuyer_code()) 
				&& !"".equals(inputParam.getNextPage())){
			
			count = orderService.orderCount(inputParam.getBuyer_code(),inputParam.getOrder_status());   //统计订单总数
			if(count !=0){
				countPage = count/10;    //总页数
			}
			orderList = orderService.orderInformation(inputParam.getBuyer_code(), inputParam.getOrder_status(), inputParam.getNextPage(),getManageCode(),true);   //订单信息
			if(orderList!=null && !orderList.isEmpty()){
				for(int i=0; i<orderList.size(); i++){
					int orderSellerNumber = 0;
					List<ApiSellerListResult> sellerResultList = new ArrayList<ApiSellerListResult>();
					ApiSellerOrderListResult apiSellerOrderListResult = new ApiSellerOrderListResult();
					
					map = orderList.get(i);
					orderCode = map.get("order_code").toString();
					sellerCode = map.get("seller_code").toString();
					
					alipayValue = apiAlipayMoveProcessService.alipayMoveParameter(orderCode,true); //获取支付宝移动支付链接地址
					//alipaySignMap = apiAlipayMoveProcessService.alipaySign(orderCode);    //获取签名后的sign
					
					apiSellerOrderListResult.setOrder_status(map.get("order_status").toString());
					apiSellerOrderListResult.setOrder_code(orderCode);
					apiSellerOrderListResult.setCreate_time(map.get("create_time").toString());
					apiSellerOrderListResult.setDue_money(map.get("due_money").toString());
					
					
                    if(map.get("pay_type").toString().equals("449716200001")){
						
						apiSellerOrderListResult.setAlipayUrl("https://wappaygw.alipay.com/service/rest.htm?");
						
						apiSellerOrderListResult.setAlipaySign(alipayValue);
						
						apiSellerOrderListResult.setPayType(map.get("pay_type").toString());
						
					}else if(map.get("pay_type").toString().equals("449716200004")){
						
						RootResult rootResult = new RootResult();
						
						WechatProcessRequest  wechatprocress = new WechatProcessRequest();
						
						MDataMap mDataMap = wechatprocress.wechatMove(orderCode, inputParam.getBrowserUrl(), rootResult );
						
						if(mDataMap!=null){
							
							
							apiSellerOrderListResult.getMicoPayment().setAppid(mDataMap.get("appid"));
							
							apiSellerOrderListResult.getMicoPayment().setNonceStr(mDataMap.get("noncestr"));
							
							apiSellerOrderListResult.getMicoPayment().setPackageValue(mDataMap.get("package"));
							
							apiSellerOrderListResult.getMicoPayment().setPartnerid(mDataMap.get("partnerid"));
							
							apiSellerOrderListResult.getMicoPayment().setPrepayid(mDataMap.get("prepayid"));
							
							apiSellerOrderListResult.getMicoPayment().setSign(mDataMap.get("sign"));
							
							apiSellerOrderListResult.getMicoPayment().setTimeStamp(mDataMap.get("timestamp"));
							
							apiSellerOrderListResult.setPayType(map.get("pay_type").toString());
							
						}else {
							
							apiOrderListResult.setResultCode(rootResult.getResultCode());
							
							apiOrderListResult.setResultMessage(rootResult.getResultMessage());
							
						}
						
					}


					
					orderSellerList = orderService.orderSellerNumber(map);    //订单商品数量(每一个订单数量加一起的总数)  和  商品code、商品单价
					
					if(orderSellerList != null && !orderSellerList.isEmpty()){
						for(int j=0; j<orderSellerList.size(); j++){
							
							orderSellerMap = orderSellerList.get(j);
							flashSalesList = orderService.flashSales(orderSellerMap.get("sku_code").toString());     //判断是否为闪够信息
							
							orderSellerNumberDouble = Integer.parseInt(orderSellerMap.get("sku_num").toString());
							orderSellerNumber +=orderSellerNumberDouble;
							
							sellerList = orderService.sellerInformation(orderSellerMap.get("sku_code").toString());   //查询商品信息
							if(sellerList != null && !sellerList.isEmpty()){
								for(int k=0; k<sellerList.size(); k++){
									List<ApiSellerStandardAndStyleResult> apiSellerStandardAndStyleResultList  = new ArrayList<ApiSellerStandardAndStyleResult>();
									ApiSellerListResult apiSellerListResult = new ApiSellerListResult();
									sellerMap = sellerList.get(k);
									
									apiSellerListResult.setMainpic_url(sellerMap.get("sku_picurl").toString());
									apiSellerListResult.setProduct_code(sellerMap.get("sku_code").toString());
									apiSellerListResult.setProduct_name(sellerMap.get("sku_name").toString());
									apiSellerListResult.setProduct_number(String.valueOf(orderSellerNumberDouble));
									apiSellerListResult.setSell_price(orderSellerMap.get("sku_price").toString());
									if(!"".equals(sellerMap.get("product_shortname")) && sellerMap.get("product_shortname")!= null){
										apiSellerListResult.setProductShortName(sellerMap.get("product_shortname").toString());
									}
									
									//apiOrderSellerDetailsResult.setPromotionDescribe(""); //促销描述
									//apiOrderSellerDetailsResult.setPromotionType(""); //促销种类
									apiSellerListResult.setProductType(String.valueOf(productService.getSkuActivityTypeForOrder(sellerMap.get("sku_code").toString(), map.get("order_code").toString())));   //商品类型
									boolean isCommented = productCommentService.isCommented(map.get("order_code").toString(),map.get("order_code").toString(), sellerMap.get("sku_code").toString(), getManageCode());
									if(isCommented){
										apiSellerListResult.setIfEvaluate("true");
									}else{
										apiSellerListResult.setIfEvaluate("false");
									}

									standardAndStyleList = orderService.sellerStandardAndStyle(sellerMap.get("sku_keyvalue").toString());    //截取  尺码 和 款型
									if(standardAndStyleList !=null && !"".equals(standardAndStyleList)){
										for(PcPropertyinfoForFamily pcPropertyinfoForFamily : standardAndStyleList){
											ApiSellerStandardAndStyleResult apiSellerStandardAndStyleResult = new ApiSellerStandardAndStyleResult();
											apiSellerStandardAndStyleResult.setStandardAndStyleKey(pcPropertyinfoForFamily.getPropertyKey());
											apiSellerStandardAndStyleResult.setStandardAndStyleValue(pcPropertyinfoForFamily.getPropertyValue());
											apiSellerStandardAndStyleResultList.add(apiSellerStandardAndStyleResult);
										}
										apiSellerListResult.setStandardAndStyleList(apiSellerStandardAndStyleResultList);
									}
									
									apiSellerListResult.setProductType(String.valueOf(productService.getSkuActivityTypeForOrder(sellerMap.get("sku_code").toString(), orderCode)));   //商品类型
									
									sellerResultList.add(apiSellerListResult);
								}
								apiSellerOrderListResult.setApiSellerList(sellerResultList);
							}
							apiSellerOrderListResult.setOrderStatusNumber(orderSellerNumber);
							
						}
					}
					
					if(flashSalesList != null && !flashSalesList.isEmpty()){
						apiSellerOrderListResult.setIfFlashSales("0");   //闪购
					}else{
						apiSellerOrderListResult.setIfFlashSales("1");   //非闪购
					}
					sellerOrderList.add(apiSellerOrderListResult);
				}
			}
			apiOrderListResult.setCountPage(countPage);
			apiOrderListResult.setNowPage(Integer.parseInt(inputParam.getNextPage()));
			apiOrderListResult.setSellerOrderList(sellerOrderList);
			return apiOrderListResult;
		}
		//https://wappaygw.alipay.com/service/rest.htm
		
		return null;
    }

}
