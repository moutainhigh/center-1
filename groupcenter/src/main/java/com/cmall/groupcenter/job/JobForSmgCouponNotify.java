package com.cmall.groupcenter.job;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.util.AESUtil;
import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.xmassystem.service.ShortLinkService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 	给发送优惠券的人员发送短信和推送通知
 */
public class JobForSmgCouponNotify extends RootJob {
	
	static Log log = LogFactory.getLog(JobForSmgCouponNotify.class);
	static String className = "com.cmall.groupcenter.job.JobForSmgCouponNotify";
	
	static ReentrantLock lock = new ReentrantLock();

	public void doExecute(JobExecutionContext context) {
		if(!lock.tryLock()) {
			return;
		}
		
		try {
			doWork();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	private void doWork() {
		MDataMap timeMap = getTimeMap();
		// 往前兼容15分钟
		String updateTime = FormatHelper.upDateTime(DateUtils.addMinutes(new Date(), -15), "yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		
		SmsUtil smsUtil = new SmsUtil();
		ShortLinkService shortLinkService = new ShortLinkService();
		List<MDataMap> list = DbUp.upTable("fh_smg_coupon").queryAll("", "", "notify_flag = 0 AND create_time > :startTime AND expired_time > now()", new MDataMap("startTime",timeMap.get("static_info")));
		String memberCode,productCode,loginName,productName,money,couponCode;
		for(MDataMap map : list) {
			memberCode = map.get("member_code");
			productCode = map.get("product_code");
			money = map.get("money");
			couponCode = map.get("coupon_code");
			loginName = (String)DbUp.upTable("mc_login_info").dataGet("login_name", "", new MDataMap("member_code", memberCode));
			productName = (String)DbUp.upTable("pc_productinfo").dataGet("product_name", "", new MDataMap("product_code", productCode));
			if(StringUtils.isBlank(loginName)) {
				continue;
			}		
			// 短信发送时间限定在7点到23点之间
			if(hour >= 7 && hour < 23) {
				StringBuffer s = new StringBuffer();
				//订单来源固定为小程序,惠家有渠道短信
				AESUtil aesUtil = new AESUtil();
				aesUtil.initialize();
				String phone_num = aesUtil.encrypt(loginName);
				try {
					String encode = URLEncoder.encode(phone_num, TopConst.CONST_BASE_ENCODING);
					String longLink = bConfig("groupcenter.wei_shop_url_new")+"zh.html?phone="+encode+"&osc=449715190025&pagetype=449748740014&ordertype=&couponCodes="+couponCode;
					String expireTime = DateUtil.addDateHour(updateTime, 24);
					String shortLink = shortLinkService.createShortLink(longLink, "system", expireTime);
					boolean b = smsUtil.sendSmsForYX(loginName, FormatHelper.formatString(bConfig("groupcenter.smg_coupon_zhaohui_new"), money, productName,money,shortLink));
					// 发送失败
					if(!b) {
						log.warn("JobForSmgCouponNotify -> failed!" + loginName + ", " + s.toString());
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			map.put("update_time", FormatHelper.upDateTime());
			map.put("notify_flag", "1");
			DbUp.upTable("fh_smg_coupon").dataUpdate(map, "update_time,notify_flag", "zid");
		}
		
		// 更新增量时间
		timeMap.put("static_info", updateTime);
		DbUp.upTable("za_static").dataUpdate(timeMap, "static_info", "static_code");
	}

	private MDataMap getTimeMap() {
		MDataMap infoMap = DbUp.upTable("za_static").one("static_code",className);
		if(infoMap == null) {
			infoMap = new MDataMap();
			infoMap.put("static_code", className);
			infoMap.put("static_info", "2020-08-06 00:00:00");
			infoMap.put("create_time", FormatHelper.upDateTime());
			infoMap.put("update_time", FormatHelper.upDateTime());
			DbUp.upTable("za_static").dataInsert(infoMap);
		}
		return infoMap;
	}

}
