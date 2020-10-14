package com.cmall.groupcenter.weixin;



/**
 * 
 * 常量类
 * @author lipengfei
 * @date 2015-5-21
 * email:lipf@ichsy.com
 *
 */
public class WebchatConstants { 
	
	/**
	 * 微公社管理编码
	 */
	public static final String CGROUP_MANAGE_CODE = "SI2011";
	
	
	/**
	 * 替换？的字符
	 */
	public static final String PARAMS_WHY_FLAG[]={"?","1M1"};
	

	/**
	 * 替换url中&的字符
	 */
	public static final String PARAMS_AND_FLAG[]={"&","1M2"};
	
	/**
	 * 替换url中=的字符
	 */
	public static final String PARAMS_EQ_FLAG[]={"=","1M3"};
	
	
	
	
	
	/**
	 * 微信信息模板---------START----------
	 */
	
	//通用
	public static final String MESSAGE_COMMON_RESPONSE_WORDS="欢迎使用{0}查询服务，请点击以下链接登录微公社网页版，自动进入\"{0}\"页面即可进行查询："
			+ "\n\n          <a href='{0}'>点击这里，立即查询</a>";

	//通用
	public static final String MESSAGE_COMMON_RESPONSE_BINDING_WORDS="欢迎使用{0}查询服务，请点击以下链接，自动进入\"{0}\"页面即可进行查询："
			+ "\n\n          <a href='{0}'>点击这里，立即查询</a>";
	
	
	
	public static final String MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON="如要查询可提现余额" +
			"，请您回复：注册手机号，密码，例如:" +
			"15001111111,123456。";
	//可提余额的未绑定时的提示信息
	public static final String MESSAGE_QUERY_ACCOUNT_BALANCE_NOTICE="尊敬的用户,"+MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON;
	
	//可提余额输入格式错误的提示信息
	public static final String MESSAGE_QUERY_ACCOUNT_BALANCE_RULE_ERROR="呃... 不大明白您的意思。"+MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON;
	
	//查询余额时输入的密码格式有误
	public static final String MESSAGE_QUERY_ACCOUNT_BALANCE_FORMAT_ERROR="密码有误，无法提供查询结果。"+MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON;
	
	//查询余额时输入的手机号有误
	public static final String MESSAGE_QUERY_ACCOUNT_BALANCE_MOBILE_ERROR="手机号有误，无法提供查询结果。"+MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON;
	
	
	//未注册
	public static final String MESSAGE_NOT_REGESTERED_NOTICE="手机号{0}尚未注册微公社，您可以点击以下链接登录微公社" +
			"网页版，自动进入\"注册\"页面即可进行注册："
			+ "\n\n     <a href='{0}'>点击这里，立即注册</a>";
	

			
	//未注册
	public static final String MESSAGE_PASSWORD_ERROR= "密码有误，无法提供查询结果。"+MESSAGE_QUERY_ACCOUNT_BALANCE_COMMON;
			

	//立即提现
	public static final String MESSAGE_WITHDRAW_NOW="欢迎使用立即提现服务，请点击以下链接登录微公社网页版，自动进入\"立即提现\"页面即可提现："
			+ "\n\n          <a href='{0}'>点击这里，立即提现</a>";
			;
			
			
			
	//未注册
	public static final String MESSAGE_SYSTEM_ERROR="出错拉，请稍后再试或联系管理员";
	/**
	 * 微信信息模板---------END----------
	 */
			
			
			
	/**
	* 链接-------START------------
	*/		
			
			
	
	public static final String PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL="web/grouppageSecond/wx_transit?web_api_openid={0}&returnUrl={0}";
	
    //注册页面
	public static final String PAGE_WEI_COMMUNITY_REGESTER_URL="web/grouppageSecond/register.html?web_api_key=betagroup";

	
	//账户明细
	public static final String PAGE_WEI_COMMUNITY_ACCOUNT_DETAIL_URL="account_detail.html";
	public static final String PAGE_WEI_COMMUNITY_ACCOUNT_DETAIL_NOTBINDING_URL=PAGE_WEI_COMMUNITY_ACCOUNT_DETAIL_URL;

	//消费明细
	public static final String PAGE_WEI_COMMUNITY_COST_DETAIL_URL="propertylist.html";
	public static final String PAGE_WEI_COMMUNITY_COST_DETAIL_NOTBINDING_URL=PAGE_WEI_COMMUNITY_COST_DETAIL_URL;

	//返利明细
	public static final String PAGE_WEI_COMMUNITY_RECEIVED_GAINS_URL="returnearnings.html";
	public static final String PAGE_WEI_COMMUNITY_RECEIVED_GAINS_NOTBINDING_URL=PAGE_WEI_COMMUNITY_RECEIVED_GAINS_URL;

	//提现记录
	public static final String PAGE_WEI_COMMUNITY_WITHDRAW_LIST_URL="account_detail.html?accountType=record_withdraw";

	public static final String PAGE_WEI_COMMUNITY_WITHDRAW_LIST_NOTBINDING_URL=PAGE_WEI_COMMUNITY_WITHDRAW_LIST_URL;

	//立即提现
	public static final String PAGE_WEI_COMMUNITY_WITHDRAW_NOW_URL="web/grouppageSecond/login.html?P=withdraw.html";
	
	//邀请好友
	public static final String PAGE_WEI_COMMUNITY_ADD_FRIEND_URL="addfriend.html";
	
	//精选特惠详情
	public static final String PAGE_WEI_COMMUNITY_RECOMMENTDETAIL_URL="web/grouppageSecond/recommenddetail?isShare=true&pid={0}";
			
	/**
	 * 链接-------END------------
	 */		
			
			
	/**
	 * 为了解决将url拼接到某个链接的参数中而出现的 ？、&等特殊字符的问题，此处需要替换掉该符号
	 * @author lipengfei
	 * @date 2015-5-25
	 * @return
	 */
	public static String urlEncode(String url){
		String newUrl =url.replace(PARAMS_WHY_FLAG[0], PARAMS_WHY_FLAG[1]);//将第一个替换为第二个
		
		newUrl = newUrl.replace(PARAMS_AND_FLAG[0], PARAMS_AND_FLAG[1]);
		
		newUrl = newUrl.replace(PARAMS_EQ_FLAG[0], PARAMS_EQ_FLAG[1]);
		
		return newUrl;
	}
	
	/**
	 * 为了解决将url拼接到某个链接的参数中而出现的 ？、&等特殊字符的问题，此处需要替换掉该符号
	 * @author lipengfei
	 * @date 2015-5-25
	 * @return
	 */
	public static String urlDecode(String url){
		
		String newUrl =url.replace(PARAMS_WHY_FLAG[1], PARAMS_WHY_FLAG[0]);//将第一个替换为第二个
		
		newUrl = newUrl.replace(PARAMS_AND_FLAG[1], PARAMS_AND_FLAG[0]);
		
		newUrl = newUrl.replace(PARAMS_EQ_FLAG[1], PARAMS_EQ_FLAG[0]);
		
		return newUrl;
	}
	
}
