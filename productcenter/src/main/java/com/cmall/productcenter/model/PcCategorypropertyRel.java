package com.cmall.productcenter.model;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcCategorypropertyRel   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-10 下午12:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 下午12:41:22   
* 修改备注：   
* @version    
*    
*/
public class PcCategorypropertyRel  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 分类编号
     */
    private String categoryCode  = ""  ;
    /**
     * 属性编号
     */
    private String propertyCode  = ""  ;

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
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryCode() {
        return this.categoryCode;
    }
    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }
    
    public String getPropertyCode() {
        return this.propertyCode;
    }
}

