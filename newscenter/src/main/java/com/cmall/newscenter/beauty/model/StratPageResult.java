package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽—启动页输出类
 * @author yangrong
 * date: 2014-09-10
 * @version1.0
 */
public class StratPageResult extends RootResultWeb {
	
	@ZapcomApi(value = "启动页")
	private List<StratPage> stratPage= new ArrayList<StratPage>();

	public List<StratPage> getStratPage() {
		return stratPage;
	}

	public void setStratPage(List<StratPage> stratPage) {
		this.stratPage = stratPage;
	}
	
}
