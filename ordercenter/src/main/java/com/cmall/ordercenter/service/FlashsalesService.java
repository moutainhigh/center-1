package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.OcActivityFlashsales;
import com.cmall.dborm.txmodel.OcActivityFlashsalesExample;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 闪购信息 service
 * @author jlin
 *
 */
public class FlashsalesService extends BaseClass {

	/**
	 * 通过 sku_code 获取当前时间该闪购商品的价格
	 * <br>当没有查到信息时返回 null
	 * @param sku_code
	 * @return
	 */
	public BigDecimal getVipPrice(String sku_code) {
		
		String status="449746810001";//状态可用
		String now=DateUtil.getSysDateTimeString();
		Map<String, Object> map=DbUp.upTable("oc_activity_flashsales").dataSqlOne("SELECT s.vip_price from oc_activity_flashsales f LEFT JOIN oc_flashsales_skuInfo s on f.activity_code=s.activity_code where f.start_time <=:now and f.end_time>=:now and s.sku_code=:sku_code and s.`status`=:status and f.status='449746740002' ", new MDataMap("now",now,"sku_code",sku_code,"status",status));
		if(map!=null){
			return (BigDecimal)map.get("vip_price");
		}
		return null;
	}
	
	/**
	 * 查看该sku 当前所属的闪购活动
	 * @param sku_code
	 * @return
	 */
	public OcActivityFlashsales getFlashsalesActivity(String sku_code) {
		
		String status="449746810001";//状态可用
		
		String now=DateUtil.getSysDateTimeString();
		Map<String, Object> map=DbUp.upTable("oc_activity_flashsales").dataSqlOne("SELECT f.* from oc_activity_flashsales f LEFT JOIN oc_flashsales_skuInfo s on f.activity_code=s.activity_code where f.start_time <=:now and f.end_time>=:now and s.sku_code=:sku_code and s.`status`=:status and f.status='449746740002' ", new MDataMap("now",now,"sku_code",sku_code,"status",status));
		
		if(map!=null&&map.size()>0){
			OcActivityFlashsales activityFlashsales =new OcActivityFlashsales();
			activityFlashsales.setUid((String)map.get("uid"));
			activityFlashsales.setStartTime((String)map.get("start_time"));
			activityFlashsales.setEndTime((String)map.get("end_time"));
			activityFlashsales.setCreateTime((String)map.get("create_time"));
			activityFlashsales.setCreateUser((String)map.get("create_user"));
			activityFlashsales.setUpdateTime((String)map.get("update_time"));
			activityFlashsales.setUpdateUser((String)map.get("update_user"));
			activityFlashsales.setStatus((String)map.get("status"));
			activityFlashsales.setActivityCode((String)map.get("activity_code"));
			activityFlashsales.setActivityName((String)map.get("activity_name"));
			activityFlashsales.setAppCode((String)map.get("app_code"));
			activityFlashsales.setOuterActivityCode((String)map.get("outer_activity_code"));
			activityFlashsales.setRemark((String)map.get("remark"));
			
			return activityFlashsales;
		}
		return null;
	}
	/**
	 * 查询活动闪够信息
	 * @param activityCode
	 * @return
	 */
	public Map<String,Object> getActivityFlashsales(String activityCode){
		MDataMap map = new MDataMap();
		map.put("activity_code", activityCode);
		
		Map<String,Object> mapFlash = DbUp.upTable("oc_activity_flashsales").dataSqlOne("select * from oc_activity_flashsales where activity_code=:activity_code", map);
		return mapFlash;
	}
	
	/**
	 * 查看闪购的销量
	 * @param activity_code 为空时为全部闪购活动
	 * @param sku_code
	 * @param seller_code
	 * @return
	 */
	public int salesNum(String activity_code,String sku_code,String seller_code){
		
		String activity_type="449715400004";//默认为惠家有的闪购
		
		if(MemberConst.MANAGE_CODE_HPOOL.equals(seller_code)){
			activity_type="AT140820100002";
		}
		
		String sql="SELECT SUM(d.sku_num) as num from oc_orderdetail d LEFT JOIN oc_orderinfo o on d.order_code=o.order_code WHERE o.order_status!='4497153900010006' and o.delete_flag=0 and d.order_code in (SELECT order_code from oc_order_activity where activity_type=:activity_type  "+(StringUtils.isNotBlank(activity_code)?"and activity_code=:activity_code":"")+"  and sku_code=:sku_code) and d.sku_code=:sku_code ";
		
		
		MDataMap dataMap = new MDataMap();
		if(StringUtils.isNotBlank(activity_code)){
			dataMap.put("activity_code",activity_code);
		}
		dataMap.put("sku_code",sku_code);
		dataMap.put("activity_type",activity_type);
		
		Map<String, Object> map=DbUp.upTable("oc_orderdetail").dataSqlOne(sql, dataMap);
		if(map!=null&&map.size()>0){
			if(map.get("num")!=null){
				return Integer.valueOf(map.get("num").toString());
			}
		}
		
		return 0;
		
	}
	
}
