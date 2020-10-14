package com.cmall.groupcenter.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.support.ShellRunSupport;
import com.cmall.systemcenter.support.ShellRunSupport.RunResult;
import com.cmall.systemcenter.support.TermChangeSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 词库全量刷新
 */
public class JobForSolrTermRefresh extends RootJob {
	
	static String DIC_FILE_PATH = "terms/ext.dic";
	
	/**
	 * 词库文件同步脚本路径，脚本日志： /var/log/rsync.log
	 */
	static String SH_FILE_PATH = "/opt/shellsrnpr/rsync.sh";

	static Lock lock = new ReentrantLock();
	static Log log = LogFactory.getLog(JobForSolrTermRefresh.class);
	
	static TermChangeSupport termChangeSupport = new TermChangeSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		if(lock.tryLock()) {
			try {
				doWork();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
	
	private void doWork() throws Exception {
		File dicFile = initOutputFile();
		if(dicFile == null) {
			return;
		}

		// 把词库表数据写入到文件
		saveWordTerm(dicFile);
		// 把品牌表数据写入到文件
		saveBrandName(dicFile);
		
		// 调用词库文件同步脚本
		RunResult runResult = new ShellRunSupport().exec(Arrays.asList(SH_FILE_PATH, dicFile.getAbsolutePath()));
		
		// 通知solr更新词库
		if(runResult.getExitValue() == 0) {
			termChangeSupport.reloadTerm();
		} else {
			log.warn("词库文件同步失败：" + runResult.getExitValue());
			noticeMail();
		}
	}
	
	private void saveWordTerm(File dicFile) throws Exception {
		List<String> termlist;
		String lastZid = "0";
		while(true) {
			termlist = new ArrayList<String>(6700);
			lastZid = queryWordTermList(termlist, lastZid);
			FileUtils.writeLines(dicFile, termlist, true);
			
			if(StringUtils.isBlank(lastZid)) {
				break;
			}
		}
	}
	
	private String queryWordTermList(List<String> termlist, String lastZid) {
		List<MDataMap> list = DbUp.upTable("sc_word_term").query("zid,term", "zid", "zid > :lastZid", new MDataMap("lastZid", lastZid), 0, 5000);
		String zid = null;
		for(MDataMap map : list) {
			termlist.add(map.get("term"));
			zid = map.get("zid");
		}
		
		// 条数不足则表示已经查询到最后一页
		if(list.size() < 5000) {
			zid = null;
		}
		return zid;
	}
	
	private void saveBrandName(File dicFile) throws Exception {
		List<MDataMap> mapList = DbUp.upTable("pc_brandinfo").queryAll("DISTINCT brand_name ", "", "flag_enable = 1", new MDataMap());
		List<String> termlist = new ArrayList<String>((int)(mapList.size() / 0.75) + 1);
		for(MDataMap map : mapList) {
			termlist.add(StringUtils.trimToEmpty(map.get("brand_name")));
		}
		
		FileUtils.writeLines(dicFile, termlist, true);
	}
	
	private File initOutputFile() {
		File file = new File(TopConst.CONST_TOP_DIR_SERVLET + "/" + DIC_FILE_PATH);
		
		try {
			// 如果文件已经存在则先进行删除
			if(file.exists()) {
				file.delete();
			}
			
			if(!file.getParentFile().exists()) {
				log.warn("JobForSolrTermRefresh createTermDir: " + file.getParentFile());
				file.getParentFile().mkdirs();
			}
			
			if(!file.createNewFile()) {
				file = null;
				log.warn("JobForSolrTermRefresh createNewFile failed: " + file);
			}
		} catch (IOException e) {
			log.warn("JobForSolrTermRefresh createNewFile failed: " + file);
			e.printStackTrace();
			return null;
		}
		
		return file;
	}
	
	private void noticeMail() {
		String notice = bConfig("zapweb.mail_notice").trim();
		if (StringUtils.isNotBlank(notice)) {
			MailSupport.INSTANCE.sendMail(notice, "词库全量刷新失败", "任务执行类： JobForSolrTermRefresh");
		}
	}

}
