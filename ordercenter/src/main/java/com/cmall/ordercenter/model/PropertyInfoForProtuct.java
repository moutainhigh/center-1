package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.PropertyValueInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter
* 类名称：PropertyInfo   
* 类描述：   
* 创建人：zhaoxq
* 
*/
public class PropertyInfoForProtuct  {
    
	@ZapcomApi(value = "属性Key编号")
    private String propertyKeyCode  = ""  ;

	@ZapcomApi(value = "属性Key名称")
    private String propertyKeyName  = ""  ;
	
	@ZapcomApi(value = "属性Value信息列表")
	private List<PropertyValueInfo> propertyValueList = new ArrayList<PropertyValueInfo>();
	
	public String getPropertyKeyCode() {
		return propertyKeyCode;
	}
	public void setPropertyKeyCode(String propertyKeyCode) {
		this.propertyKeyCode = propertyKeyCode;
	}
	public String getPropertyKeyName() {
		return propertyKeyName;
	}
	public void setPropertyKeyName(String propertyKeyName) {
		this.propertyKeyName = propertyKeyName;
	}
	public List<PropertyValueInfo> getPropertyValueList() {
		return propertyValueList;
	}
	public void setPropertyValueList(List<PropertyValueInfo> propertyValueList) {
		this.propertyValueList = propertyValueList;
	}

}

