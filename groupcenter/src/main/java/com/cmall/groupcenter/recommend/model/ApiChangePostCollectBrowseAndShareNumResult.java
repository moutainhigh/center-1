package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 修改帖子收藏数量和浏览数量返回结果
 * @author gaozx
 *
 */
public class ApiChangePostCollectBrowseAndShareNumResult extends RootResult{
	@ZapcomApi(value="收藏总数")
	private int changedCollectNum;
	@ZapcomApi(value="浏览总数")
	private int changedBrowseNum;
	@ZapcomApi(value="分享总数")
	private int changedShareNum;
	public int getChangedCollectNum() {
		return changedCollectNum;
	}
	public void setChangedCollectNum(int changedCollectNum) {
		this.changedCollectNum = changedCollectNum;
	}
	public int getChangedBrowseNum() {
		return changedBrowseNum;
	}
	public void setChangedBrowseNum(int changedBrowseNum) {
		this.changedBrowseNum = changedBrowseNum;
	}
	public int getChangedShareNum() {
		return changedShareNum;
	}
	public void setChangedShareNum(int changedShareNum) {
		this.changedShareNum = changedShareNum;
	}
}
