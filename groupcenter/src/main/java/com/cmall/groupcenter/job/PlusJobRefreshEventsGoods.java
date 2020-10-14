package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.top.PlusConfigScheduler;
import com.srnpr.xmassystem.top.PlusTopScheduler;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.GsonHelper;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webface.IKvSchedulerConfig;

/**
 * 
 * 
 *
 */
public class PlusJobRefreshEventsGoods extends PlusTopScheduler {

	public IBaseResult execByInfo(String sInfo) {

		String eventCode = new GsonHelper().fromJson(sInfo,
				new String());

		
		RootResultWeb result = new RootResultWeb();
		String sql = "select DISTINCT product_code from sc_event_item_product where event_code = '"+eventCode+"' ";
		List<Map<String, Object>> dataSqlList = DbUp.upTable("sc_event_item_product").dataSqlList(sql, null);
		List<String> lists = new ArrayList<>();
		for(Map<String, Object> map : dataSqlList) {
			String productCode = map.get("product_code").toString();
			lists.add(productCode);
		}
		/**十个线程一起跑*/
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (String productCode : lists) {
			productCode = StringUtils.trim(productCode);
			if(StringUtils.isEmpty(productCode)){
				continue;
			}

			executorService.submit(new DownTask(productCode));
		}
		
		executorService.shutdown();
		try {
			executorService.awaitTermination(1800, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	private final static PlusConfigScheduler plusConfigScheduler = new PlusConfigScheduler(
			EPlusScheduler.UpdateEventsGoods);

	public IKvSchedulerConfig getConfig() {

		return plusConfigScheduler;
	}

	public static class DownTask implements Runnable{

		private String productCode;
		
		public DownTask(String productCode) {
			super();
			this.productCode = productCode;
		}


		@Override
		public void run() {
			//触发消息队列
			new ProductJmsSupport().onChangeForProductChangeAll(productCode);
		}
		
	}
}
