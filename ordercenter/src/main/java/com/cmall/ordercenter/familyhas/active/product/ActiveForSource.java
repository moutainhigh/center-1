package com.cmall.ordercenter.familyhas.active.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.familyhas.active.BaseActive;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 *内购处理 
 * 
 */
public class ActiveForSource extends BaseActive{

	private static final String VIP_TYPE_EMPLORER = "4497469400050001";				//内部员工
	@Override
	protected Map<String, ActiveReturn> activeExc(
			List<ActiveReq> activeRequests, RootResultWeb activeResult) {
		Map<String, ActiveReturn> result = new HashMap<String, ActiveReturn>();
		
		Map<String,BigDecimal> skuCodeMap = new HashMap<String, BigDecimal>();
		Map<String,BigDecimal> skuCodeCostPriceMap = new HashMap<String, BigDecimal>();
		Map<String,String> skuSmallSellerCodeMap = new HashMap<String, String>();
		Map<String,String> skuValidateFlagMap = new HashMap<String, String>();
		for (ActiveReq activeReq : activeRequests) {
			skuCodeMap.put(activeReq.getSku_code(), BigDecimal.ZERO);
		}
		if (skuCodeMap.isEmpty()) {
			return result;
		}
		String sSql = "select ps.sku_code sku_code,ps.sell_price sell_price,pp.cost_price cost_price,pp.small_seller_code small_seller_code,pp.validate_flag validate_flag from pc_skuinfo ps,pc_productinfo pp where ps.product_code = pp.product_code and " +
				" ps.sku_code in ('"+StringUtils.join(skuCodeMap.keySet(),"','")+"') ";
		List<Map<String,Object>> skuInfoMapList = DbUp.upTable("pc_skuinfo").dataSqlList(sSql, null);
		for (Map<String,Object> map : skuInfoMapList) {
			skuCodeMap.put(map.get("sku_code").toString(), new BigDecimal(map.get("sell_price").toString())) ;
			skuCodeCostPriceMap.put(map.get("sku_code").toString(), new BigDecimal(map.get("cost_price").toString()));
			skuSmallSellerCodeMap.put(map.get("sku_code").toString(),map.get("small_seller_code").toString());
			skuValidateFlagMap.put(map.get("sku_code").toString(), map.get("validate_flag").toString());
		}
		
		//用户编号为key，对应商品信息为value
		Map<String,List<ActiveReq>> buyerActiveMap = new HashMap<String, List<ActiveReq>>();
		for (ActiveReq activeReq : activeRequests) {
			List<ActiveReq> activeReqList = new ArrayList<ActiveReq>(); 
			if (buyerActiveMap.containsKey(activeReq.getBuyer_code())) {
				activeReqList = buyerActiveMap.get(activeReq.getBuyer_code());
			}
			activeReqList.add(activeReq);
			buyerActiveMap.put(activeReq.getBuyer_code(), activeReqList);
		}
//		this.checkVipSpecialLimitCount(activeRequests,activeResult);
		for (ActiveReq activeReq : activeRequests) {
			ActiveReturn active = new ActiveReturn();
			
			//不是内购员工或不是内购日或不是LD商品,不参加内购活动（2015-08-13加上是否LD商品判断条件，惠家有3.8.2需求内购规则调整）
			//只有LD非虚拟商品才符合参加内购条件（2015-09-02）
			if(!this.checkIsVipSpecialAcvitity(activeReq.getBuyer_code(),skuSmallSellerCodeMap.get(activeReq.getSku_code()),skuValidateFlagMap.get(activeReq.getSku_code()))){
				active.setActivity_price(skuCodeMap.get(activeReq.getSku_code()));
				active.setUse_activity(false);
			}else{
				active.setActivity_code(bConfig("familyhas.vipSpecialActivityCode"));
				active.setActivity_type(bConfig("familyhas.vipSpecialActivityTypeCode"));
				active.setUse_activity(true);

				BigDecimal costPrice = skuCodeCostPriceMap.get(activeReq.getSku_code());// 商品成本价
				BigDecimal endPrice = null;
				/**
				 * 内购价=成本价+23
				 */
				String vipSpecialPrice = bConfig("productcenter.vipSpecialPrice");
				endPrice = costPrice.add(new BigDecimal(vipSpecialPrice)).setScale(1,BigDecimal.ROUND_DOWN).setScale(0,BigDecimal.ROUND_HALF_UP); // 内购价需要取整，为了支持LD的规则
				active.setActivity_price(endPrice);
			}
			active.setSku_code(activeReq.getSku_code());
			active.setProduct_code(activeReq.getProduct_code());
			result.put(activeReq.getSku_code()+"_"+activeReq.getBuyer_code(), active);
		}
		return result;
	}

	/**
	 * 判断会员是否符合参加内购条件
	 * @param activeRequests
	 * @return RootResultWeb
	 */
	public void checkVipSpecialLimitCount(List<ActiveReq> activeRequests,RootResultWeb resultWeb){
		if (null == activeRequests || activeRequests.isEmpty()) {
			return;
		}
		//用户编号为key，对应商品信息为value
		Map<String,List<ActiveReq>> buyerActiveMap = new HashMap<String, List<ActiveReq>>();
		for (ActiveReq activeReq : activeRequests) {
			PlusModelSkuInfo info = new PlusSupportProduct().upSkuInfoBySkuCode(activeReq.getSku_code(), activeReq.getBuyer_code());
			if(info.getBuyStatus()!=1||StringUtility.isNull(info.getEventCode())){
				List<ActiveReq> activeReqList = new ArrayList<ActiveReq>(); 
				if (buyerActiveMap.containsKey(activeReq.getBuyer_code())) {
					activeReqList = buyerActiveMap.get(activeReq.getBuyer_code());
				}
				activeReqList.add(activeReq);
				buyerActiveMap.put(activeReq.getBuyer_code(), activeReqList);
			}
		}
		int sameProductForMonthLimit = Integer.parseInt(bConfig("ordercenter.sameProductForMonthLimit"));		//用户每月同件sku内购限购数
		int allProductForMonthLimit = Integer.parseInt(bConfig("ordercenter.allProductForMonthLimit"));		//用户每月内购限购数
		
		//开始判断
		for (String buyerCode : buyerActiveMap.keySet()) {
			
			List<ActiveReq> skuList = buyerActiveMap.get(buyerCode);
			if (null == skuList || skuList.isEmpty()) {
				continue;
			}
			//不是内购员工或不是内购日,不参加内购活动进行下次循环
			if(!this.checkIsVipSpecialAcvitity(buyerCode)){
				continue;
			}
			/**
			 * 传入的商品数量
			 */
			Map<String,Integer> buyProductCount = new HashMap<String, Integer>();
//			MDataMap albuy = new MDataMap();
			for (ActiveReq activeReq : skuList) {
				int count = 0;
				if (buyProductCount.containsKey(activeReq.getProduct_code())) {
					count = buyProductCount.get(activeReq.getProduct_code());
				}
				count += activeReq.getSku_num();
				buyProductCount.put(activeReq.getProduct_code(), count);
//				albuy.put(activeReq.getProduct_code(), "");
			}
			if (buyProductCount.keySet().size() > allProductForMonthLimit) {
				//大于每月全部商品限购数
				resultWeb.setResultCode(939302102);
				resultWeb.setResultMessage(bInfo(939302102,allProductForMonthLimit));
				return;
			}
			/**
			 * 与历史内购订单的商品数相加判断是否相符合
			 */
			StringBuffer censusSql = new StringBuffer();  
			censusSql.append(" select SUM(od.sku_num) as num,od.product_code from oc_orderdetail od,oc_orderinfo oi,oc_order_activity oa ");
			censusSql.append(" where od.order_code = oi.order_code and oa.order_code=od.order_code and oi.buyer_code='"+buyerCode+"' ");
			censusSql.append(" and od.product_code = oa.product_code and oi.order_status!='4497153900010006' and oi.delete_flag='0' ");
			censusSql.append(" and oa.activity_type='"+bConfig("familyhas.vipSpecialActivityTypeCode")+"' and date_format(oi.create_time,'%Y-%m')=date_format(curdate(),'%Y-%m') "); 
			censusSql.append(" and od.gift_flag='1' group by od.product_code ");
		    List<Map<String, Object>> saleList=DbUp.upTable("oc_orderinfo").dataSqlList(censusSql.toString(),null);
		    /**
			 * 已经购买的商品数量
			 */
			Map<String,Integer> saleProductCount = new HashMap<String, Integer>();
		    for (Map<String, Object> map : saleList) {
		    	int count = 0;
		    	if (saleProductCount.containsKey(map.get("product_code").toString())) {
					count = saleProductCount.get(map.get("product_code").toString());
				}
				count += Integer.parseInt(String.valueOf((null == map.get("num"))?"0":map.get("num")));
				saleProductCount.put(map.get("product_code").toString(), count);
		    }
		    //以前购买的加上现在传入的商品
		    Map<String,Integer> allProductCountMap = buyProductCount;
		    for (String productCode : saleProductCount.keySet()) {
		    	int count = saleProductCount.get(productCode);
		    	if (allProductCountMap.containsKey(productCode)) {
		    		count += allProductCountMap.get(productCode);
				}
		    	allProductCountMap.put(productCode, count);
			}
		    if (buyProductCount.keySet().size() > allProductForMonthLimit) {
				//大于每月商品种类数
		    	resultWeb.setResultCode(939302102);
				resultWeb.setResultMessage(bInfo(939302102,allProductForMonthLimit));
				return;
			}
		    int surplusCount = 0;
			for (String productCode : allProductCountMap.keySet()) {
				if ((allProductCountMap.get(productCode)) > sameProductForMonthLimit) {
					surplusCount = sameProductForMonthLimit- (null == saleProductCount.get(productCode) ? 0 : saleProductCount.get(productCode));
					MDataMap productNameMap = DbUp.upTable("pc_productinfo").oneWhere("product_name", "", "", "product_code",productCode);
					resultWeb.setResultCode(939302101);
					resultWeb.setResultMessage(bInfo(939302101,"“"+productNameMap.get("product_name")+"”",surplusCount < 0 ? 0 : surplusCount));
					break;
				}
			}
		}
	}
	/**
	 * 判断是否符合参加内购条件
	 */
	public boolean checkIsVipSpecialAcvitity(String buyerCode){
		//用户类型或时间不符合，不能参加内购
		if (!checkIsVipSpecialForFamilyhas(buyerCode)
				|| !checkIsVipSpecialDayForFamilyhas() ) {
			return false;
		}
		return true;
	}
	/**
	 * 判断是否符合参加内购条件（2015-08-13加上是否LD商品判断条件，惠家有3.8.2需求内购规则调整只有LD商品参加内购）
	 * (2015-90-02加上是否LD非虚拟商品判断，只有非虚拟商品才能参加内购)
	 */
	public boolean checkIsVipSpecialAcvitity(String buyerCode,String smallSellerCode,String validateFlag){
		//用户类型或时间不符合，不能参加内购
		if (!checkIsVipSpecialForFamilyhas(buyerCode)
				|| !checkIsVipSpecialDayForFamilyhas() 
				|| !"SI2003".equals(smallSellerCode)
				|| !"N".equals(validateFlag)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否是内购员工（惠家有）
	 * @return
	 */
	public boolean checkIsVipSpecialForFamilyhas(String userCode){
		if (StringUtils.isEmpty(userCode)) {
			return false;
		}
		MDataMap mData = DbUp.upTable("mc_extend_info_homehas").oneWhere(
				"vip_type", null, null, "member_code", userCode,"vip_type",VIP_TYPE_EMPLORER);
		if(mData == null || mData.isEmpty() ){
			return false;
		}
		return true;
	}
	/**
	 * 判断是否是内购日(惠家有)
	 * @return
	 */
	public boolean checkIsVipSpecialDayForFamilyhas(){
		String weekDay = DateUtil.getSystemWeekdayString();
		String vipSpecialDays = bConfig("productcenter.vipSpecialDays");		//内购日
		boolean flag = false;
		for (String vipSpecialDay : vipSpecialDays.split(",")) {
			if (vipSpecialDay.equals(weekDay)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			return true;
		}
		return false;
	}
	
	/**
	 * 传入用户类型判断是否参加内购
	 * @param userType
	 * @return
	 */
	public boolean checkIsVipSpecial(String userType){
		if ("4497469400050001".equals(userType) && this.checkIsVipSpecialDayForFamilyhas()) {
			return true;
		}
		return false;
	}
	
}
