/**
 * Project Name:ordercenter
 * File Name:ReturnConst.java
 * Package Name:com.cmall.ordercenter.common
 * Date:2013年10月10日下午8:00:29
 *
 */

package com.cmall.systemcenter.util;

/**
 * ClassName:ReturnConst <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2013年10月10日 下午8:00:29 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class SystemCenterConst {
	/**
	 * 待到账
	 */
	public final static String PREPARE_TO_ACCOUNT = "4497154000040001";
	/**
	 * 待充积分
	 */
	public final static String PREPARE_ADD_SCORE = "4497154000040002";
	/**
	 * 已充积分
	 */
	public final static String FINISH_ADD_SCORE = "4497154000040003";

	/**
	 * 验证码最多输入错误次数
	 */
	public final static String MAX_VERIFY_CODE_SUM = "10";

	/**
	 * 验证码最短发送时间间隔
	 */
	public final static String MIN_VERIFY_TIME_STEP = "-30s";
	
	/**
	 *	pc微公社（注册、找回密码）验证码小时的时间间隔
	 */
	public final static String REGISTER_AND_FORGETPASSWORD_MIN_VERIFY_TIME_STEP = "-1h";
	
	/**
	 * 微公社验证码最短发送时间间隔
	 */
	public final static String WGS_MIN_VERIFY_TIME_STEP = "-60s";
	
	/**
	 *	微公社（注册、找回密码、短信登陆间隔）时间间隔
	 */
	public final static String WGS_DAY_VERIFY_TIME_STEP = "-24h";
	
	/**
	 * 惠币提现短信验证码最短发送时间间隔
	 */
	public final static String HUI_COINS_MIN_VERIFY_TIME_STEP = "-70s";
	

}
