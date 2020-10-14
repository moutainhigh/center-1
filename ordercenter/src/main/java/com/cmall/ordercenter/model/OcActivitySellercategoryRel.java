package com.cmall.ordercenter.model;


/**   
*    
* 项目名称：ordercenter   
* 类名称：OcActivitySellercategoryRel   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class OcActivitySellercategoryRel  {
    

    /**
     * 活动编号
     */
    private String activityCode  = ""  ;
    /**
     * 商家分类编号
     */
    private String categoryCode  = ""  ;

    
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
    
    public String getActivityCode() {
        return this.activityCode;
    }
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryCode() {
        return this.categoryCode;
    }
}

