package com.cmall.groupcenter.active.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.active.ActiveConst;
import com.cmall.groupcenter.active.ActiveType;
import com.cmall.groupcenter.active.BaseActive;
import com.cmall.groupcenter.active.IActiveForProduct;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 折扣类活动
 * @author jlin
 *
 */
public class ActiveForDiscount implements IActiveForProduct {

	
	public BaseActive doProcess(ActiveType activeType,ProductSkuInfo pcSkuInfo, int skuNum,String appCode,MDataMap paramsExt, RootResultWeb rootResult) {
		
		String sql="SELECT a.activity_code,a.activity_type_code,a.activity_title,a.start_time,a.end_time,a.pri_sort,a.create_time,a.create_user,a.update_time,a.update_user,a.remark,d.discount from gc_activity_discount d LEFT JOIN gc_activity_info a on d.activity_code=a.activity_code  " +
				"	where a.start_time<=:now and a.end_time>=:now and a.app_code=:app_code and d.sku_code=:sku_code and d.status=:status ORDER BY a.create_time LIMIT 1";
		
		String now=DateUtil.getSysDateTimeString();
		List<Map<String, Object>> list=DbUp.upTable("gc_activity_discount").dataSqlList(sql, new MDataMap("now",now,"sku_code",pcSkuInfo.getSkuCode(),"status",ActiveConst.ACTIVE_STATUS_USE,"app_code",appCode));
		
		if(list==null||list.size()<1){
			return null;
		}
		
		Map<String, Object> mDataMap=list.get(0);
		//开始计算折扣
		BigDecimal discount= (BigDecimal)mDataMap.get("discount");
		BigDecimal marketPrice = pcSkuInfo.getMarketPrice();
		
		
		BaseActive baseActive = new BaseActive();
		baseActive.setActivity_code((String)mDataMap.get("activity_code"));
		baseActive.setActivity_type_code((String)mDataMap.get("activity_type_code"));
		baseActive.setActivity_title((String)mDataMap.get("activity_title"));
		baseActive.setStart_time((String)mDataMap.get("start_time"));
		baseActive.setEnd_time((String)mDataMap.get("end_time"));
		baseActive.setPri_sort((String)mDataMap.get("pri_sort"));
		baseActive.setRemark((String)mDataMap.get("remark"));
		baseActive.setApp_code((String)mDataMap.get("app_code"));
		baseActive.setActivePrice((marketPrice.multiply(discount).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
		
		return baseActive;
	}


}
