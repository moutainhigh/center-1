package com.cmall.groupcenter.mq.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.cmall.groupcenter.homehas.HomehasSupport;
import com.cmall.groupcenter.mq.model.GiftVoucherDetailListenModel;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class GiftVoucherService {

	@SuppressWarnings("unused")
	public MWebResult reginRsyncGiftVoucherDetail(GiftVoucherDetailListenModel detail) {
		MWebResult mWebResult = new MWebResult();
		
		String dis_type = detail.getDis_type() == null ? "" : detail.getDis_type().toString(); //折扣类型 10金额券 20折扣券
		String initial_money = "0";
		if(StringUtils.isNotBlank(detail.getLj_amt()) && "20".equals(dis_type)) {
			initial_money = new BigDecimal(detail.getLj_amt().toString()).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString();
		}else {
			initial_money = detail.getLj_amt().toString();
		}
		String limit_money = detail.getOrd_amt() == null ? "0" : detail.getOrd_amt().toString(); //限定金额
		String status = detail.getSy_vl() == null ? "1" : (detail.getSy_vl().toUpperCase().equals("Y") ? "1" : "0");  //是否已使用，1:是；0:否
		String start_time = detail.getFr_date() == null ? "" : detail.getFr_date().toString();  //开始时间
		String end_time = detail.getEnd_date() == null ? "" : detail.getEnd_date().toString();  //结束时间
		String create_time = detail.getEtr_date() == null ? "" : detail.getEtr_date().toString();  //创建时间
		String update_time = detail.getMdf_date() == null ? "" : detail.getMdf_date().toString();;  //更新时间
		String big_order_code = detail.getLj_rel_id() == null ? "" : detail.getLj_rel_id().toString();  //大订单编号
		String out_coupon_code = detail.getLj_code() == null ? "" : detail.getLj_code().toString(); //礼金券编号  格式：活动编号-客代-礼金券序号
		String cust_id = detail.getCust_id() == null ? "" : detail.getCust_id().toString(); //LD客户代码  根据用户编号查询客代号是否存在，不存在则进行关联
		String member_code = detail.getMembercode() == null ? "" : detail.getMembercode().toString(); //惠家有用户编号
		String hjy_ord_id = detail.getHjy_ord_id() == null ? "" : detail.getHjy_ord_id().toString(); //惠家有关联订单号
		String mobile = detail.getMobile() == null ? "" : detail.getMobile().toString(); //用户手机号
		String coupon_code = detail.getCoupon_code() == null ? "" : detail.getCoupon_code().toString(); //惠家有优惠券编号
		String last_mdf_id = "ld"; //!!! 此处决定是否触发 第三方库的触发器
		String cust_lvl_cd = detail.getCust_lvl_cd() == null ? "" : detail.getCust_lvl_cd().toString(); //客户等级
		String is_see = "0"; //是否查看 默认未查看 只有在新增时用到
		String blocked = "0"; //不知道啥意思 数据库没有默认值 新业务未做兼容 不加就报错喽
		String lj_balance_amt = detail.getLj_balance_amt() == null? "0" : detail.getLj_balance_amt().toString(); //礼金余额
		String is_change = detail.getIs_change() == null ? "N" : detail.getIs_change().toUpperCase(); //是否允许找零
		
		//剩余金额 
		//找零券应用此字段 
		//非找零券 根据状态将此字段置为0或面额
		String surplus_money = "0"; 
		if("0".equals(status)) surplus_money = initial_money;
		if("Y".equals(is_change)) surplus_money = lj_balance_amt;
		
		//ld回写外部优惠券编号 或惠家有领券在ld使用 或ld还原优惠券
		if(!"".equals(coupon_code) && !"".equals(out_coupon_code)) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("coupon_code", coupon_code);
			mDataMap.put("out_coupon_code", out_coupon_code);
			mDataMap.put("last_mdf_id", last_mdf_id);
			mDataMap.put("status", status);
			mDataMap.put("surplus_money", surplus_money);
			mDataMap.put("update_time", update_time);
			DbUp.upTable("oc_coupon_info").dataUpdate(mDataMap, "status,out_coupon_code,last_mdf_id,surplus_money,update_time", "coupon_code");
			//回写客代及等级
			dealCustId(mobile, cust_id, cust_lvl_cd);
			//558增加优惠券使用缓存
			dealUseCouponCache(coupon_code, status);
		}else {
			
			if(cust_id == null || "".equals(cust_id)) {
				//同步礼金券明细失败，{0}的LD客户代码为空
				mWebResult.inErrorMessage(918505112, out_coupon_code);
				return mWebResult;
			}
			if(mobile == null || "".equals(mobile)) {
				//同步礼金券明细失败，{0}的手机号码为空
				mWebResult.inErrorMessage(918505113, out_coupon_code);
				return mWebResult;
			} else {
				// 过滤非手机号码的账户
				if(!mobile.matches("1\\d{10}")){
					mWebResult.inErrorMessage(918505114, out_coupon_code);
					return mWebResult;
				}
				
				MDataMap loginInfo = DbUp.upTable("mc_login_info").one("manage_code","SI2003","login_name",mobile);
				if(loginInfo != null) {
					member_code = loginInfo.get("member_code");
				} else {
					MWebResult result = new HomehasSupport().registerWithResult(mobile, RandomStringUtils.randomNumeric(8));
					
					if(result.getResultCode() != 1) {
						mWebResult.inErrorMessage(918505131, cust_id);
						return mWebResult;
					}
					member_code = ((MDataMap)result.getResultObject()).get("memberCode");
				}	
			}			
			
			String outActivityCode = "";
			String[] tmpArr = out_coupon_code.split("-");
			if(tmpArr.length == 3) {
				outActivityCode = tmpArr[0];
				MDataMap mActivityMap = DbUp.upTable("oc_activity").one("out_activity_code", outActivityCode);
				if(mActivityMap == null) {
					//同步礼金券明细失败，礼金券活动不存在
					mWebResult.inErrorMessage(918505110);
					return mWebResult;
				}
				String activity_code = mActivityMap.get("activity_code").toString();
				MDataMap mCouponTypeMap = DbUp.upTable("oc_coupon_type").one("activity_code", activity_code);
				if(mCouponTypeMap == null) {
					//同步礼金券明细失败，礼金券类型不存在
					mWebResult.inErrorMessage(918505111);
					return mWebResult;
				}
				
				//回写客代及等级
				dealCustId(mobile, cust_id, cust_lvl_cd);
				
				String coupon_type_code = mCouponTypeMap.get("coupon_type_code").toString();
				MDataMap mCouponInfoMap = DbUp.upTable("oc_coupon_info").one("out_coupon_code", out_coupon_code, "member_code", member_code);
				if(mCouponInfoMap == null) {
					//新增
					coupon_code = WebHelper.upCode("CP");
					String uid=UUID.randomUUID().toString().replace("-", "");
					MDataMap aCouponInfoMap = new MDataMap();
					aCouponInfoMap.put("uid", uid);
					aCouponInfoMap.put("coupon_type_code", coupon_type_code);
					aCouponInfoMap.put("activity_code", activity_code);
					aCouponInfoMap.put("coupon_code", coupon_code);
					aCouponInfoMap.put("member_code", member_code.toString());
					aCouponInfoMap.put("initial_money", initial_money);
					aCouponInfoMap.put("surplus_money", surplus_money);
					aCouponInfoMap.put("limit_money", limit_money);
					aCouponInfoMap.put("status", status);
					aCouponInfoMap.put("start_time", start_time);
					aCouponInfoMap.put("end_time", end_time);
					aCouponInfoMap.put("create_time", create_time);
					aCouponInfoMap.put("update_time", update_time);
					aCouponInfoMap.put("big_order_code", big_order_code);
					aCouponInfoMap.put("out_coupon_code", out_coupon_code);
					aCouponInfoMap.put("last_mdf_id", last_mdf_id);
					aCouponInfoMap.put("is_see", is_see); 
					aCouponInfoMap.put("blocked", blocked); 
					DbUp.upTable("oc_coupon_info").dataInsert(aCouponInfoMap);				
				} else {
					//558增加优惠券使用缓存
					dealUseCouponCache(mCouponInfoMap.get("coupon_code"), status);
					
					//修改
					MDataMap eCouponInfoMap = new MDataMap();
					eCouponInfoMap.put("coupon_type_code", coupon_type_code);
					eCouponInfoMap.put("activity_code", activity_code);
					eCouponInfoMap.put("member_code", member_code.toString());
					eCouponInfoMap.put("initial_money", initial_money);
					eCouponInfoMap.put("surplus_money", surplus_money);
					eCouponInfoMap.put("limit_money", limit_money);
					eCouponInfoMap.put("status", status);
					eCouponInfoMap.put("start_time", start_time);
					eCouponInfoMap.put("end_time", end_time);
					eCouponInfoMap.put("update_time", update_time);
					eCouponInfoMap.put("big_order_code", big_order_code);
					eCouponInfoMap.put("out_coupon_code", out_coupon_code);
					eCouponInfoMap.put("last_mdf_id", last_mdf_id);
					DbUp.upTable("oc_coupon_info").dataUpdate(eCouponInfoMap, "coupon_type_code,activity_code,member_code,initial_money,surplus_money,limit_money,status,start_time,end_time,update_time,big_order_code,last_mdf_id", "out_coupon_code,member_code");
				}			
			} else {
				mWebResult.inErrorMessage(918505108);
			}		
		}
		
		return mWebResult;
	}
	
	/**
	 * 如果优惠券是使用 则增加使用缓存<br/>
	 * 如果优惠券是取消 则删除使用缓存
	 * @param couponCode
	 * @param newStatus
	 */
	private void dealUseCouponCache(String couponCode, String newStatus) {
		MDataMap coupon = DbUp.upTable("oc_coupon_info").one("coupon_code", couponCode);
		if (null != coupon) {
			String oldStatus = coupon.get("status");
			if (("0".equals(oldStatus) && "1".equals(newStatus)) || ("1".equals(oldStatus) && "1".equals(newStatus))) {
				Long setnx = XmasKv.upFactory(EKvSchema.CouponUse).setnx(couponCode, "1");
				if (setnx == 1) {
					int seconds = getSeconds(coupon.get("end_time"));
					XmasKv.upFactory(EKvSchema.CouponUse).expire(couponCode, seconds);
				}
			} else if (("1".equals(oldStatus) && "0".equals(newStatus))
					|| ("0".equals(oldStatus) && "0".equals(newStatus))) {
				XmasKv.upFactory(EKvSchema.CouponUse).del(couponCode);
			}
		}
	}

	/**
	 * 根据手机号查member_code<br/>
	 * 根据member_code查mc_extend_info_homehas<br/>
	 * 回填cust_id、vip_level
	 * @param mobile
	 * @param cust_id
	 */
	private void dealCustId(String mobile, String cust_id, String cust_lvl_cd) {
		MDataMap loginInfo = DbUp.upTable("mc_login_info").one("manage_code","SI2003","login_name",mobile);
		if(loginInfo != null) {
			String member_code = loginInfo.get("member_code");
			List<Map<String, Object>> listByWhere = DbUp.upTable("mc_extend_info_homehas").listByWhere("member_code",member_code);
			
			int changeCount = 0;
			//通过member_code查惠家有的cust_id 如果不存在则插入
			if(listByWhere.size() > 0) {
				for (Map<String, Object> member : listByWhere) {
					//cust_id为空 则根据zid更新cust_id
					if(null == member.get("homehas_code") || "".equals(member.get("homehas_code").toString())) {
						MDataMap mDataMap = new MDataMap();
						mDataMap.put("zid", member.get("zid").toString());
						mDataMap.put("homehas_code", cust_id);
						mDataMap.put("vip_level", cust_lvl_cd);
						DbUp.upTable("mc_extend_info_homehas").dataUpdate(mDataMap, "homehas_code,vip_level", "zid");
						
						changeCount ++;
					}
					
					//客代相同 则只更新客户等级
					if(cust_id.equals(member.get("homehas_code"))) {
						MDataMap mDataMap = new MDataMap();
						mDataMap.put("zid", member.get("zid").toString());
						mDataMap.put("vip_level", cust_lvl_cd);
						DbUp.upTable("mc_extend_info_homehas").dataUpdate(mDataMap, "vip_level", "zid");
						changeCount ++;
					}
				}
			}
			
			if(listByWhere.size() == 0 || changeCount == 0) {
				DbUp.upTable("mc_extend_info_homehas").insert("uid",
						UUID.randomUUID().toString().replace("-", ""), "member_code", member_code,
						"homehas_code", cust_id, "vip_level", cust_lvl_cd);
			}
		}
	}
	
	/**
	 * 获取指定时间与当前时间的差值 
	 * @param endTime
	 * @return 差值 单位：秒
	 */
	private int getSeconds(String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int seconds = 0;
		try {
			Date endDate = sdf.parse(endTime);
			long time1 = new Date().getTime();
			long time2 = endDate.getTime();
			seconds = new Long(((time2 - time1) / 1000)).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return seconds;
	}
}
