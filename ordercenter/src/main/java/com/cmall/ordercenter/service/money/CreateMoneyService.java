/**
 * Project Name:ordercenter
 * File Name:ReturnMoneyService.java
 * Package Name:com.cmall.ordercenter.service.money
 * Date:2013年11月11日下午3:32:42
 *
 */
package com.cmall.ordercenter.service.money;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.RetuGoodDetailChild;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.model.ReturnMoney;
import com.cmall.ordercenter.model.ReturnMoneyLog;
import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.cmall.ordercenter.service.PresentCardService;
import com.cmall.ordercenter.service.ReturnMoneyService;
import com.cmall.ordercenter.util.CouponUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * ClassName:ReturnMoneyService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2013年11月11日 下午3:32:42 <br/>
 * 
 * @author hxd
 * @version
 * @since JDK 1.6
 * @see
 */
public class CreateMoneyService extends BaseClass {
	
	public ReturnMoneyResult creatReturnMoneyListNew(ReturnGoods goods) {
		ReturnMoneyResult result = new ReturnMoneyResult();
		// 判断此单是否已经完成退款
		MDataMap mp = DbUp.upTable("oc_return_money").one("return_goods_code",goods.getReturn_code(), "order_code", goods.getOrder_code());
		if (null != mp) {
			result.setResultCode(939301064);
			result.setResultMessage(bInfo(939301064));
			return result;
		}
		
		//判断是否是在线支付，若不是，则不生成退款单
		MDataMap orderMap=DbUp.upTable("oc_orderinfo").one("order_code",goods.getOrder_code(),"pay_type","449716200001");
		if(orderMap==null){
			
			return result;
		}
		
		// 取支付方式
		MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_type", "zid desc","order_code=:order_code", "order_code", goods.getOrder_code());
		// 取使用的积分
		BigDecimal payMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", goods.getOrder_code(), "pay_type", "449746280008"));
		if(payMoney == null){
			payMoney = BigDecimal.ZERO;
		}
		// 取使用的惠币
		BigDecimal payHjyMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", goods.getOrder_code(), "pay_type", "449746280025"));
		if(payHjyMoney == null){
			payHjyMoney = BigDecimal.ZERO;
		}
		
//		List<MDataMap> ls = getGoodsDetailByCode(goods.getReturn_code());
		
		MDataMap map = new MDataMap();
		BigDecimal returnmoney=new BigDecimal(orderMap.get("due_money").toString());
		if (returnmoney.compareTo(BigDecimal.ZERO)==1) {
			String money_no = WebHelper.upCode("RTM");
			map.put("return_money_code", money_no);
			map.put("return_goods_code", goods.getReturn_code());
			map.put("buyer_code", goods.getBuyer_code());
			map.put("seller_code", goods.getSeller_code());
			map.put("small_seller_code", goods.getSmall_seller_code());
			map.put("contacts", goods.getContacts());
			map.put("status", "4497153900040003");
			map.put("return_money", "449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0
			map.put("mobile", goods.getMobile());
			map.put("create_time", DateUtil.getNowTime());
			map.put("poundage", "0");
			map.put("order_code", goods.getOrder_code());
			map.put("pay_method", orderMap.get("pay_type"));
			map.put("online_money", "449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0
			map.put("return_accm_money", payMoney.toString());
			map.put("return_hjycoin_money", payHjyMoney.toString());
			String orderCode = goods.getOrder_code();
			String outOrderCode = "";
			try{
				MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				outOrderCode = orderInfo.get("out_order_code");
			}catch(Exception e){
				e.getStackTrace();
			}
			map.put("out_order_code", outOrderCode);
			DbUp.upTable("oc_return_money").dataInsert(map);

			// 创建流水日志
			createRetuMoneyLog(goods, money_no);
			return result;
		} else {
			return result;
		}
	}
	
	
	public ReturnMoneyResult creatReturnMoney(String order_code) {
//		ReturnMoneyResult result = new ReturnMoneyResult();
//		// 判断此单是否已经完成退款
//		MDataMap mp = DbUp.upTable("oc_return_money").one("order_code",order_code);
//		if (null != mp) {
//			result.setResultCode(939301064);
//			result.setResultMessage(bInfo(939301064));
//			return result;
//		}
//		
//		//判断是否是在线支付，若不是，则不生成退款单
//		MDataMap orderMap=DbUp.upTable("oc_orderinfo").one("order_code",order_code,"pay_type","449716200001");
//		if(orderMap==null){
//			return result;
//		}
//		
//		// 取支付方式
//		MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_type", "zid desc","order_code=:order_code", "order_code", order_code);
//		// 取使用的积分
//		BigDecimal payMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280008"));
//		if(payMoney == null){
//			payMoney = BigDecimal.ZERO;
//		}
//		
//		// 取使用的惠币
//		BigDecimal payHjycoinMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280025"));
//		if(payHjycoinMoney == null){
//			payHjycoinMoney = BigDecimal.ZERO;
//		}
//		
//		MDataMap map = new MDataMap();
//		String money_no = WebHelper.upCode("RTM");
//		map.put("return_money_code", money_no);
//		map.put("return_goods_code", "");
//		map.put("buyer_code", orderMap.get("buyer_code"));
//		map.put("seller_code", orderMap.get("seller_code"));
//		map.put("small_seller_code", orderMap.get("small_seller_code"));
//		map.put("contacts", "");//联系人
//		map.put("status", "4497153900040003");
//		map.put("return_money", "449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0
//		map.put("mobile", new MemberLoginSupport().getMemberLoginName(orderMap.get("buyer_code")));
//		map.put("create_time", DateUtil.getNowTime());
//		map.put("poundage", "0");
//		map.put("order_code", orderMap.get("order_code"));
//		map.put("pay_method", orderMap.get("pay_type"));
//		map.put("online_money", "449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0
//		map.put("return_accm_money", payMoney.toString());
//		map.put("return_hjycoin_money", payHjycoinMoney.toString());
//		map.put("out_order_code", orderMap.get("out_order_code")!=null?orderMap.get("out_order_code"):"");
//		DbUp.upTable("oc_return_money").dataInsert(map);
//		
//		//取使用的优惠券（优惠券类型为礼金券）
//		//自营品不退还礼金券，LD品退还礼金券；自营品与LD品同时使用一张礼金券，若LD品取消发货则全额退还该张礼金券，自营品取消发货则不退还
//		//LD品 small_seller_code = 'SI2003'
//		//屏蔽掉此处逻辑 -rhb 20181114
////		if(DbUp.upTable("oc_orderinfo").dataCount(" order_code=:order_code and small_seller_code=:small_seller_code", new MDataMap("order_code", order_code, "small_seller_code", "SI2003")) > 0) {
////			List<GiftVoucherInfo> reWriteLD = (new CouponUtil()).rollbackCoupon(order_code);//优惠券回滚
////			result.setList(reWriteLD);
////		}
//		
//		// 创建流水日志
//		MDataMap logMap = new MDataMap();
//		logMap.put("return_money_no", money_no);
//		logMap.put("info", "订单失败，创建退款单");
//		logMap.put("create_time", DateUtil.getNowTime());
//		logMap.put("create_user", orderMap.get("small_seller_code"));
//		logMap.put("status", map.get("status"));
//		DbUp.upTable("lc_return_money_status").dataInsert(logMap);
		
		MUserInfo user = UserFactory.INSTANCE.create();
		String operator = user == null ? "" : user.getUserCode();
		return creatReturnMoney(order_code, operator, "订单取消");
	}
	
	/**
	 * 生成退款单
	 * @param order_code
	 * @param remark
	 * @return
	 */
	public ReturnMoneyResult creatReturnMoney(String order_code,String operator,String remark) {
		
		ReturnMoneyResult result = new ReturnMoneyResult();
		// 判断此单是否已经完成退款
		MDataMap mp = DbUp.upTable("oc_return_money").one("order_code",order_code);
		if (null != mp) {
			result.setResultCode(939301064);
			result.setResultMessage(bInfo(939301064));
			return result;
		}
		
		MDataMap orderMap = null;
		//判断是否是在线支付449716200001，或第三方代收449716200010  若不是，则不生成退款单
		MDataMap orderMap1=DbUp.upTable("oc_orderinfo").one("order_code",order_code,"pay_type","449716200001");
		MDataMap orderMap2=DbUp.upTable("oc_orderinfo").one("order_code",order_code,"pay_type","449716200010");
		if(orderMap1==null && orderMap2==null){
			return result;
		}else if(orderMap1 == null){
			orderMap = orderMap2;
		}else{
			orderMap = orderMap1;
		}
		
		// 取支付方式
		MDataMap payMap = DbUp.upTable("oc_order_pay").oneWhere("pay_type", "zid desc","order_code=:order_code", "order_code", order_code);
		// 取使用的积分
		BigDecimal payMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280008"));
		if(payMoney == null){
			payMoney = BigDecimal.ZERO;
		}
		
		// 取使用的惠币
		BigDecimal payHjycoinMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280025"));
		if(payHjycoinMoney == null){
			payHjycoinMoney = BigDecimal.ZERO;
		}
		// 取使用的储值金
		BigDecimal payCzjMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280006"));
		if(payCzjMoney == null) {
			payCzjMoney = BigDecimal.ZERO;
		}
		// 取使用的暂存款
		BigDecimal payZckMoney = (BigDecimal)DbUp.upTable("oc_order_pay").dataGet("payed_money", "", new MDataMap("order_code", order_code, "pay_type", "449746280007"));
		if(payZckMoney == null) {
			payZckMoney = BigDecimal.ZERO;
		}
		
		MDataMap map = new MDataMap();
		String money_no = WebHelper.upCode("RTM");
		map.put("return_money_code", money_no);
		map.put("return_goods_code", "");
		map.put("buyer_code", orderMap.get("buyer_code"));
		map.put("seller_code", orderMap.get("seller_code"));
		map.put("small_seller_code", orderMap.get("small_seller_code"));
		map.put("contacts", "");//联系人
		map.put("status", "4497153900040003");
		map.put("return_money", null == payMap ? "0" :"449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0 多彩宝也为0 --rhb
		map.put("mobile", new MemberLoginSupport().getMemberLoginName(orderMap.get("buyer_code")));
		map.put("create_time", DateUtil.getNowTime());
		map.put("poundage", "0");
		map.put("order_code", orderMap.get("order_code"));
		map.put("pay_method", orderMap.get("pay_type"));
		map.put("online_money", null == payMap ? "0" :"449746280016".equals(payMap.get("pay_type")) ? "0" : orderMap.get("due_money")); // 微匠支付退款单金额应该为0 多彩宝也为0 --rhb
		map.put("return_accm_money", payMoney.toString());
		map.put("return_hjycoin_money", payHjycoinMoney.toString());//惠币退款金额
		map.put("return_ppc_money", payCzjMoney.toString());
		map.put("return_crdt_money", payZckMoney.toString());
		map.put("out_order_code", orderMap.get("out_order_code")!=null?orderMap.get("out_order_code"):"");
		DbUp.upTable("oc_return_money").dataInsert(map);
		
		//取使用的优惠券（优惠券类型为礼金券）
		//自营品不退还礼金券，LD品退还礼金券；自营品与LD品同时使用一张礼金券，若LD品取消发货则全额退还该张礼金券，自营品取消发货则不退还
		//LD品 small_seller_code = 'SI2003'
		//屏蔽掉此处逻辑 -rhb 20181114
//		if(DbUp.upTable("oc_orderinfo").dataCount(" order_code=:order_code and small_seller_code=:small_seller_code", new MDataMap("order_code", order_code, "small_seller_code", "SI2003")) > 0) {
//			List<GiftVoucherInfo> reWriteLD = (new CouponUtil()).rollbackCoupon(order_code);//优惠券回滚
//			result.setList(reWriteLD);
//		}
		
		// 创建流水日志
		MDataMap logMap = new MDataMap();
		logMap.put("return_money_no", money_no);
		logMap.put("info", remark);
		logMap.put("create_time", DateUtil.getNowTime());
		logMap.put("create_user", operator);
		logMap.put("status", map.get("status"));
		DbUp.upTable("lc_return_money_status").dataInsert(logMap);
		
		result.setReturnMoneyCode(money_no);
		
		return result;
	}
	
	
/**
 * 获取退款手续费
 * @param goods
 * @return
 */
	private float getPoundage(ReturnGoods goods) {
		float orderPrice = Float.parseFloat(DbUp.upTable("oc_orderinfo").one("order_code", goods.getOrder_code()).get("product_money"));
		String sql = "select sum(current_price) cprice from oc_return_goods_detail where return_code ='"+ goods.getReturn_code()+ "'";
		float returnPrice = Float.parseFloat(DbUp.upTable("oc_return_goods_detail").dataSqlList(sql, new MDataMap()).get(0).get("cprice").toString());
		//在线支付所占比重
		String sq1 ="select sum(payed_money) money from  oc_order_pay where order_code = '"
				+ goods.getOrder_code()+ "' and (pay_type='449746280003' or pay_type='449746280004')";//在线支付
		// select sum(payed_money) money from  oc_order_pay where order_code = 'DD140327100001' 
		// and pay_type='449746280003'
		String sq2 ="select sum(payed_money) money from  oc_order_pay where order_code = '"
				+ goods.getOrder_code()+ "' and pay_type='449746280001'";//购物卡支付
		//select sum(payed_money) money from  oc_order_pay where order_code = 'DD140327100001' and pay_type='449746280001'
		//在线支付
		List<Map<String, Object>> data1 = DbUp.upTable("oc_order_pay").dataSqlList(sq1, new MDataMap());//0.04		//购物卡支付
		List<Map<String, Object>> data2 = DbUp.upTable("oc_order_pay").dataSqlList(sq2, new MDataMap());//null
		//礼品卡支付所占比重
		float poundage = 0;
		if(null != data1.get(0).get("money") && null != data2.get(0).get("money"))
			poundage = returnPrice*(getPayScale("449746280003") + getPayScale("449746280001")) ;
		else if(null == data1.get(0).get("money") && null != data2.get(0).get("money"))
			poundage = returnPrice*getPayScale("449746280001") ;
		else if(null != data1.get(0).get("money") && null == data2.get(0).get("money"))
			poundage = returnPrice*getPayScale("449746280003") ;
		else
			poundage = 0;
		return poundage;
	}

	/**
	 * getGoodsDetailByCode:(获取). <br/>
	 * 
	 * @author hxd
	 * @param code
	 * @return
	 * @since JDK 1.6
	 */
	private List<MDataMap> getGoodsDetailByCode(String code) {
		MDataMap mp = new MDataMap();
		mp.put("return_code", code);
		return DbUp.upTable("oc_return_goods_detail").queryAll("", "", "", mp);
	}

	/**
	 * createRetuMoneyLog:(生成退货款流水日志). <br/>
	 * 
	 * @author hxd
	 * @param goods
	 * @param moneyCode
	 * @since JDK 1.6
	 */
	public void createRetuMoneyLog(ReturnGoods goods, String moneyCode) {
		MDataMap map = new MDataMap();
		map.put("return_money_no", moneyCode);
		map.put("info", goods.getDescription());
		map.put("create_time", DateUtil.getNowTime());
		map.put("create_user", UserFactory.INSTANCE.create().getLoginName());
		map.put("status", "4497153900040003");
		try {
			DbUp.upTable("lc_return_money_status").dataInsert(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * createRetuMoneyLog:(生成退货款流水日志). <br/>
	 * 
	 * @author hxd
	 * @param log
	 * @since JDK 1.6
	 */
	public void createRetuMoneyLog(ReturnMoneyLog log) {
		MDataMap map = new MDataMap();
		map.put("return_money_no", log.getReturn_money_no());
		map.put("info", log.getInfo());
		map.put("create_time", DateUtil.getNowTime());
		map.put("create_user", log.getCreate_user());
		map.put("status", log.getStatus());
		try {
			DbUp.upTable("lc_return_money_status").dataInsert(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getReturnMoneyCodeByUid:(这里用一句话描述这个方法的作用). <br/>
	 * 
	 * @author hxd
	 * @param flowCode
	 * @return
	 * @since JDK 1.6
	 */
	public ReturnMoney getReturnMoneyCodeByUid(String flowCode) {
		ReturnMoney r = new SerializeSupport<ReturnMoney>().serialize(
				DbUp.upTable("oc_return_money").one("uid", flowCode),
				new ReturnMoney());
		 return r;
	}

	/**
	 * getReturnGoodsCodeByUid:(获取退货单详情). <br/>
	 * 
	 * @author hxd
	 * @param flowCode
	 * @return
	 * @since JDK 1.6
	 */
	public RetuGoodDetailChild getReturnGoodsCodeByUid(String flowCode) {
		return new SerializeSupport<RetuGoodDetailChild>().serialize(DbUp
				.upTable("oc_return_goods_detail").one("uid", flowCode),
				new RetuGoodDetailChild());
	}

	/**
	 * getReturnMoneyBuUid:(通过UID获取退款单信息). <br/>
	 * 
	 * @author hxd
	 * @param uid
	 * @return
	 * @since JDK 1.6
	 */
	private ReturnMoney getReturnMoneyBuUid(String uid) {
		return new SerializeSupport<ReturnMoney>().serialize(
				DbUp.upTable("oc_return_money").one("uid", uid),
				new ReturnMoney());
	}

	/**
	 * getInSqlForList:(拼接sql). <br/>
	 * 
	 * @author hxd
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	private String getInSqlForList(List<String> list) {
		String sql = "";
		if (!list.isEmpty()) {
			for (String m : list) {
				String code = m;
				if ("".equals(sql) && code != null && !"".equals(code)) {
					sql = " in ('" + code + "'";
				} else if (code != null && !"".equals(code)) {
					sql += ",'" + code + "'";
				}
			}
		}
		if (!"".equals(sql)) {
			sql += ")";
		}
		return sql;
	}

	/**
	 * getSkuByActiveCode:(通过活动code获取参加活动的sku信息). <br/>
	 * 
	 * @author hxd
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	public List<Map<String, Object>> getSkuByActiveCode(List<String> list) {
		String sql = "select * from oc_return_goods_detail where return_code in "
				+ "(select return_code from  oc_return_goods  where order_code  "
				+ getInSqlForList(list) + " )";
		// 获取退货单详情
		List<Map<String, Object>> lst = DbUp.upTable("oc_return_goods_detail")
				.dataSqlList(sql, new MDataMap());
		return lst;
	}

	/**
	 * getReturnMess:(返还因退货产生的退款). <br/>
	 * 
	 * @author hxd
	 * @param returnCode
	 * @return
	 * @since JDK 1.6
	 */
	public RootResult returnMoney(String uid,ReturnMoneyLog log,String poundage) {

		RootResult result = new RootResult();
		// 获取退款单
		ReturnMoney money = getReturnMoneyBuUid(uid);
		MDataMap tm = DbUp.upTable("oc_return_money_detail").one("order_code",
				money.getOrder_code(), "return_money_code",
				money.getReturn_money_code());
		if(null != tm)
		{
			result.setResultCode(939301073);
			result.setResultMessage(bInfo(939301073));
			return result;
		}
		// 活动订单的支付方式 以及金额汇总
		String sql = "select pay_type,sum(payed_money) payed_money from oc_order_pay where order_code = '"
				+ money.getOrder_code() + "' group by pay_type";
		List<Map<String, Object>> list = DbUp.upTable("oc_order_pay").dataSqlList(sql, new MDataMap());
		
		float online_percent = 0;
		float card_percent = 0;
		float totalMoney = 0;
		
		for(int k=0;k<list.size();k++)
		{
			totalMoney = totalMoney +Float.parseFloat(list.get(k).get("payed_money").toString());
		}
		for(int j = 0;j <list.size();j++)
		{
			if("449746280003".equals(list.get(j).get("pay_type")))
				online_percent =  Float.parseFloat(list.get(j).get("payed_money").toString())/totalMoney;
			
			if("449746280004".equals(list.get(j).get("pay_type")))
				online_percent =  Float.parseFloat(list.get(j).get("payed_money").toString())/totalMoney;
			
			if("449746280001".equals(list.get(j).get("pay_type")))
				card_percent =  Float.parseFloat(list.get(j).get("payed_money").toString())/totalMoney;
		}
		//快钱
		String bill_pay = "449746280004";
		// 支付宝
		String ali_pay = "449746280003";
		// 购物卡支付
		String card_pay = "449746280001";
		// 货到付款
		//String cod = "449746280004";
		for (Map<String, Object> mp : list) {
			if (mp.get("pay_type").equals(ali_pay)) {
				try {
					insertReturnMoney(money, online_percent,getSeqByPayType(ali_pay, money.getOrder_code()),"",ali_pay, mp);
				} catch (Exception e) {
					result.setResultCode(939301070);
					result.setResultMessage(bInfo(939301070));
					WebHelper.errorMessage("939301071", "创建在线支付记录失败！", 0,
							"CreateMoneyService", e.getMessage(), e);
					return result;
				}
			}
			if (mp.get("pay_type").equals(bill_pay)) {
				try {
					insertReturnMoney(money, online_percent,getSeqByPayType(bill_pay, money.getOrder_code()),getMerchantId(bill_pay, money.getOrder_code()),bill_pay, mp);
				} catch (Exception e) {
					result.setResultCode(939301070);
					result.setResultMessage(bInfo(939301070));
					WebHelper.errorMessage("939301071", "创建在线支付记录失败！", 0,
							"CreateMoneyService", e.getMessage(), e);
					return result;
				}
			}
			if (mp.get("pay_type").equals(card_pay)) 
			{
				float cardMoney = card_percent * money.getReturn_money() ;
				MDataMap cardMp = new MDataMap();
				cardMp.put("pay_type", card_pay);
				cardMp.put("order_code", money.getOrder_code());
				List<MDataMap> listCard = DbUp.upTable("oc_order_pay")
						.queryAll("", "", "", cardMp);
				// 购物卡类型支付的总金额
				float totalCardMoney = 0;
				for (MDataMap mdp : listCard) {
					totalCardMoney = totalCardMoney
							+ Float.parseFloat(mdp.get("payed_money"));
				}
				//判断退款单中是否有退款完成的记录，如果有直接跳出
//				boolean flg = false;
//				for (MDataMap mdp : listCard) {
//					MDataMap mWhereMap = new MDataMap();
//					mWhereMap.put("order_code", mdp.get("order_code"));
//					mWhereMap.put("status", "4497153900040001");
//					int ct = DbUp.upTable("oc_return_money").dataCount("order_code=:order_code and status=:status", mWhereMap);
//					if (ct > 0)
//					{
//						flg =true;
//						continue;
//					}
//				}
//				if(flg == true)  // 
//				{
//					result.setResultCode(939301098);
//					result.setResultMessage(bInfo(939301098));
//					return result;
//				}
					
				
				// 假设有多张卡将钱在等比分,然后插入returnMoney_detail
				for (MDataMap mdp : listCard) {
					
				
					
					
					MDataMap tmp = new MDataMap();
					tmp.put("return_money_code", money.getReturn_money_code());
					tmp.put("order_code", money.getOrder_code());
					tmp.put("return_money",
							String.valueOf(((Float.parseFloat(mdp
									.get("payed_money")) * cardMoney / totalCardMoney))));
					tmp.put("return_seq", mdp.get("pay_sequenceid"));
					tmp.put("create_time", DateUtil.getNowTime());
					tmp.put("return_type", card_pay);
					int  count = (int) Math.ceil(money.getVirtual_money_deduction());
					tmp.put("virtual_money_deduction", String.valueOf(count));
					try {
						DbUp.upTable("oc_return_money_detail").dataInsert(tmp);
					} catch (Exception e) {
						result.setResultCode(939301069);
						result.setResultMessage(bInfo(939301069));
						e.printStackTrace();
						WebHelper.errorMessage("939301069",
								"将钱在等比分,然后插入returnMoney_detail失败！", 0,
								"CreateMoneyService", e.getMessage(), e);
						return result;
					}
					
					
				}
			}
	}
		
		//更新手续费开始
		MDataMap mp = new MDataMap();
		mp.put("return_money_code", money.getReturn_money_code());
		MDataMap mData = new MDataMap();
		mData.put("poundage", poundage);
		mData.put("return_money_code", money.getReturn_money_code());
		DbUp.upTable("oc_return_money").dataUpdate(mData, "poundage", "return_money_code");
		//更新手续费结束
		try {
			// 购物卡返款
			result = backCardMoney(money, card_pay);
			// 返还积分开始
			MDataMap map = DbUp.upTable("oc_orderinfo").one("order_code",money.getOrder_code());	
			String buyerCode = map.get("buyer_code");
			returnScore(buyerCode,String.valueOf(money.getVirtual_money_deduction()));
			// 返还积分开始	
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(939301072);
			result.setResultMessage(bInfo(939301072));
			WebHelper.errorMessage("939301072", "向礼品卡充值失败！", 0,
					"CreateMoneyService", e.getMessage(), e);
			return result;
		}
    return result;
}
	/**
	 * backCardMoney:(向购物卡反向充值). <br/>
	 * 
	 * @author hxd
	 * @param money
	 * @param card_pay
	 * @return
	 * @since JDK 1.6
	 */
	private RootResult backCardMoney(ReturnMoney money, String card_pay) {
		RootResult result = new RootResult();
		PresentCardService service = new PresentCardService();
		List<MDataMap> list_money = getMoneyDetail(money.getOrder_code());
		List<OcOrderPay> orderPays = new ArrayList<OcOrderPay>();
		for (MDataMap mp : list_money) {
			if (card_pay.endsWith(mp.get("return_type"))) {
				OcOrderPay ocOrderPay = new OcOrderPay();
				ocOrderPay.setOrderCode(mp.get("order_code"));
				ocOrderPay.setPayedMoney(Float.parseFloat(mp
						.get("return_money")));
				ocOrderPay.setPaySequenceid(mp.get("return_seq"));
				ocOrderPay.setPayType(mp.get("return_type"));
				orderPays.add(ocOrderPay);
			}
		}
		Order order = new Order();
		order.setOcOrderPayList(orderPays);
		// 向购物卡返款
		try {
			result = service.usePresentCard(order, 1);
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	/**
	 * insertReturnMoney:(插入在线支付的记录). <br/>
	 * 
	 * @author hxd
	 * @param money
	 * @param percent
	 * @param card_pay
	 * @param mp
	 * @since JDK 1.6
	 */
	private void insertReturnMoney(ReturnMoney money, float percent,
			String card_pay,String mid, String return_type, Map<String, Object> mp) {
		float cardMoney;
		cardMoney = percent* money.getReturn_money();
		MDataMap tmp = new MDataMap();
		tmp.put("return_money_code", money.getReturn_money_code());
		tmp.put("order_code", money.getOrder_code());
		tmp.put("return_money", String.valueOf(cardMoney));
		
		int  count = (int) Math.ceil(money.getVirtual_money_deduction());
		tmp.put("virtual_money_deduction", String.valueOf(count));
		tmp.put("return_seq", card_pay);
		tmp.put("create_time", DateUtil.getNowTime());
		tmp.put("return_type", return_type);
		tmp.put("merchant_id", mid);
		DbUp.upTable("oc_return_money_detail").dataInsert(tmp);
		//更新主表记录
		MDataMap tm = new MDataMap();
		tm.put("online_money", String.valueOf(cardMoney));
		tm.put("pay_method", return_type);
		tm.put("batch_no", getSeqByPayType(return_type, money.getOrder_code()));
		tm.put("return_money_code", money.getReturn_money_code());
		//tm.put("virtual_money_deduction", String.valueOf(count));
		DbUp.upTable("oc_return_money").dataUpdate(tm, "online_money,pay_method,batch_no", "return_money_code");
		/**
		 * DbUp.upTable("oc_return_money").dataUpdate(tm, "online_money", "return_money_code");
		DbUp.upTable("oc_return_money").dataUpdate(tm2, "pay_method", "return_money_code");
		DbUp.upTable("oc_return_money").dataUpdate(tm3, "batch_no", "return_money_code");
		 */
		
		//主表更新完毕
	}
    /**
     * 用支付类型和订单编号获取支付流水号
     * @param typeCode
     * @param orderCode
     * @return
     */
	private String getSeqByPayType(String typeCode, String orderCode) {
		MDataMap mp = DbUp.upTable("oc_order_pay").one("pay_type", typeCode,
				"order_code", orderCode);
		return mp.get("pay_sequenceid");
	}

	
    /**
     * 用支付类型和订单编号获取支付流水号
     * @param typeCode
     * @param orderCode
     * @return
     */
	private String getMerchantId(String typeCode, String orderCode) {
		MDataMap mp = DbUp.upTable("oc_order_pay").one("pay_type", typeCode,
				"order_code", orderCode);
		return mp.get("merchant_id");
	}
	/**
	 * getMoneyDetail:(根据orderCode获取退款详情). <br/>
	 * 
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	private List<MDataMap> getMoneyDetail(String orderCode) {
		MDataMap mp = new MDataMap();
		mp.put("order_code", orderCode);
		List<MDataMap> list = DbUp.upTable("oc_return_money_detail").queryAll(
				"", "", "", mp);
		return list;
	}
	/**
	 * 获取支付方式手续费
	 */
	private Float getPayScale(String type)
	{
		MDataMap mp = DbUp.upTable("sc_paytype").one("pay_code",type);
		return Float.parseFloat(mp.get("rturn_scale").toString());
	}
	
	
	/**
	 * 
	 * @param buyerCode 买家编号
	 * @param scoreCount 积分数量
	 */
	
	private void returnScore(String buyerCode,String scoreCount)
	{
		MDataMap dataMap = DbUp.upTable("jifen_info").one("object",buyerCode);
		if(dataMap != null)
		{
			float score = Float.parseFloat(dataMap.get("value"));
			score = score +Float.parseFloat(scoreCount);
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("value", String.valueOf(score));
			mDataMap.put("object", buyerCode);
			DbUp.upTable("jifen_info").dataUpdate(mDataMap, "value", "object");
			MDataMap dataMap2 = new MDataMap();
			dataMap2.put("to_id", buyerCode);
			//dataMap2.put("to_name", DbUp.upTable("").one("object",buyerCode));
			dataMap2.put("from_id", UserFactory.INSTANCE.create().getManageCode());
			dataMap2.put("from_name", UserFactory.INSTANCE.create().getLoginName());
			//dataMap2.put("to_value", "");
			//dataMap2.put("from_value", "");
			dataMap2.put("value", scoreCount);
			dataMap2.put("to_balance", String.valueOf(score));
			dataMap2.put("action", "0");
			dataMap2.put("trade_code", new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date())  +String.valueOf((int)(Math.random()*900)+100));
			dataMap2.put("from_type", "0");                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
			dataMap2.put("to_type", "1");
			dataMap2.put("payment", "2");
			dataMap2.put("status", "2");
			dataMap2.put("status_name", "已发放");
			dataMap2.put("op_time", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date()));
			dataMap2.put("from_balance", "0");
			DbUp.upTable("jifen_log").dataInsert(dataMap2);
		}
	}
}
