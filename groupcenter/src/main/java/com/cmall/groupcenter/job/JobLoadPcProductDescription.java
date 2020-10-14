package com.cmall.groupcenter.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * @descriptions 商品标签定时任务。
 * 	将pc_product_labels表中的数据定时更新到 pc_productdescription表的中
 * 	pc_productdescription表的keyword字段会被更新。
 * 	此需求出自3.9.6版本。
 * 
 * @refactor no
 * @author Yangcl
 * @date 2016-5-12-下午5:07:27
 * @version 1.0.0
 */
public class JobLoadPcProductDescription extends RootJob {

	public void doExecute(JobExecutionContext context) {
//		Map<String,String> labelsCodeMap = new HashMap<String,String>();
//		String cTime = DateUtil.getSysDateTimeString();
//		String where =" flag_enable = 1 and end_time >= '" + cTime + "' and start_time <= '" + cTime +"'";
//		List<MDataMap> list = DbUp.upTable("pc_product_labels").queryAll("", "start_time", where, null);
//		if(list != null && list.size() > 0){
//			for(MDataMap map : list){     
//				// product_codes = "8016410369,8016410366"的形式。按常理来说：有几个code，就应该对应几条记录
//				String productCodes = map.get("product_codes"); 
//				if (StringUtils.isNotBlank(productCodes)) {
//					for (String productCode : productCodes.split(",")) {
//						labelsCodeMap.put( productCode , map.get("label_code"));
//					}
//				}
//			}
//		}else{
//			return ;
//		}
		
//		try{
//			if (null != labelsCodeMap && !labelsCodeMap.isEmpty()) {
//				ProductJmsSupport pjs = new ProductJmsSupport();  // 刷新商品缓存
//				for (String productCode : labelsCodeMap.keySet()) {
//					MDataMap mDataMap = new MDataMap();
//					mDataMap.put("keyword", labelsCodeMap.get(productCode));
//					mDataMap.put("product_code", productCode);
//					DbUp.upTable("pc_productdescription").dataUpdate(mDataMap, "keyword", "product_code");
//					
//					PlusHelperNotice.onChangeProductInfo(productCode);		// 刷新商品缓存
//						//触发消息队列
//					pjs.onChangeForProductChangeAll(productCode);		// 刷新商品缓存
//				}
//			}
//		}catch(Exception e){ // 缓存刷新超时
//			e.printStackTrace();
//		}
			
		
	}

}








































