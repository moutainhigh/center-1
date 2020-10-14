package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 违禁品配置信息同步的响应信息
 * @author cc
 *
 */
public class RsyncResponseSyncContraband extends RsyncResponseBase {
	private List<RsyncModelContraband> result = new ArrayList<RsyncModelContraband>();

	public List<RsyncModelContraband> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelContraband> result) {
		this.result = result;
	}
}
