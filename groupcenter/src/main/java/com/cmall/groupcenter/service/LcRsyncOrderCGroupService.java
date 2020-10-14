package com.cmall.groupcenter.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.FamilyConfig;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


/**
 * 与微公社订单信息同步返利日志记录业务实现
 * @author pangjh
 *
 */
public class LcRsyncOrderCGroupService{
	
	/**
	 * 日志信息保存
	 * @param mDataMap
	 * 		保存的日志数据
	 */
	public void save(MDataMap mDataMap){
		mDataMap.put("create_time", DateUtil.getSysDateTimeString());
		mDataMap.put("update_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("lc_rsync_order_cgroup").dataInsert(mDataMap);
		
	}
	
	/**
	 * 更新日志成功标志
	 * @param status
	 * 		接口是否同步成功
	 */
	public void update(String status,MDataMap mDataMap){
		
		MDataMap logMdataMap = queryByOrderCode(mDataMap.get("order_code"));
		
		
		logMdataMap.put("update_time", DateUtil.getSysDateTimeString());
		
		logMdataMap.put("process_data", mDataMap.get("process_data"));
		
		if(FamilyConfig.RSYNC_SUCCESS.equals(status)){
			/*同步成功：1*/
			logMdataMap.put("success_flag", FamilyConfig.RSYNC_SUCCESS);
			
		}else{
			
			/*同步失败：0*/
			logMdataMap.put("success_flag", FamilyConfig.RSYNC_FAILURE);
			/*失败次数+1操作*/
			logMdataMap = calErrorCount(logMdataMap);
			
			
		}
		
		DbUp.upTable("lc_rsync_order_cgroup").update(logMdataMap);
		
	}
	
	
	public String getSystemTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	
	/**
	 * 更新日志成功标志
	 * @param status
	 * 		接口是否同步成功
	 */
	public void updateOrderStatusLog(MDataMap mDataMap){
		
//		mDataMap.put("create_time", );
		mDataMap.put("create_time", DateUtil.getSysDateTimeString());

		DbUp.upTable("lc_rsync_orderstatus_cgroup").dataInsert(mDataMap);
		
	}
	
	
	/**
	 * 根据日志中记录的错误次数，对错误的次数进行累加
	 * @param logDataMap
	 * 		日志记录数据集合
	 * @return
	 * 		累计后的日志数据
	 */
	public MDataMap calErrorCount(MDataMap logDataMap){
		
		String error_count = logDataMap.get("error_count");
		
		/*将次数进行加1操作*/
		if(StringUtils.isNotBlank(error_count)){
			
			int count = Integer.parseInt(error_count);
			
			count++;
			
			logDataMap.put("error_count", Integer.toString(count));
			
		}
		
		return logDataMap;		
		
		
	}
	
	/**
	 * 根据订单编号查询日志信息
	 * @param order_code
	 * 		订单编号
	 * @return MDataMap
	 * 		日志信息
	 */
	public MDataMap queryByOrderCode(String order_code){
		
		return DbUp.upTable("lc_rsync_order_cgroup").one("order_code",order_code);
		
	}

}
