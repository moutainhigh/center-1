package com.cmall.productcenter.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 自动选品相关功能类
 */
public class AutoSelectProductSupport {
	
	// 商品创建时间周期映射
	static Map<String,Integer> productCreateTimePeriodMap = new HashMap<String, Integer>();
	// 销量统计周期映射
	static Map<String,Integer> salesPeriodMap = new HashMap<String, Integer>();
	
	static {
		productCreateTimePeriodMap.put("4497471600490002", 7);
		productCreateTimePeriodMap.put("4497471600490003", 15);
		productCreateTimePeriodMap.put("4497471600490004", 30);
		productCreateTimePeriodMap.put("4497471600490005", 90);
		productCreateTimePeriodMap.put("4497471600490006", 180);
		productCreateTimePeriodMap.put("4497471600490007", 365);   // 1年内
		productCreateTimePeriodMap.put("4497471600490008", 2*365); // 2年内
		productCreateTimePeriodMap.put("4497471600490009", 5*365); // 5年内
		
		salesPeriodMap.put("4497471600500001", 7);     // 周销量
		salesPeriodMap.put("4497471600500002", 14);    // 2周销量
		salesPeriodMap.put("4497471600500003", 30);    // 月销量
		salesPeriodMap.put("4497471600500004", 60);    // 2月销量
		salesPeriodMap.put("4497471600500005", 180);   // 6月销量
		salesPeriodMap.put("4497471600500006", 365);   // 年销量
		salesPeriodMap.put("4497471600500007", 3650);  // 总销量
	}

	/**
	 * 根据选品规则编号查询符合条件的商品列表
	 * @param xpCode 编号
	 * @return
	 */
	public List<String> getSelectProduct(String xpCode){
		MDataMap map = DbUp.upTable("pc_product_xuanpin").one("xp_code",xpCode, "delete_flag", "0");
		if(map == null) return new ArrayList<String>();
		
		//MDataMap paraMap = new MDataMap();
		//String sql = buildDataSqlAndSort(map, paraMap).toString();
		//List<Map<String, Object>> resultMapList = DbUp.upTable("pc_product_xuanpin").dataSqlList(sql, paraMap);
		
		int maxSize = NumberUtils.toInt(map.get("max_size"));
		int dayNum = NumberUtils.toInt(map.get("day_num"));
		dayNum = dayNum < 1 ? 1 : dayNum;
		
		// 选品变化
		String refreshType = map.get("refresh_type");
		// 每日变更排序的情况下固定返回第一页数据
		if("4497471600690001".equals(refreshType)) {
			dayNum = 1;
		}
		
		// 直接查询缓存表数据
		List<MDataMap> resultMapList = DbUp.upTable("pc_product_xuanpin_cache").query("product_code", "", "", new MDataMap("xp_code", xpCode), (dayNum - 1) * maxSize, maxSize);
		ArrayList<String> list = new ArrayList<String>();
		for(MDataMap m : resultMapList) {
			list.add(m.get("product_code")+"");
		}
		
		return list;
	}
	
	/**
	 * 刷新选品池缓存数据
	 * @param xpCode 编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int refreshXuanpinCache(String xpCode){
		MDataMap map = DbUp.upTable("pc_product_xuanpin").one("xp_code",xpCode, "delete_flag", "0");
		if(map == null) return 0;
		
		MDataMap paraMap = new MDataMap();
		// 只对每日变更商品时最大存储6页商品
		if("4497471600690002".equals(map.get("refresh_type"))) {
			map.put("max_size", NumberUtils.toInt(map.get("max_size"))*6 + "" );
		}
		
		String querySql = buildDataSqlAndSort(map, paraMap).toString();
		List<Map<String, Object>> resultMapList = DbUp.upTable("pc_product_xuanpin").dataSqlList(querySql, paraMap);
		
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 每日变更排序时对结果随机排序
		if("4497471600690001".equals(map.get("refresh_type"))) {
			while(!resultMapList.isEmpty()) {
				list.add(resultMapList.remove(RandomUtils.nextInt(resultMapList.size())));
			}
		} else {
			for(Map<String, Object> m : resultMapList) {
				list.add(new HashMap<String, Object>(m));
			}
		}
		
		// 清理旧数据
		DbUp.upTable("pc_product_xuanpin_cache").delete("xp_code", xpCode);
		
		// 把新的结果插入缓存表
		String insertSql = "insert into pc_product_xuanpin_cache(xp_code,product_code,update_time) values('"+xpCode+"',:product_code,now())";
		DbUp.upTable("pc_product_xuanpin_cache").upTemplate().batchUpdate(insertSql, list.toArray(new HashMap[0]));
		
		return resultMapList.size();
	}
	
	public int getTotalProductNum(String xpCode) {
		MDataMap map = DbUp.upTable("pc_product_xuanpin").one("xp_code",xpCode, "delete_flag", "0");
		if(map == null) return 0;
		return getTotalProductNum(map);
	}
	
	public int getTotalProductNum(MDataMap mDataMap) {
		MDataMap paraMap = new MDataMap();
		String sql = buildDataCountSql(mDataMap, paraMap).toString();
		Map<String, Object> resultMap = DbUp.upTable("pc_product_xuanpin").dataSqlOne(sql, paraMap);
		
		return NumberUtils.toInt(resultMap.get("num")+"");
	}
	
	private StringBuilder buildDataSqlAndSort(MDataMap xpDataMap, MDataMap paraMap) {
		StringBuilder dataSql = buildDataSql(xpDataMap, paraMap);
		StringBuilder builder = new StringBuilder();
		
		// 按销量排序
		if("4497471600510002".equals(xpDataMap.get("sorting_rule"))) {
			Integer days = salesPeriodMap.get(xpDataMap.get("product_sales_period"));
			builder.append("SELECT t.product_code, SUM(IFNULL(ed.sales,0)) sales FROM ("+dataSql.toString()+") t ");
			builder.append(" LEFT JOIN pc_productsales_everyday ed ON t.product_code = ed.product_code AND ed.day > DATE_SUB(NOW(),INTERVAL "+(days+1)+" DAY) ");
			builder.append(" GROUP BY t.product_code ORDER BY sales DESC ");
		} else if("4497471600510003".equals(xpDataMap.get("sorting_rule"))) {
			// 按售价排序
			builder = dataSql;
			builder.append(" ORDER BY p.min_sell_price ");
		} else {
			builder = dataSql;
		}
		
		builder.append(" limit ").append(xpDataMap.get("max_size"));
		return builder;
	}
	
	private StringBuilder buildDataCountSql(MDataMap xpDataMap, MDataMap paraMap) {
		StringBuilder dataSql = buildDataSql(xpDataMap, paraMap);
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT count(*) num FROM ("+dataSql.toString()+") t");
		
		return builder;
	}
	
	private StringBuilder buildDataSql(MDataMap xpDataMap, MDataMap paraMap) {
		StringBuilder where = new StringBuilder("SELECT DISTINCT p.product_code,p.min_sell_price FROM productcenter.pc_productinfo p ");
		where.append(" LEFT JOIN usercenter.uc_sellercategory_product_relation r ON p.product_code = r.product_code ");
		where.append(" LEFT JOIN usercenter.uc_seller_info_extend e ON p.small_seller_code = e.small_seller_code ");
		where.append(" LEFT JOIN productcenter.pc_skuinfo s ON p.product_code = s.product_code ");
		where.append(" LEFT JOIN productcenter.pc_product_xuanpin_comment c ON p.product_code = c.product_code ");
		where.append(" LEFT JOIN productcenter.pc_product_xuanpin_rebuy y ON p.product_code = y.product_code ");
		where.append(" WHERE p.seller_code = 'SI2003' ");
		// 分类限制
		if(!"4497471600070001".equals(xpDataMap.get("category_limit"))) {
			String val = xpDataMap.get("category_codes");
			val = "'"+val.replace(",", "','")+"'";
			where.append(" AND (LEFT(r.category_code,12) IN ("+val+") OR LEFT(r.category_code,16) IN (" + val + "))");
		}
		
		// 商户类型限制
		if(!"4497471600070001".equals(xpDataMap.get("uc_seller_type_limit"))) {
			String val = xpDataMap.get("uc_seller_type_codes");
			val = "'"+val.replace(",", "','")+"'";
			// LD类型特殊处理
			if(xpDataMap.contains("4497478100050000")) {
				where.append(" AND (e.uc_seller_type IN("+val+") OR p.small_seller_code = 'SI2003')");
			} else {
				where.append(" AND e.uc_seller_type IN("+val+")");
			}
		}
		
		// 商户编号限制
		if(!"4497471600070001".equals(xpDataMap.get("small_seller_limit"))) {
			String val = xpDataMap.get("small_seller_codes");
			val = "'"+val.replace(",", "','")+"'";
			where.append(" AND p.small_seller_code IN("+val+")");
		}
		
		// 销售价格限制
		if(!"4497471600070001".equals(xpDataMap.get("sell_price_limit"))) {
			String val = xpDataMap.get("sell_price_range");
			paraMap.put("min_price", val.split("-")[0]);
			paraMap.put("max_price", val.split("-")[1]);
			where.append(" AND p.min_sell_price between :min_price AND :max_price");
		}
		
		// 商品状态限制，默认空字符串则表示不限制
		if(!"".equals(xpDataMap.get("product_status_limit"))) {
			paraMap.put("product_status", xpDataMap.get("product_status_limit"));
			where.append(" AND p.product_status = :product_status");
		}
		
		// 商品库存限制
		if(!"4497471600070001".equals(xpDataMap.get("product_stock_limit"))) {
			String val = xpDataMap.get("product_stock_range");
			paraMap.put("min_stock", val.split("-")[0]);
			paraMap.put("max_stock", val.split("-")[1]);
			where.append(" AND s.stock_num between :min_stock and :max_stock");
		}
		
		// 商品创建时间限制
		if(!"4497471600490001".equals(xpDataMap.get("product_create_time_limit"))) {
			Integer days = productCreateTimePeriodMap.get(xpDataMap.get("product_create_time_limit"));
			where.append(" AND p.create_time >= DATE_SUB(NOW(),INTERVAL "+days+" DAY)");
		}
		
		// 品牌限制
		if(!"4497471600070001".equals(xpDataMap.get("brand_limit"))) {
			String val = xpDataMap.get("brand_codes");
			val = "'"+val.replace(",", "','")+"'";
			where.append(" AND p.brand_code IN("+val+")");
		}
		
		// 评论数限制
		if(!"4497471600070001".equals(xpDataMap.get("comment_limit"))) {
			where.append(" AND c.comment_num >= "+xpDataMap.get("comment_num"));
		}
		
		// 好论数限制
		if(!"4497471600070001".equals(xpDataMap.get("comment_good_limit"))) {
			where.append(" AND c.comment_good_num >= "+xpDataMap.get("comment_good_num"));
		}
		
		// 复购率限制
		if(!"4497471600070001".equals(xpDataMap.get("rebuy_rate_limit"))) {
			where.append(" AND y.rebuy_rate >= "+xpDataMap.get("rebuy_rate"));
		}
		
		// 医疗品排除
		if(!"449747110001".equals(xpDataMap.get("medical_flag"))) {
			where.append(" AND r.category_code NOT IN (SELECT category_code FROM usercenter.uc_program_del_category)");
			where.append(" AND LEFT(r.category_code,16) NOT IN (SELECT category_code FROM usercenter.uc_program_del_category)");
			where.append(" AND LEFT(r.category_code,12) NOT IN (SELECT category_code FROM usercenter.uc_program_del_category)");
		}
		
		// 活动限制
		if(!"4497471600680001".equals(xpDataMap.get("activity_limit"))
				&& StringUtils.isNotBlank(xpDataMap.get("activity_type_codes"))) {
			List<String> list = new ArrayList<String>(Arrays.asList(xpDataMap.get("activity_type_codes").split(",")));
			boolean mjFlag = list.remove("4497472600010008"); // 满减活动走特殊判断
			String codes = "'"+StringUtils.join(list,"','")+"'";
			
			// 普通活动判断条件
			String flag = "IN";
			// 指定
			if("4497471600680002".equals(xpDataMap.get("activity_limit"))) {
				flag = "IN";
			}
			
			// 排除
			if("4497471600680003".equals(xpDataMap.get("activity_limit"))) {
				flag = "NOT IN";
			}
			
			String sql1 = "";
			// 普通商品活动
			if(!list.isEmpty()) {
				sql1 = " SELECT i.product_code FROM systemcenter.sc_event_info e,systemcenter.sc_event_item_product i" +
						" WHERE e.event_code = i.event_code AND e.begin_time < NOW() AND e.end_time > NOW()" +
						" AND e.event_status = '4497472700020002' AND i.flag_enable = 1" +
						" AND e.event_type_code IN("+codes+")";
			}
			
			String sql2 = "";
			// 满减商品活动
			if(mjFlag) {
				sql2 = " SELECT r.product_code FROM systemcenter.sc_event_info e,systemcenter.sc_full_cut c,systemcenter.sc_full_cut_product r" +
							" WHERE e.event_code = c.event_code AND e.event_code = r.event_code AND e.begin_time < NOW() AND e.end_time > NOW() " +
							" AND e.event_status = '4497472700020002' " +
							" AND e.event_type_code IN('4497472600010008')" +
							" AND c.full_cut_product_type = '4497476400020002'";
			}
			
			// 两种条件同时存在时是或的判断
			if(StringUtils.isNotBlank(sql1) && StringUtils.isNotBlank(sql2)) {
				// 把两种活动结果查询拼成一个子查询
				where.append(" AND p.product_code " + flag + " (");
				where.append("SELECT product_code FROM (");  // 嵌套一层增加查询效率
				where.append(sql1);
				where.append(" UNION ");
				where.append(sql2);
				where.append(") uc )");
			} else if(StringUtils.isNotBlank(sql1)) {
				where.append(" AND p.product_code " + flag + " (" + sql1 + ")");
			} else if(StringUtils.isNotBlank(sql2)) {
				where.append(" AND p.product_code " + flag + " (" + sql2 + ")");
			}
		}
		
		return where;
	}
	
	
}
