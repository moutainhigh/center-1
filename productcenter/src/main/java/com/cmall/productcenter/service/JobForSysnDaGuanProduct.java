package com.cmall.productcenter.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.quartz.JobExecutionContext;
import com.alibaba.fastjson.JSONArray;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.HttpClientSupport;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

import net.sf.json.JSONObject;

public class JobForSysnDaGuanProduct extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		LogLog.debug("达观智能推荐接口" + this.getClass().getName() + "启动");
        List<Map<String, Object>> dataSqlList = DbUp.upTable("sc_daguan_scene_product").dataSqlList("select * from sc_daguan_scene_product", null);
        List<Map<String, Object>> todaySecKillsProducts = getTodaySecKillsProducts();
        MDataMap paramMap = new MDataMap();
        String dateTimeString = DateUtil.getSysDateTimeString();
        paramMap.put("uid", WebHelper.upUuid());
        paramMap.put("create_time",dateTimeString );
        if(dataSqlList==null||dataSqlList.size()==0) {
        	if(todaySecKillsProducts!=null&&todaySecKillsProducts.size()>0) {
        		for (Map<String, Object> map : todaySecKillsProducts) {
        			boolean flag = itemOperate("add",map.get("product_code").toString());
        	        if(flag) {
        	        	paramMap.put("product_code",map.get("product_code").toString());
            	        DbUp.upTable("sc_daguan_scene_product").dataInsert(paramMap);
        	        }   			
				}
        	}
        }else {
        	//清理已有的上报数据
        	for (Map<String, Object> map : dataSqlList) {
    			boolean flag = itemOperate("clean",map.get("product_code").toString());
                if(flag) {
                	DbUp.upTable("sc_daguan_scene_product").delete("product_code",map.get("product_code").toString());
                }
			}
            if(todaySecKillsProducts!=null&&todaySecKillsProducts.size()>0) {
            	for (Map<String, Object> map : todaySecKillsProducts) {
        			boolean flag = itemOperate("add",map.get("product_code").toString());
        	        if(flag) {
        	        	paramMap.put("product_code",map.get("product_code").toString());
            	        DbUp.upTable("sc_daguan_scene_product").dataInsert(paramMap);
        	        }   			
				}
        	}
        }
	}
	
	private  boolean itemOperate(String operateType, String productCode) {
		boolean flag = false;
		Map<String, Object> product = DbUp.upTable("pc_productinfo").dataSqlOne("select i.product_code, i.product_name, i.labels, i.min_sell_price, i.max_sell_price, i.update_time from pc_productinfo i "
				+ "where i.product_code = '" + productCode + "'", new MDataMap());
		String cateid = "";
		int price = 0, item_modify_time = 0;
		String title = MapUtils.getString(product, "product_name", "");
		String item_tags = MapUtils.getString(product, "labels", "");
		String minPrice = MapUtils.getString(product, "min_sell_price", "");
		String maxPrice = MapUtils.getString(product, "max_sell_price", "");
		String update_time = MapUtils.getString(product, "update_time", "");
		
		try {
			item_tags = item_tags.replaceAll(" ", ";");
			item_tags = item_tags.replaceAll(",", ";");
			if(!"".equals(minPrice) && !"0".equals(minPrice)) {
				price = new BigDecimal(minPrice).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue(); 
			}else if(!"".equals(maxPrice) && !"0".equals(maxPrice)) {
				price = new BigDecimal(maxPrice).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			}
			if(!"".equals(update_time)) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				item_modify_time = (int) (dateFormatter.parse(update_time).getTime() / 1000);
			}
			cateid = getProductCategory(productCode);
			
			String result = doDgUp(operateType, productCode, title, cateid, item_tags, price, item_modify_time);
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

	
	private  String doDgUp(String operateType, String itemid, String title, String cateid, String item_tags, int price, int item_modify_time) {
		String url = TopConfig.Instance.bConfig("productcenter.dg_up_url") + TopConfig.Instance.bConfig("productcenter.dg_app_name");
		String appId = TopConfig.Instance.bConfig("productcenter.dg_app_id");
		String cmd = "add";
		JSONObject fields = new JSONObject();
        fields.put("itemid", itemid);
        fields.put("title", title);
        fields.put("cateid", cateid);
        fields.put("item_tags", item_tags);
        fields.put("item_modify_time", item_modify_time);
        fields.put("price", price);
        if("add".equals(operateType)) {
        	//添加时加标识，更新时不需要
            fields.put("flash_sale", 1);
        }
        //行为数据上报和调用时，需传入scene_type；即scene_type=home_flash_sale  此处无行为
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
    	MDataMap mDataMap = new MDataMap();
        try {
        	result = HttpClientSupport.doPostDg(url, goodParams.toString());
    		mDataMap.put("api_type", "dg_up");
    		mDataMap.put("member_code", "JobForSysnDaGuanProduct");
    		mDataMap.put("request_time", FormatHelper.upDateTime());
    		mDataMap.put("response_time", "");
    		mDataMap.put("fail_content", "");
		} catch (IOException e) {
			mDataMap.put("fail_content", e+"");
			mDataMap.put("response_time", FormatHelper.upDateTime());
			e.printStackTrace();
		}
        // 记录达观接口调用日志
 		if(StringUtils.isNotBlank(mDataMap.get("fail_content"))) {
 			DbUp.upTable("lc_dg_api_log").dataInsert(mDataMap);
 		}
		return result;
	}
	
	private  String getProductCategory(String productCode) {
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
	

	private static List<Map<String, Object>> getTodaySecKillsProducts() {
		String nowDay = DateUtil.getSysDateTimeString().substring(0, 10);
        String dayStartTime = nowDay+" 00:00:00";
        String dayEndTime = nowDay+" 23:59:59";
        //获取今天有效的秒杀活动商品
		/*String sql = "select o.product_code as product_code from sc_event_item_product o,sc_event_info i where event_type_code='4497472600010001' and event_status='4497472700020002' and o.event_code = i.event_code and o.flag_enable=1 "
				+ " and i.begin_time>= '"+dayStartTime+"' and i.end_time<'"+dayEndTime+"' group by o.product_code";*/
        String sql = "select o.product_code as product_code from sc_event_item_product o,sc_event_info i where event_type_code='4497472600010001' and event_status='4497472700020002' and o.event_code = i.event_code and o.flag_enable=1 "
				+ " and ((i.begin_time>= '"+dayStartTime+"' and i.begin_time<'"+dayEndTime+"') or (i.end_time>= '"+dayStartTime+"' and i.begin_time<='"+dayStartTime+"')) group by o.product_code";
		List<Map<String, Object>> dataSqlList = DbUp.upTable("sc_event_item_product").dataSqlList(sql, null);
		return dataSqlList;
	}
	

}
