package com.cmall.groupcenter.service;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.GroupConstant;
import com.cmall.groupcenter.model.AccountCouponInfo;
import com.cmall.groupcenter.model.AccountCouponListResult;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
public class CouponInfoService extends BaseClass{

	/**
	 * 查询优惠卷列表
	 * @param type 优惠卷类型 ：未使用 | 已过期
	 * @param accountCode 用户唯一标识 
	 * @return 优惠卷列表
	 */
	public AccountCouponListResult searchCoupons(String accountCode, String type,PageOption option) {
		AccountCouponListResult couponListResult  = new AccountCouponListResult();
		
		List<MDataMap> mdate = DbUp.upTable("gc_mem_coupon").query("coupon_uid",
				"-create_time,-zid"," account_code = :accountCode ", new MDataMap("accountCode",accountCode),-1,0);//查询用户相关的优惠卷唯一标识
		if(mdate != null && mdate.size() >0){
			//组织优惠卷查询条件
			String uids = " (";
			for (MDataMap mDataMap : mdate) {
				uids +="'" + mDataMap.get("coupon_uid") + "',";
			}
			uids = uids.substring(0,uids.length()-1)+")";
			
			String whereString = " uid in "+uids;
			
			MDataMap paramMap = new MDataMap("today",DateUtil.toString(new Date(),DateUtil.DATE_FORMAT_DATEONLY));
			if("1".equals(type)){//历史优惠卷
				whereString += "  and end_time < :today    ";
				Date expiredTime = new Date(new Date().getTime() - 30*86400000L);
				paramMap.put("expiredTime",DateUtil.toString(expiredTime,DateUtil.DATE_FORMAT_DATEONLY));
			}else{//未使用的优惠卷
				whereString += "  and end_time >= :today ";
			}
			
			//查询优惠卷
			List<MDataMap> coupons = DbUp.upTable("gc_coupon_import").query("uid,name,fkey_id,coupon_amount,limit_description,start_time,end_time,is_exclusive,coupon_code",
					"-update_time",whereString, paramMap, (option.getOffset()*option.getLimit()),option.getLimit());//查询用户相关的优惠卷唯一标识
			if(coupons != null && coupons.size() >0){
				for (MDataMap mDataMap : coupons) {
					//详情页兑换方法，及背景图片
					MDataMap mDataMap2 = DbUp.upTable("gc_coupon_import_manage").oneWhere("pic_url,description","-create_time"," name = :name " ,"name",mDataMap.get("fkey_id"));
					MDataMap mDataMap1 = DbUp.upTable("gc_wonderful_discovery").oneWhere("title,ios_url,android_url,ios_package,android_package,compare_version","-create_time",
							" app_code = :appCode " ,"appCode",
							GroupConstant.APPCODEMAP.get((mDataMap.get("fkey_id"))));
					AccountCouponInfo coupon =new  AccountCouponInfo();
					coupon.setUid(mDataMap.get("uid"));
					coupon.setName(mDataMap.get("name"));
					coupon.setEndTime(mDataMap.get("end_time"));
					coupon.setAmount(mDataMap.get("coupon_amount"));
					coupon.setStartTime(mDataMap.get("start_time"));
					coupon.setDescription(mDataMap.get("limit_description"));
					coupon.setIsExclusiveString(mDataMap.get("is_exclusive"));
					coupon.setCouponCode(mDataMap.get("coupon_code"));
					if(mDataMap2 !=null ){
						coupon.setPicUrl(mDataMap2.get("pic_url"));
						coupon.setConvert(mDataMap2.get("description"));
					}
					if(mDataMap1 !=null ){
						coupon.setAdAppPackage(mDataMap1.get("android_package"));
						coupon.setIosAppPackage(mDataMap1.get("ios_package"));
						coupon.setAdUrl(mDataMap1.get("android_url"));
						coupon.setIosUrl(mDataMap1.get("ios_url"));
						coupon.setAppName(mDataMap1.get("title"));
						coupon.setComVersion(mDataMap1.get("compare_version"));
					}
					couponListResult.getAccountCoupons().add(coupon);
				}
			}
			PageResults pageResults = new PageResults();
			
			String whereStr = "";
			MDataMap paramMap1 = new MDataMap("accountCode",accountCode,"today",DateUtil.toString(new Date(),"yyyy-MM-dd"));
			if("1".equals(type)){//历史优惠卷
				whereStr += "  and import.end_time < :today ";
//				Date expiredTime1 = new Date(new Date().getTime() - 30*86400000L);
//				paramMap1.put("expiredTime1",DateUtil.toString(expiredTime1,DateUtil.DATE_FORMAT_DATEONLY));
			}else{//未使用的优惠卷
				whereStr += "  and import.end_time >= :today ";
			}
			String sqlString = "select * from gc_coupon_import import where import.uid in (select coupon.coupon_uid from gc_mem_coupon coupon where coupon.account_code = :accountCode ) "+whereStr;
			List<Map<String, Object>> list= DbUp.upTable("gc_mem_coupon").dataSqlList(sqlString, paramMap1);
		//	int total = DbUp.upTable("gc_mem_coupon").count("account_code",accountCode);
			pageResults.setTotal(list.size());
			pageResults.setCount(couponListResult.getAccountCoupons().size());
			pageResults.setMore((option.getLimit() * option.getOffset() + pageResults
					.getCount()) < pageResults.getTotal() ? 1 : 0);
			couponListResult.setPageResults(pageResults);
		}
		return couponListResult;
	}

	
	/**
	 * 根据couponId 删除指定优惠卷与当前用户的关联
	 * @param couponId  优惠卷唯一标识
	 * @param accountCode 用户唯一标识
	 */
	public AccountCouponListResult removeCoupon(String couponId,String accountCode) {
		DbUp.upTable("gc_mem_coupon").dataDelete(" coupon_uid = :couponId and account_code = :accountCode ", new MDataMap("couponId",couponId,"accountCode",accountCode), "");
		return new AccountCouponListResult();
	}

	
	/**
	 * 根据couponId 查询指定优惠卷的信息
	 * @param couponId 优惠卷唯一标识
	 * @return 返回指定优惠卷的信息
	 */
	public AccountCouponListResult searchCoupon(String couponId) {
		AccountCouponListResult couponListResult  = new AccountCouponListResult();
		//查询优惠卷
		MDataMap mDataMap = DbUp.upTable("gc_coupon_import").oneWhere("uid,name,fkey_id,coupon_amount,limit_description,start_time,end_time,is_exclusive,coupon_code",
				"-import_time"," uid = :couponId ","couponId",couponId);//查询用户相关的优惠卷唯一标识
		if(mDataMap != null){
			//详情页兑换方法，及背景图片
			MDataMap mDataMap1 = DbUp.upTable("gc_coupon_import_manage").oneWhere("pic_url,description","-create_time"," name = :name " ,"name",mDataMap.get("fkey_id"));
			MDataMap mDataMap2 = DbUp.upTable("gc_wonderful_discovery").oneWhere("title,ios_url,android_url,ios_package,android_package,compare_version","-create_time",
					" app_code = :appCode " ,"appCode",
					GroupConstant.APPCODEMAP.get((mDataMap.get("fkey_id"))));
			AccountCouponInfo coupon =new  AccountCouponInfo();
			coupon.setUid(mDataMap.get("uid"));
			coupon.setName(mDataMap.get("name"));
			coupon.setEndTime(mDataMap.get("end_time"));
			coupon.setAmount(mDataMap.get("coupon_amount"));
			coupon.setStartTime(mDataMap.get("start_time"));
			coupon.setDescription(mDataMap.get("limit_description"));
			coupon.setIsExclusiveString(mDataMap.get("is_exclusive"));
			coupon.setCouponCode(mDataMap.get("coupon_code"));
			if(mDataMap1 !=null ){
				coupon.setPicUrl(mDataMap1.get("pic_url"));
				coupon.setConvert(mDataMap1.get("description"));
			}
			//-----------
			if(mDataMap2 != null){
				coupon.setAppName(mDataMap2.get("title"));
				coupon.setAdAppPackage(mDataMap2.get("android_package"));
				coupon.setIosAppPackage(mDataMap2.get("ios_package"));
				coupon.setAdUrl(mDataMap2.get("android_url"));
				coupon.setIosUrl(mDataMap2.get("ios_url"));
				coupon.setComVersion(mDataMap2.get("compare_version"));
			}
			couponListResult.getAccountCoupons().add(coupon);
		}
		return couponListResult;
	}
	
}
