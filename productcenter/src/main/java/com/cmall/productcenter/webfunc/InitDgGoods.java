package com.cmall.productcenter.webfunc;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.MapUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.HttpClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 达观推荐商品上报数据初始化
 * 将需要上报到达观的数据初始化到达观
 */
public class InitDgGoods extends RootFunc {
	private final int DO_SINGLE_COUNT = 100;
	
	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		boolean isFail = false;
		MWebResult mResult = new MWebResult();
		try {
			int count = DbUp.upTable("pc_productinfo").count("product_status", "4497153900060002");
			int forCount = (count / DO_SINGLE_COUNT) + 1;
			for(int i = 0; i < forCount; i ++) {
				int startIndex = i * DO_SINGLE_COUNT;
				List<Map<String, Object>> productList = DbUp.upTable("pc_productinfo").dataQuery("product_code, product_name, labels, min_sell_price, max_sell_price, update_time", "zid", 
						"product_status = '4497153900060002'", new MDataMap(), startIndex, DO_SINGLE_COUNT);
				for(Map<String, Object> product : productList) {
					String cateid = "";
					int price = 0, item_modify_time = 0;
					String itemid = MapUtils.getString(product, "product_code", "");
					String title = MapUtils.getString(product, "product_name", "");
					String item_tags = MapUtils.getString(product, "labels", "");
					String minPrice = MapUtils.getString(product, "min_sell_price", "");
					String maxPrice = MapUtils.getString(product, "max_sell_price", "");
					String update_time = MapUtils.getString(product, "update_time", "");
					
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
					cateid = getProductCategory(itemid);
					
					String result = doDgUp(itemid, title, cateid, item_tags, price, item_modify_time);
					JSONObject resultObject = JSONObject.fromObject(result);
					String status = resultObject.getString("status");
					
					if("FAIL".equals(status)) {
						isFail = true;
						mResult.setResultCode(0);
						mResult.setResultMessage("调用达观数据上报接口返回失败,请联系管理员!报文信息:【" + result + "】");
						break;
					}else if("WARN".equals(status)) {
						mResult.setResultMessage("调用达观数据上报接口返回成功,但有警告!报文信息:【" + result + "】");
					}
				}
				if(isFail) {
					break;
				}
			}
		}catch(Exception e) {
			mResult.setResultCode(0);
			mResult.setResultMessage("系统异常,请联系管理员!");
			e.printStackTrace();
		}
		return mResult;
	}
	
	private String doDgUp(String itemid, String title, String cateid, String item_tags, int price, int item_modify_time) {
		String url = bConfig("productcenter.dg_up_url") + bConfig("productcenter.dg_app_name");
		String appId = bConfig("productcenter.dg_app_id");
		
		JSONObject fields = new JSONObject();
        fields.put("itemid", itemid);
        fields.put("title", title);
        fields.put("cateid", cateid);
        fields.put("item_tags", item_tags);
        fields.put("item_modify_time", item_modify_time);
        fields.put("price", price);
		
        JSONObject content = new JSONObject();
        content.put("cmd", "add");
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
}
