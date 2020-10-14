package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.kjt.RsyncGetKjtProductIdByDate;
import com.cmall.groupcenter.kjt.RsyncGetKlProductsInfo;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;


/** 
* @ClassName: JobGetChangeProductFromKL 
* @Description: 考拉商品信息实时变化入库
*  
*/
public class JobGetChangeProductFromKL extends RootJob {

	public void doExecute(JobExecutionContext context) {
		System.out.println("-----------------考拉定时商品同步方法执行开始-----------------");
		new RsyncGetKlProductsInfo().doRsync();
	}
}
