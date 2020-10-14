package com.cmall.groupcenter.weixin;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.cmall.groupcenter.weixin.model.Button;
import com.cmall.groupcenter.weixin.model.CommonButton;
import com.cmall.groupcenter.weixin.model.ComplexButton;
import com.cmall.groupcenter.weixin.model.Menu;
import com.cmall.groupcenter.weixin.model.ViewButton;

/**
 * 菜单管理器
 * 
 * @author panwei
 * @date 
 */

public class MenuManager {

	private static Logger log = Logger.getLogger(MenuManager.class);
	private static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

	public static void main(String[] args) {
		String at = "XCTLFUy9d_9U7E3bIZ-q53P1GCJeVQq5hCsKpUny_GF65BsdCeIlaaUhymzGlBhSUk4TffAnwTDIW9mZQ45BDlGO-rIGWKGz3tfOtRQqBso";

		if (null != at) {
			// 调用接口创建菜单
			int result = createMenu(getMenu(), at);

			// 判断菜单创建结果
			if (0 == result){}
				//System.out.print("菜单创建成功！");
			else{
				log.info("菜单创建失败，错误码：" + result);
		}
		}
	}
	
	public static int createMenu(Menu menu, String accessToken) {
		int result = 0;

		// 拼装创建菜单的url
		String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
		// 将菜单对象转换成json字符串
		String jsonMenu = new JSONObject(menu).toString();
		// 调用接口创建菜单
		JSONObject jsonObject = WeiXinUtil.httpsRequest(url, "POST", jsonMenu);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("创建菜单失败 errcode:{" + jsonObject.getInt("errcode")
						+ "} errmsg:{" + jsonObject.getString("errmsg") + "}");
			}
		}

		return result;
	}

	
	/**
	 * 组装菜单数据
	 * 
	 * @return
	 */
	private static Menu getMenu() {
//		String appId = "wx67df21d755bcc84a";
//		String oauth2Url = WX_OAUTH2_URL.replace("APPID", appId).replace("SCOPE",
//				"snsapi_base");
		 

		CommonButton btn11 = new CommonButton();
		btn11.setName("可提现余额查询");
		btn11.setType("click");
		btn11.setKey("11");

		CommonButton btn12 = new CommonButton();
		btn12.setName("账户明细查询");
		btn12.setType("click");
		btn12.setKey("12");

		CommonButton btn13 = new CommonButton();
		btn13.setName("财产明细查询");
		btn13.setType("click");
		btn13.setKey("13");

		CommonButton btn14 = new CommonButton();
		btn14.setName("返利明细查询");
		btn14.setType("click");
		btn14.setKey("14");

		CommonButton btn15 = new CommonButton();
		btn15.setName("提现记录查询");
		btn15.setType("click");
		btn15.setKey("15");
		
		CommonButton btn21 = new CommonButton();
		btn21.setName("绑定账户");
		btn21.setType("click");
		btn21.setKey("21");

		CommonButton btn22 = new CommonButton();
		btn22.setName("免费通知提醒");
		btn22.setType("click");
		btn22.setKey("22"); 

//		CommonButton btn23 = new CommonButton();
//		btn23.setName("我的优惠券");
//		btn23.setType("click");
//		btn23.setKey("23"); 

		
		CommonButton btn24 = new CommonButton();
		btn24.setName("邀好友");
		btn24.setType("click");
		btn24.setKey("24"); 
		
		
		CommonButton btn25 = new CommonButton();
		btn25.setName("立即提现");
		btn25.setType("click");
		btn25.setKey("25"); 
		
		
		CommonButton btn31 = new CommonButton();
		btn31.setName("精选特惠");
		btn31.setType("click");
		btn31.setKey("31"); 
		
//		CommonButton btn32 = new CommonButton();
//		btn32.setName("领优惠券");
//		btn32.setType("click");
//		btn32.setKey("32"); 
//		
//		CommonButton btn33 = new CommonButton();
//		btn33.setName("互动福利");
//		btn33.setType("click");
//		btn33.setKey("33"); 
		
		ViewButton btn34 = new ViewButton();
		btn34.setName("下载APP");
		btn34.setType("view");
		btn34.setUrl("http://www.minsns.com/apps/down.html");
		
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("服务");
		mainBtn1.setSub_button(new Button[] { btn11,btn12,btn13,btn14,btn15});
		
		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("我");
		mainBtn2.setSub_button(new Button[] { btn25,btn21, btn22, btn24 });

		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("发现");
		mainBtn3.setSub_button(new Button[] { btn31, btn34 });

		Menu menu = new Menu();
		menu.setButton(new Button[] {mainBtn1,mainBtn2, mainBtn3 });

		return menu;
	}

	// 菜单创建（POST） 限100（次/天）
	public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	/**
	 * 创建菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @param accessToken
	 *            有效的access_token
	 * @return 0表示成功，其他值表示失败
	 */
	public int postMenu(Menu menu, String accessToken) {
		int result = 0;

		// 拼装创建菜单的url
		String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
		// 将菜单对象转换成json字符串
		String jsonMenu = new JSONObject(menu).toString();
		// 调用接口创建菜单
		JSONObject jsonObject = WeiXinUtil.httpsRequest(url, "POST", jsonMenu);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				log.error("创建菜单失败 errcode:{" + jsonObject.getInt("errcode")
						+ "} errmsg:{" + jsonObject.getString("errmsg") + "}");
			}
		}

		return result;
	}
}
