package com.cmall.newscenter.api;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.ApiOrderDetailsInput;
import com.cmall.newscenter.model.ApiOrderDetailsResult;
import com.cmall.newscenter.model.ApiOrderDonationDetailsResult;
import com.cmall.newscenter.model.ApiOrderSellerDetailsResult;
import com.cmall.newscenter.model.ApiSellerStandardAndStyleResult;
import com.cmall.newscenter.model.InvoiceInformationResult;
import com.cmall.newscenter.service.ProductCommentService;
import com.cmall.ordercenter.alipay.process.WechatProcessRequest;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.ApiAlipayMoveProcessService;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 订单详情(惠美丽)
 * @author wz
 *
 */
public class ApiOrderDetails extends RootApiForToken<ApiOrderDetailsResult, ApiOrderDetailsInput>{

	public ApiOrderDetailsResult Process(ApiOrderDetailsInput inputParam,
			MDataMap mRequestMap) {
		ApiOrderDetailsResult apiOrderDetailsResult = new ApiOrderDetailsResult();
		InvoiceInformationResult invoiceInformationResult = new InvoiceInformationResult();
		inputParam.setBuyer_code(getUserCode());
		
		Order order = new Order();
		OrderService orderService = new  OrderService();
		ProductService productService = new ProductService();
		ProductCommentService productCommentService = new ProductCommentService();
		
		ApiAlipayMoveProcessService apiAlipayMoveProcessService = new ApiAlipayMoveProcessService();
		String alipayValue = null;
		
		List<MDataMap> list = new ArrayList<MDataMap>();
		List<Map<String, Object>> sellerList = new ArrayList<Map<String, Object>>();
		List<PcPropertyinfoForFamily> standardAndStyleList  = new ArrayList<PcPropertyinfoForFamily>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		int  deleteFlagCount = orderService.orderCountDeleteFlag(inputParam.getOrder_code());  //判断此订单是否为以删除订单
		
		if(deleteFlagCount != 0){    //不等于0  为    未删除订单
			order = orderService.getOrder(inputParam.getOrder_code());    //获取与订单相关内容的信息
			if(order !=null&&getUserCode().equals(order.getBuyerCode())){
				
				if(BigDecimal.ZERO.compareTo(BigDecimal.valueOf(Double.valueOf(order.getDueMoney().toString())))==0){
					
				}else {
					/*判断是微信还是支付宝支付*/
					if(order.getPayType().equals("449716200001")){
						
						alipayValue = apiAlipayMoveProcessService.alipayMoveParameter(order.getOrderCode(),true); //获取支付宝移动支付参数
						apiOrderDetailsResult.setAlipayUrl("https://wappaygw.alipay.com/service/rest.htm?");
						apiOrderDetailsResult.setAlipaySign(alipayValue);
						
					}else if(order.getPayType().equals("449716200004")){
						
						RootResult rootResult = new RootResult();
						
						WechatProcessRequest  wechatprocress = new WechatProcessRequest();
						
						MDataMap mDataMap = wechatprocress.wechatMove(inputParam.getOrder_code(), inputParam.getBrowserUrl(), rootResult );
						
						if(mDataMap!=null){
							
							
							apiOrderDetailsResult.getMicoPayment().setAppid(mDataMap.get("appid"));
							
							apiOrderDetailsResult.getMicoPayment().setNonceStr(mDataMap.get("noncestr"));
							
							apiOrderDetailsResult.getMicoPayment().setPackageValue(mDataMap.get("package"));
							
							apiOrderDetailsResult.getMicoPayment().setPartnerid(mDataMap.get("partnerid"));
							
							apiOrderDetailsResult.getMicoPayment().setPrepayid(mDataMap.get("prepayid"));
							
							apiOrderDetailsResult.getMicoPayment().setSign(mDataMap.get("sign"));
							
							apiOrderDetailsResult.getMicoPayment().setTimeStamp(mDataMap.get("timestamp"));
							
							
						}else {
							
							apiOrderDetailsResult.setResultCode(rootResult.getResultCode());
							
							apiOrderDetailsResult.setResultMessage(rootResult.getResultMessage());
							
						}
						
					}
				}
				
				
				if(order.getAddress() !=null && !"".equals(order.getAddress())){
					apiOrderDetailsResult.setConsigneeAddress(order.getAddress().getAddress());
					apiOrderDetailsResult.setConsigneeName(order.getAddress().getReceivePerson());
					apiOrderDetailsResult.setConsigneeTelephone(order.getAddress().getMobilephone());
					invoiceInformationResult.setInvoiceInformationTitle(order.getAddress().getInvoiceTitle());
					invoiceInformationResult.setInvoiceInformationType(order.getAddress().getInvoiceType());
					invoiceInformationResult.setInvoiceInformationValue(order.getAddress().getInvoiceContent());
					
					apiOrderDetailsResult.setInvoiceInformation(invoiceInformationResult);
				} 
				apiOrderDetailsResult.setCreate_time(order.getCreateTime());
				
				apiOrderDetailsResult.setDue_money(
						new BigDecimal(order.getDueMoney().doubleValue()
								).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
						);
				apiOrderDetailsResult.setFirstFavorable("");  //不知道怎么获取值(收单优惠)
				
				if(!"null".equals(String.valueOf(order.getTransportMoney()))){
					apiOrderDetailsResult.setFreight(Double.parseDouble(String.valueOf(order.getTransportMoney())));   //运费目前是"0"
				}
				
				apiOrderDetailsResult.setFullSubtraction(0.00); //满减目前是"0"
				apiOrderDetailsResult.setTelephoneSubtraction(0.00); //手机下单减少目前为"0"
				
				apiOrderDetailsResult.setOrder_code(order.getOrderCode());
				apiOrderDetailsResult.setOrder_money(order.getOrderMoney().toString());
				apiOrderDetailsResult.setOrder_status(order.getOrderStatus());
				apiOrderDetailsResult.setPay_type(order.getPayType());
				apiOrderDetailsResult.setRemark(order.getAddress().getRemark());
				
				
				//判断是否是闪购
				List<ApiOrderSellerDetailsResult> orderSellerList = new ArrayList<ApiOrderSellerDetailsResult>();
				
				for(OrderDetail orderDetail : order.getProductList()){
					list = orderService.flashSales(orderDetail.getSkuCode());     //存在为闪购信息
					sellerList = orderService.sellerInformation(orderDetail.getSkuCode());   //查询商品信息
					if(sellerList != null && !sellerList.isEmpty()){
						
						for(int i=0; i<sellerList.size(); i++){
							ApiOrderSellerDetailsResult apiOrderSellerDetailsResult = new  ApiOrderSellerDetailsResult();
							List<ApiOrderDonationDetailsResult> apiOrderDonationDetailsResultList = new ArrayList<ApiOrderDonationDetailsResult>();
							List<ApiSellerStandardAndStyleResult> apiSellerStandardAndStyleResultList  = new ArrayList<ApiSellerStandardAndStyleResult>();
							
							map = sellerList.get(i);
							apiOrderSellerDetailsResult.setMainpicUrl(map.get("sku_picurl").toString());
							apiOrderSellerDetailsResult.setNumber(String.valueOf(orderDetail.getSkuNum()));
							apiOrderSellerDetailsResult.setPrice(
									new BigDecimal(orderDetail.getSkuPrice().doubleValue()
											).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							apiOrderSellerDetailsResult.setProductCode(map.get("sku_code").toString());  
							apiOrderSellerDetailsResult.setProductName(map.get("sku_name").toString());
							if(!"".equals(map.get("product_shortname")) && map.get("product_shortname")!= null){
								apiOrderSellerDetailsResult.setProductShortName(map.get("product_shortname").toString());
							}
							
							//apiOrderSellerDetailsResult.setPromotionDescribe(""); //促销描述
							//apiOrderSellerDetailsResult.setPromotionType(""); //促销种类
							apiOrderSellerDetailsResult.setRegion(orderDetail.getStoreCode());
							int activity = productService.getSkuActivityType(map.get("sku_code").toString(),getManageCode()); //商品类型
							apiOrderSellerDetailsResult.setProductType(String.valueOf(activity));   //商品类型
							
							if(activity==2){   //试用商品
								if(order.getOrderType().equals("449715200003"))
								{
									apiOrderDetailsResult.setOrder_money("0.0");
								}
							}
							//订单详情的试用商品按普通商品展示
							if(activity==2){
								apiOrderSellerDetailsResult.setProductType("0");
							}
							
							//如果是试用商品   查出结束时间
							MDataMap whereMap = new MDataMap();
							whereMap.put("sku_code",map.get("sku_code").toString());
							whereMap.put("app_code",getManageCode());
							List<MDataMap> tryouytlist =DbUp.upTable("oc_tryout_products").queryAll("end_time","","", whereMap);
							if(tryouytlist!=null && !"".equals(tryouytlist) && tryouytlist.size()!=0){  
								apiOrderSellerDetailsResult.setEnd_time(tryouytlist.get(0).get("end_time"));
							}else{
								apiOrderSellerDetailsResult.setEnd_time("");
							}
							
							boolean isCommented = productCommentService.isCommented(inputParam.getOrder_code(),getUserCode(), map.get("sku_code").toString(), getManageCode());
							if(isCommented){
								apiOrderSellerDetailsResult.setIfEvaluate("true");
							}else{
								apiOrderSellerDetailsResult.setIfEvaluate("false");
							}
							
							standardAndStyleList = orderService.sellerStandardAndStyle(map.get("sku_keyvalue").toString());    //截取  尺码 和 款型
							if(standardAndStyleList !=null && !"".equals(standardAndStyleList)){
								for(PcPropertyinfoForFamily pcPropertyinfoForFamily : standardAndStyleList){
									ApiSellerStandardAndStyleResult apiSellerStandardAndStyleResult = new ApiSellerStandardAndStyleResult();
									apiSellerStandardAndStyleResult.setStandardAndStyleKey(pcPropertyinfoForFamily.getPropertyKey());
									apiSellerStandardAndStyleResult.setStandardAndStyleValue(pcPropertyinfoForFamily.getPropertyValue());
									apiSellerStandardAndStyleResultList.add(apiSellerStandardAndStyleResult);
								}
								apiOrderSellerDetailsResult.setStandardAndStyleList(apiSellerStandardAndStyleResultList);
							}
							
							orderSellerList.add(apiOrderSellerDetailsResult);
							
							apiOrderSellerDetailsResult.setDetailsList(apiOrderDonationDetailsResultList);   //赠品信息(目前为空)
						}
						apiOrderDetailsResult.setOrderSellerList(orderSellerList);
					}
					
				}
				if(list !=null && !list.isEmpty()){
					apiOrderDetailsResult.setIfFlashSales("0");
					apiOrderDetailsResult.setFailureTimeReminder("15分钟后失效!");
				}else{
					apiOrderDetailsResult.setIfFlashSales("1");
					apiOrderDetailsResult.setFailureTimeReminder("提示:下单成功后24小时内不付款，系统将自动取消订单!");
				}
				
			}
		}
		return apiOrderDetailsResult;
	}

}
