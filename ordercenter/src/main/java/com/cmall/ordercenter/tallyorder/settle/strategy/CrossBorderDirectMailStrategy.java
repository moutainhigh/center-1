package com.cmall.ordercenter.tallyorder.settle.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.tallyorder.JoinSql;
import com.cmall.ordercenter.tallyorder.settleperiod.SettlePeriodService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 跨境直邮结算逻辑
 * 
 * @author zht
 *
 */
public class CrossBorderDirectMailStrategy extends SettleStrategy {

	public CrossBorderDirectMailStrategy(Map<String, Object> settlePeriod) {
		super(settlePeriod);
	}

	/**
	 * 跨境电商以'已发货'认为交易成功,所以状态查询以已发货,已收货,交易成功三种状态认为交易成功状态 已发货:4497153900010003
	 * 已收货:4497153900010004 交易成功:4497153900010005
	 */
	@Override
	protected List<String> getSuccessSellerList() {
		String saleStartDate = settlePeriod.get("sale_begin_date").toString(); // 结算开始时间
		String saleEndDate = settlePeriod.get("sale_end_date").toString(); // 结算结束时间

		List<String> sellerList = new ArrayList<String>();
		try {
			MDataMap map = new MDataMap();
			map.put("create_time_from", saleStartDate);
			map.put("create_time_end", saleEndDate);
			// 在线支付
			map.put("pay_type", "449716200001");

			// 只结算跨境直邮商户
			String settleTypeWhere = " AND si.uc_seller_type='4497478100050003' ";

			if (accountType.equals("4497477900030001")) {
				// 月结
				map.put("account_clear_type", "4497478100030003");
				// 原商户结算类型可能为空
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type ";
			} else if (accountType.equals("4497477900030002")) {
				// 半月结
				map.put("account_clear_type", "4497478100030004");
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type ";
			}
			
			// 屏蔽测试商户
			settleTypeWhere += " AND si.seller_company_name NOT LIKE '%测试%' ";

			// sql中增加了商户条件in ('SI2003','SI3003')
			// 结算支付类型包括在线支付和百度外卖两种,以后可能还要新增类型
			// 添加支付方式电视宝商城代收449716200006,民生商城代收4497162000070001
			String sql = "SELECT " + "small_seller_code " + "FROM " + "usercenter.uc_seller_info_extend si " + "WHERE "
					+ "si.small_seller_code IN (" + "SELECT DISTINCT " + "b.small_seller_code AS small_seller_code "
					+ "FROM " + "(" + "SELECT " + "code  " + "FROM " + "logcenter.lc_orderstatus " + "WHERE "
					+ "now_status in ('4497153900010003','4497153900010004','4497153900010005')  AND zid > 114806056 "
					+ "AND create_time>=:create_time_from " + "AND create_time<=:create_time_end "
					+ ") a, ordercenter.oc_orderinfo b " + "WHERE a.code = b.order_code "
					+ "AND b.seller_code IN ('SI2003', 'SI3003') " +
					// "AND b.small_seller_code LIKE 'SF031%' " +
					// "AND b.pay_type=:pay_type " +
					"AND b.pay_type in (" + getPayType() + ") " + ") " + settleTypeWhere;

			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus").dataSqlList(sql, map);
			if (null != list && !list.isEmpty()) {
				for (Map<String, Object> seller : list) {
					String smallSellerCode = (String) seller.get("small_seller_code");
					if (StringUtils.isNotEmpty(smallSellerCode)) {
						sellerList.add((String) seller.get("small_seller_code"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sellerList;
	}

	@Override
	protected List<String> getReturnSellerList() {
		String returnStartDate = settlePeriod.get("return_begin_date").toString(); // 退货开始时间
		String returnEndDate = settlePeriod.get("return_end_date").toString(); // 退货结束时间
		List<String> sellerList = new ArrayList<String>();
		try {
			MDataMap map = new MDataMap();
			map.put("create_time_from", returnStartDate);
			map.put("create_time_end", returnEndDate);
			// 通过审核(收货入库)
			map.put("status", "4497153900050001");

			// 只结算跨境直邮
			String settleTypeWhere = " AND si.uc_seller_type='4497478100050003' ";
			if (accountType.equals("4497477900030001")) {
				// 月结
				map.put("account_clear_type", "4497478100030003");
				// 原商户结算类型可能为空
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type ";
			} else if (accountType.equals("4497477900030002")) {
				// 半月结
				map.put("account_clear_type", "4497478100030004");
				settleTypeWhere += "AND si.account_clear_type=:account_clear_type ";
			}
			
			// 屏蔽测试商户
			settleTypeWhere += " AND si.seller_company_name NOT LIKE '%测试%' ";

			String sql = "SELECT " + "small_seller_code " + "FROM " + "usercenter.uc_seller_info_extend si " + "WHERE "
					+ "si.small_seller_code IN (" + "SELECT DISTINCT " + "b.small_seller_code AS small_seller_code "
					+ "FROM " + "(" + "SELECT " + "return_no " + "FROM " + "logcenter.lc_return_goods_status "
					+ "WHERE " + "status=:status " + "AND create_time>=:create_time_from "
					+ "AND create_time<=:create_time_end" + ") a, ordercenter.oc_return_goods b " + "WHERE "
					+ "a.return_no = b.return_code " +
					// "AND b.small_seller_code LIKE 'SF031%'" +
					") " + settleTypeWhere;

			List<Map<String, Object>> list = DbUp.upTable("oc_return_goods").dataSqlList(sql, map);
			if (null != list && !list.isEmpty()) {
				for (Map<String, Object> seller : list) {
					String smallSellerCode = (String) seller.get("small_seller_code");
					if (StringUtils.isNotEmpty(smallSellerCode)) {
						sellerList.add((String) seller.get("small_seller_code"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sellerList;
	}

	/**
	 * 跨境电商以'已发货'认为交易成功,所以状态查询以已发货,已收货,交易成功三种状态认为交易成功状态 已发货:4497153900010003
	 * 已收货:4497153900010004 交易成功:4497153900010005
	 */
	@Override
	public MDataMap grabSkuDealed(String smallSellerCode) throws Exception {
		String saleStart = settlePeriod.get("sale_begin_date").toString(); // 结算开始时间
		String saleEnd = settlePeriod.get("sale_end_date").toString(); // 结算结束时间
		String returnStart = settlePeriod.get("return_begin_date").toString(); // 退货开始时间
		String returnEnd = settlePeriod.get("return_end_date").toString(); // 退货结束时间

		MDataMap map = new MDataMap();
		try {
			if (!isTestSeller(smallSellerCode)) {
				map.put("small_seller_code", smallSellerCode);
				map.put("create_time_from", saleStart);
				map.put("create_time_end", saleEnd);
				map.put("pay_type", "449716200001");
				// sql中增加了商户条件in ('SI2003','SI3003')
				// 结算支付类型包括在线支付和百度外卖两种,以后可能还要新增类型
				// 添加支付方式电视宝商城代收449716200006,民生商城代收4497162000070001
				String sql = "SELECT " + "a.code, a.create_time " + "FROM " + "(" + "SELECT	code, create_time "
						+ "FROM logcenter.lc_orderstatus "
						+ "WHERE now_status in ('4497153900010003','4497153900010004','4497153900010005') AND zid > 114806056 "
						+ "GROUP BY code " + "HAVING create_time>=:create_time_from "
						+ "AND create_time<=:create_time_end " + ") a, ordercenter.oc_orderinfo b "
						+ "WHERE a.code = b.order_code " + "AND b.small_seller_code=:small_seller_code "
						+ "AND b.seller_code IN ('SI2003', 'SI3003') " +
						// "AND b.pay_type=:pay_type";
						"AND b.pay_type in (" + getPayType() + ") ";

				List<Map<String, Object>> successList = DbUp.upTable("lc_orderstatus").dataSqlList(sql, map);
				map.clear();
				if (null != successList && !successList.isEmpty()) {
					for (Map<String, Object> success : successList) {
						map.put(success.get("code").toString(), success.get("create_time").toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != map && !map.isEmpty()) {
			genSkuDealedSettleInfo(map, saleStart, saleEnd, returnStart, returnEnd);
		}
		return map;
	}

	private void genSkuDealedSettleInfo(MDataMap map, String saleStart, String saleEnd, String returnStart,
			String returnEnd) throws Exception {
		// 排除已经结算过的订单
		map = dealBillOrder(map);
		// 根据有效的订单编号查询sku 信息
		String ordersql = JoinSql.getJoinOrderCode(map);
		// String skuSql = "select
		// order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price
		// from oc_orderdetail where order_code " + ordersql;
		/**
		 * 添加查询字段show_price 2016-12-09 zhy
		 */
		String skuSql = "select order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price,show_price from oc_orderdetail where order_code "
				+ ordersql;
		List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataSqlList(skuSql, null);

		for (Map<String, Object> map2 : list) {
			String order_code = map2.get("order_code").toString();
			String product_code = map2.get("product_code").toString();
			Map<String, String> pcExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere(
					"settlement_type,product_code,purchase_type", "", "product_code=:product_code", "product_code",
					product_code);
			if (pcExtMap != null) {
				String settlement_type = pcExtMap.get("settlement_type").toString();
				String purchase_type = pcExtMap.get("purchase_type").toString();
				// 判断此商品结算类型是否为服务费结算并且采购类型为代收代付
				// 4497471600110003:服务费结算 4497471600160003:代收代付
				if (settlement_type.equals("4497471600110003") && purchase_type.equals("4497471600160003")) {
					String sku_code = map2.get("sku_code").toString();
					String sku_name = map2.get("sku_name").toString();
					// double sku_price =
					// Double.valueOf(map2.get("sku_price").toString());
					double success_sku_num = Double.valueOf(map2.get("sku_num").toString());
					double cost_price = Double.valueOf(map2.get("cost_price").toString());
					// sku售价
					// Map<String, String>
					// skumap=DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code","","sku_code=:sku_code","sku_code",sku_code);
					// double sell_price = 0;
					// if(skumap!=null){
					// sell_price=Double.valueOf(skumap.get("sell_price").toString());
					// }
					/**
					 * sku售价更改为取show_price字段 2016-12-09 zhy
					 */
					double sell_price = 0;
					if (StringUtils.isNotBlank(map2.get("show_price").toString())) {
						sell_price = Double.valueOf(map2.get("show_price").toString());
					}
					// 商品信息
					Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere(
							"product_code,product_name,small_seller_code,seller_code", "", "product_code=:product_code",
							"product_code", product_code);
					String product_name = "";
					String small_seller_code = "";
					// 增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
					String seller_code = "";
					if (productMap != null) {
						product_name = productMap.get("product_name").toString();
						small_seller_code = productMap.get("small_seller_code").toString();
						seller_code = productMap.get("seller_code").toString();
					}
					// 获取商户的信息
					// 新增查询字段money_collection_way（质保金收取方式），quality_retention_money（最大质保金），money_proportion（质保金比例）
					// 2016-10-28 zhy
					Map<String, String> sellerInfoMap = DbUp.upTable("uc_seller_info_extend").oneWhere(
							"seller_company_name,branch_name,bank_account,quality_retention_money,money_proportion,money_collection_way",
							"", "small_seller_code=:small_seller_code", "small_seller_code", small_seller_code);
					String seller_company_name = "";
					String branch_name = "";
					String bank_account = "";
					String money_collection_way = "账扣";// 质保金收取方式，默认为账扣
					double max_quality_retention_money = 0;// 最大质保金
					double money_proportion_rate = 0;// 质保金比例
					if (sellerInfoMap != null) {
						seller_company_name = sellerInfoMap.get("seller_company_name") == null ? ""
								: sellerInfoMap.get("seller_company_name").toString();
						branch_name = sellerInfoMap.get("branch_name").toString();// 开户行
						bank_account = sellerInfoMap.get("bank_account").toString();// 帐号
						max_quality_retention_money = Double
								.valueOf((sellerInfoMap.get("quality_retention_money") == null
										|| sellerInfoMap.get("quality_retention_money") == "" ? "0.00"
												: sellerInfoMap.get("quality_retention_money").toString()));// 质保金
						money_proportion_rate = Double.valueOf((sellerInfoMap.get("money_proportion") == null
								|| sellerInfoMap.get("money_proportion") == "" ? "0.00"
										: sellerInfoMap.get("money_proportion").toString()));// 质保金比例
						// 查询质保金收取方式编码对应中文名称 2016-10-28 zhy
						if (StringUtils.equals("4497477900050001",
								sellerInfoMap.get("money_collection_way").toString())) {
							money_collection_way = "预付";
						}
					}

					double success_amount = success_sku_num * sell_price;
					double settle_amount = success_sku_num * sell_price;

					if (small_seller_code.equals("SF03100294") || small_seller_code.equals("SF03100327")
							|| small_seller_code.equals("SF03100329") || small_seller_code.equals("SF03KJT")
							|| small_seller_code.equals("SF03MLG") || small_seller_code.equals("SF03100393")
							|| small_seller_code.equals("SF03100466") || small_seller_code.equals("SF03100443")
							|| small_seller_code.equals("SF03100541") || small_seller_code.equals("SF03100542")) {
						continue;
					}

					DbUp.upTable("oc_bill_final_export_tmp").insert("settle_code", getSettleCode(), "order_code",
							order_code, "product_settle_type", "跨境直邮结算", "passage", seller_code, "product_code",
							product_code, "product_name", product_name, "sku_code", sku_code, "sku_name", sku_name,
							"cost_price", String.valueOf(cost_price), "sell_price", String.valueOf(sell_price),
							"small_seller_code", small_seller_code, "small_seller_name", seller_company_name,
							"branch_name", branch_name, "branch_account", bank_account, "success_num",
							String.valueOf(success_sku_num), "success_amount", String.valueOf(success_amount),
							"return_num", String.valueOf("0"), "return_amount", String.valueOf("0.00"), "settle_num",
							String.valueOf(success_sku_num), "settle_amount", String.valueOf(settle_amount),
							"start_time", saleStart, "end_time", saleEnd, "tui_start", returnStart, "tui_end",
							returnEnd,
							// 新增添加质保金收取方式，最大质保金，质保金比例 2016-10-28 zhy
							"max_retention_money", String.valueOf(max_quality_retention_money), "money_proportion_rate",
							String.valueOf(money_proportion_rate), "money_collection_way", money_collection_way);
				}
			}
		}
	}

	@Override
	public MDataMap grabSkuReturned(String smallSellerCode) throws Exception {
		String saleStart = settlePeriod.get("sale_begin_date").toString(); // 结算开始时间
		String saleEnd = settlePeriod.get("sale_end_date").toString(); // 结算结束时间
		String returnStart = settlePeriod.get("return_begin_date").toString(); // 退货开始时间
		String returnEnd = settlePeriod.get("return_end_date").toString(); // 退货结束时间

		MDataMap map = new MDataMap();
		try {
			if (!isTestSeller(smallSellerCode)) {
				map.put("small_seller_code", smallSellerCode);
				map.put("create_time_from", returnStart);
				map.put("create_time_end", returnEnd);
				// 通过审核(收货入库)
				map.put("status", "4497153900050001");
				// 新添加查询字段return_code 2017-02-08 zhy
				String sql = "SELECT a.create_time,	b.order_code,b.return_code "
						+ "FROM logcenter.lc_return_goods_status a, ordercenter.oc_return_goods b "
						+ "WHERE a.return_no = b.return_code "
						+ "AND a.status=:status AND b.small_seller_code=:small_seller_code "
						+ "AND a.create_time>=:create_time_from " + "AND a.create_time<=:create_time_end";

				List<Map<String, Object>> returnList = DbUp.upTable("oc_return_goods").dataSqlList(sql, map);
				map.clear();
				if (null != returnList && returnList.size() > 0) {
					for (Map<String, Object> returnItem : returnList) {
						// map.put(returnItem.get("order_code").toString(),
						// returnItem.get("create_time").toString());
						// map.put(returnItem.get("order_code").toString(),
						// returnItem.get("return_code").toString()+","+returnItem.get("create_time").toString());
						// 一个退货单可以对应多个订单中的商品,反之不成, by zht
						map.put(returnItem.get("return_code").toString(), returnItem.get("order_code").toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != map && !map.isEmpty()) {
			genSkuReturnedSettleInfo(map, returnStart, returnEnd, saleStart, saleEnd);
		}
		return map;
	}

	private void genSkuReturnedSettleInfo(MDataMap map, String returnStart, String returnEnd, String saleStart,
			String saleEnd) {
		//排除已经结算过的退货单
		map = dealBillReturn(map);
		
		String product_code = "";
		String sku_name = "";
		double cost_price = 0;
		// 根据有效的订单编号查询sku 信息
		String ordersql = JoinSql.getJoinOrderCodeForReturn(map);
		// 获取return_code查询集合
		String returnCodeSql = JoinSql.getJoinReturnCode(map);
		// 原始退货sql
		// String skuSql = "select
		// order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price
		// from oc_orderdetail where order_code " + ordersql;
		// 部分退换货sql
		String skuSql = "SELECT a.return_code, a.order_code AS order_code, b.sku_code AS sku_code, sum(b.count) AS count,	b.return_code "
				+ "FROM ordercenter.oc_return_goods a, ordercenter.oc_return_goods_detail b " + "WHERE	a.order_code "
				+ ordersql + " and a.return_code=b.return_code and a.status='4497153900050001' "
				// 添加查询条件2017-02-08 zhy
				+ "and a.return_code " + returnCodeSql + " group by a.order_code, b.return_code, b.sku_code";
		List<Map<String, Object>> list = DbUp.upTable("oc_return_goods_detail").dataSqlList(skuSql, null);
		for (Map<String, Object> returnMap : list) {
			String order_code = returnMap.get("order_code").toString();
			String sku_code = returnMap.get("sku_code").toString();
			String skudetail = "select product_code,sku_name,sku_price,cost_price from oc_orderdetail where order_code = '"
					+ order_code + "' and sku_code= '" + sku_code + "'";
			List<Map<String, Object>> skulist = DbUp.upTable("oc_orderdetail").dataSqlList(skudetail, null);
			for (Map<String, Object> skuMap : skulist) {
				product_code = skuMap.get("product_code").toString();
				sku_name = skuMap.get("sku_name").toString();
				cost_price = Double.valueOf(skuMap.get("cost_price").toString());
			}

			Map<String, String> pcExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere(
					"settlement_type,product_code,purchase_type", "", "product_code=:product_code", "product_code",
					product_code);
			if (pcExtMap != null) {
				String settlement_type = pcExtMap.get("settlement_type").toString();
				String purchase_type = pcExtMap.get("purchase_type").toString();
				// 判断此商品结算类型是否为服务费结算并且采购类型为代收代付
				// 4497471600110003:服务费结算 4497471600160003:代收代付
				if (settlement_type.equals("4497471600110003") && purchase_type.equals("4497471600160003")) {
					double return_sku_num = Double.valueOf(returnMap.get("count").toString());// 每个订单所包含的退货单上sku_code的退货数量
					// // sku售价
					// Map<String, String> skumap =
					// DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code",
					// "",
					// "sku_code=:sku_code", "sku_code", sku_code);
					// double sell_price = 0;
					// if (skumap != null) {
					// sell_price =
					// Double.valueOf(skumap.get("sell_price").toString());
					// }

					/**
					 * 修改获取sku售价 2016-12-09 zhy
					 */
					double sell_price = 0;
					/**
					 * 新增限制条件sku_code 2017-02-09 zhy
					 */
					MDataMap skuShowPrice = DbUp.upTable("oc_orderdetail").oneWhere("show_price", "",
							"order_code=:order_code and sku_code=:sku_code", "order_code", order_code, "sku_code",
							sku_code);
					if (skuShowPrice != null) {
						sell_price = Double.valueOf(skuShowPrice.get("show_price"));
					}
					// 商品信息
					Map<String, String> productMap = DbUp.upTable("pc_productinfo").oneWhere(
							"product_code,product_name,small_seller_code,seller_code", "", "product_code=:product_code",
							"product_code", product_code);
					String product_name = "";
					String small_seller_code = "";
					// 增加了商户的编码(seller_code)作为通路 例如SI2003,SI3003
					String seller_code = "";
					if (productMap != null) {
						product_name = productMap.get("product_name").toString();
						small_seller_code = productMap.get("small_seller_code").toString();
						seller_code = productMap.get("seller_code").toString();
					}
					// 获取商户的信息
					// 新增查询字段money_collection_way（质保金收取方式），quality_retention_money（最大质保金），money_proportion（质保金比例）
					// 2016-10-28 zhy
					Map<String, String> sellerInfoMap = DbUp.upTable("uc_seller_info_extend").oneWhere(
							"seller_company_name,branch_name,bank_account,money_collection_way,quality_retention_money,money_proportion",
							"", "small_seller_code=:small_seller_code", "small_seller_code", small_seller_code);
					String seller_company_name = "";
					String branch_name = "";
					String bank_account = "";
					String money_collection_way = "账扣";// 质保金收取方式，默认为账扣
					double max_quality_retention_money = 0;// 最大质保金
					double money_proportion_rate = 0;// 质保金比例
					if (sellerInfoMap != null) {
						seller_company_name = sellerInfoMap.get("seller_company_name") == null ? ""
								: sellerInfoMap.get("seller_company_name").toString();
						branch_name = sellerInfoMap.get("branch_name").toString();// 开户行
						bank_account = sellerInfoMap.get("bank_account").toString();// 帐号
						max_quality_retention_money = Double
								.valueOf((sellerInfoMap.get("quality_retention_money") == null
										|| sellerInfoMap.get("quality_retention_money") == "" ? "0.00"
												: sellerInfoMap.get("quality_retention_money").toString()));// 质保金
						money_proportion_rate = Double.valueOf((sellerInfoMap.get("money_proportion") == null
								|| sellerInfoMap.get("money_proportion") == "" ? "0.00"
										: sellerInfoMap.get("money_proportion").toString()));// 质保金比例
						// 查询质保金收取方式编码对应中文名称 2016-10-28 zhy
						if (StringUtils.equals("4497477900050001",
								sellerInfoMap.get("money_collection_way").toString())) {
							money_collection_way = "预付";
						}
					}
					// 把数据入库订单结算表
					double return_amount = return_sku_num * sell_price;
					double settle_amount = return_sku_num * sell_price;

					if (small_seller_code.equals("SF03100294") || small_seller_code.equals("SF03100327")
							|| small_seller_code.equals("SF03100329") || small_seller_code.equals("SF03KJT")
							|| small_seller_code.equals("SF03MLG") || small_seller_code.equals("SF03100393")
							|| small_seller_code.equals("SF03100466") || small_seller_code.equals("SF03100443")
							|| small_seller_code.equals("SF03100541") || small_seller_code.equals("SF03100542")) {
						continue;
					}

					DbUp.upTable("oc_bill_final_export_tmp").insert("settle_code", getSettleCode(), "order_code",
							order_code, "product_settle_type", "跨境直邮结算", "passage", seller_code, "product_code",
							product_code, "product_name", product_name, "sku_code", sku_code, "sku_name", sku_name,
							"cost_price", String.valueOf(cost_price), "sell_price", String.valueOf(sell_price),
							"small_seller_code", small_seller_code, "small_seller_name", seller_company_name,
							"branch_name", branch_name, "branch_account", bank_account, "success_num",
							String.valueOf("0.00"), "success_amount", String.valueOf("0.00"), "return_num",
							String.valueOf(return_sku_num), "return_amount", String.valueOf(return_amount),
							"settle_num", String.valueOf(return_sku_num), "settle_amount",
							String.valueOf(settle_amount), "tui_start", returnStart, "tui_end", returnEnd, "start_time",
							saleStart, "end_time", saleEnd,
							// 新增添加质保金收取方式，最大质保金，质保金比例 2016-10-28 zhy
							"max_retention_money", String.valueOf(max_quality_retention_money), "money_proportion_rate",
							String.valueOf(money_proportion_rate), "money_collection_way", money_collection_way);
				}
			}
		}
	}

	/**
	 * 汇总本结算类型下各商户的SKU结算明细,结算数=成功交易数-退货数
	 */
	@Override
	protected void summarizeSkuSettleInfo() {
		String saleStartDate = settlePeriod.get("sale_begin_date").toString(); // 结算开始时间
		String saleEndDate = settlePeriod.get("sale_end_date").toString(); // 结算结束时间
		String returnStartDate = settlePeriod.get("return_begin_date").toString(); // 退货开始时间
		String returnEndDate = settlePeriod.get("return_end_date").toString(); // 退货结束时间
		String settledate = settlePeriod.get("account_date").toString(); // 结算日期

		StringBuffer sb = new StringBuffer();
		// 增加了passage通路字段
		sb.append(
				"select passage,product_code,product_name,sku_code,sku_name,cost_price,sell_price,product_contract_sign,product_alter,");
		sb.append("small_seller_code,small_seller_name,branch_name,branch_account,supplier_level,supplier_rate,");
		sb.append(
				"sum(success_num) as success_num, sum(success_amount) as success_amount,sum(return_num) as return_num,");
		sb.append("sum(return_amount) as return_amount,add_amount");
		// 新添加查询字段max_retention_money,rate,money_proportion_rate，money_collection_way
		// 2016-10-31 zhy
		sb.append(",max_retention_money,rate,money_proportion_rate,money_collection_way");
		// sb.append(",deduct_retention_money,sum(period_retention_money) as
		// period_retention_money,money_proportion_rate");
		// sb.append("sale_money,postage,manage_money,others,add_amount,rate,input_tax_subtotal,
		// total,other_pay_reason");
		sb.append(" from oc_bill_final_export_tmp where start_time= '" + saleStartDate + "' and end_time='"
				+ saleEndDate + "'");
		sb.append(" and tui_start='" + returnStartDate + "' and tui_end='" + returnEndDate + "'");
		sb.append(" and settle_code= '" + getSettleCode() + "'");
		sb.append(" group by passage,small_seller_code,product_code,sku_code,cost_price,sell_price");
		sb.append(" ORDER BY passage,small_seller_code,product_code,sku_code,cost_price ASC");
		// 供应商编号+商品编号+SKU编号+成本
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export_tmp").dataSqlList(sb.toString(), null);
		// 求
		String product_code = "";
		String sku_code = "";
		if (list.size() > 0) {
			for (Map<String, Object> mapinfo : list) {
				String passage = mapinfo.get("passage").toString();
				// 获取被复制商品的sku编号和商品编号
				product_code = mapinfo.get("product_code").toString();
				String product_name = mapinfo.get("product_name").toString();
				sku_code = mapinfo.get("sku_code").toString();
				String sku_name = mapinfo.get("sku_name").toString();
				double cost_price = Double.valueOf(mapinfo.get("cost_price").toString());
				double sell_price = Double.valueOf(mapinfo.get("sell_price").toString());
				// String
				// product_contract_sign=mapinfo.get("product_contract_sign").toString();//商品合同签署
				// String
				// product_alter=mapinfo.get("product_alter").toString();//商品调编
				String small_seller_code = mapinfo.get("small_seller_code").toString();
				String small_seller_name = mapinfo.get("small_seller_name").toString();
				String branch_name = mapinfo.get("branch_name").toString();
				String branch_account = mapinfo.get("branch_account").toString();
				// String
				// supplier_level=mapinfo.get("supplier_level").toString();
				// String supplier_rate=mapinfo.get("supplier_rate").toString();
				// 交易成功数量和金额(包括退货.因为先发货后退货,即先成功后退货)
				double success_num = Double.valueOf(mapinfo.get("success_num").toString());
				double success_amount = success_num * sell_price;
				// 退货数量和金额
				double return_num = Double.valueOf(mapinfo.get("return_num").toString());
				double return_amount = return_num * sell_price;
				// 结算数量和金额,结算数等于成功数减退货数
				double settle_num = success_num - return_num;
				double settle_amount = settle_num * sell_price;
				// 计算服务费
				double service_fee = settle_num * (sell_price - cost_price);

				double add_amount = Double
						.valueOf(mapinfo.get("add_amount") == null ? "0.00" : mapinfo.get("add_amount").toString());
				double total = settle_amount - add_amount;
				/**
				 * 平台商品代收代付结算单（账扣方式保证金），按照保证金账扣比例，在平台结算单上增加保证金扣除项。
				 * 预付：最大保证金按系统取数，本期保证金：0元。
				 * 账扣：本期代收货款大于0，按照扣除比例扣除保证金，直到扣除金额达到最大保证金金额；本期代收货款小于等于0，则本期保证金为0
				 * 。保证金不做逆向处理。抬头形式如下（标黄色为新增）<br>
				 * 2016-10-31 zhy<br>
				 */
				// ##############start##################
				// 已扣质保金质保金
				double max_retention_money = Double.valueOf(mapinfo.get("max_retention_money").toString());
				double rate = Double.valueOf(mapinfo.get("rate").toString());
				MDataMap map2 = new MDataMap();
				map2.put("product_code", product_code);
				map2.put("small_seller_code", small_seller_code);
				map2.put("sku_code", sku_code);
				map2.put("cost_price", String.valueOf(cost_price));
				double old_period_retention_money = 0;
				double deduct_retention_money = 0;
				double period_retention_money_sum = 0;
				double period_retention_money = 0;// 本期质保金
				double money_proportion_rate = 0;// 质保金比例
				// 质保金收取方式
				String money_collection_way = mapinfo.get("money_collection_way") != null
						? mapinfo.get("money_collection_way").toString() : "账扣";
				if (StringUtils.equals(money_collection_way, "账扣")) {
					// 此sql有一个坑select deduct_retention_money...应变为select
					// min(deduct_retention_money)....,不加min有时deduct_retention_money为0.0有时为某一期的数据
					// 会导致该期质保金会被加,双份。
					// 因为后面sum(period_retention_money)已累加,所以deduct_retention_money=old_deduct_retention_money+old_period_retention_money
					// 变为deduct_retention_money=old_period_retention_money

					String sql = "select deduct_retention_money,sum(period_retention_money) as period_retention_money from oc_bill_seller_retention_money where"
							+ " small_seller_code=:small_seller_code";
					Map<String, Object> mapbao = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(sql, map2);
					if (mapbao != null) {
						// old_deduct_retention_money=Double.valueOf(mapbao.get("deduct_retention_money")==null?"0.00":mapbao.get("deduct_retention_money").toString());
						old_period_retention_money = Double.valueOf(mapbao.get("period_retention_money") == null
								? "0.00" : mapbao.get("period_retention_money").toString());
						// deduct_retention_money=old_deduct_retention_money+old_period_retention_money;
						deduct_retention_money = old_period_retention_money;
					}
					// 结算质保金
					// 当月某商户所有sku产生的质保金之和
					MDataMap map3 = new MDataMap();
					map3.put("settle_code", getSettleCode());
					map3.put("small_seller_code", small_seller_code);
					String sql1 = "select sum(period_retention_money) as period_retention_money from oc_bill_final_export where "
							+ "small_seller_code=:small_seller_code and settle_code=:settle_code";
					Map<String, Object> mapbao1 = DbUp.upTable("oc_bill_final_export").dataSqlOne(sql1, map3);
					if (mapbao1 != null) {
						period_retention_money_sum = Double.valueOf(mapbao1.get("period_retention_money") == null
								? "0.00" : mapbao1.get("period_retention_money").toString());
					}

					// if (剩余质保金-结算金额*质保金比例）>0？结算金额*质保金比例：剩余质保金
					// 剩余质保金 =最大质保-已扣质保-∑本期质保
					money_proportion_rate = Double.valueOf(mapinfo.get("money_proportion_rate") == null ? "0.00"
							: mapinfo.get("money_proportion_rate").toString());// 质保金比例
					double remain_proportion_money = max_retention_money - deduct_retention_money
							- period_retention_money_sum;
					if (remain_proportion_money > 0) {
						if (settle_amount < 0) {
							// 本期没有结算
							period_retention_money = 0;
						} else if (remain_proportion_money - settle_amount * money_proportion_rate > 0) {
							// 还未扣完
							period_retention_money = settle_amount * money_proportion_rate;
						} else {
							// 剩于要扣的质保金小于本期应扣质保金,则本期应扣质保金等于剩余质保金
							period_retention_money = remain_proportion_money;
						}
					} else {
						period_retention_money = 0;
					}
				}
				// ##############end####################
				DbUp.upTable("oc_bill_final_export").insert("settle_code", getSettleCode(), "product_settle_type",
						"跨境直邮结算", "passage", passage, "product_code", product_code, "product_name", product_name,
						"sku_code", sku_code, "sku_name", sku_name, "cost_price", String.valueOf(cost_price),
						"sell_price", String.valueOf(sell_price), "small_seller_code", small_seller_code,
						"small_seller_name", small_seller_name, "branch_name", branch_name, "branch_account",
						branch_account, "success_num", String.valueOf(success_num), "success_amount",
						String.valueOf(success_amount), "return_num", String.valueOf(return_num), "return_amount",
						String.valueOf(return_amount), "settle_num", String.valueOf(settle_num), "settle_amount",
						String.valueOf(settle_amount), "service_fee", String.valueOf(service_fee), "total",
						String.valueOf(total), "start_time", saleStartDate, "end_time", saleEndDate, "tui_start",
						returnStartDate, "tui_end", returnEndDate,
						// 新添加字段 2016-10-31 zhy
						"money_proportion_rate", String.valueOf(money_proportion_rate), "max_retention_money",
						String.valueOf(max_retention_money), "deduct_retention_money",
						String.valueOf(deduct_retention_money), "period_retention_money",
						String.valueOf(period_retention_money), "rate", String.valueOf(rate), "money_collection_way",
						mapinfo.get("money_collection_way").toString());
			}
		}
		if (list.size() > 0) {
			retentionMoney(saleStartDate, saleEndDate);
		}
	}

	/**
	 * 生成本结算帐期内财务汇总记录
	 */
	@Override
	protected void genFinanlBill() {
		String saleStart = settlePeriod.get("sale_begin_date").toString(); // 结算开始时间
		String saleEnd = settlePeriod.get("sale_end_date").toString(); // 结算结束时间
		String returnStart = settlePeriod.get("return_begin_date").toString(); // 退货开始时间
		String returnEnd = settlePeriod.get("return_end_date").toString(); // 退货结束时间

		MDataMap map = new MDataMap();
		map.put("start_time", saleStart);
		map.put("end_time", saleEnd);
		map.put("tui_start", returnStart);
		map.put("tui_end", returnEnd);
		map.put("settle_code", getSettleCode());

		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(
				"select small_seller_code as small_seller_code,"
						+ "sum(settle_amount) as settle_amount, sum(service_fee) as service_fee,sum(period_retention_money) as period_money, sum(add_amount) as related_charges from oc_bill_final_export "
						+ "where start_time=:start_time and end_time=:end_time and tui_start=:tui_start and tui_end=:tui_end and settle_code=:settle_code",
				map);
		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> mapinfo : list) {
				double settle_amount = Double.valueOf(
						mapinfo.get("settle_amount") == null ? "0.00" : mapinfo.get("settle_amount").toString());
				double service_fee = Double
						.valueOf(mapinfo.get("service_fee") == null ? "0.00" : mapinfo.get("service_fee").toString());
				double related_charges = Double.valueOf(
						mapinfo.get("related_charges") == null ? "0.00" : mapinfo.get("related_charges").toString());
				double actual_pay_amount = settle_amount - related_charges - service_fee;
				/**
				 * 质保金 2017-05-24 zhy
				 */
				double current_period_money = Double
						.valueOf(mapinfo.get("period_money") == null ? "0.00" : mapinfo.get("period_money").toString());
				String small_seller_code = StringUtils.isEmpty((String) mapinfo.get("small_seller_code")) ? ""
						: mapinfo.get("small_seller_code").toString();
				if (StringUtils.isNotEmpty(small_seller_code)) {
					DbUp.upTable("oc_bill_finance_amount").insert("settle_code", getSettleCode(), "settle_type",
							settleType, "account_type", accountType, "settle_amount", String.valueOf(settle_amount),
							"related_charges", String.valueOf(related_charges),
							"current_period_money", String.valueOf(current_period_money),
							"service_fee",String.valueOf(service_fee), "settle_pay_moeny", String.valueOf(actual_pay_amount),
							"start_time", saleStart, "end_time", saleEnd, "settle_period",
							saleStart.substring(0, 10) + "至" + saleEnd.substring(0, 10), "settle_status", "1",
							"tuistart", returnStart, "tuiend", returnEnd, "small_seller_code", small_seller_code);
				}
			}
		}
	}

	/**
	 * 汇总本结算类型下各商户sku质保金
	 * 
	 * @param saleStartDate
	 * @param saleEndDate
	 */
	private void retentionMoney(String saleStartDate, String saleEndDate) {
		MDataMap map = new MDataMap();
		map.put("start_time", saleStartDate);
		map.put("end_time", saleEndDate);
		String settle_code = getSettleCode();
		map.put("settle_code", settle_code);
		// map.put("small_seller_code", small_seller_code);
		String sql = "select small_seller_code,max_retention_money,deduct_retention_money,sum(period_retention_money) as period_retention_money,"
				+ " start_time from oc_bill_final_export where start_time=:start_time and end_time=:end_time and settle_code=:settle_code"
				+ " group by small_seller_code ";
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_final_export").dataSqlList(sql, map);
		for (Map<String, Object> mapinfo : list) {
			String small_seller_code = mapinfo.get("small_seller_code").toString();
			double max_retention_money = Double.valueOf(mapinfo.get("max_retention_money").toString());
			double deduct_retention_money = Double.valueOf(mapinfo.get("deduct_retention_money").toString());
			double period_retention_money = Double.valueOf(mapinfo.get("period_retention_money").toString());
			DbUp.upTable("oc_bill_seller_retention_money").insert("small_seller_code", small_seller_code,
					"max_retention_money", String.valueOf(max_retention_money), "deduct_retention_money",
					String.valueOf(deduct_retention_money), "period_retention_money",
					String.valueOf(period_retention_money), "settle_time", saleStartDate, "settle_code",
					getSettleCode());
		}
	}

	public static void main(String[] args) {
		SettlePeriodService ops = new SettlePeriodService();
		List<Map<String, Object>> periodList = ops.getSettlePeriod();
		for (Map<String, Object> period : periodList) {
			if ("06".equals(period.get("code").toString())) {
				CrossBorderDirectMailStrategy s = new CrossBorderDirectMailStrategy(period);
				// s.retentionMoney("2016-11-01 00:00:00", "2016-11-30
				// 23:59:59");
				s.genFinanlBill();
			}
		}
	}

	@Override
	protected void genFinanlBillTicket() {
		// TODO Auto-generated method stub
		
	}
}
