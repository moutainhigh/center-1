package com.cmall.groupcenter.active.product;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 条件判断
 * @author ligj
 *
 */
public class ConditionCheck extends BaseClass {
	private static final String SATURDAY = "星期六";
	private static final String SUNDAY = "星期日";
//	private static final Integer ELEVEN = 11;
	private static final String VIP_TYPE_EMPLORER = "4497469400050001";				//内部员工
	private static final String VIP_TYPE_GENERAL = "4497469400050002";				//一般会员
	/**
	 * 判断是否是内购日
	 * @return
	 */
	public static boolean checkIsVipSpecialDay(){
		String weekDay = DateUtil.getSystemWeekdayString();
		 if(!SATURDAY.equals(weekDay) && !SUNDAY.equals(weekDay)){
		 return false;
		 }
		return true;
	}
	/**
	 * 判断是否是会员日
	 * @return
	 */
	public boolean checkIsVipGeneralDay(){
		SimpleDateFormat sysDateTime = new SimpleDateFormat("dd");
		String day = sysDateTime.format(new Date());
		String VipSpecial_day=bConfig("groupcenter.VipSpecial_day");
		if(StringUtils.isNotBlank(VipSpecial_day)&&VipSpecial_day.indexOf(day)>=0){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		//System.out.println(new ConditionCheck().checkIsVipGeneralDay());
	}
	
	
	/**
	 * 判断是否是内购员工（家有汇）
	 * @return
	 */
	public static boolean checkIsVipSpecial(String userCode){
		if (StringUtils.isEmpty(userCode)) {
			return false;
		}
		MDataMap mData = DbUp.upTable("mc_extend_info_homepool").oneWhere(
				"vip_type", null, null, "member_code", userCode,"vip_type",VIP_TYPE_EMPLORER);
		if(mData == null || mData.isEmpty() ){
			return false;
		}
		return true;
	}
	/**
	 * 判断是否是会员
	 * @return
	 */
	public static boolean checkIsVipGeneral(String userCode){
		if (StringUtils.isEmpty(userCode)) {
			return false;
		}
		MDataMap userInfo = DbUp.upTable("mc_extend_info_homepool").one("member_code",userCode,"vip_type",VIP_TYPE_GENERAL);
	  	if (userInfo == null || userInfo.isEmpty()) {
			return false;
	  	}
		return true;
	}
}
