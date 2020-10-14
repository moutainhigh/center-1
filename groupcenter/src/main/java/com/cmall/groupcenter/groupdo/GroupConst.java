package com.cmall.groupcenter.groupdo;

public class GroupConst {

	/**
	 * 正向清分流程
	 */
	public final static String RECKON_ORDER_EXEC_TYPE_IN = "4497465200050001";

	/**
	 * 逆向清分流程
	 */
	public final static String RECKON_ORDER_EXEC_TYPE_BACK = "4497465200050002";

	/**
	 * 清分最大级别
	 */
	public final static int RECKON_LEVEL_MAX = 2;

	/**
	 * 定义多少天前的清分金额可以自动转到可提现账户
	 */
	public final static String RECKON_AUTO_CONVERT_DAY = "-8d";

	/**
	 * 定义默认级别编号
	 */
	public static String DEFAULT_LEVEL_CODE = "4497465200010002";

	/**
	 * 定义SKU设置的对应级别序号流水
	 */
	public final static String RECKON_LEVEL_LIST = "4497465200010002,4497465200010003,4497465200010004,4497465200010005";

	/**
	 * 定义默认清分比例
	 */
	public static String DEFAULT_SCALE_RECKON = "0.01";

	/**
	 * 最高级别编号
	 */
	public final static String TOP_LEVEL_CODE = "4497465200010005";

	/**
	 * 正向返利 流程
	 */
	public final static String REBATE_ORDER_EXEC_TYPE_IN = "4497465200050003";

	/**
	 * 逆向返利 流程
	 */
	public final static String REBATE_ORDER_EXEC_TYPE_BACK = "4497465200050004";

	/**
	 * 重置返利 流程
	 */
	public final static String REBATE_ORDER_EXEC_TYPE_RESET = "4497465200050005";
	
	/**
	 * 第三方退货流程
	 */
	public final static String THIRD_RECKON_ORDER_EXEC_TYPE_BACK = "4497465200050006";


	/**
	 * 取消退货流程
	 */
	public final static String  GROUP_REKON_CANCELRETURNORDER_TYPE= "4497465200050007";


	/**
	 * 支付
	 */
	public final static String GROUP_PAY = "4497465200200001";
	
	/**
	 * 退款
	 */
	public final static String GROUP_REFUND = "4497465200200002";
	
	/**
	 * 微公社app管理编号
	 */
	public final static String GROUP_APP_MANAGE_CODE = "SI2011";
	
	/**
	 * 客服电话
	 */
	public final static String GROUP_CUSTOM_SERVICE_PHONE="400-608-9911";

	/**
	 *取消退货流程的时候会修改原有的已存在的
	 * uqcode的值，修改规则为在原有的uqcode基础上加上"_" 以及下面的字符
	 * 如，uqcode = 4497465200050006_RETURN150722100001
	 * 则修改为 4497465200050006_RETURN150722100001_c
	 */
	public final static String GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG="c";
}
