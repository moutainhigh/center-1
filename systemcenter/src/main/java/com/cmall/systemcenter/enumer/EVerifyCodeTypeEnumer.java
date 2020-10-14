package com.cmall.systemcenter.enumer;

/**
 * 验证码类型枚举
 * 
 * @author srnpr
 * 
 */
public enum EVerifyCodeTypeEnumer {

	/**
	 * 用户登陆
	 */
	MemberLogin,

	/**
	 * 用户注册
	 */
	MemberReginster,

	/**
	 * 忘记密码
	 */
	ForgetPassword,
	
	/**
	 * 更新用户基本资料
	 */
	UpdateMemInfor,
	
	/**
	 * 微公社绑定用户关系
	 */
	Binding,
	
	/**
	 * 微公社微信绑定
	 */
	WeiXinBind,
	
	/**
	 * 微公社验证码登陆
	 */
	verifyCodeLogin,
	
	/**
	 * 代理商验证
	 */
	agentPassWord,
	
	/**
	 *语音验证码-用户注册 
	 */
	voiceCodeMemberReginster,
	/**
	 * 语音验证码-忘记密码
	 */
	voiceCodeForgetPassword,
	
	/**
	 * 惠币提现获取验证码
	 */
	huiCoinsWithdraw,
	
}
