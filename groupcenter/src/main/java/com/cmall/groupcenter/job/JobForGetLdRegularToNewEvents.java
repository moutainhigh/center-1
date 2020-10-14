package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncRegularToNewEvents;
import com.cmall.groupcenter.homehas.model.RsyncModelRegularToNew;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncRegularToNew;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定期转换账户
 * 
 * @author srnpr
 *
 */
public class JobForGetLdRegularToNewEvents extends RootJob {

	public void doExecute(JobExecutionContext context) {
		RsyncRegularToNewEvents r = new RsyncRegularToNewEvents();
		r.doRsync();
		RsyncResponseSyncRegularToNew result = r.getResponseObject();
		List<RsyncModelRegularToNew> events = result.getEventList();
		String sql = "SELECT * FROM systemcenter.sc_event_info WHERE event_type_code = '4497472600010028' limit 1";
		Map<String, Object> eventInfo = DbUp.upTable("sc_event_info").dataSqlOne(sql, null);
		MDataMap insert = new MDataMap();
		String eventCode = "";
		if (eventInfo == null || eventInfo.isEmpty()) {// 活动不存在
			eventCode = WebHelper.upCode("CX");
			insert.put("uid", UUID.randomUUID().toString().replace("-", ""));
			insert.put("event_code", eventCode);
			insert.put("event_name", "");
			insert.put("event_type_code", "4497472600010028");
			insert.put("event_status", "4497472700020001");
			DbUp.upTable("sc_event_info").dataInsert(insert);//新增老退信活动
		} else {
			eventCode = eventInfo.get("event_code").toString();
		}
		MDataMap eventInfoDetail = DbUp.upTable("sc_event_regular_to_new").one("event_code", eventCode);
		MDataMap eventInfoDetailLd = new MDataMap();
		if (eventInfoDetail == null || eventInfoDetail.isEmpty()) {
			eventInfoDetailLd.put("uid", UUID.randomUUID().toString().replace("-", ""));
			eventInfoDetailLd.put("event_code", eventCode);
		} else {
			eventInfoDetailLd.put("uid", eventInfoDetail.get("uid"));
			eventInfoDetailLd.put("event_code", eventCode);
		}
		MDataMap eventInfommap = new MDataMap(eventInfo);
		String status = eventInfommap.get("event_status");
		if(events == null || events.size() == 0) {
			if("4497472700020002".equals(status)) {//已发布，需要暂停
				eventInfommap.put("event_status", "4497472700020004");//暂停
				DbUp.upTable("sc_event_info").dataUpdate(eventInfommap, "event_status", "uid");//变更活动为暂停状态
			}
			return;
		}
		for (RsyncModelRegularToNew event : events) {
			if ("old".equals(event.getCUST_TYPE())) {// 老用户活动编号
				eventInfoDetailLd.put("rewards_type_regular", this.getType(event.getSLE_TYPE()));
				eventInfoDetailLd.put("rewards_ld_code_regular", event.getEVENT_ID());
			} else {// 新用户活动编号
				String type = this.getLimitType(event.getGOOD_YN());
				eventInfoDetailLd.put("rewards_type_new", this.getType(event.getSLE_TYPE()));
				eventInfoDetailLd.put("rewards_ld_code_new", event.getEVENT_ID());
				if(type.equals("4497476400020002")) {
					eventInfoDetailLd.put("product_codes", event.getGOODLIMIT()!=null?event.getGOODLIMIT():"");
				}else if(type.equals("4497476400020003")) {
					eventInfoDetailLd.put("product_codes", event.getGOODNOJOIN()!=null?event.getGOODNOJOIN():"");
				}else {
					eventInfoDetailLd.put("product_codes", "");
				}
				eventInfoDetailLd.put("event_desc", event.getEVENT_DESC()!=null?event.getEVENT_DESC():"");
				eventInfoDetailLd.put("create_time", event.getETR_DATE()!=null?event.getETR_DATE():"");
				eventInfoDetailLd.put("product_limit_type", type);
			}
		}
		if (eventInfoDetail == null) {// 首次初始化
			DbUp.upTable("sc_event_regular_to_new").dataInsert(eventInfoDetailLd);
		} else if (!eventInfoDetail.get("rewards_ld_code_regular")
				.equals(eventInfoDetailLd.get("rewards_ld_code_regular"))
				|| !eventInfoDetail.get("rewards_ld_code_new").equals(eventInfoDetailLd.get("rewards_ld_code_new"))
				|| !eventInfoDetail.get("rewards_type_new").equals(eventInfoDetailLd.get("rewards_type_new"))
				|| !eventInfoDetail.get("rewards_ld_code_regular")
						.equals(eventInfoDetailLd.get("rewards_ld_code_regular"))) {
			//活动编号变更，活动商品变更，活动类型变更，均需更新活动详情信息。
			DbUp.upTable("sc_event_regular_to_new").dataUpdate(eventInfoDetailLd, "rewards_type_regular,rewards_type_new,rewards_ld_code_regular,rewards_ld_code_new,event_desc,create_time", "uid");
			//暂停活动
			if(!eventInfoDetail.get("rewards_ld_code_new").equals(eventInfoDetailLd.get("rewards_ld_code_new"))) {//新用户活动编号变更的话，需要初始化商品限定类型以及商品。
				DbUp.upTable("sc_event_regular_to_new").dataUpdate(eventInfoDetailLd, "product_codes,product_limit_type", "uid");
			}
			if("4497472700020002".equals(status)) {//已发布，需要暂停
				eventInfommap.put("event_status", "4497472700020004");//暂停
				DbUp.upTable("sc_event_info").dataUpdate(eventInfommap, "event_status", "uid");//变更活动为暂停状态
			}
		}
	}

	/**
	 * 商品限定类型
	 * @param good_YN 01 标识商品限定，02：标识品类限定，03：表示禁止参与商品限定
	 * @return
	 */
	private String getLimitType(String good_YN) {
		if ("01".equals(good_YN)) {
			return "4497476400020002";//仅包含
		} else if ("02".equals(good_YN)) {
			return "4497476400020002";//仅包含
		} else if ("10".equals(good_YN)) {//除外
			return "4497476400020003";
		}
		return "4497476400020001";
	}

	/**
	 * 
	 * @param sle_TYPE
	 * @return 转换type
	 */
	private String getType(String sle_TYPE) {
		if ("积分".equals(sle_TYPE)) {
			return "4497473400040002";
		} else if ("礼金".equals(sle_TYPE)) {
			return "4497473400040003";
		} else if ("立减".equals(sle_TYPE)) {
			return "4497473400040001";
		}
		return "";
	}

}
