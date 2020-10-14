package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.RsyncAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder.CouponInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder.Goods;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.FlashsalesSkuInfoService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 本地订单与E店宝订单相关操作
 * @author syz
 *
 */
public class BeautifulOrderService extends BaseClass{

	/**
	 * 创建订单完成时，同步本地订单到家有
	 * @param list
	 */
	public boolean rsyncOrder(String orderCode,String mobileid){
			RsyncAddOrder addOrder=new RsyncAddOrder();
			FlashsalesSkuInfoService flashsalesSkuInfoService = new FlashsalesSkuInfoService();
			
			com.cmall.ordercenter.service.OrderService service=new com.cmall.ordercenter.service.OrderService();
			Order order=service.getOrder(orderCode);
			RsyncRequestAddOrder request = addOrder.upRsyncRequest();
			List<OrderDetail> detailList=order.getProductList();//订单详情
			OrderAddress address =order.getAddress();//地址信息
			List<Goods> good_info = request.getGood_info();
			List<Goods> good_info_gift = request.getGift_good_info();
			String outer_activity_code_="0";//外部活动编号
			List<Long> lj_good_id=request.getLj_good_id();
			String coupon_type= "" ;//目前所有业务均为折扣立减，所有统一设置为3 若遇到其他业务，需要前台传值
			
			Map<String, Map<String, Object>> activeMap = null;
			//查询订单所有的活动
			List<Map<String, Object>> activeList=DbUp.upTable("oc_order_activity").dataSqlList("SELECT sku_code,activity_code,preferential_money from oc_order_activity where order_code =:order_code ", new MDataMap("order_code",orderCode));
			if(activeList!=null&&activeList.size()>0){
				activeMap = new HashMap<String, Map<String,Object>>(activeList.size()); 
				for (Map<String, Object> map : activeList) {
					activeMap.put((String)map.get("sku_code"), map);
				}
				coupon_type="2";
			}
			
			for (OrderDetail orderDetail : detailList) {
				
				String sku_code = orderDetail.getSkuCode();//查询sku信息
				Map<String, Object> skuMap=DbUp.upTable("pc_skuinfo").dataSqlOne("select sku_key,sell_productcode,sell_price from pc_skuinfo where sku_code=:sku_code ", new MDataMap("sku_code",sku_code));
				if(skuMap==null){
					continue;
				}
				String sku_key= (String)skuMap.get("sku_key");
				long good_id= Long.valueOf((String)skuMap.get("sell_productcode"));
				String color_id="";
				String style_id=""; 
				if(!"".equals(trim(sku_key))){
					String [] ss=sku_key.split("&");
					for (String s : ss) {
						if(s.contains("color_id=")){
							color_id=s.replace("color_id=", "");
						}
						if(s.contains("style_id=")){
							style_id=s.replace("style_id=", "");
						}
					}
				}
				
				String save_amt="0";
				String outer_activity_code="0";
				BigDecimal vip_price= null; 
				String gift_active_code="";//赠品活动编号
				boolean flag88=false;//首单88
				
				//查看活动
				if(activeMap!=null){
					Map<String, Object> map = activeMap.get(sku_code);
					if(map!=null){
						save_amt=String.valueOf(map.get("preferential_money")); //优惠金额
						String activity_code=(String)map.get("activity_code");//内部活动编号
//						
						if(activity_code.equals(bConfig("familyhas.firstActivity"))){ //首单 88 折的判断
							flag88 = true;
						}
						
						gift_active_code=activity_code;
						//查询外部编号
						if(!flag88){
							outer_activity_code=(String)DbUp.upTable("oc_activity_flashsales").dataGet("outer_activity_code", "activity_code=:activity_code", new MDataMap("activity_code",activity_code));
						}
						
						lj_good_id.add(good_id);
						
						RootResult rootResult= new RootResult();
						try {
							vip_price=flashsalesSkuInfoService.getVipPrice(activity_code, sku_code, rootResult);
						} catch (Exception e) {
							
						}
					}
				}
				
				//订单信息中的外部活动编号 取第一个即可
				if("".equals(outer_activity_code_)&&!"".equals(outer_activity_code)) {
					outer_activity_code_=outer_activity_code;
				}
				
				Goods goods=new Goods();
				goods.setGood_id(good_id);
				goods.setGood_cnt(orderDetail.getSkuNum());
//				goods.setGood_prc(orderDetail.getSkuPrice());
				goods.setGood_prc((BigDecimal)skuMap.get("sell_price"));//在这里传入原始价格
				goods.setColor_id(color_id);
				goods.setStyle_id(style_id);
				goods.setDely_fee("0");
				
				
				if(vip_price==null){
					vip_price=goods.getGood_prc();
				}
				CouponInfo coupon_info = new RsyncRequestAddOrder().new CouponInfo();
				if(flag88){//88折特有//88折特有//88折特有//88折特有//88折特有
					//goods.setSave_amt(String.valueOf((goods.getGood_prc().multiply(new BigDecimal(0.12))).setScale(2, BigDecimal.ROUND_HALF_UP))); //88折特有
					coupon_info.setSave_amt(goods.getGood_prc().multiply(new BigDecimal(0.12)).setScale(2, BigDecimal.ROUND_HALF_UP));
				}else{
					//goods.setSave_amt(String.valueOf((goods.getGood_prc().subtract(vip_price)).setScale(2, BigDecimal.ROUND_HALF_UP)));
					coupon_info.setSave_amt(goods.getGood_prc().subtract(vip_price).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				goods.getCouponList().add(coupon_info);
//				goods.setSave_amt(save_amt);//次价格原本应该取oc_order_activity 表中的preferential_money 字段，但是前端下单无法存入，所以去活动表中的价格。若出现价格不准确，恢复即可
				goods.setEvent_id(outer_activity_code);
				goods.setInv_head(address.getInvoiceTitle()); 
				
				if("0".equals(orderDetail.getGiftFlag())){ //判断赠品标示
					goods.setEvent_id(gift_active_code);//赠品的外部活动编号为活动表中的活动编号
					good_info_gift.add(goods);//赠品
				}else{
					
					if(flag88){  //首单88折的情况下，也要使用该活动编号
						goods.setEvent_id(gift_active_code);
					}
					
					good_info.add(goods);
				}
			}
			
//			449716200001	在线支付  在线支付都是支付宝
//			449716200002	货到付款 
			String payType = order.getPayType();
			String pre_aft_pay_cd="";
			String send_bank_cd="";
			if("449716200001".equals(payType)){
				pre_aft_pay_cd="10";
				send_bank_cd="54";
			} else if("449716200002".equals(payType)){
				pre_aft_pay_cd="30";
				send_bank_cd="CD1";
			}
			
			
			request.setSubsystem("app");
			request.setAccount("");
			request.setPassword("");
			request.setEtr_id("app");
//			request.setCust_id(order.getBuyerCode());//TODO 家有会员编号  待确定
			MDataMap buyCode = DbUp.upTable("mc_extend_info_homehas").one("member_code",order.getBuyerCode());
			if(buyCode!=null&&!buyCode.isEmpty()){
				request.setCust_id(buyCode.get("homehas_code"));
			}
			if(StringUtils.isEmpty(request.getCust_id())){
				request.setCust_id("0");
			}
			request.setCust_mobile(mobileid);
			request.setCoupon_type(coupon_type); 
			request.setCoupon_id("");
			request.setDlv_amt(order.getTransportMoney()==null?new BigDecimal(0):order.getTransportMoney());//运费
			request.setOrder_amt(order.getOrderMoney());//订单总金额
//			request.setUse_crdt_amt("0");
//			request.setUse_accm_amt("0");
//			request.setUse_ppc_amt("0");
			request.setPay_amt(order.getDueMoney());//用户实际支付金额
			request.setOrd_lvl_cd("10");
			request.setEvent_id(outer_activity_code_);
			request.setPre_aft_pay_cd(pre_aft_pay_cd);// 支付方式
			request.setSend_bank_cd(send_bank_cd);
			request.setExterior_accm(0);
			request.setMedi_lclss_id("7");
			request.setMedi_mclss_id("34");
			request.setHidden_json(null);
			request.setHidden_json_gift(null);
			request.setVirtual_ord("N");
			request.setWeb_ord_id(order.getOrderCode());
			request.setPay_no("");
			request.setPay_time(null);
			request.setDlv_date("");
			request.setDlv_service("");
			request.setDlv_time("");
			request.setWeb_ord_date(order.getCreateTime());
			request.setAccm_integral(1);
			request.setHy_type(1);
			request.setTel1("");
			request.setTel2("");
			
			request.setRcver_nm(address.getReceivePerson());
			request.setMobile(address.getMobilephone());
			String area_code=address.getAreaCode();
			Map<String, Object> map=DbUp.upTable("sc_tmp").dataSqlOne(" SELECT CONCAT((SELECT name from sc_tmp WHERE code=:code1 LIMIT 0,1 ),'-',(SELECT DISTINCT name from sc_tmp WHERE code=:code2 LIMIT 0,1 ),'-',(SELECT DISTINCT name from sc_tmp WHERE code=:code3 LIMIT 0,1 )) as code from sc_tmp LIMIT 0,1  ", new MDataMap("code1",area_code.subSequence(0, 2)+"0000","code2",area_code.subSequence(0, 4)+"00","code3",area_code));
			String code[] = ((String)map.get("code")).split("-");
			if(code.length>2){
				request.setLaddr(code[0]);
				request.setMaddr(code[1]); 
				request.setSaddr(code[2]);
			}
			request.setSend_addr(address.getAddress());
			request.setSrgn_cd(area_code);
			request.setZip_no(address.getPostCode());
			
		return 	addOrder.doRsync();
	}
	
	private String trim(Object obj){
		return obj==null?"":obj.toString().trim();
	}
}
