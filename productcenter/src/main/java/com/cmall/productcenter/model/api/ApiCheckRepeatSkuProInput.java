package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiCheckRepeatSkuProInput extends RootInput {
	/**
	 * product编码
	 */
	@ZapcomApi(value="product编号",require=0)
	private String productCode="";
	@ZapcomApi(value="颜色属性",require=0)
	private String colorPro="";
	@ZapcomApi(value="样式属性",require=0)
	private String stylePro="";
	@ZapcomApi(value="颜色属性名称",require=0)
	private String colorProName="";
	@ZapcomApi(value="样式属性名称",require=0)
	private String styleProName="";
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getColorPro() {
		return colorPro;
	}

	public void setColorPro(String colorPro) {
		this.colorPro = colorPro;
	}

	public String getStylePro() {
		return stylePro;
	}

	public void setStylePro(String stylePro) {
		this.stylePro = stylePro;
	}

	public String getColorProName() {
		return colorProName;
	}

	public void setColorProName(String colorProName) {
		this.colorProName = colorProName;
	}

	public String getStyleProName() {
		return styleProName;
	}

	public void setStyleProName(String styleProName) {
		this.styleProName = styleProName;
	}
	
}
