package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author wangkecheng
 *
 */
public class ApiGetCategoryRateResult  extends RootResult {

	/**
	 * 分成
	 */
	private double cpsrate;

	public double getCpsrate() {
		return cpsrate;
	}

	public void setCpsrate(double cpsrate) {
		this.cpsrate = cpsrate;
	}
	
}
