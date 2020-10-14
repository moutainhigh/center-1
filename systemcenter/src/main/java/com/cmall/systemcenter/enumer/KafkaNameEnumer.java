package com.cmall.systemcenter.enumer;

/**
 * kafka传递不同的信息分类
 * @author zhouguohui
 *
 */
public enum KafkaNameEnumer {
	
	/**
	 * 商品修改时
	 */
	OnProductChange,
	/**
	 * 商品订单
	 */
	OnProductOrder,
	/**
	 * 同步家有用户等级，积分
	 */
	OnUserChange,
	/**
	 * 下单地址不对错误
	 */
	OnOrderAddress,

}
