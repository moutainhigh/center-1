package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ReminderContent;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.xmassystem.load.LoadSellerInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSellerInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSellerQuery;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topcache.SimpleCache;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 个人中心
 * 
 * @author ligj
 *
 */
public class MyService extends BaseClass{
	
	private static SimpleCache cache = new SimpleCache(new SimpleCache.Config(300,300,"MyService",false));
	
	/**
	 * 返回可用的"我的"图片个数
	 * @param sellerCode 不能为空
	 */
	public int availableMyPic(String sellerCode){
		int count = 0;
		if (StringUtils.isNotBlank(sellerCode)) {
			count = DbUp.upTable("sc_pic_my").count("seller_code",sellerCode,"flag_view","1");
		}
		return count;
	}
	/**
	 * 返回可用的"我的"图片地址
	 * @param sellerCode 不能为空
	 */
	public String getMyPic(String sellerCode){
		String picUrl = "";
		if (StringUtils.isNotBlank(sellerCode)) {
			MDataMap myPicMap = DbUp.upTable("sc_pic_my").one("seller_code",sellerCode,"flag_view","1");
			if (myPicMap != null && !myPicMap.isEmpty()) {
				picUrl = myPicMap.get("photo_url");
			}
		}
		return picUrl;
	}
	
	/**
	 * 返回地址map
	 * @param areaCode为空时返回所有，否则返回此区域以及所有上级对应的名称
	 */
	public MDataMap getAreaNameMap(String areaCode){
		MDataMap pcgovMap = new MDataMap();
		String sWhere = "";
		if (StringUtils.isNotBlank(areaCode) && areaCode.length()==6) {
			sWhere = "code=CONCAT(LEFT("+areaCode+",2),'0000') or code=CONCAT(LEFT("+areaCode+",4),'00') or code="+areaCode;
		}
		List<MDataMap> pcgovList= DbUp.upTable("sc_tmp").queryAll("code,name", "", sWhere, new MDataMap());//查出所有的省市区信息
		for (MDataMap mDataMap : pcgovList) {
			pcgovMap.put(mDataMap.get("code"), mDataMap.get("name"));
		}
		return pcgovMap;
	}
	/**
	 * 返回提示语
	 * @param smallSellerCodes 商品编号（非空）
	 * @param viewPage 展示页面（非空）
	 * （取值范围,订单确认页：4497471600270001，支付成功页：4497471600270002，商品详情页：4497471600270003）
	 * @return
	 */
	public List<ReminderContent> getReminderList(List<String> smallSellerCodes,final String viewPage){
		List<ReminderContent> reminderList = new ArrayList<ReminderContent>();
		if (null == smallSellerCodes || smallSellerCodes.size()==0 || StringUtils.isBlank(viewPage)) {
			return reminderList;
		}
		
		List<MDataMap> reminderMapList = cache.get("ReminderList-"+viewPage, new SimpleCache.Loader<List<MDataMap>>() {
			@Override
			public List<MDataMap> load() {
				//商户类型对应编号为：（全部：4497471600260001，普通商户：4497471600260002，跨境商户：4497471600260003，指定商户：4497471600260004,LD:4497471600260005）
				String sWhere = "view_page like '%"+viewPage+"%' and status='4497469400030002' and flag_del='1'";
				return DbUp.upTable("nc_reminder_content").queryAll("", "zid desc", sWhere, null);
			}
		});

		if (null == reminderMapList || reminderMapList.isEmpty()) {
			return reminderList;
		}
		
		//获取到"全部"类型
		for (MDataMap reminderMap : reminderMapList) {
			if (reminderMap.get("seller_type").equals("4497471600260001")) {
				ReminderContent reminder = new ReminderContent();
				reminder.setContent(reminderMap.get("content"));
				reminder.setPicUrl(reminderMap.get("pic_url"));
				reminderList.add(reminder);
				break;
			}
		}
		//去重
		Map<String,String> smallSellerCodesMap = new HashMap<String,String>();
		for (String smallSellerCode : smallSellerCodes) {
			smallSellerCodesMap.put(smallSellerCode, "");
		}
//		String kjShop = bConfig("xmasproduct.kjShopCode");
		for (String smallSellerCode : smallSellerCodesMap.keySet()) {
			boolean flagMutex = false;		//指定商户类型与普通商户是否互斥
			for (MDataMap reminderMap : reminderMapList) {
				ReminderContent reminder = new ReminderContent();
				String seller_type = reminderMap.get("seller_type");
				String seller_cods = (null == reminderMap.get("seller_codes") ? "" : reminderMap.get("seller_codes"));
				if ((smallSellerCode.equals(AppConst.MANAGE_CODE_HOMEHAS) && seller_type.equals("4497471600260005"))
						||(new PlusServiceSeller().isKJSeller(smallSellerCode) && seller_type.equals("4497471600260003"))
						) {
					reminder.setContent(reminderMap.get("content"));
					reminder.setPicUrl(reminderMap.get("pic_url"));
					reminderList.add(reminder);
					continue;
				}
				if (seller_cods.contains(smallSellerCode) && seller_type.equals("4497471600260004")) {
					reminder.setContent(reminderMap.get("content"));
					reminder.setPicUrl(reminderMap.get("pic_url"));
					reminderList.add(reminder);
					flagMutex = true;
					continue;
				}
			}
			if (!flagMutex) {
				for (MDataMap reminderMap : reminderMapList) {
					
					ReminderContent reminder = new ReminderContent();
//					String seller_type = reminderMap.get("seller_type");
					PlusModelSellerInfo plusModelSellerInfo = new LoadSellerInfo().upInfoByCode(new PlusModelSellerQuery(smallSellerCode));
					String seller_type = plusModelSellerInfo.getUc_seller_type();
//					if (!new PlusServiceSeller().isKJSeller(smallSellerCode)&&smallSellerCode.startsWith("SF031")&&seller_type.equals("4497471600260002")) {
					/**
					 * 更改判断条件 2016-12-02 zhy
					 */
					if (!new PlusServiceSeller().isKJSeller(smallSellerCode)&&StringUtils.equals(seller_type, "4497471600260002")) {
						reminder.setContent(reminderMap.get("content"));
						reminder.setPicUrl(reminderMap.get("pic_url"));
						reminderList.add(reminder);
						continue;
					}
				}
			}
		}
		return reminderList;
	}
}
