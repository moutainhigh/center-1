package com.cmall.systemcenter.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 搜索词库相关处理方法
 */
public class TermChangeSupport {
	
	static Log log = LogFactory.getLog(TermChangeSupport.class);
	
	/** 新增词库 */
	public static int TYPE_ADD = 1;
	/** 删除词库 */
	public static int TYPE_DEL = 2;

	/**
	 * 更新词库
	 * @param term     单词
	 * @param operType 操作类： 1 新增、2 删除
	 */
	public void updateTerm(String term, int operType) {
		if(StringUtils.isBlank(term)) return;
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("term", StringUtils.trimToEmpty(term));
		if(operType == 1) {
			mDataMap.put("actName", "newTerm");
		} else if(operType == 2) {
			mDataMap.put("actName", "delTerm");
		} else {
			log.warn("[TermChangeSupport#updateTerm]不支持的操作类型："+operType);
			return;
		}
		
		String[] urls = TopConfig.Instance.bConfig("systemcenter.solr_term_api").split(",");
		String resultText = null;
		for(String url : urls) {
			try {
				resultText = WebClientSupport.upPost(url, mDataMap);
				if(StringUtils.isBlank(resultText) || !resultText.equalsIgnoreCase("ok")) {
					log.warn("接口调用失败：["+resultText+"]"+url);
				}
			} catch (Exception e) {
				log.warn("TermChangeSupport更新词库失败!,接口地址："+url);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 重新加载词库
	 */
	public void reloadTerm() {
		String[] urls = TopConfig.Instance.bConfig("systemcenter.solr_term_api").split(",");
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("actName", "reload");
		String resultText = null;
		for(String url : urls) {
			try {
				resultText = WebClientSupport.upPost(StringUtils.trimToEmpty(url), mDataMap);
				if(StringUtils.isBlank(resultText) || !resultText.equalsIgnoreCase("ok")) {
					log.warn("接口调用失败：["+resultText+"]"+url);
				}
			} catch (Exception e) {
				log.warn("TermChangeSupport重载词库失败!,接口地址："+url);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 刷新给定词的索引
	 */
	public void refreshIndexByTerms(Collection<String> terms) {
		List<String> paramList = new ArrayList<String>();
		for(String v : terms) {
			paramList.add("product_name like '%"+v.replace("'", "\'").replace("%", "\\%").replace("`", "\\`")+"%'");
			if(paramList.size() == 50) {
				List<MDataMap> productlist = DbUp.upTable("pc_productinfo").queryAll("product_code", "", StringUtils.join(paramList," or "), new MDataMap());
				updateSolr(productlist);
				paramList.clear();
			}
		}
		
		if(!paramList.isEmpty()) {
			List<MDataMap> productlist = DbUp.upTable("pc_productinfo").queryAll("product_code", "", StringUtils.join(paramList," or "), new MDataMap());
			updateSolr(productlist);
		}
	}
	
	private void updateSolr(List<MDataMap> productlist) {
		List<String> list = new ArrayList<String>();
		
		for(MDataMap map : productlist) {
			list.add(map.get("product_code"));
		}
		
		if(!list.isEmpty()) {
			new ProductJmsSupport().updateSolrData(StringUtils.join(list,","));
		}
	}
}
