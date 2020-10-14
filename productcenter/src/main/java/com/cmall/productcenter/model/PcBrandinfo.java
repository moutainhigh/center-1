package com.cmall.productcenter.model;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcBrandinfo   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-10 下午12:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 下午12:41:22   
* 修改备注：   
* @version    
*    
*/
public class PcBrandinfo  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 品牌编号
     */
    private String brandCode  = ""  ;
    /**
     * 品牌名称
     */
    private String brandName  = ""  ;
    /**
     * 是否可用
     */
    private Integer flagEnable   = 0 ;
    /**
     * 品牌图片
     */
    private String brandPic  = ""  ;
    /**
     * 品牌描述
     */
    private String brandNote  = ""  ;
    /**
     * 品牌英文名称
     */
    private String brandNameCn  = ""  ;
    public void setZid(Integer zid) {
        this.zid = zid;
    }
    
    public String getBrandNameCn() {
		return brandNameCn;
	}

	public void setBrandNameCn(String brandNameCn) {
		this.brandNameCn = brandNameCn;
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
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }
    
    public String getBrandCode() {
        return this.brandCode;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    
    public String getBrandName() {
        return this.brandName;
    }
    public void setFlagEnable(Integer flagEnable) {
        this.flagEnable = flagEnable;
    }
    
    public Integer getFlagEnable() {
        return this.flagEnable;
    }
    public void setBrandPic(String brandPic) {
        this.brandPic = brandPic;
    }
    
    public String getBrandPic() {
        return this.brandPic;
    }
    public void setBrandNote(String brandNote) {
        this.brandNote = brandNote;
    }
    
    public String getBrandNote() {
        return this.brandNote;
    }
}

