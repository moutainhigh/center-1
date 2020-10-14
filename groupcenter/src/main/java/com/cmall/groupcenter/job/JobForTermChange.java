package com.cmall.groupcenter.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.support.TermChangeSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 词库变化增量处理
 */
public class JobForTermChange extends RootJob {

	static Lock lock = new ReentrantLock();
	
	static TermChangeSupport termChangeSupport = new TermChangeSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		if(lock.tryLock()) {
			try {
				doWork();
			} finally {
				lock.unlock();
			}
		}
	}
	
	private void doWork() {
		MDataMap staticMap = DbUp.upTable("za_static").one("static_code", "com.cmall.groupcenter.job.JobForTermChange");
		String lastZid = staticMap.get("static_info");
		
		Map<String,Object> keyMap = new HashMap<String, Object>();
		String term;
		String operType;
		while(true) {
			// 每次取100条
			List<MDataMap> logList = DbUp.upTable("lc_word_term_log").query("", "zid", "zid > :zid", new MDataMap("zid", lastZid), 0 , 100);
			if(logList.isEmpty()) {
				break;
			}
			
			for(MDataMap logMap : logList) {
				term = StringUtils.trimToEmpty(logMap.get("term"));
				operType = logMap.get("oper_type");
				termChangeSupport.updateTerm(term, NumberUtils.toInt(operType));
				
				keyMap.put(term, "");
				lastZid = logMap.get("zid");
			}
			
			staticMap.put("static_info", lastZid);
			DbUp.upTable("za_static").dataUpdate(staticMap, "static_info", "zid");
		}
		
		// 页面有手动刷新索引按钮，此处不再重复刷新
		//termChangeSupport.refreshIndexByTerms(keyMap.keySet());
	}

}
