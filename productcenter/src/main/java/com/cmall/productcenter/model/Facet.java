package com.cmall.productcenter.model;

/**
 * 分类
 * @author zhouguohui
 */
public class Facet implements Comparable<Facet>{

	private String facetCode;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 分类统计数量
	 */
	private Long count;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}
	/**
	 * @return the facetCode
	 */
	public String getFacetCode() {
		return facetCode;
	}
	/**
	 * @param facetCode the facetCode to set
	 */
	public void setFacetCode(String facetCode) {
		this.facetCode = facetCode;
	}
	
	public int compareTo(Facet o) {
		return this.getFacetCode().compareTo(o.facetCode);
	}
	
}
