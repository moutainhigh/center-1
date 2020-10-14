package com.cmall.productcenter.common;


/**   
*    
* 项目名称：productcenter   
* 类名称：SkuCommon   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-3 下午2:21:59   
* 修改人：yanzj
* 修改时间：2013-9-3 下午2:21:59   
* 修改备注：   
* @version    
*    
*/
public class SkuCommon {
	
	public final static String ProductHead = "8016";
	public final static String SKUHead = "8019";
	public final static String ProductFlowHead = "PF";
	
	/**
	 * 
	 */
	public final static String FirstSplitStr="$$";
	/**
	 * 
	 */
	public final static String SecondSplitStr="#$#";
	
	/**
	 * 库存变动类型-下单提交
	 */
	public final static String SkuStockChangeTypeOrderCommit="1";
	/**
	 * 库存变动类型-下单回滚
	 */
	public final static String SkuStockChangeTypeOrderRollBack="2";
	
	/**
	 * sku添加
	 */
	public final static String SkuStockChangeTypeCreateProduct="3";
	/**
	 *人工修改库存 
	 */
	public final static String SkuStockChangeTypeChangeProduct="4";
	
	
	
	/**
	 * 成功标志
	 */
	public final static int SuccessFlag=1;
	
	
	public final static String FlowStatusInit = "0";
	public final static String FlowStatusXG = "1";
	
	public final static String ProAddInit = "10";//新增商品未审核状态
	public final static String ProAddOr = "11";//终审通过状态
	public final static String ProAddOrRe = "15";//终审未通过状态
	
	public final static String ProUpaInit = "20";//修改商品未审核状态
	public final static String ProUpaOr = "22";//终审通过状态
	public final static String ProUpaOrRe = "25";//终审未通过状态

}
