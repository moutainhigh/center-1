package com.cmall.groupcenter.job;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * @ClassName: JobUserCoupon
 * @Description: 定时扫描待发送优惠券用户发送优惠券
 * @author 张海生
 * @date 2015-4-9 下午2:46:24
 * 
 */
public class JobProduceCdkey extends RootJob {
	
	/**
	   * 产生随机字符串
	   * */
	private static Random randGen = new Random();;
	private static char[] numbersAndLetters = ("123456789ABCDEFGHIJKLMNPQRSTUVWXYZ").toCharArray();

	public void doExecute(JobExecutionContext context) {
		String lockCode = WebHelper.addLock(900, "cdkeyproduce136586");
		if (StringUtils.isNotBlank(lockCode)) {
			String fields = "uid,activity_code,multi_account,account_useTime,cdkey,use_people,cdkey_prefix,create_num,create_time,create_user,manage_code";
			List<MDataMap> mdataList = DbUp.upTable("oc_cdkey_provide")
					.queryAll(fields, null, "",
							new MDataMap("task_status", "0"));// 查出所有待生成的优惠码任务
			if(mdataList != null && mdataList.size() > 0){
				for (MDataMap mDataMap : mdataList) {
					this.updataCdkeyProvide(mDataMap.get("uid"),"1");//执行中
					String multiAccount = mDataMap.get("multi_account");
					if("449746250001".equals(multiAccount)){//多账户使用
						String cdkey = mDataMap.get("cdkey");
						int count = DbUp.upTable("oc_coupon_cdkey").count("cdkey",cdkey,"manage_code", mDataMap.get("manage_code"));
						if(count > 0) continue;
						MDataMap muData = new MDataMap();
						muData.put("multi_account", mDataMap.get("multi_account"));
						muData.put("account_useTime", mDataMap.get("account_useTime"));
						muData.put("cdkey", cdkey);
						muData.put("use_people", mDataMap.get("use_people"));
						muData.put("activity_code", mDataMap.get("activity_code"));
						muData.put("create_time", mDataMap.get("create_time"));
						muData.put("create_user", mDataMap.get("create_user"));
						muData.put("manage_code", mDataMap.get("manage_code"));
						try {
							DbUp.upTable("oc_coupon_cdkey").dataInsert(muData);//插入多账户使用的优惠码
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if("449746250002".equals(multiAccount)){//单账户使用
						int createNum = Integer.parseInt(mDataMap.get("create_num"));//生成数
						Set<String> randomCdkey = new HashSet<String>();
						for (int i = 0; i < 5; i++) {
							randomCdkey = this.randomString(8, createNum * 2, mDataMap.get("cdkey_prefix"));//生成优惠码
							if(randomCdkey != null && randomCdkey.size() >= createNum){
								break;
							}else{
								continue;
							}
						}
						if(randomCdkey != null && randomCdkey.size() >= createNum){
							//按前缀查询 modified by zht 2016/06/06
							List<MDataMap> cdkeyList = DbUp.upTable("oc_coupon_cdkey").queryAll("cdkey", "", "", new MDataMap("manage_code", mDataMap.get("manage_code"), "cdkey_prefix", mDataMap.get("cdkey_prefix")));
//							List<MDataMap> cdkeyList = DbUp.upTable("oc_coupon_cdkey").queryAll("cdkey", "", "", new MDataMap("manage_code", mDataMap.get("manage_code")));
							MDataMap sgData = new MDataMap();
							sgData.put("multi_account", mDataMap.get("multi_account"));
							sgData.put("account_useTime", mDataMap.get("account_useTime"));
							sgData.put("cdkey_prefix", mDataMap.get("cdkey_prefix"));
							sgData.put("activity_code", mDataMap.get("activity_code"));
							sgData.put("create_time", mDataMap.get("create_time"));
							sgData.put("create_user", mDataMap.get("create_user"));
							sgData.put("manage_code", mDataMap.get("manage_code"));
							int k =1;
							for (String cdkeystr : randomCdkey) {
								if(k > createNum)//已经插入了预订数量的优惠码
									break;
								int flag = 0;
								for (MDataMap mDataMap2 : cdkeyList) {
									if(cdkeystr.equals(mDataMap2.get("cdkey"))){
										flag = 1;//有重复的优惠码
										break;
									}
								}
								if(flag == 1) continue;
								sgData.put("cdkey", cdkeystr);
								try {
									DbUp.upTable("oc_coupon_cdkey").dataInsert(sgData);//插入单账户使用的优惠码
								} catch (Exception e) {
									e.printStackTrace();
								}
								k++;
							}
						}
					}
					this.updataCdkeyProvide(mDataMap.get("uid"),"2");//已执行
				}
			}
			WebHelper.unLock(lockCode);
		}
	}
	
	/** 
	* @Description:更新生成优惠码任务记录的状态
	* @param uid
	* @param status 执行状态
	* @author 张海生
	* @date 2015-6-3 下午4:23:43
	* @return void 
	* @throws 
	*/
	public void updataCdkeyProvide(String uid, String status){
		MDataMap smDataMap = new MDataMap();
		smDataMap.put("uid", uid);
		smDataMap.put("task_status", status);
		//先把待发放记录更新为已发放，以防网络等各种原因先发了，没更新，造成多发的情况
		DbUp.upTable("oc_cdkey_provide").dataUpdate(smDataMap, "task_status", "uid");
	}
	
	/** 
	* @Description:生成随机串
	*@param length 长度
	*@param num 生成数量
	*@param start 开头字符串
	* @author 张海生
	* @date 2015-6-3 下午2:58:53
	* @return Set<String> 
	* @throws 
	*/
	public Set<String> randomString(int length,int num,String start) {
	         if (length < 1||num < 1) {
	             return null;
	         }
	         Set<String> set = new HashSet<String>();
	         for (int j = 0; j < num; j++) {
	        	 char [] randBuffer = new char[length];
		         for (int i=0; i<randBuffer.length; i++) {
		          randBuffer[i] = numbersAndLetters[randGen.nextInt(34)];
		         }
		         set.add(start+new String(randBuffer));
	         }
	         return set;
	}
	
	public static void main(String[] args) {
		JobProduceCdkey job = new JobProduceCdkey();
		job.doExecute(null);
	}
}
