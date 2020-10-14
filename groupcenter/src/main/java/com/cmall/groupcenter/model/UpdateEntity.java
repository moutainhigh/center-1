package com.cmall.groupcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**
 * 终端信息
 * @author xiegj
 *
 */
public class UpdateEntity {
	@ZapcomApi(value = "updateKey", remark = "热词：rc,三级联动地区:area,类目:cg,导航图片:dhtp", demo = "")
	private String updateKey = "";
	
	@ZapcomApi(value = "updateValue", remark = "1：更新；0：不更新", demo = "")
	private String updateValue = "";

	public String getUpdateKey() {
		return updateKey;
	}

	public void setUpdateKey(String updateKey) {
		this.updateKey = updateKey;
	}

	public String getUpdateValue() {
		return updateValue;
	}

	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}

}
