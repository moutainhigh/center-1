
package com.cmall.ordercenter.model;

import com.cmall.productcenter.model.ProductSkuInfo;

public class SkuForCache {
	
	private ActivitySkuEntity ase = null;
	
	private ProductSkuInfo psi = null;

	public ActivitySkuEntity getAse() {
		return ase;
	}

	public void setAse(ActivitySkuEntity ase) {
		this.ase = ase;
	}

	public ProductSkuInfo getPsi() {
		return psi;
	}

	public void setPsi(ProductSkuInfo psi) {
		this.psi = psi;
	}
	
}


