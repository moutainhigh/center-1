package com.cmall.ordercenter.tallyorder.settle.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.tallyorder.settle.subwork.SettleSkuDealed;
import com.cmall.ordercenter.tallyorder.settle.subwork.SettleSkuReturned;
import com.cmall.ordercenter.util.DateFormatUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 结算策略抽象类.一个具体结算策略用一个线程执行。结算策略包括常规结算,跨境商户,跨境直邮,平台入驻. 结算总体流程:
 * 1.结算策略类：获取当前结算类型对应的结算帐期内所有有交易成功(不同结算类型成功的定义不同)订单的商户列表
 * 结算策略类：将第1步商户列表分拆,放到交易成功SKU结算子类中的线程中执行.
 * 交易成功SKU结算子类：抓取子列表内每一个供应商成交SKU信息,根据交易成功SKU结算子类按不同的结算规则 生成SKU交易结算信息放入SKU结算临时表中.
 * 2.结算策略类：获取当前结算类型对应的结算帐期内所有有退货(不同结算类型成功的定义不同)订单的商户列表
 * 结算策略类：将第2步商户列表分拆,放到退货SKU结算子类中的线程中执行.
 * 退货SKU结算子类：抓取子列表内每一个供应商退货SKU信息,根据退货SKU结算子类按不同的结算规则生成SKU 退货结算信息放入SKU结算临时表中.
 * 3.结算策略类：汇总当前结算类型内所有商户的不同SKU的交易成功结算信息和退货结算信息,存入SKU结算正式表.
 * 4.结算策略类：生成本结算类型结算帐期的财务汇总数据
 * 
 * @author zht
 *
 */
public abstract class SettleStrategy extends BaseClass implements Runnable {

	private static final int ThreadSizeInPool = 20;

	/**
	 * 结算类型 4497477900040001 常规结算 4497477900040002 跨境保税 4497477900040003 跨境直邮
	 * 4497477900040004 平台入驻
	 */
	protected String settleType;

	/**
	 * 帐期类型 4497477900030001 月结 4497477900030002 半月结
	 */
	protected String accountType;

	/**
	 * 结算周期配置
	 */
	protected Map<String, Object> settlePeriod;

	private ExecutorService service = Executors.newFixedThreadPool(ThreadSizeInPool + 2);

	public SettleStrategy(Map<String, Object> settlePeriod) {
		this.settlePeriod = settlePeriod;
		this.accountType = (String) settlePeriod.get("account_type");
		this.settleType = (String) settlePeriod.get("settle_type");
	}

	@Override
	public void run() {
		// 查询结算周期内有订单支付成功的供应商
		List<String> successSellerList = getSuccessSellerList();
		List<List<String>> itemSellerList = split(successSellerList, successSellerList.size() / (ThreadSizeInPool / 2));
		for (List<String> itemSellers : itemSellerList) {
			SettleSkuDealed tss = new SettleSkuDealed(itemSellers, settlePeriod, this);
			service.execute(tss);
		}
		// 查询结算周期内有退货单的供应商
		List<String> returnSellerList = getReturnSellerList();
		itemSellerList = split(returnSellerList, returnSellerList.size() / (ThreadSizeInPool / 2));
		for (List<String> itemSellers : itemSellerList) {
			SettleSkuReturned tss = new SettleSkuReturned(itemSellers, settlePeriod, this);
			service.execute(tss);
		}

		service.shutdown();
		while (true) {
			if (service.isTerminated()) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 汇总当前结算类型内所有商户的不同SKU的交易成功结算信息和退货结算信息,存入SKU结算正式表
		summarizeSkuSettleInfo();
		// 生成本结算类型结算帐期的财务汇总数据
		genFinanlBill();
		//生成结算单对应的发票单据信息
		genFinanlBillTicket();
		System.out.println("商户结算单生成完毕！");
	}

	/**
	 * 取得当前结算类型的结算帐期内所有有交易成功订单的商户列表
	 * 
	 * @return
	 */
	protected abstract List<String> getSuccessSellerList();

	/**
	 * 取得当前结算类型的结算帐期内所有有退货订单的商户列表
	 * 
	 * @param saleStartDate
	 * @param saleEndDate
	 * @param accountType
	 * @return
	 */
	protected abstract List<String> getReturnSellerList();

	/**
	 * 生成本结算类型（策略）对应结算周期内交易成功的SKU结算临时信息
	 * 
	 * @param smallSellerCode
	 * @return
	 * @throws Exception
	 */
	public abstract MDataMap grabSkuDealed(String smallSellerCode) throws Exception;

	/**
	 * 生成本结算类型（策略）对应结算周期内退货入库的SKU结算临时信息
	 * 
	 * @param smallSellerCode
	 * @return
	 * @throws Exception
	 */
	public abstract MDataMap grabSkuReturned(String smallSellerCode) throws Exception;

	/**
	 * 按结算类型汇总每个sku的交易成功与退货信息
	 */
	protected abstract void summarizeSkuSettleInfo();

	/**
	 * 生成本结算类型帐期内的财务结算单
	 */
	protected abstract void genFinanlBill();
	
	/**
	 * 生成结算单对应的发票单据信息
	 */
	protected abstract void genFinanlBillTicket();
	
	
	
	

	/**
	 * 将一个集合拆分成count大小的多个小集合
	 * 
	 * @param originList
	 *            原集合
	 * @param count
	 *            每个小集合的大小,最后一个集合元素个数可能小于count
	 * @return
	 */
	private <T> List<List<T>> split(List<T> originList, int count) {
		List<List<T>> result = new ArrayList<List<T>>();
		if (originList == null || originList.size() == 0)
			return result;

		int size = originList.size();
		if (size <= count || count == 0) {
			// 数据量不足count指定的大小
			result.add(originList);
		} else {
			int pre = size / count;
			int last = size % count;
			// 前面pre个集合，每个大小都是count个元素
			for (int i = 0; i < count; i++) {
				List<T> itemList = new ArrayList<T>();
				for (int j = 0; j < pre; j++) {
					itemList.add(originList.get(i * pre + j));
				}
				result.add(itemList);
			}
			// last的进行处理
			if (last > 0) {
				List<T> itemList = new ArrayList<T>();
				for (int i = 0; i < last; i++) {
					itemList.add(originList.get(pre * count + i));
				}
				result.add(itemList);
			}
		}
		return result;
	}

	/**
	 * 结算单编号.年月日+结算周期配置编号
	 * 
	 * @return
	 */
	protected String getSettleCode() {
		return DateHelper.upDate(new Date(), "yyyyMMdd") + settlePeriod.get("code").toString();
	}

	/**
	 * 
	 * 方法: isTestSeller <br>
	 * 描述: 判断当前商户是否为测试商户 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年1月1日 下午1:48:27
	 * 
	 * @param small_seller_code
	 * @return
	 */
	protected boolean isTestSeller(String small_seller_code) {
		boolean flag = false;
		String sellers = TopUp.upConfig("ordercenter.test_seller");
		if (StringUtils.isNotBlank(sellers)) {
			List<String> testSellerList = Arrays.asList(sellers.split(","));
			if (testSellerList != null && testSellerList.size() > 0) {
				if (testSellerList.contains(small_seller_code)) {
					return true;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * 方法: getPayType <br>
	 * 描述: 获取结算的支付类型 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年4月13日 下午2:05:39
	 * 
	 * @return
	 */
	public String getPayType() {
		StringBuffer sb = new StringBuffer();
		List<Map<String, Object>> array = DbUp.upTable("oc_import_define").dataSqlList(
				"select pay_type from ordercenter.oc_import_define where flag_able='449746250001'", new MDataMap());
		if (array != null && array.size() > 0) {
			for (Map<String, Object> map : array) {
				sb.append("'").append(map.get("pay_type").toString()).append("'").append(",");
			}
		}
		// 百度外卖导入订单
		sb.append("'449716200005'").append(",");
		// 电视宝商城
		sb.append("'449716200006'").append(",");
		// 民生商城
		sb.append("'4497162000070001'").append(",");
		// 第三方代收
		sb.append("'449716200010'").append(",");
		// 在线支付
		sb.append("'449716200001'");
		return sb.toString();
	}
	
	/**
	 * 排除已经结算过的订单
	 * @param map
	 * @return
	 */
	protected MDataMap dealBillOrder(MDataMap map) {
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String orderCode = iterator.next().toString();
				if (orderCode != null && !"".equals(orderCode.toString().trim())) {
					String settleCode = getSettleCode();
					// 查询该订单是否结算过
					MDataMap bill_order = DbUp.upTable("oc_bill_order").one("order_code",orderCode);
					if(bill_order != null) {
						// 如果结算过,看结算单号是否相同:如果相同,可能是第一次结算失败,允许重新结算;如果不相同,说明是重复结算,排除该订单
						String settle_code = MapUtils.getString(bill_order, "settle_code");
						if(!settle_code.equals(settleCode)) {
							map.remove(orderCode);
						}
					}else {
						// 没结算过,插入表中
						MDataMap insertMap = new MDataMap();
						insertMap.put("settle_code", settleCode);
						insertMap.put("order_code", orderCode);
						insertMap.put("order_deal_time", map.get(orderCode));
						insertMap.put("create_time", DateFormatUtil.getNowTime());
						DbUp.upTable("oc_bill_order").dataInsert(insertMap);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 排除已经结算过的退货单
	 * @param map
	 * @return
	 */
	protected MDataMap dealBillReturn(MDataMap map) {
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String returnCode = iterator.next().toString();
				if (returnCode != null && !"".equals(returnCode.toString().trim())) {
					String settleCode = getSettleCode();
					// 查询该退货单是否结算过
					MDataMap bill_return = DbUp.upTable("oc_bill_order_return").one("return_code",returnCode);
					if(bill_return != null) {
						// 如果结算过,看结算单号是否相同:如果相同,可能是第一次结算失败,允许重新结算;如果不相同,说明是重复结算,排除该退货单
						String settle_code = MapUtils.getString(bill_return, "settle_code");
						if(!settle_code.equals(settleCode)) {
							map.remove(returnCode);
						}
					}else {
						// 没结算过,插入表中
						MDataMap insertMap = new MDataMap();
						insertMap.put("settle_code", settleCode);
						insertMap.put("return_code", returnCode);
						insertMap.put("order_code", map.get(returnCode));
						insertMap.put("create_time", DateFormatUtil.getNowTime());
						DbUp.upTable("oc_bill_order_return").dataInsert(insertMap);
					}
				} 
			}
		}
		return map;
	}
	
	
}
