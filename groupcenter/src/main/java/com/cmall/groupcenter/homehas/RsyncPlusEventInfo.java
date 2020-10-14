package com.cmall.groupcenter.homehas;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadEventInfoPlusList;
import com.srnpr.xmassystem.plusquery.PlusModelQuery;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 描述: 同步LD的PLUS活动信息 <br>
 */
public class RsyncPlusEventInfo extends RsyncHomeHas<RsyncPlusEventInfo.TConfig, RsyncPlusEventInfo.TRequest, RsyncPlusEventInfo.TResponse> {

	private TRequest tRequest = new TRequest();
	private TResponse tResponse = new TResponse();
	
	public TConfig upConfig() {
		return new TConfig();
	}
	public TRequest upRsyncRequest() {
		return tRequest;
	}
	public TResponse upResponseObject() {
		return tResponse;
	}

	public RsyncResult doProcess(TRequest tRequest, TResponse tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult mWebResult = new RsyncResult();
		if(!tResponse.success){
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("同步失败："+tResponse.msg);
			return mWebResult;
		}
		
		boolean hasUpdate = false;
		for(EventInfo eventInfo : tResponse.getEventList()) {
			// 忽略非plus活动
			if(!"D".equalsIgnoreCase(eventInfo.getEvent_cd())) {
				continue;
			}
			
			// 忽略非打折类型
			if(!"20".equals(eventInfo.getDis_type())) {
				continue;
			}
			
			boolean needPause = false;
			// 是否合并商品数据
			boolean mergeProduct = false;
			
			MDataMap eventMap = new MDataMap();
			eventMap.put("event_name", StringUtils.trimToEmpty(eventInfo.getEvent_nm()));
			eventMap.put("begin_time", StringUtils.trimToEmpty(eventInfo.getFr_date()));
			eventMap.put("end_time", StringUtils.trimToEmpty(eventInfo.getEnd_date()));
			eventMap.put("event_type_code", "4497472600010026");
			eventMap.put("event_status", "4497472700020001");
			eventMap.put("seller_code", "SI2003");
			eventMap.put("event_description", StringUtils.trimToEmpty(eventInfo.getEvent_desc()));
			eventMap.put("uniform_price", "449746880001");
			eventMap.put("order_aging", "24");
			eventMap.put("time_category", "449747280001");
			eventMap.put("department", "LD");
			eventMap.put("provide_person", "LD");
			eventMap.put("out_active_code", StringUtils.trimToEmpty(eventInfo.getEvent_id()));
			eventMap.put("is_supraposition_flag", "1");
			eventMap.put("supraposition_type", "4497472600010001,4497472600010002,4497472600010004,4497472600010005,4497472600010008,4497472600010018,4497472600010019,4497472600010024");
			eventMap.put("channel_limit", "4497471600070001");
			
			MDataMap existEventMap = DbUp.upTable("sc_event_info").one("out_active_code", eventInfo.getEvent_id(),"event_type_code","4497472600010026");
			if(existEventMap == null) {
				// 新增的活动忽略已经失效的
				if("N".equalsIgnoreCase(eventInfo.getVl_yn())) {
					continue;
				}
				
				// 规则跟后台保持一致
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				String  eventCode=WebHelper.upCode("CX");
				eventCode = eventCode.substring(0,2)+df.format(new Date())+eventCode.substring(9, eventCode.length());
				eventMap.put("event_code",eventCode);
				
				DbUp.upTable("sc_event_info").dataInsert(eventMap);
				// 日志
				MDataMap map=new MDataMap();
				map.put("event_code", eventMap.get("event_code"));
				map.put("create_time", FormatHelper.upDateTime());
				map.put("status_code", "4497472700020001");
				map.put("create_user", "system");
				map.put("description", "从LD同步到新活动，活动编号为："+eventMap.get("event_code"));
				DbUp.upTable("sc_event_status_log").dataInsert(map);
			} else {
				eventMap.put("event_code",existEventMap.get("event_code"));
				
				// 修改
				List<String> updateFiledList = new ArrayList<String>();
				// 检查日期是否变更
				if(!existEventMap.get("begin_time").equals(eventMap.get("begin_time"))
						|| !existEventMap.get("end_time").equals(eventMap.get("end_time"))) {
					updateFiledList.add("begin_time");
					updateFiledList.add("end_time");
				}
				
				// 检测描述是否变更
				if(!existEventMap.get("event_description").equals(eventMap.get("event_description"))) {
					updateFiledList.add("event_description");
				}
				
				// 检测名称是否变更
				if(!existEventMap.get("event_name").equals(eventMap.get("event_name"))) {
					updateFiledList.add("event_name");
				}
				
				// 有变更的字段则更新
				if(!updateFiledList.isEmpty()) {
					DbUp.upTable("sc_event_info").dataUpdate(eventMap, StringUtils.join(updateFiledList,","), "event_code");
					hasUpdate = true;
				}
			}
			
			MDataMap limitMap = new MDataMap();
			limitMap.put("event_code", eventMap.get("event_code"));
			limitMap.put("price", eventInfo.getDis_amt());
			limitMap.put("show_name", getShowName(eventInfo.getDis_amt()));
			limitMap.put("show_notes", "可与部分活动叠加使用");
			limitMap.put("product_limit", "4497476400020001");
			limitMap.put("product_codes", "");
			limitMap.put("category_limit", "4497476400020001");
			limitMap.put("category_codes", "");
			limitMap.put("ld_category", StringUtils.trimToEmpty(eventInfo.getClasslimit()));
			limitMap.put("ld_product", StringUtils.trimToEmpty(eventInfo.getGoodlimit())+StringUtils.trimToEmpty(eventInfo.getGoodnojoin()));
			
			// 01表示商品限定
			if("01".equals(eventInfo.getGood_yn())) {
				limitMap.put("product_limit", "4497476400020002");
				limitMap.put("product_codes", StringUtils.trimToEmpty(eventInfo.getGoodlimit()));
			}
			
			// 10表示禁止参与商品限定
			if("10".equals(eventInfo.getGood_yn())) {
				limitMap.put("product_limit", "4497476400020003");
				limitMap.put("product_codes", StringUtils.trimToEmpty(eventInfo.getGoodnojoin()));
			}
			
			// 02表示品类限定
			if("02".equals(eventInfo.getGood_yn())) {
				limitMap.put("category_limit", "4497476400020002");
			}
			
			MDataMap existLimitMap = DbUp.upTable("sc_event_plus").one("event_code",eventMap.get("event_code"));
			if(existLimitMap == null) {
				// 新增
				DbUp.upTable("sc_event_plus").dataInsert(limitMap);
			} else {
				// 修改
				List<String> updateFiledList = new ArrayList<String>();
				// 检查折扣是否变更
				if(!existLimitMap.get("price").equals(limitMap.get("price"))) {
					updateFiledList.add("price");
					updateFiledList.add("show_name");
					needPause = true;
				}
				//if(!existLimitMap.get("ld_product").equals(limitMap.get("ld_product"))) {
				//	updateFiledList.add("ld_product");
				//}
				
				if(!existLimitMap.get("ld_category").equals(limitMap.get("ld_category"))) {
					updateFiledList.add("ld_category");
				}
				
				// 限制条件变更时不更新惠家有的限制条件
				//if(!existLimitMap.get("category_limit").equals(limitMap.get("category_limit"))) {
					//updateFiledList.add("category_limit");
					//updateFiledList.add("category_codes");
					//needPause = true;
				//}
				
				// 只有商品限定条件一致时才合并商品数据
				if(existLimitMap.get("product_limit").equals(limitMap.get("product_limit"))) {
					//updateFiledList.add("product_limit");
					//updateFiledList.add("product_codes");
					//updateFiledList.add("ld_product");
					mergeProduct = true;
				} else {
					// 不一致时清空保存的ld原商品数据
					updateFiledList.add("ld_product");
					limitMap.put("ld_product","");
				}
				
				if(mergeProduct) {
					// 考虑到两边都有可能修改商品列表，此处对商品做一个对比出差异然后合并的操作
					List<String> list = new ArrayList<String>(Arrays.asList(existLimitMap.get("product_codes").split(",")));
					// 对比上次同步时的商品编号，如果有变更则进行部分更新
					Map<String, List<String>> map = different(existLimitMap.get("ld_product"), limitMap.get("ld_product"));
					if(!map.get("add").isEmpty() || !map.get("remove").isEmpty()) {
						list.addAll(map.get("add"));
						list.removeAll(map.get("remove"));
						limitMap.put("product_codes", StringUtils.join(new HashSet<String>(list),","));
						updateFiledList.add("ld_product");
						updateFiledList.add("product_codes");
					}
				}
				
				// 有变更的字段则更新
				if(!updateFiledList.isEmpty()) {
					DbUp.upTable("sc_event_plus").dataUpdate(limitMap, StringUtils.join(updateFiledList,","), "event_code");
					hasUpdate = true;
				}
			}
			
			// 活动有效且需要暂停活动的情况
			if(needPause && "Y".equalsIgnoreCase(eventInfo.getVl_yn()) && "4497472700020002".equals(existLimitMap.get("event_status"))) {
				eventMap.put("event_status", "4497472700020004");
				DbUp.upTable("sc_event_info").dataUpdate(eventMap, "event_status", "event_code");
				
				// 日志
				MDataMap map=new MDataMap();
				map.put("event_code", eventMap.get("event_code"));
				map.put("create_time", FormatHelper.upDateTime());
				map.put("status_code", "4497472700020003");
				map.put("create_user", "system");
				map.put("description", "LD活动变更，活动自动暂停!");
				DbUp.upTable("sc_event_status_log").dataInsert(map);
				
				PlusHelperNotice.onChangeEvent(eventMap.get("event_code"));
			}
			
			// 活动作废
			if("N".equalsIgnoreCase(eventInfo.getVl_yn()) && !"4497472700020003".equals(existLimitMap.get("event_status"))) {
				eventMap.put("event_status", "4497472700020003");
				DbUp.upTable("sc_event_info").dataUpdate(eventMap, "event_status", "event_code");
				
				// 日志
				MDataMap map=new MDataMap();
				map.put("event_code", eventMap.get("event_code"));
				map.put("create_time", FormatHelper.upDateTime());
				map.put("status_code", "4497472700020003");
				map.put("create_user", "system");
				map.put("description", "LD活动失效，活动自动作废!");
				DbUp.upTable("sc_event_status_log").dataInsert(map);
				
				PlusHelperNotice.onChangeEvent(eventMap.get("event_code"));
			}
		}
		
		if(hasUpdate) {
			new LoadEventInfoPlusList().refresh(new PlusModelQuery("SI2003"));
		}
		
		return mWebResult;
	}
	
	private String getShowName(String dis_amt) {
		DecimalFormat df = new DecimalFormat("#"); 
		String v = df.format(Double.valueOf(dis_amt)*100);
		v = StringUtils.chomp(v, "0"); //截掉末尾0
		return "橙意卡"+v+"折";
	}
	
	/**
	 * 用新的商品列表对比老的商品列表，返回需要新增或者删除的项
	 * @param codes1
	 * @param codes2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,List<String>> different(String codes1, String codes2) {
		codes1 = StringUtils.trimToEmpty(codes1);
		codes2 = StringUtils.trimToEmpty(codes2);
		
		Map<String,List<String>> resultMap = new HashMap<String, List<String>>();
		List<String> codes1List = Arrays.asList(codes1.split(","));
		List<String> codes2List = Arrays.asList(codes2.split(","));
		
		List<String> addList = new ArrayList<String>();
		List<String> removeList = new ArrayList<String>();
		
		// 两个集合差异项列表
		Collection<String> diffList = CollectionUtils.disjunction(codes1List, codes2List);
		for(String v : diffList) {
			if(StringUtils.isBlank(v)){
				continue;
			}
			
			// 新的集合包含则表示原集合需要新增
			if(codes2List.contains(v)) {
				addList.add(v);
			} else {
				// 否则就是原集合需要删除
				removeList.add(v);
			}
		}
		
		resultMap.put("add", addList);
		resultMap.put("remove", removeList);
		
		return resultMap;
	}
	
	public static class TConfig extends RsyncConfigRsyncBase{
		@Override
		public String getRsyncTarget() {
			return "getPlusEventInfo";
		}
	}
	
	public static class TResponse implements IRsyncResponse{
		private boolean success;
		private String msg;
		private List<EventInfo> eventList = new ArrayList<EventInfo>();
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public List<EventInfo> getEventList() {
			return eventList;
		}
		public void setEventList(List<EventInfo> eventList) {
			this.eventList = eventList;
		}
	}
	
	public static class EventInfo {
		// 活动编号
		private String event_id;
		// 开始日期
		private String fr_date;
		// 结束日期
		private String end_date;
		// 促销类别
		private String event_cd;
		// 促销名
		private String event_nm;
		// 促销活动说明
		private String event_desc;
		// 有效与否
		private String vl_yn;
		// 商品限定模式:  01表示商品限定/02表示品类限定/03表示禁止参与商品限定
		private String good_yn;
		// 活动参与方式(10 商品  20 订单)
		private String attend_mode;
		// 活动承担部门
		private String dept_event;
		// 活动折扣类型:  10立减 20打折
		private String dis_type;
		// 活动满减金额或打折折扣
		private String dis_amt;
		// 限定的商品集合
		private String goodlimit;
		// 限定的品类集合
		private String classlimit;
		// 禁止的商品集合
		private String goodnojoin;
		public String getEvent_id() {
			return event_id;
		}
		public void setEvent_id(String event_id) {
			this.event_id = event_id;
		}
		public String getFr_date() {
			return fr_date;
		}
		public void setFr_date(String fr_date) {
			this.fr_date = fr_date;
		}
		public String getEnd_date() {
			return end_date;
		}
		public void setEnd_date(String end_date) {
			this.end_date = end_date;
		}
		public String getEvent_cd() {
			return event_cd;
		}
		public void setEvent_cd(String event_cd) {
			this.event_cd = event_cd;
		}
		public String getEvent_nm() {
			return event_nm;
		}
		public void setEvent_nm(String event_nm) {
			this.event_nm = event_nm;
		}
		public String getEvent_desc() {
			return event_desc;
		}
		public void setEvent_desc(String event_desc) {
			this.event_desc = event_desc;
		}
		public String getVl_yn() {
			return vl_yn;
		}
		public void setVl_yn(String vl_yn) {
			this.vl_yn = vl_yn;
		}
		public String getGood_yn() {
			return good_yn;
		}
		public void setGood_yn(String good_yn) {
			this.good_yn = good_yn;
		}
		public String getAttend_mode() {
			return attend_mode;
		}
		public void setAttend_mode(String attend_mode) {
			this.attend_mode = attend_mode;
		}
		public String getDept_event() {
			return dept_event;
		}
		public void setDept_event(String dept_event) {
			this.dept_event = dept_event;
		}
		public String getGoodlimit() {
			return goodlimit;
		}
		public void setGoodlimit(String goodlimit) {
			this.goodlimit = goodlimit;
		}
		public String getClasslimit() {
			return classlimit;
		}
		public void setClasslimit(String classlimit) {
			this.classlimit = classlimit;
		}
		public String getGoodnojoin() {
			return goodnojoin;
		}
		public void setGoodnojoin(String goodnojoin) {
			this.goodnojoin = goodnojoin;
		}
		public String getDis_type() {
			return dis_type;
		}
		public void setDis_type(String dis_type) {
			this.dis_type = dis_type;
		}
		public String getDis_amt() {
			return dis_amt;
		}
		public void setDis_amt(String dis_amt) {
			this.dis_amt = dis_amt;
		}
		
	}
	
	public static class TRequest implements IRsyncRequest{
	}
}
