package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.util.CouponUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * @ClassName: JobSpecificUserCoupon
 * @Description:定时扫描给指定账户发优惠券
 * @author 张海生
 * @date 2016-1-22 上午11:06:41
 * 
 */
public class JobSpecificUserCoupon extends RootJob { 
	//执行级别0:main 1:slave
	private int level = 0;
	private String tableName;
	private static ExecutorService service;
	
	public void doExecute(JobExecutionContext context) {
		// synchronized (JobSpecificUserCoupon.class) {
		String uuid = "";
		try {
			// modify by zht 2016/5/14
			String lockKey = "JobSpecificUserCoupon-";
			if(level == 0) {
				uuid = WebHelper.addLock(lockKey + "main", 72 * 60 * 60);
				tableName = "oc_coupon_check";
			} else {
				uuid = WebHelper.addLock(lockKey + level, 72 * 60 * 60);
				tableName = "oc_coupon_check" + level;
				if(StringUtils.isEmpty(uuid)) 
					throw new Exception("Can't lock " + tableName);
			}
			
			if (StringUtils.isNotEmpty(uuid)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String id = sdf.format(new Date());
				Thread.currentThread().setName("JobSpecificUserCoupon-" + id);
				List<MDataMap> dataList = DbUp.upTable(tableName).queryAll(
						"uid,task_code,activity_code,mobile,manage_code", "zid asc", null,
						new MDataMap("distribute_status", "4497471600250002"));// 查询未发放优惠券的用户
				if (dataList != null && dataList.size() > 0) {
					int threadSize = initThreadPool(dataList.size());
					List<List<MDataMap>> itemLists = split(dataList, threadSize);
					for(List<MDataMap> itemList : itemLists) {
						ItemJob itemJob = new ItemJob(itemList);
						service.execute(itemJob);
					}
				}
				//等待所有线程执行完毕退出释放锁
				service.shutdown();
		        try {  
		            boolean loop = true;  
		            do {  
		                loop = !service.awaitTermination(2, TimeUnit.SECONDS);
		            } while(loop);  
		        } catch (InterruptedException e) {  
		            e.printStackTrace();  
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (StringUtils.isNotEmpty(uuid)) 
				WebHelper.unLock(uuid);
		}
	}
	
	public void httpDo(String level) {
		if(StringUtils.isNotEmpty(level)) {
			try {
				this.level = Integer.parseInt(level);
			} catch(Exception e) {
				this.level = 1;
			}
			doExecute(null);
		}
	}

	/**
	 * 将一个集合拆分成count大小的多个小集合
	 * 
	 * @param originList 原集合
	 * @param count 每个小集合的大小,最后一个集合元素个数可能小于count
	 * @return
	 */
	private <T> List<List<T>> split(List<T> originList, int count) {
		if (originList == null || count < 1)
			return null;
		List<List<T>> result = new ArrayList<List<T>>();
		int size = originList.size();
		if (size <= count) {
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
	
	public int initThreadPool(int listSize) {
		int size = 1;
		if(listSize >= 1000 && listSize <= 10000) {
			size = 10;
		} else if(listSize >= 10000) {
			size = 30;
		}
		service = Executors.newFixedThreadPool(size);
		return size;
	}

	public class ItemJob implements Runnable {
		private List<MDataMap> dataList;

		public ItemJob(List<MDataMap> dataList) {
			this.dataList = dataList;
		}

		@Override
		public void run() {
			int couponCount = Integer.parseInt(bConfig("groupcenter.coupon_count"));
			CouponUtil up = new CouponUtil();
			MDataMap upData = new MDataMap();
			MDataMap upTaskMap = new MDataMap();
			MDataMap typeWhereMap = new MDataMap();
			try {
				int insertCount = 0;
				for (MDataMap mDataMap : dataList) {
					String taskCode = mDataMap.get("task_code");
					String mobile = StringUtils.isEmpty(mDataMap.get("mobile")) ? ""
							: mDataMap.get("mobile").toString().trim();
					String activityCode = mDataMap.get("activity_code");
					MDataMap acMap = DbUp.upTable("oc_activity").oneWhere("flag,begin_time,end_time", "", "",
							"activity_code", activityCode);
					if (acMap == null)
						continue;
					String nowTime = DateUtil.getNowTime();
					String startTime = acMap.get("begin_time");
					String endTime = acMap.get("end_time");
					if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0
							|| DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0
							|| acMap.get("flag") == "0")
						continue;
					MDataMap udata = DbUp.upTable("mc_login_info").oneWhere("member_code", null, null, "login_name",
							mobile, "manage_code", mDataMap.get("manage_code"));// 查询系统里是否有该用户
					if (udata != null) {
						insertCount++;
						String memberCode = udata.get("member_code");
						typeWhereMap.put("activity_code", activityCode);
						typeWhereMap.put("status", "4497469400030002");
						List<MDataMap> couponTypeList = DbUp.upTable("oc_coupon_type").queryAll("coupon_type_code", "",
								"", typeWhereMap);
						for (MDataMap typeMap2 : couponTypeList) {
							String couponTypeCode = typeMap2.get("coupon_type_code");// 优惠券类型编号
							int flag = 0;
							for (int i = 0; i < couponCount; i++) {
								flag = up.provideCoupon(memberCode, couponTypeCode, "");// 插入用户优惠记录
								if (flag == 0)
									break;
							}
							if (flag == 1) {
								up.updateCouponType(1, couponTypeCode, "system");// 更新优惠券发放数额
								String uid = mDataMap.get("uid");
								MDataMap mobileMap = DbUp.upTable(tableName).oneWhere("distribute_status", "",
										"", "uid", uid);
								if (mobileMap != null
										&& !"4497471600250003".equals(mobileMap.get("distribute_status"))) {
									upData.put("distribute_status", "4497471600250003");// 更新为已发放
									upData.put("uid", uid);
									upData.put("update_time", DateUtil.getNowTime());
									upData.put("update_user", "system");
									DbUp.upTable(tableName).dataUpdate(upData,
											"distribute_status,update_time,update_user", "uid");// 更新为已发放
								}
								MDataMap taskMap = DbUp.upTable("oc_coupon_task").oneWhere("distribute_status", "", "",
										"task_code", taskCode);
								if (taskMap != null && !"4497471600250003".equals(taskMap.get("distribute_status"))) {
									upTaskMap.put("update_time", DateUtil.getNowTime());
									upTaskMap.put("update_user", "system");
									upTaskMap.put("distribute_status", "4497471600250003");// 更新为已发放
									upTaskMap.put("task_code", taskCode);
									DbUp.upTable("oc_coupon_task").dataUpdate(upTaskMap,
											"distribute_status,update_time,update_user", "task_code");// 只要有一个账户发放就把总任务状态更新为已发放
								}
							}
						}
						if(insertCount % 100 == 0 ) {
							try {
								Thread.sleep(10L);
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class TestJob implements Runnable
	{
		String seq;
		JobSpecificUserCoupon j;
		public TestJob(String seq) {
			j = new JobSpecificUserCoupon();
			this.seq = seq;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			j.httpDo(seq);
		}
		
	}
	
	public static void main(String[] args) {
		Thread t0 = new Thread(new TestJob("0"));
		t0.start();
		Thread t1 = new Thread(new TestJob("1"));
		t1.start();
		Thread t2 = new Thread(new TestJob("2"));
		t2.start();
		Thread t3 = new Thread(new TestJob("3"));
		t3.start();
	}
}
