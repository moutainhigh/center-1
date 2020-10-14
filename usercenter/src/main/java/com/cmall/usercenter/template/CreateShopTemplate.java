package com.cmall.usercenter.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class CreateShopTemplate extends BaseClass {

	public boolean getAllSellers(){
		List<MDataMap> list = new ArrayList<MDataMap>();
		list = DbUp.upTable("uc_sellerinfo").queryAll("*", "", "", new MDataMap());
		//System.out.println("商家总数为:"+list.size());
		for(int i=0;i<list.size();i++ ){
			MDataMap map = list.get(i);
			MDataMap wh = new MDataMap();
			wh.put("seller_code", map.get("seller_code"));
			wh.put("flag_sale", "1");
			wh.put("product_status", "4497153900060002");
			List<MDataMap> ps = DbUp.upTable("pc_productinfo").queryAll("*", "", "", wh);
			StringBuffer div = this.createDIV(map, ps);
			MDataMap insertM = new MDataMap();
			insertM.put("seller_code", map.get("seller_code"));
			insertM.put("template_type_did", "449746330001");
			insertM.put("flag_enable", "1");
			insertM.put("template_name", "模板一");
			insertM.put("template_content", div.toString());
			insertM.put("create_time", DateUtil.toString(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
			DbUp.upTable("uc_shop_template").dataInsert(insertM);
			//System.out.println(i+"***"+map.get("seller_code")+"***"+map.get("seller_name"));
		}
		return true;
	}
	public StringBuffer createDIV(MDataMap map,List<MDataMap> ps){
		StringBuffer sBuffer = new StringBuffer();
		if(!ps.isEmpty()){
			sBuffer.append("<link type=\"text/css\" href=\"http://seller.cctvmall.com/cshop/resources/ctheme/shop/red.css\"  rel=\"stylesheet\" />");
			sBuffer.append("<script type=\"text/javascript\" src=\"http://seller.cctvmall.com/cshop/resources/zs/zs.js\"></script>");
			sBuffer.append("<script type=\"text/javascript\" src=\"http://seller.cctvmall.com/cshop/resources/zs/focus/zs_focus_carousel.js\"></script>");
			sBuffer.append("<div class=\"ctheme_shop_preview\">");
			sBuffer.append("<div class=\"ctheme_shop_boxbody\">");
			
			sBuffer.append("<div class=\"zat_dragable_view\" zat_template_type=\"custominfo\">");
			sBuffer.append("<div class=\"ctheme_shop_custominfo\">");
			sBuffer.append("<div style=\"background-color: #e1dfe0;\">");
			sBuffer.append("<div style=\"width: 100px; float: left; margin: 20px 50px 0px 0px;\">");
			sBuffer.append("<img style=\"width: 100px;\" src=\""+map.get("seller_pic")+"\" />");
			sBuffer.append("</div>");
			sBuffer.append("<div style=\"float: left; width: 400px; font-size: 20px; font-weight: bold; margin: 40px 0px 0px 20px;\">"+map.get("seller_name")+"</div>");
			sBuffer.append("</div>");
			sBuffer.append("<div style=\"clear: both; height: 20px;\"></div>");
			sBuffer.append("<div style=\"background-color: #8d8c8c; height: 30px; width: 100%; clear: both;\"></div>");
			sBuffer.append("</div>");
			sBuffer.append("</div>");
			
			sBuffer.append("<div class=\"zat_dragable_view\" zat_template_type=\"centerimage\">");
			sBuffer.append("<div class=\"ctheme_shop_centerimage\">");
			sBuffer.append("<img src=\"http://pic.static.cctvmall.cn/staticfiles/upload/22357/60b7066eab5c4ccd883d7b944001295f.jpg\">");
			sBuffer.append("</div>");
			sBuffer.append("</div>");
			sBuffer.append("<div class=\"zat_dragable_view\" zat_template_type=\"productlist\"><div class=\"ctheme_shop_productlist\">");
			sBuffer.append("<div class=\"ctheme_shop_productlist_fix\"><ul>");
			sBuffer.append(this.createLI(ps, 0, 8));
			sBuffer.append("</ul><div class=\"ctheme_shop_clear\"></div></div></div></div");
			if(ps.size()>=8){
				sBuffer.append("<div class=\"zat_dragable_view\" zat_template_type=\"muleimage\">");
				sBuffer.append("<div class=\"ctheme_shop_muleimage\">");
				sBuffer.append("<div class=\"ctheme_shop_muleimage_fix\"><ul><li><a href=\"\">");
				sBuffer.append("<img src=\"http://pic.static.cctvmall.cn/staticfiles/upload/22357/aaa8b0a74f5942eca254c99ec43ce3ae.jpg\">");
				sBuffer.append("</a></li></ul>");
				sBuffer.append("<div class=\" ctheme_shop_clear\"></div></div></div></div>");
				sBuffer.append("<div class=\"zat_dragable_view\" zat_template_type=\"productlist\"><div class=\"ctheme_shop_productlist\"><div class=\"ctheme_shop_productlist_fix\"><ul>");
				sBuffer.append(this.createLI(ps, 8, 16));
				sBuffer.append("</ul><div class=\"ctheme_shop_clear\"></div></div></div></div></div></div>");
			}
		}
		return sBuffer;
	}
	
	public StringBuffer createLI(List<MDataMap> ps,int start,int end){
		StringBuffer sBuffer = new StringBuffer();
		if(ps.size()<end){
			end=ps.size();
		}
		for(int i=start;i<end;i++){
			MDataMap mapInfo = ps.get(i);
			MDataMap qh = new MDataMap();
			qh.put("product_code", mapInfo.get("product_code"));
			List<MDataMap> mapSku = DbUp.upTable("pc_skuinfo").queryAll("*", "", "", qh);
			if(!mapSku.isEmpty()){
				sBuffer.append("<li>");
				sBuffer.append("<a href=\"http://detail.cctvmall.com/item/product/skucode/"+mapSku.get(0).get("sku_code")+".html\" target=\"_blank\">");
				sBuffer.append("<input class=\"c_sku\" type=\"hidden\" value=\""+mapSku.get(0).get("sku_code")+"\">");
				sBuffer.append("<div class=\"c_image\">");
				sBuffer.append("<img src=\""+mapSku.get(0).get("sku_picurl")+"\">");
				sBuffer.append("</div></a>");
				sBuffer.append("<div class=\"c_info\"><a href=\"http://detail.cctvmall.com/item/product/skucode/"+mapSku.get(0).get("sku_code")+".html\" target=\"_blank\">");
				sBuffer.append("<div class=\"c_name\">"+mapSku.get(0).get("sku_name")+"</div><div class=\"c_price\">￥"+mapSku.get(0).get("sell_price")+"</div>");
				sBuffer.append("<div class=\"c_buy ctheme_shop_png\"></div></a><div>");
				sBuffer.append("<a href=\"http://detail.cctvmall.com/item/product/skucode/"+mapSku.get(0).get("sku_code")+".html\" target=\"_blank\"></a></div></div></li>");
			}
		}
		return sBuffer;
	}
}
