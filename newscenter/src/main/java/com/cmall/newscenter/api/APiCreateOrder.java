package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.*;
import com.cmall.groupcenter.service.OrderService;
import com.cmall.ordercenter.service.ApiAlipayMoveProcessService;
import com.cmall.ordercenter.service.OrderShoppingService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 刘嘉玲创建订单接口
 * 
 * @author xiegj
 *
 */
public class APiCreateOrder extends RootApiForToken<APiCreateOrderResult, APiCreateOrderInput> {

	public APiCreateOrderResult Process(APiCreateOrderInput inputParam,
			MDataMap mRequestMap) {
		APiCreateOrderResult result = new APiCreateOrderResult();
		List<GoodsInfoForAdd> goodF = new ArrayList<GoodsInfoForAdd>();
		if(inputParam.getGoods()!=null&&!inputParam.getGoods().isEmpty()){
			for (int i = 0; i < inputParam.getGoods().size(); i++) {
				ShopCartService se = new ShopCartService();
				GoodsInfoForAdd add = inputParam.getGoods().get(i);
				add.setSku_code(se.getSkuCodeForValue(add.getProduct_code(), add.getSku_code()));
				goodF.add(add);
			}
		}
		inputParam.setGoods(goodF);
		inputParam.setBuyer_code(getUserCode());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_type", inputParam.getOrder_type());//订单类型
		map.put("order_souce", inputParam.getOrder_souce());//订单来源
		map.put("buyer_code", getUserCode());//买家编号
		map.put("buyer_name", inputParam.getBuyer_name());//收货人姓名
		map.put("buyer_address", inputParam.getBuyer_address());//收件人地址
		map.put("buyer_address_code", inputParam.getBuyer_address_code());//收货人地址第三极编号
		map.put("buyer_mobile", inputParam.getBuyer_mobile());//收件人手机号
		map.put("pay_type", inputParam.getPay_type());//支付类型
		map.put("billInfo", inputParam.getBillInfo());//发票信息 bill_Type发票类型  bill_title发票抬头 bill_detail发票内容
		map.put("goods", inputParam.getGoods());//商品列表
		map.put("seller_code", getManageCode());
		map.put("app_vision", inputParam.getVersion());
		map.put("check_pay_money", inputParam.getCheck_pay_money());
		if(inputParam.getGoods()==null||inputParam.getGoods().isEmpty()){
			result.setResultCode(916401113);
			result.setResultMessage(bInfo(916401113));
			return result;
		}
		//校验购买条件是否符合
		ShopCartService service = new ShopCartService();
		RootResult rootResult = service.checkGoodsStock(getUserCode(), inputParam.getGoods());
		if(rootResult.getResultCode()==1){
			rootResult = service.checkGoodsLimit(getManageCode(),getUserCode(), inputParam.getGoods());
			if(rootResult.getResultCode()>1){
				result.setResultCode(rootResult.getResultCode());
				result.setResultMessage(rootResult.getResultMessage());
				return result;
			}
		}else {
			result.setResultCode(rootResult.getResultCode());
			result.setResultMessage(rootResult.getResultMessage());
			return result;
		}
		Map<String, Object> re= service.createOrder(map);
		if(re.containsKey("check_pay_money_error")){
			result.setResultCode(916401133);
			result.setResultMessage(re.get("check_pay_money_error").toString());
			return result;
		}
		
		if(re.containsKey("error")&&re.get("error")!=null&&!"".equals(re.get("error").toString())&&(re.containsKey("order_code")&&!re.get("order_code").toString().equals(re.get("error").toString()))){
			result.setResultCode(2);
			result.setResultMessage(re.get("error").toString());
		}else {
			
			//同步家有
			OrderService hjy = new OrderService();
			boolean ff = hjy.rsyncOrder(re.get("order_code").toString(), getOauthInfo().getLoginName());
			if(!ff){
				MDataMap updateMap = new MDataMap();
				updateMap.put("order_code", re.get("order_code").toString());
				updateMap.put("order_status", "4497153900010006");
				updateMap.put("delete_flag", "1");
				DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "order_code");
				result.setOrder_code("");
				result.setResultCode(916401118);
				result.setResultMessage(bInfo(916401118));
				return result;
			}
			ApiAlipayMoveProcessService se = new ApiAlipayMoveProcessService();
			result.setOrder_code(re.get("order_code").toString());
			if(inputParam.getPay_type()!=null&&"449716200002".equals(inputParam.getPay_type())){
				result.setPay_url("");
				result.setSign_detail("");
			}else {
				result.setPay_url(se.alipayMoveParameter(re.get("order_code").toString(),true));
				result.setSign_detail(se.alipaySign(re.get("order_code").toString()).get("sign"));
			}
			if(ff&&"449716200002".equals(inputParam.getPay_type())){
				OrderShoppingService orderShoppingService = new OrderShoppingService();
				orderShoppingService.deleteSkuToShopCart(re.get("order_code").toString());
			}
			if(inputParam.getGoods()!=null&&!inputParam.getGoods().isEmpty()){
				for (int i = 0; i < inputParam.getGoods().size(); i++) {//更新购物车数据为已结算
					GoodsInfoForAdd add = new GoodsInfoForAdd();
					ShopCartService ss = new ShopCartService();
					ss.updateAccountFlag(getUserCode(), add.getSku_code());
				}
			}
		}
		return result;
	}

}
