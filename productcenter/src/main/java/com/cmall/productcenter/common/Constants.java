package com.cmall.productcenter.common;

/**
 * 产品常量类
 * @author pang_jhui
 *
 */
public class Constants {
	
	/**MD(招商经理)角色code参数标识*/
	public static final String ROLE_MD_PARAM_ID = "449747660001";
	
	/**流程审批按钮*/
	public static final String ZW_DEFINE_FLOW_BUTTON = "46992321";
	
	/**流程类型：商品价格审批*/
	public static final String FLOW_TYPE_SKUPRICE_APPROVE = "449717230013";
	
	/**流程类型：商品价格审批*/
	public static final String FLOW_TYPE_SKUSTOCK_APPROVE = "449717230012";
	
	/**流程状态：待财务审批(改为带运营审批)*/
	public static final String FLOW_STATUS_SKUPRICE_CW = "4497172300130001";
	
	/**流程状态：价格审核完成*/
	public static final String FLOW_STATUS_SKUPRICE_FINISH = "4497172300130002";
	
	/**流程状态：价格审核驳回(财务)*/
	public static final String FLOW_STATUS_SKUPRICE_REJECT = "4497172300130003";
	
	/**流程状态：库存待审核*/
	public static final String FLOW_STATUS_SKUSTOCK_MD = "4497172300120001";
	
	/**流程状态：库存审核完成*/
	public static final String FLOW_STATUS_SKUSTOCK_FINISH = "4497172300120002";
	
	/**流程状态：库存审核驳回*/
	public static final String FLOW_STATUS_SKUSTOCK_REJECT = "4497172300120003";
	
	/**库存审核：待审核*/
	public static String SKU_STOCK_CHANGE_STATUS_WAIT = "4497471600230001";
	
	/**库存审核：审核完成*/
	public static String SKU_STOCK_CHANGE_STATUS_FINISH = "4497471600230003";
	
	/**库存审核：审核拒绝*/
	public static String SKU_STOCK_CHANGE_STATUS_REJECT = "4497471600230002";
	
	/**库存增加*/
	public static String STOCK_OPERATE_TYPE_ADD = "1"; 
	
	/**库存减少*/
	public static String STOCK_OPERATE_TYPE_DECREASE = "2"; 
	
	/**招商经理驳回*/
	public static String FLOW_STATUS_PCAPPROVE_MD_REJECT = "4497153900060007";
	
	/**编辑负责人驳回*/
	public static String FLOW_STATUS_PCAPPROVE_BJF_REJECT = "4497153900060009";
	
	/**待招商经理审核*/
	public static String FLOW_STATUS_PCAPPROVE_MD = "4497153900060005";
	
	/**价格审批流程状态：待运营审批*/
	public static final String FLOW_STATUS_SKUPRICE_YY = "4497172300130004";
	/**价格审批流程状态：待运营审批(驳回)*/
	public static final String FLOW_STATUS_SKUPRICE_YY_5 = "4497172300130005";

}
