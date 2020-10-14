package com.cmall.groupcenter.groupapp.model;

import java.util.Comparator;


/**
 * 商品折扣比较器
 * @author Administrator
 *
 */
public class GoodComparatorDesc implements Comparator<GoodsCricleInfo>{

	@Override
	public int compare(GoodsCricleInfo o1, GoodsCricleInfo o2) {
		/*if(o1.getShareCount()>o2.getShareCount()){
			return o1.getShareCount()-o2.getShareCount();
		}
		if(o1.getShareCount()<o2.getShareCount()){
			return o1.getShareCount()-o2.getShareCount();
		}*/
		System.out.println("aaaa");
	   if(1==1){
			System.out.println("bbbbb");

		   return new Integer(o2.getShareCount()).compareTo(new Integer(o1.getShareCount()));
	   }
	   if(1==1){
			System.out.println("cccc");

		   return o2.getGoodsInfo().getRebateScale().compareTo(o1.getGoodsInfo().getRebateScale());
	   }
		
		//return o1.getDiscountPrice().compareTo(o2.getDiscountPrice());
		return 0;
	}

	

}
