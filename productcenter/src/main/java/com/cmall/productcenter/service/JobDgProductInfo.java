package com.cmall.productcenter.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.HttpClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 达观定时修改所需上报的商品
 */
public class JobDgProductInfo extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		List<Map<String, Object>> list = DbUp.upTable("lc_job_dg_log").dataSqlList("select zid, uid, itemid, type from lc_job_dg_log where isFinish = 'N' order by create_time asc limit 0,10", new MDataMap());
		for(Map<String, Object> map : list) {
			String zid = MapUtils.getString(map, "zid", "");
			String uid = MapUtils.getString(map, "uid", "");
			String itemid = MapUtils.getString(map, "itemid", "");
			String type = MapUtils.getString(map, "type", "");
			
			boolean flag = true;
			// 忽略橙意会员卡商品
			if(!itemid.equals(bConfig("xmassystem.plus_product_code"))) {
				flag = itemOperate(itemid, type);
			}
			
			if(flag) {
				updateJobLog(zid, uid);
			}
		}
	}
	
	private boolean itemOperate(String itemid, String type) {
		boolean flag = false;
		Map<String, Object> product = DbUp.upTable("pc_productinfo").dataSqlOne("select i.product_code, i.product_name, i.labels, i.min_sell_price, i.max_sell_price, i.update_time from pc_productinfo i "
				+ "where i.product_code = '" + itemid + "'", new MDataMap());
		
		String cateid = "";
		String cmd = "add";
		int price = 0, item_modify_time = 0;
		String title = MapUtils.getString(product, "product_name", "");
		String item_tags = MapUtils.getString(product, "labels", "");
		String minPrice = MapUtils.getString(product, "min_sell_price", "");
		String maxPrice = MapUtils.getString(product, "max_sell_price", "");
		String update_time = MapUtils.getString(product, "update_time", "");
		
		try {
			item_tags = item_tags.replaceAll(" ", ";");
			item_tags = item_tags.replaceAll(",", ";");
			if("down".equals(type)) {
				cmd = "delete";
			}
			if(!"".equals(minPrice) && !"0".equals(minPrice)) {
				price = new BigDecimal(minPrice).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue(); 
			}else if(!"".equals(maxPrice) && !"0".equals(maxPrice)) {
				price = new BigDecimal(maxPrice).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			}
			if(!"".equals(update_time)) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				item_modify_time = (int) (dateFormatter.parse(update_time).getTime() / 1000);
			}
			cateid = getProductCategory(itemid);
			
			String result = doDgUp(cmd, itemid, title, cateid, item_tags, price, item_modify_time);
			if(!StringUtils.isEmpty(result)) {
				JSONObject resultObject = JSONObject.fromObject(result);
				String status = resultObject.getString("status");
				if("OK".equals(status)) {
					flag = true;
				}
			}else {
				System.out.println("上报数据获取返回结果为空，有可能是连接超时！！！，请注意");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private String doDgUp(String cmd, String itemid, String title, String cateid, String item_tags, int price, int item_modify_time) {
		String url = bConfig("productcenter.dg_up_url") + bConfig("productcenter.dg_app_name");
		String appId = bConfig("productcenter.dg_app_id");
		
		JSONObject fields = new JSONObject();
        fields.put("itemid", itemid);
        fields.put("title", title);
        fields.put("cateid", cateid);
        fields.put("item_tags", item_tags);
        if(!"delete".equals(cmd)) {
        	fields.put("item_modify_time", item_modify_time);
        }
        fields.put("price", price);
		
        JSONObject content = new JSONObject();
        content.put("cmd", cmd);
        content.put("fields", fields);
        
        JSONArray contents = new JSONArray();
        contents.add(content);

        JSONObject goodParams = new JSONObject();
        goodParams.put("appid", appId);
        goodParams.put("table_name", "item");
        goodParams.put("table_content", contents);
        
        String result = "";
        try {
        	result = HttpClientSupport.doPostDg(url, goodParams.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String getProductCategory(String productCode) {
		String categorys = "";
		String sql = "select r.category_code, s.level, s.parent_code from uc_sellercategory_product_relation r, uc_sellercategory s where r.product_code = :product_code and r.seller_code = :seller_code "
					+ "and r.category_code = s.category_code and s.flaginable = '449746250001'";
		List<Map<String, Object>> list = DbUp.upTable("uc_sellercategory_product_relation").dataSqlList(sql, new MDataMap("product_code", productCode, "seller_code", "SI2003"));
		for(Map<String, Object> map : list) {
			String level = MapUtils.getString(map, "level", "");
			String category = MapUtils.getString(map, "category_code", "");
			String parent_code = MapUtils.getString(map, "parent_code", ""); 
			
			while (!"2".equals(level)) {
				String parentSql = "select s.category_code, s.parent_code, s.level from uc_sellercategory s where s.category_code = :parent_code and s.flaginable = '449746250001'";
				Map<String, Object> parent = DbUp.upTable("uc_sellercategory_product_relation").dataSqlOne(parentSql, new MDataMap("parent_code", parent_code));
				if(parent == null) {
					break;
				}else {
					level = MapUtils.getString(parent, "level", "");
					parent_code = MapUtils.getString(parent, "parent_code", ""); 
					category = MapUtils.getString(parent, "category_code", "") + "_" + category;
				}
			}
			if("".equals(categorys)) {
				categorys = category;
			}else {
				categorys += ";" + category;
			}
		}
		return categorys;
	}
	
	private void updateJobLog(String zid, String uid) {
		MDataMap map = new MDataMap();
		map.put("zid", zid);
		map.put("uid", uid);
		map.put("isFinish", "Y");
		DbUp.upTable("lc_job_dg_log").update(map);
	}
}
