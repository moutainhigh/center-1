package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * @author hxd
 *
 */
public class BoutiqueResultNew  extends RootResult{
	/**
	 * 精品汇信息
	 */
	List<Boutique> list = new ArrayList<Boutique>();
	/**
	 * 精品汇对应商品信息
	 */
	List<PcProductinfo> list2 = new ArrayList<PcProductinfo>();
	/**
	 * 分类信息
	 */
	List<PcCategoryinfo> list3 = new ArrayList<PcCategoryinfo>();
	public List<Boutique> getList() {
		return list;
	}
	public void setList(List<Boutique> list) {
		this.list = list;
	}
	public List<PcProductinfo> getList2() {
		return list2;
	}
	public void setList2(List<PcProductinfo> list2) {
		this.list2 = list2;
	}
	public List<PcCategoryinfo> getList3() {
		return list3;
	}
	public void setList3(List<PcCategoryinfo> list3) {
		this.list3 = list3;
	}
	
}
