package com.cmall.ordercenter.model;

/**
 * 商品到某地的运费
 * @author huoqiangshou
 *
 */
public class PtAreaFreight {

	/**
	 * 地区名称
	 */
	private String areaName;
	
	/**
	 * 地区code
	 */
	private String areaCode;
	
	/**
	 * 费用
	 */
	private String fee;

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((areaCode == null) ? 0 : areaCode.hashCode());
		result = prime * result
				+ ((areaName == null) ? 0 : areaName.hashCode());
		result = prime * result + ((fee == null) ? 0 : fee.hashCode());
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
		PtAreaFreight other = (PtAreaFreight) obj;
		if (areaCode == null) {
			if (other.areaCode != null)
				return false;
		} else if (!areaCode.equals(other.areaCode))
			return false;
		if (areaName == null) {
			if (other.areaName != null)
				return false;
		} else if (!areaName.equals(other.areaName))
			return false;
		if (fee == null) {
			if (other.fee != null)
				return false;
		} else if (!fee.equals(other.fee))
			return false;
		return true;
	}
	
	
	
}
