package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter 
* 类名称：PropertyValueInfo   
* 类描述：   
* 创建人：zhaoxq
* 
*/
public class PropertyValueInfo  {
    
	@ZapcomApi(value = "属性Value编号")
    private String propertyValueCode  = ""  ;

	@ZapcomApi(value = "属性Value名称")
    private String propertyValueName  = ""  ;

	public String getPropertyValueCode() {
		return propertyValueCode;
	}

	public void setPropertyValueCode(String propertyValueCode) {
		this.propertyValueCode = propertyValueCode;
	}

	public String getPropertyValueName() {
		return propertyValueName;
	}

	public void setPropertyValueName(String propertyValueName) {
		this.propertyValueName = propertyValueName;
	}
	
}

