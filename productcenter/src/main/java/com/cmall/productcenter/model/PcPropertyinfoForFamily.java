package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcPropertyinfoForFamily   
* 类描述：   
* 创建人：xiegj  
* 创建时间：2014-9-19 下午12:41:22   
* 修改人：xiegj
* 修改时间：2014-9-19 下午12:41:22   
* 修改备注：   
* @version    
*    
*/
public class PcPropertyinfoForFamily  {
    
    /**
     * sku编号
     */
	@ZapcomApi(value = "sku编号", remark = "sku编号",require = 1, demo = "8019123456")
    private String sku_code  = ""  ;
	
    /**
     * 属性编号
     */
	@ZapcomApi(value = "属性编号", remark = "属性编号",require = 1, demo = "111")
    private String propertyKey  = ""  ;
    /**
     * 属性名称
     */
	@ZapcomApi(value = "属性名称", remark = "属性名称",require = 1, demo = "222")
    private String propertyValue  = ""  ;
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public String getPropertyKey() {
		return propertyKey;
	}
	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}

