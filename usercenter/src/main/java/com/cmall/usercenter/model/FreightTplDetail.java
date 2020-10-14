package com.cmall.usercenter.model;

/**
 * 运费模板明细
 * 
 * @author huoqiangshou
 *
 */
public class FreightTplDetail {

	
	/**
	 * 运费模板id
	 */
	private String tplUid;
	
	/**
	 * 快递类型1：快递  2：EMS  4：平邮
	 */
	private String tplTypeId;
	
	/**
	 * 是否可售 1：是 0：否
	 */
	private String isEnable;
	
	/**
	 * 区域
	 */
	private String area; 
	
	/**
	 * 区域码
	 */
	private String areaCode; 
	
	/**
	 *首（件数，重量，体积）
	 */
	private String expressStart; 
	
	/**
	 *  首费
	 */
	private String expressPostage; 
	
	/**
	 * 加重
	 */
	private String expressPlus; 
	
	/**
	 * 加费用
	 */
	private String expressPostageplus;
	
	/**
	 * 排序
	 */
	private int sequence;
	
	public String getTplUid() {
		return tplUid;
	}

	public void setTplUid(String tplUid) {
		this.tplUid = tplUid;
	}
	
	

	public String getTplTypeId() {
		return tplTypeId;
	}

	public void setTplTypeId(String tplTypeId) {
		this.tplTypeId = tplTypeId;
	}
	
	
	
	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getExpressStart() {
		return expressStart;
	}

	public void setExpressStart(String expressStart) {
		this.expressStart = expressStart;
	}

	public String getExpressPostage() {
		return expressPostage;
	}

	public void setExpressPostage(String expressPostage) {
		this.expressPostage = expressPostage;
	}

	public String getExpressPlus() {
		return expressPlus;
	}

	public void setExpressPlus(String expressPlus) {
		this.expressPlus = expressPlus;
	}

	public String getExpressPostageplus() {
		return expressPostageplus;
	}

	public void setExpressPostageplus(String expressPostageplus) {
		this.expressPostageplus = expressPostageplus;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result
				+ ((areaCode == null) ? 0 : areaCode.hashCode());
		result = prime * result
				+ ((expressPlus == null) ? 0 : expressPlus.hashCode());
		result = prime * result
				+ ((expressPostage == null) ? 0 : expressPostage.hashCode());
		result = prime
				* result
				+ ((expressPostageplus == null) ? 0 : expressPostageplus
						.hashCode());
		result = prime * result
				+ ((expressStart == null) ? 0 : expressStart.hashCode());
		result = prime * result
				+ ((isEnable == null) ? 0 : isEnable.hashCode());
		result = prime * result + sequence;
		result = prime * result
				+ ((tplTypeId == null) ? 0 : tplTypeId.hashCode());
		result = prime * result + ((tplUid == null) ? 0 : tplUid.hashCode());
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
		FreightTplDetail other = (FreightTplDetail) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (areaCode == null) {
			if (other.areaCode != null)
				return false;
		} else if (!areaCode.equals(other.areaCode))
			return false;
		if (expressPlus == null) {
			if (other.expressPlus != null)
				return false;
		} else if (!expressPlus.equals(other.expressPlus))
			return false;
		if (expressPostage == null) {
			if (other.expressPostage != null)
				return false;
		} else if (!expressPostage.equals(other.expressPostage))
			return false;
		if (expressPostageplus == null) {
			if (other.expressPostageplus != null)
				return false;
		} else if (!expressPostageplus.equals(other.expressPostageplus))
			return false;
		if (expressStart == null) {
			if (other.expressStart != null)
				return false;
		} else if (!expressStart.equals(other.expressStart))
			return false;
		if (isEnable == null) {
			if (other.isEnable != null)
				return false;
		} else if (!isEnable.equals(other.isEnable))
			return false;
		if (sequence != other.sequence)
			return false;
		if (tplTypeId == null) {
			if (other.tplTypeId != null)
				return false;
		} else if (!tplTypeId.equals(other.tplTypeId))
			return false;
		if (tplUid == null) {
			if (other.tplUid != null)
				return false;
		} else if (!tplUid.equals(other.tplUid))
			return false;
		return true;
	}
	
	
}
