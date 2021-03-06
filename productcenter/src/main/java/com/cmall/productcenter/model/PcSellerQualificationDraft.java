package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.dborm.txmodel.PcQualificationInfo;

/**
 * 商户资质草稿箱
 * @author ligj
 *
 */
public class PcSellerQualificationDraft {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.zid
     *
     * @mbggenerated
     */
    private Integer zid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.uid
     *
     * @mbggenerated
     */
    private String uid = "";

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.seller_qualification_code
     *
     * @mbggenerated
     */
    private String sellerQualificationCode="";

    
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.small_seller_code
     *
     * @mbggenerated
     */
    private String smallSellerCode = "";

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.brand_code
     *
     * @mbggenerated
     */
    private String brandCode = "";

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_seller_qualification.category_code
     *
     * @mbggenerated
     */
    private String categoryCode = "";

    /**
     * 品牌资质信息JSON串 
     */
    private String qualificationJson = "";
    
    /**
     * 资质列表信息
     */
    private List<PcQualificationInfo> qualificationList = new ArrayList<PcQualificationInfo>();
    

    /**
     * 流程状态
     */
    private String flowStatus = "";
    

    /**
     * 流程备注
     */
    private String remark = "";
    
    
    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_seller_qualification.zid
     *
     * @return the value of pc_seller_qualification.zid
     *
     * @mbggenerated
     */
    public Integer getZid() {
        return zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_seller_qualification.zid
     *
     * @param zid the value for pc_seller_qualification.zid
     *
     * @mbggenerated
     */
    public void setZid(Integer zid) {
        this.zid = zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_seller_qualification.uid
     *
     * @return the value of pc_seller_qualification.uid
     *
     * @mbggenerated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_seller_qualification.uid
     *
     * @param uid the value for pc_seller_qualification.uid
     *
     * @mbggenerated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_seller_qualification.small_seller_code
     *
     * @return the value of pc_seller_qualification.small_seller_code
     *
     * @mbggenerated
     */
    public String getSmallSellerCode() {
        return smallSellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_seller_qualification.small_seller_code
     *
     * @param smallSellerCode the value for pc_seller_qualification.small_seller_code
     *
     * @mbggenerated
     */
    public void setSmallSellerCode(String smallSellerCode) {
        this.smallSellerCode = smallSellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_seller_qualification.brand_code
     *
     * @return the value of pc_seller_qualification.brand_code
     *
     * @mbggenerated
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_seller_qualification.brand_code
     *
     * @param brandCode the value for pc_seller_qualification.brand_code
     *
     * @mbggenerated
     */
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_seller_qualification.category_code
     *
     * @return the value of pc_seller_qualification.category_code
     *
     * @mbggenerated
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_seller_qualification.category_code
     *
     * @param categoryCode the value for pc_seller_qualification.category_code
     *
     * @mbggenerated
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

	public List<PcQualificationInfo> getQualificationList() {
		return qualificationList;
	}

	public void setQualificationList(List<PcQualificationInfo> qualificationList) {
		this.qualificationList = qualificationList;
	}

	public String getSellerQualificationCode() {
		return sellerQualificationCode;
	}

	public void setSellerQualificationCode(String sellerQualificationCode) {
		this.sellerQualificationCode = sellerQualificationCode;
	}

	public String getQualificationJson() {
		return qualificationJson;
	}

	public void setQualificationJson(String qualificationJson) {
		this.qualificationJson = qualificationJson;
	}

	public String getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
}