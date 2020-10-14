package com.cmall.groupcenter.active.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.active.ActiveConst;
import com.cmall.groupcenter.active.ActiveType;
import com.cmall.groupcenter.active.BaseActive;
import com.cmall.groupcenter.active.IActiveForProduct;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 限时限量
 * @author jlin
 *
 */
public class ActiveForFlash extends BaseClass implements IActiveForProduct {

	/***
	 * 
	 * 统计已经使用的促销库存
	 * @param skuCode
	 * @param activity_code
	 * @return
	 */
	private int salesNumUsed (String skuCode,String activityCode){
		BigDecimal usedNmu = new BigDecimal(0);
		//查询成功订单参与当前闪购的商品数量
		String csql="SELECT SUM(sku_num) as sm from oc_orderdetail where order_code in (SELECT order_code from oc_order_activity where order_code in (SELECT order_code from oc_orderinfo where order_status <>'4497153900010006') AND sku_code=:skuCode and activity_code=:activityCode) and  sku_code=:skuCode  ";
		List<Map<String, Object>> list=DbUp.upTable("oc_order_activity").dataSqlList(csql, new MDataMap("activityCode",activityCode,"skuCode",skuCode));
		if(list!=null&&list.size()>0){
			if(list.get(0).get("sm")!=null){
				usedNmu=(BigDecimal)list.get(0).get("sm");
			}
		}
		
		return Integer.valueOf(String.valueOf(usedNmu));
	}

	
	public BaseActive doProcess(ActiveType activeType,ProductSkuInfo pcSkuInfo, int skuNum, String appCode,MDataMap paramsExt, RootResultWeb rootResult) {
		
		String buyerCode=paramsExt.get(ActiveConst.ACTIVE_PARAMS_BUYERCODE);
		String order_shopcar=paramsExt.get(ActiveConst.ACTIVE_PARAMS_ORDER_SHOPCAR);//标示购物车中使用该活动，所有限制都不要不要滴
		
		//获取当前正在运行的闪购活动
		String sql="SELECT a.activity_code,a.activity_type_code,a.activity_title,a.start_time,a.end_time,a.pri_sort,a.create_time,a.create_user,a.update_time,a.update_user,a.remark,d.outer_activity_code from gc_activity_flash d LEFT JOIN gc_activity_info a on d.activity_code=a.activity_code " +
				" where a.start_time<=:now and a.end_time>=:now and a.app_code=:app_code and d.`status`=:status  and d.flag_type=:flag_type ORDER BY a.end_time desc LIMIT 1";
		
		String now=DateUtil.getSysDateTimeString();
		List<Map<String, Object>> list=DbUp.upTable("gc_activity_discount").dataSqlList(sql, new MDataMap("now",now,"sku_code",pcSkuInfo.getSkuCode(),"status",ActiveConst.ACTIVE_STATUS_USE,"app_code",appCode,"flag_type","4497469800030001"));
				
		if(list==null||list.size()<1){
			return null;
		}
		
		Map<String, Object> mDataMap=list.get(0);
		
		String activity_code=(String)mDataMap.get("activity_code");
		String outer_activity_code=(String)mDataMap.get("outer_activity_code");
		
		//获取活动中针对该sku设置的限制
		List<Map<String, Object>>  flash_sku_list = DbUp.upTable("gc_activity_flash_sku").dataQuery("vip_price,sales_num,purchase_limit_vip_num,purchase_limit_order_num,purchase_limit_day_num,surplus_num", "", "activity_code=:activity_code and sku_code=:sku_code and status='449746810001' ", new MDataMap("activity_code",activity_code,"sku_code",pcSkuInfo.getSkuCode()), 0, 1);
		
		if(flash_sku_list==null||flash_sku_list.size()<1){
			return null;
		}
		
		
		Map<String, Object> flashSkuMap=flash_sku_list.get(0);
		BigDecimal vip_price= (BigDecimal)flashSkuMap.get("vip_price");//优惠价格
		int purchase_limit_vip_num= (Integer)flashSkuMap.get("purchase_limit_vip_num");//每会员限购数
		int purchase_limit_order_num= (Integer)flashSkuMap.get("purchase_limit_order_num");//每单限购数量
		int purchase_limit_day_num= (Integer)flashSkuMap.get("purchase_limit_day_num");//每日限购数
		int sales_num= (Integer)flashSkuMap.get("sales_num");//促销库存
//		int surplus_num= (Integer)flashSkuMap.get("surplus_num");//剩余库存
			
		
		//此段代码供购物车使用，与本活动无关
		if(Integer.valueOf(bConfig("homepool.skuMaxNum"))>purchase_limit_order_num){
			paramsExt.put("purchase_limit_order_num", String.valueOf(purchase_limit_order_num));
		}else{
			paramsExt.put("purchase_limit_order_num", bConfig("homepool.skuMaxNum"));
		}
		
		
		if(StringUtils.isBlank(order_shopcar)){//标示购物车中使用该活动，所有限制都不要不要滴
			
		
		
		//开始计算商品通过该活动的最终价格
			
		//2.判断每单的限购数量
		if(skuNum > purchase_limit_order_num){
			rootResult.inErrorMessage(918510001, pcSkuInfo.getSkuCode(),purchase_limit_order_num);
			return null;
		}
			
		//存在购物车不登陆的情况，如果用户不登陆，不需要判断他的限购数
		if(StringUtils.isNotBlank(buyerCode)){
			
			//3.判断每会员限购数
			int my_limit_order_num = 0;
			String sql_order = "select sum(sku_num) as num from oc_orderdetail where order_code in ("
					+ " select o.order_code from oc_orderinfo o LEFT JOIN oc_order_activity a on o.order_code=a.order_code where o.order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005') and o.buyer_code = :buyer_code and a.activity_code=:activity_code and a.activity_type=:activity_type "
					
					+ ") and sku_code=:sku_code ";
			Map<String, Object> alSkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sql_order, new MDataMap("buyer_code",buyerCode,"sku_code",pcSkuInfo.getSkuCode(),"activity_type","AT140820100002","activity_code",activity_code));
			
			if (alSkuNum != null && alSkuNum.size()>0 && alSkuNum.get("num")!=null) {
				my_limit_order_num=Integer.valueOf(String.valueOf(alSkuNum.get("num")));
			}
			
			if((my_limit_order_num+skuNum) > purchase_limit_vip_num){
				rootResult.inErrorMessage(918510002, pcSkuInfo.getSkuCode(),purchase_limit_vip_num);
				return null;
			}
			
			
			//4.判断每日限购数
			String today=DateUtil.getSysDateString();//今天的日期
			int my_limit_day_num = 0;
			String sql_day = "select sum(sku_num) as num from oc_orderdetail where order_code in ( "
					+ "select o.order_code from oc_orderinfo o LEFT JOIN oc_order_activity a on o.order_code=a.order_code where LEFT(o.create_time,10)=:today and o.order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005')  and o.buyer_code = :buyer_code and a.activity_code=:activity_code and a.activity_type=:activity_type "
					+ ") and sku_code=:sku_code ";
			Map<String, Object> daySkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sql_day, new MDataMap("buyer_code",buyerCode,"sku_code",pcSkuInfo.getSkuCode(),"today",today,"activity_type","AT140820100002","activity_code",activity_code));
			
			if (daySkuNum != null && daySkuNum.size()>0 && daySkuNum.get("num")!=null) {
				my_limit_day_num=Integer.valueOf(String.valueOf(daySkuNum.get("num")));
			}
			
			if((my_limit_day_num+skuNum) > purchase_limit_day_num){
				rootResult.inErrorMessage(918510003, pcSkuInfo.getSkuCode(),purchase_limit_day_num);
				return null;
			}
			
		}
		
		//现在库存数量的增减无法有效的监听，所有剩余库存只能进行统计
		//1.剩余库存
		if(skuNum > (sales_num-salesNumUsed(pcSkuInfo.getSkuCode(), activity_code))){
			rootResult.inErrorMessage(918510004, pcSkuInfo.getSkuCode());
			return null;
		}
		
		}	
		
		BaseActive baseActive = new BaseActive();
		baseActive.setActivity_code((String)mDataMap.get("activity_code"));
		baseActive.setActivity_type_code((String)mDataMap.get("activity_type_code"));
		baseActive.setActivity_title((String)mDataMap.get("activity_title"));
		baseActive.setStart_time((String)mDataMap.get("start_time"));
		baseActive.setEnd_time((String)mDataMap.get("end_time"));
		baseActive.setPri_sort((String)mDataMap.get("pri_sort"));
		baseActive.setRemark((String)mDataMap.get("remark"));
		baseActive.setApp_code(appCode);
		baseActive.setActivePrice(vip_price);
		baseActive.setOuter_activity_code(outer_activity_code);
		
		return baseActive;
	}
	
}
