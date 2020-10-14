package com.cmall.systemcenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：ScFlowBussinesstype   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class ScFlowBussinesstype  {
    
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
     * 业务表名
     */
    private String tableName  = ""  ;
    /**
     * 业务状态字段名
     */
    private String columnName  = ""  ;
    /**
     * 通用日志
     */
    private String isCommonlog  = ""  ;
    /**
     * 创建人列
     */
    private String creatorColumnname  = ""  ;
    /**
     * 创建时间列
     */
    private String createtimeColumnname  = ""  ;

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
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    public void setIsCommonlog(String isCommonlog) {
        this.isCommonlog = isCommonlog;
    }
    
    public String getIsCommonlog() {
        return this.isCommonlog;
    }
    public void setCreatorColumnname(String creatorColumnname) {
        this.creatorColumnname = creatorColumnname;
    }
    
    public String getCreatorColumnname() {
        return this.creatorColumnname;
    }
    public void setCreatetimeColumnname(String createtimeColumnname) {
        this.createtimeColumnname = createtimeColumnname;
    }
    
    public String getCreatetimeColumnname() {
        return this.createtimeColumnname;
    }
}

