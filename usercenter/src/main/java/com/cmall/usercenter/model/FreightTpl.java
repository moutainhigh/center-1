package com.cmall.usercenter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 运费模板model
 * 
 * @author huoqiangshou
 * 
 */
public class FreightTpl {

	/**
	 * 店铺id
	 */
	private String storeId;
	
	/**
	 * 模板名称
	 */
	private String tplName;
	
	/**
	 * 省份
	 */
	private String province;
	
	/**
	 * 城市
	 */
	private String city;
	
	/**
	 * 区
	 */
	private String area;
	
	/**
	 * 发货时间，精确到分钟
	 */
	private String consignmentTime;
	
	/**
	 * 是否包邮
	 */
	private String isFree;
	
	/**
	 * 计价方式
	 */
	private String valuationType;
	/**
	 * 是否默认
	 */
	private String is_default;
	
	/**
	 * 快递
	 */
	private List<FreightTplDetail> express = new ArrayList<FreightTplDetail>();
	
	
	/**
	 * ems
	 */
	private List<FreightTplDetail> ems = new ArrayList<FreightTplDetail>();
	
	/**
	 * 平邮
	 */
	private List<FreightTplDetail> normal = new ArrayList<FreightTplDetail>();
	
	 /**
	 * 是否禁用
	 */
	private String isDisable;
	
	/**
	 * 创建时间
	 */
	private String createDate;
	
	/**
	 * 禁用时间
	 */
	private String disableDate;
	

	
	
	public String getIs_default() {
		return is_default;
	}

	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getTplName() {
		return tplName;
	}

	public void setTplName(String tplName) {
		this.tplName = tplName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getConsignmentTime() {
		return consignmentTime;
	}

	public void setConsignmentTime(String consignmentTime) {
		this.consignmentTime = consignmentTime;
	}

	public String getIsFree() {
		return isFree;
	}

	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}

	public String getValuationType() {
		return valuationType;
	}

	public void setValuationType(String valuationType) {
		this.valuationType = valuationType;
	}

	public List<FreightTplDetail> getExpress() {
		return express;
	}

	public void setExpress(List<FreightTplDetail> express) {
		this.express = express;
	}

	public List<FreightTplDetail> getEms() {
		return ems;
	}

	public void setEms(List<FreightTplDetail> ems) {
		this.ems = ems;
	}

	public List<FreightTplDetail> getNormal() {
		return normal;
	}

	public void setNormal(List<FreightTplDetail> normal) {
		this.normal = normal;
	}

	public String getIsDisable() {
		return isDisable;
	}

	public void setIsDisable(String isDisable) {
		this.isDisable = isDisable;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDisableDate() {
		return disableDate;
	}

	public void setDisableDate(String disableDate) {
		this.disableDate = disableDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result
				+ ((consignmentTime == null) ? 0 : consignmentTime.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((disableDate == null) ? 0 : disableDate.hashCode());
		result = prime * result + ((ems == null) ? 0 : ems.hashCode());
		result = prime * result + ((express == null) ? 0 : express.hashCode());
		result = prime * result
				+ ((isDisable == null) ? 0 : isDisable.hashCode());
		result = prime * result + ((isFree == null) ? 0 : isFree.hashCode());
		result = prime * result + ((normal == null) ? 0 : normal.hashCode());
		result = prime * result
				+ ((province == null) ? 0 : province.hashCode());
		result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
		result = prime * result + ((tplName == null) ? 0 : tplName.hashCode());
		result = prime * result
				+ ((valuationType == null) ? 0 : valuationType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FreightTpl other = (FreightTpl) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (consignmentTime == null) {
			if (other.consignmentTime != null)
				return false;
		} else if (!consignmentTime.equals(other.consignmentTime))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (disableDate == null) {
			if (other.disableDate != null)
				return false;
		} else if (!disableDate.equals(other.disableDate))
			return false;
		if (ems == null) {
			if (other.ems != null)
				return false;
		} else if (!ems.equals(other.ems))
			return false;
		if (express == null) {
			if (other.express != null)
				return false;
		} else if (!express.equals(other.express))
			return false;
		if (isDisable == null) {
			if (other.isDisable != null)
				return false;
		} else if (!isDisable.equals(other.isDisable))
			return false;
		if (isFree == null) {
			if (other.isFree != null)
				return false;
		} else if (!isFree.equals(other.isFree))
			return false;
		if (normal == null) {
			if (other.normal != null)
				return false;
		} else if (!normal.equals(other.normal))
			return false;
		if (province == null) {
			if (other.province != null)
				return false;
		} else if (!province.equals(other.province))
			return false;
		if (storeId == null) {
			if (other.storeId != null)
				return false;
		} else if (!storeId.equals(other.storeId))
			return false;
		if (tplName == null) {
			if (other.tplName != null)
				return false;
		} else if (!tplName.equals(other.tplName))
			return false;
		if (valuationType == null) {
			if (other.valuationType != null)
				return false;
		} else if (!valuationType.equals(other.valuationType))
			return false;
		return true;
	}
	
	
	
}
