package com.cmall.systemcenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：ScFlowStatuschange   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class ScFlowStatuschange  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 流程类型
     */
    private String flowType  = ""  ;
    /**
     * 流程起点
     */
    private String fromStatus  = ""  ;
    /**
     * 流程终点
     */
    private String toStatus  = ""  ;
    /**
     * 角色code
     */
    private String roleId  = ""  ;
    /**
     * 外部接口的实现，需要实现某个接口
     */
    private String changStatusFunc  = ""  ;

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
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
    
    public String getFlowType() {
        return this.flowType;
    }
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    public String getFromStatus() {
        return this.fromStatus;
    }
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }
    
    public String getToStatus() {
        return this.toStatus;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleId() {
        return this.roleId;
    }
    public void setChangStatusFunc(String changStatusFunc) {
        this.changStatusFunc = changStatusFunc;
    }
    
    public String getChangStatusFunc() {
        return this.changStatusFunc;
    }
}

