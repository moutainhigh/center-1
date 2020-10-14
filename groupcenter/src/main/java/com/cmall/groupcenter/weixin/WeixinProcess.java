package com.cmall.groupcenter.weixin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.groupcenter.util.DataQueryUtil;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.groupcenter.util.StringTemplateHelper;
import com.cmall.groupcenter.util.WebUtil;
import com.cmall.groupcenter.weixin.model.Article;
import com.cmall.groupcenter.weixin.model.NewsMessage;
import com.cmall.groupcenter.weixin.model.TextMessage;
import com.cmall.groupcenter.weixin.model.UserBindInfo;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class WeixinProcess extends HttpServlet{

	/**
	 * 
	 */
	private static final Logger log = Logger
			.getLogger(WeixinProcess.class);

	

	/**
	 * 
	 */
	private static final String BASE_PATH_KEY = "basePath";
	
	

	/** 
     * 确认请求来自微信服务器 
     */  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        // 微信加密签名  
        String signature = request.getParameter("signature");  
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        // 随机数  
        String nonce = request.getParameter("nonce");  
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
  
        PrintWriter out = response.getWriter();  
        WeiXinUtil util=new WeiXinUtil();
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        if (util.checkSignature(signature, timestamp, nonce)) {  
            out.print(echostr);  
        }  
        out.close();  
        out = null;  
    }  
  
    /** 
     * 处理微信服务器发来的消息 
     */  
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");  
  
        // 调用核心业务类接收消息、处理消息  
        String respMessage =processRequest(request,response);  
          
        // 响应消息  
        PrintWriter out = response.getWriter();  
        out.print(respMessage);  
        out.close();  
    }  
  

	public String processRequest(HttpServletRequest request, HttpServletResponse response) {
		 String respMessage = null;  
		try {  
            // 默认返回的文本消息内容  
            String respContent = "请求处理异常，请稍候尝试！";  
  
            // xml请求解析  
            Map<String, String> requestMap = MessageUtil.parseXml(request);  
  
            if(requestMap.keySet()!=null){
            	// 发送方帐号（open_id）  
                String fromUserName = requestMap.get("FromUserName");  
                // 公众帐号  
                String toUserName = requestMap.get("ToUserName");  
                // 消息类型  
                String msgType = requestMap.get("MsgType");  
      
                // 回复文本消息  
                TextMessage textMessage = new TextMessage();  
                textMessage.setToUserName(fromUserName);  
                textMessage.setFromUserName(toUserName);  
                textMessage.setCreateTime(new Date().getTime());  
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);  
                
                requestMap.put(BASE_PATH_KEY, WebUtil.getAppBaseUrl(request));
      
                // 文本消息  
                if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {  
//                    respContent = "您发送的是文本消息！";
                	
                	try {
                		//本次版本可以确定，但凡是用户输入了文字信息，必然是为了回复可提现余额
                		String result = requestMap.get("Content");
                		
                		//将用户可能输入的中文全角，转换成英文全角
                		String content = StringHelper.ToDBC(result);
                		
                		//将用户发过来的信息做解析,解析中英文下的标点符
                		String[] accountMessage = content.split(",",2);
//                		
                		if(accountMessage.length!=2 ||StringUtils.isEmpty( accountMessage[0].trim()) || StringUtils.isEmpty( accountMessage[1].trim())){
                			
                			//用户输入的信息不合法
                			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_QUERY_ACCOUNT_BALANCE_RULE_ERROR);
                		}else {
                			//代表两个都有值。
                			
                			/**
                			 * 校验顺序及规则如下：
								
							 *	1、是否为数字+逗号+字符 
							 	
								2、判断数字是否符合手机号格式（以1开头的11位数字）
								
								3、判断字符是否符合密码规则：6-16位字符且不能包含空格
                			 */
                			String phoneNum = accountMessage[0];
                			String passw = accountMessage[1];
                			
                			String passwordReg="^\\w{6,16}$";
                			
                			Pattern passworPattern = Pattern.compile(passwordReg);
                			
                			Matcher passMatcher = passworPattern.matcher(passw);
                			String regEx1 = "^1[0-9]{10}$"; 
                			Pattern p1 = Pattern.compile(regEx1); 
                			 
                			Matcher m1 = p1.matcher(phoneNum); 
                			boolean rs1 = m1.matches(); 
                			//1、手机号格式是否正确
                			if(rs1){//11为手机号校验成功
                				//2、手机号是否注册
                				//获取会员信息
                        		MDataMap loginInfoMap = DataQueryUtil.getLoginInfoByPhoneNum(accountMessage[0].trim());
                        		
                        		//如果未注册
                        		if(loginInfoMap==null){
                        			
                        			String url =WebUtil.getAppBaseUrl(request)+WebchatConstants.PAGE_WEI_COMMUNITY_REGESTER_URL;
                        			
                        			String message = StringTemplateHelper.outputLog(WebchatConstants.MESSAGE_NOT_REGESTERED_NOTICE, accountMessage[0],url);
                        			
//                            		message = WebUtil.getAppBaseUrl(request);
                        			
                        			return  WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,message);
                        		}else {
                        			//3、密码是否正确
                        			if(passMatcher.find()){
                        				//判断登录密码是否正确
                            			boolean loginCheck = SecrurityHelper.MD5Secruity(
                            					accountMessage[1]).equalsIgnoreCase(
                            							loginInfoMap.get("login_pass").trim());
                            			
                            			//登录成功返回余额,否则不返回任何处理
                            			if(loginCheck){
                            				return showBalanceByMemCode(loginInfoMap.get("member_code"),fromUserName, toUserName);
                            			}else {
                            				return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_PASSWORD_ERROR);
                            			}
                        				
                        			}else{
                        				return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_QUERY_ACCOUNT_BALANCE_FORMAT_ERROR);
                        			}
                        			
                        		}
                			}else{
                				//手机号长度不够
                				String reg = "^1[0-9]{1,}$"; 
                    			Pattern p2 = Pattern.compile(reg); 
                    			 
                    			Matcher m2 = p2.matcher(phoneNum); 
                    			boolean rs2 = m2.matches(); 
                    			if(rs2){
                    				return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_QUERY_ACCOUNT_BALANCE_MOBILE_ERROR);
                    			}
                				//用户输入的信息不合法
                    			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_QUERY_ACCOUNT_BALANCE_RULE_ERROR);
                			}
						}
                		
						
					} catch (Exception e) {
						e.printStackTrace();
						return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName,WebchatConstants.MESSAGE_SYSTEM_ERROR);
					}
                    
//                    return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, "1");
                }  
                // 图片消息  
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {  
                    respContent = "功能未开放，尽请期待！";  
                }  
                // 地理位置消息  
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {  
                    respContent = "功能未开放，尽请期待！";  
                }  
                // 链接消息  
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {  
                    respContent = "功能未开放，尽请期待！";  
                }  
                // 音频消息  
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {  
                    respContent = "功能未开放，尽请期待！";  
                }  
                // 事件推送  
                else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {  
                    // 事件类型  
                    String eventType = requestMap.get("Event");  
                    // 订阅  
                    if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {  
                    	return processSubscribe(requestMap); 
                    }  
                    // 取消订阅  
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {  
                        // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息  
                    	//取消关注自动解绑
                    	processUnSubscribe(requestMap);
                    }  
                    // 自定义菜单点击事件  
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) { 
                    	return processMenu(requestMap);
                    }  
                }  
      
                textMessage.setContent(respContent);  
                respMessage = MessageUtil.textMessageToXml(textMessage);  
            }
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return respMessage;  
    }  


	private String processMenu(Map<String, String> requestMap) {
		// 自定义菜单点击事件
		// 事件KEY值，与创建自定义菜单时指定的KEY值对应
		String eventKey = requestMap.get("EventKey");

		String fromUserName = requestMap.get("FromUserName");
		String toUserName = requestMap.get("ToUserName");
		
		//应用的baseUrl
		String basePath = requestMap.get(BASE_PATH_KEY);

		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(new java.util.Date().getTime());
		textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		String respContent = "";
		
		
		
		/**
		 * 获取会员的绑定信息
		 */
		
		MDataMap bindInfo = DataQueryUtil.getBindInfoByOpenId(fromUserName);
		
		//是否已绑定的标识
		boolean ifBinded=false;
		if(bindInfo!=null&&bindInfo.get("member_code")!=null&&!bindInfo.get("member_code").trim().isEmpty()){
			ifBinded = true;
		}

		//可用余额查询
		if (eventKey.equals("11")){
			
			//
			if(!ifBinded){
				
				String message=WebchatConstants.MESSAGE_QUERY_ACCOUNT_BALANCE_NOTICE;
				
				return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
				
			}else {
				return showBalanceByMemCode(bindInfo.get("member_code"), fromUserName, toUserName);
			}
			
		}

		//账户明细查询
		if (eventKey.equals("12")){
			
			String url =null;
			
			String path = "";
			//未绑定
			if(ifBinded){
				
				 path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_ACCOUNT_DETAIL_URL);
				
			}else{
				
				path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_ACCOUNT_DETAIL_NOTBINDING_URL);
				
			}
			
			url = basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL, fromUserName,path);
			
			String message = responseText("账户明细",url,ifBinded);
			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
		}

		//财产明细
		if (eventKey.equals("13")){
			
			String url =null;
			
			String path = "";
			//未绑定
			if(ifBinded){
				
				  path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_COST_DETAIL_URL);
			}else{
				
				path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_COST_DETAIL_NOTBINDING_URL);
				
			}
			
			
			url = basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL, fromUserName,path);
			
			String message = responseText("财产明细",url,ifBinded);
			
			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
		}
		
		//返利明细
		if (eventKey.equals("14")){
			
			String url =null;
			
			String path = "";
			//未绑定
			if(ifBinded){
				
			    path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_RECEIVED_GAINS_URL);
			
			}else{
				
				path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_RECEIVED_GAINS_NOTBINDING_URL);
				
			}
			
			
			url = basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL, fromUserName,path);
			
			String message = responseText("返利明细",url,ifBinded);
			
			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
			
		}
		
		//提现记录查询
		if (eventKey.equals("15")){
			
			String url =null;
			
			
			String path = "";
			//未绑定
			if(ifBinded){
				
			    path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_WITHDRAW_LIST_URL);
			    
			}else{
				
				path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_WITHDRAW_LIST_NOTBINDING_URL);
				
			}
			
			url = basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL,fromUserName,path);
			
			String message = responseText("提现记录",url,ifBinded);
			
			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
			
		}
		
		//立即提现
		if (eventKey.equals("25")){
			
			String url =null;
			
			String path = "";
			
			url = basePath+WebchatConstants.PAGE_WEI_COMMUNITY_WITHDRAW_NOW_URL;
			
			String message = StringTemplateHelper.outputLog(WebchatConstants.MESSAGE_WITHDRAW_NOW, url);
			
			return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
			
		}
		
		//精选特惠
		if(eventKey.equals("31")){
			
			return post(fromUserName,toUserName,basePath);
		}
		
		//绑定账户
		if(eventKey.equals("21")){
			String sql="select * from mc_weixin_binding where open_id =:open_id ";
			Map<String,Object> userBindMap=DbUp.upTable("mc_weixin_binding").dataSqlOne(sql, new MDataMap("open_id",fromUserName));
			if(!ifBinded){
				respContent="欢迎您绑定微公社账户，轻松绑定后无需登录即可享受以下贴心服务："
						+ "\n1、免费通知提醒"
						+ "\n2、可提现余额查询"
						+ "\n3、账户明细查询"
						+ "\n4、财产明细查询"
						+ "\n5、返利记录查询"
						+ "\n6、提现记录查询"
						+ "\n7、邀请好友"
						+ "\n\n          <a href='"+basePath+"web/grouppageSecond/wx_bind?web_api_openid="+fromUserName+"&targetId=1'>点击这里，立即绑定</a>";
			}else{
				String userInfoSql="select * from mc_login_info where member_code =:member_code ";
				Map<String,Object> userInfoMap=DbUp.upTable("mc_login_info").dataSqlOne(userInfoSql, new MDataMap("member_code",userBindMap.get("member_code").toString()));
				String mobile=userInfoMap.get("login_name").toString().substring(7);
				respContent="您尾号"+mobile+"的微公社账号已经绑定成功，无需登录可享受以下贴心服务："
						+ "\n1、免费通知提醒"
						+ "\n2、可提现余额查询"
						+ "\n3、账户明细查询"
						+ "\n4、财产明细查询"
						+ "\n5、返利记录查询"
						+ "\n6、提现记录查询"
						+ "\n7、邀请好友";
			}
			
			textMessage.setContent(respContent);
			return MessageUtil.textMessageToXml(textMessage);
		}

		//免费通知提醒
		if(eventKey.equals("22")){
			String sql="select * from mc_weixin_binding where open_id =:open_id ";
			Map<String,Object> userBindMap=DbUp.upTable("mc_weixin_binding").dataSqlOne(sql, new MDataMap("open_id",fromUserName));
			if(!ifBinded){
				respContent="您只需轻松绑定微公社，即可免费享受返利、提现等账户变动通知，更有热门活动等提醒！"
						+ "\n\n        <a href='"+basePath+"web/grouppageSecond/wx_bind?web_api_openid="+fromUserName+"&targetId=2'>点击这里，立即绑定</a>";
			}else{
				String userInfoSql="select * from mc_login_info where member_code =:member_code ";
				Map<String,Object> userInfoMap=DbUp.upTable("mc_login_info").dataSqlOne(userInfoSql, new MDataMap("member_code",userBindMap.get("member_code").toString()));
				String mobile=userInfoMap.get("login_name").toString().substring(7);
				respContent="您尾号"+mobile+"的微公社账号正在享受免费通知提醒服务，可免费享受返利、提现等账户变动通知，更有热门活动等提醒！";
			}
			textMessage.setContent(respContent);
			return MessageUtil.textMessageToXml(textMessage);
		}
		
		//邀请朋友
		if(eventKey.equals("24")){
			
			String url =null;
			
			String path = WebchatConstants.urlEncode(WebchatConstants.PAGE_WEI_COMMUNITY_ADD_FRIEND_URL);
			
			url = basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_COMMONT_TRANSIT_URL, fromUserName,path);
			
			if(!ifBinded){
				respContent="微公社返利多多，好友购物，他返你也返！请点击以下链接登录微公社网页版，自动进入“邀请好友”页面即可进行邀请："
						+ "\n\n       <a href='"+url+"'>点击这里，立即邀请</a>";
			}else{
				respContent="微公社返利多多，好友购物，他返你也返！请点击以下链接，自动进入“邀请好友”页面即可进行邀请："
						+ "\n\n        <a href='"+url+"'>点击这里，立即邀请</a>";
			}
			
			textMessage.setContent(respContent);
			return MessageUtil.textMessageToXml(textMessage);
		}
		 
		textMessage.setContent(respContent);
		String respMessage = MessageUtil.textMessageToXml(textMessage);
		return respMessage;
	}
	

	//精选特惠
	private String specialDeals(String fromUserName,
			String toUserName) {
		
		String respMessage = null;
		String sWhere=" if_pub=4497472000030001";
		List<MDataMap> listMaps=DbUp.upTable("gc_webchat_special_deals").queryAll("", "update_date desc", sWhere, new MDataMap());
		
		
		if(listMaps!=null&&listMaps.size()>0){
			NewsMessage newsMessage = new NewsMessage();
			newsMessage.setToUserName(fromUserName);
			newsMessage.setFromUserName(toUserName);
			newsMessage.setCreateTime(new Date().getTime());
			newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
			List<Article> articleList = new ArrayList<Article>();
			for(MDataMap map:listMaps){
				
				Article article = new Article();
				article.setTitle(map.get("title"));
				article.setDescription(map.get("deal_describe"));
				article.setPicUrl(map.get("pic_url"));
				article.setUrl(map.get("url"));
				articleList.add(article);

			}
			// 设置图文消息个数
			newsMessage.setArticleCount(articleList.size());
			// 设置图文消息包含的图文集合
			newsMessage.setArticles(articleList);
			// 将图文消息对象转换成xml字符串
			respMessage = MessageUtil.newsMessageToXml(newsMessage);
		}else{
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new java.util.Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			String respContent = "暂无特惠信息，敬请期待！";
			textMessage.setContent(respContent);
			respMessage = MessageUtil.textMessageToXml(textMessage);
		}
		
		return respMessage;
	}
	
	
	//精选特惠-好物推荐
	private String post(String fromUserName,
			String toUserName,String basePath){
		String respMessage = null;	
		PageOption paging=new PageOption();
		paging.setLimit(5);
		paging.setOffset(0);
		MPageData mPageData = DataPaging.upPageData("nc_post", "","-sort,-last_modify_time,-publish_time", "app_code =:app_code and flag_enable =:flag_enable and start_time <=:now_date and end_time >=:now_date", new MDataMap("app_code", WebchatConstants.CGROUP_MANAGE_CODE,"flag_enable", "4497472000010001","now_date",DateHelper.upNow()), paging);
	    
		if(mPageData.getListData().size() >0){
			NewsMessage newsMessage = new NewsMessage();
			newsMessage.setToUserName(fromUserName);
			newsMessage.setFromUserName(toUserName);
			newsMessage.setCreateTime(new Date().getTime());
			newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
			List<Article> articleList = new ArrayList<Article>();
			for(MDataMap mDataMap : mPageData.getListData()){
				Article article = new Article();
				article.setTitle(mDataMap.get("p_title"));
				if(articleList.size()==0){
					article.setPicUrl(mDataMap.get("wx_big_img_url"));
				}else{
					article.setPicUrl(mDataMap.get("wx_small_img_url"));
				}
				String url= basePath+StringTemplateHelper.outputLog(WebchatConstants.PAGE_WEI_COMMUNITY_RECOMMENTDETAIL_URL, mDataMap.get("pid"));
				article.setUrl(url);
				articleList.add(article);
			}
			// 设置图文消息个数
			newsMessage.setArticleCount(articleList.size());
			// 设置图文消息包含的图文集合
			newsMessage.setArticles(articleList);
			// 将图文消息对象转换成xml字符串
			respMessage = MessageUtil.newsMessageToXml(newsMessage);
		}else{
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new java.util.Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			String respContent = "暂无特惠信息，敬请期待！";
			textMessage.setContent(respContent);
			respMessage = MessageUtil.textMessageToXml(textMessage);
		}
		return respMessage;
	}
	

	private String processSubscribe(Map<String, String> requestMap) {
		// 订阅
		// 图文消息公司介绍
		String fromUser = requestMap.get("FromUserName");
		String toUser = requestMap.get("ToUserName");
		
		//应用的baseUrl
		String basePath = requestMap.get(BASE_PATH_KEY);
		
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUser);
		textMessage.setFromUserName(toUser);
		textMessage.setCreateTime(new java.util.Date().getTime());
		textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		String respContent="欢迎您关注微公社！建议您先绑定账户，轻松绑定后无需登录即可享受以下贴心服务："
				+ "\n1、免费通知提醒"
				+ "\n2、可提现余额查询"
				+ "\n3、账户明细查询"
				+ "\n4、财产明细查询"
				+ "\n5、返利记录查询"
				+ "\n6、提现记录查询"
				+ "\n7、邀请好友"
				+ "\n\n          <a href='"+basePath+"web/grouppageSecond/wx_bind?web_api_openid="+fromUser+"&targetId=1'>点击这里，立即绑定</a>";
		textMessage.setContent(respContent);
		String respMessage = MessageUtil.textMessageToXml(textMessage);
		
		//更新用户信息
		MDataMap bindInfo=DbUp.upTable("mc_weixin_binding").one("open_id",fromUser);
		WeiXinUtil wxUtil=new WeiXinUtil();
		UserBindInfo info=wxUtil.getUserInfo(fromUser);
		if(bindInfo!=null&&bindInfo.size()>0){
			bindInfo.put("union_id", info.getUnionId()==null?"":info.getUnionId());
			bindInfo.put("location",info.getAddress());
			bindInfo.put("headerImageUrl",info.getHeadImgUrl());
			bindInfo.put("nickName",info.getNickName());
			bindInfo.put("bind_time",info.getSubscribeTime()==null?"":info.getSubscribeTime().toString());
			DbUp.upTable("mc_weixin_binding").dataUpdate(bindInfo, "", "uid");
		}else{
			DbUp.upTable("mc_weixin_binding").
			insert("open_id",info.getOpenId(),
				"union_id",info.getUnionId()==null?"":info.getUnionId(),
				"location",info.getAddress(),
				"headerImageUrl",info.getHeadImgUrl(),
				"nickName",info.getNickName(),
				"create_time",FormatHelper.upDateTime(),
				"bind_time",info.getSubscribeTime()==null?"":info.getSubscribeTime().toString(),
				"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
		}
		return respMessage;
	}
	
	//取消关注
	private void processUnSubscribe(Map<String, String> requestMap) {
		// 订阅
		// 图文消息公司介绍
		String fromUser = requestMap.get("FromUserName");
		String toUser = requestMap.get("ToUserName");
		
		//更新用户信息
		MDataMap bindInfo=DbUp.upTable("mc_weixin_binding").one("open_id",fromUser);
		if(bindInfo!=null&&bindInfo.size()>0){
			bindInfo.put("member_code", "");
			DbUp.upTable("mc_weixin_binding").dataUpdate(bindInfo, "", "uid");
		}
	}


	/**
	 * 替换掉通用文本中的数据
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param menuName
	 * @param url
	 * @return
	 */
	public static  String responseText(String menuName,String url,boolean ifBinding){
		String value = null;
		if(ifBinding){
			 value  = WebchatConstants.MESSAGE_COMMON_RESPONSE_BINDING_WORDS;
		}else{
			 value  = WebchatConstants.MESSAGE_COMMON_RESPONSE_WORDS;
		}
		
		value = StringTemplateHelper.outputLog(value, menuName,menuName,url);
		
		return value;
	}
	
	/**
	 * 
	 * 通过gc_group_account的值来获取余额信息
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param message
	 * @return
	 * 
	 */
	public static String showBalanceByAccountCode(String accountCode,String fromUserName,String toUserName){
		
		String  message =null;
		
		MDataMap mWhereMap = new MDataMap();
		
		mWhereMap.put("account_code",accountCode);
		
		String money = String.valueOf(DbUp.upTable("gc_group_account").dataGet("account_withdraw_money", null, mWhereMap));
		
		if(StringUtils.isEmpty(money) || "null".equals(money)){
			money="0.00";
		}
		
		message = "您当前可提现余额："+money+"元";
		
		return WeiXinUtil.toResponseTextMessage(fromUserName, toUserName, message);
	}

	/**
	 * 
	 * 通过mc_member_info的值memberCode来获取余额信息
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param message
	 * @return
	 * 
	 */
	public static String showBalanceByMemCode(String memCode,String fromUserName,String toUserName){
		
		MDataMap memInfoMap = DataQueryUtil.getMemInfoByMemcode(memCode);
		
		return showBalanceByAccountCode(memInfoMap.get("account_code"), fromUserName, toUserName);
	}
	
}
