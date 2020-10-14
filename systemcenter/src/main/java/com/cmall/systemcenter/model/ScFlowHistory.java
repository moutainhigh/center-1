package com.cmall.systemcenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：ScFlowHistory   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class ScFlowHistory  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 流程Code
            
     */
    private String flowCode  = ""  ;
    /**
     * 流程类型
     */
    private String flowType  = ""  ;
    /**
     * 创建人
     */
    private String creator  = ""  ;
    /**
     * 创建时间
     */
    private String createTime  = ""  ;
    /**
     * 流程备注
     */
    private String flowRemark  = ""  ;
    /**
     * 当前状态
     */
    private String currentStatus  = ""  ;

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
    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }
    
    public String getFlowCode() {
        return this.flowCode;
    }
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
    
    public String getFlowType() {
        return this.flowType;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getCreator() {
        return this.creator;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    public void setFlowRemark(String flowRemark) {
        this.flowRemark = flowRemark;
    }
    
    public String getFlowRemark() {
        return this.flowRemark;
    }
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public String getCurrentStatus() {
        return this.currentStatus;
    }
}

