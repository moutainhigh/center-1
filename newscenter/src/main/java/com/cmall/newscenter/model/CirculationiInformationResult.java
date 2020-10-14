package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 *商品流通输出类
 * @author shiyz
 * date 2016-03-20
 *
 */
public class CirculationiInformationResult extends RootResultWeb {

	@ZapcomApi(value="商品流通信息")
	private List<CirculationiInformation> informations = new ArrayList<CirculationiInformation>();

	public List<CirculationiInformation> getInformations() {
		return informations;
	}

	public void setInformations(List<CirculationiInformation> informations) {
		this.informations = informations;
	}

}
