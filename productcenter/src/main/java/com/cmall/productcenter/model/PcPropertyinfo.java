package com.cmall.productcenter.model;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcPropertyinfo   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-10 下午12:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 下午12:41:22   
* 修改备注：   
* @version    
*    
*/
public class PcPropertyinfo  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 属性编号
     */
    private String propertyCode  = ""  ;
    /**
     * 属性名称
     */
    private String propertyName  = ""  ;
    /**
     * 父编码
     */
    private String parentCode  = ""  ;
    /**
     * 是否关键属性
     */
    private Integer flagMain   = 0 ;
    /**
     * 是否颜色属性
     */
    private Integer flagColor   = 0 ;

    public void setZid(Integer zid) {
        this.zid = zid;
    }
    
    public Integer getZid() {
        return this.zid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getUid() {
        return this.uid;
    }
    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }
    
    public String getPropertyCode() {
        return this.propertyCode;
    }
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    
    public String getParentCode() {
        return this.parentCode;
    }
    public void setFlagMain(Integer flagMain) {
        this.flagMain = flagMain;
    }
    
    public Integer getFlagMain() {
        return this.flagMain;
    }
    public void setFlagColor(Integer flagColor) {
        this.flagColor = flagColor;
    }
    
    public Integer getFlagColor() {
        return this.flagColor;
    }
}

