package com.cmall.systemcenter.model;


/**   
*    
* 项目名称：systemcenter   
* 类名称：ScDefine   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class ScDefine  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 
     */
    private String defineCode  = ""  ;
    /**
     * 
     */
    private String defineName  = ""  ;
    /**
     * 父编号
     */
    private String parentCode  = ""  ;

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
    public void setDefineCode(String defineCode) {
        this.defineCode = defineCode;
    }
    
    public String getDefineCode() {
        return this.defineCode;
    }
    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }
    
    public String getDefineName() {
        return this.defineName;
    }
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    
    public String getParentCode() {
        return this.parentCode;
    }
}

