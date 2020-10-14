package com.cmall.ordercenter.service.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.model.FSkuPrice;
import com.cmall.ordercenter.model.api.ApiGetFpriceBySkucInput;
import com.cmall.ordercenter.model.api.ApiGetFpriceBySkucResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 根据sku 查询当前时间段闪购的价格
 * @author jl
 *
 */
public class ApiGetFpriceBySkucService extends RootApiForManage<ApiGetFpriceBySkucResult,ApiGetFpriceBySkucInput> {

	public ApiGetFpriceBySkucResult Process(ApiGetFpriceBySkucInput inputParam, MDataMap mRequestMap) {
		ApiGetFpriceBySkucResult result = new ApiGetFpriceBySkucResult();
		
		//查询 activity_code
		String now=DateUtil.getSysDateTimeString();
		Map<String, Object> map=DbUp.upTable("oc_activity_flashsales").dataSqlOne("SELECT activity_code from oc_activity_flashsales WHERE start_time<=:now AND end_time>=:now and app_code='"+MemberConst.MANAGE_CODE_HOMEHAS+"' ", new MDataMap("now",now));
		
		if(map==null||map.size()<1){
			result.setResultMessage(bInfo(939301103));
			result.setResultCode(939301103);
			return result;
		}
		
		//拼接查询参数
		String activity_code=(String)map.get("activity_code");
		String sku_codes[]=inputParam.getSku_codes().split(",");
		String sql="select sell_price,vip_price,sku_code from oc_flashsales_skuInfo where status='449746810001' and activity_code=:activity_code and (  ";
		MDataMap parMap=new MDataMap("activity_code",activity_code);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < sku_codes.length; i++) {
			sb.append("or  sku_code=:sku_code").append(i).append("  ");
			parMap.put("sku_code"+i, sku_codes[i]);
		}
		
		sql+=sb.substring(2)+" )";
		
		//查询闪购信息
		List<Map<String, Object>> list=DbUp.upTable("oc_flashsales_skuInfo").dataSqlList(sql, parMap);
		List<FSkuPrice> skuPricesList=new ArrayList<FSkuPrice>(list.size());
		for (Map<String, Object> map2 : list) {
			FSkuPrice fSkuPrice=new FSkuPrice();
			fSkuPrice.setSell_price((BigDecimal)map2.get("sell_price"));
			fSkuPrice.setVip_price((BigDecimal)map2.get("vip_price"));
			fSkuPrice.setSku_code((String)map2.get("sku_code"));
			skuPricesList.add(fSkuPrice);
		}
		
		result.setSkuPrices(skuPricesList);
		return result;
	}
	
}
