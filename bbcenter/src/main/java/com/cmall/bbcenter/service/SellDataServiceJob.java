package com.cmall.bbcenter.service;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
public class SellDataServiceJob extends RootJob
{
	public void doExecute(JobExecutionContext context) {
		String flag = this.doStatis();
		//System.out.println("flag :"+flag);
	}
	public String doStatis(){
		//System.out.println("statistic .........");
		List<MDataMap> listMap = DbUp.upTable("oc_order_pay").queryAll(
		"order_code", "", "", new MDataMap());
				String sql = "SELECT sku_code,count(sku_num) sku_num FROM ordercenter.oc_orderdetail  where ";
				String sq = "";
				for (int i = 0; i < listMap.size(); i++)
				{
					if (StringUtils.isBlank(sq))
					{
						sq = "order_code ='" + listMap.get(i).get("order_code") + "' ";
					} else
					{
						sq = sq + " or order_code='" + listMap.get(i).get("order_code")
								+ "'";
					}
				}
				sql = sql + sq + " group by sku_code ";
				List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail")
						.dataSqlList(sql, new MDataMap());
				for (Map<String, Object> mp : list)
				{
					MDataMap map = new MDataMap();
					map.put("sell_count", String.valueOf(mp.get("sku_num")));
					map.put("sku_code", String.valueOf(mp.get("sku_code")));
					DbUp.upTable("pc_skuinfo").dataUpdate(map, "sell_count", "sku_code");
				}
				return "";
		}
}
