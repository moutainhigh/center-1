package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.homehas.RsyncModOrdMedia;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

/**
 * 更改订单通路
 */
public class JobForUpdateOrderChannel extends RootJobForExclusiveLock {

	public void doExecute(JobExecutionContext context) {
		MDataMap defMap = DbUp.upTable("zw_define").one("define_dids","4699233300070001","parent_did","469923330007");
		// 支持更改通路的订单来源
		String codes = StringUtils.trimToEmpty(defMap.get("define_note"));
		
		List<String> errOrderList = new ArrayList<String>();
		
		String sql = "SELECT o.order_source,o.order_code,o.out_order_code,o.small_seller_code  FROM oc_order_pay_fromsms s, oc_orderinfo o WHERE o.order_code = s.order_code AND (o.small_seller_code != 'SI2003' OR (o.small_seller_code = 'SI2003' AND o.out_order_code != ''))";
		List<Map<String,Object>> mapList = DbUp.upTable("oc_order_pay_fromsms").dataSqlList(sql, new MDataMap());
		String orderSource,orderCode,outOrderCode,smallSellerCode;
		for(Map<String,Object> map : mapList) {
			orderSource = map.get("order_source").toString();
			orderCode = map.get("order_code").toString();
			outOrderCode = map.get("out_order_code").toString();
			smallSellerCode = map.get("small_seller_code").toString();
			
			// 如果不能更改通路则更新一下标识
			if(!codes.contains(orderSource)
					|| !checkOrderArea(orderCode)) {
				DbUp.upTable("oc_order_pay_fromsms").dataUpdate(new MDataMap("order_code",orderCode,"flag","2"), "flag", "order_code");
				continue;
			}
			
			if("SI2003".equals(smallSellerCode) && StringUtils.isNotBlank(outOrderCode)) {
				// 更新LD商品通路
				if(!updateLdChannel(outOrderCode)) {
					errOrderList.add(orderCode);
					continue;
				}
			}
			
			// 更新订单通路
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code", orderCode, "order_source", "449715190025", "order_channel", "449747430023","order_type","449715200005"), "order_source,order_type,order_channel", "order_code");
			// 更新标识
			DbUp.upTable("oc_order_pay_fromsms").dataUpdate(new MDataMap("order_code",orderCode,"flag","1"), "flag", "order_code");
		}
		
		if(!errOrderList.isEmpty()) {
			LogFactory.getLog(getClass()).warn("JobForUpdateOrderChannel failed! " + StringUtils.join(errOrderList,","));
		}
	}
	
	private boolean updateLdChannel(String outOrderCode) {
		RsyncModOrdMedia rsync = new RsyncModOrdMedia();
		rsync.upRsyncRequest().setOrd_id(outOrderCode);
		return rsync.doRsync();
	}

	// 检查订单的收货地址是否可以支持更新通路
	private boolean checkOrderArea(String orderCode) {
		String areaCode = (String)DbUp.upTable("oc_orderadress").dataGet("area_code", "", new MDataMap("order_code", orderCode));
		if(areaCode == null) return false;
		
		// 北京市和贵州省的不支持更新
		if(areaCode.startsWith("11") || areaCode.startsWith("52")) {
			return false;
		}
		
		return true;
	}
}
