package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter  
* 类名称：OcOrderShipmentsForCC
* 类描述：   
* 创建人：zhaoxq
* 修改备注：   
* @version    
*    
*/
public class OcOrderShipmentsForCC{

    /**
     * 物流公司
     */
	@ZapcomApi(value="物流公司")
    private String logisticseName = "";
    
    /**
     * 运单号码
     */
	@ZapcomApi(value="运单号码")
    private String waybill = "";
    
    /**
     * 创建人
     */
	@ZapcomApi(value="创建人")
    private String creator = "";
    
    /**
     * 创建时间
     */
	@ZapcomApi(value="创建时间")
    private String createTime = "";
    
    /**
     * 发货说明
     */
	@ZapcomApi(value="发货说明")
    private String remark = "";
    
    public void setLogisticseName(String logisticseName) {
        this.logisticseName = logisticseName;
    }
    
    public String getLogisticseName() {
        return this.logisticseName;
    }
    
    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }
    
    public String getWaybill() {
        return this.waybill;
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
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getRemark() {
        return this.remark;
    }
}

