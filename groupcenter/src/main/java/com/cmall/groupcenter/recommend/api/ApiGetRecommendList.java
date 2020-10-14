package com.cmall.groupcenter.recommend.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.recommend.model.ApiGetRecommendListInput;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendListResult;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendListResultModel;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

import freemarker.template.SimpleDate;

public class ApiGetRecommendList extends RootApiForManage<ApiGetRecommendListResult,ApiGetRecommendListInput>{

		@Override
		public ApiGetRecommendListResult Process(
				ApiGetRecommendListInput inputParam, MDataMap mRequestMap) {
			String manageCode = getManageCode();
			ApiGetRecommendListResult result = new ApiGetRecommendListResult();
			List<ApiGetRecommendListResultModel> recommendMobile = new ArrayList<ApiGetRecommendListResultModel>();
			MDataMap mDataMap = new MDataMap();
			String mobile = inputParam.getMobile();
			mDataMap.put("mobile",mobile);
			List<MDataMap> recommendLogList = DbUp.upTable("gc_recommend_info").query("recommended_mobile", "", " app_code='"+manageCode+"' and source=1 and mobile=:mobile GROUP BY recommended_mobile", mDataMap, -1, -1);
		    String recommendMobileStr = "";
			for (MDataMap map : recommendLogList) {
				recommendMobileStr += map.get("recommended_mobile") +",";
			}		
			if(recommendMobileStr.length() > 0) {
				
				recommendMobileStr = recommendMobileStr.substring(0, recommendMobileStr.length() -1);
			      //原sql
	             //		String sSql = "SELECT DISTINCT l.login_name,l.login_pass,o.buyer_code FROM (" +
	            //		"SELECT login_name,login_pass,member_code FROM membercenter.mc_login_info WHERE manage_code = '"+manageCode+"' AND login_name IN( "+recommendMobileStr+")" +
	           //		") l LEFT JOIN ordercenter.oc_orderinfo o " +
	          //		"ON l.member_code = o.buyer_code AND o.order_status = '4497153900010005' ";
				//添加头像字段
				String sSql = "SELECT DISTINCT l.login_name,l.login_pass,l.avatar,l.nickname,l.date_time,o.buyer_code FROM (" +
						"SELECT a.login_name login_name,a.login_pass login_pass,a.member_code member_code,b.avatar avatar,b.nickname nickname,b.date_time date_time"
						+ "  FROM membercenter.mc_login_info a,membercenter.mc_member_sync b WHERE "
						+ " a.login_name = b.login_name and  a.member_code=b.member_code  and   a.manage_code = '"+manageCode+"' AND a.login_name IN( "+recommendMobileStr+")" 
					    + ") l LEFT JOIN    ordercenter.oc_orderinfo o " 
						+ "ON l.member_code = o.buyer_code AND o.order_status = '4497153900010005'  order by date_time asc";
				List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_orderinfo").dataSqlList(sSql, null);
				for (Map<String, Object> map : dataSqlList) {
					ApiGetRecommendListResultModel model = new ApiGetRecommendListResultModel();
					String avatar=String.valueOf(map.get("avatar"));
					String buyer_code = String.valueOf(map.get("buyer_code"));
					String login_pass = String.valueOf(map.get("login_pass"));
					String login_name = String.valueOf(map.get("login_name"));
					String nickName = String.valueOf(map.get("nickname"));
					String dateTime = String.valueOf(map.get("date_time"));
					dateTime=dateTime.substring(0,10);
					model.setNickName(nickName);
					model.setMobile(login_name);
					model.setAvatar(avatar);
					model.setDateTime(dateTime);
					if(null != buyer_code && !"".equals(buyer_code) && !"null".equals(buyer_code)) {
						model.setStatus("BH1002");
						recommendMobile.add(model);
					} else if(null != login_pass && !"".equals(login_pass)) {
						model.setStatus("BH1001");
						recommendMobile.add(model);
					}
					
				}
				
				Map<String, Object> dataSqlOne = DbUp.upTable("mc_login_info").dataSqlOne("select member_code from membercenter.mc_login_info where manage_code='"+manageCode+"' and login_name="+mobile, null);
				int rtnCoupons = 0 ;
				int rtnSum = 0 ;
				
				//查询邀请人获得的金额 （活动日期内，所有配置的活动）
				String activityCode="";
				MDataMap activeMap = DbUp.upTable("oc_coupon_relative").oneWhere("activity_code, manage_code", "", "",
						"relative_type", "7", "manage_code", manageCode);
				if (activeMap != null) {
					activityCode = activeMap.get("activity_code");
				}
				//存在活动修改后的邀请人获得优惠信息错误情况
/*     		    String sql = "SELECT SUM(initial_money) rtnCoupons,COUNT(1) rtnSum, money_type FROM ordercenter.oc_coupon_info a,ordercenter.oc_coupon_type b WHERE a.coupon_type_code=b.coupon_type_code AND"
			    		+ " a.activity_code=:activity_code and a.status=:status and a.member_code=:member_code GROUP BY b.money_type";*/
     		   //查询新表
				 String sql = "SELECT SUM(initial_money) rtnCoupons,COUNT(1) rtnSum, money_type FROM oc_coupon_member a WHERE "
				    		+ "a.member_code=:member_code GROUP BY a.money_type";
				String member_code =dataSqlOne.get("member_code").toString();
				List<Map<String, Object>> couponTypeList = DbUp.upTable("oc_coupon_member").dataSqlList(sql,
						new MDataMap("member_code",member_code));
				if (couponTypeList != null && couponTypeList.size() > 0) {
					for (Map<String, Object> cmap : couponTypeList) {
						if("449748120001".equals(cmap.get("money_type").toString())){   //金额券
							rtnCoupons+=Integer.valueOf(cmap.get("rtnCoupons").toString());
						}
						else if("449748120003".equals(cmap.get("money_type").toString())){   //礼金券
							rtnCoupons+=Integer.valueOf(cmap.get("rtnCoupons").toString());
						}
						else if("449748120002".equals(cmap.get("money_type").toString())){   //折扣券
							rtnSum=Integer.valueOf(cmap.get("rtnSum").toString());
						}
					}
					
				}
		
				result.setRtnSum(rtnSum);
				result.setRtnCoupons(rtnCoupons);;
				
			}
			
			
			if(recommendMobile.size() > 0) {
				result.setBound_status("1");
			} else {
				result.setBound_status("0");
			}
			result.setMobileList(recommendMobile);
			return result;
		}

}
