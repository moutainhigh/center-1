package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 修改好物推荐帖子的收藏数或浏览数
 * @author gaozx
 *
 */
public class ApiChangePostCollectBrowseAndShareNumInput  extends RootInput{
	
	@ZapcomApi(value="好物推荐帖子id",remark="nc_post pid",require= 1)
	private String pid = "";
	@ZapcomApi(value="收藏变化量",remark="1:加1；-2：减2",require= 0)
	private String changeCollectNum;
	@ZapcomApi(value="浏览变化量",remark="1:加1；-2：减2",require= 0)
	private String changeBrowseNum;
	@ZapcomApi(value="分享变化量",remark="1:加1；-2：减2",require= 0)
	private String changeShareNum;
	

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getChangeCollectNum() {
		return changeCollectNum;
	}

	public void setChangeCollectNum(String changeCollectNum) {
		this.changeCollectNum = changeCollectNum;
	}

	public String getChangeBrowseNum() {
		return changeBrowseNum;
	}

	public void setChangeBrowseNum(String changeBrowseNum) {
		this.changeBrowseNum = changeBrowseNum;
	}

	public String getChangeShareNum() {
		return changeShareNum;
	}

	public void setChangeShareNum(String changeShareNum) {
		this.changeShareNum = changeShareNum;
	}
}
