package com.cmall.usercenter.service;

import java.util.ArrayList;
import java.util.List;

import com.cmall.usercenter.model.ShopTemplateForI;
import com.cmall.usercenter.model.UcSellerInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class UcShopTemplateService extends BaseClass {

	/**
	 * 获取模板信息
	 * 
	 * @param sellerCodes
	 * @param type
	 *            1 只取内容 2 取头和内容
	 * @return
	 */
	public List<ShopTemplateForI> getShopTemplate(String sellerCodes, int type) {
		List<ShopTemplateForI> list = new ArrayList<ShopTemplateForI>();

		if (sellerCodes == null || sellerCodes.equals("")) {
			return list;
		} else {
			String[] sellerCodeAry = sellerCodes.split(",");
			MDataMap urMapParam = new MDataMap();
			String whereStr = "";
			int i = 0;
			for (String dm : sellerCodeAry) {
				urMapParam.put("seller_code" + i, dm);
				whereStr += " seller_code=:seller_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2) {
				whereStr = whereStr.substring(0, whereStr.length() - 2);
				whereStr = "(" + whereStr + ") and flag_enable=1";
			} else {
				whereStr = "flag_enable=1";
			}

			List<MDataMap> sellerList = DbUp.upTable("uc_shop_template").query(
					"seller_code,template_header,template_content", "",
					whereStr, urMapParam, -1, -1);
			ShopTemplateForI c = null;
			for (MDataMap m : sellerList) {
				c = new ShopTemplateForI();
				c.setSellerCode(m.get("seller_code"));

				if (type == 1) {
					c.setTemplateHeader(m.get("template_header"));
				} else if (type == 2) {
					c.setTemplateContent(m.get("template_content"));
					c.setTemplateHeader(m.get("template_header"));
				}

				list.add(c);
			}
		}

		return list;
	}

	/**
	 * 刷新店铺的默认模板 该方法用于刷新店铺的从装修店初始化过来的模板
	 */
	public void refreshTemplate() {
		DbUp.upTable("uc_shop_template").dataExec(
				"call proc_create_shoptemplate", new MDataMap());
	}

	/**
	 * 更新店铺默认模板 该方法用于更新店铺的初始化的模板
	 * 
	 * @param sUserCode
	 */
	public void updateCommonTemplate(String sUserCode) {

		MDataMap mSellerMap = DbUp.upTable("uc_sellerinfo").one("seller_code",
				sUserCode);

		MDataMap mTemplateMap = DbUp.upTable("uc_shop_template").one(
				"seller_code", sUserCode, "template_edit_did", "449746360002");

		boolean bFlageUpdate = true;

		if (mTemplateMap == null || mTemplateMap.size() == 0) {

			bFlageUpdate = false;
			mTemplateMap = new MDataMap();
			mTemplateMap.put("seller_code", sUserCode);
			mTemplateMap.put("template_type_did", "449746330001");
			mTemplateMap.put("template_edit_did", "449746360002");
			mTemplateMap.put("create_time", FormatHelper.upDateTime());

		}

		int iCountEnable = DbUp
				.upTable("uc_shop_template")
				.dataCount(
						"seller_code='"
								+ mSellerMap.get("seller_code")
								+ "' and flag_enable=1 and template_type_did='449746330001' and template_edit_did!='449746360002'",
						new MDataMap());

		if (iCountEnable < 1) {
			mTemplateMap.put("flag_enable", "1");
		} else {
			mTemplateMap.put("flag_enable", "0");
		}

		String sTempContent = upTemplateInfo();
		mTemplateMap.put("update_time", FormatHelper.upDateTime());
		sTempContent = sTempContent.replace("${shoptitle}",
				mSellerMap.get("seller_name"));
		sTempContent = sTempContent.replace("${favoritelink}", FormatHelper
				.formatString(bConfig("usercenter.favorite_url"),
						mSellerMap.get("seller_code")));

		sTempContent = sTempContent.replace("${shoplink}", FormatHelper
				.formatString(bConfig("usercenter.shop_url"),
						mSellerMap.get("seller_code")));

		sTempContent = sTempContent.replace("${shoplogo}",
				mSellerMap.get("seller_pic"));

		mTemplateMap.put("template_name", "系统默认初始模板（禁止修改）");

		mTemplateMap.put("template_header", sTempContent);
		mTemplateMap.put("template_content", sTempContent);

		if (bFlageUpdate) {
			DbUp.upTable("uc_shop_template")
					.dataUpdate(mTemplateMap, "", "zid");
		} else {
			DbUp.upTable("uc_shop_template").dataInsert(mTemplateMap);
		}

	}

	public String upTemplateInfo() {
		return "<link type=\"text/css\" href=\"http://seller.static.cctvmall.cn/cshop/resources/ctheme/shop/red.css\" rel=\"stylesheet\" />	<div class=\"ctheme_shop_preview\"><div class=\"ctheme_shop_boxbody\"><div class=\"zat_dragable_view\" zat_template_type=\"custominfo\" style=\"background-position:center;background-image: url(http://pic.static.cctvmall.cn/staticfiles/upload/2241a/227944cb1d714118b0ce391934dd5636.jpg); width: 100%; height: 111px;\"><div class=\"ctheme_shop_custominfo\"><div style=\"height:100px;background-color:#fafafa;float:left;border-bottom:solid 1px #ebebeb;border-right:solid 1px #ebebeb;border-left:solid 1px #ebebeb;\"> <div style=\"width: 100px; float: left; margin: 20px 20px 0px 20px;\"><a href=\"${shoplink}\" target=\"_blank\"><img style=\"width: 100px;\"	 src=\"${shoplogo}\" /></a></div><div style=\"float: left;  font-size: 20px; font-weight: bold; margin: 40px 40px 0px 0px;padding-left:20px;border-left:solid 1px #8b8988;\"><a href=\"${shoplink}\" target=\"_blank\" style=\"color:#000;text-decoration:none;\">${shoptitle}</a><div style=\"width:60px;height:18px;\"><a href=\"${favoritelink}\" target=\"_blank\"><img src=\"http://pic.static.cctvmall.cn/staticfiles/upload/2241a/c88dd19f5b6f47a3a4b4d63fcfcac648.jpg\"/></a></div>	</div></div></div>	</div></div></div> ";
	}

}
