package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 热门排行
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class TopRankingResult extends RootResultWeb {

	@ZapcomApi(value = "排行")
	private List<PopularSearch>  ranks = new ArrayList<PopularSearch>();

	public List<PopularSearch> getRanks() {
		return ranks;
	}

	public void setRanks(List<PopularSearch> ranks) {
		this.ranks = ranks;
	}

}
