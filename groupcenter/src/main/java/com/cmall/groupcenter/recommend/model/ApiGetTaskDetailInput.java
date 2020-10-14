package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 好物推荐详情页
 * @author gaozx
 *
 */
public class ApiGetTaskDetailInput  extends RootInput{
	
	@ZapcomApi(value="任务id",remark="nc_wgs_task tid",require= 1)
	private String tid = "";

	/**
	 * 获取  tid
	 */
	public String getTid() {
		return tid;
	}

	/**
	 * 设置 
	 * @param tid 
	 */
	public void setTid(String tid) {
		this.tid = tid;
	}
	
}
