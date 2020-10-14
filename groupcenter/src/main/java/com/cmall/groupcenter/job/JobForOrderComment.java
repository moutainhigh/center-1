package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * @description: 订单收货成功后15天内没有评价则系统给出默认好评
 *
 * @author Yangcl
 * @date 2017年4月12日 下午3:02:47 
 * @version 1.0.0
 */
public class JobForOrderComment extends RootJob {

	public void doExecute(JobExecutionContext context) {
		Date start_ = this.customDate(new Date(), -15);
		Date end_ = this.customDate(new Date(), -14);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");   
		
		String sql = 
				"select "
					+ "info.order_code as orderCode,"
					+ "detail.sku_code as skuCode,"
					+ "detail.product_code as productCode,"
					+ "detail.sku_name as skuName,"
					+ "info.create_time as createTime,"
					+ "info.buyer_code as buyerCode,"
					+ "mi.mobile_phone as mobile "
				+ " from"
					+ " ordercenter.oc_orderinfo info left join ordercenter.oc_orderdetail detail on info.order_code = detail.order_code "
					+ " left join membercenter.mc_extend_info_star mi on info.buyer_code = mi.member_code "
				+ "where "
					+ "info.update_time between '" + sdf.format(start_) + "' and '" + sdf.format(end_) + "' " 
					+ "and info.order_status = '4497153900010005' "
					+ "and info.seller_code = 'SI2003' " ;  
		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(sql, null); 
		if(list.size() == 0){
			return;
		}
		MDataMap mDataMap = new MDataMap();
		for(Map<String, Object> m : list){
			if(StringUtils.isBlank(m.get("orderCode").toString()) ||StringUtils.isBlank((String) m.get("skuCode"))||StringUtils.isBlank((String) m.get("buyerCode")) ){
				continue; // 垃圾数据不做处理
			}
			mDataMap.put("order_code", m.get("orderCode").toString());
			mDataMap.put("order_skuid", m.get("skuCode").toString());
			String where = " order_code=:order_code and order_skuid=:order_skuid";
			int count = DbUp.upTable("nc_order_evaluation").dataCount(where , mDataMap);
			if(count != 0){
				continue; // 已经评论，不做处理
			}else{
				// 添加默认评论 
				mDataMap.put("order_assessment", "默认好评");
				mDataMap.put("oder_creattime", m.get("createTime").toString());
				mDataMap.put("order_name", m.get("buyerCode").toString()); 
				mDataMap.put("manage_code", "SI2003");
				mDataMap.put("order_skuid", m.get("skuCode").toString());
				mDataMap.put("product_code", m.get("productCode").toString());  
				mDataMap.put("flag_show", "449746530001");
				mDataMap.put("check_flag", "4497172100030002");  // 审核状态
				mDataMap.put("label", "是正品,价格不错,比商场便宜,超预期");
				mDataMap.put("grade", "5");
				mDataMap.put("grade_type", "好评");
				mDataMap.put("user_mobile", m.get("mobile").toString());
				mDataMap.put("pic_status", "449747510001");  
				mDataMap.put("sku_name", m.get("skuName").toString());
				DbUp.upTable("nc_order_evaluation").dataInsert(mDataMap);  
				mDataMap.clear();  
			}
		}
		 
	}
	
	private Date customDate(Date date , int flag){
	    Calendar calendar = Calendar.getInstance();       // 日历对象
	    calendar.setTime(date);       // 设置当前日期
	    calendar.add(Calendar.DATE, flag); // 日期减一 或 加一
	    return calendar.getTime();
	}
	
	public static void main(String[] args) {
		JobForOrderComment job = new JobForOrderComment();
		job.doExecute(null);
	}
}























































