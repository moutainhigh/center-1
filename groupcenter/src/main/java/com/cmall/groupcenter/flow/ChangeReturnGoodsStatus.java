/**
 * Project Name:ordercenter
 * File Name:ChangeGoodsStatus.java
 * Package Name:com.cmall.ordercenter.service.goods
 * Date:2013年10月10日下午3:56:33
 *
*/

package com.cmall.groupcenter.flow;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.model.ReturnGoodsLog;
import com.cmall.ordercenter.service.ReturnMoneyService;
import com.cmall.ordercenter.service.goods.ReturnGoodsApi;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmasorder.service.TeslaCrdtService;
import com.srnpr.xmasorder.service.TeslaPpcService;
import com.srnpr.xmassystem.enumer.HjyBeanExecType;
import com.srnpr.xmassystem.service.HjybeanService;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.websupport.ApiCallSupport;

/**
 * ClassName:ChangeGoodsStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年10月10日 下午3:56:33 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ChangeReturnGoodsStatus extends BaseClass implements IFlowFunc 
{
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO 更新退货日志状态.
	 * @see com.cmall.systemcenter.systemface.IFlowFunc#afterFlowChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		RootResult result = new RootResult();
		ReturnGoodsApi api = new ReturnGoodsApi();
		ReturnGoods goods = api.getReturnGoodsCodeByUid(flowCode);
		ReturnGoodsLog log = new ReturnGoodsLog();
		log.setReturn_no(goods.getReturn_code());
		log.setCreate_time(DateUtil.getNowTime());
		log.setStatus(toStatus);
		log.setCreate_user(UserFactory.INSTANCE.create().getUserCode());
		log.setInfo(mSubMap.get("remark"));
		try
		{
			api.insertReturnGoodsLog(log);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		//创建退款单  4497153900040005      4497153900050001
		if("4497153900050001".equals(toStatus)) {
			try {
//				result = new CreateMoneyService().creatReturnMoneyList(goods);
//				result = new CreateMoneyService().creatReturnMoneyListNew(goods);
				
				//
				creatReturnMoney(goods);
			} catch (Exception e) {
				e.printStackTrace();
				result.setResultCode(939301065);
				result.setResultMessage(bInfo(939301065));
				e.printStackTrace();
				return result;
			}
			
			//新增逻辑
			String order_code=goods.getOrder_code();
			String goods_receipt=goods.getGoods_receipt();
			Integer allNum=0;
			Integer retNum=0;
			Map<String, Object> detailMap = DbUp.upTable("oc_orderdetail").dataSqlOne("select sum(sku_num) as sku_num from oc_orderdetail where order_code=:order_code", new MDataMap("order_code",order_code));
			if(detailMap!=null&&!detailMap.isEmpty()){
				allNum=((BigDecimal)detailMap.get("sku_num")).intValue();
			}
			
			Map<String, Object> retMap = DbUp.upTable("oc_return_goods").dataSqlOne("SELECT SUM(d.count) as sku_num from oc_return_goods g RIGHT JOIN  oc_return_goods_detail d on g.return_code=d.return_code where g.`status` in ('4497153900050001','4497153900050003') and g.order_code=:order_code", new MDataMap("order_code",order_code));
			if(retMap!=null&&!retMap.isEmpty()){
				retNum=((BigDecimal)retMap.get("sku_num")).intValue();
			}
			
			if(retNum>=allNum){//全部退货
				String order_status="4497153900010005";
//				if(StringUtils.equals(goods_receipt, "4497476900040002")){//注掉原因：全部退货不影响订单主状态
//					order_status="4497153900010006";
//				}
				
				MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140004"), "order_status_ext", "order_code");
				if(!StringUtils.equals(order_status, orderInfo.get("order_status"))){
					// 订单结算时需要
					DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",orderInfo.get("order_status"),"now_status",order_status,"info","ChangeReturnGoodsStatus"));
				}
			}else{
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140003"), "order_status_ext", "order_code");
			}
			
			// 订单退货需要退还使用的积分
			try {
				new PlusServiceAccm().addExecInfoForReturn(order_code, goods.getReturn_code(), "订单退货");
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(goods.getReturn_code(), "ChangeReturnGoodsStatus", 1,"returnForAccmAmt on ChangeReturnGoodsStatus", "", e);
			}
			
			// 订单退货需要退还使用的惠币
			try {
				new HjycoinService().addExecInfoForReturn(order_code, goods.getReturn_code(), "订单退货");
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(goods.getReturn_code(), "ChangeReturnGoodsStatus", 1,"returnForHjycoin on ChangeReturnGoodsStatus", "", e);
			}
			// 订单退货需要退还使用的储值金
			try {
				new TeslaPpcService().addExecInfoForReturn(order_code, goods.getReturn_code(), "订单退货");
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(goods.getReturn_code(), "ChangeReturnGoodsStatus", 1,"returnForPpcAmt on ChangeReturnGoodsStatus", "", e);
			}
			// 订单退货需要退还使用的暂存款
			try {
				new TeslaCrdtService().addExecInfoForReturn(order_code, goods.getReturn_code(), "订单退货");
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(goods.getReturn_code(), "ChangeReturnGoodsStatus", 1,"returnForCrdtmAmt on ChangeReturnGoodsStatus", "", e);
			}
		}
		
		return result;
	}
	
	private ReturnMoneyResult creatReturnMoney(ReturnGoods goods) {
		
		ReturnMoneyResult result = new ReturnMoneyResult();
		
//		if(StringUtils.equals("SF03KJT", goods.getSmall_seller_code())){//此处，海外购不再生成退款单
//			return result;
//		}
		// 积分退款金额，退款金额中减去积分的金额才是真正给用户退款的钱
		BigDecimal expected_return_accm_money=goods.getExpected_return_accm_money();
		BigDecimal expected_return_group_money=goods.getExpected_return_group_money();
		BigDecimal expected_return_money=goods.getExpected_return_money();
		BigDecimal expected_return_ppc_money = goods.getExpected_return_ppc_money();
		BigDecimal expected_return_crdt_money = goods.getExpected_return_crdt_money();
		BigDecimal expected_return_hjycoin_money = goods.getExpected_return_hjycoin_money();
		
		// 解决退还积分金额超过商品金额时退款单金额是负数的问题       
		//添加扣减储值金、暂存款 惠币   20180531  -rhb
		BigDecimal realReturnMoney=expected_return_money.subtract(expected_return_accm_money).subtract(expected_return_ppc_money).subtract(expected_return_crdt_money).subtract(expected_return_hjycoin_money);
		if(realReturnMoney.compareTo(BigDecimal.ZERO) < 0){
			realReturnMoney = BigDecimal.ZERO;
		}
		String money_no = WebHelper.upCode("RTM");
		if(expected_return_money.compareTo(BigDecimal.ZERO)>0){
			//生成退款单
			MDataMap map = new MDataMap();
			map.put("return_money_code", money_no);
			map.put("return_goods_code", goods.getReturn_code());
			map.put("buyer_code", goods.getBuyer_code());
			map.put("seller_code", goods.getSeller_code());
			map.put("small_seller_code", goods.getSmall_seller_code());
			map.put("contacts", "");//联系人
			map.put("status", "4497153900040003");
			map.put("return_money",realReturnMoney.toString());
			map.put("mobile", goods.getBuyer_mobile());
			map.put("create_time", DateUtil.getNowTime());
			map.put("poundage", "0");
			map.put("order_code", goods.getOrder_code());
			map.put("pay_method", "449716200001");
			map.put("online_money", realReturnMoney.toString());
			map.put("returned_money", String.valueOf(new ReturnMoneyService().returnMoney(goods.getOrder_code())));//已退款金额
			map.put("return_accm_money", goods.getExpected_return_accm_money().toString());//积分退款金额
			map.put("return_hjycoin_money", goods.getExpected_return_hjycoin_money().toString());//惠币退款金额
			map.put("return_ppc_money", goods.getExpected_return_ppc_money().toString());//储值金退款金额
			map.put("return_crdt_money", goods.getExpected_return_crdt_money().toString());//暂存款退款金额
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
			MDataMap logMap = new MDataMap();
			logMap.put("return_money_no", money_no);
			logMap.put("info", "用户退货，创建退款单");
			logMap.put("create_time", DateUtil.getNowTime());
			String create_user="";
			try {
				create_user=UserFactory.INSTANCE.create().getLoginName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logMap.put("create_user", create_user);
			logMap.put("status", map.get("status"));
			DbUp.upTable("lc_return_money_status").dataInsert(logMap);
		}else{
			// 0元单退货时退还使用的惠豆
			HjybeanService.addHjyBeanTimer(HjyBeanExecType.RETURN_GOODS, goods.getReturn_code(),goods.getOrder_code());
		}
		
		//自动退还微公社余额
		if(expected_return_group_money.compareTo(BigDecimal.ZERO)>0){
			
			//退返微公社部分
			GroupRefundInput groupRefundInput = new GroupRefundInput();
//			groupRefundInput.setTradeCode(money_no);
			groupRefundInput.setTradeCode(DbUp.upTable("oc_order_pay").one("order_code",goods.getOrder_code(),"pay_type","449746280009").get("pay_sequenceid"));
			groupRefundInput.setMemberCode(goods.getBuyer_code());
			groupRefundInput.setRefundMoney(expected_return_group_money.toString());
			groupRefundInput.setOrderCode(goods.getOrder_code());
			groupRefundInput.setRefundTime(DateUtil.getSysDateTimeString());
			groupRefundInput.setRemark("退货自动退还微公社余额");
			groupRefundInput.setBusinessTradeCode(money_no);//一个流水值退一次
//			new GroupPayService().groupRefundSome(groupRefundInput, goods.getSeller_code());
			
			ApiCallSupport<GroupRefundInput, GroupRefundResult> apiCallSupport=new ApiCallSupport<GroupRefundInput, GroupRefundResult>();
			GroupRefundResult refundResult = null;
			try {
				refundResult=apiCallSupport.doCallApi(
						bConfig("xmassystem.group_pay_url"),
						bConfig("xmassystem.group_pay_refund_face"),
						bConfig("xmassystem.group_pay_key"),
						bConfig("xmassystem.group_pay_pass"), groupRefundInput,
						new GroupRefundResult());
			} catch (Exception e) {
				//此处暂时流程，退款失败，不影响总流程
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
}

