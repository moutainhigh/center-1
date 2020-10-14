package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 统计昨天商品真实销量以及商品虚拟销量（建议定时任务设置为每天凌晨执行）
 * 
 * @author ligj
 *
 */
public class JobCensusProductYesterdaySales extends RootJob {
	
	public void doExecute(JobExecutionContext context) {
		
		StringBuffer censusSql = new StringBuffer();
		//统计下单成功-未发货状态
		//统计惠家有与沙皮狗
		censusSql.append(" select od.product_code as product_code , SUM(od.sku_num) as yesterday_sales,oi.seller_code as seller_code ");
		censusSql.append(" from ordercenter.oc_orderdetail od,ordercenter.oc_orderinfo oi ");
		censusSql.append(" where od.order_code=oi.order_code and oi.order_status in ('4497153900010002','4497153900010003','4497153900010004','4497153900010005') and oi.zid > 4685393");
		censusSql.append("  and date_format(oi.create_time,'%Y-%m-%d') = date_sub(curdate(), INTERVAL 1 DAY)  ");						
		censusSql.append("   and oi.seller_code in ('SI2003','SI3003') and oi.delete_flag='0' group by od.product_code ");
	    List<Map<String, Object>> list=DbUp.upTable("oc_orderinfo").dataSqlList(censusSql.toString(),null);
	  //获取销量统计表中昨日统计的所有的product_code,用以判断是否已经统计计算过昨日销量
		List<MDataMap> salesMapList = DbUp.upTable("pc_productsales_everyday").queryAll("product_code,day,uid", "", "day=date_sub(curdate(), INTERVAL 1 DAY)", null);
		Map<String,String> yesterdayMap = new HashMap<String, String>();
		for (MDataMap mDataMap : salesMapList) {
			yesterdayMap.put(mDataMap.get("product_code"), mDataMap.get("uid"));
		}
	    if(list!=null&&list.size()>0){
	    	//获取昨天日期
	    	Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
			 for (Map<String, Object> map : list) {
				String product_code=String.valueOf((map.get("product_code") == null ? "" : map.get("product_code")));
				String sales=String.valueOf((map.get("yesterday_sales") == null ? "0" : map.get("yesterday_sales")));
				String seller_code=String.valueOf((map.get("seller_code") == null ? "" : map.get("seller_code")));
				
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("product_code", product_code);
				mDataMap.put("sales", sales);
				mDataMap.put("day", yesterday);
				mDataMap.put("seller_code", seller_code);
				mDataMap.put("create_time", DateUtil.getSysDateTimeString());
				
				if (StringUtils.isNotEmpty(yesterdayMap.get(product_code))) {
					//如果已经存在，则不再进行修改操作
//					mDataMap.put("uid", salesMap.get(product_code));
//					DbUp.upTable("oc_product_salesCount").dataUpdate(mDataMap,"thirty_day,last_month,total_all,seller_code","uid");
				}else{
					DbUp.upTable("pc_productsales_everyday").dataInsert(mDataMap);
					XmasKv.upFactory(EKvSchema.ProductSales).del(product_code);		//刷新销量缓存
				}
			}
		 }
	}
}
