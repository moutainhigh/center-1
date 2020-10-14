package com.cmall.systemcenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：ScFlowMain   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class ScFlowMain  {
    
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
     * 更新人
     */
    private String updator  = ""  ;
    /**
     * 创建时间
     */
    private String createTime  = ""  ;
    /**
     * 更新时间
     */
    private String updateTime  = ""  ;
    /**
     * 外部单据号
     */
    private String outerCode  = ""  ;
    /**
     * 外部标题
     */
    private String flowTitle  = ""  ;
    /**
     * 描述的Url
     */
    private String flowUrl  = ""  ;
    /**
     * 流程备注
     */
    private String flowRemark  = ""  ;
    /**
     * 是否结束
     */
    private Integer flowIsend   = 0 ;
    /**
     * 当前状态
     */
    private String currentStatus  = ""  ;
    /**
     * 上一状态
     */
    private String lastStatus  = ""  ;
    /**
     * 下一级审批人列表
     */
    private String nextOperators  = ""  ;
    /**
     * 下一级审批人可以处理到节点
     */
    private String nextOperatorStatus  = ""  ;
    
    /*下一级审批人*/
    private String next_operator_id = "";

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
    public void setUpdator(String updator) {
        this.updator = updator;
    }
    
    public String getUpdator() {
        return this.updator;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getUpdateTime() {
        return this.updateTime;
    }
  
    public void setFlowTitle(String flowTitle) {
        this.flowTitle = flowTitle;
    }
    
    public String getFlowTitle() {
        return this.flowTitle;
    }
    public void setFlowUrl(String flowUrl) {
        this.flowUrl = flowUrl;
    }
    
    public String getFlowUrl() {
        return this.flowUrl;
    }
    public void setFlowRemark(String flowRemark) {
        this.flowRemark = flowRemark;
    }
    
    public String getFlowRemark() {
        return this.flowRemark;
    }
    public void setFlowIsend(Integer flowIsend) {
        this.flowIsend = flowIsend;
    }
    
    public Integer getFlowIsend() {
        return this.flowIsend;
    }
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public String getCurrentStatus() {
        return this.currentStatus;
    }
    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }
    
    public String getLastStatus() {
        return this.lastStatus;
    }
    public void setNextOperators(String nextOperators) {
        this.nextOperators = nextOperators;
    }
    
    public String getNextOperators() {
        return this.nextOperators;
    }
    public void setNextOperatorStatus(String nextOperatorStatus) {
        this.nextOperatorStatus = nextOperatorStatus;
    }
    
    public String getNextOperatorStatus() {
        return this.nextOperatorStatus;
    }

	public String getOuterCode() {
		return outerCode;
	}

	public void setOuterCode(String outerCode) {
		this.outerCode = outerCode;
	}

	public String getNext_operator_id() {
		return next_operator_id;
	}

	public void setNext_operator_id(String next_operator_id) {
		this.next_operator_id = next_operator_id;
	}
}

