package com.cmall.usercenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：UcShopTemplate   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class UcShopTemplate  {
    
    /**
     * 店铺编号
     */
    private String sellerCode  = ""  ;
    /**
     * 样式类型
     */
    private String templateTypeDid  = ""  ;
    /**
     * 是否可用
     */
    private Integer flagEnable   = 0 ;
    /**
     * 模板顶部
     */
    private String templateHeader  = ""  ;
    /**
     * 模板自动保存
     */
    private String templateAutosave  = ""  ;
    /**
     * 模板名称
     */
    private String templateName  = ""  ;
    /**
     * 模板内容
     */
    private String templateContent  = ""  ;
    /**
     * 
     */
    private String createTime  = ""  ;
    /**
     * 
     */
    private String updateTime  = ""  ;

 
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }
    
    public String getSellerCode() {
        return this.sellerCode;
    }
    public void setTemplateTypeDid(String templateTypeDid) {
        this.templateTypeDid = templateTypeDid;
    }
    
    public String getTemplateTypeDid() {
        return this.templateTypeDid;
    }
    public void setFlagEnable(Integer flagEnable) {
        this.flagEnable = flagEnable;
    }
    
    public Integer getFlagEnable() {
        return this.flagEnable;
    }
    public void setTemplateHeader(String templateHeader) {
        this.templateHeader = templateHeader;
    }
    
    public String getTemplateHeader() {
        return this.templateHeader;
    }
    public void setTemplateAutosave(String templateAutosave) {
        this.templateAutosave = templateAutosave;
    }
    
    public String getTemplateAutosave() {
        return this.templateAutosave;
    }
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }
    
    public String getTemplateContent() {
        return this.templateContent;
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
}

