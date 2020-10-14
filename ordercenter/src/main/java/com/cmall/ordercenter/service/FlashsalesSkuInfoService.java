package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

public class FlashsalesSkuInfoService extends BaseClass{

	/**
	 * 查询闪购商品的优惠价格
	 * @param activity_code
	 * @param sku_code
	 * @param ret
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getVipPrice(String activity_code,String sku_code,RootResult ret) throws Exception {
		
		try {
			BigDecimal vip_price=(BigDecimal)DbUp.upTable("oc_flashsales_skuInfo").dataGet("vip_price", "activity_code=:activity_code and sku_code=:sku_code and status=:status ", new MDataMap("activity_code",activity_code,"sku_code",sku_code,"status","449746810001"));
			return vip_price;
		} catch (Exception e) {
			ret.setResultCode(939301116);
			ret.setResultMessage(bInfo(939301116));
			throw new Exception(ret.getResultMessage());
		}
	}
	
	
	/***
	 * 
	 * 获取剩余的闪购促销库存
	 * @param skuCode
	 * @param activity_code
	 * @return
	 */
	public int salesNumSurplus (String skuCode,String activityCode){
		BigDecimal usedNmu = new BigDecimal(0);
		//查询成功订单参与当前闪购的商品数量
		String csql="SELECT SUM(sku_num) as sm from oc_orderdetail where order_code in (SELECT order_code from oc_order_activity where order_code in (SELECT order_code from oc_orderinfo where order_status <>'4497153900010006') AND sku_code=:skuCode and activity_code=:activityCode) and  sku_code=:skuCode  ";
		List<Map<String, Object>> list=DbUp.upTable("oc_order_activity").dataSqlList(csql, new MDataMap("activityCode",activityCode,"skuCode",skuCode));
		if(list!=null&&list.size()>0){
			if(list.get(0).get("sm")!=null){
				usedNmu=(BigDecimal)list.get(0).get("sm");
			}
		}
		
		BigDecimal sales_num = new BigDecimal(0);
		try {
			sales_num = (BigDecimal)DbUp.upTable("oc_flashsales_skuInfo").dataGet("sales_num", "activity_code=:activity_code and sku_code=:sku_code and status=:status ", new MDataMap("activity_code",activityCode,"sku_code",skuCode,"status","449746810001"));
		} catch (Exception e) {
			sales_num=usedNmu;
		}
		
		return Integer.valueOf(String.valueOf(sales_num.subtract(usedNmu)));
	}
	
	/**
	 * 判断是否进行闪购 [惠家有专用版]
	 * @param skuCode
	 * @param activityCode
	 * @param buyerCode 购买人
	 * @param skuNum 购买数量
	 * @return
	 */
	public boolean isFlashActiveNow(String skuCode,String activityCode,String buyerCode,int skuNum){
		
		String now=DateUtil.getSysDateTimeString();
		MDataMap acticeMap=DbUp.upTable("oc_activity_flashsales").oneWhere("activity_code", "", "start_time<=:now and end_time>:now and status=:status and activity_code=:activity_code and app_code=:app_code","now",now,"status","449746740002","activity_code",activityCode,"app_code",MemberConst.MANAGE_CODE_HOMEHAS);
		
		if(acticeMap==null||acticeMap.size()<1){
			return false;
		}
		
		
		MDataMap flashSkuMap=DbUp.upTable("oc_flashsales_skuInfo").oneWhere("sales_num,purchase_limit_vip_num,purchase_limit_order_num,purchase_limit_day_num", "", "status=:status and activity_code=:activity_code and sku_code=:sku_code","status","449746810001","activity_code",activityCode,"sku_code",skuCode);
		
		if(flashSkuMap==null||flashSkuMap.size()<1){
			return false;
		}
		
		int purchase_limit_vip_num= Integer.valueOf(flashSkuMap.get("purchase_limit_vip_num"));//每会员限购数
		int purchase_limit_order_num= Integer.valueOf(flashSkuMap.get("purchase_limit_order_num"));//每单限购数量
		int purchase_limit_day_num= Integer.valueOf(flashSkuMap.get("purchase_limit_day_num"));//每日限购数
		
		
		if(skuNum>0){
			//判断每单限购数量
			if(purchase_limit_order_num<skuNum){
				return false;
			}
			
			if(StringUtils.isNotBlank(buyerCode)){
				//判断每会员限购数
				int my_limit_order_num = 0;
				String sql_order = "select sum(sku_num) as num from oc_orderdetail where order_code in ("
						+ " select o.order_code from oc_orderinfo o LEFT JOIN oc_order_activity a on o.order_code=a.order_code where o.order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005') and o.buyer_code = :buyer_code and a.activity_code=:activity_code and a.activity_type=:activity_type "
						
						+ ") and sku_code=:sku_code ";
				Map<String, Object> alSkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sql_order, new MDataMap("buyer_code",buyerCode,"sku_code",skuCode,"activity_type","449715400004","activity_code",activityCode));
				
				if (alSkuNum != null && alSkuNum.size()>0 && alSkuNum.get("num")!=null) {
					my_limit_order_num=Integer.valueOf(String.valueOf(alSkuNum.get("num")));
				}
				
				if((my_limit_order_num+skuNum) > purchase_limit_vip_num){
					return false;
				}
				
				
				//判断每日限购数
				String today=DateUtil.getSysDateString();//今天的日期
				int my_limit_day_num = 0;
				String sql_day = "select sum(sku_num) as num from oc_orderdetail where order_code in ( "
						+ "select o.order_code from oc_orderinfo o LEFT JOIN oc_order_activity a on o.order_code=a.order_code where LEFT(o.create_time,10)=:today and o.order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005') and o.buyer_code = :buyer_code and a.activity_code=:activity_code and a.activity_type=:activity_type "
						+ ") and sku_code=:sku_code ";
				Map<String, Object> daySkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sql_day, new MDataMap("buyer_code",buyerCode,"sku_code",skuCode,"today",today,"activity_type","449715400004","activity_code",activityCode));
				
				if (daySkuNum != null && daySkuNum.size()>0 && daySkuNum.get("num")!=null) {
					my_limit_day_num=Integer.valueOf(String.valueOf(daySkuNum.get("num")));
				}
				
				if((my_limit_day_num+skuNum) > purchase_limit_day_num){
					return false;
				}
			}
			//判断促销库存
			if(salesNumSurplus(skuCode, activityCode)<skuNum){
				return false;
			}
		}
		return true;
	}
	
}
